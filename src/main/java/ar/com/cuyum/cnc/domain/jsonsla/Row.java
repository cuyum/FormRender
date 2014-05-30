package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 * 
 */
public class Row implements Serializable {
	public transient static Logger log = Logger.getLogger(Row.class);

	private static final long serialVersionUID = 1L;
	private Map<String, Componente> mapComponets = new HashMap<String, Componente>();
	
	public Row(Map<String, Componente> mapComponents) {	
		
		Iterator<Entry<String, Componente>> iterListComponent = mapComponents
				.entrySet().iterator();
		
		//Asigno los componentes
		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();			
			Componente clon = (Componente) componente.getValue().clone();
			this.mapComponets.put(componente.getKey(),clon);
		}
		
		initAllComponents();
	}
	
	public void sumarizar(List<String> sumarizados){
		Numero total = null;
		for (int i = 0, n = sumarizados.size(); i < n; i++) {
			String name = sumarizados.get(i);
			Numero aSumar = (Numero) this.mapComponets.get(name);
			if (total == null) {
				total = (Numero) ((Componente) aSumar).clone();
			} else {
				total = total.sum(aSumar);
			}
		}
		this.mapComponets.put("rowTotal", (Componente) total);
	}
	
	private void initAllComponents(){
		Iterator<Entry<String, Componente>>  iterListComponent = this.mapComponets.entrySet().iterator();

		//A cada componente le paso la lista de todos los componentes, para las validaciones
		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();
			componente.getValue().setAllComponet(mapComponets);
		}
	}	
	
	public void setDataByJson(JsonNode data) throws ExceptionValidation {
		Iterator<String> keys = data.fieldNames();
		while (keys.hasNext()) {
			String name = keys.next();
			JsonNode value = data.get(name);
			Componente componente = mapComponets.get(name);
			componente.setValueFromJson(value);			
		}
	}

	public Boolean isDataValid() throws ExceptionValidation {
		
		List<String> names = new ArrayList<String>();

		Iterator<Entry<String, Componente>> iterListComponent = mapComponets
				.entrySet().iterator();

		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();
			try {
				log.info("validando " + componente.getKey()+" de tipo: "+componente.getValue().getType());
				componente.getValue().isDataValid(componente.getKey());
				log.info(componente.getKey()+": es valido");
			} catch (ExceptionValidation e) {
				String msg = "El valor de la componente: "
						+ componente.getKey() + " - ";
				log.error(msg + " " + e.getMessage());
				throw new ExceptionValidation(msg + " " + e.getMessage());
			}
		}

		return true;
	}

	public ObjectNode valuesToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode row = mapper.createObjectNode();
		Iterator<Entry<String, Componente>> iterListComponent = mapComponets
				.entrySet().iterator();
		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();
			if ((componente.getValue() != null))
				row.put(componente.getKey(), componente.getValue()
						.valueToJson());
		}
		return row;
	}
	
	public Componente getFieldByName(String name){
		return this.mapComponets.get(name);
	}

}