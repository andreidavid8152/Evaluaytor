package com.udla.evaluaytor.businessdomain.evaluacion.services;

import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoEvaluacionDTO;
import java.util.List;

public interface EstadoEvaluacionService {
    List<EstadoEvaluacionDTO> getAllEstadosFormulario();

    EstadoEvaluacionDTO getEstadoEvaluacionById(Long id);

    EstadoEvaluacionDTO createEstadoEvaluacion(EstadoEvaluacionDTO estadoEvaluacionDTO);

    EstadoEvaluacionDTO updateEstadoEvaluacion(Long id, EstadoEvaluacionDTO estadoEvaluacionDTO);

    void deleteEstadoEvaluacion(Long id);
}
