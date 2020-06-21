package com.github.picadoh.imc.report;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Locale;

public class CompilationErrorReport {
    private final List<String> options;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompilationErrorReport(List<String> options, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        this.options = options;
        this.diagnostics = diagnostics;
    }

    public List<String> getOptions() {
        return options;
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }

    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();

        resultBuilder.append("options:\n")
                .append(options)
                .append("\n");

        resultBuilder.append("errors:\n");

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            resultBuilder
                    .append(diagnostic.getSource().getName())
                    .append(" -> ")
                    .append(diagnostic.getKind()).append(":").append(diagnostic.getCode())
                    .append(" (")
                    .append(diagnostic.getLineNumber()).append(":").append(diagnostic.getColumnNumber())
                    .append(") ")
                    .append(diagnostic.getMessage(Locale.getDefault()))
                    .append("\n");
        }

        return resultBuilder.toString();
    }
}
