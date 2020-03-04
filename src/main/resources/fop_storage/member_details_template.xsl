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
    	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
   <fo:layout-master-set>
      <fo:simple-page-master master-name="my-page" page-height="8.5in" page-width="11in">
         <fo:region-body margin="1in" margin-top="1in" margin-bottom="1in"/>
      </fo:simple-page-master>
   </fo:layout-master-set>
   <fo:page-sequence master-reference="my-page">
      <fo:flow flow-name="xsl-region-body">
      	<xsl:variable name="houseType" select="element_3" />
      		<xsl:if test="$houseType='lowerhouse'">
      			<fo:block text-align="center" font-weight="bold" font-size="15px">
						महाराष्ट्र विधानसभा सदस्य सूची
				</fo:block>
      		</xsl:if>
      		<xsl:if test="$houseType='upperhouse'">
      			<fo:block text-align="center" font-weight="bold" font-size="15px">
						महाराष्ट्र विधानपरिषद सदस्य सूची
				</fo:block>
      		</xsl:if>
      		<fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
         <fo:table>
            <fo:table-header>
               <fo:table-row>
                  <fo:table-cell width="2cm"
                                 border="solid black 1px"
                                 padding="2px"
                                 font-weight="bold"
                                 text-align="center">
                     <fo:block>अ.क्र.</fo:block>
                  </fo:table-cell>
                  <fo:table-cell width="10cm"
                                 border="solid black 1px"
                                 padding="2px"
                                 font-weight="bold"
                                 text-align="center">
                     <fo:block>सदस्यांचे नाव</fo:block>
                  </fo:table-cell>
                  
               </fo:table-row>
            </fo:table-header>
            <fo:table-body>
               <xsl:variable name="membername" select="element_1" />
               <xsl:for-each select="element_2">
               	   <xsl:variable name="vIdxPosition" select="position()" />
	               <fo:table-row>
	                  <fo:table-cell border="solid black 1px" padding="2px">
	                     <fo:block>
	                     <xsl:value-of select="." />
	                     </fo:block>
	                  </fo:table-cell>
	                  <fo:table-cell border="solid black 1px" padding="2px">
	                     <fo:block>
	                     <xsl:value-of select="$membername[$vIdxPosition]" />
	                     </fo:block>
	                  </fo:table-cell>	                  	                                                    
	               </fo:table-row>
               </xsl:for-each>
               
            </fo:table-body>
         </fo:table>
      </fo:flow>
   </fo:page-sequence>
</fo:root>
    </xsl:template>
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
    
</xsl:stylesheet>