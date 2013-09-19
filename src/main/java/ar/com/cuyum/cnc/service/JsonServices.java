package ar.com.cuyum.cnc.service;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.com.cuyum.cnc.domain.Formulario;
import ar.com.cuyum.cnc.domain.Xsl;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

@Stateless
public class JsonServices implements Serializable {

	@Inject
	private TransformationService ts;

	@Inject
	private FormRenderProperties frp;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String previewHtml(String jsonValue, ServletContext sc)
			throws Exception {
		// InputStream xmlStream = null;
		InputStream xslStream = null;
		// Formulario formulario = findById(formId);

		String xmlString = jsonToXForm(jsonValue);

		// xmlStream = fileUtils.getXmlInputStream(formulario, sc);
		xslStream = loadXslFile("formCnc.xsl", sc);
		if (null != xmlString && null != xslStream) {
			ts.setRemoteTransformation(false);
			InputStream inXml = new ByteArrayInputStream(xmlString.getBytes());
			String transformedHtml = ts.transform(inXml, xslStream, "1");
			transformedHtml = transformedHtml.replaceAll("___context___",
					sc.getServletContextName());
			return transformedHtml;
		} else
			return "XML INVAIDO";

	}

	public InputStream loadXslFile(String filename, ServletContext sc) {
		InputStream xslIS = sc.getResourceAsStream("/WEB-INF/xsl/" + filename);
		
		return xslIS;
	}

