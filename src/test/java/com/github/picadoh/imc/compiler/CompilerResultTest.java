package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CompilerResultTest {

    private CompilerResult victim;

    @BeforeTest
    public void setup() {
        CompiledClass compiledClass1 = mockClassBytecode(String.class);
        CompiledClass compiledClass2 = mockClassBytecode(Integer.class);
        victim = new CompilerResult(newArrayList(compiledClass1, compiledClass2));
    }

    @Test
    public void shouldCreateByteCodeClassLoader() {
        assertNotNull(victim.newByteCodeClassLoader(Maps.<String, byte[]>newHashMap()));
    }

    @Test
    public void shouldBeConvertedToClassMap() throws Exception {
        CompilerResult spiedVictim = spy(victim);

        ByteCodeClassLoader loader = mock(ByteCodeClassLoader.class);

        Map<String, Class<?>> classes = ImmutableMap.<String, Class<?>>builder()
                .put(String.class.getName(), String.class)
                .put(Integer.class.getName(), Integer.class)
                .build();

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

        doReturn(loader).when(spiedVictim).newByteCodeClassLoader(Maps.<String, byte[]>newHashMap());

        spiedVictim.loadClassMap();
    }

    private CompiledClass mockClassBytecode(Class<?> cls) {
        CompiledClass classBytecode = mock(CompiledClass.class);
        when(classBytecode.getClassName()).thenReturn(cls.getSimpleName());
        when(classBytecode.getClassByteCode()).thenReturn(new byte[]{1, 2, 3});
        return classBytecode;
    }

}
