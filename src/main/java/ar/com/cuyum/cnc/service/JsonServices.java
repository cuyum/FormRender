package ar.com.cuyum.cnc.service;

import java.io.InputStream;
import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.ServletContext;

import ar.com.cuyum.cnc.domain.Formulario;
import ar.com.cuyum.cnc.utils.DomUtils;
import ar.com.cuyum.cnc.utils.FileUtilsAux;


public class JsonServices implements Serializable {

	@Inject
	private FileUtilsAux fileUtils;

	@Inject
	private TransformationService ts;

	public String previewHtml(Long formId,String jsonValue, String contextName, ServletContext sc) {
		InputStream xmlStream = null;
		InputStream xslStream = null;
		Formulario formulario = findById(formId);
		//String xForm = jsonToXForm(jsonValue, formulario);

		xmlStream = fileUtils.getXmlInputStream(formulario, sc);
		xslStream = fileUtils.loadXsl(formulario.getXslTransform(), sc);
		if (null != xmlStream && null != xslStream) {
			ts.setRemoteTransformation(false);
			String transformedHtml = ts.transform(xmlStream, xslStream,
					formulario.getFormVersion());
			transformedHtml = transformedHtml.replaceAll("___context___", contextName);
			return transformedHtml;
		}
		return "";

	}

	public Formulario findById(Long id) {
		return this.entityManager.find(Formulario.class, id);
	}

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	// public String xmlView (){
	//
	// Formulario formulario = new Formulario();
	// formulario.setId(id)
	//
	// InputStream xmlStream=null;
	//
	// xmlStream = fileUtils.getXmlInputStream(formulario);
	// if (null != xmlStream){
	// formDom = fileUtils.format(xmlStream);
	// } else {
	// String msgText = "Archivo "+ formulario.getArchivo()
	// +" no encontrado en: ";
	// if (null != formulario.getUrl() && !formulario.getUrl().isEmpty())
	// msgText = msgText.concat(formulario.getUrl());
	// else msgText = msgText.concat(DomUtils.getXMLFROM());
	// FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
	// "Error:",
	// msgText);
	// FacesContext.getCurrentInstance().addMessage(null, msg);
	// }
	// return null;
	// }

	private String jsonToXForm(String jsonValue, Formulario formulario) {

		InputStream xmlStream=null;	    
		   xmlStream = fileUtils.getXmlInputStream(formulario, null);
		   String ret=null;
		   if (null != xmlStream){		   
			   ret = fileUtils.format(xmlStream);		   
		   } 
		   return ret;	   	

	}

}
