<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="usergroup.title" text="User Group"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	function loadGroups(housetype,year,sessiontype){
		var locale=$("#locale").val();		
		$.get('ref/groups?housetype='+housetype+'&year='+year+'&sessiontype='+sessiontype,function(data){
			$("#param_GROUP_"+locale).empty();
			var text="";
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					if(i==0){
					text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
					}else{
						text+="<option value='"+data[i].name+"'>"+data[i].name+"</option>";						
					}
				}
				$("#param_GROUP_"+locale).html(text);
				loadDepartments(data[0].name,housetype,year,sessiontype);				
			}else{
				$("#param_DEPARTMENT_"+locale).empty();
				$("#param_SUBDEPARTMENT_"+locale).empty();	
				$.unblockUI();		
			}
		});		
	}

	function loadDepartments(group,housetype,year,sessiontype){
		var locale=$("#locale").val();		
		$.get('ref/departments?group='+group+'&housetype='+housetype+'&year='+year+'&sessiontype='+sessiontype,function(data){
			$("#param_DEPARTMENT_"+locale).empty();
			var text="";
			console.log("Departments:"+data.length);
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
				}
				$("#param_DEPARTMENT_"+locale).html(text);
				loadSubDepartments(group,housetype,year,sessiontype);
			}else{
			$("#param_SUBDEPARTMENT_"+locale).empty();				
			$.unblockUI();
			}			
		});	
	}

	function loadSubDepartments(group,housetype,year,sessiontype){
		var locale=$("#locale").val();		
		$.get('ref/subdepartments?group='+group+'&housetype='+housetype+'&year='+year+'&sessiontype='+sessiontype,function(data){
			$("#param_SUBDEPARTMENT_"+locale).empty();
			var text="";
			console.log("Sub Departments:"+data.length);			
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

	function loadSubDepartmentsByDepartments(group,department,housetype,year,sessiontype){
		var locale=$("#locale").val();	
		$.get('ref/departments/subdepartments?group='+group+'&department='+department+'&housetype='+housetype+'&year='+year+'&sessiontype='+sessiontype,function(data){
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
	
	$('document').ready(function(){	
		initControls();
		//$('#key').val('');
		if($("#activeFrom").val()==""){	
		$("#activeFrom").val($("#currentDate").val());
		}
		$("select[multiple='multiple']").css("width","188px");
		
		var locale=$("#locale").val();
		
		$("#param_YEAR_"+locale).change(function(event){
		if($("#param_HOUSETYPE_"+locale).val()!=null){				
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			
		loadGroups($("#param_HOUSETYPE_"+locale).val(),$("#param_YEAR_"+locale).val(),$("#param_SESSIONTYPE_"+locale).val());
		event.stopImmediatePropagation();		
		}else{
			$("#param_GROUP_"+locale).empty();
			$("#param_DEPARTMENT_"+locale).empty();
			$("#param_SUBDEPARTMENT_"+locale).empty();
		}
		});

		$("#param_SESSIONTYPE_"+locale).change(function(event){
		if($("#param_HOUSETYPE_"+locale).val()!=null){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		loadGroups($("#param_HOUSETYPE_"+locale).val(),$("#param_YEAR_"+locale).val(),$("#param_SESSIONTYPE_"+locale).val());
		event.stopImmediatePropagation();
		}else{
			$("#param_GROUP_"+locale).empty();
			$("#param_DEPARTMENT_"+locale).empty();
			$("#param_SUBDEPARTMENT_"+locale).empty();
		}
		});
		
		$("#param_GROUP_"+locale).change(function(event){
		if($("#param_HOUSETYPE_"+locale).val()!=null){				
		if($("#param_GROUP_"+locale).val()!=null){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
		loadDepartments($("#param_GROUP_"+locale).val(),$("#param_HOUSETYPE_"+locale).val(),$("#param_YEAR_"+locale).val(),$("#param_SESSIONTYPE_"+locale).val());
		event.stopImmediatePropagation();		
		}else{
			$("#param_DEPARTMENT_"+locale).empty();
			$("#param_SUBDEPARTMENT_"+locale).empty();		
		}
		}
		});		

		$("#param_DEPARTMENT_"+locale).change(function(){
			if($("#param_HOUSETYPE_"+locale).val()!=null){				
				if($("#param_GROUP_"+locale).val()!=null){
				if($("#param_DEPARTMENT_"+locale).val()!=null){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
				loadSubDepartmentsByDepartments($("#param_GROUP_"+locale).val(),$("#param_DEPARTMENT_"+locale).val(),$("#param_HOUSETYPE_"+locale).val(),$("#param_YEAR_"+locale).val(),$("#param_SESSIONTYPE_"+locale).val());
				}else{
					$("#param_SUBDEPARTMENT_"+locale).empty();		
				}
				}
			}	
		});	
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="usergroup" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
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
		<label class="small"><spring:message code="usergroup.year" text="Year" /></label>			
		<select  id="param_YEAR_${locale}" name="param_YEAR_${locale}" class="sSelect">
			<c:forEach items="${years}" var="i">
			<c:choose>
			<c:when test="${selectedYear==i}">
			<option value="${i}" selected="selected">${i}</option>				
			</c:when>
			<c:otherwise>
			<option value="${i}">${i}</option>				
			</c:otherwise>
			</c:choose>				
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.sessiontype" text="Session Type" /></label>			
		<select  id="param_SESSIONTYPE_${locale}" name="param_SESSIONTYPE_${locale}" class="sSelect">
			<c:forEach items="${sessionTypes}" var="i">	
			<c:choose>
			<c:when test="${selectedSessionType==i.sessionType }">
			<option value="${i.sessionType}" selected="selected">${i.sessionType}</option>				
			</c:when>
			<c:otherwise>
			<option value="${i.sessionType}">${i.sessionType}</option>				
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
	<fieldset>
	<legend> <spring:message code="usergroup.questionparameters" text="Settings For Questions Only"/> </legend>	
	<p>
		<label class="small"><spring:message code="usergroup.group" text="Groups Type" /></label>			
		<select  id="param_GROUP_${locale}" name="param_GROUP_${locale}" multiple="multiple" size="5">
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
	</fieldset>
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