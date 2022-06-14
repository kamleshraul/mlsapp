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
                  page-height="21cm"
                  page-width="29.7cm"
                  margin-top="2cm"
                  margin-bottom="2cm"
                  margin-left="2cm"
                  margin-right="2cm">
			      <fo:region-body margin-top="1cm"/>        
			      <!-- <fo:region-before region-name="page-number" extent="2cm"/> -->
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
		    
          
	            <fo:simple-page-master master-name="otherPages"
                  page-height="21cm"
                  page-width="29.7cm"
                  margin-top="2cm"
                  margin-bottom="2cm"
                  margin-left="2cm"
                  margin-right="2cm">
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
	            	<fo:block font-family="Kokila" font-size="16pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
					    <xsl:choose>
					    	<xsl:when test="element_3='विधानसभा'">
					    		<fo:block font-size="20" font-weight="bold" text-align="center">
									महाराष्ट्र <xsl:value-of select="element_3"/>												
								</fo:block>
								<fo:block font-size="2px">&#160;</fo:block>
								<fo:block font-size="17pt" font-weight="bold" text-align="center">
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
								<fo:block text-align="center" font-size="18pt" font-weight="bold">
									-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;
								</fo:block>						
								<fo:block font-size="5px">&#160;</fo:block>	                	
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		<fo:block>
			                			दिनांक <xsl:value-of select="element_10"/> रोजी सभागृहाच्या पटलावर ठेवण्यात आलेल्या
			                		</fo:block>
			                		<fo:block>
			                			अतारांकित प्रश्नोत्तरांच्या यादी क्रमांक <xsl:value-of select="element_9"/> मध्ये
			                		</fo:block>	
			                		<fo:block>
			                			समाविष्ट करण्यात आलेल्या प्रश्नांची सूची
			                		</fo:block>		                		
			                	</fo:block>	 
			                	<fo:block font-size="9px">&#160;</fo:block>           		                	           	
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		<fo:block>प्रश्नांची एकूण संख्या - <xsl:value-of select="element_2"/></fo:block>
			                	</fo:block>
			                	<!-- <fo:block font-size="7px">&#160;</fo:block> -->
			                	<fo:block text-align="center" font-size="12pt" font-weight="bold">
			                		___________
								</fo:block>
			                	<fo:block font-size="7pt">&#160;</fo:block>
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		प्रश्नांचा तपशील
			                	</fo:block>
			                	<fo:block font-size="7pt">&#160;</fo:block>
					    	</xsl:when>
					    	
					    	<xsl:when test="element_3='विधानपरिषद'">
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="9px">&#160;</fo:block>
					    		<fo:block font-size="20" font-weight="bold" text-align="center">
									महाराष्ट्र <xsl:value-of select="element_3"/>												
								</fo:block>
								<fo:block font-size="2px">&#160;</fo:block>
								<fo:block font-size="16pt" font-weight="bold" text-align="center">
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
									-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;-&#160;
								</fo:block>						
								<fo:block font-size="5px">&#160;</fo:block>	                	
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		<fo:block>
			                			दिनांक <xsl:value-of select="element_10"/> रोजी सभागृहाच्या पटलावर ठेवण्यात आलेल्या
			                		</fo:block>
			                		<fo:block>
			                			अतारांकित प्रश्नोत्तरांच्या यादी क्रमांक <xsl:value-of select="element_9"/> मध्ये
			                		</fo:block>	
			                		<fo:block>
			                			समाविष्ट करण्यात आलेल्या प्रश्नांची सूची
			                		</fo:block>		                		
			                	</fo:block>	 
			                	<fo:block font-size="9px">&#160;</fo:block>           		                	           	
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		<fo:block>प्रश्नांची एकूण संख्या - <xsl:value-of select="element_2"/></fo:block>
			                	</fo:block>
			                	<!-- <fo:block font-size="7px">&#160;</fo:block> -->
			                	<fo:block text-align="center" font-size="9pt">
			                		___________
								</fo:block>
			                	<fo:block font-size="8pt">&#160;</fo:block>
			                	<fo:block text-align="center" font-size="16pt" font-weight="bold">
			                		प्रश्नांचा तपशील
			                	</fo:block>
			                	<fo:block font-size="8pt">&#160;</fo:block>
			                </xsl:when>
					    </xsl:choose>
					    
					    <fo:block text-align="center" font-weight="bold">
					    	<fo:table table-layout="fixed">
	                			<fo:table-column column-number="1" column-width="1cm" />
		                        <fo:table-column column-number="2" column-width="2cm" />
		                        <fo:table-column column-number="3" column-width="5cm" />
		                        <fo:table-column column-number="4" column-width="5cm" />
		                        <fo:table-column column-number="5" column-width="6cm" />
		                        <fo:table-column column-number="6" column-width="3.5cm" />
		                        <fo:table-column column-number="7" column-width="3cm" />
		                        <fo:table-header>
		                        	<fo:table-row background-color="green">
		                        		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	   	<fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    अ. क्र.
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    प्रश्न क्रमांक
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    सदस्यांचे नांव
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    मंत्री व विभाग
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    विषय
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    विभागाला पाठविल्याचा दिनांक
		                                    </fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
			                        	    <fo:block text-align="center" font-size="15pt" font-weight="bold">
	                                                                    उत्तर प्राप्त झाल्याचा दिनांक
		                                    </fo:block>
			                        	</fo:table-cell>
		                        	</fo:table-row>
		                        </fo:table-header>
		                        <fo:table-body>	                        	                    	
	                            	<xsl:for-each select="./element_1" >
		                            	<fo:table-row border="solid 0.1mm black" keep-together = "always">	                                	
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
		                                        <fo:block  text-align="center">
		                                        	<xsl:value-of select="element_1_1"/>
		                                        </fo:block>
		                                    </fo:table-cell>		                                    
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
	                                            <fo:block  text-align="center">
		                                        	<xsl:value-of select="element_1_4"/>
		                                        </fo:block>
		                                    </fo:table-cell>		                                    
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
		                                        <fo:block  text-align="left">
		                                        	<xsl:value-of select="element_1_5"/>
		                                        </fo:block> 
		                                    </fo:table-cell>	
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
		                                        <fo:block  text-align="left">
		                                        	<xsl:choose>
		                                        		<xsl:when test="boolean(element_1_10) and element_1_10!=''">
		                                        			<xsl:value-of select="element_1_15"/>, <xsl:value-of select="element_1_10"/> विभाग
		                                        		</xsl:when>
		                                        		<xsl:otherwise>
		                                        			<xsl:value-of select="element_1_15"/>
		                                        		</xsl:otherwise>
		                                        	</xsl:choose>           	
		                                        </fo:block> 
		                                    </fo:table-cell>                                    
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5" padding-right="5">
		                                        <fo:block  text-align="left">
		                                        	<xsl:value-of select="element_1_6"/>
		                                        </fo:block>
		                                    </fo:table-cell>   
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
		                                        <fo:block  text-align="center">
		                                        	<xsl:value-of select="element_1_13"/>
		                                        </fo:block>
		                                    </fo:table-cell>
		                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
		                                        <fo:block  text-align="center">
		                                        	<xsl:value-of select="element_1_14"/>
		                                        </fo:block>
		                                    </fo:table-cell>                                	
		                                </fo:table-row>	                               
	                            </xsl:for-each>	
	                        	</fo:table-body>
	                		</fo:table>
					    </fo:block>
					    
					    <fo:block font-size="12pt">&#160;</fo:block>
	                	<xsl:choose>
	                		<xsl:when test="$formatOut='application/pdf'">
	                			<fo:block font-weight="bold">
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
	                			<fo:block font-weight="bold" text-align="left">
	                				<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
	                					<fo:table-column column-number="1" column-width="5cm" />
				                        <fo:table-column column-number="2" column-width="16cm" />
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