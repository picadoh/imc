package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import com.github.picadoh.imc.model.JavaSourceString;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

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

        Map<String, String> sources = ImmutableMap.<String, String>builder()
                .put("HelloWorld", "public class HelloWorld {}")
                .build();

        verify(spiedVictim, times(1)).compile(sources);
    }

    @Test
    public void shouldCompileManyClasses() {
        InMemoryJavaCompiler spiedVictim = spy(victim);

        List<CompiledClass> compiledClasses = newArrayList(
                mockCompiledClass(String.class.getSimpleName()), mockCompiledClass(Integer.class.getSimpleName()));

        CompilerTool compilerTool = mock(CompilerTool.class);
        when(compilerTool.compile(anyListOf(JavaSourceString.class))).thenReturn(new CompilerResult(compiledClasses));

        doReturn(compilerTool).when(spiedVictim).getCompilerTool(anyListOf(String.class));

        Map<String, String> sources = ImmutableMap.<String, String>builder()
                .put("HelloWorld1", "public class HelloWorld1 {}")
                .put("HelloWorld2", "public class HelloWorld2 {}")
                .build();

        CompilerResult result = spiedVictim.compile(sources);

        assertEquals(result.getCompiledClasses(), compiledClasses);

        verify(spiedVictim).loadClasspath();
    }

    @Test
    public void shouldLoadClassPath() {
        assertFalse(Strings.isNullOrEmpty(victim.loadClasspath()));
    }

    private CompiledClass mockCompiledClass(String className) {
        CompiledClass compiledClass = mock(CompiledClass.class);
        when(compiledClass.getClassName()).thenReturn(className);
        when(compiledClass.getClassByteCode()).thenReturn(new byte[]{1, 2, 3});
        return compiledClass;
    }

}
