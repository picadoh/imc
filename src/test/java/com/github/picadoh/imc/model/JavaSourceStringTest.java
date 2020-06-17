package com.github.picadoh.imc.model;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static org.testng.Assert.assertEquals;

public class JavaSourceStringTest {

    private JavaSourceString victim;

    private static final String CLASS_NAME = "Main";
    private static final String CLASS_SOURCE_CODE = "public class Main {}";

    @BeforeTest
    public void setup() {
        victim = new JavaSourceString(CLASS_NAME, CLASS_SOURCE_CODE);
    }

    @Test
    public void shouldGetName() {
        assertEquals(victim.getName(), "Main");
    }

    @Test
    public void shouldGetCharSequence() {
        assertEquals(victim.getCharContent().toString(), "public class Main {}");
    }

    @Test
    public void shouldGetKind() {
        assertEquals(victim.getKind(), JavaFileObject.Kind.SOURCE);
    }

    @Test
    public void shouldGetSourceURL() {
        assertEquals(victim.toUri().toString(), "string:///Main.java");
    }

}
