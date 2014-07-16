/**
 * 
 */
package ar.com.cuyum.cnc.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;

/**
 * Clase creada manejo de archivos
 * 
 * @author ltroconis
 * 
 */
@Stateless
public class FileService {
		private static final int BUFFER_SIZE = 1024;
	  
	    /**
	     * 
	     * @param pZipFile archivo comprimido .zip
	     * @param pFile nombre del archivo al decomprimir
	     * @throws Exception
	     */
		public void UnZip(String pZipFile, String pFile) throws Exception {
			BufferedOutputStream bos = null;
			FileInputStream fis = null;
			ZipInputStream zipis = null;
			FileOutputStream fos = null;
	 
			try {
				fis = new FileInputStream(pZipFile);
				zipis = new ZipInputStream(new BufferedInputStream(fis));
				if (zipis.getNextEntry() != null) {
					int len = 0;
					byte[] buffer = new byte[BUFFER_SIZE];
					fos = new FileOutputStream(pFile);
					bos = new BufferedOutputStream(fos, BUFFER_SIZE);
	 
					while  ((len = zipis.read(buffer, 0, BUFFER_SIZE)) != -1)
						bos.write(buffer, 0, len);
					bos.flush();
				} else {
					throw new Exception("El zip no contenia fichero alguno");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				bos.close();
				zipis.close();
				fos.close();
				fis.close();
			}
		} 
}
