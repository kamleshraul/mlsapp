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
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before extent="2cm"/>
			      	<fo:region-after extent="1.5cm"/>
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
	        
	        <fo:page-sequence master-reference="simple" id="DocumentBody">
	        	
	        	<!-- header -->
	        	<fo:static-content flow-name="rb-common">
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for header for all pages -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-common">
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for footer for all pages -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
			       <!-- content as per report -->
			       <fo:block text-align="center" font-family="{$font}" font-size="16pt" font-weight="bold">
						प्रतिवेदन वेळापत्रक आखणी
					</fo:block>
					
					<fo:block text-align="center" font-family="{$font}" font-size="11pt" font-weight="bold">
						दिनांक: <xsl:value-of select="reportDate"></xsl:value-of>
					</fo:block>
					
	                <fo:block font-family="{$font}" font-size="12px" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
	                    <fo:table table-layout="fixed" width="100%">
	                        <fo:table-column column-number="1" column-width="4cm" />
	                        <fo:table-column column-number="2" column-width="1.5cm" />
	                        <fo:table-column column-number="3" column-width="2.5cm" />
	                        <fo:table-column column-number="4" column-width="1.5cm" />
	                        <fo:table-column column-number="5" column-width="2.5cm" />
	                        <fo:table-column column-number="6" column-width="1.5cm" />
	                        <fo:table-column column-number="7" column-width="2.5cm" />	                        
							<fo:table-header>
								<fo:table-row border="solid 0.1mm black">
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block>
											Reporter Name
										</fo:block>										
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot Timing</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot Timing</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot</fo:block>
									</fo:table-cell>
									<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
										<fo:block text-align="center" font-weight="bold">Slot Timing</fo:block>
									</fo:table-cell>									
								</fo:table-row>
							</fo:table-header>
	                        <fo:table-body>	                        	
								<xsl:for-each select="element_1">																	
									<xsl:variable name="numberOfRowsForReporter" select="(floor(count(element_1_2) div 3)) + 1"/>																						
									<xsl:variable name="reporterName" select="element_1_1"/>
									<xsl:for-each select="element_1_2">										
										<xsl:if test="position()=1 or position() mod 3 = 0">
											<xsl:variable name="slotNumber" select="position()"/>	
											<xsl:choose>
												<xsl:when test="$slotNumber=1">
													<fo:table-row border="solid 0.1mm black">
														<xsl:for-each select=". | following-sibling::element_1_2[1] |
												                              following-sibling::element_1_2[2]">									           
												           	   <xsl:if test="position()=1">
												           	   		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
														       			<xsl:attribute name="number-rows-spanned">
														       				<xsl:value-of select="$numberOfRowsForReporter"/>
														       			</xsl:attribute>
																		<fo:block>	
																			<xsl:value-of select="$reporterName" />
																		</fo:block>
																	</fo:table-cell>																	
												           	   </xsl:if>
													           <fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
																	<fo:block>
																		<xsl:value-of select="element_1_2_1" />
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
																	<fo:block>
																		<xsl:value-of select="element_1_2_2" />
																	</fo:block>
																</fo:table-cell>																												
										       			</xsl:for-each>
							       					</fo:table-row>
												</xsl:when>
												<xsl:otherwise>
													<fo:table-row border="solid 0.1mm black">
														<xsl:for-each select=". | following-sibling::element_1_2[1] |
												                              following-sibling::element_1_2[2]">									           
												           	    <fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
																	<fo:block>
																		<xsl:value-of select="element_1_2_1" />
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
																	<fo:block>
																		<xsl:value-of select="element_1_2_2" />
																	</fo:block>
																</fo:table-cell>																												
										       			</xsl:for-each>
							       					</fo:table-row>
												</xsl:otherwise>
											</xsl:choose>																													       
									    </xsl:if>																																						
									</xsl:for-each>																									
								</xsl:for-each>
	                        </fo:table-body>
	                    </fo:table>
	                </fo:block>
	            </fo:flow>
	        </fo:page-sequence>
	    </fo:root>
    </xsl:template>  
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>    
</xsl:stylesheet>