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
					<fo:block text-align="center" font-family="Kokila">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-common">
					<fo:block  text-align="center" font-family="Kokila">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		            	
	            	<fo:block font-family="Kokila" font-size="15px">
	            		<xsl:choose>
	            			<xsl:when test="element_1">
	            				<fo:block text-align="center" font-weight="bold" text-decoration="underline"> नियम २८९ अन्वये सभागृहाचे सर्व कामकाज स्थगित करण्याची सूचना</fo:block>
	            				<fo:block text-align="right" font-weight="bold" text-decoration="underline"> दिनांक <xsl:value-of select="./element_1/element_1_3" /></fo:block>
	            				<fo:table>
	            					<fo:table-column column-width="2cm"/>
					                <fo:table-column column-width="2cm"/>
					                <fo:table-column />
	            					<fo:table-body>
	            						
		            					<fo:table-row>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block>&#160;</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block> 
		            								<fo:inline> <xsl:value-of select="./element_1/element_1_4"/> </fo:inline>
		            								<xsl:value-of select="./element_2" />
		            							</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block> 
		            								<fo:inline> वेळ: सकाळी <xsl:value-of select="./element_1/element_1_5"/> </fo:inline>
		            								<xsl:value-of select="./element_3"/> वाजता
		            							</fo:block>
		            						</fo:table-cell>
		            					</fo:table-row>
		            					<fo:table-row>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block>सभापती</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block>&#160;</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block text-align="justify">
		            								आज <xsl:value-of select="./element_1/element_1_2"/> वि.प.स.
		            							</fo:block>
		            							<fo:block text-align="justify">
		            								&#160;&#160;&#160;&#160;&#160;<xsl:apply-templates select="./element_1/element_1_1"/>
		            							</fo:block>
		            						</fo:table-cell>
		            					</fo:table-row>
		            					<fo:table-row>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block>&#160;</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block>&#160;</fo:block>
		            						</fo:table-cell>
		            						<fo:table-cell border="solid 0.2mm black" display-align="before">
		            							<fo:block text-align="justify">
		            								नियम स्थगित करण्यासंदर्भात मे.कौल आणि शकधर यांच्या  प्रॅक्टिस अॅण्ड प्रोसिजर ऑफ पार्लमेंट या ग्रंथात नमूद केल्याप्रमाणे सभागृहाचे सर्व कामकाज बाजूला ठेवण्यासाठी
		            								दिवसाच्या कामकाजाच्या क्रमात एखादा विशिष्ट असा प्रस्ताव सभागृहासमोर असणे आवश्यक आहे व तो विचारात घेण्यासाठी सर्व कामकाज स्थगित करण्यासंदर्भात प्रस्ताव देता येईल. तथापि,
		            								आजच्या दिवसाच्या कामकाजाच्या पत्रिकेवर समाविष्ट असलेल्या कोणत्याही बाबीसंबंधात सभागृहाचे सर्व कामकाज स्थगित करण्याबाबत प्रस्तुत सूचनेद्वारे मा.सदस्यांनी दिलेला प्रस्ताव
		            								आजच्या दिवसाच्या कामकाजाच्या पत्रिकेवर समाविष्ट नसल्याने सन्माननीय सदस्यांनी आज दिनांक <xsl:value-of select="./element_1/element_1_3" /> रोजी उपस्थित केलेल्या नियम २८९  अन्वये सूचनेस मी अनुमती नाकारीत आहे.
		            							</fo:block>
		               						</fo:table-cell>
		            					</fo:table-row>
	            					</fo:table-body>
	            				</fo:table>				
	            			</xsl:when>
	            			<xsl:otherwise>
	            				<fo:block text-align="center" font-size="15px" font-weight="bold">
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