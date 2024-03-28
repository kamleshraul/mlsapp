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
    
    <xsl:template match="DeviceData">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="1.5cm" margin-right="1.5cm">
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
	        
	        <fo:page-sequence master-reference="first" id="DocumentBody">
	        	
	        	<!-- header -->
	        	<fo:static-content flow-name="rb-common">
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for header for all pages -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-common">
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for footer for all pages -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
			       <fo:block font-family="Kokila" font-size="15px">
			       <fo:block font-size="20px" text-align="center" font-weight="bold">
			        महाराष्ट्र <xsl:value-of select="houseTypeName"/> / 
			        <xsl:choose>
								<xsl:when test="sessionNumber = 1">
									प्रथम
								</xsl:when>
								<xsl:when test="sessionNumber = 2">
									द्वितीय
								</xsl:when>
								<xsl:when test="sessionNumber = 3">
									तृतीय
								</xsl:when>
								<xsl:when test="sessionNumber = 4">
									चतुर्थ
								</xsl:when>
								<xsl:when test="sessionNumber = 5">
									पाचवे
								</xsl:when>
								<xsl:when test="sessionNumber = 6">
									सहावे
								</xsl:when>
								<xsl:when test="sessionNumber = 7">
									सातवे
								</xsl:when>
							</xsl:choose>
							अधिवेशन, <xsl:value-of select="sessionYear"/>
			       </fo:block>
			       <!-- As Device Type field is not available so HouseType field to set the Device Type -->
			       <fo:block font-size="20px" text-align="center" font-weight="bold"><xsl:value-of select="houseType"/></fo:block>
			       <fo:block font-size="20px" text-align="center" font-weight="bold">दिनांक: <xsl:value-of select="answeringDate"/></fo:block>
			       <fo:block font-size="40px" text-decoration="underline" padding-top="10cm" text-align="center" font-weight="bold">बॅलेटमध्ये न आलेले</fo:block>
           			 <!-- Add a page break -->
          		     <fo:block break-before="page"/>
			       		<fo:block  text-align="center" font-weight="bold">
			       			<!-- <fo:block font-size="26px" margin-bottom="3mm"><xsl:value-of select="element_3[1]"/></fo:block>
			       			<fo:block margin-bottom="0.5mm" font-size="20px"><xsl:apply-templates select="element_3[2]"/></fo:block>
			       			<fo:block margin-bottom="0.5mm" font-size="20px"><xsl:apply-templates select="element_3[3]"/></fo:block> -->
			       			<!-- <fo:block margin-bottom="2mm" font-size="20px"><xsl:apply-templates select="element_3[4]"/></fo:block> -->
			       			
			       		</fo:block>
				      	<fo:block font-size="4px">&#160;</fo:block>	   
				      	<fo:block>
				       	 	 <fo:table table-layout="fixed" border-collapse="collapse">
								<fo:table-column column-width="2.5cm"/>
						        <fo:table-column column-width="4.5cm"/>
						        <fo:table-column column-width="2cm"/>
						        <fo:table-column column-width="8cm"/>
						        <fo:table-header>
						        	<fo:table-row>
						        		<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
						        			<fo:block font-weight="bold">
						        			    अ.क्र.
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
						        			<fo:block font-weight="bold">
						        				सदस्यांचे नांव
						        			</fo:block>
						           		</fo:table-cell>
						        		<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
						        			<fo:block font-weight="bold">
						        				सूचना क्रमांक
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid">
						        			<fo:block font-weight="bold">
						        				विषय
						        			</fo:block>
						        		</fo:table-cell>
						        	</fo:table-row>
						        </fo:table-header>
						         <fo:table-body>
						        	<xsl:for-each select="./deviceVOs/deviceVO">
						        		<xsl:variable name="count" select="position()"/>
						        		<fo:table-row>
							        		<xsl:choose>
							        			<xsl:when test="position()!=last()">
							        				<fo:table-cell text-align="right" border-width="0.5pt" border-style="solid" margin-right="0.5cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="formattedSerialNumber"/>.
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell text-align="center" border-width="0.5pt" border-style="solid" margin-left="0.8cm" margin-right="0.3cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block  font-size="17px" margin-bottom="0.5mm">
									        				<xsl:value-of select="memberNames"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell text-align="center" border-width="0.5pt" border-style="solid" margin-left="0.3cm" margin-right="0.3cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block font-size="17px" margin-bottom="2mm">
									        				<xsl:value-of select="formattedNumber"/>
									        			</fo:block>							        			
									        		</fo:table-cell>
									        		<fo:table-cell text-align="justify" border-width="0.5pt" border-style="solid" margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block font-size="17px" margin-bottom="2mm">
									        				<xsl:value-of select="subject"/>
									        			</fo:block>							        			
									        		</fo:table-cell>
									        	</xsl:when>
							        			<xsl:otherwise>
							   						<fo:table-cell text-align="right" border-width="0.5pt" border-style="solid" margin-right="0.5cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="formattedSerialNumber"/>.
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell text-align="center" border-width="0.5pt" border-style="solid" margin-left="0.8cm" margin-right="0.3cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block  font-size="17px" margin-bottom="0.5mm">
									        				<xsl:value-of select="memberNames"/> 
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell text-align="center" border-width="0.5pt" border-style="solid" margin-left="0.3cm" margin-right="0.3cm" display-align="center" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block font-size="17px" margin-bottom="2mm">
									        				<xsl:value-of select="formattedNumber"/>
									        			</fo:block>							        			
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" border-width="0.5pt" border-style="solid" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block font-size="17px" margin-bottom="2mm">
									        				<xsl:value-of select="subject"/>
									        			</fo:block>							        			
									        		</fo:table-cell>
									        	</xsl:otherwise>
							        		</xsl:choose>
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
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>