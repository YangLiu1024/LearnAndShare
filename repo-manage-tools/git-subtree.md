# Introduction to git subtree.

## Usage case
1. Sometimes, we want to split our code to separated repository and keep its commit history.
2. Sometimes, we put common code in separated repository and want to use its code in our project directly.

## How to use 
Assume that we have a repository [Host repo](https://github.com/YangLiu1024/GitSubTreeTestHostRepo), add we want to split its icons folder to another repository, and add a sub repository [Leaf repo](https://github.com/YangLiu1024/GitSubTreeTestLeafRepo).

### Split sub folder
the command format: `git subtree split --prefix=<path-to-sub-folder> --branch <branch-name> --squash`

this command will extract all previously commits of sub folder to generate a new branch which named <branch-name>, `--squash` is optional, which means wrap all previously commits into one commit.
  
for our case, first, cd to `Host repo` folder, then execute
```git
git subtree split -P icons --branch icons
```
  
now, a new branch `icons` which contain all previous commit history of `icons` folder is created.

### Push to new repository
Now, create a new folder, such as `GitSubTreeTestSplitRepo`, then execute `git init` to initialize. 

Then execute  `git pull <path-to-host-repo> icons` to merge `icons` branch of host repo to new repository master branch.

Now, we got the `icons` branch in new repository, remaining work is to push it to remote repository.

`git remote add origin <url-to-new-repository>`, then `git push -u origin master`.

now, a new repository with all history commits is generated.

## Benifit
The sub repository managed by subtree is transparent to user, its just a normal folder to user.
Just need to sync sub repository at suitable time.
