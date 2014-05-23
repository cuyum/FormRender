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
	private Clave_Primaria clave_primaria;
	private Map<String, Componente> mapComponets = new HashMap<String, Componente>();
	private List<Row> data = new ArrayList<Row>();
	private RelayService relayService;
	private ArrayNode sumarizadosJson;
	private String recordId;

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
				log.error("el schema grid tiene formato invalido", e);
			} catch (JsonMappingException e) {
				log.error("el schema grid tiene formato invalido", e);
			} catch (IOException e) {
				log.error("el schema grid tiene formato invalido", e);
			}
		}
	}

	private void initClave_PrimariaFromSchema() {
		ObjectMapper mapper = new ObjectMapper();
		if (this.schema.has("claves_primarias")) {

			setClavePrimaria(this.schema.get("claves_primarias").toString());

		}
	}

	private void initComponent(JsonNode schemaItemsProperties) {
		ObjectMapper mapper = new ObjectMapper();
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
				} else if (Componente.STRING.equals(type)) {
					((Texto) component).setRelayService(relayService);
				}

				mapComponets.put(name, component);
			} catch (JsonParseException e) {
				log.error("el schema propertie tiene formato invalido", e);
			} catch (JsonMappingException e) {
				log.error("el schema propertie tiene formato invalido", e);
			} catch (IOException e) {
				log.error("el schema propertie tiene formato invalido", e);
			}

			log.info("objeto mappeado name:" + name + " " + component);
		}
	}

	private void init() {
		initGridFromSchema();
		initClave_PrimariaFromSchema();
		JsonNode schemaItemsProperties = schema.get("properties");
		initComponent(schemaItemsProperties);
		JsonNode schemaOtherFields = schema.get("otherFields");
		if (schemaOtherFields != null)
			initComponent(schemaOtherFields);
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

	public Boolean validationPrimaryKey() {
		List<String> lstPK = this.clave_primaria.getClaves_primarias();
		String value, value2;
		int m = lstPK.size(), n = data.size(), cont = 0;

		if (m > 0) {

			for (int i = 0; i < n - 1; i++) {
				
				for (int k = 0; k < n; k++) {		
					cont = 0;
					if (k != i) {
						for (int j = 0; j < m; j++) {
							value = data.get(i).getFieldByName(lstPK.get(j))
									.getValueToString();
							value2 = data.get(k).getFieldByName(lstPK.get(j))
									.getValueToString();
							if (value.equals(value2)) {
								cont++;
							}							
						}
						
					}
					if(cont>=m)
						return true;
				}
			}

		} else
			return false;

		if (cont == m)
			return true;
		else
			return false;
	}

	public Boolean processData() throws ExceptionValidation {
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
					Numero total = (Numero) sumarizados.get(totalizadores
							.get(j));
					Numero aSumar = (Numero) this.data.get(i).getFieldByName(
							totalizadores.get(j));

					if (total == null) {
						total = ((Numero) (((Componente) aSumar).clone()));
					} else {
						total = total.sum(aSumar);
					}
					sumarizados.put(totalizadores.get(j), (Componente) total);
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

	public Clave_Primaria getClavePrimaria() {
		return clave_primaria;
	}

	public void setClavePrimaria(String clave_primaria) {
		Clave_Primaria pk = new Clave_Primaria();
		if(clave_primaria.equals(""))
			pk.setClaves_primarias(null);
		else
			pk.setClaves_primarias(clave_primaria);
		
		this.clave_primaria = pk;
	}

	public JsonNode valuesToJson() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode data = mapper.createObjectNode();

		ArrayNode items = mapper.createArrayNode();

		for (int i = 0, nf = this.data.size(); i < nf; i++) {
			items.add(this.data.get(i).valuesToJson());
		}

		if (sumarizadosJson != null && grid != null && grid.getCalculados()) {
			ArrayNode items2 = mapper.createArrayNode();
			ObjectNode item = mapper.createObjectNode();
			item.put("registros", items);
			item.put("sumarizados", sumarizadosJson);
			items2.add(item);
			data.put("data", items2);
		} else {
			data.put("data", items);
		}

		if (recordId != null && !recordId.isEmpty()) {
			data.put("recordId", recordId);
		}

		return data;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

}