package controladores;

public class ReclamoRequest {
	
	private Integer idreclamo;
    private String documento;
    private int codigo;
    private String ubicacion;
    private String descripcion;
    private int identificador;
    private String estado;
    
	public ReclamoRequest(Integer idreclamo, String documento, int codigo, String ubicacion, String descripcion,
			int identificador, String estado) {
		super();
		this.idreclamo = idreclamo;
		this.documento = documento;
		this.codigo = codigo;
		this.ubicacion = ubicacion;
		this.descripcion = descripcion;
		this.identificador = identificador;
		this.estado = estado;
	}

	public Integer getIdreclamo() {
		return idreclamo;
	}

	public void setIdreclamo(Integer idreclamo) {
		this.idreclamo = idreclamo;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
    


}
