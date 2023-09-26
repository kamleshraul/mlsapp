package org.mkcl.els.common.util;

import java.io.File;

import org.mkcl.els.common.exception.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	public static final String FILE_STORAGE_BASE_LOCATION; //= ApplicationConstants.systemPropertiesBundle.getString("file_storage.base_location");
	
	static {
		//CustomParameter cpstFileStorageEnabled = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_SERVER_FILE_STORAGE_ENABLED, "");
		if(ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) 
		{
			if(ApplicationConstants.environment!=null && ApplicationConstants.environment.acceptsProfiles("dev")) {
				FILE_STORAGE_BASE_LOCATION = ApplicationConstants.systemPropertiesBundle.getString("file_storage.base_location_local");
			} else {
				FILE_STORAGE_BASE_LOCATION = ApplicationConstants.systemPropertiesBundle.getString("file_storage.base_location_production");
			}		
			logger.info("(Info Log) FILE_STORAGE_BASE_LOCATION is: " + FILE_STORAGE_BASE_LOCATION);		
		
			File fileStorageDirectory = new File(FILE_STORAGE_BASE_LOCATION);
			if(!fileStorageDirectory.exists()) {
				if (fileStorageDirectory.mkdir()) {
					System.out.println("File Storage Directory is created!");
				} else {
					throw new ResourceException("Failed to create Base Directory for File Storage!");
				}
			}
		} 
		else {
			FILE_STORAGE_BASE_LOCATION = ""; //As Server File Storage is disabled in this case
		}
	}

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
