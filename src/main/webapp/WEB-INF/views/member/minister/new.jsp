<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="member.minister" text="Member Minister Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <style>
	#submit{
	cursor: pointer;
	}
	#cancel{
	cursor: pointer;
	}
	</style>
	<script type="text/javascript">
	var memberDepartmentCount = parseInt($('#memberDepartmentCount').val());
	var totalMemberDepartmentCount = 0;
	totalMemberDepartmentCount = totalMemberDepartmentCount + memberDepartmentCount;

	function addMemberDepartment(){
		memberDepartmentCount = memberDepartmentCount + 1;
		totalMemberDepartmentCount = totalMemberDepartmentCount + 1;

		var text = "<div id='memberDepartment"+memberDepartmentCount+"'>"+
			"<p>"+
			"<label class='small'>"+$('#memberDepartmentDepartmentMessage').val()+"</label>"+
			"<select name='memberDepartmentDepartment"+memberDepartmentCount+"' id='memberDepartmentDepartment"+memberDepartmentCount+"' class='sSelect departmentClass' onchange='populateSubDepartments($(this).val(), " + memberDepartmentCount + ")'>"+
			$('#departmentMaster').html()+
			"</select>"+
		    "</p>"+
		    "<p>"+
		    "<label class='labeltop'>"+$('#memberDepartmentSubDepartmentMessage').val()+"</label>"+
		    "<select name='memberDepartmentSubDepartment"+memberDepartmentCount+"' id='memberDepartmentSubDepartment"+memberDepartmentCount+"' class='sSelect' multiple='multiple' size='5' style='height:100px;margin-top:5px;'>"+
		    $('#subDepartmentMaster').html()+
			"</select>"+
		    "</p>"+
		    "<p>"+
  		  	"<label class='small'>"+$('#memberDepartmentFromDateMessage').val()+"</label>"+
  		  	"<input name='memberDepartmentFromDate"+memberDepartmentCount+"' id='memberDepartmentFromDate"+memberDepartmentCount+"' class='datemask sText'>"+
  		    "</p>"+
  		 	"<p>"+
		  	"<label class='small'>"+$('#memberDepartmentToDateMessage').val()+"</label>"+
		  	"<input name='memberDepartmentToDate"+memberDepartmentCount+"' id='memberDepartmentToDate"+memberDepartmentCount+"' class='datemask sText'>"+
		  	"</p>"+
		  	"<p>"+
  		  	"<label class='small'>"+$('#memberDepartmentIndependentChargeMessage').val()+"</label>"+
  		  	"<input type='checkbox' name='memberDepartmentIsIndependentCharge"+memberDepartmentCount+"' id='memberDepartmentIsIndependentCharge"+memberDepartmentCount+"' value='true' class='sCheck'>"+
  		  	"</p>"+
  		  	"<input type='button' class='button' id='"+memberDepartmentCount+"' value='"+$('#deleteMemberDepartmentMessage').val()+"' onclick='deleteMemberDepartment("+memberDepartmentCount+");'>"+
		  	"<input type='hidden' id='memberDepartmentId"+memberDepartmentCount+"' name='memberDepartmentId"+memberDepartmentCount+"'>"+
		  	"<input type='hidden' id='memberDepartmentLocale"+memberDepartmentCount+"' name='memberDepartmentLocale"+memberDepartmentCount+"' value='"+$('#locale').val()+"'>"+
		  	"<input type='hidden' id='memberDepartmentVersion"+memberDepartmentCount+"' name='memberDepartmentVersion"+memberDepartmentCount+"'>"+
		  	"</div>"; 

		var prevCount = memberDepartmentCount - 1;
		if(totalMemberDepartmentCount == 1){
			$('#addMemberDepartment').after(text);
		} else{
			$('#memberDepartment'+ prevCount).after(text);
		}
		$('#memberDepartmentCount').val(memberDepartmentCount); 
		// To apply datemask to the date fields
		$('.datemask').focus(function(){
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});		
	}

	function deleteMemberDepartment(id){
		$('#memberDepartment'+id).remove();
		totalMemberDepartmentCount = totalMemberDepartmentCount - 1;
		if(id == memberDepartmentCount){
			memberDepartmentCount = memberDepartmentCount - 1;
		}		
	}	

	function populateSubDepartments(department, count){
		$.get('ref/department/' + department + '/subDepartments', 
			function(data) {
				$('#memberDepartmentSubDepartment'+ count+' option').empty();
				var options = "";
				for ( var i = 0; i < data.length; i++) {
					options += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
				}
				$('#memberDepartmentSubDepartment'+ count).html(options);
			}
		).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	$(document).ready(function(){
		$('#departmentMaster').hide();
		$('#subDepartmentMaster').hide();
		$('#addMemberDepartment').click(function(){
			addMemberDepartment();
		});

		$('#ministry').prepend("<option value=''>SELECT</option>");
		$('.designationClass').change(function(){
			var options = $(this).context.outerHTML;
			var tempStr = options.split('<option value="' + $(this).val() + '">')[1];
			var designation = tempStr.split("</option>")[0];
			var stateMinister = $('#nonPortfolioDesignations').val();
			if(designation == stateMinister){
				$('#ministryDiv').hide();
				$('#ministry').val("");
				$('#ministryAssignmentDate').val('');
				$('#ministryFromDate').val('');
				$('#ministryToDate').val('');
			} else {
				$('#ministryDiv').show();
			}
		});	
	});
	</script>
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/minister" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}
	</h2>
	<h2>
		<spring:message code="member.minister" text="Minister"/>
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- Designation related fields -->
	<p>
		<label class="small"><spring:message code="member.minister.designation" text="Designation"/></label>
		<form:select path="designation" items="${designations}" itemLabel="name" itemValue="id" cssClass="sSelect designationClass"/>
		<form:errors path="designation" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="member.minister.oathDate" text="Oath Date"/></label>
		<form:input path="oathDate" cssClass="datemask sText"/>
		<form:errors path="oathDate" cssClass="validationError"/>	
	</p>	
	<p>
		<label class="small"><spring:message code="member.minister.resignationDate" text="Resignation Date"/></label>
		<form:input path="resignationDate" cssClass="datemask sText"/>
		<form:errors path="resignationDate" cssClass="validationError"/>	
	</p>	
		
	<!-- Ministry related information -->
	<div id="ministryDiv">
	<p>
		<label class="small"><spring:message code="member.minister.ministry" text="Ministry"/></label>
		<form:select path="ministry" items="${ministries}" itemLabel="name" itemValue="id" cssClass="sSelect ministerClass"/>
		<form:errors path="ministry" cssClass="validationError"/>		
	</p>
	<p>
		<label class="small"><spring:message code="member.minister.ministryAssignmentDate" text="Ministry Assignment Date"/></label>
		<form:input path="ministryAssignmentDate" cssClass="datemask sText"/>
		<form:errors path="ministryAssignmentDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.fromdate" text="From Date"/></label>
		<form:input path="ministryFromDate" cssClass="datemask sText"/>
		<form:errors path="ministryFromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.todate" text="To Date"/></label>
		<form:input path="ministryToDate" cssClass="datemask sText"/>
		<form:errors path="ministryToDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.minister.priority" text="Ministry Priority"/></label>
		<form:input path="priority" cssClass="sText"/>
		<form:errors path="priority" cssClass="validationError"/>
	</p>
	</div>
	
	<!-- Dynamic Addition of Departments -->
	<div>
		<input type="button" class="button" id="addMemberDepartment" 
		value="<spring:message code='member.minister.addDepartment' text='Add Departments'></spring:message>">
	
		<form:errors path="memberDepartments" cssClass="validationError"></form:errors>
		<c:if test="${! (empty memberDepartments)}">
		<c:set var="count" value="1"></c:set>
		<c:forEach items="${memberDepartments}" var="outer">
		<div id="memberDepartment${count}">
			<p>
			<label class="small"><spring:message code="member.minister.department" text="Department"/></label>
			<select name="memberDepartmentDepartment${count}" id="memberDepartmentDepartment${count}" class="sSelect departmentClass" onchange="populateSubDepartments(this.value, ${count})">
				<c:forEach items="${departments}" var="i">
					<c:choose>
						<c:when test="${outer.department.id==i.id}">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			</p>
			
			<!-- SubDepartments will be a multi select box -->
			<p>
			<label class="labeltop"><spring:message code="member.minister.subDepartment" text="Sub Department"/></label>
			<select name="memberDepartmentSubDepartment${count}" id="memberDepartmentSubDepartment${count}" class="sSelect" multiple="multiple" size="5" style="height:100px;margin-top:5px;">
				<c:forEach items="${mapOfSubDepartmentList}" var="i">
				<c:if test="${i.key == count}">
					<!-- size1 refers to the size of the entire subdepartment list of a 
						 particular department -->
					<c:set var="size1" value="${fn:length(i.value)}"></c:set>
					<!-- size2 refers to the size of the selected subdepartment list of a 
						 particular department -->
					<c:set var="size2" value="${fn:length(outer.subDepartments)}"></c:set>
					<!-- i.value refers to the entire list of subdepartments -->					
					<c:forEach items="${i.value}" var="j">
						<c:set var="counter" value="0"></c:set>
						<c:forEach items="${outer.subDepartments}" var="k">
							<c:set var="counter" value="${counter + 1}"></c:set>
							<c:if test="${j.id == k.id}">
								<c:set var="counter" value="${counter - 1}"></c:set>
							</c:if>
						</c:forEach>
						<c:choose>
							<c:when test="${counter < size2}">
								<option value="${j.id}" selected="selected"><c:out value="${j.name}"></c:out></option>
							</c:when>
							<c:otherwise>
								<option value="${j.id}"><c:out value="${j.name}"></c:out></option>		
							</c:otherwise>
						</c:choose>
					</c:forEach>	
				</c:if>
				</c:forEach>
			</select>
			</p>
			
			<p>
			<label class="small"><spring:message code="member.minister.departmentFromDate" text="From Date"/></label>
			<input name="memberDepartmentFromDate${count}" id="memberDepartmentFromDate${count}" class="datemask sText" 
			value="${outer.formatFromDate()}">
			</p>
			
			<p>
			<label class="small"><spring:message code="member.minister.departmentToDate" text="To Date"/></label>
			<input name="memberDepartmentToDate${count}" id="memberDepartmentToDate${count}" class="datemask sText" 
			value="${outer.formatToDate()}">
			</p>
			
			<p>
			<label class="small"><spring:message code="member.minister.departmentIndependentCharge" text="Is Independent Charge"/></label>
			<input type="checkbox" name="memberDepartmentIsIndependentCharge${count}" 
			id="memberDepartmentIsIndependentCharge${count}" value="${outer.isIndependentCharge}" class="sCheck">
			</p>
			
			<input type='button' class='button' id='${count}' 
			value='<spring:message code="member.minister.deleteDepartment" text="Delete Department"></spring:message>' 
			onclick='deleteMemberDepartment(${count});'/>
			
			
			<!-- Hidden variables required for each instance of MemberDepartment -->
			<input type='hidden' id='memberDepartmentId${count}' name='memberDepartmentId${count}' 
			value="${outer.id}">
		
			<input type='hidden' id='memberDepartmentVersion${count}' name='memberDepartmentVersion${count}' 
			value="${outer.version}">
		
			<input type='hidden' id='memberDepartmentLocale${count}' name='memberDepartmentLocale${count}' 
			value="${domain.locale}">
		
		</div>
		<c:set var="count" value="${count+1}"></c:set>		
		</c:forEach>
		</c:if>
		
		<!-- To be used from Javascript functions when a MemberDepartment is to be
			 added dynamically
		 -->
		<select name="departmentMaster" id="departmentMaster" class="sSelect" disabled="disabled">
			<c:forEach items="${departments}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
		</select>

		<select name="subDepartmentMaster" id="subDepartmentMaster" multiple="multiple" size="5" class="sSelect" style="height:100px;margin-top:5px;" disabled="disabled">
			<c:forEach items="${subDepartments}" var="i">
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
		</select>
	

		<!-- Hidden Messages to preserve the localization of the field names -->
		<input type="hidden" id="memberDepartmentCount" name="memberDepartmentCount" value="${memberDepartmentCount}"/>
		
		<input type="hidden" id="deleteMemberDepartmentMessage" name="deleteMemberDepartmentMessage" 
		value="<spring:message code='member.minister.deleteDepartment' text='Delete Department'></spring:message>" 
		disabled="disabled"/>
		
		<input type="hidden" id="memberDepartmentDepartmentMessage" name="memberDepartmentDepartmentMessage" 
		value="<spring:message code='member.minister.department' text='Department'></spring:message>" 
		disabled="disabled"/>
	
		<input type="hidden" id="memberDepartmentSubDepartmentMessage" name="memberDepartmentSubDepartmentMessage" 
		value="<spring:message code='member.minister.subDepartment' text='Sub Department'></spring:message>" 
		disabled="disabled"/>
	
		<input type="hidden" id="memberDepartmentFromDateMessage" name="memberDepartmentFromDateMessage" 
		value="<spring:message code='member.minister.departmentFromDate' text='From Date'></spring:message>" 
		disabled="disabled"/>
	
		<input type="hidden" id="memberDepartmentToDateMessage" name="memberDepartmentToDateMessage" 
		value="<spring:message code='member.minister.departmentToDate' text='To date'></spring:message>" 
		disabled="disabled"/>
	
		<input type="hidden" id="memberDepartmentIndependentChargeMessage" name="memberDepartmentIndependentChargeMessage" 
		value="<spring:message code='member.minister.departmentIndependentCharge' text='Is Independent Charge'></spring:message>" 
		disabled="disabled"/>
	</div>
		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
			
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<input id="member" name="member" value="${member}" type="hidden">
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">
	<input id="nonPortfolioDesignations" name="nonPortfolioDesignations" value="${nonPortfolioDesignations}" type="hidden">
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>