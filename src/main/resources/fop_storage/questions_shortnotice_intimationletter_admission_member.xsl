<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="QuestionData"/>
    
    <xsl:variable name="pageLayout" select="simple"/>
    
    <!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
   <xsl:template match="QuestionData">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="0.5cm" margin-bottom="1.5cm"
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
	            	<xsl:variable name="endPartOfSubDepartment">
						<xsl:value-of select="substring(subDepartment,(string-length(subDepartment)-4))"/>
					</xsl:variable>
					
					<xsl:variable name="primaryMemberConstituency">						
						<xsl:choose>
							<xsl:when test="substring-before(memberNames,',')!=''">
								<xsl:value-of select="substring-before(memberNames,',')"/>
							</xsl:when>
							<xsl:otherwise><xsl:value-of select="memberNames"/></xsl:otherwise>
						</xsl:choose>					
					</xsl:variable>
	            	<fo:block font-family="Kokila" font-size="15px" break-after="page">	            					
						<fo:block text-align="right">
							<fo:block margin-right="1.45cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.21cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="2.53cm">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<!-- <fo:block font-size="8px">&#160;</fo:block> -->					
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रेषक : </fo:block>
							<fo:block margin-left="0.90cm">प्रधान सचिव</fo:block>
							<fo:block margin-left="0.90cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block font-weight="bold">प्रति : </fo:block>	
							<fo:block margin-left="0.80cm"><xsl:value-of select="primaryMemberName"/>&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
								<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
							</xsl:choose>
							</fo:block>	
						</fo:block>
						<fo:block font-size="6px">&#160;</fo:block>
						<fo:block margin-left="1.3cm">
							<fo:inline font-weight="bold">विषय : "<xsl:value-of select="subject"/>"</fo:inline>
							या विषयावरील अल्पसूचना प्रश्न क्रमांक <xsl:value-of select="number"/>
						</fo:block>
						
						<fo:block margin-left="1.3cm">
							<fo:inline font-weight="bold"> संदर्भ : </fo:inline> या सचिवालयाचे पत्र क्रमांक __________
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							,दिनांक __________________.
						</fo:block>
						<fo:block font-size="4px">&#160;</fo:block>	
						<fo:block>महोदय,</fo:block>	
						<fo:block margin-left="1.5cm">							
							&#160;&#160;&#160;&#160;&#160; उपरोक्त विषयाच्या संदर्भाधीन पत्रान्वये कळविण्यात येते की, आपण विचारलेल्या उपरोक्त
							अल्प सूचना प्रश्न क्रमांक  <xsl:value-of select="number"/> ला <fo:inline font-weight="bold"> <xsl:value-of select="answeringDate"/> </fo:inline> रोजी 
							सभागृहात उत्तर देण्याबाबत मा. 
							<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्यमंत्री' or primaryMemberDesignation='उप मुख्यमंत्री'">
										<xsl:value-of select="primaryMemberDesignation"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
												<xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="subDepartment"/> मंत्री
											</xsl:otherwise>
										</xsl:choose>																			
									</xsl:otherwise>
								</xsl:choose> महोदयांनी अनुमती दर्शविली आहे असे आपणास कळविण्याचा मला निदेश आहे.
						</fo:block>	
						<fo:block font-size="4px">&#160;</fo:block>				
						<!-- <fo:block font-size="4px">&#160;</fo:block> -->				
						<fo:block text-align="right">
							<fo:block margin-right="2.5cm">आपला/आपली,</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>							
							<fo:block margin-right="2.5cm">कक्ष अधिकारी,</fo:block>							
							<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
						
						<fo:block>
							<fo:block font-weight="bold">प्रत: </fo:block>
							<fo:block margin-left="1.0cm">	
								<fo:block>							
								<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्यमंत्री'">
										१. माननीय <xsl:value-of select="primaryMemberDesignation"/>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='उप मुख्यमंत्री'">
										१. माननीय <xsl:value-of select="primaryMemberDesignation"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
												१. माननीय  <fo:inline font-weight="bold"><xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री  सादर अग्रेषित.</fo:inline>
											</xsl:when>
											<xsl:otherwise>
												१. माननीय <fo:inline font-weight="bold"><xsl:value-of select="subDepartment"/> </fo:inline>मंत्री सादर अग्रेषित.
											</xsl:otherwise>
										</xsl:choose>										
									</xsl:otherwise>
								</xsl:choose>
								 </fo:block>
								<fo:block>
									<xsl:choose>
										<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
											 २. <xsl:value-of select="subDepartment"/>	
										</xsl:when>
										<xsl:otherwise>
											 २. <xsl:value-of select="subDepartment"/> विभाग
										</xsl:otherwise>
									</xsl:choose>								
								</fo:block>
							</fo:block>
						</fo:block>	
												
					</fo:block>
				</fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>