package controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import exceptions.EdificioException;
import exceptions.PersonaException;
import exceptions.UnidadException;
import jakarta.transaction.Transactional;
import views.UnidadView;

@Service
public class UnidadService {

    @Autowired
    private UnidadRepository unidadRepository;
    
    @Autowired
    private PersonaService personaService;
    
    @Autowired
    private ReclamoRepository reclamoRepository;
    
    @Autowired
    private EdificioService edificioService;
    
    
    public Unidad crearUnidad(String piso, String numero, boolean habitado, Integer codigoEdificio) throws EdificioException, UnidadException {
        Edificio edificio = edificioService.buscarEdificio(codigoEdificio);

        // Verificar si ya existe una unidad con los mismos valores de piso y número
        Unidad existentUnit = unidadRepository.findByPisoAndNumero(piso, numero);
        if (existentUnit != null) {
            throw new UnidadException("Ya existe una unidad con el mismo piso y número");
        }

        Unidad unidad = new Unidad();
        unidad.setPiso(piso);
        unidad.setNumero(numero);
        unidad.setHabitado(habitado);
        unidad.setEdificio(edificio);

        unidadRepository.save(unidad);

        return unidad;
    }
    
    public List<UnidadView> unidadesPorDuenios(String mail) {
        List<UnidadView> resultado = new ArrayList<>();
        
        List<Unidad> unidades = unidadRepository.findByDueniosMail(mail);
        
        for (Unidad unidad : unidades) {
            resultado.add(unidad.toView()); // Suponiendo que tengas un método toView() en la clase Unidad
        }
        
        return resultado;
    }
    
    public List<UnidadView> unidadesPorInquilinos(String mail) {
        List<UnidadView> resultado = new ArrayList<>();
        
        List<Unidad> unidades = unidadRepository.findByInquilinosMail(mail);
        
        for (Unidad unidad : unidades) {
            resultado.add(unidad.toView()); // Suponiendo que tengas un método toView() en la clase Unidad
        }
        
        return resultado;
    }


    
    public Unidad buscarEdificioPorCodigo(Integer codigoedificio) throws UnidadException {
    	Unidad unidad= unidadRepository.findByEdificioCodigo(codigoedificio);
        if (unidad == null) {
            throw new UnidadException("No se encontró la unidad con código de edificio " + codigoedificio);
        }

        return unidad;}
    
    
    public Unidad buscarUnidadPorEdificioPisoNumero(Integer codigoedificio, String piso, String numero) throws UnidadException {
        Unidad unidad = unidadRepository.findByEdificioCodigoAndPisoAndNumero(codigoedificio, piso, numero);

        if (unidad == null) {
            throw new UnidadException("No se encontró la unidad con código de edificio " + codigoedificio +
                    ", piso " + piso + " y número " + numero);
        }

        return unidad;}
     

    public Unidad buscarUnidad(Integer id, String piso, String numero) throws UnidadException {
    		Optional<Unidad> o= unidadRepository.findById(id);
    				return o.get(); 
    	}
    
	public void transferirUnidad(Integer id, String piso, String numero, String documento) throws UnidadException, PersonaException {
		Unidad unidad = buscarUnidad(id, piso, numero);
		Persona persona = personaService.buscarPersonaPorDocumento(documento);
		unidad.transferir(persona);
	}
	
	public void agregarDuenioUnidad(Integer id, String piso, String numero, String documento) throws UnidadException, PersonaException {
		Unidad unidad = buscarUnidad(id, piso, numero);
		Persona persona = personaService.buscarPersonaPorDocumento(documento);
		unidad.agregarDuenio(persona);
	}
	
	public void agregarInquilino(Integer id, String piso, String numero, String documento) throws UnidadException, PersonaException {
	    Unidad unidad = buscarUnidad(id, piso, numero);
	    Persona persona = personaService.buscarPersonaPorDocumento(documento);
	    unidad.agregarInquilino(persona);
	    unidadRepository.save(unidad); // Asegúrate de guardar los cambios en la base de datos si es necesario
	}
	

    public void liberarUnidadPorId(Integer id) throws UnidadException {
        Optional<Unidad> optionalUnidad = unidadRepository.findById(id);
        if (optionalUnidad.isPresent()) {
            Unidad unidad = optionalUnidad.get();
            unidad.liberar(); // Aplicar la lógica de liberación en la entidad Unidad
            unidadRepository.save(unidad); // Guardar los cambios en la base de datos
        } else {
            throw new UnidadException("No se encontró la unidad con ID: " + id);
        }
    }
    
    private void limpiarPropietarios(Unidad unidad) {
        // Sacar inquilinos y duenios
        for (Persona inquilino : unidad.getInquilinos()) {
            unidad.sacarInquilino(inquilino);
        }
        for (Persona duenio : unidad.getDuenios()) {
            unidad.sacarDuenio(duenio);
        }
        // Guardar los cambios en la unidad
        unidadRepository.save(unidad);
    }
	
