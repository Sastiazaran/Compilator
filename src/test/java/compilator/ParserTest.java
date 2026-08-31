package compilator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserTest {

    @Test
    void validAssignmentDoesNotThrowAndHasNoParserErrors() {
        CompileResult result = assertDoesNotThrow(() -> Compiler.compile("{\nint x = 0;\n}"));
        assertFalse(result.getTokens().isEmpty());
        assertTrue(result.getErrors().stream().noneMatch(e -> e.contains("[Parser]")),
                () -> result.getErrors().toString());
    }

    @Test
    void emptyInputDoesNotThrow() {
        CompileResult result = assertDoesNotThrow(() -> Compiler.compile(""));
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("expected {")));
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("expected }")));
    }

    @Test
    void missingClosingBraceReportsParserError() {
        CompileResult result = Compiler.compile("{\nint x = 0;");
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("expected }")),
                () -> result.getErrors().toString());
    }

    @Test
    void logicalAndProgramParses() {
        CompileResult result = Compiler.compile("""
                {
                boolean delpuerto = true;
                int delpuerto2 = 2;
                if (!delpuerto && delpuerto2 == 2) {
                    int x = 0;
                }
                }
                """);
        assertTrue(result.getErrors().stream().noneMatch(e -> e.contains("[Parser]")),
                () -> result.getErrors().toString());
        assertTrue(result.getErrors().isEmpty(), () -> result.getErrors().toString());
    }

    @Test
    void moduloParsesInsideAssignment() {
        CompileResult result = Compiler.compile("{\nint rem = 10 % 3;\n}");
        assertTrue(result.getErrors().isEmpty(), () -> result.getErrors().toString());
    }
}
