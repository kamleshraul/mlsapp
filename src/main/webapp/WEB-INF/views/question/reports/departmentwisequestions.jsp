<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$('#departmentwisequestions__pdf').click(function() {
				$(this).attr('href', '#');
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&subDepartment="+$("#selectedSubDepartment").val()
				 +"&locale="+$("#moduleLocale").val()
				 +"&report="+"QIS_STARRED_DEPARTMENTWISE_QUESTIONS"
				 +"&reportxsl="+"departmentwisequestions"
				 +"&reportout="+"departmentwisequestions"
				 +"&outputFormat="+"PDF";	
				var resourceURL = 'question/report/departmentwisequestions/export?'+ parameters;
				$(this).attr('href', resourceURL);
			});
			$('#departmentwisequestions__word').click(function() {
				$(this).attr('href', '#');
				var parameters = "houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +"&subDepartment="+$("#selectedSubDepartment").val()
				 +"&locale="+$("#moduleLocale").val()
				 +"&report="+"QIS_STARRED_DEPARTMENTWISE_QUESTIONS"
				 +"&reportxsl="+"departmentwisequestions"
				 +"&reportout="+"departmentwisequestions"
				 +"&outputFormat="+"WORD";	
				var resourceURL = 'question/report/departmentwisequestions/export?'+ parameters;
				$(this).attr('href', resourceURL);
			});
		});
	</script>
	<style type="text/css" media="print">
		th{			
			font-size: 15px !important;
			vertical-align: top;
		}		
		#topHeader{
			font-size: 20px !important;
			font-weight: bold;
		}
	</style>
	<style type="text/css">
		th{
			border-left: 0px;border-right: 0px;
		}
		td{			
			vertical-align: top;
			border: 0px;
		}
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="question.report.notCreated" text="Report not Created"/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="question.report.empty" text="No entry found."/>
</c:when>

<c:otherwise>
<div>
	<a id="departmentwisequestions__pdf" class="exportLink" href="#" style="text-decoration: none;">
		<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
	</a>
	&nbsp;
	<a id="departmentwisequestions__word" class="exportLink" href="#" style="text-decoration: none;">
		<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
	</a>
</div>
<div id="reportDiv" >
	<div style="max-width: 800px; width: 800px; margin-left: 25px;">
		<h3 id="topHeader" style="text-align: center; color: black; text-decoration: underline; font-family: 'Times New Roman';">
			${report[0][1]} ${localisedContent[0]}
		</h3>
		<div style="font-size: 15px;">
			${localisedContent[1]} :- &nbsp;&nbsp;&nbsp;&nbsp;<b>${report[0][2]}</b>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			${localisedContent[2]} :- &nbsp;&nbsp;&nbsp;&nbsp;<b>${report[0][3]}</b>
		</div>
		<div style="margin-top: 10px;font-size: 18px;font-weight: bold;text-decoration: underline;">
			${localisedContent[3]}
		</div>
		<div style="margin-top: 15px;font-size: 18px;font-weight: bold;">
			<label style="text-decoration: underline;">${localisedContent[4]} -</label> &nbsp;&nbsp;&nbsp;${report[0][4]}
		</div>
	</div>
	<br />
	<c:set var="columns" value="0" />	
	<table class="strippedTable" style="margin-left: 25px; font-size: 15px; border-top: 0px;border-right: 0px;border-bottom: 0px;border-left: 0px;">
		<thead>
			<tr>
				<th style="text-align: center;">${localisedContent[5]}</th>
				<th style="text-align: center;">${localisedContent[6]}</th>
				<th>${localisedContent[7]}</th>
				<th>${localisedContent[8]}</th>
			</tr>
			<!-- <tr>
				<th style="font-size: 8px;" colspan="4">&nbsp;</th>
			</tr> -->
		</thead>
		<tbody>
			<c:forEach items="${report}" var="r" varStatus="counter">
				<tr style="border-top: 0px;border-bottom: 0px;">
					<td width="5%">${r[5]}</td>
					<td width="10%" style="text-align: center;">
						${r[6]}												
						<c:if test="${not empty clubbedNumbers}">	
							<br/>											
							${clubbedNumbers[counter.count-1]}				
						</c:if>
					</td>
					<td width="25%">${r[8]}</td>
					<td width="60%">${r[9]}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="selectedHouseType" value="${selectedHouseType}"/>
<input type="hidden" id="selectedSessionType" value="${selectedSessionType}"/>
<input type="hidden" id="selectedSessionYear" value="${selectedSessionYear}"/>
<input type="hidden" id="selectedSubDepartment" value="${selectedSubDepartment}"/>
<input type="hidden" id="locale" value="${locale}"/>
</body>

</html>