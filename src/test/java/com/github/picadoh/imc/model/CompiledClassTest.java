package com.github.picadoh.imc.model;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CompiledClassTest {

    @Test
    public void shouldCreateCompiledClassOfSpecificKind() {
        CompiledClass victim = new CompiledClass("my.pkg.myFileName");

        assertEquals(victim.toUri().toString(), "string:///my/pkg/myFileName.class");
        assertNotNull(victim.getClassByteCode());
    }
}
