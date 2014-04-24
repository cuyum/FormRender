package ar.com.cuyum.cnc.service.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.service.JsonServices;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/external")
@RequestScoped
public class ExternalRest {

	@Context
	HttpServletResponse response;

	@Context
	HttpServletRequest request;

	@Inject
	private JsonServices jsonService;

	@Inject
	RelayService relay;


	private transient static Logger log = Logger.getLogger(ExternalRest.class);

	@PostConstruct
	protected void init() {
		//-Dfile.encoding="UTF-8"
		String fileEncoding=System.getProperty("file.encoding");
		log.info("file.encoding="+fileEncoding);
		log.info("request encoding="+request.getCharacterEncoding());
		log.info("response encoding="+response.getCharacterEncoding());

		if (	fileEncoding==null ||
				fileEncoding.equals("")||
				!(fileEncoding.equals("UTF-8")||
				fileEncoding.toLowerCase().equals("UTF8"))) {
			log.warn("La propiedad de sistema file.encoding no es UTF8 ni UTF-8, es: "+fileEncoding);
		}

//		System.setProperty("file.encoding", "UTF-8");
//
//		boolean forceEncoding = true;
//		String encoding = "UTF-8";
//		if (forceEncoding) {
//			if (request.getCharacterEncoding()==null) {
//				try {
//					request.setCharacterEncoding(encoding);
//				} catch (UnsupportedEncodingException e) {
//					log.warn("No se puede setear encoding "+encoding+ " a request");
//				}
//			}
//
//			response.setCharacterEncoding(encoding);
//		}
	}


	@POST
	@Path("/preview")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	public String previewHtml(@Context ServletContext servletContext,
			@FormParam("value") String json) {

	    ObjectMapper mapper = new ObjectMapper();

	    JsonNode ojson;
        try {
            ojson = mapper.readTree(json);
            json =  ojson.toString();
        } catch (JsonProcessingException e2) {
            log.error(e2);
            try {
                response.sendError(400, "Error en conversion de json");
            } catch (IOException e) {
                log.error(e);
            }
        } catch (IOException e2) {
            log.error(e2);
            try {
                response.sendError(400, "Error en conversion de json");
            } catch (IOException e) {
                log.error(e);
            }
        }


		System.out.println("value="+json);
		String ret;
		try {
			ret = jsonService.previewHtml(json, servletContext);
			return ret;
		} catch (Exception e) {
			log.error(e);
			// no se si es la mejor manera de mandar error
			try {
				response.sendError(400, "Error en conversion de json");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				log.error(e1);
			}
			return e.getMessage();
		}
	}



	@POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response formConsumerURL(String input){
    	Response.ResponseBuilder builder = null;
        String ret =  "http://"+ request.getServerName() +":" +request.getServerPort() + request.getServletContext().getContextPath() + "/formulario/display.xhtml?id=";
        String cod ="";
		
        try{
			cod = jsonService.persistFormFromJson(input);
			if(cod.contains("duplicate")){
				builder = Response.ok();
				return builder.status(200).entity("{'success':'false','result':'', 'code':'0', 'message':'El codigo del formulario ya ha sido utilizado'}").build();
			}else{
				builder = Response.ok();
				return builder.status(200).entity("{'success':'true','result': ' " + ret + cod +"&repeat=1" + "'}").build();
			}
		} catch (Exception e) {
			builder = Response.serverError();
			//				response.sendError(400, "Error en conversion de json");
			return builder.status(400).entity("Error en conversion de json").build();
		}
		
	}


	@GET
	@Path("/preview2")
	@Produces("text/html")
	public String formConsumerHTML(@Context ServletContext servletContext,
			@FormParam("value") String json) {
		String builder = null;
		System.out.println("value="+json);


		try {


			URL url = new URL("http://" + request.getServerName() + ":"+request.getServerPort()+"/"+request.getServletContext().getServletContextName()
					+ "/formulario/display.xhtml?id=C1.1&repeat=1");

			builder = relay.retrieve(url);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			builder = new String(builder.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(builder);
		return builder;
	}





}
