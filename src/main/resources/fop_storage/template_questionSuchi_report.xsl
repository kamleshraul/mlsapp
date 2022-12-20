<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:param name="page-size" select="'ltr'"/>    

    <xsl:variable name="language" select="DeviceData/locale"/>
    <xsl:variable name="formatOut" select="DeviceData/outputFormat"/>

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
	     <xsl:otherwise>FreeSerif</xsl:otherwise>
	   </xsl:choose>
    </xsl:variable>

   
   <xsl:variable name="message">
	   <xsl:choose>
	     <xsl:when test="$language='mr_IN'"><xsl:value-of select="document('Lang.xml')/Lang/mr_IN/text" /></xsl:when>
	     <xsl:when test="$language='en_US'"><xsl:value-of select="document('Lang.xml')/Lang/en_US/text" /></xsl:when>
	     <xsl:otherwise>Unknown Language</xsl:otherwise>
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

    <xsl:template match="DeviceData">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	        <fo:layout-master-set>
		
	        	<!-- <xsl:choose>
	        	  ============================================
			      Page layouts for Letter-sized paper
			      ===============================================
		          <xsl:when test="$page-size='ltr'">
		            <fo:simple-page-master master-name="first"
		              page-height="11in" page-width="8.5in"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="72pt">
		              <fo:region-body margin-bottom="50pt"/>
			      <fo:region-before region-name="rb-right" 
		                extent="3cm"/>
		              <fo:region-after region-name="ra-right" 
		                extent="25pt"/>
		            </fo:simple-page-master>
		            
		            <fo:simple-page-master master-name="left"
		              page-height="11in" page-width="8.5in"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="36pt">
		              <fo:region-body margin-top="50pt" 
		                margin-bottom="50pt"/>
		              <fo:region-before region-name="rb-left" 
		                extent="25pt"/>
		              <fo:region-after region-name="ra-left" 
		                extent="25pt"/>
		            </fo:simple-page-master>
		            
		            <fo:simple-page-master master-name="right"
		              page-height="11in" page-width="8.5in"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="36pt">
		              <fo:region-body margin-top="50pt" 
		                margin-bottom="50pt"/>
		              <fo:region-before region-name="rb-right" 
		                extent="25pt"/>
		              <fo:region-after region-name="ra-right" 
		                extent="25pt"/>
		            </fo:simple-page-master>
		          </xsl:when>
		          
		         ============================================
    			  Page layouts for A4-sized paper
    			  ===============================================
		          <xsl:otherwise>
		            <fo:simple-page-master master-name="first"
		              page-height="29.7cm" page-width="21cm"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="72pt">
		              <fo:region-body margin-top="1.5cm" 
		                margin-bottom="1.5cm"/>
			      <fo:region-before region-name="rb-right" 
		                extent="3cm"/>
		              <fo:region-after region-name="ra-right" 
		                extent="1cm"/>
		            </fo:simple-page-master>
		            
		            <fo:simple-page-master master-name="left"
		              page-height="29.7cm" page-width="21cm"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="36pt">
		              <fo:region-body margin-top="1.5cm" 
		                margin-bottom="1.5cm"/>
		              <fo:region-before region-name="rb-left" 
		                extent="3cm"/>
		              <fo:region-after region-name="ra-left" 
		                extent="1cm"/>
		            </fo:simple-page-master>
		            
		            <fo:simple-page-master master-name="right"
		              page-height="29.7cm" page-width="21cm"
		              margin-right="72pt" margin-left="72pt"
			      margin-bottom="36pt" margin-top="36pt">
		              <fo:region-body margin-top="1.5cm" 
		                margin-bottom="1.5cm"/>
		              <fo:region-before region-name="rb-right" 
		                extent="3cm"/>
		              <fo:region-after region-name="ra-right" 
		                extent="1cm"/>
		            </fo:simple-page-master>
		          </xsl:otherwise>
        		</xsl:choose> -->
        		
        		<fo:simple-page-master master-name="firstPage"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="2cm"
                  margin-bottom="2cm"
                  margin-left="2.5cm"
                  margin-right="2.5cm">
			      <fo:region-body margin-top="0cm"/>        
			      <fo:region-before extent="2cm"/>
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
		    
          
	            <fo:simple-page-master master-name="otherPages"
                  page-height="29.7cm"
                  page-width="21cm"
                  margin-top="1.5cm"
                  margin-bottom="2cm"
                  margin-left="2.5cm"
                  margin-right="2.5cm">
			      <fo:region-body margin-top="1cm"/>        
			      <fo:region-before region-name="page-number" extent="2cm"/>
			      <fo:region-after extent="1.5cm"/>
			    </fo:simple-page-master>
			    
			    <!-- ============================================
			    Now we define how we use the page layouts.  One
			    is for the first page, one is for the even-
			    numbered pages, and one is for odd-numbered pages.
			    =============================================== -->

		        <fo:page-sequence-master master-name="standard">
		          <fo:repeatable-page-master-alternatives>
		            <fo:conditional-page-master-reference 
		              master-reference="firstPage" 
		              page-position="first"/>
		            <fo:conditional-page-master-reference 
		              master-reference="otherPages" 
		              odd-or-even="even"/>
		            <fo:conditional-page-master-reference 
		              master-reference="otherPages" 
		              odd-or-even="odd"/>
		          </fo:repeatable-page-master-alternatives>
		        </fo:page-sequence-master>
		    
		        
	        </fo:layout-master-set>
	        
	        <xsl:variable name="pageSequenceVariable">
				<xsl:choose>
					<xsl:when test="$formatOut='application/pdf'">
						<xsl:value-of select="'standard'"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'otherPages'"></xsl:value-of>
					</xsl:otherwise>
				</xsl:choose>	
			</xsl:variable>
			
	        <fo:page-sequence master-reference="{$pageSequenceVariable}" id="DocumentBody">
		        <fo:static-content flow-name="page-number">	        	
		        	<fo:block font-family="Kokila" font-size="15pt" text-align="center">
		        		<fo:page-number/>		        				        		
		        	</fo:block>
		        </fo:static-content>       	        
		
		    <!-- <fo:static-content flow-name="ra-right">
			  <fo:block font-size="10pt">
			    <fo:table table-layout="fixed" inline-progression-dimension="100%">
			      <fo:table-column column-width="50%"/>
			      <fo:table-column column-width="50%"/>
			      <fo:table-body>
				<fo:table-row>
				  <fo:table-cell>
				    <fo:block text-align="start" font-family="Kokila" font-style="italic" font-weight="bold">
				      powered by महाराष्ट्र ज्ञान महामंडळ मर्यादित
				    </fo:block>
				  </fo:table-cell>
				  <fo:table-cell>
				    <fo:block text-align="end" font-family="Kokila" font-style="italic" font-weight="normal">Page 
				      <fo:page-number/> of 
				      <fo:page-number-citation-last
					ref-id="DocumentBody"/>
				    </fo:block>
				  </fo:table-cell>
				</fo:table-row>
			      </fo:table-body>
			    </fo:table>
			  </fo:block>
		    </fo:static-content>

		    <fo:static-content flow-name="ra-left">
			  <fo:block font-size="10pt">
			    <fo:table table-layout="fixed" inline-progression-dimension="100%">
			      <fo:table-column column-width="50%"/>
			      <fo:table-column column-width="50%"/>
			      <fo:table-body>
				<fo:table-row>
				  <fo:table-cell>
				    <fo:block text-align="start" font-family="Kokila" font-style="italic" font-weight="normal">Page 
				      <fo:page-number/> 
				      of <fo:page-number-citation-last 
				      ref-id="DocumentBody"/>
				    </fo:block>
				  </fo:table-cell>
				  <fo:table-cell>
				    <fo:block text-align="end" font-family="Kokila" font-style="italic" font-weight="bold">
				      powered by महाराष्ट्र ज्ञान महामंडळ मर्यादित
				    </fo:block>
				  </fo:table-cell>
				</fo:table-row>
			      </fo:table-body>
			    </fo:table> 
			  </fo:block>
		    </fo:static-content>

		    <fo:static-content flow-name="rb-right">
			<fo:block font-size="10pt" text-align="start" font-family="Kokila" font-style="italic" font-weight="bold">
			    ई-विधानमंडळ
			</fo:block>
		    </fo:static-content>

		    <fo:static-content flow-name="rb-left">
			<fo:block font-size="10pt" text-align="start" font-family="Kokila" font-style="italic" font-weight="bold">
			    ई-विधानमंडळ
			</fo:block>
		    </fo:static-content> -->
	
	            <fo:flow flow-name="xsl-region-body">		    
					<fo:block font-family="Kokila" font-size="17pt" font-weight="normal" font-style="normal" space-after.optimum="3pt" text-align="justify">
					    <fo:block font-size="24pt" font-weight="bold" text-align="center">
							महाराष्ट्र <xsl:value-of select="houseType"/>												
						</fo:block>
						<fo:block font-size="18pt" font-weight="bold" text-align="center">
							<xsl:choose>
								<xsl:when test="sessionNumber = 1">
									पहिले
								</xsl:when>
								<xsl:when test="sessionNumber = 2">
									दुसरे
								</xsl:when>
								<xsl:when test="sessionNumber = 3">
									तिसरे
								</xsl:when>
								<xsl:when test="sessionNumber = 4">
									चौथे
								</xsl:when>
								<xsl:when test="sessionNumber = 5">
									पाचवे
								</xsl:when>
								<xsl:when test="sessionNumber = 6">
									सहावे
								</xsl:when>
								<xsl:when test="sessionNumber = 7">
									सातवे
								</xsl:when>
							</xsl:choose>
							अधिवेशन, <xsl:value-of select="sessionYear"/>																						
						</fo:block>		
						<fo:block text-align="center" font-weight="bold" font-size="12pt">
							-----------------------------
						</fo:block>		
						<fo:block text-align="center" font-weight="bold" font-size="18pt">
	                		<xsl:value-of select="displayAnsweringDate"/> / <xsl:value-of select="displayAnsweringDateInIndianCalendar"/> ( शके ) रोजीच्या
	                	</fo:block>	                	             	
	                	<fo:block text-align="center" font-size="18pt" font-weight="bold">
	                		तारांकित प्रश्नोत्तरांच्या यादीत समाविष्ट करण्यात आलेल्या प्रश्नांची सूची
	                	</fo:block>
	                	<fo:block font-size="4pt">&#160;</fo:block>        	           	
	                	<fo:block text-align="center" font-weight="bold">
	                		<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
	                			<fo:table-column column-number="1" column-width="2cm" />
		                        <fo:table-column column-number="2" column-width="9cm" />
		                        <fo:table-column column-number="3" column-width="2cm" />
		                        <fo:table-column column-number="4" column-width="3cm" />
			     				<fo:table-body>
			     					<xsl:for-each select="./ministryVOs/ministryVO" >
			     						<xsl:variable name="lastRowNumber">
		     								<xsl:value-of select="last()" />
		     							</xsl:variable>		     							
			     						<xsl:choose>
				     						<xsl:when test="position()=1">
				     							<fo:table-row border-collapse="collapse">
				     								<fo:table-cell>
					     								<fo:block>
					     									(<xsl:value-of select="number" />)
					     								</fo:block>
					     							</fo:table-cell>
					     							<fo:table-cell padding-left="5">
					     								<fo:block text-align="left">
					     									<xsl:value-of select="name" />
					     								</fo:block>
					     							</fo:table-cell>				     								     								
				     								<fo:table-cell display-align="center">
				     									<xsl:attribute name="number-rows-spanned">
												             <xsl:value-of select="$lastRowNumber" />
												       </xsl:attribute>
				     									<!-- <fo:block>
				     										<xsl:attribute name="font-size">
												             <xsl:value-of select="$lastRowNumber*14" />pt
												       		</xsl:attribute>											       													             
				     										}
				     									</fo:block> -->
				     									<fo:block>
														    <fo:external-graphic src="../../../resources/images/brace.JPG"  content-height="100"  content-width="70" />
														</fo:block>
				     								</fo:table-cell>
				     								<fo:table-cell display-align="center">
				     									<xsl:attribute name="number-rows-spanned">
												             <xsl:value-of select="$lastRowNumber" />
												       </xsl:attribute>
				     									<fo:block text-align="left">
				     										यांचे प्रभारी विभाग
				     									</fo:block>
				     								</fo:table-cell>
				     							</fo:table-row>
				     						</xsl:when>
				     						<xsl:otherwise>
					     						<fo:table-row border-collapse="collapse">			     						
					     							<fo:table-cell>
					     								<fo:block>
					     									(<xsl:value-of select="number" />)
					     								</fo:block>
					     							</fo:table-cell>
					     							<fo:table-cell padding-left="5">
					     								<fo:block text-align="left">
					     									<xsl:value-of select="name" />
					     								</fo:block>
					     							</fo:table-cell>		     										     							
					     						</fo:table-row>
				     						</xsl:otherwise>	
			     						</xsl:choose>		     							
			     					</xsl:for-each>			     					
			     				</fo:table-body>
	                		</fo:table>
	                	</fo:block>
	                	<fo:block font-size="17pt">&#160;</fo:block>
	                	<fo:block text-align="center" font-size="17pt" font-weight="bold">	 
	                		<fo:block font-size="18pt">प्रश्नांची एकूण संख्या - <xsl:value-of select="totalNumberOfDevices"/></fo:block>
	                		<fo:block font-size="3pt">&#160;</fo:block>
	                		<xsl:for-each select="./roundVOs/roundVO" >
	                			<xsl:choose>
	                				<xsl:when test="position()=1">
	                					पहिल्या
	                				</xsl:when>
	                				<xsl:when test="position()=2">
	                					दुसऱ्या
	                				</xsl:when>
	                				<xsl:when test="position()=3">
	                					तिसऱ्या
	                				</xsl:when>
	                				<xsl:when test="position()=4">
	                					चौथ्या
	                				</xsl:when>
	                				<xsl:when test="position()=5">
	                					पाचव्या
	                				</xsl:when>
	                				<xsl:when test="position()=6">
	                					सहाव्या
	                				</xsl:when>
	                				<xsl:when test="position()=7">
	                					सातव्या&#160;
	                				</xsl:when>
	                				<xsl:when test="position()=8">
	                					आठव्या&#160;
	                				</xsl:when>
	                				<xsl:when test="position()=9">
	                					नवव्या&#160;
	                				</xsl:when>
	                				<xsl:when test="position()=10">
	                					दहाव्या&#160;
	                				</xsl:when>
	                			</xsl:choose>
	                			 फेरीतील प्रश्नांची संख्या - <xsl:value-of select="formattedNumberOfQuestionsInGivenRound"/> [
	                			 <xsl:choose>
	                			 	<xsl:when test="firstElementInGivenRound = lastElementInGivenRound"><xsl:value-of select="firstElementInGivenRound"/></xsl:when>
	                			 	<xsl:otherwise><xsl:value-of select="firstElementInGivenRound"/> ते <xsl:value-of select="lastElementInGivenRound"/></xsl:otherwise>
	                			 </xsl:choose>	                			 
	                			 ]
	                			<fo:block font-size="3pt">&#160;</fo:block>
	                		</xsl:for-each>	                		
	                		<fo:block font-size="7pt" margin-left="2cm">	                		
	                		<!-- <fo:block font-size="0pt">&#160;</fo:block> -->
	                		<fo:block font-size="17pt">एकूण - <xsl:value-of select="totalNumberOfDevices"/></fo:block>
	                		<!-- <fo:block font-size="0pt">&#160;</fo:block> -->
	                		--------------------
	                		</fo:block>
	                		<fo:block font-size="4pt">&#160;</fo:block>               		
	                		<fo:block font-size="17pt">प्रश्नांचा तपशील </fo:block>	                		
	                	</fo:block>
	                	<fo:block font-size="10pt">&#160;</fo:block>
	                	<xsl:for-each select="./roundVOs/roundVO" >
	                		<fo:block text-align="center" font-size="17pt" font-weight="bold">
	                			<xsl:choose>
	                				<xsl:when test="position()=1">
	                					पहिली फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=2">
	                					दुसरी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=3">
	                					तिसरी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=4">
	                					चौथी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=5">
	                					पाचवी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=6">
	                					सहावी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=7">
	                					सातवी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=8">
	                					आठवी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=9">
	                					नववी फेरी
	                				</xsl:when>
	                				<xsl:when test="position()=10">
	                					दहावी फेरी
	                				</xsl:when>
	                			</xsl:choose>
	                		</fo:block>
	                		<fo:block font-size="3pt">&#160;</fo:block>
	                		<fo:block text-align="center" font-weight="bold">
		                		<fo:table table-layout="fixed" width="100%">
		                			<fo:table-column column-number="1" column-width="1cm" />
			                        <fo:table-column column-number="2" column-width="2cm" />
			                        <fo:table-column column-number="3" column-width="6.5cm" />
			                        <fo:table-column column-number="4" column-width="6.5cm" />
			                        <fo:table-header>
			                        	<fo:table-row background-color="green">
			                        		<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
				                        	   	<fo:block text-align="center" font-weight="bold">
		                                                                    अ. क्र.
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                                                    प्रश्न क्रमांक
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                                                    सदस्यांचे नांव
			                                    </fo:block>
				                        	</fo:table-cell>
				                        	<fo:table-cell display-align="center" border-width="0.5pt" border-style="solid" color="white">
				                        	    <fo:block text-align="center" font-weight="bold">
		                                                                    विषय
			                                    </fo:block>
				                        	</fo:table-cell>
			                        	</fo:table-row>
			                        </fo:table-header>
			                        <fo:table-body>	                        	                    	
		                            	<xsl:for-each select="./deviceVOs" >
			                            	<fo:table-row border="solid 0.1mm black">	                                	
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
			                                        <fo:block  text-align="center">
			                                        	<xsl:value-of select="serialNumber" />
			                                        </fo:block>
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid">
		                                            <fo:block  text-align="center">
			                                        	<xsl:value-of select="formattedNumber" />
			                                        </fo:block>
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5">
			                                        <fo:block  text-align="left">
			                                        	<xsl:value-of select="memberNames" />
			                                        </fo:block> 
			                                    </fo:table-cell>		                                    
			                                    <fo:table-cell display-align="before" border-width="0.5pt" border-style="solid" padding-left="5">
			                                        <fo:block  text-align="left">
			                                        	<xsl:value-of select="subject" />
			                                        </fo:block>
			                                    </fo:table-cell>                                   	
			                                </fo:table-row>	                               
		                            </xsl:for-each>	
		                        	</fo:table-body>
		                		</fo:table>
	                		</fo:block>
	                		<fo:block font-size="10pt">&#160;</fo:block>
	                	</xsl:for-each>	                	
	                	<fo:block font-size="20pt">&#160;</fo:block>
	                	<xsl:choose>
	                		<xsl:when test="$formatOut='application/pdf'">
	                			<fo:block font-size="17pt">
			                		विधान भवन : 
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;	                			                		                		
			                		<fo:inline font-weight="bold">
										<xsl:variable name="psName" select="userName"/>
										<xsl:variable name="psNameTitle" select="'श्री.'"/>
											<xsl:choose>
												<xsl:when test="starts-with($psName,$psNameTitle)">
													<fo:block text-align="center">
														<xsl:value-of select="substring-after($psName,$psNameTitle)"/>
													</fo:block>
												
												</xsl:when>
												<xsl:otherwise>
													<fo:block text-align="center">
														<xsl:value-of select="userName"/>
													</fo:block>
												</xsl:otherwise>
											</xsl:choose>
			                			<!-- <xsl:value-of select="userName"/> -->
			                		</fo:inline>
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<xsl:value-of select="sessionPlace"/>.
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;
			                		<xsl:value-of select="userRole"/>,
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		दिनांक: <xsl:value-of select="reportDate"/>
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		&#160;&#160;&#160;&#160;
			                		महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
			                		<!-- <fo:block text-align="right">
			                			महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
			                		</fo:block> -->
			                	</fo:block>
	                		</xsl:when>
	                		<xsl:when test="$formatOut='WORD'">
	                			<fo:block font-size="17pt" font-weight="bold" text-align="left">
	                				<fo:table border-collapse="collapse" table-layout="fixed" width="100%">
	                					<fo:table-column column-number="1" column-width="5cm" />
				                        <fo:table-column column-number="2" column-width="6cm" />
				                        <fo:table-column column-number="3" column-width="5cm" />
				                        <fo:table-body>
				                        	<fo:table-row border-collapse="collapse">
				                        		<fo:table-cell>
				     								<fo:block text-align="left">
				     									विधान भवन :
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block>
				     									&#160;
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
													<xsl:variable name="psName"
																select="userName"/>
													<xsl:variable name="psNameTitle"
																select="'श्री.'"/>
													<xsl:choose>
														<xsl:when test="starts-with($psName,$psNameTitle)">
															<fo:block text-align="center">
																<xsl:value-of select="substring-after($psName,$psNameTitle)"/>
															</fo:block>
														
														</xsl:when>
														<xsl:otherwise>
															<fo:block text-align="center">
																<xsl:value-of select="userName"/>
															</fo:block>
														</xsl:otherwise>
													</xsl:choose>
													<!--
				     								<fo:block text-align="center">
														<xsl:value-of select="userName"/>
													</fo:block>-->
													
													
				     							</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row border-collapse="collapse">
				                        		<fo:table-cell>
				     								<fo:block text-align="left">
				     									<xsl:value-of select="sessionPlace"/>.
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block>
				     									&#160;
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block text-align="center">
				     									<xsl:value-of select="userRole"/>,
				     								</fo:block>				     								
				     							</fo:table-cell>
				                        	</fo:table-row>
				                        	<fo:table-row border-collapse="collapse">
				                        		<fo:table-cell>
				     								<fo:block text-align="left">
				     									दिनांक : <xsl:value-of select="reportDate"/>
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block>
				     									&#160;
				     								</fo:block>
				     							</fo:table-cell>
				     							<fo:table-cell>
				     								<fo:block text-align="center">
				     									महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
				     								</fo:block>
				     							</fo:table-cell>
				                        	</fo:table-row>
				                        </fo:table-body>
	                				</fo:table>
			                		<!-- विधान भवन : 
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
			                		         			                		                		
			                		<fo:inline font-weight="bold">
			                			<xsl:value-of select="userName"/>
			                		</fo:inline>
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<xsl:value-of select="sessionPlace"/>.
			                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;			                		
			                		<xsl:value-of select="userRole"/>,
			                		<fo:block font-size="0pt">&#160;</fo:block>
			                		<fo:block text-align="right">
			                			महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
			                		</fo:block> -->
			                	</fo:block>
			                	<fo:block>____________________________________________________________________</fo:block>
			                	<fo:block text-align="center" font-size="15pt">
			                		मुद्रणपूर्व सर्व प्रक्रिया महाराष्ट्र विधानमंडळ सचिवालयाच्या संगणक यंत्रणेवर 
			                	</fo:block>
			                	<fo:block></fo:block>
				     			<fo:block text-align="center" font-size="15pt">
				     				मुद्रण: शासकीय मध्यवर्ती मुद्रणालय, <xsl:value-of select="sessionPlace"/>.
				     			</fo:block>
	                		</xsl:when>
	                	</xsl:choose>	                	                		                	
	                </fo:block>       
	            </fo:flow>
	            
	        </fo:page-sequence>
	    </fo:root>
    </xsl:template>   

    <!-- Apply templates to selected elements for rich text html formatting preserved  -->
	<!-- <xsl:template match="rotationOrderMainCover/rotationOrderMainHeader">	
		<xsl:call-template name="br_template"></xsl:call-template>
    	<xsl:apply-templates/>
  	</xsl:template> -->
  	
  	<!-- ============================================
    We handle a break element by inserting an 
    empty <fo:block>.
    =============================================== -->    
    <xsl:template match="br">     	
    	<fo:block></fo:block>
    </xsl:template>     
  	
  	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
   process common attributes and children
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->
	  <xsl:template name="process-common-attributes-and-children">
	    <xsl:call-template name="process-common-attributes"/>
	    <xsl:apply-templates/>
	  </xsl:template>
  	
  	<xsl:template name="process-common-attributes">
  		<xsl:attribute name="role">
      		<xsl:value-of select="concat('html:', local-name())"/>
    	</xsl:attribute>

    	<xsl:choose>
      		<xsl:when test="@xml:lang">
        		<xsl:attribute name="xml:lang">
          			<xsl:value-of select="@xml:lang"/>
        		</xsl:attribute>
      		</xsl:when>
      		<xsl:when test="@lang">
       			<xsl:attribute name="xml:lang">
          			<xsl:value-of select="@lang"/>
        		</xsl:attribute>
      		</xsl:when>
    	</xsl:choose>

	    <xsl:choose>
	      <xsl:when test="@id">
	        <xsl:attribute name="id">
	          <xsl:value-of select="@id"/>
	        </xsl:attribute>
	      </xsl:when>
	      <xsl:when test="self::html:a/@name">
	        <xsl:attribute name="id">
	          <xsl:value-of select="@name"/>
	        </xsl:attribute>
	      </xsl:when>
	    </xsl:choose>

	    <xsl:if test="@align">
	      <xsl:choose>
	        <xsl:when test="self::html:caption">
	        </xsl:when>
	        <xsl:when test="self::html:img or self::html:object">
	          <xsl:if test="@align = 'bottom' or @align = 'middle' or @align = 'top'">
	            <xsl:attribute name="vertical-align">
	              <xsl:value-of select="@align"/>
	            </xsl:attribute>
	          </xsl:if>
	        </xsl:when>
	        <xsl:otherwise>
	          <xsl:call-template name="process-cell-align">
	            <xsl:with-param name="align" select="@align"/>
	          </xsl:call-template>
	        </xsl:otherwise>
	      </xsl:choose>
	    </xsl:if>
	    
	    <xsl:if test="@valign">
	      <xsl:call-template name="process-cell-valign">
	        <xsl:with-param name="valign" select="@valign"/>
	      </xsl:call-template>
	    </xsl:if>

	    <xsl:if test="@style">
	      <xsl:call-template name="process-style">
	        <xsl:with-param name="style" select="@style"/>
	      </xsl:call-template>
	    </xsl:if>
    
  	</xsl:template>  	
  	
  	<!-- Style Attribute Handling-->
  	<xsl:template name="process-style">
	    <xsl:param name="style"/>
	    <!-- e.g., style="text-align: center; color: red"
	         converted to text-align="center" color="red" -->
	    <xsl:variable name="name"
	                  select="normalize-space(substring-before($style, ':'))"/>
	    <xsl:if test="$name">
	      <xsl:variable name="value-and-rest"
	                    select="normalize-space(substring-after($style, ':'))"/>
	      <xsl:variable name="value">
	        <xsl:choose>
	          <xsl:when test="contains($value-and-rest, ';')">
	            <xsl:value-of select="normalize-space(substring-before(
	                                  $value-and-rest, ';'))"/>
	          </xsl:when>
	          <xsl:otherwise>
	            <xsl:value-of select="$value-and-rest"/>
	          </xsl:otherwise>
	        </xsl:choose>
	      </xsl:variable>
	      <xsl:choose>
	        <xsl:when test="$name = 'width' and (self::html:col or self::html:colgroup)">
	          <xsl:attribute name="column-width">
	            <xsl:value-of select="$value"/>
	          </xsl:attribute>
	        </xsl:when>
	        <xsl:when test="$name = 'vertical-align' and (
	                                 self::html:table or self::html:caption or
	                                 self::html:thead or self::html:tfoot or
	                                 self::html:tbody or self::html:colgroup or
	                                 self::html:col or self::html:tr or
	                                 self::html:th or self::html:td)">
	          <xsl:choose>
	            <xsl:when test="$value = 'top'">
	              <xsl:attribute name="display-align">before</xsl:attribute>
	            </xsl:when>
	            <xsl:when test="$value = 'bottom'">
	              <xsl:attribute name="display-align">after</xsl:attribute>
	            </xsl:when>
	            <xsl:when test="$value = 'middle'">
	              <xsl:attribute name="display-align">center</xsl:attribute>
	            </xsl:when>
	            <xsl:otherwise>
	              <xsl:attribute name="display-align">auto</xsl:attribute>
	              <xsl:attribute name="relative-align">baseline</xsl:attribute>
	            </xsl:otherwise>
	          </xsl:choose>
	        </xsl:when>
	        <xsl:otherwise>
	          <xsl:attribute name="{$name}">
	            <xsl:value-of select="$value"/>
	          </xsl:attribute>
	        </xsl:otherwise>
	      </xsl:choose>
	    </xsl:if>
	    <xsl:variable name="rest"
	                  select="normalize-space(substring-after($style, ';'))"/>
	    <xsl:if test="$rest">
	      <xsl:call-template name="process-style">
	        <xsl:with-param name="style" select="$rest"/>
	      </xsl:call-template>
	    </xsl:if>    
  	</xsl:template>
  	
  	<!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Block-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="h1">
    <fo:block xsl:use-attribute-sets="h1">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="h2">
    <fo:block xsl:use-attribute-sets="h2">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="h3">
    <fo:block xsl:use-attribute-sets="h3">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="h4">
    <fo:block xsl:use-attribute-sets="h4">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="h5">
    <fo:block xsl:use-attribute-sets="h5">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="h6">
    <fo:block xsl:use-attribute-sets="h6">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="p">
    <fo:block xsl:use-attribute-sets="p">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <!-- initial paragraph, preceded by h1..6 or div -->
  <xsl:template match="p[preceding-sibling::*[1][
                       self::html:h1 or self::html:h2 or self::html:h3 or
                       self::html:h4 or self::html:h5 or self::html:h6 or
                       self::html:div]]">
    <fo:block xsl:use-attribute-sets="p-initial">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <!-- initial paragraph, first child of div, body or td -->
  <xsl:template match="p[not(preceding-sibling::*) and (
                       parent::html:div or parent::html:body or
                       parent::html:td)]">
    <fo:block xsl:use-attribute-sets="p-initial-first">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="blockquote">
    <fo:block xsl:use-attribute-sets="blockquote">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="pre">
    <fo:block xsl:use-attribute-sets="pre">
      <xsl:call-template name="process-pre"/>
    </fo:block>
  </xsl:template>

  <xsl:template name="process-pre">
    <xsl:call-template name="process-common-attributes"/>
    <!-- remove leading CR/LF/CRLF char -->
    <xsl:variable name="crlf"><xsl:text>&#xD;&#xA;</xsl:text></xsl:variable>
    <xsl:variable name="lf"><xsl:text>&#xA;</xsl:text></xsl:variable>
    <xsl:variable name="cr"><xsl:text>&#xD;</xsl:text></xsl:variable>
    <xsl:for-each select="node()">
      <xsl:choose>
        <xsl:when test="position() = 1 and self::text()">
          <xsl:choose>
            <xsl:when test="starts-with(., $lf)">
              <xsl:value-of select="substring(., 2)"/>
            </xsl:when>
            <xsl:when test="starts-with(., $crlf)">
              <xsl:value-of select="substring(., 3)"/>
            </xsl:when>
            <xsl:when test="starts-with(., $cr)">
              <xsl:value-of select="substring(., 2)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="."/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="address">
    <fo:block xsl:use-attribute-sets="address">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="hr">
    <fo:block xsl:use-attribute-sets="hr">
      <xsl:call-template name="process-common-attributes"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="div">
    <!-- need fo:block-container? or normal fo:block -->
    <xsl:variable name="need-block-container">
      <xsl:call-template name="need-block-container"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$need-block-container = 'true'">
        <fo:block-container>
          <xsl:if test="@dir">
            <xsl:attribute name="writing-mode">
              <xsl:choose>
                <xsl:when test="@dir = 'rtl'">rl-tb</xsl:when>
                <xsl:otherwise>lr-tb</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </xsl:if>
          <xsl:call-template name="process-common-attributes"/>
          <fo:block start-indent="0pt" end-indent="0pt">
            <xsl:apply-templates/>
          </fo:block>
        </fo:block-container>
      </xsl:when>
      <xsl:otherwise>
        <!-- normal block -->
        <fo:block>
          <xsl:call-template name="process-common-attributes"/>
          <xsl:apply-templates/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="need-block-container">
    <xsl:choose>
      <xsl:when test="@dir">true</xsl:when>
      <xsl:when test="@style">
        <xsl:variable name="s"
                      select="concat(';', translate(normalize-space(@style),
                                                    ' ', ''))"/>
        <xsl:choose>
          <xsl:when test="contains($s, ';width:') or
                          contains($s, ';height:') or
                          contains($s, ';position:absolute') or
                          contains($s, ';position:fixed') or
                          contains($s, ';writing-mode:')">true</xsl:when>
          <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="center">
    <fo:block text-align="center">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="fieldset | form | dir | menu">
    <fo:block space-before="1em" space-after="1em">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       List
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="ul">
    <fo:list-block xsl:use-attribute-sets="ul">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="li//ul">
    <fo:list-block xsl:use-attribute-sets="ul-nested">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="ol">
    <fo:list-block xsl:use-attribute-sets="ol">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="li//ol">
    <fo:list-block xsl:use-attribute-sets="ol-nested">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:list-block>
  </xsl:template>

  <xsl:template match="ul/li">
    <fo:list-item xsl:use-attribute-sets="ul-li">
      <xsl:call-template name="process-ul-li"/>
    </fo:list-item>
  </xsl:template>

  <xsl:template name="process-ul-li">
    <xsl:call-template name="process-common-attributes"/>
    <fo:list-item-label end-indent="label-end()"
                        text-align="end" wrap-option="no-wrap">
      <fo:block>
        <xsl:variable name="depth" select="count(ancestor::html:ul)" />
        <xsl:choose>
          <xsl:when test="$depth = 1">
            <fo:inline xsl:use-attribute-sets="ul-label-1">
              <xsl:value-of select="$ul-label-1"/>
            </fo:inline>
          </xsl:when>
          <xsl:when test="$depth = 2">
            <fo:inline xsl:use-attribute-sets="ul-label-2">
              <xsl:value-of select="$ul-label-2"/>
            </fo:inline>
          </xsl:when>
          <xsl:otherwise>
            <fo:inline xsl:use-attribute-sets="ul-label-3">
              <xsl:value-of select="$ul-label-3"/>
            </fo:inline>
          </xsl:otherwise>
        </xsl:choose>
      </fo:block>
    </fo:list-item-label>
    <fo:list-item-body start-indent="body-start()">
      <fo:block>
        <xsl:apply-templates/>
      </fo:block>
    </fo:list-item-body>
  </xsl:template>

  <xsl:template match="ol/li">
    <fo:list-item xsl:use-attribute-sets="ol-li">
      <xsl:call-template name="process-ol-li"/>
    </fo:list-item>
  </xsl:template>

  <xsl:template name="process-ol-li">
    <xsl:call-template name="process-common-attributes"/>
    <fo:list-item-label end-indent="label-end()"
                        text-align="end" wrap-option="no-wrap">
      <fo:block>
        <xsl:variable name="depth" select="count(ancestor::html:ol)" />
        <xsl:choose>
          <xsl:when test="$depth = 1">
            <fo:inline xsl:use-attribute-sets="ol-label-1">
              <xsl:number format="{$ol-label-1}"/>
            </fo:inline>
          </xsl:when>
          <xsl:when test="$depth = 2">
            <fo:inline xsl:use-attribute-sets="ol-label-2">
              <xsl:number format="{$ol-label-2}"/>
            </fo:inline>
          </xsl:when>
          <xsl:otherwise>
            <fo:inline xsl:use-attribute-sets="ol-label-3">
              <xsl:number format="{$ol-label-3}"/>
            </fo:inline>
          </xsl:otherwise>
        </xsl:choose>
      </fo:block>
    </fo:list-item-label>
    <fo:list-item-body start-indent="body-start()">
      <fo:block>
        <xsl:apply-templates/>
      </fo:block>
    </fo:list-item-body>
  </xsl:template>

  <xsl:template match="dl">
    <fo:block xsl:use-attribute-sets="dl">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="dt">
    <fo:block xsl:use-attribute-sets="dt">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <xsl:template match="dd">
    <fo:block xsl:use-attribute-sets="dd">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:block>
  </xsl:template>

  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Table
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="table">
    <fo:table-and-caption xsl:use-attribute-sets="table-and-caption">
      <xsl:call-template name="make-table-caption"/>
      <fo:table xsl:use-attribute-sets="table">
        <xsl:call-template name="process-table"/>
      </fo:table>
    </fo:table-and-caption>
  </xsl:template>

  <xsl:template name="make-table-caption">
    <xsl:if test="caption/@align">
      <xsl:attribute name="caption-side">
        <xsl:value-of select="html:caption/@align"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates select="html:caption"/>
  </xsl:template>

  <xsl:template name="process-table">
    <xsl:if test="@width">
      <xsl:attribute name="inline-progression-dimension">
        <xsl:choose>
          <xsl:when test="contains(@width, '%')">
            <xsl:value-of select="@width"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@width"/>px</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@border or @frame">
      <xsl:choose>
        <xsl:when test="@border &gt; 0">
          <xsl:attribute name="border">
            <xsl:value-of select="@border"/>px</xsl:attribute>
        </xsl:when>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="@border = '0' or @frame = 'void'">
          <xsl:attribute name="border-style">hidden</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'above'">
          <xsl:attribute name="border-style">outset hidden hidden hidden</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'below'">
          <xsl:attribute name="border-style">hidden hidden outset hidden</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'hsides'">
          <xsl:attribute name="border-style">outset hidden</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'vsides'">
          <xsl:attribute name="border-style">hidden outset</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'lhs'">
          <xsl:attribute name="border-style">hidden hidden hidden outset</xsl:attribute>
        </xsl:when>
        <xsl:when test="@frame = 'rhs'">
          <xsl:attribute name="border-style">hidden outset hidden hidden</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="border-style">outset</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="@cellspacing">
      <xsl:attribute name="border-spacing">
        <xsl:value-of select="@cellspacing"/>px</xsl:attribute>
      <xsl:attribute name="border-collapse">separate</xsl:attribute>
    </xsl:if>
    <xsl:if test="@rules and (@rules = 'groups' or
                      @rules = 'rows' or
                      @rules = 'cols' or
                      @rules = 'all' and (not(@border or @frame) or
                          @border = '0' or @frame and
                          not(@frame = 'box' or @frame = 'border')))">
      <xsl:attribute name="border-collapse">collapse</xsl:attribute>
      <xsl:if test="not(@border or @frame)">
        <xsl:attribute name="border-style">hidden</xsl:attribute>
      </xsl:if>
    </xsl:if>
    <xsl:call-template name="process-common-attributes"/>
    <xsl:apply-templates select="col | colgroup"/>
    <xsl:apply-templates select="thead"/>
    <xsl:apply-templates select="tfoot"/>
    <xsl:choose>
      <xsl:when test="tbody">
        <xsl:apply-templates select="tbody"/>
      </xsl:when>
      <xsl:otherwise>
        <fo:table-body xsl:use-attribute-sets="tbody">
          <xsl:apply-templates select="tr"/>
        </fo:table-body>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="caption">
    <fo:table-caption xsl:use-attribute-sets="table-caption">
      <xsl:call-template name="process-common-attributes"/>
      <fo:block>
        <xsl:apply-templates/>
      </fo:block>
    </fo:table-caption>
  </xsl:template>

  <xsl:template match="thead">
    <fo:table-header xsl:use-attribute-sets="thead">
      <xsl:call-template name="process-table-rowgroup"/>
    </fo:table-header>
  </xsl:template>

  <xsl:template match="tfoot">
    <fo:table-footer xsl:use-attribute-sets="tfoot">
      <xsl:call-template name="process-table-rowgroup"/>
    </fo:table-footer>
  </xsl:template>

  <xsl:template match="tbody">
    <fo:table-body xsl:use-attribute-sets="tbody">
      <xsl:call-template name="process-table-rowgroup"/>
    </fo:table-body>
  </xsl:template>

  <xsl:template name="process-table-rowgroup">
    <xsl:if test="ancestor::html:table[1]/@rules = 'groups'">
      <xsl:attribute name="border">1px solid</xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-common-attributes-and-children"/>
  </xsl:template>

  <xsl:template match="colgroup">
    <fo:table-column xsl:use-attribute-sets="table-column">
      <xsl:call-template name="process-table-column"/>
    </fo:table-column>
  </xsl:template>

  <xsl:template match="colgroup[col]">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="col">
    <fo:table-column xsl:use-attribute-sets="table-column">
      <xsl:call-template name="process-table-column"/>
    </fo:table-column>
  </xsl:template>

  <xsl:template name="process-table-column">
    <xsl:if test="parent::html:colgroup">
      <xsl:call-template name="process-col-width">
        <xsl:with-param name="width" select="../@width"/>
      </xsl:call-template>
      <xsl:call-template name="process-cell-align">
        <xsl:with-param name="align" select="../@align"/>
      </xsl:call-template>
      <xsl:call-template name="process-cell-valign">
        <xsl:with-param name="valign" select="../@valign"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="@span">
      <xsl:attribute name="number-columns-repeated">
        <xsl:value-of select="@span"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-col-width">
      <xsl:with-param name="width" select="@width"/>
      <!-- it may override parent colgroup's width -->
    </xsl:call-template>
    <xsl:if test="ancestor::html:table[1]/@rules = 'cols'">
      <xsl:attribute name="border">1px solid</xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-common-attributes"/>
    <!-- this processes also align and valign -->
  </xsl:template>

  <xsl:template match="tr">
    <fo:table-row xsl:use-attribute-sets="tr">
      <xsl:call-template name="process-table-row"/>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="tr[parent::html:table and html:th and not(html:td)]">
    <fo:table-row xsl:use-attribute-sets="tr" keep-with-next="always">
      <xsl:call-template name="process-table-row"/>
    </fo:table-row>
  </xsl:template>

  <xsl:template name="process-table-row">
    <xsl:if test="ancestor::html:table[1]/@rules = 'rows'">
      <xsl:attribute name="border">1px solid</xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-common-attributes-and-children"/>
  </xsl:template>

  <xsl:template match="th">
    <fo:table-cell xsl:use-attribute-sets="th">
      <xsl:call-template name="process-table-cell"/>
    </fo:table-cell>
  </xsl:template>

  <xsl:template match="td">
    <fo:table-cell xsl:use-attribute-sets="td">
      <xsl:call-template name="process-table-cell"/>
    </fo:table-cell>
  </xsl:template>

  <xsl:template name="process-table-cell">
    <xsl:if test="@colspan">
      <xsl:attribute name="number-columns-spanned">
        <xsl:value-of select="@colspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@rowspan">
      <xsl:attribute name="number-rows-spanned">
        <xsl:value-of select="@rowspan"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:for-each select="ancestor::html:table[1]">
      <xsl:if test="(@border or @rules) and (@rules = 'all' or
                    not(@rules) and not(@border = '0'))">
        <xsl:attribute name="border-style">inset</xsl:attribute>
      </xsl:if>
      <xsl:if test="@cellpadding">
        <xsl:attribute name="padding">
          <xsl:choose>
            <xsl:when test="contains(@cellpadding, '%')">
              <xsl:value-of select="@cellpadding"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@cellpadding"/>px</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(@align or ../@align or
                      ../parent::*[self::html:thead or self::html:tfoot or
                      self::html:tbody]/@align) and
                  ancestor::html:table[1]/*[self::html:col or
                      self::html:colgroup]/descendant-or-self::*/@align">
      <xsl:attribute name="text-align">from-table-column()</xsl:attribute>
    </xsl:if>
    <xsl:if test="not(@valign or ../@valign or
                      ../parent::*[self::html:thead or self::html:tfoot or
                      self::html:tbody]/@valign) and
                  ancestor::html:table[1]/*[self::html:col or
                      self::html:colgroup]/descendant-or-self::*/@valign">
      <xsl:attribute name="display-align">from-table-column()</xsl:attribute>
      <xsl:attribute name="relative-align">from-table-column()</xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-common-attributes"/>
    <fo:block>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:template name="process-col-width">
    <xsl:param name="width"/>
    <xsl:if test="$width and $width != '0*'">
      <xsl:attribute name="column-width">
        <xsl:choose>
          <xsl:when test="contains($width, '*')">
            <xsl:text>proportional-column-width(</xsl:text>
            <xsl:value-of select="substring-before($width, '*')"/>
            <xsl:text>)</xsl:text>
          </xsl:when>
          <xsl:when test="contains($width, '%')">
            <xsl:value-of select="$width"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$width"/>px</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
  	
  	<xsl:template name="process-cell-align">
	    <xsl:param name="align"/>
	    <xsl:if test="$align">
	      <xsl:attribute name="text-align">
	        <xsl:choose>
	          <xsl:when test="$align = 'char'">
	            <xsl:choose>
	              <xsl:when test="$align/../@char">
	                <xsl:value-of select="$align/../@char"/>
	              </xsl:when>
	              <xsl:otherwise>
	                <xsl:value-of select="'.'"/>
	                <!-- todo: it should depend on xml:lang ... -->
	              </xsl:otherwise>
	            </xsl:choose>
	          </xsl:when>
	          <xsl:otherwise>
	            <xsl:value-of select="$align"/>
	          </xsl:otherwise>
	        </xsl:choose>
	      </xsl:attribute>
	    </xsl:if>
  	</xsl:template>

    <xsl:template name="process-cell-valign">
	    <xsl:param name="valign"/>
	    <xsl:if test="$valign">
	      <xsl:attribute name="display-align">
	        <xsl:choose>
	          <xsl:when test="$valign = 'middle'">center</xsl:when>
	          <xsl:when test="$valign = 'bottom'">after</xsl:when>
	          <xsl:when test="$valign = 'baseline'">auto</xsl:when>
	          <xsl:otherwise>before</xsl:otherwise>
	        </xsl:choose>
	      </xsl:attribute>
	      <xsl:if test="$valign = 'baseline'">
	        <xsl:attribute name="relative-align">baseline</xsl:attribute>
	      </xsl:if>
	    </xsl:if>
  	</xsl:template>
  	
  	 <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Inline-level
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="b">
    <fo:inline xsl:use-attribute-sets="b">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="strong">
    <fo:inline xsl:use-attribute-sets="strong">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="strong//em | em//html:strong">
    <fo:inline xsl:use-attribute-sets="strong-em">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="i">
    <fo:inline xsl:use-attribute-sets="i">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="cite">
    <fo:inline xsl:use-attribute-sets="cite">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="em">
    <fo:inline xsl:use-attribute-sets="em">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="var">
    <fo:inline xsl:use-attribute-sets="var">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="dfn">
    <fo:inline xsl:use-attribute-sets="dfn">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="tt">
    <fo:inline xsl:use-attribute-sets="tt">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="code">
    <fo:inline xsl:use-attribute-sets="code">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="bd">
    <fo:inline xsl:use-attribute-sets="kbd">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="samp">
    <fo:inline xsl:use-attribute-sets="samp">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="big">
    <fo:inline xsl:use-attribute-sets="big">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="small">
    <fo:inline xsl:use-attribute-sets="small">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="sub">
    <fo:inline xsl:use-attribute-sets="sub">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="sup">
    <fo:inline xsl:use-attribute-sets="sup">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="s">
    <fo:inline xsl:use-attribute-sets="s">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="strike">
    <fo:inline xsl:use-attribute-sets="strike">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="del">
    <fo:inline xsl:use-attribute-sets="del">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="u">
    <fo:inline xsl:use-attribute-sets="u">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="ins">
    <fo:inline xsl:use-attribute-sets="ins">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="abbr">
    <fo:inline xsl:use-attribute-sets="abbr">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="acronym">
    <fo:inline xsl:use-attribute-sets="acronym">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="span">
    <fo:inline>
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="span[@dir]">
    <fo:bidi-override direction="{@dir}" unicode-bidi="embed">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:bidi-override>
  </xsl:template>

  <xsl:template match="span[@style and contains(@style, 'writing-mode')]">
    <fo:inline-container alignment-baseline="central"
                         text-indent="0pt"
                         last-line-end-indent="0pt"
                         start-indent="0pt"
                         end-indent="0pt"
                         text-align="center"
                         text-align-last="center">
      <xsl:call-template name="process-common-attributes"/>
      <fo:block wrap-option="no-wrap" line-height="1">
        <xsl:apply-templates/>
      </fo:block>
    </fo:inline-container>
  </xsl:template>

  <xsl:template match="bdo">
    <fo:bidi-override direction="{@dir}" unicode-bidi="bidi-override">
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:bidi-override>
  </xsl:template>
  
  <xsl:template match="q">
    <fo:inline xsl:use-attribute-sets="q">
      <xsl:call-template name="process-common-attributes"/>
      <xsl:choose>
        <xsl:when test="lang('ja')">
          <xsl:text>「</xsl:text>
          <xsl:apply-templates/>
          <xsl:text>」</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <!-- lang('en') -->
          <xsl:text>“</xsl:text>
          <xsl:apply-templates/>
          <xsl:text>”</xsl:text>
          <!-- todo: other languages ...-->
        </xsl:otherwise>
      </xsl:choose>
    </fo:inline>
  </xsl:template>

  <xsl:template match="q//q">
    <fo:inline xsl:use-attribute-sets="q-nested">
      <xsl:call-template name="process-common-attributes"/>
      <xsl:choose>
        <xsl:when test="lang('ja')">
          <xsl:text>『</xsl:text>
          <xsl:apply-templates/>
          <xsl:text>』</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <!-- lang('en') -->
          <xsl:text>‘</xsl:text>
          <xsl:apply-templates/>
          <xsl:text>’</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </fo:inline>
  </xsl:template>

  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Image
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="img">
    <fo:external-graphic xsl:use-attribute-sets="img">
      <xsl:call-template name="process-img"/>
    </fo:external-graphic>
  </xsl:template>

  <xsl:template match="img[ancestor::html:a/@href]">
    <fo:external-graphic xsl:use-attribute-sets="img-link">
      <xsl:call-template name="process-img"/>
    </fo:external-graphic>
  </xsl:template>

  <xsl:template name="process-img">
    <xsl:attribute name="src">
      <xsl:text>url('</xsl:text>
      <xsl:value-of select="@src"/>
      <xsl:text>')</xsl:text>
    </xsl:attribute>
    <xsl:if test="@alt">
      <xsl:attribute name="role">
        <xsl:value-of select="@alt"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@width">
      <xsl:choose>
        <xsl:when test="contains(@width, '%')">
          <xsl:attribute name="width">
            <xsl:value-of select="@width"/>
          </xsl:attribute>
          <xsl:attribute name="content-width">scale-to-fit</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="content-width">
            <xsl:value-of select="@width"/>px</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="@height">
      <xsl:choose>
        <xsl:when test="contains(@height, '%')">
          <xsl:attribute name="height">
            <xsl:value-of select="@height"/>
          </xsl:attribute>
          <xsl:attribute name="content-height">scale-to-fit</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="content-height">
            <xsl:value-of select="@height"/>px</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="@border">
      <xsl:attribute name="border">
        <xsl:value-of select="@border"/>px solid</xsl:attribute>
    </xsl:if>
    <xsl:call-template name="process-common-attributes"/>
  </xsl:template>

  <xsl:template match="object">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="param"/>
  <xsl:template match="map"/>
  <xsl:template match="area"/>
  <xsl:template match="label"/>
  <xsl:template match="input"/>
  <xsl:template match="select"/>
  <xsl:template match="optgroup"/>
  <xsl:template match="option"/>
  <xsl:template match="textarea"/>
  <xsl:template match="legend"/>
  <xsl:template match="button"/>

  <!--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
       Link
  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-->

  <xsl:template match="a">
    <fo:inline>
      <xsl:call-template name="process-common-attributes-and-children"/>
    </fo:inline>
  </xsl:template>

  <xsl:template match="a[@href]">
    <fo:basic-link xsl:use-attribute-sets="a-link">
      <xsl:call-template name="process-a-link"/>
    </fo:basic-link>
  </xsl:template>

  <xsl:template name="process-a-link">
    <xsl:call-template name="process-common-attributes"/>
    <xsl:choose>
      <xsl:when test="starts-with(@href,'#')">
        <xsl:attribute name="internal-destination">
          <xsl:value-of select="substring-after(@href,'#')"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="external-destination">
          <xsl:text>url('</xsl:text>
          <xsl:value-of select="@href"/>
          <xsl:text>')</xsl:text>
        </xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="@title">
      <xsl:attribute name="role">
        <xsl:value-of select="@title"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>
  
       
</xsl:stylesheet>