<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="QuestionData"/>
    
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
						<fo:block text-align="left">
							<fo:block text-align="center" font-weight="bold"><xsl:value-of select="houseType"/>&#160;<xsl:value-of select="formattedNumber"/></fo:block>	
							<fo:block>&#160;</fo:block>
							<fo:table>
							<fo:table-body >
      							<fo:table-row>
									<fo:table-cell>
										<fo:block>
										<xsl:choose>
											<xsl:when test="formattedNumber!=''">
												<fo:inline font-weight="bold">क्रमांक: </fo:inline><xsl:value-of select="formattedNumber"/>
											</xsl:when>
											<xsl:otherwise>
												<fo:block font-weight="bold">क्रमांक: - </fo:block>
											</xsl:otherwise>
										</xsl:choose> 
										</fo:block>             			                		                		
		                			</fo:table-cell>
		                			<fo:table-cell>
		                				<fo:block>
		                					<fo:inline font-weight="bold">स्थिती:  </fo:inline><xsl:value-of select="status"/>
		                				</fo:block>
	                				</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell font-weight="bold" number-columns-spanned="1">
									<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
										<xsl:choose>
											<xsl:when test="submissionDate!=''">
												<fo:inline font-weight="bold">सूचना दिल्याचा दिनांक: </fo:inline><xsl:value-of select="submissionDate"/>
											</xsl:when>
											<xsl:otherwise>
												<fo:block font-weight="bold">सूचना दिल्याचा दिनांक: -</fo:block>
											</xsl:otherwise>
										</xsl:choose> 
										</fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                				<fo:block>
										<xsl:choose>
											<xsl:when test="serialNumber!=''">
												<fo:inline font-weight="bold">प्राथम्य क्रमांक: </fo:inline><xsl:value-of select="serialNumber"/>
											</xsl:when>
										</xsl:choose> 
										</fo:block>  
	                				</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell font-weight="bold" number-columns-spanned="1">
									<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											<fo:inline font-weight="bold">सदस्य:  </fo:inline><xsl:value-of select="memberNames"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell font-weight="bold" number-columns-spanned="1">
									<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block>
											<fo:inline font-weight="bold">निर्वाचन क्षेत्र:  </fo:inline><xsl:value-of select="constituency"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
							</fo:table>
							<fo:block>&#160;</fo:block>
							<fo:block><fo:inline font-weight="bold">मंत्री: </fo:inline><xsl:value-of select="ministryName"/></fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block><fo:inline font-weight="bold">विभाग: </fo:inline><xsl:value-of select="subdepartmentName"/></fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block>
								<xsl:choose>
									<xsl:when test="shortDetails!=''">
										<fo:block><fo:inline font-weight="bold">पाठिंबा देणारे सदस्य: </fo:inline><xsl:apply-templates select="supportingMembers"></xsl:apply-templates></fo:block>
									</xsl:when>
								</xsl:choose>
							</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block><fo:inline font-weight="bold">विषय: </fo:inline><xsl:value-of select="subject"/></fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block><fo:inline font-weight="bold">प्रश्न: </fo:inline><xsl:apply-templates select="content"></xsl:apply-templates></fo:block>
						</fo:block>	
					</fo:block>												          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>