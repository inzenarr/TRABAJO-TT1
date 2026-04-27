package com.tt1.simulacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Objeto de Transferencia de Datos(DTO) que representa la solicitud de un usuario para iniciar una nueva simulación.
 * <p>
 * Contiene la configuración inicial requerida, mapeando el tipo de criaturas
 * con sus respectivas cantidades a generar en el tablero.
 * </p>
 */
@Schema(example = "{\"cantidadesIniciales\":[2,1],\"nombreCriaturas\":[\"alpha\",\"beta\"]}")
public class SolicitudDto {

    @Schema(example = "[2, 1]", description = "Cantidad de cada criatura a generar")
    private List<Integer> cantidadesIniciales;

    @Schema(example = "[\"alpha\", \"beta\"]", description = "Nombres de las criaturas: alpha, beta o gamma")
    private List<String> nombreCriaturas;

    /**
     * Constructor por defecto vacío.
     * Necesario para que frameworks como Spring puedan deserializar el JSON entrante en este objeto.
     */
    public SolicitudDto(){}

    /**
     * Constructor con parámetros.
     *
     * @param cI Lista con las cantidades de cada criatura a generar.
     * @param nC   Lista con los nombres de las criaturas ("alpha", "beta", "gamma").
     */
    public SolicitudDto(List<Integer> cI, List<String> nC){
        this.nombreCriaturas=nC;
        this.cantidadesIniciales=cI;
    }

    /** @return lista de cantidades iniciales*/
    public List<Integer> getCantidadesIniciales(){
        return this.cantidadesIniciales;
    }

    /** @param cI lista de cantidades iniciales*/
    public void setCantidadesIniciales(List<Integer> cI){
        this.cantidadesIniciales=cI;
    }

    /** @return lista de nombres de las criaturas*/
    public List<String> getNombreCriaturas(){
        return this.nombreCriaturas;
    }

    /** @param nC lista de nombres de las criaturas*/
    public void setNombreCriaturas(List<String> nC){
        this.nombreCriaturas=nC;
    }
}
