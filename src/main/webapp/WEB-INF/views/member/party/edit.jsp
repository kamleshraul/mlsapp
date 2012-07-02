<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.party" text="Member Party Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		if($("#selectedhouseType").val()=="lowerhouse"){
			$("#lowerhouselabel").show();
			$("#upperhouselabel").hide();				
		}else{
			$("#upperhouselabel").show();
			$("#lowerhouselabel").hide();				
		}
	});
	</script>
</head>
<body>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/party" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}
	</h2>
	<h2>
		<spring:message code="member.party" text="Election Result"/>
	</h2>
	<form:errors path="version" cssClass="validationError" cssStyle="color:red;"/>
	<p>
	<label class="small" id="lowerhouselabel"><spring:message code="generic.lowerhouse" text="Assembly"/></label>
	<label class="small" id="upperhouselabel"><spring:message code="generic.upperhouse" text="Council"/></label>
		<input type="text" id="houseName" name="houseName" value="${houseName}" readonly="readonly" class="sText">
		<input type="hidden" id="house" name="house" value="${houseId}">
		<form:errors path="house" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.party.party" text="Party"/></label>
		<input type="text" name="partyName" id="partyName" class="sText" readonly="readonly" value="${partyName}">
		<input type="hidden" id="party" name="party" value="${party}"/>
		<form:errors path="party" cssClass="validationError"/>		
	</p>	
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="sText datemask"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask"/>
		<form:errors path="toDate" cssClass="validationError"/>	
	</p>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale" />
	<form:hidden path="recordIndex"/>
	<input id="member" name="member" value="${member}" type="hidden">
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">
	<input id="selectedhouseType" name="selectedhouseType" value="${houseType}" type="hidden">
	
</form:form>
</div>
</body>
</html>