<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="QuestionData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>
    
    <!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
   <xsl:template match="QuestionData">

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
	            	<fo:block font-family="Mangal" font-size="11px">	            					
						<fo:block text-align="right">
							<fo:block>क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							महाराष्ट्र</fo:block>						
							<fo:block margin-right="1.05cm">विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="0.75cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="0.95cm">दिनांक - <xsl:value-of select="reportDate"/></fo:block>
						</fo:block>			
						
						<fo:block>&#160;</fo:block>
						
						<fo:block text-align="left">
							<fo:block>प्रेषक:</fo:block>						
							<fo:block margin-left="1cm">प्रधान सचिव,</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
						
						<fo:block>&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block>प्रति,</fo:block>						
							<fo:block font-weight="bold" margin-left="1cm"><xsl:value-of select="primaryMemberName"/>
							<xsl:if test="hasMoreMembers='yes'">
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'"> व इतर वि.स.स.</xsl:when>
									<xsl:when test="houseType='upperhouse'"> व इतर वि.प.स.</xsl:when>
								</xsl:choose>
							</xsl:if>
							</fo:block>							
						</fo:block>		
						
						<fo:block>&#160;</fo:block>
						
						<fo:block text-align="center" font-weight="bold">विषय: आपला <xsl:value-of select="deviceType"/> क्रमांक - <xsl:value-of select="number"/></fo:block>	
						
						<fo:block>&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block>&#160;</fo:block>	
						
						<fo:block margin-left="1cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;आपला उपरिनिर्दिष्ट प्रश्न खाली नमूद केलेल्या कारणांमुळे माननीय  
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">सभापतींनी</xsl:when>
							</xsl:choose>
							अस्वीकृत केला आहे, असे आपणांस कळविण्यास मला निदेश दिला आहे.							
						</fo:block>	
						<fo:block>&#160;</fo:block>				
						<fo:block text-align="right">
							<fo:block margin-right="2cm">आपला</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block margin-right="1.4cm">कक्ष अधिकारी,</fo:block>		
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>								
						</fo:block>
						-------------------------------------------------------------------------------------------------
						<fo:block font-weight="bold"><xsl:value-of select="rejectionReason"/></fo:block>	
						-------------------------------------------------------------------------------------------------
						<fo:block>&#160;</fo:block>
						<fo:block>
							<fo:inline>प्रश्न - </fo:inline>
							<fo:inline margin-left="1cm"><xsl:apply-templates select="questionText"/></fo:inline>
						</fo:block>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>