package controladores;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import exceptions.ReclamoException;
import exceptions.UnidadException;
import jakarta.transaction.Transactional;
import views.ReclamoView;

@Transactional
@RestController
@RequestMapping("/api/reclamos")
public class ControladorReclamo {
	@Autowired
	private ReclamoRepository reclamoRepository;
	
	@Autowired
	private ReclamoService reclamoService;
	@Autowired
	private EdificioRepository edificioRepository;
	@Autowired
	private PersonaService personaService;
		
    @GetMapping("/listar")
    public ResponseEntity<List<Reclamo>> listarEdificios() {
        List<Reclamo> reclamos = reclamoRepository.findAll();
        return ResponseEntity.ok(reclamos);
    }

		
		@GetMapping("/porEdificio/{codigo}")
		public ResponseEntity<?> reclamosPorEdificio(@PathVariable Integer codigo) {
		    try {
		        List<Reclamo> reclamos = reclamoRepository.findByEdificio_Codigo(codigo);
		        
		        if (reclamos.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron reclamos para el edificio con código: " + codigo);
		        }
		        
		        return ResponseEntity.ok(reclamos);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		    }
		}



	    
		@GetMapping("/porUnidad/{id}")
		public ResponseEntity<?> reclamosPorUnidad(@PathVariable Integer id) {
		    try {
		        List<Reclamo> reclamos = reclamoRepository.findByUnidad_Id(id);
		        
		        if (reclamos.isEmpty()) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron reclamos para la unidad con ID: " + id);
		        }
		        
		        return ResponseEntity.ok(reclamos);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		    }
		}

	        
	    
	    @PostMapping("/guardar")
	    public ResponseEntity<String> agregarReclamo(
	    		@RequestParam Integer codigo,
	    		@RequestParam Integer id,
	            @RequestParam String piso,
	            @RequestParam String numero,
	            @RequestParam String documento,     
	            @RequestParam String ubicacion,
	            @RequestParam String descripcion

	    ) {
	        try {
	            Integer idReclamo = reclamoService.agregarReclamo(codigo, id, piso,  numero,  documento, ubicacion, descripcion);
	            return new ResponseEntity<>("Reclamo agregado exitosamente con ID: " + idReclamo, HttpStatus.CREATED);
	        } catch (Exception e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	    @PutMapping("/actualizar/{idreclamo}")
	    public ResponseEntity<?> actualizarReclamo(@PathVariable Integer idreclamo, @RequestBody Reclamo reclamo, @RequestParam int id) throws UnidadException {
	        try {
	            Reclamo reclamoActualizado = reclamoService.actualizarReclamo(idreclamo, reclamo, id);
	            return ResponseEntity.ok(reclamoActualizado);
	        } catch (ReclamoException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        }
	    }

	    
		
	    @PostMapping("/{idreclamo}/agregar-imagen")
	    public ResponseEntity<String> agregarImagenAReclamo(
	            @PathVariable("idreclamo") Integer idreclamo,
	            @RequestParam String direccion,
	            @RequestParam String tipo
	    ) {
	        try {
	            reclamoService.agregarImagenAReclamo(idreclamo, direccion, tipo);
	            return new ResponseEntity<>("Imagen agregada al reclamo correctamente", HttpStatus.OK);
	        } catch (ReclamoException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	        }
	    }
	    
	    @GetMapping("/listar-con-imagenes")
	    public ResponseEntity<List<ImagenDTO>> listarReclamosConImagenes() {
	        List<ImagenDTO> reclamosConImagenes = reclamoService.listarReclamos();
	        return new ResponseEntity<>(reclamosConImagenes, HttpStatus.OK);
	    }

	    
	    


	    @GetMapping("/buscar/{idreclamo}")
	    public ResponseEntity<?> buscarReclamoPorId(@PathVariable("idreclamo") Integer idreclamo) {
	        try {
	            Reclamo reclamo = reclamoService.buscarReclamo(idreclamo);
	            if (reclamo != null) {
	                return ResponseEntity.ok(reclamo);
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reclamo no encontrado");
	            }
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	        }
	    }
	    
	    @PostMapping("/{idreclamo}/cambiarestado")
	    public ResponseEntity<String> cambiarEstadoReclamo(
	            @PathVariable("idreclamo") Integer idreclamo,
	            @RequestParam Estado estado
	    ) {
	        try {
	            reclamoService.cambiarEstado(idreclamo, estado);
	            return ResponseEntity.ok("Estado del reclamo actualizado correctamente");
	        } catch (ReclamoException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        }
	    }

	    
	    @GetMapping("/porPersona")
	    public ResponseEntity<?> reclamosPorPersona(@RequestParam("mail") String mail) {
	        try {
	            List<ReclamoView> reclamos = reclamoService.reclamosPorPersona(mail);
	            
	            if (reclamos.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron reclamos para la persona con documento: " + mail);
	            }
	            
	            return ResponseEntity.ok(reclamos);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
	    }

	    
	    
	    @DeleteMapping("/eliminar/{idreclamo}")
	    public ResponseEntity<String> eliminarReclamoPorId(@PathVariable("idreclamo") Integer idreclamo) {
	        try {
	            reclamoService.eliminarReclamoPorId(idreclamo);
	            return ResponseEntity.ok("Reclamo eliminado correctamente");
	        } catch (ReclamoException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	        }
	    }

}
