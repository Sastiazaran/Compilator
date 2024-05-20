import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalizer {
    private static Hashtable<String, Vector<SymbolTableItem>> symbolTable;
    private static Set<String> functions = new HashSet<String>();
    public static ErrorController errorController = new ErrorController("Semantic");
    private static final Stack<Token> stack = new Stack<Token>();

    /**
     * 
     * @return
     */

    public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable() {
        return symbolTable;
    }

    // Operations
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

    //
    public static final int Integer = 0;
    public static final int Float = 1;
    public static final int Char = 2;
    public static final int String = 3;
    public static final int Boolean = 4;
    public static final int Void = 5;
    public static final int Error = 6;
    
    /**
     * 
     */

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
            // Minus1
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Multiplication2
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Division3
            {
                    { Integer, Float, Error, Error, Error, Error },
                    { Float, Float, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // And4
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Or5
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Not6
            {
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Boolean, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Minor7
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Greater8
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Equal9
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Lor Equal10
            {
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Boolean, Boolean, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error },
                    { Error, Error, Error, Error, Error, Error }
            },
            // Gor Equall 11
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

    /**
     * 
     * @param scope
     * @param type
     * @param id
     * @param value
     */

    public static void AddVariable(String scope, String type, String id, String value) {

    }

    /**
     * 
     * @param scope
     * @param type
     * @param id
     */

    public static void AddVariable(String scope, String type, String id) {

    }

    /**
     * 
     * @param type
     * @param id
     */

    public static void AddVariable(String type, String id) {

    }

    /**
     * 
     * @param type
     * @param id
     */

    public static void CheckVariable(String type, String id) {
        // A. search the id in the symbol table
        // B. if !exist then insert type, scope = global, value(0,false, "", '')
        // C. else error(1): "Variable is already defined"
    }

    /**
     * 
     * @param id
     * @return
     */

    public static boolean CheckVariable(String id) {
        return true;
    }

    /**
     * 
     * @param id
     * @return
     */

    public static String getVariableType(String id) {
        return "pene";
    }

    /**
     * 
     * @param id
     * @return
     */

    public static boolean CheckFunction(String id) {
        return true;
    }

    /**
     * 
     * @param id
     */

    public static void addFunction(String id) {

    }

    /**
     * 
     * @return
     */

    public static Stack<Token> getStack() {
        return stack;
    }

    /**
     * 
     */

    public static void clearVariebles() {

    }

    /**
     * 
     * @param dataType
     * @return
     */

    private static int convertToDT(String dataType) {
        return -1;
    }

    /**
     * 
     * @param dataType
     * @return
     */

    private static String convertDTToString(int dataType) {
        return "pene";
    }

    /**
     * 
     * @param type1
     * @param type2
     * @param op
     * @return
     */

    private static int checkOperationB(String type1, String type2, int op) {
        return -2;
    }

    /**
     * 
     * @param type1
     * @param type2
     * @param op
     * @return
     */

    public static String checkOperationBinary(String type1, String type2, int op) {
        return "a";
    }

}
