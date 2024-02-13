import javax.swing.*;
/**
 * @author Sebastian Astiazaran
 * @author Pablo Uscanga
 * @author Rolando Palacios
 */

public class App{
    public static void main(String[] args) throws Exception {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI appGUI = new GUI();
                appGUI.setVisible(true);
            }
        });

    }
}

// private JTextField wordField;
// private JButton processButton;
// private JTable tokensTable;
// private DefaultTableModel tableModel;

// public App() {
//     // Configuración de la ventana principal
//     setTitle("Lexer App");
//     setSize(500, 300);
//     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     setLocationRelativeTo(null);

//     // Crear componentes
//     wordField = new JTextField(20);
//     processButton = new JButton("Process");
//     tableModel = new DefaultTableModel();
//     tokensTable = new JTable(tableModel);

//     // Configurar la tabla
//     tableModel.addColumn("Line");
//     tableModel.addColumn("Token");
//     tableModel.addColumn("Word");

//     // Configurar el diseño de la interfaz
//     setLayout(new BorderLayout());
//     JPanel inputPanel = new JPanel();
//     inputPanel.add(new JLabel("Word: "));
//     inputPanel.add(wordField);
//     inputPanel.add(processButton);

//     add(inputPanel, BorderLayout.NORTH);
//     add(new JScrollPane(tokensTable), BorderLayout.CENTER);

//     // Configurar el evento del botón
//     processButton.addActionListener(new ActionListener() {
//         @Override
//         public void actionPerformed(ActionEvent e) {
//             processInput();
//         }
//     });
// }

// private void processInput() {
//     String words = wordField.getText();
//     Lexer lex = new Lexer(words);
//     lex.run();
//     Vector<Token> tokens = lex.getTokens();

//     // Limpiar la tabla antes de agregar nuevas filas
//     tableModel.setRowCount(0);

//     // Agregar filas a la tabla
//     for (Token token1 : tokens) {
//         int line = token1.getLine();
//         String token = token1.getToken();
//         String word = token1.getWord();
//         tableModel.addRow(new Object[]{line, token, word});
//     }
// }

// String words = "boolean $xx= ((((((((23WE + 44 - 3 / 2 % 45 <=17) > 0xffffff.34.45;";
        // Lexer lex = new Lexer(words);

        // lex.run();

        // Vector<Token> tokens = lex.getTokens();

        // for (Token token1 : tokens) {
        //     int line = token1.getLine();
        //     String token  =token1.getToken();
        //     String word = token1.getWord();
        //    // writeTokensTable(line, token, word);
        //    System.out.println(line + " " + token + " " + word);
        // //    System.out.println(token + " ");
        // //    System.out.println(word + " ");
        // }

        // int errors = 0;
        // for(Token token : tokens) {
        //     if (token.getToken().equals("ERROR")) {
        //         errors++;
        //     }
        // }
