package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class CompilationPackageTest {

    @Test
    public void shouldCreateCompilationPackageWithUnits() {

        CompilationUnit unit1 = mock(CompilationUnit.class);
        CompilationUnit unit2 = mock(CompilationUnit.class);

        CompilationPackage compilationPackage = new CompilationPackage(newArrayList(unit1, unit2));

        assertEquals(compilationPackage.getUnits(), newArrayList(unit1, unit2));
    }

}
