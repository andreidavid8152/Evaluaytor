package com.udla.evaluaytor.businessdomain.evaluacion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoEvaluacionDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.EstadoEvaluacionRepository;

@Service
public class EstadoEvaluacionImpl implements EstadoEvaluacionService {

    @Autowired
    private EstadoEvaluacionRepository estadoEvaluacionRepository;

    @Override
    public List<EstadoEvaluacionDTO> getAllEstadosFormulario() {
        List<EstadoEvaluacion> estados = estadoEvaluacionRepository.findAll();
        return estados.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public EstadoEvaluacionDTO getEstadoEvaluacionById(Long id) {
        Optional<EstadoEvaluacion> estadoOpt = estadoEvaluacionRepository.findById(id);
        return estadoOpt.map(this::convertToDTO).orElse(null);
    }

    @Override
    public EstadoEvaluacionDTO createEstadoEvaluacion(EstadoEvaluacionDTO estadoEvaluacionDTO) {
        EstadoEvaluacion estado = convertToEntity(estadoEvaluacionDTO);
        EstadoEvaluacion savedEstado = estadoEvaluacionRepository.save(estado);
        return convertToDTO(savedEstado);
    }

    @Override
    public EstadoEvaluacionDTO updateEstadoEvaluacion(Long id, EstadoEvaluacionDTO estadoEvaluacionDTO) {
        Optional<EstadoEvaluacion> estadoOpt = estadoEvaluacionRepository.findById(id);
        if (estadoOpt.isPresent()) {
            EstadoEvaluacion estado = estadoOpt.get();
            estado.setNombre(estadoEvaluacionDTO.getNombre());
            EstadoEvaluacion updatedEstado = estadoEvaluacionRepository.save(estado);
            return convertToDTO(updatedEstado);
        }
        return null;
    }

    @Override
    public void deleteEstadoEvaluacion(Long id) {
        estadoEvaluacionRepository.deleteById(id);
    }

    private EstadoEvaluacionDTO convertToDTO(EstadoEvaluacion estado) {
        EstadoEvaluacionDTO dto = new EstadoEvaluacionDTO();
        dto.setId(estado.getId());
        dto.setNombre(estado.getNombre());
        return dto;
    }

    private EstadoEvaluacion convertToEntity(EstadoEvaluacionDTO dto) {
        EstadoEvaluacion estado = new EstadoEvaluacion();
        estado.setNombre(dto.getNombre());
        return estado;
    }
}
