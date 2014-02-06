<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="VishaysuchiData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>

	<!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
    <xsl:template match="VishaysuchiData">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="1cm" margin-bottom="1cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0.9cm"/>
			      	<fo:region-before region-name="rb-common" extent="1cm"/>
			      	<fo:region-after region-name="ra-common" extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="1cm" margin-bottom="1cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0.9cm"/>
			      	<fo:region-before region-name="rb-common" extent="1cm"/>
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
	        
        	<xsl:variable name="outputFormat">
				<xsl:choose>
					<xsl:when test="$formatOut='application/pdf'">
						<xsl:value-of select="'simple'"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'first'"></xsl:value-of>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:variable>
	        
	        <fo:page-sequence master-reference="{$outputFormat}" id="DocumentBody">
	        	
	        	<!-- header /left/start/right/end-->
	        	<fo:static-content flow-name="rb-common">
					<fo:block text-align="center" font-family="{$font}" font-size="18px">
					   	विषयसूची
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<!--<fo:static-content flow-name="ra-common">
					<fo:block text-align="center/left/start/right/end" font-family="{$font}">-->
					   	<!-- content for footer for all pages -->
					<!--</fo:block>
			    </fo:static-content>-->
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
	            	<fo:block font-family="{$font}">		       
				       <xsl:choose>
							<xsl:when test="not(./vishaysuchiList)">
								<fo:block>
									विषयसूची बनवता येत नाही.
								</fo:block>
							</xsl:when>						
							<xsl:otherwise>
								<xsl:variable name="tempPrevCatchWordIndex">-</xsl:variable>
								<xsl:for-each select="./vishaysuchiList/vishaysuchi">											
									<xsl:choose>
										<xsl:when test="./type='member'">
											<xsl:if test="not(not(./vishaysuchiDevices))">
												<fo:table>
													<fo:table-body>
														<xsl:if test="(string-length(catchWordIndex)>0) and (not(./headings) or not(./vishaysuchiDevices))">
															<fo:table-row>
																<fo:table-cell>
																	<fo:block text-align="center" font-weight="bold">														
																		<xsl:choose>
																			<xsl:when test="position()=1 and not(preceding-sibling::vishaysuchi[1]/catchWordIndex)">
																			"<xsl:value-of select="./catchWordIndex"></xsl:value-of>"
																			</xsl:when>
																			<xsl:when test="position() > 1 and preceding-sibling::vishaysuchi[1]/catchWordIndex!=./catchWordIndex">
																				"<xsl:value-of select="./catchWordIndex"></xsl:value-of>"
																			</xsl:when>
																			<xsl:otherwise>
																				&#160;
																			</xsl:otherwise>
																		</xsl:choose>															
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-family="{$font}" font-size="12px">
																		<xsl:choose>
																			<xsl:when test="position()=1 and not(preceding-sibling::vishaysuchi[1]/catchWordIndex)">
																				पृष्ठ क्रमांक
																			</xsl:when>
																			<xsl:when test="position() > 1 and preceding-sibling::vishaysuchi[1]/catchWordIndex!=./catchWordIndex">
																				पृष्ठ क्रमांक
																			</xsl:when>
																			<xsl:otherwise>
																				&#160;
																			</xsl:otherwise>
																		</xsl:choose>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<fo:table-row>
															<fo:table-cell number-columns-spanned="2">
																<fo:block><xsl:value-of select="./value"></xsl:value-of></fo:block>
																<xsl:if test="not(not(./vishaysuchiDevices))">
																	<xsl:for-each select="./vishaysuchiDevices">
																		<xsl:choose>
																			<xsl:when test="position()=1 or starts-with(preceding-sibling::vishaysuchiDevices[1]/deviceType,'questions_')!=starts-with(./deviceType,'questions_')">
																				<xsl:choose>
																					<xsl:when test="starts-with(./deviceType,'questions_')">
																						<fo:block margin-left="14px">प्रश्नोत्तरे-----</fo:block>
																					</xsl:when>
																					<xsl:otherwise>
																						<fo:block margin-left="14px"><xsl:value-of select="./deviceName"></xsl:value-of>-----</fo:block>
																					</xsl:otherwise>
																				</xsl:choose>
																				<xsl:choose>
																					<xsl:when test="position()=1 or preceding-sibling::vishaysuchiDevices[1]/catchwordHeading/catchWord!=./catchwordHeading/catchWord">
																						<fo:block margin-left="28px"><xsl:value-of select="./catchwordHeading/catchWord" />-----</fo:block>
																						<fo:block margin-left="42px"><xsl:value-of select="./catchwordHeading/heading" /></fo:block>
																					</xsl:when>
																					<xsl:otherwise>
																						<fo:block margin-left="42px"><xsl:value-of select="./catchwordHeading/heading" /></fo:block>
																					</xsl:otherwise>
																				</xsl:choose>
																			</xsl:when>
																			<xsl:otherwise>
																				<xsl:choose>
																					<xsl:when test="position()=1 or preceding-sibling::vishaysuchiDevices[1]/catchwordHeading/catchWord!=./catchwordHeading/catchWord">
																						<fo:block margin-left="28px"><xsl:value-of select="./catchwordHeading/catchWord" />-----</fo:block>
																						<fo:block margin-left="42px"><xsl:value-of select="./catchwordHeading/heading" /></fo:block>
																					</xsl:when>
																					<xsl:otherwise>
																						<fo:block margin-left="42px"><xsl:value-of select="./catchwordHeading/heading" /></fo:block>
																					</xsl:otherwise>
																				</xsl:choose>
																			</xsl:otherwise>										
																		</xsl:choose> 
																	</xsl:for-each>								
																</xsl:if>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-body>
												</fo:table>
											</xsl:if>
										</xsl:when>
										<xsl:when test="./type='catchWord'">
											<xsl:if test="not(not(./headings))">
												<fo:table>
													<fo:table-body>
														<xsl:if test="(string-length(catchWordIndex)>0) and (not(./headings) or not(./vishaysuchiDevices))">
															<fo:table-row>
																<fo:table-cell>
																	<fo:block text-align="center" font-weight="bold">			
																		<xsl:choose>
																			<xsl:when test="position()=1 and (preceding-sibling::vishaysuchi[1]/catchWordIndex!=./catchWordIndex or not(preceding-sibling::vishaysuchi[1]/catchWordIndex))">
																				"<xsl:value-of select="./catchWordIndex"></xsl:value-of>"
																			</xsl:when>
																			<xsl:when test="position() > 1 and (preceding-sibling::vishaysuchi[1]/catchWordIndex!=./catchWordIndex or not(preceding-sibling::vishaysuchi[1]/catchWordIndex))">
																				"<xsl:value-of select="./catchWordIndex"></xsl:value-of>"
																			</xsl:when>
																			<xsl:otherwise>
																				&#160;
																			</xsl:otherwise>
																		</xsl:choose>																	
																	</fo:block>
																</fo:table-cell>
																<fo:table-cell>
																	<fo:block text-align="right" font-family="{$font}" font-size="12px">
																	   	<xsl:choose>
																			<xsl:when test="position()=1 and not(preceding-sibling::vishaysuchi[1]/catchWordIndex)">
																				पृष्ठ क्रमांक
																			</xsl:when>
																			<xsl:when test="position() > 1 and preceding-sibling::vishaysuchi[1]/catchWordIndex!=./catchWordIndex">
																				पृष्ठ क्रमांक
																			</xsl:when>
																			<xsl:otherwise>
																				&#160;
																			</xsl:otherwise>
																		</xsl:choose>
																	</fo:block>
																</fo:table-cell>
															</fo:table-row>
														</xsl:if>
														<fo:table-row>
															<fo:table-cell number-columns-spanned="2">
																<fo:block>&#160;</fo:block>
																<fo:block><xsl:value-of select="./value" />-----</fo:block>							
																<xsl:for-each select="./headings">
																	<fo:block margin-left="14px">
																		<xsl:value-of select="./heading" />
																		<xsl:value-of select="./heading" />
																		<xsl:choose>
																			<xsl:when test="starts-with(deviceType,'questions_')">
																				(प्रश्न)
																			</xsl:when>
																			<xsl:when test="starts-with(deviceType,'resolutions_')">
																				(ठराव)
																			</xsl:when>
																			<xsl:when test="starts-with(deviceType,'motions_')">
																				(प्रस्ताव)
																			</xsl:when>
																			<xsl:when test="starts-with(deviceType,'bills_')">
																				(विधेयक)
																			</xsl:when>
																		</xsl:choose>
																	</fo:block>
																</xsl:for-each>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-body>
												</fo:table>
											</xsl:if>
										</xsl:when>
									</xsl:choose>			
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
			    </fo:flow>
	        </fo:page-sequence>
	    </fo:root>
    </xsl:template>      
    
    <!-- use for for loop with fixed no. of iterations -->
    <!-- <xsl:template match="/">
	    Start repeating
	    <xsl:call-template name="repeatable" />
	</xsl:template>
	
	<xsl:template name="repeatable">
	    <xsl:param name="index" select="1" />
	    <xsl:param name="total" select="10" />
	
	    Do something
	
	    <xsl:if test="not($index = $total)">
	        <xsl:call-template name="repeatable">
	            <xsl:with-param name="index" select="$index + 1" />
	        </xsl:call-template>
	    </xsl:if>
	</xsl:template> -->
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>