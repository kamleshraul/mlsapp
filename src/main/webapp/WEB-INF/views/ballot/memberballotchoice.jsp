<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {	
		$("#member").change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			var value=$(this).val();
			if(value!='-'){
			var parameters="member="+$(this).val()+"&session="
			+$("#session").val()+"&questionType="+$("#questionType").val();
			var resource='ballot/memberballot/listchoices';
			$.get(resource+'?'+parameters,function(data){
				$("#listchoices").empty();	
				$("#listchoices").html(data);
				$.unblockUI();		
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
			}else{
				$("#listchoices").empty();
				$.unblockUI();			
			}
			$("#errorDiv").hide();
			$("#successDiv").hide();
		});
		
		$("form").submit(function(e){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 
			var autofillingstartsat=parseInt($("#autofillingstartsat").val());
			var noOfAdmittedQuestion=parseInt($("#noOfAdmittedQuestions").val());
			var blankFormAutoFillingStartsAt=parseInt($("#blankFormAutoFillingStartsAt").val());
			var noOfMemberBallotChoicesExceptLast=parseInt($("#noOfMemberBallotChoicesExceptLast").val());
			
			/**** validation about providing reason for entering/updating question choices ****/
			if($('#reasonForChoicesUpdate').val()==undefined || $('#reasonForChoicesUpdate').val()=='') {
				$.unblockUI();
				$.prompt($("#blankReasonForChoicesUpdateMsg").val());
				return false;
			}

			/**** For Manually Entering Choices ****/
			if(autofillingstartsat==0){				
				var totalQuestionFilled=0;
				$(".question").each(function(){
					var value=$(this).val();
					if(value!='-'){
						totalQuestionFilled++;	
					}
				});
				//blank form submitted without clicking autofilling
				if(totalQuestionFilled==0){
					/*$.unblockUI();	
					$("#blankForm").val("yes");
					var msg=$("#submitBlankChoices").val()+" starting with Round:"+blankFormAutoFillingStartsAt;
					$.prompt(msg,{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});*/
					$.unblockUI();
					$.prompt($("#blankFormMsg").val());					
					return false;
				}
				/**** Total choices filled is leass than admitted questions.No auto filling will take place.****/
				if(totalQuestionFilled<noOfAdmittedQuestion){
					/*$.unblockUI();	
					$.prompt($("#limitedChoicesWillBeFilled").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});*/
					$.unblockUI();
					$.prompt($("#totalFilledLessThanAdmitted").val());
					return false;
				}
			}			
			/**** For Partially Auto Filling Choices ****/
			else if(autofillingstartsat>=1){
				var totalQuestionFilled=0;
				$(".question").each(function(){
					var value=$(this).val();
					var disabled=$(this).attr("disabled");
					if(value!='-'&&disabled==undefined){
						totalQuestionFilled++;	
					}
				});
				/****Auto filling at 1 ****/
				if(autofillingstartsat==1){
					$.unblockUI();
					$.prompt($("#autoFillStartsAt1").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});
					return false;
				}	
				/**** 1,2,3,4 round empty and auto filling clicked at the start of fifth round ****/
				if(totalQuestionFilled==0 && autofillingstartsat==noOfMemberBallotChoicesExceptLast+1){
					$.unblockUI();
					//$("#blankFormAutoFillingStartsFromLast").val("yes");
					$.prompt($("#blankFormAutoFillingStartsFromLastMsg").val()+$("#member option:selected").text()+" starting with round "+$("#totalRounds").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});
					return false;
				}
				/**** Partially filled choices and auto filling clicked at last round  ****/
				if(totalQuestionFilled < noOfAdmittedQuestion && autofillingstartsat==noOfMemberBallotChoicesExceptLast+1){
					$.unblockUI();	
					$.prompt($("#filledLessThanAdmittedRestFromLast").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});
					return false;
				}	
				if(totalQuestionFilled!=autofillingstartsat-1){
					$.unblockUI();	
					$.prompt($("#filledLessThanAdmitted").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	  }
					}});
					return false;
				}					
				/***Allow Submission even if AnsweringDate not specified ***/
				var totalAnsweringDateFilled=0;
				$(".answeringDate").each(function(){
					var value=$(this).val();
					var disabled=$(this).attr("disabled");
					if(value!='-'&&disabled==undefined){
						totalAnsweringDateFilled++;	
					}
				});
				if(totalAnsweringDateFilled!=autofillingstartsat-1){
					$.unblockUI();				
				 	//$.prompt($("#filledLessThanAdmitted").val());
				 	$.prompt($("#filledAnsweringDatesLessThanAdmitted").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	submitForm();
					  	}
					}});
					return false;
				}				
			}	
			
			/**** Total filled questions is equal to the number of admitted questions ****/
			$.unblockUI();
			$.prompt($("#submitChoices").val()+$("#member option:selected").text(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){					
						submitForm();
			  	  	 }
			}});	
			return false;
		});
	});
	
	function submitForm(){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.post($('form').attr('action'),  
	            $("form").serialize(),  
	            function(data){
					$("#listchoices").empty();	
   					$("#listchoices").html(data);   	   					
   					$('html').animate({scrollTop:0}, 'slow');
   				 	$('body').animate({scrollTop:0}, 'slow');	
					$.unblockUI();	   				 	   				
	            },'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
			});
	}
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<form action="ballot/memberballot/choices" method="post">
<p><label style="margin: 10px;"><spring:message
	code="memberballotchoice.member" text="Member" /></label>
	 <select id="member" name="member">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
	<c:forEach items="${eligibleMembers}" var="i">
		<c:choose>
		<c:when test="${!(empty selectedMember) && i.id==selectedMember}">
		<option value="${i.id }" selected="selected"><c:out value="${i.getFullname()}"></c:out></option>
		</c:when>
		<c:otherwise>
		<option value="${i.id }"><c:out value="${i.getFullname()}"></c:out></option>
		</c:otherwise>
		</c:choose>		
	</c:forEach>
