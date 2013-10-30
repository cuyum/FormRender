package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 *
 * los valores id y txt porque cuando mapeo el objeto json de las listas remotas esos son los campos
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable{

	private static final long serialVersionUID = 3222976116448464628L;

	private String id;
	private String text;

	public Item() {
	}

	public Item(JsonNode item) {
		this.id = (item.has("value")) ? item.get("value").asText() : item
				.get("clave").asText();
		this.text = (item.has("label")) ? item.get("label").asText() : item
				.get("valor").asText();
	}
	
	public String toString(){
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode valueAsString = mapper.createObjectNode();
		valueAsString.put("clave",id);
		valueAsString.put("valor",text);
		return valueAsString.asText();
	}
	
	public JsonNode toJson(){
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode valueAsString = mapper.createObjectNode();
		valueAsString.put("clave",id);
		valueAsString.put("valor",text);
		return valueAsString;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof Item)) return false;		
		return (id ==(((Item) o).id) && text==(((Item) o).text));
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getValue() {
		return id;
	}
	
	public void setValue(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return text;
	}	
	
	public void setLabel(String text) {
		this.text = text;
	}

}
