package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.report.CompilationErrorReport;

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

    public CompilerResult compile(List<SourceCode> sources) {
        JavaCompiler compiler = getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = getDiagnosticCollector();
        InMemoryFileManager classManager = getClassManager(compiler);

        JavaCompiler.CompilationTask task = compiler.getTask(null,
                classManager, diagnosticCollector, options, null, sources);

        CompilerResult compilerResult = classManager.getCompilerResult();

        if (!task.call()) {
            return compilerResult.withCompilationErrorReport(getCompilationErrorReport(diagnosticCollector));
        }

        return compilerResult;
    }

    JavaCompiler getSystemJavaCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }


    CompilationErrorReport getCompilationErrorReport(DiagnosticCollector<JavaFileObject> collector) {
        return new CompilationErrorReport(options, collector.getDiagnostics());
    }

    DiagnosticCollector<JavaFileObject> getDiagnosticCollector() {
        return new DiagnosticCollector<>();
    }

    InMemoryFileManager getClassManager(JavaCompiler compiler) {
        return new InMemoryFileManager(compiler.getStandardFileManager(null,
                Locale.getDefault(), Charset.defaultCharset()));
    }
}
