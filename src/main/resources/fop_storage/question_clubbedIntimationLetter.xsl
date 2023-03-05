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
			       <xsl:variable name="endPartOfSubDepartment">
						<xsl:value-of select="substring(element_1/element_1_6,(string-length(element_1/element_1_6)-4))"/>
					</xsl:variable>
	            	<fo:block font-family="Kokila" font-size="15px">	            					
						<fo:block text-align="right">
							<fo:block margin-right="1.45cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="element_1/element_1_7='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="element_1/element_1_7='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.21cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="2.53cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>		
						
						<!-- <fo:block>&#160;</fo:block> -->
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रेषक:</fo:block>						
							<fo:block margin-left="1cm">प्रधान सचिव,</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति,</fo:block>	
							<fo:block margin-left="1cm">					
							<fo:block>सचिव</fo:block>
							<xsl:choose>
								<xsl:when test="element_1/element_1_5=element_1/element_1_6">
									<fo:block><xsl:value-of select="element_1/element_1_5"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block><xsl:value-of select="element_1/element_1_5"/> (<xsl:value-of select="element_1/element_1_6"/>)</fo:block>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block font-weight="normal">महाराष्ट्र शासन मंत्रालय, मुंबई - ४०० ०३२</fo:block>	
							</fo:block>
						</fo:block>
						
						<fo:block font-size="15px">&#160;</fo:block>
						
						<fo:block margin-left="1cm">
							<fo:inline font-weight="bold">विषय - </fo:inline><xsl:value-of select="element_1/element_1_2"/>
							<fo:block margin-left="1.3cm">
								<xsl:value-of select="element_1/element_1_3"/> 
								यांचा <xsl:value-of select="element_1/element_1_8"/> 
								क्रमांक <xsl:value-of select="element_1/element_1_1"/>
							</fo:block>
						</fo:block>
						<fo:block font-size="5px">&#160;</fo:block>
						<fo:block margin-left="1cm" font-weight="bold">
							संदर्भ - या सचिवालयाचे पत्र क्रमांक ____________, दिनांक ______________
						</fo:block>
						<fo:block font-size="15px">&#160;</fo:block>
						<fo:block font-weight="bold">महोदय,</fo:block>
						<fo:block margin-left="1cm">
							उपरोक्त प्रश्नात <xsl:value-of select="element_2"/> यांच्या नावानंतर पुढील सदस्यांची नावे जोडावीत अशी विनंती आहे.
						</fo:block>
						<fo:block font-size="15px">&#160;</fo:block>
						<fo:block font-weight="bold"><xsl:value-of select="element_4"/></fo:block>
						<fo:block font-size="15px">&#160;</fo:block>
						<fo:block>या प्रश्नावरील अनुपूरक प्रश्नांना उत्तरे देण्यास मदत व्हावी म्हणून या सदस्यांनी प्रश्नोक्त विषयावर दिलेल्या मूळ प्रश्नांच्या प्रती यासोबत पाठविल्या आहेत. तसेच मूळ प्रश्न क्रमांक <xsl:value-of select="element_1/element_1_1"/> आणि त्यास जोडण्यात आलेल्या इतर प्रश्नांचे मूळ प्रारूप देखील संदर्भाकरिता पाठविले आहेत.</fo:block>
						<fo:block font-size="15px">&#160;</fo:block>
						<fo:block text-align="right">
							<fo:block margin-right="2cm">आपला</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block margin-right="1.4cm">कक्ष अधिकारी,</fo:block>		
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>								
						</fo:block>
						<fo:block break-before="page">
							<xsl:for-each select="element_3/element_3_1">
								<fo:block font-weight="bold" text-align="center">
									<xsl:value-of select="element_3_1_3"/> क्रमांक <xsl:value-of select="element_3_1_1"/>
									&#160;&#160;(<xsl:value-of select="element_3_1_4"/>)
								</fo:block>
								<fo:block font-size="5px">&#160;</fo:block>
								<fo:block>&#160;&#160;&#160;<xsl:apply-templates select="element_3_1_2"/></fo:block>
								<xsl:if test="position()!=last()">
									<fo:block font-size="15px">&#160;</fo:block>
								</xsl:if>
							</xsl:for-each>
						</fo:block>
						<fo:block break-before="page">
							<fo:block font-weight="bold" text-align="center" font-size="18px">
								मूळ प्रश्न क्रमांक <xsl:value-of select="element_1/element_1_1"/> आणि त्यास जोडण्यात आलेल्या इतर प्रश्नांचे मूळ प्रारूप
							</fo:block>
							<fo:block font-size="15px">&#160;</fo:block>
							<fo:block font-weight="bold" text-align="center">
								मूळ <xsl:value-of select="element_1/element_1_8"/> क्रमांक <xsl:value-of select="element_1/element_1_1"/>
								&#160;&#160;(<xsl:value-of select="element_1/element_1_3"/>)
							</fo:block>
							<fo:block font-size="5px">&#160;</fo:block>
							<fo:block>&#160;&#160;&#160;<xsl:apply-templates select="element_1/element_1_9"/></fo:block>
							
							<fo:block font-size="15px">&#160;</fo:block>
							
							<xsl:for-each select="element_5/element_5_1">
								<fo:block font-weight="bold" text-align="center">
									<xsl:value-of select="element_5_1_3"/> क्रमांक <xsl:value-of select="element_5_1_1"/>
									&#160;&#160;(<xsl:value-of select="element_5_1_4"/>)
								</fo:block>
								<fo:block font-size="5px">&#160;</fo:block>
								<fo:block>&#160;&#160;&#160;<xsl:apply-templates select="element_5_1_2"/></fo:block>
								<xsl:if test="position()!=last()">
									<fo:block font-size="15px">&#160;</fo:block>
								</xsl:if>
							</xsl:for-each>
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