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
public class Entero extends Componente implements Numero {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();

	private Long value;

	private Long minimum;
	private Long maximum;
	
	Entero(){}
	
	Entero(Long value){
		this.value = value;
	}

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

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getMinimum() {
		return minimum;
	}

	public void setMinimum(Long minimum) {
		this.minimum = minimum;
	}

	public Long getMaximum() {
		return maximum;
	}

	public void setMaximum(Long maximum) {
		this.maximum = maximum;
	}

	@Override
	public Boolean isDataValid(String name) throws ExceptionValidation {

		//Si el valor es nulo pero hay relevant con los valores seteados
		if (value == null){
			log.info("validando si hay datos para que el objeto sea relevante");
			if (relevant!=null && value!=null){
				if(!super.isOkRelevant(relevantMap)) return false;
			}
		}
		
		if (minimum != null && value!=null && value.compareTo(minimum) < 0) {
			throw new ExceptionValidation("Entero menor al minimo (" + minimum
					+ ") permitido");
		}
		if (maximum != null && value!=null && value.compareTo(maximum) > 0) {

			throw new ExceptionValidation("Entero mayor al maximo (" + maximum
					+ ") permitido");
		}
				
		return true;
	}

	@Override
	public void setValueFromJson(JsonNode value) throws ExceptionValidation {
		this.value = new Long(value.asText());
	}

	@Override
	public JsonNode valueToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode nodo = mapper.createObjectNode();
		nodo.put("Integer", value);
		return (value==null)?null:nodo.get("Integer");
	}

	@Override
	public String getValueToString() {
		if (value!=null) return value.toString();
		return null;
	}
	
	@Override
	public String getType() {		
		return Componente.INTEGER;
	}
	
	public Numero sum(Entero otro){
		Entero sum = new Entero();
		sum.setValue(this.value);
		if (otro==null || otro.value==null) return sum; 
		if(value==null){
			sum.setValue(otro.value);
		}else{
			sum.setValue(value + otro.value);
		}
		return sum;
	}
	
	public Numero sum(Decimal otro){
		Decimal sum = new Decimal();
		sum.setValue(BigDecimal.valueOf(this.value));
		if (otro==null || otro.getValue()==null) return sum;
		if(value==null){
			sum.setValue(otro.getValue());
		}else{			
			sum.getValue().add(otro.getValue());
		}
		return sum;
	}
	
	public Numero sum(Numero otro) {		
		if(Componente.INTEGER.equals(((Componente)otro).getType())){
			return sum((Entero)otro);
		}else {
			return sum((Decimal)otro);
		}
	}


	public Boolean lessThanOrEqual(Entero otro){
		return this.getValue() <= otro.getValue(); 
	}
	
	public Boolean lessThanOrEqual(Decimal otro){
		BigDecimal actual = BigDecimal.valueOf(this.value);
		return (actual.compareTo(otro.getValue()) <=0 );
	}
	
	public Boolean lessThanOrEqual(Numero otro){
		if(Componente.INTEGER.equals(((Componente)otro).getType())){
			return lessThanOrEqual((Entero)otro);
		}else {
			return lessThanOrEqual((Decimal)otro);
		}
	}

	@Override
	public Numero multiply(Numero otro) {
		if(Componente.INTEGER.equals(((Componente)otro).getType())){
			return multiply((Entero)otro);
		}else {
			return multiply((Decimal)otro);
		}
	}
	
	public Numero multiply(Entero otro){
		Entero mult = new Entero();
		mult.setValue(this.value*otro.value);
		return mult;
	}
	
	public Numero multiply(Decimal otro){
		Decimal mult = new Decimal();
		mult.setValue(BigDecimal.valueOf(this.value).multiply(otro.getValue()));		
		return mult;
	}
	
	public Numero divide(Entero otro){
		Decimal div = new Decimal();
		BigDecimal oper1 = BigDecimal.valueOf(this.value);
		BigDecimal oper2 = BigDecimal.valueOf(otro.getValue());
		BigDecimal result = oper1.divide(oper2,2,BigDecimal.ROUND_FLOOR);
		div.setValue(result);
		return div;
	}
	
	public Numero divide(Decimal otro){
		Decimal div = new Decimal();
		BigDecimal oper1 = BigDecimal.valueOf(this.value);
		BigDecimal oper2 = otro.getValue();
		BigDecimal result = oper1.divide(oper2,2,BigDecimal.ROUND_FLOOR);
		div.setValue(result);
		return div;
	}

	@Override
	public Numero divide(Numero otro) {
		if(Componente.INTEGER.equals(((Componente)otro).getType())){
			return divide((Entero)otro);
		}else {
			return divide((Decimal)otro);
		}
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}

	
		
}
