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
	                  	margin-left="3cm" margin-right="1.5cm">
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
	            		<fo:block text-align="right" font-size="20" font-weight="bold">
	            			<xsl:value-of select="./element_1/element_1_4"/>
	            		</fo:block>
	            		<fo:block text-align="center" font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
	            		<fo:block text-align="left" font-weight="bold" text-decoration="underline">अतितात्काळ</fo:block>
	            		<fo:block text-align="right">
							<fo:block margin-right="0.55cm">क्रमांक - _____/म.वि.स/ई-२,
							</fo:block>						
							<fo:block margin-right="0.30cm">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
							<fo:block margin-right="2.30cm">विधानभवन, <xsl:value-of select="./element_1/element_1_11" />.</fo:block>
							<fo:block margin-right="1.55cm">दिनांक :
							<fo:inline >
								<xsl:value-of select="./element_1/element_1_12" />
						   </fo:inline>
							</fo:block>
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
																							सचिव(१)(का.),
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
																							प्रधान सचिव / सचिव ,
																						</fo:block>
																					</fo:table-cell>										
																				</fo:table-row>
																				
																				
																				<fo:table-row>
																					<fo:table-cell text-align="justify">
																						<fo:block>
																							<xsl:value-of select="./element_1/element_1_8" />&#160;<!-- (<xsl:value-of select="./element_1/element_1_7" />) --> विभाग, 
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
																							<!--शिबीर कार्यालय,<xsl:value-of select="./element_1/element_1_11" />.-->
																							मंत्रालय  , <xsl:value-of select="./element_1/element_1_11" />.
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
												<fo:block margin-left="1.35cm">
													<fo:inline font-weight="bold">विषय:- </fo:inline>
													<fo:inline>
													<xsl:choose>
														<xsl:when test="./element_1/element_1_10='upperhouse'">
															<fo:inline  font-weight="bold" ><xsl:value-of select="./element_1/element_1_9"/>,</fo:inline>
															<fo:inline font-weight="bold">वि.प.स.</fo:inline> यांनी विधानपरिषदमध्ये उपस्थित केलेल्या औचित्याचा मुदयावर  त्वरित कार्यवाही करण्यासंदर्भात.
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="./element_1/element_1_9" font-weight="bold"/>
															वि.स.स. यांनी विधानपरिषदमध्ये औचित्याचा मुदयावर  त्वरित कार्यवाही करण्यासंदर्भात.
														</xsl:otherwise>
													</xsl:choose> 
													</fo:inline> 
												</fo:block>
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय,</fo:block>
            									<fo:block text-align="justify">
													&#160;&#160;&#160;&#160;&#160;&#160;
													<fo:inline>निदेशानुसार, <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_9"/></fo:inline>, वि.प.स.यांनी <fo:inline ><xsl:value-of select="./element_1/element_1_3"/></fo:inline>
													  महाराष्ट्र विधानपरिषदेत दिनांक <fo:inline font-weight="bold"> <xsl:value-of select="./element_1/element_1_12" /></fo:inline>
													  रोजी <fo:inline font-weight="bold">"औचित्याचा मुद्याद्वारे"</fo:inline> उपस्थित केलेल्या विषयाच्या संदर्भातील  कार्यवृत्ताची प्रत सोबत जोडली आहे.
														</fo:inline>संबंधित विषयाबाबत केलेली कार्यवाही अथवा त्या संदर्भातील माहिती संबंधित सदस्यांना<fo:inline font-weight="bold"> पंधरा  दिवसांच्या आत </fo:inline> मंत्री महोदयानी परस्पर  पठाविणे आवश्यक असून त्याची  प्रत
	            										<!--</fo:block>
	            										<fo:block>-->
	            										या <fo:inline font-weight="bold">सचिवालयाला पत्राच्या संदर्भासहित </fo:inline> मा . सभापतींच्या  अवलोकनार्थ या सचिवालयाला पाठविण्यात यावी, अशी आपणास विनंती करण्यात येत आहे.
            										
											</fo:block>	
												<fo:block>
													<fo:block text-align="right">
														<fo:block>&#160;</fo:block>
														<fo:block margin-right="3.1cm" font-weight="bold">आपला,</fo:block>
														<fo:block>&#160;</fo:block>
														<fo:block>&#160;</fo:block>
														<fo:block>&#160;</fo:block>														
														<fo:block margin-right="2.5cm" font-size="15px" font-weight="bold">(मंदार शेमणकर)</fo:block>							
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
													<!--(३) 	&#160;प्रधान सचिव, महाराष्ट्र शासन, संसदीय कार्य विभाग, शिबीर कार्यालय, <xsl:value-of select="./element_1/element_1_11" />.-->
													      ३) 	&#160;प्रधान सचिव /सचिव ,संसदीय कार्य विभाग,  महाराष्ट्र शासन,  मंत्रालय ,<xsl:value-of select="./element_1/element_1_11" />.													
												</fo:block>
											
            								</fo:block>
            							</fo:table-cell>
            							            	
            						</fo:table-row>
            					</fo:table-body>
            				</fo:table>      
            					
            		   			
					</fo:block>	
					
						<fo:block break-before="page">
							<fo:block font-weight="bold" text-align="center">
								<xsl:value-of select="./element_1/element_1_9"/>, वि.प.स. यांनी विधानपरिषदमध्ये विशेष उल्लेख म्हणून उपस्थित केलेली सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/>
							</fo:block>
							<fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
							<fo:block>
								<xsl:apply-templates select="./element_1[1]/element_1_5"/>						
							</fo:block>
							
							
							<xsl:for-each select="element_1">
									<xsl:if test="position()=2">
											<fo:block font-weight="bold" text-align="center">
												<fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
												सदर सूचनेस जोडण्यात आलेल्या इतर विशेष उल्लेख सूचनांचे मूळ प्रारूप 
											</fo:block>
									</xsl:if>
							</xsl:for-each>
							
														
							<fo:block>
								<xsl:for-each select="element_1">
									<xsl:if test="position()!=1">
										
										<xsl:variable name="childAdmittedNumber" select="element_1_4"/>
										<xsl:variable name="memberName" select="element_1_9" />
										<xsl:variable name="childOriginalContent" select="element_1_5" />
										<fo:block>&#160;</fo:block>
										<fo:block font-weight="bold" text-align="center">
												सूचना क्रमांक  <xsl:value-of select="$childAdmittedNumber"/>											
												(<xsl:value-of select="$memberName"/>)
										</fo:block>
										<fo:block>
											<xsl:apply-templates select="element_1_5"/>
										</fo:block>
										
									</xsl:if>
								</xsl:for-each>	
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