package ar.com.cuyum.cnc.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

import ar.com.cuyum.cnc.domain.Formulario;
import ar.com.cuyum.cnc.domain.Xsl;
import ar.com.cuyum.cnc.utils.FormRenderProperties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Stateless
public class JsonServices implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
    private TransformationService ts;

	@Inject
    private FormRenderProperties frp;

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public String previewHtml(final String jsonValue, final ServletContext sc)
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
        } else {
            return "XML INVAIDO";
        }

    }
    

    public InputStream loadXslFile(String filename, ServletContext sc) {
        InputStream xslIS = sc.getResourceAsStream("/WEB-INF/xsl/" + filename);

        return xslIS;
    }

    /**
     * Escribe los input en el xml.
     *
     * @autor ltroconis
     * @param name
     *            el nombre del objeto select, este seria el key que tiene en el
     *            Json y que va en la propiedad del tag en ref
     * @param jsonObject
     *            el objeto json que representa el input
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeXMLInput(final String name, final JsonNode jsonObject,
            final String id, final XMLStreamWriter out)
            throws XMLStreamException {

        JsonNode properties = jsonObject.get("properties");

        out.writeCharacters("\n\t\t\t\t\t");
        out.writeStartElement("input");

        if (properties.has("groups_rel")) {
            out.writeAttribute("ref", "/" + id
                    + properties.get("groups_rel").asText() + "/" + name);
        }

        if (properties.has("appearance")) {
            out.writeAttribute("appearance", properties.get("appearance")
                    .asText());
        }

        if (properties.has("label")) {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("label");
            out.writeCharacters(properties.get("label").asText());
            out.writeEndElement();
        }

        if (properties.has("hint")) {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("hint");
            out.writeCharacters(properties.get("hint").asText());
            out.writeEndElement();
        } else {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("hint");
            out.writeCharacters(properties.get("label").asText());
            out.writeEndElement();
        }
        
        out.writeCharacters("\n\t\t\t\t\t");
        out.writeEndElement();
    }
    
    /**
     * Escribe los input en el xml.
     *
     * @autor ltroconis
     * @param name
     *            el nombre del objeto select, este seria el key que tiene en el
     *            Json y que va en la propiedad del tag en ref
     * @param jsonObject
     *            el objeto json que representa el input
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeXMLTextArea(final String name, final JsonNode jsonObject,
            final String id, final XMLStreamWriter out)
            throws XMLStreamException {

        JsonNode properties = jsonObject.get("properties");

        out.writeCharacters("\n\t\t\t\t\t");
        out.writeStartElement("input");

        if (properties.has("groups_rel")) {
            out.writeAttribute("ref", "/" + id
                    + properties.get("groups_rel").asText() + "/" + name);
        }

        if (properties.has("appearance")) {
            out.writeAttribute("appearance", properties.get("appearance").asText());
        }

        if (properties.has("label")) {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("label");
            out.writeCharacters(properties.get("label").asText());
            out.writeEndElement();
        }

        if (properties.has("hint")) {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("hint");
            out.writeCharacters(properties.get("hint").asText());
            out.writeEndElement();
        } else {
            out.writeCharacters("\n\t\t\t\t\t\t");
            out.writeStartElement("hint");
            out.writeCharacters(properties.get("label").asText());
            out.writeEndElement();
        }        	

        out.writeCharacters("\n\t\t\t\t\t");
        out.writeEndElement();
    }


   
    /**

    
    /**
     * Escribe los select en el xml.
     *
     * @autor ltroconis
     * @param name
     *            el nombre del objeto select, este seria el key que tiene en el
     *            Json y que va en la propiedad del tag en ref
     * @param jsonObject
     *            el objeto json que representa el select
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeSelectOne(final String name, final JsonNode jsonObject,
            final String id, final XMLStreamWriter out)
            throws XMLStreamException {

        JsonNode properties = jsonObject.get("properties");

        out.writeCharacters("\n\t\t\t\t\t");
        out.writeStartElement("select1");

        if (properties.has("groups_rel")) {
            out.writeAttribute("ref", "/" + id
                    + properties.get("groups_rel").asText() + "/" + name);
        }

        if (properties.has("appearance")) {
            out.writeAttribute("appearance", properties.get("appearance")
                    .asText());
        }

        out.writeCharacters("\n\t\t\t\t\t\t");
        out.writeStartElement("label");
        out.writeCharacters(properties.get("label").asText());
        out.writeEndElement();

        out.writeCharacters("\n\t\t\t\t\t\t");
        out.writeStartElement("hint");

        String tooltip = properties.get("label").asText();
        
        if (properties.has("hint")) {
            tooltip = properties.get("hint").asText();
        }

        out.writeCharacters(tooltip);
        out.writeEndElement();

        if (jsonObject.has("items")) {
            JsonNode nodeItems = jsonObject.get("items");

            if (nodeItems.isArray()) {
                Iterator<JsonNode> items = nodeItems.elements();
                while (items.hasNext()) {

                    JsonNode item = items.next();

                    out.writeCharacters("\n\t\t\t\t\t\t");
                    out.writeStartElement("item");

                    out.writeCharacters("\n\t\t\t\t\t\t\t");
                    out.writeStartElement("label");
                    out.writeCharacters(item.get("label").asText());
                    out.writeEndElement();

                    out.writeCharacters("\n\t\t\t\t\t\t\t");
                    out.writeStartElement("value");
                    out.writeCharacters(item.get("value").asText());
                    out.writeEndElement();

                    out.writeCharacters("\n\t\t\t\t\t\t");
                    out.writeEndElement();
                }
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
     * Escribe los grupos en el xml (el tag abierto).
     *
     * @autor ltroconis
     * @param name
     *            el nombre del objeto group, este seria el key que tiene en el
     *            Json y que va en la propiedad del tag en ref
     * @param jsonObject
     *            el objeto json que representa el input
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeXMLOpenGroup(final String name,
            final JsonNode jsonObject, final String id,
            final XMLStreamWriter out) throws XMLStreamException {
        out.writeCharacters("\n\t\t\t");
        out.writeStartElement("group");

        if (jsonObject.has("properties")) {

            JsonNode properties = jsonObject.get("properties");

            if (properties.has("groups_rel")) {
                out.writeAttribute("ref",
                        "/" + id + properties.get("groups_rel").asText() + "/"
                                + name);
            }

            if (properties.has("appearance")) {
                out.writeAttribute("appearance", properties.get("appearance")
                        .asText());
            }

            if (properties.has("label")) {
                out.writeCharacters("\n\t\t\t");
                out.writeStartElement("label");
                out.writeCharacters(properties.get("label").asText());
                out.writeEndElement();
            }
        }

    }
    
    private void writeXMLOpenEmptyGroup(final String name,
            final JsonNode jsonObject, final String id,
            final XMLStreamWriter out) throws XMLStreamException {
        out.writeCharacters("\n\t\t\t");
        out.writeStartElement("group");


    }


    /**
     * Escribe los repeat relacionados en el grupo del xml (el tag abierto), el
     * parametro grid:true que viene en el grupo.
     *
     * @autor ltroconis
     * @param name
     *            el nombre del objeto group, este seria el key que tiene en el
     *            Json y que va en la propiedad del tag en ref, en este caso en
     *            el repeat
     * @param jsonObject
     *            el objeto json que representa el repeat, armado manualmente,
     *            no viene en el json provisto. Cuando se leen los grupos le
     *            asigno el repeat como objeto
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeXMLOpenGroupRepeat(final String name,
            final JsonNode jsonObject, final String id,
            final XMLStreamWriter out) throws XMLStreamException {
        out.writeCharacters("\n\t\t\t");
        out.writeStartElement("repeat");

        if (jsonObject.has("groups")) {
            out.writeAttribute("nodeset",
                    "/" + id + "/" + jsonObject.get("groups") + "/" + name);
        }

        if (jsonObject.has("appearance")) {
            out.writeAttribute("appearance", jsonObject.get("appearance")
                    .asText());
        }

    }

    /**
     * Escribe los repeat relacionados en el grupo del xml (el tag cerrado), el
     * parametro grid:true que viene en el grupo.
     *
     * @autor ltroconis
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     * @see #writeXMLOpenGroupRepeat(String name, JSONObject jsonObject, String
     *      id, XMLStreamWriter out)
     */
    private void writeXMLCloseGroupRepeat(final XMLStreamWriter out)
            throws XMLStreamException {
        out.writeCharacters("\n\t\t\t");
        out.writeEndElement();
    }

    /**
     * Escribe los grupos en el xml (el tag cerrado).
     *
     * @autor ltroconis
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     * @see #writeXMLOpenGroup(String name, JSONObject jsonObject, String id,
     *      XMLStreamWriter out)
     */
    private void writeXMLCloseGroup(final XMLStreamWriter out)
            throws XMLStreamException {
        out.writeCharacters("\n\t\t\t");
        out.writeEndElement();
    }

    /**
     * Método de conversión según el componente.
     *
     * @autor ltroconis
     * @param ruta
     *            la gerarquia del objeto en el json
     * @param key
     *            el nombre del objeto en el json
     * @param jsonObject
     *            el objeto json que representa el componente
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     * @throws IOException
     *             error construyendo el xml
     * @throws JsonMappingException
     *             error construyendo el xml
     * @throws JsonParseException
     *             error construyendo el xml
     * @see #writeXMLInput(String name, JSONObject jsonObject, String id,
     *      XMLStreamWriter out)
     * @see #writeSelectOne(String name, JSONObject jsonObject, String id,
     *      XMLStreamWriter out)
     * @see #writeXMLOpenGroup(String name, JSONObject jsonObject, String id,
     *      XMLStreamWriter out)
     * @see #writeXMLOpenGroupRepeat(String name, JSONObject jsonObject, String
     *      id, XMLStreamWriter out)
     */
    private void writeComponent(final String ruta, final String key,
            JsonNode jsonObject, final String id, final XMLStreamWriter out)
            throws XMLStreamException, JsonParseException,
            JsonMappingException, IOException {

        String rutaActual = ruta + "/" + key;

        if (jsonObject.has("properties")) {
            ObjectNode writerPropertie = (ObjectNode) jsonObject
                    .get("properties");
            writerPropertie.put("groups_rel", ruta);

            ObjectNode writeJsonObject = (ObjectNode) jsonObject;
            writeJsonObject.put("properties", writerPropertie);

            jsonObject = writeJsonObject;
        }

        if ("text".equals(jsonObject.get("type").asText().trim())
                || "int".equals(jsonObject.get("type").asText().trim())
                || "date".equals(jsonObject.get("type").asText().trim())
                || "decimal".equals(jsonObject.get("type").asText().trim())) {

            writeXMLInput(key, jsonObject, id, out);

        } else 
        	
        	if ("selectOne".equals(jsonObject.get("type").asText().trim())) {

            if (jsonObject.has("properties")) {
                ObjectNode writerPropertie = (ObjectNode) jsonObject
                        .get("properties");
                writerPropertie.put("appearance", "minimal");
                ObjectNode writerJsonObject = (ObjectNode) jsonObject;
                writerJsonObject.put("properties", writerPropertie);
                jsonObject = writerJsonObject;
            }

            writeSelectOne(key, jsonObject, id, out);

        } else

        if ("group".equals(jsonObject.get("type").asText().trim())) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode repeat = null;

            if (jsonObject.has("properties")) {
                if (jsonObject.get("properties").has("grid")) {
//                    if ("true".equals(jsonObject.get("properties").get("grid")
//                            .asText())) {
                        repeat = mapper.createObjectNode();
//                    }
                }
            }
            writeXMLOpenEmptyGroup("", jsonObject, id, out);
            
            writeXMLOpenGroup(key, jsonObject, id, out);
            
            if (repeat != null) {
                repeat.put("appearance", "grilla");
                repeat.put("groups", key);

                ObjectNode writerJsonObject = (ObjectNode) jsonObject;
                writerJsonObject.put("repeat", repeat);

                jsonObject = writerJsonObject;
            }

           
            if (jsonObject.has("repeat")) {
                writeXMLOpenGroupRepeat(key, jsonObject.get("repeat"), id, out);
            }
            writeXMLOpenEmptyGroup("", jsonObject, id, out);

            if (jsonObject.has("components")) {
                jsonParserObject(rutaActual, jsonObject.get("components"), id,
                        out);
            }
            
            writeXMLCloseGroup(out);

            if (jsonObject.has("repeat")) {
                writeXMLCloseGroupRepeat(out);
            }
            

            writeXMLCloseGroup(out);
            writeXMLCloseGroup(out);
        }else
        	if("string".equals(jsonObject.get("type").asText().trim())){
        		writeXMLTextArea(key, jsonObject, id, out);
        	}
        	
      	}
        

    

    /**
     * Recorrido del Json (components) para crear los componentes que van en el
     * body del xml.
     *
     * @autor ltroconis
     * @param ruta
     *            la gerarquia del objeto en el json
     * @param jsonObject
     *            el objeto json que representa el tag components
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     * @throws IOException
     *             error construyendo el xml
     * @throws JsonMappingException
     *             error construyendo el xml
     * @throws JsonParseException
     *             error construyendo el xml
     * @see #writeComponent(String ruta, String key, JSONObject jsonObject,
     *      String id, XMLStreamWriter out)
     */
    private void jsonParserObject(final String ruta, final JsonNode jsonObject,
            final String id, final XMLStreamWriter out)
            throws XMLStreamException, JsonParseException,
            JsonMappingException, IOException {
        Iterator<String> x = jsonObject.fieldNames();
        while (x.hasNext()) {
            String key = x.next().toString();

            if (jsonObject.get(key) instanceof JsonNode) {
                JsonNode object = jsonObject.get(key);

                if (object.has("type")) {
                    writeComponent(ruta, key, object, id, out);
                }

            }
        }
    }

    /**
     * Convierte un Json en xForm.
     *
     * @autor ltroconis
     * @param jsonDoc
     *            tipo String contenido de un json
     * @return String el contenido del xForm para la entrada al render
     * @throws Exception
     *             que se origina a partir de un
     *             org.json.simple.parser.ParseException
     * @throws Exception
     *             error haciendo el parser del Json
     */
    public final String jsonToXForm(String jsonDoc) throws Exception {

        JsonNode components, header;

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode jsonObject = mapper.readTree(jsonDoc);

            components = jsonObject.get("payload").get("components");

            header = jsonObject.get("header");

            String title = header.get("name").asText();

            String action = "/api/deposition/draft";

            if (header.has("action")) {
                action = header.get("action").asText();
            }

            String id = header.get("code").asText();
            String tooltip = "";

            if (header.has("hint")) {
                tooltip = header.get("hint").asText();
            } else if (header.has("descrip")) {
                tooltip = header.get("descrip").asText();
            }

            ObjectNode writerComponent = (ObjectNode) components;

            ObjectNode nodeTooltip = mapper.createObjectNode();

            ObjectNode properties = mapper.createObjectNode();
            properties.put("hint", tooltip);
            properties.put("appearance", "tooltip");
            properties.put("readonly", true);

            nodeTooltip.put("properties", properties);
            nodeTooltip.put("type", "text");

            writerComponent.put("title", nodeTooltip);

            components = writerComponent;

            OutputStream out = new ByteArrayOutputStream();

            createXML(title, action, id, tooltip, components, out);

            out.close();

            String ret = out.toString();
            return ret;

        } catch (Exception e) {
            throw new Exception("Error en conversión de JSON  a XML", e);
        }

    }

    /**
     * Recorrido del Json para crear los objetos TagInstancia que permiten crear
     * la parte de instancia del xml y del objeto nodes para crear los binds de
     * xml.
     *
     * @autor ltroconis
     * @param ruta
     *            la gerarquia del objeto en el json
     * @param tags
     *            lista donde se pone la configuración para los tag de instancia
     * @see TagInstancia
     * @param nodes
     *            mapa que guarda el recorrido por cada objeto y que sirve para
     *            construir los binds
     * @param components
     *            los componentes en el json
     */
    public static void getJsonInstanceAndBindsObject(final String ruta,
            final JsonNode components, final ArrayList<TagInstancia> tags,
            final Map<String, JsonNode> nodes) {
        String rutaActual = ruta;

        Iterator<String> x = components.fieldNames();

        while (x.hasNext()) {
            String key = x.next().toString();

            if (components.get(key) instanceof JsonNode) {
                JsonNode object = components.get(key);

                if (object.has("components")) {
                    rutaActual = rutaActual + "/" + key;
                    tags.add(new TagInstancia(true, key, false));
                }

                getJsonInstanceAndBindsObject(rutaActual, object, tags, nodes);

                if (object.has("components")) {
                    tags.add(new TagInstancia(false, key, true));
                } else if (object.has("type")) {
                    tags.add(new TagInstancia(true, key, true));
                    String ref = rutaActual + "/" + key;
                    nodes.put(ref, object);
                }
            }
        }
    }

    /**
     * Construye la cabecera del archivo xml.
     *
     * @autor ltroconis
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el archivo
     */
    private void writeXMLFileHead(final XMLStreamWriter out)
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
     * Construye la sección de instancia del archivo xml.
     *
     * @autor ltroconis
     * @param tags
     *            lista de tags que van en la instancia
     * @see TagInstancia
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeInstance(final ArrayList<TagInstancia> tags,
            final XMLStreamWriter out) throws XMLStreamException {
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
     * Construye la sección de binds del archivo xml.
     *
     * @autor ltroconis
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param binds
     *            mapa que tiene los binds
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     * @see #getJsonInstanceAndBindsObject(String ruta,JSONObject jsonObject,
     *      ArrayList<TagInstancia> tags,Map<String, JSONObject> nodes)
     *
     */
    private void writeXMLBinds(final String id,
            final Map<String, JsonNode> binds, final XMLStreamWriter out)
            throws XMLStreamException {
        Iterator<String> keys = binds.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next().toString();
            System.out.println("/" + id + key);
            String txtUrl = "url=";

            JsonNode bindObject = binds.get(key);

            out.writeCharacters("\n\t\t");
            out.writeStartElement("bind");
            out.writeAttribute("nodeset", "/" + id + key);

            if (bindObject.has("properties")) {
                JsonNode properties = bindObject.get("properties");
                if (properties.has("readonly")) {
                    out.writeAttribute("readonly", (properties.get("readonly")
                            .asBoolean()) ? "true()" : "false()");
                }
                if (properties.has("required")) {
                    out.writeAttribute("required", (properties.get("required")
                            .asBoolean()) ? "true()" : "false()");
                }
                if (properties.has("constraint")) {
                    JsonNode constraints = properties.get("constraint");
                    String constraint = "";
                    if (constraints.has("url")) {
                        constraint = txtUrl + constraints.get("url").asText();
                        System.out.println(constraints.get("url").asText());
                    }
                    if (constraints.has("depends")) {
                        constraint = constraint + " and depends=" + "/" + id
                                + "/" + constraints.get("depends").asText();
                    }
                    

                    String msg="Ingrese valor";
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==true)&&(properties.has("minValue"))){
	                    	constraint += ".>=" + properties.get("minValue").asText();
	                    	msg += " mayor o igual a " + properties.get("minValue").asText(); 
	                    }
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==false)&&(properties.has("minValue"))){
	                    	constraint += ".>" + properties.get("minValue").asText();
	                    	msg += " mayor a " + properties.get("minValue").asText(); 

	                    }
	                    
	                    if((properties.has("minValue"))&&(properties.has("maxValue"))){
	                    	constraint += " and ";
	                    	msg += " y";
	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==true)&&(properties.has("maxValue"))){
	                    	constraint += ".<=" + properties.get("maxValue").asText();
	                    	msg += " menor o igual a " + properties.get("maxValue").asText(); 

	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==false)&&(properties.has("maxValue"))){
	                    	constraint += ".<" + properties.get("maxValue").asText();
	                    	msg = " mayor a " + properties.get("maxValue").asText(); 

	                    }
	                    
                    	// Para String
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==true)&&(properties.has("minLength"))){
	                    	constraint += "string-length .>=" + properties.get("minLength").asText();
	                    	msg += " mayor o igual a " + properties.get("minLength").asText(); 
	                    }
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==false)&&(properties.has("minLength"))){
	                    	constraint += "string-length .>" + properties.get("minLength").asText();
	                    	msg += " mayor a " + properties.get("minLength").asText(); 

	                    }
	                    
	                    if((properties.has("minLength"))&&(properties.has("maxLength"))){
	                    	constraint += " and ";
	                    	msg += " y";
	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==true)&&(properties.has("maxLength"))){
	                    	constraint += "string-length .<=" + properties.get("maxLength").asText();
	                    	msg += " menor o igual a " + properties.get("maxLength").asText(); 

	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==false)&&(properties.has("maxLength"))){
	                    	constraint += "string-length .<" + properties.get("maxLength").asText();
	                    	msg = " mayor a " + properties.get("maxLength").asText(); 

	                    }
	                    
                    
                    System.out.println(constraint);
                    out.writeAttribute("constraint", constraint);
                    
                    System.out.println(constraint);
                    out.writeAttribute("jr:constraintMsg", msg);
                    
                }else{
                	//Logica para caso de constraint en int, decimal
                		String constraint ="";
                		String msg="Ingrese valor";
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==true)&&(properties.has("minValue"))){
	                    	constraint += ".>=" + properties.get("minValue").asText();
	                    	msg += " mayor o igual a " + properties.get("minValue").asText(); 
	                    }
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==false)&&(properties.has("minValue"))){
	                    	constraint += ".>" + properties.get("minValue").asText();
	                    	msg += " mayor a " + properties.get("minValue").asText(); 

	                    }
	                    
	                    if((properties.has("minValue"))&&(properties.has("maxValue"))){
	                    	constraint += " and ";
	                    	msg += " y";
	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==true)&&(properties.has("maxValue"))){
	                    	constraint += ".<=" + properties.get("maxValue").asText();
	                    	msg += " menor o igual a " + properties.get("maxValue").asText(); 

	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==false)&&(properties.has("maxValue"))){
	                    	constraint += ".<" + properties.get("maxValue").asText();
	                    	msg = " mayor a " + properties.get("maxValue").asText(); 

	                    }
	                    
	                    
                    	// Para String
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==true)&&(properties.has("minLength"))){
	                    	constraint += "string-length .>=" + properties.get("minLength").asText();
	                    	msg += " mayor o igual a " + properties.get("minLength").asText(); 
	                    }
	                    
	                    if((properties.has("minInclusive"))&&(properties.get("minInclusive").asBoolean()==false)&&(properties.has("minLength"))){
	                    	constraint += "string-length .>" + properties.get("minLength").asText();
	                    	msg += " mayor a " + properties.get("minLength").asText(); 

	                    }
	                    
	                    if((properties.has("minLength"))&&(properties.has("maxLength"))){
	                    	constraint += " and ";
	                    	msg += " y";
	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==true)&&(properties.has("maxLength"))){
	                    	constraint += "string-length .<=" + properties.get("maxLength").asText();
	                    	msg += " menor o igual a " + properties.get("maxLength").asText(); 

	                    }
	                    
	                    if((properties.has("maxInclusive"))&&(properties.get("maxInclusive").asBoolean()==false)&&(properties.has("maxLength"))){
	                    	constraint += "string-length .<" + properties.get("maxLength").asText();
	                    	msg = " mayor a " + properties.get("maxLength").asText(); 

	                    }
                  
                  System.out.println(constraint);
                  out.writeAttribute("constraint", constraint);
                  
                  System.out.println(constraint);
                  out.writeAttribute("jr:constraintMsg", msg);
                  
              }
                
                if (properties.has("relevant")) {
                    out.writeAttribute("relevant", " /" + id + "/"
                            + properties.get("relevant").asText());
                }
            }

            if (bindObject.has("type")) {
                String type = bindObject.get("type").asText();

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
     * Construye el tag head del archivo xml.
     *
     * @autor ltroconis
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param title
     *            titulo del formulario, es tomado del header.code
     * @param action
     *            el valor del action del formulario
     * @param tags
     *            lista de tags que van en la instancia
     *
     * @param binds
     *            mapa que tiene los binds
     * @param out
     *            el writerString del xml
     *
     * @throws XMLStreamException
     *             error construyendo el xml
     */
    private void writeXMLHead(final String id, final String title,
            final String action, final ArrayList<TagInstancia> tags,
            final Map<String, JsonNode> binds, final XMLStreamWriter out)
            throws XMLStreamException {
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

    /**
     * Crea el XML a partir de un objeto JSon.
     *
     * @autor ltroconis
     * @see com.googlecode.json-simple
     * @param title
     *            titulo del formulario, es tomado del header.code
     * @param action
     *            el valor del action del formulario
     * @param id
     *            el id del formulario, es tomado del header.code
     * @param tooltip
     *            lista de tags que van en la instancia
     * @param components
     *            el objeto json que representa el tag components
     * @param outputStream
     *            el OutputStream del xml
     *
     * @throws Exception
     *             que puede provenir del XMLStreamWriter y del
     *             OutputStreamWriter
     */
    public final void createXML(final String title, final String action,
            final String id, final String tooltip, final JsonNode components,
            final OutputStream outputStream) throws Exception {

        XMLStreamWriter out = XMLOutputFactory.newInstance()
                .createXMLStreamWriter(
                        new OutputStreamWriter(outputStream, "utf-8"));

        System.out.println(components);

        ArrayList<TagInstancia> tags = new ArrayList<TagInstancia>();
        Map<String, JsonNode> binds = new HashMap<String, JsonNode>();
        getJsonInstanceAndBindsObject("", components, tags, binds);

        writeXMLFileHead(out);

        writeXMLHead(id, title, action, tags, binds, out);

        /************* CUERPO DEL XML ***************/

        out.writeCharacters("\n\t");
        out.writeStartElement("h:body");

        jsonParserObject("", components, id, out);

        out.writeCharacters("\n\t");
        /* h:body */out.writeEndElement();

        out.writeCharacters("\n");
        /* h:html */out.writeEndElement();
        out.writeEndDocument();

        out.close();
    }

    // Persistimos en base y genermamos retornamos codigo que se utiliza para
    // visualizar el documento
    public String persistFormFromJson(String jsonValue)
            throws Exception {

        // Creamos y llenamos los atributos del nuevo formulario a persistir
        Formulario formulario = new Formulario();

        ObjectMapper mapper = new ObjectMapper();

        try {
           JsonNode jsonObject = mapper.readTree(jsonValue);

           JsonNode header = jsonObject.get("header");
           String code = header.get("code").asText();
           //obtener los parametros de repeat:
           //JsonNode urlParmas = jsonObject.get("header");

            formulario.setCodigo(header.get("code").asText());
            formulario.setNombre(header.get("name").asText());
//            formulario.setVersion(Integer.parseInt(header.get("version").asText()));
            formulario.setFormVersion(header.get("version").asText());
//            formulario.set(header.get("status").asText());
//            formulario.set(header.get("descrip").asText());
            
            String xmlString = jsonToXForm(jsonValue);

            String path = frp.getDestinationXml();

            try {
                File xmlToSave = new File(path
                        + System.getProperty("file.separator") + jsonObject.get("_id").asText() + ".xml");
                BufferedWriter output = new BufferedWriter(new FileWriter(
                        xmlToSave));
                output.write(xmlString);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            formulario.setArchivo(jsonObject.get("_id").asText() + ".xml");
            formulario.setUrl(path);
            formulario.setParametrosUrl("id="+ formulario.getCodigo() + "&repeat=1");
            Xsl xsl = entityManager.find(Xsl.class, 1L);
            System.out.println("Xsl encontrado: " + xsl.getNombre());
            formulario.setXslTransform(xsl);
            formulario.setFormVersion(header.get("version").asText());
            entityManager.persist(formulario);

        } catch (Exception e) {
            throw new Exception("Error en conversión de JSON  a XML", e);
        }

        return formulario.getCodigo();
    }
    
    public String previewExample(final InputStream input)
            throws Exception {

    	String str = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
    	String linea;
    	
    	StringBuilder sb = new StringBuilder();
    	
		while ((linea = br.readLine()) != null) {
			sb.append(linea);
		}
    	
		str = sb.toString();
		
        return str;
    }
    
	public String obtenerJson(String resp, String formId) {

		JSONObject ojson = new JSONObject();
		String header = obtenerHeader(formId);

		try {
			ojson = new JSONObject(resp);

			resp = header;
			for (int i = 0; i < 2; i++) {
				resp += "{" + obtenerNodo(ojson) + "},";
			}
			resp = resp.substring(0, resp.lastIndexOf(",")) + "]}}";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}

    public String obtenerNodo(final JSONObject ojson)
            throws Exception {

    	String str = "";
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode node;
    	JsonNode requeridos;
    	JsonNode datos;
    	
    	try{
    		node = mapper.readTree(ojson.toString());
    		
        	
        	node = node.get("properties").get("formulario").get("properties").get("formularios").get("items").get("properties").get("data").get("items");
            
        	requeridos = node.get("required");
        	
        	datos = node .get("properties");
        	
        	str = generarDatos(datos,requeridos);
        	
    	}catch (Exception e) {
            throw new Exception("Error al cargar el Json Schema", e);
        }

    	return str;
    }

	private String generarDatos(JsonNode datos, JsonNode requeridos) {

		String nuevoDato = "\"data\": [";
		String str = null;
		int cantInstancias;
		
		for (cantInstancias = 0; cantInstancias < 1; cantInstancias++) {
			
			Iterator<JsonNode> items = datos.elements();
			Iterator<String> namesItems = datos.fieldNames();
			if(cantInstancias==0)
				nuevoDato += "{\""+ "instance\"" + "  :  " + 0 + ", ";
			else nuevoDato += ",{\""+ "instance\"" + "  :  " + 0 + ", ";
			if (datos.get("item") != null) {
				
				nuevoDato += "\"" + "item\"" + "  : " + "\"\"" + ", ";
			}

			while (items.hasNext()) {

				JsonNode item = items.next();
				String name = namesItems.next();
				
				if (item.get("title") != null) {
					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/string") == 0) {
						str = "\"" + name + "\"  : \"" + ""
								+ "\", ";
					}
					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/integer") == 0) {
						str = "\"" + name + "\"  : " + 0
								+ ", ";
					}
					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/decimal") == 0) {
						str = "\"" + name + "\"  : " + 0.0
								+ ", ";
					}
					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/combo") == 0) {
						str = "\"" + name + "\"  : {\"label\" : \"" + ""
								+ "\", \"value\"  : \"" + "" + "\"}, ";
					}
					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/time") == 0) {
						str = "\"" + name + "\"  : " + "00:00"
								+ ", ";
					}
					
					nuevoDato += str;
				}
			}
			nuevoDato = nuevoDato.substring(0, nuevoDato.lastIndexOf(",")) +"}";
		}
		
		str = nuevoDato + "]";
		
		return str;
	}
