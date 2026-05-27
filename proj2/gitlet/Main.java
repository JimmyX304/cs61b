package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Repository.addToStage;
import static gitlet.Repository.fileExists;

import static gitlet.Utils.*;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Hanyuan Xu
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        int length = args.length;
        if (length == 0) {
            System.out.println("Please enter a command.");
            return;
        }

        String firstArg = args[0];
        String fileName;

        // TODO: fill in cases
        switch(firstArg) {
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

                fileName = args[1];
                if (!Repository.addToStage(fileName)) {
                    System.out.println("File does not exist.");
                    break;
                }

                break;
            case "commit":
                if (length == 1) {
                    System.out.println("Please enter a commit message.");
                } else if (length == 2) {
                    String msg = args[1];
                    Repository.makeCommit(msg);
                } else {
                    validateNumArgs(length, 2);
                }
                break;
            case "rm":
                validateNumArgs(length, 2);

                break;
            case "log":
                validateNumArgs(length, 1);

                Repository.outputLog();

                break;
            case "global-log":
                validateNumArgs(length, 1);

                break;
            case "find":
                validateNumArgs(length, 2);

                break;
            case "status":
                validateNumArgs(length, 1);

                break;
            case "checkout":
                if (length == 2) {
                    // operation 3

                } else if (length == 3) {
                    // operation 1

                    fileName = args[2];
                    Repository.checkout(Repository.getHead(), fileName);

                } else if (length == 4) {
                    // operation 2

                    fileName = args[3];
                    Repository.checkout(args[1], fileName);

                } else {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                validateNumArgs(length, 2);


                break;
            case "rm-branch":
                validateNumArgs(length, 2);


                break;
            case "reset":
                validateNumArgs(length, 2);



                break;
            case "merge":
                validateNumArgs(length, 2);



                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    /** Validates the number of arguments. */
    public static void validateNumArgs(int l, int correct) {
        if (l != correct) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
