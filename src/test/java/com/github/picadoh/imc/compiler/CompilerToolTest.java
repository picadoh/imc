package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.tools.*;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class CompilerToolTest {

    private CompilerTool victim;

    @BeforeTest
    public void setup() {
        initMocks(this);

        this.victim = new CompilerTool(Lists.newArrayList("-classpath", "my.jar"));
    }

    @Test
    public void shouldCallCompilerWithSuccess() {
        CompilerTool spiedVictim = spy(victim);

        JavaCompiler compiler = mock(JavaCompiler.class);

        InMemoryJavaFileManager manager = mock(InMemoryJavaFileManager.class);
        when(manager.getCompilerResult()).thenReturn(new CompilerResult());

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = mock(JavaCompiler.CompilationTask.class);
        when(task.call()).thenReturn(true);

        List<JavaSourceString> sources = newArrayList(
                new JavaSourceString("HelloWorld1", "public class HelloWorld1 {}"),
                new JavaSourceString("HelloWorld2", "public class HelloWorld2 {}")
        );

        doReturn(collector).when(spiedVictim).getDiagnosticCollector();
        doReturn(compiler).when(spiedVictim).getSystemJavaCompiler();
        doReturn(manager).when(spiedVictim).getClassManager(compiler);

        doReturn(task).when(compiler).getTask(
                any(Writer.class),
                any(JavaFileManager.class),
                eq(collector),
                anyListOf(String.class),
                anyListOf(String.class),
                eq(sources));

        CompilerResult result = spiedVictim.compile(sources);

        assertNotNull(result);
        assertFalse(result.hasErrors());
        assertTrue(result.getCompilationErrors().isEmpty());
    }

    @Test
    public void shouldCallCompilerWithFailure() {
        CompilerTool spiedVictim = spy(victim);

        JavaCompiler compiler = mock(JavaCompiler.class);

        InMemoryJavaFileManager manager = mock(InMemoryJavaFileManager.class);
        when(manager.getCompilerResult()).thenReturn(new CompilerResult());

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        collector.report(new Diagnostic<JavaFileObject>() {
            @Override
            public Kind getKind() {
                return Kind.ERROR;
            }

            @Override
            public JavaFileObject getSource() {
                return new JavaSourceString("name", "code");
            }

            @Override
            public long getPosition() {
                return 50;
            }

            @Override
            public long getStartPosition() {
                return 0;
            }

            @Override
            public long getEndPosition() {
                return 100;
            }

            @Override
            public long getLineNumber() {
                return 10;
            }

            @Override
            public long getColumnNumber() {
                return 20;
            }

            @Override
            public String getCode() {
                return "errorCode";
            }

            @Override
            public String getMessage(Locale locale) {
                return "errorMessage";
            }
        });

        JavaCompiler.CompilationTask task = mock(JavaCompiler.CompilationTask.class);
        when(task.call()).thenReturn(false);

        List<JavaSourceString> sources = newArrayList(
                new JavaSourceString("HelloWorld1", "public class HelloWorld1 {}"),
                new JavaSourceString("HelloWorld2", "public class HelloWorld2 {}")
        );

        doReturn(collector).when(spiedVictim).getDiagnosticCollector();
        doReturn(compiler).when(spiedVictim).getSystemJavaCompiler();
        doReturn(manager).when(spiedVictim).getClassManager(compiler);

        when(compiler.getTask(
                any(Writer.class),
                any(JavaFileManager.class),
                eq(collector),
                anyListOf(String.class),
                anyListOf(String.class),
                anyListOf(JavaSourceString.class)))
                .thenReturn(task);

        CompilerResult result = spiedVictim.compile(sources);

        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertTrue(result.getCompilationErrors().contains("ERROR:errorCode"));
        assertTrue(result.getCompilationErrors().contains("10:20"));
    }

}
