package com.udla.evaluaytor.businessdomain.evaluacion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoEvaluacion;

public interface EstadoEvaluacionRepository extends JpaRepository<EstadoEvaluacion, Long >{

}
