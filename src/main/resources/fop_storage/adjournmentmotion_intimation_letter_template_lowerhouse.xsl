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
	            		<fo:block text-align="right" font-size="20" font-weight="bold">
	            			<xsl:value-of select="./element_1/element_1_4"/>
	            		</fo:block>
	            		<!-- <fo:block text-align="center" font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय</fo:block> -->
	            		<fo:block text-align="left" font-weight="bold" text-decoration="underline">अतितात्काळ</fo:block>
	            		<fo:block text-align="left" font-weight="bold" text-decoration="underline">स्थगन प्रस्ताव</fo:block>
	            		<fo:block text-align="right">
							<fo:block margin-right="1.75cm">क्रमांक : _____&#160;/फ,
							</fo:block>						
							<fo:block margin-right="0.08cm">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
							<fo:block margin-right="1.72cm">विधान भवन, <xsl:value-of select="./element_1/element_1_11"/>.</fo:block>
							<fo:block margin-right="1.65cm">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>							
            				<fo:table>
            					<fo:table-body>
            						<fo:table-row>
            							<fo:table-cell>
            								<fo:block>
            									<!-- <fo:block font-size="10px">
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
												</fo:block> -->
												
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
																							प्रधान सचिव,
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
																							प्रधान सचिव,
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
																							<fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_8" /> विभाग,</fo:inline>
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
												
												<fo:block>
												<xsl:choose>
													<xsl:when test="./element_1/element_1_10='upperhouse'">
														<fo:block margin-left="3.5cm">
															विषय : <xsl:value-of select="./element_1/element_1_9"/>
														</fo:block>	
														<fo:block margin-left="4.8cm">
															दिलेली सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/> (प्रत संलग्न).
														</fo:block>
													</xsl:when>
													<xsl:otherwise>
														<fo:block margin-left="3.5cm">
															<fo:inline font-weight="bold">विषय : </fo:inline><xsl:value-of select="./element_1/element_1_6"/>
															<!-- वि.स.स. यांचा स्थगन प्रस्ताव सूचना क्रमांक - <xsl:value-of select="./element_1/element_1_4"/> (प्रत संलग्न). -->
														</fo:block>
													</xsl:otherwise>
												</xsl:choose>												
												</fo:block>
												
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय,</fo:block>
            									<fo:block text-align="justify">
            										<fo:block>&#160;</fo:block>
            										<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;मला आपणांस असे कळविण्याचे निदेश आहेत की,
														 <fo:inline font-weight="bold"><xsl:value-of select="./element_1/element_1_12"/> रोजी</fo:inline>  
														<fo:inline>उपरोक्त विषयावरील <xsl:value-of select="./element_1/element_1_9"/>, वि.स.स. यांच्या विधानसभा नियम ९७ अन्वये  प्राप्त झालेल्या स्थगन प्रस्तावावरील निर्णय देतेवेळी</fo:inline>
														<fo:inline font-weight="bold">उपरोक्त प्रकरणी शासनाने वस्तुस्थितीदर्शक निवेदन करावे</fo:inline>
														<fo:inline>असे मा. अध्यक्षांनी निदेश दिले आहेत.</fo:inline> 
													</fo:block>
													<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;मा.अध्यक्षांचे निदेशानुसार उपरोक्त प्रकरणी स्थगन प्रस्तावाच्या निवेदनाच्या प्रती सदस्यांना वितरीत												      										
	            										<fo:inline>करावयाच्या असल्याने सदर निवेदनाच्या ७०० प्रती या सचिवालयास त्वरित पाठविण्यात याव्यात, अशी आपणांस</fo:inline>
	            										<fo:inline>विनंती आहे.</fo:inline>
	            									</fo:block>
												</fo:block>	
												<fo:block>&#160;</fo:block>
												<fo:block>
													<fo:block text-align="right">
														<fo:block margin-right="3.1cm" font-weight="bold">आपला,</fo:block>
														<fo:block>&#160;</fo:block>
														<fo:block margin-right="2.5cm" font-size="15px" font-weight="bold">(सुनिल परब)</fo:block>							
														<fo:block margin-right="2.5cm" font-weight="bold">कक्ष अधिकारी,</fo:block>							
														<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
													</fo:block>
												</fo:block>
												<fo:block font-size="6px">
            										&#160;
            									</fo:block>
												<fo:block font-weight="bold">प्रत माहितीसाठी सादर :- </fo:block>
												
												<fo:block>
													(१) &#160;मा. <xsl:value-of select="./element_1/element_1_7" />.
												</fo:block>
												
												<!-- <fo:block>
													(२) मा.संसदीय कार्य मंत्री.
												</fo:block> -->
												
												<fo:block>
													(२) 	&#160; सचिव, संसदीय कार्य विभाग
												</fo:block>
												
												<fo:block>
														टीप :- विद्यमान अधिवेशन कालावधीत सदरहू स्थगन प्रस्तावावरील निवेदने सभागृहाच्या पटलावर ठेवणे आवश्यक 											      										
	            										<fo:inline>आहे. अधिवेशन समाप्त होण्यापूर्वी स्थगन प्रस्तावावरील निवेदन न झाल्यास प्रस्तुत बाब कागदपत्रे सभागृहाच्या </fo:inline>
	            										<fo:inline>पटलावर ठेवण्यासंबंधातील समितीकडे पाठविण्यात येईल याची कृपया नोंद घेऊन निवेदन अधिवेशन संस्थगित</fo:inline>
	            										<fo:inline>होण्यापूर्वी सभागृहात वितरीत करण्याच्या दृष्टीने कार्यवाही व्हावी.) (२२०२७३९९ Ext. २२०१/२२०२.) </fo:inline>
	            										
	            								</fo:block>
            								</fo:block>
            							</fo:table-cell>
            							            	
            						</fo:table-row>
            					</fo:table-body>
            				</fo:table>	            			
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