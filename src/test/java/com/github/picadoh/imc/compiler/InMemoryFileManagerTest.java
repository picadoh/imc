package com.github.picadoh.imc.compiler;

import org.junit.Test;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class InMemoryFileManagerTest {

    @Test
    public void shouldCreateInMemoryJavaFileManager() throws IOException {
        JavaFileManager.Location location = mock(JavaFileManager.Location.class);
        FileObject fileObject = mock(FileObject.class);

        JavaFileManager manager = mock(JavaFileManager.class);
        InMemoryFileManager victim = new InMemoryFileManager(manager);

        JavaFileObject javaFileObject = victim.getJavaFileForOutput(location, "SomeName", null, fileObject);

        assertNotNull(javaFileObject);
        assertEquals(JavaFileObject.Kind.CLASS, javaFileObject.getKind());

        CompiledClass expectedCompiledClass = new CompiledClass("SomeName");

        assertEquals(new CompilerResult(Collections.singletonList(expectedCompiledClass)), victim.getCompilerResult());
    }

}
