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
	                  	page-height="21cm" page-width="29.7cm"
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
	        
	        <fo:page-sequence master-reference="first" id="DocumentBody">
	        	
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
			       <fo:block font-family="Kokila" font-size="15px">
			       		<fo:block font-size="18px" text-align="center" font-weight="bold">
			       			<fo:block><xsl:value-of select="element_3[1]"/></fo:block>
			       			<fo:block><xsl:value-of select="element_3[2]"/></fo:block>
			       			<fo:block><xsl:value-of select="element_3[3]"/></fo:block>
			       		</fo:block>
			       		 <fo:block>
			       		 	<fo:table>
			       				<fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="3cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>
						        <fo:table-column column-width="1.66cm"/>						        
						        <fo:table-header>
						        	<fo:table-row>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[4]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[6]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[8]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[10]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[12]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[14]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[16]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[18]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[20]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[22]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[24]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[26]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[28]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center">
						        				<xsl:apply-templates select="element_3[30]"/>
						        			</fo:block>
						        		</fo:table-cell>						        		
						        	</fo:table-row>
						        </fo:table-header>
						        <fo:table-body>
						        	<xsl:for-each select="element_1">			
						        		<xsl:variable name="count" select="position()"/>
						        		<fo:table-row>      					        	
							        		<xsl:choose>
							        			<xsl:when test="position()!=last()">
							        				<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:value-of select="../element_2[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="left" margin-left="0.2cm">
									        				<xsl:value-of select="element_1_3"/>.									        				
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_4!=''">											
																	<xsl:value-of select="element_1_4"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>									        				
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_5!=''">											
																	<xsl:value-of select="element_1_5"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>									        				
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_6!=''">											
																	<xsl:value-of select="element_1_6"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_7!=''">											
																	<xsl:value-of select="element_1_7"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_8!=''">											
																	<xsl:value-of select="element_1_8"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_9!=''">											
																	<xsl:value-of select="element_1_9"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_10!=''">											
																	<xsl:value-of select="element_1_10"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_11!=''">											
																	<xsl:value-of select="element_1_11"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_12!=''">											
																	<xsl:value-of select="element_1_12"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_13!=''">											
																	<xsl:value-of select="element_1_13"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_14!=''">											
																	<xsl:value-of select="element_1_14"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_15!=''">											
																	<xsl:value-of select="element_1_15"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>									        		
							        			</xsl:when>
							        			<xsl:otherwise>
							        				<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:value-of select="../element_2[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="left" margin-left="0.2cm">
									        				<xsl:value-of select="element_1_3"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_4!=''">											
																	<xsl:value-of select="element_1_4"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_5!=''">											
																	<xsl:value-of select="element_1_5"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_6!=''">											
																	<xsl:value-of select="element_1_6"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_7!=''">											
																	<xsl:value-of select="element_1_7"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_8!=''">											
																	<xsl:value-of select="element_1_8"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_9!=''">											
																	<xsl:value-of select="element_1_9"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_10!=''">											
																	<xsl:value-of select="element_1_10"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_11!=''">											
																	<xsl:value-of select="element_1_11"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_12!=''">											
																	<xsl:value-of select="element_1_12"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_13!=''">											
																	<xsl:value-of select="element_1_13"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_14!=''">											
																	<xsl:value-of select="element_1_14"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="right" margin-right="0.3cm">
									        				<xsl:choose>
																<xsl:when test="element_1_15!=''">											
																	<xsl:value-of select="element_1_15"/>
																</xsl:when>
																<xsl:otherwise>
																	-
																</xsl:otherwise>
															</xsl:choose>
									        			</fo:block>
									        		</fo:table-cell>									        		
							        			</xsl:otherwise>
							        		</xsl:choose>
						        		</fo:table-row>							        								        		
					        		</xsl:for-each>	
					        		<fo:table-row>  
					        			<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				&#160;
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="left" margin-left="0.2cm">
						        				<xsl:apply-templates select="element_3[32]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_16!=''">											
														<xsl:value-of select="element_1[1]/element_1_16"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>						        				
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_17!=''">											
														<xsl:value-of select="element_1[1]/element_1_17"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_18!=''">											
														<xsl:value-of select="element_1[1]/element_1_18"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_19!=''">											
														<xsl:value-of select="element_1[1]/element_1_19"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_20!=''">											
														<xsl:value-of select="element_1[1]/element_1_20"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_21!=''">											
														<xsl:value-of select="element_1[1]/element_1_21"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_22!=''">											
														<xsl:value-of select="element_1[1]/element_1_22"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_23!=''">											
														<xsl:value-of select="element_1[1]/element_1_23"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_24!=''">											
														<xsl:value-of select="element_1[1]/element_1_24"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_25!=''">											
														<xsl:value-of select="element_1[1]/element_1_25"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_26!=''">											
														<xsl:value-of select="element_1[1]/element_1_26"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="right" margin-right="0.3cm">
						        				<xsl:choose>
													<xsl:when test="element_1[1]/element_1_27!=''">											
														<xsl:value-of select="element_1[1]/element_1_27"/>
													</xsl:when>
													<xsl:otherwise>
														-
													</xsl:otherwise>
												</xsl:choose>
						        			</fo:block>
						        		</fo:table-cell>						        		
					        		</fo:table-row>
					        	</fo:table-body>					        	
			       			</fo:table>
			       		 </fo:block>			       		
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