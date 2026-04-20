package com.tt1.simulacion.dto;

import java.util.List;

public class SolicitudDto {

    private List<Integer> cantidadesIniciales;
    private List<String> nombreEntidades;

    public SolicitudDto(){}

    public SolicitudDto(List<Integer> cI, List<String> nE){
        this.nombreEntidades=nE;
        this.cantidadesIniciales=cI;
    }

    public List<Integer> getCantidadesIniciales(){
        return this.cantidadesIniciales;
    }
    public void setCantidadesIniciales(List<Integer> cI){
        this.cantidadesIniciales=cI;
    }

    public List<String> getNombreEntidades(){
        return this.nombreEntidades;
    }
    public void setNombreEntidades(List<String> nE){
        this.nombreEntidades=nE;
    }
}
