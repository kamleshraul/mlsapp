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
			var autofillingstartsat=$("#autofillingstartsat").val();
			/**** For Manually Entering Choices ****/
			if(autofillingstartsat==0){
				var noOfAdmittedQuestion=$("#noOfAdmittedQuestions").val();
				var totalQuestionFilled=0;
				$(".question").each(function(){
					var value=$(this).val();
					if(value!='-'){
						totalQuestionFilled++;	
					}
				});
				if(totalQuestionFilled<noOfAdmittedQuestion){
					$.unblockUI();	
					$.prompt($("#filledLessThanAdmitted").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
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
					}});
				 	//$.prompt($("#filledLessThanAdmitted").val());	
					return false;
				}
			}
			
			/**** For Partially Auto Filling Choices ****/
			else if(autofillingstartsat>1){
				var totalQuestionFilled=0;
				$(".question").each(function(){
					var value=$(this).val();
					var disabled=$(this).attr("disabled");
					if(value!='-'&&disabled==undefined){
						totalQuestionFilled++;	
					}
				});
				if(totalQuestionFilled!=autofillingstartsat-1){
					$.unblockUI();	
					$.prompt($("#filledLessThanAdmitted").val(),{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
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
					}});
				 	//$.prompt($("#filledLessThanAdmitted").val());	
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
					}});
					return false;
				}
				
			}	
			
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
	        return false;  
	    }); 	   
	});
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
		<option value="${i.id }"><c:out value="${i.getFullname()}"></c:out></option>
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
	value="<spring:message code='memberballotchoice.filledlessthanadmitted' text='All choices are not specified, do you still want to continue?'/>">
	<input type="hidden" name="filledAnsweringDatesLessThanAdmitted" id="filledAnsweringDatesLessThanAdmitted"
	value="<spring:message code='memberballotchoice.filledAnsweringDatelessthanadmitted' text='Answering dates are not specified, do you still want to continue?'/>">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>