package compilator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

/**
 * Type checker and symbol table. Types are stored as {@code int}, {@code float},
 * {@code string}, {@code boolean}, {@code char}, {@code void}, or {@code error}.
 */
public class SemanticAnalyzer {

    private static final Map<String, Vector<SymbolTableItem>> symbolTable = new LinkedHashMap<>();
    private static final Stack<String> stack = new Stack<>();
    private static ErrorController errorController;

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

    public static final int Integer = 0;
    public static final int Float = 1;
    public static final int Char = 2;
    public static final int String = 3;
    public static final int Boolean = 4;
    public static final int Void = 5;
    public static final int Error = 6;

    /**
     * cube[op][leftType][rightType] — all numeric results use {@code int}, never {@code integer}.
     */
    private static final String[][][] cube = {
            // +
            {
                    {"int", "float", "error", "string", "error", "error"},
                    {"float", "float", "error", "string", "error", "error"},
                    {"error", "error", "error", "string", "error", "error"},
                    {"string", "string", "string", "string", "string", "error"},
                    {"error", "error", "error", "string", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // -
            {
                    {"int", "float", "error", "error", "error", "error"},
                    {"float", "float", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // *
            {
                    {"int", "float", "error", "error", "error", "error"},
                    {"float", "float", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // /
            {
                    {"int", "float", "error", "error", "error", "error"},
                    {"float", "float", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // & / &&
            {
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "boolean", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // | / ||
            {
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "boolean", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // unary !
            {
                    {"error", "error", "error", "error", "boolean", "error"}
            },
            // <
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // >
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // ==
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "boolean", "error", "error", "error"},
                    {"error", "error", "error", "boolean", "error", "error"},
                    {"error", "error", "error", "error", "boolean", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // <=
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // >=
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // !=
            {
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"boolean", "boolean", "error", "error", "error", "error"},
                    {"error", "error", "boolean", "error", "error", "error"},
                    {"error", "error", "error", "boolean", "error", "error"},
                    {"error", "error", "error", "error", "boolean", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // %
            {
                    {"int", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            },
            // =
            {
                    {"OK", "error", "error", "error", "error", "error"},
                    {"error", "OK", "error", "error", "error", "error"},
                    {"error", "error", "OK", "error", "error", "error"},
                    {"error", "error", "error", "OK", "error", "error"},
                    {"error", "error", "error", "error", "OK", "error"},
                    {"error", "error", "error", "error", "error", "error"}
            }
    };

    private SemanticAnalyzer() {
    }

    public static void setErrorController(ErrorController controller) {
        errorController = controller;
    }

    public static Map<String, Vector<SymbolTableItem>> getSymbolTable() {
        return symbolTable;
    }

    public static void CheckVariable(String type, String id, int lineNo) {
        if (!symbolTable.containsKey(id)) {
            Vector<SymbolTableItem> v = new Vector<>();
            switch (type) {
                case "string" -> v.add(new SymbolTableItem(type, "global", ""));
                case "void" -> v.add(new SymbolTableItem(type, "global", ""));
                case "int" -> v.add(new SymbolTableItem(type, "global", "0"));
                case "float" -> v.add(new SymbolTableItem(type, "global", "0.0"));
                case "char" -> v.add(new SymbolTableItem(type, "global", "''"));
                case "boolean" -> v.add(new SymbolTableItem(type, "global", "false"));
                default -> v.add(new SymbolTableItem(type, "global", ""));
            }
            symbolTable.put(id, v);
        } else {
            error(1, lineNo, id);
        }
    }

    public static void pushStack(String type) {
        stack.push(type == null ? "error" : type);
    }

    public static String popStack() {
        if (stack.isEmpty()) {
            return "";
        }
        return stack.pop();
    }

    public static String calculateCube(String type, String operator) {
        int typeIndex = typeIndex(type);
        if (typeIndex < 0 || typeIndex > Void) {
            return "error";
        }
        int opIndex;
        if ("-".equals(operator)) {
            opIndex = OP_minus;
        } else if ("!".equals(operator)) {
            opIndex = OP_Not;
        } else {
            return "error";
        }
        if (opIndex >= cube.length || cube[opIndex].length == 0 || typeIndex >= cube[opIndex][0].length) {
            return "error";
        }
        return cube[opIndex][0][typeIndex];
    }

    public static String calculateCube(String type1, String type2, String operator) {
        int dim2 = typeIndex(type1);
        int dim3 = typeIndex(type2);
        int dim1 = operatorIndex(operator);
        if (dim1 < 0 || dim2 < 0 || dim3 < 0 || dim2 > Void || dim3 > Void) {
            return "error";
        }
        if (dim1 >= cube.length || dim2 >= cube[dim1].length || dim3 >= cube[dim1][dim2].length) {
            return "error";
        }
        return cube[dim1][dim2][dim3];
    }

    private static int typeIndex(String type) {
        if (type == null) {
            return Error;
        }
        return switch (type) {
            case "int", "integer" -> Integer;
            case "float" -> Float;
            case "char" -> Char;
            case "string" -> String;
            case "boolean" -> Boolean;
            case "void" -> Void;
            default -> Error;
        };
    }

    private static int operatorIndex(String operator) {
        if (operator == null) {
            return -1;
        }
        return switch (operator) {
            case "-" -> OP_minus;
            case "+" -> OP_plus;
            case "*" -> OP_Mult;
            case "/" -> OP_Division;
            case "&", "&&" -> OP_And;
            case "|", "||" -> OP_Or;
            case "!" -> OP_Not;
            case "<" -> OP_Minor;
            case ">" -> OP_Greater;
            case "==" -> OP_Equal;
            case "<=" -> OP_LorEqual;
            case ">=" -> OP_GorEqual;
            case "%" -> OP_Reminder;
            case "!=" -> OP_Different;
            case "=" -> OP_Assignation;
            default -> -1;
        };
    }

    public static void error(int error, int n, String info) {
        String message = switch (error) {
            case 0 -> "Line " + n + ": [Semantic] variable " + info + " not found";
            case 1 -> "Line " + n + ": [Semantic] variable " + info + " is already defined";
            case 2 -> "Line " + n + ": [Semantic] incompatible types: type mismatch";
            case 3 -> "Line " + n + ": [Semantic] incompatible types: expected a boolean";
            case 4 -> "Line " + n + ": [Semantic] incompatible types: expected int/oct/hex/bin";
            default -> "Line " + n + ": [Semantic] error";
        };
        if (errorController != null) {
            errorController.storeError(message);
        } else {
            System.err.println(message);
        }
    }

    public static String getIdType(String id, int lineNo) {
        Vector<SymbolTableItem> items = symbolTable.get(id);
        if (items == null || items.isEmpty()) {
            error(0, lineNo, id);
            return "error";
        }
        return items.firstElement().getType();
    }

    public static void clearVariables() {
        symbolTable.clear();
        stack.clear();
    }
}
