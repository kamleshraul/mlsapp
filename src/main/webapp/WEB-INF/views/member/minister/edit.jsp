<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="member.minister" text="Member Minister Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var memberDepartmentCount=parseInt($('#memberDepartmentCount').val());
	var totalMemberDepartmentCount=0;
	totalMemberDepartmentCount=totalMemberDepartmentCount+memberDepartmentCount;
	function addMemberDepartment(){
		memberDepartmentCount=memberDepartmentCount+1;
		totalMemberDepartmentCount=totalMemberDepartmentCount+1;
		var sCount=0;
		var text="<div id='memberDepartment"+memberDepartmentCount+"'>"+
				  "<p>"+
	              "<label class='small'>"+$('#memberDepartmentDepartmentMessage').val()+"</label>"+
	              "<select name='memberDepartmentDepartment"+memberDepartmentCount+"' id='memberDepartmentDepartment"+memberDepartmentCount+"' class='sSelect'>"+
			      $('#departmentMaster').html()+
			      "</select>"+
			      "</p>"+
			      "<p>"+
	              "<label class='small'>"+$('#memberDepartmentSubDepartmentMessage').val()+"</label>"+
	              "<select name='memberDepartmentSubDepartment"+memberDepartmentCount+"' id='memberDepartmentSubDepartment"+memberDepartmentCount+"' class='sSelect'>"+
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
		var prevCount=memberDepartmentCount-1;
		if(totalMemberDepartmentCount==1){
			$('#addMemberDepartment').after(text);
		}else{
			$('#memberDepartment'+prevCount).after(text);
		}
		$('#memberDepartmentCount').val(memberDepartmentCount); 	
		//To apply datemask to the date fields
		$('.datemask').focus(function(){
			if($(this).val()==""){
				$(".datemask").mask("99/99/9999");
			}
		});		
		sCount=memberDepartmentCount;
		 if ($('#memberDepartmentDepartment'+memberDepartmentCount).val() != undefined) {
				$('#memberDepartmentDepartment'+memberDepartmentCount).change(function() {
					populateSubDepartments($('#memberDepartmentDepartment'+sCount).val(),sCount);
				});
		 }
	}
	function populateSubDepartments(department,count) {
		$.get('ref/department/' + department + '/subDepartments', function(data) {
			$('#memberDepartmentSubDepartment'+ count+' option').empty();
			var options = "";
			for ( var i = 0; i < data.length; i++) {
				options += "<option value='"+data[i].id+"'>" + data[i].name
						+ "</option>";
			}
			$('#memberDepartmentSubDepartment'+ count).html(options);
			});
	} 
	
	function deleteMemberDepartment(id){
		var memberDepartmentId=$('#memberDepartmentId'+id).val();
		if(memberDepartmentId != ''){			
	    	$.delete_('member/minister/department/'+memberDepartmentId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
	    		$('#memberDepartment'+id).remove();
	    		totalMemberDepartmentCount=totalMemberDepartmentCount-1;
				if(id==memberDepartmentCount){
					memberDepartmentCount=memberDepartmentCount-1;
				}
	    	});	
		}else{
			$('#memberDepartment'+id).remove();
			totalMemberDepartmentCount=totalMemberDepartmentCount-1;
			if(id==memberDepartmentCount){
				memberDepartmentCount=memberDepartmentCount-1;
			}
		}	
	}
	$(document).ready(function(){
		$('#departmentMaster').hide();
		$('#subDepartmentMaster').hide();
		$('#addMemberDepartment').click(function(){
			addMemberDepartment();
		});
		var deptCount='${departments.size()}';
		for(var i=1;i<=deptCount;i++){
			if($('#memberDepartmentDepartment'+i).val()!=null){
				populateSubDepartments($('#memberDepartmentDepartment'+i).val(),i);
			}
		}

		var memberDepartmentCount=$('#memberDepartmentCount').val();
		for(var i=1;i<=memberDepartmentCount;i++){
			if($('#memberDepartmentIsIndependentCharge'+i).val()=="true"){
				$('#memberDepartmentIsIndependentCharge'+i).attr("checked",true);
			}
		}	
	});
	</script>
