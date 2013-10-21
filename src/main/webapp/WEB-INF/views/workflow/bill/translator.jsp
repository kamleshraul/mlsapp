<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="bill" text="Bill Information System"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">	
		/**** detail of clubbed, referenced and lapsed bills ****/		
		function viewBillDetail(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#typeOfSelectedDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			var resourceURL='bill/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:800,height:700});
			},'html');	
		}		
		
		/**** Removing Referred Ordinance from replace_ordinance Bill ****/
		function removeReferredOrdinance(){
			if($('#viewReferredOrdinance').text()!="-") {
				$('#viewReferredOrdinance').css('text-decoration','none');
				$.prompt($('#dereferOrdinanceWarningMessage').val(),{
					buttons: {Ok:true}, callback: function(v){
				   		if(v){
				   			$('#referredOrdinance').val("");		
				   			$('#viewReferredOrdinance').text("-");
				   			$('#viewReferredOrdinance').css('text-decoration','none');
							$('#referredOrdinanceYear').text("");
				   		}     						
					}
				});
			}				
		}
		
		function referenceForBill(refType){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
			$.get('bill/referAct/init?action='+refType,function(data){		
				if(refType=='act'){
					$("#referringActResultDiv").html(data);					
					$("#referringActResultDiv").show();
				}else if(refType=='ordinance'){
					$("#referringOrdinanceResultDiv").html(data);					
					$("#referringOrdinanceResultDiv").show();
				}

				$.unblockUI();	
				$("#assistantDiv").hide();
				$("#backToBillDiv").show();
			},'html');
		}
		/**** sub departments ****/
		function loadSubDepartments(ministry){
			$.get('ref/ministry/subdepartments?ministry='+ministry,function(data){
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#subDepartment").html(subDepartmentText);			
				}else{
					$("#subDepartment").empty();
					var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
					$("#subDepartment").html(subDepartmentText);				
				}
			});
		}
		//----------------------revise drafts script----------------------//
		function clearUnrevisedDrafts() {
			$('.revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedTitlePara_'+currentLanguage).css('display')=='none') {						
					$(this).val("");
				}				
			});			
			$('.revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedContentDraftPara_'+currentLanguage).css('display')=='none') {						
					$(this).val("");
				}				
			});
			if($('#typeOfSelectedBillType').val()=="amending") {
				$('.revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];
					if($('#revisedAnnexureForAmendingBillPara_'+currentLanguage).css('display')=='none') {						
						$(this).val("");
					}				
				});
			}
			$('.revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).css('display')=='none') {
					$(this).val("");		
				}				
			});
			$('.revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedFinancialMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$(this).val("");
				}				
			});
			$('.revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];
				if($('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$(this).val("");
				}				
			});			
		};
		
		//recursive function to hide related questions for checklist when checkbox gets unchecked.
		function hideRelatedDiv(checkboxId) {
			$('#'+checkboxId+'_div').hide();
			if($('#'+checkboxId+'_div').find('input[type=checkbox]').length>0) {
				var internalCheckboxId = $('#'+checkboxId+'_div').find('input[type=checkbox]').attr('id');
				$('#'+internalCheckboxId).removeAttr('checked');
				hideRelatedDiv(internalCheckboxId);																				
			}
		}		
		
		$(document).ready(function(){
			if($('#typeOfSelectedBillType').val() != 'amending') {
				$('#referredActDiv').hide();
				$('#annexuresForAmendingBill_div').hide();
			}
			if($('#typeOfSelectedBillType').val() != 'replace_ordinance') {
				$('#referredOrdinanceDiv').hide();
			}
			$('#billType').change(function() {
				$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
					
					if(data!=undefined || data!='') {
						if(data=='amending') {
							$('#referredActDiv').show();
							$('#annexuresForAmendingBill_div').show();
							$('#referredOrdinanceDiv').hide();
						} else if(data=='replace_ordinance'){
							$('#referredOrdinanceDiv').show();
							$('#referredActDiv').hide();
							$('#annexuresForAmendingBill_div').hide();
						}else{
							$('#referredActDiv').hide();
							$('#annexuresForAmendingBill_div').hide();
							$('#referredOrdinanceDiv').hide();
						}
					} else {
						alert("Some Error Occured!");
					}
				});
			});
			/**** Referring Act for Amendment Bill ****/
			$('#referAct').click(function() {
				referenceForBill('act');
			});			
			
			$('#referOrdinance').click(function() {
				referenceForBill('ordinance');
			});	
			/**** view detail of referred act (currently showing pdf of act) ****/		
			$('#viewReferredAct').click(function() {
				if(this.text!='-') {					
					var referredActId = $('#referredAct').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='act/'+referredActId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			/**** view detail of referred ordinance****/		
			$('#viewReferredOrdinance').click(function() {
				if(this.text!='-') {					
					var referredOrdinanceId = $('#referredOrdinance').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='ordinance/'+referredOrdinanceId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			//to check/uncheck checkboxes for current checklist selection by assistant
			$('.checklist_checkbox_fields').each(function() {
				var fieldNumber = this.id.split("_")[3];
				//alert("value for field " + fieldNumber + ": " + $(this).val());
				if($(this).val()=='yes') {
					$('#checklist_checkbox_'+fieldNumber).attr('checked','checked');
					$('#checklist_checkbox_'+fieldNumber+'_div').show();
				} else {
					$('#checklist_checkbox_'+fieldNumber).removeAttr('checked');															
				}
			});
			
			$('.checklist_checkboxes').click(function() {
				if($(this).attr('checked')=='checked') {
					$('#'+this.id+'_div').show();
				} else {
					hideRelatedDiv(this.id);
					/* $('#'+this.id+'_div').hide();
					if($('#'+this.id+'_div').find('input[type=checkbox]').length>0) {
						var internalCheckboxId = $('#'+this.id+'_div').find('input[type=checkbox]').id;
						$('#'+internalCheckboxId+'_div').hide();																				
					} */
				}				
			});
					
			/**** Ministry Changes ****/
			$("#ministry").change(function(){
				if($(this).val()!=''){
					loadSubDepartments(ministry, department);
				}else{
					$("#subDepartment").empty();				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				}
			});
			/**** Citations ****/
			$("#viewCitation").click(function(){
				$.get('question/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			    },'html');
			    return false;
			});	
			/**** Revisions ****/
		    $(".viewRevisions").click(function(){
		    	var thingToBeRevised = this.id.split("_")[1];
			    $.get('bill/revisions/'+$("#id").val()+"?thingToBeRevised="+thingToBeRevised,function(data){
				    $.fancybox.open(data);
			    });
			    return false;
		    });
		    /**** Contact Details ****/
		    $("#viewContacts").click(function(){
			    var primaryMember=$("#primaryMember").val();
			    var supportingMembers=$("#selectedSupportingMembers").val();
			    var members=primaryMember;
			    if(supportingMembers!=null){
				    if(supportingMembers!=''){
					    members=members+","+supportingMembers;
				    }
			    }
			    $.get('question/members/contacts?members='+members,function(data){
				    $.fancybox.open(data);
			    });
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
				clearUnrevisedDrafts();
				$('.checklist_checkboxes').each(function() {
					var fieldNumber = this.id.split("_")[2];
					if($(this).attr('checked')=='checked') {						
						$('#checklist_checkbox_field_'+fieldNumber).val('yes');
					} else {
						$('#checklist_checkbox_field_'+fieldNumber).val('no');
						if($('#'+this.id+'_div').find('textarea').length>0) {
							$('#'+this.id+'_div').find('textarea').val("");							
						}
						else if($('#'+this.id+'_div').find('input[type=checkbox]').length>0) {
							$('#'+this.id+'_div').find('input[type=checkbox]').removeAttr('checked');																				
						}
					}					
				});
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
				clearUnrevisedDrafts();
				$('.checklist_checkboxes').each(function() {
					var fieldNumber = this.id.split("_")[2];
					if($(this).attr('checked')=='checked') {						
						$('#checklist_checkbox_field_'+fieldNumber).val('yes');
					} else {
						$('#checklist_checkbox_field_'+fieldNumber).val('no');
						if($('#'+this.id+'_div').find('textarea').length>0) {
							$('#'+this.id+'_div').find('textarea').val("");							
						}
						else if($('#'+this.id+'_div').find('input[type=checkbox]').length>0) {
							$('#'+this.id+'_div').find('input[type=checkbox]').removeAttr('checked');																				
						}
					}					
				});								
				$.prompt($('#sendTranslationMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$("#endFlagForTranslation").val("end");
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
		    /**** On Page Load ****/
		    if($("#ministrySelected").val()==''){
				$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			}
			if($("#subDepartmentSelected").val()==''){
				$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}			
			//----------------------revise drafts script----------------------//			
			$('.revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedTitlePara_'+currentLanguage).show();
					$('#reviseTitle_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#revisedTitlePara_'+currentLanguage).hide();
					if($("#title_text_"+currentLanguage).val()!=undefined && $("#title_text_"+currentLanguage).val()!='') {
						$(this).val($("#title_text_"+currentLanguage).val());
					}					
				}
			});
			
			$('.reviseTitle').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedTitlePara_'+currentLanguage).css('display')=='none') {
					$('#revisedTitlePara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedTitlePara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			
			$('.revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedContentDraftPara_'+currentLanguage).show();
					$('#reviseContentDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#revisedContentDraftPara_'+currentLanguage).hide();
					$(this).val($("#contentDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseContentDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedContentDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedContentDraftPara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedContentDraftPara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			
			if($('#typeOfSelectedBillType').val()=="amending") {
				$('.revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];	
					if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).show();
						$('#reviseAnnexureForAmendingBill_'+currentLanguage).text('Un-Revise This Draft');
					} else {
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();
						$(this).val($("#annexureForAmendingBill_text_"+currentLanguage).val());
					}
				});
			}
			
			$('.reviseAnnexureForAmendingBill').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedAnnexureForAmendingBillPara_'+currentLanguage).css('display')=='none') {
					$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			
			$('.revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();
					$('#reviseStatementOfObjectAndReasonDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();
					$(this).val($("#statementOfObjectAndReasonDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseStatementOfObjectAndReasonDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			
			$('.revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseFinancialMemorandumDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#financialMemorandumDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseFinancialMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedFinancialMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			
			$('.revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseStatutoryMemorandumDraft_'+currentLanguage).text('Un-Revise This Draft');
				} else {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#statutoryMemorandumDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseStatutoryMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();
					$(this).text('Un-Revise This Draft');
				} else {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();
					$(this).text('Revise This Draft');
				}
				return false;
			});
			//------------------------------------------------------------------
		});
		
		</script>
		 <style type="text/css">
	        @media print {
	            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
	            display:none;
	            }
	        }
	    </style>
	</head> 

	<body>
		<div class="fields clearfix watermark">		
			<div id="assistantDiv">
				<form:form action="workflow/bill" method="PUT" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceTypeForBill} ${formattedNumber}</h2>
					<form:errors path="version" cssClass="validationError"/>
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.houseType" text="House Type"/>*</label>
						<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="houseType" name="houseType" value="${houseType}" type="hidden">
						<form:errors path="houseType" cssClass="validationError"/>			
					</p>	
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.year" text="Year"/>*</label>
						<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.sessionType" text="Session Type"/>*</label>		
						<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="session" name="session" value="${session}"/>
						<form:errors path="session" cssClass="validationError"/>	
					</p>
					
					<p style="display:none;">
							<label class="small"><spring:message code="bill.deviceType" text="Device Type"/>*</label>
							<input id="formattedDeviceTypeForBill" name="formattedDeviceTypeForBill" value="${formattedDeviceTypeForBill}" class="sText" readonly="readonly">
							<input id="type" name="type" value="${deviceTypeForBill}" type="hidden">
							<input id="originalType" name="originalType" value="${originalDeviceType}" type="hidden">			
							<form:errors path="type" cssClass="validationError"/>		
					</p>	
					
					<c:if test="${!(empty domain.number)}">
					<p>
						<label class="small"><spring:message code="bill.number" text="bill Number"/>*</label>
						<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="number" name="number" value="${domain.number}" type="hidden">
						<form:errors path="number" cssClass="validationError"/>
					</p>
					</c:if>
					
					<c:if test="${!(empty submissionDate)}">
					<p>
					<label class="small"><spring:message code="bill.submissionDate" text="Submitted On"/></label>
					<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
					<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
					</p>
					</c:if>
					
					<table style="width: 100%;">
						<tr>
							<td>				
								<p>
									<label class="small"><spring:message code="bill.ministry" text="Ministry"/>*</label>
									<select name="ministry" id="ministry" class="sSelect">
										<c:forEach items="${ministries }" var="i">
											<c:choose>
												<c:when test="${i.id==ministrySelected }">									<option value="${i.id }" selected="selected">${i.name}</option>
												</c:when>
												<c:otherwise>
													<option value="${i.id }" >${i.name}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>		
									<form:errors path="ministry" cssClass="validationError"/>
								</p>
								<p>
									<label class="small"><spring:message code="bill.subdepartment" text="Sub Department"/></label>
									<select name="subDepartment" id="subDepartment" class="sSelect">
									<c:forEach items="${subDepartments }" var="i">
										<c:choose>
											<c:when test="${i.id==subDepartmentSelected }">
												<option value="${i.id }" selected="selected">${i.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${i.id }" >${i.name}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									</select>		
									<form:errors path="subDepartment" cssClass="validationError"/>
								</p>
							</td>				
							<td style="vertical-align: top;">
								<p>
									<label class="small"><spring:message code="bill.billType" text="Bill Type"/></label>
									<select id="billType" class="sSelect" name="billType">
									<c:forEach var="i" items="${billTypes}">
										<c:choose>
											<c:when test="${i.id == selectedBillType}">
												<option value="${i.id}" selected="selected">${i.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${i.id}">${i.name}</option>
											</c:otherwise>
										</c:choose>							
									</c:forEach>
									</select>						
								</p>
								<p>
									<label class="small"><spring:message code="bill.billKind" text="Bill Kind"/></label>
									<select id="billKind" class="sSelect" name="billKind">
									<c:forEach var="i" items="${billKinds}">
										<c:choose>
											<c:when test="${i.id == selectedBillKind}">
												<option value="${i.id}" selected="selected">${i.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${i.id}">${i.name}</option>
											</c:otherwise>
										</c:choose>							
									</c:forEach>
									</select>						
								</p>
								<c:if test="${selectedDeviceTypeForBill == 'bills_government'}">
								<p>
									<label class="small"><spring:message code="bill.introducingHouseType" text="Introducing House Type"/></label>
									<form:select id="introducingHouseType" class="sSelect" path="introducingHouseType">
									<c:forEach var="i" items="${introducingHouseTypes}">							
										<c:choose>
											<c:when test="${i.id == selectedIntroducingHouseType}">
												<option value="${i.id}" selected="selected">${i.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${i.id}">${i.name}</option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
									</form:select>		
									<form:errors path="introducingHouseType"></form:errors>				
								</p>
								</c:if>								
							</td>				
						</tr>
					</table>		
					
					<p>
						<label class="centerlabel"><spring:message code="bill.members" text="Members"/></label>
						<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
						<c:if test="${!(empty primaryMember)}">
							<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
						</c:if>
						<c:if test="${!(empty supportingMembers)}">
							<select  name="selectedSupportingMembers" id="selectedSupportingMembers" multiple="multiple" style="display:none;">
							<c:forEach items="${supportingMembers}" var="i">
							<option value="${i.id}" selected="selected"></option>
							</c:forEach>		
							</select>
						</c:if>	
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText">
						<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
					</p>	
					
					<div id="referredActDiv">
						<p>
							<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredAct)}">
									<a href="#" id="viewReferredAct" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
									<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredAct" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="referredActYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredAct" name="referredAct" value="${referredAct}">
						</p>
					</div>
					
					<div id="referredOrdinanceDiv">
						<p>
							<a href="#" id="referOrdinance" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referOrdinance" text="Refer Ordinance"></spring:message></a>
						</p>		
						<p>
							<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredOrdinance)}">
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px;" class="clubbedRefBills"><c:out value="${referredOrdinanceNumber}"></c:out></a>
									<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px; text-decoration: none;" class="clubbedRefBills"><c:out value="-"></c:out></a>
									<label id="referredOrdinanceYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
						</p>
					</div>	
					
					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">
					<p>
						<label class="small"><spring:message code="bill.parentbill" text="Clubbed To"></spring:message></label>
						<a href="#" id="p${parent}" onclick="viewBillDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
						<input type="hidden" id="parent" name="parent" value="${parent}">
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.clubbedbills" text="Clubbed Bills"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty clubbedBillsToShow) }">
								<c:forEach items="${clubbedBillsToShow }" var="i">
									<a href="#" id="cq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
							<c:forEach items="${clubbedBills }" var="i">
								<option value="${i.id}" selected="selected"></option>
							</c:forEach>
						</select>
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.referencedbill" text="Referenced Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty referencedBills) }">
								<c:forEach items="${referencedBills }" var="i" varStatus="index">
									<a href="#" id="rq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
									&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})	
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="referencedBill" name="referencedBill" value="${referencedBill}" />						
					</p>
					</c:if>		
					
					<p>
						<label class="small"><spring:message code="bill.lapsedbill" text="Lapsed Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty lapsedBills) }">
								<c:forEach items="${lapsedBills }" var="i" varStatus="index">
									<c:choose>
										<c:when test="${not empty i.name}">
											<a href="#" id="lq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})	
										</c:when>
										<c:otherwise>											
											<a href="#" id="lq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="lapsedBill" name="lapsedBill" value="${lapsedBill}" />						
					</p>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.titles" text="Titles of Bill" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_titles" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForTitles" text="View Revisions for Titles"></spring:message></a>
							<div id="titles_div">
								<c:forEach var="i" items="${titles}">
									<p>
										<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
										<textarea rows="2" cols="50" id="title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseTitle" id="reviseTitle_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseTitle" text="Revise This Title"></spring:message></a>
									</p>
									<p id="revisedTitlePara_${i.language.type}" style="display:none;">
										<label class="centerlabel">${i.language.name} <spring:message code="bill.revisedTitle" text=" Revised Title"/></label>						
										<c:set var="revisedTitleText" value=""></c:set>
										<c:set var="revisedTitleId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedTitleText" value="${revisedTitle_marathi}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedTitleText" value="${revisedTitle_hindi}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedTitleText" value="${revisedTitle_english}"></c:set>
												<c:set var="revisedTitleId" value="${revisedTitle_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea rows="2" cols="50" class="revisedTitle" id="revised_title_text_${i.language.type}" name="revised_title_text_${i.language.type}">${revisedTitleText}</textarea>
										<input type="hidden" name="revised_title_id_${i.language.type}" value="${revisedTitleId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>	
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.contentDrafts" text="Drafts of Bill" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_contentDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForContentDrafts" text="View Revisions for Content Drafts"></spring:message></a>
							<div id="contentDrafts_div">
								<c:forEach var="i" items="${contentDrafts}" varStatus="draftNumber">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
										<textarea class="wysiwyg" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseContentDraft" id="reviseContentDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseDraft" text="Revise This Draft"></spring:message></a>
									</p>
									<p id="revisedContentDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedContentDraft" text=" Revised Draft"/></label>						
										<c:set var="revisedContentDraftText" value=""></c:set>
										<c:set var="revisedContentDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_marathi}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_hindi}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedContentDraftText" value="${revisedContentDraft_english}"></c:set>
												<c:set var="revisedContentDraftId" value="${revisedContentDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg revisedContentDraft" id="revised_contentDraft_text_${i.language.type}" name="revised_contentDraft_text_${i.language.type}">${revisedContentDraftText}</textarea>
										<input type="hidden" name="revised_contentDraft_id_${i.language.type}" value="${revisedContentDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div id="annexuresForAmendingBill_div">
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.annexuresForAmendingBill" text="Annexures For Amending Bill" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_annexuresForAmendingBill_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForAnnexuresForAmendingBill" text="View Revisions for Annexures For Amending Bill"></spring:message></a>
							<div>
								<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="draftNumber">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.annexureForAmendingBill" text="Annexure For Amending Bill"/></label>
										<textarea class="wysiwyg" id="annexureForAmendingBill_text_${i.language.type}" name="annexureForAmendingBill_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="annexureForAmendingBill_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="annexureForAmendingBill_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseAnnexureForAmendingBill" id="reviseAnnexureForAmendingBill_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseDraft" text="Revise This Annexure"></spring:message></a>
									</p>
									<p id="revisedAnnexureForAmendingBillPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedAnnexureForAmendingBill" text=" Revised Annexure For Amending Bill"/></label>						
										<c:set var="revisedAnnexureForAmendingBillText" value=""></c:set>
										<c:set var="revisedAnnexureForAmendingBillId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedAnnexureForAmendingBillText" value="${revisedAnnexureForAmendingBill_marathi}"></c:set>
												<c:set var="revisedAnnexureForAmendingBillId" value="${revisedAnnexureForAmendingBill_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedAnnexureForAmendingBillText" value="${revisedAnnexureForAmendingBill_hindi}"></c:set>
												<c:set var="revisedAnnexureForAmendingBillId" value="${revisedAnnexureForAmendingBill_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedAnnexureForAmendingBillText" value="${revisedAnnexureForAmendingBill_english}"></c:set>
												<c:set var="revisedAnnexureForAmendingBillId" value="${revisedAnnexureForAmendingBill_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg revisedAnnexureForAmendingBill" id="revised_annexureForAmendingBill_text_${i.language.type}" name="revised_annexureForAmendingBill_text_${i.language.type}">${revisedAnnexureForAmendingBillText}</textarea>
										<input type="hidden" name="revised_annexureForAmendingBill_id_${i.language.type}" value="${revisedAnnexureForAmendingBillId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
	
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statementOfObjectAndReasonDrafts" text="Statement of Object & Reason" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_statementOfObjectAndReasonDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForStatementOfObjectAndReasonDrafts" text="View Revisions for Statement Of Object And Reason"></spring:message></a>
							<div id="statementOfObjectAndReasonDrafts_div">
								<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
										<textarea class="wysiwyg" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatementOfObjectAndReasonDraft" id="reviseStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseDraft" text="Revise This Draft"></spring:message></a>
									</p>
									<p id="revisedStatementOfObjectAndReasonDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedStatementOfObjectAndReasonDraft" text=" Revised Statement of Object & Reason"/></label>						
										<c:set var="revisedStatementOfObjectAndReasonDraftText" value=""></c:set>
										<c:set var="revisedStatementOfObjectAndReasonDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_marathi}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_hindi}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedStatementOfObjectAndReasonDraftText" value="${revisedStatementOfObjectAndReasonDraft_english}"></c:set>
												<c:set var="revisedStatementOfObjectAndReasonDraftId" value="${revisedStatementOfObjectAndReasonDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg revisedStatementOfObjectAndReasonDraft" id="revised_statementOfObjectAndReasonDraft_text_${i.language.type}" name="revised_statementOfObjectAndReasonDraft_text_${i.language.type}">${revisedStatementOfObjectAndReasonDraftText}</textarea>
										<input type="hidden" name="revised_statementOfObjectAndReasonDraft_id_${i.language.type}" value="${revisedStatementOfObjectAndReasonDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.financialMemorandumDrafts" text="Financial Memorandum" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_financialMemorandumDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForFinancialMemorandumDrafts" text="View Revisions for Financial Memorandum"></spring:message></a>
							<div id="financialMemorandumDrafts_div">
								<c:forEach var="i" items="${financialMemorandumDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
										<textarea class="wysiwyg" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseFinancialMemorandumDraft" id="reviseFinancialMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseDraft" text="Revise This Draft"></spring:message></a>
									</p>
									<p id="revisedFinancialMemorandumDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedFinancialMemorandumDraft" text=" Revised Financial Memorandum"/></label>						
										<c:set var="revisedFinancialMemorandumDraftText" value=""></c:set>
										<c:set var="revisedFinancialMemorandumDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_marathi}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_hindi}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedFinancialMemorandumDraftText" value="${revisedFinancialMemorandumDraft_english}"></c:set>
												<c:set var="revisedFinancialMemorandumDraftId" value="${revisedFinancialMemorandumDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg revisedFinancialMemorandumDraft" id="revised_financialMemorandumDraft_text_${i.language.type}" name="revised_financialMemorandumDraft_text_${i.language.type}">${revisedFinancialMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_financialMemorandumDraft_id_${i.language.type}" value="${revisedFinancialMemorandumDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div>
						<fieldset>
							<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statutoryMemorandumDrafts" text="Statutory Memorandum" /></label></legend>
							<a href="#" class="viewRevisions" id="viewRevisions_statutoryMemorandumDrafts_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForStatutoryMemorandumDrafts" text="View Revisions for Statutory Memorandum"></spring:message></a>
							<div id="statutoryMemorandumDrafts_div">
								<c:forEach var="i" items="${statutoryMemorandumDrafts}">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
										<textarea class="wysiwyg" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatutoryMemorandumDraft" id="reviseStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.reviseDraft" text="Revise This Draft"></spring:message></a>
									</p>
									<p id="revisedStatutoryMemorandumDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedStatutoryMemorandumDraft" text=" Revised Statutory Memorandum"/></label>						
										<c:set var="revisedStatutoryMemorandumDraftText" value=""></c:set>
										<c:set var="revisedStatutoryMemorandumDraftId" value=""></c:set>
										<c:choose>
											<c:when test="${i.language.type=='marathi'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_marathi}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_marathi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='hindi'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_hindi}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_hindi}"></c:set>
											</c:when>
											<c:when test="${i.language.type=='english'}">
												<c:set var="revisedStatutoryMemorandumDraftText" value="${revisedStatutoryMemorandumDraft_english}"></c:set>
												<c:set var="revisedStatutoryMemorandumDraftId" value="${revisedStatutoryMemorandumDraft_id_english}"></c:set>
											</c:when>							
										</c:choose>
										<textarea class="wysiwyg revisedStatutoryMemorandumDraft" id="revised_statutoryMemorandumDraft_text_${i.language.type}" name="revised_statutoryMemorandumDraft_text_${i.language.type}">${revisedStatutoryMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_statutoryMemorandumDraft_id_${i.language.type}" value="${revisedStatutoryMemorandumDraftId}">												
									</p>
								</c:forEach>
							</div>
						</fieldset>
					</div>
				
					<p id="internalStatusDiv">
						<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
						<input id="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>					
					<c:if test="${not empty formattedTranslationStatus}">
					<p id="translationStatusDiv">
						<label class="small"><spring:message code="bill.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					
					<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					<input type="hidden" id="translationStatus"  name="translationStatus" value="${translationStatus}">
					<input type="hidden" id="opinionFromLawAndJDStatus"  name="opinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}">
					<input type="hidden" id="recommendationFromGovernorStatus"  name="recommendationFromGovernorStatus" value="${recommendationFromGovernorStatus}">
					<input type="hidden" id="recommendationFromPresidentStatus"  name="recommendationFromPresidentStatus" value="${recommendationFromPresidentStatus}">
											
					<p>
						<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
					</p>
					
					<c:choose>
					<c:when test="${workflowtype=='TRANSLATION_WORKFLOW'}">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarksForTranslation" text="Translation Remarks"/></label>
						<form:textarea path="remarksForTranslation" cssClass="wysiwyg" readonly="true"></form:textarea>
					</p>
					</c:when>	
					<c:otherwise>
						<form:hidden path="remarksForTranslation"/>
					</c:otherwise>
					</c:choose>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
						<textarea id="remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>
					</p>					
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<c:choose>
								<c:when test="${workflowstatus!='COMPLETED' and translationStatusType=='bill_final_translation'}">
									<input id="save" type="button" value="<spring:message code='generic.save' text='Save'/>" class="butDef">	
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Send Translation'/>" class="butDef">								
								</c:when>								
							</c:choose>
						</p>
					</div>
					<form:hidden path="id"/>
					<form:hidden path="locale"/>
					<form:hidden path="version"/>
					<form:hidden path="opinionSoughtFromLawAndJD"/>					
					<form:hidden path="workflowStarted"/>	
					<form:hidden path="endFlag"/>
					<form:hidden path="actor"/>
					<form:hidden path="level"/>
					<form:hidden path="localizedActorName"/>
					<form:hidden path="workflowDetailsId"/>
					<form:hidden path="translationWorkflowStarted"/>	
					<form:hidden path="endFlagForTranslation"/>
					<form:hidden path="actorForTranslation"/>
					<form:hidden path="levelForTranslation"/>
					<form:hidden path="localizedActorNameForTranslation"/>
					<form:hidden path="workflowDetailsIdForTranslation"/>
					<form:hidden path="opinionFromLawAndJDWorkflowStarted"/>	
					<form:hidden path="endFlagForOpinionFromLawAndJD"/>
					<form:hidden path="actorForOpinionFromLawAndJD"/>
					<form:hidden path="levelForOpinionFromLawAndJD"/>
					<form:hidden path="localizedActorNameForOpinionFromLawAndJD"/>
					<form:hidden path="workflowDetailsIdForOpinionFromLawAndJD"/>
					<form:hidden path="file"/>
					<form:hidden path="fileIndex"/>	
					<form:hidden path="fileSent"/>
					<input id="levelForWorkflow" name="levelForWorkflow" type="hidden">
					<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="status" value="${status }">
					<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
					<input type="hidden" name="setSubmissionDate" id="setSubmissionDate" value="${submissionDate}">
					<input type="hidden" name="setDateOfOpinionSoughtFromLawAndJD" id="setDateOfOpinionSoughtFromLawAndJD" value="${dateOfOpinionSoughtFromLawAndJD}">
					<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" name="workflowForTranslationStartedOnDate" id="workflowForTranslationStartedOn" value="${workflowForTranslationStartedOnDate }">
					<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input type="hidden" name="taskReceivedOnDateForTranslation" id="taskReceivedOnDateForTranslation" value="${taskReceivedOnDateForTranslation }">
					<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">						
				</form:form>
				<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
				<input id="usergroupType" name="usergroupType" type="hidden" value="${usergroupType}">
				
				<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
				<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
				<input id="sendTranslationMessage" name="sendTranslationMessage" value="<spring:message code='bill.sendTranslationMessage' text='Do You Want To Send Translation?'></spring:message>" type="hidden">
				<input id="ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
				<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
				<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
				<input id="oldTranslationStatus" value="${translationStatus}" type="hidden">
				<input id="oldOpinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}" type="hidden">
				<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
				<input id="questionType" type="hidden" value="${selectedQuestionType}" />
				<input id="typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />				
				<input id="typeOfSelectedBillType" type="hidden" value="${typeOfSelectedBillType}" />
				<input type="hidden" id="referredActYearLabel" value="<spring:message code="bill.referredActYear" text="Year"/>">
			</div>		
		</div>		
	</body>
</html>