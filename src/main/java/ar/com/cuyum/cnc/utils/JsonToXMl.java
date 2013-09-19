package ar.com.cuyum.cnc.utils;

import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ar.com.cuyum.cnc.service.JsonServices;

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

	public static void main(String[] args) throws Exception{
		JSONParser parser = new JSONParser();
		
		Object fileJson;
		JSONObject jsonObject;
		try {
			Object obj = parser.parse(new FileReader("builder.json"));
			jsonObject = (JSONObject) obj;
			
			//System.out.print(jsonObject);
		} catch (ParseException e) {
			return;
		}
		
		String stringJson = jsonObject.toString();//TODO Pasat el contenido de a String Json
		//Se movió la logica de conversión a JsonServices 
		JsonServices service = new JsonServices();
		String xml = service.jsonToXForm(stringJson);
		System.out.println(xml);
		

	}

}
