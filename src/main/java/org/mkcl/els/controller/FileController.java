/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.controller.FileController.java
 * Created On: Jan 5, 2012
 */

package org.mkcl.els.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Class FileController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/file")
public class FileController extends GenericController<File> {

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(FileController.class);

    /**
     * Creates the.
     *
     * @param file the file
     * @param modelMap the model map
     * @param request the request
     * @param response the response
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     * @author sujitas
     * @since v1.0.0
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String create(@RequestParam(required = false) final MultipartFile file,
                         final ModelMap modelMap,
                         final HttpServletRequest request,
                         final HttpServletResponse response) throws IOException {
        String fileName = file.getOriginalFilename().toLowerCase();
        String qParam = request.getParameter("ext");
        long sizeAllowed = Long.parseLong(qParam.split("#")[1]);
        String[] extensions = qParam.split("#")[0].split(",");
        Document document = new Document();
        if (file.getSize() <= sizeAllowed) {
            for (String i : extensions) {
                if (fileName.endsWith(i)) {
                    document.setCreatedOn(new Date());
                    document.setFileData(file.getBytes());
                    document.setOriginalFileName(file.getOriginalFilename());
                    document.setFileSize(file.getSize());
                    document.setType(file.getContentType());
                    CustomParameter customParameter = CustomParameter
                            .findByName(
                                    CustomParameter.class, "FILE_PREFIX",
                                    "en_US");
                    document.setTag(customParameter.getValue()
                            + String.valueOf(UUID.randomUUID().hashCode()));
                    document = document.persist();
                }
            }
        }
        modelMap.addAttribute("file", document);
        return "document";

    }

    /**
     * Gets the.
     *
     * @param tag the tag
     * @param request the request
     * @param response the response
     * @author sujitas
     * @since v1.0.0
     */
    @RequestMapping(value = "{tag}", method = RequestMethod.GET)
    public void get(@PathVariable final String tag,
                    final HttpServletRequest request,
                    final HttpServletResponse response) {
        try {        	            
            this.fetchFileContentForDownload(tag, request, response);
        } 
        catch (IOException e) {
            logger.error("Error occured while downloading file:" + e.toString());
        }catch (ELSException e) {
			logger.error(e.getMessage());			
		}
    }
    
    @RequestMapping(value = "{tag}/open", method = RequestMethod.GET)
    public void openFile(@PathVariable final String tag,
                    final HttpServletRequest request,
                    final HttpServletResponse response) {
        try {
        	this.fetchFileContentForOpening(tag, request, response);
        } 
        catch (IOException e) {
            logger.error("Error occured while opening file:" + e.toString());
        }catch (ELSException e) {
			logger.error(e.getMessage());
		}
    }

    /**
     * Gets the document info.
     *
     * @param tag the tag
     * @param modelMap the model map
     * @param request the request
     * @param response the response
     * @return the document info
     */
    @RequestMapping(value = "{tag}/info", method = RequestMethod.GET)
    public @ResponseBody
    String getDocumentInfo(@PathVariable final String tag,
                           final ModelMap modelMap,
                           final HttpServletRequest request,
                           final HttpServletResponse response) {
        Document document = null;
        try{
        	document = Document.findByTag(tag);
        }catch (ELSException e) {
			logger.error(e.getMessage());
			modelMap.addAttribute("error", e.getParameter());
		}
        return document.getTag();
    }

    /**
     * Gets the image.
     *
     * @param tag the tag
     * @param request the request
     * @param response the response
     * @return the image
     */
    @RequestMapping(value = "photo/{tag}", method = RequestMethod.GET)
    public @ResponseBody
    String getImage(@PathVariable final String tag,
                         final HttpServletRequest request,
                         final HttpServletResponse response) {
        try {
        	this.fetchFileContentForPhotoDisplay(tag, request, response);
        } 
        catch (IOException e) {
            logger.error("Error occured while downloading file:" + e.toString());
        }catch (ELSException e) {
			logger.error("Error occured while downloading file:" + e.toString());
		}
        return tag;
    }

