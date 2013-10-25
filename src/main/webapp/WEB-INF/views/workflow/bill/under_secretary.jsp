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
				$.fancybox.open(data,{autoSize:false,width:750,height:700});
			},'html');	
		}		
		/**** load actors ****/
		function loadActors(value){
			if(value!='-'){		
			var recommend_nameclubbing=$("#internalStatusMaster option[value='bill_recommend_nameclubbing']").text();
			var recommend_reject_nameclubbing=$("#internalStatusMaster option[value='bill_recommend_reject_nameclubbing']").text();
		    var sendback=$("#internalStatusMaster option[value='bill_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='bill_recommend_discuss']").text();
		    var translate=$("#internalStatusMaster option[value='bill_final_translation']").text();
		    var reject_translate=$("#internalStatusMaster option[value='bill_final_reject_translation']").text();
		    var opinion_from_lawandjd=$("#internalStatusMaster option[value='bill_final_opinionFromLawAndJD']").text();
		    if(value==reject_translate) {
		    	$("#levelForWorkflow").val($('#levelForTranslation').val());
		    	$("#endFlagForTranslation").val("end");
		    	$("#translationStatus").val(value);
		    } else if(value==translate) {
				$("#levelForWorkflow").val($('#levelForTranslation').val());			
				$("#translationStatus").val(value);
			} else if(value==opinion_from_lawandjd) {
				$("#levelForWorkflow").val($('#levelForOpinionFromLawAndJD').val());			
				$("#opinionFromLawAndJDStatus").val(value);
			} else {
				$("#levelForWorkflow").val($('#level').val());
			}
		    var params="bill="+$("#id").val()+"&status="+value+
			"&usergroup="+$("#usergroup").val()+"&levelForWorkflow="+$("#levelForWorkflow").val();
			var resourceURL='ref/bill/actors?'+params;
			$.post(resourceURL,function(data){				
				if(data!=undefined&&data!=null&&data!=''&&data.length!=0){
					$("#actorForWorkflow").empty();
					var text="";
					for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#actorForWorkflow").html(text);
					$("#actorDiv").show();				
					if(value!=sendback&&value!=discuss&&value!=translate
							&&value!=recommend_reject_nameclubbing&&value!=opinion_from_lawandjd){
						$("#internalStatus").val(value);
					} 
					if(value==translate) {
						$("#translationStatus").val(value);						
					} else if(value==opinion_from_lawandjd) {
						$("#opinionFromLawAndJDStatus").val(value);						
					} else if(value==recommend_reject_nameclubbing) {
						$("#internalStatus").val(recommend_nameclubbing);
						$("#recommendationStatus").val(value);
					} else {
						$("#recommendationStatus").val(value);
					}
					/**** setting level,localizedActorName For Workflow ****/
					 var actor1=data[0].id;
					 var temp=actor1.split("#");
					 $("#levelForWorkflow").val(temp[2]);		    
					 $("#localizedActorNameForWorkflow").val(temp[3]+"("+temp[4]+")");
				}else{					
					$("#actorForWorkflow").empty();
					$("#actorDiv").hide();
					if(value!=sendback&&value!=discuss&&value!=translate&&value!=reject_translate
							&&value!=recommend_reject_nameclubbing&&value!=opinion_from_lawandjd){
						$("#internalStatus").val(value);
					} 
					if(value==translate) {
						$("#translationStatus").val(value);						
					} else if(value==opinion_from_lawandjd) {
						$("#opinionFromLawAndJDStatus").val(value);						
					} else if(value==recommend_reject_nameclubbing) {
						$("#internalStatus").val(recommend_nameclubbing);
						$("#recommendationStatus").val(value);
					} else {
						$("#recommendationStatus").val(value);
					}					
				}
			});
			}else{
				$("#actorForWorkflow").empty();
				$("#actorDiv").hide();
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#translationStatus").val($("#oldTranslationStatus").val());	
			    $("#opinionFromLawAndJDStatus").val($("#oldOpinionFromLawAndJDStatus").val());
			}
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
		
		//------------------------------------------------------------------
		/**** Load Clarifications ****/
		/* function loadClarifications(){
			$.get('ref/clarifications',function(data){
				if(data.length>0){
					var text="";
					for( var i=0;i<data.length;i++){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$("#clarificationNeededFrom").empty();
					$("#clarificationNeededFrom").html(text);
					$("#clarificationDiv").show();								
				}else{
					$("#clarificationNeededFrom").empty();
					$("#clarificationDiv").hide();
				}
			});
		} */
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
					
			$("#actorForWorkflow").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    $("#levelForWorkflow").val(temp[2]);		    
			    $("#localizedActorNameForWorkflow").val(temp[3]+"("+temp[4]+")");
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
		    /**** Internal Status Changes ****/   
		    $("#changeInternalStatus").change(function(){
			    var value=$(this).val();
			    if(value!='-'){
				    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();			    
				    loadActors(value);				    	    
			    }else{
				    $("#actorForWorkflow").empty();
				    $("#actorDiv").hide();
				    $("#internalStatus").val($("#oldInternalStatus").val());
				    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
				    $("#translationStatus").val($("#oldTranslationStatus").val());	
				    $("#opinionFromLawAndJDStatus").val($("#oldOpinionFromLawAndJDStatus").val());
				    $("#startworkflow").attr("disabled","disabled");
				    $("#submit").removeAttr("disabled");
				}		    
		    });
		    /**** On page Load ****/
		    $("#startworkflow").attr("disabled","disabled");
			$("#submit").removeAttr("disabled");
		    /**** Put Up ****/
			$("#startworkflow").click(function(e){
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
				$.prompt($('#startWorkflowMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?operation=startworkflow',  
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
			/**** On Bulk Edit ****/
			$("#submitBulkEdit").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});	
				clearUnrevisedDrafts();
				$.post($('form').attr('action'),  
		            $("form").serialize(),  
		            function(data){
	   					$('.fancybox-inner').html(data);
	   					$('html').animate({scrollTop:0}, 'slow');
	   				 	$('body').animate({scrollTop:0}, 'slow');	
		            });
		        return false;  
		    });
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
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
				$.post($('form').attr('action'), $("form").serialize(), function(data){
					$('.tabContent').html(data);
						$('html').animate({scrollTop:0}, 'slow');
						$('body').animate({scrollTop:0}, 'slow');	
					$.unblockUI();
				});
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
					<c:if test="${workflowtype=='TRANSLATION_WORKFLOW'}">
					<p id="translationStatusDiv">
						<label class="small"><spring:message code="bill.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${workflowtype=='OPINION_FROM_LAWANDJD_WORKFLOW'}">
					<p id="opinionFromLawAndJDStatusStatusDiv">
						<label class="small"><spring:message code="bill.currentOpinionFromLawAndJDStatus" text="Current Opinion From Law And JD Status"/></label>
						<input id="formattedOpinionFromLawAndJDStatus" value="${formattedOpinionFromLawAndJDStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<p id="recommendationFromGovernorStatusDiv">
						<label class="small"><spring:message code="bill.currentRecommendationFromGovernorStatus" text="Current Recommendation From Governor Status"/></label>
						<input id="formattedRecommendationFromGovernorStatus" value="${formattedRecommendationFromGovernorStatus}" type="text" readonly="readonly">
					</p>
					<p id="recommendationFromPresidentStatusDiv">
						<label class="small"><spring:message code="bill.currentRecommendationFromPresidentStatus" text="Current Recommendation From President Status"/></label>
						<input id="formattedRecommendationFromPresidentStatus" value="${formattedRecommendationFromPresidentStatus}" type="text" readonly="readonly">
					</p>
					
					<c:if test="${workflowstatus!='COMPLETED' }">
					<p>
						<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>
						<select id="changeInternalStatus" class="sSelect">
							<option value="-"><spring:message code='please.select' text='Please Select'/></option>
							<c:forEach items="${internalStatuses}" var="i">								
								<c:choose>
									<c:when test="${i.id==internalStatusSelected}">
										<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
									</c:when>
									<c:otherwise>
										<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
									</c:otherwise>
								</c:choose>								
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
					<select id="actorForWorkflow" name="actorForWorkflow" class="sSelect">
						<c:forEach var="i" items="${actors}">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>					
					</select>	
					<input type="hidden" id="localizedActorNameForWorkflow"  name="localizedActorNameForWorkflow">
					</p>					
					</c:if>	
						
					<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					<input type="hidden" id="translationStatus"  name="translationStatus" value="${translationStatus}">
					<input type="hidden" id="opinionFromLawAndJDStatus"  name="opinionFromLawAndJDStatus" value="${opinionFromLawAndJDStatus}">
					<input type="hidden" id="recommendationFromGovernorStatus"  name="recommendationFromGovernorStatus" value="${recommendationFromGovernorStatus}">
					<input type="hidden" id="recommendationFromPresidentStatus"  name="recommendationFromPresidentStatus" value="${recommendationFromPresidentStatus}">
											
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
						<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="true"></form:textarea>
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
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromGovernor" text="Recommendation From Governor"/></label>
						<form:textarea id="recommendationFromGovernor" path="recommendationFromGovernor" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromGovernor" />
					</p>
					<c:if test="${not empty dateOfRecommendationFromGovernor}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromGovernor" text="Date Of Recommendation From Governor"/></label>
					<input id="formattedDateOfRecommendationFromGovernor" name="formattedDateOfRecommendationFromGovernor" value="${formattedDateOfRecommendationFromGovernor}" class="sText" readonly="readonly">
					<input id="setDateOfDateOfRecommendationFromGovernor" name="setDateOfDateOfRecommendationFromGovernor" type="hidden"  value="${dateOfRecommendationFromGovernor}">	
					</p>
					</c:if>		
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.recommendationFromPresident" text="Recommendation From President"/></label>
						<form:textarea id="recommendationFromPresident" path="recommendationFromPresident" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="recommendationFromPresident" />
					</p>
					<c:if test="${not empty dateOfRecommendationFromPresident}">
					<p>
					<label class="small"><spring:message code="bill.dateOfRecommendationFromPresident" text="Date Of Recommendation From President"/></label>
					<input id="formattedDateOfRecommendationFromPresident" name="formattedDateOfRecommendationFromPresident" value="${formattedDateOfRecommendationFromPresident}" class="sText" readonly="readonly">
					<input id="setDateOfDateOfRecommendationFromPresident" name="setDateOfDateOfRecommendationFromPresident" type="hidden"  value="${dateOfRecommendationFromPresident}">	
					</p>
					</c:if>
					
					<div>
						<fieldset>
						<legend style="text-align: left; width: 150px;"><label><spring:message code="bill.checklist" text="Checklist" /></label></legend>
						<a href="#" class="viewRevisions" id="viewRevisions_checklist" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewRevisionsForChecklist" text="View Revisions for Checklist"></spring:message></a>
						<a href="bill/viewSchedule7OfConstitution" target="_blank" id="viewSchedule7OfConstitution" style="margin-left: 162px;margin-right: 20px;"><spring:message code="bill.viewSchedule7OfConstitution" text="View Schedule 7 Of Constitution"></spring:message></a>
						<table border="1" style="margin-left: 165px; width: 600px; border: 2px solid black;">
							<!-- <thead>
								<tr>
									<th width="50%">Question Details</th>
									<th>Answer</th>
								</tr>								
							</thead> -->
							<tbody>
								<tr id="checklistQuestion1">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<br/>
											<label><spring:message code="bill.checklistQuestion1" text="Are following things essential for this bill?"/></label>
										</div>
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.1" text="is bill recommended in accordance with constitution article 207 (1)?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_1" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_1" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_1']" />
										</div>
										<br/>																					
										<div style="display: none;" id="checklist_checkbox_1_div">
											<label><spring:message code="bill.checklistQuestion1.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;"/>
										</div>
										
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.3" text="is bill recommended in accordance with constitution article 207 (3)?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_2" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_2" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_207_3']" />
										</div>		
										<br />								
										<div style="display: none;" id="checklist_checkbox_2_div">
											<label><spring:message code="bill.checklistQuestion1.4" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_207_3']" rows="2" cols="50" style="margin: 10px;" />
										</div>
										
										<div>
											<label style="width:500px;"><spring:message code="bill.checklistQuestion1.5" text="is bill recommended in accordance with constitution article 304 (b)?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_3" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_3" type="hidden" path="checklist['isRecommendedAsPerConstitutionArticle_304_b']" />
										</div>	
										<br />									
										<div style="display: none;" id="checklist_checkbox_3_div">
											<label><spring:message code="bill.checklistQuestion1.6" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForRecommendationAsPerConstitutionArticle_304_b']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>
								<tr id="checklistQuestion2">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion2.1" text="is bill in scope of state legislature?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_4" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_4" type="hidden" path="checklist['isInScopeOfStateLegislature']" />
										</div>
										<br />
										<div style="display: none;" id="checklist_checkbox_4_div">
											<label><spring:message code="bill.checklistQuestion2.2" text="if yes, please also mention related schedule issues"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['issuesInRelatedScheduleForScopeOfStateLegislature']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>
								<tr id="checklistQuestion3">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion3.1" text="is this bill a money bill?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_5" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_5" type="hidden" path="checklist['isMoneyBill']" />
										</div>
										<br />
										<div style="display: none;" id="checklist_checkbox_5_div">
											<label><spring:message code="bill.checklistQuestion3.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForBeingMoneyBill']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>
								<tr id="checklistQuestion4">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion4.1" text="is this bill a financial bill as per constitution article 207 (1)?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_6" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_6" type="hidden" path="checklist['isFinancialBillAsPerConstitutionArticle_207_1']" />
										</div>
										<br />
										<div style="display: none;" id="checklist_checkbox_6_div">
											<label><spring:message code="bill.checklistQuestion4.2" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForFinancialBillAsPerConstitutionArticle_207_1']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>								
								<tr id="checklistQuestion5">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion5.1" text="are amendments for amending bill as per scope of original act?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_7" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_7" type="hidden" path="checklist['areAmendmentsForAmendingBillAsPerScopeOfOriginalAct']" />
										</div>
										<br/>										
									</td>
								</tr>
								<tr id="checklistQuestion6">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion6.1" text="is statutory memorandum mandatory?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_8" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_8" type="hidden" path="checklist['isStatutoryMemorandumMandatory']" />
										</div>		
										<br/>								
										<div style="display: none;" id="checklist_checkbox_8_div">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion6.2" text="is statutory memorandum as per rules?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_9" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_9" type="hidden" path="checklist['isStatutoryMemorandumAsPerRules']" />
										</div>
										<br />
										<div style="display: none;" id="checklist_checkbox_9_div">
											<label><spring:message code="bill.checklistQuestion6.3" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForStatutoryMemorandum']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>
								<tr id="checklistQuestion7">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion7.1" text="is financial memorandum mandatory?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_10" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_10" type="hidden" path="checklist['isFinancialMemorandumMandatory']" />
										</div>	
										<br/>									
										<div style="display: none;" id="checklist_checkbox_10_div">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion7.2" text="is financial memorandum as per rules?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_11" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_11" type="hidden" path="checklist['isFinancialMemorandumAsPerRules']" />
										</div>
										<br/>
										<div style="display: none;" id="checklist_checkbox_11_div">
											<label><spring:message code="bill.checklistQuestion7.3" text="if yes, please mention sections"/></label>
											<br/>
											<form:textarea class="sTextarea checklist_textareas" path="checklist['sectionsForFinancialMemorandum']" rows="2" cols="50" style="margin: 10px;" />
										</div>
									</td>
								</tr>
								<tr id="checklistQuestion8">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">
											<label style="width:500px;"><spring:message code="bill.checklistQuestion8.1" text="is statement of object and reason complete?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_12" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_12" type="hidden" path="checklist['isStatementOfObjectAndReasonComplete']" />
										</div>										
									</td>
								</tr>
								<tr id="checklistQuestion9">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">											
											<label style="width:500px;"><spring:message code="bill.checklistQuestion9.1" text="is law & judiciary department in agreement with above opinions on issues 1, 2, 6 & 7?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_13" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_13" type="hidden" path="checklist['isLawAndJudiciaryDepartmentInAgreementWithOpinions']" />
										</div>										
									</td>
								</tr>
								<tr id="checklistQuestion10">
									<td>
										<div style="border: 0px 0px 1px 0px dotted #000000;">										
											<label style="width:500px;"><spring:message code="bill.checklistQuestion10.1" text="are there any recommendations on subject-matter of this bill by sub-legislation committee?"/></label>
											<input type="checkbox" class="sCheck checklist_checkboxes" id="checklist_checkbox_14" style="margin: 10px; margin-left: 50px;">
											<form:input class="checklist_checkbox_fields" id="checklist_checkbox_field_14" type="hidden" path="checklist['isRecommendedOnSubjectMatterBySubLegislationCommittee']" />
										</div>										
									</td>
								</tr>
							</tbody>
						</table>
						</fieldset>
					</div>
					
					<c:choose>
					<c:when test="${workflowtype=='TRANSLATION_WORKFLOW'}">
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarksForTranslation" text="Translation Remarks"/></label>
						<form:textarea path="remarksForTranslation" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="remarksForTranslation" cssClass="validationError"/>
					</p>
					</c:when>	
					<c:otherwise>
						<form:hidden path="remarksForTranslation"/>
					</c:otherwise>
					</c:choose>
					
					<p>
						<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
					</p>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="bill.remarks" text="Remarks"/></label>
						<textarea id="remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>
					</p>					
					
					<div class="fields">
						<h2></h2>
						<p class="tright">
							<c:choose>
								<c:when test="${workflowstatus!='COMPLETED' and bulkedit!='yes'}">
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">										
								</c:when>
								<c:when test="${workflowstatus=='COMPLETED' and bulkedit!='yes'}">
									<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
								</c:when>
								<c:when test="${workflowstatus!='COMPLETED' and bulkedit=='yes'}">		
									<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
								</c:when>
								<c:when test="${workflowstatus=='COMPLETED' and bulkedit=='yes'}">		
									<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">			
								</c:when>
							</c:choose>
						</p>
					</div>
					<form:hidden path="id"/>
					<form:hidden path="locale"/>
					<form:hidden path="version"/>
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
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					
					
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
				<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='bill.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
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
			</div>		
		</div>
	</body>
</html>