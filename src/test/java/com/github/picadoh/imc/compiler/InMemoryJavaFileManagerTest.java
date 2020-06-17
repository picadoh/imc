package com.github.picadoh.imc.compiler;

import com.github.picadoh.imc.model.CompiledClass;
import org.testng.annotations.Test;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class InMemoryJavaFileManagerTest {

    @Test
    public void shouldCreateInMemoryJavaFileManager() throws IOException {
        JavaFileManager.Location location = mock(JavaFileManager.Location.class);
        FileObject fileObject = mock(FileObject.class);

        JavaFileManager manager = mock(JavaFileManager.class);
        InMemoryJavaFileManager victim = new InMemoryJavaFileManager(manager);

        JavaFileObject javaFileObject = victim.getJavaFileForOutput(location, "SomeName", null, fileObject);

        assertNotNull(javaFileObject);
        assertEquals(javaFileObject.getKind(), JavaFileObject.Kind.CLASS);

        CompiledClass expectedCompiledClass = new CompiledClass("SomeName");

        assertEquals(victim.getCompilerResult(), new CompilerResult(newArrayList(expectedCompiledClass)));
    }

}
