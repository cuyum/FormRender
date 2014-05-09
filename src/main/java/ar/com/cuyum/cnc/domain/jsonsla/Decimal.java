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
public class Decimal extends Componente implements Numero {

	private static final long serialVersionUID = 4270231775586991982L;

	private Map<String, List<String>> relevantMap = new HashMap<String, List<String>>();

	private List<String> relevant = new ArrayList<String>();

	private BigDecimal minimum;
	private BigDecimal maximum;
	private String porcentual;
	private String divisor;

	private BigDecimal value;

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
		if (!(o instanceof Decimal) || o == null)
			return false;
		Decimal other = ((Decimal) o);

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
	public Boolean isDataValid(String name) throws ExceptionValidation {		

		if (porcentual != null) {
			String[] operandos = porcentual.split(",");
			Numero oper1 = (Numero) this.listComponets.get(operandos[0]);
			Numero oper2 = (Numero) this.listComponets.get(operandos[1]);
			Numero oper3 = new Entero(Long.valueOf(operandos[2]));

			if (!oper1.lessThanOrEqual(oper2))
				throw new ExceptionValidation(
						"Primer operando debe ser menor o igual que el segundo operando");
			oper1 = oper1.multiply(oper3);
			oper1 = oper1.divide(oper2);
			this.value = ((Decimal)oper1).getValue();
		}
		
		if(divisor!=null){
			String[] operandos = divisor.split(",");
			Numero oper1 = (Numero) this.listComponets.get(operandos[0]);
			Numero oper2 = (Numero) this.listComponets.get(operandos[1]);

			if (!oper1.lessThanOrEqual(oper2))
				throw new ExceptionValidation(
						"Primer operando debe ser menor o igual que el segundo operando");
			oper1 = oper1.divide(oper2);
			this.value = ((Decimal)oper1).getValue();
		}
		
		// Si el valor es nulo pero hay relevant con los valores seteados
		if (value == null) {
			log.info("validando si hay datos para que el objeto sea relevante");
			if (relevant != null && value != null) {
				if (!super.isOkRelevant(relevantMap))
					return false;
			}
		}

		if (minimum != null && value != null && value.compareTo(minimum) < 0) {
			throw new ExceptionValidation("Decimal menor al minimo (" + minimum
					+ ")+ permitido");
		}
		if (maximum != null && value != null && value.compareTo(maximum) > 0) {

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
		nodo.put("Decimal", value);
		return (value == null) ? null : nodo.get("Decimal");
	}

	@Override
	public String getValueToString() {
		if (value != null)
			return value.toString();
		return null;
	}

	@Override
	public String getType() {
		return Componente.DECIMAL;
	}
	
	public String getPorcentual() {
		return porcentual;
	}

	public void setPorcentual(String porcentual) {
		this.porcentual = porcentual;
	}

	public Numero sum(Entero otro) {
		Decimal sum = new Decimal();
		sum.setValue(this.value);
		if (otro == null || otro.getValue() == null)
			return sum;
		if (value == null) {
			sum.setValue(BigDecimal.valueOf(otro.getValue()));
		} else {
			sum.value.add(BigDecimal.valueOf(otro.getValue()));
		}
		return sum;
	}

	public Numero sum(Decimal otro) {
		Decimal sum = new Decimal();
		sum.setValue(this.value);
		if (otro == null || otro.getValue() == null)
			return sum;
		if (value == null) {
			sum.setValue(otro.getValue());
		} else {
			sum.value.add(otro.getValue());
		}
		return sum;
	}

	public Numero sum(Numero otro) {
		if (Componente.INTEGER.equals(((Componente)otro).getType())) {
			return  sum((Entero) otro);
		} else {
			return sum((Decimal) otro);
		} 
	}



	public Boolean lessThanOrEqual(Decimal otro) {
		return (this.value.compareTo(otro.value) <= 0);
	}

	public Boolean lessThanOrEqual(Entero otro) {
		return (this.value.compareTo(BigDecimal.valueOf(otro.getValue())) <= 0);
	}

	public Boolean lessThanOrEqual(Numero otro) {
		if (Componente.INTEGER.equals(((Componente)otro).getType())) {
			return lessThanOrEqual((Entero) otro);
		} else {
			return lessThanOrEqual((Decimal) otro);
		} 
	}



	@Override
	public Numero multiply(Numero otro) {
		if (Componente.INTEGER.equals(((Componente)otro).getType())) {
			return multiply((Entero) otro);
		} else {
			return multiply((Decimal) otro);
		}
	}

	public Numero multiply(Entero otro) {
		Decimal mult = new Decimal();
		mult.setValue(this.value.multiply(BigDecimal.valueOf(otro.getValue())));
		return mult;
	}

	public Numero multiply(Decimal otro) {
		Decimal mult = new Decimal();
		mult.setValue(this.value.multiply(otro.value));
		return mult;
	}
	
	public Numero divide(Entero otro){
		Decimal div = new Decimal();
		div.setValue(this.value.divide(BigDecimal.valueOf(otro.getValue())));
		return div;
	}
	
	public Numero divide(Decimal otro){
		Decimal div = new Decimal();
		div.setValue(this.value.divide(otro.getValue()));
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

	public String getDivisor() {
		return divisor;
	}

	public void setDivisor(String divisor) {
		this.divisor = divisor;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}

	
}
