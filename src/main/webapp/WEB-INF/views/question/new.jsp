<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question" text="Question Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function loadMinistriesDepartmentsSubDeptAnsweringDates(group){
		$.get('ref/ministrydeptsubdeptdates/'+group,function(data){
			$("#ministry").empty();
			var ministries=data.ministries;
			var ministryText="";
			if(ministries.length>0){
			for(var i=0;i<ministries.length;i++){
				ministryText+="<option value='"+ministries[i].id+"'>"+ministries[i].name;
			}
			$("#ministry").html(ministryText);
			}

			$("#department").empty();
			var departments=data.departments;
			var departmentText="";
			if(departments.length>0){
			for(var i=0;i<departments.length;i++){
				departmentText+="<option value='"+departments[i].id+"'>"+departments[i].name;
			}
			$("#department").html(departmentText);
			$("#department").prev().show();
			$("#department").show();
			}else{
				$("#department").prev().hide();
				$("#department").hide();	
			}

			$("#subDepartment").empty();
			var subDepartments=data.subDepartments;
			var subDepartmentText="";
			if(subDepartments!=null){
			if(subDepartments.length>0){
			for(var i=0;i<subDepartments.length;i++){
				subDepartmentText+="<option value='"+subDepartments[i].id+"'>"+subDepartments[i].name;
			}
			$("#subDepartment").html(subDepartmentText);
			$("#subDepartment").prev().show();
			$("#subDepartment").show();	
			}else{
				$("#subDepartment").prev().hide();
				$("#subDepartment").hide();					
			}
			}else{
				$("#subDepartment").prev().hide();				
				$("#subDepartment").hide();	
			}
			
			$("#answeringDate").empty();
			var answeringDates=data.answeringDates;
			var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(answeringDates.length>0){
			for(var i=0;i<answeringDates.length;i++){
				answeringDatesText+="<option value='"+answeringDates[i].id+"'>"+answeringDates[i].name;
			}
			$("#answeringDate").html(answeringDatesText);
			}			
		});
	}

	function loadSubDepartments(ministry,department){
		$.get('ref/subdepartments/'+ministry+'/'+$("#department").val(),function(data){
			$("#subDepartment").empty();
			var subDepartmentText="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);
			$("#subDepartment").prev().show();
			$("#subDepartment").show();	
			}else{
				$("#subDepartment").prev().hide();
				$("#subDepartment").hide();	
			}
		});
	}

	function loadDepartments(ministry){
		$.get('ref/departments/'+ministry,function(data){
			$("#department").empty();
			var departmentText="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				departmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#department").html(departmentText);
			$("#department").prev().show();
			$("#department").show();	
			loadSubDepartments(ministry,data[0].id);
			}else{
				$("#subDepartment").empty();
				$("#department").prev().hide();
				$("#department").hide();
				$("#subDepartment").prev().hide();
				$("#subDepartment").hide();
			}
		});
	}

	function loadAnsweringDates(group,ministry){
		$.get('ref/group/'+group+'/answeringdates',function(data){
			console.log(data.length);
			console.log();
			if(data.length>0){
				$("#answeringDate").empty();				
				var answeringDatesText="";
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					answeringDatesText+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#answeringDate").html(answeringDatesText);				
			}else{
				var answeringDatesText="";
				var answeringDatesText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
				$("#answeringDate").empty();
				$("#answeringDate").html(answeringDatesText);
			}			
			loadDepartments(ministry);
		});
	}

	function loadGroup(ministry){
		$.get('ref/ministry/'+ministry+'/group?houseType='+$("#houseType").val()+'&sessionYear='+$("#sessionYear").val()+'&sessionType='+$("#sessionType").val(),function(data){
			$("#groupNumber").val(data.name);
			$("#group").val(data.id);
			//loadAnsweringDates(data.id,ministry);
			loadDepartments(ministry);
			
		});
	}
	function loadSession(){
		$.get('ref/session/'+$("#houseType").val()+'/'+$("#sessionYear").val()+'/'+$("#sessionType").val(),function(data){
			$("#session").val(data.id);
		});
	}

	function split( val ) {
		return val.split( /,\s*/ );
	}
	
	function extractLast( term ) {
		return split( term ).pop();
	}	
	var controlName=$(".autosuggestmultiple").attr("id");
	var primaryMemberControlName=$(".autosuggest").attr("id");	
	$(document).ready(function(){
		$("#ministry").change(function(){
			loadGroup($(this).val());
		});

		$("#department").change(function(){
			loadSubDepartments($("#ministry").val(),$(this).val());
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

		//removing houseType options on the basis of houseTypeFromRole value.
		var houseType=$("#houseTypeFromRole").val();
		$("#houseTypesMaster").hide();
		//logic is we want to remove upperhouse option when houseType is lowerhouse and vice versa.
		if(houseType=="lowerhouse"){
			$("#houseTypesMaster").val("upperhouse");
			var id=$("#houseTypesMaster option:selected").text();
			$("#houseType option[value='"+id+"']").remove();
		}else if(houseType=="upperhouse"){
			$("#houseTypesMaster").val("lowerhouse");
			var id=$("#houseTypesMaster option:selected").text();
			$("#houseType option[value='"+id+"']").remove();
		}	
		//autosuggest		
		$( ".autosuggest" ).autocomplete({
			minLength:3,			
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
			var value=$(this).val();
			console.log(value);
			$("select[name='"+controlName+"'] option:selected").each(function(){
				if(value.indexOf($(this).attr("class"))==-1){
					$(this).remove();
				}		
			});	
			$("select[name='"+controlName+"']").hide();				
		});
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:1,
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
				var terms = $(this).val().split(",");
				terms.pop();
				terms.push( ui.item.value );
				terms.push( "" );
				this.value = terms.join( "," );
				//adding multiple values
				var text="";
				if(terms.length==2){
					text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
					textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
					text=text+textoption+"</select>";	
					if($("select[name='"+controlName+"']").length==0){
						$(this).after(text);						
					}else{	
						$("select[name='"+controlName+"']").append(textoption);
					}											
				}else if(terms.length>2){
					if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
					}else{
					text=text+"<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
					$("select[name='"+controlName+"']").append(text);
					}
				}
				$("select[name='"+controlName+"']").hide();
				return false;
			}
		});	
		//adding please select option in answering dates
		$("#answeringDate").prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");
		//hiding subDepartments
		if($("#subDepartment").attr("disabled")!="disabled"){		
		if($("#subDepartment").val()==null){
		$("#subDepartment").prev().hide();
		$("#subDepartment").hide();	
		}
		}
		if($("#department").attr("disabled")!="disabled"){
		if($("#department").val()==null){
			$("#subDepartment").prev().hide();
			$("#subDepartment").hide();	
			$("#department").prev().hide();
			$("#department").hide();
		}
		}
		//send for approval
		$("#sendforapproval").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});	
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
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="question" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="question.new.heading" text="Enter Question Details"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>	
		
	<p>
		<label class="small"><spring:message code="question.houseType" text="House Type"/>*</label>
		<form:select path="houseType" items="${houseTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="houseType" cssClass="validationError"/>
		<select id="houseTypesMaster" name="houseTypesMaster">
		<c:forEach items="${houseTypes}" var="i">
		<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
		</c:forEach>
		</select>
	</p>	
	
	<p>
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
	
	<p>
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
	</p>
	
	<p>
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
		<label class="small"><spring:message code="question.supportingMembers" text="Supporting Members"/></label>
		<textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="50">${supportingMembersName}</textarea>
		<c:if test="${!(empty supporingMembers)}">
		<select  name="selectedSupportingMembers" multiple="multiple">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.member.getFullnameLastNameFirst()}"></option>
		</c:forEach>		
		</select>
		</c:if>
		<form:errors path="supportingMembers" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.subject" text="Subject"/>*</label>
		<form:textarea path="subject" cssClass="wysiwyg"></form:textarea>
		<form:errors path="subject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>

	<p>
		<label class="small"><spring:message code="question.language" text="Question Language"/>*</label>
		<form:select path="language" cssClass="sSelect" items="${languages}" itemLabel="name" itemValue="id"/>
		<form:errors path="language" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="small"><spring:message code="question.details" text="Details"/>*</label>
		<form:textarea path="questionText" cssClass="wysiwyg"></form:textarea>
		<form:errors path="questionText" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.priority" text="Priority"/>*</label>
		<form:select path="priority" cssClass="sSelect">
		<c:forEach var="i" begin="1" end="${priority}" step="1">
		<option value="${i}"><c:out value="${i}"></c:out></option>
		</c:forEach>
		</form:select>
		<form:errors path="priority" cssClass="validationError"/>	
	</p>	
	
	<c:choose>
	<c:when test="${! empty ministries}">
	<p>
		<label class="small"><spring:message code="question.ministry" text="Ministry"/>*</label>
		<form:select path="ministry" cssClass="sSelect" items="${ministries}" itemLabel="name" itemValue="id"/>
		<form:errors path="ministry" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.group" text="Group"/>*</label>
		<input type="text" class="sText" id="groupNumber" name="groupNumber" value="${group.number}">		
		<input type="hidden" id="group" name="group" value="${group.id}">
		<form:errors path="group" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/>*</label>
		<form:select path="department" cssClass="sSelect" items="${departments}" itemLabel="name" itemValue="id"/>
		<form:errors path="department" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/>*</label>
		<form:select path="subDepartment" cssClass="sSelect" items="${subDepartments}" itemLabel="name" itemValue="id"/>
		<form:errors path="subDepartment" cssClass="validationError"/>	
	</p>
		
	<p>
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<form:select path="answeringDate" cssClass="datemask sSelect" items="${answeringDates}" itemLabel="answeringDate" itemValue="answeringDate"/>
		<form:errors path="answeringDate" cssClass="validationError"/>	
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
	
	<p>
		<label class="small"><spring:message code="question.group" text="Group"/></label>
		<input type="text" class="sText" id="groupNumber" name="groupNumber" value="${group.number}" disabled="disabled">		
		<input type="hidden" id="group" name="group" value="${group.id}" disabled="disabled">
		<form:errors path="group" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.department" text="Department"/></label>
		<form:select path="department" cssClass="sSelect" items="${departments}" itemLabel="name" itemValue="id" disabled="true"/>
		<form:errors path="department" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="question.subdepartment" text="Sub Department"/></label>
		<form:select path="subDepartment" cssClass="sSelect" items="${subDepartments}" itemLabel="name" itemValue="id" disabled="true"/>
		<form:errors path="subDepartment" cssClass="validationError"/>	
	</p>
		
	<p>
		<label class="small"><spring:message code="question.answeringDate" text="Answering Date"/></label>
		<form:select path="answeringDate" cssClass="datemask sSelect" items="${answeringDates}" itemLabel="answeringDate" itemValue="answeringDate" disabled="true"/>
		<form:errors path="answeringDate" cssClass="validationError"/>	
	</p>	
	</c:otherwise>
	</c:choose>
	
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="sendforapproval" type="button" value="<spring:message code='generic.sendforapproval' text='Send For Approval'/>" class="butDef">
			<input id="submitquestion" type="button" value="<spring:message code='generic.submitquestion' text='Submit Question'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="createdBy"/>
	<form:hidden path="creationDate"/>
	
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
</div>
</body>
</html>