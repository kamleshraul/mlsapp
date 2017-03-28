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
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before extent="2cm"/>
			      	<fo:region-after extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="1.5cm" margin-right="1.5cm">
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
					<fo:block font-family="Mangal" font-size="11pt" font-weight="bold" text-align="right" text-decoration="underline">
		        		<xsl:value-of select="element_1/element_1_1"/>		        		
		        	</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<!-- <fo:static-content flow-name="ra-common">
					<fo:block text-align="center" font-family="Mangal">
					   	content for footer for all pages
					</fo:block>
			    </fo:static-content> -->
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
			       <!-- content as per report -->	
			       <fo:block font-family="Mangal" font-size="11pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">			       
			       		<fo:block font-size="18pt" font-weight="bold" text-align="center" text-decoration="underline">
			       			<xsl:value-of select="element_1/element_1_2"/>
			       		</fo:block>
			       		<fo:block font-size="14pt" font-weight="bold" text-align="center" text-decoration="underline">
			       			<xsl:value-of select="element_1/element_1_3"/>
			       		</fo:block>
			       		<fo:block font-size="14pt" font-weight="bold" text-align="center" text-decoration="underline">
			       			<xsl:value-of select="element_4"/>
			       		</fo:block>
			       		<fo:block font-size="14pt" font-weight="bold" text-align="center">
			       			<xsl:value-of select="element_1/element_1_4"/>
			       		</fo:block>
			       		<fo:block font-size="5pt">&#160;</fo:block>
			       		<fo:block>
			     		<xsl:choose>
			     			<xsl:when test="boolean(element_2) and count(element_2)>0">
			     				<fo:table table-layout="fixed" width="100%">
		                			<fo:table-column column-number="1" column-width="1.5cm" />
			                        <fo:table-column column-number="2" column-width="4.0cm" />
			                        <fo:table-column column-number="3" column-width="8.0cm" />
			                        <fo:table-column column-number="4" column-width="4.5cm" />
			                        <fo:table-header>
			                        	<fo:table-row>
			                        		<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
				                        	   	<fo:block text-align="center" font-weight="bold">
		                                        	<xsl:value-of select="element_1/element_1_5"/>
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                        	<xsl:value-of select="element_1/element_1_6"/>
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                        	<xsl:value-of select="element_1/element_1_7"/>
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                        	<xsl:value-of select="element_1/element_1_8"/>
			                                    </fo:block>
				                        	</fo:table-cell>
			                        	</fo:table-row>
			                        </fo:table-header>
			                        <fo:table-body>	                        	                    	
		                            	<xsl:for-each select="./element_2" >
			                            	<fo:table-row border="solid 0.1mm black">	                                	
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
			                                        <fo:block  text-align="center">
			                                        	<xsl:value-of select="element_2_1[1]" />)
			                                        </fo:block>
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
		                                            <fo:block  text-align="center">
			                                        	<xsl:apply-templates select="element_2_1[3]" />
			                                        </fo:block>
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
			                                        <fo:block text-align="left">
			                                        	<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
		                                        			<fo:table-column column-number="1" column-width="1cm" />
	                        								<fo:table-column column-number="2" column-width="6.5cm" />
	                        								<fo:table-body>
	                        									<xsl:for-each select="./element_2_2" >
			                                        			<fo:table-row border-collapse="collapse">			     						
									     							<fo:table-cell>
									     								<fo:block text-align="center">
									     									(<xsl:value-of select="element_2_2_1" />)
									     								</fo:block>
									     							</fo:table-cell>
									     							<fo:table-cell padding-left="2px">
									     								<fo:block>
									     									<xsl:value-of select="element_2_2_4" />
									     								</fo:block>
									     							</fo:table-cell>		     										     							
									     						</fo:table-row>
			                                        			</xsl:for-each>
	                        								</fo:table-body>
		                                        		</fo:table>		                                        	
			                                        </fo:block> 
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" padding-left="2px" padding-right="2px">
			                                        <fo:block  text-align="center">
			                                        	<xsl:apply-templates select="element_2_1[4]" />
			                                        </fo:block>
			                                    </fo:table-cell>                                   	
			                                </fo:table-row>	                               
		                            </xsl:for-each>	
		                            <fo:table-row border-collapse="collapse">			     						
		     							<fo:table-cell>
		     								<fo:block>
		     									&#160;
		     								</fo:block>
		     							</fo:table-cell>
		     							<fo:table-cell>
		     								<fo:block>
		     									&#160;
		     								</fo:block>
		     							</fo:table-cell>
		     							<fo:table-cell display-align="after">
		     								<fo:block font-size="14pt" font-weight="bold" text-align="center">
		     									<xsl:value-of select="element_1/element_1_9"/>
		     								</fo:block>
		     							</fo:table-cell>
		     							<fo:table-cell>
		     								<fo:block>
		     									&#160;
		     								</fo:block>
		     							</fo:table-cell>		     										     							
		     						</fo:table-row>
		                        	</fo:table-body>
		                		</fo:table>
			     			</xsl:when>
			     			<xsl:otherwise>
		     					<xsl:value-of select="../element_1/element_1_20"/>
		     				</xsl:otherwise>
			     		</xsl:choose>                		
               			</fo:block>
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