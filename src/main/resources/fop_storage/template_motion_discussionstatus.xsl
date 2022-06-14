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
    
    <xsl:attribute-set name="myBorder">
	  	<xsl:attribute name="border">solid 0.1mm black</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="centerText">
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>
	
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
	            	<fo:block font-family="Kokila" font-size="15px">
																																	
						<fo:block>
							<fo:table xsl:use-attribute-sets="myBorder">
							  	<fo:table-header text-align="center" background-color="silver">
							  		<fo:table-row>
							  			<fo:table-cell padding="1mm" border-width="0.1mm" border-style="solid" number-columns-spanned="5" font-weight="bold" font-size="15px">
							  				<fo:block>
							   					<xsl:value-of select="./element_2[1]"></xsl:value-of>
							   				</fo:block>
							   				
							   				<fo:block>
							   					<xsl:value-of select="./element_2[2]"></xsl:value-of>
							   				</fo:block>
							   				
							   				<fo:block>
							   					<xsl:value-of select="./element_2[3]"></xsl:value-of>
							   				</fo:block>
							   				
							   				<fo:block>
							   					<xsl:value-of select="./element_2[4]"></xsl:value-of>
							   				</fo:block>
							  			</fo:table-cell>
							  		</fo:table-row>
							  		
							    	<fo:table-row font-weight="bold">
							      		<fo:table-cell width="50px" padding="1mm" border-width="0.1mm" border-style="solid">
							   				<fo:block>
							   					<xsl:value-of select="./element_3[1]"></xsl:value-of>
							   				</fo:block>
							      		</fo:table-cell>
							      		
							      		<fo:table-cell width="300px" padding="1mm" border-width="0.1mm" border-style="solid">
							   				<fo:block>
							   					<xsl:value-of select="./element_3[2]"></xsl:value-of>
							   				</fo:block>
							      		</fo:table-cell>
							      		
							      		<fo:table-cell width="60px" padding="1mm" border-width="0.1mm" border-style="solid">
							   				<fo:block>
							   					<xsl:value-of select="./element_3[3]"></xsl:value-of>
							   				</fo:block>
							      		</fo:table-cell>
							      		
							      		<fo:table-cell width="60px" padding="1mm" border-width="0.1mm" border-style="solid">
							   				<fo:block>
							   					<xsl:value-of select="./element_3[4]"></xsl:value-of>
							   				</fo:block>
							      		</fo:table-cell>
							      		
							      		<fo:table-cell width="60px" padding="1mm" border-width="0.1mm" border-style="solid">
							   				<fo:block>
							   					<xsl:value-of select="./element_3[5]"></xsl:value-of>
							   				</fo:block>
							      		</fo:table-cell>
							      		
							    	</fo:table-row>
							  	</fo:table-header>
								<fo:table-body>
									<xsl:for-each select="./element_1">								
										<fo:table-row>																		
											<fo:table-cell width="50px" xsl:use-attribute-sets="myBorder" text-align="center">
												<fo:block>
													<xsl:value-of select="./element_1_2/element_1_2_1"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell width="300px" xsl:use-attribute-sets="myBorder" padding="1mm">
												<fo:block>
													<xsl:value-of select="./element_1_1"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell width="60px" xsl:use-attribute-sets="myBorder" text-align="center">
												<fo:block>
													<xsl:value-of select="./element_1_2/element_1_2_2"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell width="60px" xsl:use-attribute-sets="myBorder" text-align="center">
												<fo:block>
													<xsl:value-of select="./element_1_2/element_1_2_3"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
											
											<fo:table-cell width="60px" xsl:use-attribute-sets="myBorder" text-align="center">
												<fo:block>
													<xsl:value-of select="./element_1_2/element_1_2_4"></xsl:value-of>
												</fo:block>
											</fo:table-cell>
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
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>