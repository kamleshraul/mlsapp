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
    
    <xsl:template name="getNumber">
    	<xsl:param name="num"></xsl:param>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($num,'0','०')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'1','१')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'2','२')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'3','३')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'4','४')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'5','५')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'6','६')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'7','७')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'8','८')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'9','९')"></xsl:value-of>
    	</xsl:variable>
    	
    	<fo:block>
    		<xsl:value-of select="$finalNum"></xsl:value-of>
    	</fo:block>
    </xsl:template>
    
   <xsl:template match="root">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="21cm" page-width="29.7cm"
	                  	margin-top="2.3cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.25cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="21cm" page-width="29.7cm"
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
	            	<fo:block text-align="center" font-size="16px" font-weight="bold">
	            		विवरण क्रमांक २१
	            	</fo:block>
	            	<fo:block text-align="center" font-size="14px" font-weight="bold">
	            		महाराष्ट्र विधानपरिषद नियम १०१ अन्वये तातडीच्या व सार्वजनिक महत्त्वाच्या बाबीकडे मंत्र्यांचे लक्ष वेधण्यासाठी आलेल्या लक्षवेधी सूचना 
	            	</fo:block>	
	            	<fo:block>&#160;</fo:block>
	            	<fo:table width="100%" table-layout="fixed">
						<fo:table-column column-width="proportional-column-width(1)"/>
				        <fo:table-column column-width="60%"/>
				        <fo:table-column column-width="proportional-column-width(1)"/>
				        <fo:table-body>
				        	<fo:table-row>
				        		<fo:table-cell column-number="1">
									<fo:block>&#160;</fo:block>
								</fo:table-cell>
								<fo:table-cell column-number="2">
									<xsl:for-each select="./element_1" >   
						            	<xsl:choose>
						            		<xsl:when test="position()=1">
						            			<fo:table table-layout="fixed" width="100%">
				       								<fo:table-column column-width="80%"/>
				       								<fo:table-column column-width="10%"/>
				    								<fo:table-column column-width="10%"/>
				       								<fo:table-body>
				       									<fo:table-row>
				       										<fo:table-cell font-weight="bold" font-size="14px">
				       											<fo:block>(१)	प्राप्त झालेल्या एकूण लक्षवेधी सूचना  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell  font-size="14px">
				       											<fo:block> -  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell font-weight="bold" font-size="14px" text-align="center">
				       											<fo:block><xsl:value-of select="element_1_1"/></fo:block>
				       										</fo:table-cell>
				       									</fo:table-row>
				       									<fo:table-row>
				       										<fo:table-cell font-weight="bold" font-size="14px">
				       											<fo:block>(२)	स्वीकृत करण्यात आलेल्या एकूण लक्षवेधी सूचना  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell  font-size="14px">
				       											<fo:block> -  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell font-weight="bold" font-size="14px" text-align="center">
				       											<fo:block><xsl:value-of select="element_1_2"/></fo:block>
				       										</fo:table-cell>
				       									</fo:table-row>
				       									<fo:table-row>
				       										<fo:table-cell font-weight="bold" font-size="14px">
				       											<fo:block>(३)	सभागृहात चर्चा झालेल्या एकूण लक्षवेधी सूचना   </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell  font-size="14px">
				       											<fo:block> -  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell font-weight="bold" font-size="14px" text-align="center">
				       											<fo:block><xsl:value-of select="element_1_3"/></fo:block>
				       										</fo:table-cell>
				       									</fo:table-row>
				       									<fo:table-row>
				       										<fo:table-cell font-weight="bold" font-size="14px">
				       											<fo:block>(४)	सभागृहाच्या पटलावर ठेवण्यात आलेल्या एकूण स्वीकृत लक्षवेधी सूचनांचे जोडपत्र  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell  font-size="14px">
				       											<fo:block> -  </fo:block>
				       										</fo:table-cell>
				       										<fo:table-cell font-weight="bold" font-size="14px" text-align="center">
				       											<fo:block><xsl:value-of select="element_1_4"/></fo:block>
				       										</fo:table-cell>
				       									</fo:table-row>
				       								</fo:table-body>
					       						</fo:table>
						            		</xsl:when>
						            	</xsl:choose>
					            	</xsl:for-each>
								</fo:table-cell>
								<fo:table-cell column-number="3">
									<fo:block>&#160;</fo:block>
								</fo:table-cell>
							</fo:table-row>	 
						</fo:table-body>
					</fo:table>
		           	<fo:table>
	            		<fo:table-column column-width="10%"/>
    					<fo:table-column column-width="10%"/>
    					<fo:table-column column-width="60%"/>
    					<fo:table-column column-width="10%"/>
    					<fo:table-column column-width="10%"/>
	            		<fo:table-header>
	            			<fo:table-row >
	            				<fo:table-cell text-align="center" font-weight="bold" number-columns-spanned="5">
	            					<fo:block border-top-width="2pt" border-top-style="solid" border-top-color="black">
	            						&#160;
	            					</fo:block>
	            				</fo:table-cell>
	            			</fo:table-row>
	            			<fo:table-row>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>अ.क्र.</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>लक्षवेधी सूचना क्र. </fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>सदस्यांचे नांव व विषय</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px"> 
	            					<fo:block>ज्या तारखेस सूचना सभागृहात घेतली ती तारीख</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>शेरा</fo:block>
	            				</fo:table-cell>
	            			</fo:table-row>
	            			<fo:table-row>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>१.</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>२.</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>३.</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>४.</fo:block>
	            				</fo:table-cell>
	            				<fo:table-cell text-align="center" font-weight="bold" font-size="14px">
	            					<fo:block>५.</fo:block>
	            				</fo:table-cell>
	            			</fo:table-row>
	            			<fo:table-row>
	            				<fo:table-cell number-columns-spanned="5">
	            					<fo:block border-bottom-width="2pt" border-bottom-style="solid" border-top-color="black">&#160;</fo:block>
	            				</fo:table-cell>
	            			</fo:table-row>
	            		</fo:table-header>
	            		<fo:table-body>
	            			<xsl:for-each select="./element_1">
	            				 <fo:table-row>
		            				<fo:table-cell text-align="center" font-weight="bold">
		            					<fo:block>
			            						<xsl:call-template name="getNumber">
													<xsl:with-param name="num" select="position()" />
												</xsl:call-template>
			            					<!-- <xsl:value-of select="element_2_2_7"/> -->
			            				</fo:block>
		            					<!-- <fo:block><xsl:value-of select="position()"/></fo:block> -->
		            				</fo:table-cell>
		            				<fo:table-cell text-align="center" font-weight="bold">
		            					<fo:block><xsl:value-of select="element_1_10" /></fo:block>
		            				</fo:table-cell>
		            				<fo:table-cell >
		            					<fo:block font-weight="bold">
		            						<xsl:value-of select="element_1_6" />
		            					</fo:block>
		            					<fo:block text-align="justify">
		            						<xsl:value-of select="element_1_7" />
		            					</fo:block>
		            				</fo:table-cell>
		            				<fo:table-cell text-align="center" font-weight="bold"> 
		            					<fo:block>
		            						<xsl:value-of select="element_1_8" />
		            					</fo:block>
		            				</fo:table-cell>
		            				<fo:table-cell text-align="center" font-weight="bold">
		            					<fo:block>
		            						<xsl:value-of select="element_1_9" />
		            					</fo:block>
		            				</fo:table-cell>
		            			</fo:table-row>
		            			<fo:table-row>
		            				<fo:table-cell number-columns-spanned="5">
		            					<fo:block>&#160;</fo:block>
		            				</fo:table-cell>
			            		</fo:table-row>
	            			</xsl:for-each>
	            			<xsl:for-each select="./element_2">
	            				<xsl:variable name="outerLoopPosition" select="position()"/>
	            				<xsl:for-each select ="./element_2_2">
	            					<fo:table-row>
			            				<fo:table-cell  text-align="center" font-weight="bold">
			            					<fo:block>
			            						<xsl:call-template name="getNumber">
													<xsl:with-param name="num" select="element_2_2_7" />
												</xsl:call-template>
			            					<!-- <xsl:value-of select="element_2_2_7"/> -->
			            					</fo:block>
			            				</fo:table-cell>
			            				<fo:table-cell  text-align="center" font-weight="bold">
			            					<fo:block><xsl:value-of select="element_2_2_5" /></fo:block>
			            				</fo:table-cell>
			            				<fo:table-cell >
			            					<fo:block font-weight="bold">
			            						<xsl:value-of select="element_2_2_2" />
			            					</fo:block>
			            					<fo:block text-align="justify">
			            						<xsl:value-of select="element_2_2_4" />
			            					</fo:block>
			            				</fo:table-cell>
			            				<fo:table-cell number-columns-spanned="2" text-align="center"  font-weight="bold">
			            					<xsl:choose>
			            						<xsl:when test="$outerLoopPosition='2'">
			            							<fo:block>
					            						<xsl:value-of select="element_2_2_6" /> रोजी निवेदन सभागृहाच्या पटलावर ठेवणेकरिता
					            					</fo:block>
			            						</xsl:when>
			            						<xsl:otherwise>
			            							<fo:block>
					            						कित्ता.
					            					</fo:block>
			            						</xsl:otherwise>
			            					</xsl:choose>
			            					
			            				</fo:table-cell>
		            				</fo:table-row>	
		            				<fo:table-row>
			            				<fo:table-cell number-columns-spanned="5">
			            					<fo:block>&#160;</fo:block>
			            				</fo:table-cell>
			            			</fo:table-row>
	            				</xsl:for-each>
	            			</xsl:for-each>
	            		</fo:table-body>
	            	</fo:table> 
							
	            								          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>