<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form class="wufoo" action="member_role/assignroles/updateMemberRoles" method="post"">
<div class="info">
			<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="Note: Fields marked * are mandatory"/></div>
</div>
<ul>
		<li>
		<label class="desc"><spring:message code="mms.assignroles.memberid" text="Member Id"/><span><spring:message code="${fields.id.hint}" text=""/></span>&nbsp;<c:if test="${fields.id.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
			<input type="text" value="${memberId}" name="memberId" id="memberId" readonly="readonly">
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="mms.assignroles.member" text="Member Name"/><span><spring:message code="${fields.id.hint}" text=""/></span>&nbsp;<c:if test="${fields.id.mandatory=='MANDATORY'}">*</c:if></label>
			<div>
			<input type="text" value="${memberName}" name="memberId" id="memberId" readonly="readonly">
			</div>
		</li>
</ul>
<table class="datatable">
<thead>
<tr>
<th >Assembly</th>
<th >Role</th>
<th>From</th>
<th>To</th>
<th>Remarks</th>
</tr>
</thead>
<tbody>
<c:set var="count" value="1"></c:set>
<c:if test="${!(empty memberRoles)}">
<c:forEach items="${memberRoles}" var="i">
<tr>

<td><select id="assembly${count}" name="assembly${count}">
<c:forEach items="${assemblies}" var="j">
<option value="${j.id}"><c:out value="${j.assembly}"></c:out></option>
</c:forEach>
</select>
<input type="hidden" value="${i.id}" name="id${count}" id="id${count}">
<input type="hidden" value="${i.version}" name="version${count}" id="version${count}">
<input type="hidden" value="${i.locale}" name="locale${count}" id="locale${count}">
<input type="hidden" value="${i.assembly.id}" name="selectedassembly${count}" id="selectedassembly${count}">
</td>

<td><select id="role${count}" name="role${count}">
<c:forEach items="${roles}" var="k">
<option value="${k.id}"><c:out value="${k.name}"></c:out></option>
</c:forEach>
</select>
<input type="hidden" value="${i.role.id}" name="selectedrole${count}" id="selectedrole${count}">
</td>

<td><input value="${i.fromDate}" type="text" id="fromDate${count}" class="date" name="fromDate${count}" size="10"></td>

<td><input value="${i.toDate}" type="text" id="toDate${count}" class="date" name="toDate${count}" size="10"></td>

<td><input  id="remarks${count}"  name="remarks${count}" value="${i.remarks}"></td>
<td>
</tr>
<c:set var="count" value="${count+1}"></c:set>
</c:forEach>
</c:if>
</tbody>
</table>
<input type="hidden" value="${noOfRecords}" name="noOfRecords" id="noOfRecords">
<input id="saveForm" class="btTxt" type="submit" value="<spring:message code='generic.edit.submit' text='Update'/>" />
</form>
</body>
<head>
	<title><spring:message code="mms.assignroles.edit.title" text="Edit Assigned Roles"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<link rel="stylesheet" media="screen" href="./resources/css/tables.css" />	
	<script type="text/javascript">
	$(document).ready(function(){
		$('select[id^="assembly"]').each(function(){
			var id=this.id;
			var count=id.charAt(id.length-1);
			$('#assembly'+count).val($('#selectedassembly'+count).val());
		});
		$('select[id^="role"]').each(function(){
			var id=this.id;
			var count=id.charAt(id.length-1);
			$('#role'+count).val($('#selectedrole'+count).val());
		});
		$('input[id^="fromDate"]').each(function(){
			var oldDate=$('#'+this.id).val();
			if(oldDate!=""){
				var dateComponents=oldDate.split("-");
				var formattedDate=dateComponents[2]+"/"+dateComponents[1]+"/"+dateComponents[0];
				$('#'+this.id).val(formattedDate);
			}
			
		});
		$('input[id^="toDate"]').each(function(){
			var oldDate=$('#'+this.id).val();
			if(oldDate!=""){
				var dateComponents=oldDate.split("-");
				var formattedDate=dateComponents[2]+"/"+dateComponents[1]+"/"+dateComponents[0];
				$('#'+this.id).val(formattedDate);
			}
		});
		
	});
	</script>
</head>
</html>