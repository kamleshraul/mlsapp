<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
	xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org"
	exclude-result-prefixes="barcode common xalan">
	<!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/> 
		<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
    
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:variable name="rootNode" select="TestReportData" />

	<!-- <xsl:variable name="pageLayout" select="simple" />	-->

	<!-- declares common variables such as language & font that will be used 
		in all report stylesheets -->
	<xsl:include href="common_variables.xsl" />

	<xsl:template match="TestReportData">
		<xsl:variable name="pageSequenceMasterName">
			<xsl:choose>
				<xsl:when test="$formatOut='application/pdf'">simple</xsl:when>
				<xsl:otherwise>others</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- <xsl:text disable-output-escaping="yes"> &lt;!DOCTYPE fo:root [&lt;!ENTITY 
			nbsp "&amp;#160;"&gt;]&gt; </xsl:text> -->

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
						<!-- report name -->
						<fo:block text-align="center" font-weight="bold">
							<xsl:value-of select="testReportName"/>
						</fo:block>	
						<!-- report content -->		
						<fo:block>
							<xsl:apply-templates select="testData"/>
						</fo:block>										
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!-- declares common templates as they will be applied in all report stylesheets -->
	<xsl:include href="common_templates.xsl" />
</xsl:stylesheet>