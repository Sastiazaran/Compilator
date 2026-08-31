package compilator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Cross-platform Swing workbench for the compiler.
 */
public class GUI extends JFrame {

    private static final Color BG = new Color(0x0D1117);
    private static final Color PANEL = new Color(0x161B22);
    private static final Color BORDER = new Color(0x30363D);
    private static final Color TEXT = new Color(0xE6EDF3);
    private static final Color MUTED = new Color(0x8B949E);
    private static final Color ACCENT = new Color(0x58A6FF);
    private static final Color ERROR = new Color(0xF85149);
    private static final Color SUCCESS = new Color(0x3FB950);
    private static final Color KEYWORD = new Color(0xFF7B72);
    private static final Color LITERAL = new Color(0xA5D6FF);
    private static final Color IDENT = new Color(0xD2A8FF);
    private static final Color CURRENT_LINE = new Color(0x21262D);

    private final JTextArea editor = new JTextArea();
    private final JTextArea console = new JTextArea();
    private final JLabel statusLabel = new JLabel("Ready");
    private final DefaultTableModel tokenModel = new DefaultTableModel(new Object[] {"Line", "Token", "Word"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel symbolModel = new DefaultTableModel(
            new Object[] {"Name", "Type", "Scope", "Value"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("root"));
    private final JTree parseTree = new JTree(treeModel);
    private Path currentFile;
    private boolean dirty;

    public GUI() {
        setTitle("Compilator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 620));
        setSize(1200, 760);
        setLocationRelativeTo(null);

        Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        configureEditor(mono);
        configureConsole(mono);
        configureTree();

        JTable tokenTable = coloredTable(tokenModel, true);
        JTable symbolTable = coloredTable(symbolModel, false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tokens", wrap(tokenTable));
        tabs.addTab("Parse tree", wrap(parseTree));
        tabs.addTab("Symbol table", wrap(symbolTable));
        tabs.addTab("Console", wrap(console));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel(), tabs);
        split.setResizeWeight(0.46);
        split.setBorder(null);
        split.setDividerSize(8);

        setJMenuBar(buildMenuBar());
        add(toolbar(), BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(statusBar(), BorderLayout.SOUTH);

        loadBuiltinExample("good.cmp");
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.46));
    }

    public static void launch() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Compilator");
        installLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }

    static void installLookAndFeel() {
        try {
            Class<?> laf = Class.forName("com.formdev.flatlaf.FlatDarkLaf");
            UIManager.setLookAndFeel((javax.swing.LookAndFeel) laf.getDeclaredConstructor().newInstance());
        } catch (Exception ignored) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignoredToo) {
                // Keep the platform default.
            }
        }
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 8);
    }

    private void configureEditor(Font mono) {
        editor.setFont(mono);
        editor.setTabSize(4);
        editor.setLineWrap(false);
        editor.setBackground(BG);
        editor.setForeground(TEXT);
        editor.setCaretColor(ACCENT);
        editor.setSelectionColor(new Color(0x264F78));
        editor.setMargin(new java.awt.Insets(8, 10, 8, 8));
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                dirty = true;
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                dirty = true;
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                dirty = true;
            }
        });
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, shortcut), "analyze");
        editor.getActionMap().put("analyze", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyze();
            }
        });
    }

    private void configureConsole(Font mono) {
        console.setFont(mono);
        console.setEditable(false);
        console.setBackground(BG);
        console.setForeground(TEXT);
        console.setCaretColor(ACCENT);
        console.setMargin(new java.awt.Insets(8, 10, 8, 8));
    }

    private void configureTree() {
        parseTree.setRootVisible(true);
        parseTree.setShowsRootHandles(true);
        parseTree.setBackground(BG);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setBackgroundNonSelectionColor(BG);
        renderer.setBackgroundSelectionColor(new Color(0x264F78));
        renderer.setTextNonSelectionColor(TEXT);
        renderer.setTextSelectionColor(Color.WHITE);
        renderer.setBorderSelectionColor(ACCENT);
        parseTree.setCellRenderer(renderer);
    }

    private JPanel editorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));
        JLabel title = new JLabel("  Source");
        title.setForeground(MUTED);
        title.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        LineNumberRuler ruler = new LineNumberRuler(editor, PANEL, MUTED, CURRENT_LINE);
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setRowHeaderView(ruler);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        panel.setBackground(PANEL);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane wrap(JComponent component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        return scroll;
    }

    private JTable coloredTable(DefaultTableModel model, boolean tokens) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setBackground(BG);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0x264F78));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(PANEL);
        table.getTableHeader().setForeground(MUTED);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(BG);
                    Color color = TEXT;
                    if (tokens && column == 1) {
                        String kind = String.valueOf(value);
                        color = switch (kind) {
                            case "ERROR" -> GUI.ERROR;
                            case "KEYWORD" -> GUI.KEYWORD;
                            case "ID" -> GUI.IDENT;
                            case "Integer", "Float", "String", "Octal", "Hexadecimal", "BINARY" -> GUI.LITERAL;
                            default -> GUI.TEXT;
                        };
                    }
                    c.setForeground(color);
                }
                return c;
            }
        });
        return table;
    }

    private JMenuBar buildMenuBar() {
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(item("Open…", KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcut), e -> openFile()));
        file.add(item("Save", KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut), e -> saveFile(false)));
        file.add(item("Save As…", KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcut | InputEvent.SHIFT_DOWN_MASK),
                e -> saveFile(true)));
        file.addSeparator();
        file.add(item("Exit", KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut), e -> System.exit(0)));

        JMenu run = new JMenu("Run");
        run.add(item("Analyze", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, shortcut), e -> analyze()));

        JMenu examples = new JMenu("Examples");
        for (String name : new String[] {
                "good.cmp", "assign.cmp", "if-and.cmp", "if-lt.cmp", "type-mismatch.cmp",
                "undefined.cmp", "redeclare.cmp", "crlf.cmp"
        }) {
            examples.add(item(name, null, e -> loadBuiltinExample(name)));
        }

        bar.add(file);
        bar.add(run);
        bar.add(examples);
        return bar;
    }

    private JMenuItem item(String label, KeyStroke stroke, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(label);
        if (stroke != null) {
            item.setAccelerator(stroke);
        }
        item.addActionListener(listener);
        return item;
    }

    private JPanel toolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        JLabel brand = new JLabel("  Compilator");
        brand.setForeground(ACCENT);
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 16f));
        JButton analyze = new JButton("Analyze");
        analyze.addActionListener(e -> analyze());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setOpaque(false);
        actions.add(analyze);
        bar.add(brand, BorderLayout.WEST);
        bar.add(actions, BorderLayout.EAST);
        return bar;
    }

    private JPanel statusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        statusLabel.setForeground(MUTED);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private void analyze() {
        CompileResult result = Compiler.compile(editor.getText());
        tokenModel.setRowCount(0);
        for (Token token : result.getTokens()) {
            tokenModel.addRow(new Object[] {token.getLine(), token.getToken(), token.getWord()});
        }
        symbolModel.setRowCount(0);
        for (Map.Entry<String, Vector<SymbolTableItem>> entry : result.getSymbolTable().entrySet()) {
            SymbolTableItem item = entry.getValue().isEmpty() ? null : entry.getValue().firstElement();
            if (item != null) {
                symbolModel.addRow(new Object[] {entry.getKey(), item.getType(), item.getScope(), item.getValue()});
            }
        }
        treeModel.setRoot(result.getParseTree());
        treeModel.reload();
        expandAll(parseTree);

        StringBuilder out = new StringBuilder();
        List<String> errors = result.getErrors();
        if (errors.isEmpty()) {
            out.append("OK — no lexer, parser, or semantic errors.\n");
        } else {
            out.append(errors.size()).append(" error(s):\n");
            for (String error : errors) {
                out.append(error);
                if (!error.endsWith("\n")) {
                    out.append('\n');
                }
            }
        }
        console.setText(out.toString());
        console.setCaretPosition(0);
        console.setForeground(errors.isEmpty() ? SUCCESS : ERROR);

        String status = result.getTokens().size() + " tokens · " + errors.size() + " error"
                + (errors.size() == 1 ? "" : "s")
                + (errors.isEmpty() ? " · OK" : "");
        statusLabel.setText(status);
        statusLabel.setForeground(errors.isEmpty() ? SUCCESS : ERROR);
    }

    private void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new javax.swing.tree.TreePath(root));
    }

    private void expandAll(JTree tree, javax.swing.tree.TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                expandAll(tree, parent.pathByAddingChild(n));
            }
        }
        tree.expandPath(parent);
    }

    private void openFile() {
        JFileChooser chooser = chooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            try {
                String text = Files.readString(path, StandardCharsets.UTF_8)
                        .replace("\r\n", "\n")
                        .replace('\r', '\n');
                editor.setText(text);
                editor.setCaretPosition(0);
                currentFile = path;
                dirty = false;
                setTitle("Compilator — " + path.getFileName());
                statusLabel.setText("Opened " + path.getFileName());
                statusLabel.setForeground(MUTED);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not open file:\n" + ex.getMessage(), "Open",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile(boolean saveAs) {
        Path target = currentFile;
        if (saveAs || target == null) {
            JFileChooser chooser = chooser();
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            target = chooser.getSelectedFile().toPath();
            if (!target.getFileName().toString().contains(".")) {
                target = target.resolveSibling(target.getFileName() + ".cmp");
            }
        }
        try {
            Files.writeString(target, editor.getText().replace("\r\n", "\n").replace('\r', '\n'),
                    StandardCharsets.UTF_8);
            currentFile = target;
            dirty = false;
            setTitle("Compilator — " + target.getFileName());
            statusLabel.setText("Saved " + target.getFileName());
            statusLabel.setForeground(MUTED);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save file:\n" + ex.getMessage(), "Save",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JFileChooser chooser() {
        JFileChooser chooser = new JFileChooser(currentFile == null ? Path.of(".").toFile() : currentFile.getParent().toFile());
        chooser.setFileFilter(new FileNameExtensionFilter("Compilator source (*.cmp, *.txt)", "cmp", "txt"));
        return chooser;
    }

    private void loadBuiltinExample(String name) {
        String source = readExample(name);
        if (source == null) {
            JOptionPane.showMessageDialog(this, "Example not found: " + name, "Examples", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editor.setText(source.replace("\r\n", "\n").replace('\r', '\n'));
        editor.setCaretPosition(0);
        currentFile = null;
        dirty = false;
        setTitle("Compilator — " + name);
        analyze();
    }

    static String readExample(String name) {
        try (InputStream in = GUI.class.getResourceAsStream("/examples/" + name)) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
            // Fall through to the working directory.
        }
        Path path = Path.of("examples", name);
        if (Files.isRegularFile(path)) {
            try {
                return Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch();
    }
}
