package controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReclamoRepository  extends JpaRepository<Reclamo,Integer>{
String deleteByidreclamo(Integer numero);
	
	public Optional<Reclamo> findByIdreclamo(Integer idreclamo); 
	
	List<Reclamo> findByEdificio_Codigo(Integer codigo);
	
	List<Reclamo> findByUnidad_Id(Integer id);
	
	List<Reclamo> findByUsuarioDocumento(String documento);
	
	List<Reclamo> findByUsuarioMail(String mail);
	
	

	
	
	
	
}
