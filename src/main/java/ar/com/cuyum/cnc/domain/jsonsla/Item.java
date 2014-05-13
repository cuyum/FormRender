package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.Serializable;

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
/*@JsonIgnoreProperties(ignoreUnknown = true)*/
public class Item implements Serializable{

	private static final long serialVersionUID = 3222976116448464628L;

	private String id;
	private String text;
	private String idPadre;

   public static Item getItemFromJson(JsonNode jsonItem){
	   Item item = new Item();
	   item.setId((jsonItem.has("value")) ? jsonItem.get("value").asText() : jsonItem
				.get("clave").asText());
	   item.setText((jsonItem.has("label")) ? jsonItem.get("label").asText() : jsonItem
				.get("valor").asText());
	   return item;
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
		valueAsString.put("label",text);
		valueAsString.put("value",id);
		return valueAsString;
	}
	
	public boolean equals(Object o) {		
		if(!(o instanceof Item)) return false;
		Item other = ((Item)o);		
		if (id == null) {
	            if (other.id != null) return false;
	    } else if (!id.equals(other.id)) return false;
	    if (text == null) {
	            if (other.text != null) return false;
	    } else if (!text.equals(other.text)) return false;
		
	    return true;
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

	public String getIdPadre() {
		return idPadre;
	}

	public void setIdPadre(String idPadre) {
		this.idPadre = idPadre;
	}

}
