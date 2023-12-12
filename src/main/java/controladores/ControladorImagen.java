package controladores;

import java.util.List;
import java.util.Optional;

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

import jakarta.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/imagenes")
public class ControladorImagen {
    @Autowired
    private ImagenRepository imagenRepository;

    @GetMapping("/listar")
    public ResponseEntity<List<Imagen>> obtenerTodasLasImagenes() {
        List<Imagen> imagenes = imagenRepository.findAll();
        return ResponseEntity.ok(imagenes);
    }

    @PostMapping("/guardar")
    public Imagen createImagen(@RequestBody Imagen imagen) {
        return imagenRepository.save(imagen);
    }

    @GetMapping("/obtener/{numero}")
    public Imagen findByNumero(@PathVariable("numero")Integer numero){
        Optional<Imagen> o= imagenRepository.findById(numero);
                return o.get(); 
    }

    @DeleteMapping("/eliminar/{numero}")
    String deleteByNumero(@PathVariable("numero") Integer numero) {
        imagenRepository.deleteById(numero);
        return "imagen con el numero "+ numero + " fue eliminado";
    }

    @PutMapping("/actualizar/{numero}")
    public void actualizar(@PathVariable Integer numero, @RequestBody Imagen imagen) {
        imagen.setNumero(numero);
        imagenRepository.save(imagen);
    }
}
