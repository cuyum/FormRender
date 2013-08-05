package ar.com.cuyum.cnc.domain;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import java.lang.Override;

@Entity
public class Xsl implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id = null;

	@Version
	@Column(name = "version")
	private int version = 0;

	@Column
	private String url;

	@Column
	private String xlsVersion;
	
	@Column
	@NotNull(message="El xsl de transformaci\u00F3n del formulario es obligatorio")
	@NotEmpty(message="Indique nombre o descripci\u00F3n corta")
	private String archivo;
	
	@Column
	@NotNull(message="Indique nombre o descripci\u00F3n corta")
	@NotEmpty(message="Indique nombre o descripci\u00F3n corta")
	private String nombre;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}
	
	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		if (id != null) {
			return id.equals(((Xsl) that).id);
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		}
		return super.hashCode();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getXlsVersion() {
		return this.xlsVersion;
	}

	public void setXlsVersion(final String xlsVersion) {
		this.xlsVersion = xlsVersion;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (nombre != null && !nombre.trim().isEmpty())
	         result += "nombre: " + nombre;
		if (archivo != null && !archivo.trim().isEmpty())
	          result += ", archivo: " + archivo;
		if (xlsVersion != null && !xlsVersion.trim().isEmpty())
			result += ", xlsVersion: " + xlsVersion;
		return result;
	}
}