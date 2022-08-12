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
					<fo:block text-align="center" font-family="Kokila">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="Kokila">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		            	
	            	<fo:block font-family="Kokila" font-size="14px">		
	            	<fo:block text-align="center" font-weight="bold" text-decoration="underline">सत्र काळातील कामकाजाचा आढावा</fo:block>
	            	<fo:block>&#160;</fo:block>
	            	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;<fo:inline font-weight="bold">अध्यक्ष :</fo:inline>		 पावसाळी अधिवेशनाचा आजचा शेवटचा दिवस आहे आणि आजचे दिवसाचे कामकाज सुध्दा आता संपले आहे.मी या सत्र काळातील कामकाजाचा आढावा आता सदनासमोर ठेवतो.</fo:block>
	            		<fo:block>
	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;विधानसभेचे_________ पासून नागपूर येथे सुरु झालेले पावसाळी अधिवेशन 
आज शुक्रवार, दिनांक २० जुलै,२०१८ रोजी संस्थगित होत आहे.</fo:block>
	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;या सत्र काळातील बैठकांची एकूण संख्या १३ असून प्रत्यक्षात कामकाज ८६ तास १९ मिनिटे झालेले आहे.
मंत्री उपस्थित नसल्यामुळे १० मिनिटे व अन्य कारणामुळे ०८ तास ०९ मिनिटे असा कामकाज होऊ न शकलेला एकूण कालावधी
८ तास १९ मिनिटे आहे. सभागृहाचे रोजचे सरासरी कामकाज ६ तास ३९ मिनिटे झालेले आहे.</fo:block>
	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;प्राप्त झालेल्या तारांकित प्रश्नाची एकूण संख्या ९६६९ असून स्वीकृत झालेल्या तारांकित प्रश्नांची एकूण संख्या
८१३ आहे. सभागृहात एकूण ३७ प्रश्नांची तोंडी उत्तरे देण्यात आली. तसेच प्राप्त झालेल्या अल्पसूचना प्रश्नांची संख्या ०९
असून स्वीकृत झालेल्या अल्पसूचना प्रश्नांची संख्या ०१ आहे.</fo:block>
	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;प्राप्त झालेल्या लक्षवेधी सूचनांची एकूण संख्या २७६० असून स्वीकृत लक्षवेधी सूचनांची संख्या ११४ आहे.
तर चर्चा झालेल्या लक्षवेधी सूचनांची संख्या ४२ आहे.</fo:block>
	<fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;स्थगन प्रस्तावाच्या एकूण ११३ सूचना प्राप्त झाल्या असून सर्व सूचना अमान्य झाल्यामुळे एकही स्थगन
प्रस्तावाच्या सूचनेवर चर्चा झाली नाही.</fo:block>

               <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160; या अधिवेशनात विधानसभेने ऐकूण २३ विधेयक संमत केली व दोन्ही सभागृहांनी एकूण १५ विधेयक संमत केली.</fo:block>
     <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;अर्धा- तास चर्चेच्या ऐकूण २४६ सूचना प्राप्त झाल्या त्यापैकी १०८ स्वीकृत करण्यात आल्या आणि सभागृहात एकाही अर्धा- तास चर्चेच्या सूचनेवर चर्चा झाली नाही.</fo:block>
     <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;अशासकीय ठरावांच्या एकूण ४०८ सूचना प्राप्त झाल्या त्यापैकी २५४ सूचना मान्य झाल्या परंतु सभागृहात एकाही अशासकीय ठरावाच्या सूचनेवर चर्चा झाली नाही.</fo:block>
    <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160; शासकीय ठरावाची एकही सूचना प्राप्त झालेली नाही.</fo:block>
     <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;नियम २९३ अन्वये प्राप्त झालेल्या ०४ सूचना मान्य झाल्या त्यापैकी ०३ सूचनेवर चर्चा झाली.</fo:block>
     <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;अंतिम आठवडा प्रस्तावर चर्चा झाली.</fo:block>
     	<fo:block>&#160;</fo:block>
     		<fo:block>&#160;</fo:block>
     			<fo:block>&#160;</fo:block>
    <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160; संपूर्ण अधिवेशन कालावधी सभागृहात माननीय सदस्यांची एकूण सरासरी उपस्थिती ७६.४२ टक्के होती. त्यात जास्तीत जास्त उपस्थिती ८६.४३ टक्के तर कमीत कमी उपस्थिती १३.५६ टक्के एवढी होती.</fo:block>
    <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160; या अधिवेशनाचा समारोप करताना मी माननीय मुख्यमंत्री, माननीय संसदीय कार्यमंत्री, सर्व मंत्री महोदय, माननीय विरोधी पक्ष नेते, सर्व माननीय तालिका सभाध्यक्ष, सर्व सन्मानीय गट नेते, सर्व सन्मानीय सदस्य, प्रसिद्धी माध्यमांचे प्रतिनिधी,विधिमंडळाचे सर्व अधिकारी व कर्मचारी, सर्व सुरक्षा अधिकारी व कर्मचारी या सर्वांचे मी मनापासून आभार मानतो. मंत्रालयीन अधिकारी व कर्मचाऱ्यांचे देखील मी या ठिकाणी आवर्जून आभार मानतो.</fo:block>
     <fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                           --------------------------------------</fo:block>



</fo:block>
</fo:block>
	            				
									          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>