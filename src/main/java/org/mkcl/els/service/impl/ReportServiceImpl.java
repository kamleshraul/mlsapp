package org.mkcl.els.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
 
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.joda.time.format.FormatUtils;
import org.mkcl.els.common.exception.ResourceException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.xmlvo.XmlVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.service.IReportService;
import org.xml.sax.SAXException;
 
public class ReportServiceImpl implements IReportService {
	
	// configure fopFactory as desired
	private FopFactory fopFactory = FopFactory.newInstance();
	
	public static final String BASE_DIRECTORY;
	
	public static final String REPORT_DIRECTORY;
	
	static {
		//handle spaces in path for base directory
		String pathWithSpaces = ReportServiceImpl.class.getClassLoader().getResource("fop_storage").getPath();
		BASE_DIRECTORY = pathWithSpaces.replaceAll("%20", " ");
		
		//create Report Directory if it does not exist
		File reportDirectory = new File(BASE_DIRECTORY + "\\reports");
		if(!reportDirectory.exists()) {
			if (reportDirectory.mkdir()) {
				System.out.println("Report Directory is created!");
				REPORT_DIRECTORY = reportDirectory.getAbsolutePath();
			} else {
				REPORT_DIRECTORY = null;
				throw new ResourceException("Failed to create Report Directory for FOP Reports!");					
			}						
		} else {
			REPORT_DIRECTORY = reportDirectory.getAbsolutePath();
		}
	}	
	
	public static final String EXTENSION_XML = ".xml";
	public static final String EXTENSION_XSLT = ".xsl";
	public static final String EXTENSION_FO = ".fo";
	public static final String EXTENSION_PDF = ".pdf";
	public static final String EXTENSION_RTF = ".rtf";
	public static final String EXTENSION_WORD = ".doc";
	public static final String EXTENSION_HTML = ".html";
	
	/* format of report */
	private String reportFormat;
	
	//declaration of files used or generated per report instance
	private File fopConfigFile;
	private File xmlFile; 
	private File xsltFile;
	private File foFile; 
	private File foXsltFile;
	private File reportFile; 	
	
	public ReportServiceImpl(String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName) {
		try {
			//--------------------check for incorrect parameters------------------------
			if(fopConfigFileName == null || xsltFileName == null || reportFormat == null || reportFileName == null) {
				throw new ResourceException();
			}
			if(fopConfigFileName.isEmpty() || xsltFileName.isEmpty() || reportFormat.isEmpty() || reportFileName.isEmpty()) {
				throw new ResourceException();
			}
			
			//--------------------initialize report parameters & files---------------------
			//set current time stamp as part of filename for generated resource & output files
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			String currentTime = Integer.toString(calendar.get(Calendar.DATE)) + "_" +
								 Integer.toString(calendar.get(Calendar.MONTH)+1) + "_" +
								 Integer.toString(calendar.get(Calendar.YEAR)) + "_" +
								 Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + "_" + 
								 Integer.toString(calendar.get(Calendar.MINUTE)) + "_" + 
								 Integer.toString(calendar.get(Calendar.SECOND));
			
			this.reportFormat = reportFormat;
			
			//set files here
			this.fopConfigFile = new File(BASE_DIRECTORY, fopConfigFileName + EXTENSION_XML);
			this.xmlFile = new File(BASE_DIRECTORY, "xmlFile_"+currentTime + EXTENSION_XML);
			this.xsltFile = new File(BASE_DIRECTORY, xsltFileName + EXTENSION_XSLT);
			this.foFile = new File(BASE_DIRECTORY, "foFile_"+currentTime + EXTENSION_FO); 
			this.foXsltFile = new File(BASE_DIRECTORY, "fo2html" + EXTENSION_XSLT);
			
			//set report file name conditionally			
			if(this.reportFormat == MimeConstants.MIME_PDF) {				
				this.reportFile = new File(REPORT_DIRECTORY, reportFileName + "_" + currentTime + EXTENSION_PDF);
			}
			else if(this.reportFormat == MimeConstants.MIME_RTF || this.reportFormat == "WORD") {
				this.reportFile = new File(REPORT_DIRECTORY, reportFileName + EXTENSION_RTF);
			}
			else if(this.reportFormat == "HTML") {
				this.reportFile = new File(REPORT_DIRECTORY, reportFileName + "_" + currentTime + EXTENSION_HTML);
			} else {
				throw new ResourceException();
			}
		} catch(ResourceException e) {
			System.err.println("Report Parameters are INCORRECT.");
			e.printStackTrace();
		}		
	}

