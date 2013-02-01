<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="workflowconfig.personal" text="Workflow Settings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	//for controlling actors Index
	var workflowactorIndex=0;
	var workflowactorCount=parseInt($('#workflowactorCount').val());
	var totalWorkflowActorCount=0;
	totalWorkflowActorCount=workflowactorCount+totalWorkflowActorCount;
	function addWorkflowActor(){
		workflowactorCount=workflowactorCount+1;
		totalWorkflowActorCount=totalWorkflowActorCount+1;
		var text="<div id='workflowactor"+workflowactorCount+"'>"+
				  "<p>"+
		              "<label class='small'>"+$('#workflowactorNameMessage').val()+"</label>"+
		              "<select name='workflowactorName"+workflowactorCount+"' id='workflowactorName"+workflowactorCount+"' style='width:100px;'>"+
				      $('#usergroupTypeMaster').html()+
				      "</select>"+
				   "</p>"+
				      "<input type='button' class='button' id='"+workflowactorCount+"' value='"+$('#deleteWorkflowActorMessage').val()+"' onclick='deleteWorkflowActor("+workflowactorCount+");'>"+
				      "<input type='hidden' id='workflowactorId"+workflowactorCount+"' name='workflowactorId"+workflowactorCount+"'>"+
					  "<input type='hidden' id='workflowactorLocale"+workflowactorCount+"' name='workflowactorLocale"+workflowactorCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='workflowactorVersion"+workflowactorCount+"' name='workflowactorVersion"+workflowactorCount+"'>"+
					  "<input type='hidden' id='workflowactorLevel"+workflowactorCount+"' name='workflowactorLevel"+workflowactorCount+"' value='"+workflowactorCount+"'>"+
					  "</div>"; 
				      var prevCount=workflowactorCount-1;
				      if(totalWorkflowActorCount==1){
					   $('#addWorkflowActor').after(text);
					    }else{
				      $('#workflowactor'+prevCount).after(text);
				      }
				      $('#workflowactorCount').val(workflowactorCount); 
				      $('#workflowactorLevel'+workflowactorCount).focus();	
				      return workflowactorCount;		
	}
	function deleteWorkflowActor(id,continous){			
		$('#workflowactor'+id).remove();
		totalWorkflowActorCount=totalWorkflowActorCount-1;
		if(id==workflowactorCount){
			if(continous==null){
				workflowactorCount=workflowactorCount-1;
			}
		}
	}	

	$(document).ready(function(){	
		$('#usergroupTypeMaster').hide();
		$('#addWorkflowActor').click(function(){
			addWorkflowActor();
		});
		$("#isLocked").change(function(){
			if(("#isLocked").is(":Checked")){
				$.prompt();
			}
		});			
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark" >
<form:form action="workflowconfig" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="workflowconfig.new.heading" text="Enter Workflow Settings"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="workflowconfig.devicetype" text="Device Type"/></label>
		<form:select path="deviceType" items="${deviceTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="deviceType" cssClass="validationError"/>
	</p>	
	<p>
		<label class="small"><spring:message code="workflowconfig.workflow" text="Workflow"/></label>
		<form:select path="workflow" items="${workflows}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="workflow" cssClass="validationError"/>
	</p>	
	<p>
		<label class="small"><spring:message code="workflowconfig.islocked" text="Lock This Configuration"/></label>
		<form:checkbox path="isLocked"  cssClass="sCheck" value="true"/>
		<form:errors path="isLocked" cssClass="validationError"/>
	</p>
	<div>
	<input type="button" class="button" id="addWorkflowActor" value="<spring:message code='workflowconfig.addWorkflowActor' text='Add Workflow Actors'></spring:message>">
	<input type="hidden" id="workflowactorCount" name="workflowactorCount" value="${workflowactorCount}"/>	
	<input type="hidden" id="deleteWorkflowActorMessage" name="deleteWorkflowActorMessage" value="<spring:message code='workflowconfig.deleteWorkflowActor' text='Delete Workflow Actor'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="workflowactorNameMessage" name="workflowactorNameMessage" value="<spring:message code='workflowconfig.workflowactorName' text='User Group'></spring:message>" disabled="disabled"/>
		
	<select name="usergroupTypeMaster" id="usergroupTypeMaster" disabled="disabled">
	<c:forEach items="${userGroupTypes}" var="i">
	<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
	</c:forEach>
	</select>	
	
	<form:errors path="workflowactors" cssClass="validationError"></form:errors>
	
	<c:if test="${!(empty workflowactors)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${domain.workflowactors}" var="outer">
	<div id="workflowactor${count}">
	<p>
	    <label class="small"><spring:message code="workflowconfig.workflowactorName" text="User Group"/></label>
		<select name="workflowactorName${count}" id="workflowactorName${count}" style='width:100px;'>
		<c:forEach items="${userGroupTypes}" var="i">
		<c:choose>
		<c:when test="${outer.userGroupType.id==i.id}">		
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
		</c:when>
		<c:otherwise>
		<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
		</c:otherwise>
		</c:choose>	
		</c:forEach>
		</select>
	</p>
	<input type='button' class='button' id='${count}' value='<spring:message code="workflowconfig.deleteWorkflowActor" text="Delete Workflow Actor"></spring:message>' onclick='deleteWorkflowActor(${count});'/>
	<input type='hidden' id='workflowactorId${count}' name='workflowactorId${count}' value="${outer.id}">
	<input type='hidden' id='workflowactorVersion${count}' name='workflowactorVersion${count}' value="${outer.version}">
	<input type='hidden' id='workflowactorLocale${count}' name='workflowactorLocale${count}' value="${domain.locale}">
	<input type='hidden' id='workflowactorLevel${count}' name='workflowactorLevel${count}' value="${count}">
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>	
	</div>		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="hidden" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input type="hidden" id="workflowactorCount" name="workflowactorCount" value="${workflowactorCount}">
	<input type="hidden" id="createdOn" name="createdOn" value="${createdOn}">
	<input type="hidden" id="houseType" name="houseType" value="${houseType}">
</form:form>
</div>
</body>
</html>