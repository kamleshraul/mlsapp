<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	
	<style type="text/css" media="print">
		.toolbar{
			display: none !important;
		}
		#questionText{
			width: 400px;
		}
		.wysiwyg{
			margin-left: 160px;
			position: static !important;
			overflow: visible !important; 
			
		}
		
	</style>
	<script type="text/javascript">
	//this is for autosuggest
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	
	/**** Load Sub Departments ****/
	function loadSubDepartments(ministry){
		$.get('ref/ministry/subdepartments?ministry=' + ministry+ '&session='+$('#session').val(), 
				function(data) {
			$("#subDepartment").empty();
			var subDepartmentText = 
				"<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";
			if(data.length > 0){
			for(var i=0 ; i<data.length ;i++) {
				subDepartmentText += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
			}
			$("#subDepartment").html(subDepartmentText);			
			}else{
				$("#subDepartment").empty();
				var subDepartmentText = 
					"<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);				
			}
			$('#originalSubDepartment').val('');
			$('#group').val('');
			$("#formattedGroup").val('');
			$('#answeringDate').val('');
			$('#answeringDate').change();
			//$('#originalAnsweringDate').val('');
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
		$.get('ref/group/' + group + '/answeringdates', function(data){
			if(data.length > 0){
				$("#answeringDate").empty();				
				var answeringDatesText = 
					"<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>";
				for(var i=0 ; i<data.length; i++){
					answeringDatesText += "<option value='" + data[i].id + "'>" + data[i].name;
				}
				$("#answeringDate").html(answeringDatesText);						
			}else{
				$("#answeringDate").empty();
				var answeringDatesText = 
					"<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>";
				$("#answeringDate").html(answeringDatesText);				
			}		
			$('#answeringDate').change();
			//$('#originalAnsweringDate').val('');
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

	function loadGroup(subdepartment){
		$.get('ref/subdepartment/' + subdepartment + '/group?'+
				'session=' + $("#session").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);			
			loadAnsweringDates(data.id);			
		}).fail(function(){
			if($("#ErrorMsg").val() != ''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}	
		
	$(document).ready(function(){	
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").
			prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}else{
			$("#subDepartment").
			prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");			
		}
		
		if($("#answeringDateSelected").val()==''){
			$("#answeringDate").
			prepend("<option value='' selected='selected'>----" + $("#pleaseSelectMsg").val() + "----</option>");
		}else{
			$("#answeringDate").
			prepend("<option value=''>----" + $("#pleaseSelectMsg").val() + "----</option>");
		}		
		//autosuggest		
		$( "#formattedPrimaryMember").autocomplete({
			minLength:3,			
			source:'ref/member/supportingmembers?session='+$("#session").val(),
			select:function(event,ui){			
			$("#primaryMember").val(ui.item.id);
			},
			change:function(event,ui){
			if($("#formattedPrimaryMember").val()==""){
				$("#primaryMember").val("");
			}				
			}
		});	
		
		$("select[name='" + controlName + "']").hide();	
		$( ".autosuggestmultiple" ).change(function(){
			//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
			//for this we iterate through the slect box selected value and check if that value is present in the 
			//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
			//then that value will be removed from the select box.
			var value=$(this).val();
			$("select[name='" + controlName + "'] option:selected").each(function(){
				var optionClass = $(this).attr("class");
				if(value.indexOf(optionClass)==-1){
					$("select[name='" + controlName + "'] option[class='" + optionClass + "']").remove();
				}		
			});	
			$("select[name='" + controlName + "']").hide();				
		});
		//http://api.jqueryui.com/autocomplete/#event-select
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:3,
			source: function( request, response ) {
				$.getJSON( 'ref/member/supportingmembers?'+
						'session=' + $("#session").val() +
						'&primaryMemberId=' + $('#primaryMember').val(), {
					term: extractLast( request.term )
				}, response ).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val() != ''){
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
				if($("select[name='" + controlName  +"']").length>0){
					if($("select[name='" + controlName + "'] option[value='" + ui.item.id + "']").length > 0){
					//if option being selected is already present then do nothing
					this.value = $(this).val();					
					$("select[name='" + controlName +"']").hide();						
					}else{
					//if option is not present then add it in select box and autocompletebox
					if(ui.item.id != undefined && ui.item.value != undefined){
					var text = 
						"<option value='" + ui.item.id + "' selected='selected' class='" + ui.item.value + "'></option>";
					$("select[name='" + controlName + "']").append(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}							
					$("select[name='" + controlName + "']").hide();								
					}
				}else{
					if(ui.item.id != undefined && ui.item.value != undefined){
					text = "<select name='" + $(this).attr("id") + "'  multiple='multiple'>";
					textoption = 
						"<option value='" + ui.item.id + "' selected='selected' class='" + ui.item.value + "'></option>";				
					text = text + textoption + "</select>";
					$(this).after(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}	
					$("select[name='" + controlName + "']").hide();									
				}		
				return false;
			}
		});	
		
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
		
		//-----------------------------------------------------
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
				if($('#halfHourDiscussionReference_questionNumber').val()==null
						|| $('#halfHourDiscussionReference_questionNumber').val()==""){
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
			$.prompt($('#sendForApprovalMsg').val() + $("#selectedSupportingMembers").val(),{
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
				var wysiwygVal = $(this).val().trim();
				if(wysiwygVal == "<p></p>" || wysiwygVal=="<p><br></p>" || wysiwygVal == "<br><p></p>"){
					$(this).val("");
				}
			});
			//$('#originalSubDepartment').val($('#subDepartment').val());
			//$('#originalAnsweringDate').val($('#answeringDate').val());
			
			//---------vikas dhananjay--------------------------------------
			var deviceTypeTemp = '${selectedQuestionType}';
			
			if(deviceTypeTemp == 'questions_halfhourdiscussion_from_question'){
				
				if(deviceTypeTemp == 'questions_halfhourdiscussion_from_question'){
					if($('#questionText').is('[readonly]')){
						$('#questionText').val($('#copyOfquestionText').val());	
					}
				}
				
				var submissionStartDate = '${startDate}';
				var submissionEndDate = '${endDate}';	
				
				if( (new Date().getTime() < new Date(submissionStartDate).getTime())){
					$.prompt($('#earlySubmissionMsg').val());					
				    return false;
				}
				if( (new Date().getTime() > new Date(submissionEndDate).getTime())){
					$.prompt($("#lateSubmissionMsg").val());					
				    return false;
				}					
				if($("#primaryMember").val()==null || $("primaryMember").val()==""){
					$.prompt($("#primaryMemberEmptyMsg").val());					
					return false;
				}
				
				if($("#subject").val()==null || $("subject").val()==""){
					$.prompt($("#subjectEmptyMsg").val());					
					return false;
				}
				if($("questionText").val()=="" || $("questionText").val()==""){
					$.prompt($("#questionEmptyMsg").val());					
					return false;
				}					
				if($("#ministry").val()==null || $("#ministry").val()==""){
					$.prompt($("#ministryEmptyMsg").val());					
					return false;
				}
				
				if(deviceTypeTemp == 'questions_halfhourdiscussion_from_question'){
					//added to validate quetion number for half hour discussion--
					if($('#halfHourDiscussionReference_questionNumber').val()==null 
							|| $('#halfHourDiscussionReference_questionNumber').val()==""){
						$.prompt($("#referenceQuestionIncorrectMsg").val());
						return false;
					}
				}
			}
			//-------------------------------------------------------------------------
			
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
		    	            }).fail(function (jqxhr, textStatus, err) {
		    	            	$.unblockUI();
		    	            	$("#error_p").html("Server returned an error\n" + err +
	                                    "\n" + textStatus + "\n" +
	                                    "Please try again later.\n"+jqxhr.status+"\n"+jqxhr.statusText).css({'color':'red', 'display':'block'});
		    	            	
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
	    
	  //--------------vikas dhananjay-----------------------------------
		//for viewing the refernced question
		$('#halfhourdiscussion_referred_question').click(function(){
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			if(questionNumber!=""){
				var sessionId = '${session}';
				var locale='${domain.locale}';
				var url = 'ref/questionid?strQuestionNumber=' + questionNumber +
						'&strSessionId=' + sessionId + 
						'&deviceTypeId=' + deviceTypeTemp + 
						'&locale=' + locale + 
						'&view=view';
	
				$.get(url, function(data) {
					if(data.id!=0 && data.id!=-1){
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid=' + data.id 
								+'&questionType=' + deviceTypeTemp ,function(data){
							$.fancybox.open(data,{autoSize: false, width: 800, height:700});				
						},'html').fail(function(){
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val())
	    						.css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.")
	    						.css({'color':'red', 'display':'block'});
	    					}
	    					scrollTop();
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
			}else{
				$.prompt($("#questionNumberIncorrectMsg").val());
			}
		});
		/***** added by sandeep singh *****/
		if($("#currentStatus").val() == 'question_submit'
			 || $("#currentStatus").val() == 'question_unstarred_submit' 
			 || $("#currentStatus").val() == 'question_shortnotice_submit'
			 || $("#currentStatus").val() == 'question_halfHourFromQuestion_submit'){
			$("#ministry").attr("disabled","disabled");
			$("#subDepartment").attr("disabled","disabled");
			$("#answeringDate").attr("disabled","disabled");
			$("#priority").attr("disabled","disabled");
			$("#subject").attr("readonly","readonly");
			$("#questionText").attr("readonly","readonly");
			$("#selectedSupportingMembers").attr("readonly","readonly");
			
		}
		/**** Vikas Gupta ****/
		$('#halfHourDiscussionReference_questionNumber').change(function(){
			
			var questionNumber = $('#halfHourDiscussionReference_questionNumber').val();
			var deviceTypeTemp='${questionType}';
			
			if(questionNumber!=""){
				
				var sessionId = '${session}';
				var locale='${domain.locale}';
				
				
				var url = 'ref/questionid?'+
						'strQuestionNumber=' + questionNumber + 
						'&strSessionId=' + sessionId + 
						'&deviceTypeId=' + deviceTypeTemp + 
						'&locale=' + locale +
						'&view=view';				
				$.get(url, function(data) {
						if(data.id!=0 && data.id!=-1){							
							$.get('question/getsubject?qid=' + data.id + '&text=1' + '&questionType=' + deviceTypeTemp ,function(data1){
								if(data1.length>0){
									if(data1[0][17]!='YES') { //validation check for yaadi laid status of the referred question
										$('#subject').val('');
										$('#questionText').wysiwyg('setContent', '');
										$('#answer').wysiwyg('setContent', '');
										$("#referenceDeviceType option").each(function() {
											$(this).removeAttr('selected', 'selected');
										});
										$("#referenceDeviceType option[value='-']").attr('selected', 'selected');
										$('#halfHourDiscussionReference_questionNumber').val('');
										
										$.prompt($('#referredQuestionNotLaidInYaadiMsg').val());								
										return false;
									} else {
										$("#halfHourDiscusionFromQuestionReferenceNumber").val(questionNumber);
										$('#halfHourDiscussionReference_questionId_H').val(data.id);
										$('#halfHourDiscusionFromQuestionReference').val(data.id);
										
										$('#subject').val(data1[0][1]);
										if($('#questionText').is('[readonly]')){
											$('#questionText-wysiwyg-iframe').contents().find('html').html(data1[0][2]);
											$('#copyOfquestionText').val(data1[0][2]);
										}else{
											$('#questionText').wysiwyg('setContent',data1[0][2]);
										}
										
										if($('#answer').is('[readonly]')){
											$('#answer-wysiwyg-iframe').contents().find('html').html(data1[0][6]);
											$('#copyOfanswer').val(data1[0][6]);
										}else{
											$('#answer').wysiwyg('setContent',data1[0][6]);
										}
										
										/*$("#formattedMinistry").val(data.formattedMinistry);
										$("#ministry").val(data.ministry);
										
										$("#formattedGroup").val(data.formattedGroup);
										$("#group").val(data.group);
										
										var subDepartmentText = "<option value=''>--" + $("#pleaseSelectMsg").val() + "--</option>";
										subDepartmentText += "<option value='" + data.subDepartment + "' selected='selected'>" + data.formattedSubDepartment + "</option>";
										$("#subDepartment").empty();
										$("#subDepartment").html(subDepartmentText);*/
										
										$("#referenceDeviceType option").each(function() {
											$(this).removeAttr('selected', 'selected');
										});
										$("#referenceDeviceType option[value='"+data1[0][18]+"']").attr('selected', 'selected');
									}									
								}								
							});							
						}else{
							$('#subject').val('');
							$('#questionText').wysiwyg('setContent', '');
							$('#answer').wysiwyg('setContent', '');
							$("#referenceDeviceType option").each(function() {
								$(this).removeAttr('selected', 'selected');
							});
							$("#referenceDeviceType option[value='-']").attr('selected', 'selected');
							$('#halfHourDiscussionReference_questionNumber').val('');
						}
					//}
				}).fail(function(){
					$('#subject').val('');
					$('#questionText').wysiwyg('setContent', '');
					$('#answer').wysiwyg('setContent', '');
					$("#referenceDeviceType option").each(function() {
						$(this).removeAttr('selected', 'selected');
					});
					$("#referenceDeviceType option[value='-']").attr('selected', 'selected');
					$('#halfHourDiscussionReference_questionNumber').val('');
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}
		});
		
		$('#new_record_ForNew').click(function(){	
			newQuestion_ForNew();
		});
		
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
				if(ministryVal!=''){
					loadSubDepartments(ministryVal);
				}else{
					$("#formattedGroup").val("");
					$("#group").val("");				
					$("#subDepartment").empty();				
					$("#answeringDate").empty();
					$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");				
					$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMsg").val()+"----</option>");					
				}
			}
		});
		
		$('#subDepartment').change(function(){
			$('#originalSubDepartment').val($(this).val());
			loadGroup($(this).val());
		});
		
		$('#answeringDate').change(function(){
			$('#originalAnsweringDate').val($(this).val());
		});
		
		//print pdf
		$('#Generate_PDF').click(function () { 
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
			var parameters = {	questionId:$("#id").val(),
			 					outputFormat:"PDF"
							};
			resourceURL = 'question/report/questionPrintReport' + parameters;
			form_submit('question/report/questionPrintReport', parameters, 'GET');
			$.unblockUI();
		});
 	});
	
	/**** new question ****/
	function newQuestion_ForNew() {
		showTabByIdAndUrl('details_tab','question/new?'+$("#gridURLParams_ForNew").val());
	}
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
	<div class="commandbar">
		<div class="commandbarContent">	
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE','QIS_TYPIST')">			
			<a href="#" id="new_record_ForNew" class="butSim">
				<spring:message code="question.new" text="New"/>
			</a> |
			</security:authorize>
		</div>
	</div>
	
	<%-- <c:if test="${not fn:endsWith(memberStatusType, 'complete')}"> --%>
	<div style="text-align: right">
		<a href="#" id="Generate_PDF">
			<img src="./resources/images/pdf_icon.png" style="width:25px;height:25px;">
		</a>
	</div>
	<%-- </c:if> --%>
		
<div class="fields clearfix watermark">
<form:form action="question" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
		
	<div id="reportDiv">

	<h2>${formattedQuestionType} ${formattedNumber}</h2>

	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p id="numberP">
		<c:choose>
			<c:when test="${fn:contains(selectedQuestionType,'questions_halfhourdiscussion')}">
				<label class="small"><spring:message code="question.halfhour.number" text="Notice Number"/>*</label>
			</c:when>
			<c:otherwise>
				<label class="small"><spring:message code="question.number" text="Motion Number"/>*</label>
			</c:otherwise>
		</c:choose>
		<c:choose>
		<c:when test="${memberStatusType=='question_complete' or memberStatusType=='question_incomplete'
						or memberStatusType=='question_unstarred_complete' or memberStatusType=='question_unstarred_incomplete' 
						or memberStatusType=='question_shortnotice_complete' or memberStatusType=='question_shortnotice_incomplete' 
						or memberStatusType=='question_halfHourFromQuestion_complete' or memberStatusType=='question_halfHourFromQuestion_incomplete' }">
		<security:authorize access="hasAnyRole('QIS_TYPIST')">
		<form:input path="number" cssClass="sText"/>
		</security:authorize>	
		</c:when>
		<c:otherwise>
		<security:authorize access="hasAnyRole('QIS_TYPIST')">
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">
		<input id="number" name="number" value="${domain.number}" type="hidden">
		</security:authorize>
		</c:otherwise>
		</c:choose>
		
		<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText"  readonly="readonly">
		<input id="number" name="number" value="${domain.number}" type="hidden">
		</security:authorize>		
				
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${(empty domain.number)}">
	<p>
		<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
		<c:choose>
			<c:when test="${memberStatusType=='question_complete' or memberStatusType=='question_incomplete'
						or memberStatusType=='question_unstarred_complete' or memberStatusType=='question_unstarred_incomplete' 
						or memberStatusType=='question_shortnotice_complete' or memberStatusType=='question_shortnotice_incomplete' 
						or memberStatusType=='question_halfHourFromQuestion_complete' or memberStatusType=='question_halfHourFromQuestion_incomplete' }">
				<security:authorize access="hasAnyRole('QIS_TYPIST')">
					<form:input path="number" cssClass="sText"/>
				</security:authorize>	
			</c:when>
			<c:otherwise>
				<security:authorize access="hasAnyRole('QIS_TYPIST')">
					<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">
					<input id="number" name="number" value="${domain.number}" type="hidden">
				</security:authorize>
			</c:otherwise>
		</c:choose>		
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${!(empty submissionDate)}">
	<p>
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">	
	</p>
	</c:if>
	
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
	
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${(selectedQuestionType=='questions_halfhourdiscussion_from_question') 
			and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
			<label style="display: inline; border: 1px double blue; padding: 5px; background-color: #DCE4EF; font-weight: bold;" class="centerlabel"><spring:message code="question.numberOfsupportingMembers" text="Number of Supporting Members"></spring:message>&nbsp;${numberOfSupportingMembersComparatorHTML}&nbsp;${numberOfSupportingMembers}</label>
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
		</c:forEach>		
		</select>
		</c:if>
		<a href="#" id="viewStatus"><spring:message code="question.viewstatus" text="View Status"></spring:message></a>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>
	
	<c:if test="${selectedQuestionType!='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
			<form:textarea path="subject" rows="2" cols="50"></form:textarea>
			<form:errors path="subject" cssClass="validationError" />	
		</p>	
		
		<p id="questionTextP">
			<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
			<form:textarea path="questionText" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>		 
			<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
		
		<c:if test="${selectedQuestionType=='questions_starred' or selectedQuestionType=='questions_unstarred'}">
			<p style="display: none;">
				<label class="wysiwyglabel"><spring:message code="question.reference" text="Reference Text"/>*</label>
				<form:textarea path="questionreferenceText" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
				<form:errors path="questionreferenceText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			</p>
		</c:if>
		
	</c:if>	
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
	
		<p>
			<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
			<input class="integer" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
			<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>	
		</p>
		
		<p>
			<label class="small"><spring:message code="question.halfhour.questionrefdevicetype" text="Reference Question Device Type: "/>*</label>
			<%-- <form:input path="referenceDeviceType" cssClass="sText" /> --%>
			<select id="referenceDeviceType" name="referenceDeviceType" class="sSelect">
				<option value="-"><spring:message code="please.select" /></option>
				<c:forEach items="${hdqRefDevices}" var="d">
					<c:choose>
						<c:when test="${d==hdqRefDeviceSelected}">
							<option value="${d}" selected="selected">${d}</option>
						</c:when>
						<c:otherwise>
							<option value="${d}">${d}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<form:errors path="referenceDeviceType" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>

		<p>
			<label class="small"><spring:message code="question.halfhour.questionrefdevicemember" text="Reference Question Member: "/>*</label>
			<form:input path="referenceDeviceMember" cssClass="sText" />
			<form:errors path="referenceDeviceMember" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="question.halfhour.questionrefdeviceanswer" text="Reference Question Answering Date: "/>*</label>
			<input id="referenceDeviceAnswerDate_view" class="datemask sText" type="text" value="${formattedRefDeviceAnswerDate}"/>
			<input id="referenceDeviceAnswerDate" name="referenceDeviceAnswerDate" class="datemask sText" type="hidden" value="${refDeviceAnswerDate}"/>
			<form:errors path="referenceDeviceAnswerDate" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
		
		<p>
			<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
			<form:textarea path="subject" rows="2" cols="50"></form:textarea>
			<form:errors path="subject" cssClass="validationError" />
		</p>	
		
		<p>
			<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
			<form:textarea path="questionText" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
		
		<p>
			<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/>*</label>
			<form:textarea path="answer" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			<form:errors path="answer" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
		
		<input type="hidden" name="halfHourDiscusionFromQuestionReferenceNumber" id="halfHourDiscusionFromQuestionReferenceNumber" value="${referredQuestionNumber}" />
	</c:if>
	
	<c:if test="${selectedQuestionType=='questions_shortnotice' or selectedQuestionType=='questions_halfhourdiscussion_from_question' }">
		<p>
			<c:choose>
				<c:when test="${selectedQuestionType=='questions_shortnotice'}">
					<label class="wysiwyglabel"><spring:message code="question.shortnoticeReason" text="Reason"/>*</label>
				</c:when>
				<c:otherwise>
					<label class="wysiwyglabel"><spring:message code="question.halfhourReason" text="Points to be discussed"/>*</label>
				</c:otherwise>
			</c:choose>
			<form:textarea path="reason" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
				
		</p>
	</c:if>		
	
	<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg invalidFormattingAllowed"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<c:if test="${internalStatusType != null }">
		<c:if test="${!empty internalStatusType}">
			<c:if test="${sectionofficer_remark != null}">
				<c:if test="${! empty sectionofficer_remark}">
					<c:if test="${internalStatusType=='question_final_rejection'}">
						<p>
							<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
							<form:textarea path="remarks" cssClass="wysiwyg invalidFormattingAllowed" readonly="true"></form:textarea>
						</p>
					</c:if>
				</c:if>
			</c:if>
		</c:if>
	</c:if>
	
	<c:if test="${not empty formattedMemberStatus}">
	<p id="mainStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedMemberStatus" name="formattedMemberStatus" value="${formattedMemberStatus }" type="text" readonly="readonly" class="sText">
	</p>
	</c:if>
	
	<table>
		<c:choose>
			<c:when test="${! empty ministries}">
				<tr>
					<td style="vertical-align: top;">
						<p>
							<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
							<input id="formattedMinistry" name="formattedMinistry" type="text" class="sText" value="${formattedMinistry}">
							<input name="ministry" id="ministry" type="hidden" value="${ministrySelected}">
							<form:errors path="ministry" cssClass="validationError"/>
							<br />
							<label class="small"><spring:message code="question.department" text="Department"/></label>
							<select name="subDepartment" id="subDepartment" class="sSelect">
								<c:forEach items="${subDepartments}" var="i">
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
								<input id="chartAnsweringDate" name="chartAnsweringDate" type="hidden"  value="${chartAnsweringDate}">
							</c:if>	
							<c:if test="${selectedQuestionType=='questions_halfhourdiscussion_from_question'}">
								<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
								<form:select path="discussionDate" cssClass="datemask sSelect" >
									<option value="">---<spring:message code='please.select' text='Please Select'/>---</option>
									<c:forEach items="${discussionDates}" var="i">
										<c:choose>
											<c:when  test="${i.value==discussionDateSelected}">
												<option value="${i.value}" selected="selected">${i.name}</option>
											</c:when>
											<c:otherwise>
												<option value="${i.value}">${i.name}</option>
											</c:otherwise>					
										</c:choose>
									</c:forEach>					
								</form:select>
								<form:errors path="discussionDate" cssClass="validationError"/>
							</c:if>			
						</p>	
					</td>
					<td style="vertical-align: top;">
						<p>
							<label class="small"><spring:message code="question.group" text="Group"/>*</label>
							<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
							<input type="hidden" id="group" name="group" value="${group }">
							<form:errors path="group" cssClass="validationError"/>
							<br/>							
							<c:if test="${selectedQuestionType == 'questions_starred'}">
							<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
								<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
								<form:select path="priority" cssClass="sSelect" items="${priorities}" itemLabel="name" itemValue="number" />
								<form:errors path="priority" cssClass="validationError"/>	
								<br />
								<label class="small"><spring:message code="question.submission_priority" text="Submission Priority"/></label>
								<select id="submissionPriority" name="submissionPriority" class="sSelect">
									<option value="${defaultSubmissionPriority}"><spring:message code="question.default_ordering_for_submission" text="Creation Order"/></option>	
									<c:forEach var="submissionOrder" begin="1" end="200" step="1">
										<c:choose>
											<c:when test="${not empty domain.submissionPriority and domain.submissionPriority!=defaultSubmissionPriority and submissionOrder==domain.submissionPriority}">
												<option value="${submissionOrder}" selected="selected">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
											</c:when>
											<c:otherwise>
												<option value="${submissionOrder}">${formater.formatNumberNoGrouping(submissionOrder, locale)}</option>
											</c:otherwise>
										</c:choose>										
									</c:forEach>
								</select>
							</security:authorize>
							<security:authorize access="hasAnyRole('QIS_TYPIST')">
								<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
								<form:select path="priority" cssClass="sSelect" items="${priorities}" itemLabel="name" itemValue="number" disabled="true" />
								<form:errors path="priority" cssClass="validationError"/>
								<input type="hidden" name="priority" value="${domain.priority}">
							</security:authorize>
							</c:if>
						</p>
					</td>
				</tr>	
			</c:when>	
			<c:otherwise>		
			<div class="toolTip tpGreen clearfix">
				<p>
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="rotationordernotpublished" text="Follwoing fields will be activated on {0}(Rotation Order Publishing Date)" arguments="${rotationOrderPublishDate}"/>
				</p>
				<p></p>
			</div>			
			</c:otherwise>
		</c:choose>	
	</table>
	
	<c:if test="${recommendationStatusType == 'question_processed_rejectionWithReason'}">
	<p style="display: none;">
	<label class="wysiwyglabel"><spring:message code="question.rejectionReason" text="Rejection reason"/></label>
	<form:textarea path="rejectionReason" cssClass="wysiwyg invalidFormattingAllowed" readonly="true"></form:textarea>
	</p>
	</c:if>
	</div>
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${memberStatusType=='question_complete' or memberStatusType=='question_incomplete'
						or memberStatusType=='question_unstarred_complete' or memberStatusType=='question_unstarred_incomplete'
						or memberStatusType=='question_shortnotice_complete' or memberStatusType=='question_shortnotice_incomplete'
						or memberStatusType=='question_halfHourFromQuestion_complete' or memberStatusType=='question_halfHourFromQuestion_incomplete'}">
			<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<security:authorize access="hasAnyRole('MEMBER_LOWERHOUSE','MEMBER_UPPERHOUSE')">
			<input id="sendforapproval" type="button" value="<spring:message code='question.sendforapproval' text='Send For Approval'/>" class="butDef">
			</security:authorize>
			<input id="submitquestion" type="button" value="<spring:message code='question.submitquestion' text='Submit Question'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
		</c:when>		
		</c:choose>
		
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
	<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	<input type="hidden" name="halfHourDiscussionReference_questionId_H" id="halfHourDiscussionReference_questionId_H" />
	<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<input type="hidden" name="questionType" id="questionType" value="${questionType}">
	<input type="hidden" name="deviceType" id="deviceType" value="${deviceType}"/>
	<input type="hidden" name="department" id="department" value="${departmentSelected }">
	<input type="hidden" name="originalSubDepartment" id="originalSubDepartment" value="${originalSubDepartment}">
	<input type="hidden" name="originalAnsweringDate" id="originalAnsweringDate" value="${originalAnsweringDate}">
	
</form:form>
<input id="currentStatus" value="${internalStatusType }" type="hidden">

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
<input type="hidden" id="referredQuestionNotLaidInYaadiMsg" value="<spring:message code='question.referredQuestionNotLaidInYaadiMsg' text='Referred question is NOT yet laid in the yaadi.. Hence cannot be referred now!!'/>"/>
</div>
</body>
</html>