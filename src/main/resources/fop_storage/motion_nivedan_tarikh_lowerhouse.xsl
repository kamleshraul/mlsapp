<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="root"/>
    
    <xsl:variable name="pageLayout" select="simple"/>
    
    <!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
   <xsl:template match="root">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2.3cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.25cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-common" extent="2cm"/>
			      	<fo:region-after region-name="ra-common" extent="1.5cm"/>
  				</fo:simple-page-master>
	   				
  				<fo:page-sequence-master master-name="simple">
		          <fo:repeatable-page-master-alternatives>
		            <fo:conditional-page-master-reference 
		              master-reference="first" 
		              page-position="first"/>
		            <fo:conditional-page-master-reference 
		              master-reference="others" 
		              odd-or-even="even"/>
		            <fo:conditional-page-master-reference 
		              master-reference="others" 
		              odd-or-even="odd"/>
		          </fo:repeatable-page-master-alternatives>
		        </fo:page-sequence-master>		
			</fo:layout-master-set>				
	        
	        <fo:page-sequence master-reference="first" id="DocumentBody">	        	
	        	<!-- header -->
	        	<fo:static-content flow-name="rb-first">
					<fo:block text-align="center" font-family="Kokila">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="Kokila">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		            	
	            	<fo:block font-family="Kokila" font-size="15px">
	            		<fo:block>
            				<fo:table>
            					<fo:table-body>
            						<fo:table-row>
            							<fo:table-cell>
            								<fo:block>
            									<fo:block text-align="center" font-weight="bold">लक्षवेधी सूचना</fo:block>
            									
            									<fo:block text-align="left" font-weight="bold">अति तात्काळ</fo:block>
            									<fo:block font-size="10px">
													&#160;
												</fo:block>
            									<fo:block>
													<fo:table>
														<fo:table-body>								
															<fo:table-row>																		
																<fo:table-cell width="60px">
																	<fo:block>
																		&#160;
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block>
																		<fo:table>
																			<fo:table-body>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block font-weight="bold">
																							क्रमांक :<xsl:value-of select="./element_5" />/फ,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
																					</fo:table-cell>
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>विधान भवन, 
																							<xsl:value-of select="./element_1/element_1_11" />
																						</fo:block>
																					</fo:table-cell>
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify"><fo:block>दिनांक : <xsl:value-of select="./element_7" /></fo:block></fo:table-cell>
																				</fo:table-row>
																			</fo:table-body>
																		</fo:table>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
												</fo:block>
												
												<fo:block text-align="left" font-weight="bold">प्रेषक : </fo:block>
            									
            									<fo:block>
													<fo:table>
														<fo:table-body>								
															<fo:table-row>																		
																<fo:table-cell width="35px">
																	<fo:block>
																		&#160;
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block>
																		<fo:table>
																			<fo:table-body>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block font-weight="bold">
																							प्रधान सचिव
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							महाराष्ट्र विधानमंडळ सचिवालय, <xsl:value-of select="./element_1/element_1_11" /> 
																						</fo:block>
																					</fo:table-cell>
																				</fo:table-row>
																			</fo:table-body>
																		</fo:table>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
												</fo:block>
												
												<fo:block text-align="left" font-weight="bold">प्रति, </fo:block>
            									
            									<fo:block>
													<fo:table>
														<fo:table-body>								
															<fo:table-row>																		
																<fo:table-cell width="35px">
																	<fo:block>
																		&#160;
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block>
																		<fo:table>
																			<fo:table-body>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block font-weight="bold">
																							प्रधान सचिव,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block font-weight="bold">
																							<xsl:value-of select="./element_1/element_1_8" />&#160; विभाग, 
																						</fo:block>
																					</fo:table-cell>
																				</fo:table-row>
																				<!-- 
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							महाराष्ट्र शासन,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row> -->
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							मंत्रालय,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							मुंबई - ४०० ०३२
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																			</fo:table-body>
																		</fo:table>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
												</fo:block>
												
												<fo:block font-size="6px">&#160;</fo:block>
												
												<fo:block>
													<fo:inline font-weight="bold">विषय:- </fo:inline>
													<fo:inline font-weight="bold" text-decoration="underline">महाराष्ट्र विधानसभा नियम-१०५ अन्वये लक्षवेधी  सूचना</fo:inline> 
												</fo:block>
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय, </fo:block>
            									
            									<fo:block text-align="justify">
													&#160;&#160;&#160;&#160;&#160;&#160;
													<fo:inline>सोबत दर्शविल्याप्रमाणे दिलेली लक्षवेधी  सूचना मा. अध्यक्षांनी मान्य केली असून, उक्त लक्षवेधी सूचना सभागृहाच्या कामकाजात 
														<fo:inline font-weight="bold">
															<xsl:choose>
																<xsl:when test="./element_1/element_1_12='दिनांक '">
																	<xsl:value-of select="./element_1/element_1_12" />&#160;&#160;&#160;&#160;&#160;&#160;
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="./element_1/element_1_12" />
																</xsl:otherwise>
															</xsl:choose> 
															</fo:inline> रोजी दाखविण्यात आली  आहे. या विषयावर 
														<fo:inline font-weight="bold">मा. <xsl:value-of select="./element_1/element_1_7" /></fo:inline> यांना सभागृहात
														<fo:inline font-weight="bold"> 
															<xsl:choose>
																<xsl:when test="./element_1/element_1_12='दिनांक '">
																	<xsl:value-of select="./element_1/element_1_12" />&#160;&#160;&#160;&#160;&#160;&#160;
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="./element_1/element_1_12" />
																</xsl:otherwise>
															</xsl:choose> 
														</fo:inline> रोजी निवेदन करावयाचे आहे.
													</fo:inline>
												</fo:block>
												
												<fo:block>
													&#160;&#160;&#160;&#160;&#160;&#160;या निवेदनाच्या ७०० प्रती 
													<fo:inline font-weight="bold">
														<xsl:choose>
															<xsl:when test="./element_1/element_1_12='दिनांक '">
																<xsl:value-of select="./element_1/element_1_12" />&#160;&#160;&#160;&#160;&#160;&#160;
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="./element_1/element_1_12" />
															</xsl:otherwise>
														</xsl:choose>
													</fo:inline> पर्यत या सचिवालयाकडे पाठवाव्यात
												</fo:block>
												
												<fo:block>
													<fo:table>
														<fo:table-body>								
															<fo:table-row>																		
																<fo:table-cell width="80px">
																	<fo:block>
																		&#160;
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block>
																		<fo:table>
																			<fo:table-body>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							&#160;
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="center">
																						<fo:block>
																							आपला,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="center">
																						<fo:block>
																							&#160;
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="center">
																						<fo:block>
																							&#160;
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				<fo:table-row>
																					<fo:table-cell text-align="center" font-weight="bold">
																						<fo:block>
																							कक्ष अधिकारी,
																						</fo:block>
																						<fo:block>
																							महाराष्ट्र विधानमंडळ सचिवालय
																						</fo:block>
																					</fo:table-cell>
																				</fo:table-row>																						
																				
																			</fo:table-body>
																		</fo:table>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</fo:table-body>
													</fo:table>
												</fo:block>
												<fo:block font-size="6px">
            										&#160;
            									</fo:block>
												<fo:block>सूचनेची प्रत : </fo:block>
												
												<fo:block font-weight="bold">
													(१) <xsl:value-of select="./element_1/element_1_13" /> वि.स.स.
												</fo:block>
												
												<fo:block font-weight="bold">
													(२) मा. <xsl:value-of select="./element_1/element_1_7" />														
												</fo:block>
												
												<fo:block font-weight="bold">
													(३) संगणक कक्ष					
												</fo:block>
												
            								</fo:block>
            							</fo:table-cell>
            							<fo:table-cell width="20px">
            								<fo:block>
            									&#160;
            								</fo:block>
            							</fo:table-cell>
            							<fo:table-cell>
            								<fo:block>            									
            									<xsl:choose>
            										<xsl:when test="./element_2='advanceCopy'">
            											<fo:block text-align="center" font-size="14px">
		            										लक्षवेधी सूचनेची अग्रिम प्रत
		            									</fo:block>
		            									<fo:block text-align="center" font-size="14px">
		            										महत्वाची टिप
		            									</fo:block>
            											<fo:block text-align="center" font-size="12px">
		            										सदरची प्रत ही असुधारित असून मा.सदस्यांचे सुधारित प्रारूप व सुधारित नावे लक्षवेधी सूचनेवरील चर्चेच्या आदल्या दिवशी पुनश्च पाठविण्यात येईल
		            									</fo:block>
            										</xsl:when>
            										<xsl:when test="./element_2='revisedCopy'">
            											<fo:block text-align="center" font-size="14px">
		            										लक्षवेधी सूचनेची प्रत
		            									</fo:block>
		            									<fo:block text-align="center" font-size="15px">
		            										सुधारित प्रत.
		            									</fo:block>
		            									<fo:block font-size="14px" font-weight="bold">
		            										संदर्भ : या सचिवालयाचे पत्र क्र.<xsl:value-of select="./element_6" />/फ दिनांकित  <xsl:value-of select="./element_8" />
		            									</fo:block>
            										</xsl:when>
            										<xsl:when test="./element_2='tentativeCopy'">
            											<fo:block text-align="center" font-size="14px">
		            										लक्षवेधी सूचनेची प्रत
		            									</fo:block>
		            									<fo:block text-align="center" font-size="14px">
		            										महत्वाची टिप
		            									</fo:block>
            											<fo:block text-align="center" font-size="12px">
		            										सदरची प्रत ही असुधारित असून मा.सदस्यांचे सुधारित प्रारूप व सुधारित नावे लक्षवेधी सूचनेवरील चर्चेच्या आदल्या दिवशी पुनश्च पाठविण्यात येईल
		            									</fo:block>
            										</xsl:when>
            										<xsl:otherwise>
            											<fo:block>
		            										&#160;
		            									</fo:block>
            										</xsl:otherwise>
            									</xsl:choose>
            									
            									       									
            									<fo:block font-size="6px">
            										&#160;
            									</fo:block>
            									
            									<fo:block text-align="justify">
            										"<xsl:value-of select="./element_4"></xsl:value-of>"
            									</fo:block>
            									<fo:block font-size="6px">
            										&#160;
            									</fo:block>
            									<fo:block font-weight="bold" text-align="justify">
            										<xsl:value-of select="./element_1/element_1_9"></xsl:value-of> वि.स.स.
            									</fo:block>
											</fo:block>
            							</fo:table-cell>
            						</fo:table-row>
            					</fo:table-body>
            				</fo:table>
            			</fo:block>
 	
            			<fo:block font-size="10px">
            				&#160;
            			</fo:block>
            			
            			<fo:block font-size="14px" text-align="justify" width="500px" margin-left="40px" margin-right="40px">
            				टिप : अधिवेशन कालावधीत सदरहू लक्षवेधीवर चर्चा न झाल्यास सत्र समाप्तीच्या दिवशीच्या आदल्या कामकाजाच्या दिवशी निवेदनाच्या ७०० प्रती सभागृहाच्या पटलावर ठेवणेकरिता या सचिवालयाकडे पाठविण्यात याव्यात.  अंतिम निवेदन पाठविणे अगोदर लक्षवेधीचे प्रारुप व सुधारित नावांबाबत शाखेकडे खात्री करुन घ्यावी.) 
            				<xsl:choose>
            					<xsl:when test="./element_1/element_1_11='नागपूर'">
            						(Phone: ०७१२-२५३००१९/ ९  Ext : २२०१, २२०२ )
            					</xsl:when>
            					<xsl:otherwise>
            						(Phone: ०२२-२२०२७३९९/ ९  Ext : २२०१, २२०२ )
            					</xsl:otherwise>
            				</xsl:choose>
            				<fo:inline>
            					
            				</fo:inline>
            				
            			</fo:block>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>