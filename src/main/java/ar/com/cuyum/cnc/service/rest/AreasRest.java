/**
 * 
 */
package ar.com.cuyum.cnc.service.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

/**
 * @author Jorge Morando
 *
 */
@Path("/areas")
@RequestScoped
public class AreasRest {

	private Logger log = Logger.getLogger(AreasRest.class);

	private static List<Map<String,String>> provincias;
	private static List<Map<String,String>> partidos;
	private static List<Map<String,String>> localidades;
	private static List<Map<String,String>> areas;
	
	@GET
	@Path("/provincias")
	@Produces("application/json")
	public String provincias() {
		log.debug("Se buscan provincias");
		
		/*stub response*/
		Map<String,Object> responseMap = new HashMap<String, Object>();
		responseMap.put("list", provincias);
		responseMap.put("success", true);
		
		/*in form of json*/
		JSONObject response = new JSONObject(responseMap);
		
		return response.toString();
	}
	
	@GET
	@Path("/partidos")
	@Produces("application/json")
	public String partidos(@QueryParam("fkey") final String fkey) {
		log.debug("Se buscan partidos");
		
		
		List<Map<String,String>> p = new ArrayList<Map<String,String>>();
		
		if(fkey!=null && !fkey.isEmpty() && !fkey.equalsIgnoreCase("0") )
		for (Map<String, String> partido : partidos) {
			if(partido.get("fkey").equals(fkey))
				p.add(partido);
		}
		
		/*stub response*/
		Map<String,Object> responseMap = new HashMap<String, Object>();
		responseMap.put("list", p);
		responseMap.put("success", true);
		
		/*in form of json*/
		JSONObject response = new JSONObject(responseMap);
		
		return response.toString();
	}
	
	@GET
	@Path("/localidades")
	@Produces("application/json")
	public String localidades(@QueryParam("fkey") final String fkey) {
		log.debug("Se buscan localidades");
		
		List<Map<String,String>> l = new ArrayList<Map<String,String>>();
		
		if(fkey!=null && !fkey.isEmpty() && !fkey.equalsIgnoreCase("0"))
		for (Map<String, String> localidad : localidades) {
			if(localidad.get("fkey").equals(fkey))
				l.add(localidad);
		}
		
		/*stub response*/
		Map<String,Object> responseMap = new HashMap<String, Object>();
		responseMap.put("list", l);
		responseMap.put("success", true);
		
		/*in form of json*/
		JSONObject response = new JSONObject(responseMap);
		
		return response.toString();
	}
	
	@GET
	@Path("/areas")
	@Produces("application/json")
	public String areas(@QueryParam("fkey") final String fkey) {
		log.debug("Se buscan areas");
		
		List<Map<String,String>> a = new ArrayList<Map<String,String>>();
		
		if(fkey!=null && !fkey.isEmpty() && !fkey.equalsIgnoreCase("0"))
		for (Map<String, String> area : areas) {
			if(area.get("fkey").equals(fkey))
				a.add(area);
		}
		
		/*stub response*/
		Map<String,Object> responseMap = new HashMap<String, Object>();
		responseMap.put("list", a);
		responseMap.put("success", true);
		
		/*in form of json*/
		JSONObject response = new JSONObject(responseMap);
		
		return response.toString();
	}
	
