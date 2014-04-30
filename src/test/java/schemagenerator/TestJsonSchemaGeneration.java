//package schemagenerator;
//
//import static org.fest.assertions.api.Assertions.assertThat;
//
//import java.io.File;
//import java.net.URI;
//
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import ar.com.cuyum.cnc.utils.JsonShemaGenerator;
//
//public class TestJsonSchemaGeneration {
//	private static Logger log = Logger
//			.getLogger(TestJsonSchemaGeneration.class);
//
//
//	@BeforeClass
//	public static void startup() {
//		if (!log.isDebugEnabled()) {
//			BasicConfigurator.configure();
//		}
//
//		// creo el directorio de salida para los test si no existe
//		File output = new File(TestJsonSchemaGeneration.class.getResource(
//				"/formularios/output").getPath());
//		if (output.exists()) {
//			// borro los archivos para la salida si existen
//			File[] files = output.listFiles();
//			for (int i = 0; i < files.length; i++) {
//				if (files[i].getName().contains("-schema.json")) {
//					files[i].delete();
//				}
//			}
//		}
//	}
//
//	@Before
//	public void initTest() {
//	}
//
//	@Test
//	public final void testDummy() {
//		assertThat(true).isTrue();
//	}
//
//	/**
//	 * Recordar que el output es en
//	 * FormRender/target/test-classes/formularios/schemas.
//	 */
//	@Test
//	public final void testGenerateJsonSchema() throws Exception {
//
//		log.info("Creando esquemas");
//
//		File schemasDir = new File(this.getClass()
//				.getResource("/formularios/output").getPath());
//
//		// El directorio no tiene esquemas
//		assertThat(schemasDir.listFiles().length).isLessThanOrEqualTo(1);
//
//		// Se generan los esquemas a partir de los xml JsonShemaGenerator
//		JsonShemaGenerator jsonShemas = new JsonShemaGenerator();
//
//		File xmlDir = new File(this.getClass().getResource("/formularios/xml")
//				.getPath());
//		jsonShemas.doSchemasFromDir(xmlDir.getPath(), schemasDir.getPath());
//
//		// El directorio tiene los esquemas generador
//		assertThat(schemasDir.listFiles().length).isGreaterThan(0);
//
//		// Se generaron tantos esquemas como xml hay
//		assertThat(xmlDir.list().length).isEqualTo(
//				schemasDir.listFiles().length-1);
//
//	}
//
//}
