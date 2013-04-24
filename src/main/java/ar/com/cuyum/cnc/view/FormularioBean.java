package ar.com.cuyum.cnc.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ar.com.cuyum.cnc.domain.Formulario;

/**
 * Backing bean for Formulario entities.
 * <p>
 * This class provides CRUD functionality for all Formulario entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class FormularioBean implements Serializable
{

   private static final long serialVersionUID = 1L;
   
   private static final String CARPETA_DESTINO="Formularios";
   
   private FacesMessage msgArchivoSubida;

   /*
    * Support creating and retrieving Formulario entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private Formulario formulario;

   public Formulario getFormulario()
   {
      return this.formulario;
   }
   
   private UploadedFile file;
   
   public UploadedFile getFile() {  
       return file;  
   }  
 
   public void setFile(UploadedFile file) {  
       this.file = file;  
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;
   
   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.formulario = this.example;
      }
      else
      {
         this.formulario = findById(getId());
      }
   }

   public Formulario findById(Long id)
   {

      return this.entityManager.find(Formulario.class, id);
   }
   
   public boolean copyFile(UploadedFile file) {
	  
	   boolean copiado = false;
	   try {
		   String fileName = file.getFileName();
		   InputStream in = file.getInputstream();
		   
		   String destination=System.getProperty("user.home") + System.getProperty("file.separator") + CARPETA_DESTINO
				   + System.getProperty("file.separator");
		   
		   File carpeta = new File(destination);
		   if (!carpeta.exists()){
			   carpeta.mkdir();
		   }
		   File archivo = new File(destination+ System.getProperty("file.separator")+this.file.getFileName());
		   if (archivo.exists()){
			   msgArchivoSubida = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Carga de Archivo Fallida",
					   "Ya existe un archivo con ese nombre: "+this.file.getFileName());
			   copiado = false;
		   } else {			   
			   OutputStream out = new FileOutputStream(destination + fileName);
			   int read = 0;
			   byte[] bytes = new byte[1024];
			   while ((read = in.read(bytes)) != -1) {
				   out.write(bytes, 0, read);
			   }	     
			   in.close();
			   out.flush();
			   out.close();	    
		       copiado = true;
		   }
	   } catch (IOException e) {
		   msgArchivoSubida = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
				   "Carga de Archivo Fallida",  e.getMessage());		   
		   copiado = false;		   
	   }
	   return copiado;
   }

   /*
    * Support updating and deleting Formulario entities
    */

   public String update()
   {
	  boolean copiado;
	  if(this.file != null && !file.getFileName().isEmpty()) {
		  String nameFile = file.getFileName();
		  if (nameFile.endsWith("xml")){
		  	  copiado = copyFile(file);
		  	  if (copiado)
		  		this.formulario.setArchivo(file.getFileName());
		  	  else {		  		
				   FacesContext.getCurrentInstance().addMessage(null, msgArchivoSubida);
				   return null;
		  	  }
		  } else {
			  FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Carga de Archivo Fallida", 
					  "Solo archivos con extension xml!");  
	          FacesContext.getCurrentInstance().addMessage(null, msg); 
	          return null;
		  }
	  } else {
    	  FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Archivo de configuracion del formulario", "No hay adjuntado el archivo!!");  
          FacesContext.getCurrentInstance().addMessage(null, msg);  
          return null;
      }
	  
      this.conversation.end();
      
      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.formulario);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.formulario);
            return "view?faces-redirect=true&id=" + this.formulario.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(findById(getId()));
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Formulario entities with pagination
    */

   private int page;
   private long count;
   private List<Formulario> pageItems;

   private Formulario example = new Formulario();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public Formulario getExample()
   {
      return this.example;
   }

   public void setExample(Formulario example)
   {
      this.example = example;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Formulario> root = countCriteria.from(Formulario.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Formulario> criteria = builder.createQuery(Formulario.class);
      root = criteria.from(Formulario.class);
      TypedQuery<Formulario> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(
            getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Formulario> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(root.<String> get("nombre"), '%' + nombre + '%'));
      }
      String formVersion = this.example.getFormVersion();
      if (formVersion != null && !"".equals(formVersion))
      {
         predicatesList.add(builder.like(root.<String> get("formVersion"), '%' + formVersion + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Formulario> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Formulario entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Formulario> getAll()
   {

      CriteriaQuery<Formulario> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Formulario.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Formulario.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final FormularioBean ejbProxy = this.sessionContext.getBusinessObject(FormularioBean.class);

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context,
               UIComponent component, String value)
         {

            return ejbProxy.findById(Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context,
               UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Formulario) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Formulario add = new Formulario();

   public Formulario getAdd()
   {
      return this.add;
   }

   public Formulario getAdded()
   {
      Formulario added = this.add;
      this.add = new Formulario();
      return added;
   }
   
   public void handleFileUpload(FileUploadEvent event) {  
       FacesMessage msg = new FacesMessage(event.getFile().getFileName() + " subido correctamente!");  
       FacesContext.getCurrentInstance().addMessage(null, msg);
   }
}