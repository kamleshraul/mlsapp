<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="billamendmentmotion" text="Bill Amendment Motion Information System"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">	
		//----------------------revise drafts script----------------------//
		function clearUnrevisedSectionAmendments() {
			$('.revisedSectionAmendment').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedSectionAmendmentPara_'+currentLanguage).css('display')=='none') {					
					$(this).val("");
					$(this).wysiwyg("setContent","");
				}				
			});
			$('.revisedSectionAmendment_sectionNumber').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).css('display')=='none') {	
					alert($(this).attr('id'));
					$(this).val("");
				}				
			});
		}
		
		$(document).ready(function(){		
			initControls();
			
			/**** show section amendment for only default bill language ****/
			$('.sectionAmendment').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#sectionAmendment_para_'+currentLanguage).show();					
				} else {
					$('#sectionAmendment_para_'+currentLanguage).hide();										
				}				
			});
			
			/**** toggle section amendment for given language icon ****/
			$('.toggleSectionAmendment').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#sectionAmendment_para_'+currentLanguage).css('display')=='none') {
					$('#sectionAmendment_para_'+currentLanguage).show();			
					$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+currentLanguage).offset().top)}, 'slow');
				} else {
					$('#sectionAmendment_para_'+currentLanguage).hide();	
					$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+$('#defaultBillLanguage').val()).offset().top)}, 'slow');
				}				
			});	
			
			/**** show revised section amendment content for given language if already exists ****/
			$('.revisedSectionAmendment').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='' 
						&& $(this).val()!=$('#sectionAmendment_amendingContent_'+currentLanguage).val()
						&& $(this).val()!='<p></p>' && $(this).val()!='<br><p></p>') {		
					$('#revisedSectionAmendmentPara_'+currentLanguage).show();
					$('#reviseSectionAmendment_amendingContent_icon_'+currentLanguage).attr('title',$('#unReviseSectionAmendment_amendingContent').val());
				} else {
					$('#revisedSectionAmendmentPara_'+currentLanguage).hide();
					if($("#sectionAmendment_amendingContent_"+currentLanguage).val()!=undefined && $("#sectionAmendment_amendingContent_"+currentLanguage).val()!='') {
						$(this).val($("#sectionAmendment_amendingContent_"+currentLanguage).val());
					}					
				}
			});
			
			/**** toggle revised section amendment content for given language icon ****/
			$('.reviseSectionAmendment_amendingContent').click(function() {				
				var currentLanguage = this.id.split("_")[2];				
				if($('#revisedSectionAmendmentPara_'+currentLanguage).css('display')=='none') {
					$('#revisedSectionAmendmentPara_'+currentLanguage).show();
					$('#reviseSectionAmendment_amendingContent_icon_'+currentLanguage).attr('title',$('#unReviseSectionAmendment_amendingContent').val());
					//$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+$('#defaultBillLanguage').val()).offset().top)}, 'slow');
				} else {
					$('#revisedSectionAmendmentPara_'+currentLanguage).hide();
					$('#reviseSectionAmendment_amendingContent_icon_'+currentLanguage).attr('title',$('#reviseSectionAmendment_amendingContent').val());
					//$('html,body').animate({scrollTop:($('#scrollToSectionAmendment_'+$('#defaultBillLanguage').val()).offset().top)}, 'slow');
				}
				return false;
			});
			
		    /**** save translation (send later) ****/
		    $('#save').click(function() {
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				clearUnrevisedSectionAmendments();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
				$.post($('form').attr('action')+'?operation=saveTranslation',
						$("form").serialize(), function(data){
					$('.tabContent').html(data);
						$('html').animate({scrollTop:0}, 'slow');
						$('body').animate({scrollTop:0}, 'slow');	
					$.unblockUI();
				});				
			});
			/**** submit translation ****/ 
		    $('#submit').click(function() {
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				clearUnrevisedSectionAmendments();
				$.prompt($('#sendTranslationMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$("#endFlagForAuxillaryWorkflow").val("end");
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });						
			        	$.post($('form').attr('action')+'?operation=sendTranslation',  
			    	            $("form").serialize(),  
			    	            function(data){
			       					$('.tabContent').html(data);
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	   				 	   				
			    	            });
	    	            }
				}});			
		        return false;
			});		    
		});
		
		</script>
		
		<style type="text/css">
	        @media print {
	            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
	            display:none;
	            }
	        }
	    </style>
	    
	    <style type="text/css">
			.imageLink{
				width: 18px;
				height: 18px;				
				/* box-shadow: 2px 2px 5px #000000;
				border-radius: 5px;
				padding: 2px;
				border: 1px solid #000000; */ 
			}
			
			/* .imageLink:hover{
				box-shadow: 2px 2px 5px #888888;
				border-radius: 5px;
				padding: 2px;
				border: 1px solid #888888; 
			} */
			
			.impIcons{
				box-shadow: 2px 2px 2px black;
			}	
			
			#s7C{
				width: 24px;
			}			
		</style>
	</head> 

	<body>
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields clearfix watermark">		
			<div id="assistantDiv">
				<form:form action="workflow/billamendmentmotion" method="PUT" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceType} ${formattedNumber}</h2>
					<form:errors path="version" cssClass="validationError"/>
					
					<div style="margin-top: 20px;">
						<fieldset>
							<p style="margin-left: 145px;margin-bottom: -10px;">
							<c:forEach var="i" items="${sectionAmendments}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleSectionAmendment" id="toggleSectionAmendment_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>	
							</p>																		
							<div id="sectionAmendments_div">
								<c:forEach var="i" items="${sectionAmendments}">
									<c:set var="revisedSectionAmendment_sectionNumber">revisedSectionAmendment_sectionNumber_${i.language.type}</c:set>
									<c:set var="revisedSectionAmendment_amendingContent">revisedSectionAmendment_amendingContent_${i.language.type}</c:set>
									<c:set var="revisedSectionAmendmentId">revisedSectionAmendment_id_${i.language.type}</c:set>
									<c:set var="revisedSectionAmendment_amendedSectionId">revisedSectionAmendment_amendedSection_id_${i.language.type}</c:set>
									
									<a href="#" id="scrollToSectionAmendment_${i.language.type}"></a>
									<div id="sectionAmendment_para_${i.language.type}" style="display:none;">
										<input type="hidden" class="sText sectionAmendment_sectionNumber" id="sectionAmendment_sectionNumber_${i.language.type}" name="sectionAmendment_sectionNumber_${i.language.type}" value="${i.sectionNumber}" readonly="readonly"/>
										<input type="hidden" class="sText revisedSectionAmendment_sectionNumber" id="revised_sectionAmendment_sectionNumber_${i.language.type}" name="revised_sectionAmendment_sectionNumber_${i.language.type}" value="${requestScope[revisedSectionAmendment_sectionNumber]}"/>
										<p>
											<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.amendingContent" text="Amendment Content"/></label>
											<textarea class="wysiwyg sectionAmendment" id="sectionAmendment_amendingContent_${i.language.type}" name="sectionAmendment_amendingContent_${i.language.type}" readonly="readonly">${i.amendingContent}</textarea>
											<input type="hidden" name="sectionAmendment_id_${i.language.type}" value="${i.id}">
											<input type="hidden" name="sectionAmendment_language_id_${i.language.type}" value="${i.language.id}">
											<input type="hidden" name="sectionAmendment_amendedSection_id_${i.language.type}" value="${i.amendedSection.id}">
										</p>
										<p>
											<a href="#" class="reviseSectionAmendment_amendingContent" id="reviseSectionAmendment_amendingContent_${i.language.type}" style="margin-left: 165px;">
												<img id="reviseSectionAmendment_amendingContent_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='billamendmentmotion.reviseSectionAmendment_amendingContent' text='Revise This Amendment Content'></spring:message>" class="imageLink" />
											</a>
										</p>	
										<a href="#" id="scrollTo_revisedSectionAmendment_amendingContent_${i.language.type}"></a>
										<p id="revisedSectionAmendmentPara_${i.language.type}" style="display:none;">
											<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.revisedSectionAmendment" text=" Revised Amendment Content"/></label>						
											<textarea class="wysiwyg revisedSectionAmendment" id="revised_sectionAmendment_amendingContent_${i.language.type}" name="revised_sectionAmendment_amendingContent_${i.language.type}">${requestScope[revisedSectionAmendment_amendingContent]}</textarea>
											<input type="hidden" name="revised_sectionAmendment_id_${i.language.type}" value="${requestScope[revisedSectionAmendmentId]}">												
											<input type="hidden" name="revised_sectionAmendment_amendedSection_id_${i.language.type}" value="${requestScope[revisedSectionAmendment_amendedSectionId]}">
										</p>									
									</div>									
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<p id="translationStatusDiv">
						<label class="small"><spring:message code="billamendmentmotion.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
				
					<p>
						<label class="wysiwyglabel"><spring:message code="billamendmentmotion.remarksForTranslation" text="Translation Remarks"/></label>
						<form:textarea path="remarksForTranslation" cssClass="wysiwyg" readonly="true"></form:textarea>
					</p>
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<c:choose>
								<c:when test="${workflowstatus!='COMPLETED' and translationStatusType=='billamendmentmotion_final_translation'}">
									<input id="save" type="button" value="<spring:message code='generic.save' text='Save'/>" class="butDef">	
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Send Translation'/>" class="butDef">								
								</c:when>								
							</c:choose>
						</p>
					</div>
					<form:hidden path="id"/>
					<form:hidden path="locale"/>
					<form:hidden path="version"/>
					<form:hidden path="workflowStarted"/>	
					<form:hidden path="endFlag"/>
					<form:hidden path="level"/>
					<form:hidden path="localizedActorName"/>
					<form:hidden path="workflowDetailsId"/>
					<form:hidden path="opinionSoughtFromLawAndJD"/>		
					<form:hidden path="file"/>
					<form:hidden path="fileIndex"/>	
					<form:hidden path="fileSent"/>
					<input id="customStatus" name="customStatus" type="hidden">
					<input id="levelForAuxillaryWorkflow" name="levelForAuxillaryWorkflow" value="${level}" type="hidden">
					<input id="localizedActorNameForAuxillaryWorkflow" name="localizedActorNameForAuxillaryWorkflow" type="hidden">
					<input id="endFlagForAuxillaryWorkflow" name="endFlagForAuxillaryWorkflow" value="end" type="hidden">
					<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="status" value="${status }">
					<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
					<input type="hidden" name="setSubmissionDate" id="setSubmissionDate" value="${submissionDate}">
					<input type="hidden" name="setDateOfOpinionSoughtFromLawAndJD" id="setDateOfOpinionSoughtFromLawAndJD" value="${dateOfOpinionSoughtFromLawAndJD}">
					<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input id="amendedBill" name="amendedBill" value="${amendedBill}" type="hidden">
					<input id="amendedBillLanguages" name="amendedBillLanguages" value="${amendedBillLanguages}" type="hidden">
					<input id="isMotionRaisedByMinister" name="isMotionRaisedByMinister" value="${isMotionRaisedByMinister}" type="hidden">					
				</form:form>
				<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
				<input id="usergroupType" name="usergroupType" type="hidden" value="${usergroupType}">
				
				<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
				<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
				<input id="sendTranslationMessage" name="sendTranslationMessage" value="<spring:message code='bill.sendTranslationMessage' text='Do You Want To Send Translation?'></spring:message>" type="hidden">
				<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
				<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
				<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
				<input type="hidden" id="reviseSectionAmendment_amendingContent" value="<spring:message code="billamendmentmotion.reviseSectionAmendment_amendingContent" text="Revise This Section Amendment"></spring:message>">
				<input type="hidden" id="unReviseSectionAmendment_amendingContent" value="<spring:message code="billamendmentmotion.unReviseSectionAmendment_amendingContent" text="Un-Revise This Section Amendment"></spring:message>">		
				<input type="hidden" id="reviseSectionAmendment_sectionNumber" value="<spring:message code="billamendmentmotion.reviseSectionAmendment_sectionNumber" text="Revise This Section Number"></spring:message>">
				<input type="hidden" id="unReviseSectionAmendment_sectionNumber" value="<spring:message code="billamendmentmotion.unReviseSectionAmendment_sectionNumber" text="Un-Revise This Section Number"></spring:message>">
				<input type="hidden" id="workflowtype" value="${workflowtype}"/>
				<input type="hidden" id="workflowstatus" value="${workflowstatus}"/>
				<input type="hidden" id="confirmApprovalMsg" value="<spring:message code="billamendmentmotion.confirmApprovalMsg" text="Do you want to complete task now?"></spring:message>">
			</div>
		</div>		
	</body>
</html>