//	private String generarDatos(JsonNode datos, JsonNode requeridos) {
//
//		String nuevoDato = "\"data\": [";
//		String str = null;
//		int cantInstancias;
//		
//		for (cantInstancias = 0; cantInstancias < 2; cantInstancias++) {
//			
//			Iterator<JsonNode> items = datos.elements();
//			Iterator<String> namesItems = datos.fieldNames();
//			if(cantInstancias==0)
//				nuevoDato += "{\""+ "instance\"" + "  :  " + cantInstancias + ", ";
//			else nuevoDato += ",{\""+ "instance\"" + "  :  " + cantInstancias + ", ";
//			if (datos.get("item") != null) {
//				
//				nuevoDato += "\"" + "item\"" + "  : " + "\"Nombre Mes\"" + ", ";
//			}
//
//			while (items.hasNext()) {
//
//				JsonNode item = items.next();
//				String name = namesItems.next();
//				
//				if (item.get("title") != null) {
//					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/string") == 0) {
//						str = "\"" + name + "\"  : \"" + "String"
//								+ "\", ";
//					}
//					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/integer") == 0) {
//						str = "\"" + name + "\"  : \"" + "Numero Entero"
//								+ "\", ";
//					}
//					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/decimal") == 0) {
//						str = "\"" + name + "\"  : \"" + "Numero Decimal Con Punto"
//								+ "\", ";
//					}
//					if (item.get("$ref").asText().compareTo("formulario.json#/definitions/combo") == 0) {
//						str = "\"" + name + "\"  : {\"label\" : \"" + "Nombre"
//								+ "\", \"value\"  : \"" + "Codigo" + "\"}, ";
//					}
//					
//					nuevoDato += str;
//				}
//			}
//			nuevoDato = nuevoDato.substring(0, nuevoDato.lastIndexOf(",")) +"}";
//		}
//		
//		str = nuevoDato + "]";
//		
//		return str;
//	}

	public String obtenerHeader(String formId) {
		
		String header = new String("{\"formulario\": {\"id\": \"" + formId + "\", \"formularios\": [");
		
		return header;
	}
	
