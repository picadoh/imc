package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import com.google.common.annotations.VisibleForTesting;

import javax.tools.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

class CompilerTool {

    private final List<String> options;

    public CompilerTool(List<String> options) {
        this.options = options;
    }

    public CompilerResult compile(List<JavaSourceString> sources) {
        JavaCompiler compiler = getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = getDiagnosticCollector();
        InMemoryJavaFileManager classManager = getClassManager(compiler);

        JavaCompiler.CompilationTask task = compiler.getTask(null,
                classManager, diagnosticCollector, options, null, sources);

        CompilerResult compilerResult = classManager.getCompilerResult();

        if (!task.call()) {
            return compilerResult.withCompilationErrors(getCompilationReport(diagnosticCollector));
        }

        return compilerResult;
    }

    @VisibleForTesting
    JavaCompiler getSystemJavaCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }

    @VisibleForTesting
    protected String getCompilationReport(DiagnosticCollector<JavaFileObject> collector) {
        StringBuilder resultBuilder = new StringBuilder("[Error compiling classes] ");

        resultBuilder.append("options: ")
                .append(options)
                .append("\n");

        for (Diagnostic<?> diagnostic : collector.getDiagnostics()) {
            JavaSourceString javaSource = (JavaSourceString) diagnostic.getSource();

            resultBuilder
                    .append(javaSource.getName())
                    .append(" -> ")
                    .append(diagnostic.getKind())
                    .append(":")
                    .append(diagnostic.getCode())
                    .append(" (")
                    .append(diagnostic.getLineNumber())
                    .append(":")
                    .append(diagnostic.getColumnNumber())
                    .append(") ")
                    .append(diagnostic.getMessage(Locale.getDefault()))
                    .append("\n");
        }

        return resultBuilder.toString();
    }

    @VisibleForTesting
    DiagnosticCollector<JavaFileObject> getDiagnosticCollector() {
        return new DiagnosticCollector<>();
    }

    @VisibleForTesting
    InMemoryJavaFileManager getClassManager(JavaCompiler compiler) {
        return new InMemoryJavaFileManager(compiler.getStandardFileManager(null,
                Locale.getDefault(), Charset.defaultCharset()));
    }
}
