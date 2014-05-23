package ar.com.cuyum.cnc.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.fest.util.VisibleForTesting;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ar.com.cuyum.cnc.domain.jsonsla.Componente;
import ar.com.cuyum.cnc.exceptions.ExceptionParserJson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * 
 * @author ltroconis
 * 
 */

class DirComparator implements Comparator<File> {

	public int compare(File o1, File o2) {
		File a = (File) o1;
		File b = (File) o2;
		return a.getAbsolutePath().compareTo(b.getAbsolutePath());
	}
}

public class JsonShemaGenerator {
	private static Logger log = Logger.getLogger(JsonShemaGenerator.class);

	/**
	 * Crea el arreglo de elementos a los cuales un objeto es pertinente
	 * 
	 * @author ltroconis
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @return El arreglo de elementos pertinente
	 */
	private ArrayNode setRelevant(Node node) {
		ObjectMapper mapper = new ObjectMapper();

		ArrayNode relevantArray = mapper.createArrayNode();
		Node relevant = node.getAttributes().getNamedItem("relevant");
		if (relevant == null)
			return null;

		String[] strRelevant = relevant.getNodeValue().split(" or ");
		for (int i = 0, n = strRelevant.length; i < n; i++) {
			String item = strRelevant[i].split("/")[strRelevant[i].split("/").length - 1];
			relevantArray.add(item);
		}

		return relevantArray;
	}

	/**
	 * @author ltroconis
	 * 
	 * @param type
	 * @param constraints
	 * @return
	 * @throws ExceptionParserJson
	 */
	private JsonNode isValidConstraintToType(String type, ObjectNode constraints)
			throws ExceptionParserJson {
		List<String> validConstraints = new ArrayList<String>();

		if (Componente.COMBO.equals(type)) {
			validConstraints.addAll(Componente.COMBO_CONSTRAINTS);
		} else if (Componente.STRING.equals(type)) {
			validConstraints.addAll(Componente.STRING_CONSTRAINTS);
		} else if (Componente.INTEGER.equals(type)) {
			validConstraints.addAll(Componente.INTEGER_CONSTRAINTS);
		} else if (Componente.DECIMAL.equals(type)) {
			validConstraints.addAll(Componente.DECIMAL_CONSTRAINTS);
		} else if (Componente.TIME.equals(type)) {
			validConstraints.addAll(Componente.TIME_CONSTRAINTS);
		}  else {
			throw new ExceptionParserJson(" type no reconocido para parser ");
		}
		Iterator<String> fields = constraints.fieldNames();

		while (fields.hasNext()) {
			String field = fields.next();
			if (!validConstraints.contains(field)) {
				return JsonUtils.msg(false, "constraint " + field
						+ " no reconocido para el tipo " + type);
			}
		}

		return JsonUtils.msg(true, "es valido");
	}

