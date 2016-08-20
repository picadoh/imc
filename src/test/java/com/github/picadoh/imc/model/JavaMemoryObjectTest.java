package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class JavaMemoryObjectTest {

    private JavaMemoryObject victim;

    @Test
    public void shouldCreateSourceJavaMemoryObject() {
        victim = new JavaMemoryObject("my.pkg.myFileName", JavaFileObject.Kind.SOURCE);

        assertEquals(victim.toUri().toString(), "string:///my/pkg/myFileName.java");
        assertEquals(victim.getKind(), JavaFileObject.Kind.SOURCE);
        assertNotNull(victim.getClassBytes());
    }

    @Test
    public void shouldCreateClassJavaMemoryObject() {
        victim = new JavaMemoryObject("my.pkg.myFileName", JavaFileObject.Kind.CLASS);

        assertEquals(victim.toUri().toString(), "string:///my/pkg/myFileName.class");
        assertEquals(victim.getKind(), JavaFileObject.Kind.CLASS);
        assertNotNull(victim.getClassBytes());
    }
}
