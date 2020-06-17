package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;

class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final CompilerResult compilerResult = new CompilerResult();

    public InMemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public CompilerResult getCompilerResult() {
        return compilerResult;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        CompiledClass compiledClass = new CompiledClass(className);
        compilerResult.addCompiledClass(compiledClass);
        return compiledClass;
    }
}