	/** 
	 * Escribe los input en el xml
	 *
	 * @autor ltroconis
	 * @param  name el nombre del objeto select, este seria el key que tiene en el Json y 
	 *         que va en la propiedad del tag en ref
	 * @param  jsonObject  el objeto json que representa el input
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeXMLInput(String name, JSONObject jsonObject, String id,
			XMLStreamWriter out) throws XMLStreamException {
		
		

		JSONObject properties = (JSONObject) jsonObject.get("properties");

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("input");

		if (properties.containsKey("groups_rel"))
			out.writeAttribute("ref",
					"/" + id + ((String) properties.get("groups_rel")) + "/"
							+ name);

		if (properties.containsKey("appearance")) {
			out.writeAttribute("appearance",
					((String) properties.get("appearance")));
		}

		if (properties.containsKey("label")) {

			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("label");
			out.writeCharacters((String) properties.get("label"));
			out.writeEndElement();
		}

		if (properties.containsKey("tooltip")) {
			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("hint");
			out.writeCharacters((String) properties.get("tooltip"));
			out.writeEndElement();
		} else {
			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("hint");
			out.writeCharacters((String) properties.get("label"));
			out.writeEndElement();
		}

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeEndElement();
	}

	/** 
	 * Escribe los select en el xml
	 *
	 * @autor ltroconis
	 * @param  name el nombre del objeto select, este seria el key que tiene en el Json y 
	 *         que va en la propiedad del tag en ref
	 * @param  jsonObject  el objeto json que representa el select
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeSelectOne(String name, JSONObject jsonObject, String id,
			XMLStreamWriter out) throws XMLStreamException {

		JSONObject properties = (JSONObject) jsonObject.get("properties");

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("select1");

		if (properties.containsKey("groups_rel"))
			out.writeAttribute("ref",
					"/" + id + ((String) properties.get("groups_rel")) + "/"
							+ name);

		if (properties.containsKey("appearance")) {
			out.writeAttribute("appearance",
					((String) properties.get("appearance")));
		}

		out.writeCharacters("\n\t\t\t\t\t\t");
		out.writeStartElement("label");
		out.writeCharacters((String) properties.get("label"));
		out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t\t\t");
		out.writeStartElement("hint");

		String tooltip = (String) properties.get("label");
		if (properties.containsKey("tooltip")) {
			tooltip = (String) properties.get("tooltip");
		}

		out.writeCharacters(tooltip);
		out.writeEndElement();

		if (jsonObject.containsKey("items")) {
			JSONArray items = (JSONArray) jsonObject.get("items");

			for (int i = 0, n = items.size(); i < n; i++) {
				JSONObject item = (JSONObject) items.get(i);
				out.writeCharacters("\n\t\t\t\t\t\t");
				out.writeStartElement("item");

				out.writeCharacters("\n\t\t\t\t\t\t\t");
				out.writeStartElement("label");
				out.writeCharacters(item.get("label").toString());
				out.writeEndElement();

				out.writeCharacters("\n\t\t\t\t\t\t\t");
				out.writeStartElement("value");
				out.writeCharacters(item.get("value").toString());
				out.writeEndElement();

				out.writeCharacters("\n\t\t\t\t\t\t");
				out.writeEndElement();
			}

		} else { // Para la opción harcodeada
			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("item");

			out.writeCharacters("\n\t\t\t\t\t\t\t");
			out.writeStartElement("label");
			out.writeCharacters("Seleccione una opción");
			out.writeEndElement();

			out.writeCharacters("\n\t\t\t\t\t\t\t");
			out.writeStartElement("value");
			out.writeCharacters("-1");
			out.writeEndElement();

			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeEndElement();
		}

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeEndElement();
	}

	/** 
	 * Escribe los grupos en el xml (el tag abierto)
	 *
	 * @autor ltroconis
	 * @param  name el nombre del objeto group, este seria el key que tiene en el Json y 
	 *         que va en la propiedad del tag en ref
	 * @param  jsonObject  el objeto json que representa el input
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeXMLOpenGroup(String name, JSONObject jsonObject,
			String id, XMLStreamWriter out) throws XMLStreamException {
		out.writeCharacters("\n\t\t\t");
		out.writeStartElement("group");

		if (jsonObject.containsKey("properties")) {

			JSONObject properties = (JSONObject) jsonObject.get("properties");

			if (properties.containsKey("groups_rel")) {
				out.writeAttribute("ref",
						"/" + id + ((String) properties.get("groups_rel"))
								+ "/" + name);
			}

			if (properties.containsKey("appearance")) {
				out.writeAttribute("appearance",
						(String) properties.get("appearance"));
			}

			if (properties.containsKey("label")) {
				out.writeCharacters("\n\t\t\t");
				out.writeStartElement("label");
				out.writeCharacters((String) properties.get("label"));
				out.writeEndElement();
			}
		}

	}

	/** 
	 * Escribe los repeat relacionados en el grupo del xml (el tag abierto), el parametro grid:true 
	 * que viene en el grupo
	 *
	 * @autor ltroconis
	 * @param  name el nombre del objeto group, este seria el key que tiene en el Json y 
	 *         que va en la propiedad del tag en ref, en este caso en el repeat
	 * @param  jsonObject  el objeto json que representa el repeat, armado manualmente, no viene 
	 *         en el json provisto. Cuando se leen los grupos le asigno el repeat como objeto
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeXMLOpenGroupRepeat(String name, JSONObject jsonObject,
			String id, XMLStreamWriter out) throws XMLStreamException {
		out.writeCharacters("\n\t\t\t");
		out.writeStartElement("repeat");

		if (jsonObject.containsKey("groups")) {
			out.writeAttribute("nodeset",
					"/" + id + "/" + jsonObject.get("groups") + "/" + name);
		}

		if (jsonObject.containsKey("appearance")) {
			out.writeAttribute("appearance",
					(String) jsonObject.get("appearance"));
		}

	}
	
	/** 
	 * Escribe los repeat relacionados en el grupo del xml (el tag cerrado), el parametro grid:true 
	 * que viene en el grupo
	 *
	 * @autor ltroconis
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException
	 * @see  #writeXMLOpenGroupRepeat(String name, JSONObject jsonObject,
			String id, XMLStreamWriter out)
	 */
	private void writeXMLCloseGroupRepeat(XMLStreamWriter out) throws XMLStreamException {
		out.writeCharacters("\n\t\t\t");
		out.writeEndElement();
	}

