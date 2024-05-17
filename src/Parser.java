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
        addNote("pabuaghmandou");

        if (tokens.get(currentToken).getWord().equals("{") && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
        } else {
            error();
        }

        RULE_BODY();

        if (tokens.get(currentToken).getWord().equals("}")) {
            //Añade current token al árbol
            currentToken++;
        } else {
            error();
        }
    }

    

    public static void RULE_BODY() {
        //Añade body al árbol

        while (!tokens.get(currentToken).getWord().equals("}") && isCurrentTokenValid()) {
            if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                RULE_ASSIGNMENT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else if (isVariable(tokens.get(currentToken).getWord()) && tokens.get(currentToken).getToken().equals("KEYWORD") && isCurrentTokenValid()) {
                RULE_VARIABLE();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
                RULE_IF();
            } else if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
                RULE_RETURN();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            } else if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
                RULE_PRINT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    //Añadir ; al árbol
                    currentToken++;
                } else {
                    System.out.println("Error 3"); // Add error to console
                }
            }else {
                System.out.println("Error 4"); // Add error to console
            }
        }
        //devuelve el nivel al padre
    }

    public static void RULE_ASSIGNMENT() {
        //Añade assignment al árbol

        if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
            //Añadir id(la palabra) al árbol
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid()) {
                //Añade = al árbol
                currentToken++;
                RULE_EXPRESSION();
            } else {
                System.out.println("Error 7"); // Add error to console
            }
        }
        //Devuelve nivel al padre
    }

    public static void RULE_EXPRESSION() {
        //Añade expression al árbol

        RULE_X();

        while (tokens.get(currentToken).getWord().equals("|") && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
            RULE_X();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_X() {
        //Añade X al árbol
        RULE_Y();

        while (tokens.get(currentToken).getWord().equals("&") && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
            RULE_Y();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_Y() {
        //A{ade Y al árbol
        if (tokens.get(currentToken).getWord().equals("!") && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
        }

        RULE_R();
        //Devuelve nivel al padre
    }

    public static void RULE_R() {
        //Añade R al árbol
        RULE_E();

        while (tokens.get(currentToken).getWord().equals("<") | tokens.get(currentToken).getWord().equals(">")
                | tokens.get(currentToken).getWord().equals("==") | tokens.get(currentToken).getWord().equals("!=")
                | tokens.get(currentToken).getWord().equals("<=") | tokens.get(currentToken).getWord().equals(">=")) {
            //Añade current token al árbol
            currentToken++;
            RULE_E();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_E() {
        //Añade E al árbol
        RULE_A();

        while ((tokens.get(currentToken).getWord().equals("-") | tokens.get(currentToken).getWord().equals("+")) && isCurrentTokenValid()) {
            //Añadir current token al árbol
            currentToken++;
            RULE_A();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_A() {
        //Añade A al árbol
        RULE_B();

        while ((tokens.get(currentToken).getWord().equals("/") | tokens.get(currentToken).getWord().equals("*")) && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
            RULE_B();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_B() {
        //A{ade B al árbol

        if (tokens.get(currentToken).getWord().equals("-") && isCurrentTokenValid()) {
            //Añade - al árbol
            currentToken++;
        }

        RULE_C();
        //Devuelve nivel al padre
    }

    public static void RULE_C() {
        //Añade C al árbol

        if (isVarKey(tokens.get(currentToken).getToken()) && isCurrentTokenValid()) {
            //Añadir current token al árbol
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
            //Añadir current token al árbol
            currentToken++;
        } else if (tokens.get(currentToken).getToken().equals("KEYWORD") && isCurrentTokenValid()) {
            //Añadir current token al árbol
            currentToken++;
        } else if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                //Añade current token al árbol
                currentToken++;
            } else {
                error();
            }
        } else {
            error();
            currentToken++;
        }
        //Devuelve nivel al padre
    }

    public static void RULE_VARIABLE() {
        //Añade Variable al árbol
        if ((isVariable(tokens.get(currentToken).getWord()) && tokens.get(currentToken).getToken().equals("KEYWORD")) && isCurrentTokenValid()) {
            //Añade current token al árbol
            currentToken++;
            if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                //Añadir current token al árbol
                currentToken++;
            } else {
                error();
            }
            if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid()) {
                //Añade current token al árbol
                currentToken++;
                RULE_EXPRESSION();
            }
        }
        //Devuelve nivel al árbol
    }

    public static void RULE_WHILE() {
        //Añade While al árbol

        if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
            //Añade while al árbol
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    //Añade ) al árbol
                    currentToken++;
                    RULE_PROGRAM();
                }
                else {
                    error();
                }
            } else {
                error();
            }
        }
        //Devuelve nivel al padre
    }

    public static void RULE_IF() {
        //Añade IF al árbol
        if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
            //Añade if al árbol
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")")) {
                    //Añade ) al árbol
                    currentToken++;
                    RULE_PROGRAM();
                    if (tokens.get(currentToken).getWord().equals("else")) {
                        //Añade else al árbol
                        currentToken++;
                        RULE_PROGRAM();
                    }
                } else {
                    error();
                }
            } else {
                error();
            }
        }
        //Devuelve nivel al padre
    }

    public static void RULE_RETURN() {
        //Añade Return al árbol
        if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
            //Añade return al árbol
            currentToken++;
            RULE_EXPRESSION();
        }
        //Devuelve nivel al padre
    }

    public static void RULE_PRINT() {
        //Añade Print al árbol
        if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
            //Añade print al árbol
            currentToken++;

            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                //Añade ( al árbol
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    //Añade ) al árbol
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
