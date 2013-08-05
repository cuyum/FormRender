package ar.com.cuyum.cnc.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import ar.com.cuyum.cnc.domain.Xsl;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

/**
 * Backing bean for Xsl entities.
 * <p>
 * This class provides CRUD functionality for all Xsl entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class XslBean implements Serializable
{

	public static Logger log = Logger.getLogger(XslBean.class);
	
	private static final long serialVersionUID = 1L;

	private FacesMessage msgArchivoSubida;
	
	@Inject
	private FormRenderProperties formRenderProperties;
   /*
    * Support creating and retrieving Xsl entities
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

   private Xsl xsl;

   public Xsl getXsl()
   {
      return this.xsl;
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
         this.xsl = this.example;
      }
      else
      {
         this.xsl = findById(getId());
      }
   }

   public Xsl findById(Long id)
   {

      return this.entityManager.find(Xsl.class, id);
   }

   /*
    * Support updating and deleting Xsl entities
    */

   public String update()
   {
	  boolean copiado;
	  if(this.file != null && !file.getFileName().isEmpty()) {
		  String nameFile = file.getFileName();
		  if (nameFile.endsWith("xsl")){
		  String destination = formRenderProperties.getDestinationXml();			  
	  	  copiado = copyFile(file, destination);
	  	  if (copiado) {
	  		this.xsl.setArchivo(file.getFileName());
	  		this.xsl.setUrl(destination);
	  	  }	
	  	  else {		  		
			   FacesContext.getCurrentInstance().addMessage(null, msgArchivoSubida);
			   return null;
	  	  }
	  } else {
		  FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Carga de Archivo Fallida: ", 
				  "Solo archivos con extensi\u00F3n xsl!");  
	          FacesContext.getCurrentInstance().addMessage(null, msg); 
	          return null;
		  }
	  } else if (this.id == null){
		  FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sr. Usuario: ", "Adjunte el archivo xsl!!");  
	      FacesContext.getCurrentInstance().addMessage(null, msg);  
	      return null;
	  } 
	   
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.xsl);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.xsl);
            return "view?faces-redirect=true&id=" + this.xsl.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }
   
   public boolean copyFile(UploadedFile file, String destination) {
		  
	   boolean copiado = false;
	   boolean procesar = true;
	   try {
		   
		   InputStream in = file.getInputstream();
		   
		   if (null != destination && !destination.isEmpty()){
			   destination = destination.trim();
			   if (!destination.endsWith(System.getProperty("file.separator"))){
				   //Agrego separador de fin
				   destination = destination + System.getProperty("file.separator");
			   }
			   File carpeta = new File(destination);
			   if (!carpeta.exists()){
				   msgArchivoSubida = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Carga de Archivo Fallida",
						   "Destino incorrecto o no existente: "+ destination);
				   procesar = false;
				   copiado = false;
			   }  
		   } else {
			   msgArchivoSubida = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Carga de Archivo Fallida",
					   "Destino incorrecto o no existente: "+ destination);
			   procesar = false;
			   copiado = false;
		   }
		   
		   //TODO: Preguntar si el archivo ya existia para reescribirlo o no
		   
		   if (procesar){				   
			   OutputStream out = new FileOutputStream(destination + this.file.getFileName());
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
    	
    	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"No se puede eliminar el Xsl: ","tiene formularios asociados."));
         return null;
      }
   }

   /*
    * Support searching Xsl entities with pagination
    */

   private int page;
   private long count;
   private List<Xsl> pageItems;

   private Xsl example = new Xsl();

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

   public Xsl getExample()
   {
      return this.example;
   }

   public void setExample(Xsl example)
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
      Root<Xsl> root = countCriteria.from(Xsl.class);
      countCriteria = countCriteria.select(builder.count(root)).where(
            getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria)
            .getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Xsl> criteria = builder.createQuery(Xsl.class);
      root = criteria.from(Xsl.class);
      TypedQuery<Xsl> query = this.entityManager.createQuery(criteria
            .select(root).where(getSearchPredicates(root)));      
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Xsl> root)
   {
	   
	  Map<String, String> filters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();		
	      
	  if (filters != null){
		   
    	 if (filters.get("search:xslBeanNombre") !=null && !filters.get("search:xslBeanNombre").isEmpty()){
			   this.example.setNombre(filters.get("search:xslBeanNombre"));		 
    	 }
    	 
    	 if (filters.get("search:xslBeanArchivo") !=null && !filters.get("search:xslBeanArchivo").isEmpty()){
			   this.example.setArchivo(filters.get("search:xslBeanArchivo"));		 
    	 }
      }   

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nombre = this.example.getNombre();
      if (nombre != null && !"".equals(nombre))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("nombre")), '%' + nombre.toLowerCase() + '%'));
      }
      String archivo = this.example.getArchivo();
      if (archivo != null && !"".equals(archivo))
      {
         predicatesList.add(builder.like(builder.lower(root.<String> get("archivo")), '%' + archivo.toLowerCase() + '%'));    	  
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Xsl> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Xsl entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Xsl> getAll()
   {

      CriteriaQuery<Xsl> criteria = this.entityManager
            .getCriteriaBuilder().createQuery(Xsl.class);
      return this.entityManager.createQuery(
            criteria.select(criteria.from(Xsl.class))).getResultList();
   }

   @Resource
   private SessionContext sessionContext;

   public Converter getConverter()
   {

      final XslBean ejbProxy = this.sessionContext.getBusinessObject(XslBean.class);

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

            return String.valueOf(((Xsl) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Xsl add = new Xsl();

   public Xsl getAdd()
   {
      return this.add;
   }

   public Xsl getAdded()
   {
      Xsl added = this.add;
      this.add = new Xsl();
      return added;
   }
}