	/**
	 * Crea el esquema de los selec1 a partir de xml node bind propertie
	 * type=select1.
	 * 
	 * @author ltroconis
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson
	 *             error de parser
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeSelec1(Node node, Element selectXml)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode select1 = mapper.createObjectNode();

		select1.put("$ref", "formulario.json#/definitions/" + Componente.COMBO);

		ObjectNode constraints = Componente.setConstraintsFromXML(node,
				Componente.COMBO);
		if (constraints != null) {
			JsonNode response = isValidConstraintToType(Componente.COMBO,
					constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());

			select1.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);

		if (relevant != null)
			select1.put("relevant", relevant);

		select1.put("title", selectXml.getElementsByTagName("label").item(0)
				.getTextContent());

		Node hint = selectXml.getElementsByTagName("hint").item(0);
		if (hint != null)
			select1.put("hint", hint.getTextContent());

		ArrayNode values = mapper.createArrayNode();

		NodeList items = selectXml.getElementsByTagName("item");

		for (int i = 0, n = items.getLength(); i < n; i++) {
			Element item = (Element) selectXml.getElementsByTagName("item")
					.item(i);

			ObjectNode itemSelect = mapper.createObjectNode();
			itemSelect.put("label", item.getElementsByTagName("label").item(0)
					.getTextContent());
			itemSelect.put("value", item.getElementsByTagName("value").item(0)
					.getTextContent());

			values.add(itemSelect);
		}

		select1.put("values", values);

		return select1;
	}

	/**
	 * Crea el esquema de los string a partir de xml node bind propertie
	 * type=string.
	 * 
	 * @author ltroconis
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson
	 *             error de parser
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeString(Node node, Element inputXml)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.STRING);

		ObjectNode constraints = Componente.setConstraintsFromXML(node,
				Componente.STRING);
		if (constraints != null) {
			JsonNode response = isValidConstraintToType(Componente.STRING,
					constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

		NodeList label = inputXml.getElementsByTagName("label");
		if (label != null && label.getLength() > 0)
			input.put("title", label.item(0).getTextContent());

		Node hint = inputXml.getElementsByTagName("hint").item(0);
		if (hint != null)
			input.put("hint", hint.getTextContent());

		return input;
	}
	
	
	@VisibleForTesting
	private ObjectNode createJsonNodeIngresados()
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();
		input.put("$ref", "formulario.json#/definitions/" + Componente.INTEGER);
		return input;
	}

	@VisibleForTesting
	private ObjectNode createJsonNodeTime(Node node, Element inputXml)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.TIME);

		ObjectNode constraints = Componente.setConstraintsFromXML(node,
				Componente.TIME);
		if (constraints != null) {
			JsonNode response = isValidConstraintToType(Componente.TIME,
					constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

		input.put("title", inputXml.getElementsByTagName("label").item(0)
				.getTextContent());

		Node hint = inputXml.getElementsByTagName("hint").item(0);
		if (hint != null)
			input.put("hint", hint.getTextContent());

		return input;
	}

	/**
	 * Crea el esquema de los int a partir de xml node bind propertie type=int.
	 * 
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @param inputXml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson
	 *             error de parser
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeInt(Node node, Element inputXml)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.INTEGER);

		ObjectNode constraints = Componente.setConstraintsFromXML(node,
				Componente.INTEGER);
		if (constraints != null) {
			JsonNode response = isValidConstraintToType(Componente.INTEGER,
					constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

		input.put("title", inputXml.getElementsByTagName("label").item(0)
				.getTextContent());

		Node hint = inputXml.getElementsByTagName("hint").item(0);
		if (hint != null)
			input.put("hint", hint.getTextContent());

		return input;
	}

	@VisibleForTesting
	private ObjectNode createJsonNodeInstance() throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.INTEGER);

		return input;
	}

	@VisibleForTesting
	private ObjectNode createJsonNodeFirmaDigital() throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.STRING);

		return input;
	}

	/**
	 * Crea el esquema de los decimal a partir de xml node bind propertie
	 * type=decimal.
	 * 
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson
	 *             error de parser
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeDecimal(Node node, Element inputXml)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/" + Componente.DECIMAL);

		ObjectNode constraints = Componente.setConstraintsFromXML(node,
				Componente.DECIMAL);
		if (constraints != null) {
			JsonNode response = isValidConstraintToType(Componente.DECIMAL,
					constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

		input.put("title", inputXml.getElementsByTagName("label").item(0)
				.getTextContent());

		Node hint = inputXml.getElementsByTagName("hint").item(0);
		if (hint != null)
			input.put("hint", hint.getTextContent());

		return input;
	}

	/**
	 * Busca los bind del archivo xml
	 * 
	 * @author ltroconis
	 * @param doc
	 *            Document del archivo xml
	 * 
	 * @return la lista de nodos binsds del xml
	 */
	@VisibleForTesting
	public static NodeList getBindsFromXml(Document doc) {
		return doc.getElementsByTagName("bind");
	}

	/**
	 * Busca los bind del archivo xml
	 * 
	 * @author ltroconis
	 * @param doc
	 *            Document del archivo xml
	 * 
	 * @return la lista de nodos binsds del xml
	 */
	@VisibleForTesting
	public static String getActionFromXml(Document doc) {
		return doc.getElementsByTagName("submission").item(0).getAttributes()
				.getNamedItem("action").getNodeValue();
	}

	/**
	 * Busca el id del formulario en el xml
	 * 
	 * @author ltroconis
	 * @param doc
	 *            Document del archivo xml
	 * 
	 * @return el id del formulario
	 */
	@VisibleForTesting
	private String getIdFormularioFromXml(Document doc) {
		return doc.getElementsByTagName("instance").item(0).getChildNodes()
				.item(1).getAttributes().getNamedItem("id").getNodeValue();
	}

	/**
	 * @author ltroconis
	 * 
	 * @param doc
	 * @return
	 */
	private Map<String, Element> setMapElement(Document doc, String type) {
		NodeList listNode = doc.getElementsByTagName(type);
		Map<String, Element> map = new HashMap<String, Element>();

		for (int i = 0, n = listNode.getLength(); i < n; i++) {
			Node nodoSelect = listNode.item(i);
			if (nodoSelect.getNodeType() == Node.ELEMENT_NODE) {
				Element dato = (Element) nodoSelect;
				String ref = dato.getAttribute("ref");
				map.put(ref, dato);
			}
		}

		return map;
	}

