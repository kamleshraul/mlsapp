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
							<fo:block margin-right="2.53cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<!-- <fo:block font-size="8px">&#160;</fo:block> -->					
						
						<fo:block text-align="left">
							<fo:block>प्रति,</fo:block>						
							<fo:block font-weight="bold">सचिव,</fo:block>
							<xsl:choose>
								<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true'">
									<fo:block font-weight="bold"><xsl:value-of select="department"/>,</fo:block>
								</xsl:when>
								<xsl:otherwise>
									<fo:block font-weight="bold"><xsl:value-of select="department"/>, (<xsl:value-of select="subDepartment"/>)</fo:block>
								</xsl:otherwise>
							</xsl:choose>								
							<fo:block>महाराष्ट्र शासन,</fo:block>
							 <fo:block>मंत्रालय, मुंबई - ४०० ०३२</fo:block>	
						</fo:block>		
						
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1.5cm">							
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;माननीय  
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
								<xsl:when test="houseType='upperhouse'">उप सभापतींनी</xsl:when>
							</xsl:choose>
							खाली उदधृत केलेला <xsl:value-of select="houseTypeName"/>&#160; अल्प सूचना प्रश्न स्वीकृत केला आहे. त्या प्रश्नाचे उत्तर देण्यासंबंधी माहिती पुढीलप्रमाणे आहे –&#160;
							<!-- <fo:block>&#160;</fo:block> -->	
							<fo:block>प्रश्नाचा गट क्रमांक: <fo:inline font-weight="bold"><xsl:value-of select="groupNumber"/></fo:inline></fo:block>
						</fo:block>	
						<fo:block font-size="4px">&#160;</fo:block>				
						<fo:block>
							<fo:block font-weight="bold">प्रत माहितीसाठी सादर अग्रेषित -- 
							<!-- &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; -->
							</fo:block>
							<fo:block margin-left="1.5cm">								
								<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(२) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(२) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='उप मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <fo:inline font-weight="bold">मुख्‍यमंत्री </fo:inline></fo:block>
										<fo:block>(२) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(३) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='उप मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <fo:inline font-weight="bold">मुख्‍यमंत्री </fo:inline></fo:block>
										<fo:block>(२) माननीय <fo:inline font-weight="bold"><xsl:value-of select="primaryMemberDesignation"/></fo:inline></fo:block>
										<fo:block>(३) सर्व संबंधित सदस्यांना</fo:block>
									</xsl:when>
									<xsl:otherwise>
										<fo:block>(१) माननीय <fo:inline font-weight="bold">मुख्‍यमंत्री </fo:inline></fo:block>									
										<xsl:choose>
											<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
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
						<!-- <fo:block font-size="4px">&#160;</fo:block> -->				
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
						----------------------------------------------------------------------------------------------------------------------------
						<!-- <fo:block>&#160;</fo:block> -->
						<fo:block text-align="center" font-weight="bold"><xsl:value-of select="subject"/></fo:block>
						<fo:block font-size="4px">&#160;</fo:block>
						<fo:block margin-left="2cm" font-weight="bold">
							<fo:block>
							*<xsl:value-of select="number"/>	
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;		
							<fo:inline><xsl:value-of select="memberNames"/></fo:inline>		
							</fo:block>															
						</fo:block>
						<fo:block font-size="6px">&#160;</fo:block>
						<!-- <xsl:if test="questionReferenceText!=''">
							<xsl:value-of select="questionReferenceText"/>
						</xsl:if> -->
						<fo:block font-weight="bold">							
							सन्माननीय
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
							पुढील गोष्टींचा खुलासा करतील काय :-
						</fo:block>
						<!-- <fo:block>&#160;</fo:block> -->
						<fo:block>&#160;&#160;&#160;<xsl:apply-templates select="questionText"/></fo:block>						
					</fo:block>
					
					<fo:block font-family="Kokila" font-size="15px" break-after="page">	            					
						<fo:block text-align="right">
							<fo:block margin-right="1.50cm">क्रमांक - _____&#160;/&#160;
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">ब-१</xsl:when>
								<xsl:when test="houseType='upperhouse'">ई-१</xsl:when>
							</xsl:choose>
							</fo:block>						
							<fo:block margin-right="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block margin-right="1.30cm">विधान भवन, मुंबई/नागपूर</fo:block>
							<fo:block margin-right="2.50cm">दिनांक - &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
						</fo:block>			
						
						<!-- <fo:block font-size="8px">&#160;</fo:block> -->					
						
						<fo:block text-align="left">
							<fo:block font-weight="bold">प्रेषक</fo:block>
							<fo:block margin-left="0.40cm">प्रधान सचिव</fo:block>
							<fo:block margin-left="0.40cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							<fo:block font-weight="bold">प्रति,</fo:block>						
							<fo:block margin-left="0.40cm">सचिव</fo:block>
							<fo:block margin-left="0.40cm">
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
							</fo:block>							
							<fo:block margin-left="0.40cm">महाराष्ट्र शासन मंत्रालय, मुंबई - ४०० ०३२</fo:block>	
						</fo:block>
						<fo:block>&#160;&#160;&#160;</fo:block>						
						<fo:block margin-left="1.4cm">
							विषय : 
							<xsl:choose>
								<xsl:when test="houseType='lowerhouse'">
									<fo:inline font-weight="bold">अल्पसूचना प्रश्न क्रमांक  <xsl:value-of select="number"/></fo:inline>
								</xsl:when>
								<xsl:when test="houseType='upperhouse'">
									 <xsl:value-of select="subject"/> या विषयावरील अल्पवधी सूचना प्रश्न क्रमांक  <xsl:value-of select="number"/> या बाबत. 
								</xsl:when>
							</xsl:choose>
						</fo:block>
						<fo:block font-size="6px">&#160;</fo:block>
						
						<fo:block>महोदय,</fo:block>	
						
						<fo:block font-size="4px">&#160;</fo:block>	
						
						<fo:block margin-left="1.5cm">							
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160;<xsl:value-of select="$primaryMemberConstituency"/>, 
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">वि.स.स.</xsl:when>
									<xsl:when test="houseType='upperhouse'">वि.प.स.</xsl:when> 
								</xsl:choose>
								यांचा अल्पसूचना प्रश्न क्रमांक <xsl:value-of select="number"/> मा.
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">अध्यक्षांनी</xsl:when>
									<xsl:when test="houseType='upperhouse'">उप सभापतींनी</xsl:when>
								</xsl:choose>
								स्वीकृत केला आहे. त्याची प्रत सोबत जोडली आहे.
							</fo:block>
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160; माननीय 
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
								या प्रश्नाचे ताबडतोब उत्तर देण्यास तयार आहेत काय व तयार असल्यास कोणत्या तारखेस, हे त्वरित कळवावे अशी विनंती आहे.
								</fo:block>
								<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160; या संबंधात मी आपले लक्ष 
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">महाराष्ट्र विधानसभा </xsl:when>
									<xsl:when test="houseType='upperhouse'">महाराष्ट्र विधानपरिषद </xsl:when>
								</xsl:choose>
								नियमांतील नियम
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">८६ (४) </xsl:when>
									<xsl:when test="houseType='upperhouse'">८४ (४) </xsl:when>
								</xsl:choose> 
								 कडे वेधू इच्छितो. सदर नियमाप्रमाणे आवश्यक ती माहिती बृहन्मुंबईच्या सीमेच्या आत उपलब्ध असल्यास,अल्पसूचना प्रश्न शासनास मिळाल्यापासून सात दिवसांच्या आत त्यांचे उत्तर देण्यात आले पाहिजे व माहिती
								बृहन्मुंबईबाहेरून मागवावयाची असल्यास प्रश्न शासनास मिळाल्यापासून चौदा दिवसांच्या आत त्यांचे उत्तर देण्यात आले पाहिजे.
							</fo:block>
							<fo:block>
								&#160;&#160;&#160;&#160;&#160;&#160;&#160; माननीय 
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
								ताबडतोब उत्तर देण्यास तयार नसल्यास, सदर प्रश्नास अल्पावधी प्रश्न म्हणून उत्तर का देता येणार नाही याची कारणे त्यांनी मा.
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">अध्यक्ष</xsl:when>
									<xsl:when test="houseType='upperhouse'">उप सभापती</xsl:when>
								</xsl:choose>
								महोदयांना कळवावीत अशी त्यांना विनंती करण्यात यावी(पहा-
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'">महाराष्ट्र विधानसभा  नियम ८६(३)).</xsl:when>
									<xsl:when test="houseType='upperhouse'">महाराष्ट्र विधानपरिषद  नियम ८४(३)).</xsl:when>
								</xsl:choose>
								
							</fo:block>
							<!-- <fo:block>&#160;</fo:block> -->	
							<fo:block text-align="right">
								<fo:block margin-right="2.5cm">आपला/आपली</fo:block>
								<fo:block font-size="10.5px">&#160;</fo:block>							
								<fo:block margin-right="2.5cm">कक्ष अधिकारी</fo:block>							
								<fo:block margin-right="1.5cm">महाराष्ट्र विधानमंडळ सचिवालय</fo:block>
							</fo:block>		
						<fo:block font-size="4px">&#160;</fo:block>	
									
						<fo:block>
							<fo:block font-weight="bold">प्रत: 
							<!-- &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
							&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; -->
							</fo:block>
							<fo:block margin-left="1.5cm">	
								<fo:block>							
								<xsl:choose>
									<xsl:when test="primaryMemberDesignation='मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <xsl:value-of select="primaryMemberDesignation"/>    यांच्या माहितीसाठी सादर अग्रेषित.</fo:block>
									</xsl:when>
									<xsl:when test="ministryDisplayName='मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <xsl:value-of select="ministryDisplayName"/>    यांच्या माहितीसाठी सादर अग्रेषित.</fo:block>
									</xsl:when>
									<xsl:when test="primaryMemberDesignation='उप मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <xsl:value-of select="primaryMemberDesignation"/>    यांच्या माहितीसाठी सादर अग्रेषित.</fo:block>
									</xsl:when>
									<xsl:when test="ministryDisplayName='उप मुख्‍यमंत्री'">
										<fo:block>(१) माननीय <xsl:value-of select="ministryDisplayName"/>    यांच्या माहितीसाठी सादर अग्रेषित.</fo:block>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="isSubDepartmentNameSameAsDepartmentName='true' and $endPartOfSubDepartment='विभाग'">											
												<fo:block>(१) माननीय  <fo:inline font-weight="bold"><xsl:value-of select="substring(subDepartment,1,(string-length(subDepartment)-5))"/> मंत्री  यांच्या माहितीसाठी सादर अग्रेषित.</fo:inline></fo:block>
											</xsl:when>
											<xsl:otherwise>
												<fo:block>(१) माननीय <fo:inline font-weight="bold"><xsl:value-of select="subDepartment"/> मंत्री  यांच्या माहितीसाठी सदर अग्रेषित.</fo:inline></fo:block>
											</xsl:otherwise>
										</xsl:choose>										
									</xsl:otherwise>
								</xsl:choose>
								</fo:block>
								<fo:block>(२)<xsl:value-of select="$primaryMemberConstituency"/>
								<xsl:choose>
									<xsl:when test="houseType='lowerhouse'"> वि.स.स.</xsl:when>
									<xsl:when test="houseType='upperhouse'"> वि.प.स.</xsl:when> 
								</xsl:choose>
								यांना स्वीकृत प्रश्नाच्या प्रतीसह सादर अग्रेषित.
								</fo:block>
								<fo:block>
								सदर प्रश्न सुधारलेल्या स्वरुपात स्वीकृत करण्यात आला आहे. मा. मंत्री ताबडतोब उत्तर देण्यास तयार असल्यास सदर प्रश्नाचे सदनात उत्तर देण्याचा दिनांक आपणांस नंतर कळविण्यात येईल.
								</fo:block>
							</fo:block>
						</fo:block>	
						<!-- <fo:block font-size="4px">&#160;</fo:block> -->				
					</fo:block>	
				</fo:block>		
			  </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>