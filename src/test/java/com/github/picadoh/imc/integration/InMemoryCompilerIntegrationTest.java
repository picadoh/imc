package com.github.picadoh.imc.integration;

import com.github.picadoh.imc.compiler.InMemoryCompiler;
import com.github.picadoh.imc.compiler.CompilerResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryCompilerIntegrationTest {

    private String sourceHelloWorld;
    private String sourceHelloWorldPackaged;
    private String sourceHelloWorldFailed;
    private String sourceStringLengthSort;
    private String sourceStringSorterByLength;

    @Before
    public void setupScenario() throws Exception {
        sourceHelloWorld = readAllText("src/test/resources/test-input/HelloWorld.java");
        sourceHelloWorldPackaged = readAllText("src/test/resources/test-input/HelloWorldPackaged.java");
        sourceHelloWorldFailed = readAllText("src/test/resources/test-input/HelloWorldFailed.java");
        sourceStringLengthSort = readAllText("src/test/resources/test-input/StringLengthSort.java");
        sourceStringSorterByLength = readAllText("src/test/resources/test-input/StringSorterByLength.java");
    }

    @Test
    public void shouldCompileAndExecuteSimpleClass() throws Exception {
        CompilerResult compilerResult = new InMemoryCompiler().compile("HelloWorld", sourceHelloWorld);

        Map<String, Class<?>> classes = compilerResult.loadClassMap();

        executeMain(classes.get("HelloWorld"), new String[]{});
        assertEquals(1, classes.size());
    }

    @Test
    public void shouldCompileAndExecuteSimpleClassInsidePackage() throws Exception {
        CompilerResult compilerResult = new InMemoryCompiler().compile("com.example.HelloWorld", sourceHelloWorldPackaged);

        Map<String, Class<?>> classes = compilerResult.loadClassMap();

        executeMain(classes.get("com.example.HelloWorld"), new String[]{});
        assertEquals(1, classes.size());
    }

    @Test
    public void shouldFailCompilationForInvalidSource() {
        CompilerResult compilerResult = new InMemoryCompiler().compile("HelloWorldFailed", sourceHelloWorldFailed);

        assertTrue(compilerResult.hasErrors());
        assertTrue(compilerResult.getCompilationErrorReport().toString()
                .contains("HelloWorldFailed -> ERROR:compiler.err.not.stmt (3:15) not a statement"));
        assertTrue(compilerResult.getCompilationErrorReport().toString()
                .contains("HelloWorldFailed -> ERROR:compiler.err.expected (3:19) ';' expected"));
    }

    @Test
    public void shouldCompileAndExecuteCodeWithAnonymousClasses() throws Exception {
        CompilerResult compilerResult = new InMemoryCompiler().compile("StringLengthSort", sourceStringLengthSort);

        Map<String, Class<?>> classes = compilerResult.loadClassMap();
        Object instance = classes.get("StringLengthSort").newInstance();

        List<String> inputList = asList("aaa", "b", "aa", "a");
        classes.get("StringLengthSort").getMethod("sort", List.class).invoke(instance, inputList);

        assertEquals(asList("b", "a", "aa", "aaa"), inputList);
        assertEquals(2, classes.size());
    }

    @Test
    public void shouldCompileAndExecuteMultipleClassesWithDependencies() throws Exception {

        Map<String, Class<?>> classes = new InMemoryCompiler().compile(
                new HashMap<String, String>() {
                    {
                        put("StringLengthSort", sourceStringLengthSort);
                        put("HelloWorld", sourceHelloWorld);
                    }
                }
        ).loadClassMap();

        Class<?> stringLengthSortClass = classes.get("StringLengthSort");
        Class<?> helloWorldClass = classes.get("HelloWorld");

        executeMain(helloWorldClass, new String[]{});

        Object stringLengthSortInstance = stringLengthSortClass.newInstance();

        List<String> inputList = asList("aaa", "b", "aa", "a");
        stringLengthSortClass.getMethod("sort", List.class).invoke(stringLengthSortInstance, inputList);

        assertEquals(asList("b", "a", "aa", "aaa"), inputList);
        assertEquals(3, classes.size());
    }

    @Test
    public void shouldCompileClassImplementingKnownInterface() throws Exception {
        Map<String, Class<?>> classes = new InMemoryCompiler().compile(
                new HashMap<String, String>() {
                    {
                        put("StringLengthSort", sourceStringLengthSort);
                        put("StringSorterByLength", sourceStringSorterByLength);
                    }
                }
        ).loadClassMap();

        StringSorter instance = (StringSorter) classes.get("StringSorterByLength").newInstance();

        List<String> inputList = asList("aaa", "b", "aa", "a");
        instance.sort(inputList);

        assertEquals(asList("b", "a", "aa", "aaa"), inputList);
        assertEquals(3, classes.size());
    }

    private void executeMain(Class<?> cls, String[] args) throws Exception {
        cls.getMethod("main", String[].class).invoke(null, (Object) args);
    }

    private String readAllText(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
    }
}
