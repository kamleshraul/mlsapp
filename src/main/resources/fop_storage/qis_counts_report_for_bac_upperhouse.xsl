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
	                  	margin-top="1.8cm" margin-bottom="1.5cm"
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
			       <!-- content as per report -->	
			       <fo:block font-family="Mangal" font-size="10.5px">
			       		<fo:block text-align="center" font-size="12px" font-weight="bold" text-decoration="underline">			       			
			       			<xsl:apply-templates select="element_1[1]/element_1_3"/>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block text-align="center" font-size="11.5px" font-weight="bold" text-decoration="underline">
			       			<xsl:variable name="groupHeader" select="element_1[1]/element_1_2"></xsl:variable>			       			
			       			<xsl:if test="contains($groupHeader, 'गट')">
			       				<fo:block font-size="14px"><xsl:value-of select="$groupHeader"></xsl:value-of></fo:block>	
			       				<fo:block font-size="6px">&#160;</fo:block>
			       			</xsl:if>
			       			<fo:block>विवरण क्र.१</fo:block>		
			       			<fo:block font-size="6px" text-decoration="none">&#160;</fo:block>	       			
			       			<fo:block>तारांकित प्रश्न</fo:block>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="12.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2" border-left="0.5pt solid black" border-right="0.5pt solid black">
										<fo:block>
											<fo:table table-layout="fixed" width="100%">
												<fo:table-column column-number="1" column-width="1cm" />
												<fo:table-column column-number="2" column-width="9.3cm" />
												<fo:table-column column-number="3" column-width="2.2cm" />
												<fo:table-body>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">१.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">एकूण आलेल्या तारांकित प्रश्नांच्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_4=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_4"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">२.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">कार्यवाहीसाठी घेतलेले तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_5=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_5"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">३.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">स्वीकृत झालेले तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_23='Post_Ballot'">
																		<xsl:choose>
																			<xsl:when test="element_1[1]/element_1_6=''">--</xsl:when>
																			<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_6"/></xsl:otherwise>
																		</xsl:choose>
																	</xsl:when>
																	<xsl:otherwise>
																		<xsl:choose>
																			<xsl:when test="element_1[1]/element_1_13=''">--</xsl:when>
																			<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_13"/></xsl:otherwise>
																		</xsl:choose>
																	</xsl:otherwise>
																</xsl:choose>																
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<!-- <fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">सभागृहात तोंडी उत्तरीत झालेले तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_22=''">- -</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_22"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row> -->
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">तारांकित प्रश्नातील अतारांकित करण्यात आलेले प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_11=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_11"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">५.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">अस्वीकृत केलेले तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_7=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_7"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">६.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">विभागास/मा.सदस्यांना खुलासा विचारलेले तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_9=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_9"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>		                        	
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">७.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">संम्मिलित तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_8=''">--</xsl:when>
																	
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_8"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<xsl:if test="element_1[1]/element_1_23='Post_Ballot'">
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">८.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">तारांकित स्वीकृत प्रश्न व्यपगत</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_13=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_13"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													</xsl:if>											
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
														<xsl:choose>
															<xsl:when test="element_1[1]/element_1_23='Post_Ballot'">
																<fo:block margin-left="0.3cm">९.</fo:block>
															</xsl:when>
															<xsl:otherwise>
																<fo:block margin-left="0.3cm">८.</fo:block>
															</xsl:otherwise>
														</xsl:choose>															
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">इतर कार्यवाहीतील उरलेले प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_24=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_24"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
														<xsl:choose>
															<xsl:when test="element_1[1]/element_1_23='Post_Ballot'">
																<fo:block margin-left="0.3cm">१०.</fo:block>
															</xsl:when>
															<xsl:otherwise>
																<fo:block margin-left="0.3cm">९.</fo:block>
															</xsl:otherwise>
														</xsl:choose>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">निर्णयासाठी सादर करावयाचे राहिलेले जादा तारांकित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[1]/element_1_12=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[1]/element_1_12"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
												</fo:table-body>
											</fo:table>			       			       			
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
				      	</fo:block>		       		
			       		
			       		<fo:block font-size="10.5px">&#160;</fo:block>
			       		<fo:block text-align="center" font-size="11.5px" font-weight="bold" text-decoration="underline">
			       			<fo:block>विवरण क्र.२</fo:block>		
			       			<fo:block font-size="6px" text-decoration="none">&#160;</fo:block>	       			
			       			<fo:block>अल्पसूचना प्रश्न</fo:block>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="12.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2" border-left="0.5pt solid black" border-right="0.5pt solid black">
										<fo:block>
											<fo:table table-layout="fixed" width="100%">
												<fo:table-column column-number="1" column-width="1cm" />
												<fo:table-column column-number="2" column-width="9.3cm" />
												<fo:table-column column-number="3" column-width="2.2cm" />
												<fo:table-body>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">१.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">प्राप्त झालेले एकूण अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_4=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_4"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">२.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">स्वीकृत झालेले अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_6=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_6"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">३.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">उत्तरीत झालेले अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_10=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_10"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">अस्वीकृत अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_7=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_7"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">५.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">व.आ.अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_9=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_9"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">६.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">संम्मिलित अल्पसूचना प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[2]/element_1_8=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[2]/element_1_8"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>		                        	
												</fo:table-body>
											</fo:table>			       			       			
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
				      	</fo:block>
			       		
			       		<fo:block font-size="10.5px">&#160;</fo:block>
			       		<fo:block text-align="center" font-size="11.5px" font-weight="bold" text-decoration="underline">
			       			<fo:block>विवरण क्र.३</fo:block>		
			       			<fo:block font-size="6px" text-decoration="none">&#160;</fo:block>	       			
			       			<fo:block>तारांकित प्रश्नाच्या उत्तरातून उद्भवलेल्या अर्धा-तास चर्चेच्या सूचना</fo:block>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="12.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2" border-left="0.5pt solid black" border-right="0.5pt solid black">
										<fo:block>
											<fo:table table-layout="fixed" width="100%">
												<fo:table-column column-number="1" column-width="1cm" />
												<fo:table-column column-number="2" column-width="9.3cm" />
												<fo:table-column column-number="3" column-width="2.2cm" />
												<fo:table-body>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">१.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">प्राप्त झालेल्या अर्धा-तास चर्चेच्या एकूण सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_4=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_4"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>		                        	
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">२.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">मान्य झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_6=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_6"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">३.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">चर्चा झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_16=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_16"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>													
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">बॅलेट मध्ये आलेल्या पण चर्चा न झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_19=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_19"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">५.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">अस्वीकृत झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_7=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_7"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">६.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">विभागास खुलासा विचारण्यात आलेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_9=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_9"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">७.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">प्रलंबित सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[3]/element_1_20=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[3]/element_1_20"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>													
												</fo:table-body>
											</fo:table>			       			       			
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
				      	</fo:block>
			       		
			       		<fo:block font-size="10.5px">&#160;</fo:block>
			       		<fo:block text-align="center" font-size="11.5px" font-weight="bold" text-decoration="underline">
			       			<fo:block>विवरण क्र.४</fo:block>	
			       			<fo:block font-size="6px" text-decoration="none">&#160;</fo:block>		       			
			       			<fo:block>सार्वजनिक बाबीतून उद्भवलेल्या अर्धा-तास चर्चेच्या सूचना</fo:block>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="12.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2" border-left="0.5pt solid black" border-right="0.5pt solid black">
										<fo:block>
											<fo:table table-layout="fixed" width="100%">
												<fo:table-column column-number="1" column-width="1cm" />
												<fo:table-column column-number="2" column-width="9.3cm" />
												<fo:table-column column-number="3" column-width="2.2cm" />
												<fo:table-body>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">१.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">प्राप्त झालेल्या अर्धा-तास चर्चेच्या एकूण सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_4=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_4"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">२.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">मान्य झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_6=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_6"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">३.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">चर्चा झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_16=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_16"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">बॅलेट मध्ये आलेल्या पण चर्चा न झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_19=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_19"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">५.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">अस्वीकृत झालेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_7=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_7"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">६.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">विभागास खुलासा विचारण्यात आलेल्या सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_9=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_9"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">७.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">प्रलंबित ठेवण्यात आलेली सूचना</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[4]/element_1_20=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[4]/element_1_20"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>		                        	
												</fo:table-body>
											</fo:table>			       			       			
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
				      	</fo:block>
			       		
			       		<fo:block font-size="10.5px">&#160;</fo:block>
			       		<fo:block text-align="center" font-size="11.5px" font-weight="bold" text-decoration="underline">
			       			<fo:block>विवरण क्र.५</fo:block>			
			       			<fo:block font-size="6px" text-decoration="none">&#160;</fo:block>       			
			       			<fo:block>मूळ अतारांकित प्रश्न</fo:block>
			       		</fo:block>
			       		<fo:block font-size="6px">&#160;</fo:block>
			       		<fo:block>
				       		<fo:table table-layout="fixed" width="100%">
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-column column-width="12.5cm"/>
						        <fo:table-column column-width="proportional-column-width(1)"/>
						        <fo:table-body>
								<fo:table-row>
									<fo:table-cell column-number="1">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="2" border-left="0.5pt solid black" border-right="0.5pt solid black">
										<fo:block>
											<fo:table table-layout="fixed" width="100%">
												<fo:table-column column-number="1" column-width="1cm" />
												<fo:table-column column-number="2" column-width="9.3cm" />
												<fo:table-column column-number="3" column-width="2.2cm" />
												<fo:table-body>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">१.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">या सत्रात आलेल्या मूळ अतारांकित प्रश्न सूचनांची संख्या</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_4=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_4"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">२.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">स्वीकृत झालेले प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_6=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_6"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">३.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">अस्वीकृत प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_7=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_7"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">४.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">विभागास खुलासा विचारण्यात आलेले प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_9=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_9"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">५.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">संम्मिलित प्रश्न</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_8=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_8"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>
													<fo:table-row border="solid 0.1mm black">
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm">६.</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-left="0.3cm" margin-right="0.3cm">उत्तरीत झालेल्या प्रश्नांची संख्या (मागील सत्रातील स्वीकृत प्रश्नासह)</fo:block>
														</fo:table-cell>
														<fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
															<fo:block margin-right="0.5cm" text-align="right">
																<xsl:choose>
																	<xsl:when test="element_1[5]/element_1_10=''">--</xsl:when>
																	<xsl:otherwise><xsl:value-of select="element_1[5]/element_1_10"/></xsl:otherwise>
																</xsl:choose>
															</fo:block>
														</fo:table-cell>
													</fo:table-row>		                        	
												</fo:table-body>
											</fo:table>			       			       			
										</fo:block>
									</fo:table-cell>
									<fo:table-cell column-number="3">
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
								</fo:table-row>
						        </fo:table-body>
					      	</fo:table>
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