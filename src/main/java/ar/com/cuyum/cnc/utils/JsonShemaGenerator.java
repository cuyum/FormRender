package ar.com.cuyum.cnc.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.fest.util.VisibleForTesting;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ar.com.cuyum.cnc.exceptions.ExceptionParserJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author ltroconis
 * 
 */

class DirComparator implements Comparator<File> { 

public int compare(File o1, File o2) { 
        File a = (File)o1; 
        File b = (File)o2; 
        return a.getAbsolutePath(). 
                compareTo(b.getAbsolutePath()); 
    } 
} 

public class JsonShemaGenerator {
	private static Logger log = Logger.getLogger(JsonShemaGenerator.class);

	/**
	 * Crea el esquema de los selec1 a partir de xml node bind propertie
	 * type=select1.
	 * 
	 * @author ltroconis
	 * @param node el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson error de parser  
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeSelec1(Node node)
			throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode select1 = mapper.createObjectNode();

		select1.put("$ref", "formulario.json#/definitions/combo");

		Node constraints = node.getAttributes().getNamedItem("constraint");

		if (constraints != null) {
			String[] strConstrain = constraints.getNodeValue().split(" and ");
			for (int i = 0; i < strConstrain.length; i++) {
				String[] constraint = strConstrain[i].split("=");

				if (!Arrays.asList("url", "depends", "cuit").contains(
						constraint[0])) {
					throw new ExceptionParserJson(
							"Propiedad no valida en el select: "
									+ constraint[0]);
				}
				if ("cuit".equals(constraint[0])) {
					select1.put("cuit", true);
				} else if ("depends".equals(constraint[0])) {
					select1.put(
							constraint[0],
							constraint[1].split("/")[constraint[1].split("/").length - 1]);
				} else {
					select1.put(constraint[0], constraint[1]);
				}
			}
		}

		return select1;
	}

	/**
	 * Crea el esquema de los string a partir de xml node bind propertie
	 * type=string.
	 * 
	 * @author ltroconis
	 * @param node el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson error de parser  
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeString(Node node) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/string");

		Node relevant = node.getAttributes().getNamedItem("relevant");
		if (relevant != null) {
			input.put("relevant", relevant.getNodeValue().split("/")[relevant
					.getNodeValue().split("/").length - 1]);
		}

		return input;
	}

	/**
	 * Crea el esquema de los int a partir de xml node bind propertie type=int.
	 * 
	 * @param node el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson error de parser  
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeInt(Node node) throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/integer");

		Node constraints = node.getAttributes().getNamedItem("constraint");

		if (constraints != null) {
			String[] strConstrain = constraints.getNodeValue().split(" and ");
			for (int i = 0; i < strConstrain.length; i++) {
				String[] constraint = strConstrain[i].split("=");
				if (!Arrays.asList(".&gt;", ".&lt;",".>",".<","mask").contains(constraint[0])) {
					throw new ExceptionParserJson(
							"Propiedad no valida en el integer: "
									+ constraint[0]+" nombre: "+node.getAttributes().getNamedItem("nodeset"));
				}
				if("mask".equals(constraint[0])){
					input.put(constraint[0],constraint[1]);
				}else if(".<".equals(constraint[0])){
					//input.put("maximum",constraint[1]);
					continue;
				}else if(".>".equals(constraint[0])){
					//input.put("minimum",constraint[1]);
					continue;
				}else {
					throw new ExceptionParserJson(
							"Propiedad no fue considerada " + constraint[0]);
				}
			}
		}

		return input;
	}

	/**
	 * Crea el esquema de los decimal a partir de xml node bind propertie
	 * type=decimal.
	 * 
	 * @param node el nodo que corresponde al bind del xml
	 * @return el objeto json schema de los selec1
	 * @throws ExceptionParserJson error de parser  
	 */
	@VisibleForTesting
	private ObjectNode createJsonNodeDecimal(Node node) throws ExceptionParserJson {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode input = mapper.createObjectNode();

		input.put("$ref", "formulario.json#/definitions/decimal");
		
		Node constraints = node.getAttributes().getNamedItem("constraint");

		if (constraints != null) {
			String[] strConstrain = constraints.getNodeValue().split(" and ");
			for (int i = 0; i < strConstrain.length; i++) {
				String[] constraint = strConstrain[i].split("=");
				if (!Arrays.asList(".&gt;", ".&lt;",".>",".<").contains(constraint[0])) {
					throw new ExceptionParserJson(
							"Propiedad no valida en el decimal: "
									+ constraint[0]+" nombre: "+node.getAttributes().getNamedItem("nodeset"));
				}
				if(".<".equals(constraint[0])){
					//input.put("maximum",constraint[1]);
					continue;
				}else if(".>".equals(constraint[0])){
					//input.put("minimum",constraint[1]);
					continue;
				}else {
					throw new ExceptionParserJson(
							"Propiedad no fue considerada " + constraint[0]);
				}
			}
		}

		return input;
	}

