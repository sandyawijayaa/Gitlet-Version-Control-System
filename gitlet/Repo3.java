package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;

import static gitlet.Main.*;

/** Defines command methods that will be called in Main.java.
 * @author Sandya Wijaya
 */
public class Repo3 {

    /**
     * Current working directory.
     */
    private File cwd;
    /**
     * The staging area.
     */
    private StagingArea stage;
    /**
     * The name of current branch.
     */
    private String head = "master";


    public Repo3() {
        cwd = new File(System.getProperty("user.dir"));
        File headFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        if (headFile.exists()) {
            head = Utils.readContentsAsString(headFile);
        }
        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
        if (stageFile.exists()) {
            stage = Utils.readObject(stageFile, StagingArea.class);
        } else {
            stage = new StagingArea();
        }
    }

    /**
     * Creates a new Gitlet version-control system in the current directory.
     *
     * @param args arguments
     */
    public void createGitlet(String[] args) throws IOException {
        validateNumArgs("init", args, 1);
        File git = new File(".gitlet");
        if (git.exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }
        GIT.mkdirs();
        BLOBS_FOLDER.mkdirs();
        COMMITS_FOLDER.mkdirs();
        STAGING_FOLDER.mkdirs();
        BRANCHES_FOLDER.mkdirs();
        Commits defaultCommit = new Commits();
        File defaultFile = Utils.join(COMMITS_FOLDER,
                defaultCommit.calcHash() + ".txt");
        Utils.writeObject(defaultFile, defaultCommit);
        File masterFile = Utils.join(BRANCHES_FOLDER, "master.txt");
        masterFile.createNewFile();
        Utils.writeContents(masterFile, defaultCommit.calcHash());
        File headFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        headFile.createNewFile();
        Utils.writeContents(headFile, "master");
        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
        Utils.writeObject(stageFile, stage);
    }

    public String getHEAD() {
        return head;
    }

    /**
     * Saves a snapshot of the tracked files in the
     * head commit and staging area.
     *
     * @param args arguments
     */
    public void createCommit(String[] args) {
        validateNumArgs("commit", args, 2);
        String message = args[1];
        if (stage.getStagedAddFiles().isEmpty()
                && stage.getStagedRemoveFiles().isEmpty()) {
            System.out.print("No changes added to the commit.");
            return;
        }
        if (message.equals("")) {
            System.out.print("Please enter a commit message.");
            return;
        }
        Commits curr = getCurrentCommit();
        @SuppressWarnings("unchecked")
        TreeMap<String, String> newFiles = (TreeMap) curr.getFiles().clone();
        ArrayList<String> filesToAdd
                = new ArrayList<>(stage.getStagedAddFiles().keySet());
        for (String fileName : filesToAdd) {
            newFiles.put(fileName, stage.getStagedAddFiles().get(fileName));
        }

        String newParent = getCurrentCommit().calcHash();
        Commits newC = new Commits(message, newParent, newFiles);
        String newHash = newC.calcHash();
        File newB = Utils.join(BRANCHES_FOLDER, head + ".txt");
        Utils.writeContents(newB, newHash);
        File newCFile = Utils.join(COMMITS_FOLDER, newHash + ".txt");
        Utils.writeObject(newCFile, newC);
        stage.getStagedAddFiles().clear();
        stage.getStagedRemoveFiles().clear();
        File newStage = Utils.join(STAGING_FOLDER, "stage.txt");
        Utils.writeObject(newStage, stage);
    }

