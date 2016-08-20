package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompilationUnit;
import com.github.picadoh.imc.model.JavaMemoryObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class InMemoryClassManagerTest {

    private InMemoryClassManager victim;

    @BeforeClass
    public void setupScenario() {
        JavaFileManager manager = mock(JavaFileManager.class);
        victim = new InMemoryClassManager(manager);
    }

    @Test
    public void shouldCreateInMemoryUnits() throws IOException {
        JavaFileManager.Location location = mock(JavaFileManager.Location.class);
        FileObject fileObject = mock(FileObject.class);

        JavaFileObject fobj = victim.getJavaFileForOutput(location, "SomeName", JavaFileObject.Kind.SOURCE, fileObject);

        assertNotNull(fobj);
        assertEquals(fobj.getKind(), JavaFileObject.Kind.SOURCE);

        JavaMemoryObject expectedJavaMemoryObject = new JavaMemoryObject("SomeName", JavaFileObject.Kind.SOURCE);
        CompilationUnit expectedCompilationUnit = new CompilationUnit("SomeName", expectedJavaMemoryObject);

        assertEquals(victim.getAllClasses(), newArrayList(expectedCompilationUnit));
    }

}
