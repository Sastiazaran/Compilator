import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

public class Parser {

    private static Vector<Token> tokens;
    public static ErrorController errorController = new ErrorController("PARSER");
    private static int currentToken = 0;
    private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
    private static DefaultMutableTreeNode current_level = root;

    public Parser(Vector<Token> tokens) {
        this.tokens = tokens;
        // RULE_PROGRAM();
    }

    // print())

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

    // run
    public DefaultMutableTreeNode parse() {
        currentToken = 0;
        clearTree(root);
        // Semantic Analyzer
        SemanticAnalyzer.clearVariables();
        current_level = root;
        RULE_PROGRAM();

        return root;
    }

    private static void clearTree(DefaultMutableTreeNode node) {
        node.removeAllChildren();
    }

    private static void addNote(String name) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        current_level.add(newNode);
        current_level = newNode;
    }

    private static void addNote(String name, boolean changeLevel) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        current_level.add(newNode);
        if (changeLevel) {
            current_level = newNode;
        }

    }

    private static boolean isCurrentTokenValid() {
        return currentToken >= 0 && currentToken < tokens.size();
    }

    private static boolean isVariable(String token) {
        if (token.equals("int") || token.equals("float") || token.equals("string") || token.equals("Boolean"))
            return true;
        return false;
    }

    private static boolean isVarKey(String token) {
        if (token.equals("Integer") || token.equals("Float") || token.equals("String") || token.equals("Hexadecimal")
                || token.equals("Octal") || token.equals("BINARY")) {
            return true;
        }
        return false;
    }

    public static boolean isSameLine() {
        return (tokens.get(currentToken).getLine() == tokens.get(currentToken - 1).getLine());
    }

    /////////////////////////////////////
    ///////////// RULES /////////////
    /////////////////////////////////////

    public static void RULE_PROGRAM() {
        // NODE
        addNote("PROGRAM");

        if (tokens.get(currentToken).getWord().equals("{") && isCurrentTokenValid()) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else {
            error(1);
        }

        RULE_BODY();

        if (tokens.get(currentToken).getWord().equals("}")) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        } else {
            error(2);
        }
    }

    public static void RULE_BODY() {
        // Añade body al árbol
        addNote("BODY");        

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        int currentLine = -1;

        while (!tokens.get(currentToken).getWord().equals("}") && isCurrentTokenValid()) {
            currentLine = tokens.get(currentToken).getLine();

            if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                RULE_ASSIGNMENT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid() && isSameLine()) {
                    // Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(3);
                }
            } else if (isVariable(tokens.get(currentToken).getWord())
                    && tokens.get(currentToken).getToken().equals("KEYWORD") && isCurrentTokenValid()) {
                RULE_VARIABLE();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    // Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(3);
                }
            } else if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
                RULE_IF();
            } else if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
                RULE_RETURN();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    // Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(3);
                }
            } else if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
                RULE_PRINT();
                if (tokens.get(currentToken).getWord().equals(";") && isCurrentTokenValid()) {
                    // Añadir ; al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(3);
                }
            } else {
                System.out.println("Error 4"); // Add error to console
                error(6);
                while (isCurrentTokenValid()
                        && !(((tokens.get(currentToken).getWord().equals("!"))
                                || (tokens.get(currentToken).getWord().equals("-")) ||
                                (tokens.get(currentToken).getToken().equals("Integer"))
                                || (tokens.get(currentToken).getToken().equals("Octal")) ||
                                (tokens.get(currentToken).getToken().equals("BINARY"))
                                || (tokens.get(currentToken).getToken().equals("Hexadecimal")) ||
                                (tokens.get(currentToken).getToken().equals("String"))
                                || (tokens.get(currentToken).getToken().equals("Float")) ||
                                (tokens.get(currentToken).getToken().equals("ID"))
                                || (tokens.get(currentToken).getWord().equals("(")) ||
                                (tokens.get(currentToken).getWord().equals("true"))
                                || (tokens.get(currentToken).getWord().equals("false"))) ||
                                (tokens.get(currentToken).getWord().equals("}")))
                        && currentLine == tokens.get(currentToken).getLine()) {
                    if (tokens.get(currentToken).getToken().equals("ERROR")) {
                        addNote("error (" + tokens.get(currentToken).getWord() + ")", false);
                    }
                    currentToken++;
                }
            }
        }
        // devuelve el nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_ASSIGNMENT() {
        // Añade assignment al árbol
        addNote("RULE_ASSIGNMENT");

        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // String id = "";
        System.out.println("assignment");

        if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
            // Analizador semantico
            // if(!SemanticAnalyzer.CheckVariable(tokens.get(currentToken).getWord())){
            // SemanticAnalyzer.errorController.storeError("\nLine " +
            // (tokens.get(currentToken).getLine() -1)
            // + ": Variable" + tokens.get(currentToken).getWord() + " is not defined");
            // }

            // if (!SemanticAnalyzer.CheckVariable(tokens.get(currentToken).getWord())) {
            // SemanticAnalyzer.errorController.storeError("\nLine " +
            // (tokens.get(currentToken).getLine() - 1)
            // + ": Variable " + tokens.get(currentToken).getWord() + " is not defined");
            // System.out.println("\nLine " + (tokens.get(currentToken).getLine() - 1)
            // + ": Variable " + tokens.get(currentToken).getWord() + " is not defined"); //
            // Agrega la impresión del error
            // }

            // SEMANTIC
            SemanticAnalyzer.pushStack(SemanticAnalyzer.getIdType(tokens.get(currentToken).getToken(),
                    tokens.get(currentToken).getLine()));
            // SEMANTIC

            // Añadir id(la palabra) al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid()) {
                // Añade = al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
            } else {
                // System.out.println("Error 7"); // Add error to console
                error(7);
                while (isCurrentTokenValid() && !((tokens.get(currentToken).getWord().equals("!"))
                        || (tokens.get(currentToken).getWord().equals("-")) ||
                        (tokens.get(currentToken).getToken().equals("Integer"))
                        || (tokens.get(currentToken).getToken().equals("Octal")) ||
                        (tokens.get(currentToken).getToken().equals("BINARY"))
                        || (tokens.get(currentToken).getToken().equals("Hexadecimal")) ||
                        (tokens.get(currentToken).getToken().equals("String"))
                        || (tokens.get(currentToken).getToken().equals("Float")) ||
                        (tokens.get(currentToken).getToken().equals("ID"))
                        || (tokens.get(currentToken).getWord().equals("(")) ||
                        (tokens.get(currentToken).getWord().equals("true"))
                        || (tokens.get(currentToken).getWord().equals("false")) ||
                        (tokens.get(currentToken).getWord().equals(";"))
                        || (tokens.get(currentToken).getWord().equals(")")))) {
                    if (tokens.get(currentToken).getToken().equals("ERROR")) {
                        addNote("error (" + tokens.get(currentToken).getWord() + ")", false);
                    }
                    currentToken++;
                }
            }
            RULE_EXPRESSION();

            // Semantic
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, "=");
            if (!result.equals("OK") && !y.equals("")) {
                SemanticAnalyzer.error(2, tokens.get(currentToken - 1).getLine(), "");
            }
        }
        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_EXPRESSION() {
        // Añade expression al árbol
        addNote("RULE_EXPRESSION");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // //FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");

        RULE_X();

        while (tokens.get(currentToken).getWord().equals("|") && isCurrentTokenValid()) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_X();

            // SEMANTIC
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, "|");
            SemanticAnalyzer.pushStack(result);
            // SEMANTIC

        }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_X() {
        // Añade X al árbol
        addNote("RULE_X");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");

        RULE_Y();

        while (tokens.get(currentToken).getWord().equals("&") && isCurrentTokenValid()) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_Y();

            // SEMANTIC
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, "&");
            SemanticAnalyzer.pushStack(result);
            // SEMANTIC

        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_Y() {
        // Añade Y al árbol
        addNote("RULE_Y");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        // SEMANTIC
        boolean op_used = false;
        // SEMANTIC

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");

        if (tokens.get(currentToken).getWord().equals("!") && isCurrentTokenValid() && isSameLine()) {
            // SEMANTIC
            op_used = true;
            // SEMANTIC

            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);

            currentToken++;
        }

        RULE_R();
        if (op_used) {
            // SEMANTIC
            String x = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, "!");
            SemanticAnalyzer.pushStack(result);
            // SEMANTIC
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;

    }

    public static void RULE_R() {
        // Añade R al árbol
        addNote("RULE_R");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        String operator = "";

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");

        RULE_E();

        while (tokens.get(currentToken).getWord().equals("<") | tokens.get(currentToken).getWord().equals(">")
                | tokens.get(currentToken).getWord().equals("==") | tokens.get(currentToken).getWord().equals("!=")
                | tokens.get(currentToken).getWord().equals("<=") | tokens.get(currentToken).getWord().equals(">=")) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_E();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, operator);
            SemanticAnalyzer.pushStack(result);
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_E() {
        // Añade E al árbol
        addNote("RULE_E");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        String operator = "";

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");
        // follow.add(">");
        // follow.add("<");
        // follow.add("!=");
        // follow.add("==");

        RULE_A();

        while ((tokens.get(currentToken).getWord().equals("-") | tokens.get(currentToken).getWord().equals("+"))
                && isCurrentTokenValid() && isSameLine()) {
            // Añadir current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_A();
            // SEMANTIC
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, operator);
            SemanticAnalyzer.pushStack(result);
            // SEMANTIC
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_A() {
        // Añade A al árbol
        addNote("RULE_A");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        String operator = "";

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");
        // follow.add(">");
        // follow.add("<");
        // follow.add("!=");
        // follow.add("==");
        // follow.add("+");
        // follow.add("-");

        RULE_B();

        while ((tokens.get(currentToken).getWord().equals("/") | tokens.get(currentToken).getWord().equals("*"))
                && isCurrentTokenValid() && isSameLine()) {
            // Añade current token al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_B();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, operator);
            SemanticAnalyzer.pushStack(result);
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_B() {
        // A{ade B al árbol
        addNote("RULE_B");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();
        boolean op_used = false;

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");
        // follow.add(">");
        // follow.add("<");
        // follow.add("!=");
        // follow.add("==");
        // follow.add("+");
        // follow.add("-");
        // follow.add("*");
        // follow.add("/");

        if (tokens.get(currentToken).getWord().equals("-") && isCurrentTokenValid() && isSameLine()) {
            // Añade - al árbol
            // SEMANTIC
            op_used = true;
            // SEMANTIC

            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
        }

        RULE_C();

        if (op_used) {
            // SEMANTIC
            String x = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, "-");
            SemanticAnalyzer.pushStack(result);
            // SEMANTIC
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_C() {
        // Añade C al árbol
        addNote("RULE_C");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("!");
        // first.add("-");
        // first.add("Integer");
        // first.add("Octal");
        // first.add("Hexadecimal");
        // first.add("Float");
        // first.add("String");
        // first.add("BINARY");
        // first.add("ID");
        // first.add("true");
        // first.add("false");
        // first.add("(");
        // follow.add(")");
        // follow.add(";");
        // follow.add("|");
        // follow.add("&");
        // follow.add(">");
        // follow.add("<");
        // follow.add("!=");
        // follow.add("==");
        // follow.add("+");
        // follow.add("-");

        // addNote(tokens.get(currentToken).getToken(), false);
        // if (tokens.get(currentToken).getWord().equals("(") | isCurrentTokenValid()) {
        // currentToken++;
        // RULE_EXPRESSION();
        // }

        if (isCurrentTokenValid()) {
            if (isVarKey(tokens.get(currentToken).getToken()) && isCurrentTokenValid()) {
                // Añadir current token al árbol
                addNote(tokens.get(currentToken).getToken() + "(" + tokens.get(currentToken).getWord() + ")", false);
                SemanticAnalyzer.pushStack(TokenAnalyzer(tokens.get(currentToken).getToken()));
                currentToken++;
            } else if (tokens.get(currentToken).getToken().equals("ID") && isCurrentTokenValid()) {
                // Añadir current token al árbol
                addNote(tokens.get(currentToken).getToken() + "(" + tokens.get(currentToken).getWord() + ")", false);
                SemanticAnalyzer.pushStack(SemanticAnalyzer.getIdType(tokens.get(currentToken).getWord(),
                        tokens.get(currentToken).getLine()));
                currentToken++;
            } else if (tokens.get(currentToken).getWord().equals("true") && isCurrentTokenValid()) {
                // Añadir current token al árbol
                addNote("Boolean (" + tokens.get(currentToken).getWord() + ")", false);
                SemanticAnalyzer.pushStack(TokenAnalyzer(tokens.get(currentToken).getWord()));
                currentToken++;
            } else if (tokens.get(currentToken).getWord().equals("false") && isCurrentTokenValid()) {
                // Añadir current token al árbol
                addNote("Boolean (" + tokens.get(currentToken).getWord() + ")", false);
                SemanticAnalyzer.pushStack(TokenAnalyzer(tokens.get(currentToken).getWord()));
                currentToken++;
            } else if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                // Añade current token al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    // Añade current token al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                } else {
                    error(4);
                }
            } else {
                error(5);
            }
        } else {
            error(5);
        }

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_VARIABLE() {
        // Añade Variable al árbol
        addNote("RULE_VARIABLE");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // String type = "", id = "";

        // System.out.println("yametekudasai");

        // FIRST y FOLLOW
        // Set<String> first = new HashSet<>();
        // Set<String> follow = new HashSet<>();
        // first.add("int");
        // first.add("float");
        // first.add("string");
        // first.add("boolean");
        // follow.add(";");

        // boolean sets = true;

        if (isCurrentTokenValid() && tokens.get(currentToken).getToken().equals("KEYWORD")) {
            if (isVariable(tokens.get(currentToken).getWord())) {
                addNote("Keyword (" + tokens.get(currentToken).getWord() + ")", false);
                currentToken++;
            }
            if (isCurrentTokenValid() && tokens.get(currentToken).getToken().equals("ID") && isSameLine()) {
                addNote("ID (" + tokens.get(currentToken).getWord() + ")", false);

                SemanticAnalyzer.CheckVariable(tokens.get(currentToken - 1).getWord(),
                        tokens.get(currentToken).getWord(), tokens.get(currentToken).getLine());

                // Semantic Analyzer
                // SemanticAnalyzer.CheckVariable(tokens.get(currentToken - 1).getWord(),
                // tokens.get(currentToken).getWord());
                // System.out.println("current token - 1: " + tokens.get(currentToken -
                // 1).getWord());
                // System.out.println("current token: " + tokens.get(currentToken).getWord());

                currentToken++;
            } else {
                error(8);
            }
            if (isCurrentTokenValid() && isSameLine() && tokens.get(currentToken).getWord().equals("=")) {
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
            }
        }
        // if (isCurrentTokenValid() && tokens.get(currentToken).getWord().equals("="))
        // {
        // }

        /*
         * if ((isVariable(tokens.get(currentToken).getWord()) &&
         * tokens.get(currentToken).getToken().equals("KEYWORD")) &&
         * isCurrentTokenValid()) {
         * //Añade current token al árbol
         * addNote(tokens.get(currentToken).getWord(), false);
         * currentToken++;
         * if (tokens.get(currentToken).getToken().equals("ID") &&
         * isCurrentTokenValid()) {
         * //Añadir current token al árbol
         * addNote(tokens.get(currentToken).getWord(), false);
         * currentToken++;
         * } else {
         * error(8);
         * }
         * if (tokens.get(currentToken).getWord().equals("=") && isCurrentTokenValid())
         * {
         * //Añade current token al árbol
         * addNote(tokens.get(currentToken).getWord(), false);
         * currentToken++;
         * RULE_EXPRESSION();
         * }
         * }
         */

        // if (sets) {
        // error(5);
        // while (sets) {
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // if (sets) {
        // currentToken++;
        // }
        // }
        // } else {
        // sets = true;
        // for (String set : follow) {
        // if (set.equals(tokens.get(currentToken).getWord())) {
        // sets = false;
        // break;
        // }
        // }
        // }
        // if (sets) {
        // error(3);
        // }

        // Devuelve nivel al árbol
        current_level = current_level_at_tree;
    }

    public static void RULE_WHILE() {
        // Añade While al árbol
        addNote("RULE_WHILE");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        Set<String> first = new HashSet<>();
        first.add("while");

        if (tokens.get(currentToken).getWord().equals("while") && isCurrentTokenValid()) {
            // Añade while al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                // Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                // SEMANTIC
                String x = SemanticAnalyzer.popStack();
                if (!x.equals("Boolean")) {
                    SemanticAnalyzer.error(3, tokens.get(currentToken - 1).getLine(), "");
                    System.out.println("RULE WHILE ERROR");
                }
                // SEMANTIC
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    // Añade ) al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                    RULE_PROGRAM();
                } else {
                    error(4);
                }
            } else {
                error(9);
            }

        }

        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_IF() {
        // Añade IF al árbol
        addNote("RULE_IF");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        Set<String> first = new HashSet<>();
        first.add("if");

        if (tokens.get(currentToken).getWord().equals("if") && isCurrentTokenValid()) {
            // Añade if al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                // Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                // SEMANTIC
                String x = SemanticAnalyzer.popStack();
                if (!x.equals("Boolean")) {
                    SemanticAnalyzer.error(3, tokens.get(currentToken).getLine(), "");
                    System.out.println("ERROR IN IF FUNC");
                }
                // SEMANTIC
                if (tokens.get(currentToken).getWord().equals(")")) {
                    // Añade ) al árbol
                    addNote(tokens.get(currentToken).getWord(), false);
                    currentToken++;
                    RULE_PROGRAM();
                    if (tokens.get(currentToken).getWord().equals("else")) {
                        // Añade else al árbol
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
        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_RETURN() {
        // Añade Return al árbol
        addNote("RULE_RETURN");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        Set<String> first = new HashSet<>();
        first.add("return");

        if (tokens.get(currentToken).getWord().equals("return") && isCurrentTokenValid()) {
            // Añade return al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;
            RULE_EXPRESSION();
        }
        // Devuelve nivel al padre
        current_level = current_level_at_tree;
    }

    public static void RULE_PRINT() {
        // Añade Print al árbol
        addNote("RULE_PRINT");
        DefaultMutableTreeNode current_level_at_tree = (DefaultMutableTreeNode) current_level.getParent();

        // FIRST y FOLLOW
        Set<String> first = new HashSet<>();
        first.add("print");

        if (tokens.get(currentToken).getWord().equals("print") && isCurrentTokenValid()) {
            // Añade print al árbol
            addNote(tokens.get(currentToken).getWord(), false);
            currentToken++;

            if (tokens.get(currentToken).getWord().equals("(") && isCurrentTokenValid()) {
                // Añade ( al árbol
                addNote(tokens.get(currentToken).getWord(), false);
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getWord().equals(")") && isCurrentTokenValid()) {
                    // Añade ) al árbol
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
    // System.out.println("Error in " + tokens.get(currentToken).getLine());
    // }

    private static String TokenAnalyzer(String token) {

        switch (token) {
            case "Integer":
                return "int";
            case "Octal":
                return "int";
            case "BINARY":
                return "int";
            case "Hexadecimal":
                return "int";
            case "String":
                return "string";
            case "Float":
                return "float";
            case "true":
                return "boolean";
            case "false":
                return "boolean";
        }
        return "error";
    }

}
