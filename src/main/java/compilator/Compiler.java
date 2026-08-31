package compilator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Shared compile pipeline used by the GUI and the headless CLI.
 */
public final class Compiler {

    private Compiler() {
    }

    /**
     * Lex, parse, and type-check {@code source}. Newlines are normalized to {@code \n}
     * so Windows, macOS, and Linux files behave the same.
     */
    public static CompileResult compile(String source) {
        String normalized = source == null ? "" : source.replace("\r\n", "\n").replace('\r', '\n');
        ErrorController errors = new ErrorController("COMPILER");

        Lexer lexer = new Lexer(normalized);
        lexer.run();
        Vector<Token> tokens = lexer.getTokens();

        for (Token token : tokens) {
            if ("ERROR".equals(token.getToken())) {
                errors.storeError("Line " + token.getLine() + ": [Lexical] unexpected '" + token.getWord() + "'");
            }
        }

        SemanticAnalyzer.setErrorController(errors);
        try {
            Parser parser = new Parser(tokens, errors);
            DefaultMutableTreeNode tree = parser.parse();
            Map<String, Vector<SymbolTableItem>> symbols = copySymbols(SemanticAnalyzer.getSymbolTable());
            return new CompileResult(tokens, tree, errors.getErrors(), symbols);
        } finally {
            SemanticAnalyzer.setErrorController(null);
        }
    }

    private static Map<String, Vector<SymbolTableItem>> copySymbols(Map<String, Vector<SymbolTableItem>> source) {
        Map<String, Vector<SymbolTableItem>> copy = new LinkedHashMap<>();
        if (source == null) {
            return copy;
        }
        source.forEach((name, items) -> {
            Vector<SymbolTableItem> cloned = new Vector<>();
            if (items != null) {
                for (SymbolTableItem item : items) {
                    cloned.add(new SymbolTableItem(item.getType(), item.getScope(), item.getValue()));
                }
            }
            copy.put(name, cloned);
        });
        return copy;
    }
}
