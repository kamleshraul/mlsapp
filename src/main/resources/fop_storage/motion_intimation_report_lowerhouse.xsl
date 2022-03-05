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
	            	<fo:block font-family="Mangal" font-size="10.5px" line-height="20pt">
            			<!-- <fo:block text-align="center" font-weight="bold" font-size="14pt">
	            			महाराष्ट्र विधानमंडळ सचिवालय					
	            		</fo:block> -->
	            		
	            		<!-- <fo:block text-align="left" font-weight="bold" text-decoration="underline">
           					अति तात्काळ
           				</fo:block>
           				<fo:block text-align="left" font-weight="bold">
           					<xsl:value-of select="./element_2/element_2_9"></xsl:value-of>
           				</fo:block>
	            		<fo:block font-size="6px">&#160;</fo:block>
								
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
																	<fo:inline font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;/ई-२,</fo:inline>
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
						</fo:block> -->
						
						<fo:block margin-top="3cm">&#160;</fo:block>
						
						<fo:block font-weight="bold">
							प्रेषक : 
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;प्रधान सचिव,
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानमंडळ सचिवालय.
						</fo:block>
						
						<fo:block>&#160;</fo:block>
						
						<fo:block font-weight="bold">
							प्रति : 
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;प्रधान सचिव,
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="./element_2/element_2_4"></xsl:value-of>&#160;विभाग,
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र शासन,
						</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;मंत्रालय, मुंबई.
						</fo:block>
						
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;</fo:block>
						
						<fo:block font-weight="bold">						
							<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;विषय : <xsl:value-of select="./element_2/element_2_10"></xsl:value-of></fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;संदर्भ : <xsl:value-of select="./element_2/element_2_11"></xsl:value-of>, वि.स.स. यांची म.वि.स. नियम १०५ अन्वये</fo:block>
							<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;या सचिवालयास प्राप्त झालेली लक्षवेधी सूचना क्रमांक <xsl:value-of select="./element_2/element_2_3"></xsl:value-of> (प्रत संलग्न)</fo:block>
							
							<!-- <xsl:choose>
							<xsl:when test="./element_2/element_2_7!=''">
	    						<fo:inline>
	    							व इतर जोडलेल्या <xsl:value-of select="translate(./element_2/element_2_7,'','')"></xsl:value-of> 		
	    						</fo:inline>
	        				</xsl:when>
	           				</xsl:choose> -->											
						</fo:block>	
						
						<fo:block>&#160;</fo:block>		
						<fo:block>&#160;</fo:block>			
													
						<fo:block>महोदय,</fo:block>
						
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;निदेशानुसार आपणास कळविण्यात येते की, उपरोक्त विषयात नमूद करण्यात आलेली बाब अत्यंत गंभीर स्वरुपाची असून, सदरहू प्रकरणाची वस्तुस्थिती त्वरित मा.अध्यक्षांना जाणून घ्यावयाची असल्याने ती या सचिवालयाकडे विनाविलंब पाठवावी, ही विनंती.
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
															<fo:table-cell width="200px" text-align="center">
																<fo:block>																			
																	आपला,
																	<fo:block>&#160;</fo:block>
																	<fo:block>&#160;</fo:block>
																</fo:block>
															</fo:table-cell>										
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="center">
																<fo:block font-weight="bold">(सुनिल परब)</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="center">
																<fo:block font-weight="bold">कक्ष अधिकारी</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="center">
																<fo:block >महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
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
						
						<fo:block page-break-before="always">&#160;</fo:block>
						
						<fo:block>
							<fo:block font-size="14px">लक्षवेधी सूचनेची संलग्न प्रत (वस्तुस्थितीकरिता प्रस्तावित)</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block font-weight="bold">लक्षवेधी सूचना क्रमांक : <xsl:value-of select="./element_2/element_2_3"></xsl:value-of> </fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="./element_2/element_2_6"></xsl:value-of> वि.प.स.</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block><xsl:value-of select="./element_2/element_2_8"></xsl:value-of> </fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block>
								&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45;&#45; 				
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