package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.solicitudDto;

import java.util.List;

public interface ISimulacionService {
    int solicitar(String nombreUsuario, solicitudDto sd);
    List<Integer> getTokenUsuario(String usuario);
    String getResultado(int token);
}
