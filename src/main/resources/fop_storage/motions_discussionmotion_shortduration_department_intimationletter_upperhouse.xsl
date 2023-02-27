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
            			<fo:block text-align="center" font-weight="bold">महाराष्ट्र विधानपरिषद</fo:block>
            			<fo:block text-align="center" font-weight="bold"><xsl:value-of select="./element_2/element_2_10"></xsl:value-of></fo:block>
	            		<fo:block font-size="14px">&#160;</fo:block>    
	            			 <xsl:if test="./element_2/element_2_10='motions_discussionmotion_lastweek'">
																<fo:block font-weight="bold" text-align="justify">
							सर्वश्री <xsl:value-of select="./element_2/element_2_8"></xsl:value-of> वि.स.स यांचा म.वि.स. नियम २९२ अन्वये प्रस्तराव :
						</fo:block>	
								</xsl:if>
	            					 <xsl:if test="./element_2/element_2_10='motions_discussionmotion_publicimportance'">
	            					         						<fo:block font-weight="bold" text-align="justify">
							सर्वश्री <xsl:value-of select="./element_2/element_2_8"></xsl:value-of> वि.स.स यांचा म.वि.स. नियम २९३ अन्वये प्रस्तराव :
						</fo:block>	 					 
	            					</xsl:if>
	            					
	            					 
	            					         						<fo:block font-weight="bold" text-align="justify">
							&#160;&#160;&#160;&#160; <xsl:value-of select="./element_2/element_2_7"></xsl:value-of> वि.प.स यांनी दिलेली म.वि.प. नियम ९७ अन्वये अल्पकालीन चर्चेची सूचना क्रमांक  <xsl:value-of select="./element_2/element_2_2"></xsl:value-of> पुढीलप्रमाणे आहे:-
						</fo:block>	 					 
	            					
	            		
	<fo:block>
												&#160;
											</fo:block>
											<fo:block>
												&#160;
											</fo:block>
						<fo:block text-align="justify">
						
							&#160;&#160;&#160;&#160;&#160;&#160; "<xsl:value-of select="./element_2/element_2_4"></xsl:value-of>"
						</fo:block>
						
						<fo:block page-break-before="always">&#160;</fo:block>
						<fo:block>
							<fo:table>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-weight="bold" text-align="left" text-decoration="underline">
																	अतितात्काळ
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
															<fo:table-cell>
																<fo:block>
												&#160;
											</fo:block>
															</fo:table-cell>
														</fo:table-row>
														<fo:table-row>
															<fo:table-cell width="200px" text-align="justify">
																<fo:block font-weight="bold">
																	क्रमांक :
																	<fo:inline font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;/म.वि.स./ई-२</fo:inline>
																</fo:block>
															</fo:table-cell>										
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="justify">
																<fo:block font-weight="bold">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="justify">
																<fo:block font-weight="bold">विधान भवन मुंबई / नागपूर</fo:block>
															</fo:table-cell>
														</fo:table-row>
														
														<fo:table-row>
															<fo:table-cell width="200px" text-align="justify"><fo:block font-weight="bold">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block></fo:table-cell>
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
						<fo:block font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;संदर्भ :या सचिवालयाचे पत्र क्रमांक &#160;&#160;/म.वि.स./ई-२,दिनांक &#160;&#160;&#160;</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;</fo:block>								
						<fo:block>
						<fo:inline font-weight="bold">
							 सचिव, महाराष्ट्र शासन,<xsl:value-of select="./element_2/element_2_9"></xsl:value-of>&#160;</fo:inline>यांच्याकडे पुढील आवश्यक कार्यवाहीसाठी अग्रेषित							
						</fo:block>
						
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;चालू अधिवेशनासाठी मा.उप सभापतींनी सदर सूचना वरील स्वरुपात स्वीकृत केली असून या सूचनेवरील सभागृहातील चर्चेची तारीख मागाहून कळविण्यात येईल.					
						</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;</fo:block>
							<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;चालू अधिवेशनासाठी मा.उप सभापतींनी सदर सूचना वरील स्वरुपात स्वीकृत केली असून या सूचनेवरील चर्चा दिनांक  <fo:inline font-weight="bold" text-decoration="underline"> <xsl:value-of select="./element_2/element_2_6"></xsl:value-of></fo:inline>  रोजी घेण्यात येणार आहे.					
						</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;</fo:block>
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
																	<fo:block>आपली / आपला </fo:block>
																</fo:block>
															</fo:table-cell>										
														</fo:table-row>
														<fo:table-row>
															<fo:table-cell width="200px" text-align="center">
																<fo:block>																			
																	<fo:block>&#160;</fo:block>
																</fo:block>
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
						<fo:block>&#160;</fo:block>
						<fo:block >
							१) मा.<fo:inline font-weight="bold"><xsl:value-of select="./element_2/element_2_5"></xsl:value-of> </fo:inline> यांना माहितीसाठी सादर अग्रेषित.
							
						</fo:block>	   
						<fo:block >
							२) मा.<fo:inline font-weight="bold"><xsl:value-of select="./element_2/element_2_7"></xsl:value-of></fo:inline> वि.प.स.यांना माहितीसाठी सादर अग्रेषित.
							
						</fo:block>	           			
					
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>