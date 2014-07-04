/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@Context
	HttpServletRequest request;

	@GET
	@POST
	@Produces("application/json")
	public String relay() {
		return "{\"success\":false,\"msg\": \"No se ha configurado correctamente la plataforma\"}";
	}

	@POST
	@Path("/relay")
	@Produces("application/json")
	public String relay(@FormParam("remoteUrl") String remoteUrl,
			@FormParam("fkey") String fkey, String tipo) {
		log.info("Ingreso en relay de " + remoteUrl
				+ (fkey != null ? " con el siguiente fkey " + fkey : ""));

		JSONObject response;
		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteListHost() + remoteUrl);
			remoteResponse = relay.request(url, fkey, tipo);
			response = new JSONObject(remoteResponse);
			return response.toString();
		} catch (MalformedURLException e) {
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida ("
					+ frp.getRemoteListHost() + remoteUrl + ")";
			log.error(msg, e);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (JSONException e) {
			String msg = "No se pudo generar la respuesta json en relay service ya que el servicio ha respondido en un objeto JSON inv&aacute;lido";
			log.error(msg, e);
			log.debug("Remote response: " + remoteResponse);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		}
	}

	@POST
	@Path("/submit")
	@Produces("application/json")
	public String submit(@FormParam("submit_data") String submit_data,
			@FormParam("url") String submit_url) {

		JSONObject response;
		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteSubmissionHost() + submit_url);

			remoteResponse = relay.submit(url, submit_data);
			response = new JSONObject(remoteResponse);
			return response.toString();
		} catch (MalformedURLException e) {
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg, e);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (JSONException e) {
			String msg = "No se pudo generar la respuesta json en relay service ya que el servicio remoto ha respondido en un objeto JSON inv&aacute;lido";
			log.error(msg, e);
			log.debug("Remote response: " + remoteResponse);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		}
	}

	@GET
	@Path("/retrieve")
	@Produces("application/json")
	public String retrieve(@QueryParam("recordId") String recordId) {

		String remoteResponse = "";
		try {
			URL url = new URL(frp.getRemoteDraftHost() + "/" + recordId);

			remoteResponse = relay.retrieve(url);
			return remoteResponse;
		} catch (MalformedURLException e) {
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg, e);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		}
	}

	/**
	 * @author ltroconis
	 * 
	 * @param submit_data
	 *            el json de carga masiva
	 * @param submit_url
	 *            la url donde se va a persistir los datos
	 * @return string con un mensaje json con success y msg deacuerdo al
	 *         procesado realizado
	 */
	@POST
	@Path("/submit/massive")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String massiveSubmit(String json) {

		ObjectMapper mapper = new ObjectMapper();
		String remoteResponse = "{\"success\":false,\"msg\":\"Unable to relay message\"}";
		try {
			JsonNode jsonObj = mapper.readTree(json);
			if (!jsonObj.has("submit_data") || !jsonObj.has("url")) {
				String msg = "Alguno de los par&aacute;metros necesarios para el env&iacute;o del formulario al servicio de carga masiva no se encuentra.";
				return "{\"success\":false,\"msg\": \"" + msg + "\"}";
			}
			jsonObj = jsonObj.get("submit_data");
			URL url = new URL(frp.getRemoteDraftHost());
			remoteResponse = relay.massiveSubmit(url, jsonObj,request);
			JSONObject response = new JSONObject(remoteResponse);
			return response.toString();
		} catch (JsonParseException e) {
			String msg = "Error en parse de json, formato invalido";
			log.warn(msg, e);
			// log.debug("JSON received: " + json);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (JsonProcessingException e) {
			String msg = "Error procesando json, formato invalido";
			log.warn(msg, e);
			// log.debug("JSON received: " + json);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (MalformedURLException e) {
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg, e);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (JSONException e) {
			String msg = "No se pudo generar la respuesta json en relay service ya que el servicio remoto ha respondido en un objeto JSON inv&aacute;lido";
			log.error(msg, e);
			log.debug("Remote response: " + remoteResponse);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (Exception e) {
			String msg = "Error desconocido";
			log.warn(msg, e);
			// log.debug("JSON received: " + json);
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		}
	}

}
