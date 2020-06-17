package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;

/**
 * The result of the compilation execution
 */
public class CompilerResult {
    private final List<CompiledClass> compiledClasses;
    private final String compilationErrors;

    CompilerResult() {
        this(Lists.<CompiledClass>newArrayList());
    }

    CompilerResult(List<CompiledClass> compiledClasses) {
        this(compiledClasses, "");
    }


    CompilerResult(List<CompiledClass> compiledClasses, String compilationErrors) {
        this.compiledClasses = compiledClasses;
        this.compilationErrors = compilationErrors;
    }

    CompilerResult withCompilationErrors(String compilationErrors) {
        return new CompilerResult(compiledClasses, compilationErrors);
    }

    /**
     * @return String containing the compilation errors
     */
    public String getCompilationErrors() {
        return compilationErrors;
    }

    /**
     * @return True if there are compilation errors
     */
    public boolean hasErrors() {
        return !isNullOrEmpty(compilationErrors);
    }

    /**
     * Loads bytecode into a map of Class indexed by class name
     *
     * @return Class Map
     * @throws ClassNotFoundException When fails to load the bytecode into a Class
     */
    public Map<String, Class<?>> loadClassMap() throws ClassNotFoundException {
        Map<String, byte[]> byteCodes = newHashMap();
        for (CompiledClass compiled : compiledClasses) {
            byteCodes.put(compiled.getClassName(), compiled.getClassByteCode());
        }

        ByteCodeClassLoader classLoader = newByteCodeClassLoader(byteCodes);

        return classLoader.loadClasses();
    }

    void addCompiledClass(CompiledClass compiledClass) {
        compiledClasses.add(compiledClass);
    }

    @VisibleForTesting
    List<CompiledClass> getCompiledClasses() {
        return compiledClasses;
    }

    @VisibleForTesting
    ByteCodeClassLoader newByteCodeClassLoader(Map<String, byte[]> byteCodes) {
        return ByteCodeClassLoader.create(byteCodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompilerResult that = (CompilerResult) o;
        return Objects.equal(compiledClasses, that.compiledClasses);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(compiledClasses);
    }
}
