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
	            		<fo:block text-align="center" font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
	            		<fo:block text-align="left" font-weight="bold" text-decoration="underline">अतितात्काळ</fo:block>
	            		<fo:block text-align="right">
							<fo:block margin-right="1.65cm">क्रमांक - _____&#160;/ई-२,
							</fo:block>						
							<fo:block margin-right="0.20cm">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
							<fo:block margin-right="1.75cm">विधानभवन, <xsl:value-of select="./element_1/element_1_11" />.</fo:block>
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
												
												<!-- <fo:block text-align="left" font-weight="bold">प्रेषक : </fo:block>
            									
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
												</fo:block> -->
												
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
																							सचिव,
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
																							<xsl:value-of select="./element_1/element_1_8" />&#160;(<xsl:value-of select="./element_1/element_1_7" />) विभाग, 
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							शिबीर कार्यालय,<xsl:value-of select="./element_1/element_1_11" />.
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
												
												&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
												<fo:block>
													<fo:inline font-weight="bold">विषय:- </fo:inline>
													<fo:inline>
													<xsl:choose>
														<xsl:when test="./element_1/element_1_10='upperhouse'">
															<fo:inline  font-weight="bold" ><xsl:value-of select="./element_1/element_1_9"/>,</fo:inline>
															<fo:inline font-weight="bold">वि.प.स.</fo:inline> यांनी विधानपरिषदमध्ये विशेष उल्लेख म्हणून उपस्थित केलेल्या बाबींवर त्वरित कारवाई करण्यासंदर्भात.
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="./element_1/element_1_9" font-weight="bold"/>
															वि.स.स. यांनी विधानपरिषदमध्ये विशेष उल्लेखाद्वारे उपस्थित केलेल्या सूचनेवर त्वरित कारवाई करण्यासंदर्भात.
														</xsl:otherwise>
													</xsl:choose> 
													</fo:inline> 
												</fo:block>
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय/महोदया,</fo:block>
            									<fo:block text-align="justify">
													&#160;&#160;&#160;&#160;&#160;&#160;
													<fo:inline>निदेशानुसार, <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_9"/></fo:inline>, वि.प.स.यांनी महाराष्ट्र विधानपरिषदेत दिनांक
														<fo:inline font-weight="bold">
																	<xsl:value-of select="./element_1/element_1_12" />,
														</fo:inline>
														<fo:block>
														रोजी <fo:inline font-weight="bold">"विशेष उल्लेखाद्वारे"</fo:inline> या रूपाने उपस्थित केलेल्या विषयाच्या संदर्भातील कार्यवृत्ताची प्रत सोबत जोडली आहे.
														</fo:block>
													</fo:inline>
												<fo:block>
													संबंधित विषयाबाबत केलेली कारवाई, म.वि.प. नियम "१०१-फ" अन्वये सभागृहात सूचना उपस्थित केल्याचा
            										<fo:block>
            										दिनंकापासून<fo:inline font-weight="bold">तीस दिवसांच्या </fo:inline>आत मंत्री महोदयानी संबंधित सदस्यांना परस्पर पठाविणे आवश्यक असून त्यासंबंधीची प्रत
            										</fo:block>
            										<fo:block>
            										या सचिवालयाला पाठविनयत यावी, अशी आपणास विनंती करण्यात येत आहे.
            										</fo:block>
            										
            										</fo:block>
											</fo:block>	
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
												<fo:block font-weight="bold">याची प्रत : </fo:block>
												
												<fo:block>
													(१) &#160;मा. <xsl:value-of select="./element_1/element_1_7" />.
												</fo:block>
												
												<fo:block>
													(२) <xsl:choose>
														<xsl:when test="./element_1/element_1_10='upperhouse'">
															&#160;<xsl:value-of select="./element_1/element_1_9" />,
															वि.प.स.
														</xsl:when>
														<xsl:otherwise>
															&#160;<xsl:value-of select="./element_1/element_1_9" />
															वि.स.स.
														</xsl:otherwise>
													</xsl:choose> 													
												</fo:block>
												
												<fo:block>
													(३) 	&#160;सचिव, महाराष्ट्र शासन, संसदीय कार्य विभाग, शिबीर कार्यालय, <xsl:value-of select="./element_1/element_1_11" />.				
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