package com.udla.evaluaytor.businessdomain.evaluacion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacionDetalle;

public interface DetalleFormularioRepository extends JpaRepository<FormularioEvaluacionDetalle, Long> {
    @Modifying
    @Query("DELETE FROM FormularioEvaluacionDetalle d WHERE d.formulario.id = :formularioId")
    void deleteByFormularioId(@Param("formularioId") Long formularioId);
}
