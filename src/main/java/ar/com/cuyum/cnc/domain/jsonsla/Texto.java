package ar.com.cuyum.cnc.domain.jsonsla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ar.com.cuyum.cnc.exceptions.ExceptionComboRelayUrl;
import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

/**
 * 
 * @author ltroconis
 */
// @JsonIgnoreProperties(ignoreUnknown = true)
public class Texto extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();
	private RelayService relayService;
	
	protected void setRelayService(RelayService relayService) {
		this.relayService = relayService;		
	}

	private String value;
	private String url;
	private String hora_delta;

	public String toString() {
		return super.toString()
				+ ((value == null) ? " value = null" : " value = (" + value
						+ ")")
				+ ((relevant.size() > 0) ? " relevant size=" + relevant.size()
						+ " relevant values: " + relevantMapToString() : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof Texto) || o == null)
			return false;

		Texto other = ((Texto) o);

		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.id))
			return false;

		return true;
	}

	public List<String> getRelevant() {
		return relevant;
	}

	public void setRelevant(List<String> relevant) {
		this.relevant = relevant;
		addRelevantMap();
	}

	private void addRelevantMap() {
		for (int i = 0, n = this.relevant.size(); i < n; i++) {
			String[] rel = relevant.get(i).split("=");
			List<String> value = relevantMap.get(rel[0]);
			if (value == null) {
				relevantMap.put(rel[0], Arrays.asList(rel[1]));
			} else {
				value.add(rel[1]);
			}
		}
	}

	private String relevantMapToString() {
		StringBuilder entry = new StringBuilder();
		Iterator<Entry<String, List<String>>> rel = relevantMap.entrySet()
				.iterator();
		while (rel.hasNext()) {
			Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) rel
					.next();
			entry.append(e.getKey() + ":" + e.getValue());
		}

		return entry.toString();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Boolean isDataValid(String name) throws ExceptionValidation {
		// Si el valor es nulo pero hay relevant con los valores seteados
		if (value == null) {
			if (relevant != null && value != null) {
				if (!super.isOkRelevant(relevantMap))
					return false;
			}
		}
		
		if (url != null && hora_delta!=null) {
			log.info("opteniendo valor de la url");
			if (relayService == null)
				throw new ExceptionValidation(
						"Servicio remoto no disponible, para el string");
			JsonNode data;
			try {
				data = getFromUrl(relayService,null,url, "hora");
			} catch (ExceptionComboRelayUrl e) {				
				log.error(e);
				throw new ExceptionValidation(
						"Error en servicio remoto "+e.getMessage());
			}
			log.info(data.toString());
			Time horaDelta = (Time) this.listComponets.get(hora_delta);
			String[] date = horaDelta.getValue().split(":");
			Integer minutes = ((Integer.valueOf(date[0])*60+Integer.valueOf(data.get("result").asText()))+Integer.valueOf(date[1])); 
			Integer resultado = Integer.valueOf(minutes/60);
			minutes = minutes%60;			
			
			String hourRes= String.valueOf(resultado);
			String minRes= String.valueOf(minutes);
			if(hourRes.length()==1)
				hourRes = "0" + hourRes;
			if(minRes.length()==1)
				minRes = "0" + minRes;
			if(hourRes=="24")
				hourRes="00";
			
			this.value = hourRes + ":" + minRes;
		}
		
		// De momento cualquier texto es valido
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		this.value = value.asText();
	}

	@Override
	public JsonNode valueToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode nodo = mapper.createObjectNode();
		nodo.put("string", value);
		return (value==null)?null:nodo.get("string");
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getValueToString() {
		if (value!=null) return value.toString();
		return null;
	}
	
	@Override
	public String getType() {		
		return Componente.STRING;
	}

	public String getHora_delta() {
		return hora_delta;
	}

	public void setHora_delta(String hora_delta) {
		this.hora_delta = hora_delta;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}


}
