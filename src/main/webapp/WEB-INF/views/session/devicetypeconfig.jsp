<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.devicetypeconfig.edit" text="Edit Session Config"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title></title>
	<script type="text/javascript">	
		//fill contents required for the device type fields 
		//called on document.ready & devicetype change events
		function fillContent(deviceTypeSelected){
			
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
			if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'resolutions_nonofficial') {
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
			    (deviceTypeSelected=="questions_halfhourdiscussion_standalone")
			  ||(deviceTypeSelected=="questions_halfhourdiscussion_from_question")
			  ||(deviceTypeSelected=="questions_starred")
			  ||(deviceTypeSelected=="questions_unstarred")
			  ||(deviceTypeSelected=="questions_shortnotice")
			) 
			{
				var comparator=$("#"+deviceTypeSelected+"_numberOfSupportingMembersComparator_hidden").attr('title');	    
				$('#'+deviceTypeSelected+'_numberOfSupportingMembersComparator').val(comparator);
			}			
			
			//------------------------initialize discussion days & dates---------------------//
		    if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'questions_halfhourdiscussion_from_question' || deviceTypeSelected == 'resolutions_nonofficial') {
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
			
		  	//initialize submission end time for resolutions_nonofficial	
		  	if(deviceTypeSelected == 'resolutions_nonofficial') {
		  		var submissionEndDate = $("#"+deviceTypeSelected+"_submissionEndDate").val();
		  		$("#"+deviceTypeSelected+"_submissionEndTime").val(submissionEndDate.split(" ")[1]);
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
	    	});			
		};			
		
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
				
				//set submission start date for resolutions_nonofficial such that it is same as that for questions_starred
				if(deviceTypeSeleted == 'resolutions_nonofficial') {
					var questionSubmissionStartDate = $('#questions_starred_submissionStartDate').val();
					if(questionSubmissionStartDate != "") {
						$('#'+deviceTypeSeleted+'_submissionStartDate').val(questionSubmissionStartDate);
					}
				}
				
				//set submission end date for resolutions_nonofficial
				if(deviceTypeSeleted == 'resolutions_nonofficial') {
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
					    	});
						}						
					}					
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
	<div class="fields clearfix">
		
		<div id="ribbon">		
			<%@ include file="/common/info.jsp" %>
		</div>
						
		<div id="headerDiv">
			<p> 
				<label class="small"><spring:message code="devicetype.name" text="Device Type"/></label>
				<select id="deviceType" name="deviceType">
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
					<input id="questions_starred_rotationOrderPublishingDate" name="questions_starred_rotationOrderPublishingDate" class="datemask sText" value="${questions_starred_rotationorderpublishingdate}" />
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
					<input type="text" class="sInteger" name="questions_starred_numberOfSupportingMembers" id="questions_starred_numberOfSupportingMembers" value="${questions_starred_numberofsupportingmembers}" style="width: 62px"/>
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
					<input type="text" class="sInteger" name="questions_unstarred_numberOfSupportingMembers" id="questions_unstarred_numberOfSupportingMembers" value="${questions_unstarred_numberofsupportingmembers}" style="width: 62px" />
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_halfhourdiscussion_standalone'}">					
			<div id="questions_halfhourdiscussion_standalone" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_submissionStartDate" id="questions_halfhourdiscussion_standalone_submissionStartDate"
						value="${questions_halfhourdiscussion_standalone_submissionstartdate}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_submissionEndDate" id="questions_halfhourdiscussion_standalone_submissionEndDate" value="${questions_halfhourdiscussion_standalone_submissionenddate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<input type="checkbox" class="sCheck isBallotingRequired" id="questions_halfhourdiscussion_standalone_isBallotingRequired" value="${questions_halfhourdiscussion_standalone_isballotingrequired}" >
					<input type="hidden" id="questions_halfhourdiscussion_standalone_isBallotingRequired_Hidden" name="questions_halfhourdiscussion_standalone_isBallotingRequired" value="" />
				</p>	
				
				<p class="questions_halfhourdiscussion_standalone_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotType" text="Ballot Type" /></label>
					<select class="sSelect" name="questions_halfhourdiscussion_standalone_ballotType" id="questions_halfhourdiscussion_standalone_ballotType">
						<c:forEach items="${ballotTypes}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>
					<label id="questions_halfhourdiscussion_standalone_ballotType_hidden" title="${questions_halfhourdiscussion_standalone_ballottype}"></label>
				</p>	
				
				<p class="questions_halfhourdiscussion_standalone_ballotfields">
					<label class="small"><spring:message code="session.deviceType.ballotEvents" text="Ballot Events" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_ballotEvents" id="questions_halfhourdiscussion_standalone_ballotEvents" multiple="multiple">
						<c:forEach items="${ballotEvents}" var="i">
							<option value="${i.type}">${i.name}</option>
						</c:forEach>
					</select>							
					<label style="display: none;" id="questions_halfhourdiscussion_standalone_ballotEvents_hidden" title="${questions_halfhourdiscussion_standalone_ballotevents}"></label>
				</p>		

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_firstBallotDate" id="questions_halfhourdiscussion_standalone_firstBallotDate" value="${questions_halfhourdiscussion_standalone_firstballotdate}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Questions" /></label>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_standalone_numberOfQuestions" id="questions_halfhourdiscussion_standalone_numberOfQuestions" value="${questions_halfhourdiscussion_standalone_numberofquestions}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<select class="sSelect" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" style="width: auto; height: 22px; border: solid 1px #8d8e8d;">
						<option value=""><spring:message code='client.prompt.justSelect' text='Select'/></option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator_hidden" title="${questions_halfhourdiscussion_standalone_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" value="${questions_halfhourdiscussion_standalone_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple discussionDays" name="questions_halfhourdiscussion_standalone_discussionDays" id="questions_halfhourdiscussion_standalone_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label style="display: none;" id="questions_halfhourdiscussion_standalone_discussionDays_hidden" title="${questions_halfhourdiscussion_standalone_discussiondays}"></label>
				</p>
				
				<p id="questions_halfhourdiscussion_standalone_discussionDates_para">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Dates" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_discussionDates" id="questions_halfhourdiscussion_standalone_discussionDates" multiple="multiple">
					</select>
					<label style="display: none;" id="questions_halfhourdiscussion_standalone_discussionDates_hidden" title="${questions_halfhourdiscussion_standalone_discussiondates}"></label>
				</p>
				
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
					<input type="text" class="sInteger" name="questions_shortnotice_numberOfSupportingMembers" id="questions_shortnotice_numberOfSupportingMembers" value="${questions_shortnotice_numberofsupportingmembers}" style="width: 62px" />
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
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" value="${questions_halfhourdiscussion_from_question_numberofsupportingmembers}" style="width: 62px" />
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
				<p>
					<label class="small"><spring:message code="session.deviceType.daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" text="Days between submission end date & last discussion date" /></label>
					<input type="text" class="sInteger" name="resolutions_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" id="resolutions_nonofficial_daysBetweenSubmissionEndDateAndLastDiscussionDateOfSession" value="${resolutions_nonofficial_daysbetweensubmissionenddateandlastdiscussiondateofsession}" />
				</p>
					
				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndTime" text="Submission End Time" /></label>					
					<input type="text" class="timemask sText" id="resolutions_nonofficial_submissionEndTime" />
					<input type="hidden" class="datetimemask sText" name="resolutions_nonofficial_submissionEndDate" id="resolutions_nonofficial_submissionEndDate" value="${resolutions_nonofficial_submissionenddate}" />
					<input type="hidden" class="datetimemask sText" name="resolutions_nonofficial_submissionStartDate" id="resolutions_nonofficial_submissionStartDate" value="${resolutions_nonofficial_submissionstartdate}" />
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
</body>
</html>