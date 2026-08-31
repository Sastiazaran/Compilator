package compilator;

import java.awt.GraphicsEnvironment;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Desktop GUI when launched with no arguments; headless CLI when given a file.
 *
 * <pre>
 *   java -jar compilator-1.0.0-all.jar              # GUI
 *   java -jar compilator-1.0.0-all.jar file.cmp     # CLI
 * </pre>
 */
public class App {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && !"--gui".equals(args[0])) {
            int code = runCli(Path.of(args[0]));
            System.exit(code);
            return;
        }
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("No display available. Usage: java -jar compilator.jar <file.cmp>");
            System.exit(1);
            return;
        }
        GUI.launch();
    }

    static int runCli(Path file) throws Exception {
        if (!Files.isRegularFile(file)) {
            System.err.println("File not found: " + file);
            return 2;
        }
        String source = Files.readString(file, StandardCharsets.UTF_8);
        CompileResult result = Compiler.compile(source);

        System.out.println("== Tokens ==");
        for (Token token : result.getTokens()) {
            System.out.println(token);
        }

        System.out.println("\n== Parse tree ==");
        printTree(result.getParseTree(), 0);

        System.out.println("\n== Symbol table ==");
        if (result.getSymbolTable().isEmpty()) {
            System.out.println("(empty)");
        } else {
            for (Map.Entry<String, Vector<SymbolTableItem>> entry : result.getSymbolTable().entrySet()) {
                SymbolTableItem item = entry.getValue().isEmpty() ? null : entry.getValue().firstElement();
                if (item != null) {
                    System.out.println(entry.getKey() + "\t" + item.getType() + "\t" + item.getScope() + "\t"
                            + item.getValue());
                }
            }
        }

        System.out.println("\n== Diagnostics ==");
        if (result.getErrors().isEmpty()) {
            System.out.println("OK");
            return 0;
        }
        for (String error : result.getErrors()) {
            System.out.print(error.endsWith("\n") ? error : error + "\n");
        }
        return 1;
    }

    private static void printTree(TreeNode node, int depth) {
        if (node == null) {
            return;
        }
        System.out.println("  ".repeat(depth) + node);
        if (node instanceof DefaultMutableTreeNode treeNode) {
            for (int i = 0; i < treeNode.getChildCount(); i++) {
                printTree(treeNode.getChildAt(i), depth + 1);
            }
        }
    }
}