	/**
	 * Busca los bind del archivo xml
	 * 
	 * @author ltroconis
	 * @param doc Document del archivo xml
	 * 
	 * @return la lista de nodos binsds del xml
	 */
	@VisibleForTesting
	public static NodeList getBindsFromXml(Document doc) {
		return doc.getDocumentElement().getElementsByTagName("h:head").item(0)
				.getOwnerDocument().getElementsByTagName("model").item(0)
				.getOwnerDocument().getElementsByTagName("bind");
	}

	/**
	 * Busca el id del formulario en el xml
	 * 
	 * @author ltroconis
	 * @param doc Document del archivo xml
	 * 
	 * @return el id del formulario
	 */
	@VisibleForTesting
	private String getIdFormularioFromXml(Document doc) {
		return doc.getDocumentElement().getElementsByTagName("h:head").item(0)
				.getOwnerDocument().getElementsByTagName("model").item(0)
				.getOwnerDocument().getElementsByTagName("instance").item(0)
				.getChildNodes().item(1).getAttributes().getNamedItem("id")
				.getNodeValue();
	}

	/**
	 * Busca el id del formulario en el xml
	 * 
	 * @author ltroconis
	 * @param doc
	 *            Document del archivo xml
	 * 
	 * @return Un nodo donde se encuentran las propiedades de los elementos del formulario
	 *         y aquellos campos que son obligatorios (los que no tienen el valor relevant)
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

		String idformulario = getIdFormularioFromXml(doc);

		log.info("Procesando formulario:" + idformulario);

		ArrayNode required = mapper.createArrayNode();

		for (int i = 0, n = binds.getLength(); i < n; i++) {
			Node node = binds.item(i);
			Node readonly = node.getAttributes().getNamedItem("readonly");
			if (readonly != null)
				continue;
			Node nodeset = node.getAttributes().getNamedItem("nodeset");
			Node type = node.getAttributes().getNamedItem("type");

			if (type == null) {
				log.warn(node.getNodeName()
						+ " : no tiene tipo, se asigna type=string por defecto");
			}

			Node relevant = node.getAttributes().getNamedItem("relevant");

			int leng = nodeset.getNodeValue().split("/").length;
			String name = nodeset.getNodeValue().split("/")[leng - 1];

			if (relevant == null)
				required.add(name);
			if (type != null && "select1".equals(type.getNodeValue())) {
				ObjectNode select1 = createJsonNodeSelec1(node);
				properties.put(name, select1);
			} else if (type != null && "int".equals(type.getNodeValue())) {
				try {
					ObjectNode input = createJsonNodeInt(node);
					properties.put(name, input);
				} catch (ExceptionParserJson e) {
                    throw new ExceptionParserJson(fXmlFile.getAbsoluteFile()+":"+e.getMessage());
				}
			} else if (type != null && "decimal".equals(type.getNodeValue())) {
				ObjectNode input = createJsonNodeDecimal(node);
				properties.put(name, input);
			} else if (type == null || "string".equals(type.getNodeValue())) {
				ObjectNode string = createJsonNodeString(node);
				properties.put(name, string);
			}

		}
		returnNode.put("properties", properties);
		returnNode.put("required", required);

		return returnNode;
	}
    
	/**
	 * Crea el archivo json a partir de un json.
	 * 
	 * @author ltroconis
	 * 
	 * @param fileName el nombre incluyendo la ruta donde se va colocar el archivo
	 * @param jsonSchema el objeto json que se va a guardar en el archivo
	 * @throws IOException error construyendo el archivo
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
	 * @param id del formulario
	 * @param required los campos que son requeridos en el formulario
	 * @param properties los campos que corresponden al formulario, extraidos de los binds del xml
	 * @return ObjectNode el objeto jsonschema final 
	 */
	@VisibleForTesting
	private ObjectNode createJsonShemaObject(String id,
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

		jsonSchema.put("required", mapper.createArrayNode().add("formulario"));

		jsonSchemaProperties.put("formulario", formulario);

		jsonSchema.put("properties", jsonSchemaProperties);

		return jsonSchema;
	}
    
	/**
	 * Crea el archivo jsonschema de un xml dado y lo coloca en un directorio especificado.
	 * 
	 * @author ltroconis
	 * 
	 * @param xmlFile el archivo xml del formulario
	 * @param toDir el directorio donde se van a colocar el jsonschema
	 * @throws ParserConfigurationException 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionParserJson error haciendo el parse del archivo
	 */
	@VisibleForTesting
	public void doSchemasFromFile(File xmlFile, File toDir)
			throws ParserConfigurationException, SAXException, IOException,
			ExceptionParserJson {

		ObjectNode propertiesNodes = xmlToJsonSchemaProperties(xmlFile);

		String id = xmlFile.getName().replace(".xml", "");

		ArrayNode required = (ArrayNode) propertiesNodes.get("required");
		ObjectNode properties = (ObjectNode) propertiesNodes.get("properties");

		ObjectNode jsonSchema = createJsonShemaObject(id, required, properties);

		String fileName = toDir + "/" + id + ".schema.json";

		createJsonShemaFile(fileName, jsonSchema);
	}

	/**
	 * A partir de un directorio de xmls de formularios dado crea los jsonschemas en un directorio 
	 * especifico.
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
		Collections.sort(files,new DirComparator());
	
		for (int i = 0; i < files.size(); i++) {
			doSchemasFromFile(files.get(i), schemaDir);
		}

	}

}