	private ObjectNode createJsonNodeItem(Document doc)
			throws ExceptionParserJson {

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode item = null;

		NodeList listSelectNode = doc.getElementsByTagName("label");
		for (int i = 0, n = listSelectNode.getLength(); i < n; i++) {
			String value = listSelectNode.item(i).getTextContent();
			if ("{title}".equals(value)) {
				item = mapper.createObjectNode();
				item.put("$ref", "formulario.json#/definitions/"
						+ Componente.ITEM);
				break;
			}
		}

		return item;
	}

	private ObjectNode createJsonNodeGrid(Document doc) {
		ObjectMapper mapper = new ObjectMapper();

		NodeList listSelectNode = doc.getElementsByTagName("repeat");
		for (int i = 0, n = listSelectNode.getLength(); i < n; i++) {
			Node appearance = listSelectNode.item(i).getAttributes()
					.getNamedItem("appearance");
			if (appearance != null
					&& appearance.getNodeValue().contains("grilla")) {
				ObjectNode grid = mapper.createObjectNode();
				if (grid != null) {
					if (appearance.getNodeValue().contains("sumarizada")) {
						grid.put("sumarizada", true);
					} else {
						grid.put("sumarizada", false);
					}
					if (appearance.getNodeValue().contains("calculados")){
						grid.put("calculados", true);
					}else{
						grid.put("calculados", false);
					}
					if(appearance.getNodeValue().contains("ingresados")){
						grid.put("ingresados", true);
						grid.put("calculados", true);
					}else{
						grid.put("ingresados", false);
					}
					
					return grid;
				}
			}

		}

		return null;
	}

