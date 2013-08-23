<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.party" text="Member Party Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	function loadHouses(houseType){
		$.get('ref/houses/'+houseType,function(data){
			var text="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			    text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$("#house").empty();
			$("#house").html(text);
			}else{
				$("#house").empty();				
			}
		},'json').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	$(document).ready(function(){
		if($("#selectedhouseType").val()=="lowerhouse"){
			$("#lowerhouselabel").show();
			$("#upperhouselabel").hide();				
		}else{
			$("#upperhouselabel").show();
			$("#lowerhouselabel").hide();				
		}
		$("#houseTypes").val($("#selectedhouseType").val());
		$("#houseTypes").change(function(){
			loadHouses($(this).val());
			$("#selectedhouseType").val($(this).val());
			if($("#selectedhouseType").val()=="lowerhouse"){
				$("#lowerhouselabel").show();
				$("#upperhouselabel").hide();				
			}else{
				$("#upperhouselabel").show();
				$("#lowerhouselabel").hide();				
			}
			$("span[id$='errors']").remove();
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
<form:form action="member/party" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}
	</h2>
	<h2>
		<spring:message code="member.party" text="Election Result"/>
	</h2>
	<form:errors path="version" cssClass="validationError" cssStyle="color:red;"/>	
	<p>
	<label class="small"><spring:message code="member.house.houseType"	text="House Types" /></label>
	<select id="houseTypes" name="houseTypes" class="sSelect">
	<c:forEach items="${houseTypes }" var="i">
	<option value="${i.type}">${i.name}</option>
	</c:forEach>
	</select>											
	</p>
	
	<p>
	<label class="small" id="lowerhouselabel"><spring:message code="generic.lowerhouse" text="Assembly"/></label>
	<label class="small" id="upperhouselabel"><spring:message code="generic.upperhouse" text="Council"/></label>
		<form:select path="house" items="${houses}" itemLabel="displayName" itemValue="id" cssClass="sSelect"/>
		<form:errors path="house" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.party.party" text="Party"/></label>
		<form:select path="party" items="${parties}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
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
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>