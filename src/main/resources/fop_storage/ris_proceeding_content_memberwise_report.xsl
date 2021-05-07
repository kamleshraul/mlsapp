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
    
    <!-- declares common templates as they will be applied in all report stylesheets & can be overridden -->
  
    
    


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
			      	<fo:region-before region-name="rb-common" extent="2cm"/>
			      	<fo:region-after region-name="ra-common" extent="1.5cm"/>
			     </fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-common" extent="2cm"/>
			      	<fo:region-after region-name="ra-common" extent="1.5cm"/>
  				</fo:simple-page-master>
	   				
  				<fo:page-sequence-master master-name="simple" >
		          <fo:repeatable-page-master-alternatives>
		            <fo:conditional-page-master-reference 
		              master-reference="first" 
		              page-position="first" />
		            <fo:conditional-page-master-reference 
		              master-reference="others" 
		              odd-or-even="even"/>
		            <fo:conditional-page-master-reference 
		              master-reference="others" 
		              odd-or-even="odd"/>
		          </fo:repeatable-page-master-alternatives>
		        </fo:page-sequence-master>		
			</fo:layout-master-set>		
			<xsl:variable name="outputFormat">
				<xsl:choose>
				<xsl:when test="$formatOut='application/pdf'">
					<xsl:value-of select="'simple'"></xsl:value-of>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'first'"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>	
			</xsl:variable>
			
		
	        <xsl:for-each select="element_1">
	        <fo:page-sequence master-reference="{$outputFormat}" id="DocumentBody" initial-page-number="1"  force-page-count="no-force">
	         	<!-- header -->
	        	<fo:static-content flow-name="rb-common">
			   		<fo:block text-align="center" font-family="{$font}">
						 	         				        		
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
			       <!-- content as per report -->
			    <!--   <fo:block font-family="{$font}" font-size="16px">
			      		<xsl:choose>
						<xsl:when test="element_1_2!='' and  element_1_3!=''">
							<fo:block text-align="center">
								पृ. शी :<xsl:apply-templates select="element_1_2"></xsl:apply-templates>
							</fo:block>
							<fo:block text-align="center">
								मु. शी :<xsl:apply-templates select="element_1_3"></xsl:apply-templates>
							</fo:block>
						</xsl:when>
						<xsl:when test="element_1_2!='' and element_1_3=''">
							<fo:block text-align="center">
								पृ .शी/मु. शी  :<xsl:apply-templates select="element_1_2"></xsl:apply-templates>
							</fo:block>
						</xsl:when>
						<xsl:when test="element_1_3!='' and  element_1_2=''">
							<fo:block text-align="center">
								पृ .शी/मु. शी  :<xsl:apply-templates select="element_1_3"></xsl:apply-templates>
							</fo:block>
						</xsl:when>
						</xsl:choose>
			     		<fo:block text-align="justify">
			     			<xsl:apply-templates select="element_1_1"></xsl:apply-templates>
			     		</fo:block>
				       <fo:block></fo:block> -->
				       
				       <fo:block font-family="{$font}" font-size="16px">
			      		 <fo:table table-layout="fixed" width="100%" padding="0" >
			      		 	  <fo:table-header>
								<fo:table-row>
									<fo:table-cell text-align="start">
										<fo:block>
											<xsl:value-of select="element_1_15"></xsl:value-of>
										</fo:block>										
									</fo:table-cell>
									<fo:table-cell text-align="center">
										 <fo:block>
											(असुधारित प्रत/ प्रसिद्धीसाठी नाही)
										</fo:block>										
									</fo:table-cell>
									<fo:table-cell text-align="end" >
										<fo:block>
											<xsl:value-of select="element_1_14"></xsl:value-of>-<fo:page-number/>
										</fo:block>										
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell text-align="start">
										<fo:block>
											<xsl:value-of select="element_1_17"></xsl:value-of>
										</fo:block>										
									</fo:table-cell>
									<fo:table-cell text-align="center">
										<fo:block></fo:block>	
									</fo:table-cell>
									<fo:table-cell text-align="end" >
										<fo:block>
											<xsl:value-of select="element_1_16"></xsl:value-of>
										</fo:block>										
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>	
								<fo:table-row>
									<fo:table-cell display-align="center" text-align="justify" number-columns-spanned="3">
											<fo:block>
												<xsl:choose>
													<xsl:when test="element_1_19!=''">
													<fo:block text-align="center" font-weight="bold">
														(उपाध्यक्षस्थानी  माननीय <xsl:apply-templates select="element_1_19"></xsl:apply-templates> &#160; <xsl:apply-templates select="element_1_20"></xsl:apply-templates>)
													</fo:block>
													<fo:block>&#160;</fo:block>
													</xsl:when>
													<xsl:otherwise>
														<fo:block>&#160;</fo:block>
													</xsl:otherwise>
												</xsl:choose>
												<xsl:choose>
													<xsl:when test="element_1_2!='' and  element_1_3!=''">
													<fo:block text-align="center">
													<fo:inline font-weight="bold">पृ. शी :</fo:inline><xsl:apply-templates select="element_1_2"></xsl:apply-templates>
													<fo:inline font-weight="bold">मु. शी :</fo:inline><xsl:apply-templates select="element_1_3"></xsl:apply-templates>
													</fo:block>
													<fo:block>&#160;</fo:block>	
													</xsl:when>
													<xsl:when test="element_1_2!='' and element_1_3=''">
													<fo:block text-align="center">
														<fo:inline font-weight="bold">पृ .शी/मु. शी </fo:inline> :<xsl:apply-templates select="element_1_2"></xsl:apply-templates>
													</fo:block>
													<fo:block>&#160;</fo:block>	
													</xsl:when>
													<xsl:when test="element_1_3!='' and  element_1_2=''">
													<fo:block text-align="center">
														<fo:inline font-weight="bold">पृ .शी/मु. शी </fo:inline> :<xsl:apply-templates select="element_1_3"></xsl:apply-templates>
													</fo:block>
													<fo:block>&#160;</fo:block>	
													</xsl:when>
													
												</xsl:choose>
												<xsl:apply-templates select="element_1_1"></xsl:apply-templates>
												<!-- <xsl:value-of select="element_1_1"></xsl:value-of> -->
											</fo:block>	
											<fo:block>&#160;</fo:block>									
									</fo:table-cell>
								</fo:table-row>	
							</fo:table-body>
						 </fo:table>
					 </fo:block>
			       </fo:flow>
			 
	           <xsl:template match="element_1/element_1_1">	
		<!-- <xsl:call-template name="br_template"></xsl:call-template> -->
    			<xsl:apply-templates/>
  				</xsl:template>
	        </fo:page-sequence>
	       </xsl:for-each>
	    </fo:root>
	    
    </xsl:template>      
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>