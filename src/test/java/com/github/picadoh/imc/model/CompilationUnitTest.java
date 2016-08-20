package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CompilationUnitTest {

    @Test
    public void shouldCreateCompilationUnit() {
        JavaMemoryObject someMemoryObject = mock(JavaMemoryObject.class);
        when(someMemoryObject.getClassBytes()).thenReturn(new byte[]{1,2,3});

        CompilationUnit unit = new CompilationUnit("SomeName", someMemoryObject);

        assertEquals(unit.getName(), "SomeName");
        assertEquals(unit.getBytecode(), new byte[]{1,2,3});
    }

}
