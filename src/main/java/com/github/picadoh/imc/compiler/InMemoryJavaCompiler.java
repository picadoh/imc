package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Compiles java source code from source strings into byte arrays
 */
public class InMemoryJavaCompiler {

    /**
     * Compiles a class given its name and code.
     * If anonymous classes exist, they will result in separate compiler result entries.
     *
     * @param className Class name
     * @param classSourceCode Class source code
     * @return CompilerResult containing compiled classes or possible compilation errors
     */
    public CompilerResult compile(String className, String classSourceCode) {
        return compile(ImmutableMap.<String, String>builder()
                .put(className, classSourceCode)
                .build()
        );
    }

    /**
     * Compiles multiple classes given their names and source codes.
     * If anonymous classes exist, they will result in separate compiler result entries.
     *
     * @param classSourceMap Source codes to compile indexed by class name
     * @return CompilerResult containing compiled classes or possible compilation errors
     */
    public CompilerResult compile(Map<String, String> classSourceMap) {
        List<String> options = Arrays.asList("-classpath", loadClasspath());

        List<JavaSourceString> sources = newArrayList();
        for (Map.Entry<String, String> classSource : classSourceMap.entrySet()) {
            sources.add(new JavaSourceString(classSource.getKey(), classSource.getValue()));
        }

        CompilerTool tool = getCompilerTool(options);
        return tool.compile(sources);
    }

    @VisibleForTesting
    CompilerTool getCompilerTool(List<String> options) {
        return new CompilerTool(options);
    }

    protected String loadClasspath() {
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

        List<String> paths = newArrayList();
        for (URL url : urlClassLoader.getURLs()) {
            paths.add(url.getPath());
        }

        return Joiner.on(System.getProperty("path.separator")).join(paths);
    }
}
