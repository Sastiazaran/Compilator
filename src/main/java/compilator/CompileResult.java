package compilator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Immutable snapshot of one compile run.
 */
public final class CompileResult {

    private final List<Token> tokens;
    private final DefaultMutableTreeNode parseTree;
    private final List<String> errors;
    private final Map<String, Vector<SymbolTableItem>> symbolTable;

    public CompileResult(List<Token> tokens, DefaultMutableTreeNode parseTree, List<String> errors,
            Map<String, Vector<SymbolTableItem>> symbolTable) {
        this.tokens = tokens == null ? List.of() : List.copyOf(tokens);
        this.parseTree = parseTree == null ? new DefaultMutableTreeNode("root") : parseTree;
        this.errors = errors == null ? List.of() : List.copyOf(errors);
        this.symbolTable = symbolTable == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(symbolTable));
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public DefaultMutableTreeNode getParseTree() {
        return parseTree;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Map<String, Vector<SymbolTableItem>> getSymbolTable() {
        return symbolTable;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
