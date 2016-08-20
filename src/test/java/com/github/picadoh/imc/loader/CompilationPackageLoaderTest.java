package com.github.picadoh.imc.loader;

import com.github.picadoh.imc.model.CompilationPackage;
import com.github.picadoh.imc.model.CompilationUnit;
import com.github.picadoh.imc.model.JavaMemoryObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class CompilationPackageLoaderTest {

    private CompilationPackageLoader victim;

    @BeforeMethod
    public void setupScenario() {
        victim = new CompilationPackageLoader();
    }

    @Test
    public void shouldCreateByteArrayClassLoader() {
        assertNotNull(victim.newByteArrayClassLoader());
    }

    @Test
    public void shouldLoadClasses() throws Exception {
        CompilationPackageLoader spiedVictim = spy(victim);

        // mock byte array class loader
        ByteArrayClassLoader loader = mock(ByteArrayClassLoader.class);
        when(loader.loadClass(anyString(), any(byte[].class)))
                .thenReturn(String.class)
                .thenReturn(Integer.class);

        doReturn(loader).when(spiedVictim).newByteArrayClassLoader();

        // mock compilation package
        CompilationUnit unit1 = mockCompilationUnit(String.class);
        CompilationUnit unit2 = mockCompilationUnit(Integer.class);

        CompilationPackage pkg = mock(CompilationPackage.class);
        when(pkg.getUnits()).thenReturn(newArrayList(unit1, unit2));

        // load package
        List<Class<?>> classList = spiedVictim.load(pkg);

        // assert
        assertEquals(classList.size(), 2);
        assertEquals(classList.get(0).getSimpleName(), "String");
        assertEquals(classList.get(1).getSimpleName(), "Integer");
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void shouldThrowExceptionWhenClassNotFound() throws Exception {
        CompilationPackageLoader spiedVictim = spy(victim);

        // mock byte array class loader
        ByteArrayClassLoader loader = mock(ByteArrayClassLoader.class);
        when(loader.loadClass(anyString(), any(byte[].class))).thenThrow(new ClassNotFoundException());

        doReturn(loader).when(spiedVictim).newByteArrayClassLoader();

        // mock compilation package
        CompilationUnit unit1 = mockCompilationUnit(String.class);
        CompilationUnit unit2 = mockCompilationUnit(Integer.class);

        CompilationPackage pkg = mock(CompilationPackage.class);
        when(pkg.getUnits()).thenReturn(newArrayList(unit1, unit2));

        // load package
        spiedVictim.load(pkg);
    }

    private CompilationUnit mockCompilationUnit(Class cls) {
        JavaMemoryObject byteArrayJavaObject = mock(JavaMemoryObject.class);
        when(byteArrayJavaObject.getClassBytes()).thenReturn(new byte[]{1,2,3});

        CompilationUnit unit = mock(CompilationUnit.class);
        when(unit.getName()).thenReturn(cls.getSimpleName());
        when(unit.getBytecode()).thenReturn(new byte[]{1,2,3});
        return unit;
    }

}
