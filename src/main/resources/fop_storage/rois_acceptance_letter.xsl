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
    
   <xsl:template match="root">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="0.5cm" margin-bottom="1.5cm"
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
	            	<fo:block font-family="Mangal" font-size="10.5px">	            					
					<fo:block text-align="right" font-weight="bold">
							<fo:block margin-right="2.2cm">
								<xsl:value-of select="element_1/element_1_1"/>
							</fo:block>						
							<fo:block margin-right="2.78cm">दिनांक :</fo:block>
					</fo:block>			
						
						<fo:block font-weight="bold">प्रेषक :</fo:block>
						<fo:block margin-left="1cm" >	
						<fo:block>सचिव,</fo:block>
						<fo:block >
							<xsl:value-of select="element_1/element_1_2"/>
						</fo:block>
						</fo:block>
						<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block font-size="8px">&#160;</fo:block>	
									
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									<fo:block font-size="8px">&#160;</fo:block>	
								</xsl:when>
						</xsl:choose>					
										
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति :</fo:block>
						<fo:block margin-left="1cm" >माननीय	
							<fo:inline font-weight="bold">
								<xsl:value-of select="element_1/element_1_3"/>
							</fo:inline>
						</fo:block>
						
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold">महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1cm">							
							<xsl:value-of select="element_1/element_1_4"/> रोजी चर्चेकरिता घेण्यात येणाऱ्या अशासकीय  ठराव बॅलेटमध्ये आपले नांव <xsl:value-of select="element_1/element_1_7"></xsl:value-of> क्रमांकावर आहे.
						</fo:block>
							<fo:block margin-left="1cm">							
							आपण अशासकीय ठराव या दिवशी चर्चेकरिता मांडू इच्छिता ते कृपया खालील संमती पत्राद्वारे कळवावे.
						</fo:block>		
						<fo:block font-size="4px">&#160;</fo:block>		
						<fo:block text-align="right">
							<fo:block margin-right="2.0cm">आपला/आपली,</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>	
							<fo:block font-size="10.5px">&#160;</fo:block>	
							<fo:block margin-right="2cm">(<xsl:value-of select="element_1/element_1_5"/>)</fo:block>			
							<fo:block margin-right="2cm"><xsl:value-of select="element_1/element_1_6"/>,</fo:block>	
							<fo:block margin-right="0.60cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>						
							
						</fo:block>		
						<fo:block>----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	
							<fo:block text-align="center" font-weight="bold" font-size="14px">संमती पत्र</fo:block>
							<fo:block font-weight="bold">प्रेषक :</fo:block>
							<fo:block margin-left="1cm" >	
								<fo:block>सचिव,</fo:block>
								<fo:block >
									<xsl:value-of select="element_1/element_1_2"/>
								</fo:block>	
							</fo:block>	
							<fo:block font-weight="bold">महोदय,</fo:block>	
						
							<fo:block font-size="4px">&#160;</fo:block>	
							
							<fo:block margin-left="1cm">							
								<xsl:value-of select="element_1/element_1_4"/> रोजी अशासकीय कामकाजाकरिता मी माझे अशासकीय ठराव क्रमांक _________ मांडू इच्छितो.
							</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block text-align="right">
								<fo:block margin-right="2.0cm">आपला/आपली,</fo:block>
								<fo:block font-size="10.5px">&#160;</fo:block>	
								<fo:block font-size="10.5px">&#160;</fo:block>	
								<fo:block margin-right="2cm"><xsl:value-of select="substring-before(element_1/element_1_3,',')"/></fo:block>
								<fo:block margin-right="2.5cm"><xsl:value-of select="substring-after(element_1/element_1_3,',')"/></fo:block>			
							</fo:block>
					</fo:block>					          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>