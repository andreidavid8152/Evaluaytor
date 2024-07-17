package com.udla.evaluaytor.businessdomain.evaluacion.dto;

import lombok.Data;

@Data
public class DetalleFormularioCreateUpdateDTO {
    private int cumplimiento;
    private String observacion;
    private Long estadoDetalleId; // Solo el ID del estado detalle
    private DocumentoDTO documento;
    private Long id_matrizevaluacion; // Solo el ID de la matriz de evaluación
}
