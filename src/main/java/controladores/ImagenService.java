package controladores;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import exceptions.ImagenException;

@Service
public class ImagenService {
	
	@Autowired
	private ImagenRepository imagenRepository;
	
	private Imagen buscarImagen(Integer numero) throws ImagenException{
        Optional<Imagen> optionalImagen= imagenRepository.findByNumero(numero);
        if (optionalImagen.isPresent()) {
            return optionalImagen.get();
        } else {
            throw new ImagenException("No se encontró ningún edificio con el código: " + numero);
        }
	
	}
}
