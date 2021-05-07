<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"  
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="ProceedingData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>

	<!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
    <!-- declares common templates as they will be applied in all report stylesheets & can be overridden -->
  
    
    


    <xsl:template match="ProceedingData">
   	 
   	 

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="1.77cm" margin-bottom="1.77cm"
	                  	margin-left="3.3cm" margin-right="3.3cm">
	               	<fo:region-body margin="0cm"/>
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
			
			
	       <xsl:for-each select="./slotList/slot" > 
	       
	        <fo:page-sequence master-reference="{$outputFormat}" id="DocumentBody" initial-page-number="1"  force-page-count="no-force">
	         	<!-- header -->
	         	
	        	<fo:static-content flow-name="rb-common">
	        		<fo:block font-family="{$font}" font-size="14px">
	        		<fo:table table-layout="fixed" width="100%" padding="0">
      					<fo:table-body >
      						<fo:table-row >
								<fo:table-cell text-align="start">
									<fo:block>
										<xsl:value-of select="startDate"></xsl:value-of>
									</fo:block>										
									</fo:table-cell>
									<fo:table-cell text-align="center">
										 <fo:block>
											(असुधारित प्रत/ प्रसिद्धीसाठी नाही)
										 </fo:block>										
									</fo:table-cell>
									<fo:table-cell text-align="end" >
										<fo:block>
											<xsl:value-of select="name"></xsl:value-of>-<fo:page-number/>
										</fo:block>										
									</fo:table-cell>
									</fo:table-row>
									<fo:table-row>
										<fo:table-cell text-align="start">
											<fo:block>
												<xsl:value-of select="languageReporter"></xsl:value-of>
											</fo:block>										
										</fo:table-cell>
										<fo:table-cell text-align="center">
											<xsl:choose>
												<xsl:when test="preceding-sibling::slot[1]/reporter!=''">
													<fo:block>
														प्रथम <xsl:value-of select="preceding-sibling::slot[1]/reporter"></xsl:value-of>
													</fo:block>	
												</xsl:when>
												<xsl:when test="./reporter!=''">
													<fo:block>
														प्रथम <xsl:value-of select="./reporter"></xsl:value-of>
													</fo:block>	
												</xsl:when>
												<xsl:otherwise>
													<fo:block></fo:block>	
												</xsl:otherwise>
											</xsl:choose>
										</fo:table-cell>
										<fo:table-cell text-align="end" >
											<fo:block>
												<xsl:value-of select="startTime"></xsl:value-of>
											</fo:block>										
										</fo:table-cell>
									</fo:table-row>
      							</fo:table-body>
      						</fo:table>
      					</fo:block>
      					<fo:block>&#160;</fo:block>
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
			        <fo:block font-family="{$font}"  font-size="18px"  white-space-collapse="false" white-space-treatment="preserve" line-height="15px">
			      		<xsl:for-each select="./childVOs">
			      			<xsl:if test="position()!=1">
								<fo:block  page-break-before="always">&#160;</fo:block>
							</xsl:if>
							<fo:block>
								<xsl:if test="(position()=1 or preceding-sibling::childVOs[1]/memberrole!=./memberrole) and string-length(./memberrole)>0">
									<xsl:if test="string-length(./chairperson)>0">
										<xsl:choose>
											<xsl:when test="../houseType='upperhouse'">
												<fo:block text-align="center" font-weight="bold">
													(सभापतीस्थानी  माननीय <xsl:apply-templates select="memberrole"></xsl:apply-templates> &#160; <xsl:apply-templates select="chairperson"></xsl:apply-templates>)
												</fo:block>
												<fo:block>&#160;</fo:block>
											</xsl:when>
											<xsl:otherwise>
												<fo:block text-align="center" font-weight="bold">
													(उपाध्यक्षस्थानी  माननीय <xsl:apply-templates select="memberrole"></xsl:apply-templates> &#160; <xsl:apply-templates select="chairperson"></xsl:apply-templates>)
												</fo:block>
												<fo:block>&#160;</fo:block>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:if>
								</xsl:if>
								<xsl:if test="(preceding-sibling::childVOs[1]/pageHeading!=./pageHeading and preceding-sibling::childVOs[1]/mainHeading!=./mainHeading) 
												or (preceding-sibling::childVOs[1]/pageHeading!=./pageHeading) 
												or (preceding-sibling::childVOs[1]/mainHeading!=./mainHeading) 
												or (preceding-sibling::childVOs[1]/specialHeading!=./specialHeading)
												or position()=1 ">
									<xsl:choose>
								 		<xsl:when test="string-length(./pageHeading)>0 and string-length(./mainHeading)>0">
							 				<fo:table>	
							 					<fo:table-column column-number="1" column-width="20%" />
		                       					<fo:table-column column-number="2" column-width="80%" />
							 					<fo:table-body>
							 						<fo:table-row>
							 							<fo:table-cell>
							 								<fo:block text-align="center">
											 					<fo:inline font-weight="bold">पृ. शी. : </fo:inline>
											 				</fo:block>
							 							</fo:table-cell>
							 							<fo:table-cell>
							 								<fo:block text-align="justify"><xsl:value-of select="./pageHeading"></xsl:value-of></fo:block>
							 							</fo:table-cell>
							 						</fo:table-row>
							 						<fo:table-row>
							 							<fo:table-cell>
							 								<fo:block text-align="center">
											 					<fo:inline font-weight="bold">मु. शी. : </fo:inline>
											 				</fo:block>
							 							</fo:table-cell>
							 							<fo:table-cell>
							 								<fo:block text-align="justify"><xsl:value-of select="./mainHeading"></xsl:value-of></fo:block>
							 							</fo:table-cell>
							 						</fo:table-row>
							 					</fo:table-body>
							 				</fo:table>
							 				<fo:block>&#160;</fo:block>															 		
									 	</xsl:when>
							 			<xsl:when test="string-length(./pageHeading)>0 or string-length(./mainHeading)>0">
							 				<fo:table>
							 					<fo:table-body>
							 						<fo:table-row>
							 							<fo:table-cell>
							 								<fo:block text-align="center">
											 					<fo:inline font-weight="bold">पृ. शी./ मु. शी. :</fo:inline>
											 				</fo:block>
							 							</fo:table-cell>
							 							<fo:table-cell>
							 								<fo:block text-align="justify"><xsl:value-of select="./pageHeading"></xsl:value-of> <xsl:value-of select="./mainHeading"></xsl:value-of></fo:block>
							 							</fo:table-cell>
							 						</fo:table-row>
							 					</fo:table-body>		
							 				</fo:table>
							 				<fo:block>&#160;</fo:block>															 		
							 			</xsl:when>
							 			<xsl:when test="string-length(./specialHeading)>0">
							 				<fo:block text-align="center" font-weight="bold">
							 					 <xsl:value-of select="./specialHeading"></xsl:value-of>
							 				</fo:block>
							 				<fo:block>&#160;</fo:block>															 		
							 			</xsl:when>
						 	 	</xsl:choose>
							</xsl:if>
							<fo:block text-align="justify" line-height="200%">
								<fo:inline font-weight="bold" text-align="justify" >
									<xsl:choose>
										<xsl:when test="(position()=1 or boolean(preceding-sibling::childVOs[position()-1])=false or preceding-sibling::childVOs[1]/primaryMember!=./primaryMember) and string-length(./primaryMember)>0">
											<xsl:variable name="member" select="./primaryMember"/>
		         							<xsl:choose>
												<xsl:when test="./primaryMemberMinistry!='' or ./primaryMember='उपाध्यक्ष' or ./primaryMember='सभापती'">
													<fo:inline font-weight="bold" >
														<xsl:value-of select="./primaryMember"></xsl:value-of>
														<xsl:if test="./constituency!=''">
															(<xsl:value-of select="./constituency"></xsl:value-of>)
														</xsl:if>	
													</fo:inline>
												</xsl:when>
												<xsl:otherwise>
												<fo:inline font-weight="normal" >
													<xsl:value-of select="./primaryMember"></xsl:value-of>
													<xsl:if test="./constituency!=''">
															(<xsl:value-of select="./constituency"></xsl:value-of>)
													</xsl:if>
												</fo:inline>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:choose>
												<xsl:when test="./primaryMemberSubDepartment!=''">
													(<xsl:value-of select="./primaryMemberSubDepartment"></xsl:value-of> &#160; <xsl:value-of select="./primaryMemberDesignation"></xsl:value-of> )
												</xsl:when>
												<xsl:otherwise>
													<xsl:if test="./primaryMemberDesignation!=''">
															(<xsl:value-of select="./primaryMemberDesignation"></xsl:value-of>)
													</xsl:if>
												</xsl:otherwise>
											</xsl:choose>
											<xsl:if test="./substituteMember!=''">
												<xsl:choose>
													<xsl:when test="./substituteMemberMinistry!=''">
														<fo:inline font-weight="bold" >
															,<xsl:value-of select="./substituteMember"></xsl:value-of>
														</fo:inline>
													</xsl:when>
													<xsl:otherwise>
														<fo:inline font-weight="normal" >
															,<xsl:value-of select="./substituteMember"></xsl:value-of>
														</fo:inline>
													</xsl:otherwise>
												</xsl:choose>
												<xsl:if test="./substituteMemberMinistry!=''">
													(<xsl:value-of select="./substituteMemberMinistry"></xsl:value-of>)	
												</xsl:if>
												यांच्या करिता
											</xsl:if>
											:																														
										</xsl:when>
										<xsl:when test="string-length(./publicRepresentative)>0 and ./publicRepresentative!=''">
											<xsl:value-of select="./publicRepresentative"></xsl:value-of>	
											<xsl:if test="./publicRepresentativeDetails!=''">
												(<xsl:value-of select="./publicRepresentativeDetails"></xsl:value-of>)
											</xsl:if>
											:																			
										</xsl:when>
										<xsl:otherwise>
											&#160;&#160;&#160;&#160;&#160;
										</xsl:otherwise>
									</xsl:choose>
								</fo:inline>
									<fo:inline font-weight="normal" text-align="justify">
										 <xsl:apply-templates select="./proceedingContent"></xsl:apply-templates>
									</fo:inline>
								</fo:block>
							</fo:block>
						</xsl:for-each>		
  					<xsl:choose>
  						<xsl:when test="following-sibling::slot[1]/reporter!=''">
  							<fo:block text-align="end">
								या नंतर &#160;<xsl:apply-templates select="following-sibling::slot[1]/reporter"></xsl:apply-templates>
							</fo:block>
  						</xsl:when>
  						<xsl:when test="string-length(./nextReporter)>0 and  ./nextReporter!=''">
  							<fo:block text-align="end">
								या नंतर &#160;<xsl:apply-templates select="./nextReporter"></xsl:apply-templates>
							</fo:block>
  						</xsl:when>
  						<!-- <xsl:otherwise>
  							<fo:block text-align="end">
								 &#160;
							</fo:block>
  						</xsl:otherwise> -->
  					</xsl:choose>
  				</fo:block>
  	 		</fo:flow>
  		 </fo:page-sequence>
  		</xsl:for-each>
  	</fo:root>
  </xsl:template>   
	<xsl:template name="repeatable">
		<xsl:param name="index" select="1" />
		<xsl:param name="total" select="10" />
		  		 <!-- Do something -->
		<xsl:if test="not($index = $total)">
		 <xsl:call-template name="repeatable">
			  <xsl:with-param name="index" select="$index + 1" />
		     </xsl:call-template>
		</xsl:if>
	</xsl:template>  
	 <xsl:template name="numberLines">
	  <xsl:param name="pLastLineNum" select="0"/>
	  <xsl:param name="pText" select="."/>
	
	  <xsl:if test="string-length($pText)">
	   <xsl:value-of select="concat($pLastLineNum+1, ' ')"/>
	
	   <xsl:value-of select="substring-before($pText, '&#xA;')"/>
	   <xsl:text>&#xA;</xsl:text>
	
	   <xsl:call-template name="numberLines">
	    <xsl:with-param name="pLastLineNum"
	      select="$pLastLineNum+1"/>
	    <xsl:with-param name="pText"
	      select="substring-after($pText, '&#xA;')"/>
	   </xsl:call-template>
	  </xsl:if>
	 </xsl:template>
	
	<xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>