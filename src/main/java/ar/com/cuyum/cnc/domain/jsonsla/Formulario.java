package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.core.JsonParseException;
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
	private ArrayNode sumarizadosJson;

	// Solo la parte del esquema que interesa los items
	public Formulario(String id, JsonNode schema, RelayService relayService) {
		this.id = id;
		this.schema = schema;
		this.relayService = relayService;
		init();
	}

	private void initGridFromSchema() {
		ObjectMapper mapper = new ObjectMapper();
		if (this.schema.has("grid")) {
			try {
				setGrid(mapper.readValue(this.schema.get("grid").toString(),
						Grid.class));
			} catch (JsonParseException e) {
				log.error("el schema grid tiene formato inavalido", e);
			} catch (JsonMappingException e) {
				log.error("el schema grid tiene formato inavalido", e);
			} catch (IOException e) {
				log.error("el schema grid tiene formato inavalido", e);
			}
		}
	}

	private void init() {
		initGridFromSchema();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode schemaItemsProperties = schema.get("properties");
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

				if (Componente.COMBO.equals(type)) {
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
		Row row = new Row(mapComponets/* ,relayService */);
		try {
			row.setDataByJson(item);
		} catch (ExceptionValidation e) {
			log.error("json invalido", e);
		}
		return row;
	}

	public void addDataFromJson(ArrayNode data) {
		for (int i = 0, nf = data.size(); i < nf; i++) {
			JsonNode item = data.get(i);
			this.data.add(createDataItemFromJson(item));
		}
	}

	private void createAutorizadosJson(
			Map<String, Map<String, Componente>> mapRowTotals) {
		ObjectMapper mapper = new ObjectMapper();

		sumarizadosJson = mapper.createArrayNode();

		Iterator<Entry<String, Map<String, Componente>>> iterMapRowTotal = mapRowTotals
				.entrySet().iterator();

		while (iterMapRowTotal.hasNext()) {
			Entry<String, Map<String, Componente>> mapRow = iterMapRowTotal
					.next();

			log.info(mapRow.getKey());

			Iterator<Entry<String, Componente>> iterMapRow = mapRow.getValue()
					.entrySet().iterator();

			ObjectNode record = mapper.createObjectNode();
			while (iterMapRow.hasNext()) {
				Entry<String, Componente> field = iterMapRow.next();
				log.info(field.getKey() + " = "
						+ field.getValue().getValueToString());
				record.put(field.getKey(), field.getValue().getValueToString());
			}

			sumarizadosJson.add(record);
		}

	}

	public Boolean processData() throws ExceptionValidation, IOException {
		Map<String, Map<String, Componente>> iterMapRowTotal = new HashMap<String, Map<String, Componente>>();

		for (int i = 0, n = data.size(); i < n; i++) {
			this.data.get(i).isDataValid();

			if (this.grid != null && grid.getSumarizada()) {
				this.data.get(i).sumarizar(grid.getTotalizadores());
			}

			if (this.grid != null && grid.getCalculados()) {
				List<String> agrupadores = grid.getAgrupadores();
				List<String> totalizadores = grid.getTotalizadores();
				String key = "", coma = "";
				Map<String, Componente> sumarizados = new HashMap<String, Componente>();
				for (int j = 0, an = agrupadores.size(); j < an; j++) {
					String value = this.data.get(i)
							.getFieldByName(agrupadores.get(j))
							.getValueToString();
					key = key + coma + value;
					coma = ",";
					Texto agrupador = new Texto();
					agrupador.setValue(value);
					sumarizados.put(agrupadores.get(j), agrupador);
				}
				log.info(key);

				for (int j = 0, at = totalizadores.size(); j < at; j++) {
					Componente total = sumarizados.get(totalizadores.get(j));
					Componente aSumar = this.data.get(i).getFieldByName(
							totalizadores.get(j));

					if (!Componente.INTEGER.equals(aSumar.getType())
							&& !Componente.DECIMAL.equals(aSumar.getType())) {
						throw new IOException(
								"Valores invalidos para totalizar");
					}

					if (total == null) {
						total = (Componente) aSumar.clone();
					} else {
						if (Componente.INTEGER.equals(total.getType())) {
							total = ((Entero) total).sum(aSumar);
						} else {
							total = ((Decimal) total).sum(aSumar);
						}
					}
					sumarizados.put(totalizadores.get(j), total);
				}

				iterMapRowTotal.put(key, sumarizados);

			}
			createAutorizadosJson(iterMapRowTotal);
		}
		
		return true;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public JsonNode valuesToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode data = mapper.createObjectNode();
		
		ArrayNode items = mapper.createArrayNode();

		for (int i = 0, nf = this.data.size(); i < nf; i++) {
			items.add(this.data.get(i).valuesToJson());
		}

		if (sumarizadosJson != null && grid.getCalculados()) {
			ArrayNode items2 =  mapper.createArrayNode();
			ObjectNode item = mapper.createObjectNode();
			item.put("registros",items);
			item.put("sumarizados",sumarizadosJson);
			items2.add(item);
			data.put("data",items2);
		}else{
    	   data.put("data",items);
		}
		
		return data;
	}

}