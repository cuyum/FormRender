/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import ar.com.cuyum.cnc.service.JsonServices;
import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

/**
 * @author Jorge Morando
 *
 */
@Path("/service")
@RequestScoped
public class RelayRest {

	private Logger log = Logger.getLogger(RelayRest.class);

	@Inject
	private RelayService relay;
	
	@Inject
	private FormRenderProperties frp;
	
	@GET
	@POST
	@Produces("application/json")
	public String relay(){
		return "{\"success\":false,\"msg\": \"No se ha configurado correctamente la plataforma\"}";
	}
	
	@Inject private JsonServices jsonService;
	
	@GET
	@Path("/preview")
	@Produces("text/html")
	public String preview(@Context ServletContext servletContext,@QueryParam("formId") Long formId) {
		String cn = servletContext.getServletContextName();
		
		return jsonService.previewHtml(formId,"", cn, servletContext);
		
		
		
	}
	
	@POST
	@Path("/relay")
	@Produces("application/json")
	public String relay(@FormParam("remoteUrl") String remoteUrl, @FormParam("fkey") String fkey) {
		log.info("Ingreso en relay de "+remoteUrl+(fkey!=null?" con el siguiente fkey "+fkey:""));
		
		JSONObject response;
		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteListHost()+remoteUrl);
			remoteResponse = relay.request(url, fkey);
			response = new JSONObject(remoteResponse);
			return response.toString();
		}catch (MalformedURLException e){
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg,e);
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		} catch (JSONException e) {
			String msg = "No se pudo generar la respuesta json en relay service ya que el servicio ha respondido en un objeto JSON inv&aacute;lido";
			log.error(msg,e);
			log.debug("Remote response: "+remoteResponse);
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		}
	}
	
	@POST
	@Path("/submit")
	@Produces("application/json")
	public String submit(
			@FormParam("submit_data") String submit_data, 
			@FormParam("url") String submit_url) {
		
		JSONObject response;
		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteSubmissionHost()+submit_url);
			
			remoteResponse = relay.submit(url, submit_data);
			response = new JSONObject(remoteResponse);
			return response.toString();
		}catch (MalformedURLException e){
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg,e);
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		} catch (JSONException e) {
			String msg = "No se pudo generar la respuesta json en relay service ya que el servicio remoto ha respondido en un objeto JSON inv&aacute;lido";
			log.error(msg,e);
			log.debug("Remote response: "+remoteResponse);
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		}
	}
	
	@GET
	@Path("/retrieve")
	@Produces("application/json")
	public String retrieve(@QueryParam("recordId") String recordId) {
		
		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteSubmissionHost()+"/api/deposition/draft/"+recordId);
			
			remoteResponse = relay.retrieve(url);
			return remoteResponse;
		}catch (MalformedURLException e){
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg,e);
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		}
	}
	
}
