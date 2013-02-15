<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.devicetypeconfig.edit" text="Edit Session Config"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title></title>
<script type="text/javascript">
	//fill contents
	function fillContent(){
		var selVal=$('#deviceType').val();
	    var selectedDiv='#'+selVal;
	  
	    var isBallotingRequired = "#"+$('#deviceType').val()+"_isBallotingRequired";
	    	    
	    var isBallotingRequiredValue = $(isBallotingRequired).val();
	   
		if(selVal=="questions_halfhourdiscussion_standalone"){
			
			var comparator=$("#comparator_hidden_standalone").attr('title');
			$('#questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator').val(comparator);

		}
		
		if(selVal=="questions_halfhourdiscussion_from_question"){
			
			var comparator=$("#comparator_hidden_from_question").attr('title');
			$('#questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator').val(comparator);
			
			
		}
		
		if(selVal=="questions_starred"){
			
			var comparator=$("#comparator_hidden_starred").attr('title');
			$('#questions_starred_numberOfSupportingMembersComparator').val(comparator);
			
			
		}
		
		if(selVal=="questions_unstarred"){
			
			var comparator=$("#comparator_hidden_unstarred").attr('title');
			$('#questions_unstarred_numberOfSupportingMembersComparator').val(comparator);
			
			
		}
		
		if(selVal=="questions_shortnotice"){
			
			var comparator=$("#comparator_hidden_shortnotice").attr('title');
			$('#questions_shortnotice_numberOfSupportingMembersComparator').val(comparator);
			
			
		}
	    
	    hideDivs();
	    $(selectedDiv).show();		  
	    
	    if(isBallotingRequiredValue != undefined){
		    if(isBallotingRequiredValue.length==4){
		    	
		    	$(isBallotingRequired).attr('checked','checked');
		    	
		    }else if(isBallotingRequiredValue.length==5){
		    	
		    	$(isBallotingRequired).removeAttr('checked');
		    }
	    }
	    
	    //alert("fillContent()");
	}
	
	//hide all the divs
	function hideDivs(){
	   $('div.formDiv').hide();
	   //alert("hideDivs()");
	}
	
	function getDiscussionDates(deviceTypeSelected, discussionDays) {
		//alert("abc");
		//console.log("abc");
				
		
		$.get('ref/session/'+ $("#id").val()+'/devicetypeconfig/' + discussionDays + '/discussiondates', function(data) {
			$('#'+deviceTypeSelected+'_discussionDates option').empty();
			var options = "";
			//alert("data: "+data+"; length: "+data.length);
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			//alert(options);
			$('#'+deviceTypeSelected+'_discussionDates').html(options);
		});
		
		
		
		//alert(discussionDays);
		//$('#questions_halfhourdiscussion_standalone_discussionDays').append("dummy");
		
	};
		
	//show the particular div as option is selected
	$('document').ready(function(){
	
		var deviceTypeSelected=$("#deviceTypeSelected").val();
		//console.log(deviceTypeSelected);
		$('#deviceType').val(deviceTypeSelected);
		
		fillContent();		
		
		if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'questions_halfhourdiscussion_from_question') {
			//console.log("bifercation if,start");
			if($("#"+deviceTypeSelected+"_discussion_days_hidden").attr('title')==""){
				$("#discussion_dates_para_"+deviceTypeSelected).hide();
			}else{
				var discussionDays=$("#"+deviceTypeSelected+"_discussion_days_hidden").attr('title').split("#");
				var i;
				
				for(i = 0; i < discussionDays.length; i++){
					$("#"+deviceTypeSelected+"_discussionDays option").each(function(){
						if(discussionDays[i]==$(this).val()){
							$(this).prop('selected',true);
						}	
					});
				}	
				 //-----------to refill discussion dates			
				$.get('ref/session/'+ $("#id").val()+'/devicetypeconfig/' + discussionDays + '/discussiondates', function(data) {
					$('#'+deviceTypeSelected+'_discussionDates option').empty();
					var options = "";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].id+"'>" + data[i].name
								+ "</option>";
					}
					//alert(options); 
					$('#'+deviceTypeSelected+'_discussionDates').html(options);
					
					
					//---to highlight the selected dates
					var discussionDates=$("#"+deviceTypeSelected+"_discussion_dates_hidden").attr('title').split("#");			
					var i;			
					
					//alert(discussionDates);
					
					for(i = 0; i < discussionDates.length; i++){
						$("#"+deviceTypeSelected+"_discussionDates option").each(function(){
							//alert(discussionDates[i]+" : "+$(this).val());
							if(discussionDates[i]==$(this).val()){
								$(this).prop('selected',true);
							}	
						});
					}
				});
				
			}
			//console.log("bifercation if,end");
		}
		
		$('.datemask').focus(function(){		
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});		
		$('.datetimemask').focus(function(){		
			if($(this).val()==""){
				$(".datetimemask").mask("99/99/9999 99:99:99");
			}
		});
		
		$('#deviceType').change(function(){	

			deviceTypeSelected=$('#deviceType').val();			
			//console.log(deviceTypeSelected);
			var ribbon="${type}";
			
			if(ribbon=="success"){
				$("#ribbon").hide();	
			}				
			fillContent();
			
		//------------------------------------------------------------
		
		if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'questions_halfhourdiscussion_from_question') {
			//console.log("bifercation if,start");
			if($("#"+deviceTypeSelected+"_discussion_days_hidden").attr('title')==""){
				$("#discussion_dates_para_"+deviceTypeSelected).hide();
			}else{
				var discussionDays=$("#"+deviceTypeSelected+"_discussion_days_hidden").attr('title').split("#");
				var i;
				
				for(i = 0; i < discussionDays.length; i++){
					$("#"+deviceTypeSelected+"_discussionDays option").each(function(){
						if(discussionDays[i]==$(this).val()){
							$(this).prop('selected',true);
						}	
					});
				}	
				 //-----------to refill discussion dates			
				$.get('ref/session/'+ $("#id").val()+'/devicetypeconfig/' + discussionDays + '/discussiondates', function(data) {
					$('#'+deviceTypeSelected+'_discussionDates option').empty();
					var options = "";
					for ( var i = 0; i < data.length; i++) {
						options += "<option value='"+data[i].id+"'>" + data[i].name
								+ "</option>";
					}
					//alert(options); 
					$('#'+deviceTypeSelected+'_discussionDates').html(options);
					
					
					//---to highlight the selected dates
					var discussionDates=$("#"+deviceTypeSelected+"_discussion_dates_hidden").attr('title').split("#");			
					var i;			
					
					//alert(discussionDates);
					
					for(i = 0; i < discussionDates.length; i++){
						$("#"+deviceTypeSelected+"_discussionDates option").each(function(){
							//alert(discussionDates[i]+" : "+$(this).val());
							if(discussionDates[i]==$(this).val()){
								$(this).prop('selected',true);
							}	
						});
					}
				});
				
			}
			//console.log("bifercation if,end");
		}
			
			
			
			//----------------------------------------------------------------------
			
			
			/* questions_halfhourdiscussion_standalone_discussion_days_hidden */
			/* var discussionDays=$("#"+deviceTypeSelected+"_discussion_days_hidden").attr('title').split("#");
			alert(deviceTypeSelected + " " +discussionDays);
			 //-----------to refill discussion dates			
			$.get('ref/session/'+ $("#id").val()+'/devicetypeconfig/' + discussionDays + '/discussiondates', function(data) {
				$('#'+deviceTypeSelected+'_discussionDates option').empty();
				var options = "";
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].id+"'>" + data[i].name
							+ "</option>";
				}
				alert(options); 
				$('#'+deviceTypeSelected+'_discussionDates').html(options);
				
				
				//---to highlight the selected dates
				var discussionDates=$("#"+deviceTypeSelected+"_discussion_dates_hidden").attr('title').split("#");			
				var i;			
				
				//alert(discussionDates);
				
				for(i = 0; i < discussionDates.length; i++){
					$("#"+deviceTypeSelected+"_discussionDates option").each(function(){
						//alert(discussionDates[i]+" : "+$(this).val());
						if(discussionDates[i]==$(this).val()){
							$(this).prop('selected',true);
						}	
					});
				}
			}); */
			
	    });
		
		$('#questions_halfhourdiscussion_standalone_discussionDays').change(function(){
			var discussionDays=$("#"+deviceTypeSelected+"_discussionDays").val();	
			getDiscussionDates(deviceTypeSelected, discussionDays);
			$("#discussion_dates_para_standalone").show();
	    });
		
		
		$('#questions_halfhourdiscussion_from_question_discussionDays').change(function(){
			var discussionDays=$("#"+deviceTypeSelected+"_discussionDays").val();	
			getDiscussionDates(deviceTypeSelected, discussionDays);	
			$("#discussion_dates_para_from_question").show();
	    });
		
		//--------------------------------------
		
		$('#submit').click(function(){
			
			var deviceType = $('#deviceType').val();
			var selectedDiv = "#" + deviceType;
			var isBallotingRequired = "#" + deviceType+"_isBallotingRequired";
			var isBallotingRequiredHidden = "#" +  deviceType+"_isBallotingRequired_Hidden";
			
			if($(isBallotingRequired).is(":checked")) {
				
				$(isBallotingRequiredHidden).val("true");
				
			}else {
				
				$(isBallotingRequiredHidden).val("false");
			}
			if($('#questions_halfhourdiscussion_standalone_discussionDays').val()=="") {
				$('#discussion_days_hidden_standalone').val($('#questions_halfhourdiscussion_standalone_discussionDays').val());
			}
			
			if($('#questions_halfhourdiscussion_standalone_discussionDates').val()=="") {
				$('#discussion_dates_hidden_standalone').val($('#questions_halfhourdiscussion_standalone_discussionDates').val());
			}
			
			if($('#questions_halfhourdiscussion_from_question_discussionDays').val()=="") {
				$('#discussion_days_hidden_from_question').val($('#questions_halfhourdiscussion_from_question_discussionDays').val());
			}
			
			if($('#questions_halfhourdiscussion_from_question_discussionDates').val()=="") {
				$('#discussion_dates_hidden_from_question').val($('#questions_halfhourdiscussion_from_question_discussionDates').val());
			}
			
						
			$("#deviceTypeSelected").val(deviceType);
			
			/* if($('#'+deviceType+'_numberOfSupportingMembers').val().match('^[0-9]+$')){ */
			$('#mainForm').append($(selectedDiv));
				
			$('#mainForm').submit();	
			/* }else{
				$.prompt($('#invalidNumber').val());
			}			 */
		}); 
		
		//alert("document()");
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
				
				<c:if test="${domain.house.type.type eq 'upperhouse'}">
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

				<c:if test="${domain.house.type.type eq 'upperhouse'}">
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
					<label id="comparator_hidden_starred" title="${questions_starred_numberofsupportingmemberscomparator}"></label>
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
					<label id="comparator_hidden_unstarred" title="${questions_unstarred_numberofsupportingmemberscomparator}"></label>
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
					<input type="checkbox" class="sCheck" id="questions_halfhourdiscussion_standalone_isBallotingRequired" value="${questions_halfhourdiscussion_standalone_isballotingrequired}" >
					<input type="hidden" id="questions_halfhourdiscussion_standalone_isBallotingRequired_Hidden" name="questions_halfhourdiscussion_standalone_isBallotingRequired" value="" />
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
					<select class="sSelect" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" style="width: 50px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="">---<spring:message code='client.prompt.select' text='Please Select'/>---</option>
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="comparator_hidden_standalone" title="${questions_halfhourdiscussion_standalone_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" value="${questions_halfhourdiscussion_standalone_numberofsupportingmembers}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_discussionDays" id="questions_halfhourdiscussion_standalone_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label id="questions_halfhourdiscussion_standalone_discussion_days_hidden" title="${questions_halfhourdiscussion_standalone_discussiondays}"></label>
					<input type="hidden" id="discussion_days_hidden_standalone" name="questions_halfhourdiscussion_standalone_discussionDays" />
				</p>
				
				<p id="discussion_dates_para_standalone">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Datess" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_discussionDates" id="questions_halfhourdiscussion_standalone_discussionDates" multiple="multiple">
					</select>
					<label id="questions_halfhourdiscussion_standalone_discussion_dates_hidden" title="${questions_halfhourdiscussion_standalone_discussiondates}"></label>
					<input type="hidden" id="discussion_dates_hidden_standalone" name="questions_halfhourdiscussion_standalone_discussionDates" />
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
					<label id="comparator_hidden_shortnotice" title="${questions_shortnotice_numberofsupportingmemberscomparator}"></label>
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
					<label id="comparator_hidden_from_question" title="${questions_halfhourdiscussion_from_question_numberofsupportingmemberscomparator}"></label>
				</p>
				
				<p style="display: inline;">		
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" value="${questions_halfhourdiscussion_from_question_numberofsupportingmembers}" style="width: 62px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_from_question_discussionDays" id="questions_halfhourdiscussion_from_question_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label id="questions_halfhourdiscussion_from_question_discussion_days_hidden" title="${questions_halfhourdiscussion_from_question_discussiondays}"></label>
					<input type="hidden" id="discussion_days_hidden_from_question" name="questions_halfhourdiscussion_from_question_discussionDays" />
				</p>
				
				<p id="discussion_dates_para_from_question">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Datess" /></label>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_from_question_discussionDates" id="questions_halfhourdiscussion_from_question_discussionDates" multiple="multiple">
					</select>
					<label id="questions_halfhourdiscussion_from_question_discussion_dates_hidden" title="${questions_halfhourdiscussion_from_question_discussiondates}"></label>
					<input type="hidden" id="discussion_dates_hidden_from_question" name="questions_halfhourdiscussion_from_question_discussionDates" />
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
	
</body>
</html>