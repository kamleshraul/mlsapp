<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="town" text="Town"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function onStateChange(stateId) {
		var resourceURL = "ref/state/" + stateId + "/districts";
		$.get(resourceURL, function(data){
			var dataLength = data.length;
			if(dataLength > 0) {
				var text = "";
				for(var i = 0; i < dataLength; i++) {
					text += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
				}
				$('#district').empty();
				$('#district').html(text);
			}
			else {
				$('#district').empty();
			}
		});
	}
	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');

		$('#state').change(function(){
			var stateId = $('#state').val();
			onStateChange(stateId);
		});
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="town" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<!-- state is a simple input field and not a form input field because
		 it is not an attribute of the town instance. -->
	<p>
		<label class="small"><spring:message code="town.state" text="State" />*</label>
		<select class="sSelect" id="state">
			<c:forEach items="${states}" var="i">
				<c:choose>
					<c:when test="${state.id == i.id}">
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
		<label class="small"><spring:message code="town.district" text="District" />*</label>
		<form:select path="district" items="${districts}" itemLabel="name" itemValue="id" cssClass="sSelect"></form:select>										
		<form:errors path="district" cssClass="validationError"/>
	</p>
	
	<p> 
		<label class="small"><spring:message code="town.name" text="Name"/>*</label>
		<form:input path="name" cssClass="sText"/>
		<form:errors path="name" cssClass="validationError"/>	
	</p>	
	
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
</form:form>
</div>
</body>
</html>