    public Commits getCurrentCommit() {
        File headFile = Utils.join(BRANCHES_FOLDER, head + ".txt");
        String headHash = Utils.readContentsAsString(headFile);
        File currFile = Utils.join(COMMITS_FOLDER, headHash + ".txt");
        Commits currentCommit = Utils.readObject(currFile, Commits.class);
        return currentCommit;
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * (see the description of the commit command).
     *
     * @param args Array in format: {'add', file name}
     */
    public void addFile(String[] args) {
        validateNumArgs("add", args, 2);
        String fileName = args[1];
        File toAdd = new File(fileName);
        if (toAdd.exists()) {
            byte[] blob = Utils.readContents(toAdd);
            String blobHash = Utils.sha1(blob);
            Commits currCommit = getCurrentCommit();
            if (currCommit.getFiles().get(fileName) != null
                    && currCommit.getFiles().get(fileName).equals(blobHash)) {
                if (stage.getStagedRemoveFiles().contains(fileName)) {
                    stage.getStagedRemoveFiles().remove(fileName);
                    File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
                    Utils.writeObject(stageFile, stage);
                }
                return;
            }
            if (stage.getStagedRemoveFiles().contains(fileName)) {
                stage.getStagedRemoveFiles().remove(fileName);
            }
            File blobFile = Utils.join(BLOBS_FOLDER, blobHash + ".txt");
            Utils.writeContents(blobFile, blob);

            stage.addToStaging(fileName, blobHash);
            File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
            Utils.writeObject(stageFile, stage);
        } else {
            System.out.print("File does not exist.");
            System.exit(0);
        }
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it
     * for removal and remove the file from the working
     * directory if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     *
     * @param args arguments
     */
    public void removeFile(String[] args) {
        validateNumArgs("rm", args, 2);
        String fileName = args[1];
        boolean isStaged = stage.getStagedAddFiles().
                containsKey(fileName);
        Commits curr = getCurrentCommit();
        boolean isTracked = false;
        ArrayList<String> currCFiles
                = new ArrayList<>(curr.getFiles().keySet());
        for (String oneFileName : currCFiles) {
            if (oneFileName.equals(fileName)) {
                isTracked = true;
            }
        }
        if (isTracked) {
            Utils.restrictedDelete(fileName);
            stage.removeFromStaging(fileName);
            if (isStaged) {
                stage.getStagedAddFiles().remove(fileName);
            }
            File updatedStage = Utils.join(STAGING_FOLDER, "stage.txt");
            Utils.writeObject(updatedStage, stage);
        } else if (isStaged) {
            stage.getStagedAddFiles().remove(fileName);
            File updateStage = Utils.join(STAGING_FOLDER, "stage.txt");
            Utils.writeObject(updateStage, stage);
        } else {
            System.out.print("No reason to remove the file.");
            System.exit(0);
        }
    }


    /**
     * Displays information about each commit starting
     * from the head commit backwards until the initial commit.
     *
     * @param args arguments
     */
    public void showLog(String[] args) {
        validateNumArgs("log", args, 1);
        Commits currentCom = getCurrentCommit();
        while (currentCom != null) {
            System.out.println("===");
            System.out.println("commit " + currentCom.calcHash());
            System.out.println("Date: " + currentCom.getDatetime());
            System.out.println(currentCom.getMessage());
            System.out.println();
            if (currentCom.getParent() != null) {
                File nextCommit = Utils.join(COMMITS_FOLDER,
                        currentCom.getParent() + ".txt");
                currentCom = Utils.readObject(nextCommit, Commits.class);
            } else {
                break;
            }
        }
    }

    /**
     * Displays information about each commit ever made.
     *
     * @param args arguments
     */
    public void showGlobalLog(String[] args) {
        validateNumArgs("global-log", args, 1);
        for (File fileName : COMMITS_FOLDER.listFiles()) {
            Commits currCommit = Utils.readObject(fileName, Commits.class);
            System.out.println("===");
            System.out.println("commit " + currCommit.calcHash());
            System.out.println("Date: " + currCommit.getDatetime());
            System.out.println(currCommit.getMessage());
            System.out.println();
        }
    }

    /**
     * Finds all commits with the given commit message,
     * then prints their IDs one per line.
     *
     * @param args arguments
     */
    public void findID(String[] args) {
        validateNumArgs("find", args, 2);
        String commitMessage = args[1];
        File commitFolder = Utils.join(cwd, ".gitlet/commits");
        String[] allCommits = commitFolder.list();
        boolean found = false;
        for (String fileName : allCommits) {
            File oneFile = Utils.join(COMMITS_FOLDER, fileName);
            Commits currFile = Utils.readObject(oneFile, Commits.class);
            if (currFile.getMessage().equals(commitMessage)) {
                System.out.println(currFile.calcHash());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /**
     * Displays branches that currently exist,
     * and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     *
     * @param args arguments
     */
    public void displayStatus(String[] args) {
        validateNumArgs("status", args, 1);
        if (!GIT.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        List<String> existingBranches = new ArrayList<String>();
        File[] allBranches = BRANCHES_FOLDER.listFiles();
        for (File file : allBranches) {
            existingBranches.add(file.getName().substring(0,
                    file.getName().length() - 4));
        }
        existingBranches.remove("HEAD");
        existingBranches.remove(head);
        existingBranches.add("*" + head);
        Collections.sort(existingBranches);
        List<String> stagedFiles = new ArrayList<String>();
        for (Map.Entry<String, String> entry
                : stage.getStagedAddFiles().entrySet()) {
            stagedFiles.add(entry.getKey());
        }
        Collections.sort(stagedFiles);
        List<String> remFiles = new ArrayList<String>();
        for (String file : stage.getStagedRemoveFiles()) {
            remFiles.add(file);
        }
        Collections.sort(remFiles);

        System.out.println("=== Branches ===");
        for (String branch : existingBranches) {
            System.out.println(branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String staged : stagedFiles) {
            System.out.println(staged);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String removed : remFiles) {
            System.out.println(removed);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }

    /**
     * Takes the file in the head commit of the current branch,
     * and puts it in the working directory.
     *
     * @param args arguments
     */
    public void checkoutHead(String[] args) {
        validateNumArgs("checkout", args, 3);
        String fileName = args[2];
        Commits headCommit = getCurrentCommit();
        TreeMap<String, String> commitMap = headCommit.getFiles();
        if (!commitMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File checkDelete = new File(fileName);
        if ((checkDelete).exists()) {
            Utils.restrictedDelete(checkDelete);
        }
        File newBlob = Utils.join(BLOBS_FOLDER,
                headCommit.getFiles().get(fileName) + ".txt");
        byte[] storeRFile = Utils.readContents(newBlob);
        File newFile = new File(fileName);
        Utils.writeContents(newFile, storeRFile);
    }


    /**
     * Takes the file in the commit with the given id,
     * and puts it in the working directory.
     *
     * @param args arguments
     */
    public void checkoutGiven(String[] args) throws IOException {
        validateNumArgs("checkout", args, 4);
        String commitID = args[1];
        if (!args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String fileName = args[3];
        String[] allCommits = COMMITS_FOLDER.list();
        for (int i = 0; i < allCommits.length; i++) {
            if (allCommits[i].contains(commitID)) {
                commitID = allCommits[i];
                commitID = commitID.substring(0, commitID.length() - 4);
                break;
            }
        }

        File temp = Utils.join(COMMITS_FOLDER, commitID + ".txt");
        if (!temp.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commits currCommit = Utils.readObject(temp, Commits.class);
        if (currCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else if (!currCommit.getFiles().containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            File checkDelete = new File(fileName);
            if ((checkDelete).exists()) {
                Utils.restrictedDelete(checkDelete);
            }
            File newFile = new File(fileName);
            File newBlob = Utils.join(BLOBS_FOLDER,
                    currCommit.getFiles().get(fileName) + ".txt");
            byte[] contentBlob = Utils.readContents(newBlob);
            Utils.writeContents(newFile, contentBlob);
        }
    }

    /**
     * Takes all the files in the head commit of the branch with
     * the given name, and puts it in the working directory.
     *
     * @param args arguments
     */
    public void checkoutAll(String[] args) throws IOException {
        validateNumArgs("checkout", args, 2);
        String branchName = args[1];
        File givenBranch = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
        if (!givenBranch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        File currentBranch = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        String currentBranchName = Utils.readContentsAsString(currentBranch);
        if (branchName.equals(currentBranchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        String newCommitID = Utils.readContentsAsString(givenBranch);
        File newCommitFile = Utils.join(COMMITS_FOLDER, newCommitID + ".txt");
        Commits newCommit = Utils.readObject(newCommitFile, Commits.class);
        Commits currCommit = getCurrentCommit();
        ArrayList<File> fileList = new ArrayList<>();
        for (File f : cwd.listFiles()) {
            if (f.getName().endsWith(".txt")) {
                fileList.add(f);
            }
        }
        Commits commitToCheckout = newCommit;
        for (File f : fileList) {
            if (!currCommit.getFiles().containsKey(f.getName())
                    && commitToCheckout.getFiles().containsKey(f.getName())) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        ArrayList<String> fileNames
                = new ArrayList<>(commitToCheckout.getFiles().keySet());
        for (File f : fileList) {
            if (!commitToCheckout.getFiles().containsKey(f.getName())
                    && currCommit.getFiles().containsKey(f.getName())) {
                Utils.restrictedDelete(f);
            }
        }
        for (String f : fileNames) {
            File newFile = new File(f);
            String blobHash = commitToCheckout.getFiles().get(f);
            File blobFile = Utils.join(BLOBS_FOLDER, blobHash + ".txt");
            byte[] blobBytes = Utils.readContents(blobFile);
            Utils.writeContents(newFile, blobBytes);
        }
        stage.clear();
        File stageFile = Utils.join(STAGING_FOLDER, "stage.txt");
        Utils.writeObject(stageFile, stage);
        File branchFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        Utils.writeContents(branchFile, branchName);
    }

    /**
     * Creates a new branch with the given name and points it to the head node.
     *
     * @param args arguments
     */
    public void createBranch(String[] args) {
        validateNumArgs("branch", args, 2);
        String branchName = args[1];
        File branchFile = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
        if (branchFile.exists()) {
            System.out.print("A branch with that name already exists.");
            System.exit(0);
        }
        File headBranch = Utils.join(BRANCHES_FOLDER, head + ".txt");
        String headSha1 = Utils.readContentsAsString(headBranch);
        Utils.writeContents(branchFile, headSha1);
    }

    /**
     * Deletes the branch with the given name.
     *
     * @param args arguments
     */
    public void removeBranch(String[] args) {
        validateNumArgs("rm-branch", args, 2);
        String branchName = args[1];
        File currBranch = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        String currBranchName = Utils.readContentsAsString(currBranch);
        if (branchName.equals(currBranchName)) {
            System.out.print("Cannot remove the current branch.");
            System.exit(0);
        }
        File branchFile = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
        if (!branchFile.delete()) {
            System.out.print("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    /**
     * Checks out all files in the given commit and removes
     * tracked files that are not in said commit.
     * It also moves the current branch's head to said commit node.
     *
     * @param args arguments
     */
    public void resetBranch(String[] args) {
        validateNumArgs("reset", args, 2);
        String commitID = args[1];
        File checkoutCommitFile = Utils.join(COMMITS_FOLDER, commitID + ".txt");
        Commits checkoutCommit = Utils.readObject
                (checkoutCommitFile, Commits.class);
        if (checkoutCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        ArrayList<File> fileList = new ArrayList<>();
        for (File f : cwd.listFiles()) {
            if (f.getName().endsWith(".txt")) {
                fileList.add(f);
            }
        }
        Commits currCommit = getCurrentCommit();
        for (File f : fileList) {
            if (!currCommit.getFiles().containsKey(f.getName())
                    && checkoutCommit.getFiles().containsKey(f.getName())) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        ArrayList<String> fileNames
                = new ArrayList<>(checkoutCommit.getFiles().keySet());
        for (File f : fileList) {
            if (!checkoutCommit.getFiles().containsKey(f.getName())
                    && currCommit.getFiles().containsKey(f.getName())) {
                Utils.restrictedDelete(f);
            }
        }
        for (String f : fileNames) {
            String blobHash = checkoutCommit.getFiles().get(f);
            File newBlobFile = Utils.join(BLOBS_FOLDER, blobHash + ".txt");
            byte[] blobBytes = Utils.readContents(newBlobFile);
            Utils.writeContents(new File(f), blobBytes);
        }
        stage.clear();
        File resetStage = Utils.join(STAGING_FOLDER, "stage.txt");
        Utils.writeObject(resetStage, stage);
        File resetBranch = Utils.join(BRANCHES_FOLDER, head + ".txt");
        Utils.writeContents(resetBranch, commitID);
    }

    /**
     * Bad merge cases.
     * @param bName branch to merge
     * @return bad case or not
     */
    public boolean forbiddenMerge(String bName) throws IOException {
        boolean toReturn = false;
        File mergeBFile = Utils.join(BRANCHES_FOLDER, bName + ".txt");
        mergeBFile.createNewFile();
        File currBFile = Utils.join(BRANCHES_FOLDER, "HEAD.txt");
        String currBName = Utils.readContentsAsString(currBFile);
        if (!stage.getStagedAddFiles().isEmpty()
                || !stage.getStagedRemoveFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            toReturn = true;
        } else if (!mergeBFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            toReturn = true;
        } else if (bName.equals(currBName)) {
            System.out.println("Cannot merge a branch with itself.");
            toReturn = true;
        }
        return toReturn;
    }


    /**
     * Merges files from the given branch and the current branch.
     * @param args contain branch name to merge
     */
    public void mergeFiles(String[] args) throws IOException {
        validateNumArgs("merge", args, 2);
        String branchName = args[1];
        if (forbiddenMerge(branchName)) {
            System.exit(0);
        }
        ArrayList<File> fileList = new ArrayList<>();
        for (File f : cwd.listFiles()) {
            if (f.getName().endsWith(".txt")) {
                fileList.add(f);
            }
        }
        File givenBranch = Utils.join(BRANCHES_FOLDER, branchName + ".txt");
        String givenHeadHash = Utils.readContentsAsString(givenBranch);
        Commits cCom = getCurrentCommit();
        File bComFile = Utils.join(COMMITS_FOLDER, givenHeadHash);
        if (!bComFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commits bCom = Utils.readObject(bComFile, Commits.class);
        for (File f : fileList) {
            if (!cCom.getFiles().containsKey(f.getName())
                    && bCom.getFiles().containsKey(f.getName())) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                return;
            }
        }
    }

}






