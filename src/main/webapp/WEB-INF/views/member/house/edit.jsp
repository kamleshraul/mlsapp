<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.house" text="Member Role Details"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		
	function loadDivisionDistricts(constituency){
	    $.get('ref/divdis/'+constituency,function(data){
		    $("#division").val(data.division);
		    var text="";
		    if(data.districts.length>0){
		    for(var i=0;i<data.districts.length;i++){
			    text=text+"<option value='"+data.districts[i].id+"' selected='selected'>"+data.districts[i].name+"</option>";
		    }
		    $("#districts").empty();
			$("#districts").html(text);
		    }else{
			$("#districts").empty();			    
		    }			
	    },'json');
	}

	function loadConstituencies(houseType){
		$.get('ref/constituencies/'+houseType,function(data){
			var text="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			    text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$("#constituency").empty();
			$("#constituency").html(text);
			loadDivisionDistricts(data[0].id);
			}else{
				$("#constituency").empty();
				$("#division").val("");
				$("#districts").empty();
			}
		},'json');
	}	    
		$(document).ready(function(){	
			$("#constituency").val($("#currentConstituency").val());
			$("#constituency").change(function(){
				loadDivisionDistricts($(this).val());
			});
			if($("#houseType").val()=="lowerhouse"){
				$("#lowerhouselabel").show();
				$("#upperhouselabel").hide();
			}else{
				$("#upperhouselabel").show();
				$("#lowerhouselabel").hide();
			}			
			if($("#isSitting").val() == "true") {
				$("#isSittingSelect").val($("#isSitting").val());
			} else {
				$("#isSittingSelect").val("false");
			}
			$("#isSittingSelect").change(function(){
				$("#isSitting").val($(this).val());
			});
		});
	</script>
</head>

<body>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/house" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${fullname}		
	</h2>
	<h2>
		<spring:message code="member.role" text="Member Role"/>
	</h2>
	<form:errors path="recordIndex" cssClass="validationError" cssStyle="color:red;"/>	
	
	<p>
	<label class="small"><spring:message code="member.house.houseType"	text="House Types" /></label>	
	<input type="text" id="houseTypes" name="houseTypes" class="sText" value="${houseTypeName}" readonly="readonly">										
	</p>
	
	<p>
	<label class="small" id="lowerhouselabel"><spring:message code="generic.lowerhouse" text="Assembly"/></label>
	<label class="small" id="upperhouselabel"><spring:message code="generic.upperhouse" text="Council"/></label>
		<input type="text" id="houseName" name="houseName" value="${houseName}" class="sText" readonly="readonly">
		<input type="hidden" id="house" name="house" value="${houseId}" class="sText">
		<form:errors path="house" cssClass="validationError"/>	
	</p>	

	<p>
		<label class="small"><spring:message code="member.house.role" text="Role"/></label>
		<input type="text" id="roleName" name="roleName" value="${roleName}" class="sText" readonly="readonly">
		<input type="hidden" id="role" name="role" value="${roleId}">
		<form:errors path="role" cssClass="validationError"/>
		<select id="isSittingSelect" name="isSittingSelect" class="sSelect">
		<option value="true"><spring:message code="member.house.sitting" text="Sitting"></spring:message></option>
		<option value="false"><spring:message code="member.house.ex" text="Ex"></spring:message></option>
		</select>
		<form:errors path="isSitting" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.constituencies" text="Constituency"/></label>
		<form:select path="constituency" items="${constituencies}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="constituency" cssClass="validationError"/>		
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.division" text="Division"/></label>
		<input name="division" id="division" type="text" value="${division}" class="sText" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.district" text="District"/></label>
		<c:choose>
		<c:when test="${!(empty districts) }">
		<select name="districts" id="districts" size="5" multiple="multiple">
		<c:forEach items="${districts}" var="i">
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
		</select>
		</c:when>
		<c:otherwise>
		<select name="districts" id="districts"  size="5" multiple="multiple">
		</select>
		</c:otherwise>
		</c:choose>
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
	
	<p>
		<label class="small"><spring:message code="member.house.oathdate" text="Oath Date"/></label>
		<form:input path="oathDate" cssClass="sText datemask"/>
		<form:errors path="oathDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.resignationdate" text="Resignation Date"/></label>
		<form:input path="resignationDate" cssClass="sText datemask"/>
		<form:errors path="resignationDate" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="small"><spring:message code="member.personal.deathDate" text="Death Date"/></label>
		<input type="text" class="sText" id="deathDate" name="deathDate" value="" readonly="readonly"/>
	</p>
	
	<p>
		<label class="small"><spring:message code="member.house.remarks" text="Remarks"/></label>
		<form:textarea path="remarks" cssClass="wysiwyg" rows="5" cols="50"/>
		<form:errors path="remarks" cssClass="validationError"/>	
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">			
        </p>
	</div>
	<form:hidden path="recordIndex"/>
	<form:hidden path="version"/>
	<form:hidden path="locale"/>			
	<input id="member" name="member" value="${member}" type="hidden">
	<input id="houseType" name="houseType" value="${houseType}" type="hidden">
	<input id="currentConstituency" name="currentConstituency" value="${currentConstituency}" type="hidden">
	<form:hidden path="isSitting"/>
	
</form:form>
</div>
</body>
</html>