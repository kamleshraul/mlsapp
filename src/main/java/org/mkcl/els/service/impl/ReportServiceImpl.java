package org.mkcl.els.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
 















import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.mkcl.els.common.exception.ResourceException;
import org.mkcl.els.common.util.ApplicationConstants;
import org.mkcl.els.common.util.FormaterUtil;
import org.mkcl.els.common.xmlvo.XmlVO;
import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.service.IReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;
 
public class ReportServiceImpl implements IReportService {
	
	// configure fopFactory as desired
	private FopFactory fopFactory = FopFactory.newInstance();
	
	public static final String BASE_DIRECTORY;
	
	public static final String REPORT_DIRECTORY;
	
	public static final String IMAGE_DIRECTORY;
	
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
		
		//create Images Directory if it does not exist
		File imageDirectory = new File(BASE_DIRECTORY + "\\images");
		if(!imageDirectory.exists()) {
			if (imageDirectory.mkdir()) {
				System.out.println("Images Directory is created!");
				IMAGE_DIRECTORY = imageDirectory.getAbsolutePath();
			} else {
				IMAGE_DIRECTORY = null;
				throw new ResourceException("Failed to create Images Directory for FOP Reports!");					
			}						
		} else {
			IMAGE_DIRECTORY = imageDirectory.getAbsolutePath();
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
	private File htmlFile;
	private File xhtmlFile;
	private List<File> imageFiles;
	private File xsltFile;	
	private File foFile; 
	private File foXsltFile;
	private File reportFile; 		
	
	public ReportServiceImpl(String fopConfigFileName, String xsltFileName, String reportFormat, String reportFileName) throws Exception {
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
								 Integer.toString(calendar.get(Calendar.SECOND)) + "_" + 
								 Integer.toString(calendar.get(Calendar.MILLISECOND));
			
			this.reportFormat = reportFormat;
			
			//set files here
			this.fopConfigFile = new File(BASE_DIRECTORY, fopConfigFileName + EXTENSION_XML);
			this.xmlFile = new File(BASE_DIRECTORY, "xmlFile_" + reportFileName + "_" +currentTime + EXTENSION_XML);
			this.imageFiles = new ArrayList<File>();
			this.xsltFile = new File(BASE_DIRECTORY, xsltFileName + EXTENSION_XSLT);
			if(!this.xsltFile.exists()) {
				throw new FileNotFoundException(ApplicationConstants.XSLT_FILE_NOT_FOUND);
			}
			this.foFile = new File(BASE_DIRECTORY, "foFile_" + reportFileName + "_" + currentTime + EXTENSION_FO); 
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
	
	public ReportServiceImpl(String fopConfigFileName, String htmlFileName, String xsltFileName, String reportFormat, String reportFileName) throws Exception {
		try {
			//--------------------check for incorrect parameters------------------------
			if(fopConfigFileName == null || htmlFileName == null || xsltFileName == null || reportFormat == null || reportFileName == null) {
				throw new ResourceException();
			}
			if(fopConfigFileName.isEmpty() || htmlFileName.isEmpty() || xsltFileName.isEmpty() || reportFormat.isEmpty() || reportFileName.isEmpty()) {
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
			this.htmlFile = new File(BASE_DIRECTORY, htmlFileName + EXTENSION_HTML);
			if(!this.htmlFile.exists()) {
				throw new FileNotFoundException(ApplicationConstants.HTML_FILE_NOT_FOUND);
			}
			this.xhtmlFile = new File(BASE_DIRECTORY, "xhtmlFile_"+currentTime + EXTENSION_XML);
			this.xsltFile = new File(BASE_DIRECTORY, xsltFileName + EXTENSION_XSLT);			
			if(!this.xsltFile.exists()) {
				throw new FileNotFoundException(ApplicationConstants.XSLT_FILE_NOT_FOUND);
			}
			this.foFile = new File(BASE_DIRECTORY, "foFile_"+currentTime + EXTENSION_FO); 
			
			//set report file name conditionally			
			if(this.reportFormat == MimeConstants.MIME_PDF) {				
				this.reportFile = new File(REPORT_DIRECTORY, reportFileName + "_" + currentTime + EXTENSION_PDF);
			}
			else if(this.reportFormat == MimeConstants.MIME_RTF || this.reportFormat == "WORD") {
				this.reportFile = new File(REPORT_DIRECTORY, reportFileName + EXTENSION_RTF);
			}
			else {
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
	
	public File getHtmlFile() {
		return htmlFile;
	}
	
	public File getXhtmlFile() {
		return xhtmlFile;
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
	
	@SuppressWarnings("unchecked")
	private void createInternalElements(List<Object> currentObj, Element currentElement, String locale) {
		for(int k = 0; k < currentObj.size(); k++){
			
			Element internalElement = new Element(currentElement.getName()+"_"+(k+1));
			if(currentObj.get(k) != null){
				if(currentObj.get(k).getClass().getSimpleName().endsWith("Object[]")) {
					createInternalElements((Object[])currentObj.get(k), internalElement, locale);
				}else if(currentObj.get(k).getClass().getSimpleName().endsWith("List")) {
					createInternalElements((List<Object>)currentObj.get(k), internalElement, locale);
				} else if(currentObj.get(k).getClass().getSimpleName().equals("String")) {
					if(currentObj.get(k).toString().startsWith("currency")) {
						String formattedCurrencyValue = formatValueForIndianCurrency(currentObj.get(k).toString(), locale);
						internalElement.setText(formattedCurrencyValue);								
					} else {
						internalElement.setText(currentObj.get(k).toString());
					}
				} else if(currentObj.get(k).getClass().getSimpleName().endsWith("Integer")
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
	private void createInternalElements(Object[] currentObj, Element currentElement, String locale) {
		for(int k = 0; k < currentObj.length; k++){
			
			Element internalElement = new Element(currentElement.getName()+ "_" + (k + 1));
			if (currentObj[k] != null) {
				if (currentObj[k].getClass().getSimpleName().endsWith("Object[]")) {
					createInternalElements((Object[]) currentObj[k],internalElement,locale);
				} else if(currentObj[k].getClass().getSimpleName().endsWith("List")) {
					createInternalElements((List<Object>) currentObj[k],internalElement,locale);
				} else if(currentObj[k].getClass().getSimpleName().equals("String")) {
					if(currentObj[k].toString().startsWith("currency")) {
						String formattedCurrencyValue = formatValueForIndianCurrency(currentObj[k].toString(), locale);
						internalElement.setText(formattedCurrencyValue);								
					} else {
						internalElement.setText(currentObj[k].toString());
					}
				} else if(currentObj[k].getClass().getSimpleName().endsWith("Integer")
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
	
	@SuppressWarnings("unchecked")
	public void getXMLSource(final Object[] reportFields, final String locale) throws Exception {					
			
			String xsltFileNameWithoutExtension = FilenameUtils.removeExtension(this.xsltFile.getName());
			
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
				
				configParamElement = new Element("moduleName");
				CustomParameter moduleNameCP = CustomParameter.findByName(CustomParameter.class, xsltFileNameWithoutExtension.toUpperCase() + "_MODULE_NAME", "");
				if(moduleNameCP!=null && moduleNameCP.getValue()!=null && !moduleNameCP.getValue().isEmpty()) {
					configParamElement.setText(moduleNameCP.getValue());
				} else {
					configParamElement.setText("generic");
				}
				root.addContent(configParamElement);
				configParamElement = null;
				
				configParamElement = new Element("reportDate");			
				CustomParameter reportDateFormatParameter = CustomParameter.findByName(CustomParameter.class, xsltFileNameWithoutExtension.toUpperCase() + "_REPORTDATE_FORMAT", "");
				if(reportDateFormatParameter!=null && reportDateFormatParameter.getValue()!=null) {					
					configParamElement.setText(FormaterUtil.formatDateToString(new Date(), reportDateFormatParameter.getValue(), locale));
				} else {
					configParamElement.setText(FormaterUtil.formatDateToString(new Date(), ApplicationConstants.REPORT_DATEFORMAT, locale));
				}				
				root.addContent(configParamElement);
				configParamElement = null;				
			}
			System.out.println("PDF Started: " + new Date());
			if(reportFields!=null && reportFields.length>0) {
				for (int i = 0; i < reportFields.length; i++) {
					if(reportFields[i]!=null) {
						String classType = reportFields[i].getClass().getSimpleName();
						if(classType.equals("String")){					
							Element singleElement = new Element("element_"+(i+1));		
							if(reportFields[i].toString().startsWith("data:image")) {
								String image_filename = generateImageElement("element_"+Integer.toString(i+1), reportFields[i].toString());
						        singleElement.setText(image_filename);
							} else if(reportFields[i].toString().startsWith("currency")) {
								String formattedCurrencyValue = formatValueForIndianCurrency(reportFields[i].toString(), locale);
								singleElement.setText(formattedCurrencyValue);								
							} else {
								singleElement.setText(reportFields[i].toString());
							}							
							root.addContent(singleElement);					
						} else if(classType.equals("Object[]")){				
							Element listElement = new Element("element_"+(i+1));		
							createInternalElements(((Object[])reportFields[i]), listElement, locale);
							//singleElement.setText(reportFields[i].toString());
							root.addContent(listElement);					
						} else if(classType.endsWith("List")){					
							@SuppressWarnings("rawtypes")
							List report = (List) reportFields[i]; 
							for (int j = 0; j < report.size(); j++) {						
								Element listElement = new Element("element_"+(i+1));
								if(report.get(j).getClass().getSimpleName().equals("Object[]")){
									createInternalElements(((Object[])report.get(j)), listElement, locale);
								}else if(report.get(j).getClass().getSimpleName().endsWith("List")) {							
									createInternalElements((List<Object>)report.get(j), listElement, locale);
								} else if(report.get(j).getClass().getSimpleName().equals("String")) {
									if(report.get(j).toString().startsWith("data:image")) {
										String image_filename = generateImageElement("element_"+Integer.toString(i+1)+"_"+Integer.toString(j+1), report.get(j).toString());
										listElement.setText(image_filename);
									} else if(report.get(j).toString().startsWith("currency")) {
										String formattedCurrencyValue = formatValueForIndianCurrency(report.get(j).toString(), locale);
										listElement.setText(formattedCurrencyValue);								
									} else {
										listElement.setText(report.get(j).toString());
									}									
								}
								root.addContent(listElement);
							}					
						} else if(classType.endsWith("Map")){					
							@SuppressWarnings("rawtypes")
							Map mapReport = (Map) reportFields[i]; 
							@SuppressWarnings("rawtypes")
							Iterator iter = mapReport.entrySet().iterator();
							while(iter.hasNext()){						
								@SuppressWarnings("rawtypes")
								Map.Entry entry = (Map.Entry)iter.next(); 
								if(entry != null){
									Element mapElement = new Element("element_"+(i+1));						
									if(entry.getKey().getClass().getSimpleName().endsWith("List")){
										@SuppressWarnings("rawtypes")
										List report = (List)entry.getKey();								
										for (int j = 0; j < report.size(); j++) {	
											Element mapKeyElement = new Element(mapElement.getName()+"_1");
											if(report.get(j).getClass().getSimpleName().endsWith("List")) {
												createInternalElements((List<Object>)report.get(j), mapKeyElement, locale);										
											} else if(report.get(j).getClass().getSimpleName().equals("String")) {
												if(report.get(j).toString().startsWith("data:image")) {
													String image_filename = generateImageElement(mapElement.getName()+"_"+Integer.toString(i+1)+"_"+Integer.toString(j+1)+"_1", report.get(j).toString());
													mapKeyElement.setText(image_filename);
												} else if(report.get(j).toString().startsWith("currency")) {
													String formattedCurrencyValue = formatValueForIndianCurrency(report.get(j).toString(), locale);
													mapKeyElement.setText(formattedCurrencyValue);								
												} else {
													mapKeyElement.setText(report.get(j).toString());
												}
											}
											mapElement.addContent(mapKeyElement);								
										}
										report = null;
									} else if(entry.getKey().getClass().getSimpleName().equals("String")) {
										Element mapKeyElement = new Element(mapElement.getName()+"_1");
										if(entry.getKey().toString().startsWith("data:image")) {
											String image_filename = generateImageElement(mapElement.getName()+"_"+Integer.toString(i+1)+"_1", entry.getKey().toString());
											mapKeyElement.setText(image_filename);
										} else if(entry.getKey().toString().startsWith("currency")) {
											String formattedCurrencyValue = formatValueForIndianCurrency(entry.getKey().toString(), locale);
											mapKeyElement.setText(formattedCurrencyValue);								
										} else {
											mapKeyElement.setText(entry.getKey().toString());
										}										
										mapElement.addContent(mapKeyElement);
									}							
									if(entry.getValue().getClass().getSimpleName().endsWith("List")){
										@SuppressWarnings("rawtypes")
										List report = (List)entry.getValue();								
										for (int j = 0; j < report.size(); j++) {							
											Element mapValueElement = new Element(mapElement.getName()+"_2");
											if(report.get(j).getClass().getSimpleName().endsWith("List")) {
												createInternalElements((List<Object>)report.get(j), mapValueElement, locale);										
											} else if(report.get(j).getClass().getSimpleName().equals("Object[]")){
												createInternalElements(((Object[])report.get(j)), mapValueElement, locale);					
											} else if(report.get(j).getClass().getSimpleName().equals("String")) {
												mapValueElement.setText(report.get(j).toString());
												if(report.get(j).toString().startsWith("data:image")) {
													String image_filename = generateImageElement(mapElement.getName()+"_"+Integer.toString(i+1)+"_"+Integer.toString(j+1)+"_2", report.get(j).toString());
													mapValueElement.setText(image_filename);
												} else if(report.get(j).toString().startsWith("currency")) {
													String formattedCurrencyValue = formatValueForIndianCurrency(report.get(j).toString(), locale);
													mapValueElement.setText(formattedCurrencyValue);								
												} else {
													mapValueElement.setText(report.get(j).toString());
												}
											}
											mapElement.addContent(mapValueElement);									
										}								
										report = null;
									} else if(entry.getValue().getClass().getSimpleName().equals("String")) {
										Element mapValueElement = new Element(mapElement.getName()+"_2");
										if(entry.getValue().toString().startsWith("data:image")) {
											String image_filename = generateImageElement(mapElement.getName()+"_"+Integer.toString(i+1)+"_2", entry.getValue().toString());
											mapValueElement.setText(image_filename);
										} else if(entry.getValue().toString().startsWith("currency")) {
											String formattedCurrencyValue = formatValueForIndianCurrency(entry.getValue().toString(), locale);
											mapValueElement.setText(formattedCurrencyValue);							
										} else {
											mapValueElement.setText(entry.getValue().toString());
										}
										mapElement.addContent(mapValueElement);								
									}
									root.addContent(mapElement);
								}
							}	
							mapReport = null;
						}
					}									
				}
			}						
			
			FileOutputStream str = null;
			str = new FileOutputStream(xmlFile);
			XMLOutputter writer = new XMLOutputter();			
			writer.output(root, str);			
			
			str.close();			
	}
	
	private String generateImageElement(String element_position, String image_element_source) throws Exception {
		if(element_position!=null && !element_position.isEmpty()
				&& image_element_source!=null && !image_element_source.isEmpty()) {
			image_element_source = image_element_source.replaceFirst("data:image/png;base64,", "");
			//check java version & use decode method accordingly
			byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image_element_source);
			byte[] decodedBytesAlt = Base64.decodeBase64(image_element_source);
	        BufferedImage bfi = ImageIO.read(new ByteArrayInputStream(decodedBytes));    
	        String reportFileNameWithoutExtension = FilenameUtils.removeExtension(this.reportFile.getName());
	        File imageFile = new File(IMAGE_DIRECTORY, reportFileNameWithoutExtension+"_"+(element_position)+".png");
	        ImageIO.write(bfi , "png", imageFile);
	        bfi.flush();
	        imageFiles.add(imageFile);
	        return imageFile.getName();
		} else {
			throw new Exception();
		}		      
	}
	
	private String formatValueForIndianCurrency(String value, String locale) {
		String formattedCurrencyValue = value;
		BigDecimal currencyValue = FormaterUtil.parseNumberForIndianCurrency(value.split(":")[1], locale);
		if(currencyValue!=null) {
			if(value.split(":")[0].equals("currencyWithSymbol")) {
				formattedCurrencyValue = FormaterUtil.formatNumberForIndianCurrencyWithSymbol(currencyValue, locale);
			} else if(value.split(":")[0].equals("currency")) {
				formattedCurrencyValue = FormaterUtil.formatNumberForIndianCurrency(currencyValue, locale);
			}			
		}
		return formattedCurrencyValue;
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
	
	public void convertXHTML2FO() throws IOException, TransformerException {
	    //Setup output
	    OutputStream out = new java.io.FileOutputStream(this.foFile);
	    
        //Setup XSLT
	    //xalan transformer factory
        //TransformerFactory factory = TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null); //before upgrade to java EE 7
        TransformerFactory factory = TransformerFactory.newInstance(); //after upgrade to java EE 7
	    
	    Transformer transformer = factory.newTransformer(new StreamSource(this.xsltFile));

        //Setup input for XSLT transformation
        Source src = new StreamSource(this.xhtmlFile);

        //Resulting SAX events (the generated FO) must be piped through to FOP
        Result res = new StreamResult(out);

        //Start XSLT transformation and FOP processing
        transformer.transform(src, res);
	    
        out.close();	    
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
		if(this.xmlFile!=null && this.xmlFile.exists()) {
			this.xmlFile.delete();
			this.xmlFile = null;
		}		
		
		//for fo file
		if(this.foFile!=null && this.foFile.exists()) {
			this.foFile.delete();
			this.foFile = null;
		}
		
		//for html file
		if(this.htmlFile!=null && this.htmlFile.exists()) {
			this.htmlFile.delete();
			this.htmlFile = null;
		}
		
		//for xhtml file
		if(this.xhtmlFile!=null && this.xhtmlFile.exists()) {
			this.xhtmlFile.delete();
			this.xhtmlFile = null;
		}
		
		//for image files
		if(this.imageFiles!=null && this.imageFiles.size()>0) {
			for(File imageFile: this.imageFiles) {
				if(imageFile.exists()) {
					imageFile.delete();
				}
			}
			this.imageFiles = null;
		}
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
		
		//generate images for image elements
		if(data.getIsImageElementPresent()) {
			handleImageElementsInXmlFileBasedOnVO();
		}		
		
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
		System.out.println("PDF Ended: " + new Date());
		//finally remove resource files after report generated
		this.removeResourceFiles();
		System.out.println("Images Removed: " + new Date());
		
		return this.reportFile;
	}
	
	public File generateReportFromHtml() throws Exception {
		
		//--------------------check for missing resources------------------------
		if(this.fopConfigFile == null || this.xsltFile == null || this.htmlFile == null || this.reportFormat == null || this.reportFile == null) {
			throw new ResourceException();
		}
		if(this.reportFormat.isEmpty()) {
			throw new ResourceException();
		}	
		
		this.handleInternalHtmlTagBracketsInHtmlFile();
		
		//convert given html to well formed xhtml file using jtidy
		this.getXhtmlSource();	
		
		this.handleInternalHtmlTagBracketsInXHtmlFile();
		
		//generate fo file from xhtml using xhtml-to-fo xsl file
		this.convertXHTML2FO();
		
		//generate report in required format using fo processor
		this.convertFO2Report();
		
		//finally remove resource files after report generated
		this.removeResourceFiles();	
		
		return this.reportFile;
	}
	
	public void getXhtmlSource() throws FileNotFoundException {
		Tidy tidy = new Tidy(); //HTML parser and pretty printer.
		tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
		tidy.setXHTML(true);
		tidy.parse(new FileInputStream(this.htmlFile), new FileOutputStream(this.xhtmlFile));
	}
	
	private String handleCharacterEntities(String content) {
		content = content.replaceAll("&amp;#10;", "<br/>");
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
		
		content = content.replaceAll("&tilde;", "&#126;");
		content = content.replaceAll("&florin;", "&#131;");
		content = content.replaceAll("&elip;", "&#133;");
		content = content.replaceAll("&dag;", "&#134;");
		content = content.replaceAll("&ddag;", "&#135;");
		content = content.replaceAll("&cflex;", "&#136;");
		content = content.replaceAll("&permil;", "&#137;");
		content = content.replaceAll("&uscore;", "&#138;");
		content = content.replaceAll("&OElig;", "&#140;");
		content = content.replaceAll("&lsquo;", "&#145;");
		content = content.replaceAll("&rsquo;", "&#146;");
		content = content.replaceAll("&ldquo;", "&#147;");
		content = content.replaceAll("&rdquo;", "&#148;");
		content = content.replaceAll("&bullet;", "&#149;");
		content = content.replaceAll("&endash;", "&#150;");
		content = content.replaceAll("&emdash;", "&#151;");
		content = content.replaceAll("&trade;", "&#153;");
		content = content.replaceAll("&oelig;", "&#156;");
		content = content.replaceAll("&Yuml;", "&#159;");
		content = content.replaceAll("&nbsp;", "&#160;");
		content = content.replaceAll("&iexcl;", "&#161;");
		content = content.replaceAll("&cent;", "&#162;");
		content = content.replaceAll("&pound;", "&#163;");
		content = content.replaceAll("&curren;", "&#164;");
		content = content.replaceAll("&yen;", "&#165;");
		content = content.replaceAll("&brvbar;", "&#166;");
		content = content.replaceAll("&sect;", "&#167;");
		content = content.replaceAll("&uml;", "&#168;");
		content = content.replaceAll("&copy;", "&#169;");
		content = content.replaceAll("&ordf;", "&#170;");
		content = content.replaceAll("&laquo;", "&#171;");
		content = content.replaceAll("&not;", "&#172;");
		content = content.replaceAll("&shy;", "&#173;");
		content = content.replaceAll("&reg;", "&#174;");
		content = content.replaceAll("&macr;", "&#175;");
		content = content.replaceAll("&deg;", "&#176;");
		content = content.replaceAll("&plusmn;", "&#177;");
		content = content.replaceAll("&sup2;", "&#178;");
		content = content.replaceAll("&sup3;", "&#179;");
		content = content.replaceAll("&acute;", "&#180;");
		content = content.replaceAll("&micro;", "&#181;");
		content = content.replaceAll("&para;", "&#182;");
		content = content.replaceAll("&middot;", "&#183;");
		content = content.replaceAll("&cedil;", "&#184;");
		content = content.replaceAll("&sup1;", "&#185;");
		content = content.replaceAll("&ordm;", "&#186;");
		content = content.replaceAll("&raquo;", "&#187;");
		content = content.replaceAll("&frac14;", "&#188;");
		content = content.replaceAll("&frac12;", "&#189;");
		content = content.replaceAll("&frac34;", "&#190;");
		content = content.replaceAll("&iquest;", "&#191;");
		content = content.replaceAll("&Agrave;", "&#192;");
		content = content.replaceAll("&Aacute;", "&#193;");
		content = content.replaceAll("&Acirc;", "&#194;");
		content = content.replaceAll("&Atilde;", "&#195;");
		content = content.replaceAll("&Auml;", "&#196;");
		content = content.replaceAll("&Aring;", "&#197;");
		content = content.replaceAll("&AElig;", "&#198;");
		content = content.replaceAll("&Ccedil;", "&#199;");
		content = content.replaceAll("&Egrave;", "&#200;");
		content = content.replaceAll("&Eacute;", "&#201;");
		content = content.replaceAll("&Ecirc;", "&#202;");
		content = content.replaceAll("&Euml;", "&#203;");
		content = content.replaceAll("&Igrave;", "&#204;");
		content = content.replaceAll("&Iacute;", "&#205;");
		content = content.replaceAll("&Icirc;", "&#206;");
		content = content.replaceAll("&Iuml;", "&#207;");
		content = content.replaceAll("&ETH;", "&#208;");
		content = content.replaceAll("&Ntilde;", "&#209;");
		content = content.replaceAll("&Ograve;", "&#210;");
		content = content.replaceAll("&Oacute;", "&#211;");
		content = content.replaceAll("&Ocirc;", "&#212;");
		content = content.replaceAll("&Otilde;", "&#213;");
		content = content.replaceAll("&Ouml;", "&#214;");
		content = content.replaceAll("&times;", "&#215;");
		content = content.replaceAll("&Oslash;", "&#216;");
		content = content.replaceAll("&Ugrave;", "&#217;");
		content = content.replaceAll("&Uacute;", "&#218;");
		content = content.replaceAll("&Ucirc;", "&#219;");
		content = content.replaceAll("&Uuml;", "&#220;");
		content = content.replaceAll("&Yacute;", "&#221;");
		content = content.replaceAll("&THORN;", "&#222;");
		content = content.replaceAll("&szlig;", "&#223;");
		content = content.replaceAll("&agrave;", "&#224;");
		content = content.replaceAll("&aacute;", "&#225;");
		content = content.replaceAll("&acirc;", "&#226;");
		content = content.replaceAll("&atilde;", "&#227;");
		content = content.replaceAll("&auml;", "&#228;");
		content = content.replaceAll("&aring;", "&#229;");
		content = content.replaceAll("&aelig;", "&#230;");
		content = content.replaceAll("&ccedil;", "&#231;");
		content = content.replaceAll("&egrave;", "&#232;");
		content = content.replaceAll("&eacute;", "&#233;");
		content = content.replaceAll("&ecirc;", "&#234;");
		content = content.replaceAll("&euml;", "&#235;");
		content = content.replaceAll("&igrave;", "&#236;");
		content = content.replaceAll("&iacute;", "&#237;");
		content = content.replaceAll("&icirc;", "&#238;");
		content = content.replaceAll("&iuml;", "&#239;");
		content = content.replaceAll("&eth;", "&#240;");
		content = content.replaceAll("&ntilde;", "&#241;");
		content = content.replaceAll("&ograve;", "&#242;");
		content = content.replaceAll("&oacute;", "&#243;");
		content = content.replaceAll("&ocirc;", "&#244;");
		content = content.replaceAll("&otilde;", "&#245;");
		content = content.replaceAll("&ouml;", "&#246;");
		content = content.replaceAll("&oslash;", "&#248;");
		content = content.replaceAll("&ugrave;", "&#249;");
		content = content.replaceAll("&uacute;", "&#250;");
		content = content.replaceAll("&ucirc;", "&#251;");
		content = content.replaceAll("&uuml;", "&#252;");
		content = content.replaceAll("&yacute;", "&#253;");
		content = content.replaceAll("&thorn;", "&#254;");
		content = content.replaceAll("&yuml;", "&#255;");
		content = content.replaceAll("&euro;", "&#x20AC;");
		return content;
	}
	
	private void handleImageElementsInXmlFileBasedOnVO() throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
	            .newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document document = docBuilder.parse(this.xmlFile);

	    NodeList nodeList = document.getElementsByTagName("*");
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node node = nodeList.item(i);
	        if (node!=null && node.getNodeType() == Node.ELEMENT_NODE) {
	            // do something with the current element
	        	if(node.hasChildNodes() && node.getFirstChild().isSameNode(node.getLastChild()) && node.getTextContent().startsWith("data:image")) {
	        		String image_filename = generateImageElement(node.getNodeName()+"_"+Integer.toString(i+1), node.getTextContent());
			        node.setTextContent(image_filename);
	        	}
	        }
	    }
	    
	    // write the DOM object to the file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();	
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);	
		StreamResult streamResult = new StreamResult(this.xmlFile);
		transformer.transform(domSource, streamResult);
	}
	
	public void handleInternalHtmlTagBracketsInXmlFile() {
		try {
			String content;
			
			InputStream inputStream = new FileInputStream(this.xmlFile);
			content = IOUtils.toString(inputStream, "UTF-8");
			
			//add proper angular brackets/entities for internal html tags embedded in data
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&amp;lt;", "<");
			content = content.replaceAll("&gt;", ">");
			content = content.replaceAll("&amp;gt;", ">");
			content = content.replaceAll("&quot;", "\"");
			content = content.replaceAll("&amp;quot;", "\"");
			content = content.replaceAll("&amp;nbsp;", "&nbsp;");
			
			//convert improper html tags into proper xhtml tags
			content = content.replaceAll("<br>", "<br/>");
			content = content.replaceAll("<hr>", "<hr/>");
			
			//handle character entities
			content = handleCharacterEntities(content);
									
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
	
	public void handleInternalHtmlTagBracketsInHtmlFile() {
		try {
			String content;
			
			InputStream inputStream = new FileInputStream(this.htmlFile);
			content = IOUtils.toString(inputStream, "UTF-8");
			
			//add proper angular brackets/entities for internal html tags embedded in data
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&gt;", ">");
			content = content.replaceAll("&quot;", "\"");
			
			//handle character entities
			content = handleCharacterEntities(content);
									
			IOUtils.closeQuietly(inputStream);
			
			OutputStream outputStream = new FileOutputStream(this.htmlFile);
			IOUtils.write(content, outputStream, "UTF-8");	
			IOUtils.closeQuietly(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		} 
	}
	
	public void handleInternalHtmlTagBracketsInXHtmlFile() {
		try {
			String content;
			
			InputStream inputStream = new FileInputStream(this.xhtmlFile);
			content = IOUtils.toString(inputStream, "UTF-8");
			
			//simple hack added as to avoid error due to 'content not allowed in prolog' in doctype declarations & xhtml namespace declaration in html tag
			//remove as soon as solution is found over the root error
			if(content.indexOf("<head>", 0) < content.indexOf("<body>", 0)) {
				content = "<html>" + content.substring(content.indexOf("<head>", 0));
			} else {
				content = "<html>" + content.substring(content.indexOf("<body>", 0));
			}			
			
			//handle character entities
			content = handleCharacterEntities(content);
			
			IOUtils.closeQuietly(inputStream);
			
			OutputStream outputStream = new FileOutputStream(this.xhtmlFile);
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
	        transformer.transform(src, res);
	        
	        out.close();
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