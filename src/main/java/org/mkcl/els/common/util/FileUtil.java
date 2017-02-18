package org.mkcl.els.common.util;

public class FileUtil {

	/**
     * Given a file name, returns the extension of the file.
     * A file name may contain multiple periods, the method
     * picks up the last period delimited String & returns it.
     * If there is no period in the file name, returns the name 
     * as it is.
     */
    public static String fileExtension(String fileName) {	

		String strFileName = fileName;
		while(strFileName.contains(".")){
			strFileName = strFileName.split("\\.", 2)[1];
		}
		return strFileName;
	}
    
}
