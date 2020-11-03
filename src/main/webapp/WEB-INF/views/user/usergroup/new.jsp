<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="usergroup.title" text="User Group"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	function loadSubDepartments(){
		var locale=$("#locale").val();		
		//var departments=$("#param_DEPARTMENT_"+locale).val();
		var ministries=$("#param_MINISTRY_"+locale).val();
		if(ministries!=''){
			$.post('ref/subdepartments/byministriesname',{'ministries':ministries},function(data){
				var text="";
				if(data.length>0){
					for(var i=0;i<data.length;i++){
						var flag = false;
						$("#param_SUBDEPARTMENT_"+locale+" option").each(function(){
							if($(this).attr("selected")=="selected" && $(this).val()==data[i].name){
								flag=true;
							}
						 });
						if(flag){
							text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
						}else{
							text+="<option value='"+data[i].name+"'>"+data[i].name+"</option>";
						}
					}
					$("#param_SUBDEPARTMENT_"+locale).empty();
					$("#param_SUBDEPARTMENT_"+locale).html(text);
					$.unblockUI();				
				}else{
					$.unblockUI();				
				}
			});	
		}else{
			$("#param_SUBDEPARTMENT_"+locale).empty();
			$.unblockUI();			
		}
	}

	/* function loadDepartments(){
		var locale=$("#locale").val();		
		var ministries=$("#param_MINISTRY_"+locale).val();
		if(ministries!=''){
		$.post('ref/departments/byministriesname',{'ministries':ministries},function(data){
			$("#param_DEPARTMENT_"+locale).empty();
			var text="";
			if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].name+"' selected='selected'>"+data[i].name+"</option>";
				}
				$("#param_DEPARTMENT_"+locale).html(text);
				loadSubDepartments();
			}else{
				$("#param_DEPARTMENT_"+locale).empty();
				$("#param_SUBDEPARTMENT_"+locale).empty();
				$.unblockUI();				
			}
		});	
	}else{
		$("#param_DEPARTMENT_"+locale).empty();
		$("#param_SUBDEPARTMENT_"+locale).empty();
		$.unblockUI();				
	} 
	}*/

	function loadCommitteeNames() {
		var locale = $("#locale").val();
		var houseTypeId = getHouseTypeId();

		var resourceURL = "ref/committeeNames/houseType/" + houseTypeId;
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].value + "'>" + data[i].value + "</option>";
				}
				$('#param_COMMITTEENAME_' + locale).empty();
				$('#param_COMMITTEENAME_' + locale).html(text);
				$.unblockUI();
			}
			else {
				$('#param_COMMITTEENAME_' + locale).empty();
				$.unblockUI();
			}
		});
	}

	function getHouseTypeId() {
		var locale = $("#locale").val();
		var houseTypeName = $("#param_HOUSETYPE_" + locale).val();
		
		$('#houseTypeTypes').val(houseTypeName);
		var houseTypeId = $('#houseTypeTypes option:selected').text().trim();
		
		return houseTypeId;
	}
	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		$("select[multiple='multiple']").css("width","300px");	
		var locale=$("#locale").val();		
		$("#param_MINISTRY_"+locale).change(function(event){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
		loadSubDepartments();
		});		

		/* $("#param_DEPARTMENT_"+locale).change(function(){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 		
		loadSubDepartments();		
		}); */	
		$('#param_HOUSETYPE_' + locale).change(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			loadCommitteeNames();
		});	
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="user/usergroup" method="POST"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>		 
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
		<label class="small"><spring:message code="usergroup.name" text="Name"/></label>
		<form:select cssClass="sSelect" path="userGroupType" items="${userGroupTypes}" itemLabel="name" itemValue="id"/>
		<form:errors path="userGroupType" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message
				code="usergroup.firstName" text="First Name" /></label>
		<form:input cssClass="sText " path="firstName" />		
	</p>
	<p>
		<label class="small"><spring:message
				code="usergroup.middleName" text="Middle Name" /></label>
		<form:input cssClass="sText " path="middleName" />		
	</p>
	<p>
		<label class="small"><spring:message
				code="usergroup.lastName" text="Last Name" /></label>
		<form:input cssClass="sText " path="lastName" />		
	</p>
	<p>
		<label class="small"><spring:message code="usergroup.devicetype" text="Device Type" /></label>			
		<select  id="param_DEVICETYPE_${locale}" name="param_DEVICETYPE_${locale}" multiple="multiple" size="5" style="max-width:300px;min-width:275px;">
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
		<label class="small"><spring:message code="usergroup.committeeName" text="Committee Name" /></label>
		<select  id="param_COMMITTEENAME_${locale}" name="param_COMMITTEENAME_${locale}" multiple="multiple" size="5" style="max-width:300px;min-width:275px;">
			<c:forEach items="${committeeNames}" var="i">				
				<c:choose>
					<c:when test="${fn:contains(selectedCommitteeName,i.displayName)}">
						<option value="${i.displayName}" selected="selected">${i.displayName}</option>			
					</c:when>
					<c:otherwise>
						<option value="${i.displayName}">${i.displayName}</option>	
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
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
		<label class="small"><spring:message code="usergroup.ministry" text="Ministry" /></label>			
		<select  id="param_MINISTRY_${locale}" name="param_MINISTRY_${locale}" multiple="multiple" size="5">
			<c:forEach items="${ministries}" var="i">	
			<c:choose>
			<c:when test="${fn:contains(selectedMinistry,i.name) }">
			<option value="${i.name}" selected="selected">${i.name}</option>			
			</c:when>
			<c:otherwise>
			<option value="${i.name}">${i.name}</option>	
			</c:otherwise>
			</c:choose>			
			</c:forEach>
		</select>
		
		
	</p>
	<%-- <p>
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
		
	</p> --%>	
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
	<p>	
	<label class="small"><spring:message code="usergroup.groupsAllowed" text="Groups Allowed" /></label>
	<input type="text" id="param_GROUPSALLOWED_${locale}" name="param_GROUPSALLOWED_${locale}" value="${groupsAllowed}"/>
	</p>
	
	<p>	
		<label class="small"><spring:message code="usergroup.state" text="Current State of Actor" /></label>
		<select id="param_ACTORSTATE_${locale}" name="param_ACTORSTATE_${locale}" class="sSelect">			
			<c:forEach items="${actorstates}" var="ac">
				<c:choose>
					<c:when test="${selectedActorState==ac.id}">
						<option value="${ac.id}" selected="selected">${ac.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${ac.id}">${ac.name}</option>			
					</c:otherwise>
				</c:choose>	
			</c:forEach>
		</select>
	</p>
	
	<p>
		<label class="small"><spring:message code="usergroup.state.remark" text="Remark" /></label>
		<textarea rows="3" cols="50" name="param_ACTORREMARK_${locale}" id="param_ACTORREMARK_${locale}">${actorRemark}</textarea>
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
	
	<!-- Hidden fields that aid in Client side actions performed in Javascript -->
	<p style="display:none;">
		<select id="houseTypeTypes" class="sSelect">
			<c:forEach items="${housetypes}" var="i">
				<option value="${i.name}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
		</select>
	</p>
</form:form>
</div>	
</body>
</html>