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
	private List<String> sumarizados = new ArrayList<String>();
	//private Boolean totalizar;	
	//private RelayService relayService;

	public Row(Map<String, Componente> listComponets/*,RelayService relayService*/) {
		this.mapComponets.putAll(listComponets);		
		Iterator<Entry<String, Componente>> iterListComponent = this.mapComponets
				.entrySet().iterator();
		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();
			componente.getValue().setAllComponet(listComponets);
		}
	}
	
	public void setDataByJson(JsonNode data) throws ExceptionValidation {
		Iterator<String> keys = data.fieldNames();
		while (keys.hasNext()) {
			String name = keys.next();
			JsonNode value = data.get(name);
			Componente componente = mapComponets.get(name);
			componente.setValueFromJson(value);
			
			/*if(Componente.COMBO.equals(componente.getType())){
				((Combo)componente).setRelayService(relayService);
			}**/
		}
	}

/*	public void totalizar() {
		if (totalizar) {
			Entero total = new Entero();
			for (int i = 0, n = sumarizados.size(); i < n; i++) {
				String name = sumarizados.get(i);
				Componente aSumar = listComponets.get(name);
				if (Componente.INTEGER.equals(aSumar.getType())) {
					Long sum = (total.getValue() == null) ? 0 : total
							.getValue();
					Long value = ((Entero) aSumar).getValue();
					if (value != null) {
						total.setValue(sum = sum + value);
					}
				}
			}
			listComponets.put("rowTotal", total);
		}
	}*/

	public Boolean isDataValid() throws ExceptionValidation {

		Iterator<Entry<String, Componente>> iterListComponent = mapComponets
				.entrySet().iterator();

		while (iterListComponent.hasNext()) {
			Map.Entry<String, Componente> componente = (Map.Entry<String, Componente>) iterListComponent
					.next();
			try {
				/*if (Componente.COMBO.equals(componente.getValue().getType()))
					((Combo) componente.getValue())
							.setRelayService(relayService);*/
				
				log.info("validando:" + componente.getKey());
				componente.getValue().isDataValid();
				log.info(componente.getKey()+" tipo "+componente.getValue().getType()+": es valido");
			} catch (ExceptionValidation e) {
				String msg = "Error validando componente "
						+ componente.getKey() + ",";
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

}