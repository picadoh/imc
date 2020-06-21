package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import com.github.picadoh.imc.report.CompilationErrorReport;

import java.util.*;

/**
 * The result of the compilation execution
 */
public class CompilerResult {
    private final List<CompiledClass> compiledClasses;
    private final CompilationErrorReport compilationErrorReport;

    CompilerResult() {
        this(new ArrayList<CompiledClass>());
    }

    CompilerResult(List<CompiledClass> compiledClasses) {
        this(compiledClasses, null);
    }

    CompilerResult(List<CompiledClass> compiledClasses, CompilationErrorReport compilationErrorReport) {
        this.compiledClasses = compiledClasses;
        this.compilationErrorReport = compilationErrorReport;
    }

    CompilerResult withCompilationErrorReport(CompilationErrorReport compilationErrorReport) {
        return new CompilerResult(compiledClasses, compilationErrorReport);
    }

    /**
     * @return Compilation Error Report when compilation fails or null otherwise
     */
    public CompilationErrorReport getCompilationErrorReport() {
        return compilationErrorReport;
    }

    /**
     * @return True if there are compilation errors
     */
    public boolean hasErrors() {
        return compilationErrorReport != null;
    }

    /**
     * Loads bytecode into a map of Class indexed by class name
     *
     * @return Class Map
     * @throws ClassNotFoundException When fails to load the bytecode into a Class
     */
    public Map<String, Class<?>> loadClassMap() throws ClassNotFoundException {
        Map<String, byte[]> byteCodes = new HashMap<>();
        for (CompiledClass compiled : compiledClasses) {
            byteCodes.put(compiled.getClassName(), compiled.getClassByteCode());
        }

        ByteCodeClassLoader classLoader = newByteCodeClassLoader(byteCodes);

        return classLoader.loadClasses();
    }

    void addCompiledClass(CompiledClass compiledClass) {
        compiledClasses.add(compiledClass);
    }

    List<CompiledClass> getCompiledClasses() {
        return compiledClasses;
    }

    ByteCodeClassLoader newByteCodeClassLoader(Map<String, byte[]> byteCodes) {
        return ByteCodeClassLoader.create(byteCodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompilerResult that = (CompilerResult) o;
        return Objects.equals(compiledClasses, that.compiledClasses) &&
                Objects.equals(compilationErrorReport, that.compilationErrorReport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compiledClasses, compilationErrorReport);
    }
}