	//-------------getters & setters for files-------------//
	public String getReportFormat() {		
		return reportFormat;
	}

	public File getFopConfigFile() {
		return fopConfigFile;
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public File getXsltFile() {
		return xsltFile;
	}

	public File getFoFile() {
		return foFile;
	}
	
	public void setFoFile(File foFile) {
		this.foFile = foFile;
	}

	public File getFoXsltFile() {
		return foXsltFile;
	}

	public void setFoXsltFile(File foXsltFile) {
		this.foXsltFile = foXsltFile;
	}

	public File getReportFile() {
		return reportFile;
	}

	public void getXMLSource(XmlVO data) throws JAXBException, IOException  {		
		
		JAXBContext context;			 
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		context = JAXBContext.newInstance(data.getClass());
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(data, outStream);
		FileOutputStream str = new FileOutputStream(this.xmlFile);
		str.write(outStream.toByteArray());
		str.close(); 
	}
	
	private void createInternalElements(List<Object> currentObj, Element currentElement) {
		for(int k = 0; k < currentObj.size(); k++){
			
			Element internalElement = new Element(currentElement.getName()+"_"+(k+1));
			if(currentObj.get(k) != null){
				if(currentObj.get(k).getClass().getSimpleName().endsWith("Object[]")) {
					createInternalElements((Object[])currentObj.get(k), internalElement);
				}else if(currentObj.get(k).getClass().getSimpleName().endsWith("List")) {
					createInternalElements((List<Object>)currentObj.get(k), internalElement);
				} else if(currentObj.get(k).getClass().getSimpleName().equals("String")
						|| currentObj.get(k).getClass().getSimpleName().endsWith("Integer")
						|| currentObj.get(k).getClass().getSimpleName().equals("Long")
						|| currentObj.get(k).getClass().getSimpleName().equals("Boolean")
						|| currentObj.get(k).getClass().getSimpleName().equals("Date")) {
					internalElement.setText(currentObj.get(k).toString());
				}
			}else{
				internalElement.setText("");
			}
			currentElement.addContent(internalElement);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createInternalElements(Object[] currentObj, Element currentElement) {
		for(int k = 0; k < currentObj.length; k++){
			
			Element internalElement = new Element(currentElement.getName()+ "_" + (k + 1));
			if (currentObj[k] != null) {
				if (currentObj[k].getClass().getSimpleName().endsWith("Object[]")) {
					createInternalElements((Object[]) currentObj[k],internalElement);
				}else if (currentObj[k].getClass().getSimpleName().endsWith("List")) {
					createInternalElements((List<Object>) currentObj[k],internalElement);
				} else if (currentObj[k].getClass().getSimpleName().equals("String")
						|| currentObj[k].getClass().getSimpleName().endsWith("Integer")
						|| currentObj[k].getClass().getSimpleName().equals("Long")
						|| currentObj[k].getClass().getSimpleName().equals("Boolean")
						|| currentObj[k].getClass().getSimpleName().equals("Date")) {
					internalElement.setText(currentObj[k].toString());
				}
			}else{
				internalElement.setText("");
			}			
			currentElement.addContent(internalElement);
		}
	}
	
	public void getXMLSource(final Object[] reportFields, final String locale) throws Exception {					
		
			Element root = new Element("root");
			{
				Element configParamElement = new Element("locale");
				configParamElement.setText(locale);
				root.addContent(configParamElement);			
				configParamElement = null;
				
				configParamElement = new Element("outputFormat");	
				configParamElement.setText(reportFormat);								
				root.addContent(configParamElement);
				configParamElement = null;
				
				configParamElement = new Element("reportDate");				
				String xsltFileNameWithoutExtension = FilenameUtils.removeExtension(this.xsltFile.getName());
				CustomParameter reportDateFormatParameter = CustomParameter.findByName(CustomParameter.class, xsltFileNameWithoutExtension.toUpperCase() + "_REPORTDATE_FORMAT", "");
				if(reportDateFormatParameter!=null && reportDateFormatParameter.getValue()!=null) {					
					configParamElement.setText(FormaterUtil.formatDateToString(new Date(), reportDateFormatParameter.getValue(), locale));
				} else {
					configParamElement.setText(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.DB_DATEFORMAT, locale));
				}				
				root.addContent(configParamElement);
				configParamElement = null;				
			}
			
			for (int i = 0; i < reportFields.length; i++) {				
				String classType = reportFields[i].getClass().getSimpleName();
				if(classType.equals("String")){					
					Element singleElement = new Element("element_"+(i+1));					
					singleElement.setText(reportFields[i].toString());
					root.addContent(singleElement);					
				} else if(classType.endsWith("List")){					
					List report = (List) reportFields[i]; 
					Set s = new HashSet<Object>();				
					for (int j = 0; j < report.size(); j++) {						
						Element listElement = new Element("element_"+(i+1));
						if(report.get(j).getClass().getSimpleName().equals("Object[]")){
							createInternalElements(((Object[])report.get(j)), listElement);
						}else if(report.get(j).getClass().getSimpleName().endsWith("List")) {							
							createInternalElements((List<Object>)report.get(j), listElement);
						} else if(report.get(j).getClass().getSimpleName().equals("String")) {
							listElement.setText(report.get(j).toString());
						}
						root.addContent(listElement);
					}					
				} else if(classType.endsWith("Map")){					
					Map mapReport = (Map) reportFields[i]; 
					Iterator iter = mapReport.entrySet().iterator();
					while(iter.hasNext()){						
						Map.Entry entry = (Map.Entry)iter.next(); 
						if(entry != null){
							Element mapElement = new Element("element_"+(i+1));						
							if(entry.getKey().getClass().getSimpleName().endsWith("List")){
								List report = (List)entry.getValue();								
								for (int j = 0; j < report.size(); j++) {	
									Element mapKeyElement = new Element(mapElement.getName()+"_1");
									if(report.get(j).getClass().getSimpleName().endsWith("List")) {
										createInternalElements((List<Object>)report.get(j), mapKeyElement);										
									} else if(report.get(j).getClass().getSimpleName().equals("String")) {
										mapKeyElement.setText(report.get(j).toString());
									}
									mapElement.addContent(mapKeyElement);								
								}
								report = null;
							} else if(entry.getKey().getClass().getSimpleName().equals("String")) {
								Element mapKeyElement = new Element(mapElement.getName()+"_1");
								mapKeyElement.setText(entry.getKey().toString());	
								mapElement.addContent(mapKeyElement);
							}							
							if(entry.getValue().getClass().getSimpleName().endsWith("List")){
								List report = (List)entry.getValue();								
								for (int j = 0; j < report.size(); j++) {							
									Element mapValueElement = new Element(mapElement.getName()+"_2");
									if(report.get(j).getClass().getSimpleName().endsWith("List")) {
										createInternalElements((List<Object>)report.get(j), mapValueElement);										
									} else if(report.get(j).getClass().getSimpleName().equals("String")) {
										mapValueElement.setText(report.get(j).toString());
									}
									mapElement.addContent(mapValueElement);									
								}								
								report = null;
							} else if(entry.getValue().getClass().getSimpleName().equals("String")) {
								Element mapValueElement = new Element(mapElement.getName()+"_2");
								mapValueElement.setText(entry.getValue().toString());
								mapElement.addContent(mapValueElement);								
							}
							root.addContent(mapElement);
						}
					}	
					mapReport = null;
				}				
			}			
			
			FileOutputStream str = null;
			str = new FileOutputStream(xmlFile);
			XMLOutputter writer = new XMLOutputter();			
			writer.output(root, str);			
			
			str.close();			
	}
	
	public void convertXML2FO() throws IOException, TransformerException {
	    //Setup output
	    OutputStream out = new java.io.FileOutputStream(this.foFile);
	    
        //Setup XSLT
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(this.xsltFile));

        //Setup input for XSLT transformation
        Source src = new StreamSource(this.xmlFile);

        //Resulting SAX events (the generated FO) must be piped through to FOP
        Result res = new StreamResult(out);

        //Start XSLT transformation and FOP processing
        transformer.transform(src, res);
	    
        out.close();	    
	}
	
	public void handleHtmlTagsInFoFile() throws IOException {
		String content;
		
		InputStream inputStream = new FileInputStream(this.foFile);
		content = IOUtils.toString(inputStream, "UTF-8");
		
		content = content.replaceAll("&lt;b&gt;", "<fo:inline font-family='Mangalb' font-weight='bold'>");
		content = content.replaceAll("&lt;/b&gt;", "</fo:inline>");
		content = content.replaceAll("&lt;u&gt;", "<fo:inline text-decoration='underline'>");
		content = content.replaceAll("&lt;/u&gt;", "</fo:inline>");	
		
		IOUtils.closeQuietly(inputStream);
		
		OutputStream outputStream = new FileOutputStream(this.foFile);
		IOUtils.write(content, outputStream, "UTF-8");	
		IOUtils.closeQuietly(outputStream);
		 
	}
	
	public File convertFO2Report() throws TransformerException, SAXException, IOException {
		//TransformerException
		OutputStream out = null;
		fopFactory.setUserConfig(this.fopConfigFile);
    	 //fopFactory.setFontBaseURL("file:///C:/WINDOWS/Fonts");
		FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
		// configure foUserAgent as desired
//	    	System.out.println("width: " + foUserAgent.getPageWidth());
//	    	System.out.println("height: " + foUserAgent.getPageHeight());	    	 

        // Setup output stream.  Note: Using BufferedOutputStream
        // for performance reasons (helpful with FileOutputStreams).
        out = new FileOutputStream(this.reportFile);
        out = new BufferedOutputStream(out);

        // Construct fop with desired output format
        Fop fop = null;
        if(this.reportFormat == "WORD") {
        	fop = fopFactory.newFop(MimeConstants.MIME_RTF, foUserAgent, out);
        } else {
        	fop = fopFactory.newFop(this.reportFormat, foUserAgent, out);
        }	        	         
        System.out.println();
         	

        // Setup JAXP using identity transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); // identity transformer

        // Setup input stream	        
        Source src = new StreamSource(this.foFile);

        // Resulting SAX events (the generated FO) must be piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Start XSLT transformation and FOP processing
        transformer.transform(src, res);
        	        
        out.close();
		
		if(this.reportFormat.equals("WORD")) {
			this.copyRtfToWord();
		}
		
		return this.reportFile;
	}
	
	private void removeResourceFiles() {
		//for xml file
		this.xmlFile.delete();
		this.xmlFile = null;
		
		//for fo file
		this.foFile.delete();
		this.foFile = null;
	}
	
	public void copyRtfToWord() throws IOException, FileNotFoundException {
		File wordFile = null;
		//-------------------------if report is rtf file, copy it to word doc file in case word report is needed-------------------------//
		if(this.reportFormat.equals("WORD")) {
			//set current time stamp as part of filename for generated word file
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			String currentTime = Integer.toString(calendar.get(Calendar.DATE)) + "_" +
								 Integer.toString(calendar.get(Calendar.MONTH)+1) + "_" +
								 Integer.toString(calendar.get(Calendar.YEAR)) + "_" +
								 Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)) + "_" + 
								 Integer.toString(calendar.get(Calendar.MINUTE)) + "_" + 
								 Integer.toString(calendar.get(Calendar.SECOND));
			String reportFileNameWithoutExtension = FilenameUtils.removeExtension(this.reportFile.getName());
			wordFile = new File(ReportServiceImpl.REPORT_DIRECTORY, reportFileNameWithoutExtension  + "_" + currentTime + EXTENSION_WORD);
				
			InputStream inputStream = new FileInputStream(reportFile);
			OutputStream outputStream = new FileOutputStream(wordFile);
			
			String content = IOUtils.toString(inputStream, "UTF-8");			
			IOUtils.write(content, outputStream, "UTF-8");	
			
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);	
			
