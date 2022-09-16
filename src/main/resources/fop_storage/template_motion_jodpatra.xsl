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
    
    <xsl:template name="getNumber">
    	<xsl:param name="num"></xsl:param>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($num,'0','०')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'1','१')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'2','२')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'3','३')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'4','४')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'5','५')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'6','६')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'7','७')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'8','८')"></xsl:value-of>
    	</xsl:variable>
    	<xsl:variable name="finalNum">
    		<xsl:value-of select="translate($finalNum,'9','९')"></xsl:value-of>
    	</xsl:variable>
    	
    	<fo:block>
    		<xsl:value-of select="$finalNum"></xsl:value-of>
    	</fo:block>
    </xsl:template>
    
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
	            	  
	            	<xsl:choose>
	            		<xsl:when test="./element_3='lowerhouse'">
	            			<xsl:for-each select="./element_2" >
			            		<xsl:choose>
			            			<xsl:when test="position()=1">
			            				<fo:block text-align="center" text-decoration="underline" font-family="Kokila" font-size="21px" font-weight="bold">
				    						<xsl:value-of select="."></xsl:value-of>
				    					</fo:block>
			            			</xsl:when>
			            			<xsl:otherwise>	
			            				<fo:block text-align="center" text-decoration="underline" font-family="Kokila">
				    						<xsl:value-of select="."></xsl:value-of>
				    					</fo:block>
			            			</xsl:otherwise>
		    					</xsl:choose>
   							</xsl:for-each>
   							
	            			<fo:block font-family="Kokila" font-size="18px">     					
		     					<fo:block text-align="center" font-size="9px">&#160;</fo:block>
		       						
		       					<xsl:for-each select="./element_1">
		       						<xsl:if test="./element_1_2!=''">
			       						<fo:block font-weight="bold" text-decoration="underline">
			       							<xsl:value-of select="./element_1_1" ></xsl:value-of>
			       						</fo:block>
			       						     
			       						<fo:block text-align="center" font-size="9px">&#160;</fo:block>
			       					</xsl:if>
		       						  						
		       						<xsl:for-each select="element_1_2">
		       							<fo:inline font-weight="bold" page-break-before="always">		       						
		       								<xsl:value-of select="element_1_2_1"/>.      								
		       							</fo:inline>
		       							
		       							<fo:inline>		       						
		       								<xsl:apply-templates select="element_1_2_2"/>  								
		       							</fo:inline>
		       						
		       							<fo:block margin="21px">
		       								<xsl:value-of select="element_1_2_3"/>
		       							</fo:block>
		       						</xsl:for-each>
		       					</xsl:for-each>
							</fo:block>
	            		</xsl:when>
	            		
	            		<xsl:when test="./element_3='upperhouse'">
	            			<xsl:for-each select="./element_2">
	            				<fo:block font-weight="bold" font-family="Kokila" font-size="21px" text-align="center">
				            		<xsl:value-of select="."></xsl:value-of>
				            	</fo:block>
	            			</xsl:for-each>
	            			
	            			<fo:block font-family="Kokila" font-size="18px">     					
		     					<fo:block text-align="center" font-size="9px">&#160;</fo:block>
		       						
		       					<fo:block width="600px">
	       							<fo:table table-layout="fixed" width="100%">
	       								 <fo:table-column column-width="8%"/>
     									 <fo:table-column column-width="92%"/>
	       								<fo:table-body>
	       									<xsl:for-each select="./element_1">
	       										<xsl:if test="./element_1_2!=''">
		       										<fo:table-row>
		       											<fo:table-cell number-columns-spanned="2">
															<fo:block font-weight="bold" text-align="center" font-size="19px">
								       							<xsl:value-of select="./element_1_1" ></xsl:value-of>
								       						</fo:block>
		       											</fo:table-cell>
		       										</fo:table-row>
		       										
						       						 <fo:table-row>
						       						 	<fo:table-cell number-columns-spanned="2">
						       						 		<fo:block text-align="center" font-size="9px">&#160;</fo:block>
						       						 	</fo:table-cell>
						       						 </fo:table-row>
						       					</xsl:if>    
					       										
					       						<xsl:for-each select="element_1_2">
					       						
					       							<fo:table-row>
					       								<fo:table-cell number-columns-spanned="2">
					       									<fo:block>&#160;</fo:block>
					       								</fo:table-cell>
					       							</fo:table-row>
					       							
					       							<fo:table-row>
					       								<fo:table-cell>
					       									<fo:block font-weight="bold" text-align="center">					
							       								<xsl:value-of select="element_1_2_1"/>.      								
							       							</fo:block>
					       								</fo:table-cell>
					       								
					       								<fo:table-cell>
					       									<fo:block  text-align="justify">		       						
							       								<xsl:apply-templates select="element_1_2_2"/>  								
							       							</fo:block>
					       								</fo:table-cell>
					       							</fo:table-row>
					       						
					       							<fo:table-row>
					       								<fo:table-cell text-align="center">
					       									<fo:block>&#160;</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="justify">
							       								&#160;&#160;&#160;&#160;&#160;"<xsl:value-of select="element_1_2_4"/>"
							       							</fo:block>
					       								</fo:table-cell>
					       							</fo:table-row>
					       						</xsl:for-each>
					       					</xsl:for-each>
	       								</fo:table-body>
	       							</fo:table>
	       						</fo:block>
							</fo:block>
	            		</xsl:when>
	            	</xsl:choose>
	            	
	            	<fo:block width="600px" font-family="Kokila" font-size="18px">
						<fo:block text-align="center" font-size="9px">&#160;</fo:block>
						<fo:table>
							<fo:table-body>
								
									<fo:table-row font-weight="bold">
										<fo:table-cell>
											<fo:block>
												विधान भवन,
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell>
											<fo:block>
												&#160;
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell text-align="center">
											<fo:block>
												<xsl:value-of select="./element_6"></xsl:value-of>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									
									<fo:table-row font-weight="bold">
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="./element_4"></xsl:value-of>,
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell>
											<fo:block>
												&#160;
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell text-align="center">
											<fo:block>
												<xsl:value-of select="./element_7"></xsl:value-of>,
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									
									<fo:table-row font-weight="bold">
										<fo:table-cell>
											<fo:block>
												<xsl:value-of select="./element_5"></xsl:value-of>
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell>
											<fo:block>
												&#160;
											</fo:block>
										</fo:table-cell>
										
										<fo:table-cell text-align="center">
											<fo:block>
												<xsl:for-each select="./element_2">
													<xsl:if test="position()=1">
														<xsl:value-of select="."></xsl:value-of>
													</xsl:if>
												</xsl:for-each>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>									
							</fo:table-body>
						</fo:table>
					</fo:block>
					
					<xsl:if test="./element_3='upperhouse'">
						<fo:block font-size="14px">
							&#160;
						</fo:block>
						
						<fo:block width="600px" font-family="Kokila" font-size="18px">
							<fo:table table-layout="fixed">
								<fo:table-column column-width="7%"/>
	     						<fo:table-column column-width="93%"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell>
											<fo:block font-weight="bold">
												टीप :- 
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="justify">
												या जोडपत्रातील लक्षवेधी सुचनेच्या विभागीय हस्तांतरणाबाबत कृपया सुचनेच्या जोडपत्रातील अनुक्रमांकासहीत या सचिवालयास अवगत करण्यात यावे. 
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
									
									<fo:table-row>
										<fo:table-cell number-columns-spanned="2" text-align="center">
											<fo:block>
												 ***************
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>					
						</fo:block>
					</xsl:if>
	            								          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>