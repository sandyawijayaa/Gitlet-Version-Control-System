package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Main.*;

/** Defines command methods that will be called in Main.java.
 * @author Sandya Wijaya
 */
//public class Repo2 {
//
//    /**
//     * Current working directory.
//     */
//    private File CWD;
//    /**
//     * Name of current branch; initially this is "master".
//     */
//    private String currentBranchName = "master";
//    /**
//     * The staging area.
//     */
//    private StagingArea stage;
//
//    private String HEAD = "master";
//
//    public Repo2() {
//        CWD = new File (System.getProperty("user.dir")); // get prop
//        File headFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        if (headFile.exists()) {
//            HEAD = Utils.readContentsAsString(headFile);
//        }
//        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
//        if (stageFile.exists()) {
//            stage = Utils.readObject(stageFile, StagingArea.class);
//        } else {
//            stage = new StagingArea();
//        }
//    }
//
//    /**
//     * Creates a new Gitlet version-control system in the current directory.
//     */
//    public void createGitlet(String[] args) {
//        validateNumArgs("init", args,1);
//        File git = new File(".gitlet");
//        if (git.exists()) {
//            System.out.println("A Gitlet version-control system already exists in the current directory.");
//            System.exit(0);
//        }
//        // make folders
//        GIT.mkdirs();
//        BLOBS_FOLDER.mkdirs();
//        COMMITS_FOLDER.mkdirs();
//        STAGING_FOLDER.mkdirs();
//        BRANCHES_FOLDER.mkdirs();
//        // make initial commit
//        Commits defaultCommit = new Commits();
//        File defaultFile = Utils.join(COMMITS_FOLDER, defaultCommit.calcHash() + ".txt");
//        Utils.writeObject(defaultFile, defaultCommit);
//        // make master file
//        File masterFile = Utils.join(BRANCHES_FOLDER, "master.txt");
//        Utils.writeContents(masterFile, defaultCommit.calcHash());
//        // make current aka head
//        File headFile = Utils.join(BRANCHES_FOLDER,"HEAD.txt");
//        Utils.writeContents(headFile, defaultCommit.calcHash()); // store commitid or currentbranchname
//        // make stage
//        StagingArea stage = new StagingArea();
//        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
//        Utils.writeObject(stageFile, stage);
//    }
//
//    /**
//     * Saves a snapshot of the tracked files in the head commit and staging area.
//     */
//    public void createCommit(String args[]) {
//        validateNumArgs("commit", args,2);
//        // commit have 3 instance var: message, parent, files
//        // 1. Find out message
//        String message = args[1];
//        if (stage.getTrackedFiles().isEmpty() && stage.getUntrackedFiles().isEmpty()) {
//            System.out.print("No changes added to the commit.");
//            return;
//        }
//        if (message.equals("")) {
//            System.out.print("Please enter a commit message.");
//            return;
//        }
//        // 2. Find the new files to create the commit by making new treemap then load in files from parent and staging area
//        Commits curr = getCurrentCommit();
//        TreeMap<String, String> newFiles = (TreeMap) curr.getFiles().clone();
//        ArrayList<String> filesToAdd = new ArrayList<>(stage.getTrackedFiles().keySet());
//        for (String fileName : filesToAdd) {
//            newFiles.put(fileName, stage.getTrackedFiles().get(fileName));
//        }
//
//        // 3. find parent
//        String newParent = getCurrentCommit().calcHash();
//        // now make the commit
//        Commits newC = new Commits(message, newParent, newFiles);
//        // persistance timeee
//        String newHash = newC.calcHash();
//        File newB = Utils.join(BRANCHES_FOLDER,"HEAD.txt");
//        Utils.writeContents(newB, newHash);
//        File newCFile = Utils.join(COMMITS_FOLDER, newHash + ".txt");
//        Utils.writeObject(newCFile, newC);
//        stage.clear();
//        File newStage = Utils.join(STAGING_FOLDER, "stage.txt");
//        Utils.writeObject(newStage, stage);
//    }
//
//    public Commits getCurrentCommit() {
//        // store current commit id using readobject
//        File headFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        String headHash = Utils.readContentsAsString(headFile);
//        File currFile = Utils.join(COMMITS_FOLDER, headHash + ".txt");
//        Commits currentCommit = Utils.readObject(currFile, Commits.class);
//        return currentCommit;
//    }
//
//    /**
//     * Adds a copy of the file as it currently exists to the staging area (see the description of the commit command).
//     * For this reason, adding a file is also called staging the file for addition.
//     * Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
//     * The staging area should be somewhere in .gitlet.
//     *
//     * If the current working version of the file is identical to the version in the current commit,
//     * do not stage it to be added, and remove it from the staging area if it is already there
//     * (as can happen when a file is changed, added, and then changed back).
//     *
//     * The file will no longer be staged for removal (see gitlet rm),if it was at the time of the command.
//     * @param args Array in format: {'add', file name}
//     */
//    public void addFile(String[] args) {
//        validateNumArgs("add", args, 2);
//        String fileName = args[1];
//        File toAdd = new File(fileName);
//        if (toAdd.exists()) {
//            byte[] blob = Utils.readContents(toAdd);
//            String blobHash = Utils.sha1(blob);
//            File blobFile = Utils.join(BLOBS_FOLDER,blobHash + ".txt");
//            Utils.writeContents(blobFile, blob);
//
//            stage.addToTracked(fileName, blobHash);
//            File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
//            Utils.writeObject(stageFile, stage);
//        } else {
//            System.out.print("File does not exist.");
//            System.exit(0);
//        }
//    }
//
//    /**
//     * Unstages the file if it is currently staged for addition.
//     */
//    public void removeFile(String[] args) {
//        validateNumArgs("rm", args, 2);
//        String fileName = args[1];
//        boolean isStaged = stage.getTrackedFiles().containsKey(fileName);
//        Commits curr = getCurrentCommit();
//        boolean isTracked = false;
//        ArrayList<String> committedFiles = new ArrayList<>(curr.getFiles().keySet());
//        for (String f : committedFiles) {
//            if (f.equals(fileName)) {
//                isTracked = true; // if fileName given in current commit, then file is tracked in current commit
//            }
//        }
//        if (isTracked) { // If tracked in the current commit, stage for removal and remove the file from CWD
//            Utils.restrictedDelete(fileName);
//            stage.addToUntracked(fileName);
//            if (isStaged) {
//                stage.getTrackedFiles().remove(fileName);
//            }
//            File updatedStage = Utils.join(STAGING_FOLDER, "stage.txt");
//            Utils.writeObject(updatedStage, stage);
//        } else if (isStaged) {
//            stage.getTrackedFiles().remove(fileName);
//            File updateStage = Utils.join(STAGING_FOLDER, "stage.txt");
//            Utils.writeObject(updateStage, stage);
//        } else {
//            System.out.print("No reason to remove the file.");
//            System.exit(0);
//        }
//    }
//
//
//    /**
//     * Displays information about each commit starting from the head commit backwards until the initial commit.
//     */
//    public void showLog(String[] args) {
//        validateNumArgs("log", args,1);
//        Commits currentCom = getCurrentCommit();
//        while (currentCom != null) {
//            System.out.println("===");
//            System.out.println("commit " + currentCom.calcHash());
//            System.out.println("Date: " + currentCom.getDatetime());
//            System.out.println(currentCom.getMessage());
//            System.out.println();
//            if (currentCom.getParent() != null) {
//                File nextCommit = Utils.join(COMMITS_FOLDER,currentCom.getParent() + ".txt");
//                currentCom = Utils.readObject(nextCommit, Commits.class);
//            } else {
//                break;
//            }
//        }
//    }
//
//    /**
//     * Displays information about each commit ever made.
//     */
//    public void showGlobalLog(String[] args) {
//        validateNumArgs("global-log", args,1);
//        List<String> allCommits = Utils.plainFilenamesIn(COMMITS_FOLDER);
//        for (String fileName : allCommits) {
//            File currFile = Utils.join(CWD,fileName);
//            Commits currCommit = Utils.readObject(currFile, Commits.class);
//            System.out.println("===");
//            System.out.println("commit " + currCommit.calcHash());
//            System.out.println("Date: " + currCommit.getDatetime());
//            System.out.println(currCommit.getMessage());
//            System.out.println();
//        }
//    }
//
//    /**
//     * Finds all commits with the given commit message, then prints their IDs one per line.
//     */
//    public void findID(String[] args) {
//        validateNumArgs("find", args,2);
//        String commitMessage = args[1];
//        File commitFolder = Utils.join(CWD, ".gitlet/commits"); // not sureee
//        String[] allCommits = commitFolder.list();
//        boolean found = false;
//        for (String fileName : allCommits) {
//            File oneFile = Utils.join(COMMITS_FOLDER, fileName);
//            Commits currFile = Utils.readObject(oneFile, Commits.class);
//            if (currFile.getMessage().equals(commitMessage)) {
//                System.out.println(currFile.calcHash());
//                found = true;
//            }
//        }
//        if (!found) {
//            System.out.println("Found no commit with that message.");
//            System.exit(0);
//        }
//    }
//
//    /**
//     * Displays branches that currently exist, and marks the current branch with a *. Also displays what files have been staged for addition or removal.
//     */
//    public void displayStatus(String[] args) {
//        validateNumArgs("status", args,1);
//        List<String> existingBranches = new ArrayList<String>(); // list of names of existing branches
//        File[] allBranches = BRANCHES_FOLDER.listFiles();
//        for (File file : allBranches) {
//            existingBranches.add(file.getName().substring(0, file.getName().length() - 4)); // get rid of ".txt"
//        }
//        existingBranches.remove("HEAD");
//        existingBranches.remove(HEAD);
//        existingBranches.add("*" + HEAD);
//        Collections.sort(existingBranches);
//        List<String> stagedFiles = new ArrayList<String>();
//        for (Map.Entry<String, String> entry : stage.getTrackedFiles().entrySet()) {
//            stagedFiles.add(entry.getKey());
//        }
//        Collections.sort(stagedFiles);
//        List<String> remFiles = new ArrayList<String>();
//        for (String file : stage.getUntrackedFiles()) {
//            remFiles.add(file);
//        }
//        Collections.sort(remFiles);
//
//        System.out.println("=== Branches ===");
//        for (String branch : existingBranches) {
//            System.out.println(branch);
//        }
//        System.out.println();
//        System.out.println("=== Staged Files ===");
//        for (String staged : stagedFiles) {
//            System.out.println(staged);
//        }
//        System.out.println();
//        System.out.println("=== Removed Files ===");
//        for (String removed : remFiles) {
//            System.out.println(removed);
//        }
//        System.out.println();
//        System.out.println("=== Modifications Not Staged For Commit ===");
//        System.out.println();
//        System.out.println("=== Untracked Files ===");
//    }
//
//    /**
//     * Takes the file in the head commit of the current branch, and puts it in the working directory.
//     */
//    public void checkoutHead(String[] args) {
////        String fileName = args[2];
////        String headHash = getCurrentCommit().calcHash();
////        String[] newArgs = new String[]{"checkout", headHash, "--", fileName};
////        checkoutGiven(newArgs);
//        validateNumArgs("checkout", args,3);
//        String fileName = args[2];
//        Commits headCommit = getCurrentCommit();
//        TreeMap<String, String> commitMap = headCommit.getFiles();
//        if (!commitMap.containsKey(fileName)) {
//            System.out.println("File does not exist in that commit.");
//            System.exit(0);
//        }
//        File checkDelete = new File(fileName);
//        if ((checkDelete).exists()) {
//            Utils.restrictedDelete(checkDelete);
//        }
//        File newBlob = Utils.join(BLOBS_FOLDER, headCommit.getFiles().get(fileName) + ".txt");
//        byte[] storeRFile = Utils.readContents(newBlob);
//        File newFile = new File(fileName);
//        Utils.writeContents(newFile, storeRFile);
//    }
//
//
//    /**
//     * Takes the file in the commit with the given id, and puts it in the working directory.
//     */
//    public void checkoutGiven(String[] args) {
//        validateNumArgs("checkout", args,4);
//        String commitID = args[1];
//        String fileName = args[3];
//        String[] allCommits = COMMITS_FOLDER.list();
//        for (int i = 0; i < allCommits.length; i++) {
//            if (allCommits[i].contains(commitID)) {
//                commitID = allCommits[i];
//                commitID = commitID.substring(0, commitID.length() - 4);
//                break;
//            }
//        }
//
//        File temp = Utils.join(COMMITS_FOLDER,commitID + ".txt");
//        Commits currCommit = Utils.readObject(temp, Commits.class);
//        if (currCommit == null) {
//            System.out.println("No commit with that id exists.");
//            System.exit(0);
//        } else if (!currCommit.getFiles().containsKey(fileName)) {
//            System.out.println("File does not exist in that commit.");
//            System.exit(0);
//        } else {
//            File checkDelete = new File(fileName);
//            if ((checkDelete).exists()) {
//                Utils.restrictedDelete(checkDelete);
//            }
//            File newFile = new File(fileName);
//            File newBlob = Utils.join(BLOBS_FOLDER, currCommit.getFiles().get(fileName) + ".txt");
//            byte[] contentBlob = Utils.readContents(newBlob);
//            Utils.writeContents(newFile, contentBlob);
//        }
//    }
//
//    /**
//     * Takes all the files in the head commit of the branch with the given name, and puts it in the working directory.
//     */
//    public void checkoutAll(String[] args) {
//        validateNumArgs("checkout", args,2);
//        String branchName = args[1];
//        File givenBranch = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
//        if (!givenBranch.exists()) {
//            System.out.println("No such branch exists.");
//            System.exit(0);
//        }
//
//        String newCommitID = Utils.readContentsAsString(givenBranch);
//        File newCommitFile = Utils.join(COMMITS_FOLDER, newCommitID + ".txt");
//        Commits newCommit = Utils.readObject(newCommitFile, Commits.class);
//        Commits currCommit = getCurrentCommit();
//        ArrayList<File> fileList = new ArrayList<>();
//        for (File f : CWD.listFiles()) {
//            if (f.getName().endsWith(".txt")) {
//                fileList.add(f);
//            }
//        }
//        Commits commitToCheckout = newCommit;
//        for (File f : fileList) {
//            if (!currCommit.getFiles().containsKey(f.getName())
//                    && commitToCheckout.getFiles().containsKey(f.getName())) {
//                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
//                System.exit(0);
//            }
//        }
//        ArrayList<String> fileNames = new ArrayList<>(commitToCheckout.getFiles().keySet());
//        for (File f : fileList) {
//            if (!commitToCheckout.getFiles().containsKey(f.getName())
//                    && currCommit.getFiles().containsKey(f.getName())) {
//                Utils.restrictedDelete(f);
//            }
//        }
//        for (String f : fileNames) {
//            File newFile = new File(f);
//            String blobHash = commitToCheckout.getFiles().get(f);
//            File blobFile = Utils.join(BLOBS_FOLDER, blobHash + ".txt");
//            byte[] blobBytes = Utils.readContents(blobFile);
//            Utils.writeContents(newFile, blobBytes);
//        }
//        stage.clear();
//        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
//        Utils.writeObject(stageFile, stage);
//        File branchFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        Utils.writeContents(branchFile, branchName);
//    }
//
//    /**
//     * Creates a new branch with the given name and points it to the head node.
//     */
//    public void createBranch(String[] args) {
//        validateNumArgs("branch", args, 2);
//        String branchName = args[1];
//        File branchFile = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
//        if (branchFile.exists()) {
//            System.out.print("A branch with that name already exists.");
//            System.exit(0);
//        }
//        File headBranch = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        String headSha1 = Utils.readContentsAsString(headBranch);
//        Utils.writeContents(branchFile, headSha1); // now the new branch is the head
//    }
//
//    /**
//     * Deletes the branch with the given name.
//     */
//    public void removeBranch(String[] args) {
//        validateNumArgs("rm-branch", args, 2);
//        String branchName = args[1];
//        File currBranch = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        String currBranchName = Utils.readContentsAsString(currBranch); // if given branch name is the current branch name, cannot
//        if (branchName.equals(currBranchName)) {
//            System.out.print("Cannot remove the current branch.");
//            System.exit(0);
//        }
//        File branchFile = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
//        if (!branchFile.delete()) {
//            System.out.print("A branch with that name does not exist.");
//            System.exit(0);
//        }
//    }
//
//    /**
//     * Checks out all files in the given commit and removes tracked files that are not in said commit. It also moves the current branch's head to said commit node.
//     */
//    public void resetBranch(String[] args) {
//        validateNumArgs("reset", args, 2);
//        String commitID = args[1];
//        File checkoutCommitFile = Utils.join(COMMITS_FOLDER, commitID + ".txt");
//        Commits checkoutCommit = Utils.readObject(checkoutCommitFile, Commits.class);
//        if (checkoutCommit == null) {
//            System.out.println("No commit with that id exists.");
//            System.exit(0);
//        }
//        // get all possible files before getting rid of not needed ones
//        ArrayList<File> fileList = new ArrayList<>();
//        for (File f : CWD.listFiles()) {
//            if (f.getName().endsWith(".txt")) {
//                fileList.add(f);
//            }
//        }
//        Commits currCommit = getCurrentCommit();
//        for (File f : fileList) {
//            if (!currCommit.getFiles().containsKey(f.getName())
//                    && checkoutCommit.getFiles().containsKey(f.getName())) {
//                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
//                System.exit(0);
//            }
//        }
//        ArrayList<String> fileNames = new ArrayList<>(checkoutCommit.getFiles().keySet());
//        for (File f : fileList) {
//            if (!checkoutCommit.getFiles().containsKey(f.getName())
//                    && currCommit.getFiles().containsKey(f.getName())) {
//                Utils.restrictedDelete(f);
//            }
//        }
//        for (String f : fileNames) {
//            String blobHash = checkoutCommit.getFiles().get(f);
//            File newBlobFile = Utils.join(BLOBS_FOLDER, blobHash + ".txt");
//            byte[] blobBytes = Utils.readContents(newBlobFile);
//            Utils.writeContents(new File(f), blobBytes);
//        }
//        stage.clear();
//        File resetStage = Utils.join(STAGING_FOLDER, "stage.txt");
//        Utils.writeObject(resetStage, stage);
//        File resetBranch = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
//        Utils.writeContents(resetBranch, commitID);
//    }
//
//    /**
//     * Merges files from the given branch and the current branch.
//     */
//    public void mergeFiles(String[] args) {
//        validateNumArgs("merge", args, 2);
//    }
//
//}




