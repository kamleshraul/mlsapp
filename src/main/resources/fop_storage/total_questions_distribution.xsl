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
				<fo:simple-page-master master-name="pdf_first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="1.5cm" margin-right="1.25cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
  				<fo:simple-page-master master-name="word_first"
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
		              master-reference="{$masterReference}"
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
	        
	        <fo:page-sequence master-reference="{$masterReference}" id="DocumentBody">	        	
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
							महाराष्ट्र 
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									विधानसभेच्या
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									विधानपरिषदेच्या
								</xsl:when>							
							</xsl:choose>
							 सन <xsl:value-of select="sessionYear"/> मधील <xsl:value-of select="sessionCountName"/> (<xsl:value-of select="sessionTypeName"/>) अधिवेशनात उत्तरीत होणाऱ्या <xsl:value-of select="questionTypeName"/> सूचनांपैकी <xsl:value-of select="questionSubmissionDate"/> रोजी 
							<xsl:choose>
								<xsl:when test="dayTime='0'">
									सकाळी
								</xsl:when>
								<xsl:when test="dayTime='1'">
									दुपारी
								</xsl:when>							
							</xsl:choose>							
							<xsl:value-of select="questionSubmissionStartTime"/> ते <xsl:value-of select="questionSubmissionEndTime"/> या वेळेत घेण्यात आलेल्या प्रश्न सूचनांचा तपशील
						</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:table table-layout="fixed" width="100%">
							<fo:table-column column-number="1" column-width="1.5cm" />
	                        <fo:table-column column-number="2" column-width="5cm" />
	                        <fo:table-column column-number="3" column-width="2cm" />
	                        <fo:table-column column-number="4" column-width="2cm" />
	                        <fo:table-column column-number="5" column-width="2cm" />
	                        <fo:table-column column-number="6" column-width="2.5cm" />
	                        <fo:table-column column-number="7" column-width="2cm" />	                        
							<fo:table-header>
								<fo:table-row>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">अ.क्र.</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center">
										<fo:block font-weight="bold" margin-left="0.2cm">सदस्य</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">तारांकित स्वीकृत</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">अतारांकित स्वीकृत</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">अस्वीकृत</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">वस्तुस्थिती/ सदस्य खुलासा</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block font-weight="bold">एकूण प्रश्न सूचना</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>
								<xsl:for-each select="./questionDistributions/questionDistribution">
								<fo:table-row>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block><xsl:value-of select="sNo"/></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid">
										<fo:block margin-left="0.2cm">
											<xsl:value-of select="member"/>,
											<xsl:choose>
												<xsl:when test="houseType='lowerhouse'"><fo:block/>वि.स.स.</xsl:when>
												<xsl:when test="houseType='upperhouse'"><fo:block/>वि.प.स.</xsl:when>
											</xsl:choose>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">		
									<fo:block>			
									<xsl:variable name="countForStatus">				
									<xsl:for-each select="./distributions">										
										<xsl:choose>
											<xsl:when test="currentDeviceType='questions_starred' and statusTypeType='question_final_admission'"><fo:block/>
												<xsl:value-of select="count"/>																					
											</xsl:when>																					
										</xsl:choose>																		
									</xsl:for-each>	
									</xsl:variable>		
									<xsl:choose>
										<xsl:when test="$countForStatus!=''"><fo:block/>
											<xsl:value-of select="$countForStatus"/>																					
										</xsl:when>		
										<xsl:otherwise>-</xsl:otherwise>																			
									</xsl:choose>
									</fo:block>									
									</fo:table-cell>															
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">		
									<fo:block>			
									<xsl:variable name="countForStatus">				
									<xsl:for-each select="./distributions">										
										<xsl:choose>
											<xsl:when test="currentDeviceType='questions_unstarred' and statusTypeType='question_unstarred_final_admission'"><fo:block/>
												<xsl:value-of select="count"/>																					
											</xsl:when>																					
										</xsl:choose>																		
									</xsl:for-each>	
									</xsl:variable>		
									<xsl:choose>
										<xsl:when test="$countForStatus!=''"><fo:block/>
											<xsl:value-of select="$countForStatus"/>																					
										</xsl:when>		
										<xsl:otherwise>-</xsl:otherwise>																			
									</xsl:choose>
									</fo:block>									
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">		
									<fo:block>			
									<xsl:variable name="countForStatus">				
									<xsl:for-each select="./distributions">										
										<xsl:choose>
											<xsl:when test="currentDeviceType='questions_starred' and statusTypeType='question_final_rejection'"><fo:block/>
												<xsl:value-of select="count"/>																					
											</xsl:when>																					
										</xsl:choose>																		
									</xsl:for-each>	
									</xsl:variable>		
									<xsl:choose>
										<xsl:when test="$countForStatus!=''"><fo:block/>
											<xsl:value-of select="$countForStatus"/>																					
										</xsl:when>		
										<xsl:otherwise>-</xsl:otherwise>																			
									</xsl:choose>
									</fo:block>									
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">		
									<fo:block>			
									<xsl:variable name="countForStatus">				
									<xsl:for-each select="./distributions">										
										<xsl:choose>
											<xsl:when test="statusTypeType='clarification'"><fo:block/>
												<xsl:value-of select="count"/>																					
											</xsl:when>																					
										</xsl:choose>																		
									</xsl:for-each>	
									</xsl:variable>		
									<xsl:choose>
										<xsl:when test="$countForStatus!=''"><fo:block/>
											<xsl:value-of select="$countForStatus"/>																					
										</xsl:when>		
										<xsl:otherwise>-</xsl:otherwise>																			
									</xsl:choose>
									</fo:block>									
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center" display-align="center">
										<fo:block><xsl:value-of select="totalCount"/></fo:block>
									</fo:table-cell>
								</fo:table-row>
								</xsl:for-each>
								<fo:table-row>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center">
										<fo:block></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center" height="1cm">
										<fo:block font-weight="bold">एकूण</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="totalAdmittedQuestions"/></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="totalConvertToUnstarredAndAdmitQuestions"/></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="totalRejectedQuestions"/></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="totalClarificationQuestions"/></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="totalQuestions"/></fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell border-width="0.5pt" border-style="solid" text-align="center">
										<fo:block></fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center" height="1cm">
										<fo:block font-weight="bold">सरासरी</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="percentTotalAdmittedQuestions"/>%</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="percentTotalConvertToUnstarredAndAdmitQuestions"/>%</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="percentTotalRejectedQuestions"/>%</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block font-weight="bold"><xsl:value-of select="percentTotalClarificationQuestions"/>%</fo:block>
									</fo:table-cell>
									<fo:table-cell border-width="0.5pt" border-style="solid" display-align="center" text-align="center">
										<fo:block></fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>