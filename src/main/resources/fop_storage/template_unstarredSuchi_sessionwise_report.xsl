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
                  margin-top="1cm"
                  margin-bottom="1cm"
                  margin-left="1.5cm"
                  margin-right="1.5cm">
			      <fo:region-body margin-top="0.1cm"/>
			      <!-- <fo:region-before region-name="page-number" extent="2cm"/> -->
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
		    
          
	            <fo:simple-page-master master-name="otherPages"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1cm"
                  margin-bottom="1cm"
                  margin-left="1.5cm"
                  margin-right="1.5cm">
			      <fo:region-body margin-top="1cm"/>
			      <!-- <fo:region-before region-name="page-number" extent="2cm"/> -->
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
		        <fo:flow flow-name="xsl-region-body">	
	            	<fo:block font-family="Mangal" font-size="10.5pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
					    <fo:block font-size="18" font-weight="bold" text-align="center">
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
						<!-- <fo:block text-align="center" font-size="9pt" font-weight="bold">
							-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;
						</fo:block> -->						
						<fo:block font-size="2px">&#160;</fo:block>
	                	<fo:block text-align="left" margin-left="0.25cm" font-size="12.5pt" font-weight="bold">
	                		अतारांकित प्रश्नोत्तरांची 
                			<xsl:choose>
                				<xsl:when test="element_9 = '१'"><fo:inline font-weight="bold" text-decoration="underline">पहिली यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '२'"><fo:inline font-weight="bold" text-decoration="underline">दुसरी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '३'"><fo:inline font-weight="bold" text-decoration="underline">तिसरी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '४'"><fo:inline font-weight="bold" text-decoration="underline">चौथी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '५'"><fo:inline font-weight="bold" text-decoration="underline">पाचवी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '६'"><fo:inline font-weight="bold" text-decoration="underline">सहावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '७'"><fo:inline font-weight="bold" text-decoration="underline">सातवी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '८'"><fo:inline font-weight="bold" text-decoration="underline">आठवी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '९'"><fo:inline font-weight="bold" text-decoration="underline">नववी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१०'"><fo:inline font-weight="bold" text-decoration="underline">दहावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '११'"><fo:inline font-weight="bold" text-decoration="underline">अकरावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१२'"><fo:inline font-weight="bold" text-decoration="underline">बारावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१३'"><fo:inline font-weight="bold" text-decoration="underline">तेरावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१४'"><fo:inline font-weight="bold" text-decoration="underline">चौदावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१५'"><fo:inline font-weight="bold" text-decoration="underline">पंधरावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१६'"><fo:inline font-weight="bold" text-decoration="underline">सोळावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१७'"><fo:inline font-weight="bold" text-decoration="underline">सतरावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१८'"><fo:inline font-weight="bold" text-decoration="underline">अठरावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '१९'"><fo:inline font-weight="bold" text-decoration="underline">एकोणिसावी यादी</fo:inline></xsl:when>
                				<xsl:when test="element_9 = '२०'"><fo:inline font-weight="bold" text-decoration="underline">विसावी यादी</fo:inline></xsl:when>
                				<xsl:otherwise><fo:inline font-weight="bold" text-decoration="underline">यादी क्रमांक <xsl:value-of select="element_9"/></fo:inline></xsl:otherwise>
                			</xsl:choose> 
                			<fo:inline font-weight="bold"><xsl:value-of select="element_10"/></fo:inline> रोजी सभागृहाच्या पटलावर ठेवण्यात आली.		                		
	                	</fo:block>
	                	<fo:block font-size="2px">&#160;</fo:block>
					    
					    <fo:block>
					    	<fo:table table-layout="fixed">
					    		<fo:table-column column-number="1" column-width="9cm" />
		                        <fo:table-column column-number="2" column-width="9cm" />
		                        <fo:table-body>
		                        	<fo:table-row>
		                        		<fo:table-cell border-width="0.5pt" border-left-style="solid">
		                        			<fo:table table-layout="fixed">
					                			<fo:table-column column-number="1" column-width="1cm" />
						                        <fo:table-column column-number="2" column-width="2cm" />
						                        <fo:table-column column-number="3" column-width="6cm" />
						                        <fo:table-header>
						                        	<fo:table-row height="8mm">
						                        		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	   	<fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    अ.क्र.
						                                    </fo:block>
							                        	</fo:table-cell>
							                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	    <fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    प्रश्न क्रमांक
						                                    </fo:block>
							                        	</fo:table-cell>
							                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	    <fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    अधिवेशन
						                                    </fo:block>
							                        	</fo:table-cell>
						                        	</fo:table-row>
						                        </fo:table-header>
						                        <fo:table-body>	                        	                    	
					                            	<!-- <xsl:for-each select="./element_1" > -->
					                            	<xsl:variable name="countBefore26" select="'25'"/>
					                            	<xsl:for-each select="./element_1[position()&lt;=$countBefore26]">
						                            	<fo:table-row height="8mm">	                                	
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
						                                        <fo:block  text-align="center">
						                                        	<xsl:value-of select="element_1_1"/>
						                                        </fo:block>
						                                    </fo:table-cell>		                                    
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                                            <fo:block  text-align="center">
						                                        	<xsl:value-of select="element_1_4"/>
						                                        </fo:block>
						                                    </fo:table-cell>		                                    
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
						                                        <fo:block  text-align="left">
						                                        	सन <xsl:value-of select="element_1_6"/> चे
						                                        	<xsl:choose>
																		<xsl:when test="element_1_5 = 1">
																			प्रथम
																		</xsl:when>
																		<xsl:when test="element_1_5 = 2">
																			द्वितीय	
																		</xsl:when>
																		<xsl:when test="element_1_5 = 3">
																			तृतीय
																		</xsl:when>
																		<xsl:when test="element_1_5 = 4">
																			चतुर्थ
																		</xsl:when>
																		<xsl:when test="element_1_5 = 5">
																			पंचम
																		</xsl:when>
																		<xsl:when test="element_1_5 = 6">
																			षष्ठ
																		</xsl:when>
																		<xsl:when test="element_1_5 = 7">
																			सप्तम
																		</xsl:when>
																		<xsl:otherwise></xsl:otherwise>
																	</xsl:choose>
						                                        	(<xsl:value-of select="element_1_7"/>)
						                                        </fo:block> 
						                                    </fo:table-cell>
						                                </fo:table-row>	                               
					                            </xsl:for-each>	
					                        	</fo:table-body>
					                		</fo:table>
		                        		</fo:table-cell>
		                        		
		                        		<fo:table-cell border-width="0.5pt" border-left-style="solid">
		                        			<fo:table table-layout="fixed">
					                			<fo:table-column column-number="1" column-width="1cm" />
						                        <fo:table-column column-number="2" column-width="2cm" />
						                        <fo:table-column column-number="3" column-width="6cm" />
						                        <fo:table-header>
						                        	<fo:table-row height="8mm">
						                        		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	   	<fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    अ.क्र.
						                                    </fo:block>
							                        	</fo:table-cell>
							                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	    <fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    प्रश्न क्रमांक
						                                    </fo:block>
							                        	</fo:table-cell>
							                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" font-weight="bold">
							                        	    <fo:block text-align="center" font-size="10.5pt" font-weight="bold">
					                                                                    अधिवेशन
						                                    </fo:block>
							                        	</fo:table-cell>
						                        	</fo:table-row>
						                        </fo:table-header>
						                        <fo:table-body>	                        	                    	
					                            	<!-- <xsl:for-each select="./element_1" > -->
					                            	<xsl:variable name="countAfter25" select="'26'"/>
					                            	<xsl:for-each select="./element_1[position()&gt;=$countAfter25]">
						                            	<fo:table-row height="8mm">	                                	
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
						                                        <fo:block  text-align="center">
						                                        	<xsl:value-of select="element_1_1"/>
						                                        </fo:block>
						                                    </fo:table-cell>		                                    
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid">
					                                            <fo:block  text-align="center">
						                                        	<xsl:value-of select="element_1_4"/>
						                                        </fo:block>
						                                    </fo:table-cell>		                                    
						                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
						                                        <fo:block  text-align="left">
						                                        	सन <xsl:value-of select="element_1_6"/> चे
						                                        	<xsl:choose>
																		<xsl:when test="element_1_5 = 1">
																			प्रथम
																		</xsl:when>
																		<xsl:when test="element_1_5 = 2">
																			द्वितीय	
																		</xsl:when>
																		<xsl:when test="element_1_5 = 3">
																			तृतीय
																		</xsl:when>
																		<xsl:when test="element_1_5 = 4">
																			चतुर्थ
																		</xsl:when>
																		<xsl:when test="element_1_5 = 5">
																			पंचम
																		</xsl:when>
																		<xsl:when test="element_1_5 = 6">
																			षष्ठ
																		</xsl:when>
																		<xsl:when test="element_1_5 = 7">
																			सप्तम
																		</xsl:when>
																		<xsl:otherwise></xsl:otherwise>
																	</xsl:choose>
						                                        	(<xsl:value-of select="element_1_7"/>)
						                                        </fo:block> 
						                                    </fo:table-cell>
						                                </fo:table-row>	                               
					                            </xsl:for-each>	
					                        	</fo:table-body>
					                		</fo:table>
		                        		</fo:table-cell>
		                        	</fo:table-row>
		                        </fo:table-body>
					    	</fo:table>					    	
					    </fo:block>
					    
					    <!-- <fo:block font-size="12pt">&#160;</fo:block>
					    
	                	<fo:block font-weight="bold" text-align="left">
              				<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
              					<fo:table-column column-number="1" column-width="5cm" />
		                        <fo:table-column column-number="2" column-width="9cm" />
		                        <fo:table-column column-number="3" column-width="4cm" />
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
		     							</fo:table-cell>
		                        	</fo:table-row>
		                        	<fo:table-row border-collapse="collapse">
		                        		<fo:table-cell>
		     								<fo:block text-align="left">
		     									दिनांक : <xsl:value-of select="reportDate"/>
		     								</fo:block>
		     							</fo:table-cell>
		     							<fo:table-cell>
		     								<fo:block>
		     									&#160;
		     								</fo:block>
		     							</fo:table-cell>
		     							<fo:table-cell>
		     								<fo:block text-align="center">
		     									महाराष्ट्र&#160;<xsl:value-of select="element_3"/>.
		     								</fo:block>
		     							</fo:table-cell>
		                        	</fo:table-row>
		                        </fo:table-body>
              				</fo:table>
                		</fo:block> -->	                	                		                	
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