			this.reportFile.delete();
			this.reportFile = wordFile;
			
		}
	}
	
	public File generateReport(XmlVO data) throws Exception {
		//--------------------check for missing resources------------------------
		if(this.fopConfigFile == null || this.xsltFile == null || this.reportFormat == null || this.reportFile == null) {
			throw new ResourceException();
		}
		if(this.reportFormat.isEmpty()) {
			throw new ResourceException();
		}
		
		//generate xml file for report data
		this.getXMLSource(data);
		
		this.handleInternalHtmlTagBracketsInXmlFile();
		
		//generate fo file from xml using given xsl file
		this.convertXML2FO();
		
		//handle html tags formatting in fo file
		this.handleHtmlTagsInFoFile();
        
		//generate report in required format using fo processor
		if(this.reportFormat.equals("HTML")) {
			this.convertFO2HTML();
		} else {
			this.convertFO2Report();
		}			
		
		//finally remove resource files after report generated
		this.removeResourceFiles();	
		
		return this.reportFile;
	}
	
	public File generateReport(final Object[] reportFields, final String locale) throws Exception {
		
		//--------------------check for missing resources------------------------
		if(this.fopConfigFile == null || this.xsltFile == null || this.reportFormat == null || this.reportFile == null) {
			throw new ResourceException();
		}
		if(this.reportFormat.isEmpty()) {
			throw new ResourceException();
		}
		
		//generate xml file for report data
		this.getXMLSource(reportFields, locale);
		
		this.handleInternalHtmlTagBracketsInXmlFile();
		
		//generate fo file from xml using given xsl file
		this.convertXML2FO();
		
		//handle html tags formatting in fo file
		this.handleHtmlTagsInFoFile();
        
		//generate report in required format using fo processor
		if(this.reportFormat.equals("HTML")) {
			this.convertFO2HTML();
		} else {
			this.convertFO2Report();
		}			
		
		//finally remove resource files after report generated
		this.removeResourceFiles();
		
		return this.reportFile;
	}
	
	public void handleInternalHtmlTagBracketsInXmlFile() {
		try {
			String content;
			
			InputStream inputStream = new FileInputStream(this.xmlFile);
			content = IOUtils.toString(inputStream, "UTF-8");
			
			//add proper angular brackets/entities for internal html tags embedded in data
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&gt;", ">");
			content = content.replaceAll("&quot;", "\"");			
			
			//convert improper html tags into proper xhtml tags
			content = content.replaceAll("br", "br/");
			
			//handle character entities
			content = content.replaceAll("&amp;tilde;", "&#126;");
			content = content.replaceAll("&amp;florin;", "&#131;");
			content = content.replaceAll("&amp;elip;", "&#133;");
			content = content.replaceAll("&amp;dag;", "&#134;");
			content = content.replaceAll("&amp;ddag;", "&#135;");
			content = content.replaceAll("&amp;cflex;", "&#136;");
			content = content.replaceAll("&amp;permil;", "&#137;");
			content = content.replaceAll("&amp;uscore;", "&#138;");
			content = content.replaceAll("&amp;OElig;", "&#140;");
			content = content.replaceAll("&amp;lsquo;", "&#145;");
			content = content.replaceAll("&amp;rsquo;", "&#146;");
			content = content.replaceAll("&amp;ldquo;", "&#147;");
			content = content.replaceAll("&amp;rdquo;", "&#148;");
			content = content.replaceAll("&amp;bullet;", "&#149;");
			content = content.replaceAll("&amp;endash;", "&#150;");
			content = content.replaceAll("&amp;emdash;", "&#151;");
			content = content.replaceAll("&amp;trade;", "&#153;");
			content = content.replaceAll("&amp;oelig;", "&#156;");
			content = content.replaceAll("&amp;Yuml;", "&#159;");
			content = content.replaceAll("&amp;nbsp;", "&#160;");
			content = content.replaceAll("&amp;iexcl;", "&#161;");
			content = content.replaceAll("&amp;cent;", "&#162;");
			content = content.replaceAll("&amp;pound;", "&#163;");
			content = content.replaceAll("&amp;curren;", "&#164;");
			content = content.replaceAll("&amp;yen;", "&#165;");
			content = content.replaceAll("&amp;brvbar;", "&#166;");
			content = content.replaceAll("&amp;sect;", "&#167;");
			content = content.replaceAll("&amp;uml;", "&#168;");
			content = content.replaceAll("&amp;copy;", "&#169;");
			content = content.replaceAll("&amp;ordf;", "&#170;");
			content = content.replaceAll("&amp;laquo;", "&#171;");
			content = content.replaceAll("&amp;not;", "&#172;");
			content = content.replaceAll("&amp;shy;", "&#173;");
			content = content.replaceAll("&amp;reg;", "&#174;");
			content = content.replaceAll("&amp;macr;", "&#175;");
			content = content.replaceAll("&amp;deg;", "&#176;");
			content = content.replaceAll("&amp;plusmn;", "&#177;");
			content = content.replaceAll("&amp;sup2;", "&#178;");
			content = content.replaceAll("&amp;sup3;", "&#179;");
			content = content.replaceAll("&amp;acute;", "&#180;");
			content = content.replaceAll("&amp;micro;", "&#181;");
			content = content.replaceAll("&amp;para;", "&#182;");
			content = content.replaceAll("&amp;middot;", "&#183;");
			content = content.replaceAll("&amp;cedil;", "&#184;");
			content = content.replaceAll("&amp;sup1;", "&#185;");
			content = content.replaceAll("&amp;ordm;", "&#186;");
			content = content.replaceAll("&amp;raquo;", "&#187;");
			content = content.replaceAll("&amp;frac14;", "&#188;");
			content = content.replaceAll("&amp;frac12;", "&#189;");
			content = content.replaceAll("&amp;frac34;", "&#190;");
			content = content.replaceAll("&amp;iquest;", "&#191;");
			content = content.replaceAll("&amp;Agrave;", "&#192;");
			content = content.replaceAll("&amp;Aacute;", "&#193;");
			content = content.replaceAll("&amp;Acirc;", "&#194;");
			content = content.replaceAll("&amp;Atilde;", "&#195;");
			content = content.replaceAll("&amp;Auml;", "&#196;");
			content = content.replaceAll("&amp;Aring;", "&#197;");
			content = content.replaceAll("&amp;AElig;", "&#198;");
			content = content.replaceAll("&amp;Ccedil;", "&#199;");
			content = content.replaceAll("&amp;Egrave;", "&#200;");
			content = content.replaceAll("&amp;Eacute;", "&#201;");
			content = content.replaceAll("&amp;Ecirc;", "&#202;");
			content = content.replaceAll("&amp;Euml;", "&#203;");
			content = content.replaceAll("&amp;Igrave;", "&#204;");
			content = content.replaceAll("&amp;Iacute;", "&#205;");
			content = content.replaceAll("&amp;Icirc;", "&#206;");
			content = content.replaceAll("&amp;Iuml;", "&#207;");
			content = content.replaceAll("&amp;ETH;", "&#208;");
			content = content.replaceAll("&amp;Ntilde;", "&#209;");
			content = content.replaceAll("&amp;Ograve;", "&#210;");
			content = content.replaceAll("&amp;Oacute;", "&#211;");
			content = content.replaceAll("&amp;Ocirc;", "&#212;");
			content = content.replaceAll("&amp;Otilde;", "&#213;");
			content = content.replaceAll("&amp;Ouml;", "&#214;");
			content = content.replaceAll("&amp;times;", "&#215;");
			content = content.replaceAll("&amp;Oslash;", "&#216;");
			content = content.replaceAll("&amp;Ugrave;", "&#217;");
			content = content.replaceAll("&amp;Uacute;", "&#218;");
			content = content.replaceAll("&amp;Ucirc;", "&#219;");
			content = content.replaceAll("&amp;Uuml;", "&#220;");
			content = content.replaceAll("&amp;Yacute;", "&#221;");
			content = content.replaceAll("&amp;THORN;", "&#222;");
			content = content.replaceAll("&amp;szlig;", "&#223;");
			content = content.replaceAll("&amp;agrave;", "&#224;");
			content = content.replaceAll("&amp;aacute;", "&#225;");
			content = content.replaceAll("&amp;acirc;", "&#226;");
			content = content.replaceAll("&amp;atilde;", "&#227;");
			content = content.replaceAll("&amp;auml;", "&#228;");
			content = content.replaceAll("&amp;aring;", "&#229;");
			content = content.replaceAll("&amp;aelig;", "&#230;");
			content = content.replaceAll("&amp;ccedil;", "&#231;");
			content = content.replaceAll("&amp;egrave;", "&#232;");
			content = content.replaceAll("&amp;eacute;", "&#233;");
			content = content.replaceAll("&amp;ecirc;", "&#234;");
			content = content.replaceAll("&amp;euml;", "&#235;");
			content = content.replaceAll("&amp;igrave;", "&#236;");
			content = content.replaceAll("&amp;iacute;", "&#237;");
			content = content.replaceAll("&amp;icirc;", "&#238;");
			content = content.replaceAll("&amp;iuml;", "&#239;");
			content = content.replaceAll("&amp;eth;", "&#240;");
			content = content.replaceAll("&amp;ntilde;", "&#241;");
			content = content.replaceAll("&amp;ograve;", "&#242;");
			content = content.replaceAll("&amp;oacute;", "&#243;");
			content = content.replaceAll("&amp;ocirc;", "&#244;");
			content = content.replaceAll("&amp;otilde;", "&#245;");
			content = content.replaceAll("&amp;ouml;", "&#246;");
			content = content.replaceAll("&amp;oslash;", "&#248;");
			content = content.replaceAll("&amp;ugrave;", "&#249;");
			content = content.replaceAll("&amp;uacute;", "&#250;");
			content = content.replaceAll("&amp;ucirc;", "&#251;");
			content = content.replaceAll("&amp;uuml;", "&#252;");
			content = content.replaceAll("&amp;yacute;", "&#253;");
			content = content.replaceAll("&amp;thorn;", "&#254;");
			content = content.replaceAll("&amp;yuml;", "&#255;");
			content = content.replaceAll("&amp;euro;", "&#x20AC;");
									
			IOUtils.closeQuietly(inputStream);
			
			OutputStream outputStream = new FileOutputStream(this.xmlFile);
			IOUtils.write(content, outputStream, "UTF-8");	
			IOUtils.closeQuietly(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} 
	}
	
	public void convertFO2HTML() throws SecurityException, TransformerException, IOException{
	    //Setup output
		OutputStream out = null;
		try{
			out = new java.io.FileOutputStream(this.reportFile);
		
	    
	        //Setup XSLT
	        TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer transformer = factory.newTransformer(new StreamSource(this.foXsltFile));
		
	
	        //Setup input for XSLT transformation
	        Source src = new StreamSource(this.foFile);
	
	        //Resulting SAX events (the generated FO) must be piped through to FOP
	        Result res = new StreamResult(out);
	
	        //Start XSLT transformation and FOP processing
		}catch (Exception e) {
			if(e instanceof FileNotFoundException) {
				throw ((FileNotFoundException)e); 
			}else if(e instanceof TransformerConfigurationException) {
				throw (FileNotFoundException)e; 
			}else if(e instanceof TransformerException) {
				throw (TransformerException)e;
			}
		}finally{
			out.close();
		}
	}
 
}