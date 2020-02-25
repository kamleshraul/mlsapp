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
	            	
	            			<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell  width="50px" text-align="right">
													<fo:block>
														
													</fo:block>
												</fo:table-cell>
												<fo:table-cell  width="450px" text-align="left">
													<xsl:choose>
	            		
	            			<xsl:when test="./element_2/element_2_2='lowerhouse'">
	            				<fo:block text-align="right" font-weight="bold" text-decoration="underline">
	            				म.वि.स./फ
	            				</fo:block>
	            				<fo:block text-align="left" >
	            				&#160;&#160;&#160;<fo:inline text-decoration="underline">सादर :</fo:inline>
	            				</fo:block>
	            				
	            				 <xsl:if test="./element_2/element_2_14='motions_discussionmotion_lastweek'">
										  <fo:block text-align="left">
	            				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सर्वश्री<xsl:value-of select="./element_2/element_2_13"></xsl:value-of> व इतर वि.स.स. यांनी म.वि.स. नियम २९२ अन्वये दिलेली पृष्ठ क्र. १ वरील प्रस्तावाची सूचना कृपया पहावी.  
	            			
	            				</fo:block>	
								</xsl:if>
	            					 <xsl:if test="./element_2/element_2_14='motions_discussionmotion_publicimportance'">
	            					          					   <fo:block text-align="left">
	            				&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सर्वश्री<xsl:value-of select="./element_2/element_2_13"></xsl:value-of> व इतर वि.स.स. यांनी म.वि.स. नियम २९३ अन्वये दिलेली पृष्ठ क्र. १ वरील प्रस्तावाची सूचना कृपया पहावी.  
	            			
	            				</fo:block>
	            					</xsl:if>
	            			
	            			
			            	  
			            		<fo:block>&#160;</fo:block>
										
								<fo:block>
									<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell  width="100px" text-align="right">
													<fo:block>
														प्रस्तावाचा विषय :-
													</fo:block>
												</fo:table-cell>
												<fo:table-cell  width="350px" text-align="left">
													<fo:block font-weight="bold" text-align="justify">
														 <xsl:value-of select="./element_2/element_2_6"></xsl:value-of>
													</fo:block>
												</fo:table-cell>
											
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
								<fo:block>&#160;</fo:block>    
			            		
			            		
			            					
	           <xsl:if test="./element_2/element_2_14='motions_discussionmotion_lastweek'">
	            			
	            					  <fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानसभा "बैठकीची दिनदर्शिका" यामध्ये दर्शविल्याप्रमाणे <xsl:value-of select="./element_2/element_2_10"></xsl:value-of> रोजी <fo:inline font-weight="bold">"
										<xsl:if test="./element_2/element_2_15='ruling_party'">सत्तारूढ</xsl:if>
										<xsl:if test="./element_2/element_2_15='opposition_party'">विरोधी</xsl:if>
										<xsl:if test="./element_2/element_2_15='independent'">अपक्ष</xsl:if>
	            					  पक्षामार्फत अंतिम आठवडा प्रस्ताव"</fo:inline> चर्चेस घेण्यात येणार आहे. त्यानुसार
										<xsl:if test="./element_2/element_2_15='ruling_party'">सत्तारूढ</xsl:if>
										<xsl:if test="./element_2/element_2_15='opposition_party'">विरोधी</xsl:if>
										<xsl:if test="./element_2/element_2_15='independent'">अपक्ष</xsl:if>
									 पक्षाकडून अंतिम आठवडा प्रस्तावाची सूचना प्राप्त झाली आहे.</fo:block>
	            					</xsl:if>
	            					 <xsl:if test="./element_2/element_2_14='motions_discussionmotion_publicimportance'">
		            					<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानसभा "बैठकीची दिनदर्शिका" यामध्ये दर्शविल्याप्रमाणे <xsl:value-of select="./element_2/element_2_10"></xsl:value-of> रोजी <fo:inline font-weight="bold">"
		            						 <xsl:if test="./element_2/element_2_15='ruling_party'">सत्तारूढ</xsl:if>
										<xsl:if test="./element_2/element_2_15='opposition_party'">विरोधी</xsl:if>
										<xsl:if test="./element_2/element_2_15='independent'">अपक्ष</xsl:if>
										 पक्षाचा प्रस्ताव"</fo:inline> घेण्यात येणार आहे. त्यानुसार
									 <xsl:if test="./element_2/element_2_15='ruling_party'">सत्तारूढ</xsl:if>
										<xsl:if test="./element_2/element_2_15='opposition_party'">विरोधी</xsl:if>
										<xsl:if test="./element_2/element_2_15='independent'">अपक्ष</xsl:if> पक्षातर्फे प्रस्तावाची सूचना प्राप्त झाली आहे.</fo:block> 
	            					</xsl:if>
	            				
			            		<fo:block>&#160;</fo:block>
			            						
	            		
	            					 <xsl:if test="./element_2/element_2_14='motions_discussionmotion_lastweek'">
	            					  <fo:block font-weight="bold">
									प्रस्तावासंदर्भात म.वि.स. नियम २३ मध्ये खालीलप्रमाणे तरतुदी आहेत.
								</fo:block>
	            					</xsl:if>
	            					 <xsl:if test="./element_2/element_2_14='motions_discussionmotion_publicimportance'">
		            					 <fo:block font-weight="bold">
									म.वि.स. नियम २३ च्या तरतुदी खालीलप्रमाणे आहेत.
								</fo:block>
	            					</xsl:if>
								
								
									<fo:block>
									<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell  width="50px" text-align="center">
														<fo:block>२३.</fo:block>
												</fo:table-cell>
												<fo:table-cell  width="400px" text-align="left">
												<fo:block>(१) ज्याबाबतीत विधानसभेच्या निर्णयाची आवश्यकता असते अशा बाबींचा निर्णय,अध्यक्ष एखादया सदस्याने मांडलेला प्रस्ताव मतास टाकून करतील.</fo:block>
											<fo:block>
									(२) पुढील शर्तींची पूर्तता करीत नसेल असा कोणताही प्रस्ताव स्वीकार्य असणार नाही.-								
								</fo:block>
								<fo:block>
									(अ) तो स्पष्टपणे व नेमकेपणाने व्यक्त करण्यात येईल व त्यात एक निश्चित मुद्दा उपस्थित करण्यात येईल.							
								</fo:block>
									<fo:block>
									(ब) त्यात युक्तीवाद,अनुमाने,वाक्रोतिपूर्ण शब्दप्रयोग किंवा मानहानीकारक विधाने समाविष्ट असणार नाहीत.							
								</fo:block>
								<fo:block>(क)	पदीय किंवा सार्वजनिक नात्याने असेल त्या व्यतिरिक्त इतर बाबतीत कोणत्याही व्यक्तीच्या आचरणाचा किंवा वर्तणुकीचा त्यात उल्लेख असणार नाही, आणि</fo:block> 
								<fo:block>(ड)    तो नियम ३४, पोट-नियम (२) खंड (१),  (४)  व  (५) यात निर्दिष्ट केल्याप्रमाणे असलेल्या कोणत्याही बाबीसंबंधी असणार नाही,</fo:block>
												</fo:table-cell>
											
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
							
	   
	<fo:block>&#160;</fo:block>								
