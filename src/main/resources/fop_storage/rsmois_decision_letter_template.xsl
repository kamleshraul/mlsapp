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
	                  	margin-left="1.8cm" margin-right="1.8cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="1.8cm" margin-right="1.8cm">
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
	        
	        <fo:page-sequence master-reference="others" id="DocumentBody1">	        	
	        	<!-- header -->
	        	<fo:static-content flow-name="rb-common">
					<fo:block text-align="center" font-family="Mangal">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-common">
					<fo:block  text-align="center" font-family="Mangal">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		            	
	            	<fo:block font-family="Kokila" font-size="26px">
	            		<xsl:choose>
	            			<xsl:when test="element_1">
	            				<fo:block font-size="26px" font-weight="bold" margin-left="3.5cm" text-decoration="underline">
	            			  		<xsl:value-of select="element_1[1]/element_1_7"></xsl:value-of>
			            		</fo:block>
			            		<fo:block font-size="26px" font-weight="bold" margin-left="3.5cm" text-decoration="underline">
			            			<xsl:apply-templates select="element_1[1]/element_1_6"/>
			            		</fo:block>
			            		<fo:block font-size="8px">&#160;</fo:block> 
			            		<fo:block>
			            			<fo:inline font-size="32px" font-weight="bold" text-decoration="underline">मा. सभापती :-</fo:inline>
			            			<fo:inline font-size="20px" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline>
			            			<fo:inline font-size="32px" font-weight="bold" text-decoration="underline">सूचना</fo:inline>
			            		</fo:block>	
			            		
			            		<fo:block font-size="8px">&#160;</fo:block>
	            				<fo:block text-align="justify">
	            					&#160;&#160;&#160;&#160;आज दिनांक <xsl:value-of select="element_1[1]/element_1_11"/> रोजी 
	            					<xsl:value-of select="element_1[1]/element_1_8"/> , <xsl:value-of select="element_1[1]/element_1_4"/> यांनी सकाळी 
	            					<xsl:value-of select="element_1[1]/element_1_10"/>, <xsl:value-of select="element_1[1]/element_1_5"/>
	            					<fo:inline text-decoration="underline">" <xsl:value-of select="element_1[1]/element_1_3"/> "</fo:inline> या विषयाबाबत म.वि.प. नियम २८९ अन्वये सूचना दिली आहे.
	            				</fo:block>
	            				<fo:block font-size="14px">&#160;</fo:block>
			            		<fo:block font-size="22px" text-align="center" font-weight="bold">
									(मा. सभापती यांनी अनुमती दिल्यास संबंधित सदस्यांनी म्हणणे मांडल्यानंतर)	            			
			            		</fo:block>
			            		<fo:block font-size="14px">&#160;</fo:block>
			            		<fo:block>
			            			<fo:inline font-size="32px" font-weight="bold" text-decoration="underline">मा. सभापती :-</fo:inline>
			            			<fo:inline font-size="20px" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:inline>
			            			<fo:inline font-size="32px" font-weight="bold" text-decoration="underline">निर्णय</fo:inline>
			
			            		</fo:block>    
			            		<fo:block font-size="8px">&#160;</fo:block>
			            		<fo:block  text-align="justify">
			            			&#160;&#160;&#160;महाराष्ट्र विधानपरिषद नियमांतील एखादा नियम स्थगित करणे या संबंधाचा नियम २८९ हा आहे. कोणत्याही सदस्यास मा. सभापतींच्या अनुमतीने कोणताही नियम स्थगित
			            			करण्यासंदर्भात सूचना देता येईल.
			            		</fo:block>
			            		<fo:block font-size="8px">&#160;</fo:block>  
			            		<fo:block  text-align="justify">
			            			&#160;&#160;&#160;यावरून असे दिसून येते की, प्रश्नोत्तराचा तास किंवा इतर सगळे कामकाज स्थगित करण्यासाठी त्या दिवसाच्या कामकाजाच्या क्रमात असे काम किंवा एखादा विशिष्ट प्रस्ताव सभागृहासमोर असणे आवश्यक आहे.
			            		</fo:block>
			            		<fo:block font-size="8px">&#160;</fo:block>
			            		<fo:block  text-align="justify">
			            			&#160;&#160;&#160;<xsl:value-of select="element_1[1]/element_1_9"/>
			            		</fo:block>				
	            			</xsl:when>
	            			<xsl:otherwise>
	            				<fo:block text-align="center" font-size="14px" font-weight="bold">
	            					&#160; सूचना नाही आहे.
	            				</fo:block>
	            			</xsl:otherwise>
	            		</xsl:choose>            		
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>    
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>