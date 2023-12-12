package controladores;

import java.net.URI;
import java.util.List;
import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import exceptions.PersonaException;
import exceptions.UnidadException;
import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;

@Transactional
@RestController
@RequestMapping("/api/persona")
public class ControladorPersona {

	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private PersonaService personaService;
	
	@Autowired
	private UnidadService unidadService;
	

	
	@PostMapping("/guardar")
	public ResponseEntity<Persona> guardarPersona(@RequestBody Persona persona) {
		Persona personaGuardada = personaRepository.save(persona);
		URI ubicacion= ServletUriComponentsBuilder.fromCurrentRequest().path("/{documento}")
				.buildAndExpand(personaGuardada.getDocumento()).toUri();
		return ResponseEntity.created(ubicacion).body(personaGuardada);
	}
	
	@GetMapping("/listar")
	public ResponseEntity<List<Persona>> listarPersona( ){
		 List<Persona> personas = personaRepository.findAll();
	     return ResponseEntity.ok(personas);

	}
//	@GetMapping("/obtener/{documento}")
//	public ResponseEntity<Persona> findByDocumento(@PathVariable("documento")String documento){
//		Optional<Persona> o= personaRepository.findByDocumento(documento);
//		if (!o.isPresent())
//				{
//				return ResponseEntity.unprocessableEntity().build();
//				}
//				return ResponseEntity.ok(o.get()); 
//	}
	@PutMapping("/actualizar/{documento}")
	public ResponseEntity<?> actualizarPersona(@PathVariable String documento, @RequestBody Persona persona) {
	    try {
	        Optional<Persona> o = personaRepository.findByDocumento(documento);
	        if (!o.isPresent()) {
	            return ResponseEntity.unprocessableEntity().build();
	        }
	        Persona personaExistente = o.get();
	        // Actualizar los campos necesarios de la persona existente con la información proporcionada en el cuerpo de la solicitud
	        personaExistente.setNombre(persona.getNombre());
	        personaExistente.setMail(persona.getMail());
	        // ... actualizar otros campos si es necesario

	        personaRepository.save(personaExistente);
	        return ResponseEntity.noContent().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

	 
	@DeleteMapping("/eliminar/{documento}")
	public ResponseEntity<String> deleteByDocumento(@PathVariable String documento) {
	    try {
	        Optional<Persona> personaOptional = personaRepository.findByDocumento(documento);
	        if (personaOptional.isPresent()) {
	            Persona persona = personaOptional.get();
	            if (persona.getDocumento() != null) {
	                if (unidadService.estaHabitada(persona.getDocumento())) {
	                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                            .body("La persona está asociada a una unidad habitada, no se puede eliminar.");
	                } else {
	                    personaRepository.deleteByDocumento(documento);
	                    return ResponseEntity.ok("Persona eliminada exitosamente");
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El documento de la persona es nulo.");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La persona no fue encontrada.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}






	
	
	
	//@PutMapping("/actualizar/{documento}")
   // public ResponseEntity<Persona> actualizar(@PathVariable String documento, @RequestBody Persona persona) {
	//	Optional<Persona> o= personaRepository.findByDocumento(documento);
		//if (!o.isPresent())
			//	{
				//return ResponseEntity.unprocessableEntity().build();
				//}
       // persona.setDocumento(o.get().getDocumento());
        //personaRepository.save(persona);
        //return ResponseEntity.noContent().build();
   // }
	
    @GetMapping("/buscar/{documento}")
    public ResponseEntity<?> buscarPersonaPorDocumento(@PathVariable String documento) {
        try {
            Persona persona = personaService.buscarPersonaPorDocumento(documento);
            return new ResponseEntity<>(persona, HttpStatus.OK);
        } catch (PersonaException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/asignaradministrador/{mail}")
    public ResponseEntity<String> asignarAdministrador(@PathVariable String mail) {
        try {
            personaService.asignarComoAdministrador(mail);
            return ResponseEntity.ok("Persona asignada como administrador correctamente.");
        } catch (PersonaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String mail, @RequestParam String password) {
        Optional<Persona> personaOptional = personaRepository.findByMail(mail);

        if (personaOptional.isPresent()) {
            Persona persona = personaOptional.get();
            if (persona.getPassword().equals(password)) {
                boolean Administrador = persona.getAdministrador();
                if (Administrador) {
                    return ResponseEntity.ok("Bienvenido, eres un administrador.");
                } else {
                    return ResponseEntity.ok("Bienvenido, inicio de sesion exitoso");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
    @PutMapping("/cambiar-atributos")
    public ResponseEntity<String> cambiarAtributosPersona(
            @RequestParam String documento,
            @RequestParam String mail,
            @RequestBody Persona persona) {

        // Verificar si el documento que hace la solicitud es administrador
        if (personaService.esAdministrador(mail)) {
            try {
                boolean actualizado = personaService.actualizarPersona(mail , persona);
                if (actualizado) {
                    return ResponseEntity.ok("Los atributos de la persona han sido actualizados correctamente.");
                } else {
                    return ResponseEntity.badRequest().body("No se pudo actualizar los atributos de la persona.");
                }
            } catch (PersonaException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tienes permisos para realizar esta acción.");
        }
    }
    
    @GetMapping("/buscar-por-email")
    public ResponseEntity<?> buscarPersonaPorEmail(@RequestParam String mail) {
        try {
            Optional<Persona> personaOptional = personaRepository.findByMail(mail);
            if (personaOptional.isPresent()) {
                Persona persona = personaOptional.get();
                return new ResponseEntity<>(persona, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    
    
    
   

}