<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;म.वि.स  नियम २३ (२)  (अ) मधील तरतुदी विचारात घेता या प्रस्तावाच्या सूचनेतील विषय मुख्यत्वे <fo:inline font-weight="bold"><xsl:value-of select="./element_2/element_2_11"></xsl:value-of></fo:inline> या विभागांचा संबंध येत असून प्रस्तावातील विषय सार्वजनिक महत्वाचा असल्याने मान्य करण्यास हरकत नसावी.</fo:block>
<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;सबब, प्रस्तावाची सूचना पृष्ठ क्रमांक १ वरील प्रारुपात मान्य करण्यात यावी.</fo:block>
<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;मान्य झालेल्या, प्रस्तावाची बाब <xsl:value-of select="./element_2/element_2_10"></xsl:value-of> रोजीच्या दिवसाच्या कामकाजाच्या क्रमात दाखविण्यात यावी व सदर प्रस्तावास प्रथेप्रमाणे २ तास ३० मिनिटे वेळ देण्यात यावा.</fo:block>
<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;प्रस्तावावरील चर्चेत सहभागी होणाऱ्या सदस्यांची नावे कळविण्याबाबत सत्ताधारी पक्ष्याच्या व विरोधी पक्ष्याचा प्रतोदांना पत्राद्वारे कळविण्यात यावे </fo:block>
<fo:block>&#160;&#160;&#160;&#160;&#160;&#160;&#160;मान्यतेसाठी सादर.</fo:block>
<fo:block>&#160;</fo:block>    
<fo:block>&#160;</fo:block>
<fo:block font-weight="bold">कक्ष अधिकारी (श्री. परब) :</fo:block>
<fo:block>&#160;</fo:block>    
<fo:block>&#160;</fo:block>
<fo:block font-weight="bold">अवर सचिव :</fo:block>
<fo:block>&#160;</fo:block>    
<fo:block>&#160;</fo:block>
<fo:block font-weight="bold" >सचिव (कार्यभार) : </fo:block>
<fo:block>&#160;</fo:block>    
<fo:block>&#160;</fo:block>
<fo:block font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;मा. अध्यक्ष :</fo:block>
								
						
							
							
	            			</xsl:when>
	            			<xsl:when test="./elelment_2/element_2_5='lowerhouse'">
	            				<fo:block>
	            					&#160;
	            				</fo:block>
	            			</xsl:when>
	            		</xsl:choose>
												</fo:table-cell>
											
											</fo:table-row>
										</fo:table-body>
									</fo:table>
	            	
	            		
					
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>