</head>

<body>
<div class="fields clearfix">
<form:form action="member/minister" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.member" text="Member"></spring:message>:&nbsp;
		${domain.member.title.name} ${domain.member.firstName } ${domain.member.middleName} ${domain.member.lastName}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<!-- Designation related information -->
	<p>
		<label class="small"><spring:message code="member.minister.designation" text="Designation"/></label>
		<form:select path="designation" items="${designations}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
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
	<p>
		<label class="small"><spring:message code="member.minister.ministry" text="Ministry"/></label>
		<form:select path="ministry" items="${ministries}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="ministry" cssClass="validationError"/>		
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
	
	<!-- Dynamic Addition of Departments -->
	 
	<div>
	<input type="button" class="button" id="addMemberDepartment" 
	value="<spring:message code='member.minister.addDepartment' text='Add Departments'></spring:message>">
		
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
	
	<select name="departmentMaster" id="departmentMaster" disabled="disabled">
		<c:forEach items="${departments}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	<select name="subDepartmentMaster" id="subDepartmentMaster" disabled="disabled">
		<c:forEach items="${subDepartments}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	<form:errors path="memberDepartments" cssClass="validationError"></form:errors>
	 <c:if test="${!(empty memberDepartments)}"> 
		<c:set var="count" value="1"></c:set>
		<c:forEach items="${memberDepartments}" var="outer">
		<div id="memberDepartment${count}">
		<p>
			<label class="small"><spring:message code="member.minister.department" text="Department"/></label>
			<select name="memberDepartmentDepartment${count}" id="memberDepartmentDepartment${count}" class="sSelect">
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
		<p>
			<label class="small"><spring:message code="member.minister.subDepartment" text="Sub Department"/></label>
			<select name="memberDepartmentSubDepartment${count}" id="memberDepartmentSubDepartment${count}" class="sSelect">
				<c:forEach items="${subDepartments}" var="i">
				<c:choose>
				<c:when test="${outer.subDepartment.id==i.id}">		
				<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>		
				</c:when>
				<c:otherwise>
				<c:choose>
				<c:when test="${outer.subDepartment.id!=null}">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:when>
				</c:choose>
						
				</c:otherwise>
			</c:choose>	
			</c:forEach>
			</select>
		</p>
		<p>
	    <label class="small"><spring:message code="member.minister.departmentFromDate" text="From Date"/></label>
		<input name="memberDepartmentFromDate${count}" id="memberDepartmentFromDate${count}" class="datemask sText" value="${outer.formatFromDate()}">
		</p>
		<p>
	    <label class="small"><spring:message code="member.minister.departmentToDate" text="To Date"/></label>
		<input name="memberDepartmentToDate${count}" id="memberDepartmentToDate${count}" class="datemask sText" value="${outer.formatToDate()}">
		</p>
		<p>
		<label class="small"><spring:message code="member.minister.departmentIndependentCharge" text="Is Independent Charge"/></label>
		<input type="checkbox" name="memberDepartmentIsIndependentCharge${count}" id="memberDepartmentIsIndependentCharge${count}" value="${outer.isIndependentCharge}" class="sCheck">
		</p>
		<input type='button' class='button' id='${count}' 
		value='<spring:message code="member.minister.deleteDepartment" text="Delete Department"></spring:message>' 
		onclick='deleteMemberDepartment(${count});'/>
		<input type='hidden' id='memberDepartmentId${count}' name='memberDepartmentId${count}' value="${outer.id}">
		<input type='hidden' id='memberDepartmentVersion${count}' name='memberDepartmentVersion${count}' value="${outer.version}">
		<input type='hidden' id='memberDepartmentLocale${count}' name='memberDepartmentLocale${count}' value="${domain.locale}">
		<c:set var="count" value="${count+1}"></c:set>	
		</div>
		</c:forEach>
	 </c:if>	
	</div>
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	
	<input id="member" name="member" value="${member}" type="hidden">
</form:form>
</div>
</body>
</html>