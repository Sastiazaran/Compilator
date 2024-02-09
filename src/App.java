import java.util.Vector;

public class App {
    public static void main(String[] args) throws Exception {
        

        String words = "";
        Lexer lex = new Lexer(words);

        lex.run();

        Vector<Token> tokens = lex.getTokens();

        for (Token token1 : tokens) {
            int line = token1.getLine();
            String token  =token1.getToken();
            String word = token1.getWord();
           // writeTokensTable(line, token, word);
           System.out.println(line + " " + token + " " + word);
        //    System.out.println(token + " ");
        //    System.out.println(word + " ");
        }

        int errors = 0;
        for(Token token : tokens) {
            if (token.getToken().equals("ERROR")) {
                errors++;
            }
        }
        

        


    }
}
