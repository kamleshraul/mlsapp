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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
            FileItem file = files.get(0);
            // To check whether the image uploaded is corrupted or proper file
            if(file.getContentType().contains("image")){
            	 if(ImageIO.read(file.getInputStream())==null){
            		 isAllowed = false;
            	 }
            }
            if(isAllowed){
            	 Document document = new Document();
                 document.setCreatedOn(new Date());
                 document.setFileData(file.get());
                 document.setOriginalFileName(file.getName());
                 document.setFileSize(file.getSize());
                 document.setType(file.getContentType());
                 document.setCreatedBy(req.getParameter("authusername"));
                 CustomParameter customParameter = CustomParameter.findByName(
                         CustomParameter.class, "FILE_PREFIX", "");
                 document.setTag(customParameter.getValue()
                         + String.valueOf(System.currentTimeMillis()));
                 document = document.persist();
                 // If the file uploaded is a Process file
                 if(FileUtil.fileExtension(file.getName()).equals("bar")){
                     res.sendRedirect("workflow/deploy/" + document.getTag() + "/create.json");
                 } else {
                     res.sendRedirect("file/" + document.getTag() + "/info.json");
                 }
            }else{
            	// redirected to photo page so that proper error message can be displayed incase of corrupted file
            	res.sendRedirect("file/photo/" + null +".json");
            }
        }
        catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

}