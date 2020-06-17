package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

public class ClassURITest {

    @Test
    public void shouldCreateClassURI() {
        URI uri = ClassURI.create("my.class.name", ".class");

        assertEquals(uri.toString(), "string:///my/class/name.class");
    }

}
