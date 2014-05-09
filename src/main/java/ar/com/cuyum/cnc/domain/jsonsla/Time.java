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

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;

/**
 * 
 * @author ltroconis
 */
// @JsonIgnoreProperties(ignoreUnknown = true)
public class Time extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();

	private String value;

	public String toString() {
		return super.toString()
				+ ((value == null) ? " value = null" : " value = (" + value
						+ ")")
				+ ((relevant.size() > 0) ? " relevant size=" + relevant.size()
						+ " relevant values: " + relevantMapToString() : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof Time) || o == null)
			return false;

		Time other = ((Time) o);

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

	@Override
	public String getValueToString() {
		if (value!=null) return value.toString();
		return null;
	}
	
	@Override
	public String getType() {		
		return Componente.TIME;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}

	
}
