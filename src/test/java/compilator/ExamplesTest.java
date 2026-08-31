package compilator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamplesTest {

    private static String read(String name) throws IOException {
        Path path = Path.of("examples", name);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Test
    void validExamplesHaveNoDiagnostics() throws IOException {
        for (String name : new String[] {
                "good.cmp", "assign.cmp", "if-and.cmp", "if-lt.cmp", "if-neq.cmp",
                "if-le.cmp", "if-gt.cmp", "if-ge.cmp", "if-not.cmp", "crlf.cmp"
        }) {
            CompileResult result = Compiler.compile(read(name));
            assertTrue(result.getErrors().isEmpty(), name + " -> " + result.getErrors());
        }
    }

    @Test
    void invalidExamplesReportErrors() throws IOException {
        assertTrue(Compiler.compile(read("type-mismatch.cmp")).hasErrors());
        assertTrue(Compiler.compile(read("type-mismatch-assign.cmp")).hasErrors());
        assertTrue(Compiler.compile(read("undefined.cmp")).hasErrors());
        assertTrue(Compiler.compile(read("undefined-init.cmp")).hasErrors());
        assertTrue(Compiler.compile(read("redeclare.cmp")).hasErrors());
        assertTrue(Compiler.compile(read("missing-brace.cmp")).hasErrors());
        assertFalse(Compiler.compile(read("assign.cmp")).hasErrors());
    }
}
