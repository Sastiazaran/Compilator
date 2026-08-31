package compilator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticAnalyzerTest {

    @Test
    void additionResultIsIntNotInteger() {
        assertEquals("int", SemanticAnalyzer.calculateCube("int", "int", "+"));
        CompileResult result = Compiler.compile("{\nint x = 1 + 2;\n}");
        assertTrue(result.getErrors().isEmpty(), () -> result.getErrors().toString());
        assertEquals("int", result.getSymbolTable().get("x").firstElement().getType());
    }

    @Test
    void undefinedVariableIsReported() {
        CompileResult result = Compiler.compile("{\nint x = 0;\nprint(a);\n}");
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("variable a not found")),
                () -> result.getErrors().toString());
    }

    @Test
    void redeclarationIsReported() {
        CompileResult result = Compiler.compile("{\nint x = 0;\nint x = 1;\n}");
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("already defined")),
                () -> result.getErrors().toString());
    }

    @Test
    void assigningStringToIntIsTypeMismatch() {
        CompileResult result = Compiler.compile("{\nint x = \"hola\";\n}");
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("type mismatch")),
                () -> result.getErrors().toString());
    }

    @Test
    void ifRequiresBoolean() {
        CompileResult intCondition = Compiler.compile("{\nint x = 2;\nif (x) {\nint y = 0;\n}\n}");
        assertTrue(intCondition.getErrors().stream().anyMatch(e -> e.contains("expected a boolean")),
                () -> intCondition.getErrors().toString());

        CompileResult boolCondition = Compiler.compile("{\nboolean flag = true;\nif (flag) {\nint y = 0;\n}\n}");
        assertTrue(boolCondition.getErrors().isEmpty(), () -> boolCondition.getErrors().toString());
    }

    @Test
    void unaryNotAndMinus() {
        assertEquals("boolean", SemanticAnalyzer.calculateCube("boolean", "!"));
        assertEquals("int", SemanticAnalyzer.calculateCube("int", "-"));
        assertEquals("error", SemanticAnalyzer.calculateCube("int", "!"));
    }

    @Test
    void booleanAndOrCube() {
        assertEquals("boolean", SemanticAnalyzer.calculateCube("boolean", "boolean", "&&"));
        assertEquals("boolean", SemanticAnalyzer.calculateCube("boolean", "boolean", "||"));
        assertEquals("error", SemanticAnalyzer.calculateCube("int", "int", "&&"));
    }

    @Test
    void moduloTypes() {
        assertEquals("int", SemanticAnalyzer.calculateCube("int", "int", "%"));
        assertEquals("error", SemanticAnalyzer.calculateCube("float", "int", "%"));
    }

    @Test
    void unknownTypeDoesNotThrow() {
        assertEquals("error", SemanticAnalyzer.calculateCube("nope", "int", "+"));
        assertEquals("error", SemanticAnalyzer.calculateCube("error", "!"));
    }
}
