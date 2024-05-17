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
        //RULE_PROGRAM();
    }

    //print())
    
    
    private static void error(int type) {
        if (isCurrentTokenValid()) {
            String msg = "\nError msg: Line: " + (tokens.get(currentToken).getLine() - 1) + " expected ";
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
    private static boolean isVariable(String token) {
        if (token.equals("int") || token.equals("float") || token.equals("string") || token.equals("bool"))
            return true;
        return false;
    }

    private static boolean isVarKey(String token) {
        if (token.equals("Integer") || token.equals("Float") || token.equals("String") || token.equals("Hexadecimal") || token.equals("Octal") || token.equals("BINARY")) {
            return true;
        }
        return false;
    }

    /////////////////////////////////////
    /////////////   RULES   /////////////
    /////////////////////////////////////

    public static void RULE_PROGRAM() {
        //NODE
        addNote("PROGRAM");

        if (tokens.get(currentToken).getWord().equals("{") && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else {
            error(1);
        }

        RULE_BODY();

        if (tokens.get(currentToken).getWord().equals("}")) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else {
            error(2);
        }
    }



    public static void RULE_BODY() {
        //Añade body al árbol
        addNote("BODY");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        while (!tokens.get(currentToken).getWord().equals("}") && isCurrentTokenValid()) {
            if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                RULE_ASSIGNMENT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                    error(3);
                }
            } else if (isVariable(tokens.get(currentToken).getWord()) && tokens.get(currentToken).getToken().equals("KEYWORD") && isCurrentTokenValid()) {
                RULE_VARIABLE();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                    error(3);
                }
            } else if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
                RULE_IF();
            } else if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
                RULE_RETURN();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                    error(3);
                }
            } else if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
                RULE_PRINT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                    error(3);
                }
            }else {
                System.out.println("Error 4"); // Add error to console
                error(6);
            }
        }
        //devuelve el nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_ASSIGNMENT() {
        //Añade assignment al árbol
        addNote("RULE_ASSIGNMENT");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
            //Añadir id(la palabra) al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid()) {
                //Añade = al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
            } else {
                System.out.println("Error 7"); // Add error to console
                error(7);
            }
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_EXPRESSION() {
        //Añade expression al árbol
        addNote("RULE_EXPRESSION");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        RULE_X();

        while (tokens.get(currentToken).getWord().equals("|") && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_X();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_X() {
        //Añade X al árbol
        addNote("RULE_X");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        RULE_Y();

        while (tokens.get(currentToken).getWord().equals("&") && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_Y();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_Y() {
        //A{ade Y al árbol
        addNote("RULE_Y");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("!") && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        }

        RULE_R();
        //Devuelve nivel al padre
        current_level = current_level_at_tree;

    }

    public static void RULE_R() {
        //Añade R al árbol
        addNote("RULE_R");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        RULE_E();

        while (tokens.get(currentToken).getWord().equals("<") | tokens.get(currentToken).getWord().equals(">")
                | tokens.get(currentToken).getWord().equals("==") | tokens.get(currentToken).getWord().equals("!=")
                | tokens.get(currentToken).getWord().equals("<=") | tokens.get(currentToken).getWord().equals(">=")) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_E();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_E() {
        //Añade E al árbol
        addNote("RULE_E");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();


        RULE_A();

        while ((tokens.get(currentToken).getWord().equals("-") | tokens.get(currentToken).getWord().equals("+")) && isCurrentTokenValid()) {
            //Añadir current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_A();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_A() {
        //Añade A al árbol
        addNote("RULE_A");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        RULE_B();

        while ((tokens.get(currentToken).getWord().equals("/") | tokens.get(currentToken).getWord().equals("*")) && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_B();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_B() {
        //A{ade B al árbol
        addNote("RULE_B");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("-") && isCurrentTokenValid()) {
            //Añade - al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        }

        RULE_C();
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_C() {
        //Añade C al árbol
        addNote("RULE_C");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (isVarKey(tokens.get(currentToken).getToken()) && isCurrentTokenValid()) {
            //Añadir current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
            //Añadir current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("KEYWORD") && isCurrentTokenValid()) {
            //Añadir current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                //Añade current token al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
            } else {
                error(4);
            }
        } else {
            error(5);
            currentToken++;
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_VARIABLE() {
        //Añade Variable al árbol
        addNote("RULE_VARIABLE");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if ((isVariable(tokens.get(currentToken).getWord()) && tokens.get(currentToken).getToken().equals("KEYWORD")) && isCurrentTokenValid()) {
            //Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                //Añadir current token al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
            } else {
                error(8);
            }
            if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid()) {
                //Añade current token al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
            }
        }
        //Devuelve nivel al árbol
        current_level = current_level_at_tree;
    }

    public static void RULE_WHILE() {
        //Añade While al árbol
        addNote("RULE_WHILE");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
            //Añade while al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    //Añade ) al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                    RULE_PROGRAM();
                }
                else {
                    error(4);
                }
            } else {
                error(9);
            }
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_IF() {
        //Añade IF al árbol
        addNote("RULE_IF");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
            //Añade if al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")")) {
                    //Añade ) al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                    RULE_PROGRAM();
                    if (tokens.get(currentToken).getWord().equals("else")) {
                        //Añade else al árbol
                        addNote(tokens.get(currentToken).getWord(), false);
                        currentToken++;
                        RULE_PROGRAM();
                    }
                } else {
                    error(4);
                }
            } else {
                error(9);
            }
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_RETURN() {
        //Añade Return al árbol
        addNote("RULE_RETURN");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
            //Añade return al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_EXPRESSION();
        }
        //Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_PRINT() {
        //Añade Print al árbol
        addNote("RULE_PRINT");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
            //Añade print al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;

            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    //Añade ) al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(4);
                }
            } else {
                error(9);
            }
        }
        current_level = current_level_at_tree;
    }

    // public static void error() {
    //     System.out.println("Error in " + tokens.get(currentToken).getLine());
    // }
}