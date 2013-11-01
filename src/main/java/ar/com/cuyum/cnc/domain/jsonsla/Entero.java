package ar.com.cuyum.cnc.domain.jsonsla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 */
// @JsonIgnoreProperties(ignoreUnknown = true)
public class Entero extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();

	private Integer value;

	private Integer minimum;
	private Integer maximum;

	public String toString() {
		return super.toString()
				+ ((value == null) ? " value = null" : " value = (" + value
						+ ")")
				+ ((minimum == null) ? "" : " minimum=" + minimum)
				+ ((maximum == null) ? "" : " maximum=" + maximum)
				+ ((relevant.size() > 0) ? " relevant size=" + relevant.size()
						+ " relevant values: " + relevantMapToString() : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof Entero) || o == null)	return false;
		Entero other = ((Entero)o);
		
		if (value == null) {
	            if (other.value != null) return false;
	    } else if (!value.equals(other.id)) return false;
	   
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}

	public Integer getMaximum() {
		return maximum;
	}

	public void setMaximum(Integer maximum) {
		this.maximum = maximum;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {

		//Si el valor es nulo pero hay relevant con los valores seteados
		if (value == null){
			log.info("validando si hay datos para que el objeto sea relevante");
			if (relevant!=null && value!=null){
				if(!super.isOkRelevant(relevantMap)) return false;
			}
		}
		
		if (minimum != null && value!=null && value.compareTo(minimum) < 0) {
			throw new ExceptionValidation("Decimal menor al minimo (" + minimum
					+ ")+ permitido");
		}
		if (maximum != null && value!=null && value.compareTo(maximum) > 0) {

			throw new ExceptionValidation("Decimal mayor al maximo (" + maximum
					+ ")+ permitido");
		}
				
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		this.value = new Integer(value.asText());
	}

	@Override
	public JsonNode valueToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode nodo = mapper.createObjectNode();
		nodo.put("Integer", value);
		return (value==null)?null:nodo.get("Integer");
	}

}
