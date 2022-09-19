<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.house" text="Member Role Details"/>
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
	    },'json').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function loadConstituencies(houseType){
		var target = $('#constituency option:selected').val();
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
		},'json').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

	function loadRoles(houseType){
		$.get('ref/memberroles/'+houseType,function(data){
			var text="";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			    text=text+"<option value='"+data[i].id+"'>"+data[i].name+"</option>";
			}
			$("#role").empty();
			$("#role").html(text);
			}else{
				$("#role").empty();
			}
			loadConstituencies(houseType);			
		},'json').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}

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
			loadRoles(houseType);			
		},'json').fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	
	function prependOptionToConstituency() {
		var isDeviceTypeFieldEmpty = $('#newConstituencySelect').val();
		var optionValue = $('#pleaseSelectOption').val();
		if(isDeviceTypeFieldEmpty == 'true') {
			var option = "<option value='0' selected>" + optionValue + "</option>";
			$('#constituency').prepend(option);
		}
		else {
			var option = "<option value='0'>" + optionValue + "</option>";
			$('#constituency').prepend(option);	
		}
	}
	

	    
		$(document).ready(function(){	
			
			$("#constituency").val($("#currentConstituency").val()); 
			console.log($("#constituency").val()+"2nd line");
			prependOptionToConstituency()
			$("#houseTypes").val($("#selectedhouseType").val());
			
			$("#constituency").change(function(){
				console.log($(this).val()+"consti");
				loadDivisionDistricts($(this).val());
			});
			if($("#selectedhouseType").val()=="lowerhouse"){
				$("#lowerhouselabel").show();
				$("#upperhouselabel").hide();
			}else{
				$("#upperhouselabel").show();
				$("#lowerhouselabel").hide();	
			}
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
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark" style="background-image: url('/els/resources/images/${houseType}.jpg');">
<form:form action="member/house" method="POST" modelAttribute="domain">
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
		<label class="small"><spring:message code="member.house.role" text="Role"/></label>
		<form:select path="role" items="${roles}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
		<form:errors path="role" cssClass="validationError"/>
		<select id="isSittingSelect" name="isSittingSelect" class="sSelect">
		<option value="true"><spring:message code="member.house.sitting" text="Sitting"></spring:message></option>
		<option value="false"><spring:message code="member.house.ex" text="Ex"></spring:message></option>
		</select>
		<form:errors path="isSitting" cssClass="validationError"/>	
	</p>
		
	<p>
		<label class="small"><spring:message code="member.house.constituencies" text="Constituency"/></label>
		<form:select path="constituency" name="constituency" items="${constituencies}" itemLabel="name" itemValue="id" cssClass="sSelect"/>
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
		<select name="districts" id="districts"  size="5" multiple="multiple">
		<c:forEach items="${districts}" var="i">
		<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
		</select>
		</c:when>
		<c:otherwise>
		<select name="districts" id="districts" size="5" multiple="multiple">
		</select>
		</c:otherwise>
		</c:choose>
	</p>
				
	<p>
		<label class="small"><spring:message code="generic.fromDate" text="From Date"/></label>
		<form:input path="fromDate" cssClass="sText datemask" value="13/11/2019"/>
		<form:errors path="fromDate" cssClass="validationError"/>	
	</p>
	
	<p>
		<label class="small"><spring:message code="generic.toDate" text="To Date"/></label>
		<form:input path="toDate" cssClass="sText datemask" value="12/11/2024"/>
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
		<input type="text" class="sText" id="deathDate" name="deathDate" value="${deathdate}" readonly="readonly"/>
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
	<input id="selectedhouseType" name="selectedhouseType" value="${houseType}" type="hidden">
	<input id="currentConstituency" name="currentConstituency" value="0" type="hidden">
	<form:hidden path="isSitting"/>
    <input id="houseType" name="houseType" value="${houseType}" type="hidden">
	
	<input type="hidden" id="newConstituencySelect" name="newConstituencySelect" value="${newConstituencySelect}">
	<input type="hidden" id="pleaseSelectOption" name="pleaseSelectOption" value="<spring:message code='client.prompt.selectForDropdown' text='----Please Select----'></spring:message>">
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>