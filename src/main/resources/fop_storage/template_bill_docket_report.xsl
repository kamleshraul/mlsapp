<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common"
    xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
    <!-- <xsl:variable name="message" select="document('Lang.xml')/Lang/fr/text1"/>
  	<xsl:variable name="font" select="document('Lang.xml')/Lang/fr/fontname"/> -->
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="rootNode" select="root"/>
    
    <xsl:variable name="pageLayout" select="simple"/>

	<!-- declares common variables such as language & font that will be used in all report stylesheets -->
    <xsl:include href="common_variables.xsl"/>
    
   <xsl:template match="root">

	    <!-- <xsl:text disable-output-escaping="yes">
		    &lt;!DOCTYPE fo:root [&lt;!ENTITY nbsp "&amp;#160;"&gt;]&gt;
	    </xsl:text> -->		    
	
	    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">	  
	    	<fo:layout-master-set>				
				<fo:simple-page-master master-name="first"
	                  	page-height="29.7cm" page-width="21cm"
	                  	margin-top="2cm" margin-bottom="2cm"
	                  	margin-left="2.5cm" margin-right="2.5cm">
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
					<fo:block text-align="center" font-family="{$font}">
					   	<!-- content for header for first page -->
					</fo:block>
			    </fo:static-content>
		
				<!-- footer -->
		    	<fo:static-content flow-name="ra-first">
					<fo:block  text-align="center" font-family="{$font}">
					   	<!-- content for footer for first page -->
					</fo:block>
			    </fo:static-content>
	
				<!-- body -->
	            <fo:flow flow-name="xsl-region-body">		    
			       	<!-- content as per report -->			       
		       		<fo:block margin-top="16cm" text-align="center" font-size="20px" font-weight="bold">
		       			<xsl:choose>
		       				<xsl:when test="element_19='विधानसभा'">
		       					L.A.BILL No. <xsl:value-of select="element_23"/> OF <xsl:value-of select="element_3"/>
		       				</xsl:when>	   
		       				<xsl:when test="element_19='विधानपरिषद'">
		       					L.C.BILL No. <xsl:value-of select="element_23"/> OF <xsl:value-of select="element_3"/>
		       				</xsl:when>  			
		       			</xsl:choose>
						
					</fo:block>
					
					<fo:block>&#160;</fo:block>
					
					<xsl:if test="element_18!='english'">
						<xsl:choose>
							<xsl:when test="element_8!=''">
								<fo:block text-align="center"  font-family="{$font}" font-size="16px">
									<xsl:value-of select="element_8" />														
								</fo:block>
								<fo:block>&#160;</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block text-align="center"  font-family="{$font}" font-size="16px">
									title not found for english language.
								</fo:block>							
								<fo:block>&#160;</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
					
					<xsl:choose>
						<xsl:when test="element_7!=''">
							<fo:block text-align="center"  font-family="{$font}" font-size="16px">
								<xsl:value-of select="element_7" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block text-align="center"  font-family="{$font}" font-size="16px">
								title not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					
					<fo:block>&#160;</fo:block>
					
					<xsl:choose>
						<xsl:when test="element_9!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_9" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								content draft not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>								
					   
					<xsl:if test="contains(element_24, 'statementOfObjectAndReasonDraft')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								उद्देश व कारणे यांचे निवेदन
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								STATEMENT OF OBJECTS AND REASONS
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								SOR Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_10!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_10" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								statement of object and reason not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>	
					
					<xsl:if test="contains(element_24, 'financialMemorandumDraft')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								Financial Memorandum Marathi Header...
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								FINANCIAL MEMORANDUM
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								Financial Memorandum Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_11!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_11" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								Financial Memorandum not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					
					<xsl:if test="contains(element_24, 'statutoryMemorandumDraft')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								Statutory Memorandum Marathi Header...
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								STATUTORY MEMORANDUM
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								Statutory Memorandum Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_12!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_12" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								Statutory Memorandum not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					
					<xsl:if test="contains(element_24, 'annexureForAmendingBill') and element_20='amending'">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								annexureForAmendingBill marathi header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								annexureForAmendingBill english header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								annexureForAmendingBill hindi header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_13!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_13" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								annexure for bill not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					
					<xsl:if test="contains(element_24, 'opinionFromLawAndJD')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								opinionFromLawAndJD marathi header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								opinionFromLawAndJD english header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								opinionFromLawAndJD Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_15!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_15" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								opinion From Law And JD not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					
					<xsl:if test="contains(element_24, 'recommendationFromGovernor')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromGovernor marathi header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromGovernor english header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromGovernor Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_16!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_16" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								recommendation From Governor not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					
					<xsl:if test="contains(element_24, 'recommendationFromPresident')">
					<xsl:choose>
						<xsl:when test="element_18='marathi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromPresident marathi header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='english'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromPresident english header..
							</fo:block>
						</xsl:when>
						<xsl:when test="element_18='hindi'">
							<fo:block text-align="center" font-family="($font)" font-size="18px" font-weight="bold">
								recommendationFromPresident Hindi Header..
							</fo:block>
						</xsl:when>
					</xsl:choose>
					<fo:block>&#160;</fo:block>
					<xsl:choose>
						<xsl:when test="element_17!=''">
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								<xsl:apply-templates select="element_17" />														
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block font-family="{$font}" font-size="16px" break-after="page">
								recommendation From President not found for <xsl:value-of select="element_18"/> language.
							</fo:block>							
						</xsl:otherwise>
					</xsl:choose>
					</xsl:if>
					<!-- Docket for Bill -->
					<fo:block text-align="right" font-family="{$font}" font-size="16px">
						<xsl:choose>
							<xsl:when test="element_18='marathi'">
								<fo:block text-align="right" font-family="($font)" font-size="20px" font-weight="bold">
									महाराष्ट्र विधानमंडळ सचिवालय
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='english'">
								<fo:block text-align="right" font-family="($font)" font-size="20px" font-weight="bold">
									MAHARASHTRA LEGISLATURE SECRETARIAT
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='hindi'">
								<fo:block text-align="right" font-family="($font)" font-size="20px" font-weight="bold">
									महाराष्ट्र विधानमंडळ सचिवालय (translate in hindi)
								</fo:block>
							</xsl:when>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="element_18='marathi'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									[सन <xsl:value-of select="element_25"/> चे <xsl:value-of select="element_19"/> विधेयक क्रमांक <xsl:value-of select="element_22"/>.]
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='english'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px" font-weight="bold">
									[L. A. BILL No. <xsl:value-of select="element_23"/> OF <xsl:value-of select="element_25"/>.]
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='hindi'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									[साल <xsl:value-of select="element_25"/> का विधेयक क्रमांक <xsl:value-of select="element_22"/>.]
								</fo:block>
							</xsl:when>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="element_18='english'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px" font-weight="bold">
									[<xsl:value-of select="element_7"/>.]
								</fo:block>
							</xsl:when>
							<xsl:otherwise>
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									[<xsl:value-of select="element_7"/>.]
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
						<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
							[<xsl:value-of select="element_4"/>,<xsl:value-of select="element_28"/>
							<fo:block/>
							<xsl:choose>
							<xsl:when test="element_27='bills_nonofficial'">
								प्रभारी सदस्य.]
							</xsl:when>
							<xsl:when test="element_27='bills_government' and element_21='मुख्‍यमंत्री'">							
								<xsl:value-of select="element_21"/>.]
							</xsl:when>
							<xsl:otherwise test="element_27='bills_government' and element_21!='मुख्‍यमंत्री'">							
								<xsl:value-of select="element_5"/>.]
							</xsl:otherwise>							
							</xsl:choose>							
						</fo:block>
						<xsl:choose>
							<xsl:when test="element_18='marathi'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									<xsl:value-of select="element_26"/>,<fo:block/>									
									सचिव-१ (कार्यभार),<fo:block/>
									महाराष्ट्र <xsl:value-of select="element_19"/>.
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='english'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									<xsl:value-of select="element_26"/>,<fo:block/>
									Principal Secretary,<fo:block/>
									Maharashtra Legislative <xsl:value-of select="element_19"/>.								
								</fo:block>
							</xsl:when>
							<xsl:when test="element_18='hindi'">
								<fo:block text-align="right" margin-top="3.5cm" font-family="($font)" font-size="16px">
									<xsl:value-of select="element_26"/>,<fo:block/>
									सचिव-१ (कार्यभार),<fo:block/>
									महाराष्ट्र <xsl:value-of select="element_19"/>.
								</fo:block>
							</xsl:when>
						</xsl:choose>
					</fo:block>								          
	            </fo:flow>
	        </fo:page-sequence>        
	    </fo:root>
    </xsl:template>       
    
    <!-- declares common templates as they will be applied in all report stylesheets -->
    <xsl:include href="common_templates.xsl"/>
</xsl:stylesheet>