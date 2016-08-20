package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import javax.tools.JavaFileObject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JavaSourceFromStringTest {

    private JavaSourceFromString victim;

    private static final String HW_NAME = "HelloWorld";

    private static final String HW_CODE = "public class HelloWorld {\n"+
            "	public static void main(String[] args) {\n"+
            "		System.out.println(\"hello,world!\");\n"+
            "	}\n"+
            "}\n";

    @Test
    public void shouldGetName() {
        victim = new JavaSourceFromString(HW_NAME, HW_CODE);
        assertEquals(victim.getName(), "HelloWorld");
    }

    @Test
    public void shouldGetCharSequence() {
        victim = new JavaSourceFromString(HW_NAME, HW_CODE);
        assertTrue(victim.getCharContent(true).toString().startsWith("public class HelloWorld"));
    }

    @Test
    public void shouldGetSourceKind() {
        victim = new JavaSourceFromString(HW_NAME, HW_CODE);
        assertEquals(victim.getKind(), JavaFileObject.Kind.SOURCE);
    }

    @Test
    public void shouldGetSourceURL() {
        victim = new JavaSourceFromString(HW_NAME, HW_CODE);
        assertEquals(victim.toUri().toString(), "string:///HelloWorld.java");
    }

}
