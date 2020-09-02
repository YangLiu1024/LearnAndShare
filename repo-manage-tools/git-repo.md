# Introduction to git-repo

## Foreword
Sometimes, we split our sub module to separated repository,and each module is reposible for its own version.

Normally, host project depend on sub module through `Maven`. All sub module are imported as third party dependency.

But this will introduce some issues when sub module is updated frequently that host project must update its sub module dependecy everytime.

And if the source code of sub module is not uploaded to maven, its hard to debug sub modules.

To solve this issue, introduce multiple repository management solution: `git-repo`.

## Git-repo
`git-repo` is an extension of git which could support huge size sub repository. Its actually a set of `python` script which used to manage multiple repository.

<b>Official</b>: Repo is a tool that we built on top of Git. Repo helps us manage the many Git repositories, does the uploads to our revision control system, and automates parts of the Android development workflow. Repo is not meant to replace Git, only to make it easier to work with Git in the context of Android. The repo command is an executable Python script that you can put anywhere in your path.

### Environment Setup
* Install python 3.6 or above
* Download repo, the repo here is a python script launcher indeed.
* Could access `https://gerrit.googlesource.com/git-repo/`, if not, should use its accessible mirror
```bash
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
```
when execute the `repo` command, the script will download the real repository `https://gerrit.googlesource.com/git-repo/`

### Manifest repository
The information about how to manage multiple repository and all configuration of sub repository is stored in an independent repository, which usually called `manifest`.

And within this manifest repository, a file named `default.xml` is required, its the default manifest file. All configuration information store in this file.

If it does not exist, when execute `repo init`, need to specify option `-m` to select the manifest file name. 

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
  - `fetch` is the prefix of repository address, shared by one or more projects which use this remote, when connect to sub repository, the final used URL will be `remote.fetch` + `project.name` + `.git`, in my case, it will be `https://github.com/YangLiu1024/GitSubTreeTestLeafRepo.git` for leaf repo
  - `review` is the host name of 'Gerrit' server where reviews are uploaded by `repo upload`. This attribute is optional, if not specified, `repo upload` will not function
  - `alias` if specified, is used to override `name` to be set as the remote name in each projects. Its value could be duplicate while `name` must be unique in the manifest.xml. This help each project to be able to have same remote name which actually points to different remote.
  - `revision` is the name of git branch, such as 'master' or 'refs/heads/master', if specifed, will override the default revision in `<default>`
  - `pushurl` is the git 'push' prefix for all projects which use this remote, is optional.
  
`<default>` is used to defined the default value for `project` element if its attribute is not defined, and there is at most one default element.
 - `revision` is the name of git branch, used when neither <project> nor its 'remote' does not specify 'revision'
 - `remote` is the name of previously defined remote element
 - `dest-branch` is the name of git branch, if not set, `project` will use 'revison' by default
 - `sync-j` is the number of parallel jobs to use when syncing
 - `sync-c` set to true to only sync the specified git branch rather than whole ref space
 - `sync-s` set to true to also sync sub-projects
 - `sync-tags` set to false to only sync the specified git branch rather than the other ref tags

`<project>` is used to describle a single git repository to be cloned into the repo client workspace, could be multiple
  - `name` the name of sub repository, the name will be appeded onto its remotes fetch URL to generate the actual URL of git repository. its format will be `${remote.fetch}/${project.name}.git`
  - `path` when execute `repo sync`, the relative path to top directory of repo client work space, and the code in sub repository will be downloaded to this sub folder. if not specifed, the `project.name` will be used
  - `remote` the name of previously defined remote element
  - `revision` the branch name, could be relative to 'refs/heads', such as 'master', or absolute, such as 'refs/heads/master'. if not specifed in `project`, use the one defined in its responding `remote`. if still absent, use the one defined in `default`
  - `dest-branch` the name of git branch. when using `repo upload`, changes will be submitted for code review on this branch. if unspecified both here and in the default element, `revision` is used instead
  - `groups` list of groups to which this project belongs, whitespace or comma separated. all projects belong to the group `all`, and each project automatically belong to a group of its name:`name` and path:`path`
  - `clone-depth` set the depth to use when fetching the this project. if specifed, this value will override any value given to `repo init` with the `--depth` on the command line
  
