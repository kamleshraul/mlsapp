<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	//this is for autosuggest
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	//this is for loading sessions,ministries,group,departments,subdepartments,answering dates
	function loadSubDepartments(ministry,department){
		$.get('ref/subdepartments/'+ministry+'/'+department,function(data){
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);
			}else{
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);
			}
		});
	}

	function loadDepartments(ministry){
		$.get('ref/departments/'+ministry,function(data){
			$("#department").empty();
			var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#department").html(departmentText);
			loadSubDepartments(ministry,data[0].id);
			}else{
				$("#department").empty();
				var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#department").html(departmentText);				
				$("#subDepartment").empty();				
			}
		});
	}

	function loadAnsweringDates(group,ministry){
		$.get('ref/group/'+group+'/answeringdates',function(data){
			if(data.length>0){
				$("#answeringDate").empty();				
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					answeringDatesText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#answeringDate").html(answeringDatesText);						
			}else{
				$("#answeringDate").empty();
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#answeringDate").html(answeringDatesText);				
			}			
			loadDepartments(ministry);
		});
	}

	function loadGroup(ministry){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);
			loadAnsweringDates(data.id,ministry);			
		});
	}	
		
	$(document).ready(function(){	
				
		$("#ministry").change(function(){
			if($(this).val()!=''){
			loadGroup($(this).val());
			}else{
				$("#formattedGroup").val("");
				$("#group").val("");				
				$("#department").empty();				
				$("#subDepartment").empty();				
				$("#answeringDate").empty();		
				$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
				$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
			}
		});

		$("#department").change(function(){
			if($(this).val()!=''){
			loadSubDepartments($("#ministry").val(),$(this).val());
			}
		});
				
		//initially only minsitry will be visible as either disabled or enabled
		if($("#ministrySelected").val()==''){
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");		
		}
		if($("#departmentSelected").val()==''){
			$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}
		if($("#subDepartmentSelected").val()==''){
			$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		}
		if($("#answeringDateSelected").val()==''){
		$("#answeringDate").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
		$("#answeringDate").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}		
		//autosuggest		
		$( ".autosuggest" ).autocomplete({
			minLength:1,			
			source:'ref/members?session='+$("#session").val(),
			select:function(event,ui){
			var text="<input type='hidden' name='"+$(this).attr("id")+"' value='"+ui.item.id+"'>";
			$(this).removeAttr("id");
			$(this).removeAttr("name");			
			$(this).append(text);
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
				console.log(optionClass);
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
				$.getJSON( 'ref/members?session='+$("#session").val(), {
					term: extractLast( request.term )
				}, response );
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
		
		$("#submit").click(function(e){
			var deviceTypeTemp='${domain.type.type}';			
						
			//-----------------------------------------------------------------------------------------------------------------------------
			if((deviceTypeTemp=='questions_halfhourdiscussion_standalone') || (deviceTypeTemp=='questions_halfhourdiscussion_from_question')){
				
				var memberNumbers=0;
				var memberComparator='${numberOfSupportingMembersComparator}';
				var selectedMembers=Math.floor(parseInt($("#selectedSupportingMembers").val().split(",").length)/2);
				
				memberNumbers=parseInt('${numberOfSupportingMembers}');
				
				//added to validate session year and quetion number for half hour discussion--
				if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
					$.prompt('Provide proper reference question number.');
					return false;
				}
				//-----------------------------------------------------------------------------
				
				if((memberNumbers > 0) && (memberComparator!=null) &&(memberComparator!="")){
										
					if(memberComparator=="eq"){
						if(!(selectedMembers == memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}	
					}else if(memberComparator=="le"){
						if(!(selectedMembers <= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}else if(memberComparator=="ge"){
						if(!(selectedMembers >= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}else if(memberComparator=="gt"){
						if(!(selectedMembers > memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}else if(memberComparator=="lt"){
						if(!(selectedMembers < memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}					
				}
			}
		});
		//-----------------------------------------------------
		//send for approval
		$("#sendforapproval").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});	
			
			//-------vikas dhananjay----------------------------------
			var deviceTypeTemp='${domain.type.type}';
			
			if((deviceTypeTemp=='questions_halfhourdiscussion_standalone') || (deviceTypeTemp=='questions_halfhourdiscussion_from_question')){
				
				var memberNumbers=0;
				var memberComparator='${numberOfSupportingMembersComparator}';
				var selectedMembers=Math.floor(parseInt($("#selectedSupportingMembers").val().split(",").length)/2);
				
				memberNumbers=parseInt('${numberOfSupportingMembers}');
				
				//added to validate session year and quetion number for half hour discussion--
				if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
					$.prompt('Provide proper reference question number.');
					return false;
				}
				
				if((memberNumbers > 0) && (memberComparator!=null) &&(memberComparator!="")){
										
					if(memberComparator=="eq"){
						if(!(selectedMembers == memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}
					}else if(memberComparator=="le"){
						if(!(selectedMembers <= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}					
					}else if(memberComparator=="ge"){
						if(!(selectedMembers >= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}					
					}else if(memberComparator=="gt"){
						if(!(selectedMembers > memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}else if(memberComparator=="lt"){
						if(!(selectedMembers < memberNumbers)){
														
							var jump = "selectedSupportingMembers";
							var new_position = $('#'+jump).offset();
							window.scrollTo(new_position.left,new_position.top);
							
							$.prompt($("#supportError").attr('title'));							
							return false;							
						}					
					}					
				}
			}
			//--------------------------------------------------------
			
			$.prompt($('#confirmSupportingMembersMessage').val()+$("#selectedSupportingMembers").val(),{
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
			
			//---------vikas dhananjay--------------------------------------
			var deviceTypeTemp='${domain.type.type}';
			
			if((deviceTypeTemp=='questions_halfhourdiscussion_standalone') || (deviceTypeTemp=='questions_halfhourdiscussion_from_question')){
				
				var submissionStartDate= '${startDate}';
				var submissionEndDate= '${endDate}';	
				
				if( (new Date().getTime() < new Date(submissionStartDate).getTime())){
					$.prompt('Too early to submit.');
				    return false;
				}
				if( (new Date().getTime() > new Date(submissionEndDate).getTime())){
					$.prompt('Too late to submit.');
				    return false;
				}	
				//-------21012013
				/* if($("#primaryMember").val()==null||$("primaryMember").val()==""){
					alert($("#primaryMemberEmpty").attr('title'));					
					return false;
				}
				
				if($("#subject").val()==null||$("subject").val()==""){
					alert($("#subjectEmpty").attr('title'));					
					return false;
				}
				if($("#questionText").attr('title')==null||$("questionText").val()==""){
					alert($("#questionEmpty").attr('title'));					
					return false;
				}					
				if($("#ministryEmpty").attr('title')==null||$("ministry").val()==""){
					alert($("#ministryEmpty").attr('title'));					
					return false;
				} */
				
				var memberNumbers=0;
				var memberComparator='${numberOfSupportingMembersComparator}';
				var selectedMembers=Math.floor(parseInt($("#selectedSupportingMembers").val().split(",").length)/2);
				
				memberNumbers=parseInt('${numberOfSupportingMembers}');
				
				//added to validate session year and quetion number for half hour discussion--
				if($('#halfHourDiscussionReference_questionNumber').val()==null || $('#halfHourDiscussionReference_questionNumber').val()==""){
					$.prompt('Provide proper reference question number.');
					return false;
				}
				alert("selected members: " + selectedMembers + "member numbers: " + selectedMembers);
				if((memberNumbers > 0) && (memberComparator!=null) &&(memberComparator!="")){
										
					if(memberComparator=="eq"){
						if(!(selectedMembers == c)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}
					}else if(memberComparator=="le"){
						if(!(selectedMembers <= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}					
					}else if(memberComparator=="ge"){
						if(!(selectedMembers >= memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}					
					}else if(memberComparator=="gt"){
						if(!(selectedMembers > memberNumbers)){
							$.prompt($("#supportError").attr('title'));
							return false;
						}						
					}else if(memberComparator=="lt"){
						if(!(selectedMembers < memberNumbers)){
														
							var jump = "selectedSupportingMembers";
							var new_position = $('#'+jump).offset();
							window.scrollTo(new_position.left,new_position.top);
							
							$.prompt($("#supportError").attr('title'));							
							return false;							
						}					
					}					
				}
			}
			//-------------------------------------------------------------------------
			
			$.prompt($('#confirmQuestionSubmission').val(),{
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
		    	            });
		        }
			}});			
	        return false;  
	    }); 
	    //view supporting members status
	    $("#viewStatus").click(function(){
		    $.get('question/status/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
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
				
				
				var url = 'ref/questionid?strQuestionNumber='+questionNumber+'&strSessionId='+sessionId+'&deviceTypeId='+deviceTypeTemp+'&locale='+locale+'&view=view';
				
				//alert(url);
				
				$.get(url, function(data) {
					if(data.id==0){
						$.prompt('No question found.');
					}else if(data.id==-1){
						$.prompt('Please provide valid question number.');
					}else{
						$('#halfHourDiscussionReference_questionId_H').val(data.id);
						$.get('question/viewquestion?qid='+data.id,function(data){
							$.fancybox.open(data,{autoSize: false, width: 800, height:700});				
						},'html');
					}
				});
			}else{
				$.prompt('Please provide valid question number.');
			}
		});
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="question" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedQuestionType} ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p>
		<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
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
		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="formattedPrimaryMember" name="formattedPrimaryMember"  value="${formattedPrimaryMember}" type="text" class="sText"  readonly="readonly" class="sText">
		<input name="primaryMember" id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">		
		<form:errors path="primaryMember" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText" id="constituency" name="constituency">
	</p>
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
			<input class="sText" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
			<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
			<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>	
		</p>
	</c:if>
		
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${(domain.type.type=='questions_halfhourdiscussion_from_question' or domain.type.type=='questions_halfhourdiscussion_standalone') and (!(empty numberOfSupportingMembersComparator) and !(empty numberOfSupportingMembers))}">
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
	
	<p>
		<label class="centerlabel"><spring:message code="question.subject" text="Subject"/>*</label>
		<form:textarea path="subject" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError" />	
	</p>
	
	<p>
		<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/>*</label>
		<form:textarea path="questionText" cssClass="wysiwyg"></form:textarea>
		<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	
	<c:if test="${domain.type.type=='questions_shortnotice' or domain.type.type=='questions_halfhourdiscussion_from_question' }">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
		<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>		
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question' or domain.type.type=='questions_halfhourdiscussion_standalone'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>
	
	<c:choose>
	<c:when test="${! empty ministries}">
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect">
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
		</select>		
		<form:errors path="ministry" cssClass="validationError"/>
				
		<label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="formattedGroup" name="formattedGroup"  readonly="readonly" value="${formattedGroup}">		
		<input type="hidden" id="group" name="group" value="${group }">
		<form:errors path="group" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<select name="department" id="department" class="sSelect">
		<c:forEach items="${departments }" var="i">
		<c:choose>
		<c:when test="${i.id==departmentSelected }">
		<option value="${i.id }" selected="selected">${i.name}</option>
		</c:when>
		<c:otherwise>
		<option value="${i.id }" >${i.name}</option>
		</c:otherwise>
		</c:choose>
		</c:forEach>
		</select>
		<form:errors path="department" cssClass="validationError"/>	
		
		<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
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
	</p>	
		
	<p>
		<c:if test="${domain.type.type=='questions_starred'}">
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
		<%----changed 21012013-----------------------%>
		<%---------------------------Added by vikas & dhananjay-------------------------------------%>
		<c:choose>
			<c:when test="${domain.type.type=='questions_halfhourdiscussion_from_question' && !(empty discussionDates)}">
				<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
				<form:select path="discussionDate" cssClass="datemask sSelect" >
					<option value="<spring:message code='please.select' text='Please Select'/>">---<spring:message code='please.select' text='Please Select'/>---</option>
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
				</form:select>
				<%-- <form:errors path="discussionDate" cssClass="validationError"/> --%>
			</c:when>
			<c:when test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">		
				<div class="toolTip tpGreen clearfix">
					<p>
						<img src="./resources/images/template/icons/light-bulb-off.png">
						<spring:message code="discussionDatesNotSet" text="Discussion Dates Not Set for This Session"/>
					</p>
					<p></p>
				</div>			
			</c:when>
		</c:choose>
		
		<c:if test="${domain.type.type=='questions_starred'}">
			<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
			<form:select path="priority" cssClass="sSelect" items="${priorities}" itemLabel="name" itemValue="number">
			</form:select>
			<form:errors path="priority" cssClass="validationError"/>	
		</c:if>
	</p>	
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
	
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${internalStatusType=='questions_submit'}">
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
			<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef" disabled="disabled">
			<input id="submitquestion" type="button" value="<spring:message code='generic.submitquestion' text='Submit Question'/>" class="butDef" disabled="disabled">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef" disabled="disabled">
		</p>
		</c:when>
		<c:otherwise>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef">
			<input id="submitquestion" type="button" value="<spring:message code='generic.submitquestion' text='Submit Question'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
		</c:otherwise>
		</c:choose>
		
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="internalStatus" id="internalStatus" value="${internalStatus }">
	<input type="hidden" name="recommendationStatus" id="recommendationStatus" value="${recommendationStatus }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	<input type="hidden" name="halfHourDiscussionReference_questionId_H" id="halfHourDiscussionReference_questionId_H" />
	<%--21012013 --%>
	<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
	<input type="hidden" name="discussionDate" id="discussionDate" value="${discussionDateSelected}" />
	
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">

<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">

<label id="supportError" title='<spring:message code="question.limit.supportingmemebers" text="Please provide proper number of supporting members."></spring:message>'></label>
<label id="primaryMemberEmpty" title='<spring:message code="question.primaryMemberEmpty" text="Primary Member can not be empty."></spring:message>'></label>
<label id="subjectEmpty" title='<spring:message code="question.subjectEmpty" text="Subject can not be empty."></spring:message>'></label>
<label id="questionEmpty" title='<spring:message code="question.questionEmpty" text="Question Details can not be empty."></spring:message>'></label>
<label id="ministryEmpty" title='<spring:message code="question.ministry" text="Ministry can not be empty."></spring:message>'></label>
</div>
</body>
</html>