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

	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	        <fo:layout-master-set>
				<fo:simple-page-master master-name="firstPage"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="2cm"
                  margin-bottom="2cm"
                  margin-left="2.5cm"
                  margin-right="2.5cm">
			      <fo:region-body margin-top="0cm"/>        
			      <!-- <fo:region-before region-name="page-number" extent="2cm"/> -->
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
		    
          
	            <fo:simple-page-master master-name="otherPages"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1.5cm"
                  margin-bottom="2cm"
                  margin-left="2.5cm"
                  margin-right="2.5cm">
			      <fo:region-body margin-top="1cm"/>        
			      <fo:region-before region-name="page-number" extent="2cm"/>
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
			    
			    <!-- ============================================
			    Now we define how we use the page layouts.  One
			    is for the first page, one is for the even-
			    numbered pages, and one is for odd-numbered pages.
			    =============================================== -->

		        <fo:page-sequence-master master-name="standard">
		          <fo:repeatable-page-master-alternatives>
		            <fo:conditional-page-master-reference 
		              master-reference="firstPage" 
		              page-position="first"/>
		            <fo:conditional-page-master-reference 
		              master-reference="otherPages" 
		              odd-or-even="even"/>
		            <fo:conditional-page-master-reference 
		              master-reference="otherPages" 
		              odd-or-even="odd"/>
		          </fo:repeatable-page-master-alternatives>
		        </fo:page-sequence-master>
		    
		        
	        </fo:layout-master-set>
	        
	        <xsl:variable name="pageSequenceVariable">
				<xsl:choose>
					<xsl:when test="$formatOut='application/pdf'">
						<xsl:value-of select="'standard'"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'otherPages'"></xsl:value-of>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:variable>
			
	        <fo:page-sequence master-reference="otherPages" id="DocumentBody">
		        <fo:static-content flow-name="page-number">	        	
		        	<fo:block font-family="Kokila" font-size="15pt" text-align="left">
		        		<xsl:choose>
		        			<xsl:when test="element_3='विधानसभा'">वि.स. </xsl:when>
		        			<xsl:when test="element_3='विधानपरिषद'">वि.प. </xsl:when>
		        		</xsl:choose>
		        		<xsl:value-of select="element_9"/> (<fo:page-number/>)		        				        		
		        	</fo:block>
		        </fo:static-content>    
	
	            <fo:flow flow-name="xsl-region-body">	
	            	<fo:block font-family="Kokila" font-size="15pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
					    <xsl:choose>
					    	<xsl:when test="element_3='विधानसभा'">
					    		<fo:block font-size="20" font-weight="bold" text-align="center">
									महाराष्ट्र <xsl:value-of select="element_3"/>												
								</fo:block>
								<fo:block font-size="2px">&#160;</fo:block>
								<fo:block font-size="15pt" font-weight="bold" text-align="center">
									<xsl:choose>
										<xsl:when test="element_4 = 1">
											पहिले
										</xsl:when>
										<xsl:when test="element_4 = 2">
											दुसरे
										</xsl:when>
										<xsl:when test="element_4 = 3">
											तिसरे
										</xsl:when>
										<xsl:when test="element_4 = 4">
											चौथे
										</xsl:when>
										<xsl:when test="element_4 = 5">
											पाचवे
										</xsl:when>
										<xsl:when test="element_4 = 6">
											सहावे
										</xsl:when>
										<xsl:when test="element_4 = 7">
											सातवे
										</xsl:when>
									</xsl:choose>
									अधिवेशन, <xsl:value-of select="element_6"/>																								
								</fo:block>		
								<!-- <fo:block font-size="7px">&#160;</fo:block> -->													
								<fo:block text-align="center" font-size="9pt">
									_____________
								</fo:block>						
								<fo:block font-size="5px">&#160;</fo:block>	                	
			                	<fo:block text-align="center" font-size="15pt" font-weight="bold">
			                		अतारांकित प्रश्नोत्तरांची यादी
			                	</fo:block>	 
			                	<fo:block font-size="4.5px">&#160;</fo:block>               		                	           	
			                	<fo:block text-align="center" font-size="12pt" font-weight="bold">
			                		<fo:block font-size="15pt">प्रश्नांची एकूण संख्या - <xsl:value-of select="element_2"/></fo:block>
			                	</fo:block>
			                	<!-- <fo:block font-size="7px">&#160;</fo:block> -->
			                	<fo:block text-align="center" font-size="9pt">
			                		_____________
								</fo:block>
			                	<fo:block font-size="6pt">&#160;</fo:block>
					    	</xsl:when>
					    	
					    	<xsl:when test="element_3='विधानपरिषद'">
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="24" font-weight="bold" text-align="center">
									महाराष्ट्र <xsl:value-of select="element_3"/>												
								</fo:block>
								<fo:block font-size="2px">&#160;</fo:block>
								<fo:block font-size="15pt" font-weight="bold" text-align="center">
									<xsl:choose>
										<xsl:when test="element_4 = 1">
											पहिले
										</xsl:when>
										<xsl:when test="element_4 = 2">
											दुसरे
										</xsl:when>
										<xsl:when test="element_4 = 3">
											तिसरे
										</xsl:when>
										<xsl:when test="element_4 = 4">
											चौथे
										</xsl:when>
										<xsl:when test="element_4 = 5">
											पाचवे
										</xsl:when>
										<xsl:when test="element_4 = 6">
											सहावे
										</xsl:when>
										<xsl:when test="element_4 = 7">
											सातवे
										</xsl:when>
									</xsl:choose>
									<xsl:if test="element_3='विधानपरिषद'">(<xsl:value-of select="element_5"/>)</xsl:if>
									अधिवेशन, <xsl:value-of select="element_6"/>																								
								</fo:block>		
								<!-- <fo:block font-size="5px">&#160;</fo:block> -->													
								<fo:block text-align="center" font-size="9pt" font-weight="bold">
									_____________
								</fo:block>						
								<fo:block font-size="7px">&#160;</fo:block>	                	
			                	<fo:block text-align="center" font-size="15pt" font-weight="bold">
			                		अतारांकित प्रश्नोत्तरांची यादी
			                	</fo:block>	   
			                	<fo:block font-size="4.5px">&#160;</fo:block>             		                	           	
			                	<fo:block text-align="center" font-size="7pt" font-weight="bold">
			                		<fo:block font-size="15pt">प्रश्नांची एकूण संख्या - <xsl:value-of select="element_2"/></fo:block>
			                	</fo:block>
			                	<fo:block font-size="3px">&#160;</fo:block>
			                	<fo:block text-align="center" font-size="9pt" font-weight="bold">
									_____________
								</fo:block>
			                	<fo:block font-size="10pt">&#160;</fo:block>
					    	</xsl:when>
					    </xsl:choose>
					    
	                	<xsl:for-each select="./element_1" >
	                		<fo:block text-align="center" font-size="15pt" font-weight="bold">
	                			<xsl:value-of select="element_1_6"/>
	                		</fo:block>
	                		<fo:block font-size="5pt">&#160;</fo:block>
	                		<fo:block font-weight="bold">	                			
                				(<xsl:value-of select="element_1_1"/>)                				
                				&#160;<xsl:value-of select="element_1_4"/> (<xsl:value-of select="element_1_13"/>).
                				&#160;&#160;<xsl:value-of select="element_1_5"/>&#160;:&#160;
                				<xsl:if test="element_1_12!=''">
									<xsl:value-of select="element_1_12"/>: &#160;
								</xsl:if>
                				<fo:inline font-weight="normal">सन्माननीय </fo:inline>
                				<xsl:choose>
									<xsl:when test="element_1_11='मुख्‍यमंत्री' or element_1_11='उप मुख्‍यमंत्री'">
										<xsl:value-of select="element_1_11"/><fo:inline font-weight="normal"> पुढील गोष्टींचा खुलासा करतील काय :-</fo:inline>
									</xsl:when>
									<xsl:when test="element_1_15='मुख्‍यमंत्री' or element_1_15='उप मुख्‍यमंत्री'">
										<xsl:value-of select="element_1_11"/><fo:inline font-weight="normal"> पुढील गोष्टींचा खुलासा करतील काय :-</fo:inline>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="element_1_15"/> मंत्री <fo:inline font-weight="normal">पुढील गोष्टींचा खुलासा करतील काय :-</fo:inline>
									</xsl:otherwise>
								</xsl:choose>   				
                			</fo:block>
	                		<fo:block font-size="0pt">&#160;</fo:block>
	                		<fo:block font-weight="normal">
	                			<xsl:apply-templates select="element_1_7"></xsl:apply-templates>
	                		</fo:block>
	                		<fo:block font-size="9pt">&#160;</fo:block>
	                		<fo:block>
	                			<xsl:choose>
	                				<xsl:when test="not(element_1_8) or element_1_8=''">
	                					<fo:block font-weight="bold" text-align="center">
	                						(उत्तर आले नाही.)
	                					</fo:block>
	                				</xsl:when>
	                				<xsl:otherwise>
	                					<fo:inline font-weight="bold"><xsl:value-of select="element_1_9"/> (<xsl:value-of select="element_1_14"/>) :</fo:inline>
			                			<xsl:apply-templates select="element_1_8"></xsl:apply-templates>
	                				</xsl:otherwise>
	                			</xsl:choose>	                			
	                		</fo:block>
	                		<fo:block font-size="4.5pt">&#160;</fo:block>
	                		<fo:block text-align="center" font-size="9pt">	                			
								_____________
							</fo:block>
	                		<fo:block font-size="9pt">&#160;</fo:block>
	                	</xsl:for-each>
	                	<fo:block font-size="12pt">&#160;</fo:block>
	                	<xsl:choose>
	                		<xsl:when test="$formatOut='application/pdf'">
	                			<fo:block font-size="15pt">
			                		विधान भवन : 
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;	                			                		                		
			                		<fo:inline font-weight="bold">
			                			<xsl:value-of select="element_8"/>
			                		</fo:inline>
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<xsl:value-of select="element_7"/>.
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;
			                		प्रधान सचिव,
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<fo:block text-align="right">
			                			महाराष्ट्र&#160;<xsl:value-of select="element_3"/>
			                		</fo:block>
			                	</fo:block>
	                		</xsl:when>
	                		<xsl:when test="$formatOut='WORD'">
	                			<fo:block font-size="15pt" font-weight="bold" text-align="left">
	                				<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
	                					<fo:table-column column-number="1" column-width="5cm" />
				                        <fo:table-column column-number="2" column-width="6cm" />
				                        <fo:table-column column-number="3" column-width="5cm" />
				                        <fo:table-body>
				                        	<fo:table-row border-collapse="collapse">
				                        		<fo:table-cell>
				     								<fo:block text-align="left">
				     									विधान भवन :
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block>
				     									&#160;
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block text-align="center">
				     									<xsl:value-of select="element_8"/>
				     								</fo:block>
				     							</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row border-collapse="collapse">
				                        		<fo:table-cell>
				     								<fo:block text-align="left">
				     									<xsl:value-of select="element_7"/>.
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block>
				     									&#160;
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block text-align="center">
				     									प्रधान सचिव,
				     								</fo:block>
				     								<fo:block></fo:block>
				     								<fo:block text-align="center">
				     									महाराष्ट्र&#160;<xsl:value-of select="element_3"/>.
				     								</fo:block>
				     							</fo:table-cell>
				                        	</fo:table-row>
				                        </fo:table-body>
	                				</fo:table>
			                		<!-- विधान भवन : 
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		         			                		                		
			                		<fo:inline font-weight="bold">
			                			<xsl:value-of select="element_8"/>
			                		</fo:inline>
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<xsl:value-of select="element_7"/>.
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;			                		
			                		प्रधान सचिव,
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<fo:block text-align="right">
			                			महाराष्ट्र&#160;<xsl:value-of select="element_3"/>
			                		</fo:block> -->
			                	</fo:block>	
			                	<fo:block font-size="16px">&#160;</fo:block>		                	
			                	<!-- <fo:block text-align="center">
			                		<fo:block>_________________________</fo:block>
			                		<fo:block>शासकीय मध्यवर्ती मुद्रणालय, <xsl:value-of select="element_7"/>.</fo:block>				     				
				     			</fo:block> -->
				     			<fo:block>______________________________________________________________________________</fo:block>
			                	<fo:block font-size="15pt">
			                		<fo:block text-align="center">
				                		मुद्रणपूर्व सर्व प्रक्रिया महाराष्ट्र विधानमंडळ सचिवालयाच्या संगणक यंत्रणेवर 
				                	</fo:block>
				                	<fo:block></fo:block>
					     			<fo:block text-align="center">
					     				मुद्रण: शासकीय मध्यवर्ती मुद्रणालय, <xsl:value-of select="element_7"/>.
					     			</fo:block>
			                	</fo:block>
	                		</xsl:when>
	                	</xsl:choose>	                	                		                	
	                </fo:block>       
	            </fo:flow>
	            
	        </fo:page-sequence>
	    </fo:root>	
	    
    </xsl:template>      
    
    <!-- use for for loop with fixed no. of iterations -->
    <!-- <xsl:template match="/">
	    Start repeating
	    <xsl:call-template name="repeatable" />
	</xsl:template>
	
	<xsl:template name="repeatable">
	    <xsl:param name="index" select="1" />
	    <xsl:param name="total" select="10" />
	
	    Do something
	
	    <xsl:if test="not($index = $total)">
	        <xsl:call-template name="repeatable">
	            <xsl:with-param name="index" select="$index + 1" />
	        </xsl:call-template>
	    </xsl:if>
	</xsl:template> -->
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>