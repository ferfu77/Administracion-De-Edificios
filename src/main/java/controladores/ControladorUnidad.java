package controladores;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import exceptions.EdificioException;
import exceptions.PersonaException;
import exceptions.UnidadException;
import jakarta.transaction.Transactional;
import views.PersonaView;
import views.UnidadView;

@Transactional
@RestController
@RequestMapping("/api/unidades")
public class ControladorUnidad {

    @Autowired
    private UnidadRepository unidadRepository;

	@Autowired
	private UnidadService unidadService;
	
	@Autowired
	private EdificioRepository edificioRepository;
	
	@Autowired
	private PersonaService personaService;
	
	
    @GetMapping("/porDuenio")
    public List<UnidadView> obtenerUnidadesPorDuenios(@RequestParam("mail") String mail) {
        return unidadService.unidadesPorDuenios(mail);
    }
    
    @GetMapping("/porInquilinos")
    public List<UnidadView> obtenerUnidadesPorInquilinos(@RequestParam("mail") String mail) {
        return unidadService.unidadesPorInquilinos(mail);
    }
	
	@GetMapping("/listar")
	public ResponseEntity<List<Unidad>> getUnidades() {
	    List<Unidad> unidades = unidadRepository.findAll();
	    return ResponseEntity.ok(unidades);
	}
	
	@GetMapping("/dueniosPorUnidad/{id}")
	public ResponseEntity<List<PersonaView>> dueniosPorUnidad(@PathVariable("id") Integer id, String piso, String numero) {
	    try {
	        Unidad unidad = unidadService.buscarUnidad(id, piso, numero);
	        if (unidad != null) {
	            List<Persona> duenios = unidad.getDuenios();
	            List<PersonaView> resultado = duenios.stream().map(Persona::toView).collect(Collectors.toList());
	            return ResponseEntity.ok(resultado);
	        } else {
	            throw new UnidadException("No se encontró ninguna unidad con el ID proporcionado");
	        }
	    } catch (UnidadException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}




    
	@GetMapping("/inquilinosPorUnidad/{id}")
	public ResponseEntity<?> inquilinosPorUnidad(@PathVariable("id") Integer id, String piso, String numero) {
	    try {
	        Unidad unidad = unidadService.buscarUnidad(id, piso, numero);
	        List<Persona> inquilinos = unidad.getInquilinos();

	        if (inquilinos.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("No hay inquilinos para la unidad con el ID: " + id);
	        }

	        List<PersonaView> resultado = inquilinos.stream()
	                .map(Persona::toView)
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(resultado);
	    } catch (UnidadException u) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("No se encontró ninguna unidad con el ID: " + id);
	    }
	}


    
    
    @PostMapping("/agregarDuenioUnidad")
    public ResponseEntity<String> agregarDuenioUnidad(@RequestParam Integer id, 
                                                     @RequestParam String piso,
                                                     @RequestParam String numero,
                                                     @RequestParam String documento) {
        try {
            unidadService.agregarDuenioUnidad(id, piso, numero, documento);
            return ResponseEntity.ok("Se agregó un nuevo dueño a la unidad con éxito.");
        } catch (UnidadException | PersonaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No se pudo agregar el dueño a la unidad: " + e.getMessage());
        }
    }
    
    @PostMapping("/agregarInquilinoUnidad")
    public ResponseEntity<String> agregarInquilinoUnidad(@RequestParam Integer id, 
                                                     @RequestParam String piso,
                                                     @RequestParam String numero,
                                                     @RequestParam String documento) {
        try {
            unidadService.agregarInquilino(id, piso, numero, documento); // Modifica este método en el servicio para permitir agregar inquilinos sin verificar habitabilidad
            return ResponseEntity.ok("Se agregó un nuevo inquilino a la unidad con éxito.");
        } catch (UnidadException | PersonaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No se pudo agregar el inquilino a la unidad: " + e.getMessage());
        }
    }
	
