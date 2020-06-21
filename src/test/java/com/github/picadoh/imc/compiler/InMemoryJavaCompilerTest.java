package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import com.github.picadoh.imc.model.JavaSourceString;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class InMemoryJavaCompilerTest {

    private InMemoryJavaCompiler victim;

    @BeforeMethod
    public void setupScenario() {
        victim = new InMemoryJavaCompiler();
    }

    @Test
    public void shouldCompileOneClass() {
        InMemoryJavaCompiler spiedVictim = spy(victim);

        CompilerResult compilerResult = mock(CompilerResult.class);
        doReturn(compilerResult).when(spiedVictim).compile(anyMapOf(String.class, String.class));

        spiedVictim.compile("HelloWorld", "public class HelloWorld {}");

        Map<String, String> sources = new HashMap<String, String>() {
            {
                put("HelloWorld", "public class HelloWorld {}");
            }
        };

        verify(spiedVictim, times(1)).compile(sources);
    }

    @Test
    public void shouldCompileManyClasses() {
        InMemoryJavaCompiler spiedVictim = spy(victim);

        List<CompiledClass> compiledClasses = asList(
                mockCompiledClass(String.class.getSimpleName()), mockCompiledClass(Integer.class.getSimpleName()));

        CompilerTool compilerTool = mock(CompilerTool.class);
        when(compilerTool.compile(anyListOf(JavaSourceString.class))).thenReturn(new CompilerResult(compiledClasses));

        doReturn(compilerTool).when(spiedVictim).getCompilerTool(anyListOf(String.class));

        Map<String, String> sources = new HashMap<String, String>() {
            {
                put("HelloWorld1", "public class HelloWorld1 {}");
                put("HelloWorld2", "public class HelloWorld2 {}");
            }
        };

        CompilerResult result = spiedVictim.compile(sources);

        assertEquals(result.getCompiledClasses(), compiledClasses);

        verify(spiedVictim).loadClasspath();
    }

    @Test
    public void shouldLoadClassPath() {
        String classpath = victim.loadClasspath();
        assertNotNull(classpath);
        assertNotEquals("", classpath);
    }

    @Test
    public void shouldJoinPaths() {
        String joined = victim.joinPaths(asList("a", "b"));
        assertEquals("a" + System.getProperty("path.separator") + "b", joined);
    }

    private CompiledClass mockCompiledClass(String className) {
        CompiledClass compiledClass = mock(CompiledClass.class);
        when(compiledClass.getClassName()).thenReturn(className);
        when(compiledClass.getClassByteCode()).thenReturn(new byte[]{1, 2, 3});
        return compiledClass;
    }

}
