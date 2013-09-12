/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.primefaces.json.JSONObject;

import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.FormRenderProperties;
import ar.com.cuyum.cnc.view.FormularioBean;

@Path("/external")
@RequestScoped
public class JSONtoXMLConverterRest {

	private String context="http://54.232.16.128:8080/FormRender";
		
	@Inject
	private FormularioBean formularioBean;
	
	@Inject
	private RelayService relay;
	
	@Inject
	private FormRenderProperties frp;
	
//	private InetAddress a;
//	private ServletContext sc;
	 
	 
	@POST
    @Path("/preview")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/html; charset=utf-8")
    public String formConsumerHTML(String input){
		String builder = null;
//		log.error(a.getHostAddress());
//		log.error(sc.getContextPath());
//    	try {
//			a = InetAddress.getLocalHost();
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//			
//		}
//    	
        
    	try {
//          String page ="<meta http-equiv='refresh' content='0;url="+ context  + "/formulario/display.xhtml?id=C1.1&repeat=1' /> ";

    		URL url = new URL(context  + "/formulario/display.xhtml?id=C1.1&repeat=1");
			
			builder = relay.submit(url, null);

			
        
        } catch (Exception e) {
            // Handle generic exceptions
//           
//         Map<String, String> responseObj = new HashMap<String, String>();
//        responseObj.put("error", e.getMessage());
//        builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    return builder;
   }
    
   
    
    @POST
    @Path("/url")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_FORM_URLENCODED, "application/json"})
    public String formConsumerURL(String  input){
//    	Formulario formulario = formularioBean.findByCode("C1.4");
//    	
//    	String url=formulario.getUrl();
//        log.info(url);
    	try {
            // Validates member using bean validation
        	
//        	customerPersistenceService.save(customer);

            // Create an "ok" response
            
        } catch (Exception e) {
            
        }
        return context + "/formulario/display.xhtml?id=C1.1&repeat=1";
    }


}
