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
	                  page-height="29.7cm"
	                  page-width="21cm"
	                  margin-top="1.8cm" 
	                  margin-bottom="1.5cm"
	                  margin-left="2.5cm"
	                  margin-right="2.5cm">
				      <fo:region-body margin-top="0cm"/>        
				      <fo:region-before region-name="page-number" extent="2cm"/>
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
			      <fo:block font-family="Kokila" font-size="15px" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">	
		     		  	<fo:block font-weight="bold" text-align="right">क्रमांक - _____</fo:block>	
				       	<fo:block font-weight="bold" font-size="18px" text-align="center" >
								<fo:block text-decoration="underline">महाराष्ट्र <xsl:value-of select="./element_1/element_1_7"/></fo:block>
								<fo:block text-decoration="underline">पत्रक भाग दोन </fo:block>
								<fo:block>	<xsl:value-of select="./element_2"/>/<xsl:value-of select="./element_3"/></fo:block>
								<fo:block text-decoration="underline">समित्या गठीत करण्याबाबत </fo:block>
						</fo:block>	  
				
			      
			      
			      <xsl:choose>
						<xsl:when test="./element_1/element_1_7='विधानसभा'">
								  <fo:block  font-size="14px">
			       					&#160;&#160;&#160;&#160;&#160;सन २०१५-२०१६ या वर्षासाठी विधानमंडळाच्या विविध समित्यावर मा.उपाध्यक्ष,महाराष्ट्र विधानसभा यांनी <xsl:value-of select="./element_2"/> रोजी विधानसभा सदस्यांची खालीलप्रमाणे नामनियुक्ती केली आहे.				       		
			      	  			 </fo:block>
						</xsl:when>
						<xsl:when test="./element_1/element_1_7='विधानपरिषद'">
							  		<fo:block  font-size="14px">
			       					&#160;&#160;&#160;&#160;&#160;सन २०१५-२०१६ या वर्षासाठी विधानमंडळाच्या विविध समित्यावर मा.सभापती,महाराष्ट्र विधानपरिषद यांनी <xsl:value-of select="./element_2"/> रोजी विधानपरिषद सदस्यांची खालीलप्रमाणे नामनियुक्ती केली आहे.				       		
			      	  				</fo:block>
						</xsl:when>
						<xsl:when test="./element_1/element_1_7='दोन्ही सभागृह'">
								 <fo:block  font-size="14px">
			       					&#160;&#160;&#160;&#160;&#160;सन २०१५-२०१६ या वर्षासाठी विधानमंडळाच्या विविध समित्यावर मा.उपाध्यक्ष,महाराष्ट्र विधानसभा व मा.सभापती,महाराष्ट्र विधानपरिषद यांनी <xsl:value-of select="./element_2"/> रोजी विधानसभा व विधानपरिषद सदस्यांची खालीलप्रमाणे नामनियुक्ती केली आहे.				       		
			      	  			 </fo:block>
						</xsl:when>
			</xsl:choose>
			 
			    	            	   	    
					<fo:block >
						<fo:table table-layout="fixed">
								<fo:table-column column-number="1" column-width="10%" />
								<fo:table-column column-number="2" column-width="80%" />
								<fo:table-column column-number="3" column-width="20%" />
							<fo:table-body>
								<xsl:choose>
							    	<xsl:when test="not(./element_1)">
							    		<fo:table-row>
							     			<fo:table-cell>
									     		<fo:block>
									     			माहिती उपलब्ध नाही..
									     		</fo:block>
									     	</fo:table-cell>								     	
								     	</fo:table-row>     	
							     	</xsl:when>
								     <xsl:otherwise>					     
									     <xsl:for-each select="./element_1" >	
									      <xsl:variable name="countSerial" select="position()"></xsl:variable>
										     <xsl:if test="position() = 1 or (preceding-sibling::element_1[1]/element_1_1!=./element_1_1)">
											      <xsl:choose>
											      	<xsl:when test="position()!=1">
											      		<fo:table-row page-break-before="always">
													      <fo:table-cell font-weight="bold" text-align="center" text-decoration="underline" number-columns-spanned="3">
													     		<fo:block font-size="14px" >
													     			<xsl:value-of select="./element_1_2"/>
									   							</fo:block>
							   								</fo:table-cell>								     	
													    </fo:table-row>
											      	</xsl:when>
											      	<xsl:otherwise>
												      	<fo:table-row>
													      <fo:table-cell font-weight="bold" text-align="center" text-decoration="underline" number-columns-spanned="3">
													     		<fo:block font-size="14px">
													     			<xsl:value-of select="./element_1_2"/>
									   							</fo:block>
							   								</fo:table-cell>								     	
													    </fo:table-row>
											      	</xsl:otherwise>
											      </xsl:choose>
											     <fo:table-row>
							     					<fo:table-cell number-columns-spanned="3">
												     		<fo:block font-weight="bold" >
												     			समिती प्रमुख
												     		</fo:block>
												   	</fo:table-cell>								     	
											    </fo:table-row>
							     				<fo:table-row>
							     					<fo:table-cell>
												     		<fo:block>
													     			(<xsl:value-of select="../element_5[$countSerial]"/>) 
													     		</fo:block>
												   	</fo:table-cell>
							     					<fo:table-cell>
												     		<fo:block font-weight="bold">
												     			<xsl:value-of select="./element_1_6"></xsl:value-of>
												     		</fo:block>
												   	</fo:table-cell>
											<!-- 	   	<fo:table-cell>
													     		<fo:block font-weight="bold">
													     			<xsl:value-of select="./element_1_4"></xsl:value-of>
													     		</fo:block>
													   	</fo:table-cell>	 -->							     	
											    </fo:table-row>
											    	<fo:table-row>
							     					<fo:table-cell number-columns-spanned="3">
												     		<fo:block font-weight="bold">
												     			सदस्य
												     		</fo:block>
												   	</fo:table-cell>								     	
											    </fo:table-row>
											   </xsl:if>
											   
										     	<xsl:if test="preceding-sibling::element_1[1]/element_1_1=./element_1_1">
										     		<fo:table-row>
								     					<fo:table-cell>
													     		<fo:block>
													     			(<xsl:value-of select="../element_5[$countSerial]"/>) 
													     		</fo:block>
													   	</fo:table-cell>
								     					<fo:table-cell>
													     		<fo:block>
													     			<xsl:value-of select="./element_1_6"/>
													     		</fo:block>
													   	</fo:table-cell>	
													<!--    		<fo:table-cell>
													     		<fo:block>
													     			<xsl:value-of select="./element_1_4"></xsl:value-of>
													     		</fo:block>
													   	</fo:table-cell>	 -->						     	
												    </fo:table-row>	
										     	
										     		
											    </xsl:if>				
											</xsl:for-each>						   
								     	</xsl:otherwise>
						     		</xsl:choose>	
						    	 </fo:table-body>	
					    	 </fo:table>	    
	              	  </fo:block>       
	              	  
	              	  <!--For invited members -->
	              	  <fo:block >
						<fo:table table-layout="fixed">
								<fo:table-column column-number="1" column-width="10%" />
								<fo:table-column column-number="2" column-width="60%" />
								<fo:table-column column-number="3" column-width="30%" />
							<fo:table-body>
								<xsl:choose>
							    	<xsl:when test="not(./element_4)">
							    		<fo:table-row>
							     			<fo:table-cell>
									     		<fo:block>
									     		
									     		</fo:block>
									     	</fo:table-cell>								     	
								     	</fo:table-row>     	
							     	</xsl:when>
								     <xsl:otherwise>					     
									     <xsl:for-each select="./element_4" >	
									      <xsl:variable name="countSerial" select="position()"></xsl:variable>
										     <xsl:if test="position() = 1 or (preceding-sibling::element_4[1]/element_4_1!=./element_4_1)">
											  
											     <fo:table-row>
							     					<fo:table-cell number-columns-spanned="3">
												     		<fo:block font-weight="bold" >
												     			आमंत्रित सदस्य
												     		</fo:block>
												   	</fo:table-cell>								     	
											    </fo:table-row>
							     				<fo:table-row>
							     					<fo:table-cell>
												     	<fo:block>
													     			(<xsl:value-of select="../element_6[$countSerial]"/>) 
													     		</fo:block>
												   	</fo:table-cell>
							     					<fo:table-cell>
												     		<fo:block >
												     			<xsl:value-of select="./element_4_6"></xsl:value-of>
												     		</fo:block>
												   	</fo:table-cell>
												   	<!-- <fo:table-cell>
													     		<fo:block >
													     			<xsl:value-of select="./element_4_4"></xsl:value-of>
													     		</fo:block>
													   	</fo:table-cell>		 -->						     	
											    </fo:table-row>
											    	
											   </xsl:if>
											   
										     	<xsl:if test="preceding-sibling::element_4[1]/element_4_1=./element_4_1">
										     		<fo:table-row>
								     					<fo:table-cell>
													     		<fo:block>
													     			(<xsl:value-of select="../element_6[$countSerial]"/>) 
													     		</fo:block>
													   	</fo:table-cell>
								     					<fo:table-cell>
													     		<fo:block>
													     			<xsl:value-of select="./element_4_6"/>
													     		</fo:block>
													   	</fo:table-cell>	
													   		<!-- <fo:table-cell>
													     		<fo:block>
													     			<xsl:value-of select="./element_4_4"></xsl:value-of>
													     		</fo:block>
													   	</fo:table-cell> -->							     	
												    </fo:table-row>	
										     		
										     		
											    </xsl:if>				
											</xsl:for-each>						   
								     	</xsl:otherwise>
						     		</xsl:choose>	
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