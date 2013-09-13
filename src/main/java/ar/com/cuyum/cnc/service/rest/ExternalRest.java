package ar.com.cuyum.cnc.service.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

import org.jboss.logging.Param;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.com.cuyum.cnc.service.JsonServices;
import ar.com.cuyum.cnc.service.RelayService;

@Path("/external")
@RequestScoped
public class ExternalRest {

	@Context
	HttpServletResponse response;

	@Context
	HttpServletRequest request;

	@Inject
	private JsonServices jsonService;

	@POST
	@Path("/preview")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html")
	public String previewHtml(@Context ServletContext servletContext,
			@FormParam("value") String json) {
		
		//formate el json antes de continuar
		try {
			JSONObject ojson = (JSONObject) ((new JSONParser()).parse(json));
			json = ojson.toJSONString();
		} catch (ParseException e2) {
			e2.printStackTrace();
			try {
				response.sendError(400, "Error en conversion de json");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return e2.getMessage();
		}
		
		System.out.println("value="+json);
		String ret;
		try {
			ret = jsonService.previewHtml(json, servletContext);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			// no se si es la mejor manera de mandar error
			try {
				response.sendError(400, "Error en conversion de json");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return e.getMessage();
		} 
	}

//	@POST
//	@Path("/url")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces({ MediaType.APPLICATION_FORM_URLENCODED, "application/json" })
//	public String formConsumerURL(String input) {
//		
//		
//		try {
//			URL url = new URL("http://" + request.getServerName() + ":"+request.getServerPort()+"/"+request.getServletContext().getServletContextName()
//					+ "/formulario/display.xhtml?id=C1.1&repeat=1");
//			
//			return url.toString();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "URL INVALIDA";
//		}
//		
//		
//	}
	
	
    @POST
    @Path("/url")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/json")
    public String formConsumerURL(@FormParam("value") String input){
    	
        String ret =  "http://"+ request.getServerName() +":" +request.getServerPort() + request.getServletContext().getContextPath() + "/formulario/display.xhtml?id=";
        String cod ="";
		try{
			cod = jsonService.persistFormFromJson(input);
			return ret + cod;
		} catch (Exception e) {
			//no se si es la mejor manera de mandar error
			try {
				response.sendError(400, "Error en conversion de json");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "ERROR AL POSTEAR FORM";
		}
		
	}
	

	
	/// BORAR DESPUES ESE METODO/// solo para hacer prueba
	@Inject
	RelayService relay;


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

		return builder;
	}
	

	@GET
	@Path("/previewUTF")
	@Produces("text/html; charset=UTF-8")
	public String formConsumerHTML3(@Context ServletContext servletContext,
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

		return builder;
	}
	

	@GET
	@Path("/previewISO")
	@Produces("text/html; charset=ISO-8859-1")
	public String formConsumerHTML4(@Context ServletContext servletContext,
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

		return builder;
	}


}
