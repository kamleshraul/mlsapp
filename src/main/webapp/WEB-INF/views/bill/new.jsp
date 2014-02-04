<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title><spring:message code="bill" text="Bill Information System"/></title>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
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
	
	<script type="text/javascript">	
		//this is for autosuggest
		function split( val ) {
			return val.split( /,\s*/ );
		}	
		function extractLast( term ) {
			return split( term ).pop();
		}	
		var controlName=$(".autosuggestmultiple").attr("id");
		var primaryMemberControlName=$(".autosuggest").attr("id");
		
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
		
		function loadMinistries(session){
			$.get('ref/session/'+session+'/ministries',function(data){
				if(data.length>0){
					var minsitryText="<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>";
					for(var i=0;i<data.length;i++){
						minsitryText+="<option value='"+data[i].id+"'>"+data[i].name;				
					}
					$("#ministry").empty();
					$("#ministry").html(minsitryText);
					loadGroup(data[i].id);
				}else{
					$("#ministry").empty();
					$("#department").empty();				
					$("#subDepartment").empty();				
				}
			});
		}
		
		$(document).ready(function(){		
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
			
			/**** Auto Suggest(member login)- Member ****/		
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
			
			/**** allow refer act & ordinance as per bill type ****/
			if($('#typeOfSelectedBillType').val()=='') {
				$('#referredActDiv').hide();
				$('#referredOrdinanceDiv').hide();
			} else if($('#typeOfSelectedBillType').val()=='original') {
				$('#referredActDiv').hide();
				$('#referredOrdinanceDiv').hide();
			} else if($('#typeOfSelectedBillType').val()=='replace_ordinance'){
				$('#referredOrdinanceDiv').show();
				$('#referredActDiv').hide();
			}else{
				$('#referredActDiv').show();
				$('#referredOrdinanceDiv').show();
			}			
			
			$('#billType').change(function() {
				$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
					if(data!=undefined || data!='') {
						if(data=='original') {
							$('#referredActDiv').hide();
							$('#referredOrdinanceDiv').hide();
						} else if(data=='replace_ordinance'){
							$('#referredOrdinanceDiv').show();
							$('#referredActDiv').hide();
						}else{
							$('#referredActDiv').show();
							$('#referredOrdinanceDiv').show();
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
			
			/**** Referring Act for Amendment Bill ****/
			$('#referAct').click(function() {
				referenceForBill('act');
			});
			
			/**** Referring Ordinance for Amendment Bill ****/
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
			
			/**** Right Click Menu ****/
			$(".refer").contextMenu({menu: 'contextMenuItems'},
		        function(action, el, pos) {
				if(action=='dereferAct'){
					removeReferredAct();	
				} else if(action=='dereferOrdinance'){
					removeReferredOrdinance();	
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
			
			$("#submitbill").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});				
				if($('#typeOfSelectedDeviceType').val()=='bills_government') {
					if($('#opinionSoughtFromLawAndJD').val()=="") {
						$.prompt($('opinionFromLawAndJDNotMentionedPrompt').val());
						return false;
					}					
				}				
				if($('#referredActDiv').is(':hidden')) {
					$('#referredAct').val("");
				}
				
				if($('#referredOrdinanceDiv').is(':hidden')) {
					$('#referredOrdinance').val("");
				}
					
				if($('#typeOfSelectedDeviceType').val()=='bills_government') {
					if($('#recommendationFromGovernor').val()=="" && $('#recommendationFromPresident').val()=="") {
						$.prompt($('#recommendationFromGovernorOrPresidentNotNeededPrompt').val(),{
							buttons: {Ok:true, Cancel:false}, callback: function(u){
					        if(u){
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
					        }
						}});
					}					
				} else {
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
				}						
				return false;
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
							$('#referredActPara').hide();
							$('#referActPara').show();
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
							$('#referredOrdinancePara').hide();
							$('#referOrdinancePara').show();
				   		}     						
					}
				});
			}				
		}
		
		function referenceForBill(refType){			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });				
			$.get('bill/referAct/init?action='+refType,function(data){
				$.unblockUI();	
				$("#referringActOrdinanceResultDiv").empty();
				$("#referringActOrdinanceResultDiv").html(data);					
				$("#referringActOrdinanceResultDiv").show();				
				$("#billDiv").hide();				
			},'html');
		}
	</script>	
