import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {
    public static Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<String, Vector<SymbolTableItem>>();
    @SuppressWarnings("rawtypes")
    private static final Stack stack = new Stack();


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
    private static String cube[][][] = {
            // Sum 0
            {
                { "integer", "float", "error", "string", "error", "error" },
                { "float", "float", "error", "string", "error", "error" },
                { "error", "error", "error", "string", "error", "error" },
                { "string", "string", "string", "string", "string", "error" },
                { "error", "error", "error", "string", "boolean", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // minus 1
            {
                { "integer", "float", "error", "error", "error", "error" },
                { "float", "float", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // multiplication 2
            {
                { "integer", "float", "error", "error", "error", "error" },
                { "float", "float", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // division 3
            {
                { "integer", "float", "error", "error", "error", "error" },
                { "float", "float", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // and 4
            {
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // or 5
            {
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // not 6
            {
                { "error", "error", "error", "error", "boolean", "error" },
            },
            // minor 7
            {
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // greater 8
            {
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // equal 9
            {
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // lor equal 10
            {
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "boolean", "boolean", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // gor equal 11
            {
                { "boolean", "error", "error", "error", "error", "error" },
                { "error", "boolean", "error", "error", "error", "error" },
                { "error", "error", "boolean", "error", "error", "error" },
                { "error", "error", "error", "boolean", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // different 12
            {
                { "boolean", "error", "error", "error", "error", "error" },
                { "error", "boolean", "error", "error", "error", "error" },
                { "error", "error", "boolean", "error", "error", "error" },
                { "error", "error", "error", "boolean", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // reminder 13
            {
                { "integer", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" },
                { "error", "error", "error", "error", "error", "error" }
            },
            // assignment 14
            {
                { "integer", "error", "error", "error", "error", "error" },
                { "float", "float", "error", "error", "error", "error" },
                { "error", "error", "char", "error", "error", "error" },
                { "error", "error", "error", "string", "error", "error" },
                { "error", "error", "error", "error", "boolean", "error" },
                { "error", "error", "error", "error", "error", "error" }
            }
            
            
    };

    public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable() {
        return symbolTable;
    }

    @SuppressWarnings("unchecked")
    public static void CheckVariable(String type,  String id, int lineNo){
        if(!symbolTable.containsKey(id)){
            @SuppressWarnings("rawtypes")
            Vector v = new Vector<>();
            switch (type){
                case "String":
                    v.add(new SymbolTableItem(type, "global", ""));
                    break;
                case "void":
                    v.add(new SymbolTableItem(type, "global", ""));
                    break;
                case "int":
                    v.add(new SymbolTableItem(type, "global", "0"));
                    break;
                case "float":
                    v.add(new SymbolTableItem(type, "global", "0.0"));
                    break;
                case "char":
                    v.add(new SymbolTableItem(type, "global", "''"));
                    break;
                case "boolean":
                    v.add(new SymbolTableItem(type, "global", "false"));
                    break;
            }
            symbolTable.put(id, v);
        }else{
            //ERRORR FUNC HERE
            error(1, lineNo, id);
            System.out.println("Error on checkVariable");
        }
    }

    @SuppressWarnings("unchecked")
    public static void pushStack(String type){
        stack.add(type);
    }

    public static String popStack(){
        String result = "";
        if(!stack.isEmpty())
            result = stack.pop().toString();
        return result;
    }

    public static String calculateCube(String type, String operator){
        String result = "";

        int Dim1 = 2;
        int Dim3 = 6;
        int Dim2 = 0;
        switch (type){
            case "int":
                Dim3 = Integer;
                break;
            case "float":
                Dim3 = Float;
                break;
            case "string":
                Dim3 = Char;
                break;
            case "boolean":
                Dim3 = Boolean;
                break;
            case "void":
                Dim3 = Void;
                break;
            case "error":
                Dim3 = Error;
        }
        if(operator == "-")
            Dim1 = OP_minus;
        else if(operator == "!")
            Dim1 = OP_Not;
        result = cube[Dim1][Dim2][Dim3];
        return result;
    }

    public static String calculateCube(String type1, String type2, String operator){
        String result = "";
        int Dim1 = 0;
		int Dim2 = 6;
		int Dim3 = 6;
        if(!operator.equals("=")){
            switch (type1) {
                case "int":
                    Dim2 = Integer;
                    break;
                case "float":
                    Dim2 = Float;
                    break;
                case "char":
                    Dim2 = Char;
                    break;
                case "string":
                    Dim2 = String;
                    break;
                case "boolean":
                    Dim2 = Boolean;
                    break;
                case "void":
                    Dim2 = Void;
                    break;
                case "error":
                    Dim2 = Error;
            }
            switch(type2){
                case "int":
                    Dim3 = Integer;
                    break;
                case "float":
                    Dim3 = Float;
                    break;
                case "char":
                    Dim3 = Char;
                    break;
                case "string":
                    Dim3 = String;
                    break;
                case "boolean":
                    Dim3 = Boolean;
                    break;
                case "void":
                    Dim3 = Void;
                    break;
                case "error":
                    Dim3 = Error;
            }
        }
        if(operator.equals("-"))
            Dim1 = OP_minus;
        else if(operator.equals("+"))
            Dim1 = OP_plus;
        else if(operator.equals("*"))
            Dim1 = OP_Mult;
        else if(operator.equals("/"))
            Dim1 = OP_Division;
        else if(operator.equals("&"))
            Dim1 = OP_And;
        else if(operator.equals("|"))
            Dim1 = OP_Or;
        else if(operator.equals("!"))
            Dim1 = OP_Not;
        else if(operator.equals("<"))
            Dim1 = OP_Minor;
        else if(operator.equals(">"))
            Dim1 = OP_Greater;
        else if(operator.equals("=="))
            Dim1 = OP_Equal;
        else if(operator.equals("<="))
            Dim1 = OP_LorEqual;
        else if(operator.equals(">="))
            Dim1 = OP_GorEqual;
        else if(operator.equals("%"))
            Dim1 = OP_Reminder;
        else if(operator.equals("!="))
            Dim1 = OP_Different;
        else if(operator.equals("=")){
            if(type1.equals(type1) || type1.equals("float") && type1.equals("int"))
                result = "OK";
            return result;
        }
        result = cube[Dim1][Dim2][Dim3];
        return result;
    }

    public static void error(int error, int n, String info){
        switch(error){
            case 0:
                System.out.println("Line: " + n + ": [Semantic] variable "  + info + " not found");
                break;
            case 1:
                System.out.println("Line: " + n + ": [Semantic] variable " + info + " is already defined");
                break;
            case 2:
                System.out.println("Line: " + n + ": [Semantic] incompatible types: type mismatch");
                break;
            case 3:
                System.out.println("Line: "+ n + ": [Semantic] incompatible types: expected a boolean");
                break;
            case 4:
                System.out.println("Line: " + n + ": [Semantic] incompatible types: expected int/oct/hex/bin");
                break;            
        }
    }

    public static String getIdType(String Id, int lineNo){
        Vector<SymbolTableItem> tempSymbolTableItem;
        tempSymbolTableItem = symbolTable.get(Id);
        if(tempSymbolTableItem == null){
            error(0, lineNo, Id);
            return "error";
        }
        return ((tempSymbolTableItem.firstElement().getType()));
    }

    public static void clearVariables(){
        getSymbolTable().clear();
        while(!stack.isEmpty())
            stack.pop();
    }






    // public static void AddVariable(String scope, String type, String id, String value) {
    //     if (CheckVariable(scope, id)) {
    //         errorController.storeError("Error: Variable '" + id + "' is already defined");
    //         return;
    //     }
    //     Vector<SymbolTableItem> items = symbolTable.get(id);
    //     if (items.equals(null)) {
    //         Vector<SymbolTableItem> newItemVector = new Vector<>();
    //         symbolTable.put(id, newItemVector);
    //         items = symbolTable.get(id);
    //     }
    //     items.add(new SymbolTableItem(type, scope, value));
    // }

    // /**
    //  * Adds a variable to the symbol table with a specific scope, type, and id.
    //  * The default value is an empty string.
    //  */
    // public static void AddVariable(String scope, String type, String id) {
    //     AddVariable(scope, type, id, "");
    // }

    // /**
    //  * Adds a global variable to the symbol table with a specific type and id.
    //  * The default value is an empty string.
    //  */
    // public static void AddVariable(String type, String id) {
    //     AddVariable("global", type, id, "");
    // }

    // /**
    //  * Checks if a variable exists in the symbol table with the given scope and id.
    //  */
    // public static boolean CheckVariable(String type, String id) {
    //     Vector<SymbolTableItem> items = symbolTable.get(id);
    //     if (items == null) {
    //         return false;
    //     } else {
    //         for (SymbolTableItem item : items) {
    //             if (item.getScope().equals(type)) { // Use equals for string comparison
    //                 return true;
    //             }
    //         }
    //         return false;
    //     }
    // }

    // /**
    //  * Checks if a variable exists in the symbol table with the given id.
    //  */
    // public static boolean CheckVariable(String id) {
    //     Vector<SymbolTableItem> items = symbolTable.get(id);
    //     return items != null; // Simplified return statement
    // }

    // /**
    //  * Retrieves the type of a variable from the symbol table based on its id.
    //  */
    // public static String getVariableType(String id) {
    //     Vector<SymbolTableItem> items = symbolTable.get(id);
    //     if (items.equals(null))
    //         return "";
    //     return items.get(0).getType();
    // }

    // // Uncomment and implement these methods if function support is needed
    // // public static boolean CheckFunction(String id) {
    // // return true;
    // // }
    // // public static void addFunction(String id) {

    // // }

    // /**
    //  * Retrieves the stack used for tokens.
    //  */
    // public static Stack<Token> getStack() {
    //     return stack;
    // }

    // /**
    //  * Clears all variables from the symbol table.
    //  */
    // public static void clearVariables() {
    //     symbolTable.clear();
    // }

    // /**
    //  * Converts a string representation of a data type to its corresponding integer
    //  * constant.
    //  */
    // private static int convertToDT(String dataType) {
    //     switch (dataType) {
    //         case "Integer":
    //         case "int":
    //             return Integer;
    //         case "Float":
    //         case "float":
    //             return Float;
    //         case "Boolean":
    //         case "bool":
    //             return Boolean;
    //         case "Char":
    //         case "char":
    //             return Char;
    //         case "String":
    //         case "string":
    //             return String;
    //         case "Void":
    //         case "void":
    //             return Void;
    //         default:
    //             return -1; // Use default for invalid types
    //     }
    // }

    // /**
    //  * Converts an integer constant representing a data type to its corresponding
    //  * string representation.
    //  */
    // private static String convertDTToString(int dataType) {
    //     switch (dataType) {
    //         case Integer:
    //             return "int";
    //         case Float:
    //             return "float";
    //         case Boolean:
    //             return "bool";
    //         case Char:
    //             return "char";
    //         case String:
    //             return "string";
    //         case Void:
    //             return "void";
    //         case Error:
    //             return "error";
    //         default:
    //             return "error"; // Use default for invalid data types
    //     }
    // }

    // /**
    //  * Checks the result type of a binary operation between two data types.
    //  */
    // private static int checkOperationB(String type1, String type2, int op) {
    //     int t1 = convertToDT(type1);
    //     int t2 = convertToDT(type2);

    //     if (t1 == -1 || t2 == -1) {
    //         System.err.println("Invalid conversion: " + type1 + " to " + t1 + ", " + type2 + " to " + t2);
    //         return Error;
    //     }
    //     if (op < OP_plus || op > OP_Assignation) {
    //         System.err.println("Invalid operation code: " + op);
    //     }
    //     try {
    //         return cube[op][t2][t1];
    //     } catch (ArrayIndexOutOfBoundsException e) {
    //         System.err.println("Array index out of bounds: op = " + op + ", t1 = " + t1 + ", t2 = " + t2);
    //         throw e;
    //     }
    // }

    // /**
    //  * Checks the result type of a binary operation between two data types and
    //  * returns it as a string.
    //  */
    // public static String checkOperationBinary(String type1, String type2, int op) {
    //     return convertDTToString(checkOperationB(type1, type2, op));
    // }


}
