/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.service.ParameterService;

/**
 * @author Jorge Morando
 *
 */
@Path("/parameter")
@RequestScoped
public class ParameterRest {

	private Logger log = Logger.getLogger(ParameterRest.class);

	@Inject
	private ParameterService ps;
	
	@GET
	@POST
	@Produces("application/json")
	public String relay(){
		return "{\"success\":false,\"msg\": \"No se ha configurado correctamente la plataforma\"}";
	}
	
	@GET
	@Path("/{name}")
	@Produces("application/json")
	public String retrieve(@PathParam(value="name") String name) {
		
		if(name!=null){
			String msg = "Es necesario proveer el nombre del par&aacute;metro para su recuperaci&oacute;n";
			return "{\"success\":false,\"msg\": \""+msg+"\"}";
		}
		
		ps.getHour();
		
		return "";
	}
	
}
