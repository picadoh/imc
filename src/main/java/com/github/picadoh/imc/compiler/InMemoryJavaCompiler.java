package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.JavaSourceString;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

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
    public CompilerResult compile(final String className, final String classSourceCode) {
        return compile(new HashMap<String, String>() {
            {
                put(className, classSourceCode);
            }
        });
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

        List<JavaSourceString> sources = new ArrayList<>();
        for (Map.Entry<String, String> classSource : classSourceMap.entrySet()) {
            sources.add(new JavaSourceString(classSource.getKey(), classSource.getValue()));
        }

        CompilerTool tool = getCompilerTool(options);
        return tool.compile(sources);
    }

    CompilerTool getCompilerTool(List<String> options) {
        return new CompilerTool(options);
    }

    protected String loadClasspath() {
        URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

        List<String> paths = new ArrayList<>();
        for (URL url : urlClassLoader.getURLs()) {
            paths.add(url.getPath());
        }

        return joinPaths(paths);
    }

    String joinPaths(List<String> elements) {
        if (elements.isEmpty()) {
            return "";
        }

        String separator = System.getProperty("path.separator");
        StringBuilder builder = new StringBuilder(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            builder.append(separator).append(elements.get(i));
        }

        return builder.toString();
    }
}
