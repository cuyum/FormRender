package ar.com.cuyum.cnc.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/* 
 * @autor ltroconis@cuyum.com
 * 
 * Tipos validos en Json
 *    
 * String (Cadenas de texto)
 * number (números)
 * object (Objetos)
 * char (caracteres unicode válidos)
 * array (Un arreglo o colección de valores)
 * null (nulo)
 * boolean (valores true o false)
 */
public class JsonToXMl {
	private XMLStreamWriter out;
	private String id;

	private void writeSimpleComponent(JSONObject jsonObject)
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

	private void jsonParserObject(JSONObject jsonObject, int nivel)
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
					writeSimpleComponent(object);
				}

				jsonParserObject(object, nivel + 1);

			} else {
				System.out.println(" " + jsonObject.get(key).toString());
			}
		}
	}

	public void descomponerJson() {
		JSONParser parser = new JSONParser();
		JSONObject components, header;

		int nivel = 0;

		try {

			Object obj = parser.parse(new FileReader("prueba.json"));

			JSONObject jsonObject = (JSONObject) obj;

			components = (JSONObject) ((JSONObject) jsonObject.get("payload"))
					.get("components");

			header = (JSONObject) ((JSONObject) jsonObject.get("header"));
			try {
				String title = (String) header.get("name");
				String action = "/api/deposition/draft";
				id = (String) "J" + jsonObject.get("_id");
				String tooltip = (String) header.get("descrip");

				createXML(title, action, id, tooltip, components);

				System.out.print("Creado el xml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void createXML(String title, String action, String id,
			String tooltip, JSONObject components)
			throws FileNotFoundException, UnsupportedEncodingException,
			XMLStreamException, FactoryConfigurationError {

		OutputStream outputStream = new FileOutputStream(new File("doc.xml"));

		out = XMLOutputFactory.newInstance().createXMLStreamWriter(
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

		
		//Hacer el parse de los componentes Json a XML
		jsonParserObject(components, 0);

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

		out.close();
	}

	public static void main(String[] args) {
		JsonToXMl generador = new JsonToXMl();
		generador.descomponerJson();

	}

}
