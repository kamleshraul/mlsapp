<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="workflowconfig.personal" text="Workflow Settings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	//for controlling actors Index
	var workflowactorIndex=$("select option:selected").val();
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
		var workflowactorId=$('#workflowactorId'+id).val();			
		if(workflowactorId != ''){
	    $.delete_('workflowconfig/'+$("#id").val()+"/"+workflowactorId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
		    if(data=='SUCCESS'){
	    	$('#workflowactor'+id).remove();
	    	totalWorkflowActorCount=totalWorkflowActorCount-1;
			if(id==workflowactorCount){
				if(continous==null){
					workflowactorCount=workflowactorCount-1;
				}				
			}
		    }else{
			    $.prompt($("#deleteFailedMessage").val());
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
			$('#workflowactor'+id).remove();
			totalWorkflowActorCount=totalWorkflowActorCount-1;
			if(id==workflowactorCount){
				if(continous==null){
					workflowactorCount=workflowactorCount-1;
				}
			}
		}			
	}
	
	function prependOptionToDeviceType() {
		var isDeviceTypeFieldEmpty = $('#isDeviceTypeEmpty').val();
		var optionValue = $('#pleaseSelectOption').val();
		if(isDeviceTypeFieldEmpty == 'true') {
			var option = "<option value='0' selected>" + optionValue + "</option>";
			$('#deviceType').prepend(option);
		}
		else {
			var option = "<option value='0'>" + optionValue + "</option>";
			$('#deviceType').prepend(option);	
		}
	}
	
	$(document).ready(function(){
		$('#usergroupTypeMaster').hide();
		$('#addWorkflowActor').click(function(){
			addWorkflowActor();
		});			
		prependOptionToDeviceType();
	});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" >
<form:form action="workflowconfig" method="PUT" modelAttribute="domain">
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
		<label class="small"><spring:message code="workflowconfig.module" text="Module"/></label>
		<select name="module" id="module" style="width:100px;height: 25px;">
			<option value="" selected="selected"><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>	
			<c:choose>
				<c:when test="${module=='COMMITTEE'}">
					<option value="COMMITTEE" selected="selected"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
					<option value="REPORTING"><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
					<option value="EDITING"><spring:message code="mytask.editing" text="Editing"></spring:message></option>
				</c:when>
				<c:when test="${module=='REPORTING'}">
					<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
					<option value="REPORTING" selected="selected"><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
					<option value="EDITING"><spring:message code="mytask.editing" text="Editing"></spring:message></option>
				</c:when>
				<c:when test="${module=='EDITING'}">
					<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
					<option value="REPORTING" ><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
					<option value="EDITING" selected="selected"><spring:message code="mytask.editing" text="Editing"></spring:message></option>
				</c:when>
				<c:otherwise>
					<option value="COMMITTEE"><spring:message code="mytask.committee" text="Committee"></spring:message></option>			
					<option value="REPORTING"><spring:message code="mytask.reporting" text="Reporting"></spring:message></option>
					<option value="EDITING"><spring:message code="mytask.editing" text="Editing"></spring:message></option>
				</c:otherwise>
			</c:choose>		
							
		</select>
		<form:errors path="module" cssClass="validationError"/>
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
			<c:choose>
			<c:when test="${domain.isLocked==true }">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef" disabled="disabled">
			</c:when>
			<c:otherwise>
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
			</c:otherwise>
			</c:choose>
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
	<input type="hidden" id="isWorkflowLocked" value="${domain.isLocked}" />	
	<input type="hidden" id="workflowactorCount" name="workflowactorCount" value="${workflowactorCount}">
	<input type="hidden" id="createdOn" name="createdOn" value="${createdOn}">
	<input type="hidden" id="houseType" name="houseType" value="${houseType}">
	<input type="hidden" id="deleteFailedMessage" name="deleteFailedMessage" value="<spring:message code='workflowconfig.deletefailedmsg' text='Cannot Be Deleted'/>">	
	<input type="hidden" id="isDeviceTypeEmpty" name="isDeviceTypeEmpty" value="${isDeviceTypeEmpty}">	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="pleaseSelectOption" name="pleaseSelectOption" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
</form:form>
</div>
</body>
</html>