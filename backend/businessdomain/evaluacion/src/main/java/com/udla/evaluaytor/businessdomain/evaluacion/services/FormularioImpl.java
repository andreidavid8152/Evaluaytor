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
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoFormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Categoria;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Documento;
import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoDetalle;
import com.udla.evaluaytor.businessdomain.evaluacion.models.EstadoFormulario;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacionDetalle;
import com.udla.evaluaytor.businessdomain.evaluacion.models.MatrizEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Perito;
import com.udla.evaluaytor.businessdomain.evaluacion.models.Proveedor;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.DetalleFormularioRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.DocumentoRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.EstadoDetalleRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.EstadoFormularioRepository;
import com.udla.evaluaytor.businessdomain.evaluacion.repositories.FormularioRepository;

import reactor.core.publisher.Mono;

@Service
public class FormularioImpl implements FormularioService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    private EstadoFormularioRepository estadoFormularioRepository;

    
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

        // Llamada a microservicio para obtener Proveedor
        Proveedor proveedor = webClient.get()
                .uri("http://localhost:8081/api/empresa/proveedor/findbyid/{id}", proveedorId)
                .retrieve()
                .bodyToMono(Proveedor.class)
                .block();
        formulario.setProveedor(proveedor);

        // Llamada a microservicio para obtener Categoria
        Categoria categoria = webClient.get()
                .uri("http://localhost:8081/api/empresa/categoria/findbyid/{id}", categoriaId)
                .retrieve()
                .bodyToMono(Categoria.class)
                .block();
        formulario.setCategoria(categoria);

        // Llamada a microservicio para obtener Perito
        Perito perito = webClient.get()
                .uri("http://localhost:8081/api/empresa/perito/findbyid/{id}", peritoId)
                .retrieve()
                .bodyToMono(Perito.class)
                .block();
        formulario.setPerito(perito);

        // Completar los detalles del formulario con información de la matriz de
        // evaluación
        formulario.getDetallesFormulario().forEach(detalle -> {
            Long matrizEvaluacionId = detalle.getId_matrizevaluacion();
            MatrizEvaluacion matriz = webClient.get()
                    .uri("http://localhost:8081/api/empresa/matrizevaluacion/findbyid/{id}", matrizEvaluacionId)
                    .retrieve()
                    .bodyToMono(MatrizEvaluacion.class)
                    .block();
            detalle.setMatrizEvaluacion(matriz);
        });

        return formulario;
    }

    private FormularioDTO completeAndConvertToDTO(FormularioEvaluacion formulario) {
        // Completar la información del formulario con datos externos
        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(formulario);

        // Convertir a DTO
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
            // Puedes lanzar una excepción si el formulario no se encuentra
            throw new RuntimeException("Formulario no encontrado");
        }
    }

    @Override
    public FormularioDTO createFormulario(FormularioCreateUpdateDTO formularioDTO) {
        // Convertir el DTO a entidad
        FormularioEvaluacion formulario = convertToEntity(formularioDTO);

        // Guardar la entidad principal
        FormularioEvaluacion savedFormulario = formularioRepository.save(formulario);

        // Convertir y guardar los detalles del formulario
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

        // Completar el formulario con datos externos
        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(savedFormulario);

        // Convertir la entidad completa a DTO
        return convertToDTO(completedFormulario);
    }

    @Override
    public FormularioDTO updateFormulario(Long id, FormularioCreateUpdateDTO formularioDTO) {
        FormularioEvaluacion formulario = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado"));

        formulario.setFecha(Optional.ofNullable(formularioDTO.getFecha()).orElse(new Date()));
        formulario.setNumero(formularioDTO.getNumero());
        formulario.setEvaluacion(formularioDTO.getEvaluacion());

        // Asignar EstadoFormulario
        EstadoFormulario estadoFormulario = estadoFormularioRepository.findById(formularioDTO.getEstadoFormularioId())
                .orElseThrow(() -> new RuntimeException("EstadoFormulario no encontrado"));
        formulario.setEstadoFormulario(estadoFormulario);

        // Asignar IDs de Perito, Proveedor y Categoria
        formulario.setId_perito(formularioDTO.getPeritoId());
        formulario.setId_proveedor(formularioDTO.getProveedorId());
        formulario.setId_categoria(formularioDTO.getCategoridaId());

        // Guardar el formulario actualizado
        FormularioEvaluacion updatedFormulario = formularioRepository.save(formulario);

        // Actualizar y guardar los detalles del formulario
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

        // Completar el formulario con datos externos
        FormularioEvaluacion completedFormulario = completeFormularioWithExternalData(updatedFormulario);

        // Convertir a DTO
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
            // Crear un nuevo documento siempre
            Documento documento = new Documento();
            documento.setNombre(dto.getDocumento().getNombre());
            documento.setPath(dto.getDocumento().getPath());
            documento = documentoRepository.save(documento); // Guardar el nuevo documento
            detalle.setDocumento(documento);
        }

        detalle.setId_matrizevaluacion(dto.getId_matrizevaluacion());

        return detalle;
    }

    @Override
    public void deleteFormulario(Long id) {
        formularioRepository.deleteById(id);
    }

    private FormularioDTO convertToDTO(FormularioEvaluacion formulario) {
        FormularioDTO dto = new FormularioDTO();
        dto.setId(formulario.getId());
        dto.setFecha(formulario.getFecha());
        dto.setNumero(formulario.getNumero());
        dto.setEvaluacion(formulario.getEvaluacion());

        if (formulario.getEstadoFormulario() != null) {
            EstadoFormularioDTO estadoDTO = new EstadoFormularioDTO();
            estadoDTO.setId(formulario.getEstadoFormulario().getId());
            estadoDTO.setNombre(formulario.getEstadoFormulario().getNombre());
            dto.setEstadoFormulario(estadoDTO);
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

        // Asignar EstadoFormulario
        EstadoFormulario estadoFormulario = estadoFormularioRepository.findById(dto.getEstadoFormularioId())
                .orElseThrow(() -> new RuntimeException("EstadoFormulario no encontrado"));
        formulario.setEstadoFormulario(estadoFormulario);

        // Asignar IDs de Perito, Proveedor y Categoria
        formulario.setId_perito(dto.getPeritoId());
        formulario.setId_proveedor(dto.getProveedorId());
        formulario.setId_categoria(dto.getCategoridaId());

        return formulario;
    }

    public FormularioDTO getFormularioEvaluacion(Long formularioId) {
        // Obtén el FormularioEvaluacion desde el repositorio
        FormularioEvaluacion formularioEvaluacion = formularioRepository.findById(formularioId)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado"));

        // Obtén los IDs de Proveedor y Perito desde el formulario
        Long proveedorId = formularioEvaluacion.getId_proveedor();
        Long categoriaId = formularioEvaluacion.getId_categoria();
        Long peritoId = formularioEvaluacion.getId_perito();

        // Llama al microservicio de proveedor para obtener la información del proveedor
        WebClient webClient = webClientBuilder.build();
        Mono<Proveedor> proveedorMono = webClient.get()
                .uri("http://localhost:8081/api/empresa/proveedor/findbyid/{id}", proveedorId)
                .retrieve()
                .bodyToMono(Proveedor.class);
        Proveedor proveedor = proveedorMono.block();
        formularioEvaluacion.setProveedor(proveedor);

        Mono<Categoria> categoriaMono = webClient.get()
                .uri("http://localhost:8081/api/empresa/categoria/findbyid/{id}", categoriaId)
                .retrieve()
                .bodyToMono(Categoria.class);
        Categoria categoria = categoriaMono.block();
        formularioEvaluacion.setCategoria(categoria);

        Mono<Perito> peritoMono = webClient.get()
                .uri("http://localhost:8081/api/empresa/perito/findbyid/{id}", peritoId)
                .retrieve()
                .bodyToMono(Perito.class);
        Perito perito = peritoMono.block();
        formularioEvaluacion.setPerito(perito);

        return convertToDTO(formularioEvaluacion);
    }
}