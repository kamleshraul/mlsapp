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
	                  	margin-left="2cm" margin-right="2cm">
			      	<fo:region-body margin-top="1cm"/>        
				    <fo:region-before extent="2cm"/>
				    <fo:region-after region-name="page-number" extent="1.5cm"/>
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
	        
	        <fo:page-sequence master-reference="others" id="DocumentBody">
	        	
	        	<!-- header -->
	        	<fo:static-content flow-name="page-number">	        	
		        	<fo:block font-family="Kokila" font-size="9pt" text-align="right">
		        		Page <fo:page-number/>	        				        		
		        	</fo:block>
		        </fo:static-content>				
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
			       <!-- content as per report -->	
			       <fo:block font-family="Kokila" font-size="15pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
			       		<xsl:choose>
			       			<xsl:when test="boolean(element_1) and count(element_1)>=1">
			       				<fo:block font-size="20px" font-weight="bold" text-align="center">
									महाराष्ट्र <xsl:value-of select="element_1[1]/element_1_1"/>															
								</fo:block>
								<fo:block font-size="6px">&#160;</fo:block>
								<fo:block font-size="18px" font-weight="bold" text-align="center">
									<xsl:value-of select="element_1[1]/element_1_2"></xsl:value-of>						
								</fo:block>								
								<fo:block font-size="6px">&#160;</fo:block>
								<fo:block font-size="16px" font-weight="bold" text-align="center">
									<xsl:value-of select="element_1[1]/element_1_4"></xsl:value-of>
								</fo:block>
								<fo:block font-size="6px">&#160;</fo:block>
								<fo:block font-size="15px" font-weight="bold" text-align="center">
									<xsl:value-of select="element_1[1]/element_1_3"></xsl:value-of>
								</fo:block>
								<fo:block font-size="6px">&#160;</fo:block>
								<fo:block>
									<fo:table border="solid 0.2mm black" table-layout="fixed">										
										<xsl:choose>
											<xsl:when test="element_2='memberwise'">
												<fo:table-column column-number="1" column-width="1.2cm" />
												<fo:table-column column-number="2" column-width="6.2cm" />
												<fo:table-column column-number="3" column-width="3.2cm" />
						                        <fo:table-column column-number="4" column-width="3.2cm" />
						                        <fo:table-column column-number="5" column-width="3.2cm" />
											</xsl:when>
											<xsl:when test="element_2='datewise'">
												<fo:table-column column-number="1" column-width="1.2cm" />
												<fo:table-column column-number="2" column-width="3.2cm" />
												<fo:table-column column-number="3" column-width="4.2cm" />
						                        <fo:table-column column-number="4" column-width="4.2cm" />
						                        <fo:table-column column-number="5" column-width="4.2cm" />
											</xsl:when>
											<xsl:otherwise>
												सदस्यांचे नाव
											</xsl:otherwise>
										</xsl:choose>			               
				                        <fo:table-header>
											<fo:table-row>
												<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
													<fo:block font-weight="bold">
														अ.क्र.
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
													<fo:block font-weight="bold">
														<xsl:choose>
															<xsl:when test="element_2='memberwise'">
																सदस्यांचे नाव
															</xsl:when>
															<xsl:when test="element_2='datewise'">
																दिनांक
															</xsl:when>
															<xsl:otherwise>
																सदस्यांचे नाव
															</xsl:otherwise>
														</xsl:choose>														
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
													<fo:block font-weight="bold">
														ऑनलाईन सादर केलेल्या प्रश्नांची संख्या
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
													<fo:block font-weight="bold">
														ऑफलाईन सादर केलेल्या प्रश्नांची संख्या
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
													<fo:block font-weight="bold">
														एकूण सादर केलेल्या प्रश्नांची संख्या
													</fo:block>
												</fo:table-cell>
											</fo:table-row>
										</fo:table-header>
										<fo:table-body>
											<xsl:for-each select="element_1">
												<xsl:variable name="rowCount" select="position()"/>
												<fo:table-row>
													<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="center">
														<fo:block>
															<xsl:choose>
																<xsl:when test="boolean(../element_3)">
																	<xsl:value-of select="../element_3[$rowCount]"/>
																</xsl:when>
																<xsl:otherwise><xsl:value-of select="$rowCount"/></xsl:otherwise>
															</xsl:choose>															
														</fo:block>
													</fo:table-cell>
													<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="center">
														<fo:block>
															<xsl:value-of select="element_1_6"/>																		
														</fo:block>
													</fo:table-cell>
													<fo:table-cell border="solid 0.2mm black" margin-left="2px" margin-right="2px" padding="1.5px" display-align="center">
														<xsl:choose>
															<xsl:when test="element_1_7!='०'">
																<fo:block>
																	<xsl:choose>
																		<xsl:when test="../element_2='memberwise'">
																			<xsl:value-of select="element_1_7"/>
																		</xsl:when>
																		<xsl:when test="../element_2='datewise'">										
																			<fo:table>
																				<fo:table-column column-number="1" column-width="1.8cm" />
						                        								<fo:table-column column-number="2" column-width="2.4cm" />
																				<fo:table-body>
																					<fo:table-row>
																						<fo:table-cell text-align="left"><fo:block><xsl:value-of select="element_1_7"/></fo:block></fo:table-cell>
																						<fo:table-cell text-align="right"><fo:block>(<xsl:value-of select="element_1_13"/> सदस्य)</fo:block></fo:table-cell>
																					</fo:table-row>
																				</fo:table-body>
																			</fo:table>
																		</xsl:when>
																		<xsl:otherwise>
																			-
																		</xsl:otherwise>
																	</xsl:choose>																																																									
																</fo:block>
															</xsl:when>
															<xsl:otherwise><fo:block>-</fo:block></xsl:otherwise>
														</xsl:choose>																				
													</fo:table-cell>
													<fo:table-cell border="solid 0.2mm black" margin-left="2px" margin-right="2px" padding="1.5px" display-align="center">
														<xsl:choose>
															<xsl:when test="element_1_8!='०'">
																<fo:block>
																	<xsl:choose>
																		<xsl:when test="../element_2='memberwise'">
																			<xsl:value-of select="element_1_8"/>
																		</xsl:when>
																		<xsl:when test="../element_2='datewise'">										
																			<fo:table>
																				<fo:table-column column-number="1" column-width="1.8cm" />
						                        								<fo:table-column column-number="2" column-width="2.4cm" />
																				<fo:table-body>
																					<fo:table-row>
																						<fo:table-cell text-align="left"><fo:block><xsl:value-of select="element_1_8"/></fo:block></fo:table-cell>
																						<fo:table-cell text-align="right"><fo:block>(<xsl:value-of select="element_1_14"/> सदस्य)</fo:block></fo:table-cell>
																					</fo:table-row>
																				</fo:table-body>
																			</fo:table>
																		</xsl:when>
																		<xsl:otherwise>
																			-
																		</xsl:otherwise>
																	</xsl:choose>																																																									
																</fo:block>
															</xsl:when>
															<xsl:otherwise><fo:block>-</fo:block></xsl:otherwise>
														</xsl:choose>																				
													</fo:table-cell>
													<fo:table-cell border="solid 0.2mm black" margin-left="2px" margin-right="2px" padding="1.5px" display-align="center">
														<xsl:choose>
															<xsl:when test="element_1_9!='०'">
																<fo:block>
																	<xsl:choose>
																		<xsl:when test="../element_2='memberwise'">
																			<xsl:value-of select="element_1_9"/>
																		</xsl:when>
																		<xsl:when test="../element_2='datewise'">										
																			<fo:table>
																				<fo:table-column column-number="1" column-width="1.8cm" />
						                        								<fo:table-column column-number="2" column-width="2.4cm" />
																				<fo:table-body>
																					<fo:table-row>
																						<fo:table-cell text-align="left"><fo:block><xsl:value-of select="element_1_9"/></fo:block></fo:table-cell>
																						<fo:table-cell text-align="right"><fo:block>(<xsl:value-of select="element_1_15"/> सदस्य)</fo:block></fo:table-cell>
																					</fo:table-row>
																				</fo:table-body>
																			</fo:table>
																		</xsl:when>
																		<xsl:otherwise>
																			-
																		</xsl:otherwise>
																	</xsl:choose>																																																									
																</fo:block>
															</xsl:when>
															<xsl:otherwise><fo:block>-</fo:block></xsl:otherwise>
														</xsl:choose>																				
													</fo:table-cell>																				
												</fo:table-row>
											</xsl:for-each>
											<fo:table-row>
												<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="before">
													<fo:block font-weight="bold">
														एकूण
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="before">
													<fo:block>
														<xsl:choose>
															<xsl:when test="element_2='memberwise'">
																<fo:block font-weight="bold">ऑनलाईन सादर करणारे सदस्य: <xsl:value-of select="element_1[1]/element_1_13"/></fo:block>
																<fo:block font-weight="bold">ऑफलाईन सादर करणारे सदस्य: <xsl:value-of select="element_1[1]/element_1_14"/></fo:block>
															</xsl:when>		
															<xsl:otherwise>
																&#160;
															</xsl:otherwise>						
														</xsl:choose>												
													</fo:block>
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="before">
													<fo:block font-weight="bold">
														<xsl:value-of select="element_1[1]/element_1_10"/>																								
													</fo:block>																					
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="before">
													<fo:block font-weight="bold">
														<xsl:value-of select="element_1[1]/element_1_11"/>																								
													</fo:block>																					
												</fo:table-cell>
												<fo:table-cell border="solid 0.2mm black" margin-left="2px" padding="1.5px" display-align="before">
													<fo:block font-weight="bold">
														<xsl:value-of select="element_1[1]/element_1_12"/>																					
													</fo:block>																					
												</fo:table-cell>																				
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
			       			</xsl:when>
			       			<xsl:otherwise>
			       				अद्याप या अधिवेशनासाठी कोणत्याही सदस्याने प्रश्न सादर केलेले नाहीत.
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