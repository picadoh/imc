package com.github.picadoh.imc.model;

import java.net.URI;

class ClassURI {
    public static URI create(String name, String extension) {
        return URI.create(String.format("string:///%s%s", name.replace('.', '/'), extension));
    }
}
