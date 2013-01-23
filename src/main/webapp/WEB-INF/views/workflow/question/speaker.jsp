<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	/**** detail of clubbed and refernced questions ****/		
	function viewQuestionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+$("#selectedQuestionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&edit=false";
		var resourceURL='question/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html');	
	}	
	/**** Clubbing ****/
	function clubbingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
		$.get('clubentity/init?id='+id,function(data){
			$.unblockUI();	
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html');
	}
	/**** Referencing ****/
	function referencingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.get('refentity/init?id='+id,function(data){
			$.unblockUI();			
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html');
	}
	/**** refresh clubbing and referencing ****/
	function refreshEdit(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+$("#selectedQuestionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val();
		var resourceURL='question/'+id+'/edit?'+parameters;
		$('a').removeClass('selected');
		//id refers to the tab name and it is used just to highlight the selected tab
		$('#'+ id).addClass('selected');
		//tabcontent is the content area where result of the url load will be displayed
		$('.tabContent').load(resourceURL);
		scrollTop();
		$.unblockUI();			
	}
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
		var params="question="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
		var resourceURL='ref/question/actors?'+params;
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="";
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#actor").html(text);
				$("#actorDiv").hide();				
				$("#internalStatus").val(value);
			    $("#recommendationStatus").val(value);						
			}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val(value);
		    $("#recommendationStatus").val(value);
			}
		});
		}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	/**** group changed ****/
	function groupChanged(){
		/*var newgroup=$("#group").val();
	    var oldgroup=$("#oldgroup").val();
		    if(oldgroup!=newgroup){
			    var newStatus=$("#internalStatusMaster option[value='question_workflow_decisionstatus_groupchanged']").text();
			    $("#changeInternalStatus").val(newStatus);
			    $("#changeInternalStatus option[value==']"+newStatus+"'").show();
			    $("#internalStatus").val(newStatus);
			    $("#recommendationStatus").val(newStatus);
		    }else{
			    
		    }*/	    
	}
	/**** sub departments ****/
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
			groupChanged();
		});
	}
	/**** departments ****/
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
				groupChanged();				
			}
		});
	}
    /**** groups ****/
	function loadGroup(ministry){
		if(ministry!=''){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#formattedGroup").val(data.name);
			$("#group").val(data.id);
			loadDepartments(ministry);			
		});
		}
	}		
	$(document).ready(function(){
		/**** Ministry Changes ****/
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
				groupChanged();					
			}
		});
		/**** Department Changes ****/
		$("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
		});
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('question/citations/'+$("#type").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});								
		/**** Revise subject and text****/
		$("#reviseSubjectText").click(function(){
			$(".revise").toggle();
			if($("#revisedSubjectDiv").css("display")=="none"){
				$("#revisedSubject").val("");	
				$("#revisedQuestionText").val("setContent","");
			}else{
				$("#revisedSubject").val($("#subject").val());
				$("#revisedQuestionText").wysiwyg("setContent",$("#questionText").val());				
			}			
			return false;			
		});	
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('question/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Contact Details ****/
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
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
			    loadActors(value);
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		    }		    
	    });
	    /**** Put Up ****/
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
		$("#dateapproval").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});	
			$("#flag").val("question_after_approval_dateapproved");
			$("#endflag").val("end");
			$.prompt($('#startWorkflowMessage').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post($('form').attr('action'),  
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
		$("#daterejection").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});	
			$("#flag").val("question_after_approval_put_for_dateapproval");
			$.prompt($('#startWorkflowMessage').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post($('form').attr('action'),  
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
	    /**** Right Click Menu ****/
		$(".clubbedRefQuestions").contextMenu({
	        menu: 'contextMenuItems'
	    },
	        function(action, el, pos) {
			var id=$(el).attr("id");
			if(action=='unclubbing'){
				if(id.indexOf("cq")!=-1){
				var questionId=$("#id").val();
				var clubId=id.split("cq")[1];				
				$.post('clubentity/unclubbing?pId='+questionId+"&cId="+clubId,function(data){
					if(data=='SUCCESS'){
					$.prompt("Unclubbing Successful");				
					}else{
						$.prompt("Unclubbing Failed");
					}		
				},'html');	
				}else{
					$.prompt("Unclubbing not allowed");
				}			
			}else if(action=='dereferencing'){
				if(id.indexOf("rq")!=-1){					
				var questionId=$("#id").val();
				var refId=id.split("rq")[1];				
				$.post('refentity/dereferencing?pId='+questionId+"&rId="+refId,function(data){
					if(data=='SUCCESS'){
						$.prompt("Dereferencing Successful");				
						}else{
							$.prompt("Dereferencing Failed");
						}							
				},'html');	
				}else{
					$.prompt("Referencing not allowed");					
				}			
			}
	    });			    
	    /**** On Page Load ****/
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
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
	    if($("#revisedQuestionText").val()!=''){
	    	$("#revisedQuestionTextDiv").show();
	    }    
	    
	  //--------------vikas dhananjay 20012013--------------------------
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
<form:form action="workflow/question/mytask" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedQuestionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
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
	<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
	<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
	<input id="number" name="number" value="${domain.number}" type="hidden">
	<form:errors path="number" cssClass="validationError"/>
	</p>
	
	<c:choose>
	<c:when test="${domain.type.type=='questions_starred'}">
	<p>
	<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
	<input id="formattedAnsweringDate" name="formattedAnsweringDate" value="${formattedAnsweringDate }" class="sText" readonly="readonly">
	<input id="answeringDate" name="answeringDate" type="hidden"  value="${answeringDate}">
	</p>
	</c:when>
	<c:when test="${domain.type.type=='questions_unstarred'}">
	
	</c:when>
	<c:when test="${domain.type.type=='questions_shortnotice'}">
	
	</c:when>
	<c:when test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
	<p>
	<label class="small"><spring:message code="question.discussionDate" text="Discussion Date"/></label>
	<input id="discussionDate" name="discussionDate" value="${discussionDateSelected }" class="sText" readonly="readonly">
	<form:errors path="discussionDate" cssClass="validationError"/>
	</p>
	<p>
	<label class="small"><spring:message code="question.halfhour.questionref" text="Reference Question Number: "/>*</label>
	<input class="sText" readonly="readonly" type="text" name="halfHourDiscussionReference_questionNumber" value="${referredQuestionNumber}" id="halfHourDiscussionReference_questionNumber" />
	<form:errors path="halfHourDiscusionFromQuestionReference" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	<label class="small"><a id="halfhourdiscussion_referred_question" href="#" ><spring:message code="question.halfhour.questionrefview" text="See Referred Question"/></a></label>
	</p>	
	</c:when>	
	</c:choose>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
		<input name="priority" id="priority"  type="hidden" value="${priority }">	
		<form:errors path="priority" cssClass="validationError"/>
	</p>
	
		
	<p style="display:none;">		
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	</p>	
	
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
	<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
	<textarea id="members" class="sTextarea" readonly="readonly" rows="2" cols="50">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" id="selectedSupportingMembers" multiple="multiple" style="display:none;">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	</p>		
		
		<p style="display:none;">
		<label class="small"><spring:message code="question.parentquestion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewQuestionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>	
		<p style="display:none;">
		<label class="small"><spring:message code="question.clubbedquestions" text="Clubbed Questions"></spring:message></label>
		<c:choose>
		<c:when test="${!(empty clubbedQuestions) }">
		<c:forEach items="${clubbedQuestions }" var="i">
		<a href="#" id="cq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
		</c:forEach>
		</c:when>
		<c:otherwise>
		<c:out value="-"></c:out>
		</c:otherwise>
		</c:choose>
		<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
		<c:forEach items="${clubbedQuestions }" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>
		</select>
		</p>
		<p style="display:none;">
		<label class="small"><spring:message code="question.referencedquestions" text="Referenced Questions"></spring:message></label>
		<c:choose>
		<c:when test="${!(empty referencedQuestions) }">
		<c:forEach items="${referencedQuestions }" var="i">
		<a href="#" id="rq${i.number}" class="clubbedRefQuestions" onclick="viewQuestionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
		</c:forEach>
		</c:when>
		<c:otherwise>
		<c:out value="-"></c:out>
		</c:otherwise>
		</c:choose>
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
		<c:forEach items="${referencedQuestions }" var="i">
		<option value="${i.id}" selected="selected"></option>
		</c:forEach>
		</select>
	</p>
	<p>
	<a href="#" id="viewRevision" style="margin-left: 162px;margin-right: 20px;"><spring:message code="question.viewrevisions" text="View Revisions"></spring:message></a>
	</p>
	<c:if test="${!(empty domain.revisedSubject) }">
	<p  class="revise" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	<p style="display:none;">	
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	</c:if>
	
	<c:if test="${empty domain.revisedSubject }">
	<p  class="revise" id="revisedSubjectDiv" style="display:none;">
	<label class="centerlabel"><spring:message code="question.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	<p>	
	<label class="centerlabel"><spring:message code="question.subject" text="Subject"/></label>
	<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
	<form:errors path="subject" cssClass="validationError"/>	
	</p>
	</c:if>
	
	<c:if test="${!(empty  domain.revisedQuestionText)}">
	<p style="display:none;">
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	<p  class="revise" id="revisedQuestionTextDiv">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>
	<c:if test="${empty  domain.revisedQuestionText}">
	<p>
	<label class="wysiwyglabel"><spring:message code="question.details" text="Details"/></label>
	<form:textarea path="questionText" readonly="true" cssClass="wysiwyg"></form:textarea>
	<form:errors path="questionText" cssClass="validationError"/>	
	</p>
	<p  class="revise" id="revisedQuestionTextDiv" style="display:none;">
	<label class="wysiwyglabel"><spring:message code="question.revisedDetails" text="Revised Details"/></label>
	<form:textarea path="revisedQuestionText" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedQuestionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>
	
	
	
	<c:if test="${domain.type.type=='questions_shortnotice' or domain.type.type=='questions_halfhourdiscussion_from_question'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
		<form:textarea path="reason" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>
	
	<c:if test="${domain.type.type=='questions_halfhourdiscussion_from_question'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="question.briefExplanation" text="Brief Explanation"/>*</label>
			<form:textarea path="briefExplanation" cssClass="wysiwyg"></form:textarea>
			<form:errors path="briefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
		</p>
	</c:if>	
		
	<c:if test="${domain.type.type!='questions_shortnotice'||newRecommendationStatus!='question_after_approval_put_for_dateapproval'}">	
	<p>
	<label class="small"><spring:message code="question.putupfor" text="Put up for"/></label>
	<select id="changeInternalStatus" class="sSelect">
	<option value="-"><spring:message code='please.select' text='Please Select'/></option>
	<c:forEach items="${internalStatuses}" var="i">
	<c:if test="${(i.type!='question_workflow_decisionstatus_discuss'&&i.type!='question_workflow_decisionstatus_sendback') }">
	<c:choose>
	<c:when test="${i.type=='question_workflow_decisionstatus_groupchanged' }">
	<option value="${i.id}" style="display: none;"><c:out value="${i.name}"></c:out></option>	
	</c:when>
	<c:otherwise>
	<c:choose>
	<c:when test="${i.id==internalStatusSelected }">
	<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
	</c:when>
	<c:otherwise>
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
	</c:otherwise>
	</c:choose>
	</c:otherwise>
	</c:choose>
	</c:if>
	</c:forEach>
	</select>
	
	<select id="internalStatusMaster" style="display:none;">
	<c:forEach items="${internalStatuses}" var="i">
	<option value="${i.id}"><c:out value="${i.type}"></c:out></option>
	</c:forEach>
	</select>	
	<form:errors path="internalStatus" cssClass="validationError"/>	
	</p>
	
	<p id="actorDiv" style="display:none;">
	<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
	<select id="actor" name="actor" class="sSelect">	
	</select>
	</p>
		
	<p id="clarificationDiv" style="display:none;">
	<label class="small"><spring:message code="question.clarificationneededfrom" text="Clarification Needed from"/></label>
	<select id="clarificationNeededFrom" name="clarificationNeededFrom" class="sSelect">	
	</select>
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	</p>	
	</c:if>
	<c:if test="${domain.type.type=='questions_shortnotice'&&newRecommendationStatus=='question_after_approval_put_for_dateapproval'}">
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="question.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">	
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	</p>
	<p id="actorDiv" >
	<label class="small"><spring:message code="question.nextactor" text="Next Users"/></label>
	<select id="actor" name="actor" class="sSelect">
	<c:forEach items="${dateapprovalactors}" var="i">
	<option value="${i.id}">${i.name }</option>
	</c:forEach>	
	</select>
	</p>
	</c:if>
	<c:if test="${domain.type.type=='questions_shortnotice'&&newRecommendationStatus=='question_after_approval_put_for_dateapproval'}">
	<p>
	<label class="small"><spring:message code="question.toBeAnsweredByMinister" text="To Be Answered By Minister?"/></label>
	<form:checkbox path="toBeAnsweredByMinister" cssClass="sCheck"></form:checkbox>
	</p>
	
	<p>
	<label class="small"><spring:message code="question.dateOfAnsweringByMinister" text="Date of Answering By Minister"/></label>
	<input name="dateOfAnsweringByMinister" id="dateOfAnsweringByMinister" value="${formattedDate }">
	</p>
	
	<c:if test="${!(empty domain.answer) }">
	<p>
	<label class="wysiwyglabel"><spring:message code="question.answer" text="Answer"/></label>
	<form:textarea path="answer" cssClass="wysiwyg"></form:textarea>
	</p>	
	</c:if>	
	</c:if>
		
	<p>
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="question.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="question.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
		<c:choose>
		<c:when test="${newRecommendationStatus=='question_after_approval_put_for_dateapproval'}">
		<input id="dateapproval" type="button" value="<spring:message code='generic.dateapproval' text='Approve'/>" class="butDef">
		<input id="daterejection" type="button" value="<spring:message code='generic.daterejection' text='Reject'/>" class="butDef">
		</c:when>
		<c:otherwise>
		<input id="startworkflow" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</c:otherwise>
		</c:choose>	
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="endflag" name="endflag" value="continue" type="hidden">
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">
	<input id="level" name="level" value="${level }" type="hidden">	
	<input id="usergroup" name="usergroup" type="hidden" value="${usergroup}">
	<input type="hidden" id="flag" value="flag">		
	<%--21012013 --%>
	<input type="hidden" name="halfHourDiscusionFromQuestionReference" id="halfHourDiscusionFromQuestionReference" value="${refQuestionId}" />
	<input type="hidden" name="discussionDate" id="discussionDate" value="${discussionDateSelected}" />		
		
</form:form>
<input id="oldgroup" name="oldgroup" value="${group}" type="hidden">
<input id="formattedoldgroup" name="formattedoldgroup" value="${formattedGroup}" type="hidden">

<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='question.startworkflowmessage' text='Do You Want To Put Up Question?'></spring:message>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="departmentSelected" value="${ departmentSelected}" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="answeringDateSelected" value="${ answeringDateSelected}" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">


</div>
<ul id="contextMenuItems" >
<li><a href="#unclubbing" class="edit"><spring:message code="generic.unclubbing" text="Unclubbing"></spring:message></a></li>
<li><a href="#dereferencing" class="edit"><spring:message code="generic.dereferencing" text="Dereferencing"></spring:message></a></li>
</ul>
</body>
</html>