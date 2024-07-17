package com.udla.evaluaytor.businessdomain.evaluacion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udla.evaluaytor.businessdomain.evaluacion.dto.DetalleFormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.DetalleFormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.DocumentoDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoDetalleDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoFormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacion;
import com.udla.evaluaytor.businessdomain.evaluacion.models.FormularioEvaluacionDetalle;
import com.udla.evaluaytor.businessdomain.evaluacion.services.DetalleFormularioService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.DocumentoService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.EstadoDetalleService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.EstadoFormularioService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.FormularioService;

@RestController
@RequestMapping("/api/evaluacion")

    
public class EvaluacionController {
    @Autowired
    private FormularioService formularioService;

    //FORMULARIO

    @GetMapping("/findall")
    public List<FormularioDTO> getAllFormularios() {
        return formularioService.getAllFormularios();
    }

    @GetMapping("/findbyid/{id}")
    public ResponseEntity<FormularioDTO> getFormularioById(@PathVariable Long id) {
        FormularioDTO formulario = formularioService.getFormularioById(id);
        if (formulario != null) {
            return ResponseEntity.ok(formulario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<FormularioDTO> createFormulario(@RequestBody FormularioCreateUpdateDTO formularioDTO) {
        FormularioDTO createdFormulario = formularioService.createFormulario(formularioDTO);
        return ResponseEntity.ok(createdFormulario);
    }

    @PutMapping("/updatebyid/{id}")
    public ResponseEntity<FormularioDTO> updateFormulario(@PathVariable Long id, @RequestBody FormularioCreateUpdateDTO formularioDTO) {
        FormularioDTO updatedFormulario = formularioService.updateFormulario(id, formularioDTO);
        if (updatedFormulario != null) {
            return ResponseEntity.ok(updatedFormulario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/deletebyid/{id}")
    public ResponseEntity<Void> deleteFormulario(@PathVariable Long id) {
        formularioService.deleteFormulario(id);
        return ResponseEntity.noContent().build();
    }

    //ESTADO FORMULARIO

    @Autowired
    private EstadoFormularioService estadoFormularioService;

    @GetMapping("/estadoevaluacion/findall")
    public List<EstadoFormularioDTO> getAllEstadosFormulario() {
        return estadoFormularioService.getAllEstadosFormulario();
    }

    @GetMapping("/estadoevaluacion/findbyid/{id}")
    public ResponseEntity<EstadoFormularioDTO> getEstadoFormularioById(@PathVariable Long id) {
        EstadoFormularioDTO estado = estadoFormularioService.getEstadoFormularioById(id);
        if (estado != null) {
            return ResponseEntity.ok(estado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/estadoevaluacion/save")
    public ResponseEntity<EstadoFormularioDTO> createEstadoFormulario(@RequestBody EstadoFormularioDTO estadoFormularioDTO) {
        EstadoFormularioDTO createdEstado = estadoFormularioService.createEstadoFormulario(estadoFormularioDTO);
        return ResponseEntity.ok(createdEstado);
    }

    @PutMapping("/estadoevaluacion/updatebyid/{id}")
    public ResponseEntity<EstadoFormularioDTO> updateEstadoFormulario(@PathVariable Long id, @RequestBody EstadoFormularioDTO estadoFormularioDTO) {
        EstadoFormularioDTO updatedEstado = estadoFormularioService.updateEstadoFormulario(id, estadoFormularioDTO);
        if (updatedEstado != null) {
            return ResponseEntity.ok(updatedEstado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/estadoevaluacion/deletebyid/{id}")
    public ResponseEntity<Void> deleteEstadoFormulario(@PathVariable Long id) {
        estadoFormularioService.deleteEstadoFormulario(id);
        return ResponseEntity.noContent().build();
    }

    //DETALLE FORMULARIO
    @Autowired
    private DetalleFormularioService detalleFormularioService;

    @GetMapping("/detalleformulario/findall")
    public List<DetalleFormularioDTO> getAllDetallesFormulario() {
        return detalleFormularioService.getAllDetallesFormulario();
    }

    @GetMapping("/detalleformulario/findbyid/{id}")
    public ResponseEntity<DetalleFormularioDTO> getDetalleFormularioById(@PathVariable Long id) {
        DetalleFormularioDTO detalle = detalleFormularioService.getDetalleFormularioById(id);
        if (detalle != null) {
            return ResponseEntity.ok(detalle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/detalleformulario/save")
    public ResponseEntity<DetalleFormularioDTO> createDetalleFormulario(@RequestBody DetalleFormularioCreateUpdateDTO detalleFormularioDTO) {
        DetalleFormularioDTO createdDetalle = detalleFormularioService.createDetalleFormulario(detalleFormularioDTO);
        return ResponseEntity.ok(createdDetalle);
    }

    @PutMapping("/detalleformulario/updatebyid/{id}")
    public ResponseEntity<DetalleFormularioDTO> updateDetalleFormulario(@PathVariable Long id, @RequestBody DetalleFormularioCreateUpdateDTO detalleFormularioDTO) {
        DetalleFormularioDTO updatedDetalle = detalleFormularioService.updateDetalleFormulario(id, detalleFormularioDTO);
        if (updatedDetalle != null) {
            return ResponseEntity.ok(updatedDetalle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/detalleformulario/deletebyid/{id}")
    public ResponseEntity<Void> deleteDetalleFormulario(@PathVariable Long id) {
        detalleFormularioService.deleteDetalleFormulario(id);
        return ResponseEntity.noContent().build();
    }

    //ESTADO DETALLE
    @Autowired
    private EstadoDetalleService estadoDetalleService;

    @GetMapping("/estadodetalle/findall")
    public List<EstadoDetalleDTO> getAllEstadosDetalle() {
        return estadoDetalleService.getAllEstadosDetalle();
    }

    @GetMapping("/estadodetalle/findbyid/{id}")
    public ResponseEntity<EstadoDetalleDTO> getEstadoDetalleById(@PathVariable Long id) {
        EstadoDetalleDTO estado = estadoDetalleService.getEstadoDetalleById(id);
        if (estado != null) {
            return ResponseEntity.ok(estado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/estadodetalle/save")
    public ResponseEntity<EstadoDetalleDTO> createEstadoDetalle(@RequestBody EstadoDetalleDTO estadoDetalleDTO) {
        EstadoDetalleDTO createdEstado = estadoDetalleService.createEstadoDetalle(estadoDetalleDTO);
        return ResponseEntity.ok(createdEstado);
    }

    @PutMapping("/estadodetalle/updatebyid/{id}")
    public ResponseEntity<EstadoDetalleDTO> updateEstadoDetalle(@PathVariable Long id, @RequestBody EstadoDetalleDTO estadoDetalleDTO) {
        EstadoDetalleDTO updatedEstado = estadoDetalleService.updateEstadoDetalle(id, estadoDetalleDTO);
        if (updatedEstado != null) {
            return ResponseEntity.ok(updatedEstado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/estadodetalle/deletebyid/{id}")
    public ResponseEntity<Void> deleteEstadoDetalle(@PathVariable Long id) {
        estadoDetalleService.deleteEstadoDetalle(id);
        return ResponseEntity.noContent().build();
    }

    
//DOCUMENTO

@Autowired
    private DocumentoService documentoService;

    @GetMapping("/documento/findall")
    public List<DocumentoDTO> getAllDocumentos() {
        return documentoService.getAllDocumentos();
    }

    @GetMapping("/documento/findbyid/{id}")
    public ResponseEntity<DocumentoDTO> getDocumentoById(@PathVariable Long id) {
        DocumentoDTO documento = documentoService.getDocumentoById(id);
        if (documento != null) {
            return ResponseEntity.ok(documento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/documento/save")
    public ResponseEntity<DocumentoDTO> createDocumento(@RequestBody DocumentoDTO documentoDTO) {
        DocumentoDTO createdDocumento = documentoService.createDocumento(documentoDTO);
        return ResponseEntity.ok(createdDocumento);
    }

    @PutMapping("/documento/updatebyid/{id}")
    public ResponseEntity<DocumentoDTO> updateDocumento(@PathVariable Long id, @RequestBody DocumentoDTO documentoDTO) {
        DocumentoDTO updatedDocumento = documentoService.updateDocumento(id, documentoDTO);
        if (updatedDocumento != null) {
            return ResponseEntity.ok(updatedDocumento);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/documento/deletebyid/{id}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.noContent().build();
    }

    //METODO PROFE
    @GetMapping("/formulario/{formularioId}")
    public FormularioDTO getFormularioEvaluacion(@PathVariable Long formularioId) {
        return formularioService.getFormularioEvaluacion(formularioId);
    }

    
    //METODO PROFE
    @GetMapping("/formularioDetalleprueba/{detalleId}")
    public DetalleFormularioDTO getFormularioEvaluacionDetalle(@PathVariable Long detalleId) {
        return detalleFormularioService.getFormularioEvaluacionDetalle(detalleId);
    }
}