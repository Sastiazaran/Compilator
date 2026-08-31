package compilator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects lexer, parser, and semantic diagnostics for one compilation run.
 */
public class ErrorController {

    private final String name;
    private final List<String> errors = new ArrayList<>();

    public ErrorController() {
        this("COMPILER");
    }

    public ErrorController(String name) {
        this.name = name == null ? "COMPILER" : name;
    }

    public void storeError(String err) {
        if (err != null && !err.isBlank()) {
            errors.add(err.endsWith("\n") ? err : err + "\n");
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int size() {
        return errors.size();
    }

    public void clear() {
        errors.clear();
    }
}
