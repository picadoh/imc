package com.github.picadoh.imc.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompiledClassTest {

    @Test
    public void shouldCreateCompiledClassOfSpecificKind() {
        CompiledClass victim = new CompiledClass("my.pkg.myFileName");

        assertEquals("string:///my/pkg/myFileName.class", victim.toUri().toString());
        assertNotNull(victim.getClassByteCode());
    }
}
