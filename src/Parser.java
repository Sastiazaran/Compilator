import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.*;

public class Parser {

    private static Vector<Token> tokens;
    public static ErrorController errorController = new ErrorController("PARSER");
    private static int currentToken = 0;
    private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    private static DefaultMutableTreeNode current_level = root;

    public Parser(Vector<Token> tokens) {
        this.tokens = tokens;
        RULE_PROGRAM();
    }

    //print())
    
    
    private static void error1(int type) {
        if (isCurrentTokenValid()) {
            String msg = "\nError msg: Line: " + (tokens.get(currentToken).getLine() - 1) + ": expected";
            switch (type) {
                case 1:
                    msg += "{";
                    break;
                case 2:
                    msg += "}";
                    break;
                case 3:
                    msg += ";";
                    break;
                case 4:
                    msg += ")";
                    break;
                case 5:
                    msg += "vartype";
                    break;
                case 6:
                    msg += "validbody";
                    break;
                case 7:
                    msg += "=";
                    break;
                case 8:
                    msg += "ID";
                    break;
                case 9:
                    msg += "(";
                    break;
                case 10:
                    msg += "while";
                    break;
                case 11:
                    msg += "break";
                    break;
                case 12:
                    msg += "valid data val";
                    break;
                case 13:
                    msg += ":";
                    break;
            }
            errorController.storeError(msg);
            System.err.println(msg);
        }
    }

    
    //run
    public DefaultMutableTreeNode parse(){
        currentToken = 0;
        clearTree(root);
        current_level = root;
        RULE_PROGRAM();

        return root;   
    }

    private static void clearTree(DefaultMutableTreeNode node){
        node.removeAllChildren();;
    }

    private static void addNote(String name){
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        current_level.add(newNode);
        current_level = newNode;
    }
    

    private static boolean isCurrentTokenValid() {
        return currentToken >= 0 && currentToken < tokens.size();
    }

    public static void RULE_PROGRAM() {
        //NODE
        addNote("pabuaghmandou");

        if (tokens.get(currentToken).getWord().equals("{")) {
            currentToken++;
        } else {
            error();
        }

        RULE_BODY();

        if (tokens.get(currentToken).getWord().equals("{")) {
            currentToken++;
        } else {
            error();
        }
    }

    