	/** 
	 * Escribe los grupos en el xml (el tag cerrado)
	 *
	 * @autor ltroconis
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 * @see  #writeXMLOpenGroup(String name, JSONObject jsonObject,
	 *		String id, XMLStreamWriter out)
	 */
	private void writeXMLCloseGroup(JSONObject jsonObject, XMLStreamWriter out)
			throws XMLStreamException {
		out.writeCharacters("\n\t\t\t");
		out.writeEndElement();
	}

	
	/** 
	 * Método de conversión según el componente
	 *
	 * @autor ltroconis
	 * @param  ruta la gerarquia del objeto en el json
	 * @param  key el nombre del objeto en el json
	 * @param  jsonObject  el objeto json que representa el componente
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 * @see #writeXMLInput(String name, JSONObject jsonObject, String id,
	 *		XMLStreamWriter out)
	 * @see #writeSelectOne(String name, JSONObject jsonObject, String id,
	 *		XMLStreamWriter out)
	 * @see #writeXMLOpenGroup(String name, JSONObject jsonObject,
	 *		String id, XMLStreamWriter out)
	 * @see #writeXMLOpenGroupRepeat(String name, JSONObject jsonObject,
			String id, XMLStreamWriter out)
	 */
	private void writeComponent(String ruta, String key, JSONObject jsonObject,
			String id, XMLStreamWriter out) throws XMLStreamException {

		String rutaActual = ruta + "/" + key;

		if (jsonObject.containsKey("properties")) {
			JSONObject properties = (JSONObject) jsonObject.get("properties");
			properties.put("groups_rel", ruta);
			jsonObject.put("properties", properties);
		}

		if ("text".equals(((String) jsonObject.get("type")).trim())
				|| "numeric".equals(((String) jsonObject.get("type")).trim())
				|| "date".equals(((String) jsonObject.get("type")).trim())) {

			writeXMLInput(key, jsonObject, id, out);

		} else if ("selectOne".equals(((String) jsonObject.get("type")).trim())) {

			if (jsonObject.containsKey("properties")) {
				JSONObject properties = (JSONObject) jsonObject
						.get("properties");
				properties.put("appearance", "minimal");
				jsonObject.put("properties", properties);
			}

			writeSelectOne(key, jsonObject, id, out);

		} else

		if ("group".equals(((String) jsonObject.get("type")).trim())) {
			JSONObject repeat = null;

			if (jsonObject.containsKey("properties")) {
				JSONObject properties = (JSONObject) jsonObject
						.get("properties");
				System.out.println(properties);
				if (properties.containsKey("grid")) {
					System.out.println((String) properties.get("grid"));
					System.out.println(Boolean.getBoolean((String) properties
							.get("grid")));
					if ("true".equals((String) properties.get("grid"))) {
						repeat = new JSONObject();
					}
				}
			}

			if (repeat != null) {
				repeat.put("appearance", "grilla");
				repeat.put("groups", key);
				jsonObject.put("repeat", repeat);
			}

			writeXMLOpenGroup(key, jsonObject, id, out);

			if (jsonObject.containsKey("repeat")) {
				writeXMLOpenGroupRepeat(key,
						(JSONObject) jsonObject.get("repeat"), id, out);
			}

			if (jsonObject.containsKey("components")) {
				jsonParserObject(rutaActual,
						(JSONObject) jsonObject.get("components"), id, out);
			}

			if (jsonObject.containsKey("repeat")) {
				writeXMLCloseGroupRepeat(out);
			}

			writeXMLCloseGroup(jsonObject, out);
		}

	}

	/** 
	 * Recorrido del Json (components) para crear los componentes que van en el body 
	 * del xml 
	 *
	 * @autor ltroconis
	 * @param  ruta la gerarquia del objeto en el json
	 * @param  jsonObject el objeto json que representa el tag components
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 * @see #writeComponent(String ruta, String key, JSONObject jsonObject,
	 *		String id, XMLStreamWriter out)
	 */
	private void jsonParserObject(String ruta, JSONObject jsonObject,
			String id, XMLStreamWriter out) throws XMLStreamException {
		Iterator x = jsonObject.keySet().iterator();
		while (x.hasNext()) {
			String key = x.next().toString();
			String Objectype = jsonObject.get(key).getClass().getName();
			
			if (jsonObject.get(key) instanceof JSONObject) {
				JSONObject object = (JSONObject) jsonObject.get(key);

				if (object.containsKey("type")) {
					writeComponent(ruta, key, object, id, out);
				}

			}
		}
	}

