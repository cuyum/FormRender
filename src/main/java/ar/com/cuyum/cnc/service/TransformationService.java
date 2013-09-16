/**
 * 
 */
package ar.com.cuyum.cnc.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

import javax.ejb.Stateless;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author Jorge Morando
 * 
 */
@Stateless
public class TransformationService {

	public transient Logger log = Logger.getLogger(TransformationService.class);


	private static String XSL_FORM = "formCnc.xsl"; 

	
	private HttpClient client = new DefaultHttpClient();

	private boolean remoteTransformation = false;
	
	private URL remoteTransformationServiceUrl = null;
	
	/*================ POINT OF ENTRANCE =================*/
	
	public String transform(InputStream xmlStream, InputStream xslStream, String strParamVersion){
		String transformedXml = null;
		if(remoteTransformation){
			transformedXml = requestTransformation(xmlStream);
		}else{
			transformedXml = performTransformation(xmlStream, xslStream, strParamVersion);
		}
		return transformedXml;
	}
	
	/*=============TRANSFORMATION ALTERNATIVES============*/
	
	private String performTransformation(InputStream xmlStream, InputStream xslStream, String paramVersion){
		log.debug("Transformando el xml localmente");
		
		String result = "<html><body>Error.</body></html>";
		
		try {
			result = transformHtml(xmlStream, xslStream, paramVersion);
		} catch (Exception e) {
			log.error("Error transformando localmente el xml",e);
		}
		
        return result;
	}
	
	private String requestTransformation(InputStream xmlStream){
		HttpPost request = buildRequest(xmlStream);
		HttpResponse rawResponse = execute(request);
		String transformedXml = processResponse(rawResponse);
		return transformedXml;
	}
	
	/*=================VIA XALAN SERVICE===================*/
	
	private String transformHtml(InputStream xmlStream, InputStream xslStream, String paramVersion) throws Exception {
		
		// Create a transform factory instance.
		TransformerFactory tfactory = TransformerFactory.newInstance();
		//StringWriter resultForm = new StringWriter();
		OutputStream result = new ByteArrayOutputStream();
		
		// Create a transformer for the stylesheet.
		//Transformer transformerForm = tfactory.newTransformer(new StreamSource(new InputStreamReader(loadXsl(XSL_FORM),"UTF8")));
		Transformer transformerForm = tfactory.newTransformer(new StreamSource(new InputStreamReader(xslStream,"UTF-8")));
		
		
		// Transform the source XML to System.out.
		if (null != paramVersion && !paramVersion.isEmpty())
			transformerForm.setParameter("versionForm", paramVersion);
		Writer resultForm = new OutputStreamWriter(result, "UTF-8");
		transformerForm.transform(new StreamSource(new InputStreamReader(xmlStream, "UTF-8")),  new StreamResult(resultForm));
		resultForm.flush();
		String ret = result.toString();
		return ret;
	}
	
//	private InputStream loadXsl(String xsl){				
//		InputStream xslIS = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/xsl/" + xsl);
//		return xslIS;
//	}
	
	/*=============== VIA HTTP REQUEST======================*/
	
	private final HttpPost buildRequest(InputStream xmlFile) {
		HttpPost method = null;
		try {
			method = new HttpPost("http://joaco.cuyum.com:9999/formRenderServices/transform.php");
			
			MultipartEntity entity = new MultipartEntity();
		    entity.addPart("xmlFile", new InputStreamBody(xmlFile, "text/xml"));
			
			method.setEntity(entity);
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

	/*================= SERVICE CONFIGURATION===============*/
	
	/**
	 * By default the transformation is made locally but it can be made via remote <br />
	 * transformation service by providing url to a service that consumes an xml and produces an xml.
	 * @return
	 */
	public boolean isRemoteTransformation() {
		return remoteTransformation;
	}

	/**
	 * By default the transformation is made locally but it can be made via remote <br />
	 * transformation service by providing url to a service that consumes an xml and produces an xml.
	 * @return
	 */
	public void setRemoteTransformation(boolean remoteTransformation) {
		this.remoteTransformation = remoteTransformation;
	}

	/**
	 * By default the transformation is made locally but it can be made via remote <br />
	 * transformation service by providing url to a service that consumes an xml and produces an xml.
	 * @return the URL for the remote service
	 */
	public URL getRemoteTransformationServiceUrl() {
		return remoteTransformationServiceUrl;
	}

	/**
	 * By default the transformation is made locally but it can be made via remote <br />
	 * transformation service by providing url to a service that consumes an xml and produces an xml.
	 * @return
	 */
	public void setRemoteTransformationServiceUrl(URL remoteTransformationServiceUrl) {
		if(remoteTransformationServiceUrl != null){
			this.setRemoteTransformation(true);
			this.remoteTransformationServiceUrl = remoteTransformationServiceUrl;
		}
	}

}
