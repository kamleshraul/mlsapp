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
			       			<fo:block><xsl:value-of select="element_3[4]"/></fo:block>
			       		</fo:block>
			       		<fo:block font-size="5px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="10.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2">
										<fo:block>
											<fo:table table-layout="fixed" border-collapse="collapse">
												<fo:table-column column-width="7.5cm"/>
										        <fo:table-column column-width="3cm"/>
										        <fo:table-body>
										        	<fo:table-row>
										        		<fo:table-cell margin-left="0.3cm"><fo:block><xsl:value-of select="element_3[5]"/></fo:block></fo:table-cell>
										        		<xsl:choose>
										        			<xsl:when test="element_1[1]/element_1_8!=''">
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block><xsl:value-of select="element_1[1]/element_1_8"/></fo:block></fo:table-cell>
										        			</xsl:when>
										        			<xsl:otherwise>
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block>-</fo:block></fo:table-cell>
										        			</xsl:otherwise>
										        		</xsl:choose>										        		
										        	</fo:table-row>
										        	<fo:table-row>
										        		<fo:table-cell margin-left="0.3cm"><fo:block><xsl:value-of select="element_3[6]"/></fo:block></fo:table-cell>
										        		<xsl:choose>
										        			<xsl:when test="element_1[1]/element_1_9!=''">
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block><xsl:value-of select="element_1[1]/element_1_9"/></fo:block></fo:table-cell>
										        			</xsl:when>
										        			<xsl:otherwise>
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block>-</fo:block></fo:table-cell>
										        			</xsl:otherwise>
										        		</xsl:choose>
										        	</fo:table-row>
										        	<fo:table-row>
										        		<fo:table-cell margin-left="0.3cm"><fo:block><xsl:value-of select="element_3[7]"/></fo:block></fo:table-cell>
										        		<xsl:choose>
										        			<xsl:when test="element_1[1]/element_1_13!=''">
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block><xsl:value-of select="element_1[1]/element_1_13"/></fo:block></fo:table-cell>
										        			</xsl:when>
										        			<xsl:otherwise>
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block>-</fo:block></fo:table-cell>
										        			</xsl:otherwise>
										        		</xsl:choose>
										        	</fo:table-row>
										        	<fo:table-row>
										        		<fo:table-cell margin-left="0.3cm"><fo:block><xsl:value-of select="element_3[8]"/></fo:block></fo:table-cell>
										        		<xsl:choose>
										        			<xsl:when test="element_1[1]/element_1_10!=''">
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block><xsl:value-of select="element_1[1]/element_1_10"/></fo:block></fo:table-cell>
										        			</xsl:when>
										        			<xsl:otherwise>
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block>-</fo:block></fo:table-cell>
										        			</xsl:otherwise>
										        		</xsl:choose>
										        	</fo:table-row>
										        	<fo:table-row>
										        		<fo:table-cell margin-left="0.3cm"><fo:block><xsl:value-of select="element_3[9]"/></fo:block></fo:table-cell>
										        		<xsl:choose>
										        			<xsl:when test="element_1[1]/element_1_14!=''">
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block><xsl:value-of select="element_1[1]/element_1_14"/></fo:block></fo:table-cell>
										        			</xsl:when>
										        			<xsl:otherwise>
										        				<fo:table-cell text-align="right" margin-right="0.3cm"><fo:block>-</fo:block></fo:table-cell>
										        			</xsl:otherwise>
										        		</xsl:choose>
										        	</fo:table-row>
										        </fo:table-body>
											</fo:table>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
				      	</fo:block>		
				      	<fo:block font-size="3px">&#160;</fo:block>	    
				      	<fo:block>
							<fo:table table-layout="fixed" border-collapse="collapse">
								<fo:table-column column-width="1.5cm"/>
						        <fo:table-column column-width="3cm"/>
						        <fo:table-column column-width="3cm"/>
						        <fo:table-column column-width="4cm"/>
						        <fo:table-column column-width="10cm"/>
						        <fo:table-column column-width="3cm"/>
						        <fo:table-header>
						        	<fo:table-row>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:apply-templates select="element_3[10]"/></fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:apply-templates select="element_3[11]"/></fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:apply-templates select="element_3[12]"/></fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:value-of select="element_3[13]"/></fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:value-of select="element_3[14]"/></fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" text-align="center" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block font-weight="bold"><xsl:value-of select="element_3[15]"/></fo:block>
						        		</fo:table-cell>
						        	</fo:table-row>
						        </fo:table-header>
						        <fo:table-body>
						        	<xsl:for-each select="element_1">
						        		<xsl:variable name="count" select="position()"/>
						        		<fo:table-row>
							        		<xsl:choose>
							        			<xsl:when test="position()!=last()">
							        				<fo:table-cell text-align="right" margin-right="0.5cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="../element_2[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="1.1cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="element_1_2"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.8cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="element_1_15"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block font-weight="bold">
									        				<xsl:value-of select="element_1_4"/>			
									        			</fo:block>									        										        			
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.8cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="element_1_3"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="1cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:apply-templates select="element_1_5"/>
									        			</fo:block>
									        		</fo:table-cell>
							        			</xsl:when>
							        			<xsl:otherwise>
							        				<fo:table-cell text-align="right" margin-right="0.5cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="../element_2[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="1.1cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="element_1_2"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.8cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="element_1_15"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block font-weight="bold">
									        				<xsl:value-of select="element_1_4"/>			
									        			</fo:block>									        										        			
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.8cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="element_1_3"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="1cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:apply-templates select="element_1_5"/>
									        			</fo:block>
									        		</fo:table-cell>
							        			</xsl:otherwise>
							        		</xsl:choose>
							        	</fo:table-row>
						        	</xsl:for-each>						        	
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