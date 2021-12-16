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
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before extent="2cm"/>
			      	<fo:region-after extent="1.5cm"/>
  				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="1.25cm" margin-right="1.25cm">
			      	<fo:region-body margin-top="0cm"/>
			      	<fo:region-before region-name="rb-common" extent="2cm"/>
			      	<fo:region-after region-name="ra-common" extent="1.5cm"/>
  				</fo:simple-page-master>
	   				
  				<fo:page-sequence-master master-name="simple">
		          <fo:repeatable-page-master-alternatives>
		            <fo:conditional-page-master-reference 
		              master-reference="others" 
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
			       <!-- content as per report -->	
			       <fo:block font-family="Mangal" font-size="9px">
			       		<fo:block font-size="14px" font-weight="bold" text-align="center">
			       			<!-- <xsl:value-of select="element_1/element_1_2"/> -->
			       			<xsl:apply-templates select="element_1/element_1_2"/>
			       		</fo:block>
			       		<fo:block font-size="10px">&#160;</fo:block>
			     		<xsl:for-each select="./element_2">
			     			<fo:block text-align="center" font-weight="bold" font-size="12px">
			     				<xsl:value-of select="element_2_1[1]"/>&#160;<xsl:value-of select="../element_1/element_1_3"/>
			     				(<xsl:value-of select="../element_1/element_1_4"/><!-- &#160;<xsl:value-of select="element_2_1[2]"/> -->)
			     			</fo:block>
			     			<fo:block font-size="9px">&#160;</fo:block>	
			     			<xsl:choose>
			     				<xsl:when test="boolean(element_2_2) and count(element_2_2)>0">
			     					<fo:block>
					     				<fo:table table-layout="fixed" width="100%">
					     					<fo:table-column column-number="1" column-width="1cm" />
					                        <fo:table-column column-number="2" column-width="3cm" />
					                        <fo:table-column column-number="3" column-width="3.5cm" />
					                        <fo:table-column column-number="4" column-width="5cm" />
					                        <!-- <fo:table-column column-number="5" column-width="6cm" /> -->
					                        <fo:table-column column-number="5" column-width="3cm" />  
					                        <fo:table-column column-number="6" column-width="3cm" />                     
					                        <fo:table-header>
					                        	<fo:table-row>
					                        		<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	   	<fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:value-of select="../element_1/element_1_5"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:value-of select="../element_1/element_1_6"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:value-of select="../element_1/element_1_7"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:value-of select="../element_1/element_1_8"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<!-- <fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:value-of select="../element_1/element_1_9"/>
					                                    </fo:block>
						                        	</fo:table-cell> -->
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
				                                        		<xsl:apply-templates select="../element_1/element_1_11"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center" font-weight="bold" font-size="10px">
						                        	    		<xsl:apply-templates select="../element_1/element_1_13"/>
				                                        </fo:block>
						                        	</fo:table-cell>				                        	
					                        	</fo:table-row>
					                        </fo:table-header>
					                        <fo:table-body>
					                        	<xsl:for-each select="./element_2_2">
					                        	<fo:table-row page-break-inside="avoid">
					                        		<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	   	<fo:block margin-left="0.2cm">
				                                        	<xsl:value-of select="element_2_2_1"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block text-align="center">
				                                        	<xsl:apply-templates select="element_2_2_2"/>		                                        	
					                                    </fo:block>
					                                    <xsl:if test="boolean(element_2_2_5) and element_2_2_5!=''">
					                                    <fo:block text-align="center">					                                    	
				                                        	(<xsl:value-of select="element_2_2_5"/>)	                                        	
					                                    </fo:block>
					                                    </xsl:if>					                                    
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block margin-left="0.2cm" margin-right="0.2cm">
				                                        	<xsl:value-of select="element_2_2_3"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="element_2_2_4"/>
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<!-- <fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_10"/>&#160;:&#160;<xsl:value-of select="element_2_2_6"/>
					                                    </fo:block>
					                                    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_11"/>&#160;:&#160;<xsl:value-of select="element_2_2_7"/>
					                                    </fo:block>
					                                    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_12"/>&#160;:&#160;<xsl:value-of select="element_2_2_8"/>
					                                    </fo:block>
					                                    <xsl:if test="element_2_2_9!='NA'">
					                                    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_13"/>&#160;:&#160;<xsl:value-of select="element_2_2_9"/>
					                                    </fo:block>
					                                    </xsl:if>
					                                    <xsl:if test="element_2_2_10!='NA' and element_2_2_10!=''">
					                                    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_14"/>&#160;:&#160;<xsl:value-of select="element_2_2_10"/>
					                                    </fo:block>
					                                    </xsl:if>
					                                    <xsl:if test="element_2_2_10!='NA' and element_2_2_10=''">
					                                    <fo:block margin-left="0.2cm">
				                                            <xsl:value-of select="../../element_1/element_1_15"/>
					                                    </fo:block>
					                                    </xsl:if>
						                        	</fo:table-cell> -->	
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block margin-left="0.8cm">
						                        	    	<xsl:choose>
						                        	    		<xsl:when test="boolean(element_2_2_7) and element_2_2_7!=''">
						                        	    			<xsl:value-of select="element_2_2_7"/>
						                        	    		</xsl:when>
						                        	    		<xsl:otherwise>
						                        	    			-
						                        	    		</xsl:otherwise>
						                        	    	</xsl:choose>				                                            
					                                    </fo:block>
						                        	</fo:table-cell>
						                        	<fo:table-cell border-width="0.5pt" border-style="solid">
						                        	    <fo:block margin-left="0.7cm">
				                                            <xsl:choose>
						                        	    		<xsl:when test="boolean(element_2_2_9) and element_2_2_9!=''">
						                        	    			<xsl:value-of select="element_2_2_9"/>
						                        	    		</xsl:when>
						                        	    		<xsl:otherwise>
						                        	    			-
						                        	    		</xsl:otherwise>
						                        	    	</xsl:choose>
					                                    </fo:block>
						                        	</fo:table-cell>					                        	
					                        	</fo:table-row>
					                        	</xsl:for-each>
					                        </fo:table-body>
					     				</fo:table>			     				
					     			</fo:block>
			     				</xsl:when>
			     				<xsl:otherwise>
			     					<xsl:value-of select="../element_1/element_1_16"/>
			     				</xsl:otherwise>
			     			</xsl:choose>		     			
			     			<fo:block font-size="16px">&#160;</fo:block>
			     		</xsl:for-each>			     		
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