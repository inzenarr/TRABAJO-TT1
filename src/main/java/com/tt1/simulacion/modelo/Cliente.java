package com.tt1.simulacion.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Representa a un usuario con sus tokens de simulación. */
public class Cliente {

    private final String nombreUsuario;
    private final List<String> tokens = Collections.synchronizedList(new ArrayList<>());

    public Cliente(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void addToken(String token) {
        tokens.add(token);
    }
}
