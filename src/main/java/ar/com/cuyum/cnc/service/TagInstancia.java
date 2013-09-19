package ar.com.cuyum.cnc.service;

/**
 * Permite construir el area de instancia en el XML 
 * @autor ltroconis
 * 
 * @param abrir indica si el tag del objeto en la instancia es abierto
 * @param cerrar indica si el tag del objeto en la instancia es cerrado
 * @param tag nombre del tag a poner en la instacia
 * */
public class TagInstancia {
	private Boolean abrir;
	private String tag;
	private Boolean cerrar;

	TagInstancia(Boolean abrir, String tag, Boolean cerrar) {
		this.abrir = abrir;
		this.tag = tag;
		this.cerrar = cerrar;
	}

	public Boolean getAbrir() {
		return abrir;
	}

	public Boolean getCerrar() {
		return cerrar;
	}

	public String getTag() {
		return tag;
	}

	public void setAbrir(Boolean abrir) {
		this.abrir = abrir;
	}

	public void setCerrar(Boolean cerrar) {
		this.cerrar = cerrar;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
