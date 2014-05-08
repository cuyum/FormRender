package ar.com.cuyum.cnc.domain.jsonsla;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;


/**
 * 
 * @author ltroconis
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class RepeatItem extends Componente {

	private static final long serialVersionUID = 4270231775586991982L;
	
	private String value;

	public String toString() {		
		
		return super.toString();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Combo) || o == null)
			return false;

		return super.equals(((Componente) o));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Boolean isDataValid() throws ExceptionValidation {
		//Esto hay que pensarlo mejor de momento no tengo como saber si item es un valor valido
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
		return Componente.ITEM;
	}

	@Override
	public Boolean isDataValid(String name) throws ExceptionValidation {
		// TODO Auto-generated method stub
		return null;
	}
}
