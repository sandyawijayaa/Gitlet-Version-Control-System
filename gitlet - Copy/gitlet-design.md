# Gitlet Design Document
author: Sandya Wijaya

## 1. Classes and Data Structures

### Main.java
This class is the entry point of the program. It implements methods to set up persistence and support each command of the program. It also decides which command to call from the repo based of the argument, then calls said command method.

#### Fields

1. static final File CWD: A pointer to the current working directory of the program.
2. static final File GITLET_FOLDER: A pointer to the GITLET directory in the current working directory.
3. static final File CURRENT_BRANCH: A pointer to the current branch; the branch that we are working on.
4. static final File MASTER_BRANCH: A branch that initially points to the initial commit. It is a branch that tells us where we start.
5. static final File BRANCHES: A treemap whose key is the branch name, and value is the head commit of that branch.

### Commits.java
This class represents a commit instance. It has functions that define characteristics and behaviors of commits.

#### Fields
1. static final File COMMIT_FOLDER: A pointer to the directory persisting commit instances.
2. String _message: A string representing the message of a commit.
3. Time _time: A time representing the timestamp the commit was done.
4. String _parent: SHA1 of the previous commit right before it.
5. Treemap _files: A treemap with key file (Object data type, represents the file changed during said commit) and value SHA1 (String data type, unique identifier of a commit).

### Blobs.java
This class represents the contents of a file. For example, if we have a media file; a video, the blob is the code that allows this video to run and be the way it is.

#### Fields
1. String _contents: A string representing the contents of a file.

### StagingArea.java
This class represents where files are added or removed. With that, it creates new blobs and maps them to the files present in each commit.

#### Fields
1. Treemap _add: A treemap that adds a file to a commit.
2. Treemap _remove: A treemap that removes a file from a commit.

### Repo.java
This class defines command methods that will be called in Main.java. Different command methods will be called depending on the argument passed in.

#### Fields
1. (no fields)


## 2. Algorithms

### Main.java
1. main(String[] args): This is the entry point of the program. It first checks to make sure that the input array is not empty. Then, it calls setupPersistence to create the /.capers and /.capers/dogs for persistance. Lastly, depending on the input argument, different functions are called to perform the operation.
2. setupPersistence(): If the directory for persisting dog instances does not exist yet, then make the directory. The directory .capers will be created at the same time when the dog directory is being made.
3. exitWithError(String message): Prints out MESSAGE and exits with error code -1.
4. validateNumArgs(String cmd, String[] args, int n):  Checks the number of arguments versus the expected number and throws a RuntimeException if they do not match.

### Commits.java
1. Commit(String message, Time timeStamp, String parent, Treemap files): It creates a commit object with the specified parameters.
2. fromFile(String message): It reads in and deserializes a commit from a file with message MESSAGE in COMMIT_FOLDER. If a commit with MESSAGE passed in doesn't exist, throw IllegalArgumentException error.
3. createCommit (String[] args): Saves a snapshot of the tracked files in the head commit and staging area.

### Blobs.java
1. Blob (String contents): It creates a blob with the specified parameters.
2. toString(): It defines the string representation of a blob.

### StagingArea.java
1. addFile (String[] args): Adds a copy of the given file to the staging area.
2. removeFile (String[] args):  Unstages the file if it is currently staged for addition.

### Repo.java
1. createGitlet (String[] args): Creates a new Gitlet version-control system in the current directory.
2. showLog (Strint[] args): Displays information about each commit starting from the head commit backwards until the initial commit.
3. showGlobalLog (String[] args): Displays information about each commit ever made.
4. findID (String[] args): Finds all commits with the given commit message, then prints their IDs one per line.
5. displayStatus (String[] args): Displays branches that currently exist, and marks the current branch with a *. Also displays what files have been staged for addition or removal.
6. checkoutHead (String[] args): Takes the file in the head commit of the current branch, and puts it in the working directory.
7. checkoutGiven (Sting[] args): Takes the file in the commit with the given id, and puts it in the working directory.
8. checkoutAll (String[] args): Takes all the files in the head commit of the branch with the given name, and puts it in the working directory.
9. createBranch (String[] args): Creates a new branch with the given name and points it to the head node.
10. removeBranch (String[] args): Deletes the branch with the given name.
11. resetFiles (String[] args): Checks out all files in the given commit and removes tracked files that are not in said commit. It also moves the current branch's head to said commit node.
12. mergeFiles (String[] args: Merges files from the given branch and the current branch.


## 3. Persistence

### init
- Creates a new file in the current directory.
- Checks if this file already exists in directory. If not, make file directories.
- Does not create any files, but creates the starting commit with message 'initial commit' and timestamp 00:00:00 UTC, Thursday, 1 January 1970

### add [file name]
- Create new file with file name
- Searches for said file
- If not found, fill contents into file

### commit [message]
- Add the tracked files to staging and current commit.
- Update the files if it is:
  - Tracked in the current commit
  - Have been staged
  - Else keep the files from parent’s commit.
- Untrack the file from current commit if it staged for removal.
- Add new commit as a new node in the commit tree.
- moves head commit from parent commit's to current commit.
- Clear staging area

### rm [file name]
- Find the staged file for addition.
- If the staged file is tracked in current commit, stage the file for removal.
  - Remove the file from working directory.
- Else, unstage the file.
- If the head commit does not track the file nor the file is staged, print error message.

### log
- Searches for the current head commit
- For each commit until the initial commit, display the following information about the commit
  - commit id
  - time stamp of commit
  - commit message

### global-log
- Similar to log, but displays all the commits in history/log (all commits ever made)

### find [commit message]
- Locates the commit with given commit message
- Prints the commits' ids one per line with the inputted commit message.

### status
- Displays existing branches.
  - Current branch marked with *.
- Displays addition or removal staged files and untracked files.
- Displays modified but not staged commits.

### checkout [--] [file name]
- Find current branch
- Locate the head commit in this current branch
- Search for the file with this given file name in this head commit
  - If it exists, overwrite current version in the directory
- Put this verison of the file into the working directory.

### checkout [commit id] [--] [file name]
- Locate the commit with given commit id
- Search for the file with this given file name in this commit
  - If it exists, overwrite current version in the directory
- Put this verison of the file into the working directory.

### checkout [branch name]
- Find branch with given branch name
- Locate the head commit in this current branch
- Put all the files in this head commit into the working directory.
- Update given branch to be the CURRENT_BRANCH

### branch [branch name]
- Creates a new branch with the given name
- Head commit for said branch should be the current head node

### rm-branch [branch name]
- Finds branch with given name in directory
- If found, delete said branch

### reset [commit id]
- Find commit with given commit id in commit log/commit history
- If found, switch to this version/this commit

### merge [branch name]
- Find branch with given name in directory
  - If branch with given name is the master branch, we have two possible split points
  - If branch with given name is not master branch, we have one split point
  - (split point: latest common ancestor of the current and given branch heads)
- After merging, there is no need to commit as commit is automatically done
- Print message: “Given branch is an ancestor of the current branch”

## 4. Design Diagram

https://drive.google.com/file/d/1nnw5Y37VK30whSbkUFu4P0ispp9Xu_xx/view?usp=sharing