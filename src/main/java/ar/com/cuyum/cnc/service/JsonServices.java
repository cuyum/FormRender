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
import java.util.Iterator;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
	

	public String previewHtml(String jsonValue, ServletContext sc) throws Exception{
		//InputStream xmlStream = null;
		InputStream xslStream = null;
		//Formulario formulario = findById(formId);
		
		String xmlString = jsonToXForm(jsonValue);
		
		//xmlStream = fileUtils.getXmlInputStream(formulario, sc);
		xslStream = loadXslFile("formCnc.xsl", sc);
		if (null != xmlString && null != xslStream) {
			ts.setRemoteTransformation(false);
			InputStream inXml = new ByteArrayInputStream(xmlString.getBytes());
			String transformedHtml = ts.transform(inXml, xslStream,
					"1");
			transformedHtml = transformedHtml.replaceAll("___context___", sc.getServletContextName());
			return transformedHtml;
		} else 
			return "XML INVAIDO";

	}
	
	
	public InputStream loadXslFile(String filename, ServletContext sc) {
		InputStream xslIS = sc.getResourceAsStream("/WEB-INF/xsl/" + filename);
		return xslIS;
	}

//	public Formulario findById(Long id) {
//		return this.entityManager.find(Formulario.class, id);
//	}




    /////////////////METODOS DE CONVERSION//////////////////////////////
	private void writeSimpleComponent(JSONObject jsonObject, XMLStreamWriter out, String id)
			throws XMLStreamException {
		System.out.print("tipo:" + (String) jsonObject.get("type"));

		if ("text".equals(((String) jsonObject.get("type")).trim())
				|| "numeric".equals(((String) jsonObject.get("type")).trim())
				|| "date".equals(((String) jsonObject.get("type")).trim())) {

			System.out.println((String) ((JSONObject) jsonObject
					.get("properties")).get("label"));
			out.writeCharacters("\n\t\t\t\t\t");
			out.writeStartElement("input");
			out.writeAttribute("ref", "/" + id + "/areasprest/areas");

			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("label");
			out.writeCharacters((String) ((JSONObject) jsonObject
					.get("properties")).get("label"));// ((JSONObject)
														// jsonObject.get("properties")).
			out.writeEndElement();

			out.writeCharacters("\n\t\t\t\t\t\t");
			out.writeStartElement("hint");
			out.writeCharacters((String) ((JSONObject) jsonObject
					.get("properties")).get("label"));// ((JSONObject)
														// jsonObject.get("properties")).
			out.writeEndElement();

			out.writeCharacters("\n\t\t\t\t\t");
			out.writeEndElement();
		}

	}

	private void jsonParserObject(JSONObject jsonObject, int nivel, XMLStreamWriter out, String id)
			throws XMLStreamException {
		int nivelActual = nivel + 1;
		Iterator x = jsonObject.keySet().iterator();

		while (x.hasNext()) {
			String key = x.next().toString();
			String Objectype = jsonObject.get(key).getClass().getName();

			String espacio = String.format("%1$-" + nivelActual + "s", "");

			System.out.print(espacio + nivelActual + "->" + key + " : "
					+ Objectype);

			if (jsonObject.get(key) instanceof JSONObject) {
				JSONObject object = (JSONObject) jsonObject.get(key);

				System.out.println();
				if (object.containsKey("type")) {
					writeSimpleComponent(object, out, id);
				}

				jsonParserObject(object, nivel + 1, out, id);

			} else {
				System.out.println(" " + jsonObject.get(key).toString());
			}
		}
	}

	public String jsonToXForm(String jsonDoc)  throws Exception {

		JSONParser parser = new JSONParser();
		JSONObject components, header;

		int nivel = 0;

		Object obj;
		try {
			obj = parser.parse(jsonDoc);
		} catch (ParseException e) {
			throw new Exception("Error al parsear documento json "+ jsonDoc, e);
		}

		try {
			JSONObject jsonObject = (JSONObject) obj;

			components = (JSONObject) ((JSONObject) jsonObject.get("payload"))
					.get("components");

			header = (JSONObject) ((JSONObject) jsonObject.get("header"));

			String title = (String) header.get("name");
			String action = "/api/deposition/draft";
			String id = (String) "J" + jsonObject.get("_id");

			String tooltip = (String) header.get("descrip");
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

	

	private void createXML(String title, String action, String id,
			String tooltip, JSONObject components, OutputStream outputStream)
			throws Exception {

		// OutputStream outputStream = new FileOutputStream(new
		// File("doc.xml"));
		
		XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
				new OutputStreamWriter(outputStream, "utf-8"));

		out.writeStartDocument();
		out.writeCharacters("\n");
		out.writeStartElement("h:html");
		out.writeAttribute("xmlns", "http://www.w3.org/2002/xforms");
		out.writeAttribute("xmlns:ev", "http://www.w3.org/2001/xml-events");
		out.writeAttribute("xmlns:h", "http://www.w3.org/1999/xhtml");
		out.writeAttribute("xmlns:jr", "http://openrosa.org/javarosa");
		out.writeAttribute("xmlns:orx", "http://openrosa.org/xforms/");
		out.writeAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

		/************* CABECERA DEL XML ***************/
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

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("title");
		out.writeCharacters("\n\t\t\t\t\t");
		/* title */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("areasprest");
		out.writeAttribute("jr:template", "");

		out.writeCharacters("\n\t\t\t\t\t\t");
		out.writeStartElement("areas");
		out.writeCharacters("\n\t\t\t\t\t\t");
		/* areas */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t\t");
		/* areasprest */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t\t");
		out.writeStartElement("meta");

		out.writeCharacters("\n\t\t\t\t\t");
		/* meta */out.writeEndElement();

		out.writeCharacters("\n\t\t\t\t");
		/* id */out.writeEndElement();

		out.writeCharacters("\n\t\t\t");
		/* instance */out.writeEndElement();

		out.writeCharacters("\n\t\t");
		/* model */out.writeEndElement();

		out.writeCharacters("\n\t");
		/* h:head */out.writeEndElement();

		/************* CUERPO DEL XML ***************/

		out.writeCharacters("\n\t");
		out.writeStartElement("h:body");

		out.writeCharacters("\n\t\t");
		/* input tooltip */out.writeStartElement("input");
		out.writeAttribute("appearance", "tooltip");
		out.writeAttribute("ref", "/" + id + "/title");

		out.writeCharacters("\n\t\t\t");
		/* input hint tooltip */out.writeStartElement("hint");
		out.writeCharacters(tooltip);

		out.writeCharacters("\n\t\t\t");
		/* input hint tooltip */out.writeEndElement();

		out.writeCharacters("\n\t\t");
		/* input tooltip */out.writeEndElement();

		out.writeCharacters("\n\t\t");
		/* group */out.writeStartElement("group");
		out.writeAttribute("ref", "/" + id + "/areasprest");

		out.writeCharacters("\n\t\t\t");
		/* label group */out.writeStartElement("label");
		/* label group */out.writeEndElement();

		out.writeCharacters("\n\t\t\t");
		/* repeat */out.writeStartElement("repeat");
		out.writeAttribute("appearance", "grilla");
		out.writeAttribute("nodeset", "/" + id + "/areasprest");

		out.writeCharacters("\n\t\t\t\t");
		/* repeat group */out.writeStartElement("group");
		out.writeAttribute("appearance", "col4 well-white");
		out.writeAttribute("ref", "/" + id + "/areasprest/areas");

		// Hacer el parse de los componentes Json a XML
		jsonParserObject(components, 0, out, id);

		out.writeCharacters("\n\t\t\t\t");
		/* repeat group */out.writeEndElement();

		out.writeCharacters("\n\t\t\t");
		/* repeat */out.writeEndElement();

		out.writeCharacters("\n\t\t");
		/* group */out.writeEndElement();

		out.writeCharacters("\n\t");
		/* h:body */out.writeEndElement();

		out.writeCharacters("\n");
		/* h:html */out.writeEndElement();
		out.writeEndDocument();

	}
	
	
	
	//Persistimos en base y genermamos retornamos codigo que se utiliza para visualizar el documento
	public String persistFormFromJson(String jsonValue) throws Exception{
		

		//Creamos y llenamos los atributos del nuevo formulario a persistir
		Formulario formulario = new Formulario();

		JSONParser parser = new JSONParser();
		JSONObject header;

		Object obj;
		try {
			obj = parser.parse(jsonValue);
		} catch (ParseException e) {
			throw new Exception("Error al parsear documento json "+ jsonValue, e);
		}

		try {
			JSONObject jsonObject = (JSONObject) obj;
	
			header = (JSONObject) ((JSONObject) jsonObject.get("header"));
			
			formulario.setCodigo("J" + jsonObject.get("_id"));
			formulario.setNombre("" + header.get("name"));
		

			String xmlString = jsonToXForm(jsonValue);
			
			String path = frp.getDestinationXml();
			
		
			try {
				  File xmlToSave = new File(path + System.getProperty("file.separator") + "J" + jsonObject.get("_id") + ".xml");
		          BufferedWriter output = new BufferedWriter(new FileWriter(xmlToSave));
		          output.write(xmlString);
		          output.close();
		        } catch ( IOException e ) {
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
