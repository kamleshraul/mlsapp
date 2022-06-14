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
	            	<fo:block text-align="left" >
	            	&#160;&#160;&#160;<fo:inline font-weight="bold">सभापती :</fo:inline>पावसाळी अधिवेशनाचा आजचा शेवटचा दिवस आहे. आजच्या दिवसाचे कामकाज संपलेले आहे.
						मी या सत्रकाळातील कामकाजाचा आढावा आता सदनासमोर ठेवतो :-
	            				</fo:block>
	            				<fo:block>&#160;</fo:block>
	            	
	            						<fo:table>
										<fo:table-body>								
											<fo:table-row>
												<fo:table-cell>
													<fo:block>		
	            		<fo:table>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell  width="50px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell  width="250px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">एकूण बैठकींची संख्या
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">
																			
																					</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																	<fo:table-row>
																	<fo:table-cell  width="50px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell  width="250px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">प्रत्यक्षात झालेले कामकाज
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">
																			
																					</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
															
																	<fo:table-row>
																	<fo:table-cell  width="50px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell  width="250px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">मंत्री अनुपस्थितीमुळे वाया गेलेला वेळ
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">
																			
																					</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																	<fo:table-row>
																	<fo:table-cell  width="50px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell  width="250px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">अन्य कारणांमुळे वाया गेलेला वेळ
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">
																			
																					</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																	<fo:table-row>
																	<fo:table-cell  width="50px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">
																			</fo:block>
																	</fo:table-cell>
																	<fo:table-cell  width="250px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">रोजचे सरासरी कामकाज
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block font-weight="bold">
																			
																					</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
															</fo:table-body>
														</fo:table>
														</fo:block>
	            								</fo:table-cell>
											
											</fo:table-row>
											<fo:table-row>
											<fo:table-cell >
											<fo:block>&#160;</fo:block>
												<fo:block>		
	            		<fo:table>
															<fo:table-body>
																<fo:table-row>
																	<fo:table-cell  width="150px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">तारांकित प्रश्न
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="400px" text-align="left">
																		<fo:block>
																			एकूण प्राप्त प्रश्न :&#160;&#160;&#160;&#160;&#160;&#160;स्वीकृत प्रश्न:
																					</fo:block>
																					<fo:block>सभागृहात तोंडी उत्तरित झालेले तारांकित प्रश्न :</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																	<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">नियम ९३ च्या सूचना
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>
																			प्राप्त झालेल्या सूचना :&#160;&#160;&#160;&#160;&#160;&#160;स्वीकृत सूचना:
																					</fo:block>
																					<fo:block>सभागृहात निवेदने झालेल्या सूचनांची संख्या :</fo:block>
																					<fo:block>सभागृहाच्या पटलावर ठेवण्यात आलेली निवेदने:</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
															
																	<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">औचित्याचे मुद्दे
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block >
																			एकूण प्राप्त झालेले औचित्याचे मुद्दे :
																					</fo:block>
																						<fo:block >
																			मांडण्यात आलेले औचित्याचे मुद्दे :
																					</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																	<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">लक्षवेधी सूचना
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block >
																			प्राप्त झालेल्या सूचना :&#160;&#160;&#160;&#160;&#160;&#160;मान्य झालेल्या सूचना :
																					</fo:block>
																					<fo:block >चर्चा झालेल्या सूचना :</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																
																	<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">विशेष उल्लेख
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>
																			प्राप्त झालेल्या सूचना :
																					</fo:block>
																					<fo:block>
																			मांडण्यात आलेल्या सूचना :
																					</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">नियम ९७ अन्वये अल्पकालीन चर्चा
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>
																			एकूण प्राप्त सूचना :
																					</fo:block>
																					<fo:block>मान्य झालेल्या सूचना :</fo:block>
																					<fo:block>चर्चा झालेल्या सूचना :</fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">नियम ४६ अन्वये निवेदन
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>मंत्र्यांनी केलेली निवेदने :</fo:block>
																<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">नियम ४७ अन्वये निवेदन
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																			<fo:block>मंत्र्यांनी केलेली निवेदने :</fo:block>
																	<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">शासकीय विधेयक
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																			<fo:block>विधान परिषद विधेयक :पुर:स्थापित : </fo:block>
																		<fo:block>विधान परिषद विधेयक:संमत करण्यात आलेली विधेयके: </fo:block>
																		<fo:block>विधान सभा विधेयक:पारित केलेली विधेयके : </fo:block>
																		<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">अशासकीय विधेयक
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>प्राप्त झालेली सूचना :&#160;&#160;&#160;&#160;&#160;&#160;स्वीकृत सूचना : </fo:block>												
																					<fo:block>अस्वीकृत सूचना :&#160;&#160;&#160;&#160;&#160;&#160;विचारार्थ : </fo:block>
																					<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">नियम २६० अन्वये प्रस्ताव
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>एकूण प्राप्त सूचनांची संख्या:</fo:block>
																		<fo:block>चर्चा झालेल्या सूचना:</fo:block>
																			<fo:block>&#160;</fo:block>
																					
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">अशासकीय ठराव
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>एकूण प्राप्त सूचनांची संख्या :</fo:block>
																		<fo:block>स्वीकृत सूचनांची संख्या :</fo:block>
																		<fo:block>चर्चा झालेली सूचनांची संख्या :</fo:block>
																		<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																		<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">शासकीय ठराव
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>सूचनांची संख्या :</fo:block>
																		<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
																<fo:table-row>
																	<fo:table-cell  width="200px" text-align="justify">
																		<fo:block text-align="left" font-weight="bold">अर्धा तास चर्चा
																			</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="20px" text-align="justify">
																		<fo:block font-weight="bold">:
																			
																				</fo:block>
																	</fo:table-cell>	
																		<fo:table-cell width="200px" text-align="justify">
																		<fo:block>प्राप्त सूचनांची संख्या :</fo:block>
																		<fo:block>मान्य झाल्या :</fo:block>
																		<fo:block>चर्चा झाली :</fo:block>
																		<fo:block>&#160;</fo:block>
																	</fo:table-cell>										
																</fo:table-row>
															</fo:table-body>
														</fo:table>
														</fo:block>
											</fo:table-cell>
											</fo:table-row>
										</fo:table-body>
									</fo:table>
														
					<fo:block font-weight="bold">अंतिम आठवडा प्रस्तावावर चर्चा झाली.</fo:block>
					</fo:block>			
									          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>