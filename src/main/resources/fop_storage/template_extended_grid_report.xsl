<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
	xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org"
	exclude-result-prefixes="barcode common xalan">
	<!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/> 
		<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
    
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:variable name="rootNode" select="root" />

	<xsl:variable name="pageLayout" select="simple" />	

	<!-- declares common variables such as language & font that will be used 
		in all report stylesheets -->
	<xsl:include href="common_variables.xsl" />
	
	<xsl:param name="element_2_column_count">3</xsl:param> <!-- set the number of columns in each row here -->

	<xsl:template match="root">
		<xsl:variable name="pageSequenceMasterName">
			<xsl:choose>
				<xsl:when test="$formatOut='application/pdf'">simple</xsl:when>
				<xsl:otherwise>first</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="renderForView" select="0" />
		<xsl:variable name="pageWidth">
			<xsl:choose>
				<xsl:when test="$renderForView=1">
					<xsl:value-of select="count(element_2)*4+5"/>
				</xsl:when>
				<xsl:otherwise>
					<!-- handle for portrait & landscape here -->
					21
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- <xsl:text disable-output-escaping="yes"> &lt;!DOCTYPE fo:root [&lt;!ENTITY 
			nbsp "&amp;#160;"&gt;]&gt; </xsl:text> -->

		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="first"
					page-height="29.7cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
					<xsl:attribute name="page-width">
						<xsl:value-of select="format-number($pageWidth,'#.00')"/>cm
					</xsl:attribute>
					<fo:region-body margin-top="0cm" />
					<fo:region-before extent="2cm" />
					<fo:region-after extent="1.5cm" />					
				</fo:simple-page-master>
				<fo:simple-page-master master-name="others"
					page-height="29.7cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="1.5cm" margin-right="1.5cm">
					<xsl:attribute name="page-width">
						<xsl:value-of select="format-number($pageWidth,'#.00')"/>cm
					</xsl:attribute>
					<fo:region-body margin-top="0cm" />
					<fo:region-before region-name="rb-common" extent="2cm" />
					<fo:region-after region-name="ra-common" extent="1.5cm" />
				</fo:simple-page-master>

				<fo:page-sequence-master master-name="simple">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference
							master-reference="first" page-position="first" />
						<fo:conditional-page-master-reference
							master-reference="others" odd-or-even="even" />
						<fo:conditional-page-master-reference
							master-reference="others" odd-or-even="odd" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>

			<fo:page-sequence id="DocumentBody">
				<xsl:attribute name="master-reference">
					<xsl:value-of select="$pageSequenceMasterName"/>
				</xsl:attribute>

				<!-- header -->
				<fo:static-content flow-name="rb-common">
					<fo:block text-align="center" font-family="{$font}">
						<!-- content for header for all pages center/left/start/right/end" -->
					</fo:block>
				</fo:static-content>

				<!-- footer -->
				<fo:static-content flow-name="ra-common">
					<fo:block text-align="right" font-family="{$font}">
						<!-- content for footer for all pages -->
					</fo:block>
				</fo:static-content>

				<!-- body -->
				<fo:flow flow-name="xsl-region-body">
					<!-- content as per report -->
					<fo:block font-family="{$font}" font-size="10pt">
						<!-- <fo:block text-align="center" font-weight="bold">
							<xsl:value-of select="element_1"/>
						</fo:block> -->
						<fo:block>
							<xsl:for-each select="element_1">
								<fo:block font-weight="bold" text-align="center" vertical-align="middle">
									<xsl:value-of select="."/>
								</fo:block>
							</xsl:for-each>
						</fo:block>
						
						<fo:block>
							<!-- <fo:table table-layout="fixed" xsl:use-attribute-sets=" Table_1"> -->
							<fo:table table-layout="fixed">
								<xsl:call-template name="table_dynamic_columns_width">
									<xsl:with-param name="pageWidth" select="$pageWidth" />
									<xsl:with-param name="index" select="1" />
									<xsl:with-param name="total" select="count(element_2)" />
								</xsl:call-template>
								<fo:table-header>
									<fo:table-row>
										<xsl:for-each select="element_2">
											<!-- <fo:table-cell keep-together.within-column="always" xsl:use-attribute-sets="Cell_1"> -->
											<fo:table-cell keep-together.within-column="always" display-align="center">
												<fo:block>
													<xsl:call-template name="zero_width_space_1">
													    <xsl:with-param name="data">
													    	<xsl:value-of select="."/>
													    </xsl:with-param>
													</xsl:call-template>
													<!-- <xsl:value-of select="."/> -->
												</fo:block>
											</fo:table-cell>									
										</xsl:for-each>										
									</fo:table-row>
								</fo:table-header>
								<fo:table-body>
									<!-- <xsl:for-each select="element_3">
										<fo:table-row>										    
										    <xsl:for-each select="element_2">
										        <xsl:variable name="level2Count" select="$level1Count * 2 + position()"/>
										        <fo:table-cell keep-together.within-column="always">
													<fo:block>
														<xsl:call-template name="zero_width_space_1">
														    <xsl:with-param name="data">
														    	<xsl:value-of select="*[position()=$index]"></xsl:value-of>
														    </xsl:with-param>
														</xsl:call-template>
														<xsl:value-of select="$level2Count"></xsl:value-of>
													</fo:block>
												</fo:table-cell>									        
										    </xsl:for-each>
									    </fo:table-row>
									</xsl:for-each> -->
									<xsl:for-each select="element_3">
										<fo:table-row>
											<xsl:call-template name="table_dynamic_columns_data">
												<xsl:with-param name="index" select="1" />
												<xsl:with-param name="total" select="count(../element_2)" />
											</xsl:call-template>											
										</fo:table-row>
									</xsl:for-each>
								</fo:table-body>
							</fo:table>
						</fo:block>
						
						<fo:block font-size="20px"></fo:block>
						
						<fo:block>							
							<fo:table table-layout="fixed" >
								<fo:table-body>
									<xsl:apply-templates select="element_2[position() mod $element_2_column_count = 1 or position() = 1]" mode="row"/>
								</fo:table-body>
							</fo:table>
						</fo:block>						
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	
	<xsl:template match="element_2" mode="row">
		<fo:table-row>
			<xsl:apply-templates select=". | following-sibling::element_2[position() &lt; $element_2_column_count]" mode="cell"/>
		</fo:table-row>
	</xsl:template>
	
	<xsl:template match="element_2" mode="cell">
		<fo:table-cell keep-together.within-column="always" display-align="center" vertical-align="middle">
			<fo:block><xsl:value-of select="."/></fo:block>
		</fo:table-cell>
	</xsl:template>

	<!-- use for for loop with fixed no. of iterations *[position()=$index]-->
	<xsl:template name="table_dynamic_columns_data">
		<xsl:param name="index"/>
		<xsl:param name="total"/>
		
		<fo:table-cell keep-together.within-column="always" display-align="center">
			<fo:block>
				<xsl:call-template name="zero_width_space_1">
				    <xsl:with-param name="data">
				    	<xsl:choose>
				    		<xsl:when test="$index=1">
				    			<xsl:choose>
						            <xsl:when test="preceding-sibling::element_3[1]/element_3_1[position()]=*[position()=$index]">
						               	<xsl:text></xsl:text>
						            </xsl:when>
						            <xsl:otherwise>
						          		<xsl:value-of select="*[position()=$index]"/>
						            </xsl:otherwise>
						        </xsl:choose>
						    </xsl:when>
				    		<xsl:when test="$index=3">
				    			<xsl:choose>
						            <xsl:when test="preceding-sibling::element_3[1]/element_3_3[position()]=*[position()=$index]">
						               	<xsl:text></xsl:text>
						            </xsl:when>
						            <xsl:otherwise>
						          		<xsl:value-of select="*[position()=$index]"/>
						            </xsl:otherwise>
						        </xsl:choose>
						    </xsl:when>
				    		<xsl:otherwise>
				    			<xsl:value-of select="*[position()=$index]"/>
				    		</xsl:otherwise>
				    	</xsl:choose>				    				    	
				    </xsl:with-param>
				</xsl:call-template>				
			</fo:block>
		</fo:table-cell>
		
		<xsl:if test="not($index = $total)">
			<xsl:call-template name="table_dynamic_columns_data">
				<xsl:with-param name="index" select="$index + 1" />
				<xsl:with-param name="total" select="$total"></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- use for for loop with fixed no. of iterations -->
	<xsl:template name="table_dynamic_columns_width">
		<xsl:param name="pageWidth"/>
		<xsl:param name="index"/>
		<xsl:param name="total"/>
		<fo:table-column>
			<xsl:attribute name="column-width">
				<xsl:choose>
					<xsl:when test="$index=5">
						<!-- set custom width in cm -->
						2cm
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="format-number(($pageWidth - 5) div $total,'#.00')"></xsl:value-of>cm
					</xsl:otherwise>
				</xsl:choose>				
			</xsl:attribute>
		</fo:table-column>
		<xsl:if test="not($index = $total)">
			<xsl:call-template name="table_dynamic_columns_width">
				<xsl:with-param name="pageWidth" select="$pageWidth"/>
				<xsl:with-param name="index" select="$index + 1" />
				<xsl:with-param name="total" select="$total"></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="zero_width_space_1">
		<xsl:param name="data"/>
		<xsl:param name="counter" select="0"/>
		<xsl:choose>
		   <xsl:when test="$counter &lt; string-length($data)">
		      <xsl:value-of select='concat(substring($data,$counter,1),"&#8203;")'/>
		       	<xsl:call-template name="zero_width_space_2">
		          <xsl:with-param name="data" select="$data"/>
		          <xsl:with-param name="counter" select="$counter+1"/>
		  		</xsl:call-template>
		   </xsl:when>
		   <xsl:otherwise>
		   </xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template name="zero_width_space_2">
		<xsl:param name="data"/>
		<xsl:param name="counter"/>
		<xsl:value-of select='concat(substring($data,$counter,1),"&#8203;")'/>
	 	<xsl:call-template name="zero_width_space_1">
		    <xsl:with-param name="data" select="$data"/>
		    <xsl:with-param name="counter" select="$counter+1"/>
	  	</xsl:call-template>
	</xsl:template>

	<!-- declares common templates as they will be applied in all report stylesheets -->
	<xsl:include href="common_templates.xsl" />
</xsl:stylesheet>