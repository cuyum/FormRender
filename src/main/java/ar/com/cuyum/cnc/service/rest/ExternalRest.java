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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

	@SuppressWarnings("finally")
	@POST
	@Path("/preview")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/html")
	public String previewHtml(@Context ServletContext servletContext,
			String json) {

		String ret;
		try {
			ret = jsonService.previewHtml(json, servletContext);
			return ret;
		} catch (Exception e) {
			// no se si es la mejor manera de mandar error
			response.sendError(400, "Error en conversion de json");

		} finally {
			return "";
		}
	}

	@POST
	@Path("/url")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_FORM_URLENCODED, "application/json" })
	public String formConsumerURL(String input) {
		
		
		try {
			URL url = new URL("http://" + request.getServerName() + ":"+request.getServerPort()+"/"+request.getServletContext().getServletContextName()
					+ "/formulario/display.xhtml?id=C1.1&repeat=1");
			
			return url.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "URL INVALIDA";
		}
		
		
	}

	
	/// BORAR DESPUES ESE METODO/// solo para hacer prueba
	@Inject
	RelayService relay;

	@POST
	@GET
	@Path("/preview2")
	//@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html; charset=utf-8")
	public String formConsumerHTML() {
		String builder = null;
		// log.error(a.getHostAddress());
		// log.error(sc.getContextPath());
		// try {
		// a = InetAddress.getLocalHost();
		// } catch (UnknownHostException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		//
		// }
		//

		try {
			// String page ="<meta http-equiv='refresh' content='0;url="+
			// context + "/formulario/display.xhtml?id=C1.1&repeat=1' /> ";

			URL url = new URL("http://" + request.getServerName() + ":"+request.getServerPort()+"/"+request.getServletContext().getServletContextName()
					+ "/formulario/display.xhtml?id=C1.1&repeat=1");
			
			
			System.out.println("URL: "+url);
//			System.out.println("request.getLocalAddr(): "+request.getLocalAddr());
//			System.out.println("request.getLocalName(): "+request.getLocalName());
//			System.out.println("request.getLocalPort(): "+request.getLocalPort());
//			System.out.println("request.getRemoteAddr(): "+request.getRemoteAddr());
//			System.out.println("request.getRemoteHost(): "+request.getRemoteHost());
//			System.out.println("request.getRemotePort(): "+request.getRemotePort());
//			System.out.println("request.getServerName(): "+request.getServerName());
//			System.out.println("request.getServerPort(): "+ request.getServerPort());
//			System.out.println("request.getServletPath(): "+request.getServletPath());
//			System.out.println("request.getServletContext().getServletContextName(): "+request.getServletContext().getServletContextName());
//			System.out.println("request.getServletContext().getContextPath(): "+request.getServletContext().getContextPath());

			
			builder = relay.submit(url, null);

		} catch (Exception e) {
			// Handle generic exceptions
			//
			// Map<String, String> responseObj = new HashMap<String, String>();
			// responseObj.put("error", e.getMessage());
			// builder =
			// Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}

		return builder;
	}

}
