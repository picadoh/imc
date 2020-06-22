package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.tools.*;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CompilerToolTest {

    private CompilerTool victim;

    @Mock
    private Diagnostic<JavaFileObject> diag1;

    @Mock
    private Diagnostic<JavaFileObject> diag2;

    @Before
    public void setup() {
        initMocks(this);

        this.victim = new CompilerTool(asList("-classpath", "my.jar"));
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

        List<JavaSourceString> sources = asList(
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
        assertNull(result.getCompilationErrorReport());
    }

    @Test
    public void shouldCallCompilerWithFailure() {
        CompilerTool spiedVictim = spy(victim);

        JavaCompiler compiler = mock(JavaCompiler.class);

        InMemoryJavaFileManager manager = mock(InMemoryJavaFileManager.class);
        when(manager.getCompilerResult()).thenReturn(new CompilerResult());

        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        collector.report(diag1);
        collector.report(diag2);

        JavaCompiler.CompilationTask task = mock(JavaCompiler.CompilationTask.class);
        when(task.call()).thenReturn(false);

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

        CompilerResult result = spiedVictim.compile(new ArrayList<JavaSourceString>());

        List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();
        diagnostics.add(diag1);
        diagnostics.add(diag2);

        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(asList("-classpath", "my.jar"), result.getCompilationErrorReport().getOptions());
        assertEquals(diagnostics, result.getCompilationErrorReport().getDiagnostics());
    }

}
