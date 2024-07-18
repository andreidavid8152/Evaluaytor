package com.udla.evaluaytor.businessdomain.evaluacion.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.udla.evaluaytor.businessdomain.evaluacion.dto.DetalleFormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.DetalleFormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.DocumentoDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoDetalleDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoEvaluacionDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Categoria;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Documento;
import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoDetalle;
import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacionDetalle;
import com.udla.evaluaytor.businessdomain.evaluacion.models.MatrizEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Perito;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Proveedor;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.DetalleFormularioRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.DocumentoRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.EstadoDetalleRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.EstadoEvaluacionRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.FormularioRepository;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class FormularioImpl implements FormularioService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    private EstadoEvaluacionRepository estadoEvaluacionRepository;

    @Autowired
    private DetalleFormularioRepository detalleFormularioRepository;

    @Autowired
    private EstadoDetalleRepository estadoDetalleRepository;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Override
    public List<FormularioDTO> getAllFormularios() {
        List<FormularioEvaluacion> formularios = formularioRepository.findAll();
        return formularios.stream()
                .map(this::completeAndConvertToDTO)
                .collect(Collectors.toList());
    }

    private FormularioEvaluacion completeFormularioWithExternalData(FormularioEvaluacion formulario) {
        Long proveedorId = formulario.getId_proveedor();
        Long categoriaId = formulario.getId_categoria();
        Long peritoId = formulario.getId_perito();

        WebClient webClient = webClientBuilder.build();

        Proveedor proveedor = webClient.get()
                .uri("http://EMPRESA/api/empresa/proveedor/findbyid/{id}", proveedorId)
                .retrieve()
                .bodyToMono(Proveedor.class)
                .block();
        formulario.setProveedor(proveedor);

        Categoria categoria = webClient.get()
                .uri("http://EMPRESA/api/empresa/categoria/findbyid/{id}", categoriaId)
                .retrieve()
                .bodyToMono(Categoria.class)
                .block();
        formulario.setCategoria(categoria);

        Perito perito = webClient.get()
                .uri("http://EMPRESA/api/empresa/perito/findbyid/{id}", peritoId)
                .retrieve()
                .bodyToMono(Perito.class)
                .block();
        formulario.setPerito(perito);

        formulario.getDetallesFormulario().forEach(detalle -> {
            Long matrizEvaluacionId = detalle.getId_matrizevaluacion();
            MatrizEvaluacion matriz = webClient.get()
                    .uri("http://EMPRESA/api/empresa/matrizevaluacion/findbyid/{id}", matrizEvaluacionId)
                    .retrieve()
                    .bodyToMono(MatrizEvaluacion.class)
                    .block();
            detalle.setMatrizEvaluacion(matriz);
        });

        return formulario;
    }

    private FormularioDTO completeAndConvertToDTO(FormularioEvaluacion formulario) {
        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(formulario);

        return convertToDTO(completedFormulario);
    }

    @Override
    public FormularioDTO getFormularioById(Long id) {
        Optional<FormularioEvaluacion> formularioOpt = formularioRepository.findById(id);
        if (formularioOpt.isPresent()) {
            FormularioEvaluacion formulario = formularioOpt.get();
            FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(formulario);
            return convertToDTO(completedFormulario);
        } else {
            throw new RuntimeException("Formulario no encontrado");
        }
    }

    @Override
    public FormularioDTO createFormulario(FormularioCreateUpdateDTO formularioDTO) {
        FormularioEvaluacion formulario = convertToEntity(formularioDTO);

        FormularioEvaluacion savedFormulario = formularioRepository.save(formulario);

        if (formularioDTO.getDetallesFormulario() != null) {
            List<FormularioEvaluacionDetalle> detalles = formularioDTO.getDetallesFormulario().stream()
                    .map(detalleDTO -> {
                        FormularioEvaluacionDetalle detalle = convertDetalleToEntity(detalleDTO);
                        detalle.setFormulario(savedFormulario);
                        return detalleFormularioRepository.save(detalle);
                    })
                    .collect(Collectors.toList());
            savedFormulario.setDetallesFormulario(detalles);
        }

        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(savedFormulario);

        return convertToDTO(completedFormulario);
    }

    @Override
    public FormularioDTO updateFormulario(Long id, FormularioCreateUpdateDTO formularioDTO) {
        FormularioEvaluacion formulario = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado"));

        formulario.setFecha(Optional.ofNullable(formularioDTO.getFecha()).orElse(new Date()));
        formulario.setNumero(formularioDTO.getNumero());
        formulario.setEvaluacion(formularioDTO.getEvaluacion());

        EstadoEvaluacion estadoEvaluacion = estadoEvaluacionRepository.findById(formularioDTO.getEstadoEvaluacionId())
                .orElseThrow(() -> new RuntimeException("EstadoEvaluacion no encontrado"));
        formulario.setEstadoEvaluacion(estadoEvaluacion);

        formulario.setId_perito(formularioDTO.getPeritoId());
        formulario.setId_proveedor(formularioDTO.getProveedorId());
        formulario.setId_categoria(formularioDTO.getCategoridaId());

        FormularioEvaluacion updatedFormulario = formularioRepository.save(formulario);

        if (formularioDTO.getDetallesFormulario() != null) {
            List<FormularioEvaluacionDetalle> detalles = formularioDTO.getDetallesFormulario().stream()
                    .map(detalleDTO -> {
                        FormularioEvaluacionDetalle detalle = convertDetalleToEntity(detalleDTO);
                        detalle.setFormulario(updatedFormulario);
                        return detalleFormularioRepository.save(detalle);
                    })
                    .collect(Collectors.toList());
            updatedFormulario.setDetallesFormulario(detalles);
        }

        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(updatedFormulario);

        return convertToDTO(completedFormulario);
    }

    private FormularioEvaluacionDetalle convertDetalleToEntity(DetalleFormularioCreateUpdateDTO dto) {
        FormularioEvaluacionDetalle detalle = new FormularioEvaluacionDetalle();
        detalle.setCumplimiento(dto.getCumplimiento());
        detalle.setObservacion(dto.getObservacion());

        if (dto.getEstadoDetalleId() != null) {
            EstadoDetalle estadoDetalle = estadoDetalleRepository.findById(dto.getEstadoDetalleId())
                    .orElseThrow(() -> new RuntimeException("EstadoDetalle no encontrado"));
            detalle.setEstadoDetalle(estadoDetalle);
        }

        if (dto.getDocumento() != null) {
            Documento documento = new Documento();
            documento.setNombre(dto.getDocumento().getNombre());
            documento.setPath(dto.getDocumento().getPath());
            documento = documentoRepository.save(documento);
            detalle.setDocumento(documento);
        }

        detalle.setId_matrizevaluacion(dto.getId_matrizevaluacion());

        return detalle;
    }

    @Override
    @Transactional
    public void deleteFormulario(Long id) {
        // Eliminar filas dependientes
        detalleFormularioRepository.deleteByFormularioId(id);

        // Eliminar fila principal
        formularioRepository.deleteById(id);
    }

    private FormularioDTO convertToDTO(FormularioEvaluacion formulario) {
        FormularioDTO dto = new FormularioDTO();
        dto.setId(formulario.getId());
        dto.setFecha(formulario.getFecha());
        dto.setNumero(formulario.getNumero());
        dto.setEvaluacion(formulario.getEvaluacion());

        if (formulario.getEstadoEvaluacion() != null) {
            EstadoEvaluacionDTO estadoDTO = new EstadoEvaluacionDTO();
            estadoDTO.setId(formulario.getEstadoEvaluacion().getId());
            estadoDTO.setNombre(formulario.getEstadoEvaluacion().getNombre());
            dto.setEstadoEvaluacion(estadoDTO);
        }

        if (formulario.getProveedor() != null) {
            Proveedor proveedorDTO = new Proveedor();
            proveedorDTO.setId(formulario.getProveedor().getId());
            proveedorDTO.setNombre(formulario.getProveedor().getNombre());
            proveedorDTO.setTelefono(formulario.getProveedor().getTelefono());
            proveedorDTO.setDireccion(formulario.getProveedor().getDireccion());
            dto.setProveedor(proveedorDTO);
        }

        if (formulario.getPerito() != null) {
            Perito peritoDTO = new Perito();
            peritoDTO.setId(formulario.getPerito().getId());
            peritoDTO.setNombre(formulario.getPerito().getNombre());
            peritoDTO.setDireccion(formulario.getPerito().getDireccion());
            peritoDTO.setTelefono(formulario.getPerito().getTelefono());
            dto.setPerito(peritoDTO);
        }

        if (formulario.getCategoria() != null) {
            Categoria categoriaDTO = new Categoria();
            categoriaDTO.setId(formulario.getCategoria().getId());
            categoriaDTO.setDescripcion(formulario.getCategoria().getDescripcion());
            dto.setCategoria(categoriaDTO);
        }

        if (formulario.getDetallesFormulario() != null) {
            List<DetalleFormularioDTO> detallesDTO = formulario.getDetallesFormulario().stream()
                    .map(this::convertDetalleToDTO)
                    .collect(Collectors.toList());
            dto.setDetallesFormulario(detallesDTO);
        }

        return dto;
    }

    private DetalleFormularioDTO convertDetalleToDTO(FormularioEvaluacionDetalle detalle) {
        DetalleFormularioDTO dto = new DetalleFormularioDTO();
        dto.setId(detalle.getId());
        dto.setCumplimiento(detalle.getCumplimiento());
        dto.setObservacion(detalle.getObservacion());

        if (detalle.getEstadoDetalle() != null) {
            EstadoDetalleDTO estadoDTO = new EstadoDetalleDTO();
            estadoDTO.setId(detalle.getEstadoDetalle().getId());
            estadoDTO.setNombre(detalle.getEstadoDetalle().getNombre());
            dto.setEstadoDetalle(estadoDTO);
        }

        if (detalle.getDocumento() != null) {
            DocumentoDTO documentoDTO = new DocumentoDTO();
            documentoDTO.setId(detalle.getDocumento().getId());
            documentoDTO.setNombre(detalle.getDocumento().getNombre());
            documentoDTO.setPath(detalle.getDocumento().getPath());
            dto.setDocumento(documentoDTO);
        }

        if (detalle.getMatrizEvaluacion() != null) {
            MatrizEvaluacion matrizDTO = new MatrizEvaluacion();
            matrizDTO.setId(detalle.getMatrizEvaluacion().getId());
            matrizDTO.setPregunta(detalle.getMatrizEvaluacion().getPregunta());
            matrizDTO.setPuntos(detalle.getMatrizEvaluacion().getPuntos());
            matrizDTO.setRequiereDocumento(detalle.getMatrizEvaluacion().getRequiereDocumento());
            dto.setMatrizEvaluacion(matrizDTO);
        }

        return dto;
    }

    private FormularioEvaluacion convertToEntity(FormularioCreateUpdateDTO dto) {
        FormularioEvaluacion formulario = new FormularioEvaluacion();
        formulario.setFecha(Optional.ofNullable(dto.getFecha()).orElse(new Date()));
        formulario.setNumero(dto.getNumero());
        formulario.setEvaluacion(dto.getEvaluacion());

        EstadoEvaluacion estadoEvaluacion = estadoEvaluacionRepository.findById(dto.getEstadoEvaluacionId())
                .orElseThrow(() -> new RuntimeException("EstadoEvaluacion no encontrado"));
        formulario.setEstadoEvaluacion(estadoEvaluacion);

        formulario.setId_perito(dto.getPeritoId());
        formulario.setId_proveedor(dto.getProveedorId());
        formulario.setId_categoria(dto.getCategoridaId());

        return formulario;
    }

    public FormularioDTO getFormularioEvaluacion(Long formularioId) {
        FormularioEvaluacion formularioEvaluacion = formularioRepository.findById(formularioId)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado"));

        Long proveedorId = formularioEvaluacion.getId_proveedor();
        Long categoriaId = formularioEvaluacion.getId_categoria();
        Long peritoId = formularioEvaluacion.getId_perito();

        WebClient webClient = webClientBuilder.build();
        Mono<Proveedor> proveedorMono = webClient.get()
                .uri("http://EMPRESA/api/empresa/proveedor/findbyid/{id}", proveedorId)
                .retrieve()
                .bodyToMono(Proveedor.class);
        Proveedor proveedor = proveedorMono.block();
        formularioEvaluacion.setProveedor(proveedor);

        Mono<Categoria> categoriaMono = webClient.get()
                .uri("http://EMPRESA/api/empresa/categoria/findbyid/{id}", categoriaId)
                .retrieve()
                .bodyToMono(Categoria.class);
        Categoria categoria = categoriaMono.block();
        formularioEvaluacion.setCategoria(categoria);

        Mono<Perito> peritoMono = webClient.get()
                .uri("http://EMPRESA/api/empresa/perito/findbyid/{id}", peritoId)
                .retrieve()
                .bodyToMono(Perito.class);
        Perito perito = peritoMono.block();
        formularioEvaluacion.setPerito(perito);

        return convertToDTO(formularioEvaluacion);
    }
}