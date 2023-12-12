package controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadRepository extends JpaRepository<Unidad,Integer> {
	public Optional<Unidad> findById(Integer id); 
	
	Unidad findByEdificioCodigoAndPisoAndNumero(Integer codigoedificio, String piso, String numero);
	
	Unidad findByEdificioCodigo(Integer codigoedificio);
	
    Unidad findByPisoAndNumero(String piso, String numero);
    
    
    List<Unidad> findByDueniosMail(String mail);
    
    List<Unidad> findByInquilinosMail(String mail);

   
    
    

	
}
