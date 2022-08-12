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
	                  	margin-top="0.5cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.25cm">
			      	<fo:region-body margin-top="0cm">
			      		<xsl:if test="element_6='false' and element_5='NO'">
				      		<xsl:attribute name="background-image">
	                			<xsl:text>report_images/preview_watermark.jpg</xsl:text>
	                		</xsl:attribute>
                		</xsl:if>
			      	</fo:region-body>
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
	            	<fo:block font-family="Kokila" font-size="15px">	  
	            		<fo:block font-weight="bold">
	            			<fo:table table-layout="fixed" width="100%">
	            				<fo:table-column column-number="1" column-width="70%" />
								<fo:table-column column-number="2" column-width="30%" />
	            				<fo:table-body>
		            				<fo:table-row>
		            					<fo:table-cell>
		            						<fo:block-container>
			            						<fo:block text-decoration="underline">तात्काळ</fo:block>
			            						<fo:block>
			            							स्मरणपत्र क्रमांक : <xsl:value-of select="element_4"></xsl:value-of>&#160;<xsl:if test="element_6='false' and element_5='NO'">(PREVIEW COPY)</xsl:if>
			            						</fo:block>
		            						</fo:block-container>
		            					</fo:table-cell>	            					
		            					<fo:table-cell>
		            						<fo:block-container>
			            						<fo:block>
			            							क्रमांक : _______&#160;/
													<xsl:choose>
														<xsl:when test="element_1[1]/element_1_4='lowerhouse'">ब-१</xsl:when>
														<xsl:when test="element_1[1]/element_1_4='upperhouse'">ई-१ कक्ष</xsl:when>
													</xsl:choose>
												</fo:block>						
												<fo:block>महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
												<fo:block>विधान भवन,</fo:block>
												<fo:block>मुंबई/नागपूर</fo:block>
												<!-- <fo:block>दिनांक : <xsl:value-of select="element_1[1]/element_1_14"/></fo:block> -->
												<fo:block>दिनांक : <xsl:value-of select="element_7"/></fo:block>
											</fo:block-container>
		            					</fo:table-cell>
		            				</fo:table-row>
	            				</fo:table-body>
	            			</fo:table>
	            		</fo:block>       		
						<fo:block text-align="left">
							<fo:block>प्रेषक :</fo:block>						
							<fo:block margin-left="1.5cm">प्रधान सचिव,</fo:block>
							<fo:block margin-left="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
						</fo:block>		
						
						<fo:block font-size="4px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block>प्रति :</fo:block>						
							<fo:block margin-left="1.5cm">उप सचिव,</fo:block>
							<fo:block margin-left="1.5cm">महाराष्ट्र शासन,</fo:block>
							<fo:block margin-left="1.5cm"><xsl:value-of select="element_1[1]/element_1_3"/>,</fo:block>							
							<fo:block margin-left="1.5cm">मंत्रालय, मुंबई - ४०० ०३२.</fo:block>	
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block text-align="center" font-weight="bold">
							<fo:inline>विषय</fo:inline>
							: आपल्या विभागाकडे पाठविलेल्या
							<xsl:choose>
								<xsl:when test="element_1[1]/element_1_2='questions_starred'">तारांकित</xsl:when>
								<xsl:when test="element_1[1]/element_1_2='questions_unstarred'">अतारांकित</xsl:when>
							</xsl:choose>
							स्वीकृत प्रश्नांची उत्तरे त्वरीत पाठविण्याबाबत.
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold">महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>
						
						<fo:block text-align="justify">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;उपरोक्त विषयाच्या अनुषंगाने निर्देशानुसार आपणांस कळविण्यात येते की, आपल्या विभागाकडे खालील
							<xsl:choose>
								<xsl:when test="element_1[1]/element_1_2='questions_starred'">तारांकित</xsl:when>
								<xsl:when test="element_1[1]/element_1_2='questions_unstarred'">अतारांकित</xsl:when>
							</xsl:choose>
							स्वीकृत प्रश्न पाठविण्यात आले आहेत.
								
							<fo:block font-size="6px">&#160;</fo:block>	
							
							<fo:block>
								<fo:table border="solid 0.2mm black" table-layout="fixed" width="100%">
									<fo:table-column column-number="1" column-width="1.2cm" />
									<fo:table-column column-number="2" column-width="3.2cm" />
									<fo:table-column column-number="3" column-width="6.0cm" />
									<fo:table-column column-number="4" column-width="2.6cm" />
			                        <fo:table-column column-number="5" column-width="2.6cm" />
			                        <fo:table-column column-number="6" column-width="2.6cm" />
			                        <fo:table-header>
			                        	<fo:table-row>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">अ.क्र.</fo:block>
			                        		</fo:table-cell>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">
			                        				<xsl:choose>
														<xsl:when test="element_1[1]/element_1_2='questions_starred'">तारांकित प्रश्न क्र.</xsl:when>
														<xsl:when test="element_1[1]/element_1_2='questions_unstarred'">प्रश्न क्रमांक,</xsl:when>
													</xsl:choose>
			                        			</fo:block>
			                        			<fo:block font-weight="bold">अधिवेशन</fo:block>
			                        		</fo:table-cell>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">विषय</fo:block>
			                        		</fo:table-cell>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">विभागास प्रश्न</fo:block>
			                        			<fo:block font-weight="bold">पाठविल्याचा दिनांक</fo:block>
			                        		</fo:table-cell>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">या सचिवालयास</fo:block>
			                        			<fo:block font-weight="bold">उत्तर पाठविण्याचा दिनांक</fo:block>
			                        		</fo:table-cell>
			                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
			                        			<fo:block font-weight="bold">पाठविलेल्या</fo:block>
			                        			<fo:block font-weight="bold">स्मरणपत्राचा दिनांक</fo:block>
			                        		</fo:table-cell>
			                        	</fo:table-row>
			                        </fo:table-header>
			                        <fo:table-body>
			                        	<xsl:for-each select="element_1">
			                        		<xsl:variable name="rowCount" select="position()"/>
			                        		<fo:table-row>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.2cm">
				                        			<fo:block><xsl:value-of select="../element_3[$rowCount]"/>.</fo:block>
				                        		</fo:table-cell>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.1cm">
				                        			<fo:block text-align="center" font-weight="bold"><xsl:value-of select="element_1_1"/></fo:block>
				                        			<fo:block text-align="center"><xsl:value-of select="element_1_10"/></fo:block>
				                        		</fo:table-cell>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.2cm">
				                        			<fo:block><xsl:value-of select="element_1_9"/></fo:block>
				                        		</fo:table-cell>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.4cm">
				                        			<fo:block><xsl:value-of select="element_1_7"/></fo:block>
				                        			<xsl:if test="../element_5='NO'">
				                        				<fo:block font-size="4px">&#160;</fo:block>
				                        				<fo:block><xsl:value-of select="element_1_13"/></fo:block>
				                        			</xsl:if>
				                        		</fo:table-cell>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.4cm">
				                        			<fo:block><xsl:value-of select="../element_2[$rowCount]"/></fo:block>				                        							                        			
				                        		</fo:table-cell>
				                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="left" margin-left="0.4cm">
				                        			<fo:block><xsl:apply-templates select="element_1_12"/></fo:block>
				                        			<!-- <fo:block><xsl:value-of select="element_1_12"/></fo:block> -->
				                        		</fo:table-cell>
			                        	</fo:table-row>
			                        	</xsl:for-each>			                        	
			                        </fo:table-body>
								</fo:table>								
							</fo:block>		
							
							<fo:block font-size="8px">&#160;</fo:block>	
							
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;महाराष्ट्र विधानपरिषद नियम क्रमांक ७० (२) अन्वये वरील
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='questions_starred'">तारांकित</xsl:when>
									<xsl:when test="element_1[1]/element_1_2='questions_unstarred'">अतारांकित</xsl:when>
								</xsl:choose>
								स्वीकृत प्रश्नांची उत्तरे ठरवून दिलेल्या
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='questions_unstarred'"><fo:inline font-weight="bold">"तीस दिवसाच्या आत"</fo:inline> या</xsl:when>
								</xsl:choose>
								कालावधीतच या सचिवालयास पाठविणे आवश्यक होते. तथापि आपल्या विभागाकडून उक्त प्रश्नांची उत्तरे अद्याप अप्राप्त आहेत.
							</fo:block>
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;अत: महाराष्ट्र विधानपरिषद नियम क्रमांक ७० (२) च्या तरतुदींच्या अधीन राहता प्रश्नांची उत्तरे पाठविण्यास झालेल्या विलंबाची कारणे मा.उप सभापती, महाराष्ट्र विधानपरिषद यांना अवगत करण्यात यावीत व प्रस्तुत
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='questions_starred'">तारांकित</xsl:when>
									<xsl:when test="element_1[1]/element_1_2='questions_unstarred'">अतारांकित</xsl:when>
								</xsl:choose>
								प्रश्नांची उत्तरे त्वरीत पाठविण्यात यावीत, ही विनंती.
							</fo:block>			
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>		
								
						<fo:block text-align="right">
							<fo:block margin-right="2.6cm">आपला/आपली,</fo:block>
							<fo:block font-size="14px">&#160;</fo:block>							
							<fo:block margin-right="2.7cm">कक्ष अधिकारी</fo:block>							
							<fo:block margin-right="1.4cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>
							<fo:block font-weight="bold">याची प्रत :</fo:block>
							<fo:block margin-left="1.5cm">१) सचिव, महाराष्ट्र शासन, संसदीय कार्य विभाग, मंत्रालय, मुंबई - ४०० ०३२.</fo:block>
							<fo:block margin-left="1.5cm">२) मा.उप सभापती, महाराष्ट्र विधानपरिषद यांचे सचिव, विधान भवन, मुंबई - ४०० ०३२.</fo:block>
						</fo:block>										
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>