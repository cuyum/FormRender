package ar.com.cuyum.cnc.domain.jsonsla;

import java.math.BigDecimal;
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
public class Decimal extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();

	private BigDecimal minimum;
	private BigDecimal maximum;

	private BigDecimal value;

	public String toString() {

		return super.toString()
				+((value==null)?" value = null":" value = ("+value+")") 
				+((minimum==null)?"":" minimum="+ minimum)
				+ ((maximum==null)?"":" maximum="+ maximum)
				+ ((relevant.size() > 0) ? " relevant size=" + relevant.size()
						+ " relevant values: " + relevantMapToString() : "");
	}

	public boolean equals(Object o) {
		if (!(o instanceof Combo) || o == null)
			return false;

		return super.equals(((Componente) o));
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

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// si el campo es obligatorio el valor no puede ser nulo
		// esto hay que pensarlo mejor
		if (value == null)
			return null;

		if (minimum != null && value.compareTo(minimum) < 0) {
			throw new ExceptionValidation("Decimal menor al minimo (" + minimum
					+ ")+ permitido");
		}
		if (maximum != null && value.compareTo(maximum) > 0) {

			throw new ExceptionValidation("Decimal mayor al maximo (" + maximum
					+ ")+ permitido");
		}
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		this.value = new BigDecimal(value.asText());
	}
	
	@Override
	public JsonNode valueToJson() {
	   ObjectMapper mapper = new ObjectMapper();
	   ObjectNode nodo = mapper.createObjectNode();
	   nodo.put("Decimal",value);
	   return nodo.get("Decimal");
	}
}
