package controladores;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import views.PersonaView;

@Entity
@Table(name="personas")
public class Persona {
	@Id
	private String documento;
	private String nombre;
	private String mail;
	@Column(name = "contrasenia")
	private String password;
	@Convert(converter = BooleanConverter.class)
	private boolean administrador;
	
	public Persona(String documento, String nombre, String mail, String password) {
		this.documento = documento;
		this.nombre = nombre;
		this.mail = mail;
		this.password = password;
	}

    public Persona() {
    }

    
    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }
    public boolean getAdministrador() {
    	return administrador;
    }
	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getDocumento() {
		return documento;
	}

	public String getNombre() {
		return nombre;
	}

	
	public String getMail() {
		return mail;
	}

	public String getPassword() {
		return password;
	}

	public PersonaView toView() {
		return new PersonaView(documento, nombre);
	}


}
