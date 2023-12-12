package controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import exceptions.PersonaException;

@Service
public class PersonaService {

	
	@Autowired
	private PersonaRepository personaRepository;
    public Persona buscarPersonaPorDocumento(String documento) throws PersonaException {
        Optional<Persona> optionalPersona = personaRepository.findByDocumento(documento);
        if (optionalPersona.isPresent()) {
            return optionalPersona.get();
        } else {
            throw new PersonaException("No se encontró ninguna persona con el documento: " + documento);
        }
    }
    
    public void asignarComoAdministrador(String mail) throws PersonaException {
        Optional<Persona> personaOptional = personaRepository.findByMail(mail);
        if (personaOptional.isPresent()) {
            Persona persona = personaOptional.get();
            persona.setAdministrador(true); // Establecer como administrador
            personaRepository.save(persona);
        } else {
            throw new PersonaException("Persona no encontrada con documento: " + mail);
        }
    }
    
    public boolean actualizarPersona(String mail, Persona persona) throws PersonaException {
        Optional<Persona> personaOptional = personaRepository.findByMail(mail);

        if (personaOptional.isPresent()) {
            Persona personaExistente = personaOptional.get();
            personaExistente.setNombre(persona.getNombre());
            personaExistente.setPassword(persona.getPassword());
          
            // Actualiza otras propiedades según sea necesario
            personaRepository.save(personaExistente);
            return true;
        } else {
            throw new PersonaException("No se encontró ninguna persona con el correo electrónico: " + mail);
        }
    }

    
    


    

    public boolean esAdministrador(String documento) {
        Optional<Persona> personaOptional = personaRepository.findByDocumento(documento);
        
        if (personaOptional.isPresent()) {
            Persona persona = personaOptional.get();
            return persona.getAdministrador();
        } else {
            // Si no se encuentra la persona, se puede manejar de diferentes maneras, por ejemplo:
            // Lanzar una excepción, devolver false o realizar alguna acción por defecto.
            return false;
        }
    }

    
    public Persona obtenerPersonaPorMail(String mail) throws PersonaException {
        Optional<Persona> personaOptional = personaRepository.findByMail(mail);

        if (personaOptional.isPresent()) {
            return personaOptional.get();
        } else {
            throw new PersonaException("No se encontró ninguna persona con el correo electrónico: " + mail);
        }
    }

    

    
}

