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
												</fo:block> -->
												
												<fo:block font-size="12px">&#160;</fo:block>
												
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
																							<xsl:value-of select="./element_1/element_1_3" />,
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
												
												<fo:block font-size="12px">&#160;</fo:block>
												
												<fo:block font-weight="bold">
													<fo:block margin-left="2.5cm">
														विषय : सन <xsl:value-of select="./element_1/element_1_1"/> दि.<xsl:value-of select="./element_1/element_1_2"/> रोजीच्या
													</fo:block>	
													<fo:block margin-left="3.8cm">
														कपात सूचना
													</fo:block>
													<fo:block>&#160;</fo:block>
													<fo:block margin-left="2.5cm">
														संदर्भ : <xsl:value-of select="./element_1/element_1_4"/>
													</fo:block>	
													<fo:block margin-left="3.8cm">
														<xsl:value-of select="./element_1/element_1_5"/>
													</fo:block>												
												</fo:block>
												
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block text-align="left" font-weight="bold">महोदय,</fo:block>
            									<fo:block text-align="justify">
            										<fo:block>&#160;</fo:block>
            										<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;उपरोक्त संदर्भाधिन विषयाच्या अनुषंगाने आपणास कळविण्यात येते की, मा. अध्यक्ष, महाराष्ट विधानसभा 
														यांनी सन <xsl:value-of select="./element_1/element_1_1"/> दिनांक <xsl:value-of select="./element_1/element_1_2"/> रोजीच्या मान्य केलेल्या कपात 
														सूचनांची एकत्रित यादी यासोबत आपल्या माहितीसाठी व योग्य त्या कार्यवाहीसाठी पाठविण्यात येत आहे.
													</fo:block>
													<fo:block>&#160;</fo:block>
													<fo:block>
														&#160;&#160;&#160;&#160;&#160;&#160;मा.अध्यक्ष, महाराष्ट्र विधानसभा यांनी दिलेल्या निदेशानुसार <fo:inline font-weight="bold">कपात सूचनांची उत्तरे मा.सदस्यांना 
														तीस दिवसाच्या आत पाठविण्यात यावीत तसेच उत्तराची एक प्रत या सचिवालयास माहितीसाठी पाठविण्यात यावी,</fo:inline> अशी आपणास विनंती आहे.
	            									</fo:block>
												</fo:block>	
												<fo:block>&#160;</fo:block>
												<fo:block>
													<fo:block text-align="right">
														<fo:block margin-right="3.1cm" font-weight="bold">आपला,</fo:block>
														<fo:block>&#160;</fo:block>
														<fo:block margin-right="2.5cm" font-size="15px" font-weight="bold">(दामोदर गायकर)</fo:block>
														<fo:block margin-right="2.5cm" font-weight="bold">कक्ष अधिकारी,</fo:block>							
														<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
													</fo:block>
												</fo:block>
												<fo:block font-size="10px">
            										&#160;
            									</fo:block>
												<fo:block font-weight="bold">याची प्रत माहितीसाठी व आवश्यक त्या कार्यवाहीसाठी अग्रेषित :- </fo:block>
												<fo:block font-size="6px">
            										&#160;
            									</fo:block>
												<fo:block>
													<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
						               					<fo:table-column column-number="1" column-width="6cm" />
								                        <fo:table-column column-number="2" column-width="6cm" />
								                        <fo:table-column column-number="3" column-width="6cm" />
								                        <fo:table-body>
								                        	<fo:table-row border-collapse="collapse">
								                        		<fo:table-cell>
								     								<fo:block text-align="left">
								     									<fo:block margin-left="1cm">१) प्रधान सचिव,</fo:block>
								     									<fo:block margin-left="1.5cm">वित्त विभाग,</fo:block>
								     									<fo:block margin-left="1.5cm">महाराष्ट्र शासन,</fo:block>
								     									<fo:block margin-left="1.5cm">मंत्रालय, मुंबई.</fo:block>
								     								</fo:block>
								     							</fo:table-cell>
								     							<fo:table-cell>
								     								<fo:block text-align="left">
								     									<fo:block margin-left="1cm">२) सचिव,</fo:block>
								     									<fo:block margin-left="1.5cm">संसदीय कार्य विभाग,</fo:block>
								     									<fo:block margin-left="1.5cm">महाराष्ट्र शासन,</fo:block>
								     									<fo:block margin-left="1.5cm">मंत्रालय, मुंबई.</fo:block>
								     								</fo:block>
								     							</fo:table-cell>
								     							<fo:table-cell>
								     								<fo:block text-align="left">
								     									<fo:block margin-left="1cm">३) कार्यासन अधिकारी,</fo:block>
								     									<fo:block margin-left="1.5cm">अर्थसंकल्प शाखा,</fo:block>
								     									<fo:block margin-left="1.5cm"><xsl:value-of select="./element_1/element_1_3" />,</fo:block>
								     									<fo:block margin-left="1.5cm">मंत्रालय, मुंबई.</fo:block>
								     								</fo:block>
								     							</fo:table-cell>
								                        	</fo:table-row>
								                        </fo:table-body>
						               				</fo:table>
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