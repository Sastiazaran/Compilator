package compilator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerCliTest {

    @Test
    void compileResultExposesTokensTreeAndSymbols() {
        CompileResult result = Compiler.compile("{\nint x = 0;\n}");
        assertTrue(result.getTokens().size() >= 7);
        assertEquals("root", result.getParseTree().toString());
        assertTrue(result.getSymbolTable().containsKey("x"));
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void cliReturnsZeroForValidFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("ok.cmp");
        Files.writeString(file, "{\nint x = 0;\n}", StandardCharsets.UTF_8);
        assertEquals(0, App.runCli(file));
    }

    @Test
    void cliReturnsOneForErrors(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("bad.cmp");
        Files.writeString(file, "{\nint x = \"no\";\n}", StandardCharsets.UTF_8);
        assertEquals(1, App.runCli(file));
    }

    @Test
    void cliReturnsTwoWhenFileMissing(@TempDir Path dir) throws Exception {
        assertEquals(2, App.runCli(dir.resolve("missing.cmp")));
    }

    @Test
    void windowsNewlinesDoNotChangeCompileOutcome() {
        String unix = "{\nint x = 0;\n}";
        String windows = "{\r\nint x = 0;\r\n}";
        CompileResult a = Compiler.compile(unix);
        CompileResult b = Compiler.compile(windows);
        assertEquals(a.getTokens().size(), b.getTokens().size());
        assertEquals(a.hasErrors(), b.hasErrors());
    }
}
