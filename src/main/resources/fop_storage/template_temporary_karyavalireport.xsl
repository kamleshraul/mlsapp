<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="ResolutionData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>

	<!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
    <xsl:template match="ResolutionData">

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
	        
	        <fo:page-sequence master-reference="others" id="DocumentBody">
	        	
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
	            	    
			      	<fo:block font-family="Kokila" font-size="15px" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
	               		
	               		<fo:block font-size="15px" font-weight="bold" text-align="right">
							पताका क्रमांक <xsl:value-of select="karyavaliNumber"/>
						</fo:block>
	               		
	               		<fo:block font-size="15px" font-weight="bold">							
							<xsl:choose>
			            		<xsl:when test="houseType='lowerhouse'">अशासकीय ठराव (म.वि.स. नियम १०६):-</xsl:when>
			            		<xsl:when test="houseType='upperhouse'">अशासकीय ठराव (म.वि.प. नियम १०२):-</xsl:when>
			            		<xsl:otherwise>अशासकीय ठराव :-</xsl:otherwise>
		            		</xsl:choose>
						</fo:block>
					
						
					
				
				<xsl:choose>
					<xsl:when test="not(./resolutionList/resolutionListForMember)">
						<fo:block font-size="7px">&#160;</fo:block>
						<fo:block font-size="15px" font-weight="bold">
				     		माहिती उपलब्ध नाही.
				     	 </fo:block>
					</xsl:when>
					<xsl:otherwise>
					<fo:block>
						 <xsl:for-each select="./resolutionList/resolutionListForMember" >
						 	<fo:block font-size="7px">&#160;</fo:block>
						 	<fo:block font-size="15px" font-weight="bold">				    
						      <xsl:value-of select="memberName" /> यांचे ठराव क्रमांक
						      <xsl:for-each select="./deviceVOs" >
								<xsl:value-of select="formattedNumber" />
								<xsl:if test="position() &lt; (last()-1)">, </xsl:if>
								<xsl:if test="position()=(last()-1)"> व </xsl:if>
						      </xsl:for-each>
						    </fo:block>
						    <fo:block font-size="12px">&#160;</fo:block>
						    	<fo:block>
						    		<fo:table table-layout="fixed" width="16cm">
						    			<fo:table-body>
						    				<xsl:choose>
						    					<xsl:when test="boolean(deviceVOs)">
						    						<xsl:for-each select="./deviceVOs" >
									 					<fo:table-row>
									 						<fo:table-cell width="1.2cm">
									 							<fo:block>(<xsl:value-of select="formattedNumber" />)</fo:block>
									 						</fo:table-cell>
									 						<fo:table-cell width="14.8cm" text-align="justify">
									 							 <fo:block><xsl:value-of select="content" /></fo:block>
									 						</fo:table-cell>
									 					</fo:table-row>
									 					<fo:table-row>
								 						<!-- <fo:table-cell number-columns-spanned="2">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell> -->
								 						<fo:table-cell width="1.2cm">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell>
								 						<fo:table-cell width="14.8cm">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell>
							 						</fo:table-row>
							 						</xsl:for-each>
						    					</xsl:when>
						    					<xsl:otherwise>
								    				<fo:table-row>
								 						<!-- <fo:table-cell number-columns-spanned="2">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell> -->
								 						<fo:table-cell width="1.2cm">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell>
								 						<fo:table-cell width="14.8cm">
								 							<fo:block font-size="7px">&#160;</fo:block>
								 						</fo:table-cell>
							 						</fo:table-row>
						    					</xsl:otherwise>
						    				</xsl:choose>
						    			</fo:table-body>
						    		</fo:table>
						   		 </fo:block>
							 </xsl:for-each>
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