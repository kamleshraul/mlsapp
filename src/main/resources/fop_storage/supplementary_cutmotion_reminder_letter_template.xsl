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
	            		<fo:block>
	            			<fo:table table-layout="fixed" width="100%">
	            				<fo:table-column column-number="1" column-width="70%" />
								<fo:table-column column-number="2" column-width="30%" />
	            				<fo:table-body>
		            				<fo:table-row>
		            					<fo:table-cell>
		            						<fo:block-container>
			            						<!-- <fo:block font-weight="bold" text-decoration="underline">तात्काळ</fo:block> -->
			            						<fo:block font-weight="bold">स्मरणपत्र क्रमांक : <xsl:value-of select="element_4"></xsl:value-of></fo:block>
		            						</fo:block-container>
		            					</fo:table-cell>	            					
		            					<fo:table-cell>
		            						<fo:block-container>
			            						<fo:block>
			            							क्रमांक : ______&#160;/म.वि.स./ड-३
												</fo:block>						
												<fo:block>महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
												<fo:block>विधान भवन, मुंबई/नागपूर</fo:block>
												<fo:block>दिनांक - <xsl:value-of select="element_1[1]/element_1_14"/></fo:block>
											</fo:block-container>
		            					</fo:table-cell>
		            				</fo:table-row>
	            				</fo:table-body>
	            			</fo:table>
	            		</fo:block>
						
						<!-- <fo:block text-align="left" font-weight="bold">
							<fo:block>प्रेषक :</fo:block>						
							<fo:block margin-left="1.5cm">प्रधान सचिव</fo:block>
							<fo:block margin-left="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block> -->
						
						<fo:block font-size="4px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति : </fo:block>
							<fo:block margin-left="1.5cm" font-weight="bold">प्रधान सचिव</fo:block>
							<fo:block margin-left="1.5cm" font-weight="bold"><xsl:value-of select="element_1[1]/element_1_3"/>,</fo:block>
							<fo:block margin-left="1.5cm">महाराष्ट्र शासन,</fo:block>
							<fo:block margin-left="1.5cm">मंत्रालय, मुंबई - ४०० ०३२</fo:block>	
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold" text-align="center">
							<fo:inline font-weight="bold">विषय</fo:inline>
							- प्रलंबित
							<xsl:choose>
								<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'">अर्थसंकल्पीय</xsl:when>
								<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'">पूरक</xsl:when>
							</xsl:choose>
							कपात सूचनांची उत्तरे पाठविण्याबाबत...
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold">महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block text-align="justify">							
							<fo:block>				
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
								निदेशानुसार आपणांस कळविण्यात येते की, चौदाव्या विधानसभा कालावधीतील विविध अधिवेशनातील 
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'">अर्थसंकल्पीय</xsl:when>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'">पूरक</xsl:when>
								</xsl:choose>
								कपात सूचना आवश्यक त्या कार्यवाहीसाठी आपल्या विभागाकडे पाठविण्यात आल्या असून मा.अध्यक्ष, महाराष्ट्र विधानसभा यांनी सभागृहात दिलेल्या निदेशानुसार कपात सूचनेची उत्तरे 
								संबंधित सदस्यांना <fo:inline font-weight="bold">तीस दिवसांच्या आत</fo:inline> पाठविण्याबाबत कळविण्यात आले आहे. 
								त्यानुषंगाने सदर कपात सूचनांची उत्तरे तीस दिवसात मा.सदस्यांना पाठविणे अपेक्षित होते. 
								तथापि, अद्यापही 
								<fo:inline font-weight="bold">
									<xsl:value-of select="element_6"/>
									<xsl:choose>
										<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'"> अर्थसंकल्पीय</xsl:when>
										<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'"> पूरक</xsl:when>
									</xsl:choose>
								</fo:inline> 
								कपात सूचनांची उत्तरे मा.सदस्यांना पाठविण्यात आलेली नाहीत. 
								तसेच उत्तराची एक प्रत या सचिवालयाकडेही प्राप्त झालेली नाही. तसेच उत्तर पाठविण्यास मुदतवाढ मिळण्याबाबत विनंतीही करण्यात आलेली नाही. 
								<fo:inline font-weight="bold">(अद्यापही उत्तरे प्राप्त न झालेल्या प्रलंबित 
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'"> अर्थसंकल्पीय</xsl:when>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'"> पूरक</xsl:when>
								</xsl:choose>
								कपात सूचनांचे विवरणपत्र सोबत जोडले आहे.)</fo:inline>
							</fo:block>	
							<fo:block font-size="4px">&#160;</fo:block>	
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
								तरी, कृपया आपल्या विभागाकडून 
								<fo:inline font-weight="bold">
									<xsl:value-of select="element_6"/>
									<xsl:choose>
										<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'"> अर्थसंकल्पीय</xsl:when>
										<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'"> पूरक</xsl:when>
									</xsl:choose>
								</fo:inline>
								कपात सूचनांची उत्तरे विहित मुदतीत का पाठविण्यात आली नाहीत.
								अपवादात्मक परिस्थितीत काही विशिष्ट कारणामुळे विलंब होणार असल्यास मुदतवाढ मिळण्याबाबतची विनंती का करण्यात आली नाही. याची कारणे या सचिवालयास कळविण्यात यावीत.
							</fo:block>
							<fo:block font-size="4px">&#160;</fo:block>	
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
								तसेच उत्तर पाठविण्यास झालेल्या विलंबास कोण जबाबदार आहे त्यांच्यावर जबाबदारी निश्चित करुन केलेल्या कार्यवाहीसह प्रलंबित 
								<xsl:choose>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'">अर्थसंकल्पीय</xsl:when>
									<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'">पूरक</xsl:when>
								</xsl:choose>
								कपात सुचनांची उत्तरे मा.सदस्यांना तात्काळ पाठवून 
								उत्तराच्या प्रती या सचिवालयास पाठविण्यात याव्यात, अशी आपणांस विनंती आहे.
							</fo:block>										
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block text-align="right">
							<fo:block margin-right="3.2cm">आपला</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>	
							<fo:block margin-right="2.4cm" font-weight="bold">(विलास आठवले)</fo:block>
							<fo:block margin-right="2.9cm">उप सचिव</fo:block>
							<fo:block margin-right="1.4cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
						
						<!-- <fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>
							<fo:block font-weight="bold">याची प्रत :</fo:block>
							<fo:block margin-left="1.5cm">अति. सचिव, संसदीय कार्य विभाग,</fo:block>
							<fo:block margin-left="1.5cm">मंत्रालय, मुंबई ४०० ०३२. यांना माहितीसाठी.</fo:block>
						</fo:block>	-->									
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	        
	        <fo:page-sequence master-reference="others" id="DocumentBody2">    	
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
	            	<fo:block font-family="Mangal" font-size="10.5px">
	            		<fo:block font-size="14px" font-weight="bold" text-align="center">
	            			<xsl:value-of select="element_1[1]/element_1_3"></xsl:value-of>
	            		</fo:block>
	            		
	            		<fo:block font-size="6px">&#160;</fo:block>	
	            		
	            		<fo:block font-size="13px" font-weight="bold" text-align="center" text-decoration="underline">
	            			विवरणपत्र
	            		</fo:block>
	            	
	            		<fo:block font-size="4px">&#160;</fo:block>
							
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
													<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_budgetary'">अर्थसंकल्पीय कपात सूचना क्र.</xsl:when>
													<xsl:when test="element_1[1]/element_1_2='motions_cutmotion_supplementary'">पूरक कपात सूचना क्र.</xsl:when>
												</xsl:choose>
		                        			</fo:block>
		                        			<fo:block font-weight="bold">अधिवेशन</fo:block>
		                        		</fo:table-cell>
		                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
		                        			<fo:block font-weight="bold">विषय</fo:block>
		                        		</fo:table-cell>
		                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
		                        			<fo:block font-weight="bold">विभागास सूचना</fo:block>
		                        			<fo:block font-weight="bold">पाठविल्याचा दिनांक</fo:block>
		                        		</fo:table-cell>
		                        		<fo:table-cell border="solid 0.2mm black" display-align="before" text-align="center">
		                        			<fo:block font-weight="bold">विभागाकडून उत्तर</fo:block>
		                        			<fo:block font-weight="bold">येण्याचा दिनांक</fo:block>
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
	            	</fo:block>
	            </fo:flow>
	        </fo:page-sequence>
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>