</select>
</p>
<div id="listchoices">
</div>
<input type="hidden" id="session" name="session" value="${session }">
<input type="hidden" id="questionType" name="questionType" value="${questionType}">
</form>

 	<input type="hidden" name="pleaseSelect" id="pleaseSelect"
	value="<spring:message code='please.select' text='Please Select'/>">
	<input type="hidden" name="filledLessThanAdmitted" id="filledLessThanAdmitted"
	value="<spring:message code='memberballotchoice.filledlessthanadmitted' text='Number of choices is less than number of admitted questions.Since autofilling has been selected rest questions will be auto filled from chart?'/>">
	<input type="hidden" name="filledAnsweringDatesLessThanAdmitted" id="filledAnsweringDatesLessThanAdmitted"
	value="<spring:message code='memberballotchoice.filledAnsweringDatelessthanadmitted' text='Answering dates are not specified, do you still want to continue?'/>">
	<input type="hidden" name="submitChoices" id="submitChoices"
	value="<spring:message code='memberballotchoice.submitChoices' text='Do you want to submit choices for member :'/>">
	<input type="hidden" name="submitBlankChoices" id="submitBlankChoices"
	value="<spring:message code='memberballotchoice.submitBlankChoices' text='No choices filled .Questions will be auto filled from chart'/>">	
	<input type="hidden" name="blankFormAutoFillingStartsFromLastMsg" id="blankFormAutoFillingStartsFromLastMsg"
	value="<spring:message code='memberballotchoice.blankFormAutoFillingStartsFromLastMsg' text='No choices filled .Questions will be auto filled from chart for member :'/>">
	<input type="hidden" name="filledLessThanAdmittedRestFromLast" id="filledLessThanAdmittedRestFromLast"
	value="<spring:message code='memberballotchoice.filledLessThanAdmittedRestFromLast' text='Number of choices filled is less than number of admitted questions.Rest Questions will be auto filled from chart starting with last round'/>">
	<input type="hidden" name="autoFillStartsAt1" id="autoFillStartsAt1"
	value="<spring:message code='memberballotchoice.autoFillStartsAt1' text='Auto filling choices from begining will reset all entered choices(if any).Do you want to continue?'/>">
	<input type="hidden" name="limitedChoicesWillBeFilled" id="limitedChoicesWillBeFilled"
	value="<spring:message code='memberballotchoice.limitedChoicesWillBeFilled' text='Number of choices is less than number of admitted questions.No auto filling will take place.Do you still want to continue?'/>">
	<input type="hidden" name="blankFormMsg" id="blankFormMsg"
	value="<spring:message code='memberballotchoice.blankForm' text='No choices filled.If you want to auto fill choices from chart starting from last round click auto fill at last round'/>">
	<input type="hidden" name="totalFilledLessThanAdmitted" id="totalFilledLessThanAdmitted"
	value="<spring:message code='memberballotchoice.totalFilledLessThanAdmitted' text='Total choices filled is less than total admitted questions.If you want to auto fill choices from chart starting from last round click auto fill at last round'/>">
	<input type="hidden" id="blankReasonForChoicesUpdateMsg"
	value="<spring:message code='memberballotchoice.blankReasonForChoicesUpdateMsg' text='Please provide the reason for updating the question choices for selected member'/>">
	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>