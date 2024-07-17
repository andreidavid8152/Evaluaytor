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

import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoDetalleDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.EstadoFormularioDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioCreateUpdateDTO;
import com.udla.evaluaytor.businessdomain.evaluacion.dto.FormularioDTO;

import com.udla.evaluaytor.businessdomain.evaluacion.services.EstadoDetalleService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.EstadoFormularioService;
import com.udla.evaluaytor.businessdomain.evaluacion.services.FormularioService;

@RestController
@RequestMapping("/api/evaluacion")

    
public class EvaluacionController {
    @Autowired
    private FormularioService formularioService;

    //EVALUACION
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
}