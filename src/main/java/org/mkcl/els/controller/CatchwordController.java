package org.mkcl.els.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.mkcl.els.domain.Catchword;
import org.mkcl.els.domain.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/catchword")
public class CatchwordController extends GenericController<Catchword>{
	@RequestMapping(value = "/upload/{id}", method = RequestMethod.POST)
    public String create(final @PathVariable(value="id") String docTag, 
    					final ModelMap modelMap,
                         final HttpServletRequest request,
                         final HttpServletResponse response,
                         final Locale locale) throws IOException {
		
		try{
	        Document doc = Document.findByTag(docTag);
	        WordReaderUtil.doRead(doc.getFileData(), docTag, locale.toString());
	        
	    }catch (Exception e) {
			logger.debug("catchword/upload", e);
			e.printStackTrace();
		}
        return "catchword/upload";
    }
}

class WordReaderUtil{

	private void readDocFile(byte[] data, String docTag, String locale) {
		//File docFile = null;
		WordExtractor docExtractor = null;
		//WordExtractor exprExtractor = null;
		try {
			//docFile = new File("c:\\file.doc");
			// A FileInputStream obtains input bytes from a file.
			//FileInputStream fis = new FileInputStream(docFile.getAbsolutePath());
			ByteArrayInputStream bAIS = new ByteArrayInputStream(data);
			
			// A HWPFDocument used to read document file from FileInputStream
			HWPFDocument doc = new HWPFDocument(bAIS);

			docExtractor = new WordExtractor(doc);
		} catch (Exception exep) {
			System.out.println(exep.getMessage());
		}

		// This Array stores each line from the document file.
		String[] docArray = docExtractor.getParagraphText();

		if(docArray != null){
			for (int i = 0; i < docArray.length; i++) {
				
				Catchword catchWord = new Catchword();
				catchWord.setLocale(locale);
				catchWord.setValue(docArray[i]);
				catchWord.setDocID(docTag);
				catchWord.persist();
			}
		}
	}
	
	public static void doRead(byte[] data, String docTag, String locale){
		WordReaderUtil reader = new WordReaderUtil();
		reader.readDocFile(data, docTag, locale);
	}
}
