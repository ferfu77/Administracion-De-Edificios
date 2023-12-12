package controladores;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EdificioRepository extends JpaRepository<Edificio,Integer>{
String deleteByCodigo(Integer codigo);
	
	public Optional<Edificio> findByCodigo(Integer codigo); 
}
