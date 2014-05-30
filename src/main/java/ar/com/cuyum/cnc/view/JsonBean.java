package ar.com.cuyum.cnc.view;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.ws.rs.core.Context;

import org.apache.http.HttpRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.protocol.RequestContent;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.model.UploadedFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import ar.com.cuyum.cnc.domain.Formulario;
import ar.com.cuyum.cnc.service.JsonServices;
import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.FormRenderProperties;
import ar.com.cuyum.cnc.utils.JsonUtils;

/**
 * Backing bean for Xsl entities.
 * <p>
 * This class provides CRUD functionality for all Xsl entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class JsonBean implements Serializable {

	public static Logger log = Logger.getLogger(JsonBean.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private FormRenderProperties frp;
	
	@Inject
	Formulario formulario;

	@Inject
	private JsonServices jsonService;
	
	@Inject
	private RelayService relayService;
	
	@Context
	HttpServletRequest req;
	
	private String schema;
	private String json;
	private String jsonDates="Ingrese aqui su objeto Json";
	private String msg = null;	
	
	/*
	 * Support creating and retrieving Xsl entities
	 */

	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
		}
	}
	
	public String getSchema() {
		
		
		InputStream input = null;
		
		FacesContext context = FacesContext.getCurrentInstance();
		ServletContext request = (ServletContext) context.getExternalContext()
				.getContext();
		
		String form = id + "-schema.json";
		try {
			input = request.getResourceAsStream("/schemas/" + form);
			schema = jsonService.previewExample(input);
		} catch (Exception exp) {
			log.error(exp);
		}
		schema = jsonService.formatJson(schema);
		return schema;
	}
	
	public String getJson() {

		InputStream input = null;

		FacesContext context = FacesContext.getCurrentInstance();
		ServletContext request = (ServletContext) context.getExternalContext()
				.getContext();

		String form = id + "-schema.json";

		try {
			input = request.getResourceAsStream("/schemas/" + form);
			schema = jsonService.previewExample(input);
			json = jsonService.obtenerJson(schema, id);
		} catch (Exception exp) {
			log.error(exp);
		}
		json = jsonService.formatJson(json);
		return json;
	}

	public String validarJson() {
		
		String response = null;
		FacesContext context = FacesContext.getCurrentInstance();
		
		req = (HttpServletRequest) context.getExternalContext().getRequest();;
		
		try {
			URL url = new URL(frp.getRemoteSubmissionHost() + "/api/deposition");
			response = relayService.massiveSubmit(url, jsonDates, req);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response = cleanMsg(response);
		
		setMsg(response);
		return response;
	}
	
	public String cleanMsg(String msj) {
		String msg=msj;
		msg = msg.replace("\\", "");
		
		if(msg.indexOf("msg")!=-1){
			
			msg = msg.substring(msg.indexOf("msg")-1, msg.indexOf("}"));
			if(msg.indexOf("{")!=-1)
				msg = msg + "}}";
			else msg = "{"+ msg + "}";
		}
		
		return msg;
	}

	public String getJsonDates() {
		return jsonDates;
	}

	public void setJsonDates(String jsonDates) {
		this.jsonDates = jsonDates;
	}
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}