<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="supportingmember" text="Supporting Member"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		/**** detail of amended bill ****/		
		function viewAmendedBillDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="ugparam="+$("#ugparam").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='bill/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:1000,height:750});
			},'html');	
		}		
		$(document).ready(function(){		
			initControls();			
			$.get('ref/billamendmentmotion/amendedBillInfo?amendedBillInfo='+$('#amendedBillInfo').val(), function(data) {
				$('#showAmendedBillText').empty();
				$('#showAmendedBillText').html(data);				
				$('#showAmendedBill').text($('#showAmendedBillText').html());
			});
			$('#showAmendedBill').click(function() {
				viewAmendedBillDetail($('#amendedBill').val());
			});
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
			/**** view supporting members status ****/
		    $("#viewStatus").click(function(){
			    $.get('billamendmentmotion/status/'+$("#billAmendmentMotion").val(),function(data){
				    $.fancybox.open(data);
			    }).fail(function(){
	    			if($("#ErrorMsg").val()!=''){
	    				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    			}else{
	    				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    			}
	    			scrollTop();
	    		});
			    return false;
		    });	
		    /**** refer section content ****/
			$('.referSectionText').click(function() {
				var currentLanguage = this.id.split("_")[1];
				if($('#referSectionText_'+currentLanguage).hasClass("referred")) {
					$('#referSectionText_'+currentLanguage).removeClass("referred");
					$('#referSectionTextImage_'+currentLanguage).attr('title',$('#referSectionTextImageTitle').val());
					$('#referredSectionTextPara_'+currentLanguage).hide();					
				} else {
					$('#referSectionText_'+currentLanguage).addClass("referred");
					$('#referSectionTextImage_'+currentLanguage).attr('title',$('#deReferSectionTextImageTitle').val());					
					$('#referredSectionTextPara_'+currentLanguage).show();
				}	
				$('html,body').animate({scrollTop:($('#scrollToReferredSectionText_'+currentLanguage).offset().top)}, 'slow');
			});
			/**** refer Bill Draft ****/
			$('.referBillDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];
				if($('#referBillDraft_'+currentLanguage).hasClass("referred")) {
					$('#referBillDraft_'+currentLanguage).removeClass("referred");
					$('#referBillDraftImage_'+currentLanguage).attr('title',$('#referBillDraftImageTitle').val());
					$('#referredBillDraftPara_'+currentLanguage).hide();				
				} else {
					if($('#referredBillDraft_'+currentLanguage).val()==""
							|| $('#referredBillDraft_'+currentLanguage).val()=="<p></p>"
							|| $('#referredBillDraft_'+currentLanguage).val()=="<br><p></p>") {
						$.get('ref/billamendmentmotion/getReferredBillDraft?billId='+$('#amendedBill').val()
								+'&language='+currentLanguage, function(data) {
							$('#referredBillDraft_'+currentLanguage).wysiwyg("setContent", data.name);
							$('#referredBillDraft_'+currentLanguage).val(data.name);
						});
					}
					$('#referBillDraft_'+currentLanguage).addClass("referred");
					$('#referBillDraftImage_'+currentLanguage).attr('title',$('#deReferBillDraftImageTitle').val());					
					$('#referredBillDraftPara_'+currentLanguage).show();
				}
				$('html,body').animate({scrollTop:($('#scrollToReferredBillDraft_'+currentLanguage).offset().top)}, 'slow');
			});
			$("#submit").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});						
				$.prompt($('#confirmApprovalMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){			        	
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			        	$.post($('form').attr('action'), $("form").serialize(), function(data){
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
	</style>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
<div class="fields clearfix watermark">
<form:form action="workflow/billamendmentmotion/supportingmember" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
	<c:choose>
	<c:when test="${status=='COMPLETED'}">
	<spring:message code="generic.taskcompleted" text="Task Already Completed Successfully"/>		
	</c:when>
	<c:otherwise>
	<spring:message code="supportingmember.heading" text="Approve request to add you as supporting member"/>		
	</c:otherwise>
	</c:choose>
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p>
		<label class="small"><spring:message code="billamendmentmotion.primaryMember" text="Primary Member"/>*</label>
		<input id="primaryMember" class="sText" type="text"  value="${primaryMemberName}" readonly="readonly" style="height: 28px;">
	</p>		
	
	<p>
		<label class="centerlabel"><spring:message code="billamendmentmotion.supportingMembers" text="Supporting Members"/></label>
		<textarea id="supportingMembers"  class="sTextarea" readonly="readonly" rows="2" cols="50">${supportingMembersName}</textarea>
		<a href="#" id="viewStatus"><spring:message code="billamendmentmotion.viewstatus" text="View Status"></spring:message></a>		
	</p>
	
	<p>	
		<label class="small"><spring:message code="billamendmentmotion.amendedBill" text="Amended Bill"/></label>
		<a id="showAmendedBill" href="#"></a>
		<span id="showAmendedBillText" style="display:none;"></span>
	</p>
	
	<div style="margin-top: 20px;">
		<fieldset>
			<p style="margin-bottom: 10px;">
			<c:set var="isFirstIcon" value="true"></c:set>
			<c:forEach var="i" items="${sectionAmendments}" varStatus="position">
			<c:choose>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
					<a href="#" class="toggleSectionAmendment" id="toggleSectionAmendment_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
					<c:set var="isFirstIcon" value="false"></c:set>
				</c:when>
				<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
					<a href="#" class="toggleSectionAmendment" id="toggleSectionAmendment_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			</p>						
			<div id="sectionAmendments_div">
				<c:forEach var="i" items="${sectionAmendments}">
					<a href="#" id="scrollToSectionAmendment_${i.language.type}"></a>
					<div id="sectionAmendment_para_${i.language.type}" style="display:none;">
						<c:set var="referredSectionText">referredSectionText_${i.language.type}</c:set>											
						<p>
							<label class="small">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.sectionNumber" text="Section Number"/></label>
							<input class="sText sectionAmendment_sectionNumber" id="sectionAmendment_sectionNumber_${i.language.type}" name="sectionAmendment_sectionNumber_${i.language.type}" value="${i.sectionNumber}" readonly="readonly"/>
							<a href="#" id="referSectionText_${i.language.type}" class="referSectionText" style="margin-left: 10px;text-decoration: none;display:${not empty requestScope[referredSectionText]?'inline':'none'};">		
								<img id="referSectionTextImage_${i.language.type}" src="./resources/images/Ico_Refer2.jpg" title="<spring:message code='billamendmentmotion.sectionAmendment.referSectionText' text='Refer Section Text'/>" class="imageLink" />
							</a>
						</p>
						<p>
							<a href="#" id="referBillDraft_${i.language.type}" class="referBillDraft" style="margin-left: 162px;text-decoration: none;">
								<img id="referBillDraftImage_${i.language.type}" src="./resources/images/Ico_Refer1.jpg" title="<spring:message code='billamendmentmotion.sectionAmendment.referBillDraft' text='Refer Bill Draft'/>" class="imageLink" />
							</a>
						</p>
						<a href="#" id="scrollToReferredBillDraft_${i.language.type}"></a>
						<p id="referredBillDraftPara_${i.language.type}" style="display: none;">
							<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.referredBillDraft" text="Referred Bill Draft"/></label>
							<textarea class="wysiwyg" id="referredBillDraft_${i.language.type}"></textarea>
						</p>
						<a href="#" id="scrollToReferredSectionText_${i.language.type}"></a>
						<p id="referredSectionTextPara_${i.language.type}" style="display: none;">
							<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.referredSectionText" text="Referred Section Text"/></label>
							<textarea class="wysiwyg" id="referredSectionText_${i.language.type}" readonly="readonly">${requestScope[referredSectionText]}</textarea>
						</p>
						<p>
							<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.amendingContent" text="Amendment Content"/></label>
							<textarea class="wysiwyg sectionAmendment" id="sectionAmendment_amendingContent_${i.language.type}" name="sectionAmendment_amendingContent_${i.language.type}" readonly="readonly">${i.amendingContent}</textarea>
						</p>
						<input type="hidden" name="sectionAmendment_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="sectionAmendment_language_id_${i.language.type}" value="${i.language.id}">
						<input type="hidden" name="sectionAmendment_amendedSection_id_${i.language.type}" value="${i.amendedSection.id}">				
					</div>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<c:choose>
	<c:when test="${status=='COMPLETED' || status=='TIMEOUT'}">
	<p>
	<label class="small"><spring:message code="billamendmentmotion.decisionstatus" text="Decision?"/>*</label>
	<input id="formattedDecisionStatus" name="formattedDecisionStatus" class="sText" readonly="readonly" value="${formattedDecisionStatus}">
	<input id="decisionStatus" name="decisionStatus" class="sText" readonly="readonly" value="${decisionStatus}" type="hidden">
	</p>	
	<div class="fields">
		<h2></h2>
		<%-- <p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
		</p> --%>
	</div>	
	</c:when>
	<c:otherwise>
	<p>
		<label class="small"><spring:message code="billamendmentmotion.decisionstatus" text="Decision?"/>*</label>
		<form:select path="decisionStatus" cssClass="sSelect" items="${decisionStatus}" itemLabel="name" itemValue="id"/>
	</p>
	<div class="fields">
		<h2></h2>
		<p class="tright">
		<c:if test="${workflowstatus!='COMPLETED' and bulkedit!='yes'}">
			<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
		<c:if test="${workflowstatus!='COMPLETED' and bulkedit=='yes'}">
			<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
		</c:if>
		</p>
	</div>	
	</c:otherwise>
	</c:choose>	
	<p style="display:none;">
		<label class="small"><spring:message code="supportingmember.remarks" text="Remarks"/>*</label>
		<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
		<form:errors path="remarks" cssClass="validationError"></form:errors>
	</p>	
		
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<%-- <form:hidden path="workflowStarted"/> --%>
	<%-- <form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/> --%>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
	<form:hidden path="approvalDate"/>
	<input type="hidden" id="currentSupportingMember" name="currentSupportingMember" value="${currentSupportingMember}">
	<input type="hidden" id="requestReceivedOnDate" name="requestReceivedOnDate" value="${requestReceivedOnDate }">
	<input type="hidden" id="billAmendmentMotion" name="billAmendmentMotion" value="${billAmendmentMotion}">
	<input type="hidden" id="task" name="task" value="${task}">	
	<input type="hidden" id="workflowDetailsId" name="workflowDetailsId" value="${workflowDetailsId}">
	<input type="hidden" id="workflowdetails" name="workflowdetails" value="${workflowdetails}">	
	<input type="hidden" id="requestReceivedOn" name="requestReceivedOn" value="${requestReceivedOn }">	
	<input type="hidden" id="defaultBillLanguage" name="defaultBillLanguage" value="${defaultBillLanguage}">	
	<input id="amendedBill" name="amendedBill" value="${amendedBill}" type="hidden">
	<input id="amendedBillLanguages" name="amendedBillLanguages" value="${amendedBillLanguages}" type="hidden">
</form:form>	
	<input type="hidden" id="confirmApprovalMsg" value="<spring:message code="billamendmentmotion.confirmApprovalMsg" text="Do you want to complete task now?"></spring:message>">
	<input id="referSectionTextImageTitle" value="<spring:message code='billamendmentmotion.referSectionTextImageTitle' text='Refer Section Text'/>" type="hidden">
	<input id="deReferSectionTextImageTitle" value="<spring:message code='billamendmentmotion.deReferSectionTextImageTitle' text='De-Refer Section Text'/>" type="hidden">
	<input id="amendedBillInfo" value="${amendedBillInfo}" type="hidden">
</div>
</body>
</html>