    public static void RULE_BODY() {
        while (!tokens.get(currentToken).getWord().equals("}")) {
            /*
             * RULE_EXPRESSION();
             * 
             * if (tokens.get(currentToken).getWord().equals(";")) {
             * currentToken++;
             * } else {
             * error();
             * }
             */
            if (tokens.get(currentToken).getToken().equals("ID")) {
                RULE_ASSIGNMENT();
                if (tokens.get(currentToken).getWord().equals(";")) {
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else if (tokens.get(currentToken).getToken().equals("Integer")
                    | tokens.get(currentToken).getToken().equals("Float")
                    | tokens.get(currentToken).getToken().equals("Octal")
                    | tokens.get(currentToken).getToken().equals("Hexadecimal")) {
                RULE_VARIABLE();
                if (tokens.get(currentToken).getWord().equals(";")) {
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else if (tokens.get(currentToken).getWord().equals("while")) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getWord().equals("if")) {
                RULE_IF();
            } else if (tokens.get(currentToken).getWord().equals("return")) {
                RULE_RETURN();
                if (tokens.get(currentToken).getWord().equals(";")) {
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else {
                System.out.println("Error 4"); // Add error to console
            }
        }
    }

    public static void RULE_ASSIGNMENT() {
        if (tokens.get(currentToken).getToken().equals("ID")) {
            currentToken++;
        } else {
            System.out.println("Error 3"); // Add error to console
        }
        if (tokens.get(currentToken).getWord().equals("=")) {
            currentToken++;
        } else {
            System.out.println("Error 3"); // Add error to console
        }
        RULE_EXPRESSION();
    }

    public static void RULE_EXPRESSION() {
        RULE_X();

        while (tokens.get(currentToken).getWord().equals("|")) {
            currentToken++;
            RULE_X();
        }
    }

    public static void RULE_X() {
        RULE_Y();

        while (tokens.get(currentToken).getWord().equals("&")) {
            currentToken++;
            RULE_Y();
        }
    }

    public static void RULE_Y() {
        if (tokens.get(currentToken).getWord().equals("!")) {
            currentToken++;
        }

        RULE_R();
    }

    public static void RULE_R() {
        RULE_E();

        while (tokens.get(currentToken).getWord().equals("<") | tokens.get(currentToken).getWord().equals(">")
                | tokens.get(currentToken).getWord().equals("==") | tokens.get(currentToken).getWord().equals("!=")) {
            currentToken++;
            RULE_E();
        }
    }

    public static void RULE_E() {
        RULE_A();

        while (tokens.get(currentToken).getWord().equals("-") | tokens.get(currentToken).getWord().equals("+")) {
            currentToken++;
            RULE_A();
        }
    }

    public static void RULE_A() {
        RULE_B();

        while (tokens.get(currentToken).getWord().equals("/") | tokens.get(currentToken).getWord().equals("*")) {
            currentToken++;
            RULE_B();
        }
    }

    public static void RULE_B() {
        if (tokens.get(currentToken).getWord().equals("-")) {
            currentToken++;
        }

        RULE_C();
    }

    public static void RULE_C() {
        if (tokens.get(currentToken).getToken().equals("Integer")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("Octal")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("Hexadecimal")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("BINARY")) {
            currentToken++;
        } else if (tokens.get(currentToken).getWord().equals("true")) {
            currentToken++;
        } else if (tokens.get(currentToken).getWord().equals("false")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("String")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("Float")) {
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("ID")) {
            currentToken++;
        } else if (tokens.get(currentToken).getWord().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getWord().equals(")")) {
                currentToken++;
            } else {
                error();
            }
        } else {
            error();
        }
    }

    public static void RULE_VARIABLE() {
        if (tokens.get(currentToken).getWord().equals("int") | tokens.get(currentToken).getWord().equals("float")
                | tokens.get(currentToken).getWord().equals("boolean")
                | tokens.get(currentToken).getWord().equals("string")
                | tokens.get(currentToken).getWord().equals("void")) {
            currentToken++;
        } else {
            System.out.println("Error 3"); // Add error to console
        }
        if (tokens.get(currentToken).getToken().equals("ID")) {
            currentToken++;
        } else {
            System.out.println("Error 3"); // Add error to console
        }
    }

    public static void RULE_WHILE() {
        if (tokens.get(currentToken).getWord().equals("while")) {
            currentToken++;
        } else {
            error();
        }
        if (tokens.get(currentToken).getWord().equals("(")) {
            currentToken++;
        } else {
            error();
        }

        RULE_EXPRESSION();

        if (tokens.get(currentToken).getWord().equals(")")) {
            currentToken++;
        } else {
            error();
        }

        RULE_PROGRAM();

    }

    public static void RULE_IF() {
        if (tokens.get(currentToken).getWord().equals("if")) {
            currentToken++;
        } else {
            error();
        }
        if (tokens.get(currentToken).getWord().equals("(")) {
            currentToken++;
        } else {
            error();
        }

        RULE_EXPRESSION();

        if (tokens.get(currentToken).getWord().equals(")")) {
            currentToken++;
        } else {
            error();
        }

        RULE_PROGRAM();

        if (tokens.get(currentToken).getWord().equals("else")) {
            currentToken++;

            RULE_PROGRAM();

        }
    }

    public static void RULE_RETURN() {
        if (tokens.get(currentToken).getWord().equals("return")) {
            currentToken++;
        } else {
            System.out.println("Error 3"); // Add error to console
        }
    }

    public static void RULE_PRINT() {
        if (tokens.get(currentToken).getWord().equals("print")) {
            currentToken++;

            if (tokens.get(currentToken).getWord().equals("(")) {
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")")) {
                    currentToken++;
                } else {
                    error1(4);
                }
            } else {
                error1(9);
            }
        }
    }

    public static void error() {
        System.out.println("Error in " + tokens.get(currentToken).getLine());
    }
}
