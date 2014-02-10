package ar.com.cuyum.cnc.service.rest;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

import com.fasterxml.jackson.databind.JsonSerializer;

import ar.com.cuyum.cnc.service.JsonServices;
import ar.com.cuyum.cnc.service.RelayService;

@Path("/previewJson")
@RequestScoped
public class PreviewJsonRest {

	@Context
	HttpServletResponse response;

	@Context
	HttpServletRequest request;

	@Inject
	private JsonServices jsonService;

	@Inject
	RelayService relay;

	private transient static Logger log = Logger
			.getLogger(PreviewJsonRest.class);

	@PostConstruct
	protected void init() {
		// -Dfile.encoding="UTF-8"
		String fileEncoding = System.getProperty("file.encoding");
		log.info("file.encoding=" + fileEncoding);
		log.info("request encoding=" + request.getCharacterEncoding());
		log.info("response encoding=" + response.getCharacterEncoding());

		if (fileEncoding == null
				|| fileEncoding.equals("")
				|| !(fileEncoding.equals("UTF-8") || fileEncoding.toLowerCase()
						.equals("UTF8"))) {
			log.warn("La propiedad de sistema file.encoding no es UTF8 ni UTF-8, es: "
					+ fileEncoding);
		}

	}

	@GET
	@Path("/jsonEjemplo")
	@Produces("application/json")
	public String previewJson(@Context ServletContext servletContext, @FormParam("value") String json) throws IOException {

		InputStream input= this.getClass().getClassLoader().getResourceAsStream("jsonEjemplo.txt");
		
		String resp = new String();
		try {
			resp = jsonService.previewExample(input);
		} catch (Exception exp) {
			log.error(exp);
		}

		return resp;
	}
	
	@GET
	@Path("jsonSchema/{id}")
	@Produces("application/json")
	public String previewSchema(@Context ServletContext servletContext, @PathParam("id") String formId) throws IOException {

		
		String form = formId + "-schema.json";
		InputStream input;		
		
		String resp = new String();
		try {
			input= servletContext.getResourceAsStream("/schemas/" + form);
			resp = jsonService.previewExample(input);
		} catch (Exception exp) {
			log.error(exp);
		}

		return resp;
	}
	
	@GET
	@Path("jsonDates/{id}")
	@Produces("application/json")
	public String previewJsonDates(@Context ServletContext servletContext, @PathParam("id") String formId) throws IOException {

		String form = formId + "-schema.json";
		InputStream input;

		String resp = null;
		String response = null;
		
		try {
			input= servletContext.getResourceAsStream("/schemas/" + form);
			resp = jsonService.previewExample(input);
			response = jsonService.obtenerJson(resp, formId);
		} catch (Exception exp) {
			log.error(exp);
		}
		
		return response;
	}
}