	/**
	 * Convierte un Json en xForm
	 *  
	 * @autor ltroconis
	 * @param  jsonDoc tipo String  contenido de un json
	 * @return  String  el contenido del xForm para la entrada al render
	 * @throws Exception que se origina a partir de un org.json.simple.parser.ParseException
	 * @throws org.json.simple.parser.ParseException error haciendo el parser del Json
	 */
	public String jsonToXForm(String jsonDoc) throws Exception {

		JSONParser parser = new JSONParser();
		JSONObject components, header;

		Object obj;
		try {
			obj = parser.parse(jsonDoc);
		} catch (ParseException e) {
			throw new Exception("Error al parsear documento json " + jsonDoc, e);
		}

		try {
			JSONObject jsonObject = (JSONObject) obj;

			components = (JSONObject) ((JSONObject) jsonObject.get("payload"))
					.get("components");

			header = (JSONObject) ((JSONObject) jsonObject.get("header"));

			String title = (String) header.get("name");

			String action = "/api/deposition/draft";

			if (header.containsKey("action")) {
				action = (String) header.get("action");
			}

			String id = (String) header.get("code");
			String tooltip = "";

			if (header.containsKey("tooltip")) {
				tooltip = (String) header.get("tooltip");
			} else if (header.containsKey("descrip")) {
				tooltip = (String) header.get("descrip");
			}

			JSONObject titleTooltip = new JSONObject();

			JSONObject propertiesTitleTooltip = new JSONObject();
			propertiesTitleTooltip.put("tooltip", tooltip);
			propertiesTitleTooltip.put("appearance", "tooltip");
			propertiesTitleTooltip.put("readonly", true);
			titleTooltip.put("properties", propertiesTitleTooltip);
			titleTooltip.put("type", "text");
			components.put("title", titleTooltip);

			OutputStream out = new ByteArrayOutputStream();

			createXML(title, action, id, tooltip, components, out);

			out.close();

			System.out.print("Creado el xml");

			String ret = out.toString();
			return ret;

		} catch (Exception e) {
			throw new Exception("Error en conversión de JSON  a XML", e);
		}

	}

	/** 
	 * Recorrido del Json para crear los objetos TagInstancia que permiten crear la parte de 
	 * instancia del xml y del objeto nodes para crear los binds de xml
	 *
	 * @autor ltroconis
	 * @param  ruta la gerarquia del objeto en el json
	 * @param  tags lista donde se pone la configuración para los tag de instancia
	 * @see  TagInstancia
	 * @param  nodes mapa que guarda el recorrido por cada objeto y que sirve para construir los binds
	 * @return void
	 */
	public void getJsonInstanceAndBindsObject(String ruta,
			JSONObject jsonObject, ArrayList<TagInstancia> tags,
			Map<String, JSONObject> nodes) {
		String rutaActual = ruta;

		Iterator x = jsonObject.keySet().iterator();

		while (x.hasNext()) {
			String key = x.next().toString();
			String Objectype = jsonObject.get(key).getClass().getName();

			if (jsonObject.get(key) instanceof JSONObject) {
				JSONObject object = (JSONObject) jsonObject.get(key);

				if (object.containsKey("components")) {
					rutaActual = rutaActual + "/" + key;
					tags.add(new TagInstancia(true, key, false));
				}

				getJsonInstanceAndBindsObject(rutaActual, object, tags, nodes);

				if (object.containsKey("components")) {
					tags.add(new TagInstancia(false, key, true));
				} else if (object.containsKey("type")) {
					tags.add(new TagInstancia(true, key, true));
					String ref = rutaActual + "/" + key;
					nodes.put(ref, object);
				}
			}
		}
	}

