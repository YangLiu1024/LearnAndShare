# Introduction to git-repo

## Foreword
Sometimes, we split our sub module to separated repository,and each module is reposible for its own version.

Normally, host project depend on sub module through `Maven`. All sub module are imported as third party dependency.

But this will introduce some issues when sub module is updated frequently that host project must update its sub module dependecy everytime.

And if the source code of sub module is not uploaded to maven, its hard to debug sub modules.

To solve this issue, introduce multiple repository management solution, such as `git-repo`, `git-subtree`, `git-submodule`, `gitslave`.

## Git-repo
`git-repo` is an extension of git which could support huge size sub repository. Its actually a set of `python` script which used to manage multiple repository.

<b>Official</b>: Repo is a tool that we built on top of Git. Repo helps us manage the many Git repositories, does the uploads to our revision control system, and automates parts of the Android development workflow. Repo is not meant to replace Git, only to make it easier to work with Git in the context of Android. The repo command is an executable Python script that you can put anywhere in your path.

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
  - `fetch` is the prefix of repository address, when connect to sub repository, the final used URL will be `remote.fetch` + `project.name` + `.git`, in my case, it will be `https://github.com/YangLiu1024/GitSubTreeTestLeafRepo.git` for leaf repo

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
repo sync -f -d -m <selected-manifest-file-name> <projects>...
```
`-f` means even if current project fail to sync, continue to sync next project

`-d` means check out to the branch defined in manifest file automatic before sync

`-m` means use specified manifest file

after this command, all or specifed sub repository defined in `default.xml` will be downloaded to our work space,the file structure shown as below.

note that when specify project name, `repo sync <project>`
* if this project has never been synced before, its equal to `git clone`, all branches in remote repository will be copied to local project directory
* if this project has been synced before, then its equal to `git remote update; git rebase`, it will use currently checked-out branch in the local project directory to rebase. 
  - if the local branch isn't tracking a remote branch, then no synchronization occurs for the project
  - if the git rebase operation result in merge conflicts, use normal git command, such as git rebase --continue, to resolve the conflicts
  
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

### Diff repo
```git
repo diff <projects>...
```
show changes under repo

### Modify project
Do any modification to leaf project, then execute `repo status` again
```git
$ .repo/repo/repo status
project leaf/                                   (*** NO BRANCH ***)
 -m     leaf.md
```
to check the diff, 
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
the status information for each file use two-letter code.

for the first letter, an uppercase letter indicate how the staging area differ from the last committed state
Letter   |  Meaning  
-------- | ---------
\- | no change
A | Added
M | Modified 
D | Deleted 
R | Renamed 
C | Copied 
T | Mode changed
U | Unmerged

for the second letter, a lowercase letter indicate how the working directory differ from the index
Letter | Meaning
------ | -------
\- | New/unknown
m | Modified 
d | Deleted

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
this command will execute `<git command>` for all specified projects through `/bin/sh`, for example `repo forall -c git push`

### Delelet merged branch
```git
repo prune <projects>...
```
Delete branch that are already merged

### Show current used manifest
```git
repo manifest
```
this command will print current used manifest file

## Common Error
### Symlinks
Repo will use symlinks heavily internally. On *NIX platforms, this isn't an issue, but Windows makes it a bit difficult.

There are some documents out there for how to do this, but usually the easiest answer is to run your shell as an Administrator and invoke repo/git in that.

This isn‘t a great solution, but Windows doesn’t make this easy, so here we are. 

Launch Git Bash
If you install Git Bash (see below), you can launch that with appropriate permissions so that all programs “just work”.

* Open the Start Menu (i.e. press the ⊞ key).
* Find/search for “Git Bash”.
* Right click it and select “Run as administrator”.

### Network forbidden
If you can not access [Gerrit repo](https://gerrit.googlesource.com/git-repo/) because of firewall or something, you can try its [github mirror](https://github.com/GerritCodeReview/git-repo)

