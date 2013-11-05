<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="bill" text="Bill Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		.toolbar{
			display: none !important;
		}
		#noticeContent{
			width: 400px;
		}
		.wysiwyg{
			margin-left: 160px;
			position: static !important;
			overflow: visible !important; 
			
		}		
	</style>
	
	<style type="text/css">
		.imageLink{
			width: 14px;
			height: 14px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		}
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
	</style>
	
	<script type="text/javascript">	
		//this is for autosuggest
		function split( val ) {
			return val.split( /,\s*/ );
		}	
		function extractLast( term ) {
			return split( term ).pop();
		}	
		var controlName=$(".autosuggestmultiple").attr("id");
	
		/**** Load Sub Departments ****/
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
	
		$(document).ready(function(){	
			$("#currentHouseType").val($("#selectedHouseType").val());
			
			if($('#ministrySelected').val()=="" || $('#ministrySelected').val()==undefined){		
				$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select ministry for now, this option will be useful.
				$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
				
			if($('#subDepartmentSelected').val()=="" || $('#subDepartmentSelected').val()==undefined){
				$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select subdepartment for now, this option will be useful.
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");			
			}
			
			if($('#selectedBillType').val()=="" || $('#selectedBillType').val()==undefined){		
				$("#billType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill type for now, this option will be useful.
				$("#billType").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			
			if($('#selectedBillKind').val()=="" || $('#selectedBillKind').val()==undefined){		
				$("#billKind").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill kind for now, this option will be useful.
				$("#billKind").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			
			if($('#selectedIntroducingHouseType').val()=="" || $('#selectedIntroducingHouseType').val()==undefined){		
				$("#introducingHouseType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
			}else{
				//in case member doesnt want to select introducing housetype for now, this option will be useful.
				$("#introducingHouseType").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");		
			}
			
			//autosuggest		
			$( ".autosuggest").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers?session='+$("#session").val(),
				select:function(event,ui){			
				$("#primaryMember").val(ui.item.id);
			}	
			});	
			$("select[name='"+controlName+"']").hide();	
			$( ".autosuggestmultiple" ).change(function(){
				//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
				//for this we iterate through the slect box selected value and check if that value is present in the 
				//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
				//then that value will be removed from the select box.
				var value=$(this).val();
				$("select[name='"+controlName+"'] option:selected").each(function(){
					var optionClass=$(this).attr("class");
					if(value.indexOf(optionClass)==-1){
						$("select[name='"+controlName+"'] option[class='"+optionClass+"']").remove();
					}		
				});	
				$("select[name='"+controlName+"']").hide();				
			});
			//http://api.jqueryui.com/autocomplete/#event-select
			$( ".autosuggestmultiple" ).autocomplete({
				minLength:3,
				source: function( request, response ) {
					$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()+'&primaryMemberId='+$('#primaryMember').val(), {
						term: extractLast( request.term )
					}, response );
				},			
				search: function() {
					var term = extractLast( this.value );
					if ( term.length < 2 ) {
						return false;
					}
				},
				focus: function() {
					return false;
				},
				select: function( event, ui ) {
					//what happens when we are selecting a value from drop down
					var terms = $(this).val().split(",");
					//if select box is already present i.e atleast one option is already added
					if($("select[name='"+controlName+"']").length>0){
						if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
						//if option being selected is already present then do nothing
						this.value = $(this).val();					
						$("select[name='"+controlName+"']").hide();						
						}else{
						//if option is not present then add it in select box and autocompletebox
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
						$("select[name='"+controlName+"']").append(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
						}							
						$("select[name='"+controlName+"']").hide();								
						}
					}else{
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
						textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
						text=text+textoption+"</select>";
						$(this).after(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
						}	
						$("select[name='"+controlName+"']").hide();									
					}		
					return false;
				}
			});
			
			/**** refer act only in case of amendment bill type ****/
			if($('#typeOfSelectedBillType').val()!='amending') {
				$('#referredActDiv').hide();
			}	
			
			if($('#typeOfSelectedBillType').val()!='replace_ordinance') {
				$('#referredOrdinanceDiv').hide();
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
			
			$('#billType').change(function() {
				$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
					if(data!=undefined || data!='') {
						if(data=='amending') {
							$('#referredActDiv').show();
							$('#referredOrdinanceDiv').hide();
						} else if(data=='replace_ordinance'){
							$('#referredOrdinanceDiv').show();
							$('#referredActDiv').hide();
						}else{
							$('#referredActDiv').hide();
							$('#referredOrdinanceDiv').hide();
						}
					} else {
						alert("Some Error Occured!");
					}
				});
			});
			
			/**** Ministry Changes ****/	
			$("#ministry").change(function(){
				if($(this).val()!=''){
					loadSubDepartments($(this).val());
				}else{
					$("#subDepartment").empty();				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
				}
			});
			
			//view supporting members status
		    $("#viewStatus").click(function(){
			    $.get('bill/status/'+$("#id").val(),function(data){
				    $.fancybox.open(data);
			    });
			    return false;
		    });
			
		    /**** Referring Act for Amendment Bill ****/
			$('#referAct').click(function() {
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
				$.get('bill/referAct/init?',function(data){
					$.unblockUI();			
					//$.fancybox.open(data,{autoSize:false,width:750,height:700});
					$("#referringActResultDiv").html(data);					
					$("#referringActResultDiv").show();				
					$("#billDiv").hide();
					$("#backToBillDiv").show();			
				},'html');
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
			
		  	//send for supporting member approval
			$("#sendforapproval").click(function(e){
				//no need to send for approval in case of empty supporting members.
				if($("#selectedSupportingMembers").val()==""){								
					$.prompt($('#supportingMembersEmptyMsg').val(),{
						buttons: {Ok:true}, callback: function(v){
					   		if(v){
					   			scrollTop();
					   			$('#selectedSupportingMembers').focus();
					   		}     						
						}
					});	
					return false;
				}
				
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				
				if($('#referredActDiv').is(':hidden')) {
					$('#referredAct').val("");
				}
				
				if($('#referredOrdinanceDiv').is(':hidden')) {
					$('#referredOrdinance').val("");
				}
				
				$.prompt($('#sendForApprovalMsg').val()+$("#selectedSupportingMembers").val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?operation=approval',  
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
			
			//send for submission
			$("#submitbill").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				
				if($('#referredActDiv').is(':hidden')) {
					$('#referredAct').val("");
				}
				
				if($('#referredOrdinanceDiv').is(':hidden')) {
					$('#referredOrdinance').val("");
				}
				
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			        	$.post($('form').attr('action')+'?operation=submit',  
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
			
			if($("#currentStatus").val()=='bill_submit'){
				$("#ministry").attr("disabled","disabled");
				$("#department").attr("disabled","disabled");
				$("#subDepartment").attr("disabled","disabled");
				$("#billType").attr("disabled","disabled");
				$("#billKind").attr("disabled","disabled");
				if($('#typeOfSelectedDeviceType').val()=='bills_government') {
					$("#introducingHouseType").attr("disabled","disabled");
				}				
				$("#title").attr("readonly","readonly");
				//for content drafts, SOR drafts, financial memorandum drafts  & statutory memorandum drafts
				$(".drafts").each(function() {
					$(this).attr("readonly","readonly");
				});				
			}
			
			/**** Right Click Menu ****/
			$(".refer").contextMenu({menu: 'contextMenuItems'},
		        function(action, el, pos) {
				if(action=='dereferAct'){
					removeReferredAct();	
				} else if(action=='dereferOrdinance'){
					removeReferredOrdinance();	
				}
	    	});
			
			$('#referOrdinance').click(function() {
				referenceForBill('ordinance');
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
		});
		
		/**** Removing Referred Act from amending Bill ****/
		function removeReferredAct(){
			if($('#viewReferredAct').text()!="-") {
				$('#viewReferredAct').css('text-decoration','none');
				$.prompt($('#dereferActWarningMessage').val(),{
					buttons: {Ok:true}, callback: function(v){
				   		if(v){
				   			$('#referredAct').val("");		
				   			$('#viewReferredAct').text("-");
				   			$('#viewReferredAct').css('text-decoration','none');				   			
							$('#referredActYear').text("");				   			
				   		}     						
					}
				});
			}							
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
			$.get('bill/referAct/init?action='+refType,function(data){
				$.unblockUI();			
				if(refType=='act'){
					$("#referringActResultDiv").html(data);					
					$("#referringActResultDiv").show();
				}else if(refType=='ordinance'){
					$("#referringOrdinanceResultDiv").html(data);					
					$("#referringOrdinanceResultDiv").show();
				}
				$("#billDiv").hide();
				$("#backToBillDiv").show();
				
			},'html');
		}
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<div id="billDiv">
<form:form action="bill" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<h2>${formattedDeviceTypeForBill} ${formattedNumber}</h2>
	<p>
		<form:errors path="version" cssClass="validationError"/>
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
		
	<p>
		<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
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
	
	<p>
		<label class="small"><spring:message code="bill.ministry" text="Ministry"/></label>
		<select name="ministry" id="ministry" class="sSelect">
		<c:forEach items="${ministries}" var="i">
			<c:choose>
				<c:when test="${i.id==ministrySelected }">
					<option value="${i.id}" selected="selected">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}" >${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</select>
		<form:errors path="ministry" cssClass="validationError"/>
		<label class="small"><spring:message code="bill.subdepartment" text="Sub Department"/></label>
		<select name="subDepartment" id="subDepartment" class="sSelect">
		<c:forEach items="${subDepartments}" var="i">
			<c:choose>
				<c:when test="${i.id==subDepartmentSelected}">
					<option value="${i.id}" selected="selected">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		</select>
	</p>
	
	<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
	<p>
		<label class="centerlabel"><spring:message code="bill.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<%-- <c:if test="${(selectedQuestionType=='questions_halfhourdiscussion_from_question' or selectedQuestionType=='questions_halfhourdiscussion_standalone') and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
			<label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel"><spring:message code="bill.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label>
		</c:if> --%>
		<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
		</c:forEach>		
		</select>
		</c:if>
		<a href="#" id="viewStatus"><spring:message code="bill.viewstatus" text="View Status"></spring:message></a>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>
	</c:if>
	
	<div id="referredActDiv">
		<p>
			<a href="#" id="referAct" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referAct" text="Refer Act"></spring:message></a>
		</p>		
		<p>
			<label class="small"><spring:message code="bill.referredAct" text="Referred Act"></spring:message></label>
			<c:choose>
				<c:when test="${!(empty referredAct)}">
					<a href="#" id="viewReferredAct" style="font-size: 18px;" class="refer"><c:out value="${referredActNumber}"></c:out></a>
					<label id="referredActYear">(<spring:message code="bill.referredActYear" text="Year"/>: ${referredActYear})</label>
				</c:when>
				<c:otherwise>
					<a href="#" id="viewReferredAct" style="font-size: 18px; text-decoration: none;" class="refer"><c:out value="-"></c:out></a>
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
						<a href="#" id="viewReferredOrdinance" style="font-size: 18px;" class="refer"><c:out value="${referredOrdinanceNumber}"></c:out></a>
						<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
					</c:when>
					<c:otherwise>
						<a href="#" id="viewReferredOrdinance" style="font-size: 18px; text-decoration: none;" class="refer"><c:out value="-"></c:out></a>
						<label id="referredOrdinanceYear"></label>
					</c:otherwise>
				</c:choose>
				<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
			</p>
		</div>		
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.titles" text="Titles of Bill" /></label></legend>
			<c:forEach var="i" items="${titles}" varStatus="position">
			<c:choose>
				<c:when test="${position.count==1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}"  style="display:inline;margin-left: 162px;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
				<c:when test="${position.count!=1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" style="margin-left: 20px;" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			<div id="titles_div">
				<c:forEach var="i" items="${titles}">												
					<p id="title_para_${i.language.type}" style="display:none;">
						<label class="centerlabel">${i.language.name} <spring:message code="bill.title" text="Title"/></label>
						<textarea rows="2" cols="50" class="title" id="title_text_${i.language.type}" name="title_text_${i.language.type}">${i.text}</textarea>
						<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>	

	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.contentDrafts" text="Drafts of Bill" /></label></legend>
			<c:forEach var="i" items="${contentDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${position.count==1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}"  style="display:inline;margin-left: 162px;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
				<c:when test="${position.count!=1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" style="margin-left: 20px;" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			<div id="contentDrafts_div">
				<c:forEach var="i" items="${contentDrafts}">
					<p id="contentDraft_para_${i.language.type}" style="display:none;">
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.contentDraft" text="Draft"/></label>
						<textarea class="wysiwyg contentDraft" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}">${i.text}</textarea>
						<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<div>
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statementOfObjectAndReasonDrafts" text="Statement of Object & Reason" /></label></legend>
			<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${position.count==1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}"  style="display:inline;margin-left: 162px;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
				<c:when test="${position.count!=1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" style="margin-left: 20px;" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			<div id="statementOfObjectAndReasonDrafts_div">
				<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}">
					<p id="statementOfObjectAndReasonDraft_para_${i.language.type}" style="display:none;">
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statementOfObjectAndReasonDraft" text="Statement of Object & Reason"/></label>
						<textarea class="wysiwyg statementOfObjectAndReasonDraft" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}">${i.text}</textarea>
						<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	
	<p>
	<input type="button" id="financialMemorandumDrafts_button" class="button" value="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'/>"/>
	<div id="financialMemorandumDrafts_div"  style="display:none;">
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.financialMemorandumDrafts" text="Financial Memorandums" /></label></legend>
			<c:forEach var="i" items="${financialMemorandumDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${position.count==1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}"  style="display:inline;margin-left: 162px;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
				<c:when test="${position.count!=1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" style="margin-left: 20px;" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			<div>
				<c:forEach var="i" items="${financialMemorandumDrafts}">
					<p id="financialMemorandumDraft_para_${i.language.type}" style="display:none;">
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.financialMemorandumDraft" text="Financial Memorandum"/></label>
						<textarea class="wysiwyg financialMemorandumDraft" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
						<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	</p>	
	
	<p>
	<input type="button" id="statutoryMemorandumDrafts_button" class="button" value="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'/>"/>
	<div id="statutoryMemorandumDrafts_div" style="display:none;">
		<fieldset>
			<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.statutoryMemorandumDrafts" text="Statutory Memorandums" /></label></legend>
			<c:forEach var="i" items="${statutoryMemorandumDrafts}" varStatus="position">
			<c:choose>
				<c:when test="${position.count==1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}"  style="display:inline;margin-left: 162px;">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
					</a>
				</c:when>
				<c:when test="${position.count!=1 and i.language.type!=defaultBillLanguage}">
					<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}">
						<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" style="margin-left: 20px;" />
					</a>
				</c:when>
			</c:choose>					
			</c:forEach>
			<div>
				<c:forEach var="i" items="${statutoryMemorandumDrafts}">
					<p id="statutoryMemorandumDraft_para_${i.language.type}" style="display:none;">
						<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.statutoryMemorandumDraft" text="Statutory Memorandum"/></label>
						<textarea class="wysiwyg statutoryMemorandumDraft" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
						<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
						<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
					</p>
				</c:forEach>
			</div>
		</fieldset>
	</div>
	</p>
	
	<c:if test="${selectedDeviceTypeForBill=='bills_government'}">
	<p>
	<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
	<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg drafts"></form:textarea>
	<form:errors path="opinionSoughtFromLawAndJD" />
	</p>
	<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
	<p>
	<label class="small"><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
	<input id="formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" readonly="readonly">
	<input id="setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
	</p>
	</c:if>
	</c:if>		
	
	<c:if test="${not empty sectionofficer_remark and internalStatusType=='bill_final_rejection'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
			<form:textarea path="remarks" cssClass="wysiwyg" readonly="true"></form:textarea>
		</p>
	</c:if>
	
	<c:if test="${internalStatusType == 'bill_final_rejection'}">
	<p>
	<label class="wysiwyglabel"><spring:message code="bill.rejectionReason" text="Rejection reason"/></label>
	<form:textarea path="rejectionReason" cssClass="wysiwyg"></form:textarea>
	</p>
	</c:if>
	</div>
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${memberStatusType=='bill_complete' or memberStatusType=='bill_incomplete'}">
			<p class="tright">
			<security:authorize access="hasAnyRole('BIS_CLERK')">	
				<input id="submitbill" type="button" value="<spring:message code='bill.submitbill' text='Submit Bill'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
				<input id="sendforapproval" type="button" value="<spring:message code='bill.sendforapproval' text='Send For Approval'/>" class="butDef">
				</c:if>
				<input id="submitbill" type="button" value="<spring:message code='bill.submitbill' text='Submit bill'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>			
			</p>
		</c:when>	
		<c:otherwise>
			<p class="tright">
				<security:authorize access="hasAnyRole('BIS_CLERK')">	
					<input id="submitbill" type="button" value="<spring:message code='bill.submitbill' text='Submit bill'/>" class="butDef" disabled="disabled">				
				</security:authorize>			
				<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
					<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
					<input id="sendforapproval" type="button" value="<spring:message code='bill.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
					</c:if>
					<input id="submitbill" type="button" value="<spring:message code='bill.submitbill' text='Submit bill'/>" class="butDef" disabled="disabled">
					<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef" disabled="disabled">
				</security:authorize>				
			</p>
		</c:otherwise>
		</c:choose>
		
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
	<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input type="hidden" name="currentHouseType" value="${currentHouseType}"/>
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
</form:form>

<%-- <input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden"> --%>
<input id="currentStatus" value="${internalStatusType }" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<%-- <input id="departmentSelected" value="${ departmentSelected}" type="hidden"> --%>
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="selectedBillType" value="${selectedBillType}" type="hidden">
<input id="selectedBillKind" value="${selectedBillKind}" type="hidden">
<input id="selectedIntroducingHouseType" value="${selectedIntroducingHouseType}" type="hidden">
<input id="typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />
<input type="hidden" id="typeOfSelectedBillType" value="${typeOfSelectedBillType}" />
<input type="hidden" id="referredActYearLabel" value="<spring:message code="bill.referredActYear" text="Year"/>">
<input type="hidden" id="dereferActWarningMessage" value="<spring:message code="dereferActWarningMessage" text="Do you really want to de-refer this act?"/>">
<input type="hidden" id="referredOrdinanceYearLabel" value="<spring:message code="bill.referredOrdinanceYear" text="Year"/>">
<input type="hidden" id="dereferOrdinanceWarningMessage" value="<spring:message code="dereferOrdinanceWarningMessage" text="Do you really want to de-refer this ordinance?"/>">
<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='bill.client.prompt.submit' text='Do you want to submit the bill.'></spring:message>" type="hidden">
<input id="billCount" value="${billCount}" type="hidden">
<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
<input id="extrasubmissionMsg" value="<spring:message code='bill.client.prompt.submit' text='The limit of 5 bill is Exceeded ,Do you still want to submit the bill'></spring:message>" type="hidden">
</div>
</div>
<ul id="contextMenuItems" style="width: 200px; list-style-type: none; list-style-position: inside;">
	<li><a href="#dereferAct" class="edit"><spring:message code="bill.dereferact" text="De-Refer Act"></spring:message></a></li>
	<li><a href="#dereferOrdinance" class="edit"><spring:message code="bill.dereferordinance" text="De-Refer Ordinance"></spring:message></a></li>
</ul>
<div id="referringActResultDiv" style="display:none;">
</div>
<div id="referringOrdinanceResultDiv" style="display:none;">
</div>
</body>
</html>