<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="session.workflowconfig.personal" text="Workflow Settings"/>
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
	    		  "<label class='small'>"+$('#workflowactorLevelMessage').val()+"</label>"+
	    		  "<input name='workflowactorLevel"+workflowactorCount+"' id='workflowactorLevel"+workflowactorCount+"' style='width:100px;'>"+
	    		  "<label class='small'>"+$('#workflowactorGroupMessage').val()+"</label>"+
	    		  "<input name='workflowactorGroup"+workflowactorCount+"' id='workflowactorGroup"+workflowactorCount+"' style='width:100px;'>"+
		              "<label class='small'>"+$('#workflowactorNameMessage').val()+"</label>"+
		              "<select name='workflowactorName"+workflowactorCount+"' id='workflowactorName"+workflowactorCount+"' style='width:100px;'>"+
				      $('#usergroupTypeMaster').html()+
				      "</select>"+
				   "</p>"+
				      "<input type='button' class='button' id='"+workflowactorCount+"' value='"+$('#deleteWorkflowActorMessage').val()+"' onclick='deleteWorkflowActor("+workflowactorCount+");'>"+
				      "<input type='hidden' id='workflowactorId"+workflowactorCount+"' name='workflowactorId"+workflowactorCount+"'>"+
					  "<input type='hidden' id='workflowactorLocale"+workflowactorCount+"' name='workflowactorLocale"+workflowactorCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='workflowactorVersion"+workflowactorCount+"' name='workflowactorVersion"+workflowactorCount+"'>"+
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
	});
	</script>
</head>

<body>
<div class="fields clearfix watermark" >
<form:form action="session/workflowconfig" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="session.workflowconfig.new.heading" text="Enter Workflow Settings"/>		
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="session.workflowconfig.devicetype" text="Device Type"/></label>
		<form:select path="deviceType" items="${deviceTypes}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="deviceType" cssClass="validationError"/>
	</p>	
	<p>
		<label class="small"><spring:message code="session.workflowconfig.workflow" text="Workflow"/></label>
		<form:select path="workflow" items="${workflows}" itemValue="id" itemLabel="name" cssClass="sSelect"/>
		<form:errors path="workflow" cssClass="validationError"/>
	</p>	
	<div>
	<input type="button" class="button" id="addWorkflowActor" value="<spring:message code='session.workflowconfig.addWorkflowActor' text='Add Workflow Actors'></spring:message>">
	<input type="hidden" id="workflowactorCount" name="workflowactorCount" value="${workflowactorCount}"/>	
	<input type="hidden" id="deleteWorkflowActorMessage" name="deleteWorkflowActorMessage" value="<spring:message code='session.workflowconfig.deleteWorkflowActor' text='Delete Workflow Actor'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="workflowactorLevelMessage" name="workflowactorLevelMessage" value="<spring:message code='session.workflowconfig.workflowactorLevel' text='Level'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="workflowactorNameMessage" name="workflowactorNameMessage" value="<spring:message code='session.workflowconfig.workflowactorName' text='User Group'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="workflowactorGroupMessage" name="workflowactorGroupMessage" value="<spring:message code='session.workflowconfig.workflowactorGroup' text='Group'></spring:message>" disabled="disabled"/>
		
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
	    <label class="small"><spring:message code="session.workflowconfig.workflowactorLevel" text="Level"/></label>
		<input name="workflowactorLevel${count}" id="workflowactorLevel${count}"  value="${outer.level}" style='width:100px;'>
	    <label class="small"><spring:message code="session.workflowconfig.workflowactorGroup" text="Group"/></label>
		<input name="workflowactorGroup${count}" id="workflowactorGroup${count}"  value="${outer.groupName}" style='width:100px;'>
	    <label class="small"><spring:message code="session.workflowconfig.workflowactorName" text="User Group"/></label>
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
	<input type='button' class='button' id='${count}' value='<spring:message code="session.workflowconfig.deleteWorkflowActor" text="Delete Workflow Actor"></spring:message>' onclick='deleteWorkflowActor(${count});'/>
	<input type='hidden' id='workflowactorId${count}' name='workflowactorId${count}' value="${outer.id}">
	<input type='hidden' id='workflowactorVersion${count}' name='workflowactorVersion${count}' value="${outer.version}">
	<input type='hidden' id='workflowactorLocale${count}' name='workflowactorLocale${count}' value="${domain.locale}">
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>
	</div>		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input type="hidden" id="workflowactorCount" name="workflowactorCount" value="${workflowactorCount}">
	<input id="session" name="session" value="${session}" type="hidden">
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">
</form:form>
</div>
</body>
</html>