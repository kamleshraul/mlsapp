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
					<xsl:variable name="endPartOfPreviousSubDepartment">
						<xsl:value-of select="substring(previousSubDepartment,(string-length(previousSubDepartment)-4))"/>
					</xsl:variable>					
					<xsl:variable name="departmentWithoutVibhag">
						<xsl:value-of select="substring(department,1,(string-length(department)-5))"/>
					</xsl:variable>
					<xsl:variable name="previousDepartmentWithoutVibhag">
						<xsl:value-of select="substring(previousDepartment,1,(string-length(previousDepartment)-5))"/>
					</xsl:variable>
	            	<fo:block font-family="Kokila" font-size="15px">	   
	            		<fo:block text-align="right">
							<fo:block margin-right="1.10cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१,२</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.21cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="2.53cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>  								
						
						<!-- <fo:block font-size="8px">&#160;</fo:block> -->					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रेषक :</fo:block>						
							<fo:block margin-left="1cm">प्रधान सचिव,</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति,</fo:block>	
							<fo:block margin-left="1cm">(१) सचिव,</fo:block>
							<fo:block margin-left="1.6cm">महाराष्ट्र शासन,</fo:block>
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<fo:block margin-left="1.6cm"><xsl:value-of select="department"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम)'">
											<fo:block margin-left="1.6cm"><xsl:value-of select="department"/> (सार्वजनिक उपक्रम)</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम वगळून)'">
											<fo:block margin-left="1.6cm"><xsl:value-of select="department"/> (सार्वजनिक उपक्रम वगळून)</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रमांसह)'">
											<fo:block margin-left="1.6cm"><xsl:value-of select="department"/> (सार्वजनिक उपक्रमांसह)</fo:block>
										</xsl:when>
										<xsl:otherwise>
											<fo:block margin-left="1.6cm"><xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>)</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block margin-left="1.6cm">मंत्रालय, मुंबई - ४०० ०३२.</fo:block>
							<fo:block margin-left="1cm">(२) सचिव,</fo:block>
							<fo:block margin-left="1.6cm">महाराष्ट्र शासन,</fo:block>
							<xsl:choose>
								<xsl:when test="previousDepartment=previousSubDepartment">
									<fo:block margin-left="1.6cm"><xsl:value-of select="previousDepartment"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block margin-left="1.6cm"><xsl:value-of select="previousDepartment"/> (<xsl:value-of select="previousSubDepartment"/>)</fo:block>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block margin-left="1.6cm">मंत्रालय, मुंबई - ४०० ०३२.</fo:block>						
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block margin-left="2.5cm" font-weight="bold">
							विषय - "<xsl:value-of select="subject"/>" या विषयावरील  <xsl:value-of select="primaryMemberName"/> व इतर
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
								<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
							</xsl:choose>
							यांचा <xsl:value-of select="deviceType"/> क्रमांक <xsl:value-of select="number"/>
						</fo:block>
						<fo:block margin-left="2.5cm" font-weight="bold">
							संदर्भ - (१) या सचिवालयाचे पत्र क्र. ____/ ब-१/२, दिनांक __________
						</fo:block>
						<fo:block margin-left="3.6cm" font-weight="bold">
							(२) 
							<xsl:choose>
								<xsl:when test="previousDepartment=previousSubDepartment">
									<xsl:value-of select="$previousDepartmentWithoutVibhag"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$previousDepartmentWithoutVibhag"/> (<xsl:value-of select="previousSubDepartment"/>)
								</xsl:otherwise>
							</xsl:choose>							
							विभागाचे पत्र क्रमांक _________________,
						</fo:block>
						<fo:block margin-left="3.7cm">
							दिनांक __________.
						</fo:block>
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block>							
							&#160;&#160;&#160;&#160;&#160;&#160;उपरोक्त संदर्भाधीन पत्राच्या अनुषंगाने निदेशानुसार आपण्यास कळविण्यात येते की,
							"<fo:inline font-weight="bold"><xsl:value-of select="subject"/></fo:inline>"
							या विषयावरील <fo:inline font-weight="bold"><xsl:value-of select="deviceType"/> क्रमांक <xsl:value-of select="number"/></fo:inline> हा
							<xsl:choose>
								<xsl:when test="previousDepartment=previousSubDepartment">
									<fo:inline font-weight="bold"><xsl:value-of select="$previousDepartmentWithoutVibhag"/></fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<fo:inline font-weight="bold"><xsl:value-of select="$previousDepartmentWithoutVibhag"/> (<xsl:value-of select="previousSubDepartment"/>)</fo:inline>
								</xsl:otherwise>
							</xsl:choose>
							विभागाने
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<fo:inline font-weight="bold"><xsl:value-of select="$departmentWithoutVibhag"/></fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<fo:inline font-weight="bold"><xsl:value-of select="$departmentWithoutVibhag"/> (<xsl:value-of select="subDepartment"/>)</fo:inline>
								</xsl:otherwise>
							</xsl:choose> 
							विभागाकडे हस्तांतरित केल्याचे दिनांक __________ रोजी या सचिवालयास कळविले आहे. या संदर्भात आपल्या निदर्शनास आणण्यात येते की, उपरोक्त प्रश्नांचे बॅलेट दिनांक <xsl:value-of select="ballotDate"/>
							रोजी काढण्यात आले असून आता सदरहू प्रश्न पूर्वी कळविल्याप्रमाणे दिनांक <xsl:value-of select="answeringDate"/> रोजी उत्तरासाठी मा. 							
							<xsl:choose>
								<xsl:when test="previousMinistryDesignation='मुख्‍यमंत्री' or previousMinistryDesignation='उप मुख्‍यमंत्री'">
									<xsl:value-of select="previousMinistryDesignation"/>
								</xsl:when>		
								<xsl:when test="previousMinistryDisplayName='मुख्‍यमंत्री' or previousMinistryDisplayName='उप मुख्‍यमंत्री'">
									<xsl:value-of select="previousMinistryDisplayName"/>
								</xsl:when>						
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="previousDepartment=previousSubDepartment and $endPartOfPreviousSubDepartment='विभाग'">											
											<xsl:value-of select="substring(previousSubDepartment,1,(string-length(previousSubDepartment)-5))"/> मंत्री
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="previousSubDepartment"/> मंत्री
										</xsl:otherwise>
									</xsl:choose>																			
								</xsl:otherwise>
							</xsl:choose>
							यांच्या ऐवजी मा.
							<xsl:choose>
								<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री' or primaryMemberDesignation='उप मुख्‍यमंत्री'">
									<xsl:value-of select="primaryMemberDesignation"/>
								</xsl:when>
								<xsl:when test="ministryDisplayName='मुख्‍यमंत्री' or ministryDisplayName='उप मुख्‍यमंत्री'">
									<xsl:value-of select="ministryDisplayName"/>
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
							</xsl:choose>
							यांच्या नावे दाखविण्यात आला आहे. तरी आपणास विनंती करण्यात येत आहे की,
							<fo:inline font-weight="bold">मा.
								<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री' or primaryMemberDesignation='उप मुख्‍यमंत्री'">
										<xsl:value-of select="primaryMemberDesignation"/>
									</xsl:when>
									<xsl:when test="ministryDisplayName='मुख्‍यमंत्री' or ministryDisplayName='उप मुख्‍यमंत्री'">
										<xsl:value-of select="ministryDisplayName"/>
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
								</xsl:choose>
							</fo:inline>महोदयांना दिनांक <fo:inline><xsl:value-of select="answeringDate"/></fo:inline> रोजी उपस्थित राहून उक्त प्रश्नांच्या अनुपूरक प्रश्नांना उत्तर देण्याची विनंती करण्यात यावी.							
						</fo:block>	
						<fo:block>
							&#160;&#160;&#160;&#160;&#160;&#160;सदर प्रश्नाच्या प्रती <fo:inline font-weight="bold">आजच</fo:inline> या सचिवालयास पाठविण्याची व्यवस्था करण्यात यावी.
						</fo:block>
							
						<fo:block font-size="4px">&#160;</fo:block>	
									
						<fo:block text-align="right">
							<fo:block margin-right="2.5cm">आपला</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>							
							<fo:block margin-right="2cm">कक्ष अधिकारी</fo:block>							
							<fo:block margin-right="0.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>
						
						<fo:block font-size="6px">&#160;</fo:block>	
						
						<fo:block>		
							<fo:block font-weight="bold">याची प्रत :</fo:block>		
							<fo:block margin-left="1cm">	
								(१) सचिव, महाराष्ट्र शासन, संसदीय कार्य विभाग, मंत्रालय, मुंबई - ३२								
							 </fo:block>
							 <fo:block margin-left="1cm">
							 	(२) मा. <xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री' or primaryMemberDesignation='उप मुख्‍यमंत्री'">
										<xsl:value-of select="primaryMemberDesignation"/>
									</xsl:when>
									<xsl:when test="ministryDisplayName='मुख्‍यमंत्री' or ministryDisplayName='उप मुख्‍यमंत्री'">
										<xsl:value-of select="ministryDisplayName"/>
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
								</xsl:choose> यांचे खाजगी सचिव
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