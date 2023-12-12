package controladores;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import exceptions.UnidadException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import views.EdificioView;
import views.UnidadView;
@Entity
@Table(name="unidades")

public class Unidad {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="identificador")
	private Integer id;
	private String piso;
	private String numero;
	private boolean habitado;
	@ManyToOne
	@JoinColumn(name="codigoedificio")
	private Edificio edificio;
    @ManyToMany
    @JoinTable(name = "duenios", joinColumns={
            @JoinColumn(name="identificador",nullable = false)
    },inverseJoinColumns = {@JoinColumn (name="documento",nullable = false)}
    )
    private List<Persona> duenios=new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "inquilinos", joinColumns={
            @JoinColumn(name="identificador",nullable = false)
    },inverseJoinColumns = {@JoinColumn (name="documento",nullable = false)}
    )
    private List<Persona> inquilinos=new ArrayList<>();
    
	public boolean isHabitado() {
		return habitado;
	}
	
    public Unidad() {
    }

	public void setHabitado(boolean habitado) {
		this.habitado = habitado;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setEdificio(Edificio edificio) {
		this.edificio = edificio;
	}

	
	public Unidad(Integer id, String piso, String numero, Edificio edificio) {
		this.id = id;
		this.piso = piso;
		this.numero = numero;
		this.habitado = false;
		this.edificio = edificio;
		this.duenios = new ArrayList<Persona>();
		this.inquilinos = new ArrayList<Persona>();
	}

	public void transferir(Persona nuevoDuenio) {
		duenios = new ArrayList<Persona>();
		duenios.add(nuevoDuenio);
	}
	
	public void agregarDuenio(Persona duenio) {
		duenios.add(duenio);
	}
	
	public void agregarInquilino(Persona inquilino) {
		inquilinos.add(inquilino);
	}
	
    public void sacarInquilino(Persona persona) {

        for(Persona p: inquilinos){
            if (p.getDocumento().equals(persona.getDocumento())){
                inquilinos.remove(p);
                break;
            }
        }
        if(inquilinos.isEmpty()){
            this.habitado=false;
        }

    }

    public void sacarDuenio(Persona persona) {

        for(Persona p: duenios){
            if (p.getDocumento().equals(persona.getDocumento())){
                duenios.remove(p);
                break;
            }
        }
        if(duenios.isEmpty()){
            System.out.println("UNIDAD SIN DUENIO");
        }

    }
	
    public void alquilar(Persona inquilino) {
        inquilinos.add(inquilino);
        this.habitado = true; // En este caso, marcar la unidad como habitada al agregar un inquilino
    }

	
	public boolean estaHabitado() {
		return habitado;
	}
	
	public void liberar() {
		this.inquilinos = new ArrayList<Persona>();
		this.habitado = false;
		
	}
	
	public void habitar() throws UnidadException {
		if(this.habitado)
			throw new UnidadException("La unidad ya esta habitada");
		else
			this.habitado = true;
	}
	
	public Integer getId() {
		return id;
	}

	public String getPiso() {
		return piso;
	}

	public String getNumero() {
		return numero;
	}

	
	public Edificio getEdificio() {
		return edificio;
	}

	public List<Persona> getDuenios() {
		return duenios;
	}
	public void setDuenios(List<Persona> duenios) {
		this.duenios = duenios;
	}
	public List<Persona> getInquilinos() {
		return inquilinos;
	}
	
	public void setInquilinos(List<Persona> inquilinos) {
		this.inquilinos = inquilinos;
	}
	

	public UnidadView toView() {
		EdificioView auxEdificio = edificio.toView();
		return new UnidadView(id, piso, numero, habitado, auxEdificio);
	}
}
