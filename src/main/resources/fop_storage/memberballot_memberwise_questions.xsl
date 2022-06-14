<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="MemberwiseQuestionData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>
    
    <!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
   <xsl:template match="MemberwiseQuestionData">

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
	            		<fo:block font-weight="bold">
	            			<fo:block margin-left="2cm">            				     				
								<xsl:value-of select="submissionDate"/> रोजी स्विकारण्यात आलेल्या तारांकित प्रश्नांचा तपशिल
							</fo:block>
							<fo:block>&#160;</fo:block>							
							<fo:block margin-left="0.5cm">	
	            				<fo:table border-collapse="collapse" table-layout="fixed" width="65%">
	            					<fo:table-body>
	            						<fo:table-row>
	            							<fo:table-cell><fo:block>तारांकित स्वीकृत प्रश्न</fo:block></fo:table-cell>
	            							<xsl:choose>
	            								<xsl:when test="admittedQuestionCount != '०'">
	            									<fo:table-cell><fo:block><xsl:value-of select="admittedQuestionCount"/></fo:block></fo:table-cell>
	            								</xsl:when>
	            								<xsl:otherwise>
	            									<fo:table-cell><fo:block>-</fo:block></fo:table-cell>
	            								</xsl:otherwise>
	            							</xsl:choose>	            							
	            						</fo:table-row>
	            						<fo:table-row>
	            							<fo:table-cell><fo:block>अतारांकित स्वीकृत प्रश्न</fo:block></fo:table-cell>
	            							<xsl:choose>
	            								<xsl:when test="convertedToUnstarredAndAdmittedQuestionCount != '०'">
	            									<fo:table-cell><fo:block><xsl:value-of select="convertedToUnstarredAndAdmittedQuestionCount"/></fo:block></fo:table-cell>
	            								</xsl:when>
	            								<xsl:otherwise>
	            									<fo:table-cell><fo:block>-</fo:block></fo:table-cell>
	            								</xsl:otherwise>
	            							</xsl:choose>
	            						</fo:table-row>	            						
	            						<fo:table-row>
		           							<fo:table-cell><fo:block>वस्तुस्थिती/सदस्य खुलासा</fo:block></fo:table-cell>
		           							<xsl:choose>
	            								<xsl:when test="clarificationQuestionCount != '०'">
	            									<fo:table-cell><fo:block><xsl:value-of select="clarificationQuestionCount"/></fo:block></fo:table-cell>
	            								</xsl:when>
	            								<xsl:otherwise>
	            									<fo:table-cell><fo:block>-</fo:block></fo:table-cell>
	            								</xsl:otherwise>
	            							</xsl:choose>		           							
		           						</fo:table-row>
		           						<fo:table-row>
	            							<fo:table-cell><fo:block>अस्वीकृत प्रश्न</fo:block></fo:table-cell>
	            							<xsl:choose>
	            								<xsl:when test="rejectedQuestionCount != '०'">
	            									<fo:table-cell><fo:block><xsl:value-of select="rejectedQuestionCount"/></fo:block></fo:table-cell>
	            								</xsl:when>
	            								<xsl:otherwise>
	            									<fo:table-cell><fo:block>-</fo:block></fo:table-cell>
	            								</xsl:otherwise>
	            							</xsl:choose>
	            						</fo:table-row>			            			
									</fo:table-body>
							</fo:table>          
	            			</fo:block>
							<fo:block>&#160;</fo:block>
													
							<xsl:if test="count(./groupList)=1">
								<xsl:if test="count(./groupList/group)>0">
									<xsl:for-each select="./groupList/group">
										<xsl:if test="hasQuestionsForGivenMember='true'">
											<xsl:variable name="currentGroupNumber" select="number"/>
											<fo:block break-after="page">
											<fo:block text-align="center" font-weight="bold">
												<fo:block font-size="18px">गट-<xsl:value-of select="formattedNumber"/></fo:block>
												<fo:block font-size="17px" text-decoration="underline">
													<xsl:value-of select="../../member"/>
													<xsl:choose>
														<xsl:when test="../../houseType='lowerhouse'">, वि.स.स.</xsl:when>
														<xsl:when test="../../houseType='upperhouse'">, वि.प.स.</xsl:when>
													</xsl:choose>
												</fo:block>
											</fo:block>
											<fo:block font-size="6px">&#160;</fo:block>
											<fo:block font-weight="bold">
												<fo:table table-layout="fixed" width="100%">
													<fo:table-column column-number="1" column-width="11cm" />
	                        						<fo:table-column column-number="2" column-width="7cm" />
													<fo:table-header>
														<fo:table-row>
														<fo:table-cell border-width="0.5pt" border-style="solid">
															<fo:block text-align="center" font-size="15px">गटामध्ये समाविष्ट असलेले मंत्री</fo:block>
														</fo:table-cell>
														<fo:table-cell border-width="0.5pt" border-style="solid">
															<fo:block font-size="15px" margin-left="0.2cm">उत्तराचा दिनांक</fo:block>
														</fo:table-cell>
														</fo:table-row>														
													</fo:table-header>
													<fo:table-body>
														<fo:table-row display-align="center">
														<fo:table-cell border-width="0.5pt" border-style="solid" margin-left="0.2cm" margin-right="1.0cm">
															<fo:block font-size="14px">
																<fo:block font-size="6px">&#160;</fo:block>
																<xsl:for-each select="./ministries">	
																	<fo:table table-layout="fixed" width="100%">
																		<fo:table-column column-number="1" column-width="2cm" />
	                        											<fo:table-column column-number="2" column-width="8cm" />
																		<fo:table-body>
																			<fo:table-row>
																				<fo:table-cell display-align="before">
																					<fo:block><xsl:value-of select="formattedNumber"/></fo:block>
																				</fo:table-cell>
																				<fo:table-cell display-align="before">
																					<fo:block><xsl:value-of select="name"/></fo:block>
																				</fo:table-cell>
																			</fo:table-row>
																		</fo:table-body>
																	</fo:table>																
																	
																</xsl:for-each>
																<fo:block font-size="10px">&#160;</fo:block>
															</fo:block>
														</fo:table-cell>
														<fo:table-cell border-width="0.5pt" border-style="solid">
															<fo:block font-size="14px" margin-left="0.2cm">
																<xsl:for-each select="./answeringDates">																	
																	<fo:block>
																		<xsl:value-of select="name"/>																	 
																	</fo:block>
																</xsl:for-each>
															</fo:block>
														</fo:table-cell>
														</fo:table-row>														
													</fo:table-body>
												</fo:table>
											</fo:block>	
											
											<fo:block font-size="5px">&#160;</fo:block>
																															
											<xsl:if test="count(./starredQuestionVOs)>0">				
												<fo:block font-size="15px" font-weight="bold">
													तारांकित स्वीकृत प्रश्न
												</fo:block>	
												<fo:block font-size="5px">&#160;</fo:block>	
												<fo:block>
												<fo:table table-layout="fixed" width="100%">
													<fo:table-column column-number="1" column-width="1.5cm" />
                   									<fo:table-column column-number="2" column-width="2.5cm" />
                   									<fo:table-column column-number="3" column-width="14cm" />
                   									<!-- <fo:table-column column-number="4" column-width="5cm" /> -->
													<fo:table-header>
														<fo:table-row>
															<fo:table-cell display-align="center" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">अ.क्र.</fo:block>
															</fo:table-cell>
															<fo:table-cell text-align="center" display-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">प्रश्न क्रमांक</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">विषय</fo:block>
															</fo:table-cell>
															<!-- <fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="13px">जोडणी विषयक माहिती</fo:block>
															</fo:table-cell> -->
														</fo:table-row>
													</fo:table-header>
													<fo:table-body>
														<xsl:for-each select="./starredQuestionVOs">													
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="sno"/></fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="before" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionNumber"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionSubject"/></fo:block>
															</fo:table-cell>
															<!-- <fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="clubbingInformation"/></fo:block>
															</fo:table-cell> -->
														</fo:table-row>																																					
														</xsl:for-each>
													</fo:table-body>													
												</fo:table>	
												</fo:block>
												<fo:block font-size="5px">&#160;</fo:block>																														
											</xsl:if>										
											
											<xsl:if test="count(./unstarredQuestionVOs)>0">				
												<fo:block font-size="15px" font-weight="bold">
													अतारांकित स्वीकृत प्रश्न
												</fo:block>	
												<fo:block font-size="5px">&#160;</fo:block>	
												<fo:block>
												<fo:table table-layout="fixed" width="100%">
													<fo:table-column column-number="1" column-width="1.5cm" />
                   									<fo:table-column column-number="2" column-width="2.5cm" />
                   									<fo:table-column column-number="3" column-width="14cm" />
                   									<!-- <fo:table-column column-number="4" column-width="5cm" /> -->
													<fo:table-header>
														<fo:table-row>
															<fo:table-cell display-align="center" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">अ.क्र.</fo:block>
															</fo:table-cell>
															<fo:table-cell text-align="center" display-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">प्रश्न क्रमांक</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">विषय</fo:block>
															</fo:table-cell>
															<!-- <fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="13px">जोडणी विषयक माहिती</fo:block>
															</fo:table-cell> -->
														</fo:table-row>
													</fo:table-header>
													<fo:table-body>
														<xsl:for-each select="./unstarredQuestionVOs">													
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="sno"/></fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="before" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionNumber"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionSubject"/></fo:block>
															</fo:table-cell>
															<!-- <fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="clubbingInformation"/></fo:block>
															</fo:table-cell> -->
														</fo:table-row>																																					
														</xsl:for-each>
													</fo:table-body>													
												</fo:table>	
												</fo:block>
												<fo:block font-size="5px">&#160;</fo:block>																															
											</xsl:if>											
											
											<xsl:if test="count(./rejectedQuestionVOs)>0">				
												<fo:block font-size="15px" font-weight="bold">
													अस्वीकृत प्रश्न
												</fo:block>	
												<fo:block font-size="5px">&#160;</fo:block>	
												<fo:block>
												<fo:table table-layout="fixed" width="100%">
													<fo:table-column column-number="1" column-width="1.5cm" />
                   									<fo:table-column column-number="2" column-width="2.5cm" />
                   									<fo:table-column column-number="3" column-width="9cm" />
                   									<fo:table-column column-number="4" column-width="5cm" />
													<fo:table-header>
														<fo:table-row>
															<fo:table-cell display-align="center" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">अ.क्र.</fo:block>
															</fo:table-cell>
															<fo:table-cell text-align="center" display-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">प्रश्न क्रमांक</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">विषय</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">कारणे</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-header>
													<fo:table-body>
														<xsl:for-each select="./rejectedQuestionVOs">													
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="sno"/></fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="before" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionNumber"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionSubject"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:apply-templates select="questionReason"/></fo:block>
															</fo:table-cell>
														</fo:table-row>																																					
														</xsl:for-each>
													</fo:table-body>													
												</fo:table>	
												</fo:block>
												<fo:block font-size="5px">&#160;</fo:block>																														
											</xsl:if>
											
											<xsl:if test="count(./clarificationQuestionVOs)>0">				
												<fo:block font-size="15px" font-weight="bold">
													वस्तुस्थिती/सदस्य खुलासा प्रश्न
												</fo:block>	
												<fo:block font-size="5px">&#160;</fo:block>	
												<fo:block>
												<fo:table table-layout="fixed" width="100%">
													<fo:table-column column-number="1" column-width="1.5cm" />
                   									<fo:table-column column-number="2" column-width="2.5cm" />
                   									<fo:table-column column-number="3" column-width="9cm" />
                   									<fo:table-column column-number="4" column-width="5cm" />
													<fo:table-header>
														<fo:table-row>
															<fo:table-cell display-align="center" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">अ.क्र.</fo:block>
															</fo:table-cell>
															<fo:table-cell text-align="center" display-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">प्रश्न क्रमांक</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">विषय</fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="center" text-align="center" border-width="0.5pt" border-style="solid" padding="0.1cm">
																<fo:block font-weight="bold" font-size="15px">सादर प्रकार</fo:block>
															</fo:table-cell>
														</fo:table-row>
													</fo:table-header>
													<fo:table-body>
														<xsl:for-each select="./clarificationQuestionVOs">													
														<fo:table-row keep-together.within-page="always">
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="sno"/></fo:block>
															</fo:table-cell>
															<fo:table-cell display-align="before" margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionNumber"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="questionSubject"/></fo:block>
															</fo:table-cell>
															<fo:table-cell margin-left="0.2cm" border-width="0.5pt" border-style="solid" padding="0.1cm" padding-right="0.2cm">
																<fo:block font-weight="normal"><xsl:value-of select="statusType"/></fo:block>
															</fo:table-cell>
														</fo:table-row>																																					
														</xsl:for-each>
													</fo:table-body>													
												</fo:table>	
												</fo:block>																															
											</xsl:if>										
										</fo:block>
										</xsl:if>													
									</xsl:for-each>									
								</xsl:if>								
							</xsl:if>							
							<!-- <hr width="5cm"></hr> -->
	            		</fo:block>		
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>