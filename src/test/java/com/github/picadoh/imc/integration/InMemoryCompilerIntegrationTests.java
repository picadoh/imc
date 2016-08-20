package com.github.picadoh.imc.integration;

import com.github.picadoh.imc.compiler.InMemoryCompiler;
import com.github.picadoh.imc.loader.CompilationPackageLoader;
import com.github.picadoh.imc.model.CompilationPackage;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

public class InMemoryCompilerIntegrationTests {

    private String sourceHelloWorld;
    private String sourceStringLengthSort;
    private String sourceStringSorterByLength;

    @BeforeClass
    public void setupScenario() throws Exception {
        sourceHelloWorld = Resources.toString(Resources.getResource("test-input/HelloWorld.java"), Charsets.UTF_8);
        sourceStringLengthSort = Resources.toString(Resources.getResource("test-input/StringLengthSort.java"), Charsets.UTF_8);
        sourceStringSorterByLength = Resources.toString(Resources.getResource("test-input/StringSorterByLength.java"), Charsets.UTF_8);
    }

    @Test
    public void shouldCompileAndExecuteHelloWorld() throws Exception {

        CompilationPackage pkg = new InMemoryCompiler().singleCompile("HelloWorld", sourceHelloWorld);

        CompilationPackageLoader loader = new CompilationPackageLoader();
        List<Class<?>> classes = loader.load(pkg);
        Class<?> mainClass = classes.get(0);

        // invoke through reflection
        mainClass.getMethod("main", String[].class).invoke(null, (Object) null);

        assertEquals(classes.size(), 1);
    }

    @Test
    public void shouldCompileAndExecuteStringLengthSort() throws Exception {

        CompilationPackage pkg = new InMemoryCompiler().singleCompile("StringLengthSort", sourceStringLengthSort);

        CompilationPackageLoader loader = new CompilationPackageLoader();
        Map<String, Class<?>> classes = loader.loadAsMap(pkg);
        Class<?> mainClass = classes.get("StringLengthSort");

        // invoke through reflection
        Object instance = mainClass.newInstance();

        List<String> inputList = newArrayList("aaa", "b", "aa", "a");
        mainClass.getMethod("sort", List.class).invoke(instance, inputList);

        assertEquals(inputList, newArrayList("b", "a", "aa", "aaa"));
        assertEquals(classes.size(), 2);
    }

    @Test
    public void shouldCompileAndExecuteMultipleClasses() throws Exception {

        CompilationPackage pkg = new InMemoryCompiler().compile(
                ImmutableMap.<String, String>builder()
                        .put("StringLengthSort", sourceStringLengthSort)
                        .put("HelloWorld", sourceHelloWorld)
                        .build()
        );

        CompilationPackageLoader loader = new CompilationPackageLoader();
        Map<String, Class<?>> classes = loader.loadAsMap(pkg);

        Class<?> stringLengthSortClass = classes.get("StringLengthSort");
        Class<?> helloWorldClass = classes.get("HelloWorld");

        // invoke hello world
        helloWorldClass.getMethod("main", String[].class).invoke(null, (Object) null);

        // invoke string length sort
        Object stringLengthSortInstance = stringLengthSortClass.newInstance();

        List<String> inputList = newArrayList("aaa", "b", "aa", "a");
        stringLengthSortClass.getMethod("sort", List.class).invoke(stringLengthSortInstance, inputList);

        assertEquals(inputList, newArrayList("b", "a", "aa", "aaa"));
        assertEquals(classes.size(), 3);
    }

    @Test
    public void shouldCompileClassImplementingKnownInterface() throws Exception {

        // let's also use a reference to another fresh compiled class, to spice things up
        CompilationPackage pkg = new InMemoryCompiler().compile(
                ImmutableMap.<String, String>builder()
                        .put("StringLengthSort", sourceStringLengthSort)
                        .put("StringSorterByLength", sourceStringSorterByLength)
                        .build()
        );

        CompilationPackageLoader loader = new CompilationPackageLoader();
        Map<String, Class<?>> classes = loader.loadAsMap(pkg);
        Class<?> mainClass = classes.get("StringSorterByLength");

        // invoke without reflection

        StringSorter instance = (StringSorter)mainClass.newInstance();

        List<String> inputList = newArrayList("aaa", "b", "aa", "a");
        instance.sort(inputList);

        assertEquals(inputList, newArrayList("b", "a", "aa", "aaa"));
        assertEquals(classes.size(), 3);
    }

}