	@PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarUnidad(@PathVariable int id,@RequestParam String documento,  @RequestBody Unidad unidad ) {
		if (personaService.esAdministrador(documento))
		{
			try {
				Optional<Unidad> o= unidadRepository.findById(id);
				if (!o.isPresent())
						{
						return ResponseEntity.unprocessableEntity().build();
						}
				unidad.setId(o.get().getId());
				unidadRepository.save(unidad);
		        return ResponseEntity.noContent().build();
			}
			catch(Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( e.getMessage());
			}
		}
			else
			{
				  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tienes permisos para realizar esta acción.");
			}
		
	
    }
	
	@PostMapping("/liberarInquilino")
	public ResponseEntity<String> liberarUnidad(@RequestParam("id") Integer id) {
	    try {
	        unidadService.liberarUnidadPorId(id);
	        return new ResponseEntity<>("Unidad liberada exitosamente", HttpStatus.OK);
	    } catch (UnidadException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@PostMapping("/crear")
	public ResponseEntity<String> crearUnidad(
	        @RequestParam String piso,
	        @RequestParam String numero,
	        @RequestParam boolean habitado,
	        @RequestParam Integer codigoedificio
	) {
	    try {
	        Unidad nuevaUnidad = unidadService.crearUnidad(piso, numero, habitado, codigoedificio);
	        return ResponseEntity.ok("Unidad creada con ID: " + nuevaUnidad.getId());
	    } catch (EdificioException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    }
	}

    
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarUnidadPorId(@PathVariable("id") Integer id) {
        try {
            Unidad unidad = unidadService.buscarUnidadPorId(id);
            if (unidad != null) {
                return ResponseEntity.ok(unidad);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unidad no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PostMapping("/alquilar")
    public ResponseEntity<String> alquilarUnidad(@RequestBody TransferenciaUnidadRequest request) {
        try {
            unidadService.transferirUnidad(
                    request.getId(),
                    request.getPiso(),
                    request.getNumero(),
                    request.getDocumento()
                );
            
            unidadService.establecerUnidadComoHabitada(request.getId());
            return ResponseEntity.ok("Unidad alquilada correctamente.");
        } catch (PersonaException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Acceso denegado: " + e.getMessage());
        } catch (UnidadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No se pudo transferir la unidad: " + e.getMessage());
        }
    }

    
    @PostMapping("/transferirUnidad")
    public ResponseEntity<String> transferirUnidad(@RequestBody TransferenciaUnidadRequest request) {
        try {
            unidadService.transferirUnidad(
                request.getId(),
                request.getPiso(),
                request.getNumero(),
                request.getDocumento()
            );
            return ResponseEntity.ok("La unidad ha sido transferida con éxito.");
        } catch (PersonaException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Acceso denegado: " + e.getMessage());
        } catch (UnidadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("No se pudo transferir la unidad: " + e.getMessage());
        }
    }

    
    @PostMapping("/habitar")
    public ResponseEntity<String> habitarUnidad(
            @RequestParam Integer id,
            @RequestParam String piso,
            @RequestParam String numero
    ) {
        try {
            unidadService.habitarUnidad(id, piso, numero);
            return ResponseEntity.ok("Unidad habitada exitosamente");
        } catch (UnidadException e) {
            if (e.getMessage().contains("No se encontró ninguna unidad")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La ID de la unidad es incorrecta");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }


    
    @PostMapping("/{id}/sacarinquilino")
    public ResponseEntity<String> sacarInquilinoDeUnidad(@PathVariable("id") Integer id) {
        try {
            unidadService.sacarInquilino(id);
            return ResponseEntity.ok("Inquilinos removidos de la unidad correctamente");
        } catch (UnidadException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarUnidad(@PathVariable("id") Integer id) {
        try {
            unidadService.eliminarUnidad(id);
            return ResponseEntity.ok("Unidad eliminada correctamente");
        } catch (UnidadException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/sacarduenio")
    public ResponseEntity<String> sacarDuenioDeUnidad(@PathVariable("id") Integer id) {
        try {
            unidadService.sacarDuenio(id);
            return ResponseEntity.ok("Dueños removidos de la unidad correctamente");
        } catch (UnidadException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    

    
    
}



