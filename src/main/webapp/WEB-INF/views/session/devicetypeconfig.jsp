<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.devicetypeconfig.edit" text="Edit Session Config"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title></title>
	<script type="text/javascript">	
		var countOfQuestionsToBeAskedForFactualPosition = 0;
		var countOfQuestionsToBeAskedForClarificationFromMember = 0;
		var countOfQuestionsToBeAskedForClarificationFromDepartment = 0;
	
		//fill contents required for the device type fields 
		//called on document.ready & devicetype change events
		function fillContent(deviceTypeSelected){
			//console.log(deviceTypeSelected);
			//select div of selected device type
		    var selectedDiv='#'+deviceTypeSelected;
			
		    //--------------------Ballot Config-----------------------//
		    
		    //initialize isBallotingRequired
		    var isBallotingRequired = "#"+$('#deviceType').val()+"_isBallotingRequired";		    
		    var isBallotingRequiredValue = $(isBallotingRequired).val();		  	
		    if(isBallotingRequiredValue != undefined){
			    if(isBallotingRequiredValue.length==4){
			    	
			    	$(isBallotingRequired).attr('checked','checked');
			    	
			    }else if(isBallotingRequiredValue.length==5){
			    	
			    	$(isBallotingRequired).removeAttr('checked');
			    }
		    }
		   
		  	//initialize ballot fields
			if(deviceTypeSelected == 'motions_standalonemotion_halfhourdiscussion' || deviceTypeSelected == 'resolutions_nonofficial') {
				//pre-select ballot type which is already set
				var ballotType=$("#"+deviceTypeSelected+"_ballotType_hidden").attr('title');			
				$('#'+deviceTypeSelected+'_ballotType').val(ballotType);
				
				//pre-select ballot events which are already set
				if($("#"+deviceTypeSelected+"_ballotEvents_hidden").attr('title')!=""){					
					var ballotEvents=$("#"+deviceTypeSelected+"_ballotEvents_hidden").attr('title').split("#");
					var i;					
					for(i = 0; i < ballotEvents.length; i++){
						$("#"+deviceTypeSelected+"_ballotEvents option").each(function(){
							if(ballotEvents[i]==$(this).val()){
								//$(this).prop('selected',true);
								$(this).attr('selected','selected');
							}	
						});
					}
				}
				
				//show/hide ballot fields depending on isBallotingRequired
				if($('#'+deviceTypeSelected+'_isBallotingRequired').is(':checked')){
					$('.'+ deviceTypeSelected+ '_ballotfields').show();			
				}else {
					$('.'+ deviceTypeSelected+ '_ballotfields').hide();
				}
			}
			
		  	//initialize numberOfSupportingMembersComparator
			if(
			    (deviceTypeSelected=="motions_standalonemotion_halfhourdiscussion")
			  ||(deviceTypeSelected=="questions_halfhourdiscussion_from_question")
			  ||(deviceTypeSelected=="questions_starred")
			  ||(deviceTypeSelected=="questions_unstarred")
			  ||(deviceTypeSelected=="questions_shortnotice")
			  ||(deviceTypeSelected=="motions_discussionmotion_lastweek")
			  ||(deviceTypeSelected=="motions_discussionmotion_publicimportance")
			  ||(deviceTypeSelected=="motions_discussionmotion_shortduration")
			  ||(deviceTypeSelected=="motions_rules_suspension")
			  	  
			) 
			{
				var comparator=$("#"+deviceTypeSelected+"_numberOfSupportingMembersComparator_hidden").attr('title');	    
				$('#'+deviceTypeSelected+'_numberOfSupportingMembersComparator').val(comparator);
			}			
			
			//------------------------initialize discussion days & dates---------------------//
		    if(deviceTypeSelected == 'motions_standalonemotion_halfhourdiscussion' 
		    	|| deviceTypeSelected == 'questions_halfhourdiscussion_from_question'
		    	|| deviceTypeSelected == 'resolutions_nonofficial'
		    	|| deviceTypeSelected == 'bills_nonofficial') {
				//if no discussion days are selected, hide discussion dates paragraph
		    	if($("#"+deviceTypeSelected+"_discussionDays_hidden").attr('title')==""){
					$("#"+deviceTypeSelected+"_discussionDates_para").hide();
				}
				//else pre-select discussionDays which are already set
		    	else{
					var discussionDays=$("#"+deviceTypeSelected+"_discussionDays_hidden").attr('title').split("#");
					var i;
					
					for(i = 0; i < discussionDays.length; i++){
						$("#"+deviceTypeSelected+"_discussionDays option").each(function(){
							if(discussionDays[i]==$(this).val()){
								$(this).prop('selected',true);
							}	
						});
					}	
					
					//pre-select discussionDates for pre-selected discussion days					
					getDiscussionDates(deviceTypeSelected, discussionDays);
					
					//---to highlight the selected discussion dates---//
					var discussionDates=$("#"+deviceTypeSelected+"_discussionDates_hidden").attr('title').split("#");			
					var i;
					for(i = 0; i < discussionDates.length; i++){
						$("#"+deviceTypeSelected+"_discussionDates option").each(function(){							
							if(discussionDates[i]==$(this).val()){
								$(this).prop('selected',true);
							}	
						});
					}				
				}
			}
			
			//initialize bill types
			if(deviceTypeSelected == 'bills_nonofficial' || deviceTypeSelected == 'bills_government') {
				if($("#"+deviceTypeSelected+"_billTypesAllowed_hidden").attr('title')!=""){								
					var billTypesAllowed=$("#"+deviceTypeSelected+"_billTypesAllowed_hidden").attr('title').split("#");
					var i;					
					for(i = 0; i < billTypesAllowed.length; i++){
						$("#"+deviceTypeSelected+"_billTypesAllowed option").each(function(){
							if(billTypesAllowed[i]==$(this).val()){
								//$(this).prop('selected',true);
								$(this).attr('selected','selected');
							}	
						});
					}					
				}
			}			
			
			//initialize bill kinds
			if(deviceTypeSelected == 'bills_nonofficial' || deviceTypeSelected == 'bills_government') {
				if($("#"+deviceTypeSelected+"_billKindsAllowed_hidden").attr('title')!=""){							
					var billKindsAllowed=$("#"+deviceTypeSelected+"_billKindsAllowed_hidden").attr('title').split("#");
					var i;					
					for(i = 0; i < billKindsAllowed.length; i++){
						$("#"+deviceTypeSelected+"_billKindsAllowed option").each(function(){
							if(billKindsAllowed[i]==$(this).val()){
								//$(this).prop('selected',true);
								$(this).attr('selected','selected');
							}	
						});
					}					
				}
			}
			
			//initialize languages allowed & schedule 7 of constitution for them
			if(deviceTypeSelected == 'bills_nonofficial' || deviceTypeSelected == 'bills_government') {
				if($("#"+deviceTypeSelected+"_languagesAllowed_hidden").attr('title')!=""){					
					var languagesAllowed=$("#"+deviceTypeSelected+"_languagesAllowed_hidden").attr('title').split("#");
					var i;					
					for(i = 0; i < languagesAllowed.length; i++){
						$("#"+deviceTypeSelected+"_languagesAllowed option").each(function(){
							var currentLanguage = $(this).val();							
							if(languagesAllowed[i]==currentLanguage){
								//$(this).prop('selected',true);
								$(this).attr('selected','selected');
								$('#'+deviceTypeSelected+'_schedule7OfConstitution_para_'+currentLanguage).show();
							}	
						});
					}					
				}
			}
			
			//initialize languages compulsory
			if(deviceTypeSelected == 'bills_nonofficial' || deviceTypeSelected == 'bills_government') {
				if($("#"+deviceTypeSelected+"_languagesCompulsory_hidden").attr('title')!=""){					
					var languagesCompulsory=$("#"+deviceTypeSelected+"_languagesCompulsory_hidden").attr('title').split("#");
					var i;					
					for(i = 0; i < languagesCompulsory.length; i++){
						$("#"+deviceTypeSelected+"_languagesCompulsory option").each(function(){
							if(languagesCompulsory[i]==$(this).val()){
								//$(this).prop('selected',true);
								$(this).attr('selected','selected');
							}	
						});
					}				
				}
			}
			
		  	//initialize submission end time for resolutions_nonofficial & bills_nonofficial	
		  	if(deviceTypeSelected == 'resolutions_nonofficial' || deviceTypeSelected == 'bills_nonofficial') {
		  		var submissionEndDate = $("#"+deviceTypeSelected+"_submissionEndDate").val();
		  		$("#"+deviceTypeSelected+"_submissionEndTime").val(submissionEndDate.split(" ")[1]);
		  	}
		  	
		  	//initialize questions to be asked in factual position of non-official resolution
		  	if(deviceTypeSelected == 'resolutions_nonofficial'
		  			|| deviceTypeSelected == 'motions_standalonemotion_halfhourdiscussion') {		  		
		  		initializeQuestionsAskedForClarification(deviceTypeSelected, "questionsAskedForFactualPosition");
		  	} else if(deviceTypeSelected == 'questions_starred') {		  		
		  		initializeQuestionsAskedForClarification(deviceTypeSelected, "clarificationFromMemberQuestions");
		  		initializeQuestionsAskedForClarification(deviceTypeSelected, "clarificationFromDepartmentQuestions");
		  	}
		    
		  	//hide all the divs
		    hideDivs();
		  	
		  	//hide 'please select' option in case of already selected case
		    $(selectedDiv).find('.sSelectMultiple, .sSelect').each(function() {		    	
		    	if($(this).val() != "") {
			    	$(this).find('option[value=""]').removeAttr('selected');
			    	$(this).find('option[value=""]').hide();
		    	}
		    	
		    	//apply multiselect with checkboxes on this multiple selectbox
		    	if($(this).is('.sSelectMultiple')) {
		    		$(this).multiSelect();
		    	}
		    });
		  	
		  	//show div of selected device type
		    $(selectedDiv).show();
		    
		}
		
		function initializeQuestionsAskedForClarification(deviceTypeSelected, fieldname) {			
			var clarificationFor = "FactualPosition";
			if(fieldname=="clarificationFromMemberQuestions") {
				clarificationFor = "ClarificationFromMember";
			} else if(fieldname=="clarificationFromDepartmentQuestions") {
				clarificationFor = "ClarificationFromDepartment";
			}			
			var questions = $('#'+deviceTypeSelected+'_'+fieldname).val().split("##");				
	  		var questionsHtml = "";		  		
	  		if(questions.length==1 && questions == '') {
	  			questionsHtml += "<p id='"+deviceTypeSelected+"_"+fieldname+"_para1' class='questionPara'><label class='small questionLabel' style='vertical-align: top; text-align: right;'><spring:message code='question.details' text='Question'/> 1&nbsp;</label><textarea class='sTextArea' cols='50' rows='2' id='"+deviceTypeSelected+"_"+fieldname+"1'></textarea></p>";
	  			if(fieldname=="clarificationFromMemberQuestions") {
	  				countOfQuestionsToBeAskedForClarificationFromMember++;
				} else if(fieldname=="clarificationFromDepartmentQuestions") {
					countOfQuestionsToBeAskedForClarificationFromDepartment++;
				} else {
					countOfQuestionsToBeAskedForFactualPosition++;
				}	  			
	  		} else {
	  			for(var i=0; i<questions.length; i++) {	
	  				if(i==0) {
	  					questionsHtml += "<p id='"+deviceTypeSelected+"_"+fieldname+"_para"+(i+1)+"' class='questionPara'><label class='small questionLabel' style='vertical-align: top; text-align: right;'><spring:message code='question.details' text='Question'/> "+(i+1)+"&nbsp;</label><textarea class='sTextArea' cols='50' rows='2' id='"+deviceTypeSelected+"_"+fieldname+(i+1)+"'>"+questions[i]+"</textarea></p>";
	  				} else {
	  					questionsHtml += "<p id='"+deviceTypeSelected+"_"+fieldname+"_para"+(i+1)+"' class='questionPara'><label class='small questionLabel' style='vertical-align: top; text-align: right;'><spring:message code='question.details' text='Question'/> "+(i+1)+"&nbsp;</label><textarea class='sTextArea' cols='50' rows='2' id='"+deviceTypeSelected+"_"+fieldname+(i+1)+"'>"+questions[i]+"</textarea><input id='"+deviceTypeSelected+"_removeQuestionFor"+clarificationFor+(i+1)+"' type='button' value='Remove Question' class='butDef' onClick='deleteQuestionToBeAskedForClarification(\""+fieldname+"\","+(i+1)+")'></p>";
	  				}
	  				if(fieldname=="clarificationFromMemberQuestions") {
		  				countOfQuestionsToBeAskedForClarificationFromMember++;
					} else if(fieldname=="clarificationFromDepartmentQuestions") {
						countOfQuestionsToBeAskedForClarificationFromDepartment++;
					} else {
						countOfQuestionsToBeAskedForFactualPosition++;
					}
		  		}		  			
	  		}
	  		questionsHtml += "<input id='"+deviceTypeSelected+"_addQuestionFor"+clarificationFor+"' type='button' value='Add Question' class='butDef' style='margin-left: 160px;'>";
	  		$('#'+deviceTypeSelected+'_'+fieldname+'_div').html(questionsHtml);		  		
	  		
	  		$('#'+deviceTypeSelected+'_addQuestionFor'+clarificationFor).click(function() {		
	  			var questionNumber = "";
	  			if(fieldname=="clarificationFromMemberQuestions") {
	  				countOfQuestionsToBeAskedForClarificationFromMember++;
	  				questionNumber = countOfQuestionsToBeAskedForClarificationFromMember;
				} else if(fieldname=="clarificationFromDepartmentQuestions") {
					countOfQuestionsToBeAskedForClarificationFromDepartment++;
					questionNumber = countOfQuestionsToBeAskedForClarificationFromDepartment;
				} else {
					countOfQuestionsToBeAskedForFactualPosition++;
					questionNumber = countOfQuestionsToBeAskedForFactualPosition;
				}
	  			var questionHtml = "";
	  			questionHtml += "<p id='"+deviceTypeSelected+"_"+fieldname+"_para"+questionNumber+"' class='questionPara'><label class='small questionLabel' style='vertical-align: top; text-align: right;'><spring:message code='question.details' text='Question'/> "+questionNumber+"&nbsp;</label><textarea class='sTextArea' cols='50' rows='2' id='"+deviceTypeSelected+"_"+fieldname+questionNumber+"'></textarea><input id='"+deviceTypeSelected+"_removeQuestionFor"+clarificationFor+(questionNumber)+"' type='button' value='Remove Question' class='butDef' onClick='deleteQuestionToBeAskedForClarification(\""+fieldname+"\","+questionNumber+")'></p>";
	  			$('#'+deviceTypeSelected+'_addQuestionFor'+clarificationFor).before(questionHtml);
	  			var countLabel = 0;
				$('#'+deviceTypeSelected+'_'+fieldname+'_div').children('p.questionPara').each(function() {
					countLabel++;
					labelInSequence = "<spring:message code='question.details' text='Question'/> " + countLabel + "&nbsp;";
					$(this).find('label.questionLabel').html(labelInSequence);							
				});
			});
		}
		
		//hide all the divs
		function hideDivs(){
		   $('div.formDiv').hide();		   
		}
		
		//to find discussion dates of given device type for selected discussion days
		function getDiscussionDates(deviceTypeSelected, discussionDays) {
			$.ajax({
		         url: 'ref/session/'+ $("#id").val()+'/devicetypeconfig/' + discussionDays + '/discussiondates',	             
		         async:   false,
		         success: function(data) {
		        	$('#'+deviceTypeSelected+'_discussionDates option').empty();
		        	var options="<option value='' style='display: none;'>----"+$("#pleaseSelectMsg").val()+"----</option>";			
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].id+"'>" + data[i].name	+ "</option>";
					}				
					$('#'+deviceTypeSelected+'_discussionDates').html(options);		
					$('#'+deviceTypeSelected+'_discussionDates').multiSelect();
	             }	         	 
	    	}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		};
		
		function deleteQuestionToBeAskedForClarification(fieldname, questionNumber) {			
			var selectedDeviceType = $('#deviceType').val();			
			$('#'+selectedDeviceType+'_'+fieldname+'_para'+questionNumber).remove();
			var countLabel = 0;
			$('#'+selectedDeviceType+'_'+fieldname+'_div').children('p.questionPara').each(function() {
				countLabel++;
				labelInSequence = "<spring:message code='question.details' text='Question'/> " + countLabel + "&nbsp;";
				$(this).find('label.questionLabel').html(labelInSequence);							
			});			
  		}
		
		$('document').ready(function(){
			initControls();
			
			//add please select option if not exists already for all select & multiselect fields.
			//this is necessary to pass empty value in case no option is/should be selected.
			$('.sSelectMultiple, .sSelect').each(function() {
				//checking whether already present or not.
				if( $(this).find('option[value=""]').val() != '') {					
					$(this).prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");
				}
			});
			
			//get the selected device type for handling corresponding fields
			var deviceTypeSelected=$("#deviceTypeSelected").val();
			$('#deviceType').val(deviceTypeSelected);
			
			//fill contents required for the device type fields
			fillContent(deviceTypeSelected);			
			
			//register datemask on focus event
			$('.datemask').focus(function(){		
				if($(this).val()==""){
					$(".datemask").mask("99/99/9999");
				}
			});		
			//register datetimemask on focus event
			$('.datetimemask').focus(function(){		
				if($(this).val()==""){
					$(".datetimemask").mask("99/99/9999 99:99:99");
				}
			});		
			
			$('.timemask').focus(function(){		
				if($(this).val()==""){
					$(".timemask").mask("99:99:99");
				}
			});	
			
			$('#deviceType').change(function(){	
				//get the selected device type for handling corresponding fields
				deviceTypeSelected=$('#deviceType').val();	
				
				//hide the success/error ribbon which maybe present before device type was changed.
				var ribbon="${type}";				
				if(ribbon=="success"){
					$("#ribbon").hide();	
				}
				
				countOfQuestionsToBeAskedForFactualPosition = 0;
				countOfQuestionsToBeAskedForClarificationFromMember = 0;
				countOfQuestionsToBeAskedForClarificationFromDepartment = 0;
				
				//fill contents required for the device type fields
				fillContent(deviceTypeSelected);	
				
		    });
			
			$('.discussionDays').change(function(){
				//get discussion dates for changed discussion days					
				var discussionDays=$("#"+deviceTypeSelected+"_discussionDays").val();					
				getDiscussionDates(deviceTypeSelected, discussionDays);
				
				//show discussion dates paragraph in case atleast one discussion day is selected					
				if ($("#"+deviceTypeSelected+"_discussionDays option:selected").length > 0) {				
					$("#"+deviceTypeSelected+"_discussionDates_para").show();
				} 
				//hide discussion dates paragraph in case no discussion day is selected
				else {					
					$("#"+deviceTypeSelected+"_discussionDates_para").hide();
				}				
		    });
			
			$('.isBallotingRequired').change(function(){
				//show/hide ballot fields on changing isBallotingRequired 
				if($(this).is(':checked')){					
					$('.'+ deviceTypeSelected+ '_ballotfields').show();
				}else {					
					$('.'+ deviceTypeSelected+ '_ballotfields').hide();
				}
			});
			
			$('.changeFile_schedule7OfConstitution').click(function() {
				var deviceTypeForSchedule7 = $('#deviceType').val();				
				deviceTypeForSchedule7 = deviceTypeForSchedule7.replace(/_/g, "-");
				var linkId = this.id;
				$.prompt($('#confirmFileChangeMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){			        				
						$('#' + $('#deviceType').val() + '_languagesAllowed option:selected').each(function() {					
							var language = this.value;
							if(linkId.indexOf(language) >= 0) {
								var fileid = deviceTypeForSchedule7+"-schedule7OfConstitution-"+language;
								$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
									$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
									$('#image_'+fileid).attr("src","");
									$('#image_'+fileid).hide();
								});
							}					
						});
						$('#'+linkId).hide();
	    	        }
				}});														
		        return false;				
			});
			
			$('.changeFile_instructionalOrder').click(function() {
				var deviceTypeForInstructionalOrder = $('#deviceType').val();				
				deviceTypeForInstructionalOrder = deviceTypeForInstructionalOrder.replace(/_/g, "-");
				var linkId = this.id;
				$.prompt($('#confirmFileChangeMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){			        				
			        	var fileid = deviceTypeForInstructionalOrder+"-instructionalOrder";
						$.get('./common/file_upload.jsp?fileid='+fileid,function(dataupload){
							$('#file_'+fileid+'_downloadUploadedFile').replaceWith(dataupload);
							$('#image_'+fileid).attr("src","");
							$('#image_'+fileid).hide();
						});
						$('#'+linkId).hide();
	    	        }
				}});														
		        return false;				
			});
			
			$('#submit').click(function(){
				//get selected device type
				var deviceTypeSeleted = $('#deviceType').val();
				
				//get div of selected device type
				var selectedDiv = "#" + deviceTypeSeleted;
				
				//flag to check whether there are any ajax errors
				var readyToSubmit = true;
				
				//as value of every multiselect becomes null in case when all options are deselected
				$(selectedDiv).find('.sSelectMultiple').each(function() {	
					$(this).find('option[value=""]').removeAttr('selected');					
					if($(this).val() == null) {						
						$(this).val("");
					}					
				});
				
				//set submission start date for bills_nonofficial such that it is same as that for questions_starred
				if(deviceTypeSeleted == 'bills_nonofficial') {
					var questionSubmissionStartDate = $('#questions_starred_submissionStartDate').val();
					if(questionSubmissionStartDate != "") {
						$('#'+deviceTypeSeleted+'_submissionStartDate').val(questionSubmissionStartDate);
					}
				}
				
				//set submission end date for resolutions_nonofficial
				if(deviceTypeSeleted == 'resolutions_nonofficial' || deviceTypeSeleted == 'bills_nonofficial') {
					var daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession = $('#'+deviceTypeSeleted+'_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession').val();
					if(daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession == "" || daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession == null || daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession == undefined) {
						$.prompt($('#invalidDaysBetweenSubmissionEndDateAndLastDiscussionDateOfSession').val());
						return false;
					} else {
						var discussionDates = $('#'+deviceTypeSeleted+'_discussionDates').val();
						if(discussionDates == "" || discussionDates == null || discussionDates == undefined) {
							$.prompt($('#noDiscussionDateSelected').val());
							return false;
						} else {
							var lastDiscussionDate = discussionDates[discussionDates.length-1];						
							$.ajax({
						         url: 'ref/getLastSubmissionDateFromLastDiscussionDate?lastDiscussionDateStr='+lastDiscussionDate+'&daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession='+daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession,	             
						         async:   false,
						         success: function(data) {
						            var submissionEndTime = $('#'+deviceTypeSeleted+'_submissionEndTime').val();
						        	if(submissionEndTime == "") {
						        		$('#'+deviceTypeSeleted+'_submissionEndDate').val(data.id + " 00:00:00");			        		
						        	} else {
						        		$('#'+deviceTypeSeleted+'_submissionEndDate').val(data.id + " " + submissionEndTime);
						        	}					        	
					             },
					             error: function() {					            	 
					            	 readyToSubmit = false;
					             }
					    	}).fail(function(){
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
								}
								scrollTop();
							});
						}						
					}					
				}
				
				//set questions to be asked in factual position
				if(deviceTypeSeleted == 'resolutions_nonofficial' || deviceTypeSeleted == 'motions_standalonemotion_halfhourdiscussion') {
					var questionsAsked = "";
					for(var i=1; i<=countOfQuestionsToBeAskedForFactualPosition; i++) {
						var questionContent = $('#'+deviceTypeSeleted+'_questionsAskedForFactualPosition'+i).val();
						if(questionContent != undefined && questionContent!= "") {
							questionsAsked += questionContent;
							questionsAsked += "##";
						}						
					}
					questionsAsked = questionsAsked.slice(0,-2);					
					$('#'+deviceTypeSeleted+'_questionsAskedForFactualPosition').val(questionsAsked);					
				} else if(deviceTypeSeleted == 'questions_starred') {
					var questionsAsked = "";
					for(var i=1; i<=countOfQuestionsToBeAskedForClarificationFromMember; i++) {
						var questionContent = $('#'+deviceTypeSeleted+'_clarificationFromMemberQuestions'+i).val();
						if(questionContent != undefined && questionContent!= "") {
							questionsAsked += questionContent;
							questionsAsked += "##";
						}						
					}
					questionsAsked = questionsAsked.slice(0,-2);					
					$('#'+deviceTypeSeleted+'_clarificationFromMemberQuestions').val(questionsAsked);
					questionsAsked = "";
					for(var i=1; i<=countOfQuestionsToBeAskedForClarificationFromDepartment; i++) {
						questionContent = $('#'+deviceTypeSeleted+'_clarificationFromDepartmentQuestions'+i).val();
						if(questionContent != undefined && questionContent!= "") {
							questionsAsked += questionContent;
							questionsAsked += "##";
						}						
					}
					questionsAsked = questionsAsked.slice(0,-2);					
					$('#'+deviceTypeSeleted+'_clarificationFromDepartmentQuestions').val(questionsAsked);
				}				
				
				//set final value for isBallotingRequired
				var isBallotingRequired = "#" + deviceTypeSeleted+"_isBallotingRequired";
				var isBallotingRequiredHidden = "#" +  deviceTypeSeleted+"_isBallotingRequired_Hidden";				
				if($(isBallotingRequired).is(":checked")) {					
					$(isBallotingRequiredHidden).val("true");					
				}else {					
					$(isBallotingRequiredHidden).val("false");
				}
				
				//set empty values for all ballot fields in case no balloting is required
				if(!$('#' +  deviceTypeSeleted+ '_isBallotingRequired').is(':checked')){				
					
					//following commented code not working to unselect options. in fact this is happening for any dropdown
					/* $("#questions_halfhourdiscussion_standalone_ballotType option:selected").each(function(){
						//$(this).prop('selected',false);
						$(this).removeAttr('selected');
					}); */
					//therefore setting value of selectbox to empty
					$('#' +  deviceTypeSeleted+ '_ballotType').val("");
					
					//following commented code not working to unselect options
					/* $("#questions_halfhourdiscussion_standalone_ballotEvents option:selected").each(function(){
						//$(this).prop('selected',false);
						$(this).removeAttr('selected');
					}); */
					//therefore setting value of selectbox to empty
					$('#' +  deviceTypeSeleted+ '_ballotEvents').val("");				
					
				}
				
				if(deviceTypeSeleted == 'bills_nonofficial' || deviceTypeSeleted == 'bills_government') {
					var deviceTypeForFile = deviceTypeSeleted.replace(/_/g, "-");
					$('#' + deviceTypeSeleted + '_languagesAllowed option:selected').each(function() {
						var language = $(this).val();
						var fileTag = $('#'+deviceTypeForFile+'-schedule7OfConstitution-'+language).val();
						$('#'+deviceTypeSeleted+'_schedule7OfConstitution_'+language).val(fileTag);	
						$('#'+deviceTypeForFile+'-schedule7OfConstitution-'+language).removeAttr('name');
					});	
					fileTag = $('#'+deviceTypeForFile+'-instructionalOrder').val();					
					$('#'+deviceTypeSeleted+'_instructionalOrder').val(fileTag);
					$('#'+deviceTypeForFile+'-instructionalOrder').removeAttr('name');					
				}
				
				//set selected device type as form field
				$("#deviceTypeSelected").val(deviceTypeSeleted);				
				
				//add fields of selected device type only to form
				$('#mainForm').append($(selectedDiv));
				
				//submit form in case of no ajax errors
				if(readyToSubmit == true) {
					$('#mainForm').submit();
				} else {
					$.prompt($('#ajaxErrorOccured').val());
					return false;
				}											
			});			
		});
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div class="fields clearfix">
		
		<div id="ribbon">		
			<%@ include file="/common/info.jsp" %>
		</div>
						
		<div id="headerDiv">
			<p> 
				<label class="small"><spring:message code="devicetype.name" text="Device Type"/></label>
				<select id="deviceType" name="deviceType" class="sSelect">
					<option value=""><spring:message code="please.select" text="Please Select"/></option>
					<c:forEach items="${deviceTypesEnabled}" var="i">
					<option value="${i.type}">${i.name}</option>
					</c:forEach>
				</select>
			</p>
		</div>
		
		<form:form action="session/devicetypeconfig" method="POST" modelAttribute="domain" id="mainForm">				
			<form:hidden path="version"  />
			<form:hidden id="id" path="id" />
			<form:hidden path="locale"/>
			<input type="hidden" name="deviceTypeSelected" id="deviceTypeSelected" value="${deviceTypeSelected}"/>
			
		</form:form>
		
		<c:forEach  items="${deviceTypesEnabled}" var="i">
			<c:if test="${i.type eq 'questions_starred'}">					
			<div id="questions_starred" class="formDiv">
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_processingMode" text="Processing Mode" /></label>
					<select id="questions_starred_processingMode" class="sSelect" name="questions_starred_processingMode">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
							<c:when test="${questions_starred_processingmode=='lowerhouse'}">
								<option value="lowerhouse" selected="selected"><spring:message code='generic.lowerhouse' text='Lowerhouse'/></option>
								<option value="upperhouse"><spring:message code='generic.upperhouse' text='Upperhouse'/></option>
							</c:when>
							<c:when test="${questions_starred_processingmode=='upperhouse'}">
								<option value="lowerhouse"><spring:message code='generic.lowerhouse' text='Lowerhouse'/></option>
								<option value="upperhouse" selected="selected"><spring:message code='generic.upperhouse' text='Upperhouse'/></option>
							</c:when>
							<c:otherwise>
								<option value="lowerhouse"><spring:message code='generic.lowerhouse' text='Lowerhouse'/></option>
								<option value="upperhouse"><spring:message code='generic.upperhouse' text='Upperhouse'/></option>
							</c:otherwise>
						</c:choose>
					</select>
				</p>
				
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate" text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionStartDate" id="questions_starred_submissionStartDate" value="${questions_starred_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionEndDate" id="questions_starred_submissionEndDate" value="${questions_starred_submissionenddate}" />
				</p>
				
				<c:if test="${houseType eq 'upperhouse'}">
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionFirstBatchStartDate" text="Submission First Batch Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionFirstBatchStartDate" id="questions_starred_submissionFirstBatchStartDate" value="${questions_starred_submissionfirstbatchstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionFirstBatchEndDate" text="Submission First Batch End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionFirstBatchEndDate" id="questions_starred_submissionFirstBatchEndDate" value="${questions_starred_submissionfirstbatchenddate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionSecondBatchStartDate" text="Submission Second Batch Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionSecondBatchStartDate" id="questions_starred_submissionSecondBatchStartDate" value="${questions_starred_submissionsecondbatchstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionSecondBatchEndDate" text="Submission Second Batch End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionSecondBatchEndDate" id="questions_starred_submissionSecondBatchEndDate" value="${questions_starred_submissionsecondbatchenddate}" />
				</p>				
				</c:if>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_starred_firstBallotDate" id="questions_starred_firstBallotDate" value="${questions_starred_firstballotdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInFirstBatch" text="Number of Question In First Batch" /></label>
					<input type="text" class="sInteger" name="questions_starred_NumberOfQuestionInFirstBatch" id="questions_starred_NumberOfQuestionInFirstBatch" value="${questions_starred_numberofquestioninfirstbatch}" />
				</p>

				<c:if test="${houseType eq 'upperhouse'}">
				<p>
					<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInSecondBatch" text="Number of Question In Second Batch" /></label>
					<input type="text" class="sInteger" name="questions_starred_NumberOfQuestionInSecondBatch" id="questions_starred_NumberOfQuestionInSecondBatch" value="${questions_starred_numberofquestioninsecondbatch}" />
				</p>
				</c:if>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Balloting Required" /></label>
					<input type="checkbox" class="sCheck" id="questions_starred_isBallotingRequired" value="${questions_starred_isballotingrequired}" >
					<input type="hidden" id="questions_starred_isBallotingRequired_Hidden" name="questions_starred_isBallotingRequired" value="" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.rotationOrderPublishingDate" text="Rotation Order Publishing Date " /></label>
					<input id="questions_starred_rotationOrderPublishingDate" name="questions_starred_rotationOrderPublishingDate" class="datetimemask sText" value="${questions_starred_rotationorderpublishingdate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="questions_starred_numberOfSupportingMembersComparator" id="questions_starred_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="questions_starred_numberOfSupportingMembersComparator_hidden" title="${questions_starred_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input class="sText"type="text" class="sText" name="questions_starred_numberOfSupportingMembers" id="questions_starred_numberOfSupportingMembers" value="${questions_starred_numberofsupportingmembers}" style="width: 62px"/>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderCover" text="Rotation Order Covering Letter" /></label>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderCover" name="questions_starred_rotationOrderCover">${questions_starred_rotationordercover}</textarea>
				</p>
					
				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderHeader" text="Rotation Order Header" /></label>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderHeader" name="questions_starred_rotationOrderHeader">${questions_starred_rotationorderheader}</textarea>
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderFooter" text="Rotation Order Footer" /></label>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderFooter" name="questions_starred_rotationOrderFooter">${questions_starred_rotationorderfooter}</textarea>
				</p>							
				
				<p>
					<label class="small"><spring:message code="session.deviceType.totalRoundsMemberBallot" text="Total Rounds In Member Ballot" /></label>
					<c:set var="key" value="questions_starred_totalRoundsMemberBallot"></c:set>
					<input type="text" class="sText" id="questions_starred_totalRoundsMemberBallot" name="questions_starred_totalRoundsMemberBallot" value="${questions_starred_totalroundsmemberballot}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.totalRoundsFinalBallot" text="Total Rounds In Final Ballot" /></label>
					<c:set var="key" value="questions_starred_totalRoundsFinalBallot"></c:set>
					<input type="text" class="sText" id="questions_starred_totalRoundsFinalBallot" name="questions_starred_totalRoundsFinalBallot" value="${questions_starred_totalroundsfinalballot}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_finalSubmissionDate_difference" text="Final SubmissionDate Difference" /></label>
					<c:set var="key" value="questions_starred_finalSubmissionDate_difference"></c:set>
					<input type="text" class="sText" id=questions_starred_finalSubmissionDate_difference name="questions_starred_finalSubmissionDate_difference" value="${questions_starred_finalsubmissiondate_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_lastReceivingDateFromDepartment_difference" text="Last Receiving Date From Department Difference" /></label>
					<c:set var="key" value="questions_starred_lastReceivingDateFromDepartment_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_lastReceivingDateFromDepartment_difference" name="questions_starred_lastReceivingDateFromDepartment_difference" value="${questions_starred_lastreceivingdatefromdepartment_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_lastSendingDateToDepartment_difference" text="Last Sending Date To Department Difference" /></label>
					<c:set var="key" value="questions_starred_lastSendingDateToDepartment_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_lastSendingDateToDepartment_difference" name="questions_starred_lastSendingDateToDepartment_difference" value="${questions_starred_lastsendingdatetodepartment_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_lastDateForChangingDepartment_difference" text="Last Date For Changing Department Difference" /></label>
					<c:set var="key" value="questions_starred_lastDateForChangingDepartment_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_lastDateForChangingDepartment_difference" name="questions_starred_lastDateForChangingDepartment_difference" value="${questions_starred_lastdateforchangingdepartment_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_speakerSendingDate_difference" text="Speaker Sending Date Difference" /></label>
					<c:set var="key" value="questions_starred_speakerSendingDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_speakerSendingDate_difference" name="questions_starred_speakerSendingDate_difference" value="${questions_starred_speakersendingdate_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_yaadiPrintingDate_difference" text="Yaadi Printing Date Difference" /></label>
					<c:set var="key" value="questions_starred_yaadiPrintingDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_yaadiPrintingDate_difference" name="questions_starred_yaadiPrintingDate_difference" value="${questions_starred_yaadiprintingdate_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_yaadiReceivingDate_difference" text="Yaadi Receiving Date Difference" /></label>
					<c:set var="key" value="questions_starred_yaadiReceivingDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_yaadiReceivingDate_difference" name="questions_starred_yaadiReceivingDate_difference" value="${questions_starred_yaadireceivingdate_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_suchhiPrintingDate_difference" text="Suchhi Printing Date Difference" /></label>
					<c:set var="key" value="questions_starred_suchhiPrintingDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_suchhiPrintingDate_difference" name="questions_starred_suchhiPrintingDate_difference" value="${questions_starred_suchhiprintingdate_difference}" />
				</p>
				
				<p>				
					<label class="small"><spring:message code="session.deviceType.questions_starred_suchhiReceivingDate_difference" text="Suchhi Receiving Date Difference" /></label>
					<c:set var="key" value="questions_starred_suchhiReceivingDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_suchhiReceivingDate_difference" name="questions_starred_suchhiReceivingDate_difference" value="${questions_starred_suchhireceivingdate_difference}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_suchhiDistributionDate_difference" text="Suchhi Distribution Date Difference" /></label>
					<c:set var="key" value="questions_starred_suchhiDistributionDate_difference"></c:set>
					<input type="text" class="sText" id="questions_starred_suchhiDistributionDate_difference" name="questions_starred_suchhiDistributionDate_difference" value="${questions_starred_suchhidistributiondate_difference}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_noOfRoundsMemberBallotAttendance" text="No. of Rounds In Member Ballot Attendance" /></label>
					<c:set var="key" value="questions_starred_noOfRoundsMemberBallotAttendance"></c:set>
					<input type="text" class="sText" id="questions_starred_noOfRoundsMemberBallotAttendance" name="questions_starred_noOfRoundsMemberBallotAttendance" value="${questions_starred_noofroundsmemberballotattendance}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_noOfRoundsMemberBallot" text="No. of Rounds In Member Ballot" /></label>
					<c:set var="key" value="questions_starred_noOfRoundsMemberBallot"></c:set>
					<input type="text" class="sText" id="questions_starred_noOfRoundsMemberBallot" name="questions_starred_noOfRoundsMemberBallot" value="${questions_starred_noofroundsmemberballot}" />
				</p>			
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_noOfRoundsMemberBallotFinal" text="No. of Rounds In Final Member Ballot" /></label>
					<c:set var="key" value="questions_starred_noOfRoundsMemberBallotFinal"></c:set>
					<input type="text" class="sText" id="questions_starred_noOfRoundsMemberBallotFinal" name="questions_starred_noOfRoundsMemberBallotFinal" value="${questions_starred_noofroundsmemberballotfinal}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_starred_noOfRoundsBallot" text="No. of Rounds In Ballot" /></label>
					<c:set var="key" value="questions_starred_noOfRoundsBallot"></c:set>
					<input type="text" class="sText" id="questions_starred_noOfRoundsBallot" name="questions_starred_noOfRoundsBallot" value="${questions_starred_noofroundsballot}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.clarificationFromMemberQuestions" text="Questions Asked For Clarification From Member" /></label></legend>
					<div id="questions_starred_clarificationFromMemberQuestions_div">
						
					</div>					
					<input type="hidden" id="questions_starred_clarificationFromMemberQuestions" name="questions_starred_clarificationFromMemberQuestions" value="${questions_starred_clarificationfrommemberquestions}"/>
					</fieldset>
				</div>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.clarificationFromDepartmentQuestions" text="Questions Asked For Clarification From Department" /></label></legend>
					<div id="questions_starred_clarificationFromDepartmentQuestions_div">
						
					</div>					
					<input type="hidden" id="questions_starred_clarificationFromDepartmentQuestions" name="questions_starred_clarificationFromDepartmentQuestions" value="${questions_starred_clarificationfromdepartmentquestions}"/>
					</fieldset>
				</div>				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_unstarred'}">					
			<div id="questions_unstarred" class="formDiv">				
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_unstarred_submissionStartDate" id="questions_unstarred_submissionStartDate"
						value="${questions_unstarred_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_unstarred_submissionEndDate" id="questions_unstarred_submissionEndDate" value="${questions_unstarred_submissionenddate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="questions_unstarred_numberOfSupportingMembersComparator" id="questions_unstarred_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="questions_unstarred_numberOfSupportingMembersComparator_hidden" title="${questions_unstarred_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input type="text" class="sText" name="questions_unstarred_numberOfSupportingMembers" id="questions_unstarred_numberOfSupportingMembers" value="${questions_unstarred_numberofsupportingmembers}" style="width: 62px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_unstarred_yaadiNumberingParameter" text="Yaadi Numbering Parameter" /></label>
					<select id="questions_unstarred_yaadiNumberingParameter" class="sSelect" name="questions_unstarred_yaadiNumberingParameter">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
							<c:when test="${questions_unstarred_yaadinumberingparameter=='house'}">
								<option value="house" selected="selected"><spring:message code='questions_unstarred_yaadiNumberingParameter.house' text='House'/></option>
								<option value="session"><spring:message code='questions_unstarred_yaadiNumberingParameter.session' text='Session'/></option>
							</c:when>
							<c:when test="${questions_unstarred_yaadinumberingparameter=='session'}">
								<option value="house"><spring:message code='questions_unstarred_yaadiNumberingParameter.house' text='House'/></option>
								<option value="session" selected="selected"><spring:message code='questions_unstarred_yaadiNumberingParameter.session' text='Session'/></option>
							</c:when>
							<c:otherwise>
								<option value="house"><spring:message code='questions_unstarred_yaadiNumberingParameter.house' text='House'/></option>
								<option value="session"><spring:message code='questions_unstarred_yaadiNumberingParameter.session' text='Session'/></option>
							</c:otherwise>
						</c:choose>
					</select>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.questions_unstarred_numberOfQuestionsInYaadi" text="Number of Questions in Yaadi" /></label>
					<input type="text" class="sInteger" name="questions_unstarred_numberOfQuestionsInYaadi" id="questions_unstarred_numberOfQuestionsInYaadi" value="${questions_unstarred_numberofquestionsinyaadi}" />
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_standalonemotion_halfhourdiscussion'}">					
			<div id="motions_standalonemotion_halfhourdiscussion" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_standalonemotion_halfhourdiscussion_submissionStartDate" id="motions_standalonemotion_halfhourdiscussion_submissionStartDate"
						value="${motions_standalonemotion_halfhourdiscussion_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_standalonemotion_halfhourdiscussion_submissionEndDate" id="motions_standalonemotion_halfhourdiscussion_submissionEndDate" value="${motions_standalonemotion_halfhourdiscussion_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="motions_standalonemotion_halfhourdiscussion_isBallotingRequired" value="${motions_standalonemotion_halfhourdiscussion_isballotingrequired}" >
					<input type="hidden" id="motions_standalonemotion_halfhourdiscussion_isBallotingRequired_Hidden" name="motions_standalonemotion_halfhourdiscussion_isBallotingRequired" value="" />
				</p>	
				
				<p class="questions_halfhourdiscussion_standalone_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="motions_standalonemotion_halfhourdiscussion_ballotType" id="motions_standalonemotion_halfhourdiscussion_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="motions_standalonemotion_halfhourdiscussion_ballotType_hidden" title="${motions_standalonemotion_halfhourdiscussion_ballottype}"></label>
				</p>	
				
				<p class="motions_standalonemotion_halfhourdiscussion_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="motions_standalonemotion_halfhourdiscussion_ballotEvents" id="motions_standalonemotion_halfhourdiscussion_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="motions_standalonemotion_halfhourdiscussion_ballotEvents_hidden" title="${motions_standalonemotion_halfhourdiscussion_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_standalonemotion_halfhourdiscussion_firstBallotDate" id="motions_standalonemotion_halfhourdiscussion_firstBallotDate" value="${motions_standalonemotion_halfhourdiscussion_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Questions" /></label>
					<input type="text" class="sInteger" name="motions_standalonemotion_halfhourdiscussion_numberOfQuestions" id="motions_standalonemotion_halfhourdiscussion_numberOfQuestions" value="${motions_standalonemotion_halfhourdiscussion_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembersComparator" id="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembersComparator_hidden" title="${motions_standalonemotion_halfhourdiscussion_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input class="sText" type="text" name="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembers" id="motions_standalonemotion_halfhourdiscussion_numberOfSupportingMembers" value="${motions_standalonemotion_halfhourdiscussion_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_standalonemotion_halfhourdiscussion_discussionDays" id="motions_standalonemotion_halfhourdiscussion_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_standalonemotion_halfhourdiscussion_discussionDays_hidden" title="${motions_standalonemotion_halfhourdiscussion_discussiondays}"></label>
				</p>
				
				<p id="motions_standalonemotion_halfhourdiscussion_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_standalonemotion_halfhourdiscussion_discussionDates" id="motions_standalonemotion_halfhourdiscussion_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_standalonemotion_halfhourdiscussion_discussionDates_hidden" title="${motions_standalonemotion_halfhourdiscussion_discussiondates}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_standalonemotion_halfhourdiscussion_numberOfDaysForFactualPositionReceiving" id="motions_standalonemotion_halfhourdiscussion_numberOfDaysForFactualPositionReceiving" value="${motions_standalonemotion_halfhourdiscussion_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_standalonemotion_halfhourdiscussion_reminderDayNumberForFactualPosition" id="motions_standalonemotion_halfhourdiscussion_reminderDayNumberForFactualPosition" value="${motions_standalonemotion_halfhourdiscussion_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_standalonemotion_halfhourdiscussion_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_standalonemotion_halfhourdiscussion_questionsAskedForFactualPosition" name="motions_standalonemotion_halfhourdiscussion_questionsAskedForFactualPosition" value="${motions_standalonemotion_halfhourdiscussion_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>	
				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_shortnotice'}">					
			<div id="questions_shortnotice" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_shortnotice_submissionStartDate" id="questions_shortnotice_submissionStartDate"
						value="${questions_shortnotice_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_shortnotice_submissionEndDate" id="questions_shortnotice_submissionEndDate" value="${questions_shortnotice_submissionenddate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="questions_shortnotice_numberOfSupportingMembersComparator" id="questions_shortnotice_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="questions_shortnotice_numberOfSupportingMembersComparator_hidden" title="${questions_shortnotice_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input type="text" class="sText" name="questions_shortnotice_numberOfSupportingMembers" id="questions_shortnotice_numberOfSupportingMembers" value="${questions_shortnotice_numberofsupportingmembers}" style="width: 62px" />
				</p>				
			</div>
		</c:if>
		
				<c:if test="${i.type eq 'motions_discussionmotion_lastweek'}">					
			<div id="motions_discussionmotion_lastweek" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_lastweek_submissionStartDate" id="motions_discussionmotion_lastweek_submissionStartDate"
						value="${motions_discussionmotion_lastweek_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_lastweek_submissionEndDate" id="motions_discussionmotion_lastweek_submissionEndDate" value="${motions_discussionmotion_lastweek_submissionenddate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_discussionmotion_lastweek_numberOfSupportingMembersComparator" id="motions_discussionmotion_lastweek_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_discussionmotion_lastweek_numberOfSupportingMembersComparator_hidden" title="${motions_discussionmotion_lastweek_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input type="text" class="sText" name="motions_discussionmotion_lastweek_numberOfSupportingMembers" id="motions_discussionmotion_lastweek_numberOfSupportingMembers" value="${motions_discussionmotion_lastweek_numberofsupportingmembers}" style="width: 62px" />
				</p>				
			</div>
		</c:if>
		
				<c:if test="${i.type eq 'motions_discussionmotion_publicimportance'}">					
			<div id="motions_discussionmotion_publicimportance" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_publicimportance_submissionStartDate" id="motions_discussionmotion_publicimportance_submissionStartDate"
						value="${motions_discussionmotion_publicimportance_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_publicimportance_submissionEndDate" id="motions_discussionmotion_publicimportance_submissionEndDate" value="${motions_discussionmotion_publicimportance_submissionenddate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_discussionmotion_publicimportance_numberOfSupportingMembersComparator" id="motions_discussionmotion_publicimportance_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_discussionmotion_publicimportance_numberOfSupportingMembersComparator_hidden" title="${motions_discussionmotion_publicimportance_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input type="text" class="sText" name="motions_discussionmotion_publicimportance_numberOfSupportingMembers" id="motions_discussionmotion_publicimportance_numberOfSupportingMembers" value="${motions_discussionmotion_publicimportance_numberofsupportingmembers}" style="width: 62px" />
				</p>				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_discussionmotion_shortduration'}">					
			<div id="motions_discussionmotion_shortduration" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_shortduration_submissionStartDate" id="motions_discussionmotion_shortduration_submissionStartDate"
						value="${motions_discussionmotion_shortduration_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussionmotion_shortduration_submissionEndDate" id="motions_discussionmotion_shortduration_submissionEndDate" value="${motions_discussionmotion_shortduration_submissionenddate}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_discussionmotion_shortduration_numberOfSupportingMembersComparator" id="motions_discussionmotion_shortduration_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_discussionmotion_shortduration_numberOfSupportingMembersComparator_hidden" title="${motions_discussionmotion_shortduration_numberofsupportingmemberscomparator}"></label>
				</p>
				<p style="display: inline;">		
					<input type="text" class="sText" name="motions_discussionmotion_shortduration_numberOfSupportingMembers" id="motions_discussionmotion_shortduration_numberOfSupportingMembers" value="${motions_discussionmotion_shortduration_numberofsupportingmembers}" style="width: 62px" />
				</p>				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_halfhourdiscussion_from_question'}">					
			<div id="questions_halfhourdiscussion_from_question" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_submissionStartDate" id="questions_halfhourdiscussion_from_question_submissionStartDate"
						value="${questions_halfhourdiscussion_from_question_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_submissionEndDate" id="questions_halfhourdiscussion_from_question_submissionEndDate" value="${questions_halfhourdiscussion_from_question_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck" id="questions_halfhourdiscussion_from_question_isBallotingRequired" value="${questions_halfhourdiscussion_from_question_isballotingrequired}" >
					<input type="hidden" id="questions_halfhourdiscussion_from_question_isBallotingRequired_Hidden" name="questions_halfhourdiscussion_from_question_isBallotingRequired" value="" />
				</p>
				

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_firstBallotDate" id="questions_halfhourdiscussion_from_question_firstBallotDate" value="${questions_halfhourdiscussion_from_question_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Questions" /></label>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_from_question_numberOfQuestions" id="questions_halfhourdiscussion_from_question_numberOfQuestions" value="${questions_halfhourdiscussion_from_question_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator" style="width: 100px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator_hidden" title="${questions_halfhourdiscussion_from_question_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input type="text" class="sText" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" value="${questions_halfhourdiscussion_from_question_numberofsupportingmembers}" style="width: 62px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="questions_halfhourdiscussion_from_question_discussionDays" id="questions_halfhourdiscussion_from_question_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="questions_halfhourdiscussion_from_question_discussionDays_hidden" title="${questions_halfhourdiscussion_from_question_discussiondays}"></label>
				</p>
				
				<p id="questions_halfhourdiscussion_from_question_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_from_question_discussionDates" id="questions_halfhourdiscussion_from_question_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="questions_halfhourdiscussion_from_question_discussionDates_hidden" title="${questions_halfhourdiscussion_from_question_discussiondates}"></label>
				</p>				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'resolutions_nonofficial'}">					
			<div id="resolutions_nonofficial" class="formDiv">	
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate" text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="resolutions_nonofficial_submissionStartDate" id="resolutions_nonofficial_submissionStartDate" value="${resolutions_nonofficial_submissionstartdate}" />
				</p>
								
				<p>
					<label class="small"><spring:message code="session.deviceType.daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" text="Days between submission end date & last discussion date" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" id="resolutions_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" value="${resolutions_nonofficial_daysbetweensubmissionenddateandlastdiscussiondateofsession}" />
				</p>
					
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>					
					<input type="text" class="timemask sText" id="resolutions_nonofficial_submissionEndTime" />
					<input type="hidden" class="datetimemask sText" name="resolutions_nonofficial_submissionEndDate" id="resolutions_nonofficial_submissionEndDate" value="${resolutions_nonofficial_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfResolutions" text="Number of Resolutions" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_numberOfResolutions" id="resolutions_nonofficial_numberOfResolutions" value="${resolutions_nonofficial_numberofresolutions}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="resolutions_nonofficial_isBallotingRequired" value="${resolutions_nonofficial_isballotingrequired}" >
					<input type="hidden" id="resolutions_nonofficial_isBallotingRequired_Hidden" name="resolutions_nonofficial_isBallotingRequired" value="" />
				</p>	
				
				<p class="resolutions_nonofficial_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="resolutions_nonofficial_ballotType" id="resolutions_nonofficial_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="resolutions_nonofficial_ballotType_hidden" title="${resolutions_nonofficial_ballottype}"></label>
				</p>	
				
				<p class="resolutions_nonofficial_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="resolutions_nonofficial_ballotEvents" id="resolutions_nonofficial_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="resolutions_nonofficial_ballotEvents_hidden" title="${resolutions_nonofficial_ballotevents}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="resolutions_nonofficial_discussionDays" id="resolutions_nonofficial_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="resolutions_nonofficial_discussionDays_hidden" title="${resolutions_nonofficial_discussiondays}"></label>
				</p>
				
				<p id="resolutions_nonofficial_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="resolutions_nonofficial_discussionDates" id="resolutions_nonofficial_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="resolutions_nonofficial_discussionDates_hidden" title="${resolutions_nonofficial_discussiondates}"></label>
				</p>	
				
				<p>
					<label class="small" ><spring:message code="resolution.patrakbhag2footer" text="Patrak Bhag 2 Footer" /></label>
					<textarea class="wysiwyg" cols="50" rows="5" name="resolutions_nonofficial_patrakbhag2footer" >${resolutions_nonofficial_patrakbhag2footer}</textarea>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfReminderMailForFactualPosition" text="Number of Reminder mails" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_numberOfReminderMailForFactualPosition" id="resolutions_nonofficial_numberOfReminderMailForFactualPosition" value="${resolutions_nonofficial_numberofremindermailforfactualposition}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_numberOfDaysForFactualPositionReceiving" id="resolutions_nonofficial_numberOfDaysForFactualPositionReceiving" value="${resolutions_nonofficial_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInResolutionsFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_reminderDayNumberForFactualPosition" id="resolutions_nonofficial_reminderDayNumberForFactualPosition" value="${resolutions_nonofficial_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInResolutionsFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<!-- <textarea class="sTextArea" cols="50" rows="5" id="resolutions_nonofficial_questionsAskedForFactualPosition" name="resolutions_nonofficial_questionsAskedForFactualPosition"></textarea> -->
					<div id="resolutions_nonofficial_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="resolutions_nonofficial_questionsAskedForFactualPosition" name="resolutions_nonofficial_questionsAskedForFactualPosition" value="${resolutions_nonofficial_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>		
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'resolutions_government'}">					
			<div id="resolutions_government" class="formDiv">						
				<p>
					<label class="small"><spring:message code="session.deviceType.daysForDiscussionDateToBeDecided" text="Days For Discussion Date To Be Decided" /></label>
					<input type="text" class="sInteger" name="resolutions_government_daysForDiscussionDateToBeDecided" id="resolutions_government_daysForDiscussionDateToBeDecided" value="${resolutions_government_daysfordiscussiondatetobedecided}" />
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_calling_attention'}">					
			<div id="motions_calling_attention" class="formDiv">	
				<c:if test="${houseType=='lowerhouse'}">
				<p>
					<label class="small"><spring:message code="session.deviceType.firstBatchStartTime" text="First Batch Start Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_firstBatchStartTime" id="motions_calling_attention_firstBatchStartTime" value="${motions_calling_attention_firstbatchstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.firstBatchEndTime" text="First Batch End Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_firstBatchEndTime" id="motions_calling_attention_firstBatchEndTime" value="${motions_calling_attention_firstbatchendtime}" />
				</p>	
				<p>
					<label class="small"><spring:message code="session.deviceType.secondBatchStartTime" text="Second Batch Start Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_secondBatchStartTime" id="motions_calling_attention_secondBatchStartTime" value="${motions_calling_attention_secondbatchstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.secondBatchEndTime" text="Second Batch End Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_secondBatchEndTime" id="motions_calling_attention_secondBatchEndTime" value="${motions_calling_attention_secondbatchendtime}" />
				</p>		
				</c:if>
				<c:if test="${houseType=='upperhouse'}">
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionStartTime" text="Submission Start Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_submissionStartTime" id="motions_calling_attention_submissionStartTime" value="${motions_calling_attention_submissionstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_submissionEndTime" id="motions_calling_attention_submissionEndTime" value="${motions_calling_attention_submissionendtime}" />
				</p>				
				</c:if>
									
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Supporting Members" /></label>
					<select class="sSelect" name="motions_calling_attention_numberOfSupportingMembersComparator" id="motions_calling_attention_numberOfSupportingMembersComparator">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
						<c:when test="${motions_calling_attention_numberofsupportingmemberscomparator=='equal'}">
						<option value="equal" selected="selected">&#61;</option>
						</c:when>
						<c:otherwise>
						<option value="equal">&#61;</option>
						</c:otherwise>
						</c:choose>
						<c:choose>
						<c:when test="${motions_calling_attention_numberofsupportingmemberscomparator=='lessthan'}">
						<option value="lessthan" selected="selected">&lt;</option>
						</c:when>
						<c:otherwise>
						<option value="lessthan">&lt;</option>
						</c:otherwise>
						</c:choose>		
						<c:choose>
						<c:when test="${motions_calling_attention_numberofsupportingmemberscomparator=='lessthanequal'}">
						<option value="lessthanequal" selected="selected">&le;</option>						
						</c:when>
						<c:otherwise>
						<option value="lessthanequal">&le;</option>						
						</c:otherwise>
						</c:choose>		
						<c:choose>
						<c:when test="${motions_calling_attention_numberofsupportingmemberscomparator=='greaterthan'}">
						<option value="greaterthan" selected="selected">&gt;</option>
						</c:when>
						<c:otherwise>
						<option value="greaterthan">&gt;</option>
						</c:otherwise>
						</c:choose>		
						<c:choose>
						<c:when test="${motions_calling_attention_numberofsupportingmemberscomparator=='greaterthanequal'}">
						<option value="greaterthanequal" selected="selected">&ge;</option>
						</c:when>
						<c:otherwise>
						<option value="greaterthanequal">&ge;</option>
						</c:otherwise>
						</c:choose>								
					</select>
					<input type="text" class="sText" name="questions_shortnotice_numberOfSupportingMembers" id="questions_shortnotice_numberOfSupportingMembers" value="${questions_shortnotice_numberofsupportingmembers}"/>
				</p>
				<!-- As checkbox is not getting submitted in selective fields submission of form, So hidden input field is taken to submit its value -->	
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="Is Balloting Required?" /></label>
					<input type="checkbox" class="sCheck" id="motions_calling_attention_isBallotingRequired" value="${motions_calling_attention_isballotingrequired}" />
					<input type="hidden" name="motions_calling_attention_isBallotingRequired" id="motions_calling_attention_isBallotingRequired_Hidden" value="" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.typeOfBallot" text="Type of Ballot" /></label>
					<select class="sSelect" name="motions_calling_attention_typeOfBallot" id="motions_calling_attention_typeOfBallot">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
						<c:when test="${motions_calling_attention_typeofballot=='member_ballot'}">
						<option value="member_ballot" selected="selected"><spring:message code="ballot.type.member" text="Member Ballot"></spring:message></option>
						</c:when>
						<c:otherwise>
						<option value="member_ballot"><spring:message code="ballot.type.member" text="Member Ballot"></spring:message></option>
						</c:otherwise>
						</c:choose>
						<c:choose>
						<c:when test="${motions_calling_attention_typeofballot=='notice_ballot'}">
						<option value="notice_ballot" selected="selected"><spring:message code="ballot.type.notice" text="Notice Ballot"></spring:message></option>
						</c:when>
						<c:otherwise>
						<option value="notice_ballot"><spring:message code="ballot.type.notice" text="Notice Ballot"></spring:message></option>
						</c:otherwise>
						</c:choose>
						<c:choose>
						<c:when test="${motions_calling_attention_typeofballot=='subject_ballot'}">
						<option value="subject_ballot" selected="selected"><spring:message code="ballot.type.subject" text="Subject Ballot"></spring:message></option>
						</c:when>
						<c:otherwise>
						<option value="subject_ballot"><spring:message code="ballot.type.subject" text="Subject Ballot"></spring:message></option>
						</c:otherwise>
						</c:choose>						
					</select>				
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.ballotTimeline" text="Ballot Happens Post?" /></label>
					<select class="sSelect" name="motions_calling_attention_ballotTimeline" id="motions_calling_attention_ballotTimeline">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
						<c:when test="${motions_calling_attention_ballottimeline=='submission'}">
						<option value="submission" selected="selected"><spring:message code="ballot.timeline.submission" text="Submission"></spring:message></option>
						</c:when>
						<c:otherwise>
						<option value="submission"><spring:message code="ballot.timeline.submission" text="Submission"></spring:message></option>
						</c:otherwise>
						</c:choose>
						<c:choose>
						<c:when test="${motions_calling_attention_ballottimeline=='admission'}">
						<option value="admission" selected="selected"><spring:message code="ballot.timeline.admission" text="Admission"></spring:message></option>
						</c:when>
						<c:otherwise>
						<option value="admission"><spring:message code="ballot.timeline.admission" text="Admission"></spring:message></option>
						</c:otherwise>
						</c:choose>
					</select>				
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.ballotStartTime" text="Ballot Start Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_ballotStartTime" id="motions_calling_attention_ballotStartTime" value="${motions_calling_attention_ballotstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.ballotEndTime" text="Ballot End Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_calling_attention_ballotEndTime" id="motions_calling_attention_ballotEndTime" value="${motions_calling_attention_ballotendtime}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_calling_attention_numberOfDaysForFactualPositionReceiving" id="motions_calling_attention_numberOfDaysForFactualPositionReceiving" value="${motions_calling_attention_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_calling_attention_reminderDayNumberForFactualPosition" id="motions_calling_attention_reminderDayNumberForFactualPosition" value="${motions_calling_attention_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_calling_attention_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_calling_attention_questionsAskedForFactualPosition" name="motions_calling_attention_questionsAskedForFactualPosition" value="${motions_calling_attention_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>				
			</div>
		</c:if>
		
		<!-- cutmotion device type config starts-->
		<c:if test="${i.type eq 'motions_cutmotion_budgetary'}">					
			<div id="motions_cutmotion_budgetary" class="formDiv">		
							
				<p>
					<label class="small"><spring:message code="session.deviceType.BudgetDate" text="Budget Date" /></label>
					<input type="text" class="sText datetimemask" name="motions_cutmotion_budgetary_budgetDate" id="motions_budgetary__cutmotion_budgetDate" value="${motions_cutmotion_budgetary_budgetdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.BudgetLayingDate" text="Budget Laying Date" /></label>
					<input type="text" class="sText datetimemask" name="motions_cutmotion_budgetary_budgetLayDate" id="motions_cutmotion_budgetary_budgetLayDate" value="${motions_cutmotion_budgetary_budgetlaydate}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDateFactor" text="Submission End Date Determinig Factor" /></label>
					<input type="text" class="integer sText" name="motions_cutmotion_budgetary_submissionEndDateFactor" id="motions_cutmotion_budgetary_submissionEndDateFactor" value="${motions_cutmotion_budgetary_submissionenddatefactor}" style="height: 24px;" />
				</p>
					
				<p style="display: inline;">
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Supporting Members" /></label>
					<select class="sSelect"  style="width: 120px; margin-top: 10px; border: solid 1px #8d8e8d;" name="motions_cutmotion_budgetary_numberOfSupportingMembersComparator" id="motions_cutmotion_budgetary_numberOfSupportingMembersComparator">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
							<c:when test="${motions_cutmotion_budgetary_numberofsupportingmemberscomparator=='equal'}">
								<option value="equal" selected="selected">&#61;</option>
							</c:when>
							<c:otherwise>
								<option value="equal">&#61;</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${motions_cutmotion_budgetary_numberofsupportingmemberscomparator=='lessthan'}">
								<option value="lessthan" selected="selected">&lt;</option>
							</c:when>
							<c:otherwise>
								<option value="lessthan">&lt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_cutmotion_budgetary_numberofsupportingmemberscomparator=='lessthanequal'}">
								<option value="lessthanequal" selected="selected">&le;</option>						
							</c:when>
							<c:otherwise>
								<option value="lessthanequal">&le;</option>						
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${cmotions_cutmotion_budgetary_numberofsupportingmemberscomparator=='greaterthan'}">
							<option value="greaterthan" selected="selected">&gt;</option>
							</c:when>
							<c:otherwise>
							<option value="greaterthan">&gt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_cutmotion_budgetary_numberofsupportingmemberscomparator=='greaterthanequal'}">
								<option value="greaterthanequal" selected="selected">&ge;</option>
							</c:when>
							<c:otherwise>
								<option value="greaterthanequal">&ge;</option>
							</c:otherwise>
						</c:choose>								
					</select>
				</p>		
				<p style="display: inline;">
					<input type="text" class="sText integer" name="motions_cutmotion_budgetary_numberOfSupportingMembers" id="motions_cutmotion_budgetary_numberOfSupportingMembers" value="${motions_cutmotion_budgetary_numberofsupportingmembers}" style="width: 60px; height: 24px;"/>
				</p>
				<%-- <p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_cutmotion_budgetary_discussionDays" id="motions_cutmotion_budgetary_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_cutmotion_budgetary_discussionDays_hidden" title="${motions_cutmotion_budgetary_discussiondays}"></label>
				</p>
				
				<p id="motions_cutmotion_budgetary_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_cutmotion_budgetary_discussionDates" id="motions_cutmotion_budgetary_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_cutmotion_budgetary_discussionDates_hidden" title="${motions_cutmotion_budgetary_discussiondates}"></label>
				</p> --%>							
			</div>
		</c:if>		
		<c:if test="${i.type eq 'motions_cutmotion_supplementary'}">					
			<div id="motions_cutmotion_supplementary" class="formDiv">		
							
				<p>
					<label class="small"><spring:message code="session.deviceType.BudgetDate" text="Budget Date" /></label>
					<input type="text" class="sText datetimemask" name="motions_cutmotion_supplementary_budgetDate" id="motions_cutmotion_supplementary_budgetDate" value="${motions_cutmotion_supplementary_budgetdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.BudgetLayingDate" text="Budget Laying Date" /></label>
					<input type="text" class="sText datetimemask" name="motions_cutmotion_supplementary_budgetLayDate" id="motions_cutmotion_supplementary_budgetLayDate" value="${motions_cutmotion_supplementary_budgetlaydate}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDateFactor" text="Submission End Date Determinig Factor" /></label>
					<input type="text" class="sText  integer" name="motions_cutmotion_supplementary_submissionEndDateFactor" id="motions_cutmotion_supplementary_submissionEndDateFactor" value="${motions_cutmotion_supplementary_submissionenddatefactor}" style="height: 24px;" />
				</p>
									
				<p style="display: inline;">
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Supporting Members" /></label>
					<select class="sSelect"  style="margin-top: 10px; width: 120px; border: solid 1px #8d8e8d;" name="motions_cutmotion_supplementary_numberOfSupportingMembersComparator" id="motions_cutmotion_supplementary_numberOfSupportingMembersComparator">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
							<c:when test="${motions_cutmotion_supplementary_numberofsupportingmemberscomparator=='equal'}">
								<option value="equal" selected="selected">&#61;</option>
							</c:when>
							<c:otherwise>
								<option value="equal">&#61;</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${motions_cutmotion_supplementary_numberofsupportingmemberscomparator=='lessthan'}">
								<option value="lessthan" selected="selected">&lt;</option>
							</c:when>
							<c:otherwise>
								<option value="lessthan">&lt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_cutmotion_supplementary_numberofsupportingmemberscomparator=='lessthanequal'}">
								<option value="lessthanequal" selected="selected">&le;</option>						
							</c:when>
							<c:otherwise>
								<option value="lessthanequal">&le;</option>						
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_cutmotion_supplementary_numberofsupportingmemberscomparator=='greaterthan'}">
							<option value="greaterthan" selected="selected">&gt;</option>
							</c:when>
							<c:otherwise>
							<option value="greaterthan">&gt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_cutmotion_supplementary_numberofsupportingmemberscomparator=='greaterthanequal'}">
								<option value="greaterthanequal" selected="selected">&ge;</option>
							</c:when>
							<c:otherwise>
								<option value="greaterthanequal">&ge;</option>
							</c:otherwise>
						</c:choose>								
					</select>
				</p>		
				<p style="display: inline;">
					<input type="text" class="sText integer" name="motions_cutmotion_supplementary_numberOfSupportingMembers" id="motions_cutmotion_supplementary_numberOfSupportingMembers" value="${motions_cutmotion_supplementary_numberofsupportingmembers}" style="width: 60px; height: 24px;"/>
				</p>	
				
				<%-- <p>
					<label class="centerlabel"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_cutmotion_supplementary_discussionDays" id="motions_cutmotion_supplementary_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_cutmotion_supplementary_discussionDays_hidden" title="${motions_cutmotion_supplementary_discussiondays}"></label>
				</p>
				
				<p id="motions_cutmotion_supplementary_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_cutmotion_supplementary_discussionDates" id="motions_cutmotion_supplementary_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_cutmotion_supplementary_discussionDates_hidden" title="${motions_cutmotion_supplementary_discussiondates}"></label>
				</p>	 --%>					
			</div>
		</c:if>
		<!-- cutmotion device type config ends-->
		
		<%-- eventmotion device config begins --%>
		<c:if test="${i.type eq 'motions_eventmotion_condolence'}">					
			<div id="motions_eventmotion_condolence" class="formDiv">		
							
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_condolence_submissionStartDate" id="motions_eventmotion_condolence_submissionStartDate"
						value="${motions_eventmotion_condolence_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_condolence_submissionEndDate" id="motions_eventmotion_condolence_submissionEndDate" value="${motions_eventmotion_condolence_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="motions_eventmotion_condolence_isBallotingRequired" value="${motions_eventmotion_condolence_isballotingrequired}" >
					<input type="hidden" id="motions_eventmotion_condolence_isBallotingRequired_Hidden" name="motions_eventmotion_condolence_isBallotingRequired" value="" />
				</p>	
				
				<p class="motions_eventmotion_condolence_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="motions_eventmotion_condolence_ballotType" id="motions_eventmotion_condolence_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="motions_eventmotion_condolence_ballotType_hidden" title="${motions_eventmotion_condolence_ballottype}"></label>
				</p>	
				
				<p class="motions_eventmotion_condolence_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="motions_eventmotion_condolence_ballotEvents" id="motions_eventmotion_condolence_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="motions_eventmotion_condolence_ballotEvents_hidden" title="${motions_eventmotion_condolence_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_condolence_firstBallotDate" id="motions_eventmotion_condolence_firstBallotDate" value="${motions_eventmotion_condolence_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Motions" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_condolence_numberOfQuestions" id="motions_eventmotion_condolence_numberOfQuestions" value="${motions_eventmotion_condolence_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_eventmotion_condolence_numberOfSupportingMembersComparator" id="motions_eventmotion_condolence_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_eventmotion_condolence_numberOfSupportingMembersComparator_hidden" title="${motions_eventmotion_condolence_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input class="sText" type="text" name="motions_eventmotion_condolence_numberOfSupportingMembers" id="motions_eventmotion_condolence_numberOfSupportingMembers" value="${motions_eventmotion_condolence_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_eventmotion_condolence_discussionDays" id="motions_eventmotion_condolence_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_eventmotion_condolence_discussionDays_hidden" title="${motions_eventmotion_condolence_discussiondays}"></label>
				</p>
				
				<p id="motions_eventmotion_condolence_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_eventmotion_condolence_discussionDates" id="motions_eventmotion_condolence_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_eventmotion_condolence_discussionDates_hidden" title="${motions_eventmotion_condolence_discussiondates}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_condolence_numberOfDaysForFactualPositionReceiving" id="motions_eventmotion_condolence_numberOfDaysForFactualPositionReceiving" value="${motions_eventmotion_condolence_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_condolence_reminderDayNumberForFactualPosition" id="motions_eventmotion_condolence_reminderDayNumberForFactualPosition" value="${motions_eventmotion_condolence_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_eventmotion_condolence_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_eventmotion_condolence_questionsAskedForFactualPosition" name="motions_eventmotion_condolence_questionsAskedForFactualPosition" value="${motions_eventmotion_condolence_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>								
			</div>
		</c:if>	
		
		
		<c:if test="${i.type eq 'motions_eventmotion_congratulatory'}">					
			<div id="motions_eventmotion_congratulatory" class="formDiv">		
							
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_congratulatory_submissionStartDate" id="motions_eventmotion_congratulatory_submissionStartDate"
						value="${motions_eventmotion_congratulatory_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_congratulatory_submissionEndDate" id="motions_eventmotion_congratulatory_submissionEndDate" value="${motions_eventmotion_congratulatory_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="motions_eventmotion_congratulatory_isBallotingRequired" value="${motions_eventmotion_congratulatory_isballotingrequired}" >
					<input type="hidden" id="motions_eventmotion_congratulatory_isBallotingRequired_Hidden" name="motions_eventmotion_congratulatory_isBallotingRequired" value="" />
				</p>	
				
				<p class="motions_eventmotion_congratulatory_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="motions_eventmotion_congratulatory_ballotType" id="motions_eventmotion_congratulatory_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="motions_eventmotion_congratulatory_ballotType_hidden" title="${motions_eventmotion_congratulatory_ballottype}"></label>
				</p>	
				
				<p class="motions_eventmotion_congratulatory_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="motions_eventmotion_congratulatory_ballotEvents" id="motions_eventmotion_congratulatory_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="motions_eventmotion_congratulatory_ballotEvents_hidden" title="${motions_eventmotion_congratulatory_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_eventmotion_congratulatory_firstBallotDate" id="motions_eventmotion_congratulatory_firstBallotDate" value="${motions_eventmotion_congratulatory_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Motions" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_congratulatory_numberOfQuestions" id="motions_eventmotion_congratulatory_numberOfQuestions" value="${motions_eventmotion_congratulatory_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_eventmotion_congratulatory_numberOfSupportingMembersComparator" id="motions_eventmotion_congratulatory_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_eventmotion_congratulatory_numberOfSupportingMembersComparator_hidden" title="${motions_eventmotion_congratulatory_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input class="sText" type="text" name="motions_eventmotion_congratulatory_numberOfSupportingMembers" id="motions_eventmotion_congratulatory_numberOfSupportingMembers" value="${motions_eventmotion_congratulatory_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_eventmotion_congratulatory_discussionDays" id="motions_eventmotion_congratulatory_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_eventmotion_congratulatory_discussionDays_hidden" title="${motions_eventmotion_congratulatory_discussiondays}"></label>
				</p>
				
				<p id="motions_eventmotion_congratulatory_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_eventmotion_congratulatory_discussionDates" id="motions_eventmotion_congratulatory_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_eventmotion_congratulatory_discussionDates_hidden" title="${motions_eventmotion_congratulatory_discussiondates}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_congratulatory_numberOfDaysForFactualPositionReceiving" id="motions_eventmotion_congratulatory_numberOfDaysForFactualPositionReceiving" value="${motions_eventmotion_congratulatory_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_eventmotion_congratulatory_reminderDayNumberForFactualPosition" id="motions_eventmotion_congratulatory_reminderDayNumberForFactualPosition" value="${motions_eventmotion_congratulatory_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_eventmotion_congratulatory_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_eventmotion_congratulatory_questionsAskedForFactualPosition" name="motions_eventmotion_congratulatory_questionsAskedForFactualPosition" value="${motions_eventmotion_congratulatory_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>								
			</div>
		</c:if>	
		<%-- eventmotion device config ends --%>
		
		<%-- discussionmotion device config begins --%>
			
		
		<c:if test="${i.type eq 'motions_discussion_publicimportance'}">					
			<div id="motions_discussion_publicimportance" class="formDiv">		
							
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_publicimportance_submissionStartDate" id="motions_discussion_publicimportance_submissionStartDate"
						value="${motions_discussion_publicimportance_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_publicimportance_submissionEndDate" id="motions_discussion_publicimportance_submissionEndDate" value="${motions_discussion_publicimportance_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="motions_discussion_publicimportance_isBallotingRequired" value="${motions_discussion_publicimportance_isballotingrequired}" >
					<input type="hidden" id="motions_discussion_publicimportance_isBallotingRequired_Hidden" name="motions_discussion_publicimportance_isBallotingRequired" value="" />
				</p>	
				
				<p class="motions_discussion_publicimportance_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="motions_discussion_publicimportance_ballotType" id="motions_discussion_publicimportance_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="motions_discussion_pulicimportance_ballotType_hidden" title="${motions_discussion_publicimportance_ballottype}"></label>
				</p>	
				
				<p class="motions_discussion_publicimportance_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="motions_discussion_publicimportance_ballotEvents" id="motions_discussion_publicimportance_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="motions_discussion_publicimportance_ballotEvents_hidden" title="${motions_discussion_publicimportance_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_publicimportance_firstBallotDate" id="motions_discussion_publicimportance_firstBallotDate" value="${motions_discussion_publicimportance_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Motions" /></label>
					<input type="text" class="sInteger" name="motions_discussion_publicimportance_numberOfQuestions" id="motions_discussion_publicimportance_numberOfQuestions" value="${motions_discussion_publicimportance_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_discussion_publicimportance_numberOfSupportingMembersComparator" id="motions_discussion_publicimportance_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_discussion_publicimportance_numberOfSupportingMembersComparator_hidden" title="${motions_discussion_publicimportance_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input class="sText" type="text" name="motions_discussion_publicimportance_numberOfSupportingMembers" id="motions_discussion_publicimportance_numberOfSupportingMembers" value="${motions_discussion_publicimportance_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_discussion_publicimportance_discussionDays" id="motions_discussion_publicimportance_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_discussion_publicimportance_discussionDays_hidden" title="${motions_discussion_publicimportance_discussiondays}"></label>
				</p>
				
				<p id="motions_discussion_publicimportance_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_discussion_publicimportance_discussionDates" id="motions_discussion_publicimportance_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_discussion_publicimportance_discussionDates_hidden" title="${motions_discussion_publicimportance_discussiondates}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_discussion_publicimportance_numberOfDaysForFactualPositionReceiving" id="motions_discussion_publicimportance_numberOfDaysForFactualPositionReceiving" value="${motions_discussion_publicimportance_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_discussion_publicimportance_reminderDayNumberForFactualPosition" id="motions_discussion_publicimportance_reminderDayNumberForFactualPosition" value="${motions_discussion_publicimportance_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_discussion_publicimportance_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_discussion_publicimportance_questionsAskedForFactualPosition" name="motions_discussion_publicimportance_questionsAskedForFactualPosition" value="${motions_discussion_publicimportance_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>								
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_discussion_lastweek'}">					
			<div id="motions_discussion_lastweek" class="formDiv">		
							
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_lastweek_submissionStartDate" id="motions_discussion_lastweek_submissionStartDate"
						value="${motions_discussion_lastweek_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_lastweek_submissionEndDate" id="motions_discussion_lastweek_submissionEndDate" value="${motions_discussion_lastweek_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="motions_discussion_lastweek_isBallotingRequired" value="${motions_discussion_lastweek_isballotingrequired}" >
					<input type="hidden" id="motions_discussion_lastweek_isBallotingRequired_Hidden" name="motions_discussion_lastweek_isBallotingRequired" value="" />
				</p>	
				
				<p class="motions_discussion_lastweek_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="motions_discussion_lastweek_ballotType" id="motions_discussion_lastweek_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="motions_discussion_pulicimportance_ballotType_hidden" title="${motions_discussion_lastweek_ballottype}"></label>
				</p>	
				
				<p class="motions_discussion_lastweek_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="motions_discussion_lastweek_ballotEvents" id="motions_discussion_lastweek_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="motions_discussion_lastweek_ballotEvents_hidden" title="${motions_discussion_lastweek_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="motions_discussion_lastweek_firstBallotDate" id="motions_discussion_lastweek_firstBallotDate" value="${motions_discussion_lastweek_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Motions" /></label>
					<input type="text" class="sInteger" name="motions_discussion_lastweek_numberOfQuestions" id="motions_discussion_lastweek_numberOfQuestions" value="${motions_discussion_lastweek_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="motions_discussion_lastweek_numberOfSupportingMembersComparator" id="motions_discussion_lastweek_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="motions_discussion_lastweek_numberOfSupportingMembersComparator_hidden" title="${motions_discussion_lastweek_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input class="sText" type="text" name="motions_discussion_lastweek_numberOfSupportingMembers" id="motions_discussion_lastweek_numberOfSupportingMembers" value="${motions_discussion_lastweek_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="motions_discussion_lastweek_discussionDays" id="motions_discussion_lastweek_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="motions_discussion_lastweek_discussionDays_hidden" title="${motions_discussion_lastweek_discussiondays}"></label>
				</p>
				
				<p id="motions_discussion_lastweek_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="motions_discussion_lastweek_discussionDates" id="motions_discussion_lastweek_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="motions_discussion_lastweek_discussionDates_hidden" title="${motions_discussion_lastweek_discussiondates}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfDaysForFactualPositionReceiving" text="Reply Day Numbers For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_discussion_lastweek_numberOfDaysForFactualPositionReceiving" id="motions_discussion_lastweek_numberOfDaysForFactualPositionReceiving" value="${motions_discussion_lastweek_numberofdaysforfactualpositionreceiving}" />
				</p>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.reminderDayNumberInHDSFactualPostion" text="Reminder Day Number For Factual Position" /></label>
					<input type="text" class="sInteger" name="motions_discussion_lastweek_reminderDayNumberForFactualPosition" id="motions_discussion_lastweek_reminderDayNumberForFactualPosition" value="${motions_discussion_lastweek_reminderdaynumberforfactualposition}" />
				</p>
				
				<div>
					<fieldset>
					<legend style="text-align: left; width: 150px;"><label><spring:message code="session.deviceType.questionsAskedInHDSFactualPosition" text="Questions Asked For Factual Position" /></label></legend>
					<div id="motions_discussion_lastweek_questionsAskedForFactualPosition_div">
						
					</div>					
					<input type="hidden" id="motions_discussion_lastweek_questionsAskedForFactualPosition" name="motions_discussion_lastweek_questionsAskedForFactualPosition" value="${motions_discussion_lastweek_questionsaskedforfactualposition}"/>
					</fieldset>
				</div>								
			</div>
		</c:if>	
		<%-- dicussionmotion device config ends --%>
		
		<%--bill coonfig --%>
		<c:if test="${i.type eq 'bills_nonofficial'}">					
			<div id="bills_nonofficial" class="formDiv">						
				<p>
					<label class="small"><spring:message code="session.deviceType.daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" text="Days between submission end date & last discussion date" /></label>
					<input type="text" class="sInteger" name="bills_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" id="bills_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" value="${bills_nonofficial_daysbetweensubmissionenddateandlastdiscussiondateofsession}" />
				</p>
					
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>					
					<input type="text" class="timemask sText" id="bills_nonofficial_submissionEndTime" />
					<input type="hidden" class="datetimemask sText" name="bills_nonofficial_submissionEndDate" id="bills_nonofficial_submissionEndDate" value="${bills_nonofficial_submissionenddate}" />
					<input type="hidden" class="datetimemask sText" name="bills_nonofficial_submissionStartDate" id="bills_nonofficial_submissionStartDate" value="${bills_nonofficial_submissionstartdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Balloting Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="bills_nonofficial_isBallotingRequired" value="${bills_nonofficial_isballotingrequired}" >
					<input type="hidden" id="bills_nonofficial_isBallotingRequired_Hidden" name="bills_nonofficial_isBallotingRequired" value="" />
				</p>
				
				<%-- <p>
					<label class="small"><spring:message code="session.deviceType.numberOfBills" text="Number of Bills" /></label>
					<input type="text" class="sInteger" name="bills_nonofficial_numberOfBills" id="bills_nonofficial_numberOfBills" value="${bills_nonofficial_numberofbills}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="bills_nonofficial_isBallotingRequired" value="${bills_nonofficial_isballotingrequired}" >
					<input type="hidden" id="bills_nonofficial_isBallotingRequired_Hidden" name="bills_nonofficial_isBallotingRequired" value="" />
				</p>	
				
				<p class="bills_nonofficial_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="bills_nonofficial_ballotType" id="bills_nonofficial_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="bills_nonofficial_ballotType_hidden" title="${bills_nonofficial_ballottype}"></label>
				</p>	
				
				<p class="bills_nonofficial_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_ballotEvents" id="bills_nonofficial_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="bills_nonofficial_ballotEvents_hidden" title="${bills_nonofficial_ballotevents}"></label>
				</p> --%>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.billTypesAllowed" text="Bill Types Allowed" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_billTypesAllowed" id="bills_nonofficial_billTypesAllowed" multiple="multiple">
						<c:forEach items="${billTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_nonofficial_billTypesAllowed_hidden" title="${bills_nonofficial_billtypesallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.billKindsAllowed" text="Bill Kinds Allowed" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_billKindsAllowed" id="bills_nonofficial_billKindsAllowed" multiple="multiple">
						<c:forEach items="${billKinds}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_nonofficial_billKindsAllowed_hidden" title="${bills_nonofficial_billkindsallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.languagesAllowed" text="Languages Allowed" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_languagesAllowed" id="bills_nonofficial_languagesAllowed" multiple="multiple">
						<c:forEach items="${languages}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_nonofficial_languagesAllowed_hidden" title="${bills_nonofficial_languagesallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.languagesCompulsory" text="Languages Compulsory" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_languagesCompulsory" id="bills_nonofficial_languagesCompulsory" multiple="multiple">
						<c:forEach items="${languages}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_nonofficial_languagesCompulsory_hidden" title="${bills_nonofficial_languagescompulsory}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.defaultTitleLanguage" text="Default Language" /></label>
					<select class="sSelect" name="bills_nonofficial_defaultTitleLanguage" id="bills_nonofficial_defaultTitleLanguage">
						<c:forEach items="${languages}" var="i">
							<c:choose>
								<c:when test="${i.type==bills_nonofficial_defaulttitlelanguage}">
									<option value="${i.type}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.type}">${i.name}</option>
								</c:otherwise>
							</c:choose>							
						</c:forEach>
					</select>					
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.translationTimeoutDays" text="Number of Translation Timeout Days" /></label>
					<input type="text" class="sInteger" name="bills_nonofficial_translationTimeoutDays" id="bills_nonofficial_translationTimeoutDays" value="${bills_nonofficial_translationtimeoutdays}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="bills_nonofficial_discussionDays" id="bills_nonofficial_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="bills_nonofficial_discussionDays_hidden" title="${bills_nonofficial_discussiondays}"></label>
				</p>
				
				<p id="bills_nonofficial_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="bills_nonofficial_discussionDates" id="bills_nonofficial_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="bills_nonofficial_discussionDates_hidden" title="${bills_nonofficial_discussiondates}"></label>
				</p>
				
				<c:forEach items="${languages}" var="i">
				<p id="bills_nonofficial_schedule7OfConstitution_para_${i.type}" style="display: none;">
					<label class="small"><spring:message code="session.deviceType.schedule7OfConstitution.${i.type}" text="Schedule 7 Of Constitution in ${i.type}" /></label>
					<c:set var="schedule7OfConstitutionForGivenLanguage" value="bills_nonofficial_schedule7ofconstitution_${i.type}"/>
					<c:choose>
						<c:when test="${empty requestScope[schedule7OfConstitutionForGivenLanguage]}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="bills-nonofficial-schedule7OfConstitution-${i.type}" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_download.jsp">
								<jsp:param name="fileid" value="bills-nonofficial-schedule7OfConstitution-${i.type}" />
								<jsp:param name="filetag" value="${requestScope[schedule7OfConstitutionForGivenLanguage]}" />
								<jsp:param name="isRemovable" value="false" />
							</jsp:include>
							<a href="javascript:void(0)" id="changeFile_bills_nonofficial_schedule7OfConstitution_${i.type}" class="changeFile_schedule7OfConstitution">Change File</a>
						</c:otherwise>
					</c:choose>
					<input type="hidden" id="bills_nonofficial_schedule7OfConstitution_${i.type}" name="bills_nonofficial_schedule7OfConstitution_${i.type}" value=""/>					
				</p>
				</c:forEach>	
				
				<p>
					<label class="small"><spring:message code="session.deviceType.instructionalOrder" text="Instructional Order" /></label>
					<c:choose>
						<c:when test="${empty bills_nonofficial_instructionalorder}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="bills-nonofficial-instructionalOrder" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_download.jsp">
								<jsp:param name="fileid" value="bills-nonofficial-instructionalOrder" />
								<jsp:param name="filetag" value="${bills_nonofficial_instructionalorder}" />
								<jsp:param name="isRemovable" value="false" />
							</jsp:include>
							<a href="javascript:void(0)" id="changeFile_bills_nonofficial_instructionalOrder" class="changeFile_instructionalOrder">Change File</a>
						</c:otherwise>
					</c:choose>
					<input type="hidden" id="bills_nonofficial_instructionalOrder" name="bills_nonofficial_instructionalOrder" value=""/>					
				</p>					
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'bills_government'}">					
			<div id="bills_government" class="formDiv">						
				<%-- <p>
					<label class="small"><spring:message code="session.deviceType.numberOfBills" text="Number of Bills" /></label>
					<input type="text" class="sInteger" name="bills_government_numberOfBills" id="bills_government_numberOfBills" value="${bills_government_numberofbills}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="bills_government_isBallotingRequired" value="${bills_government_isballotingrequired}" >
					<input type="hidden" id="bills_government_isBallotingRequired_Hidden" name="bills_government_isBallotingRequired" value="" />
				</p>	
				
				<p class="bills_government_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="bills_government_ballotType" id="bills_government_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="bills_government_ballotType_hidden" title="${bills_government_ballottype}"></label>
				</p>	
				
				<p class="bills_government_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="bills_government_ballotEvents" id="bills_government_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="bills_government_ballotEvents_hidden" title="${bills_government_ballotevents}"></label>
				</p> --%>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.billTypesAllowed" text="Bill Types Allowed" /></label>
					<select class="sSelectMultiple" name="bills_government_billTypesAllowed" id="bills_government_billTypesAllowed" multiple="multiple">
						<c:forEach items="${billTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_government_billTypesAllowed_hidden" title="${bills_government_billtypesallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.billKindsAllowed" text="Bill Kinds Allowed" /></label>
					<select class="sSelectMultiple" name="bills_government_billKindsAllowed" id="bills_government_billKindsAllowed" multiple="multiple">
						<c:forEach items="${billKinds}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_government_billKindsAllowed_hidden" title="${bills_government_billkindsallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.languagesAllowed" text="Languages Allowed" /></label>
					<select class="sSelectMultiple" name="bills_government_languagesAllowed" id="bills_government_languagesAllowed" multiple="multiple">
						<c:forEach items="${languages}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_government_languagesAllowed_hidden" title="${bills_government_languagesallowed}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.languagesCompulsory" text="Languages Compulsory" /></label>
					<select class="sSelectMultiple" name="bills_government_languagesCompulsory" id="bills_government_languagesCompulsory" multiple="multiple">
						<c:forEach items="${languages}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label style="display: none;" id="bills_government_languagesCompulsory_hidden" title="${bills_government_languagescompulsory}"></label>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.defaultTitleLanguage" text="Default Language" /></label>
					<select class="sSelect" name="bills_government_defaultTitleLanguage" id="bills_government_defaultTitleLanguage">
						<c:forEach items="${languages}" var="i">
							<c:choose>
								<c:when test="${i.type==bills_government_defaulttitlelanguage}">
									<option value="${i.type}" selected="selected">${i.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${i.type}">${i.name}</option>
								</c:otherwise>
							</c:choose>							
						</c:forEach>
					</select>					
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.translationTimeoutDays" text="Number of Translation Timeout Days" /></label>
					<input type="text" class="sInteger" name="bills_government_translationTimeoutDays" id="bills_government_translationTimeoutDays" value="${bills_government_translationtimeoutdays}" />
				</p>
				
				<c:forEach items="${languages}" var="i">
				<p id="bills_government_schedule7OfConstitution_para_${i.type}" style="display: none;">
					<label class="small"><spring:message code="session.deviceType.schedule7OfConstitution.${i.type}" text="Schedule 7 Of Constitution in ${i.type}" /></label>
					<c:set var="schedule7OfConstitutionForGivenLanguage" value="bills_government_schedule7ofconstitution_${i.type}"/>
					<c:choose>
						<c:when test="${empty requestScope[schedule7OfConstitutionForGivenLanguage]}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="bills-government-schedule7OfConstitution-${i.type}" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_download.jsp">
								<jsp:param name="fileid" value="bills-government-schedule7OfConstitution-${i.type}" />
								<jsp:param name="filetag" value="${requestScope[schedule7OfConstitutionForGivenLanguage]}" />
								<jsp:param name="isRemovable" value="false" />
							</jsp:include>
							<a href="javascript:void(0)" id="changeFile_bills_government_schedule7OfConstitution_${i.type}" class="changeFile_schedule7OfConstitution">Change File</a>
						</c:otherwise>
					</c:choose>
					<input type="hidden" id="bills_government_schedule7OfConstitution_${i.type}" name="bills_government_schedule7OfConstitution_${i.type}" value=""/>					
				</p>
				</c:forEach>		
				
				<p>
					<label class="small"><spring:message code="session.deviceType.instructionalOrder" text="Instructional Order" /></label>
					<c:choose>
						<c:when test="${empty bills_government_instructionalorder}">
							<jsp:include page="/common/file_upload.jsp">
								<jsp:param name="fileid" value="bills-government-instructionalOrder" />
							</jsp:include>
						</c:when>
						<c:otherwise>		
							<jsp:include page="/common/file_download.jsp">
								<jsp:param name="fileid" value="bills-government-instructionalOrder" />
								<jsp:param name="filetag" value="${bills_government_instructionalorder}" />
								<jsp:param name="isRemovable" value="false" />
							</jsp:include>
							<a href="javascript:void(0)" id="changeFile_bills_government_instructionalOrder" class="changeFile_instructionalOrder">Change File</a>
						</c:otherwise>
					</c:choose>
					<input type="hidden" id="bills_government_instructionalOrder" name="bills_government_instructionalOrder" value=""/>					
				</p>		
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_adjournment'}">
			<div id="motions_adjournment" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartTime" text="Submission Start Time"></spring:message></label>
					<input type="text" class="sText" maxlength="5" name="motions_adjournment_submissionStartTime" id="motions_adjournment_submissionStartTime" value="${motions_adjournment_submissionstarttime}" />
					<label style="margin-left: 5px;">
						(<b>Format</b> = &nbsp;<spring:message code="generic.hour" text="hours"/> <b>:</b> <spring:message code="generic.minute" text="minutes"/>)
					</label>
				</p>
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time"></spring:message></label>
					<input type="text" class="sText" maxlength="5" name="motions_adjournment_submissionEndTime" id="motions_adjournment_submissionEndTime" value="${motions_adjournment_submissionendtime}" />
					<label style="margin-left: 5px;">
						(<b>Format</b> = &nbsp;<spring:message code="generic.hour" text="hours"/> <b>:</b> <spring:message code="generic.minute" text="minutes"/>)
					</label>
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'motions_rules_suspension'}">					
			<div id="motions_rules_suspension" class="formDiv">	
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionStartTime" text="Submission Start Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_rules_suspension_submissionStartTime" id="motions_rules_suspension_submissionStartTime" value="${motions_rules_suspension_submissionstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>
					<input type="text" class="sText datetimemask" name="motions_rules_suspension_submissionEndTime" id="motions_rules_suspension_submissionEndTime" value="${motions_rules_suspension_submissionendtime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfMotions" text="Number of Motions" /></label>
					<input type="text" class="sInteger" name="motions_rules_suspension_numberOfMotions" id="motions_rules_suspension_numberOfMotions" value="${motions_rules_suspension_numberofmotions}" />
				</p>				
		
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Supporting Members" /></label>
					<select class="sSelect" name="motions_rules_suspension_numberofsupportingmemberscomparator" id="motions_rules_suspension_numberofsupportingmemberscomparator">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<c:choose>
							<c:when test="${motions_rules_suspension_numberofsupportingmemberscomparator=='equal'}">
								<option value="equal" selected="selected">&#61;</option>
							</c:when>
							<c:otherwise>
								<option value="equal">&#61;</option>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${motions_rules_suspension_numberofsupportingmemberscomparator=='lessthan'}">
								<option value="lessthan" selected="selected">&lt;</option>
							</c:when>
							<c:otherwise>
								<option value="lessthan">&lt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_rules_suspension_numberofsupportingmemberscomparator=='lessthanequal'}">
								<option value="lessthanequal" selected="selected">&le;</option>						
							</c:when>
							<c:otherwise>
								<option value="lessthanequal">&le;</option>						
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_rules_suspension_numberofsupportingmemberscomparator=='greaterthan'}">
								<option value="greaterthan" selected="selected">&gt;</option>
							</c:when>
							<c:otherwise>
								<option value="greaterthan">&gt;</option>
							</c:otherwise>
						</c:choose>		
						<c:choose>
							<c:when test="${motions_rules_suspension_numberofsupportingmemberscomparator=='greaterthanequal'}">
								<option value="greaterthanequal" selected="selected">&ge;</option>
							</c:when>
							<c:otherwise>
								<option value="greaterthanequal">&ge;</option>
							</c:otherwise>
						</c:choose>								
					</select>
					<input type="text" class="sText" name="motions_rules_suspension_numberOfSupportingMembers" id="motions_rules_suspension_numberOfSupportingMembers" value="${motions_rules_suspension_numberofsupportingmembers}"/>
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'notices_specialmention'}">
			<div id="notices_specialmention" class="formDiv">	
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionStartTime" text="Submission Start Time" /></label>
					<input type="text" class="sText datetimemask" name="notices_specialmention_submissionStartTime" id="notices_specialmention_submissionStartTime" value="${notices_specialmention_submissionstarttime}" />
				</p>
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>
					<input type="text" class="sText datetimemask" name="notices_specialmention_submissionEndTime" id="notices_specialmention_submissionEndTime" value="${notices_specialmention_submissionendtime}" />
				</p>

			</div>
		</c:if>
		
		<c:if test="${i.type eq 'proprietypoint'}">
			<div id="proprietypoint" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartTime" text="Submission Start Time"></spring:message></label>
					<input type="text" class="sText" maxlength="5" name="proprietypoint_submissionStartTime" id="proprietypoint_submissionStartTime" value="${proprietypoint_submissionstarttime}" />
					<label style="margin-left: 5px;">
						(<b>Format</b> = &nbsp;<spring:message code="generic.hour" text="hours"/> <b>:</b> <spring:message code="generic.minute" text="minutes"/>)
					</label>
				</p>
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time"></spring:message></label>
					<input type="text" class="sText" maxlength="5" name="proprietypoint_submissionEndTime" id="proprietypoint_submissionEndTime" value="${proprietypoint_submissionendtime}" />
					<label style="margin-left: 5px;">
						(<b>Format</b> = &nbsp;<spring:message code="generic.hour" text="hours"/> <b>:</b> <spring:message code="generic.minute" text="minutes"/>)
					</label>
				</p>
			</div>
		</c:if>
		
		</c:forEach>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" />
			</p>
		</div>
	</div>
	<input type="hidden" id="invalidNumber" value="<spring:message code='client.NAN' text='Not a proper number.' />" />
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="invalidDaysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" value="<spring:message code='resolutions.invalidDaysBetweenSubmissionEndDateAndLastDiscussionDateOfSession' text='Please Enter Days Between SubmissionEndDate And LastDiscussionDateOfSession' />" />
	<input type="hidden" id="noDiscussionDateSelected" value="<spring:message code='resolutions.noDiscussionDateSelected' text='Please set discussion date/s' />" />
	<input type="hidden" id="ajaxErrorOccured" value="<spring:message code='ajaxErrorOccured' text='Some Error Occured' />" />
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="confirmFileChangeMsg" value="<spring:message code='generic.confirmFileChangeMsg' text='Are you sure to change this file?'/>"/>
</body>
</html>