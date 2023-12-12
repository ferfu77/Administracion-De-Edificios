package controladores;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenRepository extends JpaRepository<Imagen,Integer> {
	String deleteByNumero(Integer numero);
	
	public Optional<Imagen> findByNumero(Integer numero); 
}
