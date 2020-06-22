package com.github.picadoh.imc.compiler;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InMemoryCompilerTest {

    private InMemoryCompiler victim;

    @Before
    public void setupScenario() {
        victim = new InMemoryCompiler();
    }

    @Test
    public void shouldCompileOneClass() {
        InMemoryCompiler spiedVictim = spy(victim);

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
        InMemoryCompiler spiedVictim = spy(victim);

        List<CompiledClass> compiledClasses = asList(
                mockCompiledClass(String.class.getSimpleName()), mockCompiledClass(Integer.class.getSimpleName()));

        CompilerTool compilerTool = mock(CompilerTool.class);
        when(compilerTool.compile(anyListOf(SourceCode.class))).thenReturn(new CompilerResult(compiledClasses));

        doReturn(compilerTool).when(spiedVictim).getCompilerTool(anyListOf(String.class));

        Map<String, String> sources = new HashMap<String, String>() {
            {
                put("HelloWorld1", "public class HelloWorld1 {}");
                put("HelloWorld2", "public class HelloWorld2 {}");
            }
        };

        CompilerResult result = spiedVictim.compile(sources);

        assertEquals(compiledClasses, result.getCompiledClasses());

        verify(spiedVictim).loadClasspath();
    }

    @Test
    public void shouldLoadClassPath() {
        String classpath = victim.loadClasspath();
        assertNotNull(classpath);
        assertFalse(classpath.isEmpty());
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
