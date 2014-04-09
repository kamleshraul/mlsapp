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
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="{$font}">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">	
	            	<fo:block font-family="{$font}" font-size="16px">	            					
						<fo:block text-align="right">
							<fo:block>क्रमांक - _____&#160;/&#160;ई-१ महाराष्ट्र</fo:block>						
							<fo:block margin-right="1.05cm">विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="0.75cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="0.95cm">दिनांक - <xsl:value-of select="reportDate"/></fo:block>
						</fo:block>			
						
						<fo:block>&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block>प्रति,</fo:block>						
							<fo:block font-weight="bold">सचिव</fo:block>
							<fo:block font-weight="bold"><xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>)</fo:block>
							<fo:block>महाराष्ट्र शासन मंत्रालय, मुंबई - 400 032</fo:block>							
						</fo:block>		
						
						<fo:block>&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block>&#160;</fo:block>	
						
						<fo:block margin-left="1.5cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;खालील उदधृत केलेला <xsl:value-of select="houseTypeName"/> अतारांकित प्रश्न माननीय
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">सभापतींनी</xsl:when>
							</xsl:choose>
							स्वीकृत केला आहे. सदरहू प्रश्नाचे उत्तर महाराष्‍ट्र विधानमंडळ नियमांतील नियम ७०(२) अन्वये तीस दिवसांच्‍या आत म्‍हणजे दिनांक __________ पावेतो या सचिवालयास पाठविण्‍यात यावे – 
						</fo:block>	
						<fo:block>&#160;</fo:block>				
						<fo:block>
							<fo:block font-weight="bold">प्रत माहितीसाठी सादर अग्रेषित -- 
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							<fo:inline font-weight="normal">आपला</fo:inline></fo:block>
							<fo:block margin-left="1.5cm">
								<fo:block>(१) माननीय मुख्यमंत्री</fo:block>
								<fo:block>(२) माननीय <fo:inline font-weight="bold"><xsl:value-of select="subDepartment"/> मंत्री</fo:inline></fo:block>
								<fo:block>(३) सर्व संबंधित सदस्यांना</fo:block>
							</fo:block>
						</fo:block>	
						<fo:block>&#160;</fo:block>				
						<fo:block text-align="right">
							<fo:block margin-right="2cm">प्रधान सचिव</fo:block>							
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block margin-right="1.5cm">महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									<fo:block margin-right="1.35cm">महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
							</xsl:choose>
						</fo:block>
						-------------------------------------------------------------------------------------------------
						<fo:block>&#160;</fo:block>
						<fo:block text-align="center" font-weight="bold"><xsl:value-of select="subject"/></fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block margin-left="2cm" font-weight="bold">
							अतारांकित प्रश्‍न क्रमांक <xsl:value-of select="number"/>	
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;		
							<fo:inline><xsl:value-of select="memberNames"/></fo:inline>			
						</fo:block>
						<fo:block><xsl:apply-templates select="questionText"/></fo:block>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>