package ar.com.cuyum.cnc.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import ar.com.cuyum.cnc.domain.jsonsla.Formulario;
import ar.com.cuyum.cnc.domain.jsonsla.Row;
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
			String msg = "Error procesando json, formato invalido";
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
			String msg = "Error procesando esquemas, el jsonschema está mal formado";
			log.error(msg, e);
			return JsonUtils.msg(false, msg);
		} catch (IOException e) {
			String msg = "Error procesando esquemas, error de E/S jsonschema";
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
		schemas.put("schema-url", schemaPath);
		schemas.put("schema-form-url", schemaFormPath);
		return schemas;
	}

	private static ObjectNode processingMessagesReport(ProcessingReport report) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode msg = mapper.createObjectNode();
		ObjectNode error = mapper.createObjectNode();
		Iterator<ProcessingMessage> messages = report.iterator();
		while (messages.hasNext()) {
			ProcessingMessage msj = messages.next();
			if (LogLevel.ERROR.equals(msj.getLogLevel())) {
				error.put(msj.asJson().get("instance").get("pointer").asText(),
						msj.getMessage());
			}
		}
		msg.put("errores", error);
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

			try {
				formulario.processData();
			} catch (ExceptionValidation e) {
				log.error(e);
				return msg(false,
						"error procesando data masiva en el formulario:(" + j
								+ ") " + e.getMessage());
			} 

			listDataForm.add(formulario.valuesToJson());
		}

		ObjectNode response = mapper.createObjectNode();
		response.put("id", idForm);
		response.put("listDataForm", listDataForm);

		return JsonUtils.msg(true, response.toString());
	}

}