`extend-project` is used to modify the atrributes of named project. this element is mostly used in a local manifest file, to modify the attributes of an existing project without completely replacing the existing project definition. this makes the local manifest more robust against changes to the original manifest
 - `path` if specifed, limit the change to projects checked out at the specified path, rather than all projects with the given name
 - `groups` list of additional groups to which this projects belongs.
 - `revision` if specified, override the revision of the original `project`
 - `remote` if specifed, override the remote of original `project`
 
 `copyfile` used as children of `project` element, could be zero or multiple. Each element describle a 'src-dest' pair of files, the 'src' file will be copied to the 'dest' place during `repo sync` command. 'src' is project relative, 'dest' is relative to the top of the tree. Copying from paths outside of the project or paths outside of the repo client is not allowed. 'src' and 'dest' must be files. Directory or symlinks are not allowed. Parent directories of 'dest' will be automatically created if missing
 
 `linkfile` is just like `copyfile`, instead of copying, it create a symlink.
 
 `remove-project` is used to delete the named project from the internal manifest table, possibly allowing a subsequence project element in the same manifest file to replace the project with a different source. this element is mostly used in a local manifest file, where the user can remove a project, and possibly replace it with their own definition.
  - `name` the name of project
 
 `include` provides the capability of including another manifest file into the originating manifest. Normal rules apply for the target manifest to include - it must be a usable manifest on its own.
  - `name` the manifest to include, specifed relative to the manifest repository's root.

