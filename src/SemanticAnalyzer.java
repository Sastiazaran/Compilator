import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {
    private static Hashtable<String, Vector<SymbolTableItem>> symbolTable;
    public static ErrorController errorController = new ErrorController("Semantic");
    private static final Stack<Token> stack = new Stack<Token>();

    // Constants representing operations
    public static final int OP_plus = 0;
    public static final int OP_minus = 1;
    public static final int OP_Mult = 2;
    public static final int OP_Division = 3;
    public static final int OP_And = 4;
    public static final int OP_Or = 5;
    public static final int OP_Not = 6;
    public static final int OP_Minor = 7;
    public static final int OP_Greater = 8;
    public static final int OP_Equal = 9;
    public static final int OP_LorEqual = 10;
    public static final int OP_GorEqual = 11;
    public static final int OP_Different = 12;
    public static final int OP_Reminder = 13;
    public static final int OP_Assignation = 14;

    // Constants representing data types
    public static final int Integer = 0;
    public static final int Float = 1;
    public static final int Char = 2;
    public static final int String = 3;
    public static final int Boolean = 4;
    public static final int Void = 5;
    public static final int Error = 6;

    // Type compatibility cube
    private static int cube[][][] = {
            // Sum 0
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, String, Error, Error },
                    { Error, Error, String, String, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Minus 1
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Multiplication 2
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Division 3
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // And 4
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Or 5
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Not 6
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Minor 7
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Greater 8
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Equal 9
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Lor Equal 10
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Gor Equal 11
            {
                    { Boolean, Error, Error, Error, Error, Error },
                    { Error, Boolean, Error, Error, Error, Error },
                    { Error, Error, Boolean, Error, Error, Error },
                    { Error, Error, Error, Boolean, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Different 12
            {
                    { Boolean, Error, Error, Error, Error, Error },
                    { Error, Boolean, Error, Error, Error, Error },
                    { Error, Error, Boolean, Error, Error, Error },
                    { Error, Error, Error, Boolean, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Reminder 13
            {
                    { Integer, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Assignment 14
            {
                    { Integer, Error, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Char, Error, Char, Error, Error, Error },
                    { Error, Error, String, String, Error, Error },
                    { Boolean, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            }
    };

    private Vector<Token> tokens;

    public SemanticAnalyzer(Vector<Token> tokens) {
        this.tokens = tokens;
    }

    public void run() {
        for (Token token : tokens) {
            String tokenType = token.getToken();
            String tokenWord = token.getWord();
            switch (tokenType) {
                case "VARIABLE_DECLARATION":
                    // Assuming tokenWord is in format "type id" for declaration
                    String[] parts = tokenWord.split(" ");
                    if (parts.length == 2) {
                        String type = parts[0];
                        String id = parts[1];
                        if (!CheckVariable(id)) {
                            AddVariable(type, id);
                        } else {
                            errorController.storeError("Error: Variable '" + id + "' is already defined.");
                        }
                    } else {
                        errorController.storeError("Error: Invalid variable declaration '" + tokenWord + "'.");
                    }
                    break;
                case "ASSIGNMENT":
                    // Assuming tokenWord is in format "id = value"
                    String[] assignParts = tokenWord.split("=");
                    if (assignParts.length == 2) {
                        String id = assignParts[0].trim();
                        String value = assignParts[1].trim();
                        if (CheckVariable(id)) {
                            // Perform type checking here if necessary
                            AddVariable(getVariableType(id), id, value);
                        } else {
                            errorController.storeError("Error: Variable '" + id + "' is not defined.");
                        }
                    } else {
                        errorController.storeError("Error: Invalid assignment '" + tokenWord + "'.");
                    }
                    break;
                case "OPERATION":
                    // Assuming tokenWord is in format "operand1 operator operand2"
                    String[] opParts = tokenWord.split(" ");
                    if (opParts.length == 3) {
                        String operand1 = opParts[0];
                        String operator = opParts[1];
                        String operand2 = opParts[2];
                        String type1 = getVariableType(operand1);
                        String type2 = getVariableType(operand2);
                        int op = getOperatorCode(operator);
                        String resultType = checkOperationBinary(type1, type2, op);
                        if (resultType.equals("error")) {
                            errorController.storeError("Error: Invalid operation '" + tokenWord + "'.");
                        }
                    } else {
                        errorController.storeError("Error: Invalid operation '" + tokenWord + "'.");
                    }
                    break;
                default:
                    errorController.storeError("Error: Unknown token type '" + tokenType + "'.");
                    break;
            }
        }
    }

    /**
     * Returns the operator code for a given operator string.
     */
    private int getOperatorCode(String operator) {
        switch (operator) {
            case "+":
                return OP_plus;
            case "-":
                return OP_minus;
            case "*":
                return OP_Mult;
            case "/":
                return OP_Division;
            case "&&":
                return OP_And;
            case "||":
                return OP_Or;
            case "!":
                return OP_Not;
            case "<":
                return OP_Minor;
            case ">":
                return OP_Greater;
            case "==":
                return OP_Equal;
            case "<=":
                return OP_LorEqual;
            case ">=":
                return OP_GorEqual;
            case "!=":
                return OP_Different;
            case "%":
                return OP_Reminder;
            case "=":
                return OP_Assignation;
            default:
                return -1; // Use -1 for invalid operators
        }
    }

    /**
     * Adds a variable to the symbol table with a specific scope, type, id, and
     * value.
     * If the variable already exists in the given scope, an error is recorded.
     */
    public static void AddVariable(String scope, String type, String id, String value) {
        if (CheckVariable(scope, id)) {
            errorController.storeError("Error: Variable '" + id + "' is already defined");
            return;
        }
        Vector<SymbolTableItem> items = symbolTable.get(id);
        if (items.equals(null)) {
            Vector<SymbolTableItem> newItemVector = new Vector<>();
            symbolTable.put(id, newItemVector);
            items = symbolTable.get(id);
        }
        items.add(new SymbolTableItem(type, scope, value));
    }

    /**
     * Adds a variable to the symbol table with a specific scope, type, and id.
     * The default value is an empty string.
     */
    public static void AddVariable(String scope, String type, String id) {
        AddVariable(scope, type, id, "");
    }

    /**
     * Adds a global variable to the symbol table with a specific type and id.
     * The default value is an empty string.
     */
    public static void AddVariable(String type, String id) {
        AddVariable("global", type, id, "");
    }

    /**
     * Checks if a variable exists in the symbol table with the given scope and id.
     */
    public static boolean CheckVariable(String type, String id) {
        Vector<SymbolTableItem> items = symbolTable.get(id);
        if (items.equals(null)) {
            return false;
        } else {
            for (SymbolTableItem item : items) {
                if (item.getScope().equals(type)) { // Use equals for string comparison
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks if a variable exists in the symbol table with the given id.
     */
    public static boolean CheckVariable(String id) {
        Vector<SymbolTableItem> items = symbolTable.get(id);
        return items != null; // Simplified return statement
    }

    /**
     * Retrieves the type of a variable from the symbol table based on its id.
     */
    public static String getVariableType(String id) {
        Vector<SymbolTableItem> items = symbolTable.get(id);
        if (items.equals(null))
            return "";
        return items.get(0).getType();
    }

    // Uncomment and implement these methods if function support is needed
    // public static boolean CheckFunction(String id) {
    // return true;
    // }
    // public static void addFunction(String id) {

    // }

    /**
     * Retrieves the stack used for tokens.
     */
    public static Stack<Token> getStack() {
        return stack;
    }

    /**
     * Clears all variables from the symbol table.
     */
    public static void clearVariables() {
        symbolTable.clear();
    }

    /**
     * Converts a string representation of a data type to its corresponding integer
     * constant.
     */
    private static int convertToDT(String dataType) {
        switch (dataType) {
            case "Integer":
            case "int":
                return Integer;
            case "Float":
            case "float":
                return Float;
            case "Boolean":
            case "bool":
                return Boolean;
            case "Char":
            case "char":
                return Char;
            case "String":
            case "string":
                return String;
            case "Void":
            case "void":
                return Void;
            default:
                return -1; // Use default for invalid types
        }
    }

    /**
     * Converts an integer constant representing a data type to its corresponding
     * string representation.
     */
    private static String convertDTToString(int dataType) {
        switch (dataType) {
            case Integer:
                return "int";
            case Float:
                return "float";
            case Boolean:
                return "bool";
            case Char:
                return "char";
            case String:
                return "string";
            case Void:
                return "void";
            case Error:
                return "error";
            default:
                return "error"; // Use default for invalid data types
        }
    }

    /**
     * Checks the result type of a binary operation between two data types.
     */
    private static int checkOperationB(String type1, String type2, int op) {
        int t1 = convertToDT(type1);
        int t2 = convertToDT(type2);

        if (t1 == -1 || t2 == -1) {
            System.err.println("Invalid conversion: " + type1 + " to " + t1 + ", " + type2 + " to " + t2);
            return Error;
        }
        if (op < OP_plus || op > OP_Assignation) {
            System.err.println("Invalid operation code: " + op);
        }
        try {
            return cube[op][t2][t1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: op = " + op + ", t1 = " + t1 + ", t2 = " + t2);
            throw e;
        }
    }

    /**
     * Checks the result type of a binary operation between two data types and
     * returns it as a string.
     */
    public static String checkOperationBinary(String type1, String type2, int op) {
        return convertDTToString(checkOperationB(type1, type2, op));
    }


}
