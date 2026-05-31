package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *
 *  @author Hanyuan Xu
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            int length = args.length;
            if (length == 0) {
                System.out.println("Please enter a command.");
                return;
            }

            String firstArg = args[0];
            String fileName;
            String branchName;
            switch (firstArg) {
                case "init":
                    validateNumArgs(length, 1);

                    if (Repository.fileExists(".gitlet")) {
                        System.out.println("A Gitlet version-control system already exists in the current directory.");
                        break;
                    }

                    Repository.init();

                    break;
                case "add":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    fileName = args[1];
                    if (!Repository.addToStage(fileName)) {
                        System.out.println("File does not exist.");
                        break;
                    }

                    break;
                case "commit":
                    if (length == 1 || args[1].isBlank()) {
                        System.out.println("Please enter a commit message.");
                    } else if (length == 2) {
                        validateRepoExists();
                        String msg = args[1];
                        Repository.makeCommit(msg);
                    } else {
                        validateNumArgs(length, 2);
                    }
                    break;
                case "rm":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    fileName = args[1];
                    Repository.removeFile(fileName);

                    break;
                case "log":
                    validateNumArgs(length, 1);
                    validateRepoExists();

                    Repository.outputLog();

                    break;
                case "global-log":
                    validateNumArgs(length, 1);
                    validateRepoExists();

                    Repository.outputGlobalLog();

                    break;
                case "find":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    String commitMessage = args[1];
                    Repository.getCommitsWithMessage(commitMessage);

                    break;
                case "status":
                    validateNumArgs(length, 1);
                    validateRepoExists();

                    Repository.outputStatus();

                    break;
                case "checkout":
                    if (length == 2) {
                        // operation 3

                        validateRepoExists();
                        branchName = args[1];
                        Repository.checkoutBranch(branchName);

                    } else if (length == 3) {
                        // operation 1

                        if (args[1].equals("--")) {
                            validateRepoExists();
                            fileName = args[2];
                            Repository.checkout(Repository.getHead(), fileName);
                        } else {
                            validateNumArgs(length, 100);
                        }
                    } else if (length == 4) {
                        // operation 2

                        if (args[2].equals("--")) {
                            validateRepoExists();
                            fileName = args[3];
                            Repository.checkout(args[1], fileName);
                        } else {
                            validateNumArgs(length, 100);
                        }
                    } else {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    break;
                case "branch":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    branchName = args[1];
                    Repository.addBranch(branchName);

                    break;
                case "rm-branch":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    branchName = args[1];
                    Repository.removeBranch(branchName);

                    break;
                case "reset":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    Repository.checkoutCommit(args[1]);

                    break;
                case "merge":
                    validateNumArgs(length, 2);
                    validateRepoExists();

                    branchName = args[1];
                    Repository.mergeBranch(branchName);

                    break;
                default:
                    System.out.println("No command with that name exists.");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Validates the number of arguments. */
    public static void validateNumArgs(int l, int correct) {
        if (l != correct) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Validates the repository exists. */
    public static void validateRepoExists() {
        if (!Repository.fileExists(".gitlet")) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
