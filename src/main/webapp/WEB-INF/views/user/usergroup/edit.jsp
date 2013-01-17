<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="usergroup.title" text="User Group"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	function loadSubDepartments(ministry,department){
		$.post('ref/subdepartments/'+ministry+'/'+department+'/byname',function(data){
			$("#param_SUBDEPARTMENT_"+locale).empty();
			var text="";
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
				}
				$("#param_SUBDEPARTMENT_"+locale).html(text);
				$.unblockUI();				
			}else{
				$.unblockUI();				
			}
		});	
	}

	function loadDepartments(ministry){
		$.post('ref/departments/'+ministry+'/byname',function(data){
			$("#param_DEPARTMENT_"+locale).empty();
			var text="";
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
				}
				$("#param_DEPARTMENT_"+locale).html(text);
				loadSubDepartments(ministry,data[0].name);
			}else{
				$.unblockUI();				
			}
		});	
	}

	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		if($("#activeFrom").val()==""){	
		$("#activeFrom").val($("#currentDate").val());
		}
		$("select[multiple='multiple']").css("width","188px");
		
		var locale=$("#locale").val();

		$("#param_MINISTRY_"+locale).change(function(event){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
			loadDepartments($(this).val());
			//event.stopImmediatePropagation();
			});		

		$("#param_DEPARTMENT_"+locale).change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
			loadSubDepartments($("#param_MINISTRY_"+locale).val(),$(this).val());		
			});	
			

	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="usergroup" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]</h2>
	<form:errors path="version" cssClass="validationError"/>		 
	<p> 
		<label class="small"><spring:message code="usergroup.name" text="Name"/></label>
		<form:select cssClass="sSelect" path="userGroupType" items="${userGroupTypes}" itemLabel="name" itemValue="id"/>
		<form:errors path="userGroupType" cssClass="validationError"/>	
	</p>	
	<p> 
		<label class="small"><spring:message code="usergroup.activefrom" text="Active From"/></label>
		<form:input cssClass="datemask sText" path="activeFrom"/>
		<form:errors path="activeFrom" cssClass="validationError"/>	
	</p>	
	<p> 
		<label class="small"><spring:message code="usergroup.activeto" text="Active Upto"/></label>
		<form:input cssClass="datemask sText" path="activeTo"/>
		<form:errors path="activeTo" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.housetype" text="House Type" /></label>			
		<select  id="param_HOUSETYPE_${locale}" name="param_HOUSETYPE_${locale}" class="sSelect">
			<c:forEach items="${housetypes}" var="i">
			<c:choose>
			<c:when test="${selectedHouseType==i.name}">
			<option value="${i.name}" selected="selected">${i.name}</option>				
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>				
			</c:otherwise>
			</c:choose>				
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.devicetype" text="Device Type" /></label>			
		<select  id="param_DEVICETYPE_${locale}" name="param_DEVICETYPE_${locale}" multiple="multiple" size="5">
			<c:forEach items="${deviceTypes}" var="i">				
			<c:choose>
			<c:when test="${fn:contains(selectedDeviceType,i.name)}">
			<option value="${i.name}" selected="selected">${i.name}</option>			
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>	
			</c:otherwise>
			</c:choose>
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.group" text="Groups Type" /></label>			
		<select  id="param_MINISTRY_${locale}" name="param_MINISTRY_${locale}" multiple="multiple" size="5">
			<c:forEach items="${groups}" var="i">	
			<c:choose>
			<c:when test="${fn:contains(selectedGroup,i.number) }">
			<option value="${i.number}" selected="selected">${i.number}</option>			
			</c:when>
			<c:otherwise>
			<option value="${i.number}">${i.number}</option>	
			</c:otherwise>
			</c:choose>			
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.department" text="Departments" /></label>			
		<select  id="param_DEPARTMENT_${locale}" name="param_DEPARTMENT_${locale}" multiple="multiple" size="5">
			<c:forEach items="${departments}" var="i">				
			<c:choose>
			<c:when test="${fn:contains(selectedDepartment,i.name) }">
			<option value="${i.name}" selected="selected">${i.name}</option>			
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>			
			</c:otherwise>
			</c:choose>				
			</c:forEach>
		</select>
	</p>	
	<p>
		<label class="small"><spring:message code="usergroup.subdepartment" text="Sub-Departments" /></label>			
		<select  id="param_SUBDEPARTMENT_${locale}" name="param_SUBDEPARTMENT_${locale}" multiple="multiple" size="5">
			<c:forEach items="${subdepartments}" var="i">				
			<c:choose>
			<c:when test="${fn:contains(selectedSubDepartment,i.name) }">
			<option value="${i.name}" selected="selected">${i.name}</option>
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>			
			</c:otherwise>
			</c:choose>							
			</c:forEach>
		</select>
	</p>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<form:hidden path="version" />
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<input type="hidden" id="credential" name="credential" value="${domain.credential.id}">	
	<input type="hidden" id="currentDate" value="${currentdate}">
</form:form>
</div>	
</body>
</html>