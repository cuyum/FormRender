package schemagenerator;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingReport;

public class TestJsonValidator {
	private static Logger log = Logger.getLogger(TestJsonValidator.class);

	static URI dir_actual = URI.create(System.getProperty("user.dir"));

	@BeforeClass
	public static void startup() {
		BasicConfigurator.configure();

	}

	@Test
	public final void testDummy() {
		assertThat(true).isTrue();
	}

	@Test
	public final void test_C1_1_OK1() throws JsonProcessingException,
			FileNotFoundException, IOException, ProcessingException {

		log.info("C1.1 Ok1");		
		
		String actualPath = "file:///" + this.getClass().getResource("..").getPath(),
			       schemaPath = actualPath+"formularios/schemas/C1.1-schema.json",
			       schemaGeneralPath = actualPath+"formularios/schemas/formulario.json";
		
		final LoadingConfigurationBuilder builder = LoadingConfiguration
				.newBuilder();
		
		ObjectMapper mapper = new ObjectMapper();

		final JsonNode schemaNode = mapper.readTree(this.getClass()
				.getResourceAsStream("/formularios/schemas/C1.1-schema.json"));
		
		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
				.getResourceAsStream("/formularios/schemas/formulario.json"));
		
		builder.preloadSchema(schemaPath, schemaNode);
		builder.preloadSchema(schemaGeneralPath, schemaFormNode);

		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
				.setLoadingConfiguration(builder.freeze()).freeze();

		final JsonNode data = mapper.readTree(this.getClass().getResourceAsStream("/formularios/data/C1.1_OK1.json"));
		
		final JsonSchema schema = factory.getJsonSchema(schemaPath);

		ProcessingReport report = schema.validate(data);
		
		log.debug(report);

		assertThat(report.isSuccess()).isTrue();		
	}
	
	@Test
	public final void test_C1_1_OK2() throws JsonProcessingException,
			FileNotFoundException, IOException, ProcessingException {

		log.info("C1.1 Ok2");		
		
		String actualPath = "file:///" + this.getClass().getResource("..").getPath(),
			       schemaPath = actualPath+"formularios/schemas/C1.1-schema.json",
			       schemaGeneralPath = actualPath+"formularios/schemas/formulario.json";
		
		final LoadingConfigurationBuilder builder = LoadingConfiguration
				.newBuilder();
		
		ObjectMapper mapper = new ObjectMapper();

		final JsonNode schemaNode = mapper.readTree(this.getClass()
				.getResourceAsStream("/formularios/schemas/C1.1-schema.json"));
		
		final JsonNode schemaFormNode = mapper.readTree(this.getClass()
				.getResourceAsStream("/formularios/schemas/formulario.json"));
		
		builder.preloadSchema(schemaPath, schemaNode);
		builder.preloadSchema(schemaGeneralPath, schemaFormNode);

		final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
				.setLoadingConfiguration(builder.freeze()).freeze();

		final JsonNode data = mapper.readTree(this.getClass().getResourceAsStream("/formularios/data/C1.1_OK2.json"));
		
		final JsonSchema schema = factory.getJsonSchema(schemaPath);

		ProcessingReport report = schema.validate(data);
		
		log.debug(report);

		assertThat(report.isSuccess()).isTrue();		
	}

}
