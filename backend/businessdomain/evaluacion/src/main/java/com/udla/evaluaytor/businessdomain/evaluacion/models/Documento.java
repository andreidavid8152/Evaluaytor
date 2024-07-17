package com.udla.evaluaytor.businessdomain.evaluacion.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String path;

    @OneToOne(mappedBy = "documento")
    private FormularioEvaluacionDetalle detalleFormulario;
}

