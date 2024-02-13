import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

class GUI extends JFrame {

    private JTextField wordField;
    private JButton processButton;
    private JTable tokensTable;
    private DefaultTableModel tableModel;
    private JTextArea consoleTextArea;

    public GUI() {
        // Configuración de la ventana principal
        setTitle("Lexer App");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear componentes
        wordField = new JTextField(60);
        processButton = new JButton("Process");
        tableModel = new DefaultTableModel();
        tokensTable = new JTable(tableModel);
        consoleTextArea = new JTextArea(5, 80);

        // Configurar la tabla
        tableModel.addColumn("Line");
        tableModel.addColumn("Token");
        tableModel.addColumn("Word");

        // Configurar el diseño de la interfaz
        setLayout(new BorderLayout());
        
        // Agregar JScrollPane a inputPanel
        JScrollPane inputScrollPane = new JScrollPane();
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Word: "));
        inputPanel.add(wordField);
        inputPanel.add(processButton);
        inputScrollPane.setViewportView(inputPanel);
        add(inputScrollPane, BorderLayout.NORTH);

        // Agregar JScrollPane a la tabla
        JScrollPane tokensScrollPane = new JScrollPane(tokensTable);
        add(tokensScrollPane, BorderLayout.CENTER);

        // Agregar JScrollPane a la consola
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        add(consoleScrollPane, BorderLayout.SOUTH);

        // Configurar el evento del botón
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });
    }

    private void processInput() {
        String words = wordField.getText();
        Lexer lex = new Lexer(words);
        lex.run();
        Vector<Token> tokens = lex.getTokens();

        // Limpiar la tabla antes de agregar nuevas filas
        tableModel.setRowCount(0);

        // Agregar filas a la tabla
        for (Token token1 : tokens) {
            int line = token1.getLine();
            String token = token1.getToken();
            String word = token1.getWord();
            tableModel.addRow(new Object[]{line, token, word});
        }

        // Mostrar información en la consola
        consoleTextArea.append("Processed input:\n" + words + "\n");
        consoleTextArea.append("Tokens:\n");
        for (Token token : tokens) {
            consoleTextArea.append(token.getLine() + " " + token.getToken() + " " + token.getWord() + "\n");
        }
        consoleTextArea.append("----------------------------\n");
    }

}