	/**
	 * Busca el id del formulario en el xml
	 * 
	 * @author ltroconis
	 * @param doc
	 *            Document del archivo xml
	 * 
	 * @return Un nodo donde se encuentran las propiedades de los elementos del
	 *         formulario y aquellos campos que son obligatorios (los que no
	 *         tienen el valor relevant)
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ExceptionParserJson
	 * @throws Exception
	 */
	@VisibleForTesting
	private ObjectNode xmlToJsonSchemaProperties(File fXmlFile)
			throws ParserConfigurationException, SAXException, IOException,
			ExceptionParserJson {

		ObjectMapper mapper = new ObjectMapper();

		ObjectNode returnNode = mapper.createObjectNode();

		ObjectNode properties = mapper.createObjectNode();

		ObjectNode otherFields = mapper.createObjectNode();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbFactory.newDocumentBuilder();
		Document doc = db.parse(fXmlFile);

		NodeList binds = getBindsFromXml(doc);

		String action = getActionFromXml(doc);

		Map<String, Element> listSelect = setMapElement(doc, "select1");
		Map<String, Element> listInput = setMapElement(doc, "input");

		ObjectNode item = createJsonNodeItem(doc);
		if (item != null)
			properties.put("item", item);

		String idformulario = getIdFormularioFromXml(doc);

		ObjectNode grid = createJsonNodeGrid(doc);
		
		log.info("Procesando formulario:" + idformulario);		
		ArrayNode required = mapper.createArrayNode();
		ArrayNode agrupadores = mapper.createArrayNode();
		ArrayNode totalizadores = mapper.createArrayNode();
		ArrayNode claves_primarias = mapper.createArrayNode();
		List<String> auxiliarClavesPrimarias = new ArrayList<String>();

		// Lista de nombres de los que ciertos elementos dependen
		List<String> depends = new ArrayList<String>();

		properties.set("instance", createJsonNodeInstance());
		for (int i = 0, n = binds.getLength(); i < n; i++) {
			Node node = binds.item(i);
			Node readonly = node.getAttributes().getNamedItem("readonly");
			Node calculate = node.getAttributes().getNamedItem("calculate");
			if (calculate != null)
				continue;

			Node nodeset = node.getAttributes().getNamedItem("nodeset");
			Node type = node.getAttributes().getNamedItem("type");
			Node pkey = null;
			pkey = node.getAttributes().getNamedItem("clave_primaria");

			if (type == null) {
				log.warn(node.getNodeName()
						+ "-"
						+ nodeset.getNodeValue()
						+ " : no tiene tipo, se obvia, este dato no es relevante para la data del formulario");
				// busci el siguiente bind
				continue;
			}

			Node relevant = node.getAttributes().getNamedItem("relevant");

			int leng = nodeset.getNodeValue().split("/").length;
			String name = nodeset.getNodeValue().split("/")[leng - 1];
			
			
			Node req = node.getAttributes().getNamedItem("required");

			if (relevant == null && req != null
					&& req.getNodeValue().equals("true()"))
				required.add(name);

			ObjectNode object = null;
			try {

				if (type != null && "select1".equals(type.getNodeValue())) {
					Element select = listSelect.get(nodeset.getNodeValue());
					object = createJsonNodeSelec1(node, select);

				} else if (type != null && "int".equals(type.getNodeValue())) {
					Element input = listInput.get(nodeset.getNodeValue());
					object = createJsonNodeInt(node, input);
				} else if (type != null
						&& "decimal".equals(type.getNodeValue())) {
					Element input = listInput.get(nodeset.getNodeValue());
					object = createJsonNodeDecimal(node, input);
				} else if (type == null || "string".equals(type.getNodeValue())) {
					Element input = listInput.get(nodeset.getNodeValue());
					object = createJsonNodeString(node, input);
				} else if (type == null || "time".equals(type.getNodeValue())) {
					Element input = listInput.get(nodeset.getNodeValue());
					object = createJsonNodeTime(node, input);
				}

			} catch (ExceptionParserJson e) {
				throw new ExceptionParserJson(fXmlFile.getAbsoluteFile() + ":"
						+ e.getMessage());
			}

			if (object.has("depends")) {
				if (name.equals(object.get("depends").asText())) {
					throw new ExceptionParserJson(fXmlFile.getAbsoluteFile()
							+ ": el elemento " + name + " depende de si mismo");
				}
				depends.add(object.get("depends").asText());
			}

			if (object != null) {
				
				auxiliarClavesPrimarias.add(name);
				
				if (readonly == null) {
					properties.put(name, object);
				} else {
					otherFields.put(name, object);
				}

				if (object.has("agrupador")) {
					agrupadores.add(name);
				}
				
				if (object.has("clave_primaria")) {
					claves_primarias.add(name);
				}

				if (object.has("totalizador")) {
					totalizadores.add(name);
					if(grid.has("ingresados") && grid.get("ingresados").asBoolean()){
						properties.put(name+"_ingresado", createJsonNodeIngresados());
						required.add(name+"_ingresado");
					}
				}
			}
		}
		properties.put("firma_digital", createJsonNodeFirmaDigital());

		for (int i = 0, n = depends.size(); i < n; i++) {
			if (!properties.has(depends.get(i)))
				throw new ExceptionParserJson(fXmlFile.getAbsoluteFile()
						+ ": posee un elemento que depende de "
						+ depends.get(i) + " pero este elemento no existe");
		}

		returnNode.put("properties", properties);
		
		returnNode.put("required", required);
//		if(claves_primarias.size()==0){
//			for (int i = 0; i < required.size(); i++) {
//				claves_primarias.add(required.get(i));
//			}
//		}
		returnNode.put("claves_primarias", claves_primarias);
		returnNode.put("action", action);
		if(otherFields.size()>0) 
			returnNode.put("otherFields", otherFields);	
		
		if (grid != null) {
			if (agrupadores.size() > 0)
				grid.put("agrupadores", agrupadores);
			if (totalizadores.size() > 0)
				grid.put("totalizadores", totalizadores);
			returnNode.put("grid",grid);
		}

		return returnNode;
	}

	/**
	 * Crea el archivo json a partir de un json.
	 * 
	 * @author ltroconis
	 * 
	 * @param fileName
	 *            el nombre incluyendo la ruta donde se va colocar el archivo
	 * @param jsonSchema
	 *            el objeto json que se va a guardar en el archivo
	 * @throws IOException
	 *             error construyendo el archivo
	 */
	@VisibleForTesting
	private void createJsonShemaFile(String fileName, ObjectNode jsonSchema)
			throws IOException {
		File archivo = new File(fileName);
		FileWriter escribir = new FileWriter(archivo, false);
		escribir.write(jsonSchema.toString());
		escribir.close();
	}

