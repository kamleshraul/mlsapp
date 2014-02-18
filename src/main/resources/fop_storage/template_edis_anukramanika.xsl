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
					   	अनुक्रमणिका
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
							<xsl:when test="not(./element_1)">
								<fo:block>
									अनुक्रमणिका बनवता येत नाही.
								</fo:block>
							</xsl:when>						
							<xsl:otherwise>
								<xsl:for-each select="./element_1">
									<xsl:if test="string-length(element_1_2)>0">
										<xsl:if test="position()=1 or preceding-sibling::element_1[1]/element_1_12!=element_1_12">
											<fo:block text-align="center" border-width="1pt" border-color="black" border-style="solid">
												<xsl:value-of select="element_1_12"></xsl:value-of>
											</fo:block>
											<xsl:if test="position()=1">
												<fo:block font-weight="bold" font-size="12px" text-align="center">
													अनुक्रमणिका
												</fo:block>
											</xsl:if>
										</xsl:if>
										<xsl:choose>
											<xsl:when test="string-length(element_1_10)>0">
												<xsl:if test="position()=1 or (preceding-sibling::element_1[1]/element_1_10!=element_1_10)">
													<xsl:if test="starts-with(element_1_10,'questions_') and position()=1">
														<fo:block font-weight="bold" margin-left="12px">
															प्रश्नोतर
														</fo:block>
													</xsl:if>
												</xsl:if>
												<fo:block margin-left="30px">
													<xsl:value-of select="element_1_2"></xsl:value-of>
												</fo:block>
											</xsl:when>
											<xsl:otherwise>
												<fo:block font-weight="bold" margin-left="12px">
													<xsl:value-of select="element_1_2"></xsl:value-of>
												</fo:block>												
											</xsl:otherwise>						
										</xsl:choose>
									</xsl:if>									
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