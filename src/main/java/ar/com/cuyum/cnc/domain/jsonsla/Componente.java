package ar.com.cuyum.cnc.domain.jsonsla;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import ar.com.cuyum.cnc.exceptions.ExceptionComboRelayUrl;
import ar.com.cuyum.cnc.exceptions.ExceptionParserJson;
import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Componente implements Serializable {

	public transient static Logger log = Logger.getLogger(Componente.class);

	private static final long serialVersionUID = 6076612513788995386L;

	public static final String CONSTRAINT_DEPENDS = "depends";
	public static final String CONSTRAINT_CUIT = "cuit";
	public static final String CONSTRAINT_PERIODICIDAD = "periodicidad";
	public static final String CONSTRAINT_AGRUPADOR = "agrupador";
	public static final String CONSTRAINT_URL = "url";
	public static final String CONSTRAINT_HORA_DELTA = "hora_delta";
	public static final String CONSTRAINT_MASK = "mask";
	public static final String CONSTRAINT_PORCENTUAL = "porcentual";
	public static final String CONSTRAINT_TOTALIZADOR = "totalizador";
	public static final String CONSTRAINT_DIVISOR = "divisor";	
	
	//en jsonschema minimu es incluyente lo mismo que <=
	public static final String CONSTRAINT_MINIMO = "minimum";
	
	//en jsonschema maximum es incluyente lo mismo que >=
	public static final String CONSTRAINT_MAXIMO = "maximum";
	
	//Hace excluyente el valor mínimo
	public static final String CONSTRAINT_EXCLUSIVE_MIN = "exclusiveMinimum";

	//Hace excluyente el valor máximo
	public static final String CONSTRAINT_EXCLUSIVE_MAX = "exclusiveMaximum";
	 
		
	public static String STRING = "string";
	public static String COMBO = "combo";
	public static String INTEGER = "integer";
	public static String DECIMAL = "decimal";
	public static String ITEM = "item";
	public static String TIME = "time";

	public static List<String> COMBO_CONSTRAINTS = Arrays.asList(
			Componente.CONSTRAINT_URL, Componente.CONSTRAINT_DEPENDS,
			Componente.CONSTRAINT_CUIT, Componente.CONSTRAINT_PERIODICIDAD,
			Componente.CONSTRAINT_AGRUPADOR);

	public static List<String> STRING_CONSTRAINTS = Arrays.asList(
			Componente.CONSTRAINT_URL,Componente.CONSTRAINT_HORA_DELTA);
	
	public static List<String> INTEGER_CONSTRAINTS = 
			Arrays.asList(".&gt;", ".&lt;", ".>", ".<",
			Componente.CONSTRAINT_MASK,
			Componente.CONSTRAINT_MINIMO,
			Componente.CONSTRAINT_MAXIMO,
			Componente.CONSTRAINT_EXCLUSIVE_MIN,
			Componente.CONSTRAINT_EXCLUSIVE_MAX,
			Componente.CONSTRAINT_TOTALIZADOR);
	
	public static List<String> DECIMAL_CONSTRAINTS = 
			Arrays.asList(".&gt;", ".&lt;", ".>", ".<",
			Componente.CONSTRAINT_MASK,
			Componente.CONSTRAINT_MINIMO,
			Componente.CONSTRAINT_MAXIMO,
			Componente.CONSTRAINT_EXCLUSIVE_MIN,
			Componente.CONSTRAINT_EXCLUSIVE_MAX,
			Componente.CONSTRAINT_PORCENTUAL,
			Componente.CONSTRAINT_DIVISOR);	
	
	public static List<String> TIME_CONSTRAINTS = 
			Arrays.asList(Componente.CONSTRAINT_AGRUPADOR);	

	protected Map<String, Componente> listComponets = new HashMap<String, Componente>();

	abstract public Boolean isDataValid() throws ExceptionValidation;

	abstract public void setValueFromJson(JsonNode value)
			throws ExceptionValidation;

	public static <T> Class<T> getTypeClass(String type) {
		if (STRING.equals(type))
			return (Class<T>) Texto.class;
		if (COMBO.equals(type))
			return (Class<T>) Combo.class;
		if (INTEGER.equals(type))
			return (Class<T>) Entero.class;
		if (DECIMAL.equals(type))
			return (Class<T>) Decimal.class;
		if (ITEM.equals(type))
			return (Class<T>) RepeatItem.class;
		if (TIME.equals(type))
			return (Class<T>) Time.class;
		return null; // explota si se incorpora un nuevo tipo
	}

	abstract public JsonNode valueToJson();

	protected void setAllComponet(Map<String, Componente> listComponets) {
		this.listComponets = listComponets;
	};

	// propiedades comunes a todos los componentes

	protected String ref;

	protected String id;

	protected String title;

	protected String appearance;

	protected String hint;

	protected String label;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppearance() {
		return appearance;
	}

	public void setAppearance(String appearance) {
		this.appearance = appearance;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRef() {
		return ref;
	}

	@JsonProperty("$ref")
	public void setRef(String ref) {
		this.ref = ref;
	}

	public boolean equals(Componente o) {
		if (!(o instanceof Componente))
			return false;
		return (ref == (((Componente) o).ref) && id == (((Componente) o).id)
				&& title == (((Componente) o).title)
				&& appearance == (((Componente) o).appearance)
				&& hint == (((Componente) o).hint) && label == (((Componente) o).label));
	}

	public String toString() {
		return "type = " + getType() + " ref =" + ref
				+ ((id != null) ? " id =" + id : "")
				+ ((title != null) ? " title =" + title : "")
				+ ((appearance != null) ? " appearance =" + appearance : "")
				+ ((hint != null) ? " hint =" + hint : "")
				+ ((label != null) ? " label =" + label : "");
	}

	public String getType() {
		return ref.split("#/definitions/")[1];
	}

	protected Boolean isOkRelevant(Map<String, List<String>> relevantMap)
			throws ExceptionValidation {
		Iterator<Entry<String, List<String>>> rel = relevantMap.entrySet()
				.iterator();
		while (rel.hasNext()) {
			Map.Entry<String, List<String>> e = (Map.Entry<String, List<String>>) rel
					.next();
			String msg = "valor de " + e.getKey() + "=" + e.getValue()
					+ " y el valor del objeto es null";
			Componente componete = this.listComponets.get(e.getKey());
			if (COMBO.equals(componete.getType())) {
				if (e.getValue().equals(((Combo) componete).getValue().getId())) {
					throw new ExceptionValidation(msg);
				}
			} else if (INTEGER.equals(componete.getType())) {
				Long val = ((Entero) componete).getValue();
				if (e.getValue().equals(val.toString())) {
					throw new ExceptionValidation(msg);
				}
			} else if (DECIMAL.equals(componete.getType())) {
				BigDecimal dec = ((Decimal) componete).getValue();
				if (e.getValue().equals(dec.toString())) {
					throw new ExceptionValidation(msg);
				}
			} else if (STRING.equals(componete.getType())) {
				String text = ((Texto) componete).getValue();
				if (e.getValue().equals(text)) {
					throw new ExceptionValidation(msg);
				}
			} else {
				throw new ExceptionValidation(
						"Tipo no considerado para constraint relevant");
			}

		}
		return true;
	}

	/**
	 * 
	 * @ltroconis
	 * 
	 * @param node
	 * @param type
	 * @return
	 * @throws ExceptionParserJson
	 */
	public static ObjectNode setConstraintsFromXML(Node node, String type)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode constraintResult = mapper.createObjectNode();
		Node nodeConstraint = node.getAttributes().getNamedItem("constraint");
		if (nodeConstraint == null)
			return null;

		List<String> constraints = Arrays.asList(nodeConstraint.getNodeValue()
				.split(" and "));

		for (int i = 0, n = constraints.size(); i < n; i++) {
			String constraint = constraints.get(i);
			if (constraints.get(i).indexOf(" or ") != -1) {
				List<String> cc = Arrays.asList(constraints.get(i)
						.split(" or "));
				constraint = cc.get(0);
				constraints.set(i, constraint);
				cc.remove(0);
				if (cc.size() > 0)
					constraints.addAll(cc);
			}
			if (constraint.indexOf(".<=") != -1
					|| constraint.indexOf(".&lt;=") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".<=") + 3, constraint.length());
				// minimum es equivalente a <= , a menos que se especifique la
				// propiedad
				// exclusiveMinimum=true
				if (INTEGER.equals(type)) {
					constraintResult.put(CONSTRAINT_MAXIMO, new Long(number));
				} else if (DECIMAL.equals(type)) {
					BigDecimal bd = new BigDecimal(number);
					bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
					constraintResult.put(CONSTRAINT_MAXIMO, bd);
				}
			} else if (constraint.indexOf(".>=") != -1
					|| constraint.indexOf(".&gt;=") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".>=") + 3, constraint.length());
				// maximum es equivalente a >= a menos que se especifique la
				// propiedad
				// exclusiveMaximum=true
				if (INTEGER.equals(type)) {
					constraintResult.put(CONSTRAINT_MINIMO, new Long(number));
				} else if (DECIMAL.equals(type)) {
					BigDecimal bd = new BigDecimal(number);
					bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
					constraintResult.put(CONSTRAINT_MINIMO, bd);
				}
			} else if (constraint.indexOf(".<") != -1
					|| constraint.indexOf(".&lt;") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".<") + 2, constraint.length());
				if (INTEGER.equals(type)) {
					constraintResult.put(CONSTRAINT_MINIMO, new Long(number));
				} else if (DECIMAL.equals(type)) {
					BigDecimal bd = new BigDecimal(number);
					constraintResult.put(CONSTRAINT_MINIMO, bd);
				}
				constraintResult.put(CONSTRAINT_EXCLUSIVE_MIN, true);
			} else if (constraint.indexOf(".>") != -1
					|| constraint.indexOf(".&gt;") != -1) {
				String number = constraint.substring(
						constraint.indexOf(".>") + 2, constraint.length());
				if (INTEGER.equals(type)) {
					constraintResult.put(CONSTRAINT_MAXIMO, new Long(number));
				} else if (type.equals("decimal")) {
					constraintResult.put(CONSTRAINT_MAXIMO, new Double(number));
				}
				constraintResult.put(CONSTRAINT_EXCLUSIVE_MAX, true);
			} else if (constraint.indexOf("depends=") != -1) {
				String[] depends = constraint.split("=");
				constraintResult
						.put(depends[0],
								depends[1].split("/")[depends[1].split("/").length - 1]);
			} else if (constraint.indexOf(CONSTRAINT_URL+"=") != -1) {
				String[] url = constraint.split("=");
				constraintResult.put(url[0], url[1]);
			} else if (constraint.indexOf(CONSTRAINT_CUIT) != -1) {
				constraintResult.put(CONSTRAINT_CUIT, true);
			} else if (constraint.indexOf(CONSTRAINT_MASK+"=") != -1) {
				String[] mask = constraint.split("=");
				constraintResult.put(mask[0], mask[1]);
			} else if (constraint.indexOf(CONSTRAINT_PERIODICIDAD) != -1) {
				String[] periodicidad = constraint.split("=");
				constraintResult.put(periodicidad[0], periodicidad[1]);
			} else if (constraint.indexOf(CONSTRAINT_AGRUPADOR) != -1) {
				String[] agrupador = constraint.split("=");
				constraintResult.put(agrupador[0], agrupador[1]);
			} else if (constraint.indexOf(CONSTRAINT_PORCENTUAL) != -1) {
				String[] porcentual = constraint.split("=");
				String[] valores = porcentual[1].split(",");
				String dividendo = valores[0].split("/")[valores[0].split("/").length - 1];
				String divisor = valores[1].split("/")[valores[1].split("/").length - 1];
				constraintResult.put(porcentual[0], dividendo.trim() + ","
						+ divisor.trim() + "," + valores[2]);
			} else if (constraint.indexOf(CONSTRAINT_TOTALIZADOR) != -1) {
				constraintResult.put(CONSTRAINT_TOTALIZADOR, true);
			} else if (constraint.indexOf(CONSTRAINT_HORA_DELTA) != -1) {
				String[] porcentual = constraint.split("=");
				constraintResult
						.put(porcentual[0],
								porcentual[1].split("/")[porcentual[1]
										.split("/").length - 1].trim());
			} else if (constraint.indexOf(CONSTRAINT_DIVISOR) != -1) {
				String[] porcentual = constraint.split("=");
				constraintResult.put(porcentual[0], porcentual[1]);
			} else {
				throw new ExceptionParserJson(
						"Constraint propertie no reconocida: " + constraint);
			}
		}

		return constraintResult;
	}

	public static Boolean validCuit(String inputString) {
		if (inputString.length() == 11) {
			String Caracters_1_2 = inputString.substring(0, 2);
			if ("20".equals(Caracters_1_2) || "23".equals(Caracters_1_2)
					|| "24".equals(Caracters_1_2) || "27".equals(Caracters_1_2)
					|| "30".equals(Caracters_1_2) || "33".equals(Caracters_1_2)
					|| "34".equals(Caracters_1_2)) {
				int Count = inputString.charAt(0) * 5 + inputString.charAt(1)
						* 4 + inputString.charAt(2) * 3 + inputString.charAt(3)
						* 2 + inputString.charAt(4) * 7 + inputString.charAt(5)
						* 6 + inputString.charAt(6) * 5 + inputString.charAt(7)
						* 4 + inputString.charAt(8) * 3 + inputString.charAt(9)
						* 2 + inputString.charAt(10) * 1;
				double Division = Count / 11;
				if (Division == Math.floor(Division)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @author ltroconis
	 * 
	 * @param url
	 *            remota
	 * @param fkey
	 *            valor de dependencia
	 * @return
	 * @throws ExceptionComboRelayUrl
	 */
	protected List<Item> getValuesFromUrl(RelayService relayService,
			String fkey, String url) throws ExceptionComboRelayUrl {

		if (relayService == null)
			throw new ExceptionComboRelayUrl(
					"No se puede cargar el combo a partir de la URL, el servicio no está disponible");
		FormRenderProperties frp = relayService.getFormRenderProperties();
		if (frp == null)
			throw new ExceptionComboRelayUrl(
					"No se puede cargar el combo a partir de la URL, propiedades no disponibles");
		ObjectMapper mapper = new ObjectMapper();
		List<Item> items = new ArrayList<Item>();
		String msg_error = "No se pudo generar la respuesta json en relay service ya que "
				+ "el servicio remoto ha respondido en un objeto JSON inv&aacute;lid";
		try {
			String remoteHost = frp.getRemoteListHost();
			URL urlRemote = new URL(remoteHost + url);
			String remoteResponse = relayService.request(urlRemote, fkey);
			JsonNode remoteMsg = mapper.readTree(remoteResponse);
			ArrayNode result = (ArrayNode) remoteMsg.get("result");
			items = mapper.readValue(result.toString(),
					new TypeReference<List<Item>>() {
					});
		} catch (JsonParseException e) {
			log.error(msg_error, e);
		} catch (JsonMappingException e) {
			log.error(msg_error, e);
		} catch (JsonProcessingException e) {
			log.error(msg_error, e);
		} catch (IOException e) {
			log.error(msg_error, e);
		}

		return items;

	}

}