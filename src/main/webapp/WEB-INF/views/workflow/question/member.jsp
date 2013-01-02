<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	//this is for loading group,departments,subdepartments
	var statusBeforeGroupChange=$("#selectedInternalStatus").val();	
	function loadSubDepartments(ministry,department){
		if(department!=''){
		$.get('ref/subdepartments/'+ministry+'/'+department,function(data){
			$("#subDepartment").empty();
			var subDepartmentText="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);
			$("#subDepartment").prev().show();
			$("#subDepartment").show();	
			}else{
				$("#subDepartment").empty();
				$("#subDepartment").prev().hide();
				$("#subDepartment").hide();	
			}			
		});
		}
	}

	function loadDepartments(ministry){
		if(ministry!=''){
		$.get('ref/departments/'+ministry,function(data){
			$("#department").empty();
			var departmentText="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#department").html(departmentText);
			$("#department").prev().show();
			$("#department").show();	
			loadSubDepartments(ministry,data[0].id);			
			}else{
				$("#department").empty();
				$("#department").prev().hide();
				$("#department").hide();
				$("#subDepartment").empty();				
				$("#subDepartment").prev().hide();
				$("#subDepartment").hide();
			}
		});
		}
	}

	function loadGroup(ministry){
		if(ministry!=''){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#groupNumber").val(data.name);
			$("#group").val(data.id);
			$("#groupNumber").prev().show();
			$("#groupNumber").show();
			loadDepartments(ministry);
			//here we will check if group has changed.if it has then we will set internal and recommendation status as group changed and end 
			//the process.
			if($("#groupNumber").val()==$("#groupNumberForActor").val()){
				$("#selectedInternalStatus").val(statusBeforeGroupChange);				
				$("#internalStatus").val(statusBeforeGroupChange);
				$("#recommendationStatus").val(statusBeforeGroupChange);
				$("#pv_endflag").val("continue");
			}else{
				$("#groupNumberForActor").val($("#groupNumber").val());				//
				$("#internalStatusMaster option").each(function(){
					var text=$(this).text();
					var value=$(this).val();
					if(text=='question_workflow_decisionstatus_groupchanged'){
						statusBeforeGroupChange=$("#selectedInternalStatus").val();
						$("#selectedInternalStatus").val(value);
						$("#internalStatus").val(value);
						$("#recommendationStatus").val(value);
						$("#pv_endflag").val("end");
					}
				});
			}
			
		});
		}
	}	
	
	$(document).ready(function(){
		$("#ministry").change(function(){
			if($(this).val()!=''){
			loadGroup($(this).val());
			}else{
				$("#groupNumber").val("");
				$("#group").val("");
				$("#groupNumber").hide();
				$("#groupNumber").prev().hide();
				$("#department").empty();
				$("#department").hide();
				$("#department").prev().hide();
				$("#subDepartment").empty();
				$("#subDepartment").hide();
				$("#subDepartment").prev().hide();					
			}
		});

		$("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		});
		
		$("#viewCitation").click(function(){
			$.get('question/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});
		$("#copySubject").click(function(){
			$("#revisedSubject").val($("#subject").val());
			return false;			
		});
		$("#copyQuestionText").click(function(){
			$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val());
			return false;			
		});	
		$("#reviseSubjectText").click(function(){
			$(".revise").toggle();
			if($("#revisedSubject").css("display")=="none"){
				$("#revisedSubject").val("");	
				$("#revisedQuestionText").wysiwyg("setContent","");						
			}else{
				$("#revisedSubject").val($("#subject").val());
				$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val());				
			}			
			return false;			
		});			
		$("select[name='selectedSupportingMembers']").hide();
		//initially only minsitry will be visible as either disabled or enabled
		if($("#group").val()==""){
		}
		if($("#selectedDepartment").val()==null||$("#selectedDepartment").val()==''){
		$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
		$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#selectedSubDepartment").val()==null||$("#subDepartment").val()==''){
			$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}				
		if($("#selectedClarificationNeeded").val()==null||$("#selectedClarificationNeeded").val()==''){
			$("#clarificationNeededFrom").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#clarificationNeededFrom").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}		
		//hide internal status master.if internal status is clarification needed then only display clarification needed from else hide it.
		$("#internalStatusMaster").hide();		
		//start workflow
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
		        	$.post($('form').attr('action')+'?operation=startworkflow',  
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
		
		//view revisions
	    $("#viewRevision").click(function(){
		    $.get('question/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });

	    //view contact details
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("#selectedSupportingMembers").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('question/members/contacts?members='+members,function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });	    

	       
	    $("#selecedtInternalStatus").change(function(){
		    $("#clarificationDiv").hide();		    
		    var value=$(this).val();
		    var type=$("#internalStatusMaster option[value='"+value+"']").text();
		    var sortorder="asc";
		    if(value!='-'){
			$("#actorDiv").show();		    	    
			$("#workflowType").val(type);
			$("#internalStatus").val(value);
			$("#recommendationStatus").val(value);    
		    if(type=='question_workflow_decisionstatus_clarificationneeded'){
			    $("#clarificationDiv").show();
		    }
		    var params="sessionId="+$("#sessionId").val()+"&deviceTypeId="+$("#deviceTypeId").val()+
			"&workflowType="+$("#workflowType").val();
		    $.post('ref/wfconfig?'+params,function(data){
				if(data!=undefined){
					$("#workflowConfigId").val(data.id);
					loadActors(sortorder);								
				}else{
					$("#workflowConfigId").val(data.id);
					$("#actor").empty();
				}
			});
		    }else{
			    $("#actorDiv").hide();	
			    $("#internalStatus").val($("#selectedInternalStatus").val());	
			    $("#recommendationStatus").val($("#selectedInternalStatus").val());			    	    
		    }
		    
	    });  
	    //if there is revised text then show it
	    if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
	    if($("#revisedSubject").val()!=''){
	    	$("#revisedQuestionTextDiv").show();
	    }	    
	    //if question is in processing then appropriate value should be indicated
	    var internalStatus=$("#internalStatus").val();
	    $("#internalStatusMaster option").each(function(){
		    var valueToBeSet=$(this).val();
		    if(valueToBeSet==internalStatus){
			    $("#selectedInternalStatus").val(valueToBeSet) ;
		    }
	    });
	    if($("#selectedInternalStatus").val()=='-'){
		    $("#actorDiv").hide();
	    }
	    
	});

	function loadActors(sortorder){
		var params="sessionId="+$("#sessionId").val()+"&deviceTypeId="+$("#deviceTypeId").val()+
		"&workflowType="+$("#workflowType").val()+"&groupNumber="+$("#groupNumber").val()+
		"&workflowConfigId="+$("#workflowConfigId").val()+"&level="+$("#level").val()+"&sortorder="+sortorder;
		$.post('ref/actors?'+params,function(data){
			if(data.length>0){
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
			$("#actor").html(text);										
			}else{
			$("#actor").empty();
			}
		});
	}

	function viewQuestionDetail(id){
		$.get('question/'+id+'/edit?edit=false&clubbedQuestionDetail=yes',function(data){
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html');	
	}	
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
    </style>
</head> 

<body>
<div class="fields clearfix watermark">
<form:form action="workflow/question/secretary" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${deviceType} ${domain.number}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
	<label class="small"><spring:message code="question.number" text="Question Number"/></label>
	<form:input path="number" cssClass="sText" readonly="true"/>
	<form:errors path="number" cssClass="validationError"/>	
	
	<label class="small"><spring:message code="question.priority" text="Priority"/></label>
	<form:input path="priority" cssClass="sText" readonly="true"/>	
	<form:errors path="priority" cssClass="validationError"/>	
	</p>
		
	
	<p>
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<form:input path="submissionDate" cssClass="sText" readonly="true"/>
	<form:errors path="submissionDate" cssClass="validationError"/>	
	
	<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
	<input id="answeringDateLabel" name="answeringDateLabel" type="text" value="${answeringDate}" class="sText"/>
	<input id="answeringDate" name="answeringDate" type="hidden" value="${answeringDateSelected}"/>
	<form:errors path="answeringDate" cssClass="validationError"/>		
	</p>
	
	<p>
	<label class="small"><spring:message code="question.ministry" text="Ministry"/></label>
	<select name="ministry" id="ministry" class="sSelect">
	<c:forEach items="${ministries}" var="i">
		<c:choose>
		<c:when test="${ministrySelected == i.id }">
			<option value="${i.id }" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:when>
		<c:otherwise>
			<option value="${i.id }"><c:out value="${i.name}"></c:out></option>	
		</c:otherwise>
		</c:choose>		
	</c:forEach>
	</select>
	<form:errors path="ministry" cssClass="validationError"/>	
	
	<label class="small"><spring:message code="question.group" text="Group"/></label>
	<input type="text" class="sText" id="groupNumber" name="groupNumber" value="${group.number}" readonly="readonly">		
	<input type="hidden" id="group" name="group" value="${group.id}">
	<form:errors path="group" cssClass="validationError"/>	
	</p>	
	
	<p>
	<label class="small"><spring:message code="question.department" text="Department"/></label>
	<select name="department" id="department" class="sSelect">
	<c:forEach items="${departments}" var="i">
		<c:choose>
		<c:when test="${departmentSelected == i.id }">
			<option value="${i.id }" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:when>
		<c:otherwise>
			<option value="${i.id }"><c:out value="${i.name}"></c:out></option>	
		</c:otherwise>
		</c:choose>		
	</c:forEach>
	</select>
	<form:errors path="department" cssClass="validationError"/>	
	
	<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
	<select name="subDepartment" id="subDepartment" class="sSelect">
	<c:forEach items="${subDepartments}" var="i">
		<c:choose>
		<c:when test="${subDepartmentSelected == i.id }">
			<option value="${i.id }" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:when>
		<c:otherwise>
			<option value="${i.id }"><c:out value="${i.name}"></c:out></option>	
		</c:otherwise>
		</c:choose>		
	</c:forEach>
	</select>
	<form:errors path="subDepartment" cssClass="validationError"/>	
	</p>	
		
	
	<p>
	<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
	<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" id="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	</p>	
	
	<p style="display: none;">
	<a href="#" id="viewContacts" style="margin-left:162px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>
	</p>	
	
	<p style="display: none;">
	<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
	<a href="#" id="p${parent}" onclick="viewQuestionDetail(${parent});"><c:out value="${parentNumber}"></c:out></a>
	<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>	

	<p style="display: none;">
	<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
	<c:choose>
	<c:when test="${!(empty clubbedQuestions) }">
	<c:forEach items="${clubbedQuestions }" var="i">
	<a href="#" id="cq${i.id}" class="clubbedQuestions" onclick="viewQuestionDetail(${i.id});"><c:out value="${i.name}"></c:out></a>
	</c:forEach>
	</c:when>
	<c:otherwise>
	<c:out value="-"></c:out>
	</c:otherwise>
	</c:choose>
	<select id="clubbings" name="clubbings" multiple="multiple" style="display:none;">
	<c:forEach items="${clubbedQuestions }" var="i">
	<option value="${i.id}" selected="selected"></option>
	</c:forEach>
	</select>
	</p>
	
	<p>	
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	
	<p style="display: none;">
	<a href="#" id="reviseSubjectText" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.reviseSubjectText" text="Revise Subject and Question"></spring:message></a>
	<a href="#" id="viewRevision"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	
	<p style="display:none;" class="revise" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	<a href="#" id="copySubject" ><spring:message code="question.copysubject" text="Copy original subject"></spring:message></a>	
	</p>
	
	<p style="display:none;" class="revise" id="revisedQuestionTextDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	<a href="#" id="copyQuestionText" style="float:right;margin-top:-100px;margin-right:40px;"><spring:message code="question.viewcitation" text="Copy original question"></spring:message></a>	
	</p>
	
	<c:if test="${internalStatusType!='questions_submit'&&internalStatusType!='question_before_workflow_clubbed'}">		
	<p>
	<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
	<select id="selecedtInternalStatus">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatus}" var="i">
	<c:if test="${(i.type!='question_workflow_decisionstatus_discuss'&&i.type!='question_workflow_decisionstatus_sendback') }">
	<c:choose>
	<c:when test="${i.type=='question_workflow_decisionstatus_groupchanged' }">
	<option value="${i.id}" style="display: none;"><c:out value="${i.name}"></c:out></option>	
	</c:when>
	<c:otherwise>
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
	</c:otherwise>
	</c:choose>
	</c:if>
	</c:forEach>
	</select>
	<select id="internalStatusMaster">
	<c:forEach items="${internalStatus}" var="i">
	<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="internalStatus" cssClass="validationError"/>	
	</p>
	
	<p id="actorDiv">
	<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
	<select id="actor" name="actor" class="sSelect">	
	<c:forEach items="${actors}" var="i">
	<option value="${i.id }"><c:out value="${i.name}"></c:out></option>
	</c:forEach>	
	</select>
	</p>
		
	<p id="clarificationDiv" style="display:none;">
	<label class="small"><spring:message code="question.clarificationneededfrom" text="Clarification Needed from"/></label>
	<form:select path="clarificationNeededFrom" cssClass="sSelect" items="${clarificationsNeededFrom}" itemLabel="name" itemValue="id"/>
	<form:errors path="clarificationNeededFrom" cssClass="validationError" />	
	</p>
	</c:if>
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatusSelected }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${internalStatusSelected }">	
	<p style="display: none;">
	<a href="#" id="viewCitation" style="margin-left: 162px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
		<c:if test="${empty edit}">
		<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:if>
	</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<input id="houseType" name="houseType" value="${houseTypeId}" type="hidden">
	<input type="hidden" id="sessionYear" name="sessionYear" value="${sessionYearSelected}"/>
	<input type="hidden" id="sessionType" name="sessionType" value="${sessionTypeSelected}"/>	
	<input type="hidden" id="session" name="session" value="${session}"/>
	<input type="hidden" id="type" name="type" value="${deviceTypeSelected}"/>
	<input type="hidden" id="language" name="language" value="${languageSelected}"/>
	<input type="hidden" id="status" name="status" value="${status}"/>
	<input type="hidden" id="sessionId" name="sessionId" value="${sessionId}"/>
	<input type="hidden" id="deviceTypeId" name="deviceTypeId" value="${deviceTypeId}"/>
	<input type="hidden" id="workflowType" name="workflowType" value="${workflowType}"/>
	<input type="hidden" id="groupNumber" name="groupNumber" value="${groupNumber}"/>
	<input type="hidden" id="workflowConfigId" name="workflowConfigId" value="${workflowConfigId}"/>
	<input type="hidden" id="level" name="level" value="${level}"/>
	<input type="hidden" id="pv_endflag" name="pv_endflag" value="continue"/>	
	<form:hidden path="createdBy"/>
	<input id="creationDate" name="creationDate" value="${creationDate}" type="hidden">	
	<input type="hidden" id="pv_endflag" name="pv_endflag" value="continue"/>
	<input id="task" name="task" value="${task}" type="hidden">	
</form:form>
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="selectedDepartment" type="hidden" value="${departmentSelected }">
<input id="selectedSubDepartment" type="hidden" value="${subDepartmentSelected }">
<input id="selectedInternalStatus" type="hidden" value="${internalStatusSelected }">
<input id="selectedClarificationNeeded" type="hidden" value="${clarificationsNeededSelected }">
<input id="startWorkflowMessage" type="hidden" value="<spring:message code='question.startworkflow' text='Do you want to put up question?'/>">
</div>
</body>
</html>