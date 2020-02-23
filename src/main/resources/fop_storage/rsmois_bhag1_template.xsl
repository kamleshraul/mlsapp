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
	            		<xsl:choose>
	            			<xsl:when test="element_1">
	            				<fo:block text-align="center" font-size="16px" font-weight="bold">
			            			<xsl:value-of select="element_1[1]/element_1_1"></xsl:value-of>
			            		</fo:block>
			            		<fo:block text-align="center" font-size="14px" font-weight="bold">
			            			<xsl:value-of select="element_1[1]/element_1_4"/>
			            		</fo:block>
			            		<fo:block text-align="center" font-size="14px" font-weight="bold">
			            			दिनांक : <xsl:value-of select="element_1[1]/element_1_2"></xsl:value-of>
			            		</fo:block>	
			            		<fo:block font-size="14px">&#160;</fo:block>
	            				
            					<!-- <xsl:if test="position()!=1">
            						<fo:block break-before="page"/>
            					</xsl:if> -->	            						
			            		<fo:block>
										<fo:block>
											आज दिनांक <xsl:value-of select="element_1[1]/element_1_2"></xsl:value-of> रोजीचे सभागृहातील आजचे इतर सर्व कामकाज <xsl:value-of select="element_1[1]/element_1_4"/>
										</fo:block>
										<fo:block>
											स्थगित करण्याबाबत खालील विषयांवर सूचना प्राप्त झालेल्या आहेत  :-
										</fo:block>
			            		</fo:block>    
			            		<fo:block >
			            			<fo:block font-size="14px" font-weight="bold">सूचना क्र.  (<xsl:value-of select="element_1[1]/element_1_6"/>) :  
			            				<xsl:choose>
                                      		<xsl:when test="element_1_7='श्री.देवेंद्र फडणवीस'">श्री.देवेंद्र फडणवीस,मा.विरोधी पक्ष नेता व इतर वि.स.स.</xsl:when>
											<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_7"/> व इतर वि.स.स.</xsl:otherwise>
                                      	</xsl:choose>
			            			</fo:block> 
			            				
			            		<fo:block>
			            			 	&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"<xsl:value-of select="element_1[1]/element_1_9"/>"
			            		</fo:block>	
			            		</fo:block>		
	            			</xsl:when>
	            			<xsl:otherwise>
	            				<fo:block text-align="center" font-size="14px" font-weight="bold">
	            					सध्या एकही प्रस्ताव भाग १ साठी उपलब्ध नाही.
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