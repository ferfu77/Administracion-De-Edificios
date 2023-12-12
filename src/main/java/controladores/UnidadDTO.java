package controladores;

public class UnidadDTO {
	
	public class UnidadRequest {
	    private Integer identificador;
	    private String piso;
	    private String numero;
	    private boolean habitado;
	    private Integer codigoedificio;
	    

	    public UnidadRequest() {
	    }

	    public UnidadRequest(Integer identificador, String piso, String numero, boolean habitado, Integer codigoedificio /* Otros campos */) {
	        this.identificador = identificador;
	        this.piso = piso;
	        this.numero = numero;
	        this.habitado = habitado;
	        this.codigoedificio = codigoedificio;

	    }


	    public Integer getIdentificador() {
	        return identificador;
	    }

	    public void setIdentificador(Integer identificador) {
	        this.identificador = identificador;
	    }

	    public Integer getCodigoEdificio() {
	        return codigoedificio;
	    }

	    public void setCodigoEdificio(Integer codigoedificio) {
	        this.codigoedificio = codigoedificio;
	    }

	    public String getPiso() {
	        return piso;
	    }

	    public void setPiso(String piso) {
	        this.piso = piso;
	    }

	    public String getNumero() {
	        return numero;
	    }

	    public void setNumero(String numero) {
	        this.numero = numero;
	    }

	    public boolean isHabitado() {
	        return habitado;
	    }

	    public void setHabitado(boolean habitado) {
	        this.habitado = habitado;
	    }


	}


}
