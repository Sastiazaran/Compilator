package compilator;

import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Recursive-descent parser. All token access is guarded so empty input and
 * end-of-file do not throw. Logical {@code &}/{@code &&} and {@code |}/{@code ||}
 * are treated as the same operators; {@code %} is a multiplicative operator.
 */
public class Parser {

    private final Vector<Token> tokens;
    private final ErrorController errorController;
    private int currentToken;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode currentLevel;

    public Parser(Vector<Token> tokens) {
        this(tokens, new ErrorController("PARSER"));
    }

    public Parser(Vector<Token> tokens, ErrorController errorController) {
        this.tokens = tokens == null ? new Vector<>() : tokens;
        this.errorController = errorController == null ? new ErrorController("PARSER") : errorController;
    }

    public ErrorController getErrorController() {
        return errorController;
    }

    public DefaultMutableTreeNode parse() {
        currentToken = 0;
        root = new DefaultMutableTreeNode("root");
        currentLevel = root;
        SemanticAnalyzer.clearVariables();
        RULE_PROGRAM();
        return root;
    }

    private void error(int type) {
        int line = isCurrentTokenValid() ? tokens.get(currentToken).getLine()
                : (tokens.isEmpty() ? 1 : tokens.lastElement().getLine());
        String expected = switch (type) {
            case 1 -> "{";
            case 2 -> "}";
            case 3 -> ";";
            case 4 -> ")";
            case 5 -> "vartype";
            case 6 -> "validbody";
            case 7 -> "=";
            case 8 -> "ID";
            case 9 -> "(";
            case 10 -> "while";
            case 11 -> "break";
            case 12 -> "valid data val";
            case 13 -> ":";
            default -> "token";
        };
        errorController.storeError("Line " + line + ": [Parser] expected " + expected);
    }

    private void addNote(String name) {
        addNote(name, true);
    }

