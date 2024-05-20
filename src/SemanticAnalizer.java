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
    
    public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable(){
        return symbolTable;
    }

    private static int cube[][][] = {
        {},
        {}
    };

    public static void AddVariable(String scope, String type, String id, String value){

    }

    public static void AddVariable(String scope, String type, String id){

    }

    public static void AddVariable(String type, String id){
        
    }

    public static void CheckVariable(String type, String id){
        //A. search the id in the symbol table
        //B. if !exist then insert type, scope = global, value(0,false, "", '')
        //C. else error(1): "Variable is already defined"
    }

    public static boolean CheckVariable(String id){
        return true;
    }

    public static String getVariableType(String id){
        return "pene";
    }

    public static boolean CheckFunction(String id){
        return true;
    }

    public static void addFunction(String id){

    }

    public static Stack<Token> getStack(){
        return stack;
    }

    public static void clearVariebles(){

    }

    private static int convertToDT(String dataType){
        return -1;
    }

    private static String convertDTToString(int dataType){
        return "pene";
    }

    private static int checkOperationB(String type1, String type2, int op){
        return -2;
    }

    public static String checkOperationBinary(String type1, String type2, int op){
        return "a";
    }
    

}
