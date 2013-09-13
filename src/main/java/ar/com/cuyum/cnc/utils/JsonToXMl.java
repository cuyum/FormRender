package ar.com.cuyum.cnc.utils;

import java.io.FileReader;
import java.io.Reader;

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
		Reader fileJson = new FileReader("prueba.json");
		String stringJson = ""; //TODO Pasat el contenido de a String Json
		//Se movió la logica de conversión a JsonServices 
		JsonServices service = new JsonServices();
		String xml = service.jsonToXForm(stringJson);
		System.out.println(xml);
		

	}

}