    private void addNote(String name, boolean changeLevel) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        currentLevel.add(newNode);
        if (changeLevel) {
            currentLevel = newNode;
        }
    }

    private boolean isCurrentTokenValid() {
        return currentToken >= 0 && currentToken < tokens.size();
    }

    private boolean wordIs(String word) {
        return isCurrentTokenValid() && word.equals(tokens.get(currentToken).getWord());
    }

    private boolean typeIs(String type) {
        return isCurrentTokenValid() && type.equals(tokens.get(currentToken).getToken());
    }

    private boolean wordIsAny(String... words) {
        if (!isCurrentTokenValid()) {
            return false;
        }
        String current = tokens.get(currentToken).getWord();
        for (String word : words) {
            if (word.equals(current)) {
                return true;
            }
        }
        return false;
    }

    private String currentWord() {
        return isCurrentTokenValid() ? tokens.get(currentToken).getWord() : "";
    }

    private String currentType() {
        return isCurrentTokenValid() ? tokens.get(currentToken).getToken() : "";
    }

    private int currentLine() {
        return isCurrentTokenValid() ? tokens.get(currentToken).getLine() : 0;
    }

    private static boolean isVariable(String token) {
        return "int".equals(token) || "float".equals(token) || "string".equals(token) || "boolean".equals(token)
                || "char".equals(token) || "void".equals(token);
    }

    private static boolean isVarKey(String token) {
        return "Integer".equals(token) || "Float".equals(token) || "String".equals(token)
                || "Hexadecimal".equals(token) || "Octal".equals(token) || "BINARY".equals(token);
    }

    private boolean isSameLine() {
        if (!isCurrentTokenValid() || currentToken == 0) {
            return true;
        }
        return tokens.get(currentToken).getLine() == tokens.get(currentToken - 1).getLine();
    }

    private void consumeWord() {
        if (isCurrentTokenValid()) {
            currentToken++;
        }
    }

    private boolean isExpressionStart() {
        return wordIs("!") || wordIs("-") || typeIs("Integer") || typeIs("Octal") || typeIs("BINARY")
                || typeIs("Hexadecimal") || typeIs("String") || typeIs("Float") || typeIs("ID")
                || wordIs("(") || wordIs("true") || wordIs("false");
    }

    private void skipUntilSync() {
        int line = currentLine();
        while (isCurrentTokenValid() && currentLine() == line && !wordIs("}") && !wordIs(";")) {
            if (typeIs("ERROR")) {
                addNote("error (" + currentWord() + ")", false);
            }
            currentToken++;
        }
        if (wordIs(";") && isCurrentTokenValid()) {
            currentToken++;
        }
    }

    public void RULE_PROGRAM() {
        addNote("PROGRAM");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (wordIs("{")) {
            addNote(currentWord(), false);
            consumeWord();
        } else {
            error(1);
        }

        RULE_BODY();

        if (wordIs("}")) {
            addNote(currentWord(), false);
            consumeWord();
        } else {
            error(2);
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_BODY() {
        addNote("BODY");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        while (isCurrentTokenValid() && !wordIs("}")) {
            if (typeIs("ID")) {
                RULE_ASSIGNMENT();
                if (wordIs(";") && isSameLine()) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(3);
                    skipUntilSync();
                }
            } else if (isVariable(currentWord()) && typeIs("KEYWORD")) {
                RULE_VARIABLE();
                if (wordIs(";")) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(3);
                    skipUntilSync();
                }
            } else if (wordIs("while")) {
                RULE_WHILE();
            } else if (wordIs("if")) {
                RULE_IF();
            } else if (wordIs("return")) {
                RULE_RETURN();
                if (wordIs(";")) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(3);
                    skipUntilSync();
                }
            } else if (wordIs("print")) {
                RULE_PRINT();
                if (wordIs(";")) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(3);
                    skipUntilSync();
                }
            } else {
                error(6);
                if (isCurrentTokenValid()) {
                    if (typeIs("ERROR")) {
                        addNote("error (" + currentWord() + ")", false);
                    }
                    currentToken++;
                }
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_ASSIGNMENT() {
        addNote("RULE_ASSIGNMENT");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (typeIs("ID")) {
            SemanticAnalyzer.pushStack(SemanticAnalyzer.getIdType(currentWord(), currentLine()));
            addNote(currentWord(), false);
            consumeWord();
            if (wordIs("=")) {
                addNote(currentWord(), false);
                consumeWord();
            } else {
                error(7);
                while (isCurrentTokenValid() && !isExpressionStart() && !wordIs(";") && !wordIs(")")) {
                    if (typeIs("ERROR")) {
                        addNote("error (" + currentWord() + ")", false);
                    }
                    currentToken++;
                }
            }
            RULE_EXPRESSION();

            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            String result = SemanticAnalyzer.calculateCube(x, y, "=");
            if (!"OK".equals(result) && y != null && !y.isEmpty()) {
                SemanticAnalyzer.error(2, tokens.isEmpty() ? 1 : tokens.get(Math.max(0, currentToken - 1)).getLine(), "");
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_EXPRESSION() {
        addNote("RULE_EXPRESSION");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        RULE_X();

        while (wordIs("|") || wordIs("||")) {
            String operator = currentWord();
            addNote(operator, false);
            consumeWord();
            RULE_X();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, y, operator));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_X() {
        addNote("RULE_X");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        RULE_Y();

        while (wordIs("&") || wordIs("&&")) {
            String operator = currentWord();
            addNote(operator, false);
            consumeWord();
            RULE_Y();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, y, operator));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_Y() {
        addNote("RULE_Y");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();
        boolean opUsed = false;

        if (wordIs("!") && isSameLine()) {
            opUsed = true;
            addNote(currentWord(), false);
            consumeWord();
        }

        RULE_R();
        if (opUsed) {
            String x = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, "!"));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_R() {
        addNote("RULE_R");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        RULE_E();

        while (wordIsAny("<", ">", "==", "!=", "<=", ">=")) {
            String operator = currentWord();
            addNote(operator, false);
            consumeWord();
            RULE_E();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, y, operator));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_E() {
        addNote("RULE_E");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        RULE_A();

        while (isCurrentTokenValid() && isSameLine() && (wordIs("-") || wordIs("+"))) {
            String operator = currentWord();
            addNote(operator, false);
            consumeWord();
            RULE_A();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, y, operator));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_A() {
        addNote("RULE_A");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        RULE_B();

        while (isCurrentTokenValid() && isSameLine() && (wordIs("/") || wordIs("*") || wordIs("%"))) {
            String operator = currentWord();
            addNote(operator, false);
            consumeWord();
            RULE_B();
            String x = SemanticAnalyzer.popStack();
            String y = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, y, operator));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_B() {
        addNote("RULE_B");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();
        boolean opUsed = false;

        if (wordIs("-") && isSameLine()) {
            opUsed = true;
            addNote(currentWord(), false);
            consumeWord();
        }

        RULE_C();

        if (opUsed) {
            String x = SemanticAnalyzer.popStack();
            SemanticAnalyzer.pushStack(SemanticAnalyzer.calculateCube(x, "-"));
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_C() {
        addNote("RULE_C");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (isCurrentTokenValid()) {
            if (isVarKey(currentType())) {
                addNote(currentType() + "(" + currentWord() + ")", false);
                SemanticAnalyzer.pushStack(tokenAnalyzer(currentType()));
                consumeWord();
            } else if (typeIs("ID")) {
                addNote(currentType() + "(" + currentWord() + ")", false);
                SemanticAnalyzer.pushStack(SemanticAnalyzer.getIdType(currentWord(), currentLine()));
                consumeWord();
            } else if (wordIs("true") || wordIs("false")) {
                addNote("boolean (" + currentWord() + ")", false);
                SemanticAnalyzer.pushStack(tokenAnalyzer(currentWord()));
                consumeWord();
            } else if (wordIs("(")) {
                addNote(currentWord(), false);
                consumeWord();
                RULE_EXPRESSION();
                if (wordIs(")")) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(4);
                }
            } else {
                error(5);
            }
        } else {
            error(5);
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_VARIABLE() {
        addNote("RULE_VARIABLE");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (typeIs("KEYWORD") && isVariable(currentWord())) {
            String declaredType = currentWord();
            addNote("Keyword (" + declaredType + ")", false);
            consumeWord();
            if (typeIs("ID") && isSameLine()) {
                addNote("ID (" + currentWord() + ")", false);
                SemanticAnalyzer.CheckVariable(declaredType, currentWord(), currentLine());
                SemanticAnalyzer.pushStack(SemanticAnalyzer.getIdType(currentWord(), currentLine()));
                consumeWord();
            } else {
                error(8);
            }
            if (wordIs("=") && isSameLine()) {
                addNote(currentWord(), false);
                consumeWord();
                RULE_EXPRESSION();
                String x = SemanticAnalyzer.popStack();
                String y = SemanticAnalyzer.popStack();
                String result = SemanticAnalyzer.calculateCube(x, y, "=");
                if (!"OK".equals(result) && y != null && !y.isEmpty()) {
                    int line = isCurrentTokenValid() ? tokens.get(Math.max(0, currentToken - 1)).getLine()
                            : (tokens.isEmpty() ? 1 : tokens.lastElement().getLine());
                    SemanticAnalyzer.error(2, line, "");
                }
            } else {
                SemanticAnalyzer.popStack();
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_WHILE() {
        addNote("RULE_WHILE");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (wordIs("while")) {
            addNote(currentWord(), false);
            consumeWord();
            if (wordIs("(")) {
                addNote(currentWord(), false);
                consumeWord();
                RULE_EXPRESSION();
                String x = SemanticAnalyzer.popStack();
                if (!"boolean".equals(x)) {
                    int line = isCurrentTokenValid() ? tokens.get(Math.max(0, currentToken - 1)).getLine()
                            : (tokens.isEmpty() ? 1 : tokens.lastElement().getLine());
                    SemanticAnalyzer.error(3, line, "");
                }
                if (wordIs(")")) {
                    addNote(currentWord(), false);
                    consumeWord();
                    RULE_PROGRAM();
                } else {
                    error(4);
                }
            } else {
                error(9);
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_IF() {
        addNote("RULE_IF");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (wordIs("if")) {
            addNote(currentWord(), false);
            consumeWord();
            if (wordIs("(")) {
                addNote(currentWord(), false);
                consumeWord();
                RULE_EXPRESSION();
                String x = SemanticAnalyzer.popStack();
                if (!"boolean".equals(x)) {
                    int line = isCurrentTokenValid() ? currentLine()
                            : (tokens.isEmpty() ? 1 : tokens.lastElement().getLine());
                    SemanticAnalyzer.error(3, line, "");
                }
                if (wordIs(")")) {
                    addNote(currentWord(), false);
                    consumeWord();
                    RULE_PROGRAM();
                    if (wordIs("else")) {
                        addNote(currentWord(), false);
                        consumeWord();
                        RULE_PROGRAM();
                    }
                } else {
                    error(4);
                }
            } else {
                error(9);
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_RETURN() {
        addNote("RULE_RETURN");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (wordIs("return")) {
            addNote(currentWord(), false);
            consumeWord();
            RULE_EXPRESSION();
        }

        currentLevel = parent != null ? parent : root;
    }

    public void RULE_PRINT() {
        addNote("RULE_PRINT");
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) currentLevel.getParent();

        if (wordIs("print")) {
            addNote(currentWord(), false);
            consumeWord();
            if (wordIs("(")) {
                addNote(currentWord(), false);
                consumeWord();
                RULE_EXPRESSION();
                if (wordIs(")")) {
                    addNote(currentWord(), false);
                    consumeWord();
                } else {
                    error(4);
                }
            } else {
                error(9);
            }
        }

        currentLevel = parent != null ? parent : root;
    }

    private static String tokenAnalyzer(String token) {
        return switch (token) {
            case "Integer", "Octal", "BINARY", "Hexadecimal" -> "int";
            case "String" -> "string";
            case "Float" -> "float";
            case "true", "false" -> "boolean";
            default -> "error";
        };
    }
}