more information about manifest format, please refer to [manifest format](https://gerrit.googlesource.com/git-repo/+/master/docs/manifest-format.md)

### Local Manifests
Additional remotes and projects may be added through local manifest file which stored in `${repo client}/.repo/local_manifests/*.xml`

user may add projects to the local manifest prior to a `repo sync` invocation, instructing repo to automatically download and manage these extra projects.

manifest files store in `${repo client}/.repo/local_manifests/*.xml` will be loaded in alphabetical order.

note that a `local_manifest.xml` could exist under `${repo client}/.repo/`, this method is deprecated in favor of using multiple manifest files as mentioned above. if it exist, it will be loaded before any manifest files stored in `${repo client}/.repo/local_manifests/*.xml`

A simple sample
```bash
yangliu@LT424684 MINGW64 /git-repo-demo/.repo
$ cat local_manifests/local_manifest.xml
<manifest>
        <remove-project name="YangLiu1024/GitSubTreeTestLeafRepo"></remove-project>
        <remove-project name="YangLiu1024/GitSubTreeTestSplitRepo"></remove-project>
</manifest>
```
if you check the manifest file which used by repo client here, you can execute `repo manifest`, and find that it print the merged manifest.xml instead of original one.
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo manifest
<?xml version="1.0" encoding="UTF-8"?>
<manifest>
  <remote fetch="https://github.com" name="github"/>

  <default remote="github" revision="refs/heads/master" sync-j="4"/>

  <project name="YangLiu1024/GitSubTreeTestHostRepo" path="host"/>
</manifest>
```

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
* if this project has never been synced before, its almost equal to `git clone`, all branches in remote repository will be copied to local project directory, but there is no local branch
* if this project has no local branch or do nothing change yet, simply pull the latest project to project folder
* if this project has some commits already, then its equal to `git remote update; git rebase`, it will use currently checked-out branch in the local project directory to rebase. 
  - if the local branch isn't tracking a remote branch, then no synchronization occurs for the project
  - if the git rebase operation result in merge conflicts, fix conflict first, then `git add`, finally use `git rebase --continue`, to resolve the conflicts
  
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

this command will create new branch for all or specifed projects and switch to this branch automatically, <b>and the new created branch is created based on the ${revision} branch defined in manifest file and it will track this remote branch on `git pull` automatically, but `git push` is not</b>

if you want to create responding remote branch, you can execute `git push -u github ${branch-name}`, then a new remote branch with same name will be created.

'-u' here is used to set upstream binding, the local branch will bind to responding remote branch on `git pull` and `git push`.

if does not add '-u' here, you need to execute `git branch --set-upstream-to github/${branch-name}` to change `git pull` binding from remote/${revision} to remote/${branch-name}

if you want to push the change of local ${branc-name} to remote/${revision}, you need to execute `git push github HEAD:${revision}`

```git
$ .repo/repo/repo start modify-through-repo leaf split
$ .repo/repo/repo status
project split/                                  branch modify-through-repo${}
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

### List projects and their associated directories
execute `repo list` to display project folder path and its responding project name
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo list
host : YangLiu1024/GitSubTreeTestHostRepo
```

### Get info on the branch
execute `repo info` to get info on the manifest branch, current branch or unmerged branches
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo info
Manifest branch: refs/heads/master
Manifest merge branch: refs/heads/master
Manifest groups: all,-notdefault
----------------------------
Project: YangLiu1024/GitSubTreeTestHostRepo
Mount path: C:/Localdata/YangLiusoftware/git/Git/git-repo-demo/host
Current revision: 7fe624869a051ca79fff82216b222fc1ea15214b
Current branch: master
Manifest revision: refs/heads/master
Local Branches: 1 [master]
----------------------------
```

### Delete branch
execute `repo abandon [--all | <branch_name>] [<project>...]` to abandon the branch permanently(including its all history) from your local repository.

its equal to `git branch -D <branch_name>`. note that `-all` means delete all branches for specified projects
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo info
Manifest branch: refs/heads/master
Manifest merge branch: refs/heads/master
Manifest groups: all,-notdefault
----------------------------
Project: YangLiu1024/GitSubTreeTestHostRepo
Mount path: C:/Localdata/YangLiusoftware/git/Git/git-repo-demo/host
Current revision: 7fe624869a051ca79fff82216b222fc1ea15214b
Current branch: test-branch
Manifest revision: refs/heads/master
Local Branches: 2 [master, test-branch]
----------------------------
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo abandon test-branch host
Abandoned branches:
test-branch              | host

yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo branch
   master                    | in all projects

yangliu@LT424684 MINGW64 /git-repo-demo
$ cd host/

yangliu@LT424684 MINGW64 /git-repo-demo/host ((7fe6248...))
$ git status
HEAD detached at 7fe6248
nothing to commit, working tree clean

yangliu@LT424684 MINGW64 /git-repo-demo/host ((7fe6248...))
$ git branch
* (HEAD detached at 7fe6248)
  master
```
### Checkout branch
`repo checkout <branch_name> [<project>...]` checks out an exsiting branch that was previously created by `repo start`

This command is equal to `repo forall [<project>...] -c git checkout <branch_name>`

### Diff manifest
`repo diffmanifests manifest1.xml [manifest2.xml] [options]` show the difference between projects revisions of manifest1 and manifest2. if manifest2 is not specified, current manifest.xml will be used instead.
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo diffmanifests .repo/manifests/default.xml

removed projects :

        leaf at revision refs/heads/master
        split at revision refs/heads/master
```
### Display unmerged branch
`repo overview` command is used to display an overview of the projects branches, and list any local commits that have not yet been merged into the project.

by default, all branchs are displayed, and option `-b` to restrict the output to only currently checked out branch of each project
```bash
yangliu@LT424684 MINGW64 /git-repo-demo
$ .repo/repo/repo overview
Deprecated. See repo info -o.
Projects Overview

project host/
* test-branch                       ( 1 commit , Wed Sep 2 14:52:58 2020 +0800)
                                      - 9b4bc7ce test repo overview
```
note that this command is deprecated, instead you can use `repo info -o`

### Rebase with upstream branch
`repo rebase [<project>...]` command rebase local branches on upstream branch

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

