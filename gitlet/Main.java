package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sandya Wijaya
 */
public class Main {
    /**
     * Make folders.
     */
    static final File GIT = new File(".gitlet");
    /**
     * Make BLOBS FOLDER.
     */
    static final File BLOBS_FOLDER = Utils.join(GIT, "blobs");
    /**
     * Make COMMITS FOLDER.
     */
    static final File COMMITS_FOLDER = Utils.join(GIT, "commits");
    /**
     * Make STAGING FOLDER.
     */
    static final File STAGING_FOLDER = Utils.join(GIT, "staging");
    /**
     * Make BRANCHES FOLDER.
     */
    static final File BRANCHES_FOLDER = Utils.join(GIT, "branches");
    /**
     * Make folders.
     */
    static final File BRANCHES_TREE = Utils.join
            (BRANCHES_FOLDER, "branchesTree");

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        Repo3 repo = new Repo3();
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        switch (args[0]) {
        case "init":
            repo.createGitlet(args);
            break;
        case "add":
            repo.addFile(args);
            break;
        case "commit":
            repo.createCommit(args);
            break;
        case "rm":
            repo.removeFile(args);
            break;
        case "log":
            repo.showLog(args);
            break;
        case "global-log":
            repo.showGlobalLog(args);
            break;
        case "find":
            repo.findID(args);
            break;
        case "status":
            repo.displayStatus(args);
            break;
        case "branch":
            repo.createBranch(args);
            break;
        case "rm-branch":
            repo.removeBranch(args);
            break;
        case "reset":
            repo.resetBranch(args);
            break;
        case "checkout":
            if (args.length == 4) {
                repo.checkoutGiven(args);
                break;
            } else if (args.length == 3) {
                repo.checkoutHead(args);
                break;
            } else if (args.length == 2) {
                repo.checkoutAll(args);
                break;
            }
        default:
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        return;
    }

    /**
     * Prints out MESSAGE and exits with error code -1.
     * Note:
     *     The functionality for erroring/exit codes is different within Gitlet
     *     so DO NOT use this as a reference.
     *     Refer to the spec for more information.
     * @param message message to print
     */
    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(-1);
    }

    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
