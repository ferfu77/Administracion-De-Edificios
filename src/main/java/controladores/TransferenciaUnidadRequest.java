package controladores;

public class TransferenciaUnidadRequest {
    public TransferenciaUnidadRequest(Integer id, String piso, String numero, String documento) {
		super();
		this.id = id;
		this.piso = piso;
		this.numero = numero;
		this.documento = documento;
	}
	private Integer id;
    private String piso;
    private String numero;
    private String documento;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}

}
