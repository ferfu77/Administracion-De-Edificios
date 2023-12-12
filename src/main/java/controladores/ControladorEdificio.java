package controladores;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import exceptions.EdificioException;
import exceptions.PersonaException;
import jakarta.transaction.Transactional;
import views.PersonaView;
import views.UnidadView;

@Transactional
@RestController
@RequestMapping("/api/edificios")
public class ControladorEdificio {
	@Autowired
	private EdificioRepository edificioRepository;
	
	@Autowired
	private EdificioService edificioService;
	
	@Autowired
	private PersonaService personaService;
	
	@GetMapping("/listar")
	public ResponseEntity<List<Edificio>> listarEdificio(Pageable pageable){
	    Page<Edificio> edificiosPage = edificioRepository.findAll(pageable);
	    List<Edificio> edificios = edificiosPage.getContent(); 
	    return ResponseEntity.ok(edificios);
	}

	
	@GetMapping("/listar/{codigoedificio}")
	public ResponseEntity<?> getUnidadesPorEdificio(@PathVariable("codigoedificio") Integer codigoedificio) {
	    try {
	        Edificio edificio = edificioService.buscarEdificio(codigoedificio);
	        if (edificio == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body("No se encontró ningún edificio con el código: " + codigoedificio);
	        }

	        List<Unidad> unidades = edificio.getUnidades();
	        List<UnidadView> resultado = new ArrayList<>();
	        for (Unidad unidad : unidades) {
	            resultado.add(unidad.toView());
	        }

	        return ResponseEntity.ok(resultado);
	    } catch (EdificioException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("No se encontró ningún edificio con el código: " + codigoedificio);
	    }
	}



    
	@GetMapping("/habilitados/{codigoedificio}")
	public ResponseEntity<?> getHabilitadosPorEdificio(@PathVariable("codigoedificio") Integer codigoedificio) {
	    try {
	        Edificio edificio = edificioService.buscarEdificio(codigoedificio);
	        Set<Persona> habilitados = edificio.habilitados();
	        List<PersonaView> resultado = new ArrayList<>();
	        for (Persona persona : habilitados) {
	            resultado.add(persona.toView());
	        }
	        return ResponseEntity.ok(resultado);
	    } catch (EdificioException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún edificio con el código: " + codigoedificio);
	    }
	}

    
    
    
	@GetMapping("/dueniosPorEdificio/{codigoedificio}")
	public ResponseEntity<?> dueniosPorEdificio(@PathVariable("codigoedificio") Integer codigoedificio) {
	    try {
	        Edificio edificio = edificioService.buscarEdificio(codigoedificio);
	        Set<Persona> duenios = edificio.duenios();
	        List<PersonaView> resultado = new ArrayList<>();
	        for (Persona persona : duenios) {
	            resultado.add(persona.toView());
	        }
	        return ResponseEntity.ok(resultado);
	    } catch (EdificioException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún dueño en el edificio: " + codigoedificio);
	    }
	}



	@GetMapping("/habitantesPorEdificio/{codigoedificio}")
	public ResponseEntity<?> habitantesPorEdificio(@PathVariable("codigoedificio") Integer codigoedificio) {
	    try {
	        Edificio edificio = edificioService.buscarEdificio(codigoedificio);
	        Set<Persona> habitantes = edificio.habitantes();
	        List<PersonaView> resultado = new ArrayList<>();
	        for (Persona persona : habitantes) {
	            resultado.add(persona.toView());
	        }
	        return ResponseEntity.ok(resultado);
	    } catch (EdificioException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún habitante en el edificio: " + codigoedificio);
	    }
	}

    
    
	
    

	@PutMapping("/guardar")
	public ResponseEntity<String> guardarEdificio(@RequestBody Edificio edificio) {
	    try {
	        Edificio edificioGuardado = edificioRepository.save(edificio);
	        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest().path("/{codigo}")
	                .buildAndExpand(edificioGuardado.getCodigo()).toUri();
	        return ResponseEntity.created(ubicacion).body("Edificio guardado correctamente.");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("Error al guardar el edificio: " + e.getMessage());
	    }
	}


	
	
	@DeleteMapping("/eliminar/{codigo}")
	public ResponseEntity<?> deleteByCodigo(@PathVariable("codigo") Integer codigo) {
	    try {
	        Optional<Edificio> o = edificioRepository.findByCodigo(codigo);
	        if (!o.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe edificio con el código: " + codigo);
	        }
	        edificioRepository.delete(o.get());
	        return ResponseEntity.ok("Edificio eliminado correctamente");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el edificio: " + e.getMessage());
	    }
	}

	
	
	@PutMapping("/actualizar/{codigo}")
	public ResponseEntity<?> actualizarEdificio(@PathVariable int codigo, @RequestBody Edificio edificio) {
	    try {
	        Optional<Edificio> o = edificioRepository.findByCodigo(codigo);
	        if (!o.isPresent()) {
	            return ResponseEntity.unprocessableEntity().build();
	        }
	        Edificio edificioExistente = o.get();
	        
	        edificioExistente.setNombre(edificio.getNombre());
	        edificioExistente.setDireccion(edificio.getDireccion());
	        
	        edificioRepository.save(edificioExistente);
	        return ResponseEntity.noContent().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

	
	@GetMapping("buscar/{codigo}")
    public ResponseEntity<?> buscarPersonaPorDocumento(@PathVariable Integer codigo) {
        try {
            Edificio edificio= edificioService.buscarEdificioPorCodigo(codigo);
            return new ResponseEntity<>(edificio, HttpStatus.OK);
        } catch (EdificioException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
	
}
