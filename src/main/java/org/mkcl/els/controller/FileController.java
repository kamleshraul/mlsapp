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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.Document;
import org.mkcl.els.domain.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        Document document = null;
        try {
        	document = Document.findByTag(tag);
            response.setContentType(document.getType());
            response.setContentLength((int) document.getFileSize());
            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + document.getOriginalFileName() + "\"");
            if(document.getPath()!=null && !document.getPath().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) {
            	java.io.File storageFile = new java.io.File(document.getPath());
            	FileCopyUtils.copy(FileUtils.readFileToByteArray(storageFile), response.getOutputStream());
            } 
            else {
            	FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
            }            
            
        } catch (IOException e) {
            logger.error("Error occured while downloading file:" + e.toString());
        }catch (ELSException e) {
			logger.error(e.getMessage());			
		}
    }
    
    @RequestMapping(value = "{tag}/open", method = RequestMethod.GET)
    public void openFile(@PathVariable final String tag,
                    final HttpServletRequest request,
                    final HttpServletResponse response) {
        Document document = null;
        try {
        	document = Document.findByTag(tag);
            response.setContentType(document.getType());
            response.setContentLength((int) document.getFileSize());
            response.setHeader("Content-Disposition", "inline; filename=\""
                    + document.getOriginalFileName() + "\"");
            if(document.getPath()!=null && !document.getPath().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) {
            	java.io.File storageFile = new java.io.File(document.getPath());
            	FileCopyUtils.copy(FileUtils.readFileToByteArray(storageFile), response.getOutputStream());
            } 
            else {
            	FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
            }
        } catch (IOException e) {
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
        Document document = null;
        try {
        	document = Document.findByTag(tag);
            response.setContentType(document.getType());
            response.setContentLength((int) document.getFileSize());
            if(document.getPath()!=null && !document.getPath().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) {
            	java.io.File storageFile = new java.io.File(document.getPath());
            	FileCopyUtils.copy(FileUtils.readFileToByteArray(storageFile), response.getOutputStream());
            } 
            else {
            	FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
            }
        } catch (IOException e) {
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
        	if(document.getPath()!=null && !document.getPath().isEmpty() && ApplicationConstants.SERVER_FILE_STORAGE_ENABLED.equals("YES")) {
            	java.io.File storageFile = new java.io.File(document.getPath());      
            	storageFile.delete();  		
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
}
