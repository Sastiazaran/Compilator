import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * @author Sebastian Astiazaran
 * @author Pablo Uscanga
 * @author Rolando Palacios
 */

public class Lexer {

    private String text;
    private Vector<Token> tokens;
    private static final String[] KEYWORDS = { "if", "else",
            "while", "switch", "case", "return", "int", "float", "void", "char", "string", "boolean", "true",
            "false", "print" };
    private static final List<String> KEYWORD = Arrays.asList(KEYWORDS);

    // CONSTANTS
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
    private static final int DELIMITER = 20;

    // Table

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
            { 11, 11, 11, 11, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP }, // dot
            { 11, 11, 11, 11, ERROR, ERROR, ERROR, 12, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP }, // dot
                                                                                                                // float
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP }, // exp
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP }, // exp
                                                                                                                   // float
            { ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR,
                    STOP }, // f float
            { 13, 13, 13, 13, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, STOP }, // sign
                                                                                                                   // exp
            { 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, STOP }, // first"
            { 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, STOP }, // TextString
            { ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR, ERROR,
                    STOP } // String
    };

    // Constructor
    public Lexer(String text) {
        this.text = text;
    }

    // run
    public void run() {
        tokens = new Vector<Token>();
        String line;
        int counterOfLines = 1;
        while (!text.isEmpty()) {
            int eolAt = findNextLineSeparator();
            if (eolAt >= 0) {
                line = text.substring(0, eolAt);
                text = text.substring(eolAt + 1);
            } else {
                line = text;
                text = "";
            }
            splitLine(counterOfLines, line);
            counterOfLines++;
        }
    }

    private int findNextLineSeparator() {
        int index = text.indexOf("\n");
        int carriageReturnIndex = text.indexOf("\r\n");
        if (index < 0 || (carriageReturnIndex >= 0 && carriageReturnIndex < index)) {
            return carriageReturnIndex;
        }
        return index;
    }

    private void splitLine(int row, String line) {
        int state = 0;
        int index = 0;
        char currentChar;
        String string = "";
        if (line.isEmpty())
            return;

        // DFA

        do {
            currentChar = line.charAt(index);
            state = calculateNextState(state, currentChar);
            if (!isDelimiter(currentChar) && (!isOperator(currentChar) || state == 15 || state == 16 || state == 17)
                    && (!isSpace(currentChar)))
                string += currentChar;
            index++;
        } while (index < line.length()
                && (((!isOperator(currentChar) || state == 15) && !isDelimiter(currentChar) && (!isSpace(currentChar)))
                        || state == 16 || state == 17)
                && (!isQuotationMark(currentChar) || state == 16));

        // Review Final State

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
            if (!string.equals(" ") && !string.equals(""))
                tokens.add(new Token(string, "ERROR", row));
        }

        // Current char

        if (isDelimiter(currentChar))
            tokens.add(new Token("" + currentChar + "", "DELIMITER", row));
        else if (isOperator(currentChar)) {
            if (index < line.length()) {
                String tempOperators = Character.toString(currentChar) + Character.toString(line.charAt(index));
                // System.out.println(tempOperators);
                if (tempOperators.equals("&&") || tempOperators.equals("!=") || tempOperators.equals("==")
                        || tempOperators.equals("<=") || tempOperators.equals(">=")) {
                    tokens.add(new Token(tempOperators, "OPERATOR", row));
                    index++;
                } else {
                    tokens.add(new Token("" + currentChar + "", "OPERATOR", row));
                }
            } else {
                tokens.add(new Token("" + currentChar + "", "OPERATOR", row));
            }
        }

        /*
         * else if (isOperator(currentChar))
         * tokens.add(new Token(""+currentChar+"", "OPERATOR", row));
         */

        // loop
        if (index < line.length())
            splitLine(row, line.substring(index));

    }

    // calculate state

    private int calculateNextState(int state, char currentChar) {
        if (isSpace(currentChar) || isDelimiter(currentChar) || isOperator(currentChar) || isQuotationMark(currentChar)
                || isEnter(currentChar))
            // return stateTable[state][DELIMITER];
            if (currentChar == '-' && state == 12)
                return 15;
            else if (currentChar == '"' && state == 0)
                return 16;
            else if (currentChar == '"' && state == 17)
                return 18;
            else if (state == 16 || state == 17)
                return 17;
            else
                return state;
        else if (currentChar == 'b' || currentChar == 'B')
            return stateTable[state][B];
        else if (currentChar == '0')
            return stateTable[state][ZERO];
        else if (currentChar == '1')
            return stateTable[state][ONE];
        else if (currentChar == '2' || currentChar == '3' || currentChar == '4' || currentChar == '5'
                || currentChar == '6' || currentChar == '7')
            return stateTable[state][TWOSEVEN];
        else if (currentChar == '8' || currentChar == '9')
            return stateTable[state][EIGHTNINE];
        else if (currentChar == 'A' || currentChar == 'a')
            return stateTable[state][A];
        else if (currentChar == 'B' || currentChar == 'b')
            return stateTable[state][B];
        else if (currentChar == 'C' || currentChar == 'D' || currentChar == 'c' || currentChar == 'd')
            return stateTable[state][CD];
        else if (currentChar == 'E' || currentChar == 'e')
            return stateTable[state][E];
        else if (currentChar == 'F' || currentChar == 'f')
            return stateTable[state][F];
        else if (currentChar == 'G' || currentChar == 'H' || currentChar == 'I' || currentChar == 'J'
                || currentChar == 'K' || currentChar == 'L' || currentChar == 'M'
                || currentChar == 'N' || currentChar == 'P' || currentChar == 'Q' || currentChar == 'R'
                || currentChar == 'S' || currentChar == 'T' || currentChar == 'O'
                || currentChar == 'V' || currentChar == 'U' || currentChar == 'W' || currentChar == 'g'
                || currentChar == 'h' || currentChar == 'i' || currentChar == 'j' || currentChar == 'k'
                || currentChar == 'l'
                || currentChar == 'm' || currentChar == 'n' || currentChar == 'o' || currentChar == 'p'
                || currentChar == 'q' || currentChar == 'r' || currentChar == 's' || currentChar == 't'
                || currentChar == 'u'
                || currentChar == 'v' || currentChar == 'w')
            return stateTable[state][GW];
        else if (currentChar == 'x' || currentChar == 'X')
            return stateTable[state][X];
        else if (currentChar == 'y' || currentChar == 'z' || currentChar == 'Y' || currentChar == 'Z')
            return stateTable[state][YZ];
        else if (currentChar == '$' || currentChar == '_')
            return stateTable[state][CASH];
        return stateTable[state][OTHER];
    }

    // Delimiter

    private boolean isDelimiter(char c) {
        char[] delimiters = { ':', ';', '(', ')', '[', ']', '{', '}', ',' };
        for (int x = 0; x < delimiters.length; x++) {
            if (c == delimiters[x])
                return true;
        }
        return false;
    }

    // Operator

    private boolean isOperator(char o) {
        char[] operators = { '+', '-', '*', '/', '<', '>', '=', '!', '&', '|' };
        for (int x = 0; x < operators.length; x++) {
            if (o == operators[x])
                return true;
        }
        return false;
    }

    // Quotations

    private boolean isQuotationMark(char o) {
        char[] quote = { '"', '\'' };
        for (int x = 0; x < quote.length; x++) {
            if (o == quote[x])
                return true;
        }
        return false;
    }

    // Space

    private boolean isSpace(char o) {
        return o == ' ';
    }

    // Enter

    private boolean isEnter(char o) {
        return o == '\n';
    }

    // get Tokens

    public Vector<Token> getTokens() {
        return tokens;
    }

}