    /**
     * Removes the.
     *
     * @param tag the tag
     * @param request the request
     * @return true, if successful
     * @author sujitas
     * @since v1.0.0
     */
    @Transactional
    @RequestMapping(value = "remove/{tag}", method = RequestMethod.DELETE)
    public @ResponseBody
    boolean remove(@PathVariable("tag") final String tag,
                   final HttpServletRequest request) {
        Document document =null;
        try{
        	document = Document.findByTag(tag);
        	if(document.getLocationHierarchy()!=null && !document.getLocationHierarchy().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) {
//            	java.io.File storageFile = new java.io.File(document.getLocationHierarchy());      
//            	storageFile.delete();  		
        	}
        	document.remove();
        	return true;
        }catch (ELSException e) {
			logger.error(e.getMessage());
			return false;
		}
        
    }

    /**** Methods for File Master ****/    
    @Override
    protected void customValidateCreate(File domain, BindingResult result, HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		if(domain.getName()==null){
			result.rejectValue("name","NameEmpty","Name is not set.");
		}		
	}
    
    @Override
    protected void customValidateUpdate(File domain, BindingResult result, HttpServletRequest request) {
		/**** Version Mismatch ****/
		if (domain.isVersionMismatch()) {
			result.rejectValue("version", "VersionMismatch", "concurrent updation is not allowed.");
		}
		if(domain.getName()==null){
			result.rejectValue("name","NameEmpty","Name is not set.");
		}		
	}
    
    private void fetchFileContentForDownload(final String tag, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ELSException {
    	this.fetchFileContent(tag, false, false, request, response);
    }
    
    private void fetchFileContentForOpening(final String tag, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ELSException {
    	this.fetchFileContent(tag, false, true, request, response);
    }
    
    private void fetchFileContentForPhotoDisplay(final String tag, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ELSException {
    	this.fetchFileContent(tag, true, false, request, response);
    }
    
    private void fetchFileContent(final String tag, final boolean isPhotoToBeDisplayed,  final boolean isFetchedForOpen, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ELSException {
    	Document document = Document.findByTag(tag);
        response.setContentType(document.getType());
        response.setContentLength((int) document.getFileSize());
        
        if(!isPhotoToBeDisplayed) {
        	StringBuffer contentDispositionHeader = new StringBuffer("");
            if(isFetchedForOpen) {
            	contentDispositionHeader.append("inline;");
            } else {
            	contentDispositionHeader.append("attachment;");
            }
            contentDispositionHeader.append(" ");
            contentDispositionHeader.append("filename=\"");
            contentDispositionHeader.append(document.getOriginalFileName());
            contentDispositionHeader.append("\"");
            response.setHeader("Content-Disposition", contentDispositionHeader.toString());
            
//            response.setHeader("Content-Disposition", "attachment; filename=\""
//            			+ document.getOriginalFileName() + "\"");
        }
        
        if(document.getLocationHierarchy()!=null && !document.getLocationHierarchy().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) 
        {
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
            
            CustomParameter csptURLForFileUploadOnFileServer = CustomParameter.findByName(CustomParameter.class, ApplicationConstants.CSPT_FILE_SERVER_FILE_DOWNLOAD_URL, "");
    		String fileUploadURLForFileServer = null;
    		if(csptURLForFileUploadOnFileServer!=null && csptURLForFileUploadOnFileServer.getValue()!=null) {
    			fileUploadURLForFileServer = csptURLForFileUploadOnFileServer.getValue();
    		} else {
    			fileUploadURLForFileServer = ApplicationConstants.FILE_SERVER_FILE_DOWNLOAD_URL_DEFAULT;
    		}
    		
            StringBuffer cdnURLForFileDownload = new StringBuffer(fileUploadURLForFileServer.replace("{fileId}", document.getFileIdOnFileServer()));
//            cdnURLForFileDownload.append("?request_parameters_here=");
//            cdnURLForFileDownload.append(request_parameter_value);
            
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.set("Accept", document.getType());
            
            // Create the request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(headers);

            // Make the HTTP GET request for File Download
            ResponseEntity<ByteArrayResource> responseEntity = restTemplate.exchange(cdnURLForFileDownload.toString(), HttpMethod.GET, requestEntity, ByteArrayResource.class);
            System.out.println("Response from external API: " + responseEntity.getBody());
            if(responseEntity!=null && responseEntity.getStatusCode().value()==HttpStatus.OK.value()) {
            	System.out.println("File Downloaded Successfully");
            	FileCopyUtils.copy(responseEntity.getBody().getByteArray(), response.getOutputStream());
            }
        } 
        else {
        	FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
        }
    }
}
