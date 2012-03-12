<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="${urlPattern}" text="Constituencies"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		if($('#states').val()!=undefined){
			$('#states').change(function(){
				$.ajax({
					url:'ref/'+$('#states').val()+'/districts',
					datatype:'json',
					success:function(data){				
					$('#districts option').empty();
					var options="";
					for(var i=0;i<data.length;i++){
						options+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}
					$('#districts').html(options);
					$('#districts').sexyselect('destroy');
					$('#districts').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true});
				}							
				});
		});	
		} 
		});
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="${urlPattern}" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	<p> 
		<label class="small"><spring:message code="${urlPattern}.state" text="State"/></label>
			<select name="state" id="states">
				<c:forEach items="${states}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:forEach>
			</select>	
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.district" text="District"/></label>
		<form:select path="districts" items="${districts}" itemValue="id" itemLabel="name" size="5" multiple="multiple" id="districts"></form:select>
		<form:errors path="districts" cssClass="validationError" />
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.name" text="Constituency"/></label>
		<form:input cssClass="sSelect" path="name"/>
		<form:errors path="name" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.number" text="Constituency Number"/></label>
		<form:input cssClass="sSelect" path="number"/>
		<form:errors path="number" cssClass="validationError"/>
	</p>
	<p>
		<label class="small"><spring:message code="${urlPattern}.reserved" text="Reserved?"/></label>
		<form:checkbox cssClass="sSelect" path="reserved"/>
		<form:errors path="reserved" cssClass="validationError"/>	
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
</form:form>
</div>
</body>
</html>