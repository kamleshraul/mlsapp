<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$(document).ready(function() {			
		$('#selectedParty').change(function() {
			generatePartywiseQuestionsCountReportInHTML($(this).val());			
		});
	});
	
	function generatePartywiseQuestionsCountReportInHTML(party) {
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(party!=undefined && party!="") {
			var parameters="sessionId="+$("#session").val()
			+"&questionTypeId="+$("#questionType").val()
			+"&partyId="+party
			+"&locale="+$("#locale").val()
			+"&reportQuery="+$("#questionTypeType").val().toUpperCase()+"_PARTYWISE_MEMBERWISE_STATUS_COUNTS"
			+"&reportFileName=partywise_questions_count_report";
			var resource='question/report/partywise_questions_count_report';
			$.get(resource+'?'+parameters,function(data){
				$("#reportDataDiv").empty();	
				$("#reportDataDiv").html(data);
				$.unblockUI();		
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.");
				}
				scrollTop();
			});
		} else {
			$("#reportDataDiv").empty();
			$.unblockUI();
		}
		$("#errorDiv").hide();
		$("#successDiv").hide();				
	}
</script>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<p>
	<label><spring:message code="partywise_questions_count_report.party" text="Party" /></label>
	<select id="selectedParty" name="selectedParty">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${partyList}" var="i">
			<option value="${i.id }"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
</p>
<div id="reportDataDiv">
</div>
<input type="hidden" id="session" value="${session }">
<input type="hidden" id="questionType" value="${questionType}">
<input type="hidden" id="questionTypeType" value="${questionTypeType}">
<input type="hidden" id="locale" value="${locale}">
<input type="hidden" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>">
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="allSelected" value="<spring:message code='generic.allSelected' text='All Selected'/>">
</body>
</html>