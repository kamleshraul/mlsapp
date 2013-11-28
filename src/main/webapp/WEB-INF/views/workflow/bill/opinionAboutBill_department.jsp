<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="bill" text="Bill Information System"/>
		</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
		/**** detail of referenced bill ****/		
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
				$.fancybox.open(data,{autoSize:false,width:1000,height:750});
			},'html');	
		}
		function viewActDetail(id) {
			if(this.text!='-') {					
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var resourceURL='act/'+id+'/edit?edit=false';
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:800,height:700});
				},'html');
			}				
		};
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
		
		$(document).ready(function(){
			if(($('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
					&& $('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
					&& $('#financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
				||
				($('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
				&& $('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
				&& $('#revised_financialMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
			) {
				$('#financialMemorandumDrafts_button').hide();
				$('#financialMemorandumDrafts_div').show();
			} else {
				$('#financialMemorandumDrafts_button').show();
			}
			
			if(($('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
					&& $('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
					&& $('#statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
				||
				($('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!=undefined
				&& $('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="" 
				&& $('#revised_statutoryMemorandumDraft_text_'+$('#defaultBillLanguage').val()).val()!="<p></p>")
			) {
				$('#statutoryMemorandumDrafts_button').hide();
				$('#statutoryMemorandumDrafts_div').show();
			} else {
				$('#statutoryMemorandumDrafts_button').show();
			}
			
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
			/**** read only form for department in opinion seeking workflow ****/
			$("#currentForm :input").not("#opinionSoughtFromLawAndJD,#remarks,#save,#submit").attr("disabled", true);
			$("#currentForm :input").not("#opinionSoughtFromLawAndJD,#remarks,#save,#submit").css("color","black");
			$("#currentForm .wysiwyg").not("#opinionSoughtFromLawAndJD,#remarks").attr("readonly", true);
			$("#currentForm a").not(".viewRevisions, .iconLink, .referenceLink").css("display", "none");	
			/** as a result of above display:none, if some elements get dispersed, preserve their location **/
			$("#viewRevisions_title").css("margin-left","162px");
			$("#viewRevisions_title").css("margin-right","20px");	
			
			/**** Ministry Changes ****/
			$("#ministry").change(function(){
				if($(this).val()!=''){
					loadSubDepartments(ministry, department);
				}else{
					$("#subDepartment").empty();				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				}
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
		    /**** save opinion (send later) ****/ 
		    $('#save').click(function() {
		    	var operation = "";
		    	/**** read only form for department in special cases ****/
				$("#currentForm :input").not("#opinionSoughtFromLawAndJD,#remarks,#save,#submit").removeAttr("disabled");
				operation = "saveOpinionFromLawAndJD";	    	
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				clearUnrevisedDrafts();
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
				$.post($('form').attr('action')+'?operation='+operation,
						$("form").serialize(), function(data){
					$('.tabContent').html(data);
						$('html').animate({scrollTop:0}, 'slow');
						$('body').animate({scrollTop:0}, 'slow');	
					$.unblockUI();
				});
			});
			/**** submit opinion ****/ 
		    $('#submit').click(function() {			    	
				/**** read only form for department in opinion seeking workflow ****/
				$("#currentForm :input").not("#opinionSoughtFromLawAndJD,#remarks,#save,#submit").removeAttr("disabled");	    	
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				//validate opinion is given or not
		    	if($('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
		    		if($('#opinionSoughtFromLawAndJD').val()=='' || $('#opinionSoughtFromLawAndJD').val()==undefined) {
		    			$.prompt("Please enter the opinion for the bill.");
		    			return false;
		    		}
		    	}
				clearUnrevisedDrafts();
				$.prompt($('#sendOpinionFromLawAndJDMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$("#endFlagForOpinionFromLawAndJD").val("end");
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });						
			        	$.post($('form').attr('action')+'?operation=sendOpinionFromLawAndJD',  
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
			
			/**** show title for only default bill language ****/
			$('.title').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#title_para_'+currentLanguage).show();					
				} else {
					$('#title_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle title for given language icon ****/
			$('.toggleTitle').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#title_para_'+currentLanguage).css('display')=='none') {
					$('#title_para_'+currentLanguage).show();					
				} else {
					$('#title_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show content draft for only default bill language ****/
			$('.contentDraft').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#contentDraft_para_'+currentLanguage).show();					
				} else {
					$('#contentDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle contentDraft for given language icon ****/
			$('.toggleContentDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#contentDraft_para_'+currentLanguage).css('display')=='none') {
					$('#contentDraft_para_'+currentLanguage).show();					
				} else {
					$('#contentDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show annexure for only default bill language ****/
			$('.annexureForAmendingBill').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#annexureForAmendingBill_para_'+currentLanguage).show();					
				} else {
					$('#annexureForAmendingBill_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle annexure for given language icon ****/
			$('.toggleAnnexureForAmendingBill').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#annexureForAmendingBill_para_'+currentLanguage).css('display')=='none') {
					$('#annexureForAmendingBill_para_'+currentLanguage).show();					
				} else {
					$('#annexureForAmendingBill_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show statement of object and reason draft for only default bill language ****/
			$('.statementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
				} else {
					$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle statement of object and reason draft for given language icon ****/
			$('.toggleStatementOfObjectAndReasonDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#statementOfObjectAndReasonDraft_para_'+currentLanguage).css('display')=='none') {
					$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
				} else {
					$('#statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show financial memorandum draft for only default bill language ****/
			$('.financialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#financialMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#financialMemorandumDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle financial memorandum draft on user demand ****/
			$('#financialMemorandumDrafts_button').click(function() {
				if($('#financialMemorandumDrafts_div').css('display')=='none') {
					$('#financialMemorandumDrafts_div').show();
				} else {
					$('#financialMemorandumDrafts_div').hide();
				}
				return false;
			});
			/**** toggle financial memorandum draft for given language icon ****/
			$('.toggleFinancialMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#financialMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
					$('#financialMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#financialMemorandumDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			
			/**** show statutory memorandum draft for only default bill language ****/
			$('.statutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#statutoryMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#statutoryMemorandumDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle statutory memorandum draft on user demand ****/
			$('#statutoryMemorandumDrafts_button').click(function() {
				if($('#statutoryMemorandumDrafts_div').css('display')=='none') {
					$('#statutoryMemorandumDrafts_div').show();
				} else {
					$('#statutoryMemorandumDrafts_div').hide();
				}
				return false;
			});
			/**** toggle statutory memorandum draft for given language icon ****/
			$('.toggleStatutoryMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#statutoryMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
					$('#statutoryMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#statutoryMemorandumDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			//----------------------revise drafts script----------------------//			
			$('.revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {					
					$('#revisedTitlePara_'+currentLanguage).show();					
				} else {
					$('#revisedTitlePara_'+currentLanguage).hide();						
				}
			});		
			
			$('.revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedContentDraftPara_'+currentLanguage).show();					
				} else {
					$('#revisedContentDraftPara_'+currentLanguage).hide();										
				}
			});
			
			if($('#typeOfSelectedBillType').val()=="amending") {
				$('.revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];	
					if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).show();						
					} else {
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();						
					}
				});
			}
			
			$('.revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];					
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();					
				} else {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();					
				}
			});
			
			$('.revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).show();					
				} else {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();					
				}
			});
			
			$('.revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();					
				} else {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();					
				}
			});
			//------------------------------------------------------------------
		});
		
		/* function dereferencingInt(referId){
			var whichDevice= $('#whichDevice').val();
			var device=$("#questionType").val();
			var deviceId=$("#id").val();
			
			$.post('refentity/dereferencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
				if(data=='SUCCESS'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}				
			},'html');
			return false;
		} */	
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
		</style>
	</head> 

	<body>
		<div class="fields clearfix watermark">		
			<div id="assistantDiv">
				<form:form id="currentForm" action="workflow/bill" method="PUT" modelAttribute="domain">
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
					
					<c:if test="${not empty domain.number or not empty domain.submissionDate}">
					<p>
						<c:if test="${not empty domain.number}">
						<label class="small"><spring:message code="bill.number" text="bill Number"/>*</label>
						<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="number" name="number" value="${domain.number}" type="hidden">
						<form:errors path="number" cssClass="validationError"/>
						</c:if>
						<c:if test="${not empty domain.submissionDate}">				
						<label class="small"><spring:message code="bill.submissionDate" text="Submitted On"/></label>
						<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
						<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
						</c:if>
					</p>
					</c:if>
					
					<p>
						<label class="centerlabel"><spring:message code="bill.members" text="Members"/></label>
						<textarea id="members" class="sTextarea" readonly="readonly" rows="1" cols="20">${memberNames}</textarea>
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
						<label class="centerlabel"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText centerlabel">
						<a href="#" id="viewContacts" style="vertical-align:top; margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.ministry" text="Ministry"/>*</label>
						<select name="ministry" id="ministry" class="sSelect">
							<c:forEach items="${ministries }" var="i">
								<c:choose>
									<c:when test="${i.id==ministrySelected }">									
										<option value="${i.id }" selected="selected">${i.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${i.id }" >${i.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>		
						<form:errors path="ministry" cssClass="validationError"/>
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
					<!-- <h2></h2> -->
					
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
						<form:errors path="billType" cssClass="validationError"></form:errors>		
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
						<form:errors path="billKind" cssClass="validationError"></form:errors>	
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
					
					<h2></h2>

					<div id="referredActDiv">
						<p>
							<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredAct)}">
									<a href="#" id="viewReferredAct" class="referenceLink" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
									<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredAct"  class="referenceLink" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="referredActYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredAct" name="referredAct" value="${referredAct}">
						</p>
					</div>
					<div id="referredOrdinanceDiv">
						<p>
							<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredOrdinance)}">
									<a href="#" id="viewReferredOrdinance" class="referenceLink" style="font-size: 18px;"><c:out value="${referredOrdinanceNumber}"></c:out></a>
									<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredOrdinance" class="referenceLink" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="referredOrdinanceYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
						</p>
					</div>

					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">		
					<c:choose>	
					<c:when test="${empty parent}"><c:set var="displayParentBill" value="none"/></c:when>
					<c:otherwise><c:set var="displayParentBill" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayParentBill};">
						<label class="small"><spring:message code="bill.parentbill" text="Clubbed To"></spring:message></label>
						<a href="#" id="p${parent}" class="referenceLink" onclick="viewBillDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
						<input type="hidden" id="parent" name="parent" value="${parent}">
					</p>
					
					<c:choose>	
					<c:when test="${empty clubbedBillsToShow}"><c:set var="displayClubbedBillsToShow" value="none"/></c:when>
					<c:otherwise><c:set var="displayClubbedBillsToShow" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayClubbedBillsToShow};">
						<label class="small"><spring:message code="bill.clubbedbills" text="Clubbed Bills"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty clubbedBillsToShow) }">
								<c:forEach items="${clubbedBillsToShow}" var="i">
									<a href="#" id="cq${i.number}" class="clubbedRefBills referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
					
					<c:choose>	
					<c:when test="${empty referencedBills}"><c:set var="displayReferencedBills" value="none"/></c:when>
					<c:otherwise><c:set var="displayReferencedBills" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayReferencedBills};">
						<label class="small"><spring:message code="bill.referencedbill" text="Referenced Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty referencedBills) }">
								<c:forEach items="${referencedBills }" var="i" varStatus="index">
									<c:choose>
										<c:when test="${not empty i.name}">
											<c:choose>
												<c:when test="${isActReferenced=='true'}">
													<a href="#" id="rq${i.number}" class="clubbedRefBills referenceLink" onclick="viewActDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:when>
												<c:otherwise>
													<a href="#" id="rq${i.number}" class="clubbedRefBills referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:otherwise>
											</c:choose>												
										</c:when>
										<c:otherwise>											
											<a href="#" id="rq${i.number}" class="clubbedRefBills referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="referencedBill" name="referencedBill" value="${referencedBill}" />						
					</p>
					</c:if>	
					
					<c:choose>	
					<c:when test="${empty lapsedBills}"><c:set var="displayLapsedBills" value="none"/></c:when>
					<c:otherwise><c:set var="displayLapsedBills" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayLapsedBills};">
						<label class="small"><spring:message code="bill.lapsedbill" text="Lapsed Bill"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty lapsedBills) }">
								<c:forEach items="${lapsedBills }" var="i" varStatus="index">
									<c:choose>
										<c:when test="${not empty i.name}">
											<a href="#" id="lq${i.number}" class="clubbedRefBills referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})	
										</c:when>
										<c:otherwise>											
											<a href="#" id="lq${i.number}" class="clubbedRefBills referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
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
					
					<!-- <h2></h2> -->
					
					<div style="margin-top: 20px;">
						<fieldset>
							<a href="#" class="viewRevisions" id="viewRevisions_titles"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForTitles' text='View Revisions for Titles'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${titles}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleTitle iconLink" id="toggleTitle_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>																			
							<div id="titles_div">
								<c:forEach var="i" items="${titles}">
									<div id="title_para_${i.language.type}" style="display:none;">
									<p>
										<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
										<textarea rows="2" cols="50" class="title" id="title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseTitle" id="reviseTitle_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="reviseTitle_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseTitle' text='Revise This Title'></spring:message>" class="imageLink" />
										</a>
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
									</div>								
								</c:forEach>
							</div>
						</fieldset>
					</div>					
					
					<div style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="viewRevisions" id="viewRevisions_contentDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForContentDrafts' text='View Revisions for Content Drafts'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${contentDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleContentDraft iconLink" id="toggleContentDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>							
							</p>						
							<div id="contentDrafts_div">
								<c:forEach var="i" items="${contentDrafts}" varStatus="draftNumber">
									<div id="contentDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
										<textarea class="wysiwyg contentDraft" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseContentDraft" id="reviseContentDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
											<img id="reviseContentDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseContentDraft' text='Revise This Content Draft'></spring:message>" class="imageLink" />
										</a>
										<c:if test="${i.language.type==defaultBillLanguage}">											
											<a href="#" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
												<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
											</a>											
										</c:if>
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
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div id="annexuresForAmendingBill_div" style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="viewRevisions" id="viewRevisions_annexuresForAmendingBill"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForAnnexureForAmendingBills' text='View Revisions for Annexures For Amending Bill'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleAnnexureForAmendingBill iconLink" id="toggleAnnexureForAmendingBill_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>							
							</p>						
							<div>
								<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="draftNumber">
									<div id="annexureForAmendingBill_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.annexureForAmendingBill" text="Annexure For Amending Bill"/></label>
										<textarea class="wysiwyg annexureForAmendingBill" id="annexureForAmendingBill_text_${i.language.type}" name="annexureForAmendingBill_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="annexureForAmendingBill_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="annexureForAmendingBill_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseAnnexureForAmendingBill" id="reviseAnnexureForAmendingBill_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
											<img id="reviseAnnexureForAmendingBill_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseAnnexureForAmendingBill' text='Revise This Annexure For Amending Bill'></spring:message>" class="imageLink" />
										</a>
										<c:if test="${i.language.type==defaultBillLanguage}">											
											<a href="#" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
												<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
											</a>											
										</c:if>
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
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>					
	
					<div style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -20px;">
							<a href="#" class="viewRevisions" id="viewRevisions_statementOfObjectAndReasonDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForStatementOfObjectAndReasonDrafts' text='View Revisions for Statement Of Object And Reason'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleStatementOfObjectAndReasonDraft iconLink" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div id="statementOfObjectAndReasonDrafts_div">
								<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
									<div id="statementOfObjectAndReasonDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
										<textarea class="wysiwyg statementOfObjectAndReasonDraft" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatementOfObjectAndReasonDraft" id="reviseStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="reviseStatementOfObjectAndReasonDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseSOR' text='Revise This Statement of Object and Reason'></spring:message>" class="imageLink" />
										</a>
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
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<p style="margin-left: 162px;margin-top: 20px;">
					<a href="#" id="financialMemorandumDrafts_button" class="iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Fmemo.jpg" title="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>	
					<a href="#" id="statutoryMemorandumDrafts_button" class="iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Smemo.jpg" title="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>
					</p>
					
					<div id="financialMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="viewRevisions" id="viewRevisions_financialMemorandumDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForFinancialMemorandumDrafts' text='View Revisions for Financial Memorandum'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${financialMemorandumDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleFinancialMemorandumDraft iconLink" id="toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div>
								<c:forEach var="i" items="${financialMemorandumDrafts}">
									<div id="financialMemorandumDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
										<textarea class="wysiwyg financialMemorandumDraft" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseFinancialMemorandumDraft" id="reviseFinancialMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="reviseFinancialMemorandumDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseFM' text='Revise This Financial Memorandum'></spring:message>" class="imageLink" />
										</a>
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
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>					
					
					<div id="statutoryMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -20px;">
							<a href="#" class="viewRevisions" id="viewRevisions_statutoryMemorandumDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForStatutoryMemorandumDrafts' text='View Revisions for Statutory Memorandum'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${statutoryMemorandumDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleStatutoryMemorandumDraft iconLink" id="toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div>
								<c:forEach var="i" items="${statutoryMemorandumDrafts}">
									<div id="statutoryMemorandumDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
										<textarea class="wysiwyg statutoryMemorandumDraft" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatutoryMemorandumDraft" id="reviseStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="reviseStatutoryMemorandumDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseSM' text='Revise This Statutory Memorandum'></spring:message>" class="imageLink" />
										</a>
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
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>
				
					<div>
					<p id="opinionFromLawAndJDStatusStatusDiv">
						<label class="small"><spring:message code="bill.currentOpinionFromLawAndJDStatus" text="Current Opinion From Law And JD Status"/></label>
						<input id="formattedOpinionFromLawAndJDStatus" value="${formattedOpinionFromLawAndJDStatus}" type="text" readonly="readonly">
					</p>					
					<p id="internalStatusDiv">
						<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
						<input id="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>							
					<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					<input type="hidden" id="translationStatus"  name="translationStatus" value="${translationStatus}">
					<input type="hidden" id="opinionFromLawAndJDStatus"  name="opinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}">
					<input type="hidden" id="recommendationFromGovernorStatus"  name="recommendationFromGovernorStatus" value="${recommendationFromGovernorStatus}">
					<input type="hidden" id="recommendationFromPresidentStatus"  name="recommendationFromPresidentStatus" value="${recommendationFromPresidentStatus}">
					</div>
											
					<c:set var="isOpinionFromLawAndJDReadonly" value="true"/>
					<c:if test="${workflowtype=='OPINION_FROM_LAWANDJD_WORKFLOW' and workflowstatus!='COMPLETED'}">
						<c:set var="isOpinionFromLawAndJDReadonly" value="false"/>
					</c:if>
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
						<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="${isOpinionFromLawAndJDReadonly}"></form:textarea>
						<form:errors path="opinionSoughtFromLawAndJD" />
					</p>
					<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
					<p>
					<label class="small"><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
					<input id="formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" readonly="readonly">
					<input id="setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
					</p>
					</c:if>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
						<textarea id="remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>
					</p>					
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<c:if test="${workflowstatus!='COMPLETED'}">
								<input id="save" type="button" value="<spring:message code='bill.saveOpinion' text='Save Opinion'/>" class="butDef">	
								<input id="submit" type="button" value="<spring:message code='bill.submitOpinion' text='Send Opinion'/>" class="butDef">								
							</c:if>
						</p>
					</div>
					
					<form:hidden path="id"/>
					<form:hidden path="locale"/>
					<form:hidden path="version"/>
					<form:hidden path="remarksForTranslation"/>
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
					<form:hidden path="recommendationFromGovernorWorkflowStarted"/>	
					<form:hidden path="endFlagForRecommendationFromGovernor"/>
					<form:hidden path="actorForRecommendationFromGovernor"/>
					<form:hidden path="levelForRecommendationFromGovernor"/>
					<form:hidden path="localizedActorNameForRecommendationFromGovernor"/>
					<form:hidden path="workflowDetailsIdForRecommendationFromGovernor"/>
					<form:hidden path="recommendationFromPresidentWorkflowStarted"/>	
					<form:hidden path="endFlagForRecommendationFromPresident"/>
					<form:hidden path="actorForRecommendationFromPresident"/>
					<form:hidden path="levelForRecommendationFromPresident"/>
					<form:hidden path="localizedActorNameForRecommendationFromPresident"/>
					<form:hidden path="workflowDetailsIdForRecommendationFromPresident"/>
					<form:hidden path="admissionDate"/>
					<form:hidden path="rejectionDate"/>
					<form:hidden path="isIncomplete"/>
					<form:hidden path="file"/>
					<form:hidden path="fileIndex"/>	
					<form:hidden path="fileSent"/>
					<form:hidden path="recommendationFromGovernor"/>
					<form:hidden path="recommendationFromPresident"/>
					
					<input id="levelForWorkflow" name="levelForWorkflow" type="hidden">
					<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="status" value="${status }">
					<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
					<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" name="workflowForTranslationStartedOnDate" id="workflowForTranslationStartedOn" value="${workflowForTranslationStartedOnDate }">
					<input type="hidden" name="workflowForOpinionFromLawAndJDStartedOnDate" id="workflowForOpinionFromLawAndJDStartedOn" value="${workflowForOpinionFromLawAndJDStartedOnDate }">
					<input type="hidden" name="workflowForRecommendationFromGovernorStartedOnDate" id="workflowForRecommendationFromGovernorStartedOn" value="${workflowForRecommendationFromGovernorStartedOnDate }">
					<input type="hidden" name="workflowForRecommendationFromPresidentStartedOnDate" id="workflowForRecommendationFromPresidentStartedOn" value="${workflowForRecommendationFromPresidentStartedOnDate }">
					<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input type="hidden" name="taskReceivedOnDateForTranslation" id="taskReceivedOnDateForTranslation" value="${taskReceivedOnDateForTranslation }">
					<input type="hidden" name="taskReceivedOnDateForOpinionFromLawAndJD" id="taskReceivedOnDateForOpinionFromLawAndJD" value="${taskReceivedOnDateForOpinionFromLawAndJD}">
					<input type="hidden" name="taskReceivedOnDateForRecommendationFromGovernor" id="taskReceivedOnDateForRecommendationFromGovernor" value="${taskReceivedOnDateForRecommendationFromGovernor}">
					<input type="hidden" name="taskReceivedOnDateForRecommendationFromPresident" id="taskReceivedOnDateForRecommendationFromPresident" value="${taskReceivedOnDateForRecommendationFromPresident}">
					<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
					<input id="workflowtype" name="workflowtype" value="${workflowtype}" type="hidden">
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input type="hidden" name="setDateOfRecommendationFromGovernor" value="${dateOfRecommendationFromGovernor}">
					<input type="hidden" name="setDateOfRecommendationFromPresident" value="${dateOfRecommendationFromPresident}">
					
					<!-- --------------------------PROCESS VARIABLES -------------------------------- -->	
					<%-- <input id="mailflag" name="mailflag" value="${pv_mailflag}" type="hidden">
					<input id="timerflag" name="timerflag" value="${pv_timerflag}" type="hidden">
					<input id="reminderflag" name="reminderflag" value="${pv_reminderflag}" type="hidden">	
					
					<!-- mail related variables -->
					<input id="mailto" name="mailto" value="${pv_mailto}" type="hidden">
					<input id="mailfrom" name="mailfrom" value="${pv_mailfrom}" type="hidden">
					<input id="mailsubject" name="mailsubject" value="${pv_mailsubject}" type="hidden">
					<input id="mailcontent" name="mailcontent" value="${pv_mailcontent}" type="hidden">
					
					<!-- timer related variables -->
					<input id="timerduration" name="timerduration" value="${pv_timerduration}" type="hidden">
					<input id="lasttimerduration" name="lasttimerduration" value="${pv_lasttimerduration}" type="hidden">	
					
					<!-- reminder related variables -->
					<input id="reminderto" name="reminderto" value="${pv_reminderto}" type="hidden">
					<input id="reminderfrom" name="reminderfrom" value="${pv_reminderfrom}" type="hidden">
					<input id="remindersubject" name="remindersubject" value="${pv_remindersubject}" type="hidden">
					<input id="remindercontent" name="remindercontent" value="${pv_remindercontent}" type="hidden"> --%>		
				</form:form>
				<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
				<input id="usergroupType" name="usergroupType" type="hidden" value="${usergroupType}">
				
				<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
				<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
				<input id="startWorkflowMessage" value="<spring:message code='bill.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
				<input id="sendOpinionFromLawAndJDMessage" value="<spring:message code='bill.sendOpinionFromLawAndJDMessage' text='Do You Want To Send Opinion Now?'></spring:message>" type="hidden">
				<input id="sendRecommendationFromGovernor" value="<spring:message code='bill.sendRecommendationFromGovernor' text='Do You Want To Send Recommendation From Governor Now?'></spring:message>" type="hidden">
				<input id="sendRecommendationFromPresident" value="<spring:message code='bill.sendRecommendationFromPresident' text='Do You Want To Send Recommendation From President Now?'></spring:message>" type="hidden">
				<input id="ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
				<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
				<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
				<input id="oldTranslationStatus" value="${translationStatus}" type="hidden">
				<input id="oldOpinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}" type="hidden">
				<input id="oldRecommendationFromGovernorStatus" name="oldRecommendationFromGovernorStatus" value="${recommendationFromGovernorStatus}" type="hidden">
				<input id="oldRecommendationFromPresidentStatus" name="oldRecommendationFromPresidentStatus" value="${recommendationFromPresidentStatus}" type="hidden">
				<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
				<input id="questionType" type="hidden" value="${selectedQuestionType}" />
				<input id="typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />
				<input id="typeOfSelectedBillType" type="hidden" value="${typeOfSelectedBillType}" />
				<input type="hidden" id="isActReferenced" value="${isActReferenced}">
				<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
			</div>		
		</div>
	</body>
</html>