import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Vector;

class LineNumberTextArea extends JTextPane {
    private JTextArea textArea;

    public LineNumberTextArea(JTextArea textArea) {
        this.textArea = textArea;
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });
        updateLineNumbers();
    }

    private void updateLineNumbers() {
        StringBuilder lineNumbers = new StringBuilder();
        int lines = textArea.getLineCount();
        for (int i = 1; i <= lines; i++) {
            lineNumbers.append(i).append(System.lineSeparator());
        }
        setText(lineNumbers.toString());
    }
}

class GUI extends JFrame {

    private JTextArea wordArea;
    private JButton processButton;
    private JTable tokensTable;
    private DefaultTableModel tableModel;
    private JTextArea consoleTextArea;
    private JTree parserTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    public GUI() {
        // Configuración de la ventana principal
        setTitle("Lexer App");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear componentes
        wordArea = new JTextArea(10, 30);
        processButton = new JButton("Process");
        tableModel = new DefaultTableModel();
        tokensTable = new JTable(tableModel);
        consoleTextArea = new JTextArea(10, 30);

        // Configurar la tabla
        tableModel.addColumn("Line");
        tableModel.addColumn("Token");
        tableModel.addColumn("Word");

        // Crear panel para la entrada de texto y botón
        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new BorderLayout());
        wordPanel.add(new JLabel("Words:"), BorderLayout.NORTH);

        // Configurar wordArea con números de línea
        LineNumberTextArea lineNumberTextArea = new LineNumberTextArea(wordArea);
        JScrollPane wordScrollPane = new JScrollPane(wordArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wordScrollPane.setRowHeaderView(lineNumberTextArea);
        wordPanel.add(wordScrollPane, BorderLayout.CENTER);
        wordPanel.add(processButton, BorderLayout.SOUTH);

        // Crear paneles para las pestañas
        JPanel tablePanel = new JPanel();
        JPanel consolePanel = new JPanel();
        JPanel parserPanel = new JPanel(); // Nuevo panel para la pestaña "Parser"
        JPanel treePanel = new JPanel();

        // Configurar tablePanel
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(new JScrollPane(tokensTable), BorderLayout.CENTER);

        // Configurar consolePanel
        consolePanel.setLayout(new BorderLayout());
        consolePanel.add(new JScrollPane(consoleTextArea), BorderLayout.CENTER);

        // Configurar parserPanel
        parserPanel.setLayout(new BorderLayout());
        parserPanel.add(new JLabel("Parser Output"), BorderLayout.NORTH);
        JTextArea parserTextArea = new JTextArea(10, 30);
        parserPanel.add(new JScrollPane(parserTextArea), BorderLayout.CENTER);

        // Configurar treePanel
        treePanel.setLayout(new BorderLayout());
        treePanel.add(new JLabel("Tree:"), BorderLayout.NORTH);
        rootNode = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);
        parserTree = new JTree(treeModel);
        treePanel.add(new JScrollPane(parserTree), BorderLayout.CENTER);

        // Crear JTabbedPane y agregar los paneles
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Lexer", tablePanel);
        tabbedPane.addTab("Console", consolePanel);
        tabbedPane.addTab("Parser", parserPanel);
        tabbedPane.addTab("Tree", treePanel);

        // Crear el panel principal y agregar componentes
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(wordPanel, BorderLayout.WEST); // Siempre visible a la izquierda
        mainPanel.add(tabbedPane, BorderLayout.CENTER); // Pestañas a la derecha

        // Agregar el panel principal a la ventana
        add(mainPanel);

        // Configurar el evento del botón
        processButton.addActionListener(e -> processInput(parserTextArea));
    }

    private void processInput(JTextArea parserTextArea) {
        String words = wordArea.getText();
        Lexer lex = new Lexer(words);
        lex.run();
        Vector<Token> tokens = lex.getTokens();

        // Limpiar la tabla antes de agregar nuevas filas
        tableModel.setRowCount(0);

        // Agregar filas a la tabla
        for (Token token : tokens) {
            int line = token.getLine();
            String tokenType = token.getToken();
            String word = token.getWord();
            tableModel.addRow(new Object[]{line, tokenType, word});
        }

        // Mostrar información en la consola
        StringBuilder consoleOutput = new StringBuilder();
        consoleOutput.append("Processed input:\n").append(words).append("\n");
        consoleOutput.append("Tokens:\n");
        for (Token token : tokens) {
            consoleOutput.append(token.getLine()).append(" ").append(token.getToken()).append(" ").append(token.getWord()).append("\n");
        }

        Parser parser = new Parser(tokens);
        DefaultMutableTreeNode rootNode = parser.parse();
        treeModel.setRoot(rootNode);
        treeModel.reload();

        ErrorController errorController = Parser.errorController;

        for (String error : errorController.getErrors()) {
            consoleOutput.append(error).append("\n");
        }

        consoleOutput.append("----------------------------\n");
        consoleTextArea.append(consoleOutput.toString());

        // Simular procesamiento del parser (puedes reemplazar esto con la lógica real de tu parser)
        StringBuilder parserOutput = new StringBuilder();
        parserOutput.append("Parser Output:\n");
        for (Token token : tokens) {
            parserOutput.append("Processed token: ").append(token.getWord()).append("\n");
        }
        parserTextArea.setText(parserOutput.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
