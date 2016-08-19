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
	                  	margin-top="1.8cm" margin-bottom="1.5cm"
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
			       <fo:block>
			       		<!-- scale 1 -->
			       		<xsl:if test="boolean(element_1)">
			       			<fo:block font-size="10px"><xsl:value-of select="element_1"/></fo:block>
							<xsl:variable name="indicator_scale_img">images/<xsl:value-of select="element_1"/></xsl:variable>
							<fo:block>
					       		<fo:external-graphic content-height="24cm" content-width="18cm">
							      	<xsl:attribute name="src">
							             <xsl:value-of select="$indicator_scale_img" />
							       	</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</xsl:if>
						<!-- scale 2 -->
						<xsl:if test="boolean(element_2)">
							<fo:block font-size="10px">&#160;</fo:block>
							<xsl:variable name="indicator_scale_img">images/<xsl:value-of select="element_2"/></xsl:variable>
							<fo:block>
					       		<fo:external-graphic content-height="5cm"  content-width="18cm">
							      	<xsl:attribute name="src">
							             <xsl:value-of select="$indicator_scale_img" />
							       	</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</xsl:if>	
						<!-- scale 3 -->
						<xsl:if test="boolean(element_3)">
							<fo:block font-size="10px">&#160;</fo:block>
							<xsl:variable name="indicator_scale_img">images/<xsl:value-of select="element_3"/></xsl:variable>
							<fo:block>
					       		<fo:external-graphic content-height="5cm"  content-width="18cm">
							      	<xsl:attribute name="src">
							             <xsl:value-of select="$indicator_scale_img" />
							       	</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</xsl:if>	
						<!-- scale 4 -->
						<xsl:if test="boolean(element_4)">
							<fo:block font-size="10px">&#160;</fo:block>
							<xsl:variable name="indicator_scale_img">images/<xsl:value-of select="element_4"/></xsl:variable>
							<fo:block>
					       		<fo:external-graphic content-height="5cm"  content-width="18cm">
							      	<xsl:attribute name="src">
							             <xsl:value-of select="$indicator_scale_img" />
							       	</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</xsl:if>
						<!-- scale 5 -->
						<xsl:if test="boolean(element_5)">
							<fo:block font-size="10px">&#160;</fo:block>
							<xsl:variable name="indicator_scale_img">images/<xsl:value-of select="element_5"/></xsl:variable>
							<fo:block>
					       		<fo:external-graphic content-height="5cm"  content-width="18cm">
							      	<xsl:attribute name="src">
							             <xsl:value-of select="$indicator_scale_img" />
							       	</xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</xsl:if>														       		
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