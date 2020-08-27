# Introduction to git subtree.

## Git subtree help
```bash
-bash-4.1$ git subtree
usage: git subtree add   --prefix=<prefix> <commit>
   or: git subtree add   --prefix=<prefix> <repository> <ref>
   or: git subtree merge --prefix=<prefix> <commit>
   or: git subtree pull  --prefix=<prefix> <repository> <ref>
   or: git subtree push  --prefix=<prefix> <repository> <ref>
   or: git subtree split --prefix=<prefix> <commit...>

    -h, --help            show the help
    -q                    quiet
    -d                    show debug messages
    -P, --prefix ...      the name of the subdir to split out
    -m, --message ...     use the given message as the commit message for the merge commit

options for 'split'
    --annotate ...        add a prefix to commit message of new commits
    -b, --branch ...      create a new branch from the split subtree
    --ignore-joins        ignore prior --rejoin commits
    --onto ...            try connecting new tree to an existing one
    --rejoin              merge the new branch back into HEAD

options for 'add', 'merge', and 'pull'
    --squash              merge subtree changes as a single commit
```

## Usage case
1. Sometimes, we want to split our code to separated repository and keep its commit history.
2. Sometimes, we put common code in separated repository and want to use its code in our project directly.

## How to use 
Assume that we have a repository [Host repo](https://github.com/YangLiu1024/GitSubTreeTestHostRepo), add we want to split its icons folder to [Split repo](https://github.com/YangLiu1024/GitSubTreeTestSplitRepo), and add a sub repository [Leaf repo](https://github.com/YangLiu1024/GitSubTreeTestLeafRepo).

### Split sub folder icons
the command format: `git subtree split --prefix=<path-to-sub-folder> --branch <branch-name> --squash`

this command will extract all previously commits of sub folder to generate a new branch which named <branch-name>, `--squash` is optional, which means wrap all previously commits into one commit.
  
for our case, first, cd to `Host repo` folder, then execute
```git
git subtree split -P icons --branch icons
```
  
now, a new branch `icons` which contain all previous commit history of `icons` folder is created.

### Push to new split repository
Now, create a new folder, such as `GitSubTreeTestSplitRepo`, then execute `git init` to initialize. 

Then execute  `git pull <path-to-host-repo> icons` to merge `icons` branch of host repo to new repository master branch.

Now, we got the `icons` branch in new repository, remaining work is to push it to remote repository.

`git remote add origin <url-to-new-repository>`, then `git push -u origin master`.

now, a new repository with all history commits is generated.

### Update host repository
We just created a new repository with splited code, then need to clean host repository first.
```git
git rm -rf icons
git add .
git commit -m "remove icons folder, because split it to another repository"
git push
```
now, the `icons` folder is deleted. Need to change to depend on the new created split repository

the command format: `git subtree add -P <path-to-sub-folder> <url-to-repository> <branch-name> --squash`

`<path-to-sub-folder>` is used to store the code of the `<url-to-repository>` with branch `<branch-name>`

for our case,
```git
git subtree add -P icons https://github.com/YangLiu1024/GitSubTreeTestSplitRepo.git master
```
the code of `Split repo` of branch `master` will be download to `icons` folder. 

now you will find that your branch is ahead of origin which is introduced by `git subtree add`, so need to `git push` to sync the change

### Add leaf repository
This is similar with adding `Split repo`.
```git
git subtree add -P leaf https://github.com/YangLiu1024/GitSubTreeTestLeafRepo.git master
```
now, a new folder `leaf` is created which contain the code of `Leaf repo` of branch `master`.

Finally, `git push` to sync the change.

### Push change in host repo to sub repo
User develop on host repository as usually, and they may change some files under `split repo` or `leaf repo` folder.
all things work expected as single repository. 

note that the files under `split repo` folder or 'leaf repo' folder is just synced within current host repository, the real 'Split repo' or `Leaf repo` is not impacted.

And at suitable time, user want to sync the change on `split repo` folder or `leaf repo` folder to real repository, he need to execute
```git
git subtree push -P <path-to-sub-folder> <url-to-repository> <branch-name>
```
to push the change to real sub repository.

Note that current host repo will extract all commits to `<path-to-sub-folder>` intelligently, then push to <url-to-repository> <branch-name>
  
### Pull change in sub repo to host repo
The sub repo maybe get some update by someone others, to sync the change to host repo, need to execute
```git
git subtree pull -P <path-to-sub-folder> <url-to-repository> <branch-name>
```
note that `git pull` in host repo will only fetch the change made in host repo, if sub repo is changed by someone else, need to execute `git subtree pull` to sync


## Benifit of subtree
The sub repository managed by subtree is transparent to user, its just a normal folder to user.
Just need to sync sub repository at suitable time.
