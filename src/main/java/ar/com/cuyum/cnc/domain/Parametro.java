package ar.com.cuyum.cnc.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="parametros", uniqueConstraints = { @UniqueConstraint(columnNames = { "nombre" }) })
public class Parametro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id = null;

	@Version
	@Column(name = "version")
	private int version = 0;

	@Column
	@NotNull(message = "Indique el c\u00F3digo del formulario")
	@NotEmpty(message = "Indique el c\u00F3digo del formulario")
	@Length(min = 0, max = 255)
	private String nombre;

	@Column(columnDefinition = "text")
	@NotEmpty(message = "Indique nombre o descripci\u00F3n corta")
	@NotNull(message = "Indique nombre o descripci\u00F3n corta")
	private String valor;

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

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
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
			return id.equals(((Parametro) that).id);
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
	
	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (nombre != null && !nombre.trim().isEmpty())
			result += "nombre: " + nombre;
		if (valor != null && !valor.trim().isEmpty())
			result += "valor: " + valor;
		return result;
	}
}