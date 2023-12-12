package controladores;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona,Integer> {
String deleteByDocumento(String documento);
	
	public Optional<Persona> findByDocumento(String documento); 
	
	public Optional<Persona> findByMail(String mail); 
	
	public void deleteByMail(String mail);
}
