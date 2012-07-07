<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="group.rotationorder.edit" text="Question Dates"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var count=${dateCount};
		
	$(document).ready(function(){		
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);	
		
		$(".datemask").mask("99/99/9999");
		for(var i=0;i<count;i++){
			
			if(!$('#date'+i).is(':checked')){
				$('#submissionDate'+i).val("");
			};
			
		};
		
		var y = ${domain.year};
		$('#year').val(y.toString());
		
		$(".sOption").click(function(){
			for(var j=0;j<count;j++){
				if($('#date'+j).is(':checked')){
					var sDate=$('#submittingDate'+j).val();
					$('#submissionDate'+j).val(sDate);
				}
				else{
					$('#submissionDate'+j).val("");
				};
			};
			
		});
		
		$('#submit').click(function(){
			var flag=0;
			for(var k=0;k<count;k++){
				if($('#date'+k).is(':checked')){
					flag=1;
				};
			}
			if(flag==0){
				$.prompt($('#errorMsg').val());
				return false;
			};
				
		});
	});
	
	
	</script>
	<!-- <style type="text/css">
	th
	{
	text-align:center;
	font-style:bold;
	font-size:16px;
	}
	</style> -->
</head>

<body>
<div class="fields clearfix">
<form:form action="group/rotationorder" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details"/></h2>
	<form:errors path="version" cssClass="validationError"/>
	<p>
		<label class="small"><spring:message code="group.number" text="Group" /></label>
		<form:input cssClass="sText" path="number" readonly="true"/>
	</p>	
	<p>
		<label class="small"><spring:message code="group.houseType" text="House Type" /></label>
		<form:input cssClass="sText" path="houseType.name" readonly="true"/>
	</p>
	<p>
		<label class="small"><spring:message code="group.year" text="Year" /></label>
		<form:input cssClass="sText" id="year" path="year" readonly="true"/>
	</p>
	<p>
		<label class="small"><spring:message code="group.sessionType" text="Session Type" /></label>
		<form:input cssClass="sText" path="sessionType.sessionType" readonly="true"/>
	</p>	
	<p>
		<table>
			<tr>
				<td valign="middle">				
					<label class="small"><spring:message code="group.rotationorder.selectDates" text="Rotation Order"/></label>
				</td>
				<td>
					<table>
						<tr>
							<th><spring:message code="group.rotationorder.select" text="Select"/></th>
							<th></th>
							<th></th>				
							<th><spring:message code="group.rotationorder.anweringDate" text="Answering Date"/></th>		
							<th></th>
							<th></th>	
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th><spring:message code="group.rotationorder.submissionDate" text="Last Submission Date"/></th>
						</tr>
						<tr></tr>
						<c:forEach begin="1" end="${dateCount}" varStatus="i">
						<tr>
							<td>		
								<c:choose>
									<c:when test="${empty selects}">
										<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true" class="sOption">
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${selects[i.count-1]=='true'}">
												<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true" class="sOption" checked="checked">
											</c:when>
											<c:otherwise>
												<input class="sOption" type="checkbox" id="date${i.count-1}" name="date${i.count-1}" value="true" class="sOption">
											</c:otherwise>
										</c:choose>				
									</c:otherwise>
								</c:choose>		
							</td>
		
							<td></td>
							<td></td>	
		
							<td>
								<input class="datemask sText" type="text"  name="answeringDate${i.count-1}" value="${answeringDates[i.count-1]}" readonly="readonly">
							</td>	
		
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>		
		
							<td>
								<input class="datemask sText" type="text" id="submissionDate${i.count-1}" name="submissionDate${i.count-1}" value="${submissionDates[i.count-1]}">
								<input type="hidden" id="submittingDate${i.count-1 }" value="${submissionDates[i.count-1]}">
							</td>		
						</tr>	
						<tr></tr>
						</c:forEach>
					</table>
				</td>
			</tr>
		</table>
	</p>		
	<p>
	 <div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	
	<input type="hidden" name="dateCount" value="${dateCount}">
	<input type="hidden" id="errorMsg" value='<spring:message code="group.rotationorder.errormsg"/>'>
	<input type="hidden" id="key" name="key">
	<form:hidden path="version"/>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>
</body>
</html>