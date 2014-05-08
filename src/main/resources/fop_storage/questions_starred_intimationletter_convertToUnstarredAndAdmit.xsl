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
					<fo:block text-align="center" font-family="Mangal">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="Mangal">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">	
	            	<xsl:variable name="endPartOfSubDepartment">
						<xsl:value-of select="substring(subDepartment,(string-length(subDepartment)-4))"/>
					</xsl:variable>
	            	<fo:block font-family="Mangal" font-size="10.5px">	            					
						<fo:block text-align="right">
							<fo:block margin-right="1.45cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.21cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="1.82cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<!-- <fo:block>&#160;</fo:block> -->					
						
						<fo:block text-align="left">
							<fo:block>प्रति,</fo:block>						
							<fo:block font-weight="bold">सचिव</fo:block>
							<xsl:choose>
								<xsl:when test="department=subDepartment">
									<fo:block font-weight="bold"><xsl:value-of select="department"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block font-weight="bold"><xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>)</fo:block>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block>महाराष्ट्र शासन मंत्रालय, मुंबई - ४०० ०३२</fo:block>							
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1.5cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;खालील उदधृत केलेला <xsl:value-of select="houseTypeName"/> अतारांकित प्रश्न माननीय
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">सभापतींनी</xsl:when>
							</xsl:choose>
							स्वीकृत केला आहे. सदरहू प्रश्नाचे उत्तर महाराष्‍ट्र विधानमंडळ नियमांतील नियम ७०(२) अन्वये तीस दिवसांच्‍या आत म्‍हणजे दिनांक __________ पावेतो या सचिवालयास पाठविण्‍यात यावे – 
						</fo:block>	
						<fo:block font-size="4px">&#160;</fo:block>				
						<fo:block>
							<fo:block font-weight="bold">प्रत माहितीसाठी सादर अग्रेषित -- </fo:block>
							<!-- &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; -->
							<fo:block margin-left="1.5cm">
								<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्यमंत्री'">
										<fo:block>(१) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(२) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='उप मुख्यमंत्री'">
										<fo:block>(१) माननीय मुख्यमंत्री</fo:block>
										<fo:block>(२) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(३) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:otherwise>
										<fo:block>(१) माननीय मुख्यमंत्री</fo:block>
										<xsl:choose>
											<xsl:when test="department=subDepartment and $endPartOfSubDepartment='विभाग'">											
												<fo:block>(२) माननीय  <fo:inline font-weight="bold"><xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री</fo:inline></fo:block>
											</xsl:when>
											<xsl:otherwise>
												<fo:block>(२) माननीय <fo:inline font-weight="bold"><xsl:value-of select="subDepartment"/> मंत्री</fo:inline></fo:block>
											</xsl:otherwise>
										</xsl:choose>
										<fo:block>(३) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
						</fo:block>	
						<!-- <fo:block>&#160;</fo:block> -->				
						<fo:block text-align="right">
							<fo:block margin-right="2.5cm">आपला</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>							
							<fo:block margin-right="2cm">प्रधान सचिव</fo:block>							
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block margin-right="1.5cm">महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									<fo:block margin-right="1.35cm">महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
							</xsl:choose>
						</fo:block>
						-------------------------------------------------------------------------------------------------------------------------------
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block text-align="center" font-weight="bold"><xsl:value-of select="subject"/></fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block margin-left="2cm" font-weight="bold">
							अतारांकित प्रश्‍न क्रमांक <xsl:value-of select="number"/>	
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;		
							<fo:inline><xsl:value-of select="memberNames"/></fo:inline>			
						</fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<xsl:if test="parentNumber!=''">
							<xsl:value-of select="parentDeviceType"/> क्रमांक <xsl:value-of select="parentNumber"/> ला 
							दिनांक <xsl:value-of select="parentAnsweringDate"/> रोजी दिलेल्या उत्तराच्या संदर्भात
						</xsl:if>
						<fo:block font-weight="bold">
							सन्माननीय
							<xsl:choose>
								<xsl:when test="primaryMemberDesignation='मुख्यमंत्री' or primaryMemberDesignation='उप मुख्यमंत्री'">
									<xsl:value-of select="primaryMemberDesignation"/>
								</xsl:when>
								<!-- <xsl:when test="primaryMemberDesignation='उप मुख्यमंत्री'">
									<xsl:value-of select="primaryMemberDesignation"/>
								</xsl:when> -->
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="department=subDepartment and $endPartOfSubDepartment='विभाग'">											
											<xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="subDepartment"/> मंत्री
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							पुढील गोष्टींचा खुलासा करतील काय :-
						</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>&#160;&#160;&#160;<xsl:apply-templates select="questionText"/></fo:block>
						<xsl:if test="remarks!=''">
							<fo:block break-before="page">
								<xsl:value-of select="remarks"></xsl:value-of>
							</fo:block>
						</xsl:if>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>