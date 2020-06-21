package com.github.picadoh.imc.compiler;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ByteCodeClassLoaderTest {

    @Test
    public void shouldFindClass() throws Exception {
        ByteCodeClassLoader victim = spy(ByteCodeClassLoader.create(new HashMap<String, byte[]>()));

        doReturn(String.class).when(victim).defineClass(anyString(), any(byte[].class));

        Class<?> cls = victim.findClass("something");

        assertNotNull(cls);
        assertEquals(cls, String.class);
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
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
        assertEquals(classes.size(), 1);
        assertEquals(classes.get(String.class.getName()), String.class);
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
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
