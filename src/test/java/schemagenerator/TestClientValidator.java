package schemagenerator;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
//import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
//import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
//import com.github.fge.jsonschema.main.JsonSchema;
//import com.github.fge.jsonschema.main.JsonSchemaFactory;
//import com.github.fge.jsonschema.report.LogLevel;
//import com.github.fge.jsonschema.report.ProcessingMessage;
//import com.github.fge.jsonschema.report.ProcessingReport;

@SuppressWarnings("deprecation")
public class TestClientValidator {
//	private static Logger log = Logger.getLogger(TestClientValidator.class);
//
//	@BeforeClass
//	public static void startup() {		
//		//BasicConfigurator.configure() 
//		//org.apache.http.wire=ERROR
//	}
//
//	@Test
//	public final void testDummy() {
//		assertThat(true).isTrue();
//		log.info("test dummy okey");
//	}
//	
//	@Test
//	public final void test_massiveService()  {
//
//		HttpPost method = null;
//		try {
//			log.info("Ejecutando test test_massiveService");
//
//			log.info("Obteniendo archivo");
//			File file = new File("/home/jorge/Trabajo_Lisi/git/FormRender/src/test/resources/formularios/data/ejemplo.json");
//		    //File file = new File("/home/jorge/Trabajo_Lisi/git/FormRender/src/test/resources/formularios/data/e1_1_12_1.json");
//		    
//			log.info("Configurando cliente con la url de carga masiva");
//		    HttpClient client = new DefaultHttpClient();
//		  	method = new HttpPost("http://localhost:8080/FormRender/rest/service/submit/massive");
//		  	
//			FileEntity fileEntity = new FileEntity(file,ContentType.APPLICATION_JSON);
//			fileEntity.setChunked(true);
//			method.setEntity(fileEntity);
//			
//			 
//			method.addHeader("Content-Type","application/json");
//
//			log.info("Ejecutando post");
//			HttpResponse response = client.execute(method);
//
//			log.info("Obteniendo respuesta y mostrandola");
//			BufferedReader rd = new BufferedReader(new InputStreamReader(
//					response.getEntity().getContent()));
//
//			String line = "";
//			while ((line = rd.readLine()) != null) {	
//				log.info(line);
////				ObjectMapper mapper2 = new ObjectMapper();
////				JsonNode node2 = mapper2.readTree(line);
////				if (node2.has("success")
////						&& node2.get("success").asBoolean() == false) {
////					log.info(node2.get("msg").asText());
////				} else {
////					log.info(node2);
////				}
//			}
//
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			// Release the connection.
//			if(method!=null)
//				method.releaseConnection();
//		}
//
//		log.info("Finalizado test");
//	}
}
