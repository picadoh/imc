package com.github.picadoh.imc.compiler;

import com.google.common.annotations.VisibleForTesting;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

class ByteCodeClassLoader extends ClassLoader {

    private final Map<String, byte[]> byteCodes;

    private ByteCodeClassLoader(Map<String, byte[]> byteCodes) {
        super(ByteCodeClassLoader.class.getClassLoader());
        this.byteCodes = byteCodes;
    }

    public static ByteCodeClassLoader create(final Map<String, byte[]> byteCodes) {
        return AccessController.doPrivileged(new PrivilegedAction<ByteCodeClassLoader>() {
            @Override
            public ByteCodeClassLoader run() {
                return new ByteCodeClassLoader(byteCodes);
            }
        });
    }

    public Map<String, Class<?>> loadClasses() throws ClassNotFoundException {
        Map<String, Class<?>> classes = newHashMap();

        for (String fqn: byteCodes.keySet()) {
            classes.put(fqn, loadClass(fqn));
        }

        return classes;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected Class findClass(String fqn) throws ClassNotFoundException {
        try {
            return defineClass(fqn, byteCodes.get(fqn));
        } catch (ClassFormatError e) {
            throw new ClassNotFoundException(fqn, e);
        }
    }

    @VisibleForTesting
    Class<?> defineClass(String fqn, byte[] bytecode) {
        return defineClass(fqn, bytecode, 0, bytecode.length);
    }

}
