package com.github.picadoh.imc.report;

import com.github.picadoh.imc.model.JavaSourceString;
import org.testng.annotations.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

public class CompilationErrorReportTest {
    @Test
    public void shouldCreateCompilationErrorReport() {
        List<String> options = newArrayList("option1", "option2");

        List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();
        diagnostics.add(buildDiagnostic("cls1", 1, 10, "msg1"));
        diagnostics.add(buildDiagnostic("cls2", 2, 20, "msg2"));

        CompilationErrorReport report = new CompilationErrorReport(options, diagnostics);

        assertEquals(report.getOptions(), newArrayList("option1", "option2"));
        assertEquals(report.getDiagnostics(), diagnostics);

        assertEquals(report.toString(),
                "options:\n" +
                        "[option1, option2]\n" +
                        "errors:\n" +
                        "cls1 -> ERROR:errorCode (1:10) msg1\n" +
                        "cls2 -> ERROR:errorCode (2:20) msg2\n");
    }

    private Diagnostic<JavaFileObject> buildDiagnostic(
            final String className, final long line, final long column, final String message) {
        return new Diagnostic<JavaFileObject>() {
            @Override
            public Kind getKind() {
                return Kind.ERROR;
            }

            @Override
            public JavaFileObject getSource() {
                return new JavaSourceString(className, "sourceCode");
            }

            @Override
            public long getPosition() {
                return 0;
            }

            @Override
            public long getStartPosition() {
                return 0;
            }

            @Override
            public long getEndPosition() {
                return 0;
            }

            @Override
            public long getLineNumber() {
                return line;
            }

            @Override
            public long getColumnNumber() {
                return column;
            }

            @Override
            public String getCode() {
                return "errorCode";
            }

            @Override
            public String getMessage(Locale locale) {
                return message;
            }
        };
    }
}
