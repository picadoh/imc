package com.github.picadoh.imc.compiler;

import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static org.junit.Assert.assertEquals;

public class SourceCodeTest {

    private SourceCode victim;

    private static final String CLASS_NAME = "Main";
    private static final String CLASS_SOURCE_CODE = "public class Main {}";

    @Before
    public void setup() {
        victim = new SourceCode(CLASS_NAME, CLASS_SOURCE_CODE);
    }

    @Test
    public void shouldGetName() {
        assertEquals("Main", victim.getName());
    }

    @Test
    public void shouldGetCharSequence() {
        assertEquals("public class Main {}", victim.getCharContent().toString());
    }

    @Test
    public void shouldGetKind() {
        assertEquals(JavaFileObject.Kind.SOURCE, victim.getKind());
    }

    @Test
    public void shouldGetSourceURL() {
        assertEquals("string:///Main.java", victim.toUri().toString());
    }

}
