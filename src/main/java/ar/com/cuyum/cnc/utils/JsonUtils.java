package ar.com.cuyum.cnc.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;

import ar.com.cuyum.cnc.domain.jsonsla.Formulario;
import ar.com.cuyum.cnc.exceptions.ExceptionValidation;
import ar.com.cuyum.cnc.service.RelayService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;


/**
 * 
 * @author ltroconis
 * 
 */
public class JsonUtils {
	public transient static Logger log = Logger.getLogger(JsonUtils.class);

	public static JsonNode msg(Boolean success, String msg) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode msgJson = mapper.createObjectNode();
		msgJson.put("success", success);
		msgJson.put("msg", msg);
		return msgJson;
	}

	/**
	 * valida que el string es un formato json valido
	 * 
	 * @param json
	 * @return
	 */
	private static JsonNode doJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObject = mapper.createObjectNode();

		try {
			jsonObject = mapper.readTree(json);
			json = jsonObject.toString();
		} catch (JsonProcessingException e2) {
			String msg = "Error procesando json, formato invalido " + "- Línea: " + (e2.getLocation().getLineNr());
			log.error(msg, e2);
			return JsonUtils.msg(false, msg);
		} catch (IOException e2) {
			String msg = "Error E/S procesando json";
			log.error(e2);
			return JsonUtils.msg(false, msg);
		}

		return jsonObject;
	}

	/**
	 * Carga los json schemas
	 * 
	 * @param schema
	 * @param formulario
	 * @return
	 */
	private static JsonNode getJsonSchemasObjectFromURL(URL schema,
			URL formulario) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode schemas = mapper.createObjectNode();

		try {
			schemas.put("schema", mapper.readTree(schema));
			schemas.put("schema-form", mapper.readTree(formulario));
		} catch (JsonProcessingException e) {
			String msg = "Error procesando esquemas, el jsonschema está mal formado - " + "Línea: " + e.getLocation().getLineNr();
			log.error(msg, e);
			return JsonUtils.msg(false, msg);
		} catch (IOException e) {
			String msg = "Error procesando esquemas, error de E/S jsonschema - Verifique el id del formulario";
			log.error(msg, e);
			return JsonUtils.msg(false, msg);
		}

		return schemas;

	}

	private static JsonNode getJsonSchemasFromPath(String schemaPath,
			String schemaFormPath) {
		URL urlSchema, urlFormulario;
		try {
			urlSchema = new URL(schemaPath);
			urlFormulario = new URL(schemaFormPath);
		} catch (MalformedURLException e) {
			String msg = "Error checkeando json, url de jsonschema mal formado";
			log.error(msg, e);
			return msg(false, msg);
		}
		ObjectNode schemas = (ObjectNode) getJsonSchemasObjectFromURL(
				urlSchema, urlFormulario);

		if(schemas.get("success")!=null){
			if(schemas.get("success").asBoolean()){
				schemas.put("schema-url", schemaPath);
				schemas.put("schema-form-url", schemaFormPath);
			}
		}else {
			schemas.put("schema-url", schemaPath);
			schemas.put("schema-form-url", schemaFormPath);
		}
		return schemas;
	}

	private static ObjectNode processingMessagesReport(ProcessingReport report) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode msg = mapper.createObjectNode();
		ObjectNode error = mapper.createObjectNode();
		String msje,prop="";
		Iterator<ProcessingMessage> messages = report.iterator();
		while (messages.hasNext()) {
			ProcessingMessage msj = messages.next();
			if (LogLevel.ERROR.equals(msj.getLogLevel())) {
				
				msje = msj.getMessage();
				int keymsge=0;
				
				if(msje.indexOf("properties which are not allowed")!=-1){
					keymsge = 1;
					if(msje.indexOf("[")!=-1){
						prop = msje.substring(msje.indexOf("["), msje.indexOf("]")+1);
					}
				}
					
				if(msje.indexOf("required properties")!=-1){
					keymsge = 2;
					if(msje.indexOf("[")!=-1){
						prop = msje.substring(msje.indexOf("["), msje.indexOf("]")+1);
					}
				}
				if(msje.indexOf("not contain duplicate elements")!=-1)
					keymsge = 3;
				if(msje.indexOf("instance type")!=-1){
					keymsge = 4;
					if(msje.indexOf("[")!=-1){
						prop = msje.substring(msje.indexOf("["), msje.indexOf("]")+1);
					}
				}				
				if(msje.indexOf("array is too short")!=-1)
					keymsge = 5;
				
				msje = searchError(keymsge);
				
				if(!msje.equals(""))
					error.put(msj.asJson().get("instance").get("pointer").asText(),
						msje+prop);
				else error.put(msj.asJson().get("instance").get("pointer").asText(),
						msj.getMessage());
			}
		}
		msg.put("errores", error);
		return msg;
	}

	private static String searchError(int msj) {
		String msg="";
		
		switch (msj) {
		case 1:
			msg="El objeto tiene propiedades no permitidas por el schema:";
			break;
		case 2:
			msg="El objeto requiere la propiedad:";
			break;
		case 3:
			msg="El formulario no puede tener registros duplicados";
			break;
		case 4:
			msg="Error de tipo de dato - Tipo permitido:";
			break;
		case 5:
			msg="Debe tener al menos un registro en data";
			break;

		default:
			break;
		}
		
//		final ClassLoader loader = JsonUtils.class.getClassLoader();
//		
//		final String FILE_NAME = "message.txt";
//		
//		try{
//            FileInputStream fstream = new FileInputStream(loader.getResource(FILE_NAME).getPath());
//            DataInputStream entrada = new DataInputStream(fstream);
//            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));
//            String strLinea;
//           
//            while ((strLinea = buffer.readLine()) != null)   {
//                String msgIng = strLinea.substring(0, strLinea.indexOf('='));
//                String msgEsp = strLinea.substring(strLinea.indexOf('=')+1);
//            	
//                if(msj.trim().equals(msgIng.trim())){
//                	msg=msgEsp;
//                }
//            }
//            
//            entrada.close();
//        }catch (Exception e){ //Catch de excepciones
//            System.err.println("-----------------Ocurrio un error: " + e.getMessage());
//        }
		return msg;
	}
	
	public static JsonNode checkSchema(JsonNode schemas, JsonNode data) {

		final LoadingConfigurationBuilder builder = LoadingConfiguration
				.newBuilder();
		builder.preloadSchema(schemas.get("schema-url").asText(),
				schemas.get("schema"));
		builder.preloadSchema(schemas.get("schema-form-url").asText(),
				schemas.get("schema-form"));

		log.debug(schemas.get("schema"));

		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
				.setLoadingConfiguration(builder.freeze()).freeze();

		JsonSchema schema;
		ProcessingReport report;

		try {
			schema = factory.getJsonSchema(schemas.get("schema-url").asText());
			report = schema.validate(data);
		} catch (ProcessingException e) {
			String msg = "Error checkeando json, url de jsonschema mal formado";
			log.error(msg + e.getProcessingMessage().asJson());
			return msg(false, msg);
		}

		if (!report.isSuccess()) {
			ObjectNode msg = processingMessagesReport(report);
			return JsonUtils.msg(false, msg.toString());
		}

		return JsonUtils.msg(report.isSuccess(), schemas.get("schema")
				.toString());
	}

	private static String getActualPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":"
				+ request.getServerPort() + "/"
				+ request.getServletContext().getServletContextName();
	}

	private static String getPathSChemaURL(HttpServletRequest request,
			String idForm) {
		return getActualPath(request) + "/schemas/" + idForm + "-schema.json";
	}

	private static String getPathSChemaFormURL(HttpServletRequest request) {
		return getActualPath(request) + "/schemas/formulario.json";
	}
	
	public JsonNode proccessDataValidation(int formPosition, JsonNode formNode,
			JsonNode schemaItemsDescription, String idForm,
			RelayService relayService) {

		// si ya pasó las validaciones de jsonschema se pasa a la segunda
		// etapa de validación
		// del formulario

		Formulario formulario = new Formulario(idForm, schemaItemsDescription,
				relayService);
		ArrayNode data = (ArrayNode) formNode.get("data");
		formulario.addDataFromJson(data);
		log.info(formulario.valuesToJson());

		if (formNode.has("recordId")) {
			String recordId = formNode.get("recordId").asText();
			formulario.setRecordId(recordId);
		}

		if (formulario.validationPrimaryKey()) {
			return msg(false,
					"ERROR claves primarias duplicadas en formulario:("
							+ formPosition + ")");
		}
		try {
			formulario.processData();
		} catch (ExceptionValidation e) {
			log.error(e);
			return msg(false, "ERROR procesando datos en formulario:("
					+ formPosition + ")" + e.getMessage());
		}

		return formulario.valuesToJson();
	};
	
	private JsonNode getSchemaItemsDescription(JsonNode schemas) {
		// se obtienen los campos del formulario a partir del json schema, para
		// ser validados
		JsonNode formularios = schemas.get("schema").get("properties")
				.get("formulario").get("properties").get("formularios")
				.get("items").get("properties");
		JsonNode schemaItemsDescription = formularios.get("data").get("items");

		return schemaItemsDescription;
	}
	
	public JsonNode proccessDataValidation(JsonNode validJson,
  			HttpServletRequest request, RelayService relayService,
  			FormRenderProperties frp) {
		
		ObjectMapper mapper = new ObjectMapper();
  		// if el json no es un json valido y por eso no fue creado
  		if (validJson.has("success") && !validJson.get("success").asBoolean()){
  			return validJson;
  		}	
  		// formulario
  		if (!validJson.has("formulario")|| !validJson.get("formulario").has("id")) {
			return JsonUtils.msg(false,	"Json Invalido, no posee id de formulario");
  		}
  
  		String idForm = validJson.get("formulario").get("id").asText();
  
		String schemaPath = JsonUtils.getPathSChemaURL(request,idForm);
  		String schemaFormPath = JsonUtils.getPathSChemaFormURL(request);
  		JsonNode schemas = getJsonSchemasFromPath(schemaPath, schemaFormPath);
  
  		// si ya pasó las validaciones de jsonschema se pasa a la segunda etapa
  		// de validación
  		// de cada formulario
		ArrayNode listForms = (ArrayNode) validJson.get("formulario").get("formularios");
  
		//ArrayNode listDataForm = mapper.createArrayNode();
  
		JsonNode schemaItemsDescription = getSchemaItemsDescription(schemas);
		
  		for (int j = 0, n = listForms.size(); j < n; j++) {
  
  			JsonNode formNode = listForms.get(j);
			JsonNode formResult = proccessDataValidation(j,formNode,schemaItemsDescription,idForm,relayService);
  			
			if(formResult.has("success") && formResult.get("success").asBoolean()==false){
				return formResult;
  			}
			listForms.add(formResult);
			listForms.remove(j);
  		}
  
  		ObjectNode response = mapper.createObjectNode();
  		response.put("id", idForm);
		response.put("listDataForm", listForms);
  
		return response; 
  	}

	//FIXME:Lo usa JsonBean por eso no lo borre, hay que ver para que y cambiarlo
	public JsonNode proccessDataValidation(String submit_data,
			HttpServletRequest request, RelayService relayService,
			FormRenderProperties frp) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode validJson = doJson(submit_data);

		// if el json no es un json valido y por eso no fue creado
		if (validJson.has("success") && !validJson.get("success").asBoolean())
			return validJson;

		// si el json no posee la entrada formulario o no tiene el id del
		// formulario
		if (!validJson.has("formulario")
				|| !validJson.get("formulario").has("id")) {
			return JsonUtils.msg(false,
					"Json Invalido, no posee id de formulario");
		}

		String idForm = validJson.get("formulario").get("id").asText();

		String schemaPath = JsonUtils.getPathSChemaURL(request, idForm);
		String schemaFormPath = JsonUtils.getPathSChemaFormURL(request);
		JsonNode schemas = getJsonSchemasFromPath(schemaPath, schemaFormPath);

		// si no se pudo contruir el objeto de los schemas
		if (schemas.has("success") && !schemas.get("success").asBoolean()) {
			return schemas;
		}

		JsonNode success = checkSchema(schemas, validJson);

		// si no es un json data valido segun el json schema correspondiente
		if (!success.get("success").asBoolean())
			return success;

		// si ya pasó las validaciones de jsonschema se pasa a la segunda etapa
		// de validación
		// de cada formulario
		ArrayNode listForms = (ArrayNode) validJson.get("formulario").get(
				"formularios");

		// se obtienen los campos del formulario a partir del json schema, para
		// ser validados
		JsonNode formularios = schemas.get("schema").get("properties")
				.get("formulario").get("properties").get("formularios")
				.get("items").get("properties");

		JsonNode schemaItemsDescription = formularios.get("data").get("items");

		ArrayNode listDataForm = mapper.createArrayNode();

		for (int j = 0, n = listForms.size(); j < n; j++) {

			JsonNode formNode = listForms.get(j);

			// si ya pasó las validaciones de jsonschema se pasa a la segunda
			// etapa de validación
			// del formulario

			Formulario formulario = new Formulario(idForm,
					schemaItemsDescription, relayService);
			ArrayNode data = (ArrayNode) formNode.get("data");
			formulario.addDataFromJson(data);
			log.info(formulario.valuesToJson());
			
			if(formNode.has("recordId")){
				String recordId =  formNode.get("recordId").asText();
				formulario.setRecordId(recordId);
			}
	
			if (formulario.validationPrimaryKey()) {
				return msg(false,
						"ERROR claves primarias duplicadas en formulario:(" + j
								+ ")");
			}
			try {
				formulario.processData();
			} catch (ExceptionValidation e) {
				log.error(e);
				return msg(false,
						"ERROR procesando datos en formulario:(" + j
								+ ")" + e.getMessage());
			} 
			
			

			listDataForm.add(formulario.valuesToJson());
		}

		ObjectNode response = mapper.createObjectNode();
		response.put("id", idForm);
		response.put("listDataForm", listDataForm);

		return JsonUtils.msg(true, response.toString());
	}

}
