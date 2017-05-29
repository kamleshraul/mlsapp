<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		function prependOptionToDeviceType() {
			var isDeviceTypeFieldEmpty = $('#isDeviceTypeEmpty').val();
			var optionValue = $('#allOption').val();
			if(isDeviceTypeFieldEmpty == 'true') {
				var option = "<option value='0' selected>" + optionValue + "</option>";
				$('#deviceType').prepend(option);
			}
			else {
				var option = "<option value='0'>" + optionValue + "</option>";
				$('#deviceType').prepend(option);	
			}
		}		
		
		/**** load actors ****/
		function loadActors(value){
			//console.log(value);
			if(value!='-'){
				var params="cutMotionDate="+$("#id").val()+"&status="+value+
				"&userGroup="+$("#usergroup").val()+"&level="+$("#level").val()+
				"&usergroupType="+$("#usergroupType").val()+"&deviceType="+$("#deviceType").val()+
				"&houseType="+$("#selectedHouseType").val()+"&isWF=YES&workflowDetailsId="+$("#workflowDetails").val();
				var resourceURL='ref/cutmotiondate/actors?'+params;
			    var sendback=$("#internalStatusMaster option[value='cutmotiondate_recommend_datesendback']").text();			
			    var discuss=$("#internalStatusMaster option[value='cutmotiondate_recommend_datediscuss']").text();		
				$.get(resourceURL,function(data){
					if(data!=undefined||data!=null||data!=''){
						$("#actor").empty();
						var text="";
						for(var i=0;i<data.length;i++){
							text+="<option value='"+data[i].value+"'>"+data[i].name+"</option>";
						}
						$("#actor").html(text);
						$("#actorDiv").show();				
						/**** in case of sendback and discuss only recommendation status is changed ****/
						if(value!=sendback&&value!=discuss){
							$("#internalStatus").val(value);
						}
						$("#recommendationStatus").val(value);			
						/**** setting level,localizedActorName ****/
						/*  var actor1=data[0].id;
						 var temp=actor1.split("#");
						 $("#level").val(temp[2]);		    
						 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
						 $('#actorName').val(temp[4]);
						 $('#actorName').css('display','inline'); */
					}else{
						$("#actor").empty();
						$("#actorDiv").hide();
						/**** in case of sendback and discuss only recommendation status is changed ****/
						if(value!=sendback&&value!=discuss){
							$("#internalStatus").val(value);
						}
					    $("#recommendationStatus").val(value);
					}
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else{
				$("#actor").empty();
				$("#actorDiv").hide();
				$("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			}
		}
		
		//populate discussionDateForm for selected record
		function populateDiscussionDateForm(recordId) {
			var recordIndex = recordId.split("_")[1];
			$('#discussionDate').val($('#discussionDate'+recordIndex).val());
			populateDiscussionDates($('#discussionDate').val());
			$('#submissionEndDate').val($('#submissionEndDate'+recordIndex).val());	
			populateSubmissionEndDates($('#discussionDate').val(), false);
			populateDepartments($('#discussionDate').val());
			if($('#discussionDate'+recordIndex+'_departmentsCount').val()!=undefined && $('#discussionDate'+recordIndex+'_departmentsCount').val()>0) {
				$('.discussionDate'+recordIndex+'_department').each(function(){
					var deptId = $(this).val();
					var deptIdentifier = $(this).attr('id').split("_")[1];
					var deptName = $('#discussionDate'+recordIndex+"_"+deptIdentifier+'_name').val();
					$("#selectedDepartments").append("<option value='"+deptId+"'>"+deptName+"</option>");
					$("#allDepartments option[value='"+deptId+"']").remove();		
				});
				$("#selectedDepartments option").each(function() {
					$("#allDepartments option[value='"+$(this).val()+"']").remove();
				});
			}
		}
		
		function populateDiscussionDates(discussionDateValue) {
			$('#discussionDate option').each(function() {
				$(this).css('display', 'block');			
			});
			if(discussionDateValue==undefined || discussionDateValue=='' || discussionDateValue=='0') {			
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					$('#discussionDate option[value="'+$('#discussionDate'+recordIndex).val()+'"]').css('display', 'none');
				});
			} else {
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					if($('#discussionDate'+recordIndex).val()!=discussionDateValue) {
						$('#discussionDate option[value="'+$('#discussionDate'+recordIndex).val()+'"]').css('display', 'none');
					}				
				});
			}
		}
		
		function populateSubmissionEndDates(discussionDateValue, isDiscussionDateChanged) {
			if(discussionDateValue==undefined || discussionDateValue=='' || discussionDateValue=='0') {
				$('#submissionEndDate').val("0");
				$('#submissionEndDate option').each(function() {
					$(this).css('display', 'block');
					$(this).siblings("[value='"+ $(this).val() +"']").css('display', 'none');
				});
			} else {
				var selectedIndexOfSubmissionDate = $('#submissionEndDate').prop('selectedIndex');
				if($('#submissionEndDate').val()==undefined || $('#submissionEndDate').val()=='' || $('#submissionEndDate').val()=='0' || isDiscussionDateChanged==true) {
					selectedIndexOfSubmissionDate = $('#discussionDate').prop('selectedIndex');
				}
				var indexCountOfSubmissionEndDate = 0;
				$('#submissionEndDate option').each(function() {
					$(this).css('display', 'block');
					if(indexCountOfSubmissionEndDate>selectedIndexOfSubmissionDate) {
						$(this).css('display', 'none'); //remove if higher dates can be set and hence need to be shown
					} else {
						if(indexCountOfSubmissionEndDate==selectedIndexOfSubmissionDate) {
							$(this).attr('selected', 'selected');	
							$(this).css('display', 'block');
							$(this).siblings("[value='"+ $(this).val() +"']").css('display', 'none');
						} else {
							$(this).siblings("[value='"+ $(this).val() +"']").css('display', 'none');
						}						
					}					
					indexCountOfSubmissionEndDate++;
				});
			}
		}
		
		function populateDepartments(discussionDateValue) {
			$('#allDepartments option').each(function() {
				$(this).css('display', 'block');			
			});
			if(discussionDateValue==undefined || discussionDateValue=='' || discussionDateValue=='0') {	
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					var departmentsCount = $('#discussionDate'+recordIndex+'_departmentsCount').val();
					if(departmentsCount!=undefined && departmentsCount>0) {
						for(var deptIndex=1; deptIndex<=departmentsCount; deptIndex++) {
							$('#allDepartments option[value="'+$('#discussionDate'+recordIndex+'_department'+deptIndex).val()+'"]').css('display', 'none');
						}
					}				
				});
			} else {
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					if($('#discussionDate'+recordIndex).val()!=discussionDateValue) {
						var departmentsCount = $('#discussionDate'+recordIndex+'_departmentsCount').val();
						if(departmentsCount!=undefined && departmentsCount>0) {
							for(var deptIndex=1; deptIndex<=departmentsCount; deptIndex++) {
								$('#allDepartments option[value="'+$('#discussionDate'+recordIndex+'_department'+deptIndex).val()+'"]').css('display', 'none');
							}
						}
					}				
				});
			}
		}
		
		//reset discussionDateForm
		function resetDiscussionDateForm() {
			$('#discussionDate').val("0");
			$('#discussionDate option').each(function() {
				$(this).css('display', 'block');
			});
			$('#submissionEndDate').val("0");
			$('#submissionEndDate option').each(function() {
				$(this).css('display', 'block');
			});
			$('#selectedDepartments').empty();
			$('#allDepartments').empty();
			$('#allDepartments').html($('#allDepartmentsMaster').html());
			$('#allDepartments option').each(function() {
				$(this).css('display', 'block');
			});
		}
		
		$(document).ready(function(){
			if($('#discussionDatesCount').val()==0) {		
				$('#editDiscussionDate').hide();
				$('#deleteDiscussionDate').hide();
			}
			
			$('#discussionDatesDiv').delegate('.departmentDateRecord', 'click', function() {
				var recordId = $(this).attr('id');						
				if($('#'+recordId).css('background-color')=='rgb(255, 239, 143)') { //row is de-selected
					var recordIndex = recordId.split("_")[1];
					if(recordIndex % 2 == 1) {
						$('#'+recordId).css('background-color', '#dae4ec');
					} else {
						$('#'+recordId).css('background-color', '#c6d3dd');
					}
					//reset discussionDateForm
					resetDiscussionDateForm();
				} else { //row is selected
					$('.departmentDateRecord').each(function() {
						var recordIndex = $(this).attr('id').split("_")[1];
						if(recordIndex % 2 == 1) {
							$(this).css('background-color', '#dae4ec');
						} else {
							$(this).css('background-color', '#c6d3dd');
						}				
					});				
					$('#'+recordId).css('background-color', '#ffef8f'); //optionally #ffff99
					//reset discussionDateForm
					resetDiscussionDateForm();
					//populate discussionDateForm for selected record
					populateDiscussionDateForm(recordId);							
				}			
			});
			
			$('#discussionDatesDiv').delegate('.departmentDateRecord', 'dblclick', function() {
				var recordId = $(this).attr('id');						
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					if(recordIndex % 2 == 1) {
						$(this).css('background-color', '#dae4ec');
					} else {
						$(this).css('background-color', '#c6d3dd');
					}				
				});				
				$('#'+recordId).css('background-color', '#ffef8f'); //optionally #ffff99			
				//reset discussionDateForm
				resetDiscussionDateForm();
				//populate discussionDateForm for selected record
				populateDiscussionDateForm(recordId);			
				$('#editDiscussionDate').click(); //fire edit event for the selected record
			});
			
			$('#addDiscussionDate').click(function() {
				$('#update_failed_div').hide();
				$('#update_success_div').hide();
				$('#discussionDateFormFieldSetLegend').empty();
				$('#discussionDateFormFieldSetLegend').html($('#addDiscussionDateLabel').val());
				$('#discussionDateFormDiv').show();
				//reset discussionDateForm
				resetDiscussionDateForm();
				populateDiscussionDates($('#discussionDate').val());
				populateSubmissionEndDates($('#discussionDate').val(), false);			
				populateDepartments($('#discussionDate').val());
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					if(recordIndex % 2 == 1) {
						$(this).css('background-color', '#dae4ec');
					} else {
						$(this).css('background-color', '#c6d3dd');
					}				
				});
				$('#discussionDatesTableDiv').hide();
				$('#addDiscussionDate').hide();
				$('#editDiscussionDate').hide();
				$('#deleteDiscussionDate').hide();
				$('#successMessageDiv').hide();
				$('#errorMessageDiv').hide();
				$('.tright').hide();
				$('#saveDiscussionDate').show();
				$('#cancelDiscussionDate').show();
			});
			
			$('#editDiscussionDate').click(function() {
				$('#update_failed_div').hide();
				$('#update_success_div').hide();
				if($('#discussionDate').val()=='0') {
					$.prompt('Please select a record to edit!');
					return false;
				}	
				$('#discussionDateFormFieldSetLegend').empty();
				$('#discussionDateFormFieldSetLegend').html($('#editDiscussionDateLabel').val());
				$('#discussionDateFormDiv').show();
				$('#discussionDatesTableDiv').hide();
				$('#addDiscussionDate').hide();
				$('#editDiscussionDate').hide();
				$('#deleteDiscussionDate').hide();
				$('#successMessageDiv').hide();
				$('#errorMessageDiv').hide();
				$('.tright').hide();
				$('#saveDiscussionDate').show();
				$('#cancelDiscussionDate').show();
			});		
			
			$('#discussionDate').change(function() {
				populateSubmissionEndDates($(this).val(), true);
			});
			
			$('#submissionEndDate').click(function() {
				if($('#discussionDate').val()==undefined || $('#discussionDate').val()=='' || $('#discussionDate').val()=='0') {
					$.prompt("Please select the discussion date!");
				}
			});
			
			$('#submissionEndDate').change(function() {
				if($('#discussionDate').val()==undefined || $('#discussionDate').val()=='' || $('#discussionDate').val()=='0') {
					$.prompt("Please select the discussion date!");
				}
			});
			
			$('#saveDiscussionDate').click(function() {
				if($("#discussionDate").val()==undefined || $("#discussionDate").val()=='' || $("#discussionDate").val()=='0') {
					$.prompt("Please select the discussion date!");
					return false;
				}			
				if($("#submissionEndDate").val()==undefined || $("#submissionEndDate").val()=='' || $("#submissionEndDate").val()=='0') {
					$.prompt("Please select the submission end date!");
					return false;
				}
				//for leader of opposition, add validation on 'selectedDepartments' for atleast one department
				var usergroupType = $('#usergroupType').val();
				if(usergroupType=='leader_of_opposition'
						&& ($("#selectedDepartments option").length==undefined || $("#selectedDepartments option").length==0)) {
					$.prompt("Please select atleast one department for the discussion date!");
					return false;
				}
				
				$.prompt($('#discussionDateSubmissionPrompt').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$("#selectedDepartments option").each(function(){
							$(this).attr('selected', 'selected');
						});
			        	var param = "discussionDate="+$("#discussionDate").val()+
									"&submissionEndDate="+$("#submissionEndDate").val()+
									"&selectedDepartmentsForDiscussionDate="+$("#selectedDepartments").val();
			        	
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			        	$.post("cutmotiondate/"+$("#id").val()+"/discussiondate?"+param,
			    	            function(data){
			        				$('#discussionDatesDiv').html(data);
			        				var currentVersion = parseInt($('#version').val());
			       					$('#version').val(currentVersion + 1);
			       					//reset discussionDateForm
			       					resetDiscussionDateForm();
			       					$('#discussionDateFormDiv').hide();
			       					$('#saveDiscussionDate').hide();
			       					$('#cancelDiscussionDate').hide();
			       					$('#successMessageDiv').show();
			       					$('#discussionDatesTableDiv').show();	
			       					$('.departmentDateRecord').each(function() {
			       						var recordIndex = $(this).attr('id').split("_")[1];
			       						if(recordIndex % 2 == 1) {
			       							$(this).css('background-color', '#dae4ec');
			       						} else {
			       							$(this).css('background-color', '#c6d3dd');
			       						}				
			       					});
			       					//reset discussionDateForm
			       					resetDiscussionDateForm();
			       					$('#addDiscussionDate').show();
			       					$('#editDiscussionDate').show();
			       					$('#deleteDiscussionDate').show();
			       					$('.tright').show();
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');
			    					$.unblockUI();	   				 	   				
			    	            }		    	       
			        	).fail(function(){
			    					$.unblockUI();
			    					$('#errorMessageDiv').show();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				}
			        	);
			        }
				}});
				return false;	
			});
			
			$('#deleteDiscussionDate').click(function() {
				$('#update_failed_div').hide();
				$('#update_success_div').hide();
				if($('#discussionDate').val()=='0') {
					$.prompt('Please select a record to delete!');
					return false;
				}
				$.prompt($('#discussionDateRemovalPrompt').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	var param = "discussionDate="+$("#discussionDate").val();
			        	
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			        	$.post("cutmotiondate/"+$("#id").val()+"/discussiondate/removal?"+param,
			    	            function(data){
			        				$('#discussionDatesDiv').html(data);
			        				var currentVersion = parseInt($('#version').val());
			       					$('#version').val(currentVersion + 1);
			       					$('#successMessageDiv').show();
			       					$('.departmentDateRecord').each(function() {
			       						var recordIndex = $(this).attr('id').split("_")[1];
			       						if(recordIndex % 2 == 1) {
			       							$(this).css('background-color', '#dae4ec');
			       						} else {
			       							$(this).css('background-color', '#c6d3dd');
			       						}				
			       					});
			       					//reset discussionDateForm
			       					resetDiscussionDateForm();
			       					$('#addDiscussionDate').show();
			       					//show/hide edit and delete icons if existing rows are there or not
			       					if($('#discussionDatesCount').val()!=undefined && $('#discussionDatesCount').val()>0) {
			       						$('#editDiscussionDate').show();
			       						$('#deleteDiscussionDate').show();
			       					} else {
			       						$('#editDiscussionDate').hide();
			       						$('#deleteDiscussionDate').hide();
			       					}
			       					$('.tright').show();
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');
			    					$.unblockUI();	   				 	   				
			    	            }		    	       
			        	).fail(function(){
			    					$.unblockUI();
			    					$('#errorMessageDiv').show();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				}
			        	);
			        }
				}});
				return false;
			});
			
			$('#cancelDiscussionDate').click(function() {
				//reset discussionDateForm
				resetDiscussionDateForm();
				$('#discussionDateFormDiv').hide();
				$('#saveDiscussionDate').hide();
				$('#cancelDiscussionDate').hide();
				$('#discussionDatesTableDiv').show();
				$('.departmentDateRecord').each(function() {
					var recordIndex = $(this).attr('id').split("_")[1];
					if(recordIndex % 2 == 1) {
						$(this).css('background-color', '#dae4ec');
					} else {
						$(this).css('background-color', '#c6d3dd');
					}				
				});
				$('#addDiscussionDate').show();
				//show/hide edit and delete icons if existing rows are there or not
				if($('#discussionDatesCount').val()!=undefined && $('#discussionDatesCount').val()>0) {
					$('#editDiscussionDate').show();
					$('#deleteDiscussionDate').show();
				} else {
					$('#editDiscussionDate').hide();
					$('#deleteDiscussionDate').hide();
				}				
				$('.tright').show();
			});
			
			//****Departments Selection Related Script (for Custom Multiselect)****//
			/** Remove Default Selected Items From All Items Box **/
			$("#selectedDepartments option").each(function() {
				$("#allDepartments option[value='"+$(this).val()+"']").remove();
			});
			/** Move Selected Departments To Select Box **/
			$("#to2").click(function(){
				$("#allDepartments option:selected").each(function(){
					$("#selectedDepartments").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
					$("#allDepartments option[value='"+$(this).val()+"']").remove();				
				});
			});
			/** Move Selected Departments To All Departments Box **/
			$("#to1").click(function(){
				$("#selectedDepartments option:selected").each(function(){
					$("#allDepartments").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
					$("#selectedDepartments option[value='"+$(this).val()+"']").remove();				
				});
			});
			/** for moving items up **/
			$(".up").click(function(){
				//get the currently slected item and its index
				var current=$("#selectedDepartments option:selected");
				var index=parseInt(current.index());
					//if index is not 0 then proceed
					if(index!=0){
						//swap current with previous
						var prev=$("#selectedDepartments option:eq("+(index-1)+")");
						var prevVal=prev.val();
						var prevText=prev.text();
						prev.val(current.val());
						prev.text(current.text());
						current.val(prevVal);
						current.text(prevText);	
						//set previous as selected and remove selection from current
						prev.attr("selected","selected");
						current.removeAttr("selected");								
					}
				
			});
			/** for moving items down **/	
			$(".down").click(function(){
				//get the currently slected item and its index			
				var current=$("#selectedDepartments option:selected");
				var index=parseInt(current.index());
				var length=$("#selectedDepartments option").length;
				//if end of items is not reached then proceed
					if(index!=length-1){
						//swap current with next				
						var next=$("#selectedDepartments option:eq("+(index+1)+")");
						var nextVal=next.val();
						var nextText=next.text();
						next.val(current.val());
						next.text(current.text());
						current.val(nextVal);
						current.text(nextText);	
						//set next as selected and remove selection from current
						next.attr("selected","selected");
						current.removeAttr("selected");					
					}			
			});
			
			$("#actor").change(function(){
			    var actor=$(this).val();
			    var temp=actor.split("#");
			    $("#level").val(temp[2]);		    
			    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
			    $("#actorName").val(temp[4]);
			    $("#actorName").css('display','inline');
		    });			
		       
		    /**** Internal Status Changes ****/   
		    $("#changeInternalStatus").change(function(){
			    var value=$(this).val();
			    if(value!='-'){
				    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();
				    //var type = $("#internalStatusMaster option[value='"+ value +"']").text();
				    //console.log(type);
				    //if(type=='cutmotiondate_recommend_datesendback' || type=='cutmotiondate_recommend_datediscuss'){
				    //	loadActors($("#internalStatus").val());
				    //}else{
				    	loadActors(value);
				    //}
				    /* $("#submit").attr("disabled","disabled");
				    $("#startworkflow").removeAttr("disabled");	 */	    
			    }else{
				    $("#actor").empty();
				    $("#actorDiv").hide();
				    $("#internalStatus").val($("#oldInternalStatus").val());
				    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
				    $("#startworkflow").attr("disabled","disabled");
				    $("#submit").removeAttr("disabled");
				}		    
		    });
			//$("#submit").removeAttr("disabled");
		    /**** Put Up ****/
			$("#startworkflow").click(function(e){
				//removing <p><br></p>  from wysiwyg editor
				$(".wysiwyg").each(function(){
					var wysiwygVal=$(this).val().trim();
					if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
						$(this).val("");
					}
				});			
				$.prompt($('#startWorkflowMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
			        	$.post($('form').attr('action')+'?username='+ $('#authusername').val() +'&operation=startworkflow',  
			    	            $("form").serialize(),  
			    	            function(data){
			       					$('.tabContent').html(data);
			       					$('html').animate({scrollTop:0}, 'slow');
			       				 	$('body').animate({scrollTop:0}, 'slow');	
			    					$.unblockUI();	   				 	   				
			    	            }).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				});
	    	            }
				}});			
		        return false;  
		    });
		    
			$('#subdepartmentMaster').hide();
			$('#subdepartmentDateMaster').hide();
			$("#actorDiv").show();
			$('#addDepartment').click(function(){
				addDepartment();
			});	
			
			$("#submitcutmotiondate").click(function(){
				var param = "?usergroup="+$("#usergroup").val()+
						"&usergroupType="+$("#usergroupType").val()+
						"&role="+$("#role").val()+"&operation=updatedept";
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post($("form[action='workflow/cutmotiondate']").attr('action')+param,
						$("form[action='workflow/cutmotiondate']").serialize(),function(data){
						$.unblockUI();	   				 	   				
			        });
			});
		});
			
		
	  	function split( val ) {
			return val.split( /,\s*/ );
		}	
		function extractLast( term ) {
			return split( term ).pop();
		}
	</script>
	<style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
        #clubbedQuestionTextsDiv{
        	background: none repeat-x scroll 0 0 #FFF;
		    box-shadow: 0 2px 5px #888888;
		    max-height: 260px;
		    right: 0;
		    position: fixed;
		    top: 10px;
		    width: 300px;
		    z-index: 10000;
		    overflow: auto;
		    border-radius: 10px;
	    }
	    
	    .strippedTable tr:hover {
	          /* background-color: #ffff99 !important; */
	    }
	    
	    .imageLink{
			width: 20px;
			height: 20px;	
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
		
		.btnMS{
			width:30px;
			margin: 5px;
			font-size: 13px; 
			padding: 5px;
			/*display: inline;*/
		}
    </style>
</head> 

<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${error!=''}">
		<h3 style="color: #FF0000;">${error}</h3>
	</c:if>
	<div class="fields clearfix watermark">	
		<div id="assistantDiv">
			<form:form action="workflow/cutmotiondate" method="PUT" modelAttribute="domain">
				<%@ include file="/common/info.jsp" %>
				<h2>${formattedDeviceType}</h2>
				<form:errors path="version" cssClass="validationError"/>
				
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.houseType" text="House Type"/>*</label>
					<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
					<input id="houseType" name="houseType" value="${houseType}" type="hidden">
					<form:errors path="houseType" cssClass="validationError"/>			
				</p>
				
				<p style="display:none;">
					<label class="small"><spring:message code="cutmotion.year" text="Year"/>*</label>
					<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
					<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
				</p>
				
				<p style="display:none;">
					<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
					<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
					<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
					<input type="hidden" id="session" name="session" value="${session}"/>
					<form:errors path="session" cssClass="validationError"/>	
				</p>
				
				<p style="display:none;">
					<label class="small"><spring:message code="device.type" text="Type"/>*</label>
					<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
					<input id="deviceType" name="deviceType" value="${deviceType}" type="hidden">		
					<form:errors path="deviceType" cssClass="validationError"/>		
				</p>
					
				<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus}">
				<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
				
				<p>
					<label class="small"><spring:message code="device.type" text="Type"/>*</label>
					<input id="formattedDeviceType" name="formattedDeviceType" value="${formattedDeviceType}" class="sText" readonly="readonly">
				</p>
				
				<p id="internalStatusDiv">
					<label class="small"><spring:message code="cutmotiondate.currentStatus" text="Current Status"/></label>
					<input class="sText" id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
				</p>
		
				<p style="display: none;">
					<select id="internalStatusMaster">
						<c:forEach items="${internalStatuses}" var="i">
							<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
						</c:forEach>
					</select>
				</p>
				<c:choose>		
					<c:when test="${usergroupType=='leader_of_opposition' and workflowstatus=='COMPLETED'}">
						<p style="display: none;">
							${usergroupType}
						</p>
					</c:when>
					<c:otherwise>
						<p>
							<label class="small"><spring:message code="cutmotiondate.putupfor" text="Put up for"/></label>
							<select id="changeInternalStatus" class="sSelect">
							<option value="-"><spring:message code='please.select' text='Please Select'/></option>
							<c:forEach items="${internalStatuses}" var="i">
								<c:choose>
									<c:when test="${i.id==internalStatus}">
										<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
									</c:when>
									<c:otherwise>
										<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
									</c:otherwise>
								</c:choose>
							</c:forEach>
							</select>	
							<form:errors path="internalStatus" cssClass="validationError"/>	
						</p>
					
						<p id="actorDiv" style="display: none;">
							<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
							<select class="sSelect" name="actor" id="actor">
								<c:if test="${not(empty actors)}">
									<c:forEach items="${actors}" var="a">
										<option value="${a.value}"> ${a.name}</option>				
									</c:forEach>
								</c:if>
							</select>
							<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
						</p>	
					</c:otherwise>
				</c:choose>
				<%-- <p>
					<label class="wysiwyglabel"><spring:message code="cutmotion.remarks" text="Remarks"/></label>
					<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
				</p>
					 --%>
					 
				<div id="discussionDateOpearators" style="margin-top: 20px;margin-left: 10px;">
					<a href="#" id="addDiscussionDate" style="margin-right: 10px;text-decoration: none;">
						<img src="./resources/images/add.jpg" title="<spring:message code='cutmotiondate.addDiscussionDate' text='Add Discussion Date'></spring:message>" class="imageLink" />
					</a>		
					<a href="#" id="editDiscussionDate" style="margin-right: 10px;text-decoration: none;">
						<img src="./resources/images/edit_circular.png" title="<spring:message code='cutmotiondate.editDiscussionDate' text='Edit Discussion Date'></spring:message>" class="imageLink" />
					</a>
					<a href="#" id="saveDiscussionDate" style="margin-right: 10px;text-decoration: none;display: none;">
						<img src="./resources/images/save.jpg" title="<spring:message code='cutmotiondate.saveDiscussionDate' text='Save Discussion Date'></spring:message>" class="imageLink" />
					</a>
					<a href="#" id="cancelDiscussionDate" style="margin-right: 10px;text-decoration: none;display: none;">
						<img src="./resources/images/cancel.png" title="<spring:message code='cutmotiondate.cancelDiscussionDate' text='Cancel Discussion Date'></spring:message>" class="imageLink" />
					</a>
					<a href="#" id="deleteDiscussionDate" style="margin-right: 10px;text-decoration: none;">
						<img src="./resources/images/delete.jpg" title="<spring:message code='cutmotiondate.deleteDiscussionDate' text='Delete Discussion Date'></spring:message>" class="imageLink" />
					</a>
				</div>
				<div id="successMessageDiv" class="toolTip tpGreen clearfix" style="display: none;">
					<p style="font-size: 14px;">
						<img src="./resources/images/template/icons/light-bulb-off.png">
						<spring:message code="update_success" text="Data saved successfully."/>
					</p>
					<p></p>
				</div>
				<div id="errorMessageDiv" class="toolTip tpRed clearfix" style="display: none;">
					<p style="font-size: 14px;">
						<img src="./resources/images/template/icons/light-bulb-off.png">
						<spring:message code="update_failed" text="Please correct following errors."/>
					</p>
					<p></p>
				</div>
				<div id="discussionDateFormDiv" style="display: none;">
					<fieldset id="discussionDateFormFieldSet" style="border: 1px solid;padding: 1em;display: block;">
						<legend id="discussionDateFormFieldSetLegend" style="font-size: 14px;font-weight: bold;background-color: lightblue;">
							<!-- <a href="#"><img src="./resources/images/arrow_collapse.jpg" style="width: 12px;height: 12px;"/></a> -->
							<spring:message code="cutmotiondate.addDiscussionDate" text="Add Discussion Date"/>
						</legend>
						<p>
							<label class="small"><spring:message code="cutmotiondate.discussionDate" text="Discussion Date"/></label>
							<select id="discussionDate" name="discussionDate" class="sSelect">
								<option value="0"><spring:message code="please.select" text="Please Select"/></option>
								<c:forEach items="${discussionDates}" var="i">
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
								</c:forEach>
							</select>			
						</p>
						<p>
							<label class="small"><spring:message code="cutmotiondate.submissionEndDate" text="Submission End Date"/></label>
							<select id="submissionEndDate" name="submissionEndDate" class="sSelect">
								<option value="0"><spring:message code="please.select" text="Please Select"/></option>
								<c:forEach items="${submissionEndDates}" var="i">
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
								</c:forEach>
							</select>
						</p>
						<p>
							<label class="small"><spring:message code="cutmotiondate.departments" text="Departments"/></label>
							<table style="float: right;margin-top: -20px; margin-right: 145px;">
								<tr>
									<td>
										<select id="allDepartments" multiple="multiple" size="10" style="width:250px;">
											<c:forEach items="${departments}" var="i">
												<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
											</c:forEach>
										</select>
										<select id="allDepartmentsMaster" style="display: none;">
											<c:forEach items="${departments}" var="i">
												<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
											</c:forEach>
										</select>
									</td>
									<td>
										<input type="button" id="to2" value="&gt;" class="btnMS" />
										<!-- <input type="button" id="allTo2" value="&gt;&gt;" class="btnMS" /> -->
										<br>
										<!-- <input type="button" id="allTo1" value="&lt;&lt;" class="btnMS" /> -->
										<input type="button" id="to1" value="&lt;" class="btnMS"  />
									</td>
									<td>
										<select id="selectedDepartments" name="selectedDepartments" multiple="multiple" size="10" style="width:250px;">
										</select>
									</td>
									<td>
										<input id="up" type="button" value="&#x2191;" class="up btnMS"/>
										<br>
										<input id="down" type="button" value="&#x2193;" class="down btnMS"/>
									</td>
								</tr>
							</table>
						</p>				
					</fieldset>
				</div>
				<div id="discussionDatesTableDiv">
					<table id="discussionDatesTable" style="width: 100%;" class="strippedTable">
						<thead>
							<tr>
								<th class="expand" width="25%">
									<spring:message code="cutmotiondate.discussionDate" text="Discussion Date"/>
								</th>
								<th class="expand" width="50%">
									<label><spring:message code="cutmotiondate.departments" text="Departments with Priority"/></label>
								</th>
								<th class="expand" width="25%">
									<spring:message code="cutmotiondate.submissionEndDate" text="Submission End Date"/>
								</th>
							</tr>
						</thead>
						<tbody id="discussionDatesDiv">
							<%@ include file="/WEB-INF/views/cutmotiondate/discussion_dates_table.jsp" %>
						</tbody>
					</table>
				</div>
					
				<div class="fields">
					<h2></h2>
					<p class="tright">
			
						<c:if test="${workflowstatus=='PENDING'}">
							<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">			
						</c:if>
						<c:if test="${workflowstatus=='COMPLETED'}">
							<input id="submitcutmotiondate" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
						</c:if>
				</div>
				<form:hidden path="id"/>
				<form:hidden path="locale"/>
				<form:hidden path="version"/>
				
				<input type="hidden" name="level" id="level" value="${level}" />
				<input type="hidden" name="status" id="status" value="${status }">
				<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
				<input type="hidden" name="setCreatedOn" id="setCreatedOn" value="${creationDate }">
				<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
				<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
				<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
				<input id="role" name="role" value="${role}" type="hidden">
				<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
				<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
				<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
				<input type="hidden" id="workflowDetails" name="workflowDetails" value="${workflowdetails}" />
				<input type="hidden" id="actorLength" value="${fn:length(actors)}" />
			</form:form>
		</div>
	</div>
	
	<input type="hidden" id="workflowStatus" value="${workflowstatus}" />
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="addDiscussionDateLabel" value="<spring:message code='cutmotiondate.addDiscussionDate' text='Add Discussion Date'/>" type="hidden">
	<input id="editDiscussionDateLabel" value="<spring:message code='cutmotiondate.editDiscussionDate' text='Edit Discussion Date'/>" type="hidden">
	<input id="discussionDateSubmissionPrompt" value="<spring:message code='client.prompt.cutmotiondate.discussionDateSubmissionPrompt' text='Do you want to save the discussion date?'/>" type="hidden">
	<input id="discussionDateRemovalPrompt" value="<spring:message code='client.prompt.cutmotiondate.discussionDateRemovalPrompt' text='Do you really want to remove the discussion date?'/>" type="hidden">
	<input id="confirmDateSubmission" value="<spring:message code='confirm.cutmotiondatesubmission.message' text='Do you want to submit the Cut Motion Date?'></spring:message>" type="hidden">
	<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='cutmotiondate.startworkflowmessage' text='Do You Want To Put Up Cutmotion Date?'></spring:message>" type="hidden">
	<input id="oldStatus" value="${status}" type="hidden">
	<input type="hidden" id="oldInternalStatus" value="${internalstatus}" />
	<input type="hidden" id="oldRecommendationStatus" value="${recommendationstatus}" />
	<input id="deviceType" type="hidden" value="${deviceTypeType}" />
	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>