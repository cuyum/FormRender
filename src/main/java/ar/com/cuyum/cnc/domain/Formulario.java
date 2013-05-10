package ar.com.cuyum.cnc.domain;

import javax.persistence.Entity;

import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;


import java.lang.Override;

@Entity
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
   private String codigo;

   @Column
   private String nombre;
   
   @Column
   private String archivo;

   @Column
   private String formVersion;
   
   @Column
   private String url;
   
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