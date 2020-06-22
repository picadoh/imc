package com.github.picadoh.imc.model;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ClassURITest {

    @Test
    public void shouldCreateClassURI() {
        URI uri = ClassURI.create("my.class.name", ".class");

        assertEquals("string:///my/class/name.class", uri.toString());
    }

}
