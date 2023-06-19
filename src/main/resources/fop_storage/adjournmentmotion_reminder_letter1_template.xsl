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
		            		<fo:block>
		            			<fo:table table-layout="fixed" width="100%">
			            			<fo:table-column column-number="1" column-width="5cm" />
									<fo:table-column column-number="2" column-width="10cm" />
									<fo:table-column column-number="3" column-width="2cm" />
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="left" font-size="20" font-weight="bold" text-decoration="underline">
													स्मरणपत्र क्र.१										
												</fo:block>										
											</fo:table-cell>
											<fo:table-cell>
												<fo:block>&#160;</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block text-align="right" font-size="20" font-weight="bold">
							            			<xsl:value-of select="./element_1/element_1_4"/>
							            		</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
			            		</fo:table>
		            		</fo:block>
		            		<fo:block font-size="5">&#160;</fo:block>
		            		<fo:block>
		            			<fo:table table-layout="fixed" width="100%">
			            			<fo:table-column column-number="1" column-width="10cm" />
									<fo:table-column column-number="2" column-width="2cm" />
									<fo:table-column column-number="3" column-width="5cm" />
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell>
												<fo:block text-align="left" font-size="15" font-weight="bold" text-decoration="underline">
													<fo:block>विधानपरिषद म.वि.प. नियम ९३ अन्वये सूचना</fo:block>
													<fo:block>अति-तात्काळ</fo:block>
												</fo:block>										
											</fo:table-cell>
											<fo:table-cell>
												<fo:block>&#160;</fo:block>
											</fo:table-cell>
											<fo:table-cell>
												<fo:block>क्रमांक - _____&#160;/ई-२,</fo:block>						
												<fo:block>महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
												<fo:block>विधान भवन, <xsl:value-of select="./element_1/element_1_11"/>.</fo:block>
												<xsl:choose>
													<xsl:when test="boolean(reportDate)">
														<fo:block>दिनांक : <xsl:value-of select="reportDate"></xsl:value-of></fo:block>
													</xsl:when>
													<xsl:otherwise>
														<fo:block>दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
													</xsl:otherwise>
												</xsl:choose>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
			            		</fo:table>
		            		</fo:block>		            		
		            		<fo:block>						
	            				<fo:table>
	            					<fo:table-body>
	            						<fo:table-row>
	            							<fo:table-cell>
	            								<fo:block>
													<fo:block text-align="left" font-size="14" font-weight="bold">प्रेषक : </fo:block>	            									
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
																								सचिव-१ (कार्यभार),
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
													
													<fo:block text-align="left" font-weight="bold">प्रति : </fo:block>
	            									
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
																								प्रधान सचिव / सचिव,
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
													
													<fo:block font-size="5px">&#160;</fo:block>
													
													<fo:block>
														<fo:table table-layout="fixed" width="100%">
									            			<fo:table-column column-number="1" column-width="4cm" />
															<fo:table-column column-number="2" column-width="0.5cm" />
															<fo:table-column column-number="3" column-width="15cm" />
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell>
																		<fo:block text-align="right" font-weight="bold">
																			विषय
																		</fo:block>										
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block>&#160;:&#160;</fo:block>
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block>
													            			<fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_9"/>, वि.प.स.</fo:inline> यांनी विधानपरिषद नियम ९३ अन्वये
													            		</fo:block>
													            		<fo:block>
													            			दिलेल्या <fo:inline font-weight="bold">सूचना क्रमांक  <xsl:value-of select="./element_1/element_1_4"/></fo:inline> वरील निवेदन त्वरीत प्राप्त होण्यासंदर्भात.
													            		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</fo:table-body>
									            		</fo:table>
													</fo:block>
													<fo:block font-size="3">&#160;</fo:block>
													<fo:block>
														<fo:table table-layout="fixed" width="100%">
									            			<fo:table-column column-number="1" column-width="4cm" />
															<fo:table-column column-number="2" column-width="0.5cm" />
															<fo:table-column column-number="3" column-width="15cm" />
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell>
																		<fo:block text-align="right" font-weight="bold">
																			संदर्भ
																		</fo:block>										
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block>&#160;:&#160;</fo:block>
																	</fo:table-cell>
																	<fo:table-cell>
																		<fo:block>
													            			या सचिवालयाचे पत्र, दिनांकीत <xsl:value-of select="./element_1/element_1_17"/>
													            		</fo:block>
																	</fo:table-cell>
																</fo:table-row>
															</fo:table-body>
									            		</fo:table>
													</fo:block>
													
													<fo:block font-size="5">&#160;</fo:block>
													
													<fo:block text-align="left" font-weight="bold">महोदय/महोदया,</fo:block>
	            									<fo:block text-align="justify">
	            										<fo:block font-size="5">&#160;</fo:block>
	            										<fo:block>
															&#160;&#160;&#160;&#160;&#160;&#160;निदेशानुसार आपले लक्ष या सचिवालयाच्या संदर्भाधीन पत्राकडे वेधण्यात येते, महाराष्ट्र विधानपरिषदेच्या सन <xsl:value-of select="./element_1/element_1_14"/> च्या
															<xsl:choose>
																<xsl:when test="./element_1/element_1_15='1'">पहिल्या</xsl:when>
																<xsl:when test="./element_1/element_1_15='2'">दुसऱ्या</xsl:when>
																<xsl:when test="./element_1/element_1_15='3'">तिसऱ्या</xsl:when>
																<xsl:when test="./element_1/element_1_15='4'">चौथ्या</xsl:when>
																<xsl:when test="./element_1/element_1_15='5'">पाचव्या</xsl:when>
															</xsl:choose>														  
															(<xsl:value-of select="./element_1/element_1_16"/>) अधिवेशनात मा.उप सभापतींनी दिलेल्या निदेशानुसार म.वि.प. नियम ९३ अन्वये उपस्थित केलेल्या सूचनेवरील निवेदन अद्याप या सचिवालयास प्राप्त झालेले नाही.
															सदर बाब गंभीर आहे. 
														</fo:block>
														<fo:block font-weight="bold">
															&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानपरिषद नियम ९३ अन्वये उपस्थित केलेल्या सूचनेचा विषय हा तातडीचा,
															सार्वजनिक महत्वाचा व गंभीर असल्याने या सूचनेवरील निवेदनाच्या प्रती विहित मुदतीत उपलब्ध करून देणे आवश्यक असतानाही आपणांकडून प्राप्त झाल्या नसल्याने सन्माननीय सदस्य तीव्र नाराजी व्यक्त करीत आहेत.
		            									</fo:block>
		            									<fo:block>
															&#160;&#160;&#160;&#160;&#160;&#160;आपणांस पुन्हा विनंती करण्यात येते की, <fo:inline font-weight="bold">या सूचनेवरील निवेदनाच्या प्रती त्वरित या सचिवालयाकडे पाठविण्याची व्यवस्था करावी.</fo:inline>
		            									</fo:block>
													</fo:block>
														
													<fo:block font-size="5">&#160;</fo:block>
													
													<fo:block>
														<fo:block text-align="right">
															<fo:block margin-right="3.1cm" font-weight="bold">आपला,</fo:block>
															<fo:block>&#160;</fo:block>
															<fo:block margin-right="2.5cm" font-size="15px" font-weight="bold">(मंदार शेमणकर)</fo:block>							
															<fo:block margin-right="2.5cm">कक्ष अधिकारी,</fo:block>							
															<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
														</fo:block>
													</fo:block>
													
													<fo:block font-size="5px">&#160;</fo:block>
													
													<fo:block font-weight="bold">याची प्रत : </fo:block>		
													
													<fo:block margin-left="1.25cm">										
														<fo:block>
															१. मा. <xsl:value-of select="./element_1/element_1_7" />.
														</fo:block>													
														<fo:block>
															२. मा.संसदीय कार्य मंत्री.
														</fo:block>													
														<fo:block>
															३. प्रधान सचिव / सचिव, संसदीय कार्य विभाग, महाराष्ट्र शासन, मंत्रालय, मुंबई.
														</fo:block>
													</fo:block>
	            								</fo:block>
	            							</fo:table-cell>	            							            	
	            						</fo:table-row>
	            					</fo:table-body>
	            				</fo:table>
	           				</fo:block>	 
	           				
	           				<fo:block break-before="page">
								<fo:block font-weight="bold" text-align="center">
									<xsl:value-of select="./element_1/element_1_9"/>, वि.प.स. यांनी विधानपरिषद नियम ९३ अन्वये दिलेली सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/>
								</fo:block>
								<fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
								<fo:block>
									<xsl:apply-templates select="./element_1/element_1_5"/>
								</fo:block>
							</fo:block>           			
						</fo:block>
						
						<!-- <fo:block break-before="page">
							<fo:block font-weight="bold" text-align="center">
								<xsl:value-of select="./element_1/element_1_9"/>, वि.प.स. यांनी विधानपरिषद नियम ९३ अन्वये दिलेली सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/>
							</fo:block>
							<fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
							<fo:block>
								<xsl:apply-templates select="./element_1/element_1_5"/>
							</fo:block>
						</fo:block> -->
					</fo:block>											          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>