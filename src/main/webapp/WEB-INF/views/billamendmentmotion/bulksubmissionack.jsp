<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="billamendmentmotion.bulksubmission" text="Bulk Submissions" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		td{min-width:150px; max-width:350px;min-height:30px;}
		th{min-width:150px; max-width:350px;min-height:30px;}
	</style>
	<script type="text/javascript">
	$(document).ready(function(){
		$('.amendedBill').each(function() {
			var amendedBill_TdId = this.id;				
			var amendedBillInfo = $('#'+amendedBill_TdId).html().replace(/\#/g,"~");			
			$.get('ref/billamendmentmotion/amendedBillInfo?amendedBillInfo='+amendedBillInfo, function(data) {
				$('#'+amendedBill_TdId).empty();
				$('#'+amendedBill_TdId).html(data);			
			}).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		});
		
		$('.amendedSection').each(function() {			
			var amendedSection_TdId = this.id;		
			var amendedSectionInfo = $('#sectionNumberLabel').val() + " " + $('#'+amendedSection_TdId).html().split("#")[1];
			$('#'+amendedSection_TdId).html(amendedSectionInfo);
		});
	});
	</script>
</head>
<body>	
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<table class="uiTable">
		<tr>
			<th><spring:message code="billamendmentmotion.number" text="Number"></spring:message></th>
			<th><spring:message code="billamendmentmotion.amendedBill" text="Amended Bill"></spring:message></th>
			<th><spring:message code="billamendmentmotion.amendedSection" text="Amended Section"></spring:message></th>
		</tr>			
		<c:forEach items="${billAmendmentMotions}" var="i" varStatus="cnt">
			<tr>
				<td>${i.formatNumber()}</td>
				<td class="amendedBill" id="amendedBill${cnt.count}" style="min-width:240px !important;">${i.amendedBillInfo}</td>
				<td class="amendedSection" id="amendedSection${cnt.count}">${i.defaultAmendedSectionNumberInfo}</td>
			</tr>
		</c:forEach>
	</table>	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input id="sectionNumberLabel" value="<spring:message code='billamendmentmotion.sectionAmendment.sectionNumber' text='Section Number'></spring:message>" type="hidden">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>