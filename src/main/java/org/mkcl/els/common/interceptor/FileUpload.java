/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.common.interceptor.FileUpload.java
 * Created On: Mar 3, 2012
 */
package org.mkcl.els.common.interceptor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mkcl.els.common.exception.ResourceException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FileUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;

/**
 * The Class FileUpload.
 *
 * @author sandeeps
 * @since v1.0.0
 */
public class FileUpload extends HttpServlet {

    /** The Constant serialVersionUID. */
    private static final transient long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
	@Override
    public void doPost(final HttpServletRequest req,
            final HttpServletResponse res) throws ServletException, IOException {
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> files = upload.parseRequest(req);
            Boolean isAllowed = true;
            String errorCode = null;
            FileItem file = files.get(0);
            // To check whether the image uploaded is corrupted or proper file
            if(file.getContentType().contains("image")){
            	 if(ImageIO.read(file.getInputStream())==null){
            		 isAllowed = false;
            	 }
            }
            // To check whether the file getting uploaded is within the maximum size limit
            String sizeLimitForFile = req.getParameter("maxFileSizeMB");
            if(sizeLimitForFile==null || sizeLimitForFile.isEmpty()) {
            	sizeLimitForFile = ApplicationConstants.DEFAULT_MAX_FILE_UPLOAD_LIMIT;
            }
            if(file.getSize() > (Integer.parseInt(sizeLimitForFile) * 1000000)) { //Size Limit calculated in MB
            	isAllowed = false;
            	errorCode = sizeLimitForFile+" MB_size_exceeded";
            }
            if(isAllowed){
            	 Document document = new Document();
                 document.setCreatedOn(new Date());
                 //document.setFileData(file.get());
                 document.setOriginalFileName(file.getName());
                 document.setFileSize(file.getSize());
                 document.setType(file.getContentType());
                 document.setCreatedBy(req.getParameter("authusername"));
                 CustomParameter csptFilePrefix = CustomParameter.findByName(
                         CustomParameter.class, "FILE_PREFIX", "");
                 StringBuffer uniqueTagGenerated = new StringBuffer();
                 if(csptFilePrefix!=null && csptFilePrefix.getValue()!=null) {
                	 uniqueTagGenerated.append(csptFilePrefix.getValue());
                	 uniqueTagGenerated.append("_");
                 }
                 uniqueTagGenerated.append(String.valueOf(Math.abs(UUID.randomUUID().hashCode())));
                 uniqueTagGenerated.append("_");
                 uniqueTagGenerated.append(String.valueOf(System.currentTimeMillis()));
                 document.setTag(uniqueTagGenerated.toString());
                 //document.setTag(csptFilePrefix.getValue() + "_" + UUID.randomUUID().toString() + "_" + String.valueOf(System.currentTimeMillis()));
                 
                 /** store file contents either in database documents table or external file storage server now **/
                 String storageType = req.getParameter("storageType");
                 //storageType = "file_server";
             	 if(storageType!=null && storageType.equals("file_server") && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES"))
             	 {
             		 StringBuffer fileName = new StringBuffer();
                     File locationDirectory = null;
                     String locationHierarchy = req.getParameter("locationHierarchy");
                     if(locationHierarchy!=null && !locationHierarchy.isEmpty() && !locationHierarchy.equals("~")) {
                    	String parentLocation = FileUtil.FILE_STORAGE_BASE_LOCATION;
                    	for(String currentLocation: locationHierarchy.split("~")) {
                    		locationDirectory = new java.io.File(parentLocation, currentLocation);
                         	if(!locationDirectory.exists()) {
                     			if (locationDirectory.mkdir()) {
                     				System.out.println("Directory is created at: " + locationDirectory.getAbsolutePath());
                     			} else {
                     				throw new ResourceException("Failed to create Directory at: " + locationDirectory.getAbsolutePath());
                     			}
                     		}
                         	parentLocation = locationDirectory.getAbsolutePath();
                    	}
                     } 
                     else {
                    	 locationDirectory = new java.io.File(FileUtil.FILE_STORAGE_BASE_LOCATION);
                     }
                  	
                  	 fileName.append(document.getTag());
                  	 fileName.append(".");
                  	 fileName.append(FileUtil.fileExtension(file.getName()));                  	
                     java.io.File storageFile = new java.io.File(locationDirectory, fileName.toString());
                     file.write(storageFile);
                     
                     document.setPath(storageFile.getAbsolutePath());
                     document.setLocationHierarchy(locationHierarchy);
             	} 
             	else {
             		document.setFileData(file.get());
             	}
                 
                 document = document.persist();
                 // If the file uploaded is a Process file
                 if(FileUtil.fileExtension(file.getName()).equals("bar")){
                     res.sendRedirect("workflow/deploy/" + document.getTag() + "/create.json");
                 } else {
                     res.sendRedirect("file/" + document.getTag() + "/info.json");
                 }
            }else{
            	// redirected to photo page so that proper error message can be displayed incase of corrupted file
            	res.sendRedirect("file/photo/" + errorCode +".json");
            }
        }
        catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}