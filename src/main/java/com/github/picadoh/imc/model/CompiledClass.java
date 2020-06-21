package com.github.picadoh.imc.model;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.util.Objects;

public class CompiledClass extends SimpleJavaFileObject {
    private final String className;
    private final ByteArrayOutputStream classByteCode;

    public CompiledClass(String className) {
        super(ClassURI.create(className, Kind.CLASS.extension), Kind.CLASS);
        this.className = className;
        this.classByteCode = new ByteArrayOutputStream();
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassByteCode() {
        return classByteCode.toByteArray();
    }

    @Override
    public final OutputStream openOutputStream() throws IOException {
        return classByteCode;
    }

    @Override
    public final Writer openWriter() throws IOException {
        return new OutputStreamWriter(openOutputStream());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompiledClass that = (CompiledClass) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
