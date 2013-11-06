package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 *
 */
public class Formulario  implements Serializable{
	public transient static Logger log = Logger.getLogger(Formulario.class);

	private static final long serialVersionUID = 1L;
	private Map<String,Componente> listComponets= new HashMap<String,Componente>();
	private String id;
	
	//No lo injecto porque me daba problemas de recursividad 
	//tal vez hay que pensar mejor esta parte
	private RelayService relayService;
	
	public Formulario(String idForm, JsonNode schemaItemsProperties,RelayService relayService){
		this.init(schemaItemsProperties);
		this.relayService = relayService;
		this.id=idForm;
	}
	
	public String getId(){
		return id;
	}
	
	/**
	 * Inicializa mapas para validacion del json.
	 * Los campos requeridos no son tomados en cuenta, pues ya se validaron con el jsonschema. 
	 * 
	 * @param schemaItemsProperties
	 */
	private void init(JsonNode schemaItemsProperties){
		Iterator<String> names = schemaItemsProperties.fieldNames();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		while(names.hasNext()){
			String name = names.next();
			JsonNode nodePropertie = schemaItemsProperties.get(name);
			String type = nodePropertie.get("$ref").asText().split("#/definitions/")[1];
			
			Componente component = null;
			try {
				component = (Componente) mapper.readValue(nodePropertie.toString(), Componente.getTypeClass(type));
				listComponets.put(name,component);					
			} catch (JsonParseException e) {
				log.error("el schema propertie tiene formato inavalido",e);
			} catch (JsonMappingException e) {
				log.error("el schema propertie tiene formato inavalido",e);
			} catch (IOException e) {
				log.error("el schema propertie tiene formato inavalido",e);
			}	
			
			log.info("objeto mappeado "+component);
		}
		
	}
	
	public void setDataByJson(JsonNode data) throws ExceptionValidation  {
		Iterator<String> keys = data.fieldNames();
		while (keys.hasNext()) {
			String name = keys.next();
			JsonNode value = data.get(name);
			Componente componente = listComponets.get(name);
			componente.setValueFromJson(value);
		}
	}
	
	public Boolean isDataValid() throws ExceptionValidation {

		Iterator<Entry<String,Componente>> iterListComponent = listComponets.entrySet()
				.iterator();
		
		while (iterListComponent.hasNext()) {
			Map.Entry<String,Componente> componente = (Map.Entry<String,Componente>) iterListComponent
					.next();
			try {
				if(Componente.COMBO.equals( componente.getValue().getType()))
					((Combo) componente.getValue()).setRelayService(relayService);				
				componente.getValue().setAllComponet(listComponets);
				log.info("validando:"+componente.getKey());
				componente.getValue().isDataValid();
			} catch (ExceptionValidation e) {
				String msg = "Error validando componente "+componente.getKey()+",";
				log.error(msg+" "+e.getMessage());
				throw new ExceptionValidation(msg+" "+e.getMessage());
			}
		}

		return true;
	}
	
	public String toString(){
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode formulario = mapper.createObjectNode();
		Iterator<Entry<String,Componente>> iterListComponent = listComponets.entrySet()
				.iterator();		
		while (iterListComponent.hasNext()) {
			Map.Entry<String,Componente> componente = (Map.Entry<String,Componente>) iterListComponent
					.next();
			formulario.put(componente.getKey(),(componente.getValue()==null)?null:componente.getValue().valueToJson());
		}
		return formulario.toString();
	} 
	
}