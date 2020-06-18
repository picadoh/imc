package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import com.google.common.collect.Lists;
import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.tools.*;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

public class CompilerToolTest {

    private CompilerTool victim;

    @Mock
    private Diagnostic<JavaFileObject> diag1;

    @Mock
    private Diagnostic<JavaFileObject> diag2;

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

        CompilerResult result = spiedVictim.compile(Lists.<JavaSourceString>newArrayList());

        List<Diagnostic<? extends JavaFileObject>> diagnostics = new ArrayList<>();
        diagnostics.add(diag1);
        diagnostics.add(diag2);

        assertNotNull(result);
        assertTrue(result.hasErrors());
        assertEquals(result.getCompilationErrorReport().getOptions(), newArrayList("-classpath", "my.jar"));
        assertEquals(result.getCompilationErrorReport().getDiagnostics(), diagnostics);
    }

}