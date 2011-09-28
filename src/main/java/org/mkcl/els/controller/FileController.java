
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


@Controller
@RequestMapping("/file")
public class FileController {
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	@Autowired
	IDocumentService documentService;	
	@Autowired
	ICustomParameterService customParameterService;

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
				document.setTag(customParameterService.findByName("FILE_PREFIX").getValue()+UUID.randomUUID().toString());
			
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

	@Transactional
	@RequestMapping(value = "remove/{tag}", method = RequestMethod.DELETE)
	public @ResponseBody boolean remove(@PathVariable("tag") String tag,
			HttpServletRequest request){
		documentService.removeByTag(tag);
		return true;
	}

	
}
