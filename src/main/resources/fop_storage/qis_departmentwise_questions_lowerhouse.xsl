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
	    
	    <xsl:variable name="houseType" select="element_1[1]/element_1_2"/>
     	<xsl:variable name="selectedAnsweringDate" select="element_4"/>
     	<xsl:variable name="deviceType" select="element_1[1]/element_1_12"/>
     	<xsl:variable name="status" select="element_1[1]/element_1_14"/>
	    
	    <xsl:variable name="pageMode">
	    	<xsl:choose>
	    		<xsl:when test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">landscape</xsl:when>
	    		<xsl:otherwise>portrait</xsl:otherwise>
	    	</xsl:choose>
	    </xsl:variable>	    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="portrait"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="1.5cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
  				<fo:simple-page-master master-name="landscape"
	                  	page-height="21cm" page-width="29.7cm"
	                  	margin-top="1.5cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-first" extent="2cm"/>
			      	<fo:region-after region-name="ra-first" extent="1.5cm"/>
  				</fo:simple-page-master>
  				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="1.5cm" margin-bottom="1.5cm"
	                  	margin-left="1.5cm" margin-right="1.5cm">
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
	        
	        <fo:page-sequence master-reference="{$pageMode}" id="DocumentBody">
	        	
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
			       <fo:block font-family="Kokila" font-size="15px">			       		
			       		<fo:block font-size="18px" text-align="center" font-weight="bold" text-decoration="underline">
			       			<xsl:value-of select="element_1[1]/element_1_3"/>&#160;<xsl:value-of select="element_2[2]"/>
			       		</fo:block>	
			       		<fo:block font-size="4px">&#160;</fo:block>
			       		<fo:block>
			       			<xsl:value-of select="element_2[3]"/> :- &#160;&#160;<fo:inline font-weight="bold"><xsl:value-of select="element_1[1]/element_1_4"/></fo:inline>
			       			&#160;&#160;&#160;&#160;
			       			<xsl:value-of select="element_2[4]"/> :- &#160;&#160;<fo:inline font-weight="bold"><xsl:value-of select="element_1[1]/element_1_5"/></fo:inline>
			       		</fo:block>
			       		<fo:block font-size="4px">&#160;</fo:block>
			       		<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
			       			<xsl:value-of select="element_1[1]/element_1_13"/> - <xsl:value-of select="element_1[1]/element_1_15"/>
			       		</fo:block>
			       		<fo:block font-size="4px">&#160;</fo:block>
			       		<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
			       			<xsl:value-of select="element_2[7]"/> - <xsl:value-of select="element_1[1]/element_1_7"/>
			       		</fo:block>
			       		<xsl:variable name="isSubDepartmentSelected">
			       			<xsl:value-of select="element_1[1]/element_1_21"/>
			       		</xsl:variable>
			       		<xsl:if test="$isSubDepartmentSelected='yes'">
				       		<fo:block font-size="4px">&#160;</fo:block>
				       		<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
				       			<xsl:value-of select="element_2[6]"/> - <xsl:value-of select="element_1[1]/element_1_6"/>
				       		</fo:block>
			       		</xsl:if>
			       		<fo:block font-size="4px">&#160;</fo:block>
			       		<xsl:if test="$houseType='lowerhouse' and $deviceType='questions_starred' and $status='question_final_admission' and $selectedAnsweringDate!=''">
				       		<fo:block font-size="17px" font-weight="bold" text-align="center">		       			
				       			<xsl:value-of select="element_1[1]/element_1_18"/>&#160;<xsl:value-of select="element_2[13]"/>
				       		</fo:block>			       		
				       		<fo:block font-size="4px">&#160;</fo:block>
			       		</xsl:if>			       		
			       		<fo:block>			       			
			       			<fo:table table-layout="fixed" width="100%">
			       				<xsl:choose>
			       					<xsl:when test="$deviceType='questions_starred' and $status='question_final_admission'">
			       						<fo:table-column column-width="1.5cm"/>
								        <fo:table-column column-width="3cm"/>
								        <fo:table-column column-width="4cm"/>
								        <fo:table-column column-width="2.5cm"/>
								        <fo:table-column column-width="5.5cm"/>
								        <fo:table-column column-width="1.8cm"/>
			       					</xsl:when>
			       					<xsl:when test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">
			       						<fo:table-column column-width="1.5cm"/>
								        <fo:table-column column-width="3.5cm"/>
								        <fo:table-column column-width="4.5cm"/>
								        <fo:table-column column-width="7cm"/>
								        <fo:table-column column-width="3cm"/>
								        <fo:table-column column-width="2cm"/>
								        <fo:table-column column-width="3cm"/>
			       					</xsl:when>
			       					<xsl:otherwise>
			       						<fo:table-column column-width="1.5cm"/>
								        <fo:table-column column-width="3.5cm"/>
								        <fo:table-column column-width="4.5cm"/>
								        <fo:table-column column-width="7cm"/>
			       					</xsl:otherwise>
			       				</xsl:choose>			       				
						        <fo:table-header>
						        	<fo:table-row>
						        		<fo:table-cell display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center" font-weight="bold">
						        				<xsl:value-of select="element_2[1]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block text-align="center" font-weight="bold">
						        				<xsl:value-of select="element_2[8]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
						        			<fo:block>
						        				<xsl:value-of select="element_2[9]"/>
						        			</fo:block>
						        		</fo:table-cell>
						        		<xsl:choose>
					       					<xsl:when test="$deviceType='questions_starred' and $status='question_final_admission'">
					       						<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
								        			<fo:block>
								        				<xsl:value-of select="element_2[11]"/>
								        			</fo:block>
								        		</fo:table-cell>
								        		<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
								        			<fo:block>
								        				<xsl:value-of select="element_2[10]"/>
								        			</fo:block>
								        		</fo:table-cell>
								        		<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
								        			<fo:block>
								        				<xsl:value-of select="element_2[12]"/>
								        			</fo:block>
								        		</fo:table-cell>
					       					</xsl:when>
					       					<xsl:otherwise>
					       						<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
								        			<fo:block>
								        				<xsl:value-of select="element_2[10]"/>
								        			</fo:block>
								        		</fo:table-cell>
								        		<xsl:if test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">
								        			<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:apply-templates select="element_2[13]"/>									        				
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:apply-templates select="element_2[12]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" display-align="before" font-weight="bold" border-top-width="0.5pt" border-top-style="solid" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:apply-templates select="element_2[11]"/>
									        			</fo:block>
									        		</fo:table-cell>
								        		</xsl:if>
					       					</xsl:otherwise>
					       				</xsl:choose>						        		
						        	</fo:table-row>
						        </fo:table-header>
						        <fo:table-body>
						        	<xsl:for-each select="element_1">
						        		 <xsl:variable name="count" select="position()"/>
						        		<fo:table-row>
							        		<xsl:choose>
							        			<xsl:when test="position()!=last()">
							        				<fo:table-cell margin-left="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="../element_8[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block text-align="center">
									        				<fo:block><xsl:value-of select="element_1_8"/></fo:block>							        								        			
									        				<fo:block><xsl:apply-templates select="../element_3[$count]"/></fo:block>							        				
									        			</fo:block>									        			
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
									        			<fo:block>
									        				<xsl:value-of select="element_1_10"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<xsl:choose>
										        		<xsl:when test="$deviceType='questions_starred' and $status='question_final_admission'">
										        			<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
											        			<fo:block>
											        				<xsl:value-of select="element_1_16"/>
											        			</fo:block>
											        		</fo:table-cell>
										        			<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
											        			<fo:block>
											        				<xsl:value-of select="element_1_11"/>
											        			</fo:block>
											        		</fo:table-cell>
											        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
											        			<fo:block>
											        				<xsl:value-of select="element_1_17"/>
											        			</fo:block>
											        		</fo:table-cell>
										        		</xsl:when>
										        		<xsl:otherwise>
										        			<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
											        			<fo:block>
											        				<xsl:value-of select="element_1_11"/>
											        			</fo:block>
											        		</fo:table-cell>
											        		<xsl:if test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">
											        			<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_22"/>									        				
												        			</fo:block>
												        		</fo:table-cell>
												        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_17"/>
												        			</fo:block>
												        		</fo:table-cell>
												        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_16"/>
												        			</fo:block>
												        		</fo:table-cell>
											        		</xsl:if>
										        		</xsl:otherwise>
									        		</xsl:choose>									        		
							        			</xsl:when>
							        			<xsl:otherwise>
							        				<fo:table-cell margin-left="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="../element_8[$count]"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<fo:table-cell display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block text-align="center">
									        				<fo:block><xsl:value-of select="element_1_8"/></fo:block>							        								        			
									        				<fo:block><xsl:apply-templates select="../element_3[$count]"/></fo:block>							        				
									        			</fo:block>									        			
									        		</fo:table-cell>
									        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
									        			<fo:block>
									        				<xsl:value-of select="element_1_10"/>
									        			</fo:block>
									        		</fo:table-cell>
									        		<xsl:choose>
								       					<xsl:when test="$deviceType='questions_starred' and $status='question_final_admission'">
								       						<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
											        			<fo:block>
											        				<xsl:value-of select="element_1_16"/>
											        			</fo:block>
											        		</fo:table-cell>
											        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
											        			<fo:block>
											        				<xsl:value-of select="element_1_11"/>
											        			</fo:block>
											        		</fo:table-cell>
											        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
											        			<fo:block>
											        				<xsl:value-of select="element_1_17"/>
											        			</fo:block>
											        		</fo:table-cell>
								       					</xsl:when>
								       					<xsl:otherwise>
								       						<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
											        			<fo:block>
											        				<xsl:value-of select="element_1_11"/>
											        			</fo:block>
											        		</fo:table-cell>
											        		<xsl:if test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">
											        			<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_22"/>									        				
												        			</fo:block>
												        		</fo:table-cell>
												        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_17"/>
												        			</fo:block>
												        		</fo:table-cell>
												        		<fo:table-cell margin-left="0.3cm" margin-right="0.3cm" display-align="before" padding-top="0.1cm" padding-bottom="0.1cm" border-bottom-width="0.5pt" border-bottom-style="solid">
												        			<fo:block>
												        				<xsl:apply-templates select="element_1_16"/>
												        			</fo:block>
												        		</fo:table-cell>
											        		</xsl:if>
								       					</xsl:otherwise>
								       				</xsl:choose>									        		
							        			</xsl:otherwise>
							        		</xsl:choose>
							        	</fo:table-row>
						        	</xsl:for-each>
						        </fo:table-body>
			       			</fo:table>
			       		</fo:block>
			       		<xsl:if test="$deviceType='questions_unstarred' and $status='question_unstarred_final_admission'">
			       			<fo:block font-size="6px">&#160;</fo:block>
			       			<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
				       			<xsl:value-of select="element_2[14]"/> - <xsl:value-of select="element_5"/>
				       		</fo:block>
				       		<fo:block font-size="4px">&#160;</fo:block>
				       		<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
				       			<xsl:value-of select="element_2[15]"/> - <xsl:value-of select="element_6"/>
				       		</fo:block>
				       		<fo:block font-size="4px">&#160;</fo:block>
				       		<fo:block font-size="17px" font-weight="bold" text-decoration="underline">
				       			<xsl:value-of select="element_2[16]"/> - <xsl:value-of select="element_7"/>
				       		</fo:block>
			       		</xsl:if>
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