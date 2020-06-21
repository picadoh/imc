package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CompilerResultTest {

    private CompilerResult victim;

    @BeforeTest
    public void setup() {
        CompiledClass compiledClass1 = mockClassBytecode(String.class);
        CompiledClass compiledClass2 = mockClassBytecode(Integer.class);
        victim = new CompilerResult(asList(compiledClass1, compiledClass2));
    }

    @Test
    public void shouldCreateByteCodeClassLoader() {
        assertNotNull(victim.newByteCodeClassLoader(new HashMap<String, byte[]>()));
    }

    @Test
    public void shouldBeConvertedToClassMap() throws Exception {
        CompilerResult spiedVictim = spy(victim);

        ByteCodeClassLoader loader = mock(ByteCodeClassLoader.class);

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>() {
            {
                put(String.class.getName(), String.class);
                put(Integer.class.getName(), Integer.class);
            }
        };

        when(loader.loadClasses()).thenReturn(classes);

        doReturn(loader).when(spiedVictim).newByteCodeClassLoader(anyMapOf(String.class, byte[].class));

        Map<String, Class<?>> classMap = spiedVictim.loadClassMap();

        // assert
        assertEquals(classMap.size(), 2);
        assertEquals(classMap.get(String.class.getName()), String.class);
        assertEquals(classMap.get(Integer.class.getName()), Integer.class);
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenClassNotFound() throws Exception {
        CompilerResult spiedVictim = spy(victim);

        ByteCodeClassLoader loader = mock(ByteCodeClassLoader.class);
        when(loader.loadClasses()).thenThrow(new ClassNotFoundException());

        doReturn(loader).when(spiedVictim).newByteCodeClassLoader(new HashMap<String, byte[]>());

        spiedVictim.loadClassMap();
    }

    private CompiledClass mockClassBytecode(Class<?> cls) {
        CompiledClass classBytecode = mock(CompiledClass.class);
        when(classBytecode.getClassName()).thenReturn(cls.getSimpleName());
        when(classBytecode.getClassByteCode()).thenReturn(new byte[]{1, 2, 3});
        return classBytecode;
    }

}
