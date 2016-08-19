<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title>
			<spring:message code="billamendmentmotion" text="Bill Amendment Motion Information System"/>
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
		/**** detail of clubbed, referenced and lapsed 'bill amendment motions' ****/		
		function viewBillAmendmentMotionDetail(id){
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
			var resourceURL='billamendmentmotion/'+id+'/edit?'+parameters;
			$.get(resourceURL,function(data){
				$.unblockUI();
				$.fancybox.open(data,{autoSize:false,width:1000,height:750});
			},'html');	
		}
		/**** load actors ****/
		function loadActors(value){
			if(value!='-'){			
				var translate=$("#internalStatusMaster option[value='billamendmentmotion_final_translation']").text();
				var reject_translate=$("#internalStatusMaster option[value='billamendmentmotion_final_reject_translation']").text();
				var opinion_from_lawandjd=$("#internalStatusMaster option[value='billamendmentmotion_final_opinionFromLawAndJD']").text();
				var sendback=$("#internalStatusMaster option[value='billamendmentmotion_recommend_sendback']").text();			
				var discuss=$("#internalStatusMaster option[value='billamendmentmotion_recommend_discuss']").text();
				var sendToSectionOfficer = $("#internalStatusMaster option[value='billamendmentmotion_processed_sendToSectionOfficer']").text();
				var amendmentReadyForDiscussion = $("#internalStatusMaster option[value='billamendmentmotion_processed_readyForDiscussion']").text();
				var rejected = $("#internalStatusMaster option[value='billamendmentmotion_processed_rejectionWithReason']").text();
				var clubbingApproved = $("#internalStatusMaster option[value='billamendmentmotion_final_clubbing']").text();
			    var clubbingRejected = $("#internalStatusMaster option[value='billamendmentmotion_final_reject_clubbing']").text();	
			    var nameclubbingApproved = $("#internalStatusMaster option[value='billamendmentmotion_final_nameclubbing']").text();
			    var nameclubbingRejected = $("#internalStatusMaster option[value='billamendmentmotion_final_reject_nameclubbing']").text();	    
			    var clubbingPostAdmissionRecommendApprove = $("#internalStatusMaster option[value='billamendmentmotion_recommend_clubbingPostAdmission']").text();
			    var clubbingPostAdmissionRecommendReject = $("#internalStatusMaster option[value='billamendmentmotion_recommend_reject_clubbingPostAdmission']").text();
			    var clubbingPostAdmissionApproved = $("#internalStatusMaster option[value='billamendmentmotion_final_clubbingPostAdmission']").text();
			    var clubbingPostAdmissionRejected = $("#internalStatusMaster option[value='billamendmentmotion_final_reject_clubbingPostAdmission']").text();
			    var unclubbingRecommendApprove = $("#internalStatusMaster option[value='billamendmentmotion_recommend_unclubbing']").text();
			    var unclubbingRecommendReject = $("#internalStatusMaster option[value='billamendmentmotion_recommend_reject_unclubbing']").text();
			    var unclubbingApproved = $("#internalStatusMaster option[value='billamendmentmotion_final_unclubbing']").text();
			    var unclubbingRejected = $("#internalStatusMaster option[value='billamendmentmotion_final_reject_unclubbing']").text();
			    var admitDueToReverseClubbingRecommendApprove = $("#internalStatusMaster option[value='billamendmentmotion_recommend_admitDueToReverseClubbing']").text();
			    var admitDueToReverseClubbing = $("#internalStatusMaster option[value='billamendmentmotion_final_admitDueToReverseClubbing']").text();
			    var valueToSend = "";
			    if(value==amendmentReadyForDiscussion || value==rejected
			    		|| value==clubbingApproved || value==clubbingRejected					
						|| value==nameclubbingApproved || value == nameclubbingRejected
						|| value==clubbingPostAdmissionApproved || value==clubbingPostAdmissionRejected
						|| value==unclubbingApproved || value == unclubbingRejected
						|| value==admitDueToReverseClubbing){
			    	$("#endFlag").val("end");
					if(value!=amendmentReadyForDiscussion && value!=rejected
							&&value!=clubbingPostAdmissionApproved && value!=clubbingPostAdmissionRejected
							&& value!=unclubbingApproved && value!=unclubbingRejected
							&& value!=admitDueToReverseClubbing) {
						$("#internalStatus").val(value);
					}	
			    	$("#recommendationStatus").val(value);
			    	$("#actor").empty();
					$("#actorDiv").hide();					
					return false;
			    } else if(value==sendToSectionOfficer) {
			    	valueToSend = $("#internalStatus").val();
			    } else {
					valueToSend = value;
				}
			    if(value==reject_translate) {
			    	$("#endFlagForAuxillaryWorkflow").val("end");
			    } else {
			    	if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
			    		$("#endFlagForAuxillaryWorkflow").val("continue");
				    } else {
				    	$("#endFlag").val("continue");
				    }									
				}
			    var level=$("#oldLevel").val();
			    if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
			    	level=$("#oldLevelForAuxillaryWorkflow").val();
			    }
			    var params="billamendmentmotion="+$("#id").val()+"&status="+valueToSend+
				"&usergroup="+$("#usergroup").val()+"&level="+level;
				var resourceURL='ref/billamendmentmotion/actors?'+params;
				$.post(resourceURL,function(data){		
					$("#actor").empty();
					if(data!=undefined&&data!=null&&data!=''&&data.length!=0){					
						var text="";
						for(var i=0;i<data.length;i++){
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$("#actor").html(text);
						$("#actorDiv").show();						
						/**** setting level,localizedActorName For Workflow ****/
						 var actor1=data[0].id;
						 var temp=actor1.split("#");
						 if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
							 $("#levelForAuxillaryWorkflow").val(temp[2]);		    
							 $("#localizedActorNameForAuxillaryWorkflow").val(temp[3]+"("+temp[4]+")");
						 } else {
							 $("#level").val(temp[2]);		    
							 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
						 }						 
					}else{					
						$("#actorDiv").hide();						
					}
					if(value != sendback &&value != discuss && value != sendToSectionOfficer
							&& value != translate && value != reject_translate && value != opinion_from_lawandjd
							&& value != clubbingPostAdmissionRecommendApprove && value != clubbingPostAdmissionRecommendReject
							&& value != unclubbingRecommendApprove && value != unclubbingRecommendReject
							&& value != admitDueToReverseClubbingRecommendApprove) {
						
						$("#internalStatus").val(value);							
					} 
				    if(value==translate || value==reject_translate || value==opinion_from_lawandjd) {
						$("#customStatus").val(value);
					} else {							
						$("#recommendationStatus").val(value);
					}
				});
			}else{
				$("#actor").empty();
				$("#actorDiv").hide();
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#customStatus").val("");	
			    if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
			    	$("#levelForAuxillaryWorkflow").val($("#oldLevelForAuxillaryWorkflow").val());
			    } else {
			    	$("#level").val($("#oldLevel").val());
			    }	    
			}
		}
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
					$(this).val("");
				}				
			});
		}		
		$(document).ready(function(){
			initControls();	
			
			$.get('ref/billamendmentmotion/amendedBillInfo?amendedBillInfo='+$('#amendedBillInfo').val(), function(data) {
				$('#showAmendedBillText').empty();
				$('#showAmendedBillText').html(data);				
				$('#showAmendedBill').text($('#showAmendedBillText').html());
			}).fail(function() {
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				//resetControls();
				scrollTop();
			});
			
			$('#showAmendedBill').click(function() {
				viewAmendedBillDetail($('#amendedBill').val());
			});
			
			/* if($('#remarks').val()!=undefined
					&& $('#remarks').val()!="" 
					&& $('#remarks').val()!="<p></p>") {
				$('#remarks_div').show();
			} else {
				$('#remarks_div').hide();
			} */
			
			if($('#changeInternalStatus').val()==undefined
					|| $('#changeInternalStatus').val()==""
					|| $('#changeInternalStatus').val()=="-") {
				$('#actorDiv').hide();
			} else {
				$('#actorDiv').show();
			}
			
			if($('#hideActorsFlag').val()=='true') {
				$("#actorDiv").hide();
			}

			$("#actor").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
			    	$("#levelForAuxillaryWorkflow").val(temp[2]);		    
				    $("#localizedActorNameForAuxillaryWorkflow").val(temp[3]+"("+temp[4]+")");
			    } else {
			    	$("#level").val(temp[2]);		    
				    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
			    }			    
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
			
			/**** show revised section number for given language if already exists ****/
			$('.revisedSectionAmendment_sectionNumber').each(function() {
				var currentLanguage = this.id.split("_")[3];	
				if($(this).val()!=null && $(this).val()!=undefined && $(this).val()!=''
						&& $(this).val()!=$('#sectionAmendment_sectionNumber_'+currentLanguage).val()) {		
					$('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).show();
					$('#reviseSectionAmendment_sectionNumber_icon_$'+currentLanguage).attr('title',$('#unReviseSectionAmendment_sectionNumber').val());
				} else {
					$('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).hide();
					if($("#sectionAmendment_sectionNumber_"+currentLanguage).val()!=undefined && $("#sectionAmendment_sectionNumber_"+currentLanguage).val()!='') {
						$(this).val($("#sectionAmendment_sectionNumber_"+currentLanguage).val());
					}					
				}
			});
			
			/**** toggle revised section number for given language icon ****/
			$('.reviseSectionAmendment_sectionNumber').click(function() {
				var currentLanguage = this.id.split("_")[2];				
				if($('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).css('display')=='none') {
					$('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).show();
					$('#reviseSectionAmendment_sectionNumber_icon_'+currentLanguage).attr('title',$('#unReviseSectionAmendment_sectionNumber').val());
				} else {
					$('#revisedSectionAmendment_sectionNumber_Para_'+currentLanguage).hide();
					$('#reviseSectionAmendment_sectionNumber_icon_'+currentLanguage).attr('title',$('#reviseSectionAmendment_sectionNumber').val());
				}
				return false;
			});
			
			/**** revised section number change event ****/
			$('.revisedSectionAmendment_sectionNumber').change(function() {
				var currentLanguage = this.id.split("_")[3];				
				$('#revisedSectionAmendment_referSectionText_'+currentLanguage).removeClass("referred");
				$('#revisedSectionAmendment_referSectionTextImage_'+currentLanguage).attr('title',$('#revisedSectionAmendment_referSectionTextImageTitle').val());
				$('#revisedSectionAmendment_referredSectionTextPara_'+currentLanguage).hide();
				if($(this).val()!="") {
					//$('#revisedSectionAmendment_referSectionText_'+currentLanguage).show();
					$.get('ref/billamendmentmotion/getReferredSectionText?billId='+$('#amendedBill').val()
								+'&sectionNumber='+$('#revised_sectionAmendment_sectionNumber_'+currentLanguage).val()
								+'&language='+currentLanguage, function(data) {
						$('#revisedSectionAmendment_referredSectionText_'+currentLanguage).wysiwyg("setContent", data.name);
						$('#revisedSectionAmendment_referredSectionText_'+currentLanguage).val(data.name);
						if(data.id!="0") {
							$('#revisedSectionAmendment_referSectionText_'+currentLanguage).show();
						} else {
							$('#revisedSectionAmendment_referSectionText_'+currentLanguage).hide();
						}
					}).fail(function() {
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						//resetControls();
						scrollTop();
					});
				} else {					
					$('#revisedSectionAmendment_referSectionText_'+currentLanguage).hide();
					$('#revisedSectionAmendment_referredSectionText_'+currentLanguage).wysiwyg("setContent", "");
					$('#revisedSectionAmendment_referredSectionText_'+currentLanguage).val("");										
				}				
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
			
			/**** refer revised section content ****/
			$('.revisedSectionAmendment_referSectionText').click(function() {				
				var currentLanguage = this.id.split("_")[2];
				if($('#revisedSectionAmendment_referSectionText_'+currentLanguage).hasClass("referred")) {
					$('#revisedSectionAmendment_referSectionText_'+currentLanguage).removeClass("referred");
					$('#revisedSectionAmendment_referSectionTextImage_'+currentLanguage).attr('title',$('#revisedSectionAmendment_referSectionTextImageTitle').val());
					$('#revisedSectionAmendment_referredSectionTextPara_'+currentLanguage).hide();					
				} else {
					$('#revisedSectionAmendment_referSectionText_'+currentLanguage).addClass("referred");
					$('#revisedSectionAmendment_referSectionTextImage_'+currentLanguage).attr('title',$('#deReferSectionTextImageTitle').val());					
					$('#revisedSectionAmendment_referredSectionTextPara_'+currentLanguage).show();
				}	
				$('html,body').animate({scrollTop:($('#scrollTo_revisedSectionAmendment_referredSectionText_'+currentLanguage).offset().top)}, 'slow');
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
			
			/**** Citations ****/
			$("#viewCitation").click(function(){
				$.get('billamendmentmotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			    },'html');
			    return false;
			});	
			
			/**** Revisions ****/
		    $(".viewRevisions").click(function(){
		    	$.get('billamendmentmotion/revisions/'+$("#id").val(),function(data){
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
		    
		    $('#submit').click(function() {
				if($('#workflowstatus').val()=='COMPLETED') {
					$.prompt("Action completed already!!!!");
					return false;
				}
				if($('#changeInternalStatus').val()=='-' && $('#changeInternalStatus').val()!=undefined) {
					$.prompt("Please select the action first.");					
					return false;
				}
				var rejected = $("#internalStatusMaster option[value='bill_processed_rejectionWithReason']").text();
				if($('#changeInternalStatus').val()==rejected) {
					if($('#rejectionReason').val()=='' || $('#rejectionReason').val()==undefined) {
						$.prompt("Please provide rejection reason/s.");
						return false;
					}
				}
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});
				clearUnrevisedSectionAmendments();
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
		    
		 	// set status for workflow if default action is there
			/* var statusRecommended = $('#changeInternalStatus').val();
			if(statusRecommended!=null && statusRecommended!=undefined 
					&& statusRecommended!="" && statusRecommended!="-")  {
				if($('#workflowtype').val()=='TRANSLATION_WORKFLOW' || $('#workflowtype').val()=='OPINION_FROM_LAWANDJD_WORKFLOW') {
					$('#customStatus').val(statusRecommended);
				} else {								
					$('#internalStatus').val(statusRecommended);
					$('#recommendationStatus').val(statusRecommended);
				}
			} */		
			
			//load actors as per default action
		    $("#changeInternalStatus").change();		
		});		
		</script>
		
		<style type="text/css" media="print">
			textarea[class=wysiwyg]{
				display:block;
			}		
		</style>
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
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div class="fields clearfix watermark">		
			<div id="assistantDiv">
				<form:form action="workflow/billamendmentmotion" method="PUT" modelAttribute="domain">
					<%@ include file="/common/info.jsp" %>
					<h2>${formattedDeviceType} ${formattedNumber}</h2>
					<p>
						<form:errors path="version" cssClass="validationError"/>
					</p>			
					
					<p style="display:none;">
						<label class="small"><spring:message code="billamendmentmotion.houseType" text="House Type"/>*</label>
						<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
						<input id="houseType" name="houseType" value="${houseType}" type="hidden">
						<form:errors path="houseType" cssClass="validationError"/>			
					</p>	
				
					<p style="display:none;">
						<label class="small"><spring:message code="billamendmentmotion.year" text="Year"/>*</label>
						<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
						<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
					</p>
				
					<p style="display:none;">
						<label class="small"><spring:message code="billamendmentmotion.sessionType" text="Session Type"/>*</label>		
						<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
						<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
						<input type="hidden" id="session" name="session" value="${session}"/>
						<form:errors path="session" cssClass="validationError"/>	
					</p>
				
					<p style="display:none;">
						<label class="small"><spring:message code="billamendmentmotion.deviceType" text="Device Type"/>*</label>
						<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
						<input id="type" name="type" value="${deviceType}" type="hidden">					
						<form:errors path="type" cssClass="validationError"/>		
					</p>	
					
					<c:if test="${not empty domain.number or not empty domain.submissionDate}">
					<p>
						<c:if test="${not empty domain.number}">
						<label class="small"><spring:message code="billamendmentmotion.number" text="bill Number"/>*</label>
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
						<label class="centerlabel"><spring:message code="billamendmentmotion.members" text="Members"/></label>
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
						<label class="small"><spring:message code="billamendmentmotion.primaryMemberConstituency" text="Constituency"/>*</label>
						<input type="text" readonly="readonly" value="${constituency}" class="sText">
						<a href="#" id="viewContacts" style="vertical-align:top; margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>
					</p>
						
					<p>	
						<label class="small"><spring:message code="billamendmentmotion.amendedBill" text="Amended Bill"/></label>
						<a id="showAmendedBill" href="#"></a>
						<span id="showAmendedBillText" style="display:none;"></span>
					</p>
					
					<c:if test="${usergroupType=='assistant'}">		
					<p style="margin-left: 162px;">
						<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="billamendmentmotion.clubbing" text="Clubbing"></spring:message></a>
						<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin-right: 20px;"><spring:message code="billamendmentmotion.refresh" text="Refresh"></spring:message></a>
					</p>
					</c:if>
					<c:choose>	
						<c:when test="${empty parent}"><c:set var="displayParentMotion" value="none"/></c:when>
						<c:otherwise><c:set var="displayParentMotion" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayParentMotion};">
						<label class="small"><spring:message code="billamendmentmotion.parentBillAmendmentMotion" text="Clubbed To"></spring:message></label>
						<a href="#" id="p${parent}" onclick="viewBillAmendmentMotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>						
						<input type="hidden" id="parent" name="parent" value="${parent}">
					</p>		
					<c:choose>	
						<c:when test="${empty clubbedMotionsToShow}"><c:set var="displayClubbedMotionsToShow" value="none"/></c:when>
						<c:otherwise><c:set var="displayClubbedMotionsToShow" value="inline"/></c:otherwise>		
					</c:choose>
					<p style="display: ${displayClubbedMotionsToShow};">
						<label class="small"><spring:message code="billamendmentmotion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
						<c:choose>
							<c:when test="${!(empty clubbedMotionsToShow) }">
								<c:forEach items="${clubbedMotionsToShow}" var="i">
									<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewBillAmendmentMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:out value="-"></c:out>
							</c:otherwise>
						</c:choose>
						<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
							<c:forEach items="${clubbedMotionsToShow}" var="i">
								<option value="${i.id}" selected="selected"></option>
							</c:forEach>
						</select>
					</p>
					
					<div style="margin-top: 20px;">
						<fieldset>
							<p style="margin-bottom: 10px;">
							<a href="#" class="viewRevisions" id="viewRevisions_sectionAmendments"  style="margin-left: 162px;text-decoration: none;">
								<img src="./resources/images/ViewRevision.jpg" title="<spring:message code='billamendmentmotion.viewRevisionsForSectionAmendments' text='View Revisions for Section Amendments'></spring:message>" class="imageLink" />
							</a>
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
											<a href="#" class="reviseSectionAmendment_sectionNumber" id="reviseSectionAmendment_sectionNumber_${i.language.type}" style="margin-left: 20px;">
												<img id="reviseSectionAmendment_sectionNumber_icon_${i.language.type}" src="./resources/images/Revise.jpg" title="<spring:message code='billamendmentmotion.reviseSectionAmendment_sectionNumber' text='Revise This Section Number'></spring:message>" class="imageLink" />
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
											<textarea class="wysiwyg" id="referredSectionText_${i.language.type}">${requestScope[referredSectionText]}</textarea>
										</p>
										<c:set var="revisedSectionAmendment_referredSectionText">revisedSectionAmendment_referredSectionText_${i.language.type}</c:set>
										<a href="#" id="scrollTo_revisedSectionAmendment_sectionNumber_${i.language.type}"></a>
										<p id="revisedSectionAmendment_sectionNumber_Para_${i.language.type}" style="display:none;">
											<label class="small">${i.language.name} <spring:message code="billamendmentmotion.sectionAmendment.revisedSectionNumber" text="Revised Section Number"/></label>
											<input class="sText revisedSectionAmendment_sectionNumber" id="revised_sectionAmendment_sectionNumber_${i.language.type}" name="revised_sectionAmendment_sectionNumber_${i.language.type}" value="${requestScope[revisedSectionAmendment_sectionNumber]}"/>
											<a href="#" id="revisedSectionAmendment_referSectionText_${i.language.type}" class="revisedSectionAmendment_referSectionText" style="margin-left: 10px;text-decoration: none;display:${not empty requestScope[revisedSectionAmendment_referredSectionText]?'inline':none};">		
												<img id="revisedSectionAmendment_referSectionTextImage_${i.language.type}" src="./resources/images/Ico_Refer2.jpg" title="<spring:message code='billamendmentmotion.revisedSectionAmendment.referSectionText' text='Refer Revised Section Text'/>" class="imageLink" />
											</a>
										</p>
										<a href="#" id="scrollTo_revisedSectionAmendment_referredSectionText_${i.language.type}"></a>
										<p id="revisedSectionAmendment_referredSectionTextPara_${i.language.type}" style="display: none;">
											<label class="wysiwyglabel">${i.language.name} <spring:message code="billamendmentmotion.revisedSectionAmendment.referredSectionText" text="Referred Revised Section Text"/></label>
											<textarea class="wysiwyg" id="revisedSectionAmendment_referredSectionText_${i.language.type}">${requestScope[revisedSectionAmendment_referredSectionText]}</textarea>
										</p>
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
					
					<c:if test="${not empty domain.opinionSoughtFromLawAndJD}">						
					<div id="opinionSoughtFromLawAndJD_div">
						<p id="opinionSoughtFromLawAndJD_para">
							<label class="wysiwyglabel"><spring:message code="billamendmentmotion.opinionSoughtFromLawAndJD" text="Opinion from Law & Judiciary Department"/></label>
							<form:textarea id="opinionSoughtFromLawAndJD" path="opinionSoughtFromLawAndJD" cssClass="wysiwyg" readonly="true"></form:textarea>
							<form:errors path="opinionSoughtFromLawAndJD" />
						</p>					
						<c:if test="${not empty dateOfOpinionSoughtFromLawAndJD}">
						<p>
						<label class="small"><spring:message code="billamendmentmotion.dateOfOpinionSoughtFromLawAndJD" text="Date Of Opinion Sought From Law And JD"/></label>
						<input id="formattedDateOfOpinionSoughtFromLawAndJD" name="formattedDateOfOpinionSoughtFromLawAndJD" value="${formattedDateOfOpinionSoughtFromLawAndJD}" class="sText" style="margin-left: 10px !important;" readonly="readonly">
						<input id="setDateOfOpinionSoughtFromLawAndJD" name="setDateOfOpinionSoughtFromLawAndJD" type="hidden"  value="${dateOfOpinionSoughtFromLawAndJD}">	
						</p>
						</c:if>
					</div>
					</c:if>
					
					<div>
					<c:if test="${workflowtype=='TRANSLATION_WORKFLOW'}">
					<p id="translationStatusDiv">
						<label class="small"><spring:message code="billamendmentmotion.currentTranslationStatus" text="Current Translation Status"/></label>
						<input id="formattedTranslationStatus" value="${formattedTranslationStatus}" type="text" readonly="readonly">
					</p>
					</c:if>
					<c:if test="${workflowtype=='OPINION_FROM_LAWANDJD_WORKFLOW'}">
					<p id="opinionFromLawAndJDStatusStatusDiv">
						<label class="small"><spring:message code="billamendmentmotion.currentOpinionFromLawAndJDStatus" text="Current Opinion From Law And JD Status"/></label>
						<input id="formattedOpinionFromLawAndJDStatus" value="${formattedOpinionFromLawAndJDStatus}" type="text" readonly="readonly">
					</p>
					</c:if>					
					<p id="internalStatusDiv">
						<label class="small"><spring:message code="billamendmentmotion.currentStatus" text="Current Status"/></label>
						<input id="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
					</p>
					<c:if test="${workflowstatus!='COMPLETED' }">
					<p>
						<label class="small"><spring:message code="billamendmentmotion.putupfor" text="Put up for"/></label>
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
					<label class="small"><spring:message code="billamendmentmotion.nextactor" text="Next Users"/></label>
					<select id="actor" name="actor" class="sSelect">
						<c:forEach var="i" items="${actors}">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>					
					</select>	
					</p>					
					</c:if>	
						
					<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
					<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
					</div>
					
					<div id="remarks_div">
					<c:choose>
					<c:when test="${workflowtype=='TRANSLATION_WORKFLOW'}">
					<p>
						<label class="wysiwyglabel"><spring:message code="billamendmentmotion.remarksForTranslation" text="Translation Remarks"/></label>
						<form:textarea path="remarksForTranslation" cssClass="wysiwyg" readonly="true"></form:textarea>
						<form:errors path="remarksForTranslation" cssClass="validationError"/>
					</p>
					</c:when>	
					<c:otherwise>
						<form:hidden path="remarksForTranslation"/>
					</c:otherwise>
					</c:choose>
					
					<p>
						<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="billamendmentmotion.viewcitation" text="View Citations"></spring:message></a>	
					</p>
					
					<c:if test="${internalStatusType == 'billamendmentmotion_final_rejection'}">
					<p>
					<label class="wysiwyglabel"><spring:message code="billamendmentmotion.rejectionReason" text="Rejection reason"/></label>
					<form:textarea id="rejectionReason" path="rejectionReason" cssClass="wysiwyg"></form:textarea>
					<form:errors path="rejectionReason" cssClass="validationError"/>
					</p>
					</c:if>
					
					<p>
						<label class="wysiwyglabel"><spring:message code="billamendmentmotion.remarks" text="Remarks"/></label>
						<textarea id="remarks" name="remarks" class="wysiwyg">${currentRemarks}</textarea>
					</p>				
					</div>
					
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
					<form:hidden path="level"/>
					<form:hidden path="localizedActorName"/>
					<form:hidden path="workflowDetailsId"/>				
					<form:hidden path="file"/>
					<form:hidden path="fileIndex"/>	
					<form:hidden path="fileSent"/>
					<input id="customStatus" name="customStatus" type="hidden">
					<input id="oldLevel" name="oldLevel" value="${domain.level}" type="hidden">
					<input id="levelForAuxillaryWorkflow" name="levelForAuxillaryWorkflow" value="${level}" type="hidden">
					<input id="oldLevelForAuxillaryWorkflow" name="oldLevelForAuxillaryWorkflow" value="${level}" type="hidden">
					<input id="localizedActorNameForAuxillaryWorkflow" name="localizedActorNameForAuxillaryWorkflow" type="hidden">
					<input id="endFlagForAuxillaryWorkflow" name="endFlagForAuxillaryWorkflow" value="continue" type="hidden">
					<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
					<input type="hidden" name="status" id="status" value="${status }">
					<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
					<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
					<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
					<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
					<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
					<input id="role" name="role" value="${role}" type="hidden">
					<input id="taskid" name="taskid" value="${taskid}" type="hidden">
					<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
					<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
					<input id="amendedBill" name="amendedBill" value="${amendedBill}" type="hidden">
					<input id="amendedBillLanguages" name="amendedBillLanguages" value="${amendedBillLanguages}" type="hidden">
					<input id="isMotionRaisedByMinister" name="isMotionRaisedByMinister" value="${isMotionRaisedByMinister}" type="hidden">					
					
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
				<input id="defaultBillLanguage" value="${defaultBillLanguage}" type="hidden">
				<input id="referSectionTextImageTitle" value="<spring:message code='billamendmentmotion.referSectionTextImageTitle' text='Refer Section Text'/>" type="hidden">
				<input id="deReferSectionTextImageTitle" value="<spring:message code='billamendmentmotion.deReferSectionTextImageTitle' text='De-Refer Section Text'/>" type="hidden">
				<input id="amendedBillInfo" value="${amendedBillInfo}" type="hidden">
				<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
				<input id="oldRecommendationStatus" value="${recommendationStatus}" type="hidden">
				<input id="hideActorsFlag" type="hidden" value="${hideActorsFlag}" />
				<input type="hidden" id="defaultBillLanguage" value="${defaultBillLanguage}">
				<input id="sendForTranslationMessage" name="sendForTranslationMessage" value="<spring:message code='bill.sendForTranslationMessage' text='Do You Want To Send for Translation of Selected Fields in remarks?'></spring:message>" type="hidden">
				<input id="sendForOpinionFromLawAndJDMessage" name="sendForOpinionFromLawAndJDMessage" value="<spring:message code='bill.sendForOpinionFromLawAndJDMessage' text='Do You Want To Send for Opinion Seeking From Law And Judiciary Department?'></spring:message>" type="hidden">
				<input id="sendForNameclubbingPrompt" value="<spring:message code='bill.sendForNameclubbingPrompt' text='Do You Want To Send for Name Clubbing?'></spring:message>" type="hidden">
				<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='bill.startworkflowmessage' text='Do You Want To Put Up Bill?'></spring:message>" type="hidden">
				<input id="translationPendingMessage" value="<spring:message code='bill.translationPendingMessage' text='Translation is neither received or timed out.. So Bill cannot be put up.'></spring:message>" type="hidden">
				<input id="startWorkflowDespiteTranslationPendingMessage" name="startWorkflowDespiteTranslationPendingMessage" value="<spring:message code='bill.startWorkflowDespiteTranslationPendingMessage' text='Translation is Pending..Still Do You Want To Put Up Bill?'></spring:message>" type="hidden">
				<input id="remarksCompulsoryWhenPutupForRejectionMessage" value="<spring:message code='bill.remarksCompulsoryWhenPutupForRejectionMessage' text='Remarks are compulsory when putup for rejection.. So Bill cannot be put up.'></spring:message>" type="hidden">		
				<input id="translationStatusType" type="hidden" value="${translationStatusType}" />		
				<input id="internalStatusType" type="hidden" value="${internalStatusType}" />
				<input id="internalStatusPriority" type="hidden" value="${internalStatusPriority}" />
				<input id="recommendationStatusType" type="hidden" value="${recommendationStatusType}" />
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