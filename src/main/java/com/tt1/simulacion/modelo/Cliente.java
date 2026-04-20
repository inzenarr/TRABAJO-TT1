package com.tt1.simulacion.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Representa a un usuario con sus tokens de simulación. */
public class Cliente {

    private final String nombreUsuario;
    private final List<Integer> tokens = Collections.synchronizedList(new ArrayList<>());

    public Cliente(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public List<Integer> getTokens() {
        return tokens;
    }

    public void addToken(int token) {
        tokens.add(token);
    }
}
