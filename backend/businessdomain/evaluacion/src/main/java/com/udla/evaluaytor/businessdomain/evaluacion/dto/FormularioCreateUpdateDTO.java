package com.udla.evaluaytor.businessdomain.evaluacion.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class FormularioCreateUpdateDTO {
    private Date fecha;
    private String numero; 
    private int evaluacion;
    private Long estadoFormularioId;
    private Long proveedorId;
    private Long categoridaId;
    private Long peritoId; // Solo el ID del estado formulario
    private List<DetalleFormularioCreateUpdateDTO> detallesFormulario;
}

