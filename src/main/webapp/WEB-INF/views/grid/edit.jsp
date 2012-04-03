<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="grid" text="Grids"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');	
	});		
</script>
</head>
<body>
<div class="fields clearfix">
<form:form  action="grid" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>			 
		<p> 
			<label class="small"><spring:message code="grid.name" text="Name"/></label>
			<form:input cssClass="sSelect" path="name"/>
			<form:errors path="name" cssClass="validationError"/>	
		</p>
		
		<p>
			<label class="small"><spring:message code="grid.title" text="Title"/></label>
			<form:input cssClass="sSelect" path="title"/>
			<form:errors path="title" cssClass="validationError"/>
		</p>
		<p>
			<label class="small"><spring:message code="grid.colNames" text="Column Names"/></label>
			<form:input cssClass="sSelect" path="colNames"/>
			<form:errors path="colNames" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.colModel" text="Column Model"/></label>
			<form:input cssClass="sSelect" path="colModel"/>
			<form:errors path="colModel" cssClass="validationError"/>	
		</p>	
		<p>
			<label class="small"><spring:message code="grid.pageSize" text="Page Size"/></label>
			<form:input cssClass="sSelect" path="pageSize"/>
			<form:errors path="pageSize" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.sortField" text="Sort Field"/></label>
			<form:input cssClass="sSelect" path="sortField"/>
			<form:errors path="sortField" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.sortOrder" text="Sort Order"/></label>
			<form:input cssClass="sSelect" path="sortOrder"/>
			<form:errors path="sortOrder" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.query" text="Select Query"/></label>
			<form:input cssClass="sSelect" path="query"/>
			<form:errors path="query" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.countQuery" text="Count Query"/></label>
			<form:input cssClass="sSelect" path="countQuery"/>
			<form:errors path="countQuery" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.nativeQuery" text="Native Query"/></label>
			<form:input cssClass="sSelect" path="nativeQuery"/>
			<form:errors path="nativeQuery" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.width" text="Width"/></label>
			<form:input cssClass="sSelect" path="width"/>
			<form:errors path="width" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.height" text="Height"/></label>
			<form:input cssClass="sSelect" path="height"/>
			<form:errors path="height" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.detailView" text="Detail View"/></label>
			<form:input cssClass="sSelect" path="detailView"/>
			<form:errors path="detailView" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.multiSelect" text="Multi Select?"/></label>
			<form:checkbox path="multiSelect" cssClass="sSelect"/>
			<form:errors path="multiSelect" cssClass="validationError"/>	
		</p>
		<p>
			<label class="small"><spring:message code="grid.localized" text="Localized?"/></label>
			<form:checkbox path="localized" cssClass="sSelect"/>
			<form:errors path="localized" cssClass="validationError"/>	
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