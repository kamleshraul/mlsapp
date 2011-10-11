/*
******************************************************************
File: org.mkcl.els.controller.FileController.java
Copyright (c) 2011, sandeeps, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */

package org.mkcl.els.controller;

import java.io.IOException;

import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mkcl.els.domain.Document;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


// TODO: Auto-generated Javadoc
/**
 * The Class FileController.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Controller
@RequestMapping("/file")
public class FileController {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	/** The document service. */
	@Autowired
	IDocumentService documentService;	
	
	/** The custom parameter service. */
	@Autowired
	ICustomParameterService customParameterService;

	/**
	 * Creates the.
	 *
	 * @param file the file
	 * @param modelMap the model map
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String  create(@RequestParam MultipartFile file, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws IOException{
		String fileName=file.getOriginalFilename().toLowerCase();
		String qParam=request.getParameter("ext");
		long sizeAllowed=Long.parseLong(qParam.split("#")[1]);
		String[] extensions=qParam.split("#")[0].split(",");
		Document document = new Document();	
		if(file.getSize()<=sizeAllowed){
		for(String i:extensions)
		{
			if(fileName.endsWith(i))
			{
				document.setCreatedOn(new Date());
				document.setFileData(file.getBytes());
				document.setOriginalFileName(file.getOriginalFilename());
				document.setFileSize(file.getSize());
				document.setType(file.getContentType());
				document.setTag(customParameterService.findByName("FILE_PREFIX").getValue()+String.valueOf(UUID.randomUUID().hashCode()));
			
			try {
				document = documentService.save(document);

			} catch (IOException e) {
				logger.error("Error occured while uploading file:"+e.toString());
			}			
			}
		}
		}
		modelMap.addAttribute("file",document);
		return "document";
	
	}

	/**
	 * Gets the.
	 *
	 * @param tag the tag
	 * @param request the request
	 * @param response the response
	 */
	@RequestMapping(value = "{tag}", method = RequestMethod.GET)
	public void get(@PathVariable String tag, HttpServletRequest request,  HttpServletResponse response){
		Document document = documentService.findByTag(tag);
		try {
			response.setContentType(document.getType());
			response.setContentLength((int) document.getFileSize());
			response.setHeader("Content-Disposition","attachment; filename=\"" + document.getOriginalFileName() +"\"");
			FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
		} catch (IOException e) {
			logger.error("Error occured while downloading file:"+e.toString());
		}
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
	public void  getImage(@PathVariable String tag, HttpServletRequest request,  HttpServletResponse response){
		Document document = documentService.findByTag(tag);
		try {
			response.setContentType(document.getType());
			response.setContentLength((int) document.getFileSize());
			FileCopyUtils.copy(document.getFileData(), response.getOutputStream());
		} catch (IOException e) {
			logger.error("Error occured while downloading file:"+e.toString());
		}
		return;
	}

	/**
	 * Removes the.
	 *
	 * @param tag the tag
	 * @param request the request
	 * @return true, if successful
	 */
	@Transactional
	@RequestMapping(value = "remove/{tag}", method = RequestMethod.DELETE)
	public @ResponseBody boolean remove(@PathVariable("tag") String tag,
			HttpServletRequest request){
		documentService.removeByTag(tag);
		return true;
	}

	
}