</head>
<body>
	<div class="fields clearfix watermark">
	<div id="billDiv">
		<form:form action="bill" method="POST" modelAttribute="domain">
		<%@ include file="/common/info.jsp" %>
		<div>
		<h2><spring:message code="bill.new.heading" text="Enter Bill Details"/>	
		</h2>
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
			<input id="originalType" name="originalType" value="${deviceTypeForBill}" type="hidden">			
			<form:errors path="type" cssClass="validationError"/>		
		</p>	
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
		<p>
			<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
			<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
			<form:errors path="primaryMember" cssClass="validationError"/>	
			<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
			<label class="small"><spring:message code="bill.primaryMemberConstituency" text="Constituency"/>*</label>
			<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">	
			</c:if>
		</p>
		</security:authorize>
		<security:authorize access="hasAnyRole('BIS_CLERK')">		
		<p>
			<label class="small"><spring:message code="bill.primaryMember" text="Primary Member"/>*</label>
			<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
			<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
			<form:errors path="primaryMember" cssClass="validationError"/>		
		</p>	
		</security:authorize>
		
		<p>
			<label class="small"><spring:message code="bill.ministry" text="Ministry"/></label>
			<form:select path="ministry" id="ministry" class="sSelect">
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
			</form:select>
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
		
		<p>
			<label class="small"><spring:message code="bill.billType" text="Bill Type"/></label>
			<form:select id="billType" class="sSelect" path="billType">
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
			</form:select>	
			<label class="small"><spring:message code="bill.billKind" text="Bill Kind"/></label>
			<form:select id="billKind" class="sSelect" path="billKind">
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
			</form:select>					
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
			<form:errors path="introducingHouseType" cssClass="validationError"/>				
		</p>
		</c:if>
		
		<c:if test="${selectedDeviceTypeForBill != 'bills_government'}">
		<p>
			<label class="centerlabel"><spring:message code="bill.supportingMembers" text="Supporting Members"/></label>
			<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
			<%-- <label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="bill.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label> --%>										
			<c:if test="${!(empty supporingMembers)}">		
			<select  name="selectedSupportingMembers" multiple="multiple">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" class="${i.member.getFullname()}"></option>
			</c:forEach>		
			</select>
			</c:if>
			<form:errors path="supportingMembers" cssClass="validationError"/>	
		</p>
		</c:if>
		
		<h2></h2>
		
		<div id="referredActDiv">
			<c:choose>	
			<c:when test="${not empty referredAct}"><c:set var="displayReferActLink" value="none"/></c:when>
			<c:otherwise><c:set var="displayReferActLink" value="inline"/></c:otherwise>		
			</c:choose>
			<p id="referActPara" style="display: ${displayReferActLink};">
				<a href="#" id="referAct" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referAct" text="Refer Act"></spring:message></a>
			</p>		
			<c:choose>
			<c:when test="${empty referredAct}"><c:set var="displayReferredAct" value="none"/></c:when>
			<c:otherwise><c:set var="displayReferredAct" value="inline"/></c:otherwise>		
			</c:choose>	
			<p id="referredActPara" style="display: ${displayReferredAct};">
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
				<form:errors path="referredAct" cssClass="validationError" />
			</p>
		</div>	
		
		<div id="referredOrdinanceDiv" style="margin-top:10px;">
			<c:choose>	
			<c:when test="${not empty referredAct}"><c:set var="displayReferActLink" value="none"/></c:when>
			<c:otherwise><c:set var="displayReferActLink" value="inline"/></c:otherwise>		
			</c:choose>
			<p id="referOrdinancePara" style="display: ${displayReferActLink}">
				<a href="#" id="referOrdinance" style="margin: 0px 0px 0px 162px;"><spring:message code="bill.referOrdinance" text="Refer Ordinance"></spring:message></a>
			</p>	
			<c:choose>	
			<c:when test="${empty referredOrdinance}"><c:set var="displayReferredOrdinance" value="none"/></c:when>
			<c:otherwise><c:set var="displayReferredOrdinance" value="inline"/></c:otherwise>		
			</c:choose>	
			<p id="referredOrdinancePara" style="display: ${displayReferredOrdinance};">			
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
		
		<div style="margin-top: 20px;">
			<fieldset>
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${titles}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleTitle" id="toggleTitle_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
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
							<textarea rows="2" cols="50" class="title" id="title_text_${i.language.type}" name="title_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="title_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="title_language_id_${i.language.type}" value="${i.language.id}">						
						</p>							
						</div>								
					</c:forEach>
				</div>
			</fieldset>
		</div>	
	
		<div style="margin-top: 20px;">
			<fieldset>
				<p style="margin-bottom: -10px;">
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${contentDrafts}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleContentDraft" id="toggleContentDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
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
							<textarea class="wysiwyg contentDraft" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>						
						</div>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<div style="margin-top: 20px;">
			<fieldset>
				<p style="margin-bottom: -20px;">
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${statementOfObjectAndReasonDrafts}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleStatementOfObjectAndReasonDraft" id="toggleStatementOfObjectAndReasonDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
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
							<textarea class="wysiwyg statementOfObjectAndReasonDraft" id="statementOfObjectAndReasonDraft_text_${i.language.type}" name="statementOfObjectAndReasonDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="statementOfObjectAndReasonDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="statementOfObjectAndReasonDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
						</div>
					</c:forEach>
				</div>
			</fieldset>
		</div>					
		
		<p style="margin-left: 162px;margin-top: 20px;">
		<a href="#" id="financialMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
			<img src="./resources/images/Fmemo.jpg" title="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'></spring:message>" class="imageLink impIcons" />
		</a>	
		<a href="#" id="statutoryMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
			<img src="./resources/images/Smemo.jpg" title="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'></spring:message>" class="imageLink impIcons" />
		</a>
		</p>
		
		<div id="financialMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
			<fieldset>
				<p style="margin-bottom: -10px;">
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${financialMemorandumDrafts}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleFinancialMemorandumDraft" id="toggleFinancialMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
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
							<textarea class="wysiwyg financialMemorandumDraft" id="financialMemorandumDraft_text_${i.language.type}" name="financialMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="financialMemorandumDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="financialMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
						</div>
					</c:forEach>
				</div>
			</fieldset>
		</div>					
		
		<div id="statutoryMemorandumDrafts_div"  style="display:none; margin-top: 20px;">
			<fieldset>
				<p style="margin-bottom: -20px;">
				<c:set var="isFirstIcon" value="true"></c:set>
				<c:forEach var="i" items="${statutoryMemorandumDrafts}" varStatus="position">
				<c:choose>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon=='true'}">
						<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 165px;text-decoration: none;">
							<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
						</a>
						<c:set var="isFirstIcon" value="false"></c:set>
					</c:when>
					<c:when test="${i.language.type!=defaultBillLanguage and isFirstIcon!='true'}">
						<a href="#" class="toggleStatutoryMemorandumDraft" id="toggleStatutoryMemorandumDraft_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
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
							<textarea class="wysiwyg statutoryMemorandumDraft" id="statutoryMemorandumDraft_text_${i.language.type}" name="statutoryMemorandumDraft_text_${i.language.type}">${i.text}</textarea>
							<input type="hidden" name="statutoryMemorandumDraft_id_${i.language.type}" value="${i.id}">
							<input type="hidden" name="statutoryMemorandumDraft_language_id_${i.language.type}" value="${i.language.id}">						
						</p>
						</div>
					</c:forEach>
				</div>
			</fieldset>
		</div>
		
		<c:if test="${selectedDeviceTypeForBill=='bills_government'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
			<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg"></form:textarea>
			<form:errors path="opinionSoughtFromLawAndJD" />
		</p>
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.recommendationFromGovernor" text="Recommendation From Governor"/></label>
			<form:textarea id="recommendationFromGovernor" path="recommendationFromGovernor" cssClass="wysiwyg"></form:textarea>
			<form:errors path="recommendationFromGovernor" />
		</p>
		<p>
			<label class="wysiwyglabel"><spring:message code="bill.recommendationFromPresident" text="Recommendation From President"/></label>
			<form:textarea id="recommendationFromPresident" path="recommendationFromPresident" cssClass="wysiwyg"></form:textarea>
			<form:errors path="recommendationFromPresident" />
		</p>
		</c:if>		
		</div>
		<div class="fields">
			<h2></h2>
			<p class="tright">
			<security:authorize access="hasAnyRole('BIS_CLERK')">	
				<input id="submitbill" type="button" value="<spring:message code='bill.submitBill' text='Submit Bill'/>" class="butDef">			
			</security:authorize>
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
				<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				<c:if test="${selectedDeviceTypeForBill!='bills_government'}">
				<input id="sendforapproval" type="button" value="<spring:message code='bill.sendforapproval' text='Send For Approval'/>" class="butDef">
				</c:if>
				<input id="submitbill" type="button" value="<spring:message code='bill.submitBill' text='Submit Bill'/>" class="butDef">
				<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			</security:authorize>				
			</p>
		</div>	
		<form:hidden path="version" />
		<form:hidden path="locale"/>
		<input id="role" name="role" value="${role}" type="hidden">
		<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
		<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
			
		</form:form>
		<input id="ministrySelected" value="${ministrySelected }" type="hidden">
		<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
		<input id="selectedBillType" value="${selectedBillType}" type="hidden">
		<input id="selectedBillKind" value="${selectedBillKind}" type="hidden">
		<input id="selectedIntroducingHouseType" value="${selectedIntroducingHouseType}" type="hidden">
		<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
		<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
		<input id="submissionMsg" value="<spring:message code='bill.client.prompt.submit' text='Do you want to submit the bill'></spring:message>" type="hidden">	
		<input type="hidden" id="typeOfSelectedDeviceType" value="${selectedDeviceTypeForBill}">	
		<input type="hidden" id="typeOfSelectedBillType" value="${typeOfSelectedBillType}" />
		<input type="hidden" id="referredActYearLabel" value="<spring:message code="bill.referredActYear" text="Year"/>">
		<input type="hidden" id="referredOrdinanceYearLabel" value="<spring:message code="bill.referredOrdinanceYear" text="Year"/>">
		<input type="hidden" id="dereferActWarningMessage" value="<spring:message code="dereferActWarningMessage" text="Do you really want to de-refer this act?"/>">
		<input type="hidden" id="dereferOrdinanceWarningMessage" value="<spring:message code="dereferOrdinanceWarningMessage" text="Do you really want to de-refer this ordinance?"/>">
		<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
		<input type="hidden" id="opinionFromLawAndJDNotMentionedPrompt" value="<spring:message code="bill.opinionFromLawAndJDNotMentionedPrompt" text="Please mention opinion from law and judiciary department"/>">
		<input type="hidden" id="recommendationFromGovernorOrPresidentNotNeededPrompt" value="<spring:message code="bill.recommendationFromGovernorOrPresidentNotNeededPrompt" text="Are you sure that recommendation from governor or president is not needed?"/>">
	</div>
	</div>
	<ul id="contextMenuItems" style="width: 200px; list-style-type: none; list-style-position: inside;">
		<li><a href="#dereferAct" class="edit"><spring:message code="bill.dereferact" text="De-Refer Act"></spring:message></a></li>
		<li><a href="#dereferOrdinance" class="edit"><spring:message code="bill.dereferordinance" text="De-Refer Ordinance"></spring:message></a></li>
	</ul>
	<div id="referringActOrdinanceResultDiv" style="display:none;">
	</div>
</body>
</html>