	public Unidad buscarUnidadPorReclamo(Integer idreclamo, String piso, String numero) throws UnidadException {
	    Optional<Reclamo> optionalReclamo = reclamoRepository.findById(idreclamo);
	    if (optionalReclamo.isPresent()) {
	        Reclamo reclamo = optionalReclamo.get();
	        // Aquí supongamos que en Reclamo hay una lista de unidades asociadas
	        // Recorremos la lista para encontrar la unidad con el piso y número especificado
	        List<Unidad> unidades = reclamo.getUnidades();
	        for (Unidad unidad : unidades) {
	            if (unidad.getPiso().equals(piso) && unidad.getNumero().equals(numero)) {
	                return unidad;
	            }
	        }
	        throw new UnidadException("No se encontró la unidad asociada al reclamo con piso " + piso + " y número " + numero);
	    } else {
	        throw new UnidadException("No se encontró ningún reclamo con el ID: " + idreclamo);
	    }
	}
	
	public Unidad buscarUnidadPorId(Integer id) throws UnidadException {
	    Optional<Unidad> optionalUnidad = unidadRepository.findById(id);
	    if (optionalUnidad.isPresent()) {
	        return optionalUnidad.get();
	    } else {
	        throw new UnidadException("No se encontró ninguna unidad con el ID: " + id);
	    }
	}
	
    public void alquilarUnidad(Integer id, String piso, String numero, String documento)
            throws UnidadException, PersonaException {
        Unidad unidad = buscarUnidad(id, piso, numero);
        Persona persona = personaService.buscarPersonaPorDocumento(documento);
        unidad.alquilar(persona);
        unidadRepository.save(unidad);
    }
    
    public void habitarUnidad(Integer id, String piso, String numero) throws UnidadException {
        Unidad unidad = buscarUnidad(id, piso, numero);
        if (!unidad.isHabitado()) { // Verificar si la unidad ya está habitada
            unidad.setHabitado(true); // Establecer la unidad como habitada solo si no lo está
            unidadRepository.save(unidad); // Guardar cambios en la unidad
        }
        // No lanzar una excepción si la unidad ya está habitada, simplemente no realizará cambios en ese caso
    }
    
    public void sacarInquilino(Integer id) throws UnidadException {
        Optional<Unidad> optionalUnidad = unidadRepository.findById(id);
        
        if (optionalUnidad.isPresent()) {
            Unidad unidad = optionalUnidad.get();
            unidad.setInquilinos(new ArrayList<>()); 
            unidad.setHabitado(false);

            unidadRepository.save(unidad);
        } else {
            throw new UnidadException("Unidad no encontrada con ID: " + id);
        }
    }
    
    
    public void eliminarUnidad(Integer id) throws UnidadException {
        Optional<Unidad> optionalUnidad = unidadRepository.findById(id);
        if (optionalUnidad.isPresent()) {
            Unidad unidad = optionalUnidad.get();
            if (unidad.isHabitado()) {
                throw new UnidadException("No se puede eliminar una unidad habitada");
            } else {
                unidadRepository.delete(unidad);
            }
        } else {
            throw new UnidadException("No se encontró la unidad con ID: " + id);
        }
    
    }
    


    
    public void sacarDuenio(Integer id) throws UnidadException {
        Optional<Unidad> unidadOptional = unidadRepository.findById(id);
        if (unidadOptional.isPresent()) {
            Unidad unidad = unidadOptional.get();
            unidad.setDuenios(new ArrayList<>()); 
            unidad.setHabitado(false);

            unidadRepository.save(unidad);
        } else {
            throw new UnidadException("Unidad no encontrada con ID: " + id);
        }
    }
    

    public void establecerUnidadComoHabitada(Integer id) throws UnidadException {
        Optional<Unidad> unidadOptional = unidadRepository.findById(id);
        if (unidadOptional.isPresent()) {
            Unidad unidad = unidadOptional.get();
            unidad.setHabitado(true); // Establecer la unidad como habitada
            unidadRepository.save(unidad);
        } else {
            throw new UnidadException("Unidad no encontrada con ID: " + id);
        }
    }
    
    public boolean estaAsociadaAUnaUnidad(String documento) {
        List<Unidad> unidades = unidadRepository.findAll();
        
        for (Unidad unidad : unidades) {
            for (Persona duenio : unidad.getDuenios()) {
                if (duenio.getDocumento().equals(documento)) {
                    return true;
                }
            }
            for (Persona inquilino : unidad.getInquilinos()) {
                if (inquilino.getDocumento().equals(documento)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean estaHabitada(String mail) {
        List<Unidad> unidades = unidadRepository.findAll();
        
        for (Unidad unidad : unidades) {
            if (unidad.isHabitado()) {
                for (Persona duenio : unidad.getDuenios()) {
                    if (duenio.getMail() != null && duenio.getMail().equals(mail)) {
                        return true;
                    }
                }
                for (Persona inquilino : unidad.getInquilinos()) {
                    if (inquilino.getMail() != null && inquilino.getMail().equals(mail)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    

}