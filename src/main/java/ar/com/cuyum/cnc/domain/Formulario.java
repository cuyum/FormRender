package ar.com.cuyum.cnc.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"codigo"})})
public class Formulario implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", updatable = false, nullable = false)
   private Long id = null;
   
   @Version
   @Column(name = "version")
   private int version = 0;
   
   @Column
   @NotNull(message="Indique el c\u00F3digo del formulario")
   @NotEmpty(message="Indique el c\u00F3digo del formulario")   
   private String codigo;

   @Column
   @NotEmpty(message="Indique nombre o descripci\u00F3n corta")
   @NotNull(message="Indique nombre o descripci\u00F3n corta")
   private String nombre;
   
   @Column
   @NotNull(message="El xml de definici\u00F3n del formulario es obligatorio")
   @NotEmpty(message="El xml de definici\u00F3n del formulario es obligatorio")
   private String archivo;

   @Column(name = "parametrosurl")
   private String parametrosUrl;
   
   @Column
   private String formVersion;
   
   @Column
   private String url;
   
   @OneToOne(cascade={CascadeType.ALL})
   @NotNull(message="Seleccione el xsl de transformaci\u00F3n")
   private Xsl xslTransform;
   
   public Xsl getXslTransform() {
	   return xslTransform;
   }
	
   public void setXslTransform(Xsl xslTransform) {
	   this.xslTransform = xslTransform;
   }

   public Long getId()
   {
      return this.id;
   }

   public void setId(final Long id)
   {
      this.id = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }
      if (that == null)
      {
         return false;
      }
      if (getClass() != that.getClass())
      {
         return false;
      }
      if (id != null)
      {
         return id.equals(((Formulario) that).id);
      }
      return super.equals(that);
   }

   @Override
   public int hashCode()
   {
      if (id != null)
      {
         return id.hashCode();
      }
      return super.hashCode();
   }

   public String getNombre()
   {
      return this.nombre;
   }

   public void setNombre(final String nombre)
   {
      this.nombre = nombre;
   }

   public String getCodigo() {
	   return codigo;
   }

   public void setCodigo(String codigo) {
	   this.codigo = codigo;
   }

   public String getFormVersion()
   {
      return this.formVersion;
   }

   public void setFormVersion(final String formVersion)
   {
      this.formVersion = formVersion;
   }   

   public String getArchivo() {
	  return archivo;
   }

   public void setArchivo(String archivo) {
	  this.archivo = archivo;
   }

   public String getUrl() {
	   return url;
   }
	
   public void setUrl(String url) {
		this.url = url;
	}
   
   

   public String getParametrosUrl() {
	return parametrosUrl;
}

public void setParametrosUrl(String parametrosUrl) {
	this.parametrosUrl = parametrosUrl;
}

@Override
   public String toString()
   {
      String result = getClass().getSimpleName() + " ";
      if (nombre != null && !nombre.trim().isEmpty())
         result += "nombre: " + nombre;
      if (codigo != null && !codigo.trim().isEmpty())
          result += "codigo: " + codigo;
      if (archivo != null && !archivo.trim().isEmpty())
          result += ", archivo: " + archivo;
      if (formVersion != null && !formVersion.trim().isEmpty())
         result += ", formVersion: " + formVersion;
      return result;
   }
}