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
			//$("#subDepartment").prev().show();
			//$("#subDepartment").show();	
			}else{
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);
				//$("#subDepartment").prev().hide();
				//$("#subDepartment").hide();	
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
			//$("#department").prev().show();
			//$("#department").show();	
			loadSubDepartments(ministry,data[0].id);
			}else{
				$("#department").empty();
				var departmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#department").html(departmentText);				
				//$("#department").prev().hide();
				//$("#department").hide();
				$("#subDepartment").empty();				
				//$("#subDepartment").prev().hide();
				//$("#subDepartment").hide();
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
				//$("#answeringDate").prev().show();
				//$("#answeringDate").show();				
			}else{
				$("#answeringDate").empty();
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#answeringDate").html(answeringDatesText);				
				//$("#answeringDate").prev().hide();
				//$("#answeringDate").hide();
			}			
			loadDepartments(ministry);
		});
	}

	function loadGroup(ministry){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#groupNumber").val(data.name);
			$("#group").val(data.id);
			//$("#groupNumber").prev().show();
			//$("#groupNumber").show();
			loadAnsweringDates(data.id,ministry);			
		});
	}

	function loadMinistries(session){
		$.get('ref/session/'+session+'/ministries',function(data){
			if(data.length>0){
				var minsitryText="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					minsitryText+="<option value='"+data[i].id+"'>"+data[i].name;				
				}
				$("#ministry").empty();
				$("#ministry").html(minsitryText);
				loadGroup(data[i].id);
			}else{
				$("#ministry").empty();
				$("#groupNumber").val("");
				$("#group").val("");
				//$("#groupNumber").hide();
				//$("#groupNumber").prev().hide();
				$("#department").empty();
				//$("#department").hide();
				//$("#department").prev().hide();
				$("#subDepartment").empty();
				//$("#subDepartment").hide();
				//$("#subDepartment").prev().hide();
				$("#answeringDate").empty();
				//$("#answeringDate").hide();			
				//$("#answeringDate").prev().hide();
			}
		});
	}
	
	function loadSession(){
		$.get('ref/session/'+$("#houseType").val()+'/'+$("#sessionYear").val()+'/'+$("#sessionType").val(),function(data){
			$("#session").val(data.id);
			loadMinistries(data.id);			
		});
	}			
	$(document).ready(function(){
		
		
		$("#questionText").change(function(){
			
		});
		$("#ministry").change(function(){
			if($(this).val()!=''){
			loadGroup($(this).val());
			}else{
				$("#groupNumber").val("");
				$("#group").val("");
				//$("#groupNumber").hide();
				//$("#groupNumber").prev().hide();
				$("#department").empty();
				//$("#department").hide();
				//$("#department").prev().hide();
				$("#subDepartment").empty();
				//$("#subDepartment").hide();
				//$("#subDepartment").prev().hide();
				$("#answeringDate").empty();
				//$("#answeringDate").hide();			
				//$("#answeringDate").prev().hide();	
			}
		});

		$("#department").change(function(){
			if($(this).val()!=''){
			loadSubDepartments($("#ministry").val(),$(this).val());
			}
		});

		$("#sessionYear").change(function(){
		loadSession();	
		});

		$("#houseType").change(function(){
			loadSession();	
		});

		$("#sessionType").change(function(){
			loadSession();	
		});

		//initially only minsitry will be visible as either disabled or enabled
		if($("#group").val()==""){		
		}
		if($("#ministrySelected").val()==""){
			$("#ministry").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#departmentSelected").val()==null||$("#departmentSelected").val()==''){		
		$("#department").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");		
		}else{
		$("#department").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#subDepartmentSelected").val()==null||$("#subDepartmentSelected").val()==''){		
		$("#subDepartment").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}else{
			$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");
		}
		if($("#answeringDateSelected").val()==null||$("#answeringDateSelected").val()==''){		
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
		//send for approval
		$("#sendforapproval").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});
			if($("#primaryMember").val()==null||$("primaryMember").val()==""){
				alert($("#primaryMemberEmpty").val());
				return false;
			}
			if($("#subject").val()==null||$("subject").val()==""){
				alert($("#subjectEmpty").val());
				return false;
			}
			if($("#questionText").val()==null||$("questionText").val()==""){
				alert($("#questionEmpty").val());
				return false;
			}	
			if($("#selectedSupportingMembers").val()==null||$("selectedSupportingMembers").val()==""){
				alert($("#supportingMemberEmpty").val());
				return false;
			}
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
			if($("#primaryMember").val()==null||$("primaryMember").val()==""){
				alert($("#primaryMemberEmpty").val());
				return false;
			}
			if($("#subject").val()==null||$("subject").val()==""){
				alert($("#subjectEmpty").val());
				return false;
			}
			if($("#questionText").val()==null||$("questionText").val()==""){
				alert($("#questionEmpty").val());
				return false;
			}	
			if($("#selectedSupportingMembers").val()==null||$("selectedSupportingMembers").val()==""){
				alert($("#supportingMemberEmpty").val());
				return false;
			}
			if($("#ministry").val()==null||$("ministry").val()==""){
				alert($("#ministry").val());
				return false;
			}
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
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="question" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="question.edit.heading" text="Question No."/> ${number}	
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<c:if test="${!(empty domain.number)}">
	<p>
		<label class="small"><spring:message code="question.number" text="Question Number"/>*</label>
		<input id="number" name="number" value="${domain.number}" class="sText" readonly="readonly">
		<form:errors path="number" cssClass="validationError"/>
	</p>
	</c:if>
	
	<c:if test="${!(empty submissionDate)}">
	<p>
	<label class="small"><spring:message code="question.submissionDate" text="Submitted On"/></label>
	<input id="setSubmissionDate" name="setSubmissionDate" type="text" class="sText" value="${submissionDate}">	
	<input id="setCreationDate" name="setCreationDate" type="hidden" value="${creationDate}">		
	</p>
	</c:if>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.houseType" text="House Type"/>*</label>
		<input id="houseTypeName" name="houseTypeName" value="${houseTypeName}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseTypeId}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>
	</p>	
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.year" text="Year"/>*</label>
		<select id="sessionYear" name="sessionYear" class="sSelect">
		<c:forEach items="${years}" var="i">
		<c:choose>
		<c:when test="${sessionYearSelected==i}">
		<option selected="selected" value="${i}"><c:out value="${i}"></c:out></option>
		</c:when>
		<c:otherwise>
		<option value="${i}"><c:out value="${i}"></c:out></option>
		</c:otherwise>
		</c:choose>
		</c:forEach>		
		</select>
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.sessionType" text="Session Type"/>*</label>		
		<select id="sessionType" name="sessionType" class="sSelect">
		<c:forEach items="${sessionTypes}" var="i">
		<c:choose>
		<c:when test="${sessionTypeSelected==i.id}">
		<option selected="selected" value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
		</c:when>
		<c:otherwise>
		<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
		</c:otherwise>
		</c:choose>
		</c:forEach>		
		</select>
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="type" cssClass="validationError"/>		
		
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.type" text="Type"/>*</label>
		<form:select path="type" items="${questionTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="type" cssClass="validationError"/>	
	</p>	
		
	<p>
		<label class="small"><spring:message code="question.primaryMember" text="Primary Member"/>*</label>
		<input id="primaryMember" class="autosuggest" type="text"  value="${primaryMemberName}" readonly="readonly" style="height: 28px;">
		<c:if test="${!(empty primaryMember)}">
		<input name="primaryMember" value="${primaryMember}" type="hidden">
		</c:if>
		<form:errors path="primaryMember" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${primaryMemberConstituency}" class="sText">
	</p>	
		
	<p>
		<label class="centerlabel"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
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
	
	<c:if test="${selectedQuestionType=='questions_shortnotice'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="question.reason" text="Reason"/>*</label>
		<form:textarea path="reason" cssClass="wysiwyg"></form:textarea>
		<form:errors path="reason" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	</c:if>
	
	<p style="display:none;">
		<label class="small"><spring:message code="question.language" text="Question Language"/>*</label>
		<form:select path="language" cssClass="sSelect" items="${languages}" itemLabel="name" itemValue="id"/>
		<form:errors path="language" cssClass="validationError"/>	
	</p>	
	
	<p>
		
	</p>	
	
	<c:choose>
	<c:when test="${! empty ministries}">
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect">
		<c:forEach items="${ministries}" var="i">
		<c:choose>
		<c:when test="${ministrySelected==i.id }">
		<option value="${i.id }" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:when>
		<c:otherwise>
		<option value="${i.id }"><c:out value="${i.name}"></c:out></option>	
		</c:otherwise>
		</c:choose>		
		</c:forEach>
		</select>
		<form:errors path="ministry" cssClass="validationError"/>
		
		<label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="groupNumber" name="groupNumber" value="${group.number}" readonly="readonly">		
		<input type="hidden" id="group" name="group" value="${group.id}">
		<form:errors path="group" cssClass="validationError"/>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<select name="department" id="department" class="sSelect">
		<c:forEach items="${departments}" var="i">
		<c:choose>
		<c:when test="${departmentSelected==i.id }">
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
		<c:when test="${subDepartmentSelected==i.id }">
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
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<select name="answeringDate" id="answeringDate" class="sSelect">
		<c:forEach items="${answeringDates}" var="i">
		<c:choose>
		<c:when test="${answeringDateSelected==i.id }">
		<option value="${i.id }" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:when>
		<c:otherwise>
		<option value="${i.id }"><c:out value="${i.name}"></c:out></option>	
		</c:otherwise>
		</c:choose>		
		</c:forEach>
		</select>
		<form:errors path="answeringDate" cssClass="validationError"/>	
		
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<form:select path="priority" cssClass="sSelect">
		<c:forEach var="i" begin="1" end="${priority}" step="1">
		<c:choose>
		<c:when test="${prioritySelected==i }">
		<option value="${i}" selected="selected"><c:out value="${i}"></c:out></option>		
		</c:when>
		<c:otherwise>
		<option value="${i}"><c:out value="${i}"></c:out></option>		
		</c:otherwise>
		</c:choose>
		</c:forEach>
		</form:select>
		<form:errors path="priority" cssClass="validationError"/>	
	</p>	
	</c:when>	
	<c:otherwise>		
	<div class="toolTip tpGreen clearfix">
		<p>
			<img src="./resources/images/template/icons/light-bulb-off.png">
			<spring:message code="rotationordernotpublished" text="Follwoing fields will be activated on {0},{1} {2}(Rotation Order Publishing Date)" arguments="${rotationOrderPublishDate}"/>
		</p>
		<p></p>
	</div>
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/></label>
		<form:select path="ministry" cssClass="sSelect" items="${ministries}" itemLabel="name" itemValue="id" disabled="true"/>
		<form:errors path="ministry" cssClass="validationError"/>	
	</p>	
	</c:otherwise>
	</c:choose>
	
	
	 <div class="fields">
		<h2></h2>
		<c:choose>
		<c:when test="${statusType=='questions_submit'}">
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
	<form:hidden path="createdBy"/>
	<input id="status" name="status" value="${status}" type="hidden">
	
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="supportingMemberEmpty" value="<spring:message code='question.supportingmembers.empty' text='Supporting Members cannot be empty'></spring:message>" type="hidden">
<input id="subjectEmpty" value="<spring:message code='question.subject.empty' text='Subject cannot be empty'></spring:message>" type="hidden">
<input id="questionEmpty" value="<spring:message code='question.text.empty' text='Question cannot be empty'></spring:message>" type="hidden">
<input id="primaryMemberEmpty" value="<spring:message code='question.primaryMember.empty' text='Primary Member cannot be empty'></spring:message>" type="hidden">
<input id="ministryEmpty" value="<spring:message code='question.ministry.empty' text='Ministry cannot be empty'></spring:message>" type="hidden">
<input id="confirmQuestionSubmission" value="<spring:message code='confirm.questionsubmission.message' text='Do you want to submit the question.'></spring:message>" type="hidden">
<input type="hidden" id="ministrySelected" name="ministrySelected" value="${ministrySelected}">
<input type="hidden" id="departmentSelected" name="departmentSelected" value="${departmentSelected}">
<input type="hidden" id="subDepartmentSelected" name="subDepartmentSelected" value="${subDepartmentSelected}">
<input type="hidden" id="answeringDateSelected" name="answeringDateSelected" value="${answeringDateSelected}">
<input type="hidden" id="requestSendTo" value="${supportingMembersName}">

</div>
</body>
</html>