<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="cutmotion.departmentwise_yaadi_report" text="Departmentwise Yaadi Report"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	function generateDepartmentwiseYaadiReport(selectedSubDepartment){
		var parameters = {
				houseType				: $("#yHouseTypeType").val(),
				sessionYear				: $('#ySessionYear').val(),
				sessionType				: $("#ySessionType").val(),
				sessionId				: $("#ySession").val(),
				subDepartment			: $("#yDepartment").val(),
				cutMotionType			: $("#yDeviceType").val(),
				locale					: $("#yLocale").val(),
				reportQuery				: "CMOIS_YAADI_REPORT"/* + "_" + $("#selectedHouseType").val().toUpperCase()*/,
				xsltFileName			: 'cmois_yaadi_report_template'/* + '_' + $("#selectedHouseType").val()*/,
				outputFormat			: 'PDF',
				reportFileName			: "cmois_yaadi_report"/* + "_" + $("#selectedCutMotionType").val()*/
		}
		form_submit('cutmotion/report/yaadi_report', parameters, 'GET');
	}	
	
	$('document').ready(function(){		
		$('#linkForReport').css('font-size','20px');
		
		$('#linkForReport').click(function(){
			if($('#yDepartment').val()=='0' || $('#yDepartment').val()=='') {
				$.prompt($('#noYaadiDepartmentSelectedPromptMsg').val());
				return false;
			} else {
				generateDepartmentwiseYaadiReport($('#yDepartment').val());
			}			
		});
	});		
	</script>
</head>
<body>
<div class="fields clearfix">
	<div>
		<%-- <h2><spring:message code="cutmotion.departmentwise_yaadi_report" text="Departmentwise Yaadi Report"/></h2> --%>	
		
		<h3>${sessionTypeName} <spring:message code="house.module.session" text="Session"/>, ${formattedSessionYear}</h3>
		
		<p style="margin-top: 30px;margin-bottom: 35px;">
			<label class="small"><spring:message code="cutmotion.department" text="Department"/></label>
			<select id="yDepartment" class="sSelect">
				<option id="pleaseSelectOption" value="0"><spring:message code='please.select' text='Please Select'/></option>
				<c:forEach var="i" items="${allYaadiDepartmentDetails}">
					<option value="${i.value}">${i.name}</option>
				</c:forEach>
			</select>
		</p>
		<p></p>
		<h2></h2>
		<p align="right">
			<a href="#" id="linkForReport">
				<spring:message code="cutmotion.generateYaadiReport" text="Generate Yaadi Report"/>
			</a>
		</p>
	</div>
	
	<input type="hidden" id="yHouseTypeType" value="${houseType}"/>
	<input type="hidden" id="yHouseType" value="${houseTypeId}"/>
	<input type="hidden" id="ySessionYear" value="${sessionYear}"/>
	<input type="hidden" id="ySessionType" value="${sessionType}"/>
	<input type="hidden" id="ySession" value="${sessionId}"/>
	<input type="hidden" id="yDeviceType" value="${deviceType}"/>
	<input type="hidden" id="yLocale" value="${locale}"/>
	<input id="noYaadiDepartmentSelectedPromptMsg" value="<spring:message code='cutmotion.noYaadiDepartmentSelectedPromptMsg' text='Please select department for the yaadi report'/>" type="hidden">
</div>	
</body>
</html>