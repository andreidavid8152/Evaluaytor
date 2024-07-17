package com.udla.evaluaytor.businessdomain.evaluacion.dto;

import com.udla.evaluaytor.businessdomain.evaluacion.models.MatrizEvaluacion;

import lombok.Data;

@Data
public class DetalleFormularioDTO {
    private Long id;
    private int cumplimiento;
    private String observacion;
    private EstadoDetalleDTO estadoDetalle;
    private DocumentoDTO documento;
    //private FormularioDTO formulario;
    private MatrizEvaluacion matrizEvaluacion; // Campo para incluir MatrizEvaluacion
}