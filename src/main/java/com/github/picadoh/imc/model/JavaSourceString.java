package com.github.picadoh.imc.model;

import javax.tools.SimpleJavaFileObject;
import java.util.Objects;

public class JavaSourceString extends SimpleJavaFileObject {
    private final String className;
    private final String classSourceCode;

    public JavaSourceString(String className, String classSourceCode) {
        super(ClassURI.create(className, Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.classSourceCode = classSourceCode;
    }

    public final CharSequence getCharContent() {
        return getCharContent(false);
    }

    @Override
    public final CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return classSourceCode;
    }

    @Override
    public final String getName() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaSourceString that = (JavaSourceString) o;
        return Objects.equals(className, that.className) &&
                Objects.equals(classSourceCode, that.classSourceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, classSourceCode);
    }
}
