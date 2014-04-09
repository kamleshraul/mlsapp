<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:param name="page-size" select="'A4'"/>   
    
    <xsl:variable name="language" select="$rootNode/locale"/>
    
    <xsl:variable name="formatOut" select="$rootNode/outputFormat"/>    
    
    <xsl:variable name="font">
	   <xsl:choose>
	     <xsl:when test="$language='mr_IN'">
		<xsl:choose>
		    <xsl:when test="$formatOut='application/pdf'">
			<xsl:value-of select="document('Lang.xml')/Lang/mr_IN/pdf/fontname" />
		    </xsl:when>
		    <xsl:when test="$formatOut='application/rtf'">
			<xsl:value-of select="document('Lang.xml')/Lang/mr_IN/rtf/fontname" />
		    </xsl:when>
		    <xsl:when test="$formatOut='WORD'">
			<xsl:value-of select="document('Lang.xml')/Lang/mr_IN/word/fontname" />
		    </xsl:when>
		    <xsl:when test="$formatOut='HTML'">
			<xsl:value-of select="document('Lang.xml')/Lang/mr_IN/html/fontname" />
		    </xsl:when>
		</xsl:choose>	
	     </xsl:when>
	     <xsl:when test="$language='en_US'"><xsl:value-of select="document('Lang.xml')/Lang/en_US/fontname" /></xsl:when>
	     <xsl:otherwise>Kokila</xsl:otherwise>
	   </xsl:choose>
    </xsl:variable>       
    
    <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Block-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

	  <xsl:attribute-set name="h1">
	    <xsl:attribute name="font-size">2em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">0.67em</xsl:attribute>
	    <xsl:attribute name="space-after">0.67em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="h2">
	    <xsl:attribute name="font-size">1.5em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">0.83em</xsl:attribute>
	    <xsl:attribute name="space-after">0.83em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="h3">
	    <xsl:attribute name="font-size">1.17em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="h4">
	    <xsl:attribute name="font-size">1em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">1.17em</xsl:attribute>
	    <xsl:attribute name="space-after">1.17em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="h5">
	    <xsl:attribute name="font-size">0.83em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">1.33em</xsl:attribute>
	    <xsl:attribute name="space-after">1.33em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="h6">
	    <xsl:attribute name="font-size">0.67em</xsl:attribute>
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="space-before">1.67em</xsl:attribute>
	    <xsl:attribute name="space-after">1.67em</xsl:attribute>
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
    
    <xsl:attribute-set name="p">
		    <xsl:attribute name="space-before">1em</xsl:attribute>
		    <xsl:attribute name="space-after">1em</xsl:attribute>
		    <!-- e.g.,
		    <xsl:attribute name="text-indent">1em</xsl:attribute>
		    -->
	</xsl:attribute-set>
	
	<xsl:attribute-set name="p-initial" use-attribute-sets="p">
		    <!-- initial paragraph, preceded by h1..6 or div -->
		    <!-- e.g.,
		    <xsl:attribute name="text-indent">0em</xsl:attribute>
		    -->
  	</xsl:attribute-set>
  	
  	<xsl:attribute-set name="p-initial-first" use-attribute-sets="p-initial">
    		<!-- initial paragraph, first child of div, body or td -->
  	</xsl:attribute-set>

	  <xsl:attribute-set name="blockquote">
	    <xsl:attribute name="start-indent">inherited-property-value(start-indent) + 24pt</xsl:attribute>
	    <xsl:attribute name="end-indent">inherited-property-value(end-indent) + 24pt</xsl:attribute>
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="pre">
	    <xsl:attribute name="font-size">0.83em</xsl:attribute>
	    <xsl:attribute name="font-family">monospace</xsl:attribute>
	    <xsl:attribute name="white-space">pre</xsl:attribute>
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="address">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="hr">
	    <xsl:attribute name="border">1px inset</xsl:attribute>
	    <xsl:attribute name="space-before">0.67em</xsl:attribute>
	    <xsl:attribute name="space-after">0.67em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	       List
	  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	
	  <xsl:attribute-set name="ul">
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="ul-nested">
	    <xsl:attribute name="space-before">0pt</xsl:attribute>
	    <xsl:attribute name="space-after">0pt</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="ol">
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="ol-nested">
	    <xsl:attribute name="space-before">0pt</xsl:attribute>
	    <xsl:attribute name="space-after">0pt</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="ul-li">
	    <!-- for (unordered)fo:list-item -->
	    <xsl:attribute name="relative-align">baseline</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="ol-li">
	    <!-- for (ordered)fo:list-item -->
	    <xsl:attribute name="relative-align">baseline</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="dl">
	    <xsl:attribute name="space-before">1em</xsl:attribute>
	    <xsl:attribute name="space-after">1em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="dt">
	    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
	    <xsl:attribute name="keep-together.within-column">always</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="dd">
	    <xsl:attribute name="start-indent">inherited-property-value(start-indent) + 24pt</xsl:attribute>
	  </xsl:attribute-set>
	
	  <!-- list-item-label format for each nesting level -->
	
	  <xsl:param name="ul-label-1">&#x2022;</xsl:param>
	  <xsl:attribute-set name="ul-label-1">
	    <xsl:attribute name="font">1em serif</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:param name="ul-label-2">o</xsl:param>
	  <xsl:attribute-set name="ul-label-2">
	    <xsl:attribute name="font">0.67em monospace</xsl:attribute>
	    <xsl:attribute name="baseline-shift">0.25em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:param name="ul-label-3">-</xsl:param>
	  <xsl:attribute-set name="ul-label-3">
	    <xsl:attribute name="font">bold 0.9em sans-serif</xsl:attribute>
	    <xsl:attribute name="baseline-shift">0.05em</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:param name="ol-label-1">1.</xsl:param>
	  <xsl:attribute-set name="ol-label-1"/>
	
	  <xsl:param name="ol-label-2">a.</xsl:param>
	  <xsl:attribute-set name="ol-label-2"/>
	
	  <xsl:param name="ol-label-3">i.</xsl:param>
	  <xsl:attribute-set name="ol-label-3"/>
	
	  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	       Table
	  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	
	  <xsl:attribute-set name="inside-table">
	    <!-- prevent unwanted inheritance -->
	    <xsl:attribute name="start-indent">0pt</xsl:attribute>
	    <xsl:attribute name="end-indent">0pt</xsl:attribute>
	    <xsl:attribute name="text-indent">0pt</xsl:attribute>
	    <xsl:attribute name="last-line-end-indent">0pt</xsl:attribute>
	    <xsl:attribute name="text-align">start</xsl:attribute>
	    <xsl:attribute name="text-align-last">relative</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="table-and-caption" >
	    <!-- horizontal alignment of table itself
	    <xsl:attribute name="text-align">center</xsl:attribute>
	    -->
	    <!-- vertical alignment in table-cell -->
	    <xsl:attribute name="display-align">center</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="table">
	    <xsl:attribute name="border-collapse">separate</xsl:attribute>
	    <xsl:attribute name="border-spacing">2px</xsl:attribute>
	    <xsl:attribute name="border">1px</xsl:attribute>
	    <!--
	    <xsl:attribute name="border-style">outset</xsl:attribute>
	    -->
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="table-caption" use-attribute-sets="inside-table">
	    <xsl:attribute name="text-align">center</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="table-column">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="thead" use-attribute-sets="inside-table">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="tfoot" use-attribute-sets="inside-table">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="tbody" use-attribute-sets="inside-table">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="tr">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="th">
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="text-align">center</xsl:attribute>
	    <xsl:attribute name="border">1px</xsl:attribute>
	    <!--
	    <xsl:attribute name="border-style">inset</xsl:attribute>
	    -->
	    <xsl:attribute name="padding">1px</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="td">
	    <xsl:attribute name="border">1px</xsl:attribute>
	    <!--
	    <xsl:attribute name="border-style">inset</xsl:attribute>
	    -->
	    <xsl:attribute name="padding">1px</xsl:attribute>
	  </xsl:attribute-set>
	
	  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	       Inline-level
	  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	
	  <xsl:attribute-set name="b">
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="strong">
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="strong-em">
	    <xsl:attribute name="font-weight">bold</xsl:attribute>
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="i">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="cite">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="em">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="var">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="dfn">
	    <xsl:attribute name="font-style">italic</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="tt">
	    <xsl:attribute name="font-family">monospace</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="code">
	    <xsl:attribute name="font-family">monospace</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="kbd">
	    <xsl:attribute name="font-family">monospace</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="samp">
	    <xsl:attribute name="font-family">monospace</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="big">
	    <xsl:attribute name="font-size">larger</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="small">
	    <xsl:attribute name="font-size">smaller</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="sub">
	    <xsl:attribute name="baseline-shift">sub</xsl:attribute>
	    <xsl:attribute name="font-size">smaller</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="sup">
	    <xsl:attribute name="baseline-shift">super</xsl:attribute>
	    <xsl:attribute name="font-size">smaller</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="s">
	    <xsl:attribute name="text-decoration">line-through</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="strike">
	    <xsl:attribute name="text-decoration">line-through</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="del">
	    <xsl:attribute name="text-decoration">line-through</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="u">
	    <xsl:attribute name="text-decoration">underline</xsl:attribute>
	  </xsl:attribute-set>
	  <xsl:attribute-set name="ins">
	    <xsl:attribute name="text-decoration">underline</xsl:attribute>
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="abbr">
	    <!-- e.g.,
	    <xsl:attribute name="font-variant">small-caps</xsl:attribute>
	    <xsl:attribute name="letter-spacing">0.1em</xsl:attribute>
	    -->
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="acronym">
	    <!-- e.g.,
	    <xsl:attribute name="font-variant">small-caps</xsl:attribute>
	    <xsl:attribute name="letter-spacing">0.1em</xsl:attribute>
	    -->
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="q"/>
	  <xsl:attribute-set name="q-nested"/>
	
	  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	       Image
	  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	
	  <xsl:attribute-set name="img">
	  </xsl:attribute-set>
	
	  <xsl:attribute-set name="img-link">
	    <xsl:attribute name="border">2px solid</xsl:attribute>
	  </xsl:attribute-set>
	
	  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	       Link
	  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	
	  <xsl:attribute-set name="a-link">
	    <xsl:attribute name="text-decoration">underline</xsl:attribute>
	    <xsl:attribute name="color">blue</xsl:attribute>
	  </xsl:attribute-set>
</xsl:stylesheet>