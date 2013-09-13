package ar.com.cuyum.cnc.service.rest;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import ar.com.cuyum.cnc.service.JsonServices;


@Path("/external")
@RequestScoped
public class ExternalRest {
	
	@Context HttpServletResponse response;
	
	@Context HttpServletRequest request;
	
	@Inject private JsonServices jsonService;
	
	@SuppressWarnings("finally")
	@POST
    @Path("/preview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/html")
    public String previewHtml(@Context ServletContext servletContext, String json){
		
		String ret;
		try{
			ret = jsonService.previewHtml(json, servletContext);
			return ret;
		} catch (Exception e) {
			//no se si es la mejor manera de mandar error
			response.sendError(400, "Error en conversion de json");
			
		} finally {
			return "";
		}
   }
	
	
   
    
    @POST
    @Path("/url")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_FORM_URLENCODED, "application/json"})
    public String formConsumerURL(String  input){
    	
        return  "http://"+ request.getRemoteAddr() + "/formulario/display.xhtml?id=C1.1&repeat=1";
    }

}
