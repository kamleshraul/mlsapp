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
	                  	margin-top="2.3cm" margin-bottom="1.5cm"
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
	            	<fo:block font-family="Kokila" font-size="15px">
	            		<fo:block text-align="right" font-weight="bold">तात्काळ</fo:block>
	            		<fo:block>
	            			<fo:inline font-weight="bold"><xsl:value-of select="primaryMemberName"/> व इतर </fo:inline>, 
	            			<xsl:choose>
								<xsl:when test="houseType='lowerhouse'"><fo:inline font-weight="bold">वि.स.स.</fo:inline> यांनी महाराष्ट्र विधानसभा नियमांतील नियम ९४ अन्वये (अर्धा-तास चर्चा)</xsl:when>
								<xsl:when test="houseType='upperhouse'">वि.प.स. यांनी महाराष्ट्र विधानपरिषद नियम ९२, अनुसार</xsl:when>
							</xsl:choose>
							दिलेली सूचना (अर्धा-तास चर्चेची सूचना) पुढीलप्रमाणे आहे :-							
	            		</fo:block>  
	            		<fo:block font-size="6px">&#160;</fo:block>    
	            		<fo:block font-weight="bold">
	            			&#160;&#160;&#160;&#160;	            				   
	            			<xsl:apply-templates select="bExplanation"/>         			
	            		</fo:block> 
	            		<fo:block font-size="12px">&#160;</fo:block>
	            		<fo:block text-align="right">
							<fo:block margin-right="1.45cm">क्रमांक - _____&#160;&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">फ</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<xsl:choose>
								<xsl:when test="sessionPlace='मुंबई'">
									<fo:block margin-right="2.3cm">विधान भवन, <xsl:value-of select="sessionPlace"/></fo:block>
								</xsl:when>
								<xsl:when test="sessionPlace='नागपूर'">
									<fo:block margin-right="1.98cm">विधान भवन, <xsl:value-of select="sessionPlace"/></fo:block>
								</xsl:when>
							</xsl:choose>							
							<fo:block margin-right="1.82cm">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<!-- <fo:block font-size="8px">&#160;</fo:block> -->		
						
						<fo:block font-size="9px">&#160;</fo:block>
						
						<fo:block>
							संदर्भ : या सचिवालयाचे पृष्ठांकन क्रमांक ________&#160;&#160;&#160;फ, दिनांक
						</fo:block>			
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>							
							प्रधान सचिव, 
							<fo:inline font-weight="bold">
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<xsl:value-of select="department"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रम)
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम वगळून)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रम वगळून)
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रमांसह)'">
											<xsl:value-of select="department"/> (सार्वजनिक उपक्रमांसह)
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>)
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							</fo:inline>यांना माहितीसाठी अग्रेषित							
						</fo:block>		
						<fo:block font-size="9px">&#160;</fo:block>
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;चालू अधिवेशनासाठी मा.
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">उपाध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">सभापतींनी</xsl:when>
							</xsl:choose>
							ही सूचना मान्य केली आहे. उपरोक्त विषयावरील चर्चा सभागृहात दिनांक &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; रोजी घेण्यात येईल.
						</fo:block>	
						<fo:block font-size="6px">&#160;</fo:block>					
						<fo:block text-align="right">
							<fo:block margin-right="2.3cm">आपला</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>							
							<fo:block margin-right="1.8cm">कक्ष अधिकारी</fo:block>							
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
						</fo:block>
						<fo:block font-size="12px">&#160;</fo:block>
						<fo:block font-weight="bold">
						<fo:block>
							(१)
							<xsl:choose>
								<xsl:when test="primaryMemberDesignation='मुख्यमंत्री' or primaryMemberDesignation='उप मुख्यमंत्री'">
									<xsl:value-of select="primaryMemberDesignation"/>.
								</xsl:when>									
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
											<xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री.
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="subDepartment"/> मंत्री.
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>	
						<fo:block>
							(२)
							<xsl:value-of select="primaryMemberName"/>,
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
								<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
							</xsl:choose>							
						</fo:block>							
						</fo:block>
						<fo:block> &#160;&#160;&#160;यांना माहितीसाठी सादर अग्रेषित.</fo:block>						
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>