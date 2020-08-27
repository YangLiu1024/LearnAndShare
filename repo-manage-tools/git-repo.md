# Introduction to git-repo

## Foreword
Sometimes, we split our sub module to separated repository,and each module is reposible for its own version.

Normally, host project depend on sub module through `Maven`. All sub module are imported as third party dependency.

But this will introduce some issues when sub module is updated frequently that host project must update its sub module dependecy everytime.

And if the source code of sub module is not uploaded to maven, its hard to debug sub modules.

To solve this issue, introduce multiple repository management solution, such as `git-repo`, `git-subtree`, `git-submodule`, `gitslave`.

## Git-repo
`git-repo` is an extension of git which could support huge size sub repository. Its actually a set of `python` script which used to manage multiple repository.

### Environment Setup
* Install python 3.6 or above
* Download repo, the repo here is a python script indeed. 
```bash
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
```
when execute the `repo` command, the script will download the real repository `https://gerrit.googlesource.com/git-repo/`

### Manifest repository
The information about how to manage multiple repository and all configuration of sub repository is stored in an independent repository, which usually called `manifest`.

And within this manifest repository, a file named `default.xml` is required. All configuration information store in this file.

A simple example, refer to [Manifest repo](https://github.com/YangLiu1024/GitRepoManifestRepo)
```xml
<manifest>
    <remote name="github" fetch="https://github.com"/>
    <default revision="refs/heads/master" remote="github" sync-j="4"/>
    <project name="YangLiu1024/GitSubTreeTestLeafRepo" remote="github" path="leaf"/>
    <project name="YangLiu1024/GitSubTreeTestSplitRepo" remote="github" path="split"/>
</manifest>
```
the `<manifest>` is the root element.

`<remote>` is used to config remote repository, could be multiple.
  - `name` is the name of remote repository, should be unique in this xml among all remotes. the name here will be used as remote name after `repo sync` for the project download from this remote, in another word, its equal to `git remote add <remote.name> <remote.fetch + project.name>` for each project.
  - `fetch` is the prefix of repository address, when connect to sub repository, the final used URL will be `remote.fetch` + `project.name`, in my case, it will be `https://github.com/YangLiu1024/GitSubTreeTestLeafRepo` for leaf repo

`<project>` is used to config each sub repository
  - `name` the name of sub repository
  - `path` when execute `repo sync`, the relative path to root directory, and the code in sub repository will be downloaded to this sub folder
  - `remote` the remote defined in `<remote>`
  - `revision` the branch name
  
`<default>` is used to defined the default value for project attribute if its not defined in `<project>`

more information about manifest format, please refer to [manifest format](https://gerrit.googlesource.com/git-repo/+/master/docs/manifest-format.md)

### Initialize the git-repo
when manifest repository is ready, its time to initialize our repo work space.
```git
repo init -u <url-to-manifest-repo> -b <manifest-repo-branch-name> -m <selected-manifest-file-name>
```
`-b` is optional, its default value is `master`

`-m` is optional, its default value is `default.xml`

in my case, it wil be
```git
repo init -u https://github.com/YangLiu1024/GitRepoManifestRepo
```
in this step, a folder named `.repo` will be created, `repo` will download latest [git-repo](https://gerrit.googlesource.com/git-repo/) to sub folder `repo`, and the folders for manifest repository
```git
yangliu@LT424684 MINGW64 /git-repo-demo/.repo
$ ls -al
total 23
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 ./
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 ../
-rw-r--r-- 1 wa-clxie 1049089 514 Aug 27 11:18 manifest.xml
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:18 manifests/
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:20 manifests.git/
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:20 repo/
```

Now, time to sync all sub repository to our work space.
```git
repo sync -f -d -m <selected-manifest-file-name>
```
`-f` means even if current project fail to sync, continue to sync next project

`-d` means roll back current work space to the revision defined in manifest file

`-m` means use specified manifest file

after this command, all sub repository defined in `default.xml` will be downloaded to our work space,the file structure shown as below
```git
$ ls -al
total 12
drwxr-xr-x 1 wa-clxie 1049089 0 Aug 27 11:21 ./
drwxr-xr-x 1 wa-clxie 1049089 0 Aug 27 11:07 ../
drwxr-xr-x 1 wa-clxie 1049089 0 Aug 27 11:21 .repo/
drwxr-xr-x 1 wa-clxie 1049089 0 Aug 27 11:21 leaf/
drwxr-xr-x 1 wa-clxie 1049089 0 Aug 27 11:21 split/
```
for `.repo` folder, some new folders are created
```git
$ ls -al
total 23
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 ./
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 ../
-rw-r--r-- 1 wa-clxie 1049089 126 Aug 27 11:21 .repo_fetchtimes.json
-rw-r--r-- 1 wa-clxie 1049089 514 Aug 27 11:18 manifest.xml
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:18 manifests/
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:20 manifests.git/
-rw-r--r-- 1 wa-clxie 1049089  13 Aug 27 11:21 project.list
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 project-objects/
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:21 projects/
drwxr-xr-x 1 wa-clxie 1049089   0 Aug 27 11:20 repo/
```
<b>note that for each project, it has no local branch yet.</b>

### Check repo status
```git
repo status
```
similar with `git status`, this command will list modification info for all projects
```git
$ .repo/repo/repo status
nothing to commit (working directory clean)
```

### Modify project
Do any modification to leaf project, then execute `repo status` again
```git
$ .repo/repo/repo status
project leaf/                                   (*** NO BRANCH ***)
 -m     leaf.md
```
to check the diff, 
```git
repo diff
```
the diff results
```git
$ .repo/repo/repo diff leaf/leaf.md

project leaf/
diff --git a/leaf.md b/leaf.md
index 8a82ad7..4157327 100644
--- a/leaf.md
+++ b/leaf.md
@@ -4,3 +4,5 @@ modify the file by leaf repo
 //after git subtree pull -P leaf leaf master, there will be conflict
 //because both host repo and leaf repo modify the file
 //so fix the conflict and commit the change again
+
+modify the leaf.md from repo work space
```
then modify any file for `split` project, then check status
```git
$ .repo/repo/repo status
project split/                                  (*** NO BRANCH ***)
 -d     settings-16.png
project leaf/                                   (*** NO BRANCH ***)
 -m     leaf.md
```

### Create branch through repo
```git
repo start <new-branch-name> [--all | <project>...]
```
`<project>` could be `project.name` or `project.path` defined in manifest file

this command will create new branch for all or specifed projects, <b>and the new created branch is based on the branch defined in manifest file and track this remote branch automaticly</b>

```git
$ .repo/repo/repo start modify-through-repo leaf split
$ .repo/repo/repo status
project split/                                  branch modify-through-repo
 -d     settings-16.png
project leaf/                                   branch modify-through-repo
 -m     leaf.md
```

### Execute command for all projects
```git
repo forall <project_list> -c <git command>
```
this command will execute `<git command>` for all specified projects, for example `repo forall -c git push`

### Show current used manifest
```git
repo manifest
```
this command will print current used manifest file

## Common Error

