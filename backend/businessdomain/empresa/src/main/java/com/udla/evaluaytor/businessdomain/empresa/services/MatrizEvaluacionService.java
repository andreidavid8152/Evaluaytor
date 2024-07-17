package com.udla.evaluaytor.businessdomain.empresa.services;

import java.util.List;

import com.udla.evaluaytor.businessdomain.empresa.dto.MatrizEvaluacionCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.empresa.dto.MatrizEvaluacionDTO;

public interface MatrizEvaluacionService {
    List<MatrizEvaluacionDTO> getAllMatricesEvaluacion();

    MatrizEvaluacionDTO getMatrizEvaluacionById(Long id);

    MatrizEvaluacionDTO createMatrizEvaluacion(MatrizEvaluacionCreateUpdateDTO matrizEvaluacionDTO);

    MatrizEvaluacionDTO updateMatrizEvaluacion(Long id, MatrizEvaluacionCreateUpdateDTO matrizEvaluacionDTO);
}