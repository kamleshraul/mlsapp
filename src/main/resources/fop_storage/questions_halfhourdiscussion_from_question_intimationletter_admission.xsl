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
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रेषक,</fo:block>						
							<fo:block font-weight="bold" margin-left="1cm">सचिव,</fo:block>
							<fo:block margin-left="1cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
						</fo:block>	
						
						<fo:block font-size="6px">&#160;</fo:block>					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रति,</fo:block>	
							<fo:block margin-left="1cm">					
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
								<xsl:choose>
									<xsl:when test="sessionPlace='मुंबई'">
										<fo:block>महाराष्ट्र शासन मंत्रालय, मुंबई - ४०० ०३२</fo:block>
									</xsl:when>
									<xsl:when test="sessionPlace='नागपूर'">
										<fo:block>महाराष्ट्र शासन,</fo:block>
										<fo:block>शिबीर कार्यालय, नागपूर</fo:block>
									</xsl:when>
								</xsl:choose>						
							</fo:block>
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block text-align="center" font-weight="bold">
							विषय -
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">म.वि.स. नियम ९४(१)</xsl:when>
								<xsl:when test="houseType='upperhouse'">म.वि.प. नियम ९२(२)</xsl:when>
							</xsl:choose>	
							अन्वये उपस्थित केलेली अर्धा-तास चर्चेची सूचना &#160; <xsl:value-of select="number"/>.						
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block font-weight="bold">महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block>							
							&#160;&#160;&#160;&#160;&#160;&#160;उपरोक्त विषयाच्या अनुषंगाने निर्देशानुसार आपणास कळविण्यात येते की,
							<fo:inline font-weight="bold">"<xsl:value-of select="subject"/>"</fo:inline> या विषयावरील 
							<fo:inline font-weight="bold">
								<xsl:value-of select="referredQuestionMemberName"/> व इतर
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
									<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
								</xsl:choose>
							</fo:inline>
							यांचा 
							<xsl:choose>
								<xsl:when test="deviceType='अतारांकित प्रश्न'">
									<xsl:value-of select="referredQuestionYaadiLayingDate"/> रोजी सभागृहाच्या पटलावर 
									ठेवण्यात आलेल्या अतारांकित प्रश्नोत्तरांच्या यादी क्रमांक <xsl:value-of select="referredQuestionYaadiNumber"/> मधील प्रश्न क्रमांक <xsl:value-of select="referredQuestionNumber"/> ला									
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="referredQuestionDeviceType"/> क्रमांक <fo:inline font-weight="bold"><xsl:value-of select="referredQuestionNumber"/></fo:inline> ला 
									<xsl:value-of select="referredQuestionAnsweringDate"/> रोजी
								</xsl:otherwise>
							</xsl:choose>
							दिलेल्या उत्तराच्या संदर्भात 
							<fo:inline font-weight="bold">
								<xsl:value-of select="primaryMemberName"/>,
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
									<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
								</xsl:choose>
							</fo:inline>
							यांनी महाराष्ट्र <xsl:value-of select="houseTypeName"/>
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'"> नियम ९४(१)</xsl:when>
								<xsl:when test="houseType='upperhouse'"> नियम ९२(२)</xsl:when>								
							</xsl:choose>
							अन्वये उपस्थित केलेली अर्धा-तास चर्चेची सूचना माननीय
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">सभापतींनी</xsl:when>								
							</xsl:choose>
							स्वीकृत केली आहे.
						</fo:block>	
						<fo:block font-size="2px">&#160;</fo:block>
						<xsl:choose>
							<xsl:when test="not(discussionDate) or discussionDate=''">
								<fo:block>
									&#160;&#160;&#160;&#160;&#160;&#160;सदर सूचना बॅलेटमध्ये आल्यास, सत्र संपेपर्यंत चर्चेसाठी ठेवण्यात येईल.
								</fo:block>								
							</xsl:when>
							<xsl:otherwise>
								<fo:block>
									&#160;&#160;&#160;&#160;&#160;&#160;सदर सूचना बॅलेटमध्ये आली असून ती <fo:inline font-weight="bold"><xsl:value-of select="discussionDate"/></fo:inline>
									रोजी चर्चेसाठी ठेवण्यात आली आहे.
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>						
						<fo:block font-size="4px">&#160;</fo:block>			
						<fo:block text-align="right">
							<fo:block margin-right="2.3cm">आपला</fo:block>
							<fo:block font-size="10.5px">&#160;</fo:block>							
							<fo:block margin-right="1.8cm">कक्ष अधिकारी</fo:block>							
							<fo:block margin-right="0.3cm">महाराष्ट्र विधानमंडळ सचिवालय.</fo:block>
						</fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block font-weight="bold">
							<fo:inline text-decoration="underline">प्रतिलिपी</fo:inline> - 
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:block margin-left="1cm">(१) मा. 
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
									<fo:block margin-left="1cm">(२)
										<xsl:value-of select="primaryMemberName"/>,
										<xsl:choose>
											<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
											<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
										</xsl:choose>
									</fo:block>
									<fo:block margin-left="1.6cm" font-weight="normal">यांना माहितीसाठी प्रत अग्रेषित.</fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block margin-left="1cm">(१)
										<xsl:value-of select="primaryMemberName"/>,
										<xsl:choose>
											<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
											<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
										</xsl:choose>
									</fo:block>
									<fo:block margin-left="1.6cm" font-weight="normal">यांना माहितीसाठी प्रत अग्रेषित.</fo:block>
								</xsl:otherwise>
							</xsl:choose>

						</fo:block>	
						<fo:block break-before="page">
							<fo:block font-weight="bold" text-align="center">
								<fo:block font-size="18px" text-decoration="underline">
									महाराष्ट्र <xsl:value-of select="houseTypeName"/>
								</fo:block>
								<fo:block font-size="15px">
									<xsl:choose>
										<xsl:when test="sessionNumber='1'">पहिले</xsl:when>
										<xsl:when test="sessionNumber='2'">दुसरे</xsl:when>
										<xsl:when test="sessionNumber='3'">तिसरे</xsl:when>
										<xsl:when test="sessionNumber='4'">चौथे</xsl:when>
										<xsl:when test="sessionNumber='5'">पाचवे</xsl:when>
										<xsl:otherwise>पहिले</xsl:otherwise>
									</xsl:choose>
									अधिवेशन <xsl:value-of select="sessionYear"/>
								</fo:block>
								<fo:block font-size="15px">
									अर्धा-तास चर्चेची सूचना
								</fo:block>
							</fo:block>
							<fo:block font-size="10px">&#160;</fo:block>
							<fo:block font-size="12px">
								<fo:block>
									&#160;&#160;&#160;&#160;&#160;<fo:inline font-weight="bold">"<xsl:value-of select="subject"/>"</fo:inline>
									या विषयावरील 
										<fo:inline font-weight="bold">
										<xsl:value-of select="referredQuestionMemberName"/> व इतर
										<xsl:choose>
											<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
											<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
										</xsl:choose>
									</fo:inline>
									यांचा 
									<xsl:choose>
										<xsl:when test="deviceType='अतारांकित प्रश्न'">
											<xsl:value-of select="referredQuestionYaadiLayingDate"/> रोजी सभागृहाच्या पटलावर 
											ठेवण्यात आलेल्या अतारांकित प्रश्नोत्तरांच्या यादी क्रमांक <xsl:value-of select="referredQuestionYaadiNumber"/> मधील प्रश्न क्रमांक <xsl:value-of select="referredQuestionNumber"/> ला								
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="referredQuestionDeviceType"/> क्रमांक <fo:inline font-weight="bold"><xsl:value-of select="referredQuestionNumber"/></fo:inline> ला 
											<xsl:value-of select="referredQuestionAnsweringDate"/> रोजी
										</xsl:otherwise>
									</xsl:choose>
									दिलेल्या उत्तराच्या संदर्भात 
									<fo:inline font-weight="bold">
										<xsl:value-of select="primaryMemberName"/>,
										<xsl:choose>
											<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
											<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when>
										</xsl:choose>
									</fo:inline>
									यांनी महाराष्ट्र <xsl:value-of select="houseTypeName"/>
									<xsl:choose>
										<xsl:when test="houseType='lowerhouse'"> नियम ९४(१)</xsl:when>
										<xsl:when test="houseType='upperhouse'"> नियम ९२(१)</xsl:when>								
									</xsl:choose>
									अन्वये उपस्थित केलेली अर्धा-तास चर्चेची सूचना.
								</fo:block>
								<fo:block font-size="15px">&#160;</fo:block>
								<fo:block font-weight="bold" text-align="center" text-decoration="underline">
									उपस्थित करावयाचे मुद्दे
								</fo:block>
								<fo:block font-size="6px">&#160;</fo:block>
								<fo:block margin-left="1.5cm">
									<xsl:choose>
			            				<xsl:when test="reason!='' and reason!='-' and reason!='--' and reason!='---' and reason!='----' and reason!='-----'">
			            					<xsl:apply-templates select="reason"/>
			            				</xsl:when>
			            				<xsl:when test="bExplanation!=''"><xsl:apply-templates select="bExplanation"/></xsl:when>
			            			</xsl:choose>									
								</fo:block>
								<fo:block font-size="20pt">&#160;</fo:block>
								<xsl:if test="houseType='lowerhouse'">
						            <xsl:choose>
				                		<xsl:when test="$formatOut='application/pdf'">
				                			<fo:block font-weight="bold">
						                		विधान भवन
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                			                		                		
						                		<fo:inline font-weight="bold">
						                			<xsl:value-of select="userName"/>
						                		</fo:inline>
						                		<fo:block font-size="0pt">&#160;</fo:block>
						                		<xsl:value-of select="sessionPlace"/>
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;
						                		सचिव
						                		<fo:block font-size="0pt">&#160;</fo:block>
						                		दिनांक: <xsl:value-of select="reportDate"/>
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;
						                		महाराष्ट्र&#160;<xsl:value-of select="houseTypeName"/>
						                		<!-- <fo:block text-align="right">
						                			महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
						                		</fo:block> -->
						                	</fo:block>
				                		</xsl:when>
				                		<xsl:when test="$formatOut='WORD'">
				                			<fo:block font-weight="bold">
						                		विधान भवन
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;         			                		                		
						                		<fo:inline font-weight="bold">
						                			<xsl:value-of select="userName"/>
						                		</fo:inline>
						                		<fo:block font-size="0pt">&#160;</fo:block>
						                		<xsl:value-of select="sessionPlace"/>
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;			                		
						                		&#160;&#160;&#160;&#160;&#160;&#160;
						                		सचिव
						                		<fo:block font-size="0pt">&#160;</fo:block>
						                		<!-- <fo:block text-align="right">
						                			महाराष्ट्र&#160;<xsl:value-of select="houseType"/>
						                		</fo:block> -->
						                		दिनांक: <!-- <xsl:value-of select="reportDate"/> -->
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						                		&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;					                		
						                		महाराष्ट्र <xsl:value-of select="houseTypeName"/>
						                	</fo:block>
				                		</xsl:when>
				                	</xsl:choose>
			                	</xsl:if>
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