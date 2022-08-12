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
    
   <xsl:template match="ResolutionData">

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
					<fo:block font-family="Kokila" font-size="18px" font-weight="bold">
						<fo:block font-family="Kokila" text-align="center">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
	            	<fo:block font-family="Kokila" font-size="15px">	            					
						<fo:block text-align="right">
							<fo:block margin-right="0.20cm">क्रमांक...............................
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">फ&#160;</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-२</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="3.21cm">विधान भवन,</fo:block>
							<fo:block margin-right="3.33cm">मुंबई/नागपूर</fo:block>
							<fo:block margin-right="2.53cm">दिनांक :- &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<fo:block font-weight="bold">प्रेषक</fo:block>
						<fo:block margin-left="1cm" >	
						<fo:block>प्रधान सचिव</fo:block>
						<fo:block >
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block>महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									<fo:block>महाराष्ट्र <xsl:value-of select="houseTypeName"/></fo:block>
								</xsl:when>
							</xsl:choose>
						</fo:block>
						</fo:block>
						<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block font-size="8px">&#160;</fo:block>	
									
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									<fo:block font-size="8px">&#160;</fo:block>	
								</xsl:when>
						</xsl:choose>					
										
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति,</fo:block>
						<fo:block margin-left="1cm" >	
							<fo:inline font-weight="bold"><xsl:value-of select="memberNames"/>, 
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">
										वि.स.स. 
									</xsl:when>
									<xsl:when test="houseType='upperhouse'">
										वि.प.स
									</xsl:when>
								</xsl:choose>
							</fo:inline>
							<fo:block>
											
							<fo:inline font-weight="bold">विषय :-</fo:inline>
							<xsl:value-of select="subject"/>
							</fo:block>
							<fo:block margin-left="1.5cm" >
							<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">
										म.वि.स. 
									</xsl:when>
									<xsl:when test="houseType='upperhouse'">
										म.वि.प.
									</xsl:when>
							</xsl:choose>
							नियम
							<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">
										१०२ 
									</xsl:when>
									<xsl:when test="houseType='upperhouse'">
										१०२
									</xsl:when>
							</xsl:choose>
							अन्वये प्राप्त अशासकीय ठराव क्रमांक <xsl:value-of select="number"/>.
							</fo:block>
						</fo:block>
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold">महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1cm">							
							आपणांस असे कळविण्यात येत आहे की,<xsl:value-of select="rejectionReason"/>
							आपली वरील अशासकीय ठरावाची सूचना मा.
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">उप सभापतींनी</xsl:when>
							</xsl:choose>
						<fo:inline font-weight="bold">&#160;अस्वीकृत</fo:inline> केली आहे.
						</fo:block>	
						<fo:block font-size="4px">&#160;</fo:block>		
						<fo:block text-align="right">
							<fo:block margin-right="2.5cm">आपला,</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>	
							<fo:block font-size="10.5px">&#160;</fo:block>	
										
							<fo:block margin-right="2cm">कक्ष अधिकारी,</fo:block>	
							<fo:block margin-right="0.60cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>						
							
						</fo:block>		
						<fo:block>
							<fo:block >याची प्रत,</fo:block>
							<fo:block margin-left="1cm">		
								
												<fo:inline>(१) सचिव,</fo:inline>
												<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<xsl:value-of select="department"/>,
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रम),
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम वगळून)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रम वगळून),
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रमांसह)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रमांसह),
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>),
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
												<fo:block>(२) मा. <fo:inline><xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/>&#160;मंत्री</fo:inline>&#160;यांना या सचिवालयाचे पृष्ठांकन क्रमांक.........
												<xsl:choose>
													<xsl:when test="houseType='lowerhouse'">फ</xsl:when>
													<xsl:when test="houseType='upperhouse'">ई-२</xsl:when>
												</xsl:choose>, दिनांकित............. च्या अनुसार माहितीसाठी सादर अग्रेषित.
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