package com.udla.evaluaytor.businessdomain.empresa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MatrizEvaluacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pregunta;
    private int puntos;
    private int requiereDocumento;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}

