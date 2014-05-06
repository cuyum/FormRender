//package schemagenerator;
//
//import static org.fest.assertions.api.Assertions.assertThat;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Iterator;
//
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.fge.jsonschema.exceptions.ProcessingException;
//import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
//import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
//import com.github.fge.jsonschema.main.JsonSchema;
//import com.github.fge.jsonschema.main.JsonSchemaFactory;
//import com.github.fge.jsonschema.report.LogLevel;
//import com.github.fge.jsonschema.report.ProcessingMessage;
//import com.github.fge.jsonschema.report.ProcessingReport;
//
//public class TestJsonValidator {
//	private static Logger log = Logger.getLogger(TestJsonValidator.class);
//
//	@BeforeClass
//	public static void startup() {
//		if (log.isTraceEnabled()) {
//			log.info("log configurado");
//		} else {
//			BasicConfigurator.configure();
//		}
//	}
//
//	@Test
//	public final void testDummy() {
//		assertThat(true).isTrue();
//	}
//
//	@Test
//	public final void test_C1_1_OK1() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.1-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.1-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.1_OK1.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.1 está Ok en 1er test ****");
//	}
//
//	@Test
//	public final void test_C1_1_OK2() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.1-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.1-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.1_OK2.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.1 está Ok en 2do test ****");
//	}
//
//	@Test
//	public final void test_C1_3_OK1() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.3-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.3-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.3_OK1.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.3 está Ok en 1er test ****");
//	}
//	
//	@Test
//	public final void test_C1_3_OK2() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.3-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.3-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.3_OK2.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.3 está Ok en 2do test ****");
//	}
//	
//	@Test
//	public final void test_C1_10_OK1() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.10-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.10-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.10_OK1.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.10 está Ok en 1er test ****");
//
//		assertThat(report.isSuccess()).isTrue();		
//	}
//	
//	@Test
//	public final void test_C1_10_OK2() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.10-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.10-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.10_OK2.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.10 está Ok en 2do test ****");
//
//		assertThat(report.isSuccess()).isTrue();		
//	}
//	  
//	 
//	@Test
//	public final void test_C1_14_OK1() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.14-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.14-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.14_OK1.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.14 está Ok en 1er test ****");
//
//		assertThat(report.isSuccess()).isTrue();
//	}
//	
//	@Test
//	public final void test_C1_14_OK2() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.14-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.14-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.14_OK2.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.14 está Ok en 2do test ****");
//
//		assertThat(report.isSuccess()).isTrue();
//	}
//	  
//	@Test
//	public final void test_C1_15_OK() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.15-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.15-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.15_OK1.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.15 está Ok en 1er test ****");
//
//		assertThat(report.isSuccess()).isTrue();
//		
//	}
//	
//	@Test
//	public final void test_C1_15_OK2() throws JsonProcessingException,
//			FileNotFoundException, IOException, ProcessingException {
//
//		String actualPath = "file:///"
//				+ this.getClass().getResource("..").getPath(), schemaPath = actualPath
//				+ "formularios/schemas/C1.15-schema.json", schemaGeneralPath = actualPath
//				+ "formularios/schemas/formulario.json";
//
//		final LoadingConfigurationBuilder builder = LoadingConfiguration
//				.newBuilder();
//
//		ObjectMapper mapper = new ObjectMapper();
//
//		final JsonNode schemaNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/C1.15-schema.json"));
//
//		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/schemas/formulario.json"));
//
//		builder.preloadSchema(schemaPath, schemaNode);
//		builder.preloadSchema(schemaGeneralPath, schemaFormNode);
//
//		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
//				.setLoadingConfiguration(builder.freeze()).freeze();
//
//		final JsonNode data = mapper.readTree(this.getClass()
//				.getResourceAsStream("/formularios/data/C1.15_OK2.json"));
//
//		final JsonSchema schema = factory.getJsonSchema(schemaPath);
//
//		ProcessingReport report = schema.validate(data);
//
//		Iterator<ProcessingMessage> messages = report.iterator();
//		while (messages.hasNext()) {
//			ProcessingMessage msj = messages.next();
//			if (LogLevel.ERROR.equals(msj.getLogLevel())) {				
//				log.debug(msj.getMessage()+" >>"+msj.asJson().get("instance").get("pointer").asText());
//			}
//		}
//
//		assertThat(report.isSuccess()).isTrue();
//
//		log.info("**** C1.15 está Ok en 2do test ****");
//
//		assertThat(report.isSuccess()).isTrue();
//		
//	}
//	 
//}