public String repeat(String tab, int count){
		
		String a = "";
		
		for (int i=0;i<count;i++)
			a+=tab;
		return a.toString();
	}
	
	public String formatJson(String json){
		
		int i= 0, il = 0,indentLevel = 0;
		char currentChar;
		Boolean inString= false;
		String tab= "	",newJson= "";
		
		for (i = 0, il = json.length(); i < il; i++) { 
            currentChar = json.charAt(i);
            switch (currentChar) {
            case '{': 
            case '[': 
                if (!inString) { 
                    newJson += currentChar + "\n" + repeat(tab, indentLevel + 1);
                    indentLevel += 1; 
                } else { 
                    newJson += currentChar; 
                }
                break; 
            case '}': 
            case ']': 
                if (!inString) { 
                    indentLevel -= 1; 
                    newJson += "\n" + repeat(tab, indentLevel) + currentChar; 
                } else { 
                    newJson += currentChar; 
                } 
                break; 
            case ',': 
                if (!inString) { 
                    newJson += ",\n" + repeat(tab, indentLevel); 
                } else { 
                    newJson += currentChar; 
                } 
                break; 
            case ':': 
                if (!inString) { 
                    newJson += ": "; 
                } else { 
                    newJson += currentChar; 
                } 
                break; 
            case ' ':
            case '\n':
            case '\t':
                if (inString) {
                    newJson += currentChar;
                }
                break;
            case '"': 
                if (i > 0 && json.charAt(i - 1) != '\\') {
                    inString = !inString; 
                }
                newJson += currentChar; 
                break;
            default: 
                newJson += currentChar; 
                break;                    
            } 
        } 

        return newJson;
	}
	

}
