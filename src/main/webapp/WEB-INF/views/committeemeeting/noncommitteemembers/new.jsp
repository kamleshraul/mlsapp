<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="noncommitteemembers" text="non committe emembers"/>
	</title>	
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">	
	

	

	var noncommitteemembersCount = parseInt($('#noncommitteememberSize').val());
	var totalnoncommitteemembersCount = 0;
	totalnoncommitteemembersCount = totalnoncommitteemembersCount + noncommitteemembersCount;
	function addNonCommitteeMemberInfoFunction() {
		noncommitteemembersCount = noncommitteemembersCount + 1;
		totalnoncommitteemembersCount = totalnoncommitteemembersCount + 1;

		var text = "<div id='noncommitteemembersInfo" + noncommitteemembersCount + "'>" +
		    "<p>" +
  		  	"<label class='small'>" + $('#nonCommitteeMemberMessage').val() + "*</label>" +
  		  	"<input name='noncommitteemember" + noncommitteemembersCount + "' id='noncommitteemember" + noncommitteemembersCount + "' class='sText'>" +
  		    "</p>" +
  		 	
  		  	"<input type='button' class='button' id='" + noncommitteemembersCount + "' value='" + $('#deletenonCommitteeMemberInfoMessage').val() + "' onclick='deleteNonCommitteeMemberInfo(" + noncommitteemembersCount + ");'>" +
		  	"<input type='hidden' id='noncommitteemembersInfoId" + noncommitteemembersCount + "' name='noncommitteemembersInfoId" + noncommitteemembersCount +"'>" +
		  	"<input type='hidden' id='noncommitteemembersInfoLocale" + noncommitteemembersCount + "' name='noncommitteemembersInfoLocale" + noncommitteemembersCount + "' value='" + $('#locale').val() +"'>" +
		  	"<input type='hidden' id='noncommitteemembersInfoVersion" + noncommitteemembersCount + "' name='noncommitteemembersInfoVersion" + noncommitteemembersCount + "'>" +
		  	"</div>"; 

		var prevCount = noncommitteemembersCount - 1;
		if(totalnoncommitteemembersCount == 1){
			$('#addNonCommitteeMemberInfo').after(text);
		} else{
			$('#noncommitteemembersInfo'+ prevCount).after(text);
		}
		$('#noncommitteememberSize').val(noncommitteemembersCount); 

		// To apply datemask to the date fields
		$('.datemask').focus(function(){
			if($(this).val() == ""){
				$(".datemask").mask("99/99/9999");
			}
		});

		// To apply timemask to the time fields
		$('.timemask').focus(function(){
			if($(this).val() == ""){
				$(".timemask").mask("99:99:99");
			}
		});
	}

	function deleteNonCommitteeMemberInfo(id) {
		var noncommitteemembersInfoId = $('#noncommitteemembersInfoId' + id).val();
		if(noncommitteemembersInfoId != ''){			
	    	$.delete_('committeemeeting/noncommitteemembers/info/' + noncommitteemembersInfoId + '/delete', 
	    	    null, 
	    	    function(data, textStatus, XMLHttpRequest) {
	    			$('#noncommitteemembersInfo' + id).remove();
	    			noncommitteemembersCount = noncommitteemembersCount - 1;
					if(id == noncommitteemembersCount) {
						noncommitteemembersCount = noncommitteemembersCount - 1;
					}
	    		}
    		).fail(function() {
				if($("#ErrorMsg").val() != '') {
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}
				else {
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		} 
		else {
			$('#noncommitteemembersInfo'+id).remove();
			totalnoncommitteemembersCount = totalnoncommitteemembersCount - 1;
			if(id == noncommitteemembersCount) {
				noncommitteemembersCount = noncommitteemembersCount - 1;
			}
		}	
	}
	
	$('document').ready(function(){	
		initControls();
		$('#key').val('');

		$('#addNonCommitteeMemberInfo').click(function(){
			addNonCommitteeMemberInfoFunction();
		});
		
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
<form:form action="committeemeeting/noncommitteemembers" method="POST" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;<spring:message code="generic.new" text="New"></spring:message>]
	</h2>
	
	<form:errors path="version" cssClass="validationError"/>
	
	<p>
		<label class="small"><spring:message code="generic.department" text="Department Name"/>*</label>
		<form:input path="departmentName" cssClass="sText"/>
	</p>
		<p>
		<label class="small"><spring:message code="nonCommitteeMemberTypes" text="Non Committee members Type" /></label>
		<select id="nonCommitteeMemberTypes" name="nonCommitteeMemberTypes" class="sSelect">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${nonCommitteeMemberTypes}" var="i">
				<c:choose>
					<c:when test="${nonCommitteeMemberType.id == i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	
	<!-- Dynamic Addition of Itinerary -->
	<div>
		<input type="button" id="addNonCommitteeMemberInfo" class="button" value="<spring:message code='noncommitteemember.addMemberInfo' text='Add Member Information'></spring:message>">
		<form:errors path="noncommiteememberinformation" cssClass="validationError"></form:errors>
		<c:if test="${not empty noncommitteemembers}">
			<c:set var="noncommitteemembersCount" value="1"></c:set>
			<c:forEach items="${noncommitteemembers}" var="outer">
				<div id="noncommitteemembersInfo${noncommitteemembersCount}">
					<p>
						<label class="small"><spring:message code="Memberinfo.noncommitteemember" text="Member"/>*</label>
						<input id="noncommitteemember${noncommitteemembersCount}" name="noncommitteemember${noncommitteemembersCount}" class="sText" value="${outer.noncommitteemember}">
					</p>
					
					
					<input type='button' id='${noncommitteemembersCount}' class='button' value='<spring:message code="noncommitteememberinfo.delete" text="Delete Member Info"></spring:message>' onclick='noncommitteememberInfo(${noncommitteemembersCount});'/>
					
					<!-- Hidden variables required for each instance of TourItinerary -->
					<input type='hidden' id='noncommitteemembersInfoId${noncommitteemembersCount}' name='noncommitteemembersInfoId${noncommitteemembersCount}' value="${outer.id}">
					<input type='hidden' id='noncommitteemembersInfoVersion${noncommitteemembersCount}' name='noncommitteemembersInfoVersion${noncommitteemembersCount}' value="${outer.version}">
					<input type='hidden' id='noncommitteemembersInfoLocale${noncommitteemembersCount}' name='noncommitteemembersInfoVersion${noncommitteemembersCount}' value="${domain.locale}">
				</div>
				<c:set var="noncommitteemembersCount" value="${noncommitteemembersCount + 1}"></c:set>
			</c:forEach>		
		</c:if>
		
		<!-- Hidden Messages to preserve the localization of the field names -->
		<input type="hidden" id="noncommitteememberSize" name="noncommitteememberSize" value="${noncommitteememberSize}"/>
		<input type="hidden" id="committeeMeetingId" name="committeeMeetingId" value="${committeeMeetingId}">
		<input type="hidden" id="deletenonCommitteeMemberInfoMessage" name="deletenonCommitteeMemberInfoMessage" value="<spring:message code='noncommitteemember.info.delete' text='Delete Member Info'></spring:message>" disabled="disabled"/>
		
		<input type="hidden" id="nonCommitteeMemberMessage" name="nonCommitteeMemberMessage" value="<spring:message code='noncommitteememberinfo.noncommitteemember' text='Member'></spring:message>" disabled="disabled"/>
	</div>
	
	
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
	<input type="hidden" name="houseType" id="houseType" value="${houseTypeId}">
</form:form>
</div>
</body>
</html>