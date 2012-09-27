<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="group.title" text="Title"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);	
		$('#submit').click(function(){
			if($('#sessionType').val()==null){
				alert($('#noSessionTypeMessage').val());
				return false;
		}	
	});
	
	if ($('#year').val() != undefined) {
		$('#year').change(
				function() {
					$.ajax({
						url : 'ref/' + $('#year').val() +'/'+$('#houseType').val()+ '/sessionType',
						datatype : 'json',
						success : function(data) {
							$('#sessionType option').remove();
							if(data.length==0){
								alert($('#noSessionTypeMessage').val());
							}
							else{
								for ( var i = 0; i < data.length; i++) {
									$('#sessionType').append(
											"<option value='"+data[i].id+"'>"
													+ data[i].name + "</option>");
									}
								}
							}
					});
				});
		}
	
	if ($('#houseType').val() != undefined) {
		$('#houseType').change(
				function() {
					$.ajax({
						url : 'ref/' + $('#year').val() +'/'+$('#houseType').val()+ '/sessionType',
						datatype : 'json',
						success : function(data) {
							$('#sessionType option').remove();
							if(data.length==0){
								alert($('#noSessionTypeMessage').val());
							}
							else{
								
								for ( var i = 0; i < data.length; i++) {
									$('#sessionType').append(
											"<option value='"+data[i].id+"'>"
													+ data[i].name + "</option>");
								}
							}
						}
					});
				});
		}
});		
	
</script>
</head>
<body>
<div class="fields clearfix">
<form:form  action="group" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>	
	<form:errors path="version" cssClass="validationError"/>	
	<p>	 
	<label class="small"><spring:message code="group.number" text="Group" /></label>			
		<form:select path="number" id="number" cssClass="sSelect">
			<c:forEach begin= "1" end = '${groupNo}' var="i">
				<c:choose>
					<c:when test="${domain.number == i}">
						<option value="${i}" selected="selected">${i}</option>
					</c:when>
					<c:otherwise>
						<option value="${i}">${i}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</form:select>
		<form:errors path="number" cssClass="validationError" />
	</p>			
	<p>
		<label class="small"><spring:message code="group.houseType" text="House Type" /></label>			
		<form:select path="houseType" id="houseType" cssClass="sSelect" items="${houseTypes}" itemLabel="name" itemValue="id">
		</form:select>
		<form:errors path="houseType" cssClass="validationError" />
	</p>	
	<p>
		<label class="small"><spring:message code="group.year" text="Year" /></label>			
		<form:select path="year" id="year" cssClass="sSelect" items="${years}" >
		</form:select>
		<form:errors path="year" cssClass="validationError" />
	</p>	
	<p>
		<label class="small"><spring:message code="group.sessionType" text="Session Type" /></label>			
		<form:select path="sessionType" id="sessionType" cssClass="sSelect" items="${sessionTypes}" itemLabel="sessionType" itemValue="id">
		</form:select>
		<form:errors path="sessionType" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="group.ministries" text="Ministries" /></label>			
		<form:select path="ministries" id="ministries" items="${ministries}" itemValue="id" itemLabel="name" multiple="multiple" size="10"/>
		<form:errors path="ministries" cssClass="validationError" />
	</p>				
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>	
	<input type="hidden" id="key" name="key">
	<input type="hidden" id="noSessionTypeMessage" value='<spring:message code="group.noSessionType" text="Session Does Not exist "></spring:message>'>	
	<form:hidden path="version" />
	<form:hidden  path="id"/>
	<form:hidden path="locale"/>	
</form:form>
</div>	
</body>
</html>