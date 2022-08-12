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
	                  	margin-left="2cm" margin-right="2cm">
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
	        
	        <fo:page-sequence master-reference="others" id="DocumentBody">	        	
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
	            				<fo:block text-align="right" font-weight="bold" text-decoration="underline">
			            			म.वि.स./इ-१
			            		</fo:block>
			            		<fo:block text-align="left" font-weight="bold">
			            			सादर :-
			            		</fo:block>
			            		<fo:block text-align="justify">
			            			पृष्ठ क्रमांक (__________) वरील  <fo:inline font-weight="bold">म.वि.प. नियम २८९ </fo:inline> अन्वये आज  <xsl:value-of select="element_1[1]/element_1_2"/> रोजीच्या 
			            			कामकाज स्थगित करण्यासंबंधी दिलेली सूचना कृपया पहावी
			            		</fo:block>
			            		<fo:block font-size="12px">&#160;</fo:block>
	            				
            					<!-- <xsl:if test="position()!=1">
            						<fo:block break-before="page"/>
            					</xsl:if> -->	            						
			            		<fo:block>
			            			<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
			            				<fo:table-column column-number="1" column-width="1.5cm" />
				                        <fo:table-column column-number="2" column-width="3cm" />
				                        <fo:table-column column-number="3" column-width="6cm" />
				                        <fo:table-column column-number="4" column-width="4cm" />
				                        <fo:table-column column-number="5" column-width="2cm" />
				                        <fo:table-header>
				                        	<fo:table-row>
				                        		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                        	   	<fo:block text-align="center" font-weight="bold">
			                                        	अ.क्र.
				                                    </fo:block>
					                        	</fo:table-cell>
					                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                        	    <fo:block text-align="center" font-weight="bold">
			                                        	वेळ
				                                    </fo:block>
					                        	</fo:table-cell>
					                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                        	    <fo:block text-align="center" font-weight="bold">
			                                    		सदस्यांचे नाव
				                                    </fo:block>
					                        	</fo:table-cell>
					                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                        	    <fo:block text-align="center" font-weight="bold">
			                                    		विषय
				                                    </fo:block>
					                        	</fo:table-cell>
					                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                        	    <fo:block text-align="center" font-weight="bold">
			                                    		निर्णय
				                                    </fo:block>
					                        	</fo:table-cell>
				                        	</fo:table-row>
				                        </fo:table-header>
				                        <fo:table-body>
				                        	<xsl:for-each select="element_1">
					                        	<fo:table-row border="solid 0.1mm black">	                                	
				                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
				                                        <fo:block text-align="center">
				                                        	<xsl:value-of select="element_1_11" />
				                                        	<xsl:value-of select="element_1_13" />
				                                        </fo:block> 
				                                    </fo:table-cell>		                                    
				                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
				                                        <fo:block text-align="left" font-weight="bold">
				                                        	सकाळी <xsl:value-of select="element_1_12" /> <xsl:value-of select="element_1_14" />,
				                                        </fo:block> 
				                                    </fo:table-cell>		                                    
				                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
				                                        <fo:block text-align="justify" font-weight="bold">
				                                        	<xsl:value-of select="element_1_7" />
				                                        </fo:block>
				                                    </fo:table-cell>   
				                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
				                                        <fo:block text-align="justify" font-weight="bold">
				                                        	<xsl:value-of select="element_1_9" />
				                                        </fo:block>
				                                    </fo:table-cell>
				                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
				                                        <fo:block text-align="justify" font-weight="bold">
				                                        	<xsl:value-of select="element_1_8" />
				                                        </fo:block>
				                                    </fo:table-cell>                                 	
				                                </fo:table-row>
			                                </xsl:for-each>
				                        </fo:table-body>
			            			</fo:table>
			            			<fo:block page-break-before="always">
			            				&#160;&#160;&#160;&#160;&#160;वरील अतिशय गंभीर व तातडीच्या विषयाबाबत चर्चा करण्यासाठी सभागृहाचे सर्व कामकाज बाजूला ठेऊन/ प्रश्नोत्तराचा तास स्थगित करून म.वि.प. नियम २८९ च्या तरतुदी प्रमाणे स्थगित करून चर्चा करण्यात यावी आसे सूचनेत नमूद केले आहे. 
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block>
			            				&#160;&#160;&#160;&#160;&#160;उक्त सूचनेच्या संदर्भात नमूद करण्यात येते की, “महाराष्ट्र विधानपरिषद नियम २८९ नुसार कोणत्याही सदस्याला मा. उप सभापतींच्या संमतीने कोणताही नियम “सभागृहापुढील कोणत्याही विशिष्ट प्रस्तावास तो लागू करताना” स्थगित करण्यात यावा आस प्रस्ताव मांडता येईल आणि प्रस्ताव मंजूर झाला तर संबंधित नियम त्या वेळेपुरता स्थगित होईल.
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block>
			            				&#160;&#160;&#160;&#160;&#160;नियम स्थगित करण्यासंदर्भात मे.कौल आणि शकधर यांच्या प्रॅक्टिस अँड प्रोसिजर ऑफ पार्लमेंट या पुस्तकातील (सहावी सुधारणा आवृत्ती) पृष्ठ क्रमांक ९९३ वरील मजकूर कृपया पाहावा, यावरून असे दिसून येते की, प्रश्नोत्ताराचा तास स्थगित करण्यासाठी दिवसाच्या कामकाजाच्या क्रमात एखादा विशिष्ट आस प्रस्ताव साभगृहासमोर असणे आवश्यक आहे व तो विचारात घेण्यासाठी प्रश्नोत्तराचा तास स्थगित करण्यासंबंधात प्रस्ताव तेद येईल.
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block>
			            				&#160;&#160;&#160;&#160;&#160;कौल आणि शकधर यांच्या सदर पुस्तकातील पृष्ठ क्रमांक ९९३ वर असेही नमूद करण्यात आले आहे की, नियम स्थगित करण्यासंबंधात प्राप्त झालेल्या सुचनांमधील हरकतीच्या मुद्यांवर निर्णय देताना लोकसभेच्या माननीय अध्यक्षानी असे मत व्यक्त केले की, 
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block font-weight="bold">
			            				“There are three classes of business:-
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block font-weight="bold">
			            				&#160;&#160;&#160;&#160;&#160;Business before the House at Moment, Business before the House of the day (i.e. included in the list of business but not before the House at the Moment) and business pending in the House but not before the House (i.e. not included in the list of business) in the view of the above classification business pending in the House but not included in the list of business, is not business before the House for purpose of Rule” 
									</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block>
			            				&#160;&#160;&#160;&#160;&#160;या वरून असे दिसून येईल की, सूचना क्रमांक ______________ आजच्या दिवसाच्या कामकाज पत्रिकेवर कोणतीही बाब समाविष्ट नाही. प्रस्तुत प्रस्ताव अमान्य करण्यात यावा.
			            			</fo:block>
			            			<fo:block font-size="6px">&#160;</fo:block>
			            			<fo:block>
			            				&#160;&#160;&#160;&#160;&#160;मान्यतेसाठी सादर
			            			</fo:block>
			            		</fo:block>            				
	            			</xsl:when>
	            			<xsl:otherwise>
	            				<fo:block text-align="center" font-size="15px" font-weight="bold">
	            					सध्या एकही सूचना उपलब्ध नाही.
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