	/** 
	 *Construye la cabecera del archivo xml
	 *
	 * @autor ltroconis
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeXMLFileHead(XMLStreamWriter out)
			throws XMLStreamException {
		out.writeStartDocument();
		out.writeCharacters("\n");
		out.writeStartElement("h:html");
		out.writeAttribute("xmlns", "http://www.w3.org/2002/xforms");
		out.writeAttribute("xmlns:ev", "http://www.w3.org/2001/xml-events");
		out.writeAttribute("xmlns:h", "http://www.w3.org/1999/xhtml");
		out.writeAttribute("xmlns:jr", "http://openrosa.org/javarosa");
		out.writeAttribute("xmlns:orx", "http://openrosa.org/xforms/");
		out.writeAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
	}

	/** 
	 * Construye la sección de instancia del archivo xml
	 *
	 * @autor ltroconis
	 * @param  tags lista de tags que van en la instancia
	 * @see  TagInstancia
	 * @param  out el writerString del xml  
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeInstance(ArrayList<TagInstancia> tags, XMLStreamWriter out)
			throws XMLStreamException {
		int n = tags.size();

		for (int i = 0; i < n; i++) {
			TagInstancia tag = tags.get(i);
			if (tag.getAbrir() && tag.getCerrar()) {
				out.writeCharacters("\n\t\t\t\t\t\t");
				out.writeStartElement(tag.getTag());
				out.writeEndElement();
			} else if (tag.getAbrir()) {
				out.writeCharacters("\n\t\t\t\t\t\t");
				out.writeStartElement(tag.getTag());
			} else if (tag.getCerrar()) {
				out.writeCharacters("\n\t\t\t\t\t\t");
				out.writeEndElement();
			}
		}
	}

	/** 
	 * Construye la sección de binds del archivo xml
	 *
	 * @autor ltroconis
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  binds mapa que tiene los binds 
	 * @param  out el writerString del xml  
	 * @return void
	 * @throws XMLStreamException  
	 * @see  #getJsonInstanceAndBindsObject(String ruta,JSONObject jsonObject, ArrayList<TagInstancia> tags,Map<String, JSONObject> nodes)
	 * 
	 */
	private void writeXMLBinds(String id, Map<String, JSONObject> binds,
			XMLStreamWriter out) throws XMLStreamException {
		Iterator keys = binds.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next().toString();
			System.out.println("/" + id + key);

			JSONObject bindObject = binds.get(key);

			out.writeCharacters("\n\t\t");
			out.writeStartElement("bind");
			out.writeAttribute("nodeset", "/" + id + key);

			if (bindObject.containsKey("properties")) {
				JSONObject properties = (JSONObject) bindObject
						.get("properties");
				if (properties.containsKey("readonly")) {
					out.writeAttribute("readonly", ((Boolean) properties
							.get("readonly")) ? "true()" : "false()");
				}
				if (properties.containsKey("required")) {
					out.writeAttribute("required", ((Boolean) properties
							.get("required")) ? "true()" : "false()");
				}
				if (properties.containsKey("constraint")) {
					JSONObject constraints = (JSONObject) properties
							.get("constraint");
					String constraint = "";
					if (constraints.containsKey("url")) {
						constraint = constraint + "url="
								+ constraints.get("url");
					}
					if (constraints.containsKey("depends")) {
						constraint = constraint + " and depends=" + "/" + id
								+ "/" + constraints.get("depends");
					}
					out.writeAttribute("constraint", constraint);
				}
				if (properties.containsKey("relevant")) {
					out.writeAttribute("relevant", " /" + id + "/"
							+ (String) properties.get("relevant"));
				}
			}

			if (bindObject.containsKey("type")) {
				String type = (String) bindObject.get("type");

				if ("selectOne".equals(type)) {
					type = "select1";
				}

				if ("text".equals(type)) {
					type = "string";
				}

				out.writeAttribute("type", type);
			}

			out.writeEndElement();
		}

