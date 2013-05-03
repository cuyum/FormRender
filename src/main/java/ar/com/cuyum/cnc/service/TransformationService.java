/**
 * 
 */
package ar.com.cuyum.cnc.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author Jorge Morando
 * 
 */
@Stateless
public class TransformationService {

	public transient Logger log = Logger.getLogger(TransformationService.class);

	private HttpClient client = new DefaultHttpClient();
	
	public String transformXml(String xmlUrl){
		HttpPost request = buildRequest(xmlUrl);
		HttpResponse rawResponse = execute(request);
		String responseXml = processResponse(rawResponse);
		return responseXml;
	}
	
	private final HttpPost buildRequest(String xmlUrl) {

		List<BasicNameValuePair> bodyParams = new ArrayList<BasicNameValuePair>();
		HttpPost method = null;
		try {			
			String urlpath = "http://localhost/formRenderServices/transform.php";
			method = new HttpPost(urlpath);
			if (bodyParams != null) {
				if (bodyParams.size() == 0) {
					bodyParams.add(new BasicNameValuePair("xmlUrl", xmlUrl));
				}
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				for (BasicNameValuePair nvp : bodyParams) {
					entity.addPart(nvp.getName(), new StringBody(nvp.getValue()));
				}
				((HttpPost) method).setEntity(entity);
			}

		} catch (Exception e) {
			log.error(e);
		}
		
		return method;
	}
	
	private HttpResponse execute(HttpRequestBase method) {
		HttpResponse resp = null;

		try {
			log.info("Ejecutando: " + method);
			resp = client.execute(method);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return resp;
	}
	
	private final String processResponse(HttpResponse rawResponse) {
		String entity = "<root></root>";
		try {
			entity = EntityUtils.toString(rawResponse.getEntity());
		} catch (Exception e) {
			log.error(e);
		}
		return entity;
	}

}
