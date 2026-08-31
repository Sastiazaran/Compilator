# Compilator

Educational **lexer**, **parser**, and **semantic analyzer** with a desktop workbench that runs the same way on Windows, macOS, and Linux.

Programs are written in a small C-like language. The tool tokenizes the source, builds a parse tree, type-checks it, and shows diagnostics in one place.

## Requirements

- **Java 17** or newer (the bytecode target is 17 so school lab JDKs and current JDKs both work)
- No global Maven install is required — use the Maven Wrapper (`mvnw` / `mvnw.cmd`)

## Run

GUI (default when a display is available):

```bash
./mvnw -q compile exec:java
```

Windows: `mvnw.cmd -q compile exec:java`

Headless CLI (any OS, including CI):

```bash
./mvnw -q -DskipTests package
java -jar target/compilator-1.0.0-all.jar examples/good.cmp
```

Analyze a file and print tokens, the parse tree, the symbol table, and errors. Exit code `0` means a clean program, `1` means diagnostics, `2` means the file was missing.

Open the GUI against a specific file by launching the app and using **File → Open**, or run with `--gui` when you have a display.

Keyboard: **Ctrl+Enter** (Windows/Linux) or **Cmd+Enter** (macOS) runs Analyze.

## Language

A program is a block. Statements are declarations, assignments, `if` / `else`, `while`, `print`, and `return`.

```text
{
int x = 1 + 2;
boolean ok = x < 10 && true;
if (ok) {
    print(x);
} else {
    print(0);
}
}
```

| Kind | Examples |
| --- | --- |
| Types | `int`, `float`, `string`, `boolean`, `char`, `void` |
| Literals | `10`, `0xFF`, `0b10`, `1.5`, `"hello"`, `true`, `false` |
| Operators | `+ - * / %` `&`/`&&` `\|`/`\|\|` `!` `< > == != <= >=` |

`&` and `&&` are the same logical AND; `|` and `||` are the same logical OR. Line endings may be LF, CRLF, or CR — they tokenize identically. Tabs count as whitespace.

## Project layout

```text
src/main/java/compilator/   compiler + Swing UI
src/test/java/compilator/   JUnit 5 tests
examples/                   sample programs (also bundled in the jar)
```

```bash
./mvnw test
```

## Authors

Sebastian Astiazaran, Pablo Uscanga, Rolando Palacios
