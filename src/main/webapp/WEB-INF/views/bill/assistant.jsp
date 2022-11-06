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
		/**** Clubbing ****/
		function clubbingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="billId="+id
						+"&usergroup="+$("#currentusergroup").val()
				        +"&usergroupType="+$("#currentusergroupType").val();		
			$.get('clubentity/init?'+params,function(data){
				$.unblockUI();	
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#clubbingResultDiv").html(data);
				$("#clubbingResultDiv").show();
				$("#referencingResultDiv").hide();
				$("#assistantDiv").hide();
				$("#backToBillDiv").show();			
			},'html');
		}
		/**** Referencing ****/
		function referencingInt(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val()
	        +"&deviceType="+$("#typeOfSelectedDeviceType").val();
			$.get('refentity/init?'+params,function(data){
				$.unblockUI();			
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#referencingResultDiv").html(data);
				$("#referencingResultDiv").show();
				$("#clubbingResultDiv").hide();
				$("#assistantDiv").hide();
				$("#backToBillDiv").show();			
			},'html');
		}		
		/**** Refer Lapsed Bill ****/
		function referLapsedBill(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var params="id="+id
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val()
	        +"&deviceType="+$("#typeOfSelectedDeviceType").val();
			$.get('lapsedentity/init?'+params,function(data){
				$.unblockUI();			
				//$.fancybox.open(data,{autoSize:false,width:750,height:700});
				$("#referLapsedResultDiv").html(data);
				$("#referLapsedResultDiv").show();
				$("#referencingResultDiv").hide();
				$("#clubbingResultDiv").hide();
				$("#assistantDiv").hide();
				$("#backToBillDiv").show();			
			},'html');
		}
		/**** refresh clubbing and referencing ****/
		function refreshEdit(id){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#typeOfSelectedDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
	        +"&usergroupType="+$("#currentusergroupType").val();
			
			var resourceURL='bill/'+id+'/edit?'+parameters;
			$('a').removeClass('selected');
			//id refers to the tab name and it is used just to highlight the selected tab
			$('#'+ id).addClass('selected');
			//tabcontent is the content area where result of the url load will be displayed
			$('.tabContent').load(resourceURL,function(data){
				scrollTop();
				$.unblockUI();
			});
			$("#referencingResultDiv").hide();
			$("#clubbingResultDiv").hide();
			$("#assistantDiv").show();			
		}
		/**** load actors ****/
		function loadActors(value){		
			if(value!='-'){			
		    var sendback=$("#internalStatusMaster option[value='bill_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='bill_recommend_discuss']").text();
		    var translate=$("#internalStatusMaster option[value='bill_recommend_translation']").text();
		    var opinion_from_lawandjd=$("#internalStatusMaster option[value='bill_recommend_opinionFromLawAndJD']").text();
		    var recommendation_from_governor=$("#internalStatusMaster option[value='bill_recommend_recommendationFromGovernor']").text();
		    var recommendation_from_president=$("#internalStatusMaster option[value='bill_recommend_recommendationFromPresident']").text();		    
		    var params="bill="+$("#id").val()+"&status="+value+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
			var resourceURL='ref/bill/actors?'+params;
			$.post(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''&&data.length!=0){
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#actor").html(text);
					$("#actorDiv").show();				
					if(value==translate || value==opinion_from_lawandjd 
							|| value==recommendation_from_governor || value==recommendation_from_president) {
						$("#customStatus").val(value);
						$("#internalStatus").val($("#oldInternalStatus").val());
			    		$("#recommendationStatus").val($("#oldRecommendationStatus").val());			    		
					} else if(value==sendback || value==discuss) {
						$("#internalStatus").val($("#oldInternalStatus").val());
						$("#recommendationStatus").val(value);							
					} else {
						$("#internalStatus").val(value);
						$("#recommendationStatus").val(value);
					}	
					/**** setting level,localizedActorName ****/
					var actor1=data[0].id;
					var temp=actor1.split("#");
					$("#level").val(temp[2]);		    
					$("#localizedActorName").val(temp[3]+"("+temp[4]+")");
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					if(value==translate || value==opinion_from_lawandjd 
							|| value==recommendation_from_governor || value==recommendation_from_president) {
						$("#customStatus").val(value);
						$("#internalStatus").val($("#oldInternalStatus").val());
			    		$("#recommendationStatus").val($("#oldRecommendationStatus").val());			    		
					} else if(value==sendback || value==discuss) {
						$("#internalStatus").val($("#oldInternalStatus").val());
						$("#recommendationStatus").val(value);							
					} else {
						$("#internalStatus").val(value);
						$("#recommendationStatus").val(value);
					}
				}
			});
			}else{
				$("#actor").empty();
				$("#actorDiv").hide();				
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#customStatus").val("");
			}
		}
		/**** sub departments ****/
		function loadSubDepartments(ministry){
			$.get('ref/ministry/subdepartments?ministry='+ministry+'&session='+$('#session').val()
					,function(data){
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#subDepartment").html(subDepartmentText);			
				}else{
					$("#subDepartment").empty();
					var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
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
					$('#revised-contentDraft-file-'+currentLanguage).val("");
				}				
			});
			if($('#typeOfSelectedBillType').val()=="amending") {
				$('.revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];
					if($('#revisedAnnexureForAmendingBillPara_'+currentLanguage).css('display')=='none') {						
						$(this).val("");
						$('#revised-annexureForAmendingBill-file-'+currentLanguage).val("");
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
		
		function getIframeSelectionText(iframe) {
		  var win = iframe.contentWindow;
		  var doc = iframe.contentDocument || win.document;		  
		  if (win.getSelection) {		  
		    return win.getSelection().toString();
		  } else if (doc.selection && doc.selection.createRange) {			 
		    return doc.selection.createRange().text;
		  }
		}
		
		$(document).ready(function(){
			/**** Auto Suggest(member login)- Minister ****/
			$( "#formattedMinistry").autocomplete({
				minLength:3,			
				source:'ref/getministries?session='+$('#session').val()+'&deviceTypeId='+$('#originalType').val()
				+'&memberId='+$('#primaryMember').val(),
				select:function(event,ui){	
					$("#ministry").val(ui.item.id);								
				},
				change:function(event,ui){
					if(ui.item!=undefined) {
						var ministryVal=ui.item.id;
						console.log(ministryVal);
						if(ministryVal!=''){
							console.log(ministryVal);
							loadSubDepartments(ministryVal);						
						}else{
							//$("#department").empty();				
							$("#subDepartment").empty();				
							//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
							$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
						}
					} else {
						$("#ministry").val("");
						$( "#formattedMinistry").val("");
						//$("#department").empty();				
						$("#subDepartment").empty();				
						//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
						$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
					}					
				}
			});
			
			$('#opinionSoughtFromLawAndJD').wysiwyg({
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
			$('#opinionSoughtFromLawAndJD_para').children().filter('div.wysiwyg').addClass('fixed-width-wysiwyg');
						
			if($('#opinionSoughtFromLawAndJD').val()!=undefined
					&& $('#opinionSoughtFromLawAndJD').val()!="" 
					&& $('#opinionSoughtFromLawAndJD').val()!="<p></p>") {				
				/* if($('#isChecklistFilled').val()=="true") {					
					$('#checklist_button').hide();
					$('#checklist_div').show();
					$('#opinionSoughtFromLawAndJD_div').show();
					$('.checklist_button_hr').show();
				} else {
					$('#checklist_button').show();
				} */
				$('#checklist_button').hide();
				$('#checklist_div').show();
				$('#opinionSoughtFromLawAndJD_div').show();
				$('.checklist_button_hr').show();
			} else {
				$('#checklist_button').hide();
			}			
			
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
			
			if($('#recommendationFromGovernor').val()!=undefined
					&& $('#recommendationFromGovernor').val()!="" 
					&& $('#recommendationFromGovernor').val()!="<p></p>") {
				$('#recommendationFromGovernor_button').show();
			} else {
				$('#recommendationFromGovernor_button').hide();
			}
			
			if($('#recommendationFromPresident').val()!=undefined
					&& $('#recommendationFromPresident').val()!="" 
					&& $('#recommendationFromPresident').val()!="<p></p>") {
				$('#recommendationFromPresident_button').show();
			} else {
				$('#recommendationFromPresident_button').hide();
			}
			
			if($('#remarks').val()!=undefined
					&& $('#remarks').val()!="" 
					&& $('#remarks').val()!="<p></p>") {
				$('#remarks_div').show();
			} else {
				$('#remarks_div').hide();
			}			
			
			if($('#typeOfSelectedBillType').val() == '' || $('#typeOfSelectedBillType').val() == 'original'
					|| $('#typeOfSelectedBillType').val() == 'replace_ordinance') {				
				$('#annexuresForAmendingBill_div').hide();
			}
			
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
			} else if($('#typeOfSelectedBillType').val()=='amending' && $('#typeOfSelectedDeviceType').val()=='bills_nonofficial') {
				$('#referredActDiv').show();
				$('#referredOrdinanceDiv').hide();
			} else{
				$('#referredActDiv').show();
				$('#referredOrdinanceDiv').show();
			}
			
			/**** Schedule 7 Of Constitution ****/
			$(".viewSchedule7OfConstitution").click(function(){	
				var languageForSchedule7OfConstitution = "";
				if(this.type=='checkbox') {
					languageForSchedule7OfConstitution = $('#defaultBillLanguage').val();
				} else {
					languageForSchedule7OfConstitution = this.id.split("_")[1];
				}
				var resourceURL = 'bill/getSchedule7OfConstitution?sessionId='+$("#session").val()
						+'&deviceTypeId='+$("#type").val()+'&language='+languageForSchedule7OfConstitution;
				if(languageForSchedule7OfConstitution!="" && languageForSchedule7OfConstitution!=undefined) {
					$.get(resourceURL,function(data){
						$.fancybox.open(data, {autoSize: false, width: 800, height: 600});
					},'html');
				} else {
					$.prompt("some error!");
				}								
			});
			
			/**** Instructional Order ****/
			$(".viewInstructionalOrder").click(function(){
				$.get('bill/getInstructionalOrder?sessionId='+$("#session").val()
						+'&deviceTypeId='+$("#type").val(),function(data){
					$.fancybox.open(data, {autoSize: false, width: 800, height: 600});
				},'html');
			});
						
			$('#billType').change(function() {
				$.get('ref/getTypeOfSelectedBillType?selectedBillTypeId='+$('#billType').val(),function(data) {
					
					if(data!=undefined || data!='') {
						if(data=='' || data=='original' || data=='replace_ordinance') {
							$('#annexuresForAmendingBill_div').hide();
						} else {
							$('#annexuresForAmendingBill_div').show();
						}
						if(data=='original') {
							$('#referredActDiv').hide();
							$('#referredOrdinanceDiv').hide();
						} else if(data=='replace_ordinance'){
							$('#referredOrdinanceDiv').show();
							$('#referredActDiv').hide();
						} else if(data=='amending' && $('#typeOfSelectedDeviceType').val()=='bills_nonofficial') {
							$('#referredActDiv').show();
							$('#referredOrdinanceDiv').hide();
						} else{
							$('#referredActDiv').show();
							$('#referredOrdinanceDiv').show();
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
			
			//autosuggest for bill introducing member
			$( ".autosuggest").autocomplete({
				minLength:3,			
				source:'ref/bill/membersforintroduction?billId='+$("#id").val(),
				select:function(event,ui){		
				//alert(ui.item.id);
				$("#introducedBy").val(ui.item.id);
			}	
			});
			
			$("#actor").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    $("#level").val(temp[2]);		    
			    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    });
					
			/**** Back To Bill ****/
			$("#backToBill").click(function(){
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				$("#referLapsedResultDiv").hide();
				$("#assistantDiv").show();
				//Hide update success/failure message on coming back to bill
				$(".toolTip").hide();
			});
			/**** Ministry Changes ****/
			$("#formattedMinistry").change(function(){
				if($(this).val()==''){
					$("#ministry").val("");
					//$("#department").empty();				
					$("#subDepartment").empty();				
					//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
				}
			});
			/**** Citations ****/
			$("#viewCitation").click(function(){
				$.get('bill/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			    },'html');
			    return false;
			});	
			/**** Revisions ****/
		    $(".viewRevisions").click(function(){
		    	var thingToBeRevised = this.id.split("_")[1];		    	
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
		    /**** Internal Status Changes ****/   
		    $("#changeInternalStatus").change(function(){
		    	var value=$(this).val();
			    if(value!='-'){
			    	$('#remarks_div').show();
				    loadActors(value);				    				    		    
			    }else{
				    $("#actor").empty();
				    $("#actorDiv").hide();
				    $("#contentForIntroduction").hide();
				    $('#remarks_div').hide();
				    $("#internalStatus").val($("#oldInternalStatus").val());
				    $("#recommendationStatus").val($("#oldRecommendationStatus").val());	
				    $("#customStatus").val("");
				}		    
		    });
		    /**** Recommendation Status Changes (Status Updation Post Admission)****/   
		    $("#changeRecommendationStatus").change(function(){
		    	var value=$(this).val();
		    	if(value!='-'){	
		    		$('#contentForStatusUpdation').show();	
		    		$('#remarks_div').show();
			    	$("#recommendationStatus").val(value);
			    	$('#submit').removeAttr('disabled');
			    }else{
			    	$('#contentForStatusUpdation').hide();
			    	$('#remarks_div').hide();
			    	$('#submit').attr('disabled', 'disabled');			    	
			    	$("#internalStatus").val($("#oldInternalStatus").val());
				    $("#recommendationStatus").val($("#oldRecommendationStatus").val()); 
				}		    
		    });
		    $('#populateAvailableStatusDates').click(function() {
		    	$.get('bill/populateAvailableStatusDates?billId='+$("#id").val()+ "&statusId="+ $("#recommendationStatus").val(), function(data){
				    $.fancybox.open(data, {autoSize: false, width: 400, height:300});
			    },'html');
			    return false;
		    });
		    $('#addVotingDetail').click(function(e) {
		    	$.get('votingdetail/new?deviceId='+$("#id").val()+ "&houseType="+ $("#currentHouseType").val()
		    			+ "&houseRound="+ $("#houseRound").val()+ "&deviceType="+ $("#typeOfSelectedDeviceType").val()
		    			+ "&votingFor="+ $("#votingForPassingOfBill").val()+ "&openThroughOverlay=yes", function(data){
				    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
			    },'html');		    	
			    return false;
		    });
		    $('#editVotingDetails').click(function(e) {
		    	$.get('votingdetail/editVotingDetailsForDevice?deviceId='+$("#id").val()+ "&houseType="+ $("#currentHouseType").val()
		    			+ "&deviceType="+ $("#typeOfSelectedDeviceType").val()
		    			+ "&votingFor="+ $("#votingForPassingOfBill").val(), function(data){
				    $.fancybox.open(data, {autoSize: false, width: 1000, height:600});
			    },'html');		    	
			    return false;
		    });
		    $('#editStatusUpdation').click(function(e) {
		    	$.get('bill/editStatusUpdation?billId='+$("#id").val()+ "&houseType="+ $("#currentHouseType").val(), function(data){
				    $.fancybox.open(data, {autoSize: false, width: 850, height:600});
			    },'html');		    	
			    return false;
		    });
		    /**** Right Click Menu ****/
			$(".clubbedRefBills").contextMenu({
		        menu: 'contextMenuItems'
		    },
		        function(action, el, pos) {
				var id=$(el).attr("id");				
				if(action=='unclubbing'){
					if(id.indexOf("cq")!=-1){
					var billId=$("#id").val();
					var clubId=id.split("cq")[1];				
					$.post('clubentity/unclubbing?pId='+billId+"&cId="+clubId+"&whichDevice=bills_",function(data){
						if(data=='SUCCESS'){
						$.prompt("Unclubbing Successful");				
						}else{
							$.prompt("Unclubbing Failed");
						}		
					},'html');	
					}else{
						$.prompt("Unclubbing not allowed");
					}			
				}else if(action=='dereferencing'){
					if(id.indexOf("rq")!=-1){					
					var billId=$("#id").val();
					var refId=id.split("rq")[1];	
					var device = "";
					if($('#isActReferenced').val()=='true') {
						device = "act";
					} else {
						device=$("#typeOfSelectedDeviceType").val();
					}			
					$.post('refentity/dereferencing?pId='+billId+'&rId='+refId+'&device='+device,function(data){
						if(data=='SUCCESS'){
							$.prompt("Dereferencing Successful");				
							}else{
								$.prompt("Dereferencing Failed");
							}							
					},'html');	
					}else{
						$.prompt("Referencing not allowed");					
					}			
				}else if(action=='dereferLapsedBill'){
					if(id.indexOf("lq")!=-1){					
						var billId=$("#id").val();
						var refId=id.split("lq")[1];	
						var device=$("#typeOfSelectedDeviceType").val();
						$.post('lapsedentity/dereferlapsed?pId='+billId+'&rId='+refId+'&device='+device,function(data){
							if(data=='SUCCESS'){
								$.prompt("Derefer Lapsed Bill Successful");				
								}else{
									$.prompt("Derefer Lapsed Bill Failed");
								}							
						},'html');	
						}else{
							$.prompt("Referring Lapsed Bill not allowed");					
						}	
				}
		    });
		    
			/**** On Page Load ****/
		    /* if($("#ministrySelected").val()==''){
				$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			} */
			if($("#subDepartmentSelected").val()==''){
				$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}else{
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}	
			if($('#selectedBillType').val()=="" || $('#selectedBillType').val()==undefined){		
				$("#billType").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill type for now, this option will be useful.
				$("#billType").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
			}			
			if($('#selectedBillKind').val()=="" || $('#selectedBillKind').val()==undefined){		
				$("#billKind").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
			}else{
				//in case member doesnt want to select bill kind for now, this option will be useful.
				$("#billKind").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
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
					$('#contentDraft_FileDiv_'+currentLanguage).show();
				} else {
					$('#contentDraft_para_'+currentLanguage).hide();
					$('#contentDraft_FileDiv_'+currentLanguage).hide();
				}
			});
			/**** toggle contentDraft for given language icon ****/
			$('.toggleContentDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#contentDraft_para_'+currentLanguage).css('display')=='none') {
					$('#contentDraft_para_'+currentLanguage).show();
					$('#contentDraft_FileDiv_'+currentLanguage).show();
				} else {
					$('#contentDraft_para_'+currentLanguage).hide();
					$('#contentDraft_FileDiv_'+currentLanguage).hide();
				}
				return false;
			});
			
			/**** show annexure for only default bill language ****/
			$('.annexureForAmendingBill').each(function() {
				var currentLanguage = this.id.split("_")[2];				
				if(currentLanguage==$('#defaultBillLanguage').val()) {		
					$('#annexureForAmendingBill_para_'+currentLanguage).show();		
					$('#annexureForAmendingBill_FileDiv_'+currentLanguage).show();
				} else {
					$('#annexureForAmendingBill_para_'+currentLanguage).hide();		
					$('#annexureForAmendingBill_FileDiv_'+currentLanguage).hide();
				}
			});
			/**** toggle annexure for given language icon ****/
			$('.toggleAnnexureForAmendingBill').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#annexureForAmendingBill_para_'+currentLanguage).css('display')=='none') {
					$('#annexureForAmendingBill_para_'+currentLanguage).show();		
					$('#annexureForAmendingBill_FileDiv_'+currentLanguage).show();
				} else {
					$('#annexureForAmendingBill_para_'+currentLanguage).hide();	
					$('#annexureForAmendingBill_FileDiv_'+currentLanguage).hide();
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
			/**** toggle recommendation from governor on user demand ****/
			$('#recommendationFromGovernor_button').click(function() {
				if($('#recommendationFromGovernor_div').css('display')=='none') {
					$('#recommendationFromGovernor_div').show();
				} else {
					$('#recommendationFromGovernor_div').hide();
				}
				return false;
			});
			/**** toggle recommendation from president on user demand ****/
			$('#recommendationFromPresident_button').click(function() {
				if($('#recommendationFromPresident_div').css('display')=='none') {
					$('#recommendationFromPresident_div').show();
				} else {
					$('#recommendationFromPresident_div').hide();
				}
				return false;
			});
			/**** toggle checklist on user demand ****/
			$('#checklist_button').click(function() {
				if($('#checklist_div').css('display')=='none') {										
					if($('#opinionSoughtFromLawAndJD_div').css('display')=='none'							
							&& $('#opinionSoughtFromLawAndJD').val()!=""
							&& $('#opinionSoughtFromLawAndJD').val()!="<p></p>") {						
						$('#opinionSoughtFromLawAndJD_div').show();
						$('#checklist_div').show();
						$('.checklist_button_hr').show();
					} 
					/* else {
						$.prompt($('#opinionFromLawAndJdNotReceivedMsg').val());
					} */					
				} else {
					$('#checklist_div').hide();
					$('#opinionSoughtFromLawAndJD_div').hide();
					$('.checklist_button_hr').hide();
				}
				return false;
			});
			//----------------------revise drafts script----------------------//		
			$('.revisedTitle').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {					
					$('#revisedTitlePara_'+currentLanguage).show();
					$('#reviseTitle_icon_'+currentLanguage).attr('title',$('#unReviseTitle_text').val());
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
					$('#reviseTitle_icon_'+currentLanguage).attr('title',$('#unReviseTitle_text').val());
				} else {
					$('#revisedTitlePara_'+currentLanguage).hide();
					$('#reviseTitle_icon_'+currentLanguage).attr('title',$('#reviseTitle_text').val());
				}
				return false;
			});
			$('.revisedContentDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				var revisedContentDraftFile = $('#revised-contentDraft-file-'+currentLanguage).val();
				if(($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='')
						|| (revisedContentDraftFile!=undefined && revisedContentDraftFile!='')) {
					$('#revisedContentDraftPara_'+currentLanguage).show();
					$('#revisedContentDraft_FileDiv_'+currentLanguage).show();
					$('#reviseContentDraft_icon_'+currentLanguage).attr('title',$('#unReviseContentDraft_text').val());
				} else {
					$('#revisedContentDraftPara_'+currentLanguage).hide();
					$('#revisedContentDraft_FileDiv_'+currentLanguage).hide();
					if($("#contentDraft_text_"+currentLanguage).val()!=undefined && $("#contentDraft_text_"+currentLanguage).val()!='') {
						$(this).val($("#contentDraft_text_"+currentLanguage).val());
					}					
				}
			});
			
			$('.reviseContentDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedContentDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedContentDraftPara_'+currentLanguage).show();
					$('#revisedContentDraft_FileDiv_'+currentLanguage).show();
					$('#reviseContentDraft_icon_'+currentLanguage).attr('title',$('#unReviseContentDraft_text').val());
				} else {
					$('#revisedContentDraftPara_'+currentLanguage).hide();
					$('#revisedContentDraft_FileDiv_'+currentLanguage).hide();
					$('#reviseContentDraft_icon_'+currentLanguage).attr('title',$('#reviseContentDraft_text').val());
				}
				return false;
			});
			
			if($('#typeOfSelectedBillType').val()=="amending") {
				$('.revisedAnnexureForAmendingBill').each(function() {
					var currentLanguage = this.id.split("_")[3];	
					var revisedAnnexureForAmendingBillFile = $('#revised-annexureForAmendingBill-file-'+currentLanguage).val();
					if(($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='')
							|| (revisedAnnexureForAmendingBillFile!=undefined && revisedAnnexureForAmendingBillFile!='')) {	
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).show();
						$('#revisedAnnexureForAmendingBill_FileDiv_'+currentLanguage).show();
						$('#reviseAnnexureForAmendingBill_icon_'+currentLanguage).attr('title',$('#unReviseAnnexureForAmendingBill_text').val());
					} else {
						$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();
						$('#revisedAnnexureForAmendingBill_FileDiv_'+currentLanguage).hide();
						$(this).val($("#annexureForAmendingBill_text_"+currentLanguage).val());
					}
				});
			}
			
			$('.reviseAnnexureForAmendingBill').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedAnnexureForAmendingBillPara_'+currentLanguage).css('display')=='none') {
					$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).show();
					$('#revisedAnnexureForAmendingBill_FileDiv_'+currentLanguage).show();
					$('#reviseAnnexureForAmendingBill_icon_'+currentLanguage).attr('title',$('#unReviseAnnexureForAmendingBill_text').val());
				} else {
					$('#revisedAnnexureForAmendingBillPara_'+currentLanguage).hide();
					$('#revisedAnnexureForAmendingBill_FileDiv_'+currentLanguage).hide();
					$('#reviseAnnexureForAmendingBill_icon_'+currentLanguage).attr('title',$('#reviseAnnexureForAmendingBill_text').val());
				}
				return false;
			});
			
			$('.revisedStatementOfObjectAndReasonDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];					
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();
					$('#reviseStatementOfObjectAndReasonDraft_icon_'+currentLanguage).attr('title',$('#unReviseStatementOfObjectAndReasonDraft_text').val());
				} else {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();
					$(this).val($("#statementOfObjectAndReasonDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseStatementOfObjectAndReasonDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).show();
					$('#reviseStatementOfObjectAndReasonDraft_icon_'+currentLanguage).attr('title',$('#unReviseStatementOfObjectAndReasonDraft_text').val());
				} else {
					$('#revisedStatementOfObjectAndReasonDraftPara_'+currentLanguage).hide();
					$('#reviseStatementOfObjectAndReasonDraft_icon_'+currentLanguage).attr('title',$('#reviseStatementOfObjectAndReasonDraft_text').val());
				}
				return false;
			});
			
			$('.revisedFinancialMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseFinancialMemorandumDraft_icon_'+currentLanguage).attr('title',$('#unReviseFinancialMemorandumDraft_text').val());
				} else {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#financialMemorandumDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseFinancialMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedFinancialMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseFinancialMemorandumDraft_icon_'+currentLanguage).attr('title',$('#unReviseFinancialMemorandumDraft_text').val());
				} else {
					$('#revisedFinancialMemorandumDraftPara_'+currentLanguage).hide();
					$('#reviseFinancialMemorandumDraft_icon_'+currentLanguage).attr('title',$('#reviseFinancialMemorandumDraft_text').val());
				}
				return false;
			});
			
			$('.revisedStatutoryMemorandumDraft').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!='') {		
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseStatutoryMemorandumDraft_icon_'+currentLanguage).attr('title',$('#unReviseStatutoryMemorandumDraft_text').val());
				} else {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();
					$(this).val($("#statutoryMemorandumDraft_text_"+currentLanguage).val());
				}
			});
			
			$('.reviseStatutoryMemorandumDraft').click(function() {
				var currentLanguage = this.id.split("_")[1];				
				if($('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).css('display')=='none') {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).show();
					$('#reviseStatutoryMemorandumDraft_icon_'+currentLanguage).attr('title',$('#unReviseStatutoryMemorandumDraft_text').val());
				} else {
					$('#revisedStatutoryMemorandumDraftPara_'+currentLanguage).hide();
					$('#reviseStatutoryMemorandumDraft_icon_'+currentLanguage).attr('title',$('#reviseStatutoryMemorandumDraft_text').val());
				}
				return false;
			});
			//------------------------------------------------------------------
						
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
			
			/** Confirm whether this is money bill or not  **/
			$("#checklist_checkbox_5").click(function(e) {
				var alertMessage = "";
				var checkedBox = "";
				if($(this).is(':checked')) {
					alertMessage = $('#isMoneyBillPrompt').val();
					checkedBox = "checked";
				} else {
					alertMessage = $('#isNotMoneyBillPrompt').val();
					checkedBox = "";
				}	
				$.prompt(alertMessage,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	
	    	        } else {
	    	        	if(checkedBox=="checked") {
			        		$("#checklist_checkbox_5").removeAttr('checked');
			        		hideRelatedDiv('checklist_checkbox_5');
							return false;
						} else {
							$("#checklist_checkbox_5").attr('checked','checked');		
							$('#checklist_checkbox_5_div').show();
							return false;
						} 	        	
	    	        }
			        return false;
				}});				
			});
			
			/**** Single Updation for All Workflows ****/
			$('#submit').click(function() {		
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
				var admit=$("#internalStatusMaster option[value='bill_recommend_admission']").text();			
				var reject=$("#internalStatusMaster option[value='bill_recommend_rejection']").text();
				var nameclub=$("#internalStatusMaster option[value='bill_recommend_nameclubbing']").text();
				var translate=$("#internalStatusMaster option[value='bill_recommend_translation']").text();
				var opinion_from_lawandjd=$("#internalStatusMaster option[value='bill_recommend_opinionFromLawAndJD']").text();
				var recommendation_from_governor=$("#internalStatusMaster option[value='bill_recommend_recommendationFromGovernor']").text();
				var recommendation_from_president=$("#internalStatusMaster option[value='bill_recommend_recommendationFromPresident']").text();
				var actionForUpdation = $('#changeInternalStatus').val();				
				if(actionForUpdation==undefined || actionForUpdation=='-') {
					//validations for introduction part
					var updateToIntroduced = $("#recommendationStatusMaster option[value='bill_processed_introduced']").text();
					if($('#changeRecommendationStatus').val()==updateToIntroduced) {
						if($('#checklist_checkbox_field_2').val()=="yes" && $('#recommendationFromGovernor').val()=="") {
							$.prompt($('#recommendationFromGovernorEmptyMsg').val());
							return false;
						}
						if($('#checklist_checkbox_field_3').val()=="yes" && $('#recommendationFromPresident').val()=="") {
							$.prompt($('#recommendationFromPresidentEmptyMsg').val());
							return false;
						}					
					}	
					//validations for under consideration part
					if($('#internalStatusType').val()=='bill_processed_underConsideration') {
						if($('#recommendationStatusType').val().match("^bill_processed_toBeDiscussed")) {
							var consideredStatus = $("#recommendationStatusMaster option[value^='bill_processed_considered_']").text();
							if($('#changeRecommendationStatus').val()!=consideredStatus) {
								$.prompt($('#billNotSetUnderConsiderationMsg').val());
								return false;
							}						
						}	
						var referToJoinCommitteeStatus = $("#recommendationStatusMaster option[value^='bill_processed_referToJointCommittee']").text();
						var reReferToJoinCommitteeStatus = $("#recommendationStatusMaster option[value^='bill_processed_reReferToJointCommittee']").text();
						$.get('ref/getTypeOfSelectedBillKind?selectedBillKindId='+$('#billKind').val(), function(selectedBillKind) {
							if(selectedBillKind=='money') {
								if($('#changeRecommendationStatus').val()==referToJoinCommitteeStatus
										|| $('#changeRecommendationStatus').val()==reReferToJoinCommitteeStatus) {
									$.prompt($('#moneyBillCantBeReferredToJointCommitteeMsg').val());
									return false;
								}
							}
						});									
					}
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
					$.post($('form').attr('action'), $("form").serialize(), function(data){
						$('.tabContent').html(data);
							$('html').animate({scrollTop:0}, 'slow');
							$('body').animate({scrollTop:0}, 'slow');	
						$.unblockUI();
					});
				} else {					
					var promptMessage="";
					var operation="";
					if(actionForUpdation==translate) {
						promptMessage = $('#sendForTranslationMessage').val();
						operation = "sendForTranslation";
					} else if(actionForUpdation==opinion_from_lawandjd) {
						promptMessage = $('#sendForOpinionFromLawAndJDMessage').val();
						operation = "sendForOpinionFromLawAndJD";
					} else if(actionForUpdation==recommendation_from_governor) {
						var fieldNameForGovernorRecommendation = "checklist['isRecommendedAsPerConstitutionArticle_207_3']";
						if($("input[name='"+fieldNameForGovernorRecommendation+"']").val()=='no') {
							alert("recommendation from governor is not required.");
							return false;
						}
						promptMessage = $('#sendForRecommendationFromGovernorPrompt').val();
						operation = "sendForRecommendationFromGovernor";
					} else if(actionForUpdation==recommendation_from_president) {
						var fieldNameForPresidentRecommendation = "checklist['isRecommendedAsPerConstitutionArticle_304_b']";
						if($("input[name='"+fieldNameForPresidentRecommendation+"']").val()=='no') {
							alert("recommendation from president is not required.");
							return false;
						}
						promptMessage = $('#sendForRecommendationFromPresidentPrompt').val();
						operation = "sendForRecommendationFromPresident";
					} else if(actionForUpdation==nameclub) {
						promptMessage = $('#sendForNameclubbingPrompt').val();
						operation = "sendForNameclubbing";
					} else if(actionForUpdation==admit || actionForUpdation==reject) {
						if($('#opinionSoughtFromLawAndJD').val()=='') {
							$.prompt("Opinion is not received yet from Law & Judiciary Department. So Bill cannot be put up.");
							return false;
						}				
						/** remarks compulsory when putup for rejection **/
						var putupForRejection=$("#internalStatusMaster option[value='bill_recommend_rejection']").text();
						if($('#changeInternalStatus').val()==putupForRejection) {
							if($('#remarks').val()=="") {
								$.prompt($('#remarksCompulsoryWhenPutupForRejectionMessage').val());
								return false;
							}
						}						
						/** putup not allowed when translation is pending **/
						if($('#translationStatusType').val()=='bill_recommend_translation'
								|| $('#translationStatusType').val()=='bill_final_translation') {
							$.prompt($('#translationPendingMessage').val());
							return false;
							//promptMessage = $('#startWorkflowDespiteTranslationPendingMessage').val();
						} else {
							if($('#isAnyBillSubmittedEarierThanCurrentBillToBePutup').val()=='true') {
								promptMessage = $('#billsSubmittedEarierPendingForPutupWarning').val();
							}else {
								promptMessage = $('#startWorkflowMessage').val();
							}	
							operation = "startworkflow";
						}						
					}
					$.prompt(promptMessage,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
				        	$.post($('form').attr('action')+'?operation='+operation,  
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
				}				 
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
			
			div.fixed-width-wysiwyg {
				min-width: 400px !important;
				left: 10px !important;
				margin-top: 10px;
			}
			
			#opinionSoughtFromLawAndJD-wysiwyg-iframe {height: 400px;}			
			
			.textdraft_file {
				float: right; 
				margin: -210px 20px;
				position: relative;
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
				<form:form action="bill" method="PUT" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceTypeForBill} ${formattedNumber}	
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
						<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
						<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}">
						<%-- <select name="ministry" id="ministry" class="sSelect">
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
					<p style="display: none;">
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
							<label class="small"><spring:message code="bill.referredOrdinance" text="Referred Ordinance"></spring:message></label>
							<c:choose>
								<c:when test="${!(empty referredOrdinance)}">
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px;"><c:out value="${referredOrdinanceNumber}"></c:out></a>
									<label id="referredOrdinanceYear">(<spring:message code="bill.referredOrdinanceYear" text="Year"/>: ${referredOrdinanceYear})</label>
								</c:when>
								<c:otherwise>
									<a href="#" id="viewReferredOrdinance" style="font-size: 18px; text-decoration: none;"><c:out value="-"></c:out></a>
									<label id="referredOrdinanceYear"></label>
								</c:otherwise>
							</c:choose>
							<input type="hidden" id="referredOrdinance" name="referredOrdinance" value="${referredOrdinance}">
						</p>
					</div>
					
					<c:if test="${usergroupType=='assistant'}">		
					<p style="margin-left: 162px;">
						<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">
						<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="bill.clubbing" text="Clubbing"></spring:message></a>
						<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin-right: 20px;"><spring:message code="bill.referencing" text="Referencing"></spring:message></a>
						</c:if>						
						<a href="#" id="referLapsedBill" onclick="referLapsedBill(${domain.id});" style="margin-right: 20px;"><spring:message code="bill.referLapsed" text="Refer Lapsed Bill"></spring:message></a>
						<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin-right: 20px;"><spring:message code="bill.refresh" text="Refresh"></spring:message></a>
					</p>
					</c:if>
					
					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial'}">		
					<c:choose>	
					<c:when test="${empty parent}"><c:set var="displayParentBill" value="none"/></c:when>
					<c:otherwise><c:set var="displayParentBill" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayParentBill};">
						<label class="small"><spring:message code="bill.parentbill" text="Clubbed To"></spring:message></label>
						<a href="#" id="p${parent}" onclick="viewBillDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
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
													<a href="#" id="rq${i.number}" class="clubbedRefBills" onclick="viewActDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:when>
												<c:otherwise>
													<a href="#" id="rq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
													&nbsp;(${referencedBillsSessionAndDevice[index.count-1]})
												</c:otherwise>
											</c:choose>												
										</c:when>
										<c:otherwise>											
											<a href="#" id="rq${i.number}" class="clubbedRefBills" onclick="viewBillDetail(${i.number});" style="font-size: 18px;"><spring:message code="bill.referredBillWithoutNumber" text="Click To See"/></a>
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
					
					<!-- <h2></h2> -->
					
					<div style="margin-top: 20px;">
						<fieldset>
							<a href="#" class="viewRevisions" id="viewRevisions_titles"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForTitles' text='View Revisions for Titles'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${titles}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
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
										<label class="single-row-textarea-label">${i.language.name} <spring:message code="bill.shortTitle" text="Short Title"/></label>
										<textarea rows="1" cols="30" id="title_shortText_${i.language.type}" name="title_shortText_${i.language.type}">${i.shortText}</textarea>
									</p>
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
										<textarea class="wysiwyg contentDraft" id="contentDraft_text_${i.language.type}" name="contentDraft_text_${i.language.type}" readonly="readonly">${i.text}</textarea>
										<div class="textdraft_file" id="contentDraft_FileDiv_${i.language.type}">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="contentDraft-file-${i.language.type}" />
												<jsp:param name="filetag" value="${i.file}" />
												<jsp:param name="isUploadAllowed" value="false" />
												<jsp:param name="isRemovable" value="false" />
											</jsp:include>			
										</div>
										<input type="hidden" name="contentDraft_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="contentDraft_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<p>
										<a href="#" class="reviseContentDraft" id="reviseContentDraft_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
											<img id="reviseContentDraft_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseContentDraft' text='Revise This Content Draft'></spring:message>" class="imageLink" />
										</a>
										<c:set var="schedule7OfConstitution" value="schedule7OfConstitution_${i.language.type}"/>
										<c:if test="${not empty requestScope[schedule7OfConstitution]}">
										<a href="javascript:void(0)" id="viewSchedule7OfConstitution_${i.language.type}" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
											<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
										</a>
										</c:if>
									</p>
									<p id="revisedContentDraftPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedContentDraft" text=" Revised Draft"/></label>						
										<c:set var="revisedContentDraftText" value="revisedContentDraft_${i.language.type}"></c:set>
										<c:set var="revisedContentDraftId" value="revisedContentDraft_id_${i.language.type}"></c:set>
										<c:set var="revisedContentDraftFile" value="revisedContentDraft-file-${i.language.type}"></c:set>										
										<textarea class="wysiwyg revisedContentDraft" id="revised_contentDraft_text_${i.language.type}" name="revised_contentDraft_text_${i.language.type}">${requestScope[revisedContentDraftText]}</textarea>
										<div class="textdraft_file" id="revisedContentDraft_FileDiv_${i.language.type}">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="revised-contentDraft-file-${i.language.type}" />
												<jsp:param name="filetag" value="${requestScope[revisedContentDraftFile]}" />
												<jsp:param name="isUploadAllowed" value="true" />
												<jsp:param name="isRemovable" value="true" />
												<jsp:param name="isDeletable" value="${not empty requestScope[revisedContentDraftFile]? 'false' : 'true'}" />
											</jsp:include>			
										</div>
										<input type="hidden" name="revised_contentDraft_id_${i.language.type}" value="${requestScope[revisedContentDraftId]}">												
									</p>
									</div>
								</c:forEach>
							</div>
						</fieldset>
					</div>
					
					<form:select cssStyle="display: none;" id="sections" path="sections" items="${sections}" itemLabel="hierarchyOrder" itemValue="id"></form:select>
					
					<div id="annexuresForAmendingBill_div" style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: -10px;">
							<a href="#" class="viewRevisions" id="viewRevisions_annexuresForAmendingBill"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForAnnexuresForAmendingBill' text='View Revisions for Annexures For Amending Bill'></spring:message>" class="imageLink" />
							</a>
							<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="position">
							<c:choose>
								<c:when test="${i.language.type!=defaultBillLanguage}">
									<a href="#" class="toggleAnnexureForAmendingBill" id="toggleAnnexureForAmendingBill_${i.language.type}" style="margin-left: 20px;text-decoration: none;">
										<img src="./resources/images/ico_${i.language.type}.jpg" title="${i.language.name}" class="imageLink" />
									</a>
								</c:when>
							</c:choose>					
							</c:forEach>
							<c:if test="${not empty instructionalOrder}">
							<a href="javascript:void(0)" class="viewInstructionalOrder" style="margin-left: 20px;text-decoration: none;">
								<img src="./resources/images/instruction_icon.png" title="<spring:message code='bill.viewInstructionalOrder' text='View Instructional Order'></spring:message>" class="imageLink" />
							</a>
							</c:if>
							</p>							
							<div>
								<c:forEach var="i" items="${annexuresForAmendingBill}" varStatus="draftNumber">
									<div id="annexureForAmendingBill_para_${i.language.type}" style="display:none;">
									<p>
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.annexureForAmendingBill" text="Annexure For Amending Bill"/></label>
										<textarea class="wysiwyg annexureForAmendingBill" id="annexureForAmendingBill_text_${i.language.type}" name="annexureForAmendingBill_text_${i.language.type}" ${(empty i.text and empty i.file)?'':'readonly="readonly"'}>${i.text}</textarea>
										<div class="textdraft_file" id="annexureForAmendingBill_FileDiv_${i.language.type}">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="annexureForAmendingBill-file-${i.language.type}" />
												<jsp:param name="filetag" value="${i.file}" />
												<jsp:param name="isUploadAllowed" value="${(empty i.text and empty i.file)?'true':'false'}" />
												<jsp:param name="isRemovable" value="${(empty i.text and empty i.file)?'true':'false'}" />
											</jsp:include>		
										</div>
										<input type="hidden" name="annexureForAmendingBill_id_${i.language.type}" value="${i.id}">
										<input type="hidden" name="annexureForAmendingBill_language_id_${i.language.type}" value="${i.language.id}">						
									</p>
									<c:choose>
										<c:when test="${not empty i.text or not empty i.file}">
											<p>
												<a href="#" class="reviseAnnexureForAmendingBill" id="reviseAnnexureForAmendingBill_${i.language.type}" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
													<img id="reviseAnnexureForAmendingBill_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='bill.reviseAnnexureForAmendingBill' text='Revise This Annexure For Amending Bill'></spring:message>" class="imageLink" />
												</a>
												<c:set var="schedule7OfConstitution" value="schedule7OfConstitution_${i.language.type}"/>
												<c:if test="${not empty requestScope[schedule7OfConstitution]}">
												<a href="javascript:void(0)" id="viewSchedule7OfConstitution_${i.language.type}" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
													<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
												</a>
												</c:if>
											</p>
										</c:when>
										<c:otherwise>
											<p>
												<c:set var="schedule7OfConstitution" value="schedule7OfConstitution_${i.language.type}"/>
												<c:if test="${not empty requestScope[schedule7OfConstitution]}">
												<a href="javascript:void(0)" id="viewSchedule7OfConstitution_${i.language.type}" class="viewSchedule7OfConstitution" style="margin-left: 162px;margin-right: 20px;text-decoration: none;">
													<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
												</a>
												</c:if>
											</p>
										</c:otherwise>
									</c:choose>
									<p id="revisedAnnexureForAmendingBillPara_${i.language.type}" style="display:none;">
										<label class="wysiwyglabel">${i.language.name} <spring:message code="bill.revisedAnnexureForAmendingBill" text=" Revised Annexure For Amending Bill"/></label>						
										<c:set var="revisedAnnexureForAmendingBillText" value="revisedAnnexureForAmendingBill_${i.language.type}"></c:set>
										<c:set var="revisedAnnexureForAmendingBillId" value="revisedAnnexureForAmendingBill_id_${i.language.type}"></c:set>
										<c:set var="revisedAnnexureForAmendingBillFile" value="revisedAnnexureForAmendingBill-file-${i.language.type}"></c:set>										
										<textarea class="wysiwyg revisedAnnexureForAmendingBill" id="revised_annexureForAmendingBill_text_${i.language.type}" name="revised_annexureForAmendingBill_text_${i.language.type}">${requestScope[revisedAnnexureForAmendingBillText]}</textarea>
										<div class="textdraft_file" id="revisedAnnexureForAmendingBill_FileDiv_${i.language.type}">
											<jsp:include page="/common/file_load.jsp">
												<jsp:param name="fileid" value="revised-annexureForAmendingBill-file-${i.language.type}" />
												<jsp:param name="filetag" value="${requestScope[revisedAnnexureForAmendingBillFile]}" />
												<jsp:param name="isUploadAllowed" value="true" />
												<jsp:param name="isRemovable" value="true" />
												<jsp:param name="isDeletable" value="${not empty requestScope[revisedAnnexureForAmendingBillFile]? 'false' : 'true'}" />
											</jsp:include>			
										</div>
										<input type="hidden" name="revised_annexureForAmendingBill_id_${i.language.type}" value="${requestScope[revisedAnnexureForAmendingBillId]}">												
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
					<a href="#" id="financialMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Fmemo.jpg" title="<spring:message code='bill.financialMemorandumDrafts' text='Financial Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>	
					<a href="#" id="statutoryMemorandumDrafts_button" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Smemo.jpg" title="<spring:message code='bill.statutoryMemorandumDrafts' text='Statutory Memorandums'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="checklist_button" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/checklist.jpg" title="<spring:message code='bill.checklist' text='Checklist'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="recommendationFromGovernor_button" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Grec.jpg" title="<spring:message code='bill.recommendationFromGovernor' text='Recommendation From Governor'></spring:message>" class="imageLink impIcons" />
					</a>
					<a href="#" id="recommendationFromPresident_button" style="margin-right: 20px;text-decoration: none;">
						<img src="./resources/images/Prec.jpg" title="<spring:message code='bill.recommendationFromPresident' text='Recommendation From President'></spring:message>" class="imageLink impIcons" />
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
					
					<div id="recommendationFromGovernor_div"  style="display:none;">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromGovernor" text="Recommendation From Governor"/></label>
						<form:textarea id="recommendationFromGovernor" path="recommendationFromGovernor" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromGovernor" />
					</p>
					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial' and not empty dateOfRecommendationFromGovernor}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromGovernor" text="Date Of Recommendation From Governor"/></label>
					<input id="formattedDateOfRecommendationFromGovernor" name="formattedDateOfRecommendationFromGovernor" value="${formattedDateOfRecommendationFromGovernor}" class="sText" readonly="readonly">
					<input id="setDateOfRecommendationFromGovernor" name="setDateOfRecommendationFromGovernor" type="hidden"  value="${dateOfRecommendationFromGovernor}">	
					</p>
					</c:if>		
					</div>					
					
					<div id="recommendationFromPresident_div"  style="display:none;">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromPresident" text="Recommendation From President"/></label>
						<form:textarea id="recommendationFromPresident" path="recommendationFromPresident" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromPresident" />
					</p>
					<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial' and not empty dateOfRecommendationFromPresident}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromPresident" text="Date Of Recommendation From President"/></label>
					<input id="formattedDateOfRecommendationFromPresident" name="formattedDateOfRecommendationFromPresident" value="${formattedDateOfRecommendationFromPresident}" class="sText" readonly="readonly">
					<input id="setDateOfRecommendationFromPresident" name="setDateOfRecommendationFromPresident" type="hidden"  value="${dateOfRecommendationFromPresident}">	
					</p>
					</c:if>	
					</div>						
					<div>
						<h2 class="checklist_button_hr" style="display: none;"></h2>
						<div id="opinionSoughtFromLawAndJD_div" style="display: none;float: left;margin-top: 10px;">
							<p id="opinionSoughtFromLawAndJD_para">
								<label><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
								<c:if test="${selectedDeviceTypeForBill=='bills_government' or not empty dateOfOpinionSoughtFromLawAndJD}">
								<!-- <div id="opinionSoughtFromLawAndJD_FileDiv" style="float: right;margin-left: 20px;margin-bottom: 5px;margin-right: 20px;position: relative;"> -->
								<jsp:include page="/common/file_load.jsp">
									<jsp:param name="fileid" value="opinionSoughtFromLawAndJDFile" />
									<jsp:param name="filetag" value="${domain.opinionSoughtFromLawAndJDFile}" />
									<jsp:param name="isRemovable" value="false" />
									<jsp:param name="isUploadAllowed" value="false" />									
								</jsp:include>							
								<!-- </div> -->
								</c:if>
								<br/>
								<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="true"></form:textarea>
								<form:errors path="opinionSoughtFromLawAndJD" />
							</p>											
							<c:if test="${selectedDeviceTypeForBill=='bills_nonofficial' and not empty dateOfOpinionSoughtFromLawAndJD}">
							<p>
							<label><spring:message code="bill.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
							<br/>
							<input id="formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" style="margin-left: 10px !important;" readonly="readonly">
							<input id="setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
							</p>
							</c:if>							
						</div>
						
						<div id="checklist_div"  style="display: none;margin-left: 420px;margin-top: 10px;">					
							<fieldset>
							<label><spring:message code="bill.checklist" text="Checklist" /></label>							
							<a href="#" class="viewRevisions" id="viewRevisions_checklist" style="margin-left: 20px;margin-bottom: 5px;margin-right: 20px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='bill.viewRevisionsForChecklist' text='View Revisions for Checklist'></spring:message>" class="imageLink" />
							</a>
							<a href="#" id="viewSchedule7OfConstitution_${defaultBillLanguage}" class="viewSchedule7OfConstitution" style="margin-right: 20px;text-decoration: none;">
								<img id="s7C" src="./resources/images/s7C.jpg" title="<spring:message code='bill.viewSchedule7OfConstitution' text='View Schedule 7 Of Constitution'></spring:message>" class="imageLink" />
							</a>
							<br/>							
							<table style="width: 500px; border: 2px solid black;">
								<tbody>
									<tr id="checklistQuestion1" style="border-bottom: 2px solid black;">
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
															<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="checklist_checkbox_1" style="margin: 10px; margin-left: 10px;">
															<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_1" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_1']" />
														</div>																																			
														<div style="display: none;" id="checklist_checkbox_1_div">
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
															<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="checklist_checkbox_2" style="margin: 10px; margin-left: 10px;">
															<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_2" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_3']" />
														</div>																							
														<div style="display: none;" id="checklist_checkbox_2_div">
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
															<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_3" style="margin: 10px; margin-left: 10px;">
															<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_3" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_304_b']" />
														</div>
														<div style="display: none;" id="checklist_checkbox_3_div">
															<label><spring:message code="bill.checklistQuestion1.6" text="if yes, please mention sections"/></label>
															<br/>
															<form:textarea class="sTextarea" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_304_b']" rows="2" cols="50" style="margin: 10px;" />
														</div>
													</td>
												</tr>
											</table>											
										</td>
									</tr>
									<tr id="checklistQuestion2" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[2]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">												
												<label style="width:400px;"><spring:message code="bill.checklistQuestion2.1" text="is bill in scope of state legislature?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes viewSchedule7OfConstitution" id="checklist_checkbox_4" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_4" type="hidden" path="checklist['isInScopeOfStateLegislature']" />
											</div>
											
											<div style="display: none;" id="checklist_checkbox_4_div">
												<label><spring:message code="bill.checklistQuestion2.2" text="if yes, please also mention related schedule issues"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['issuesInRelatedScheduleForScopeOfStateLegislature']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="checklistQuestion3" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[3]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion3.1" text="is this bill a money bill?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_5" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_5" type="hidden" path="checklist['isMoneyBill']" />
											</div>
											
											<div style="display: none;" id="checklist_checkbox_5_div">
												<label><spring:message code="bill.checklistQuestion3.2" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForBeingMoneyBill']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="checklistQuestion4" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[4]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion4.1" text="is this bill a financial bill as per constitution article 207 (1)?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_6" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_6" type="hidden" path="checklist['isFinancialBillAsPerConstitutionArticle_207_1']" />
											</div>
											
											<div style="display: none;" id="checklist_checkbox_6_div">
												<label><spring:message code="bill.checklistQuestion4.2" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForFinancialBillAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>								
									<tr id="checklistQuestion5" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[5]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion5.1" text="are amendments for amending bill as per scope of original act?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_7" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_7" type="hidden" path="checklist['areAmendmentsForAmendingBillAsPerScopeOfOriginalAct']" />
											</div>																					
										</td>
									</tr>
									<tr id="checklistQuestion6" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[6]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion6.1" text="is statutory memorandum mandatory?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_8" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_8" type="hidden" path="checklist['isStatutoryMemorandumMandatory']" />
											</div>		
																			
											<div style="display: none;" id="checklist_checkbox_8_div">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion6.2" text="is statutory memorandum as per rules?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_9" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_9" type="hidden" path="checklist['isStatutoryMemorandumAsPerRules']" />
											</div>
											
											<div style="display: none;" id="checklist_checkbox_9_div">
												<label><spring:message code="bill.checklistQuestion6.3" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForStatutoryMemorandum']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="checklistQuestion7" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[7]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion7.1" text="is financial memorandum mandatory?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_10" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_10" type="hidden" path="checklist['isFinancialMemorandumMandatory']" />
											</div>	
																				
											<div style="display: none;" id="checklist_checkbox_10_div">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion7.2" text="is financial memorandum as per rules?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_11" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_11" type="hidden" path="checklist['isFinancialMemorandumAsPerRules']" />
											</div>
											
											<div style="display: none;" id="checklist_checkbox_11_div">
												<label><spring:message code="bill.checklistQuestion7.3" text="if yes, please mention sections"/></label>
												<br/>
												<form:textarea class="sTextarea" path="checklist['sectionsForFinancialMemorandum']" rows="2" cols="50" style="margin: 10px;" />
											</div>
										</td>
									</tr>
									<tr id="checklistQuestion8" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[8]})</td>
										<td>
											<div style="border: 0px 0px 1px 0px dotted #000000;">
												<label style="width:400px;"><spring:message code="bill.checklistQuestion8.1" text="is statement of object and reason complete?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_12" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_12" type="hidden" path="checklist['isStatementOfObjectAndReasonComplete']" />
											</div>																		
										</td>
									</tr>
									<tr id="checklistQuestion9" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[9]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">											
												<label style="width:400px;"><spring:message code="bill.checklistQuestion9.1" text="is law & judiciary department in agreement with above opinions on issues 1, 2, 6 & 7?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_13" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_13" type="hidden" path="checklist['isLawAndJudiciaryDepartmentInAgreementWithOpinions']" />
											</div>																			
										</td>
									</tr>
									<tr id="checklistQuestion10" style="border-bottom: 2px solid black;">
										<td style="vertical-align: top;padding-top: 13px;padding-left: 3px;">(${checklistSerialNumbers[10]})</td>
										<td style="vertical-align: top;padding-top: 13px;">
											<div style="border: 0px 0px 1px 0px dotted #000000;">										
												<label style="width:400px;"><spring:message code="bill.checklistQuestion10.1" text="are there any recommendations on subject-matter of this bill by sub-legislation committee?"/></label>
												<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_14" style="margin: 10px; margin-left: 38px;">
												<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_14" type="hidden" path="checklist['isRecommendedOnSubjectMatterBySubLegislationCommittee']" />
											</div>																				
										</td>
									</tr>
								</tbody>
							</table>
							</fieldset>
						</div>	
						<h2 class="checklist_button_hr" style="display: none;"></h2>									
					</div>
					
					<div style="margin-top: 20px;">					
					<c:if test="${translationStatusType!='translation_notsend' and not empty formattedTranslationStatus and memberStatusType=='bill_submit'}">
					<p id="translationStatusDiv">
						<label class="small"><spring:message code="bill.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${opinionFromLawAndJDStatusType!='opinionFromLawAndJD_notsend' and not empty formattedOpinionFromLawAndJDStatus and selectedDeviceTypeForBill=='bills_nonofficial'}">
					<p id="opinionFromLawAndJDStatusStatusDiv">
						<label class="small"><spring:message code="bill.currentOpinionFromLawAndJDStatus" text="Current Opinion From Law And JD Status"/></label>
						<input id="formattedOpinionFromLawAndJDStatus" value="${formattedOpinionFromLawAndJDStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${recommendationFromGovernorStatusType!='recommendationFromGovernor_notsend' and not empty formattedRecommendationFromGovernorStatus and selectedDeviceTypeForBill=='bills_nonofficial'}">
					<p id="recommendationFromGovernorStatusDiv">
						<label class="small"><spring:message code="bill.currentRecommendationFromGovernorStatus" text="Current Recommendation From Governor Status"/></label>
						<input id="formattedRecommendationFromGovernorStatus" value="${formattedRecommendationFromGovernorStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${recommendationFromPresidentStatusType!='recommendationFromPresident_notsend' and not empty formattedRecommendationFromPresidentStatus and selectedDeviceTypeForBill=='bills_nonofficial'}">
					<p id="recommendationFromPresidentStatusDiv">
						<label class="small"><spring:message code="bill.currentRecommendationFromPresidentStatus" text="Current Recommendation From President Status"/></label>
						<input id="formattedRecommendationFromPresidentStatus" value="${formattedRecommendationFromPresidentStatus}" type="text" readonly="readonly">
					</p>
					</c:if>	
				
					<c:if test="${internalStatusPriority < statusUpdationPriority}">
						<p id="internalStatusDiv">
							<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
							<input id="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
						</p>	
						<%-- <c:if test="${(internalStatusType=='bill_submit') || (internalStatusType=='bill_system_assistantprocessed')
							|| ((internalStatusType=='bill_recommend_admission' or internalStatusType=='bill_recommend_rejection') && bulkedit=='yes')}"> --%>
						<p id="changeInternalStatusPara">
							<label class="small"><spring:message code="bill.putupfor" text="Put up for"/></label>
							<select id="changeInternalStatus" class="sSelect">
								<option value="-"><spring:message code='please.select' text='Please Select'/></option>
								<c:forEach items="${internalStatuses}" var="i">
									<c:if test="${(i.type!='bill_recommend_sendback'&&i.type!='bill_recommend_discuss') }">
										<c:choose>
											<c:when test="${i.id==internalStatusSelected}">
												<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
											</c:when>
											<c:otherwise>
												<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>
							</select>		
							<select id="internalStatusMaster" style="display:none;">
							<c:forEach items="${internalStatuses}" var="i">
							<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
							</c:forEach>
							</select>	
							<form:errors path="internalStatus" cssClass="validationError"/>	
						</p>
						<p id="actorDiv">
						<label class="small"><spring:message code="bill.nextactor" text="Next Users"/></label>
						<select id="actor" name="actor" class="sSelect">
							<c:forEach var="i" items="${actors}">
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
							</c:forEach>					
						</select>	
						<input type="hidden" id="localizedActorName" name="localizedActorName">
						</p>
					</c:if>
					
					<c:if test="${internalStatusPriority >= statusUpdationPriority}">
						<p id="recommendationStatusDiv">
							<label class="small"><spring:message code="bill.currentStatus" text="Current Status"/></label>
							<input id="formattedRecommendationStatus" value="${formattedRecommendationStatus}" type="text" readonly="readonly">
						</p>	
						<p>
							<label class="small"><spring:message code="bill.currentHouseRound" text="Current House Round"/></label>
							<input id="formattedCurrentHouseRound" class="sInteger" value="${formattedCurrentHouseRound}" type="text" readonly="readonly">
							<input type="hidden" id="currentHouseRound" name="currentHouseRound" value="${currentHouseRound}">
						</p>					
						<p>
							<label class="small"><spring:message code="bill.currentExpectedStatusDate" text="Current Expected Status Date"/></label>
							<input id="currentExpectedStatusDate" name="currentExpectedStatusDate" value="${currentExpectedStatusDate}" class="datemask sText" readonly="readonly"/>							
						</p>
						<p>
							<label class="small"><spring:message code="bill.currentStatusDate" text="Current Status Date"/></label>
							<input id="currentStatusDate" name="currentStatusDate" value="${currentStatusDate}" class="datemask sText" readonly="readonly"/>							
						</p>
						<c:if test="${internalStatusType!='bill_processed_toBeIntroduced'}">
							<p>
								<label class="small"><spring:message code="bill.introducedBy" text="Introduced By"/></label>
								<input type="text" id="introducedByMemberName" class="sText" name="introducedByMemberName" value="${introducedByMember.getFullname()}" readonly="readonly">
								<input id="introducedBy" name="introducedBy" value="${introducedByMember.getId()}" type="hidden" />
							</p>
						</c:if>
						<p id="changeRecommendationStatusPara">
							<label class="small"><spring:message code="bill.putupfor" text="Put up for"/></label>
							<select id="changeRecommendationStatus" class="sSelect">
								<option value="-"><spring:message code='please.select' text='Please Select'/></option>
								<c:forEach items="${recommendationStatuses}" var="i">
									<c:choose>
										<c:when test="${i.id==recommendationStatusSelected}">
											<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
										</c:when>
										<c:otherwise>
											<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>	
							<a id="addVotingDetail" href="javascript:void(0);"><spring:message code="bill.addVotingDetail" text="Add Voting Details"/></a>	
							<a id="editVotingDetails" href="javascript:void(0);" style="display: inline; margin-left: 20px;"><spring:message code="bill.editVotingDetails" text="Edit Voting Details"/></a>
							<a id="editStatusUpdation" href="javascript:void(0);" style="display: inline; margin-left: 20px;"><spring:message code="bill.editStatusUpdation" text="Edit Status Updation"/></a>
							<select id="recommendationStatusMaster" style="display:none;">
							<c:forEach items="${recommendationStatuses}" var="i">
							<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
							</c:forEach>
							</select>	
							<form:errors path="recommendationStatus" cssClass="validationError"/>	
						</p>
						<div id="contentForStatusUpdation" style="display:none;">
							<c:if test="${internalStatusType=='bill_processed_toBeIntroduced'}">
								<p>
									<label class="small"><spring:message code="bill.introducedBy" text="Introduced By"/></label>
									<input type="text" id="introducedByMemberName" class="autosuggest sText" name="introducedByMemberName" value="${introducedByMember.getFullname()}">
									<input id="introducedBy" name="introducedBy" value="${introducedByMember.getId()}" type="hidden" />
								</p>
							</c:if>
							<p>
								<label class="small"><spring:message code="bill.houseRound" text="House Round"/></label>
								<form:select id="houseRound" class="sSelect" path="houseRound" items="${houseRoundVOs}" itemLabel="name" itemValue="value"/>							
							</p>
							<p>
								<label class="small"><spring:message code="bill.expectedStatusDate" text="Expected Status Date"/></label>								
								<input id="expectedStatusDate" name="expectedStatusDate" class="datemask sText"/>							
							</p>
							<p>
								<label class="small"><spring:message code="bill.statusDate" text="Status Date"/></label>
								<form:input id="statusDate" path="statusDate" value="${statusDate}" class="datemask sText"/>							
							</p>	
							<a id="populateAvailableStatusDates" href="#"><spring:message code="bill.populateAvailableStatusDates" text="Refer Available Status Dates"/></a>					
						</div>
					</c:if>				
									
					<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					</div>
					
					<div id="remarks_div">
					<p>
						<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
					</p>					
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
						<textarea id="remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>						
					</p>
					</div>	
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</p>
					</div>
					<form:hidden path="id"/>
					<form:hidden path="locale"/>
					<form:hidden path="version"/>					
					<form:hidden path="admissionDate"/>
					<form:hidden path="rejectionDate"/>
					<form:hidden path="isIncomplete"/>
					<form:hidden path="remarksForTranslation"/>
					<input type="hidden" name="currentHouseType" value="${currentHouseType}"/>
					
					<form:hidden path="file"/>
					<form:hidden path="fileIndex"/>	
					<form:hidden path="fileSent"/>
					<input id="level" name="level" value="${level}" type="hidden">
					<input id="endflag" name="endflag" value="continue" type="hidden">
					<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="status" value="${status }">
					<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${domain.dataEnteredBy}">
					<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
					<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input id="oldInternalStatus" name="oldInternalStatus" value="${internalStatus}" type="hidden">
					<input id="oldRecommendationStatus" name="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
					<input id="customStatus" name="customStatus" type="hidden">
					<input type="hidden" id="isPrintRequisitionForGazetteSent" name="isPrintRequisitionForGazetteSent" value="${isPrintRequisitionForGazetteSent}" />
					<input type="hidden" id="isPrintRequisitionPostPassedByFirstHouseSent" name="isPrintRequisitionPostPassedByFirstHouseSent" value="${isPrintRequisitionPostPassedByFirstHouseSent}" />
										
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
				<input id="sendForTranslationMessage" name="sendForTranslationMessage" value="<spring:message code='bill.sendForTranslationMessage' text='Do You Want To Send for Translation of Selected Fields in remarks?'></spring:message>" type="hidden">
				<input id="sendForOpinionFromLawAndJDMessage" name="sendForOpinionFromLawAndJDMessage" value="<spring:message code='bill.sendForOpinionFromLawAndJDMessage' text='Do You Want To Send for Opinion Seeking From Law And Judiciary Department?'></spring:message>" type="hidden">
				<input id="sendForRecommendationFromGovernorPrompt" value="<spring:message code='bill.sendForRecommendationFromGovernorPrompt' text='Do You Want To Send for Recommendation From Governor?'></spring:message>" type="hidden">
				<input id="sendForRecommendationFromPresidentPrompt" value="<spring:message code='sendForRecommendationFromPresidentPrompt' text='Do You Want To Send for Recommendation From President?'></spring:message>" type="hidden">
				<input id="sendForNameclubbingPrompt" value="<spring:message code='bill.sendForNameclubbingPrompt' text='Do You Want To Send for Name Clubbing?'></spring:message>" type="hidden">
				<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='bill.startworkflowmessage' text='Do You Want To Put Up Bill?'></spring:message>" type="hidden">
				<input id="billsSubmittedEarierPendingForPutupWarning" value="<spring:message code='bill.billsSubmittedEarierPendingForPutupWarning' text='There are bills submitted earier & pending for putup.. Do You Still Want To Put Up Bill?'></spring:message>" type="hidden">
				<input id="translationPendingMessage" value="<spring:message code='bill.translationPendingMessage' text='Translation is neither received or timed out.. So Bill cannot be put up.'></spring:message>" type="hidden">
				<input id="startWorkflowDespiteTranslationPendingMessage" name="startWorkflowDespiteTranslationPendingMessage" value="<spring:message code='bill.startWorkflowDespiteTranslationPendingMessage' text='Translation is Pending..Still Do You Want To Put Up Bill?'></spring:message>" type="hidden">
				<input id="remarksCompulsoryWhenPutupForRejectionMessage" value="<spring:message code='bill.remarksCompulsoryWhenPutupForRejectionMessage' text='Remarks are compulsory when putup for rejection.. So Bill cannot be put up.'></spring:message>" type="hidden">
				<input id="ministrySelected" value="${ministrySelected }" type="hidden">
				<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">				
				<input id="selectedBillType" value="${selectedBillType}" type="hidden">
				<input id="selectedBillKind" value="${selectedBillKind}" type="hidden">
				<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
				<input id="questionType" type="hidden" value="${selectedQuestionType}" />
				<input id="typeOfSelectedDeviceType" type="hidden" value="${selectedDeviceTypeForBill}" />
				<input type="hidden" id="typeOfSelectedBillType" value="${typeOfSelectedBillType}" />
				<input id="translationStatusType" type="hidden" value="${translationStatusType}" />		
				<input id="typeOfSelectedBillType" type="hidden" value="${typeOfSelectedBillType}" />
				<input id="internalStatusType" type="hidden" value="${internalStatusType}" />
				<input id="internalStatusPriority" type="hidden" value="${internalStatusPriority}" />
				<input id="recommendationStatusType" type="hidden" value="${recommendationStatusType}" />
				<input id="isMoneyBill" type="hidden" value="${isMoneyBill}" />	
				<input id="sendForGazettePublishingPrompt" type="hidden" value="<spring:message code="bill.sendForGazettePublishingPrompt" text="Do you want to send request for gazette publishing now?"/>">
				<input id="sendForPressCopiesPostPassedByFirstHousePrompt" type="hidden" value="<spring:message code="sendForPressCopiesPostPassedByFirstHousePrompt" text="Do you want to send request for press copies now?"/>">
				<input type="hidden" id="introductionDateEmptyMsg" value="<spring:message code="bill.introductionDateEmptyMsg" text="Please set introduction date for the bill"/>">
				<input type="hidden" id="recommendationFromGovernorEmptyMsg" value="<spring:message code="bill.recommendationFromGovernorEmptyMsg" text="recommendation from governor is not received yet.\nso bill cannot be introduced."/>">
				<input type="hidden" id="recommendationFromPresidentEmptyMsg" value="<spring:message code="bill.recommendationFromPresidentEmptyMsg" text="recommendation from president is not received yet.\nso bill cannot be introduced."/>">
				<input type="hidden" id="gazetteReportNotUploadedMsg" value="<spring:message code="bill.gazetteReportNotUploadedMsg" text="Please upload gazette report/s."/>">
				<input type="hidden" id="docketReportNotUploadedMsg" value="<spring:message code="docketReportNotUploadedMsg" text="Please upload docket report/s."/>">
				<input type="hidden" id="isAnyBillSubmittedEarierThanCurrentBillToBePutup" value="${isAnyBillSubmittedEarierThanCurrentBillToBePutup}" />
				<input type="hidden" id="billNotSetUnderConsiderationMsg" value="<spring:message code="bill.billNotSetUnderConsiderationMsg" text="Bill has not been set for consideration."/>">
				<input type="hidden" id="moneyBillCantBeReferredToJointCommitteeMsg" value="<spring:message code="bill.moneyBillCantBeReferredToJointCommitteeMsg" text="Money Bill Cannot Be Referred To Joint Committee."/>">
				<input type="hidden" id="votingForPassingOfBill" value="${votingForPassingOfBill}">
				<input type="hidden" id="isActReferenced" value="${isActReferenced}">
				<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
				<input type="hidden" id="isMoneyBillPrompt" value="<spring:message code="bill.isMoneyBillPrompt" text="Are you sure this is a money bill?"></spring:message>">
				<input type="hidden" id="isNotMoneyBillPrompt" value="<spring:message code="bill.isNotMoneyBillPrompt" text="Are you sure this is not a money bill?"></spring:message>">
				<input type="hidden" id="isChecklistFilled" value="${isChecklistFilled}">
				<input type="hidden" id="reviseTitle_text" value="<spring:message code="bill.reviseTitle_text" text="Revise This Title"></spring:message>">
				<input type="hidden" id="unReviseTitle_text" value="<spring:message code="bill.unReviseTitle_text" text="Un-Revise This Title"></spring:message>">
				<input type="hidden" id="reviseContentDraft_text" value="<spring:message code="bill.reviseContentDraft_text" text="Revise This Content Draft"></spring:message>">
				<input type="hidden" id="unReviseContentDraft_text" value="<spring:message code="bill.unReviseContentDraft_text" text="Un-Revise This Content Draft"></spring:message>">
				<input type="hidden" id="reviseStatementOfObjectAndReasonDraft_text" value="<spring:message code="bill.reviseStatementOfObjectAndReasonDraft_text" text="Revise This Statement of Object and Reason"></spring:message>">
				<input type="hidden" id="unReviseStatementOfObjectAndReasonDraft_text" value="<spring:message code="bill.unReviseStatementOfObjectAndReasonDraft_text" text="Un-Revise This Statement of Object and Reason"></spring:message>">
				<input type="hidden" id="reviseFinancialMemorandumDraft_text" value="<spring:message code="bill.reviseFinancialMemorandumDraft_text" text="Revise This Financial Memorandum"></spring:message>">
				<input type="hidden" id="unReviseFinancialMemorandumDraft_text" value="<spring:message code="bill.unReviseFinancialMemorandumDraft_text" text="Un-Revise This Financial Memorandum"></spring:message>">
				<input type="hidden" id="reviseStatutoryMemorandumDraft_text" value="<spring:message code="bill.reviseStatutoryMemorandumDraft_text" text="Revise This Statutory Memorandum"></spring:message>">
				<input type="hidden" id="unReviseStatutoryMemorandumDraft_text" value="<spring:message code="bill.unReviseStatutoryMemorandumDraft_text" text="Un-Revise This Statutory Memorandum"></spring:message>">
				
				<%-- <input type="hidden" id="opinionFromLawAndJdNotReceivedMsg" value="<spring:message code="bill.opinionFromLawAndJdNotReceivedMsg" text="Opinion From Law And Judiciary Department Is Not Received Yet."></spring:message>"> --%>
				
				<%-- <input type="hidden" id="hdsRefEntity" value="${hdsRefEntity}" /> --%>
				
				<ul id="contextMenuItems">
					<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
					<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
					<li><a href="#dereferLapsedBill" class="edit"><spring:message code="generic.bill.dereferencinglapsedbill" text="Derefer Lapsed Bill"></spring:message></a></li>
				</ul>
			</div>
		
		</div>
		<div id="referringActResultDiv" style="display:none;">
		</div>
		
		<div id="referringOrdinanceResultDiv" style="display:none;">
		</div>
		
		<div id="clubbingResultDiv" style="display:none;">
		</div>
		
		<div id="referencingResultDiv" style="display:none;">
		</div>
		
		<div id="referLapsedResultDiv" style="display:none;">
		</div>
	</body>
</html>