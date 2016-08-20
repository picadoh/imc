package com.github.picadoh.imc.loader;

import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ByteArrayClassLoaderTest {

    @Test
    public void shouldFindClass() throws Exception {
        ByteArrayClassLoader victim = spy(ByteArrayClassLoader.newInstance());
        victim.setBytecode(new byte[]{1,2,3});

        doReturn(String.class).when(victim).defineClass(anyString(), any(byte[].class));

        Class<?> cls = victim.findClass("something");

        assertNotNull(cls);
        assertEquals(cls, String.class);
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenClassNotFound() throws Exception {
        ByteArrayClassLoader victim = spy(ByteArrayClassLoader.newInstance());
        victim.setBytecode(new byte[]{1, 2, 3});

        doThrow(ClassFormatError.class).when(victim).defineClass(anyString(), any(byte[].class));

        victim.findClass("com.github.picadoh.examples.javainception.Byte");
    }

    @Test
    public void shouldLoadClass() throws ClassNotFoundException {
        ByteArrayClassLoader victim = spy(ByteArrayClassLoader.newInstance());

        doReturn(String.class).when(victim).loadClass(anyString());

        Class<?> cls = victim.loadClass("something", new byte[]{1, 2, 3});

        assertNotNull(cls);
        assertEquals(cls, String.class);
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenFailsToLoadClass() throws ClassNotFoundException {
        ByteArrayClassLoader victim = spy(ByteArrayClassLoader.newInstance());

        doThrow(ClassNotFoundException.class).when(victim).loadClass(anyString());

        victim.loadClass("something", new byte[]{1, 2, 3});
    }

}
