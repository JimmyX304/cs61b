package gitlet;

import java.io.IOException;

import static gitlet.Repository.add;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Hanyuan Xu
 */
public class Main {

    private static boolean setUpRepo = false;

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
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command

                if (length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }

                if (!setUpRepo) {
                    System.out.println("A Gitlet version-control system already exists in the current directory.");
                    break;
                }

                Repository.setUpPersistence();
                setUpRepo = true;

                break;
            case "add":
                // TODO: handle the `add [filename]` command

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }

                String fileName = args[1];
                if (!add(fileName)) {
                    System.out.println("File does not exist.");
                    break;
                }
                break;
            case "commit":

                if (length == 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }


                break;
            case "rm":

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }


                break;
            case "log":

                if (length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }


                break;
            case "global-log":

                if (length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }


                break;
            case "find":

                if (length == 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            case "status":

                if (length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            case "checkout":


                break;
            case "branch":

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            case "rm-branch":

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            case "reset":

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            case "merge":

                if (length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }



                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
