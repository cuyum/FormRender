/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.cuyum.cnc.service.FileService;
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
	
	@Inject
	private FileService fileService;

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
	
	private String doFileToMasive(String fileName){
		
		File jsonFile = new File(fileName);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = null;
		try {
			jsonObj = mapper.readTree(jsonFile);
		} catch (JsonProcessingException e) {
			String msg = "Error en parse de json, formato invalido";
			log.warn(msg, e);			
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		} catch (IOException e) {
			String msg = "Error en parse de json, archivo invalido";
			log.warn(msg, e);			
			return "{\"success\":false,\"msg\": \"" + msg + "\"}";
		}
		
		return doMassive(jsonObj);
			
	}
	
	
	@POST
	@Path("/upload/massive")
	@Consumes("multipart/form-data")
	@Produces("application/json")
	public String uploadFile(MultipartFormDataInput input) {
 
		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		if (uploadForm.containsKey("file") == false) {
			return "{\"success\":false,\"msg\": \""
					+ "No se encontro la entrada 'file'" + "\"}";
		}

		List<InputPart> inputParts = uploadForm.get("file");

		for (InputPart inputPart : inputParts) {

			try {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);

				if (fileName.endsWith(".zip") == false) {
					return "{\"success\":false,\"msg\": \"" + "El archivo debe ser un zip" + "\"}";
				}

				InputStream inputStream = inputPart.getBody(InputStream.class,
						null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				fileName = "/tmp/" + fileName;

				writeFile(bytes, fileName);

				log.info("Guardado el archivo:");

				String fileNamejson = fileName.replace(".zip", ".json");

				fileService.UnZip(fileName, fileNamejson);

				return doFileToMasive(fileNamejson);

			} catch (IOException e) {
				e.printStackTrace();
				return "{\"success\":false,\"msg\": \""	+ "Error subiendo el archivo" + "\"}";
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"success\":false,\"msg\": \""	+ "Error decomprimiendo el archivo" + "\"}";
			}

		}

		return "{\"success\":true,\"msg\": \"" + "upload:" + fileName + "\"}";
		 
	}
 
	/**
	 * header sample
	 * {
	 * 	Content-Type=[image/png], 
	 * 	Content-Disposition=[form-data; name="file"; filename="filename.extension"]
	 * }
	 **/
	//get uploaded filename, is there a easy way in RESTEasy?
	private String getFileName(MultivaluedMap<String, String> header) {
 
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
 
		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
 
				String[] name = filename.split("=");
 
				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
 
	//save to somewhere
	private void writeFile(byte[] content, String filename) throws IOException {
 
		File file = new File(filename);
 
		if (!file.exists()) {
			file.createNewFile();
		}
 
		FileOutputStream fop = new FileOutputStream(file);
 
		fop.write(content);
		fop.flush();
		fop.close();
 
	}
		
	private String doMassive(JsonNode jsonObj){
		String response = "{\"success\":false,\"msg\":\"Unable to relay message\"}";
		try {			
			Runtime garbage = Runtime.getRuntime();
		    garbage.gc();
			if (!jsonObj.has("submit_data") || !jsonObj.has("url")) {
				String msg = "Alguno de los par&aacute;metros necesarios para el env&iacute;o del formulario al servicio de carga masiva no se encuentra.";
				return "{\"success\":false,\"msg\": \"" + msg + "\"}";
			}
			jsonObj = jsonObj.get("submit_data");
			URL url = new URL(frp.getRemoteDraftHost());
			//URL url = new URL(frp.getRemoteSubmissionHost() + "/api/deposition");
			response = relay.massiveSubmit(url, jsonObj,request);
			jsonObj=null;
			garbage.gc();
			//response = new JSONObject(response).toString();
			log.info("El servicio rest envia respuesta:"+response);	
			return response;
		} catch (MalformedURLException e) {
			String msg = "No se pudo generar la petici&oacute;n con relay service ya que la URL suministrada es inv&aacute;lida";
			log.error(msg, e);
			response = "{\"success\":false,\"msg\": \"" + msg + "\"}";
			return response;
		} catch (Exception e) {
			String msg = "Un error ha ocurrido";
			log.warn(msg, e);
			response = "{\"success\":false,\"msg\": \"" + msg + "\"}";
			return response;
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
		String response = "{\"success\":false,\"msg\":\"Unable to relay message\"}";
		JsonNode jsonObj;
		try {
			jsonObj = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			String msg = "Error procesando json, formato invalido";
			log.warn(msg, e);			
			response = "{\"success\":false,\"msg\": \"" + msg + "\"}";
			return response;
		} catch (IOException e) {
			String msg = "Error procesando json, formato invalido";
			log.warn(msg, e);			
			response = "{\"success\":false,\"msg\": \"" + msg + "\"}";
			return response;
		}
		
		return doMassive(jsonObj);
	}
}
