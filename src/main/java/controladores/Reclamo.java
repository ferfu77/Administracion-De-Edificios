package controladores;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import views.EdificioView;
import views.ImagenView;
import views.PersonaView;
import views.ReclamoView;
import views.UnidadView;
@Entity
@Table(name = "reclamos")
public class Reclamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idreclamo;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="documento")
    @JsonProperty(access=Access.WRITE_ONLY)
	private Persona usuario;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="codigo")
    @JsonProperty(access=Access.WRITE_ONLY)
	private Edificio edificio;
	private String ubicacion;
	private String descripcion;
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="identificador")
	@JsonProperty(access=Access.WRITE_ONLY)
	private Unidad unidad;
	@Enumerated(EnumType.STRING)
	private Estado estado;
	@OneToMany(mappedBy = "reclamo", cascade=CascadeType.ALL)
    @JsonProperty(access=Access.WRITE_ONLY)
	private List<Imagen> imagenes;
	
	public Reclamo() {}
	public Reclamo(Persona usuario, Edificio edificio, String ubicacion, String descripcion, Unidad unidad) {
		this.usuario = usuario;
		this.edificio = edificio;
		this.ubicacion = ubicacion;
		this.descripcion = descripcion;
		this.unidad = unidad;
		this.estado = Estado.nuevo;
		imagenes = new ArrayList<Imagen>();
	}

	public void agregarImagen(Imagen imagen) {
	    ; // Establecer la relación entre la imagen y el reclamo
	    imagenes.add(imagen);
	    imagen.setReclamo(this);
	}


	public Integer getIdReclamo() {
		return idreclamo;
	}

	public void setIdReclamo(Integer idReclamo) {
		this.idreclamo = idReclamo;
	}

	public Persona getUsuario() {
		return usuario;
	}

	public void setUsuario(Persona usuario) {
		this.usuario = usuario;
	}
	public void setEdificio(Edificio edificio) {
		this.edificio = edificio;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public void setUnidad(Unidad unidad) {
		this.unidad = unidad;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public void setImagenes(List<Imagen> imagenes) {
		this.imagenes = imagenes;
	}
	public Edificio getEdificio() {
		return edificio;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Unidad getUnidad() {
		return unidad;
	}

	public Estado getEstado() {
		return estado;
	}
	
	public List<Imagen> getImagenes(){
		return this.imagenes;
	}
	
	public void cambiarEstado(Estado estado) {
		this.estado = estado;
	}
	public ReclamoView toView() {
	    PersonaView usuarioView = usuario != null ? usuario.toView() : null;
	    EdificioView edificioView = edificio != null ? edificio.toView() : null;
	    UnidadView unidadView = unidad != null ? unidad.toView() : null;

	    List<ImagenView> imagenesView = new ArrayList<>();
	    if (imagenes != null) {
	        for (Imagen imagen : imagenes) {
	            imagenesView.add(imagen.toView());
	        }
	    }

	    return new ReclamoView(idreclamo, usuarioView, edificioView, ubicacion, descripcion, unidadView, estado, imagenesView);
	}
	public List<Unidad> getUnidades() {
		// TODO Auto-generated method stub
		return null;
	}

	}

