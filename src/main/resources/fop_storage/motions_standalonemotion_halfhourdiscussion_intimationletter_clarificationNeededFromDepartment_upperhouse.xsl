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
						<fo:table table-layout="fixed" width="100%">
	            			<fo:table-column column-number="1" column-width="5cm" />
							<fo:table-column column-number="2" column-width="5cm" />
							<fo:table-column column-number="3" column-width="8.25cm" />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-weight="bold">
											<fo:block>अति तात्काळ</fo:block>											
										</fo:block>										
									</fo:table-cell>
									<fo:table-cell>
										<fo:block>&#160;</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="right" font-weight="bold">
											<fo:block margin-right="1.8cm">क्रमांक - _____/
											<xsl:choose>
												<xsl:when test="houseType='lowerhouse'">फ़,</xsl:when>
												<xsl:when test="houseType='upperhouse'">ई-२,</xsl:when>
											</xsl:choose>
											</fo:block>						
											<fo:block margin-right="0.38cm">महाराष्ट्र विधानमंडळ सचिवालय,</fo:block>
											<fo:block margin-right="3.3cm">विधान भवन,</fo:block>
											<fo:block margin-right="3.35cm">मुंबई/नागपूर.</fo:block>
											<fo:block margin-right="1.95cm">दिनांक : &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
	            		</fo:table>		
						
						<!-- <fo:block>&#160;</fo:block> -->
						
						<fo:block text-align="left">
							<fo:block>प्रेषक : </fo:block>						
							<fo:block margin-left="1cm">सचिव-१ (कार्यभार),</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block>प्रति : </fo:block>	
							<fo:block margin-left="1cm">					
							<fo:block>उप सचिव,</fo:block>
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<fo:block><xsl:value-of select="department"/></fo:block>
								</xsl:when>
								<xsl:otherwise>
									<xsl:choose>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम)'">
											<fo:block><xsl:value-of select="department"/> (सार्वजनिक उपक्रम),</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रम वगळून)'">
											<fo:block><xsl:value-of select="department"/> (सार्वजनिक उपक्रम वगळून),</fo:block>
										</xsl:when>
										<xsl:when test="subDepartment='सार्वजनिक बांधकाम (सार्वजनिक उपक्रमांसह)'">
											<fo:block><xsl:value-of select="department"/> (सार्वजनिक उपक्रमांसह),</fo:block>
										</xsl:when>
										<xsl:otherwise>
											<fo:block><xsl:value-of select="department"/> (<xsl:value-of select="subDepartment"/>),</fo:block>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:otherwise>
							</xsl:choose>
							<fo:block>महाराष्ट्र शासन,</fo:block>
							<fo:block>मंत्रालय, मुंबई ४०० ०३२.</fo:block>	
							</fo:block>
						</fo:block>		
						
						<fo:block font-size="12px">&#160;</fo:block>
						
						<fo:block margin-left="1cm">
							विषय : <xsl:value-of select="primaryMemberName"/>,
							<xsl:choose>								
								<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
								<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
							</xsl:choose>
							यांनी महाराष्ट्र <xsl:value-of select="houseTypeName"/> नियम ९२ अन्वये उपस्थित केलेली
							<fo:block margin-left="1.2cm">
								अर्धा-तास चर्चेची सूचना (सर्वसाधारण) क्रमांक - <xsl:value-of select="number"/>
							</fo:block>
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>				
						
						<fo:block margin-left="1cm">
							संदर्भ : या सचिवालयाचे पत्र क्रमांक ____________________/
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१,</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१,</xsl:when>
							</xsl:choose>
							<fo:block margin-left="1.2cm">
								दिनांकित ___________
							</fo:block>
						</fo:block>
						
						<fo:block font-size="10px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block>
	            			&#160;&#160;&#160;&#160;
	            			
	            			<xsl:choose>

	            				<xsl:when test="bExplanation!=''">
	            				<xsl:apply-templates select="bExplanation"/>
	            				</xsl:when>
	            				
	            				//	<xsl:when test="reason!='' and reason!='-' and reason!='--' and reason!='---' and reason!='----' and reason!='-----'">
	            				//	<xsl:apply-templates select="reason"/>
	            				//</xsl:when>	
	            			
	            			</xsl:choose>	            			
	            		</fo:block> 
							<fo:block font-size="4px">&#160;</fo:block>
						<fo:block>							
							&#160;&#160;&#160;&#160;&#160;उपरोल्लिखित सूचनाधीन विषयाबाबत खाली नमूद केलेल्या क्रमांक (______) येथील माहिती या सचिवालयास त्वरित कळवावी अशी विनंती आहे :- 												
						</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>
						
						<fo:block margin-left="1cm">
							<fo:block>(१) सूचनाधीन विषयी नेमकी वस्तुस्थिती काय आहे?</fo:block>
							<fo:block>(२) सूचनाधीन बाब वस्तुस्थितीवर आधारलेली आहे काय?</fo:block>
							<fo:block>(३) सूचनाधीन विषयाबाबत राज्य शासन प्रामुख्याने जबाबदार आहे काय?</fo:block>
							<fo:block>(४) सूचनाधीन प्रकरणाचा निर्णय केव्हा घेण्यात आला आहे?</fo:block>
						</fo:block>
													
						<fo:block font-size="6px">&#160;</fo:block>		
								
						<fo:block text-align="right">
							<fo:block margin-right="2cm">आपला,</fo:block>
							<fo:block>&#160;</fo:block>
							<fo:block margin-right="1.4cm">कक्ष अधिकारी,</fo:block>		
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>								
						</fo:block>						
					</fo:block>							          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>