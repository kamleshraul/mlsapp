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
					<fo:block text-align="center" font-family="Mangal">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="Mangal">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		            	
	            	<fo:block font-family="Mangal" font-size="10.5px">
	            		<fo:block>
	            		<fo:block text-align="right" font-size="18" font-weight="bold">
	            			<xsl:value-of select="./element_1/element_1_4"/>
	            		</fo:block>
	            		<fo:block text-align="center" font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
	            		<fo:block text-align="left" font-weight="bold" text-decoration="underline">अतितात्काळ</fo:block>
	            		<fo:block text-align="right">
							<fo:block margin-right="1.50cm">क्रमांक - _____&#160;/ई-२,
							</fo:block>						
							<fo:block margin-right="0.10cm">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
							<fo:block margin-right="2.00cm">विधान भवन, <xsl:value-of select="./element_1/element_1_11"/>.</fo:block>
							<fo:block margin-right="1.65cm">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>	
            				<fo:table>
            					<fo:table-body>
            						<fo:table-row>
            							<fo:table-cell>
            								<fo:block>
            									<fo:block font-size="10px">
													&#160;
												</fo:block>
            									<fo:block>
													<fo:table>
														<fo:table-body>								
															<fo:table-row>																		
																	<fo:table-cell width="100px">
																	<fo:block>
																		&#160;
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block>
																	&#160;
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
																							सचिव (का.),
																						</fo:block>
																						<fo:block>महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
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
																							प्रधान सचिव / सचिव
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							महाराष्ट्र शासन,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							<xsl:value-of select="./element_1/element_1_8" /> विभाग,
																						</fo:block>
																					</fo:table-cell>
																				</fo:table-row>																																								
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							मंत्रालय, मुंबई.
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
												
												<fo:block font-weight="bold">
												<xsl:choose>
													<xsl:when test="./element_1/element_1_10='upperhouse'">
														<fo:block margin-left="3.5cm">
															विषय : <xsl:value-of select="./element_1/element_1_9"/>, वि.प.स. यांनी विधानपरिषद नियम ९३ अन्वये
														</fo:block>	
														<fo:block margin-left="4.8cm">
															दिलेली सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/> (प्रत संलग्न).
														</fo:block>
													</xsl:when>
													<xsl:otherwise>
														<fo:block margin-left="3.5cm">
															विषय : <xsl:value-of select="./element_1/element_1_9"/>
															वि.स.स. यांचा स्थगन प्रस्ताव सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/> (प्रत संलग्न).
														</fo:block>
													</xsl:otherwise>
												</xsl:choose>												
												</fo:block>
												
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय/महोदया,</fo:block>
            									<fo:block text-align="justify">
            										<fo:block>&#160;</fo:block>
            										<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;निदेशानुसार आपणांस कळविण्यात येत आहे की, सन २०१९ च्या दुसऱ्या (पावसाळी) 
														अधिवेशनात <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_12"/> रोजी</fo:inline>  
														महाराष्ट्र विधानपरिषदेत उपस्थित करण्यात आलेल्या नियम ९३ अन्वये सूचनेच्या <fo:inline font-weight="bold">(प्रत संलग्न)</fo:inline> संदर्भात 
														शासनाने ५ दिवसांत निवेदन करावे असे मा. सभापतींनी निदेश दिले असून त्या अनुषंगाने 
														<fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_13"/> 
														या तारखेपर्यंत विधानपरिषदेत निवेदन करण्यासंदर्भात मा.मंत्री महोदयांना अवगत करण्यात यावे.</fo:inline> 
														तसेच सदर निवेदनाच्या ३०० प्रती या सचिवालयाच्या “ई-२” शाखेकडे मा.मंत्री महोदयांनी विधानपरिषद सभागृहात निवेदन करावयाच्या 
														<fo:inline font-weight="bold">एक दिवस अगोदर सकाळी १०.००</fo:inline> वाजेपर्यंत पाठविण्याची व्यवस्था करावी. 
													</fo:block>
													<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;त्याचप्रमाणे या <fo:inline font-weight="bold">निवेदनांच्या प्रतींवर सन २०१९ चे दुसरे (पावसाळी) अधिवेशन 
														तसेच वर नमूद केलेला सूचना क्रमांक, निवेदन करणाऱ्या मा.मंत्री महोदयांचे नाव व विभागाचा स्पष्टपणे उल्लेख करून, निवेदन करण्याबाबतचे 
														मा. मंत्री महोदयांचे पत्र, महाराष्ट्र विधानपरिषद, विधान भवन, मुंबई यांना अग्रेषित करण्यात यावे,</fo:inline> अशी आपणांस विनंती करण्यात येत आहे.												      										
	            									</fo:block>
												</fo:block>	
												<fo:block>&#160;</fo:block>
												<fo:block>
													<fo:block text-align="right">
														<fo:block margin-right="3.1cm" font-weight="bold">आपली,</fo:block>
														<fo:block>&#160;</fo:block>
														<fo:block margin-right="2.5cm" font-size="10.5px" font-weight="bold">(पुष्पा र. दळवी)</fo:block>							
														<fo:block margin-right="2.5cm">कक्ष अधिकारी,</fo:block>							
														<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
													</fo:block>
												</fo:block>
												<fo:block font-size="6px">
            										&#160;
            									</fo:block>
												<fo:block font-weight="bold">याची प्रत :- </fo:block>
												
												<fo:block>
													(१) &#160;मा. <xsl:value-of select="./element_1/element_1_7" />.
												</fo:block>
												
												<fo:block>
													(२) मा.संसदीय कार्य मंत्री.
												</fo:block>
												
												<fo:block>
													(३) 	&#160;प्रधान सचिव / सचिव, संसदीय कार्य विभाग, महाराष्ट्र शासन, मंत्रालय, मुंबई.
												</fo:block>
											
            								</fo:block>
            							</fo:table-cell>
            							            	
            						</fo:table-row>
            					</fo:table-body>
            				</fo:table>
            			
 	
            			<fo:block font-size="10px">
            				&#160;
            			</fo:block>
            			
	</fo:block>
	</fo:block>
											          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>