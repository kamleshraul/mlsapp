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
	            	
	            		<xsl:choose>
	            			<xsl:when test="./element_1/element_1_10='upperhouse'">
	            				
	            				<xsl:if test="./element_2='yes'">
	            					<fo:block text-align="right" font-weight="bold" font-size="14pt">
	            						अग्रिम प्रत
	            					</fo:block>
	            					<fo:block font-size="12px">&#160;</fo:block>
	            				</xsl:if>
		            			<fo:block text-align="center" font-weight="bold" font-size="14pt">
			            			<xsl:value-of select="./element_1/element_1_2"></xsl:value-of>						
			            		</fo:block>  
			            		<fo:block font-size="6px">&#160;</fo:block>    
			            		<fo:block text-align="center" font-weight="bold" font-size="14pt">
			            			     <xsl:value-of select="./element_1/element_1_3"></xsl:value-of>       			
			            		</fo:block> 
			            		<fo:block font-size="12px">&#160;</fo:block>
								<fo:block>
									<fo:inline font-weight="bold">
										&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="./element_1/element_1_9"></xsl:value-of>
									</fo:inline>
									
									<xsl:if test="./element_1/element_1_10='upperhouse'">
										, वि.प.स यांनी महाराष्ट्र विधानपरिषद नियम 101 अनुसार दिलेली 
										<fo:inline font-weight="bold">
											लक्षवेधी सूचना क्र.<xsl:value-of select="./element_1/element_1_4"></xsl:value-of>
										</fo:inline> पुढीलप्रमाणे आहे:-
									</xsl:if> 
									<xsl:if test="./element_1/element_1_10='lowerhouse'">
										, वि.स.स यांनी महाराष्ट्र विधानसभा नियम 101 अनुसार दिलेली 
										<fo:inline font-weigth="bold">
											लक्षवेधी सूचना क्र.<xsl:value-of select="./element_1/element_1_4"></xsl:value-of>
										</fo:inline>
										 पुढीलप्रमाणे आहे:-
									</xsl:if>
								</fo:block>
								
								<fo:block font-size="12px">&#160;</fo:block>						
								
								<fo:block text-align="justify">
									&#160;&#160;&#160;&#160;&#160;"<xsl:value-of select="./element_1/element_1_5"></xsl:value-of>"
								</fo:block>
								
								<fo:block>&#160;</fo:block>
								
								<fo:block>
									<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell>
													<fo:block>
														&#160;
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														&#160;
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<fo:table>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell width="200px" text-align="justify">
																		<fo:block>
																			क्रमांक :
																			<fo:inline font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;/म.वि.स./ई-2,</fo:inline>
																		</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																<fo:table-row>
																	<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
																	</fo:table-cell>
																</fo:table-row>
																
																<fo:table-row>
																	<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">विधान भवन मुंबई / नागपूर</fo:block>
																	</fo:table-cell>
																</fo:table-row>
																
																<fo:table-row>
																	<fo:table-cell width="200px" text-align="justify"><fo:block>दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block></fo:table-cell>
																</fo:table-row>
															</fo:table-body>
														</fo:table>
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								
								<fo:block>&#160;</fo:block>
								<fo:block>&#160;</fo:block>
								
								<fo:block>
									संदर्भ : (1)	या सचिवालयाचे पत्र क्रमांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;/म.वि.स./ई-2, दिनांक :
								</fo:block>
								
								<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;(2) सचिव, महाराष्ट्र शासन, विभाग यांचे सूचना हस्तांतरणाबाबतचे पत्र           दिनांकित  </fo:block>
								
								<fo:block>&#160;</fo:block>
								<xsl:choose>
									<xsl:when test="./element_2='yes'">
										<fo:block font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;सचिव, महाराष्ट्र शासन,  <xsl:value-of select="./element_1/element_1_8"></xsl:value-of>, यांच्याकडे पुढील आवश्यक कार्यवाहीसाठी अग्रेषित.</fo:block>
									</xsl:when>
									<xsl:otherwise>
										<fo:block font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;सचिव, महाराष्ट्र शासन,  <xsl:value-of select="./element_1/element_1_8"></xsl:value-of>, यांच्याकडे आवश्यक कार्यवाहीसाठी अग्रेषित.</fo:block>
									</xsl:otherwise>
								</xsl:choose>
								
								
								
								<xsl:choose>
									<xsl:when test="./element_2='yes'">
										<xsl:if test="./element_1/element_1_10='lowerhouse'">
												मा. उपाध्यक्ष,महाराष्ट्र विधानसभा यांच्या निदेशानुसार आपणास कळविण्यात येते की, महाराष्ट्र विधानसभेच्या विद्यमान सत्रासाठी उपरोक्त लक्षवेधी सूचनेचे असुधारित प्रारूप प्रस्तावित केलेले असून, या सूचनेच्या पुढील कार्यवाहीसाठी मा. मंत्री महोदयांच्या माहितीस्तव अग्रेषित करण्यात येत आहे.
											</xsl:if> 
											<xsl:if test="./element_1/element_1_10='upperhouse'">
												मा.सभापती,महाराष्ट्र विधानपरिषद यांच्या निदेशानुसार आपणास कळविण्यात येते की, महाराष्ट्र विधानपरिषदेच्या विद्यमान सत्रासाठी उपरोक्त <fo:inline font-weight="bold">लक्षवेधी सूचनेचे असुधारित प्रारूप प्रस्तावित केलेले असून</fo:inline>, या सूचनेच्या पुढील कार्यवाहीसाठी मा. मंत्री महोदयांच्या माहितीस्तव अग्रेषित करण्यात येत आहे.
												<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;या सूचनेवरील मा.मंत्री महोदयांच्या निवेदनाच्या 250 प्रती तात्काळ या सचिवालयाकडे पाठविण्यात याव्यात.</fo:block>
											</xsl:if>
											  
									</xsl:when>
									<xsl:otherwise>
										<fo:block>							
											&#160;&#160;&#160;&#160;&#160;&#160;&#160;निदेशानुसार आपणास कळविण्यात येते की, महाराष्ट्र
											<xsl:if test="./element_1/element_1_10='lowerhouse'">
												विधानसभेच्या 
											</xsl:if> 
											<xsl:if test="./element_1/element_1_10='upperhouse'">
												विधानपरिषदेच्या 
											</xsl:if>
											 सद्य:सत्रासाठी मा.सभापतींनी उपरोक्त लक्षवेधी सूचना वरील स्वरुपात स्वीकृत केली असून, या सूचनेवरील मा.मंत्री महोदयांच्या निवेदनाच्या 250 प्रती तात्काळ या सचिवालयाकडे पाठविण्यात याव्यात.
										</fo:block>
									</xsl:otherwise>
								</xsl:choose>
								
														
								<xsl:if test="./element_2!='yes'">										
									<fo:block>
									
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;निदेशानुसार आपणास कळविण्यात येते की महाराष्ट्र विधानपरिषदेच्या सद्य: सत्रासाठी मा. सभापतींनी
										उपरोक्त <fo:inline font-weight="bold">लक्षवेधी सूचना क्रमांक <xsl:value-of select="./element_1/element_1_4"></xsl:value-of></fo:inline>
										वरील स्वरुपात <fo:inline font-weight="bold">स्वीकृत</fo:inline> केली असून  ती दिनांक <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_11"></xsl:value-of></fo:inline> रोजीच्या दिवसाच्या
										कामकाजाच्या क्रमात दाखविण्यात आली आहे. या लक्षवेधी सूचनेवर संबंधित मंत्री महोदयांनी करावयाच्या निवेदनाच्या <fo:inline font-weight="bold">४५०</fo:inline> 
										प्रती उशिरात उशिरा दिनांक <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_11"></xsl:value-of></fo:inline> रोजी &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; वाजेपर्यंत या सचिवालयास पाठवाव्यात.
										
									</fo:block>
								</xsl:if>
								<fo:block>&#160;</fo:block>
								
								<fo:block font-weight="bold">
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;या सूचनेवरील निवेदन सभागृहात केव्हा करावे लागेल ती तारीख आपणास मागाहून कळविण्यात येईल.							
								</fo:block>
								
								<fo:block font-weight="bold">
									कृपया ही बाब अति-तातडीची समजण्यात यावी.
								</fo:block>
								
								<fo:block>&#160;</fo:block>
								
								
								<fo:block margin-left="0px" width="300px">						
									<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell>
													<fo:block>
														&#160;
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														&#160;
													</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block>
														<fo:table>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell text-align="center">
																	<fo:block font-weight="bold">
																		<xsl:choose>
																		<xsl:when test="./element_1/element_1_10='lowerhouse'">
																			आपला/आपली
																		</xsl:when>
																		<xsl:otherwise>
																			आपली
																		</xsl:otherwise>
																		</xsl:choose>
																		</fo:block>
																		<fo:block>&#160;</fo:block>
																		<fo:block>&#160;</fo:block>
																		<fo:block font-weight="bold">
																			कक्ष अधिकारी,
																		</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																<fo:table-row>
																	<fo:table-cell text-align="center">
																		<fo:block font-weight="bold">
																			महाराष्ट्र विधानमंडळ सचिवालय.
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
								
								<fo:block>&#160;</fo:block>
								<fo:block>&#160;</fo:block>
								
								<fo:block>
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;(1) 
									<fo:inline font-weight="bold">मा. <xsl:value-of select="./element_1/element_1_7"></xsl:value-of></fo:inline>
								</fo:block>

										<fo:block>
											&#160;&#160;&#160;&#160;&#160;&#160;&#160;(2) 
											<fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_9"></xsl:value-of></fo:inline>
											<fo:inline>
											 	वि.प.स. यांना माहितीसाठी सादर अग्रेषित.
											 </fo:inline>								
										</fo:block>
										<fo:block font-size="6px">
											&#160;&#160;&#160;&#160;&#160;&#160;&#160;													
										</fo:block>
					
								<fo:block>
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;टिप :-
									<fo:inline font-weight="bold">विद्यमान अधिवेशनाच्या सन व सत्र कालावधीचा उल्लेख लक्षवेधी सूचनेच्या मध्यभागी व लक्षवेधी सूचना क्रमांक निवेदनाच्या उजव्या बाजूला मोठ्या व ठळक अक्षरात लक्षवेधी सूचनेच्या निवेदन प्रतींवर छापण्यात यावा.</fo:inline>													
								</fo:block>
		            		</xsl:when>
		            		
		            		<xsl:when test="./element_1/element_1_10='lowerhouse'">
		            			<fo:block>
		            				<fo:table>
		            					<fo:table-body>
		            						<fo:table-row>
		            							<fo:table-cell>
		            								<fo:block>
		            									<fo:block text-align="center" font-weight="bold">विधानसभा लक्षवेधी सूचना</fo:block>
		            									
		            									<fo:block text-align="left" font-weight="bold">अति तात्काळ</fo:block>
		            									
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
																									क्रमांक :&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;/फ,
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
																							<fo:table-cell text-align="justify"><fo:block>दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block></fo:table-cell>
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
																									सचिव
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
																									<xsl:value-of select="./element_1/element_1_8" />&#160;(<xsl:value-of select="./element_1/element_1_7" />) विभाग, 
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
															&#160;&#160;&#160;&#160;&#160;&#160;
															<fo:inline font-weight="bold" text-decoration="underline">विषय:- महाराष्ट्र विधानसभा नियम-१०५ अन्वये लक्षवेधी  सूचना</fo:inline> 
														</fo:block>
														
														<fo:block text-align="left" font-weight="bold">महोदय, </fo:block>
		            									
		            									<fo:block text-align="justify">
															&#160;&#160;&#160;&#160;&#160;&#160;
															<fo:inline>सोबत दर्शविल्याप्रमाणे दिलेली लक्षवेधी  सूचना मा. उपाध्यक्षांनी मान्य केली असून, उक्त लक्षवेधी सूचना सभागृहाच्या कामकाजात 
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
																</fo:inline> रोजी निवेदन  करावयाचे आहे.
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
														
														<fo:block>सूचनेची प्रत : </fo:block>
														
														<fo:block font-weight="bold">
															(१) <xsl:value-of select="./element_1/element_1_9" /> वि.स.स.
														</fo:block>
														
														<fo:block font-weight="bold">
															(२) मा. <xsl:value-of select="./element_1/element_1_7" />														
														</fo:block>
														
		            								</fo:block>
		            							</fo:table-cell>
		            							<fo:table-cell>
		            								<fo:block>
		            									&#160;
		            								</fo:block>
		            							</fo:table-cell>
		            							<fo:table-cell>
		            								<fo:block>
		            									<fo:block font-weight="bold" text-align="center">
		            										लक्षवेधी सूचनेची प्रत
		            									</fo:block>
		            									
		            									
		            									
		            									<fo:block font-weight="bold">
		            										संदर्भ : या सचिवालयाचे पत्र क्र.&#160;&#160;&#160;&#160;&#160;&#160;&#160;/फ दिनांकित 
		            									</fo:block>
		            									
		            									<fo:block font-size="6px">
		            										&#160;
		            									</fo:block>
		            									
		            									<fo:block>
		            										"<xsl:value-of select="./element_1/element_1_5"></xsl:value-of>"
		            									</fo:block>
		            									
		            									<fo:block font-weight="bold">
		            										<xsl:value-of select="./element_1/element_1_9"></xsl:value-of>
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
		            			
		            			<fo:block font-size="9px" text-align="justify" width="500px" margin-left="40px" margin-right="40px">
		            				टिप : अधिवेशन कालावधीत सदरहू लक्षवेधीवर चर्चा न झाल्यास सत्र समाप्तीच्या दिवशीच्या आदल्या कामकाजाच्या दिवशी निवेदनाच्या ६०० प्रती सभागृहाच्या पटलावर ठेवणेकरिता या सचिवालयाकडे पाठविण्यात याव्यात.  अंतिम निवेदन पाठविणे अगोदर लक्षवेधीचे प्रारुप व सुधारित नावांबाबत शाखेकडे खात्री करुन घ्यावी.) (Phone: ०७१२-२५३००१९/ ९  Ext : २२०१, २२०२ )
		            			</fo:block>
		            			
		            		</xsl:when>
	            		</xsl:choose>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>