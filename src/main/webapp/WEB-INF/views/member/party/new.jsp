<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.party" text="Member Party Details"/>
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
		},'json');
	}

	function hideUnhideRulingPartyMemberField() {
		var partyType = getPartyType();
		if(partyType == "opposition_party") {
			$('.rulingPartyMemberField').hide();
		}
		else {
			$('.rulingPartyMemberField').show();
			if(partyType == "ruling party")
				{
				alert(partyType);
				$('.rulingPartyMemberField').prop('checked', true);
				}
		}
	}
	
	function getPartyType() {
		// Read the id from party.  
		var partyId = $('#party').val();

		// Find the type corresponding to the partyId from partyTypes.
		$('#partyTypes').val(partyId);
		var type = $('#partyTypes option:selected').text().trim();
		
		return type;
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
		hideUnhideRulingPartyMemberField();
		$('#party').change(function(){
			hideUnhideRulingPartyMemberField();
		});
	});
	</script>
</head>
<body>
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
	
	<p class="rulingPartyMemberField">
		<label class="small"><spring:message code="member.party.isMemberOfRulingParty" text="Is Member of Ruling Party?"/></label>
		<form:checkbox path="isMemberOfRulingParty" id="isMemberOfRulingParty" cssClass="sCheck"/>
		<form:errors path="isMemberOfRulingParty" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
        <form:input path="fromDate" cssClass="sText datemask" value="24/10/2019"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask" value="23/10/2024"/>
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
	
	<!-- Hidden fields that aid in Client side actions performed in Javascript -->
	<p style="display:none;">
		<select id="partyTypes" class="sSelect">
			<c:forEach items="${parties}" var="i">
				<option value="${i.id}"><c:out value="${i.partyType.type}"></c:out></option>
			</c:forEach>
		</select>
	</p>
	
</form:form>
</div>
</body>
</html>