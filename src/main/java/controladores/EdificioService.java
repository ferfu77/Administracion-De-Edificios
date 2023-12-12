package controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import exceptions.EdificioException;
import exceptions.PersonaException;

@Service
public class EdificioService {

    @Autowired
    private EdificioRepository edificioRepository;
    
    @Autowired
    private ReclamoRepository reclamoRepository;

    public Edificio buscarEdificio(Integer codigoedificio) throws EdificioException {
        Optional<Edificio> optionalEdificio = edificioRepository.findByCodigo(codigoedificio);
        if (optionalEdificio.isPresent()) {
            return optionalEdificio.get();
        } else {
            throw new EdificioException("No se encontró ningún edificio con el código: " + codigoedificio);
        }
    }
    
    public Edificio buscarEdificioPorReclamo(Integer idreclamo) throws EdificioException {
        Optional<Reclamo> optionalReclamo = reclamoRepository.findByIdreclamo(idreclamo);
        if (optionalReclamo.isPresent()) {
            Reclamo reclamo = optionalReclamo.get();
            return reclamo.getEdificio();
        } else {
            throw new EdificioException("No se encontró ningún edificio asociado al reclamo con el ID: " + idreclamo);
        }
    }
    
    public Edificio buscarEdificioPorCodigo(Integer codigo) throws EdificioException{
        Optional<Edificio> optionalEdificio= edificioRepository.findByCodigo(codigo);
        if (optionalEdificio.isPresent()) {
            return optionalEdificio.get();
        } else {
            throw new EdificioException("No se encontró ninguna persona con el documento: " + codigo);
        }
    	
    }
    

    

}