	/**
	 * Crea el objeto jsonschema.
	 * 
	 * @author ltroconis
	 * @param id
	 *            del formulario
	 * @param required
	 *            los campos que son requeridos en el formulario
	 * @param properties
	 *            los campos que corresponden al formulario, extraidos de los
	 *            binds del xml
	 * @return ObjectNode el objeto jsonschema final
	 */
	@VisibleForTesting
	private ObjectNode createJsonShemaObject(String id, ObjectNode description) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode dataItems = mapper.createObjectNode();
		ObjectNode data = mapper.createObjectNode();
		ObjectNode formularios = mapper.createObjectNode();
		ObjectNode formulariosItems = mapper.createObjectNode();
		ObjectNode formulariosItemsProperties = mapper.createObjectNode();
		ObjectNode formulario = mapper.createObjectNode();
		ObjectNode formularioProperties = mapper.createObjectNode();
		ObjectNode idFormulario = mapper.createObjectNode();
		ObjectNode jsonSchemaProperties = mapper.createObjectNode();
		ObjectNode jsonSchema = mapper.createObjectNode();
		ArrayNode requiredForm = mapper.createArrayNode();

		ArrayNode required = (ArrayNode) description.get("required");
		ArrayNode claves_primarias = (ArrayNode) description.get("claves_primarias");
		ObjectNode grid = (ObjectNode) description.get("grid");
		ObjectNode properties = (ObjectNode) description.get("properties");
		TextNode action = (TextNode) description.get("action");

		dataItems.put("type", "object");
		dataItems.put("required", required);
		dataItems.put("claves_primarias", claves_primarias);

		if (grid!=null)
			dataItems.put("grid",grid);

		dataItems.put("properties", properties);

		if (description.has("otherFields"))
			dataItems.put("otherFields",  description.get("otherFields"));
		dataItems.put("additionalProperties", false);

		data.put("type", "array");
		data.put("minItems", 1);
		data.put("items", dataItems);
		data.put("uniqueItems", true);

		formulariosItemsProperties.put("data", data);

		formulariosItems.put("type", "object");
		formulariosItems.put("required", mapper.createArrayNode().add("data"));

		formulariosItems.put("properties", formulariosItemsProperties);
		formularios.put("type", "array");
		formularios.put("minItems", 1);
		formularios.put("items", formulariosItems);

		formularioProperties.put("id", idFormulario);
		formularioProperties.put("formularios", formularios);

		idFormulario.put("enum", mapper.createArrayNode().add(id));

		formulario.put("type", "object");

		requiredForm.add("id");
		requiredForm.add("formularios");

		formulario.put("required", requiredForm);
		formulario.put("properties", formularioProperties);

		jsonSchema.put("$schema", "http://json-schema.org/draft-04/schema");
		jsonSchema.put("type", "object");
		jsonSchema.put("action", action);
		jsonSchema.put("method", "form-data-post");

		jsonSchema.put("required", mapper.createArrayNode().add("formulario"));

		jsonSchemaProperties.put("formulario", formulario);

		jsonSchema.put("properties", jsonSchemaProperties);

		return jsonSchema;
	}

	/**
	 * Crea el archivo jsonschema de un xml dado y lo coloca en un directorio
	 * especificado.
	 * 
	 * @author ltroconis
	 * 
	 * @param xmlFile
	 *            el archivo xml del formulario
	 * @param toDir
	 *            el directorio donde se van a colocar el jsonschema
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionParserJson
	 *             error haciendo el parse del archivo
	 */
	@VisibleForTesting
	public void doSchemasFromFile(File xmlFile, File toDir)
			throws ParserConfigurationException, SAXException, IOException,
			ExceptionParserJson {

		ObjectNode propertiesNodes = xmlToJsonSchemaProperties(xmlFile);

		String id = xmlFile.getName().replace(".xml", "");

		ObjectNode jsonSchema = createJsonShemaObject(id, propertiesNodes);

		String fileName = toDir + "/" + id + "-schema.json";

		createJsonShemaFile(fileName, jsonSchema);
	}

	/**
	 * A partir de un directorio de xmls de formularios dado crea los
	 * jsonschemas en un directorio especifico.
	 * 
	 * @ltroconis
	 * 
	 * @param fromDirXmlName
	 * @param toDirSchemaName
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionParserJson
	 */
	public void doSchemasFromDir(String fromDirXmlName, String toDirSchemaName)
			throws ParserConfigurationException, SAXException, IOException,
			ExceptionParserJson {

		File dir = new File(fromDirXmlName);
		File schemaDir = new File(toDirSchemaName);

		File[] dirFiles = dir.listFiles();

		ArrayList<File> files = new ArrayList<File>();
		files.addAll(Arrays.asList(dirFiles));
		Collections.sort(files, new DirComparator());

		for (int i = 0; i < files.size(); i++) {
			doSchemasFromFile(files.get(i), schemaDir);
		}

	}

}
