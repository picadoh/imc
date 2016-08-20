package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompilationPackage;
import com.github.picadoh.imc.model.CompilationUnit;
import com.github.picadoh.imc.model.JavaMemoryObject;
import com.github.picadoh.imc.model.JavaSourceFromString;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class InMemoryCompilerTest {

    private InMemoryCompiler victim;

    @BeforeMethod
    public void setupScenario() {
        victim = new InMemoryCompiler();
    }

    @Test
    public void shouldCompileSingleClass() throws InMemoryCompiler.CompilerException {
        InMemoryCompiler spiedVictim = spy(victim);

        CompilationPackage pkg = mock(CompilationPackage.class);
        doReturn(pkg).when(spiedVictim).compile(anyMapOf(String.class, String.class));

        spiedVictim.singleCompile("HelloWorld", "public class HelloWorld {}");

        Map<String, String> sources = ImmutableMap.<String, String>builder()
                .put("HelloWorld", "public class HelloWorld {}")
                .build();

        verify(spiedVictim, times(1)).compile(sources);
    }

    @Test
    public void shouldCompileMultipleClasses() throws InMemoryCompiler.CompilerException {
        InMemoryCompiler spiedVictim = spy(victim);

        JavaCompiler.CompilationTask task = mock(JavaCompiler.CompilationTask.class);
        when(task.call()).thenReturn(true);

        JavaCompiler compiler = mock(JavaCompiler.class);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        List<CompilationUnit> compilationUnits = newArrayList(
                mockCompilationUnit(String.class), mockCompilationUnit(Integer.class));

        InMemoryClassManager manager = mock(InMemoryClassManager.class);
        when(manager.getAllClasses()).thenReturn(compilationUnits);

        doReturn(collector).when(spiedVictim).getDiagnosticCollector();
        doReturn(compiler).when(spiedVictim).getSystemJavaCompiler();
        doReturn(manager).when(spiedVictim).getClassManager(eq(compiler));

        // compiler.getTask(null, manager, collector, options, null, strFiles);
        when(compiler.getTask(
                any(Writer.class),
                any(JavaFileManager.class),
                eq(collector),
                anyListOf(String.class),
                anyListOf(String.class),
                eq(newArrayList(
                        new JavaSourceFromString("HelloWorld1", "public class HelloWorld1 {}"),
                        new JavaSourceFromString("HelloWorld2", "public class HelloWorld2 {}")
                )))
        ).thenReturn(task);

        Map<String, String> sources = ImmutableMap.<String, String>builder()
                .put("HelloWorld1", "public class HelloWorld1 {}")
                .put("HelloWorld2", "public class HelloWorld2 {}")
                .build();

        CompilationPackage pkg = spiedVictim.compile(sources);

        assertEquals(pkg.getUnits(), compilationUnits);

        // should not build compilation error report
        verify(spiedVictim).loadClasspath();
        verify(spiedVictim, times(0)).buildCompilationReport(eq(collector), anyListOf(String.class));
    }

    @Test
    public void shouldGenerateDiagnosticWhenCompilationFails() throws InMemoryCompiler.CompilerException {

        InMemoryCompiler spiedVictim = spy(victim);

        JavaCompiler.CompilationTask task = mock(JavaCompiler.CompilationTask.class);
        when(task.call()).thenReturn(false);

        JavaCompiler compiler = mock(JavaCompiler.class);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        List<CompilationUnit> compilationUnits = newArrayList(mockCompilationUnit(String.class));

        InMemoryClassManager manager = mock(InMemoryClassManager.class);
        when(manager.getAllClasses()).thenReturn(compilationUnits);

        doReturn(collector).when(spiedVictim).getDiagnosticCollector();
        doReturn(compiler).when(spiedVictim).getSystemJavaCompiler();
        doReturn(manager).when(spiedVictim).getClassManager(eq(compiler));
        doReturn("TestReport").when(spiedVictim).buildCompilationReport(eq(collector), anyListOf(String.class));

        // compiler.getTask(null, manager, collector, options, null, strFiles);
        when(compiler.getTask(
                any(Writer.class),
                any(JavaFileManager.class),
                eq(collector),
                anyListOf(String.class),
                anyListOf(String.class),
                anyListOf(JavaMemoryObject.class)))
                .thenReturn(task);

        try {
            spiedVictim.singleCompile("HelloWorld", "public class HelloWorld {}");
            fail(); // force fail if it gets here
        } catch (InMemoryCompiler.CompilerException e) {
            assertTrue(e.getMessage().startsWith("TestReport"));
            verify(spiedVictim).buildCompilationReport(eq(collector), anyListOf(String.class));
        } finally {
            verify(spiedVictim).loadClasspath();
        }
    }

    private CompilationUnit mockCompilationUnit(Class cls) {
        JavaMemoryObject byteArrayJavaObject = mock(JavaMemoryObject.class);
        when(byteArrayJavaObject.getClassBytes()).thenReturn(new byte[]{1,2,3});

        CompilationUnit unit = mock(CompilationUnit.class);
        when(unit.getName()).thenReturn(cls.getSimpleName());
        when(unit.getBytecode()).thenReturn(new byte[]{1,2,3});
        return unit;
    }

}
