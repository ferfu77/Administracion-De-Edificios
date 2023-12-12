package controladores;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import views.ImagenView;

@Entity
@Table(name = "imagenes")
public class Imagen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer numero;
	@Column(name="path")
	private String direccion;
	private String tipo;
	@ManyToOne
	@JoinColumn(name = "idreclamo")
    @JsonProperty(access=Access.WRITE_ONLY)
	private Reclamo reclamo;
	public Imagen() {}
	public Imagen(String direccion, String tipo) {
		this.direccion = direccion;
		this.tipo = tipo;
	}

	public Integer getNumero() {
		return numero;
	}
	
	public Reclamo getReclamo() {
		return reclamo;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTipo() {
		return tipo;
	}
	
	
	
	public void setReclamo(Reclamo reclamo) {
		this.reclamo = reclamo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public ImagenView toView() {
		return new ImagenView(numero, direccion, tipo);
	}
}
