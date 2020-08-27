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
The information about how to manage multiple repository and all configuration of sub repository is stored in an independent repository, which usually named `manifest`.
