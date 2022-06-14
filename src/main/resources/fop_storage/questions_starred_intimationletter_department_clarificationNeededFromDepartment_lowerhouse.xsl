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
	            	<fo:block font-family="Kokila" font-size="15px">	            					
						<fo:block text-align="right">
							<fo:block margin-right="1.45cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.21cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<xsl:choose>
								<xsl:when test="boolean(inwardLetterDate)">
									<fo:block margin-right="2.53cm">दिनांक : <xsl:value-of select="inwardLetterDate"></xsl:value-of></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block margin-right="2.53cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>		
						
						<!-- <fo:block>&#160;</fo:block> -->
						
						<fo:block text-align="left">
							<fo:block>प्रेषक:</fo:block>						
							<fo:block margin-left="1cm">प्रधान सचिव,</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block>प्रति,</fo:block>	
							<fo:block margin-left="1cm" font-weight="bold">					
							<fo:block>उप सचिव</fo:block>
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<fo:block font-weight="bold"><xsl:value-of select="department"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम)'">
											<fo:block font-weight="bold"><xsl:value-of select="department"/> (सार्वजनिक उपक्रम)</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम वगळून)'">
											<fo:block font-weight="bold"><xsl:value-of select="department"/> (सार्वजनिक उपक्रम वगळून)</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रमांसह)'">
											<fo:block font-weight="bold"><xsl:value-of select="department"/> (सार्वजनिक उपक्रमांसह)</fo:block>
										</xsl:when>
										<xsl:otherwise>
											<fo:block font-weight="bold"><xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>)</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block font-weight="normal">महाराष्ट्र शासन मंत्रालय, मुंबई - ४०० ०३२</fo:block>	
							</fo:block>
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block text-align="center" font-weight="bold">विषय: <xsl:value-of select="houseTypeName"/>&#160;<xsl:value-of select="deviceType"/> क्रमांक - <xsl:value-of select="number"/></fo:block>
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1cm">
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;<fo:inline font-weight="bold">उपरिनिर्दिष्ट प्रश्नाबाबतची वस्तुस्थिती</fo:inline> या सचिवायलास
									<fo:inline font-weight="bold">त्वरीत									
									<xsl:if test="boolean(daysCountForReceivingClarificationFromDepartment)">
										<xsl:value-of select="daysCountForReceivingClarificationFromDepartment"/> दिवसात
									</xsl:if>							
									कळवावी</fo:inline> अशी विनंती आहे.
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;उपरिनिर्दिष्ट प्रश्नाबाबत खाली नमूद केलेल्या क्रमांक <xsl:value-of select="questionIndexesForClarification"/> वरील मुद्द्याबाबतची माहिती, या सचिवायलास त्वरीत कळवावी अशी विनंती आहे.
								</xsl:when>
							</xsl:choose>
													
							 												
						</fo:block>	
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block margin-left="1cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;सदरहू प्रश्न  स्वीकृत करण्यासारख्या आहे की नाही हे ठरविण्यासाठी ही माहिती आवश्यक असून ती हे पत्र मिळाल्यापासून
							 <xsl:choose>
								<xsl:when test="boolean(daysCountForReceivingClarificationFromDepartment)">
									<xsl:value-of select="daysCountForReceivingClarificationFromDepartment"/> दिवसांच्या आत
								</xsl:when>
								<xsl:otherwise>
									चार दिवसांच्या आत
								</xsl:otherwise>
							</xsl:choose>
							या सचिवालयास कळवावी. 
							उक्त अवधीत आपणाकडून माहिती न आल्यास, प्रश्न स्वीकृत होऊन तो शासनाकडे उत्तरासाठी पाठविला जाण्याची शक्यता आहे 												
						</fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block margin-left="1cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							सदरहू प्रश्न  स्वीकृत झाल्यास, तो दिनांक
							<xsl:choose>
								<xsl:when test="boolean(answeringDate)">
									<xsl:value-of select="answeringDate"/>
								</xsl:when>
								<xsl:otherwise>
									__________
								</xsl:otherwise>
							</xsl:choose>
							रोजी उत्तरासाठी ठेवण्यात येईल.									
						</fo:block>
							
						<fo:block font-size="6px">&#160;</fo:block>		
								
						<fo:block text-align="right">
							<fo:block margin-right="2cm">आपला</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block margin-right="1.4cm">कक्ष अधिकारी,</fo:block>		
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>								
						</fo:block>
						
						<!-- <fo:block font-weight="bold">
							<xsl:for-each select="questionsAskedForClarification/questionAskedForClarification">
								<fo:block><xsl:value-of select="value"/></fo:block>
								<xsl:if test="position()!=last()">
									<fo:block>&#160;</fo:block>
								</xsl:if>								
							</xsl:for-each>
						</fo:block>	 -->
						-----------------------------------------------------------------------------------------------------------------------------------------
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block text-align="center" font-weight="bold">विषय - <xsl:value-of select="subject"/></fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block margin-left="1cm" font-weight="bold">
							<fo:inline><xsl:value-of select="memberNames"/></fo:inline>			
						</fo:block>
						<fo:block font-size="6px">&#160;</fo:block>
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
										<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
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
						<!-- <fo:block>&#160;</fo:block> -->						
						<fo:block>&#160;&#160;<xsl:apply-templates select="questionText"/></fo:block>
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>