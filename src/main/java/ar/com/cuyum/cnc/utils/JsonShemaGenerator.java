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

		if ("combo".equals(type)) {
			validConstraints = Arrays.asList("url", "depends", "cuit");
		} else if ("string".equals(type)) {
			; // No se evaluán constrains por los momentos
		} else if ("integer".equals(type)) {
			validConstraints = Arrays.asList(".&gt;", ".&lt;", ".>", ".<",
					"mask");
		} else if ("decimal".equals(type)) {
			validConstraints = Arrays.asList(".&gt;", ".&lt;", ".>", ".<");
		} else {
			throw new ExceptionParserJson(" type no reconocido para parser ");
		}

		Iterator<String> fields = constraints.fieldNames();

		while (fields.hasNext()) {
			String field = fields.next();
			if (!validConstraints.contains(field))
				JsonUtils.msg(false, "constraint " + field
						+ " no reconocido para el tipo " + type);
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
	private ObjectNode createJsonNodeSelec1(Node node,Element select)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode select1 = mapper.createObjectNode();

		select1.put("$ref", "formulario.json#/definitions/combo");

		ObjectNode constraints = CncFieldValidator.setConstraints(node,"combo");
		if (constraints != null) {
			JsonNode response = isValidConstraintToType("combo", constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());

			select1.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);

		if (relevant != null)
			select1.put("relevant", relevant);
		
		select1.put("label",select.getElementsByTagName("label").item(0).getTextContent());
		
		select1.put("hint",select.getElementsByTagName("hint").item(0).getTextContent());
		
		ArrayNode values = mapper.createArrayNode();
		
		Element item = (Element) select.getElementsByTagName("item").item(0);
		
		ObjectNode itemSelect = mapper.createObjectNode();
		itemSelect.put("label",item.getElementsByTagName("label").item(0).getTextContent());
		itemSelect.put("value",item.getElementsByTagName("value").item(0).getTextContent());
		
		values.add(itemSelect);
		
		select1.put("values",values);
		
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
	private ObjectNode createJsonNodeString(Node node)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/string");

		ObjectNode constraints = CncFieldValidator.setConstraints(node,"string");
		if (constraints != null) {
			JsonNode response = isValidConstraintToType("string", constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

		return input;
	}

	/**
	 * Crea el esquema de los int a partir de xml node bind propertie type=int.
	 * 
	 * @param node
	 *            el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson
	 *             error de parser
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeInt(Node node) throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/integer");

		ObjectNode constraints = CncFieldValidator.setConstraints(node,"integer");
		if (constraints != null) {
			JsonNode response = isValidConstraintToType("integer", constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

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
	private ObjectNode createJsonNodeDecimal(Node node)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/decimal");

		ObjectNode constraints = CncFieldValidator.setConstraints(node,"decimal");
		if (constraints != null) {
			JsonNode response = isValidConstraintToType("decimal", constraints);
			if (!response.get("success").asBoolean())
				throw new ExceptionParserJson(response.get("msg").toString());
			input.putAll(constraints);
		}

		ArrayNode relevant = setRelevant(node);
		if (relevant != null)
			input.put("relevant", relevant);

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
		return   doc.getElementsByTagName("submission").item(0)
				.getAttributes().getNamedItem("action").getNodeValue();
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
		return  doc.getElementsByTagName("instance").item(0)
				.getChildNodes().item(1).getAttributes().getNamedItem("id")
				.getNodeValue();
	}

	/**
	 * @author ltroconis
	 * 
	 * @param doc
	 * @return
	 */
	private Map<String,Element> setMapSelect1(Document doc){
		NodeList listSelectNode = doc.getElementsByTagName("select1");
		Map<String,Element> listSelect = new HashMap<String,Element>();
		
		for(int i=0,n=listSelectNode.getLength();i<n;i++){
			Node nodoSelect = listSelectNode.item(i);
			if (nodoSelect.getNodeType() == Node.ELEMENT_NODE) {
				Element dato = (Element) nodoSelect;
				String ref = dato.getAttribute("ref");
				listSelect.put(ref, dato);
			}
		}
		
		return listSelect;
	} 
	
	private ObjectNode createJsonNodeItem(Document doc) throws ExceptionParserJson{
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode item = null;
		
		NodeList listSelectNode = doc.getElementsByTagName("label");
		for(int i=0,n=listSelectNode.getLength();i<n;i++){
           String value = listSelectNode.item(i).getTextContent();
			if ("{title}".equals(value)){
				item = mapper.createObjectNode();
				item.put("$ref", "formulario.json#/definitions/item");
				break;
			}
		}
		
		return item;
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

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbFactory.newDocumentBuilder();
		Document doc = db.parse(fXmlFile);

		NodeList binds = getBindsFromXml(doc);

		String action = getActionFromXml(doc);
		
		Map<String,Element> listSelect = setMapSelect1(doc);
		
		ObjectNode item = createJsonNodeItem(doc);
		if(item!=null) properties.put("item", item);

		String idformulario = getIdFormularioFromXml(doc);

		log.info("Procesando formulario:" + idformulario);

		ArrayNode required = mapper.createArrayNode();
		
		//Lista de nombres de los que ciertos elementos dependen		
		List<String> depends = new ArrayList<String>();

		for (int i = 0, n = binds.getLength(); i < n; i++) {
			Node node = binds.item(i);
			Node readonly = node.getAttributes().getNamedItem("readonly");
			if (readonly != null)
				continue;
			Node nodeset = node.getAttributes().getNamedItem("nodeset");
			Node type = node.getAttributes().getNamedItem("type");

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

			if (relevant == null)
				required.add(name);

			ObjectNode object = null;
			try {

				if (type != null && "select1".equals(type.getNodeValue())) {	
					Element select = listSelect.get(nodeset.getNodeValue());
					
					object = createJsonNodeSelec1(node,select);
					
				} else if (type != null && "int".equals(type.getNodeValue())) {

					object = createJsonNodeInt(node);
				} else if (type != null
						&& "decimal".equals(type.getNodeValue())) {
					object = createJsonNodeDecimal(node);
				} else if (type == null || "string".equals(type.getNodeValue())) {
					object = createJsonNodeString(node);
				}

			} catch (ExceptionParserJson e) {
				throw new ExceptionParserJson(fXmlFile.getAbsoluteFile() + ":"
						+ e.getMessage());
			}
			
			if(object.has("depends")){
				if (name.equals(object.get("depends").asText())){
					throw new ExceptionParserJson(fXmlFile.getAbsoluteFile() +
							": el elemento "+name+" depende de si mismo");
				}
				depends.add(object.get("depends").asText());
			}

			if (object != null)
				properties.put(name, object);
		}
		
		for(int i=0,n=depends.size();i<n;i++){
			if(!properties.has(depends.get(i)))
				throw new ExceptionParserJson(fXmlFile.getAbsoluteFile() +
						": posee un elemento que depende de "+depends.get(i)+" pero este elemento no existe");	
		}
		
		returnNode.put("properties", properties);
		returnNode.put("required", required);
		returnNode.put("action", action);

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
	private ObjectNode createJsonShemaObject(String id, String action,
			ArrayNode required, ObjectNode properties) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode items = mapper.createObjectNode();
		ObjectNode data = mapper.createObjectNode();
		ObjectNode formulario = mapper.createObjectNode();
		ObjectNode formularioProperties = mapper.createObjectNode();
		ObjectNode idFormulario = mapper.createObjectNode();
		ObjectNode jsonSchemaProperties = mapper.createObjectNode();
		ObjectNode jsonSchema = mapper.createObjectNode();
		ArrayNode requiredForm = mapper.createArrayNode();

		items.put("type", "object");
		items.put("required", required);
		items.put("properties", properties);
		items.put("additionalProperties", false);

		data.put("type", "array");
		data.put("minItems", 1);
		data.put("items", items);
		data.put("uniqueItems", true);

		idFormulario.put("enum", mapper.createArrayNode().add(id));

		formularioProperties.put("id", idFormulario);
		formularioProperties.put("data", data);

		formulario.put("type", "object");

		requiredForm.add("id");
		requiredForm.add("data");

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

		ArrayNode required = (ArrayNode) propertiesNodes.get("required");
		ObjectNode properties = (ObjectNode) propertiesNodes.get("properties");
		TextNode action = (TextNode) propertiesNodes.get("action");

		ObjectNode jsonSchema = createJsonShemaObject(id, action.asText(),
				required, properties);

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
