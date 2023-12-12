package controladores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import exceptions.EdificioException;
import exceptions.PersonaException;
import exceptions.ReclamoException;
import exceptions.UnidadException;
import views.ImagenView;
import views.ReclamoView;

@Service
public class ReclamoService {
	
	@Autowired
	private ReclamoRepository reclamoRepository;
	
	@Autowired
	private EdificioService edificioService;
	@Autowired
	private UnidadService unidadService;
	
	@Autowired
	private PersonaService personaService;
	
    public List<ReclamoView> obtenerReclamosPorEdificio(Integer codigo) {
        List<Reclamo> reclamos = reclamoRepository.findByEdificio_Codigo(codigo);
        List<ReclamoView> reclamoViews = new ArrayList<>();

        for (Reclamo reclamo : reclamos) {
            reclamoViews.add(reclamo.toView());
        }

        return reclamoViews;
    }
    
    public List<ReclamoView> obtenerReclamosPorUnidad(Integer id) {
        List<Reclamo> reclamos = reclamoRepository.findByUnidad_Id(id);
        List<ReclamoView> reclamoViews = new ArrayList<>();

        for (Reclamo reclamo : reclamos) {
            reclamoViews.add(reclamo.toView());
        }

        return reclamoViews;
    }
    
    public Reclamo buscarReclamo(Integer idreclamo) throws ReclamoException {
        Optional<Reclamo> optionalReclamo = reclamoRepository.findByIdreclamo(idreclamo);
        if (optionalReclamo.isPresent()) {
            return optionalReclamo.get();
        } else {
            throw new ReclamoException("No se encontró ningún edificio con el código: " + idreclamo);
        }
    }
    
    
    //Unidad se busca por su ID no por el codigodeedificio

	
	
	public void agregarImagenAReclamo(Integer idreclamo, String direccion, String tipo) throws ReclamoException {
	    Optional<Reclamo> optionalReclamo = reclamoRepository.findByIdreclamo(idreclamo);
	    if (optionalReclamo.isPresent()) {
	        Reclamo reclamo = optionalReclamo.get();
	        Imagen imagen = new Imagen(direccion, tipo);
	        reclamo.agregarImagen(imagen); // Agregar imagen al reclamo
	        
	        reclamoRepository.save(reclamo);
	    } else {
	        throw new ReclamoException("No se encontró ningún reclamo con el ID: " + idreclamo);
	    }
    
	 }
	
	public void cambiarEstado(Integer idreclamo, Estado estado) throws ReclamoException {
		Reclamo reclamo = buscarReclamo(idreclamo);
		reclamo.cambiarEstado(estado);
		reclamoRepository.save(reclamo);
	}
	
    public List<ReclamoView> reclamosPorPersona(String mail) {
        List<ReclamoView> resultado = new ArrayList<>();
        
        List<Reclamo> reclamos = reclamoRepository.findByUsuarioMail(mail);
        
        for (Reclamo reclamo : reclamos) {
            resultado.add(reclamo.toView()); 
        }
        
        return resultado;
    }
    

    public void eliminarReclamoPorId(Integer idreclamo) throws ReclamoException {
        Optional<Reclamo> reclamoOptional = reclamoRepository.findByIdreclamo(idreclamo);
        if (reclamoOptional.isPresent()) {
            Reclamo reclamo = reclamoOptional.get();
            reclamoRepository.delete(reclamo);
        } else {
            throw new ReclamoException("Reclamo no encontrado con ID: " + idreclamo);
        }
    }
    
    public Reclamo actualizarReclamo(Integer idreclamo, Reclamo reclamo, int id) throws ReclamoException, UnidadException {
        Reclamo reclamoExistente = reclamoRepository.findByIdreclamo(idreclamo).orElse(null);
        Unidad unidad= unidadService.buscarUnidadPorId(id);
        reclamoExistente.setUnidad(unidad);

        if (reclamoExistente == null) {
            throw new ReclamoException("El reclamo con ID " + idreclamo + " no fue encontrado.");
        }
        reclamoExistente.setUbicacion(reclamo.getUbicacion());
        reclamoExistente.setDescripcion(reclamo.getDescripcion());
        // ... Actualiza otros campos según tu lógica

        return reclamoRepository.save(reclamoExistente);
    }

    public List<ImagenDTO> listarReclamos() {
        List<Reclamo> reclamos = reclamoRepository.findAll(); 
        List<ImagenDTO> reclamosConImagenes = new ArrayList<>();
        
        for (Reclamo reclamo : reclamos) {
            List<ImagenView> imagenes = obtenerImagenesPorReclamo(reclamo.getIdReclamo());
            ImagenDTO imagenDTO = new ImagenDTO(reclamo, imagenes);
            reclamosConImagenes.add(imagenDTO);
        }

        return reclamosConImagenes;
    }

    
    private List<ImagenView> obtenerImagenesPorReclamo(Integer idReclamo) {
        Reclamo reclamo = reclamoRepository.findById(idReclamo).orElse(null); // Buscar el reclamo por su ID

        if (reclamo != null) {
            List<Imagen> imagenes = reclamo.getImagenes(); // Suponiendo que tienes un método para obtener las imágenes asociadas al reclamo
            List<ImagenView> imagenesView = new ArrayList<>();

            for (Imagen imagen : imagenes) {
                // Suponiendo que Imagen tiene un método toView() para convertirse a ImagenView
                imagenesView.add(imagen.toView());
            }
            return imagenesView;
        } else {
            // Manejo si el reclamo no se encuentra
            return Collections.emptyList();
        }
    }
    
    public Integer agregarReclamo(Integer codigo, Integer id, String piso, String numero, String documento, String ubicacion, String descripcion) throws EdificioException, UnidadException, PersonaException {
        Edificio edificio = edificioService.buscarEdificioPorCodigo(codigo);
        Unidad unidad = unidadService.buscarUnidadPorId(id);

        // Verificar si la persona es propietaria o inquilina de la unidad
        Persona persona = personaService.buscarPersonaPorDocumento(documento);
        boolean esPropietario = unidad.getDuenios().contains(persona);
        boolean esInquilino = unidad.getInquilinos().contains(persona);

        if (!esPropietario && !esInquilino) {
            throw new PersonaException("La persona no es propietaria ni inquilina de la unidad.");
        }

        Reclamo reclamo = new Reclamo(persona, edificio, ubicacion, descripcion, unidad);
        reclamoRepository.save(reclamo);
        return reclamo.getIdReclamo();
    }

    


	
	
	
	
	


}
