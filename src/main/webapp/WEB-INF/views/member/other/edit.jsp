<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="member.other" text="Member Other Details"/>
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
	var positionCount=parseInt($('#positionCount').val());
	var totalPositionCount=0;
	totalPositionCount=totalPositionCount+positionCount;
	function addPosition(){
		positionCount=positionCount+1;
		totalPositionCount=totalPositionCount+1;
		var text="<div id='position"+positionCount+"'>"+
				  "<p>"+
	    		  "<label class='small'>"+$('#fromDatePositionMessage').val()+"</label>"+
	    		  "<input name='positionFromDate"+positionCount+"' id='positionFromDate"+positionCount+"' class='sText'>"+
	    		  "</p>"+
	    		  "<p>"+
	    		  "<label class='small'>"+$('#toDatePositionMessage').val()+"</label>"+
	    		  "<input name='positionToDate"+positionCount+"' id='positionToDate"+positionCount+"' class='sText'>"+
	    		  "</p>"+
	    		  "<p>"+
	    		  "<label class='small'>"+$('#positionPositionMessage').val()+"</label>"+
	    		  "<textarea name='positionPosition"+positionCount+"' id='positionPosition"+positionCount+"' class='sText' rows='5' cols='50'></textarea>"+
	    		  "</p>"+
				      "<input type='button' class='button' id='"+positionCount+"' value='"+$('#deletePositionMessage').val()+"' onclick='deletePosition("+positionCount+");'>"+
					  "<input type='hidden' id='positionId"+positionCount+"' name='positionId"+positionCount+"'>"+
					  "<input type='hidden' id='positionLocale"+positionCount+"' name='positionLocale"+positionCount+"' value='"+$('#locale').val()+"'>"+
					  "<input type='hidden' id='positionVersion"+positionCount+"' name='positionVersion"+positionCount+"'>"+
				      "</div>"; 
				      var prevCount=positionCount-1;
				      if(totalPositionCount==1){
					   $('#addPosition').after(text);
					    }else{
				      $('#position'+prevCount).after(text);
				      }
				      $('#positionCount').val(positionCount); 				
	}
	function deletePosition(id){
		var positionId=$('#positionId'+id).val();
		if(positionId != ''){			
	    $.delete_('member/other/position/'+positionId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
	    	$('#position'+id).remove();
			totalPositionCount=totalPositionCount-1;
			if(id==positionCount){
				positionCount=positionCount-1;
			}
	    }).fail(function(){
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
		}	else{
			$('#position'+id).remove();
			totalPositionCount=totalPositionCount-1;
			if(id==positionCount){
				positionCount=positionCount-1;
			}
		}		
	}	
		$(document).ready(function(){
			$('#addPosition').click(function(){
				addPosition();
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
<form:form action="member/other" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="member.new.heading" text="Enter Details"/>:&nbsp;
		${domain.title.name} ${domain.firstName} ${domain.middleName} ${domain.lastName}
	</h2>
	<form:errors path="version" cssClass="validationError" cssStyle="color:red;"/>
	<div>
	<input type="button" class="button" id="addPosition" value="<spring:message code='member.other.addPosition' text='Add Position'></spring:message>" style="display:none;">
	<input type="hidden" id="positionCount" name="positionCount" value="${positionCount}"/>
	
	<input type="hidden" id="deletePositionMessage" name="deletePositionMessage" value="<spring:message code='member.other.deletePosition' text='Delete Position'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="fromDatePositionMessage" name="fromDatePositionMessage" value="<spring:message code='member.other.fromYear' text='From Year'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="toDatePositionMessage" name="toDatePositionMessage" value="<spring:message code='member.other.toYear' text='To Year'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="positionPositionMessage" name="positionPositionMessage" value="<spring:message code='member.other.positionPosition' text='Position'></spring:message>" disabled="disabled"/>
	
	<form:errors path="positionsHeld" cssClass="validationError"></form:errors>
	<c:if test="${!(empty positions)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${positions}" var="outer">
	<div id="position${count}">
	<p>
	    <label class="small"><spring:message code="member.other.fromYear" text="From Year"/></label>
		<input name="positionFromDate${count}" id="positionFromDate${count}" class="sText" value="${outer.fromDate}">
	</p>
	<p>
	    <label class="small"><spring:message code="member.other.toYear" text="To Year"/></label>
		<input name="positionToDate${count}" id="positionToDate${count}" class="sText" value="${outer.toDate}">
	</p>
	<p>
	    <label class="small"><spring:message code="member.other.positionPosition" text="Position"/></label>
		<textarea name="positionPosition${count}" id="positionPosition${count}" class="sText" rows="5" cols="50">${outer.position}</textarea>
	</p>
	<input type='button' class='button' id='${count}' value='<spring:message code="member.other.deletePosition" text="Delete Position"></spring:message>' onclick='deletePosition(${count});'>
	<input type='hidden' id='positionId${count}' name='positionId${count}' value="${outer.id}">
	<input type='hidden' id='positionLocale${count}' name='positionLocale${count}' value="${domain.locale}">
	<input type='hidden' id='positionVersion${count}' name='positionVersion${count}' value="${outer.version}">
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>
	</div>				
	<p style="display: none;">
		<label class="labelcentered"><spring:message code="member.other.socialCulturalActivities" text="Social Activities"/></label>
		<form:textarea path="socialCulturalActivities" cssClass="sTextarea" cols="50" rows="5"></form:textarea>
		<form:errors path="socialCulturalActivities" cssClass="validationError"/>	
	</p>
	
	<p style="display: none;">
		<label class="labelcentered"><spring:message code="member.other.educationalCulturalActivities" text="Educational and Cultural Activities"/></label>
		<form:textarea path="educationalCulturalActivities" cssClass="sTextarea" cols="50" rows="5"></form:textarea>
		<form:errors path="educationalCulturalActivities" cssClass="validationError"/>	
	</p>
	<p>
		<label class="labelcentered"><spring:message code="member.other.literaryArtisticScientificAccomplishments" text="Literary,Artistic and Scientific Accomplishments"/></label>
		<form:textarea path="literaryArtisticScientificAccomplishments" cssClass="sTextarea" cols="50" rows="5"></form:textarea>
		<form:errors path="literaryArtisticScientificAccomplishments" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.other.otherInformation" text="Other Information"/></label>
		<form:textarea path="otherInformation" cssClass="wysiwyg" cols="50" rows="5"></form:textarea>
		<form:errors path="otherInformation" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.other.hobbySpecialInterests" text="Hobby and Special Interests"/></label>
		<form:textarea path="hobbySpecialInterests" cssClass="wysiwyg" cols="50" rows="5"></form:textarea>
		<form:errors path="hobbySpecialInterests" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.other.countriesVisited" text="Countries Visited"/></label>
		<form:textarea path="countriesVisited" cssClass="wysiwyg" cols="50" rows="5"></form:textarea>
		<form:errors path="countriesVisited" cssClass="validationError"/>	
	</p>
	<p>
		<label class="small"><spring:message code="member.other.publications" text="Publications"/></label>
		<form:textarea path="publications" cssClass="wysiwyg" cols="50" rows="5"></form:textarea>
		<form:errors path="publications" cssClass="validationError"/>	
	</p>		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
			
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>	
	<input type="hidden" id="houseType" name="houseType" value="${houseType}">
</form:form>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
