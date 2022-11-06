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
		$(document).ready(function(){
			$('#readonly_opinionSoughtFromLawAndJD').wysiwyg({
				resizeOptions: {maxWidth: 600},
				controls:{	
					fullscreen: {
						visible: true,
						hotkey:{
							"ctrl":1|0,
							"key":122
						},
						exec: function () {
							if ($.wysiwyg.fullscreen) {
								$.wysiwyg.fullscreen.init(this);
							}
						},
						tooltip: "Fullscreen"
					},
					strikeThrough: { visible: true },
					underline: { visible: true },
					subscript: { visible: true },
					superscript: { visible: true },
					insertOrderedList  : { visible : true},
					increaseFontSize:{visible:true},
					decreaseFontSize:{visible:true},
					highlight: {visible:true}			
				},
				plugins: {
					autoload: true,
					i18n: { lang: "mr" }
				}
			});
			$('#readonly_opinionSoughtFromLawAndJD_para').children().filter('div.wysiwyg').addClass('fixed-width-wysiwyg');
						
			if($('#readonly_opinionSoughtFromLawAndJD').val()!=undefined
					&& $('#readonly_opinionSoughtFromLawAndJD').val()!="" 
					&& $('#readonly_opinionSoughtFromLawAndJD').val()!="<p></p>") {				
				if($('#readonly_isChecklistFilled').val()=="true") {					
					$('#readonly_checklist_button').hide();
					$('#readonly_checklist_div').show();
					$('#readonly_opinionSoughtFromLawAndJD_div').show();
					$('.readonly_checklist_button_hr').show();
				} else {
					$('#readonly_checklist_button').show();
				}
			} else {
				$('#readonly_checklist_button').hide();
			}			
			
			if(($('#readonly_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!=undefined
					&& $('#readonly_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="" 
					&& $('#readonly_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="<p></p>")
				||
				($('#readonly_revised_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!=undefined
				&& $('#readonly_revised_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="" 
				&& $('#readonly_revised_financialMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="<p></p>")
			) {
				$('#readonly_financialMemorandumDrafts_button').hide();
				$('#readonly_financialMemorandumDrafts_div').show();
			} else {
				$('#readonly_financialMemorandumDrafts_button').show();
			}
			
			if(($('#readonly_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!=undefined
					&& $('#readonly_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="" 
					&& $('#readonly_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="<p></p>")
				||
				($('#readonly_revised_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!=undefined
				&& $('#readonly_revised_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="" 
				&& $('#readonly_revised_statutoryMemorandumDraft_text_'+$('#readonly_defaultBillLanguage').val()).val()!="<p></p>")
			) {
				$('#readonly_statutoryMemorandumDrafts_button').hide();
				$('#readonly_statutoryMemorandumDrafts_div').show();
			} else {
				$('#readonly_statutoryMemorandumDrafts_button').show();
			}
			
			if($('#readonly_recommendationFromGovernor').val()!=undefined
					&& $('#readonly_recommendationFromGovernor').val()!="" 
					&& $('#readonly_recommendationFromGovernor').val()!="<p></p>") {
				$('#readonly_recommendationFromGovernor_button').show();
			} else {
				$('#readonly_recommendationFromGovernor_button').hide();
			}
			
			if($('#readonly_recommendationFromPresident').val()!=undefined
					&& $('#readonly_recommendationFromPresident').val()!="" 
					&& $('#readonly_recommendationFromPresident').val()!="<p></p>") {
				$('#readonly_recommendationFromPresident_button').show();
			} else {
				$('#readonly_recommendationFromPresident_button').hide();
			}
			
			/**** allow refer act & ordinance as per bill type ****/
			if($('#readonly_typeOfSelectedBillType').val()=='') {
				$('#readonly_referredActDiv').hide();
				$('#readonly_referredOrdinanceDiv').hide();
			} else if($('#readonly_typeOfSelectedBillType').val()=='original') {
				$('#readonly_referredActDiv').hide();
				$('#readonly_referredOrdinanceDiv').hide();
			} else if($('#readonly_typeOfSelectedBillType').val()=='replace_ordinance'){
				$('#readonly_referredOrdinanceDiv').show();
				$('#readonly_referredActDiv').hide();
			} else if($('#readonly_typeOfSelectedBillType').val()=='amending' && $('#readonly_typeOfSelectedDeviceType').val()=='bills_nonofficial'){
				$('#readonly_referredOrdinanceDiv').hide();
				$('#readonly_referredActDiv').show();
			} else{
				$('#readonly_referredActDiv').show();
				$('#readonly_referredOrdinanceDiv').show();
			}
			
			//to check/uncheck checkboxes for current checklist selection by assistant
			$('.readonly_checklist_checkbox_fields').each(function() {
				var fieldNumber = this.id.split("_")[4];
				//alert("value for field " + fieldNumber + ": " + $(this).val());
				if($(this).val()=='yes') {
					$('#readonly_checklist_checkbox_'+fieldNumber).attr('checked','checked');
					$('#readonly_checklist_checkbox_'+fieldNumber+'_div').show();
				} else {
					$('#readonly_checklist_checkbox_'+fieldNumber).removeAttr('checked');															
				}
			});			
			/**** Revisions ****/
		    $(".readonly_viewRevisions").click(function(){
		    	var thingToBeRevised = this.id.split("_")[2];		    	
			    $.get('bill/revisions/'+$("#id").val()+"?thingToBeRevised="+thingToBeRevised,function(data){
				    if(thingToBeRevised=="checklist") {
				    	$.fancybox.open(data, {autoSize: false, width: 900, height:600});
				    } else {
				    	$.fancybox.open(data);
				    }			    	
			    });
			    return false;
		    });
		    /**** Contact Details ****/
		    $("#readonly_viewContacts").click(function(){
			    var primaryMember=$("#readonly_primaryMember").val();
			    var supportingMembers=$("#readonly_selectedSupportingMembers").val();
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
		    /**** view detail of referred act (currently showing pdf of act) ****/		
			$('#readonly_viewReferredAct').click(function() {
				if(this.text!='-') {					
					var referredActId = $('#readonly_referredAct').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='act/'+referredActId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			/**** view detail of referred ordinance****/		
			$('#readonly_viewReferredOrdinance').click(function() {
				if(this.text!='-') {					
					var referredOrdinanceId = $('#readonly_referredOrdinance').val();
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
					var resourceURL='ordinance/'+referredOrdinanceId+'/edit?edit=false';
					$.get(resourceURL,function(data){
						$.unblockUI();
						$.fancybox.open(data,{autoSize:false,width:800,height:700});
					},'html');
				}				
			});
			/**** read only form for view purpose ****/
			$("#readonly_currentForm :input[type=text]").attr("readonly", true);
			$("#readonly_currentForm :input[type!=text]").attr("disabled", true);
			$("#readonly_currentForm :input").css("color","black");
			$("#readonly_currentForm .wysiwyg").attr("readonly", true);
			$("#readonly_currentForm a").not(".readonly_viewRevisions, .readonly_iconLink, .readonly_referenceLink").css("display", "none");
			$(".view").css("display", "inline-block");
			/** as a result of above display:none, if some elements get dispersed, preserve their location **/
			$("#readonly_viewRevisions_title").css("margin-left","162px");
			$("#readonly_viewRevisions_title").css("margin-right","20px");
		    /**** On page Load ****/		   
		    /* if($("#readonly_ministrySelected").val()==''){
				$("#readonly_ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#readonly_ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			} */
			if($("#readonly_subDepartmentSelected").val()==''){
				$("#readonly_subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#readonly_subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}	
			if($('#readonly_selectedBillType').val()=="" || $('#readonly_selectedBillType').val()==undefined){		
				$("#readonly_billType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill type for now, this option will be useful.
				$("#readonly_billType").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			}			
			if($('#readonly_selectedBillKind').val()=="" || $('#readonly_selectedBillKind').val()==undefined){		
				$("#readonly_billKind").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill kind for now, this option will be useful.
				$("#readonly_billKind").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			}
			/**** show title for only default bill language ****/
			$('.readonly_title').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_title_para_'+currentLanguage).show();					
				} else {
					$('#readonly_title_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle title for given language icon ****/
			$('.readonly_toggleTitle').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#readonly_title_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_title_para_'+currentLanguage).show();					
				} else {
					$('#readonly_title_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show content draft for only default bill language ****/
			$('.readonly_contentDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_contentDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_contentDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle contentDraft for given language icon ****/
			$('.readonly_toggleContentDraft').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#readonly_contentDraft_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_contentDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_contentDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show annexure for only default bill language ****/
			$('.readonly_annexureForAmendingBill').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_annexureForAmendingBill_para_'+currentLanguage).show();					
				} else {
					$('#readonly_annexureForAmendingBill_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle annexure for given language icon ****/
			$('.readonly_toggleAnnexureForAmendingBill').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#readonly_annexureForAmendingBill_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_annexureForAmendingBill_para_'+currentLanguage).show();					
				} else {
					$('#readonly_annexureForAmendingBill_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show statement of object and reason draft for only default bill language ****/
			$('.readonly_statementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle statement of object and reason draft for given language icon ****/
			$('.readonly_toggleStatementOfObjectAndReasonDraft').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#readonly_statementOfObjectAndReasonDraft_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_statementOfObjectAndReasonDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_statementOfObjectAndReasonDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			/**** show financial memorandum draft for only default bill language ****/
			$('.readonly_financialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_financialMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_financialMemorandumDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle financial memorandum draft on user demand ****/
			$('#readonly_financialMemorandumDrafts_button').click(function() {
				if($('#readonly_financialMemorandumDrafts_div').css('display')=='none') {
					$('#readonly_financialMemorandumDrafts_div').show();
				} else {
					$('#readonly_financialMemorandumDrafts_div').hide();
				}
				return false;
			});
			/**** toggle financial memorandum draft for given language icon ****/
			$('.readonly_toggleFinancialMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#readonly_financialMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_financialMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_financialMemorandumDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			
			
			/**** show statutory memorandum draft for only default bill language ****/
			$('.readonly_statutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];				
				if(currentLanguage==$('#readonly_defaultBillLanguage').val()) {		
					$('#readonly_statutoryMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_statutoryMemorandumDraft_para_'+currentLanguage).hide();										
				}
			});
			/**** toggle statutory memorandum draft on user demand ****/
			$('#readonly_statutoryMemorandumDrafts_button').click(function() {
				if($('#readonly_statutoryMemorandumDrafts_div').css('display')=='none') {
					$('#readonly_statutoryMemorandumDrafts_div').show();
				} else {
					$('#readonly_statutoryMemorandumDrafts_div').hide();
				}
				return false;
			});
			/**** toggle statutory memorandum draft for given language icon ****/
			$('.readonly_toggleStatutoryMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#readonly_statutoryMemorandumDraft_para_'+currentLanguage).css('display')=='none') {
					$('#readonly_statutoryMemorandumDraft_para_'+currentLanguage).show();					
				} else {
					$('#readonly_statutoryMemorandumDraft_para_'+currentLanguage).hide();					
				}
				return false;
			});
			/**** toggle checklist on user demand ****/
			$('#readonly_checklist_button').click(function() {
				if($('#readonly_checklist_div').css('display')=='none') {										
					if($('#readonly_opinionSoughtFromLawAndJD_div').css('display')=='none'							
							&& $('#readonly_opinionSoughtFromLawAndJD').val()!=""
							&& $('#readonly_opinionSoughtFromLawAndJD').val()!="<p></p>") {						
						$('#readonly_opinionSoughtFromLawAndJD_div').show();
						$('#readonly_checklist_div').show();
						$('.readonly_checklist_button_hr').show();
					} 
					/* else {
						$.prompt($('#readonly_opinionFromLawAndJdNotReceivedMsg').val());
					} */					
				} else {
					$('#readonly_checklist_div').hide();
					$('#readonly_opinionSoughtFromLawAndJD_div').hide();
					$('.readonly_checklist_button_hr').hide();
				}
				return false;
			});
			
			//----------------------revise drafts script----------------------//			
			$('.readonly_revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {					
					$('#readonly_revisedTitlePara_'+currentLanguage).show();					
				} else {
					$('#readonly_revisedTitlePara_'+currentLanguage).hide();						
				}
			});		
			
			$('.readonly_revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[4];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedContentDraftPara_'+currentLanguage).show();					
				} else {
					$('#readonly_revisedContentDraftPara_'+currentLanguage).hide();										
				}
			});
			
			if($('#readonly_typeOfSelectedBillType').val()=="amending") {
				$('.readonly_revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];	
					if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
						$('#readonly_revisedAnnexureForAmendingBillPara_'+currentLanguage).show();						
					} else {
						$('#readonly_revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();						
					}
				});
			}
			
			$('.readonly_revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];					
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();					
				} else {
					$('#readonly_revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();					
				}
			});
			
			$('.readonly_revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedFinancialMemorandumDraftPara_'+currentLanguage).show();					
				} else {
					$('#readonly_revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();					
				}
			});
			
			$('.readonly_revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#readonly_revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();					
				} else {
					$('#readonly_revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();					
				}
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
			
			#readonly_s7C{
				width: 24px;
			}
			
			div.fixed-width-wysiwyg {
				min-width: 400px !important;
				left: 10px !important;
				margin-top: 10px;
			}
			
			#readonly_opinionSoughtFromLawAndJD-wysiwyg-iframe {height: 400px;}		
			
			.textdraft_file {
				float: right; 
				margin: -210px 20px;
				position: relative;
			}		
		</style>
	</head> 

	<body>
		<div class="fields clearfix watermark">		
			<div id="readonly_assistantDiv">
				<form:form id="readonly_currentForm" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceTypeForBill} ${formattedNumber}</h2>		
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.houseType" text="House Type"/>*</label>
						<input id="readonly_formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="readonly_houseType" name="houseType" value="${houseType}" type="hidden">								
					</p>	
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.year" text="Year"/>*</label>
						<input id="readonly_formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="readonly_sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
					</p>
					
					<p style="display:none;">
						<label class="small"><spring:message code="bill.sessionType" text="Session Type"/>*</label>		
						<input id="readonly_formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="readonly_sessionType" name="sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="readonly_session" name="session" value="${session}"/>						
					</p>
					
					<p style="display:none;">
							<label class="small"><spring:message code="bill.deviceType" text="Device Type"/>*</label>
							<input id="readonly_formattedDeviceTypeForBill" name="formattedDeviceTypeForBill" value="${formattedDeviceTypeForBill}" class="sText" readonly="readonly">
							<input id="readonly_type" name="type" value="${deviceTypeForBill}" type="hidden">
							<input id="readonly_originalType" name="originalType" value="${originalDeviceType}" type="hidden">											
					</p>	
					
					<c:if test="${not empty domain.number or not empty domain.submissionDate}">
					<p>
						<c:if test="${not empty domain.number}">
						<label class="small"><spring:message code="bill.number" text="bill Number"/>*</label>
						<input id="readonly_formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
						<input id="readonly_number" name="number" value="${domain.number}" type="hidden">
						<form:errors path="number" cssClass="validationError"/>
						</c:if>
						<c:if test="${not empty domain.submissionDate}">				
						<label class="small"><spring:message code="bill.submissionDate" text="Submitted On"/></label>
						<input id="readonly_formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
						<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
						</c:if>
					</p>
					</c:if>
					
					<p>
						<label class="centerlabel"><spring:message code="bill.members" text="Members"/></label>
						<textarea id="readonly_members" class="sTextarea" readonly="readonly" rows="1" cols="20">${memberNames}</textarea>
						<c:if test="${!(empty primaryMember)}">
							<input id="readonly_primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
						</c:if>
						<c:if test="${!(empty supportingMembers)}">
							<select  name="selectedSupportingMembers" id="readonly_selectedSupportingMembers" multiple="multiple" style="display:none;">
							<c:forEach items="${supportingMembers}" var="i">
							<option value="${i.id}" selected="selected"></option>
							</c:forEach>		
							</select>
						</c:if>	
						<label class="centerlabel"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText centerlabel">
						<a href="#" id="readonly_viewContacts" style="vertical-align:top; margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>
					</p>
					
					<p>
						<label class="small"><spring:message code="bill.ministry" text="Ministry"/>*</label>
						<input id="readonly_formattedMinistry" name="readonly_formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
						<input name="readonly_ministry" id="readonly_ministry" type="hidden" value="${ministrySelected}">
						<%-- <select name="ministry" id="readonly_ministry" class="sSelect">
							<c:forEach items="${ministries }" var="i">
								<c:choose>
									<c:when test="${i.id==ministrySelected }">									
										<option value="${i.id }" selected="selected">${i.dropdownDisplayName}</option>
									</c:when>
									<c:otherwise>
										<option value="${i.id }" >${i.dropdownDisplayName}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select> --%>		
						<form:errors path="ministry" cssClass="validationError"/>
						<label class="small"><spring:message code="bill.subdepartment" text="Sub Department"/></label>
						<select name="subDepartment" id="readonly_subDepartment" class="sSelect">
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
						<select id="readonly_billType" class="sSelect" name="billType">
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
						<select id="readonly_billKind" class="sSelect" name="billKind">
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
					<p style="display: none;">
						<label class="small"><spring:message code="bill.introducingHouseType" text="Introducing House Type"/></label>
						<form:select id="readonly_introducingHouseType" class="sSelect" path="introducingHouseType">
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

					<div id="readonly_referredActDiv">
						<p>
							<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredAct)}">
									<a href="#" id="readonly_viewReferredAct" class="readonly_referenceLink" style="font-size: 18px;"><c:out value="${referredActNumber}"></c:out></a>
									<label id="readonly_referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="readonly_viewReferredAct" class="readonly_referenceLink" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="readonly_referredActYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="readonly_referredAct" name="referredAct" value="${referredAct}">
						</p>
					</div>
					<div id="readonly_referredOrdinanceDiv" style="margin-top:10px;">
						<p>
							<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredOrdinance)}">
									<a href="#" id="readonly_viewReferredOrdinance" class="readonly_referenceLink" style="font-size: 18px;"><c:out value="${referredOrdinanceNumber}"></c:out></a>
									<label id="readonly_referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="readonly_viewReferredOrdinance" class="readonly_referenceLink" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="readonly_referredOrdinanceYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="readonly_referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
						</p>
					</div>

					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">		
					<c:choose>	
					<c:when test="${empty parent}"><c:set var="displayParentBill" value="none"/></c:when>
					<c:otherwise><c:set var="displayParentBill" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayParentBill};">
						<label class="small"><spring:message code="bill.parentbill" text="Clubbed To"></spring:message></label>
						<a href="#" id="readonly_p${parent}" class="readonly_referenceLink" onclick="viewBillDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
						<input type="hidden" id="readonly_parent" name="parent" value="${parent}">
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
									<a href="#" id="readonly_cq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<select id="readonly_clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
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
													<a href="#" id="readonly_rq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewActDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:when>
												<c:otherwise>
													<a href="#" id="readonly_rq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:otherwise>
											</c:choose>												
										</c:when>
										<c:otherwise>											
											<a href="#" id="readonly_rq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="readonly_referencedBill" name="referencedBill" value="${referencedBill}" />						
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
											<a href="#" id="readonly_lq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})	
										</c:when>
										<c:otherwise>											
											<a href="#" id="readonly_lq${i.number}" class="clubbedRefBills readonly_referenceLink" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
											&nbsp;(${lapsedBillsSessionAndDevice[index.count-1]})
										</c:otherwise>
									</c:choose>									
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="readonly_lapsedBill" name="lapsedBill" value="${lapsedBill}" />						
					</p>
					
					<!-- <h2></h2> -->
					
					<div style="margin-top: 20px;">
						<fieldset>
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_titles"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForTitles' text='View Revisions for Titles'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${titles}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleTitle readonly_iconLink" id="readonly_toggleTitle_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>																			
							<div id="readonly_titles_div">
								<c:forEach var="i" items="${titles}">
									<div id="readonly_title_para_${i.language.type}" style="display:none;">
									<p>
										<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
										<textarea rows="2" cols="50" class="readonly_title" id="readonly_title_text_${i.language.type}" name="title_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseTitle" id="readonly_reviseTitle_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="readonly_reviseTitle_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseTitle' text='Revise This Title'></spring:message>" class="imageLink" />
										</a>
									</p>															
									<p id="readonly_revisedTitlePara_${i.language.type}" style="display:none;">
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
										<textarea rows="2" cols="50" class="readonly_revisedTitle" id="readonly_revised_title_text_${i.language.type}" name="revised_title_text_${i.language.type}">${revisedTitleText}</textarea>
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
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_contentDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForContentDrafts' text='View Revisions for Content Drafts'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${contentDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleContentDraft readonly_iconLink" id="readonly_toggleContentDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>							
							</p>						
							<div id="readonly_contentDrafts_div">
								<c:forEach var="i" items="${contentDrafts}" varStatus="draftNumber">
									<div id="readonly_contentDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
										<textarea class="wysiwyg readonly_contentDraft" id="readonly_contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<div class="textdraft_file">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="readonly-contentDraft-file-${i.language.type}" />
												<jsp:param name="filetag" value="${i.file}" />
												<jsp:param name="isUploadAllowed" value="false" />
												<jsp:param name="isRemovable" value="false" />
											</jsp:include>			
										</div>
										<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseContentDraft" id="readonly_reviseContentDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
											<img id="readonly_reviseContentDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseContentDraft' text='Revise This Content Draft'></spring:message>" class="imageLink" />
										</a>
										<c:if test="${i.language.type==defaultBillLanguage}">											
											<a href="#" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
												<img id="readonly_s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
											</a>											
										</c:if>
									</p>
									<p id="readonly_revisedContentDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedContentDraft" text=" Revised Draft"/></label>						
										<c:set var="revisedContentDraftText" value="revisedContentDraft_${i.language.type}"></c:set>
										<c:set var="revisedContentDraftId" value="revisedContentDraft_id_${i.language.type}"></c:set>
										<c:set var="revisedContentDraftFile" value="revisedContentDraft-file-${i.language.type}"></c:set>										
										<textarea class="wysiwyg revisedContentDraft" id="readonly_revised_contentDraft_text_${i.language.type}" name="revised_contentDraft_text_${i.language.type}">${requestScope[revisedContentDraftText]}</textarea>
										<div class="textdraft_file">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="readonly-revised-contentDraft-file-${i.language.type}" />
												<jsp:param name="filetag" value="${requestScope[revisedContentDraftFile]}" />
												<jsp:param name="isUploadAllowed" value="false" />
												<jsp:param name="isRemovable" value="false" />
											</jsp:include>			
										</div>
										<input type="hidden" name="revised_contentDraft_id_${i.language.type}" value="${revisedContentDraftId}">												
									</p>
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<div id="readonly_annexuresForAmendingBill_div" style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_annexuresForAmendingBill"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForAnnexureForAmendingBills' text='View Revisions for Annexures For Amending Bill'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleAnnexureForAmendingBill readonly_iconLink" id="readonly_toggleAnnexureForAmendingBill_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>							
							</p>						
							<div>
								<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="draftNumber">
									<div id="readonly_annexureForAmendingBill_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.annexureForAmendingBill" text="Annexure For Amending Bill"/></label>
										<textarea class="wysiwyg readonly_annexureForAmendingBill" id="readonly_annexureForAmendingBill_text_${i.language.type}" name="annexureForAmendingBill_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="annexureForAmendingBill_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="annexureForAmendingBill_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseAnnexureForAmendingBill" id="readonly_reviseAnnexureForAmendingBill_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
											<img id="readonly_reviseAnnexureForAmendingBill_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseAnnexureForAmendingBill' text='Revise This Annexure For Amending Bill'></spring:message>" class="imageLink" />
										</a>
										<c:if test="${i.language.type==defaultBillLanguage}">											
											<a href="#" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
												<img id="readonly_s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
											</a>											
										</c:if>
									</p>
									<p id="readonly_revisedAnnexureForAmendingBillPara_${i.language.type}" style="display:none;">
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
										<textarea class="wysiwyg readonly_revisedAnnexureForAmendingBill" id="readonly_revised_annexureForAmendingBill_text_${i.language.type}" name="revised_annexureForAmendingBill_text_${i.language.type}">${revisedAnnexureForAmendingBillText}</textarea>
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
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_statementOfObjectAndReasonDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForStatementOfObjectAndReasonDrafts' text='View Revisions for Statement Of Object And Reason'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleStatementOfObjectAndReasonDraft readonly_iconLink" id="readonly_toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div id="readonly_statementOfObjectAndReasonDrafts_div">
								<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
									<div id="readonly_statementOfObjectAndReasonDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
										<textarea class="wysiwyg readonly_statementOfObjectAndReasonDraft" id="readonly_statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatementOfObjectAndReasonDraft" id="readonly_reviseStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="readonly_reviseStatementOfObjectAndReasonDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseSOR' text='Revise This Statement of Object and Reason'></spring:message>" class="imageLink" />
										</a>
									</p>
									<p id="readonly_revisedStatementOfObjectAndReasonDraftPara_${i.language.type}" style="display:none;">
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
										<textarea class="wysiwyg readonly_revisedStatementOfObjectAndReasonDraft" id="readonly_revised_statementOfObjectAndReasonDraft_text_${i.language.type}" name="revised_statementOfObjectAndReasonDraft_text_${i.language.type}">${revisedStatementOfObjectAndReasonDraftText}</textarea>
										<input type="hidden" name="revised_statementOfObjectAndReasonDraft_id_${i.language.type}" value="${revisedStatementOfObjectAndReasonDraftId}">												
									</p>
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>					
					
					<p style="margin-left: 162px;margin-top: 20px;">
					<a href="#" id="readonly_financialMemorandumDrafts_button" class="readonly_iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Fmemo.jpg" title="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>	
					<a href="#" id="readonly_statutoryMemorandumDrafts_button" class="readonly_iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Smemo.jpg" title="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="readonly_checklist_button" class="readonly_iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/checklist.jpg" title="<spring:message code='bill.checklist' text='Checklist'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="readonly_recommendationFromGovernor_button" class="readonly_iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Grec.jpg" title="<spring:message code='bill.recommendationFromGovernor' text='Recommendation From Governor'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="readonly_recommendationFromPresident_button" class="readonly_iconLink" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Prec.jpg" title="<spring:message code='bill.recommendationFromPresident' text='Recommendation From President'></spring:message>" class="imageLink impIcons" />
					</a>
					</p>
					
					<div id="readonly_financialMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_financialMemorandumDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForFinancialMemorandumDrafts' text='View Revisions for Financial Memorandum'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${financialMemorandumDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleFinancialMemorandumDraft readonly_iconLink" id="readonly_toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div>
								<c:forEach var="i" items="${financialMemorandumDrafts}">
									<div id="readonly_financialMemorandumDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
										<textarea class="wysiwyg readonly_financialMemorandumDraft" id="readonly_financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseFinancialMemorandumDraft" id="readonly_reviseFinancialMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="readonly_reviseFinancialMemorandumDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseFM' text='Revise This Financial Memorandum'></spring:message>" class="imageLink" />
										</a>
									</p>
									<p id="readonly_revisedFinancialMemorandumDraftPara_${i.language.type}" style="display:none;">
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
										<textarea class="wysiwyg readonly_revisedFinancialMemorandumDraft" id="readonly_revised_financialMemorandumDraft_text_${i.language.type}" name="revised_financialMemorandumDraft_text_${i.language.type}">${revisedFinancialMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_financialMemorandumDraft_id_${i.language.type}" value="${revisedFinancialMemorandumDraftId}">												
									</p>
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>					
					
					<div id="readonly_statutoryMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -20px;">
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_statutoryMemorandumDrafts"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForStatutoryMemorandumDrafts' text='View Revisions for Statutory Memorandum'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${statutoryMemorandumDrafts}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="readonly_toggleStatutoryMemorandumDraft readonly_iconLink" id="readonly_toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							</p>
							<div>
								<c:forEach var="i" items="${statutoryMemorandumDrafts}">
									<div id="readonly_statutoryMemorandumDraft_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
										<textarea class="wysiwyg readonly_statutoryMemorandumDraft" id="readonly_statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseStatutoryMemorandumDraft" id="readonly_reviseStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;">
											<img id="readonly_reviseStatutoryMemorandumDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseSM' text='Revise This Statutory Memorandum'></spring:message>" class="imageLink" />
										</a>
									</p>
									<p id="readonly_revisedStatutoryMemorandumDraftPara_${i.language.type}" style="display:none;">
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
										<textarea class="wysiwyg readonly_revisedStatutoryMemorandumDraft" id="readonly_revised_statutoryMemorandumDraft_text_${i.language.type}" name="revised_statutoryMemorandumDraft_text_${i.language.type}">${revisedStatutoryMemorandumDraftText}</textarea>
										<input type="hidden" name="revised_statutoryMemorandumDraft_id_${i.language.type}" value="${revisedStatutoryMemorandumDraftId}">												
									</p>
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>					
					
					<div id="readonly_recommendationFromGovernor_div"  style="display:none;">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromGovernor" text="Recommendation From Governor"/></label>
						<form:textarea id="readonly_recommendationFromGovernor" path="recommendationFromGovernor" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromGovernor" />
					</p>
					<c:if test="${not empty dateOfRecommendationFromGovernor}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromGovernor" text="Date Of Recommendation From Governor"/></label>
					<input id="readonly_formattedDateOfRecommendationFromGovernor" name="formattedDateOfRecommendationFromGovernor" value="${formattedDateOfRecommendationFromGovernor}" class="sText" readonly="readonly">
					<input id="readonly_setDateOfRecommendationFromGovernor" name="setDateOfRecommendationFromGovernor" type="hidden"  value="${dateOfRecommendationFromGovernor}">	
					</p>
					</c:if>		
					</div>					
					
					<div id="readonly_recommendationFromPresident_div"  style="display:none;">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromPresident" text="Recommendation From President"/></label>
						<form:textarea id="readonly_recommendationFromPresident" path="recommendationFromPresident" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromPresident" />
					</p>
					<c:if test="${not empty dateOfRecommendationFromPresident}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromPresident" text="Date Of Recommendation From President"/></label>
					<input id="readonly_formattedDateOfRecommendationFromPresident" name="formattedDateOfRecommendationFromPresident" value="${formattedDateOfRecommendationFromPresident}" class="sText" readonly="readonly">
					<input id="readonly_setDateOfRecommendationFromPresident" name="setDateOfRecommendationFromPresident" type="hidden"  value="${dateOfRecommendationFromPresident}">	
					</p>
					</c:if>	
					</div>						
					<div>
						<h2 class="readonly_checklist_button_hr" style="display: none;"></h2>
						<div id="readonly_opinionSoughtFromLawAndJD_div" style="display: none;float: left;margin-top: 10px;">
							<p id="readonly_opinionSoughtFromLawAndJD_para">
								<label><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
								<br/>
								<form:textarea id="readonly_opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="true"></form:textarea>
								<form:errors path="opinionSoughtFromLawAndJD" />
							</p>					
							<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
							<p>
							<label><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
							<br/>
							<input id="readonly_formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" style="margin-left: 10px !important;" readonly="readonly">
							<input id="readonly_setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
							</p>
							</c:if>
						</div>
						
						<div id="readonly_checklist_div"  style="display: none;margin-left: 420px;margin-top: 10px;">					
							<fieldset>
							<label><spring:message code="bill.checklist" text="Checklist" /></label>							
							<a href="#" class="readonly_viewRevisions" id="readonly_viewRevisions_checklist" style="margin-left: 20px;margin-bottom: 5px;margin-right: 20px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForChecklist' text='View Revisions for Checklist'></spring:message>" class="imageLink" />
							</a>
							<a href="#" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
								<img id="readonly_s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
							</a>
							<br/>							
							<table style="width: 500px; border: 2px solid black;">
								<tbody>
									<tr id="readonly_checklistQuestion1" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 15px;padding-left: 3px;">(${checklistSerialNumbers[1]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<br/>												
												<label><spring:message code="bill.checklistQuestion1" text="Are following things essential for this bill?"/></label>
											</div>
											<table style="width: 490px;">
												<tr>
													<td style="vertical-align: top;padding-top: 11px;padding-left: 8px;">
														(<spring:message code="bill.checklistQuestion1.A" text="A"/>)
													</td>
													<td>														
														<div>
															<label style="width:400px;"><spring:message code="bill.checklistQuestion1.1" text="is bill recommended in accordance with constitution article 207 (1)?"/></label>
															<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="readonly_checklist_checkbox_1" style="margin: 10px; margin-left: 10px;">
															<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_1" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_1']" />
														</div>																																			
														<div style="display: none;" id="readonly_checklist_checkbox_1_div">
															<label><spring:message code="bill.checklistQuestion1.2" text="if yes, please mention sections"/></label>
															<br/>
															<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;"/>
														</div>
													</td>
												</tr>
												<tr>
													<td style="vertical-align: top;padding-top: 11px;padding-left: 8px;">
														(<spring:message code="bill.checklistQuestion1.B" text="B"/>)
													</td>
													<td>
														<div>
															<label style="width:400px;"><spring:message code="bill.checklistQuestion1.3" text="is bill recommended in accordance with constitution article 207 (3)?"/></label>
															<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="readonly_checklist_checkbox_2" style="margin: 10px; margin-left: 10px;">
															<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_2" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_3']" />
														</div>																							
														<div style="display: none;" id="readonly_checklist_checkbox_2_div">
															<label><spring:message code="bill.checklistQuestion1.4" text="if yes, please mention sections"/></label>
															<br/>
															<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_3']" rows="2" cols="50" style="margin: 10px;" />
														</div>													
													</td>
												</tr>
												<tr>
													<td style="vertical-align: top;padding-top: 11px;padding-left: 8px;">
														(<spring:message code="bill.checklistQuestion1.C" text="C"/>)
													</td>
													<td>
														<div>
															<label style="width:400px;"><spring:message code="bill.checklistQuestion1.5" text="is bill recommended in accordance with constitution article 304 (b)?"/></label>
															<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_3" style="margin: 10px; margin-left: 10px;">
															<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_3" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_304_b']" />
														</div>
														<div style="display: none;" id="readonly_checklist_checkbox_3_div">
															<label><spring:message code="bill.checklistQuestion1.6" text="if yes, please mention sections"/></label>
															<br/>
															<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_304_b']" rows="2" cols="50" style="margin: 10px;" />
														</div>
													</td>
												</tr>
											</table>											
										</td>
									</tr>
									<tr id="readonly_checklistQuestion2" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[2]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">												
												<label style="width:400px;"><spring:message code="bill.checklistQuestion2.1" text="is bill in scope of state legislature?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="readonly_checklist_checkbox_4" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_4" type="hidden" path="checklist['isInScopeOfStateLegislature']" />
											</div>
											
											<div style="display: none;" id="readonly_checklist_checkbox_4_div">
												<label><spring:message code="bill.checklistQuestion2.2" text="if yes, please also mention related schedule issues"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['issuesInRelatedScheduleForScopeOfStateLegislature']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="readonly_checklistQuestion3" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[3]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion3.1" text="is this bill a money bill?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_5" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_5" type="hidden" path="checklist['isMoneyBill']" />
											</div>
											
											<div style="display: none;" id="readonly_checklist_checkbox_5_div">
												<label><spring:message code="bill.checklistQuestion3.2" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForBeingMoneyBill']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="readonly_checklistQuestion4" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[4]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion4.1" text="is this bill a financial bill as per constitution article 207 (1)?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_6" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_6" type="hidden" path="checklist['isFinancialBillAsPerConstitutionArticle_207_1']" />
											</div>
											
											<div style="display: none;" id="readonly_checklist_checkbox_6_div">
												<label><spring:message code="bill.checklistQuestion4.2" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForFinancialBillAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>								
									<tr id="readonly_checklistQuestion5" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[5]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion5.1" text="are amendments for amending bill as per scope of original act?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_7" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_7" type="hidden" path="checklist['areAmendmentsForAmendingBillAsPerScopeOfOriginalAct']" />
											</div>																					
										</td>
									</tr>
									<tr id="readonly_checklistQuestion6" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[6]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion6.1" text="is statutory memorandum mandatory?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_8" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_8" type="hidden" path="checklist['isStatutoryMemorandumMandatory']" />
											</div>		
																			
											<div style="display: none;" id="readonly_checklist_checkbox_8_div">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion6.2" text="is statutory memorandum as per rules?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_9" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_9" type="hidden" path="checklist['isStatutoryMemorandumAsPerRules']" />
											</div>
											
											<div style="display: none;" id="readonly_checklist_checkbox_9_div">
												<label><spring:message code="bill.checklistQuestion6.3" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForStatutoryMemorandum']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="readonly_checklistQuestion7" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[7]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion7.1" text="is financial memorandum mandatory?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_10" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_10" type="hidden" path="checklist['isFinancialMemorandumMandatory']" />
											</div>	
																				
											<div style="display: none;" id="readonly_checklist_checkbox_10_div">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion7.2" text="is financial memorandum as per rules?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_11" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_11" type="hidden" path="checklist['isFinancialMemorandumAsPerRules']" />
											</div>
											
											<div style="display: none;" id="readonly_checklist_checkbox_11_div">
												<label><spring:message code="bill.checklistQuestion7.3" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForFinancialMemorandum']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="readonly_checklistQuestion8" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[8]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion8.1" text="is statement of object and reason complete?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_12" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_12" type="hidden" path="checklist['isStatementOfObjectAndReasonComplete']" />
											</div>																		
										</td>
									</tr>
									<tr id="readonly_checklistQuestion9" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[9]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">											
												<label style="width:400px;"><spring:message code="bill.checklistQuestion9.1" text="is law & judiciary department in agreement with above opinions on issues 1, 2, 6 & 7?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_13" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_13" type="hidden" path="checklist['isLawAndJudiciaryDepartmentInAgreementWithOpinions']" />
											</div>																			
										</td>
									</tr>
									<tr id="readonly_checklistQuestion10" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[10]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">										
												<label style="width:400px;"><spring:message code="bill.checklistQuestion10.1" text="are there any recommendations on subject-matter of this bill by sub-legislation committee?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="readonly_checklist_checkbox_14" style="margin: 10px; margin-left: 38px;">
												<form:input class="readonly_checklist_checkbox_fields" id="readonly_checklist_checkbox_field_14" type="hidden" path="checklist['isRecommendedOnSubjectMatterBySubLegislationCommittee']" />
											</div>																				
										</td>
									</tr>
								</tbody>
							</table>
							</fieldset>
						</div>	
						<h2 class="readonly_checklist_button_hr" style="display: none;"></h2>									
					</div>		
					
					<div>
						<c:choose>
							<c:when test="${internalStatusPriority < statusUpdationPriority}">
							<p id="readonly_internalStatusDiv">
								<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
								<input id="readonly_formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
							</p>
							</c:when>
							<c:otherwise>
							<p id="readonly_recommendationStatusDiv">
								<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
								<input id="readonly_formattedRecommendationStatus" value="${formattedRecommendationStatus}" type="text" readonly="readonly">
							</p>
							</c:otherwise>
						</c:choose>
						
						<input type="hidden" id="readonly_internalStatus"  name="internalStatus" value="${internalStatus }">
						<input type="hidden" id="readonly_recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					</div>		
					
					<form:hidden id="readonly_id" path="id"/>
					<input id="readonly_levelForWorkflow" name="levelForWorkflow" type="hidden">
					<input type="hidden" name="status" id="readonly_status" value="${status }">
					<input type="hidden" name="createdBy" id="readonly_createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="readonly_dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="readonly_setCreationDate" value="${creationDate }">
					<input id="readonly_setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input type="hidden" name="workflowStartedOnDate" id="readonly_workflowStartedOnDate" value="${workflowStartedOnDate }">
					<input type="hidden" name="workflowForTranslationStartedOnDate" id="readonly_workflowForTranslationStartedOn" value="${workflowForTranslationStartedOnDate }">
					<input type="hidden" name="workflowForOpinionFromLawAndJDStartedOnDate" id="readonly_workflowForOpinionFromLawAndJDStartedOn" value="${workflowForOpinionFromLawAndJDStartedOnDate }">
					<input type="hidden" name="taskReceivedOnDate" id="readonly_taskReceivedOnDate" value="${taskReceivedOnDate }">	
					<input type="hidden" name="taskReceivedOnDateForTranslation" id="readonly_taskReceivedOnDateForTranslation" value="${taskReceivedOnDateForTranslation }">
					<input type="hidden" name="taskReceivedOnDateForOpinionFromLawAndJD" id="readonly_taskReceivedOnDateForOpinionFromLawAndJD" value="${taskReceivedOnDateForOpinionFromLawAndJD}">
					<input id="readonly_role" name="role" value="${role}" type="hidden">
					<input id="readonly_taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="readonly_usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="readonly_usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input id="readonly_oldInternalStatus" name="oldInternalStatus" value="${internalStatus}" type="hidden">
					<input id="readonly_oldRecommendationStatus" name="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
				</form:form>
				<input id="readonly_pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
				<input id="readonly_ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="readonly_subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">				
				<input id="readonly_selectedBillType" value="${selectedBillType}" type="hidden">
				<input id="readonly_selectedBillKind" value="${selectedBillKind}" type="hidden">
				<input id="readonly_typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />
				<input id="readonly_typeOfSelectedBillType" type="hidden" value="${typeOfSelectedBillType}" />
				<input type="hidden" id="readonly_isActReferenced" value="${isActReferenced}">
				<input type="hidden" id="readonly_defaultBillLanguage" value="${defaultBillLanguage}">
				<input type="hidden" id="readonly_isMoneyBillPrompt" value="<spring:message code="bill.isMoneyBillPrompt" text="Are you sure this is a money bill?"></spring:message>">
				<input type="hidden" id="readonly_isNotMoneyBillPrompt" value="<spring:message code="bill.isNotMoneyBillPrompt" text="Are you sure this is not a money bill?"></spring:message>">
				<input type="hidden" id="readonly_isChecklistFilled" value="${isChecklistFilled}">
				<input type="hidden" id="readonly_reviseTitle_text" value="<spring:message code="bill.reviseTitle_text" text="Revise This Title"></spring:message>">
				<input type="hidden" id="readonly_unReviseTitle_text" value="<spring:message code="bill.unReviseTitle_text" text="Un-Revise This Title"></spring:message>">
				<input type="hidden" id="readonly_reviseContentDraft_text" value="<spring:message code="bill.reviseContentDraft_text" text="Revise This Content Draft"></spring:message>">
				<input type="hidden" id="readonly_unReviseContentDraft_text" value="<spring:message code="bill.unReviseContentDraft_text" text="Un-Revise This Content Draft"></spring:message>">
				<input type="hidden" id="readonly_reviseAnnexureForAmendingBill_text" value="<spring:message code="bill.reviseAnnexureForAmendingBill_text" text="Revise This Annexure For Amending Bill"></spring:message>">
				<input type="hidden" id="readonly_unReviseAnnexureForAmendingBill_text" value="<spring:message code="bill.unReviseAnnexureForAmendingBill_text" text="Un-Revise This Annexure For Amending Bill"></spring:message>">
				<input type="hidden" id="readonly_reviseStatementOfObjectAndReasonDraft_text" value="<spring:message code="bill.reviseStatementOfObjectAndReasonDraft_text" text="Revise This Statement of Object and Reason"></spring:message>">
				<input type="hidden" id="readonly_unReviseStatementOfObjectAndReasonDraft_text" value="<spring:message code="bill.unReviseStatementOfObjectAndReasonDraft_text" text="Un-Revise This Statement of Object and Reason"></spring:message>">
				<input type="hidden" id="readonly_reviseFinancialMemorandumDraft_text" value="<spring:message code="bill.reviseFinancialMemorandumDraft_text" text="Revise This Financial Memorandum"></spring:message>">
				<input type="hidden" id="readonly_unReviseFinancialMemorandumDraft_text" value="<spring:message code="bill.unReviseFinancialMemorandumDraft_text" text="Un-Revise This Financial Memorandum"></spring:message>">
				<input type="hidden" id="readonly_reviseStatutoryMemorandumDraft_text" value="<spring:message code="bill.reviseStatutoryMemorandumDraft_text" text="Revise This Statutory Memorandum"></spring:message>">
				<input type="hidden" id="readonly_unReviseStatutoryMemorandumDraft_text" value="<spring:message code="bill.unReviseStatutoryMemorandumDraft_text" text="Un-Revise This Statutory Memorandum"></spring:message>">
				<input type="hidden" id="readonly_workflowtype" value="${workflowtype}"/>
			</div>		
		</div>		
	</body>
</html>