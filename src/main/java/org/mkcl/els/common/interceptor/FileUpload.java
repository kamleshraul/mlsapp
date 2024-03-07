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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FileUtil;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
            if(file.getSize() > (Integer.parseInt(sizeLimitForFile) * 1024000)) { //Size Limit calculated in MB
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
                 
                 /** store file contents either in database documents table or external file storage server now **/
                 String storageType = req.getParameter("storageType");
                 //storageType = "file_server";
                 if(storageType!=null && storageType.equals("file_server") && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES"))
             	 {
                	 String locationHierarchy = req.getParameter("locationHierarchy");
                     if(locationHierarchy!=null && !locationHierarchy.isEmpty() && !locationHierarchy.equals("~") && !locationHierarchy.equals("/")) {
                    	 RestTemplate restTemplate = new RestTemplate();
                 		 CustomParameter csptURLForTokenGenerationOnFileServer = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_FILE_SERVER_TOKEN_GENERATION_URL, "");
                 		 String tokenGenerationURLForFileServer = null;
                 		 if(csptURLForTokenGenerationOnFileServer!=null && csptURLForTokenGenerationOnFileServer.getValue()!=null) {
                 			tokenGenerationURLForFileServer = csptURLForTokenGenerationOnFileServer.getValue();
                 		 } else {
                 			tokenGenerationURLForFileServer = ApplicationConstants.FILE_SERVER_TOKEN_GENERATION_URL_DEFAULT;
                 		 }
                         
                         HttpEntity<String> reqEntity = new HttpEntity<String>("Hello CDN File Server!");
                         
                         @SuppressWarnings("rawtypes")
                         ResponseEntity<HashMap> mapResponse = restTemplate.exchange(tokenGenerationURLForFileServer, HttpMethod.GET, reqEntity, HashMap.class);
                         System.out.println(mapResponse.getBody().get("message") + ": " +mapResponse.getBody().get("token"));
                         String token = mapResponse.getBody().get("token").toString();
                         
                         // Create a temporary file for sending uploading content as Resource
                         String fileSuffix = null;
                         if(file.getContentType()!=null
                        		 && file.getContentType().equalsIgnoreCase(ApplicationConstants.MIME_PDF)) {
                        	 fileSuffix = ApplicationConstants.EXTENSION_PDF;
                         }
                         File tempFile = File.createTempFile("temp", fileSuffix);
                         // Transfer the uploading file content to the temporary file
                         FileOutputStream outputStream = null;
                         try {
                             outputStream = new FileOutputStream(tempFile);
                             outputStream.write(file.get());
                         } finally {
                             if (outputStream != null) {
                                 try {
                                     outputStream.close();
                                 } catch (IOException ex) {
                                     // Log or handle exception
                                 }
                             }
                         }
                         // Create a FileSystemResource from the temporary file
                         Resource resource = new FileSystemResource(tempFile);
                         
                         CustomParameter csptURLForFileUploadOnFileServer = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_FILE_SERVER_FILE_UPLOAD_URL, "");
                 		 String fileUploadURLForFileServer = null;
                 		 if(csptURLForFileUploadOnFileServer!=null && csptURLForFileUploadOnFileServer.getValue()!=null) {
                 			fileUploadURLForFileServer = csptURLForFileUploadOnFileServer.getValue();
                 		 } else {
                 			fileUploadURLForFileServer = ApplicationConstants.FILE_SERVER_FILE_UPLOAD_URL_DEFAULT;
                 		 }
                         
                         String fileType = null;
                         String fileLocationPath = null;
                         if(locationHierarchy!=null && !locationHierarchy.isEmpty()) {
                        	 String[] locationHierarchyArr = locationHierarchy.split("~");
                        	 fileType = locationHierarchyArr[0];
                        	 if(locationHierarchyArr.length>1) {
                        		 StringBuffer fileLocationPathBuffer = new StringBuffer("");
                        		 for(int i=1; i<locationHierarchyArr.length; i++) {
                        			 fileLocationPathBuffer.append(locationHierarchyArr[i]);
                        			 if(i!=locationHierarchyArr.length-1) {
                        				 fileLocationPathBuffer.append("/");
                        			 }
                        		 }
                        		 fileLocationPath = fileLocationPathBuffer.toString();
                            	 //fileLocationPath = locationHierarchyArr[1].replaceAll("~", "/");
                        	 }
                         } else {
                        	 locationHierarchy = "";
                        	 fileLocationPath = "";
                         }
                         
                         StringBuffer clientDataForCDN = new StringBuffer(req.getParameter("authusername"));
                         clientDataForCDN.append("##");
                         clientDataForCDN.append(ApplicationConstants.SERVER_FILE_STORAGE_CLIENT_ID);
                         clientDataForCDN.append("##");
                         clientDataForCDN.append(fileType);
                         clientDataForCDN.append("##");
                         
                         // Create headers
                         HttpHeaders headers = new HttpHeaders();
                         headers.set("Authorization", token);
                         headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                         // Create the multipart request body
                         MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
                         //body.add("token", token);
                         body.add("file", resource);
                         body.add("path", fileLocationPath);
                         body.add("data", clientDataForCDN.toString());
                         body.add("originalFileName", file.getName());

                         // Create the request entity
                         HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(body, headers);

                         // Make the HTTP POST request for File Upload
                         @SuppressWarnings("rawtypes")
    					 ResponseEntity<HashMap> responseEntity = restTemplate.exchange(fileUploadURLForFileServer, HttpMethod.POST, requestEntity, HashMap.class);
                         System.out.println("Response from external API: " + responseEntity.getBody());
                         if(responseEntity!=null && responseEntity.getStatusCode().value()==HttpStatus.OK.value()) {
                        	 document.setLocationHierarchy(locationHierarchy);
                     		 document.setTag(responseEntity.getBody().get("fileTag").toString());
                     		 document.setFileIdOnFileServer(responseEntity.getBody().get("id").toString());
                         }
                         else {
                        	 errorCode = "error_in_file_upload at : " + locationHierarchy;
                        	 // redirected to photo page so that proper error message can be displayed incase of corrupted file
                         	 res.sendRedirect("file/photo/" + errorCode +".json");
                         }
                         
                         tempFile.delete();
                         file.delete();                 		 
                     }
                     else{
                    	 errorCode = "file_type_missing_in_location_hierarchy_parameter";
                    	 // redirected to photo page so that proper error message can be displayed incase of corrupted file
                     	 res.sendRedirect("file/photo/" + errorCode +".json");
                     }             		 
             	} 
             	else {
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