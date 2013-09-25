package ar.com.cuyum.cnc.utils;

import java.io.FileReader;

import ar.com.cuyum.cnc.service.JsonServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Permite probar por separado del render el convertidor de json a xml.
 *
 * Los tipos validos en Json
 *
 * String (Cadenas de texto) number (números) object (Objetos) char (caracteres
 * unicode válidos) array (Un arreglo o colección de valores) null (nulo)
 * boolean (valores true o false)
 *
 * @autor ltroconis@cuyum.com
 */
public class JsonToXMl {

    /**
     * Constructor privado.
     */
    private JsonToXMl() { }

    /**
     * Prueba del transformador de json a xml.
     *
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
       ObjectMapper mapper = new ObjectMapper();

       JsonNode jsonObject = mapper.readTree(new FileReader("builder.json"));



        String stringJson = jsonObject.toString(); // TODO Pasat el contenido de
                                                   // a String Json
        // Se movió la logica de conversión a JsonServices
        JsonServices service = new JsonServices();
        String xml = service.jsonToXForm(stringJson);
        System.out.println(xml);

    }

}
