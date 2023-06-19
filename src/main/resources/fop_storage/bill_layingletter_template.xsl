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
	        
	        <fo:page-sequence master-reference="simple" id="DocumentBody">
	        	
	        	<!-- header -->
	        	<!-- <fo:static-content flow-name="rb-common">
					<fo:block text-align="center/left/start/right/end" font-family="{$font}">
					   	content for header for all pages
					</fo:block>
			    </fo:static-content>
		
				footer
		    	<fo:static-content flow-name="ra-common">
					<fo:block text-align="center/left/start/right/end" font-family="{$font}">
					   	content for footer for all pages
					</fo:block>
			    </fo:static-content> -->
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">
	            	<fo:block font-family="{$font}" font-size="13px">
	            	<xsl:choose>
       					<xsl:when test="element_9='विधानसभा'">
       					<fo:block text-align="right">
			       			महाराष्ट्र विधानमंडळ सचिवालय,<fo:block/>
			       			विधान भवन, मुंबई - ४०० ०३२.<fo:block/>
			       			दिनांक: <xsl:value-of select="element_7"/>.
			       		</fo:block>
			       			           			  
		           		<fo:block>&#160;</fo:block>
		           		<fo:block>&#160;</fo:block>
		           		
		           		<fo:block font-weight="bold">
		           			प्रेषक:<fo:block/>
		           			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सचिव-१ (कार्यभार),<fo:block/>
		           			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र <xsl:value-of select="element_9"/>.
		           		</fo:block>
		           		
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
		           		<fo:block font-weight="bold">
		           			प्रति:<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानसभेचे सर्व सदस्य.	           				
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block font-weight="bold">
	           				महोदय / महोदया,
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block text-align="justify">
	           				<fo:block>
       							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानपरिषदेने दिनांक <xsl:value-of select="element_4"/> रोजी संमत केलेल्या खालील विधेयकाची प्रत आपल्या माहितीसाठी पाठविण्यात येत आहे आणि आपणास असे कळविण्यात येत आहे की, महाराष्ट्र विधानसभा नियमातील नियम <xsl:value-of select="element_10"/> अन्वये सदरहू विधेयकाची प्रत विधानसभेच्या पटलावर ठेवण्यात आली आहे.
       						</fo:block>
       						
       						<fo:block>&#160;</fo:block>
       						
       						<fo:block font-weight="bold">
       							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"सन <xsl:value-of select="element_3"/> चे विधानपरिषद विधेयक क्रमांक <xsl:value-of select="element_1"/> - <xsl:value-of select="element_2"/>."
       						</fo:block>	           				
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block text-align="right">
	           				आपला,<fo:block/>
	           				<fo:block>&#160;</fo:block>
	           				<fo:block>&#160;</fo:block>	           				
	           				<fo:block font-weight="bold">(  <xsl:value-of select="element_12"/> )</fo:block>	
	           				 <xsl:value-of select="element_13"/>,<fo:block/>
	           				महाराष्ट्र विधानमंडळ सचिवालय.
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block font-weight="bold">
	           			याची प्रत :-
	           			</fo:block>
	           			<fo:block>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(१) मा.मुख्‍यमंत्री.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(२) मा.संसदीय कार्यमंत्री.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(३) प्रधान सचिव, महाराष्ट्र शासन, <xsl:value-of select="element_11"/> विभाग,<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;मंत्रालय, मुंबई - ४०० ०३२.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(४) सचिव (विधी विधान), महाराष्ट्र शांसन, मंत्रालय, मुंबई - ४०० ०३२.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(५) सचिव, महाराष्ट्र विधानपरिषद यांना माहितीसाठी अग्रेषित.
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>		           		
	           			
	           			<fo:block font-weight="bold">
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सदरहू विधेयक विधानसभेच्या पटलावर <xsl:value-of select="element_6"/>, दिनांक <xsl:value-of select="element_5"/> रोजी ठेवण्यात आले आहे.
	           			</fo:block>
       					</xsl:when>       					
       					
       					<xsl:when test="element_9='विधानपरिषद'">
       					<fo:block text-align="right">
			       			महाराष्ट्र विधानमंडळ सचिवालय,<fo:block/>
			       			विधान भवन, मुंबई - ४०० ०३२.<fo:block/>
			       			दिनांक: <xsl:value-of select="element_7"/>.
			       		</fo:block>
			       			           			  
		           		<fo:block>&#160;</fo:block>
		           		<fo:block>&#160;</fo:block>
		           		
		           		<fo:block font-weight="bold">
		           			प्रेषक:<fo:block/>
		           			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सचिव-१ (कार्यभार),<fo:block/>
		           			&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र <xsl:value-of select="element_9"/>.
		           		</fo:block>
		           		
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
		           		<fo:block font-weight="bold">
		           			प्रति:<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानपरिषदेचे सर्व सदस्य.	           				
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block font-weight="bold">
	           				महोदय / महोदया,
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block text-align="justify">
	           				<fo:block>
       							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानसभेने दिनांक <xsl:value-of select="element_4"/> रोजी संमत केलेल्या खालील विधेयकाची प्रत आपल्या माहितीसाठी पाठविण्यात येत आहे आणि आपणास असे कळविण्यात येत आहे की, महाराष्ट्र विधानपरिषद नियमातील नियम <xsl:value-of select="element_10"/> अन्वये सदरहू विधेयकाची प्रत विधानपरिषदेच्या पटलावर ठेवण्यात आली आहे.
       						</fo:block>
       						
       						<fo:block>&#160;</fo:block>
       						
       						<fo:block font-weight="bold">
       							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"सन <xsl:value-of select="element_3"/> चे विधानसभा विधेयक क्रमांक <xsl:value-of select="element_1"/> - <xsl:value-of select="element_2"/>."
       						</fo:block>	           				
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block text-align="right">
	           				आपला,<fo:block/>
	           				<fo:block>&#160;</fo:block>
	           				<fo:block>&#160;</fo:block>	           				
	           				<!-- <fo:block font-weight="bold">( गोपाळ र. दळवी )</fo:block>	
	           				अवर सचिव (समिती),<fo:block/> -->
	           				<fo:block font-weight="bold">(  <xsl:value-of select="element_12"/> )</fo:block>	
	           				 <xsl:value-of select="element_13"/>,<fo:block/>
	           				महाराष्ट्र विधानमंडळ सचिवालय.
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block font-weight="bold">
	           			याची प्रत :-
	           			</fo:block>
	           			<fo:block>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(१) मा.मुख्‍यमंत्री.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(२) मा.संसदीय कार्यमंत्री.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(३) प्रधान सचिव, महाराष्ट्र शासन, <xsl:value-of select="element_11"/> विभाग,<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;मंत्रालय, मुंबई - ४०० ०३२.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(४) सचिव (विधी विधान), महाराष्ट्र शांसन, मंत्रालय, मुंबई - ४०० ०३२.<fo:block/>
	           				&#160;&#160;&#160;&#160;&#160;&#160;(५) सचिव, महाराष्ट्र विधानसभा यांना माहितीसाठी अग्रेषित.
	           			</fo:block>
	           			
	           			<fo:block>&#160;</fo:block>
	           			
	           			<fo:block font-weight="bold">
	           				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सदरहू विधेयक विधानपरिषदेच्या पटलावर <xsl:value-of select="element_6"/>, दिनांक <xsl:value-of select="element_5"/> रोजी ठेवण्यात आले आहे.
	           			</fo:block>
       					</xsl:when>
       				</xsl:choose>	       		
	           		</fo:block>
	            </fo:flow>
	        </fo:page-sequence>
	    </fo:root>
    </xsl:template>      
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>