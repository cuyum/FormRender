package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.JsonUtils;

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
public class Formulario implements Serializable {
	public transient static Logger log = Logger.getLogger(Row.class);

	private static final long serialVersionUID = 1L;

	private String id;
	private JsonNode schema;
	private Grid grid;
	private Map<String, Componente> mapComponets = new HashMap<String, Componente>();
	private List<Row> data = new ArrayList<Row>();
	private RelayService relayService;

	// Solo la parte del esquema que interesa los items
	public Formulario(String id, JsonNode schema, RelayService relayService) {
		this.id = id;
		this.schema = schema;
		this.relayService = relayService;
		init();		
	}

	private void init() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode schemaItemsProperties = schema.get("properties");

		if (schema.has("grid")) {
			try {
				setGrid(mapper.readValue(schema.get("grid").toString(),
						Grid.class));
			} catch (JsonParseException e) {
				log.error("el schema grid tiene formato inavalido", e);
			} catch (JsonMappingException e) {
				log.error("el schema grid tiene formato inavalido", e);
			} catch (IOException e) {
				log.error("el schema grid tiene formato inavalido", e);
			}
		}

		Iterator<String> names = schemaItemsProperties.fieldNames();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
		while (names.hasNext()) {
			String name = names.next();
			JsonNode nodePropertie = schemaItemsProperties.get(name);
			String type = nodePropertie.get("$ref").asText()
					.split("#/definitions/")[1];

			Componente component = null;
			try {
				component = (Componente) mapper
						.readValue(nodePropertie.toString(),
								Componente.getTypeClass(type));
				if(Componente.COMBO.equals(component.getType())){
					((Combo) component).setRelayService(relayService);
				}
				mapComponets.put(name, component);
			} catch (JsonParseException e) {
				log.error("el schema propertie tiene formato inavalido", e);
			} catch (JsonMappingException e) {
				log.error("el schema propertie tiene formato inavalido", e);
			} catch (IOException e) {
				log.error("el schema propertie tiene formato inavalido", e);
			}

			log.info("objeto mappeado " + component);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private Row createDataItemFromJson(JsonNode item) {
		Row row = new Row(mapComponets/*,relayService*/);
		try {
			row.setDataByJson(item);
		} catch (ExceptionValidation e) {
			log.error("json inavalido", e);
		}
		return row;
	}

	public void addDataFromJson(ArrayNode data) {
		for (int i = 0, nf = data.size(); i < nf; i++) {
			JsonNode item = data.get(i);
			this.data.add(createDataItemFromJson(item));
		}
	}

	public Boolean processData() throws ExceptionValidation {
		for (int i = 0, nf = data.size(); i < nf; i++) {
			this.data.get(i).isDataValid();
			//this.data.get(i).totalizar();
		}
		return true;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	public JsonNode valuesToJson(){
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode dataReturn = mapper.createArrayNode(); 
		for (int i = 0, nf = data.size(); i < nf; i++) {
			dataReturn.add(this.data.get(i).valuesToJson());
		}
		return dataReturn;
	}

}