	public AreasRest(){
	
		/*PROVINCIAS*/
		Map<String,String> provincia1 = new HashMap<String,String>();
		provincia1.put("label", "Buenos Aires");
		provincia1.put("value", "1");
		Map<String,String> provincia2 = new HashMap<String,String>();
		provincia2.put("label", "San Juan");
		provincia2.put("value", "2");
		
		/*stub list*/
		provincias = new ArrayList<Map<String,String>>();
		provincias.add(provincia1);
		provincias.add(provincia2);
		
		/*PARTIDOS*/
		Map<String,String> partido1 = new HashMap<String,String>();
		partido1.put("label", "Tandil");
		partido1.put("value", "10");
		partido1.put("fkey", "1");
		Map<String,String> partido2 = new HashMap<String,String>();
		partido2.put("label", "Gral Pueyrredón");
		partido2.put("value", "11");
		partido2.put("fkey", "1");
		Map<String,String> partido3 = new HashMap<String,String>();
		partido3.put("label", "Capital");
		partido3.put("value", "20");
		partido3.put("fkey", "2");
		Map<String,String> partido4 = new HashMap<String,String>();
		partido4.put("label", "Rawson");
		partido4.put("value", "21");
		partido4.put("fkey", "2");
		
		/*stub list*/
		partidos = new ArrayList<Map<String,String>>();
		partidos.add(partido1);
		partidos.add(partido2);
		partidos.add(partido3);
		partidos.add(partido4);
		
		/*LOCALIDADES*/
		Map<String,String> localidad1 = new HashMap<String,String>();
		localidad1.put("label", "Gardey");
		localidad1.put("value", "100");
		localidad1.put("fkey", "10");
		Map<String,String> localidad2 = new HashMap<String,String>();
		localidad2.put("label", "Vela");
		localidad2.put("value", "101");
		localidad2.put("fkey", "10");
		Map<String,String> localidad3 = new HashMap<String,String>();
		localidad3.put("label", "Mar del Plata");
		localidad3.put("value", "200");
		localidad3.put("fkey", "11");
		Map<String,String> localidad4 = new HashMap<String,String>();
		localidad4.put("label", "Miramar");
		localidad4.put("value", "201");
		localidad4.put("fkey", "11");
		Map<String,String> localidad5 = new HashMap<String,String>();
		localidad5.put("label", "Concepcion");
		localidad5.put("value", "300");
		localidad5.put("fkey", "20");
		Map<String,String> localidad6 = new HashMap<String,String>();
		localidad6.put("label", "San Martin");
		localidad6.put("value", "301");
		localidad6.put("fkey", "20");
		Map<String,String> localidad7 = new HashMap<String,String>();
		localidad7.put("label", "Villa Krause");
		localidad7.put("value", "400");
		localidad7.put("fkey", "21");
		Map<String,String> localidad8 = new HashMap<String,String>();
		localidad8.put("label", "Rawson");
		localidad8.put("value", "401");
		localidad8.put("fkey", "21");
		
		/*stub list*/
		localidades = new ArrayList<Map<String,String>>();
		localidades.add(localidad1);
		localidades.add(localidad2);
		localidades.add(localidad3);
		localidades.add(localidad4);
		localidades.add(localidad5);
		localidades.add(localidad6);
		localidades.add(localidad7);
		localidades.add(localidad8);
		
		/*AREAS*/
		Map<String,String> area1 = new HashMap<String,String>();
		area1.put("label", "Gardey1");
		area1.put("value", "1000");
		area1.put("fkey", "100");
		Map<String,String> area2 = new HashMap<String,String>();
		area2.put("label", "Gardey2");
		area2.put("value", "1001");
		area2.put("fkey", "100");
		Map<String,String> area3 = new HashMap<String,String>();
		area3.put("label", "Vela1");
		area3.put("value", "1002");
		area3.put("fkey", "101");
		Map<String,String> area4 = new HashMap<String,String>();
		area4.put("label", "Vela2");
		area4.put("value", "1003");
		area4.put("fkey", "101");
		
		Map<String,String> area5 = new HashMap<String,String>();
		area5.put("label", "MDP1");
		area5.put("value", "2000");
		area5.put("fkey", "200");
		Map<String,String> area6 = new HashMap<String,String>();
		area6.put("label", "MDP2");
		area6.put("value", "2001");
		area6.put("fkey", "200");
		Map<String,String> area7 = new HashMap<String,String>();
		area7.put("label", "Miramar1");
		area7.put("value", "2002");
		area7.put("fkey", "201");
		Map<String,String> area8 = new HashMap<String,String>();
		area8.put("label", "Miramar2");
		area8.put("value", "2003");
		area8.put("fkey", "201");
		
		Map<String,String> area9 = new HashMap<String,String>();
		area9.put("label", "Concepcion1");
		area9.put("value", "3000");
		area9.put("fkey", "300");
		Map<String,String> area10 = new HashMap<String,String>();
		area10.put("label", "Concepcion2");
		area10.put("value", "3001");
		area10.put("fkey", "300");
		Map<String,String> area11 = new HashMap<String,String>();
		area11.put("label", "SM1");
		area11.put("value", "3002");
		area11.put("fkey", "301");
		Map<String,String> area12 = new HashMap<String,String>();
		area12.put("label", "SM2");
		area12.put("value", "3003");
		area12.put("fkey", "301");
		
		Map<String,String> area13 = new HashMap<String,String>();
		area13.put("label", "VK1");
		area13.put("value", "4000");
		area13.put("fkey", "400");
		Map<String,String> area14 = new HashMap<String,String>();
		area14.put("label", "VK2");
		area14.put("value", "4001");
		area14.put("fkey", "400");
		Map<String,String> area15 = new HashMap<String,String>();
		area15.put("label", "Rawson1");
		area15.put("value", "4002");
		area15.put("fkey", "401");
		Map<String,String> area16 = new HashMap<String,String>();
		area16.put("label", "Rawson2");
		area16.put("value", "4002");
		area16.put("fkey", "401");
		
		/*stub list*/
		areas = new ArrayList<Map<String,String>>();
		areas.add(area1);
		areas.add(area2);
		areas.add(area3);
		areas.add(area4);
		areas.add(area5);
		areas.add(area6);
		areas.add(area7);
		areas.add(area8);
		areas.add(area9);
		areas.add(area10);
		areas.add(area11);
		areas.add(area12);
		areas.add(area13);
		areas.add(area14);
		areas.add(area15);
		areas.add(area16);
		
	}
}
