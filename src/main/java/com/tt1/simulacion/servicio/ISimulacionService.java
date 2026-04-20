package com.tt1.simulacion.servicio;

import com.tt1.simulacion.dto.SolicitudDto;

import java.util.List;

public interface ISimulacionService {
    int solicitar(String nombreUsuario, SolicitudDto sd);
    List<Integer> getTokenUsuario(String usuario);
    String getResultado(int token);
}
