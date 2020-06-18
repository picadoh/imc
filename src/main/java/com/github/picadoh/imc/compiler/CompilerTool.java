package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import com.github.picadoh.imc.report.CompilationErrorReport;
import com.google.common.annotations.VisibleForTesting;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
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
            return compilerResult.withCompilationErrorReport(getCompilationErrorReport(diagnosticCollector));
        }

        return compilerResult;
    }

    @VisibleForTesting
    JavaCompiler getSystemJavaCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }

    @VisibleForTesting
    CompilationErrorReport getCompilationErrorReport(DiagnosticCollector<JavaFileObject> collector) {
        return new CompilationErrorReport(options, collector.getDiagnostics());
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
