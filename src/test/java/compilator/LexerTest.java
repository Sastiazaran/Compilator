package compilator;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexerTest {

    private static List<Token> lex(String source) {
        Lexer lexer = new Lexer(source);
        lexer.run();
        return lexer.getTokens();
    }

    private static String kinds(List<Token> tokens) {
        return tokens.stream().map(t -> t.getToken() + ":" + t.getWord()).collect(Collectors.joining(" "));
    }

    @Test
    void keywordsAndIdentifier() {
        List<Token> tokens = lex("int x");
        assertEquals("KEYWORD:int ID:x", kinds(tokens));
    }

    @Test
    void integerOctalHexBinaryFloat() {
        List<Token> tokens = lex("10 0xFF 0b10 1.5");
        assertTrue(tokens.stream().anyMatch(t -> "Integer".equals(t.getToken()) && "10".equals(t.getWord())));
        assertTrue(tokens.stream().anyMatch(t -> "Hexadecimal".equals(t.getToken())));
        assertTrue(tokens.stream().anyMatch(t -> "BINARY".equals(t.getToken())));
        assertTrue(tokens.stream().anyMatch(t -> "Float".equals(t.getToken()) && "1.5".equals(t.getWord())));
    }

    @Test
    void stringLiteral() {
        List<Token> tokens = lex("\"hello\"");
        assertEquals(1, tokens.size());
        assertEquals("String", tokens.get(0).getToken());
        assertEquals("\"hello\"", tokens.get(0).getWord());
    }

    @Test
    void twoCharOperatorsIncludingAndOr() {
        List<Token> tokens = lex("&& || == != <= >=");
        List<String> words = tokens.stream().map(Token::getWord).toList();
        assertEquals(List.of("&&", "||", "==", "!=", "<=", ">="), words);
        assertTrue(tokens.stream().allMatch(t -> "OPERATOR".equals(t.getToken())));
    }

    @Test
    void moduloIsAnOperator() {
        List<Token> tokens = lex("10 % 3");
        assertEquals("Integer:10 OPERATOR:% Integer:3", kinds(tokens));
    }

    @Test
    void tabsAreWhitespace() {
        List<Token> tokens = lex("int\tx\t=\t1;");
        assertEquals("KEYWORD:int ID:x OPERATOR:= Integer:1 DELIMITER:;", kinds(tokens));
    }

    @Test
    void unixAndWindowsAndClassicMacLineEndingsShareLineNumbers() {
        List<Token> unix = lex("{\nint x = 0;\n}");
        List<Token> windows = lex("{\r\nint x = 0;\r\n}");
        List<Token> classic = lex("{\rint x = 0;\r}");
        assertEquals(kinds(unix), kinds(windows));
        assertEquals(kinds(unix), kinds(classic));
        Token intTokUnix = unix.stream().filter(t -> "int".equals(t.getWord())).findFirst().orElseThrow();
        Token intTokWin = windows.stream().filter(t -> "int".equals(t.getWord())).findFirst().orElseThrow();
        Token intTokMac = classic.stream().filter(t -> "int".equals(t.getWord())).findFirst().orElseThrow();
        assertEquals(2, intTokUnix.getLine());
        assertEquals(2, intTokWin.getLine());
        assertEquals(2, intTokMac.getLine());
    }

    @Test
    void emptyInputYieldsNoTokens() {
        assertTrue(lex("").isEmpty());
        assertTrue(lex(null).isEmpty());
    }
}
