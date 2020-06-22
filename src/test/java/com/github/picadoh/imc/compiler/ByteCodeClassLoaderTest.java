package com.github.picadoh.imc.compiler;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ByteCodeClassLoaderTest {

    @Test
    public void shouldFindClass() throws Exception {
        ByteCodeClassLoader victim = spy(ByteCodeClassLoader.create(new HashMap<String, byte[]>()));

        doReturn(String.class).when(victim).defineClass(anyString(), any(byte[].class));

        Class<?> cls = victim.findClass("something");

        assertNotNull(cls);
        assertEquals(String.class, cls);
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenClassNotFound() throws Exception {
        ByteCodeClassLoader victim = spy(ByteCodeClassLoader.create(new HashMap<String, byte[]>()));

        doThrow(ClassFormatError.class).when(victim).defineClass(anyString(), any(byte[].class));

        victim.findClass("com.example.NonExistingClass");
    }

    @Test
    public void shouldLoadClass() throws ClassNotFoundException {
        ByteCodeClassLoader victim = spy(ByteCodeClassLoader.create(
                new HashMap<String, byte[]>() {
                    {
                        put(String.class.getName(), new byte[]{1, 2, 3});
                    }
                }
        ));

        doReturn(String.class).when(victim).loadClass(anyString());

        Map<String, Class<?>> classes = victim.loadClasses();

        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals(String.class, classes.get(String.class.getName()));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenFailsToLoadClass() throws ClassNotFoundException {
        ByteCodeClassLoader victim = spy(ByteCodeClassLoader.create(
                new HashMap<String, byte[]>() {
                    {
                        put(String.class.getName(), new byte[]{1, 2, 3});
                    }
                }
        ));

        doThrow(ClassNotFoundException.class).when(victim).loadClass(anyString());

        victim.loadClasses();
    }
}
