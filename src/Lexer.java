import java.util.Vector;

public class Lexer {

    private String text;
    private Vector<Token> tokens;
    private static final String[] KEYWORD = {"if","else",
    "while", "switch", "case","return","int","float","void","char","string","boolean","true",
    "false","print"};
    
    //CONSTANTS
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWOSEVEN = 2;
    private static final int EIGHTNINE = 3;
    private static final int A = 4;
    private static final int B = 5;
    private static final int CF = 6;
    private static final int GW = 7;
    private static final int X = 8;
    private static final int YZ = 9;
    private static final int CASH = 10;
    private static final int OTHER = 11;
    private static final int STOP = -2;
    private static final int ERROR = -4;
    private static final int DELIMITER = -4;

    //Table

    private static final int[][] stateTable = {
        {2,3,3,3,1,1,1,1,1,1,1,ERROR,STOP},
        {1,1,1,1,1,1,1,1,1,1,1,ERROR,STOP},
        {4,4,4,ERROR,ERROR,5,ERROR,ERROR,7,ERROR,ERROR,ERROR,STOP},
        {3,3,3,3,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {4,4,4,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {6,6,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {6,6,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {8,8,8,8,8,8,8,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {8,8,8,8,8,8,8,ERROR,ERROR,ERROR,ERROR,ERROR,STOP},
        {ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,ERROR,STOP}
    };

    //Constructor
    public Lexer(String text) {
        this.text = text;
    }

    //run
    public void run(){
        tokens = new Vector<Token>();
        String line;
        int counterOfLines = 1;
        do{
            int eolAt = text.indexOf(System.lineSeparator());
            if (eolAt >= 0){
                line = text.substring(0, eolAt);
                if(text.length()>0) text = text.substring(eolAt + 1);
            }else {
                line = text;
                text = "";
            }
            splitLine(counterOfLines, line);
            counterOfLines++;
        }while(!text.equals(""));
    }

    private void splitLine(int row, String line) {
        int state = 0;
        int index = 0;
        char currentChar;
        String string = "";
        if(line.equals("")) return;

        //DFA

        do{
            currentChar = line.charAt(index);
            state = calculateNextState(state, currentChar);
            if( !isDelimiter(currentChar) && !isOperator(currentChar))
                string = string + currentChar;
            index++;
        }while (index < line.length() && state != STOP);

        // Review Final State

        if (state == 3) {
            tokens.add(new Token(string, "BINARY", row));
        } else {
            if(!string.equals(" "))
            tokens.add(new Token(string, "ERROR", row));
        }

        //Current char

        if(isDelimiter(currentChar))
        tokens.add(new Token(currentChar+"", "DELIMITER", row));
        else if (isOperator(currentChar))
        tokens.add(new Token(currentChar+"", "OPERATOR", row));
        //loop
        if(index < line.length())
        splitLine(row, line.substring(index));
    }

    //calculate state

    private int calculateNextState(int state, char currentChar){
        if (isSpace(currentChar) || isDelimiter(currentChar) || isOperator(currentChar) || isQuotationMark(currentChar))
            return stateTable[state][DELIMITER];
        else if (currentChar == 'b' || currentChar == 'B')
            return stateTable[state][B];
        else if (currentChar == '0')
            return stateTable[state][ZERO];
        else if (currentChar == '1')
            return stateTable[state][ONE];
        else if (currentChar == '2' || currentChar == '3' || currentChar == '4' || currentChar == '5' || currentChar == '6' || currentChar == '7')
            return stateTable[state][TWOSEVEN];
        else if (currentChar == '8' || currentChar == '9')
            return stateTable[state][EIGHTNINE];
        else if (currentChar == 'A' || currentChar == 'a')
            return stateTable[state][A];
        else if (currentChar == 'B' || currentChar == 'b')
            return stateTable[state][B];
        else if (currentChar == 'C' || currentChar == 'D' || currentChar == 'E' || currentChar == 'F' || currentChar == 'c' || currentChar == 'd' || currentChar == 'e' || currentChar == 'f')
            return stateTable[state][CF];
        else if (currentChar == 'G' || currentChar == 'H' || currentChar == 'I' || currentChar == 'J' || currentChar == 'K' || currentChar == 'L' || currentChar == 'M'
        || currentChar == 'N' || currentChar == 'P' || currentChar == 'Q' || currentChar == 'R' || currentChar == 'S' || currentChar == 'T' || currentChar == 'O'
        || currentChar == 'V' || currentChar == 'U' || currentChar == 'W' || currentChar == 'g' || currentChar == 'h' || currentChar == 'i' || currentChar == 'j' || currentChar == 'k' || currentChar == 'l' 
        || currentChar == 'm' || currentChar == 'n' || currentChar == 'o' || currentChar == 'p' || currentChar == 'q' || currentChar == 'r' || currentChar == 's' || currentChar == 't' || currentChar == 'u' 
        || currentChar == 'v' || currentChar == 'w')
            return stateTable[state][GW];
        else if (currentChar == 'x' || currentChar == 'X')
            return stateTable[state][X];
        else if (currentChar == 'y' || currentChar == 'z' || currentChar == 'Y' || currentChar == 'Z' )
            return stateTable[state][YZ];
        else if (currentChar == '$' || currentChar == '_')
            return stateTable[state][CASH];
        return stateTable[state][OTHER];
    }

    //Delimiter

    private boolean isDelimiter(char c){
        char[] delimiters = {':',';','(',')','[',']','{','}',','};
        for (int x = 0; x < delimiters.length; x++) {
            if(c == delimiters[x]) return true;
        }
        return false;
    }
    
    //Operator

    private boolean isOperator(char o){
        char[] operators = {'+','-','*','/','<','>','=','!','&','|'};
        for (int x = 0; x < operators.length; x++) {
            if(o == operators[x]) return true;
        }
        return false;
    }
    
    //Quotations

    private boolean isQuotationMark(char o){
        char[] quote = {'"','\''};
        for (int x = 0; x < quote.length; x++) {
            if(o == quote[x]) return true;
        }
        return false;
    }

    //Space

    private boolean isSpace(char o){
        return o == ' ';
    }
    
    // get Tokens

    public Vector<Token> getTokens() {
        return tokens;
    }

}
