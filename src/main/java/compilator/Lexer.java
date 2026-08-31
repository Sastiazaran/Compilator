package compilator;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * DFA-based lexer. Splits source on any of {@code \n}, {@code \r\n}, or {@code \r}
 * so Windows, Unix, and classic Mac files tokenize the same way.
 */
public class Lexer {

    private String text;
    private Vector<Token> tokens;
    private static final String[] KEYWORDS = {
            "if", "else", "while", "switch", "case", "return", "int", "float", "void",
            "char", "string", "boolean", "true", "false", "print"
    };
    private static final List<String> KEYWORD = Arrays.asList(KEYWORDS);

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWOSEVEN = 2;
    private static final int EIGHTNINE = 3;
    private static final int A = 4;
    private static final int B = 5;
    private static final int CD = 6;
    private static final int E = 7;
    private static final int F = 8;
    private static final int GW = 9;
    private static final int X = 10;
    private static final int YZ = 11;
    private static final int CASH = 12;
    private static final int DOT = 13;
    private static final int OTHER = 14;
    private static final int STOP = -2;
    private static final int ERROR = 11;

    private static final int[][] stateTable = {
            { 2, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, ERROR, STOP },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, ERROR, ERROR, STOP },
            { ERROR, 4, 4, ERROR, ERROR, 5, ERROR, 12, 14, ERROR, 7, ERROR, ERROR, 10, ERROR, STOP },
            { 3, 3, 3, 3, ERROR, ERROR, ERROR, 12, 14, ERROR, ERROR, ERROR, ERROR, 10, ERROR, STOP },
            { 4, 4, 4, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 6, 6, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 6, 6, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 8, 8, 8, 8, 8, 8, 8, 8, 8, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 8, 8, 8, 8, 8, 8, 8, 8, 8, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR,
                    STOP },
            { 11, 11, 11, 11, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 11, 11, 11, 11, ERROR, ERROR, ERROR, 12, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR,
                    STOP },
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP },
            { 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, STOP },
            { 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, STOP },
            { ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR,
                    STOP }
    };

    public Lexer(String text) {
        this.text = text == null ? "" : text;
    }

    public void run() {
        tokens = new Vector<>();
        int counterOfLines = 1;
        while (!text.isEmpty()) {
            LineBreak breakAt = findNextLineBreak();
            String line;
            if (breakAt != null) {
                line = text.substring(0, breakAt.index);
                text = text.substring(breakAt.index + breakAt.length);
            } else {
                line = text;
                text = "";
            }
            splitLine(counterOfLines, line);
            counterOfLines++;
        }
    }

    private static final class LineBreak {
        final int index;
        final int length;

        LineBreak(int index, int length) {
            this.index = index;
            this.length = length;
        }
    }

    /**
     * Finds the first {@code \r\n}, {@code \n}, or {@code \r} so tokenization is OS-independent.
     */
    private LineBreak findNextLineBreak() {
        int n = text.indexOf('\n');
        int r = text.indexOf('\r');
        if (n < 0 && r < 0) {
            return null;
        }
        if (r >= 0 && (n < 0 || r < n)) {
            int length = (r + 1 < text.length() && text.charAt(r + 1) == '\n') ? 2 : 1;
            return new LineBreak(r, length);
        }
        return new LineBreak(n, 1);
    }

    private void splitLine(int row, String line) {
        int state = 0;
        int index = 0;
        char currentChar;
        String string = "";
        if (line.isEmpty()) {
            return;
        }

        do {
            currentChar = line.charAt(index);
            state = calculateNextState(state, currentChar);
            if (!isDelimiter(currentChar) && (!isOperator(currentChar) || state == 15 || state == 16 || state == 17)
                    && (!isSpace(currentChar))) {
                string += currentChar;
            }
            index++;
        } while (index < line.length()
                && (((!isOperator(currentChar) || state == 15) && !isDelimiter(currentChar) && (!isSpace(currentChar)))
                        || state == 16 || state == 17)
                && (!isQuotationMark(currentChar) || state == 16));

        if (state == 6) {
            tokens.add(new Token(string, "BINARY", row));
        } else if (state == 1) {
            if (KEYWORD.contains(string)) {
                tokens.add(new Token(string, "KEYWORD", row));
            } else {
                tokens.add(new Token(string, "ID", row));
            }
        } else if (state == 3 || state == 2) {
            tokens.add(new Token(string, "Integer", row));
        } else if (state == 4) {
            tokens.add(new Token(string, "Octal", row));
        } else if (state == 8) {
            tokens.add(new Token(string, "Hexadecimal", row));
        } else if (state == 14 || state == 13 || state == 11) {
            tokens.add(new Token(string, "Float", row));
        } else if (state == 18) {
            tokens.add(new Token(string, "String", row));
        } else {
            if (!string.isBlank()) {
                tokens.add(new Token(string, "ERROR", row));
            }
        }

        if (isDelimiter(currentChar)) {
            tokens.add(new Token(String.valueOf(currentChar), "DELIMITER", row));
        } else if (isOperator(currentChar)) {
            if (index < line.length()) {
                String tempOperators = Character.toString(currentChar) + Character.toString(line.charAt(index));
                if (isTwoCharOperator(tempOperators)) {
                    tokens.add(new Token(tempOperators, "OPERATOR", row));
                    index++;
                } else {
                    tokens.add(new Token(String.valueOf(currentChar), "OPERATOR", row));
                }
            } else {
                tokens.add(new Token(String.valueOf(currentChar), "OPERATOR", row));
            }
        }

        if (index < line.length()) {
            splitLine(row, line.substring(index));
        }
    }

    private static boolean isTwoCharOperator(String op) {
        return "&&".equals(op) || "||".equals(op) || "!=".equals(op) || "==".equals(op)
                || "<=".equals(op) || ">=".equals(op);
    }

    private int calculateNextState(int state, char currentChar) {
        if (isSpace(currentChar) || isDelimiter(currentChar) || isOperator(currentChar) || isQuotationMark(currentChar)
                || isEnter(currentChar)) {
            if (currentChar == '-' && state == 12) {
                return 15;
            } else if (currentChar == '"' && state == 0) {
                return 16;
            } else if (currentChar == '"' && state == 17) {
                return 18;
            } else if (state == 16 || state == 17) {
                return 17;
            } else {
                return state;
            }
        } else if (currentChar == 'b' || currentChar == 'B') {
            return stateTable[state][B];
        } else if (currentChar == '0') {
            return stateTable[state][ZERO];
        } else if (currentChar == '1') {
            return stateTable[state][ONE];
        } else if (currentChar == '2' || currentChar == '3' || currentChar == '4' || currentChar == '5'
                || currentChar == '6' || currentChar == '7') {
            return stateTable[state][TWOSEVEN];
        } else if (currentChar == '8' || currentChar == '9') {
            return stateTable[state][EIGHTNINE];
        } else if (currentChar == 'A' || currentChar == 'a') {
            return stateTable[state][A];
        } else if (currentChar == 'C' || currentChar == 'D' || currentChar == 'c' || currentChar == 'd') {
            return stateTable[state][CD];
        } else if (currentChar == 'E' || currentChar == 'e') {
            return stateTable[state][E];
        } else if (currentChar == 'F' || currentChar == 'f') {
            return stateTable[state][F];
        } else if (isGwLetter(currentChar)) {
            return stateTable[state][GW];
        } else if (currentChar == 'x' || currentChar == 'X') {
            return stateTable[state][X];
        } else if (currentChar == 'y' || currentChar == 'z' || currentChar == 'Y' || currentChar == 'Z') {
            return stateTable[state][YZ];
        } else if (currentChar == '$' || currentChar == '_') {
            return stateTable[state][CASH];
        }
        return stateTable[state][OTHER];
    }

    private static boolean isGwLetter(char currentChar) {
        return currentChar == 'G' || currentChar == 'H' || currentChar == 'I' || currentChar == 'J'
                || currentChar == 'K' || currentChar == 'L' || currentChar == 'M'
                || currentChar == 'N' || currentChar == 'P' || currentChar == 'Q' || currentChar == 'R'
                || currentChar == 'S' || currentChar == 'T' || currentChar == 'O'
                || currentChar == 'V' || currentChar == 'U' || currentChar == 'W' || currentChar == 'g'
                || currentChar == 'h' || currentChar == 'i' || currentChar == 'j' || currentChar == 'k'
                || currentChar == 'l'
                || currentChar == 'm' || currentChar == 'n' || currentChar == 'o' || currentChar == 'p'
                || currentChar == 'q' || currentChar == 'r' || currentChar == 's' || currentChar == 't'
                || currentChar == 'u'
                || currentChar == 'v' || currentChar == 'w';
    }

    private boolean isDelimiter(char c) {
        return c == ':' || c == ';' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == ',';
    }

    private boolean isOperator(char o) {
        return o == '+' || o == '-' || o == '*' || o == '/' || o == '%' || o == '<' || o == '>' || o == '=' || o == '!'
                || o == '&' || o == '|';
    }

    private boolean isQuotationMark(char o) {
        return o == '"' || o == '\'';
    }

    private boolean isSpace(char o) {
        return o == ' ' || o == '\t' || o == '\f';
    }

    private boolean isEnter(char o) {
        return o == '\n' || o == '\r';
    }

    public Vector<Token> getTokens() {
        return tokens == null ? new Vector<>() : tokens;
    }
}
