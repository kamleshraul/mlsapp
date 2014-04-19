<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		textarea[class=wysiwyg]{
			display:block;
		}
		 .ui-combobox-toggle {
		    position: absolute;
		    top: 0;
		    bottom: 0;
		    margin-left: -1px;
		    padding: 0;
		    /* support: IE7 */
		    *height: 1.7em;
		    *top: 0.1em;
 		 }
		  .ui-combobox-input {
		    margin: 0;
		    padding: 0.3em;
		  }
	</style>
	
	<script type="text/javascript"><!--
	//this is for autosuggest
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	var primaryMemberControlName=$(".autosuggest").attr("id");
	
	//this is for loading sessions,ministries,group,departments,subdepartments,answering dates
	/**** Load Sub Departments ****/
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
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}	

	function loadAnsweringDates(group,ministry){
		$.get('ref/group/'+group+'/answeringdates',function(data){
			if(data.length>0){
				$("#answeringDate").empty();				
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					answeringDatesText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#answeringDate").html(answeringDatesText);						
			}else{
				$("#answeringDate").empty();
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
				$("#answeringDate").html(answeringDatesText);		
			}			
			loadSubDepartments(ministry);
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function loadGroup(ministry){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);			
			loadAnsweringDates(data.id,ministry);			
		}).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function loadMinistries(session){
		$.get('ref/session/'+session+'/ministries',function(data){
			if(data.length>0){
				var minsitryText="<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					minsitryText+="<option value='"+data[i].id+"'>"+data[i].name;				
				}
				$("#ministry").empty();
				$("#ministry").html(minsitryText);
				loadGroup(data[i].id);
			}else{
				$("#ministry").empty();
				$("#formattedGroup").val("");
				$("#group").val("");				
				$("#department").empty();				
				$("#subDepartment").empty();				
				$("#answeringDate").empty();				
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
	
	$(document).ready(function(){
		
		/*$("#formattedMinistry").change(function(){
			var ministryVal=$('#ministry').val();
			console.log(ministryVal);
			if(ministryVal!=''){
				console.log(ministryVal);
				loadGroup(ministryVal);
				loadSubDepartments(ministryVal);
			}else{
				$("#formattedGroup").val("");
				$("#group").val("");				
				//$("#department").empty();				
				$("#subDepartment").empty();				
				$("#answeringDate").empty();
				//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
				$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");					
			}
		});	*/				
		
		/* if($('#ministrySelected').val()=="" || $('#ministrySelected').val()==undefined){		
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>");
		} */
		$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
		$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");
		
		//autosuggest		
		$( "#formattedPrimaryMember").autocomplete({
			minLength:3,			
			source:'ref/member/supportingmembers?session='+$("#session").val(),
			select:function(event,ui){			
			$("#primaryMember").val(ui.item.id);
			}			
		});		
		$("select[name='"+controlName+"']").hide();			
		$( ".autosuggestmultiple" ).change(function(){
			//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
			//for this we iterate through the slect box selected value and check if that value is present in the 
			//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
			//then that value will be removed from the select box.
			var value=$(this).val();
			$("select[name='"+controlName+"'] option:selected").each(function(){
				var optionClass=$(this).attr("class");
				if(value.indexOf(optionClass)==-1){
					$("select[name='"+controlName+"'] option[class='"+optionClass+"']").remove();
				}		
			});	
			$("select[name='"+controlName+"']").hide();				
		});
		//http://api.jqueryui.com/autocomplete/#event-select
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:3,
			source: function( request, response ) {
				$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()+'&primaryMemberId='+$('#primaryMember').val(), {
					term: extractLast( request.term )
				}, response ).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			},			
			search: function() {
				var term = extractLast( this.value );
				if ( term.length < 2 ) {
					return false;
				}
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				//what happens when we are selecting a value from drop down
				var terms = $(this).val().split(",");
				//if select box is already present i.e atleast one option is already added
				if($("select[name='"+controlName+"']").length>0){
					if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
					//if option being selected is already present then do nothing
					this.value = $(this).val();					
					$("select[name='"+controlName+"']").hide();						
					}else{
					//if option is not present then add it in select box and autocompletebox
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
					$("select[name='"+controlName+"']").append(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}							
					$("select[name='"+controlName+"']").hide();								
					}
				}else{
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
					textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
					text=text+textoption+"</select>";
					$(this).after(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}	
					$("select[name='"+controlName+"']").hide();									
				}		
				return false;
			}
		});	
		
		//--------vikas dhananjay-----------------------------		
		//submit(draft) 
		$("#submit").click(function(e){	
			
			var deviceTypeTemp='${selectedQuestionType}';
			if(deviceTypeTemp=='questions_halfhourdiscussion_from_question'){
				$('#questionText').val($('#copyOfquestionText').val());
				//added to validate quetion number for half hour discussion--
				if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
					$.prompt($("#referenceQuestionIncorrectMsg").val());
					return false;
				}
			}
		});
		
		//send for approval
		$("#sendforapproval").click(function(e){
			//no need to send for approval in case of empty supporting members.
			if($("#selectedSupportingMembers").val()==""){
				$.prompt($('#supportingMembersEmptyMsg').val(),{
					buttons: {Ok:true}, callback: function(v){
				   		if(v){
				   			scrollTop();
				   			$('#selectedSupportingMembers').focus();
				   		}     						
					}
				});	
				return false;
			}

			//----------------vikas dhananjay----------------------------------------------------------------------------------------------
			var deviceTypeTemp='${selectedQuestionType}';
			if(deviceTypeTemp=='questions_halfhourdiscussion_from_question'){
				$('#questionText').val($('#copyOfquestionText').val());
				//added to validate quetion number for half hour discussion--
				if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
					$.prompt($("#referenceQuestionIncorrectMsg").val());
					return false;
				}
			}
			//-----------------------------------------------------------------------------

			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});	
			//-------------------------------
			$.prompt($('#sendForApprovalMsg').val()+$("#selectedSupportingMembers").val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post($('form').attr('action')+'?operation=approval',  
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
		//send for submission
		$("#submitquestion").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});		
			
			//---------------------vikas dhananjay---------------------
			var deviceTypeTemp='${selectedQuestionType}';
			
			if(deviceTypeTemp=='questions_halfhourdiscussion_from_question'){
				
				var submissionStartDate= '${startDate}';
				var submissionEndDate= '${endDate}';
				
				if((deviceTypeTemp=='questions_halfhourdiscussion_from_question')){
					$('#questionText').val($('#copyOfquestionText').val());
				}
				if( (new Date().getTime() < new Date(submissionStartDate).getTime())){
					$.prompt($('#earlySubmissionMsg').val());					
				    return false;
				}
				if( (new Date().getTime() > new Date(submissionEndDate).getTime())){
					$.prompt($("#lateSubmissionMsg").val());					
				    return false;
				}
				
				if($("#primaryMember").val()==null||$("primaryMember").val()==""){
					$.prompt($("#primaryMemberEmptyMsg").val());					
					return false;
				}
				
				if($("#subject").val()==null||$("subject").val()==""){
					$.prompt($("#subjectEmptyMsg").val());					
					return false;
				}
				if($("questionText").val()==""||$("questionText").val()==""){
					$.prompt($("#questionEmptyMsg").val());					
					return false;
				}					
				if($("ministry").val()==""||$("ministry").val()==""){
					$.prompt($("#ministryEmptyMsg").val());					
					return false;
				}
				
				if(deviceTypeTemp=='questions_halfhourdiscussion_from_question'){
					//added to validate quetion number for half hour discussion--
					if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
						$.prompt($("#referenceQuestionIncorrectMsg").val());
						return false;
					}
				}
			}			
			//------------------------------------------------------------
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		        	$.post($('form').attr('action')+'?operation=submit',  
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
		
		//view supporting members status
	    $("#viewStatus").click(function(){
		    $.get('question/status/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });
		
		
		//--------------vikas dhananjay 20012013--------------------------
		//for viewing the refernced question
		$('#halfhourdiscussion_referred_question').click(function(){
			
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			if(questionNumber!=""){
				
				var sessionId = '${session}';
				var locale='${domain.locale}';
				
				
				var url = 'ref/questionid?strQuestionNumber='+questionNumber+'&strSessionId='+sessionId+'&deviceTypeId='+deviceTypeTemp+'&locale='+locale+'&view=view';
				
				//$.prompt(url);
				
				$.get(url, function(data) {
					if(data.id==0){
						$.prompt($('#noQuestionMsg').val());
					}else if(data.id==-1){
						$.prompt($("#questionNumberIncorrectMsg").val());
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid='+data.id,function(data){
							$.fancybox.open(data,{autoSize: false, width: 800, height:700});				
						},'html');
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
				$.prompt($("#questionNumberIncorrectMsg").val());
			}
		});
		/**** Vikas Gupta ****/
		$('#halfHourDiscussionReference_questionNumber').change(function(){
			
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			//alert('helo'+questionNumber+':'+deviceTypeTemp);
			if(questionNumber!=""){
				
				var sessionId = '${session}';
				var locale='${domain.locale}';
				
				
				var url = 'ref/questionid?strQuestionNumber='+questionNumber+'&strSessionId='+sessionId+'&deviceTypeId='+deviceTypeTemp+'&locale='+locale+'&view=view';
				
				$.get(url, function(data) {
					if(data.id==0){
						$('#subject').val('');
						if($('#questionText').is('[readonly]')){
							$('#questionText-wysiwyg-iframe').contents().find('html').html('');
							$('#copyOfquestionText').val('');
						}
						$.prompt($('#noQuestionMsg').val());
					}else if(data.id==-1){
						$('#subject').val('');
						if($('#questionText').is('[readonly]')){
							$('#questionText-wysiwyg-iframe').contents().find('html').html('');
							$('#copyOfquestionText').val('');
						}
						$.prompt($("#questionNumberIncorrectMsg").val());
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/getsubject?qid='+data.id+'&text=1',function(data){
							$('#subject').val(data.name);
							if($('#questionText').is('[readonly]')){
								$('#questionText-wysiwyg-iframe').contents().find('html').html(data.value);
								$('#copyOfquestionText').val(data.value);
							}
						});
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
		});
		
		/**** To prevent the copy paste in supporting members field ****/
		$("#selectedSupportingMembers").bind('copy paste', function (e) {
		       e.preventDefault();
		 });
		

		$( "#formattedMinistry").autocomplete({
			minLength:3,			
			source:'ref/getministries?session='+$('#session').val(),
			select:function(event,ui){			
			$("#ministry").val(ui.item.id);
			},
			change:function(event,ui){
				var ministryVal=ui.item.id;	
				console.log(ministryVal);
				if(ministryVal!=''){
					console.log(ministryVal);
					loadGroup(ministryVal);
					loadSubDepartments(ministryVal);
				}else{
					$("#formattedGroup").val("");
					$("#group").val("");				
					//$("#department").empty();				
					$("#subDepartment").empty();				
					$("#answeringDate").empty();
					//$("#department").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
					$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");					
				}
			}
		});
		
		$('#number').change(function(){
			$.get('ref/getQuestionByNumberAndSession?number='+$(this).val()+'&session='+$('#session').val(),function(data){
				if(data){
					$('#numberError').css('display','inline');
				}
			});
		});
	});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">

<form:form action="question" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<div id="reportDiv">
	<h2><spring:message code="question.new.heading" text="Enter Question Details"/> (${formattedQuestionType})		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<security:authorize access="hasAnyRole('QIS_TYPIST')">	
	<p>
		<label class="small"><spring:message code="question.number" text="Motion Number"/>*</label>
		<form:input path="number" cssClass="sText"/>
		<form:errors path="number" cssClass="validationError"/>
		<span id='numberError' style="display: none; color: red;">
			<spring:message code="QuestionNumber.domain.NonUnique" text="Duplicate Number"></spring:message>
		</span>
		<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
	</p>
	</security:authorize>	
		
	<p style="display:none;">
		<label class="small"><spring:message code="question.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.year" text="Year"/>*</label>
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
		<label class="small"><spring:message code="question.type" text="Type"/>*</label>
		<input id="formattedQuestionType" name="formattedQuestionType" value="${formattedQuestionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${questionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
		
	<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input name="primaryMember" id="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>
	</security:authorize>
	
	<security:authorize access="hasAnyRole('QIS_TYPIST')">		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember" type="text" class="sText autosuggest" value="${formattedPrimaryMember}">
		<input name="primaryMember" id="primaryMember" type="hidden" value="${primaryMember}">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>	
	</security:authorize>	
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
			<input class="sText" type="text" name="halfHourDiscussionReference_questionNumber" id="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" />
			<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>	
		</p>
	</c:if>		
	
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${(selectedQuestionType=='questions_halfhourdiscussion_from_question' or selectedQuestionType=='questions_halfhourdiscussion_standalone') and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
			<label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel" id="supportingMemberMessage"><spring:message code="question.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label>										
		</c:if>
		
		<c:if test="${!(empty supporingMembers)}">		
		<select  name="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.member.getFullname()}"></option>
		</c:forEach>		
		</select>
		</c:if>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>
	
	<c:if test="${selectedQuestionType!='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
			<form:textarea path="subject" rows="2" cols="50"></form:textarea>
			<form:errors path="subject" cssClass="validationError" />	
		</p>	
		<c:if test="${not (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='upperhouse')}">
			<p>
				<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
				<form:textarea path="questionText" cssClass="wysiwyg"></form:textarea>
				<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
			</p>
			
			<c:if test="${selectedQuestionType=='questions_starred' or selectedQuestionType=='questions_unstarred'}">
				<p style="display: none;">
					<label class="wysiwyglabel"><spring:message code="question.reference" text="Reference Text"/>*</label>
					<form:textarea path="questionreferenceText" cssClass="wysiwyg"></form:textarea>
					<form:errors path="questionreferenceText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
				</p>
			</c:if>
		</c:if>
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
			<form:textarea path="subject" rows="2" cols="50" readonly="true"></form:textarea>
			<form:errors path="subject" cssClass="validationError" />
		</p>
		<p>
			<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
			<form:textarea path="questionText" cssClass="wysiwyg" readonly="true"></form:textarea>
			<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question' or (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='upperhouse')}">
	<p>
		<c:choose>
			<c:when test="${selectedQuestionType=='questions_shortnotice'}">
				<label class="wysiwyglabel"><spring:message code="question.shortnoticeReason" text="Reason"/>*</label>
			</c:when>
			<c:otherwise>
				<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
			</c:otherwise>
		</c:choose>
		<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>	
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question' or (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='upperhouse')}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>
	
	<table style="width: 100%;">
	<c:choose>
		<c:when test="${! empty ministries}">
			<tr>
				<td>
				<p>
					<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
					<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
					<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}">
					<%-- <select name="ministry" id="ministry" class="sSelect">
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
					</select> --%>
					<form:errors path="ministry" cssClass="validationError"/>
					<br />	
					<label class="small"><spring:message code="question.department" text="Department"/>*</label>
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
					<br />				
					<c:if test="${selectedQuestionType=='questions_starred'}">
						<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
						<select name="answeringDate" id="answeringDate" class="sSelect">
							<c:forEach items="${answeringDates }" var="i">
								<c:choose>
									<c:when test="${i.id==answeringDate }">
										<option value="${i.id }" selected="selected">${i.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${i.id }" >${i.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<form:errors path="answeringDate" cssClass="validationError"/>
					</c:if>	
					<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question' or selectedQuestionType=='questions_halfhourdiscussion_standalone'}">
						<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
						<form:select path="discussionDate" cssClass="datemask sSelect" >
							<option value="">---<spring:message code='please.select' text='Please Select'/>---</option>
							<c:forEach items="${discussionDates}" var="i">
								<c:choose>
									<c:when  test="${i==discussionDateSelected}">
										<option value="${i}" selected="selected">${i}</option>
									</c:when>
									<c:otherwise>
										<option value="${i}">${i}</option>
									</c:otherwise>					
								</c:choose>
							</c:forEach>					
						</form:select><td>
						<form:errors path="discussionDate" cssClass="validationError"/>
					</c:if>
				</p>
			</td>
				
			<td style="vertical-align: top;">
				<c:if test="${not (selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='lowerhouse')}">
					<p>
				</c:if>
				<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_standalone' and houseTypeType=='lowerhouse'}">
					<p style="display: none;" >
				</c:if>
					<label class="small"><spring:message code="question.group" text="Group" />*</label>
					<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">
					<input type="hidden" id="group" name="group" value="${group }">
					<form:errors path="group" cssClass="validationError"/>		
					<br />					
					<c:if test="${selectedQuestionType=='questions_starred'}">
						<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
						<form:select path="priority" cssClass="sSelect" items="${priorities}" itemLabel="name" itemValue="number" />
						<form:errors path="priority" cssClass="validationError"/>	
					</c:if>
				</p>	
			</td>
		</c:when>	
		<c:otherwise>		
		<div class="toolTip tpGreen clearfix">
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">
				<spring:message code="rotationordernotpublished" text="Follwoing fields will be activated on {0}(Rotation Order Publishing Date)" arguments="${rotationOrderPublishDate}"/>
			</p>
		</div>			
		</c:otherwise>
	</c:choose>
	</table>
</div>	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">	
			<input id="sendforapproval" type="button" value="<spring:message code='question.sendforapproval' text='Send For Approval'/>" class="butDef">
			</security:authorize>
			<input id="submitquestion" type="button" value="<spring:message code='question.submitquestion' text='Submit Question'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	
	
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input type="hidden" name="originalType"  id="originalType" value="${questionType}"/>
	<input type="hidden" name="halfHourDiscussionReference_questionId_H" id="halfHourDiscussionReference_questionId_H" />
	<input type="hidden" name="selectedSupportingMembersIfErrors" value="${selectedSupportingMembersIfErrors}" />
</form:form>

<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">

<input id="noQuestionMsg" value='<spring:message code="question.notfound" text="Question does not exist."></spring:message>' type="hidden" />
<input id="supportingMembersCountErrorMsg" value='<spring:message code="client.error.question.limit.supportingmemebers" text="Please provide proper number of supporting members."></spring:message>' type="hidden">
<input id="primaryMemberEmptyMsg" value='<spring:message code="client.error.question.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>' type="hidden">
<input id="subjectEmptyMsg" value='<spring:message code="client.error.question.subjectEmpty" text="Subject can not be empty."></spring:message>' type="hidden">
<input id="questionEmptyMsg" value='<spring:message code="client.error.question.questionEmpty" text="Question Details can not be empty."></spring:message>' type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="supportingMembersEmptyMsg" value="<spring:message code='client.error.supportingmemberempty' text='Supporting Member is required to send for approval.'/>" type="hidden">
<input id="referenceQuestionIncorrectMsg" value="<spring:message code='client.error.referencequestionincorrect' text='Please Provide Correct Question Number'/>" type="hidden">
<input id="questionNumberIncorrectMsg" value="<spring:message code='client.error.referencequestionincorrect' text='Please Provide Proper Question Number'/>" type="hidden">
<input id="questionReferenceEmptyMsg" value="<spring:message code='client.error.questionreferenceempty' text='Please Provide Proper Refernce Number'/>" type="hidden">
<input id="lateSubmissionMsg" value="<spring:message code='client.error.latesubmission' text='Too late to submit.'/>" type="hidden">
<input id="earlySubmissionMsg" value="<spring:message code='client.error.earlysubmission' text='Too early to submit.'/>" type="hidden">
<input id="sendForApprovalMsg" value="<spring:message code='client.prompt.approve' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>

</div>
</body>
</html>