		out.writeCharacters("\n\t\t");
		out.writeStartElement("bind");
		out.writeAttribute("nodeset", "/" + id + "/meta/instanceID");
		out.writeAttribute("calculate", "concat('uuid:', uuid())");
		out.writeAttribute("readonly", "true()");
		out.writeAttribute("type", "string");
		out.writeEndElement();
	}
	
	/** 
	 * Construye el tag head del archivo xml
	 *
	 * @autor ltroconis
	 * @param  id  el id del formulario, es tomado del header.code
	 * @param  title  titulo del formulario, es tomado del header.code
	 * @param  action el valor del action del formulario 
	 * @param  tags lista de tags que van en la instancia
	 * @param  binds mapa que tiene los binds 
	 * @param  out el writerString del xml 
	 * @return void
	 * @throws XMLStreamException 
	 */
	private void writeXMLHead(String id, String title, String action,
			ArrayList<TagInstancia> tags, Map<String, JSONObject> binds,
			XMLStreamWriter out) throws XMLStreamException {
		out.writeCharacters("\n\t");
		out.writeStartElement("h:head");

		out.writeCharacters("\n\t\t");
		out.writeStartElement("h:title");
		out.writeCharacters(title);
		/* h:title */out.writeEndElement();

		out.writeCharacters("\n\t\t");
		out.writeStartElement("model");

		out.writeCharacters("\n\t\t\t");
		out.writeStartElement("submission");
		out.writeAttribute("action", action);
		out.writeAttribute("method", "form-data-post");

		out.writeCharacters("\n\t\t\t");
		/* submission */out.writeEndElement();

		out.writeCharacters("\n\t\t\t");
		out.writeStartElement("instance");

		out.writeCharacters("\n\t\t\t\t");
		out.writeStartElement(id);
		out.writeAttribute("id", id);

		writeInstance(tags, out);

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("meta");

		out.writeCharacters("\n\t\t\t\t\t\t");
		out.writeStartElement("instanceID");
		out.writeCharacters("\n\t\t\t\t\t\t");
		/* meta */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t\t");
		/* meta */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t");
		/* id */out.writeEndElement();

		out.writeCharacters("\n\t\t\t");
		/* instance */out.writeEndElement();

		writeXMLBinds(id, binds, out);

		out.writeCharacters("\n\t\t");
		/* model */out.writeEndElement();

		out.writeCharacters("\n\t");
		/* h:head */out.writeEndElement();
	}

	private void createXML(String title, String action, String id,
			String tooltip, JSONObject components, OutputStream outputStream)
			throws Exception {

		ArrayList<TagInstancia> tags = new ArrayList<TagInstancia>();
		Map<String, JSONObject> binds = new HashMap<String, JSONObject>();
		getJsonInstanceAndBindsObject("", components, tags, binds);

		XMLStreamWriter out = XMLOutputFactory.newInstance()
				.createXMLStreamWriter(
						new OutputStreamWriter(outputStream, "utf-8"));

		writeXMLFileHead(out);

		writeXMLHead(id, title, action, tags, binds, out);

		/************* CUERPO DEL XML ****************/

		out.writeCharacters("\n\t");
		out.writeStartElement("h:body");

		jsonParserObject("", components, id, out);

		out.writeCharacters("\n\t");
		/* h:body */ out.writeEndElement();

		out.writeCharacters("\n");
		/* h:html */ out.writeEndElement();
		out.writeEndDocument();

	}

	// Persistimos en base y genermamos retornamos codigo que se utiliza para
	// visualizar el documento
	public String persistFormFromJson(String jsonValue) throws Exception {

		// Creamos y llenamos los atributos del nuevo formulario a persistir
		Formulario formulario = new Formulario();

		JSONParser parser = new JSONParser();
		JSONObject header;

		Object obj;
		try {
			obj = parser.parse(jsonValue);
		} catch (ParseException e) {
			throw new Exception("Error al parsear documento json " + jsonValue,
					e);
		}

		try {
			JSONObject jsonObject = (JSONObject) obj;
			
			header = (JSONObject) ((JSONObject) jsonObject.get("header"));

			String title = (String) header.get("name");

			String id = (String) header.get("code");
		
			header = (JSONObject) ((JSONObject) jsonObject.get("header"));

			formulario.setCodigo(id);
			formulario.setNombre(title);

			String xmlString = jsonToXForm(jsonValue);

			String path = frp.getDestinationXml();

			try {
				File xmlToSave = new File(path
						+ System.getProperty("file.separator") + id + ".xml");
				BufferedWriter output = new BufferedWriter(new FileWriter(
						xmlToSave));
				output.write(xmlString);
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			formulario.setArchivo("J" + jsonObject.get("_id") + ".xml");
			formulario.setUrl(path);
			Xsl xsl = entityManager.find(Xsl.class, 1L);
			System.out.println("Xsl encontrado: " + xsl.getNombre());
			formulario.setXslTransform(xsl);
			entityManager.persist(formulario);

		} catch (Exception e) {
			throw new Exception("Error en conversión de JSON  a XML", e);
		}

		return formulario.getCodigo();
	}

}
