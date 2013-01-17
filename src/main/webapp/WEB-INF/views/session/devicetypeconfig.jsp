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
		console.log("abc");
				
		
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
		console.log(deviceTypeSelected);
		$('#deviceType').val(deviceTypeSelected);
		
		fillContent();		
		
		if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'questions_halfhourdiscussion_from_question') {
			console.log("bifercation if,start");
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
			console.log("bifercation if,end");
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
			console.log(deviceTypeSelected);
			var ribbon="${type}";
			
			if(ribbon=="success"){
				$("#ribbon").hide();	
			}				
			fillContent();
			
		//------------------------------------------------------------
		
		if(deviceTypeSelected == 'questions_halfhourdiscussion_standalone' || deviceTypeSelected == 'questions_halfhourdiscussion_from_question') {
			console.log("bifercation if,start");
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
			console.log("bifercation if,end");
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
			
			$('#mainForm').append($(selectedDiv));
			
			$('#mainForm').submit();
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
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<c:set var="key" value="questions_starred_submissionStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionStartDate" id="questions_starred_submissionStartDate"
						value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<c:set var="key" value="questions_starred_submissionEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionEndDate" id="questions_starred_submissionEndDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionFirstBatchStartDate" text="Submission First Batch Start Date" /></label>
					<c:set var="key" value="questions_starred_submissionFirstBatchStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionFirstBatchStartDate" id="questions_starred_submissionFirstBatchStartDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionFirstBatchEndDate" text="Submission First Batch End Date" /></label>
					<c:set var="key" value="questions_starred_submissionFirstBatchEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionFirstBatchEndDate" id="questions_starred_submissionFirstBatchEndDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionSecondBatchStartDate" text="Submission Second Batch Start Date" /></label>
					<c:set var="key" value="questions_starred_submissionSecondBatchStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionSecondBatchStartDate" id="questions_starred_submissionSecondBatchStartDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionSecondBatchEndDate" text="Submission Second Batch End Date" /></label>
					<c:set var="key" value="questions_starred_submissionSecondBatchEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_submissionSecondBatchEndDate" id="questions_starred_submissionSecondBatchEndDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<c:set var="key" value="questions_starred_firstBallotDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_starred_firstBallotDate" id="questions_starred_firstBallotDate" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInFirstBatch" text="Number of Question In First Batch" /></label>
					<c:set var="key" value="questions_starred_NumberOfQuestionInFirstBatch"></c:set>
					<input type="text" class="sInteger" name="questions_starred_NumberOfQuestionInFirstBatch" id="questions_starred_NumberOfQuestionInFirstBatch" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.NumberOfQuestionInSecondBatch" text="Number of Question In Second Batch" /></label>
					<c:set var="key" value="questions_starred_NumberOfQuestionInSecondBatch"></c:set>
					<input type="text" class="sInteger" name="questions_starred_NumberOfQuestionInSecondBatch" id="questions_starred_NumberOfQuestionInSecondBatch" value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<c:set var="key" value="questions_starred_isBallotingRequired"></c:set>
					<input type="checkbox" class="sCheck" id="questions_starred_isBallotingRequired" value="${domain.parameters[key]}" >
					<input type="hidden" id="questions_starred_isBallotingRequired_Hidden" name="questions_starred_isBallotingRequired" value="" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.rotationOrderPublishingDate" text="Rotation Order Publishing Date " /></label>
					<c:set var="key" value="questions_starred_rotationOrderPublishingDate"></c:set>
					<input id="questions_starred_rotationOrderPublishingDate" name="questions_starred_rotationOrderPublishingDate" class="datemask sText" value="${domain.parameters[key]}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderCover" text="Rotation Order Covering Letter" /></label>
					<c:set var="key" value="questions_starred_rotationOrderCover"></c:set>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderCover" name="questions_starred_rotationOrderCover">${domain.parameters[key]}</textarea>
				</p>
					
				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderHeader" text="Rotation Order Header" /></label>
					<c:set var="key" value="questions_starred_rotationOrderHeader"></c:set>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderHeader" name="questions_starred_rotationOrderHeader">${domain.parameters[key]}</textarea>
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.rotationOrderFooter" text="Rotation Order Footer" /></label>
					<c:set var="key" value="questions_starred_rotationOrderFooter"></c:set>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_rotationOrderFooter" name="questions_starred_rotationOrderFooter">${domain.parameters[key]}</textarea>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.totalRoundsMemberBallot" text="Total Rounds In Member Ballot" /></label>
					<c:set var="key" value="questions_starred_totalRoundsMemberBallot"></c:set>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_totalRoundsMemberBallot" name="questions_starred_totalRoundsMemberBallot">${domain.parameters[key]}</textarea>
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.totalRoundsFinalBallot" text="Total Rounds In Final Ballot" /></label>
					<c:set var="key" value="questions_starred_totalRoundsFinalBallot"></c:set>
					<textarea class="wysiwyg" cols="50" rows="5" id="questions_starred_totalRoundsFinalBallot" name="questions_starred_totalRoundsFinalBallot">${domain.parameters[key]}</textarea>
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_unstarred'}">					
			<div id="questions_unstarred" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<c:set var="key" value="questions_unstarred_submissionStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_unstarred_submissionStartDate" id="questions_unstarred_submissionStartDate"
						value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<c:set var="key" value="questions_unstarred_submissionEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_unstarred_submissionEndDate" id="questions_unstarred_submissionEndDate" value="${domain.parameters[key]}" />
				</p>
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_halfhourdiscussion_standalone'}">					
			<div id="questions_halfhourdiscussion_standalone" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_submissionStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_submissionStartDate" id="questions_halfhourdiscussion_standalone_submissionStartDate"
						value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_submissionEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_submissionEndDate" id="questions_halfhourdiscussion_standalone_submissionEndDate" value="${domain.parameters[key]}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_isBallotingRequired"></c:set>
					<input type="checkbox" class="sCheck" id="questions_halfhourdiscussion_standalone_isBallotingRequired" value="${domain.parameters[key]}" >
					<input type="hidden" id="questions_halfhourdiscussion_standalone_isBallotingRequired_Hidden" name="questions_halfhourdiscussion_standalone_isBallotingRequired" value="" />
				</p>
				

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_firstBallotDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_standalone_firstBallotDate" id="questions_halfhourdiscussion_standalone_firstBallotDate" value="${domain.parameters[key]}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Questions" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_numberOfQuestions"></c:set>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_standalone_numberOfQuestions" id="questions_halfhourdiscussion_standalone_numberOfQuestions" value="${domain.parameters[key]}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator"></c:set>
					<select class="sSelect" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembersComparator" style="width: 50px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="comparator_hidden_standalone" title="${domain.parameters[key]}"></label>
				</p>
				
				<p style="display: inline;">		
					<c:set var="key" value="questions_halfhourdiscussion_standalone_numberOfSupportingMembers"></c:set>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" id="questions_halfhourdiscussion_standalone_numberOfSupportingMembers" value="${domain.parameters[key]}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_discussionDays"></c:set>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_discussionDays" id="questions_halfhourdiscussion_standalone_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label id="questions_halfhourdiscussion_standalone_discussion_days_hidden" title="${domain.parameters[key]}"></label>
					<input type="hidden" id="discussion_days_hidden_standalone" name="questions_halfhourdiscussion_standalone_discussionDays" />
				</p>
				
				<p id="discussion_dates_para_standalone">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Datess" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_standalone_discussionDates"></c:set>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_standalone_discussionDates" id="questions_halfhourdiscussion_standalone_discussionDates" multiple="multiple">
					</select>
					<label id="questions_halfhourdiscussion_standalone_discussion_dates_hidden" title="${domain.parameters[key]}"></label>
					<input type="hidden" id="discussion_dates_hidden_standalone" name="questions_halfhourdiscussion_standalone_discussionDates" />
				</p>
				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_shortnotice'}">					
			<div id="questions_shortnotice" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<c:set var="key" value="questions_shortnotice_submissionStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_shortnotice_submissionStartDate" id="questions_shortnotice_submissionStartDate"
						value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<c:set var="key" value="questions_shortnotice_submissionEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_shortnotice_submissionEndDate" id="questions_shortnotice_submissionEndDate" value="${domain.parameters[key]}" />
				</p>
				
			</div>
		</c:if>
		
		<c:if test="${i.type eq 'questions_halfhourdiscussion_from_question'}">					
			<div id="questions_halfhourdiscussion_from_question" class="formDiv">
				<p >
					<label class="small"><spring:message code="session.deviceType.submissionStartDate"
							text="Submission Start Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_submissionStartDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_submissionStartDate" id="questions_halfhourdiscussion_from_question_submissionStartDate"
						value="${domain.parameters[key]}" />
				</p>

				<p>
					<label class="small"><spring:message code="session.deviceType.submissionEndDate" text="Submission End Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_submissionEndDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_submissionEndDate" id="questions_halfhourdiscussion_from_question_submissionEndDate" value="${domain.parameters[key]}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.isBallotingRequired" text="is Ballotng Required" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_isBallotingRequired"></c:set>
					<input type="checkbox" class="sCheck" id="questions_halfhourdiscussion_from_question_isBallotingRequired" value="${domain.parameters[key]}" >
					<input type="hidden" id="questions_halfhourdiscussion_from_question_isBallotingRequired_Hidden" name="questions_halfhourdiscussion_from_question_isBallotingRequired" value="" />
				</p>
				

				<p>
					<label class="small"><spring:message code="session.deviceType.firstBallotDate" text="First Ballot Date" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_firstBallotDate"></c:set>
					<input type="text" class="datetimemask sText" name="questions_halfhourdiscussion_from_question_firstBallotDate" id="questions_halfhourdiscussion_from_question_firstBallotDate" value="${domain.parameters[key]}" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.numberOfQuestions" text="Number of Questions" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_numberOfQuestions"></c:set>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_from_question_numberOfQuestions" id="questions_halfhourdiscussion_from_question_numberOfQuestions" value="${domain.parameters[key]}" />
				</p>
				
				<p style="display: inline;">
					<%-- <label class="small"><spring:message code="session.deviceType.numberOfSupportingMembersComparator" text="Number of Supporting Members Comparator" /></label> --%>
					<label class="small"><spring:message code="session.deviceType.numberOfSupportingMembers" text="Number of Supporting Members" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator"></c:set>
					<select class="sSelect" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembersComparator" style="width: 50px; height: 22px; border: solid 1px #8d8e8d;">
						<option value="eq">&#61;</option>
						<option value="lt">&lt;</option>
						<option value="gt">&gt;</option>
						<option value="le">&le;</option>
						<option value="ge">&ge;</option>
					</select>
					<label id="comparator_hidden_from_question" title="${domain.parameters[key]}"></label>
				</p>
				
				<p style="display: inline;">		
					<c:set var="key" value="questions_halfhourdiscussion_from_question_numberOfSupportingMembers"></c:set>
					<input type="text" class="sInteger" name="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" id="questions_halfhourdiscussion_from_question_numberOfSupportingMembers" value="${domain.parameters[key]}" style="width: 112px" />
				</p>
				
				<p>
					<label class="small"><spring:message code="session.deviceType.discussionDays" text="Discussion Days" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_discussionDays"></c:set>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_from_question_discussionDays" id="questions_halfhourdiscussion_from_question_discussionDays" multiple="multiple">
						<option value="Monday"><spring:message code="week.days.monday" text="Monday" /></option>
						<option value="Tuesday"><spring:message code="week.days.tuesday" text="Tuesday" /></option>
						<option value="Wednesday"><spring:message code="week.days.wednesday" text="Wednesday" /></option>
						<option value="Thursday"><spring:message code="week.days.thursday" text="Thursday" /></option>
						<option value="Friday"><spring:message code="week.days.friday" text="Friday" /></option>
						<option value="Saturday"><spring:message code="week.days.saturday" text="Saturday" /></option>
					</select>
					<label id="questions_halfhourdiscussion_from_question_discussion_days_hidden" title="${domain.parameters[key]}"></label>
					<input type="hidden" id="discussion_days_hidden_from_question" name="questions_halfhourdiscussion_from_question_discussionDays" />
				</p>
				
				<p id="discussion_dates_para_from_question">
					<label class="small"><spring:message code="session.deviceType.discussionDates" text="Discussion Datess" /></label>
					<c:set var="key" value="questions_halfhourdiscussion_from_question_discussionDates"></c:set>
					<select class="sSelectMultiple" name="questions_halfhourdiscussion_from_question_discussionDates" id="questions_halfhourdiscussion_from_question_discussionDates" multiple="multiple">
					</select>
					<label id="questions_halfhourdiscussion_from_question_discussion_dates_hidden" title="${domain.parameters[key]}"></label>
					<input type="hidden" id="discussion_dates_hidden_from_question" name="questions_halfhourdiscussion_from_question_discussionDates" />
				</p>
				
			</div>
		</c:if>
		
		</c:forEach>
		<div class="fields">
			<h2></h2>
			<p class="tright">
				<input id="submit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			</p>
		</div>
	</div>
</body>
</html>