<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$("#vivranBackDiv").click(function(){
				showVivranReport();
			});
			
			$("#vivranBackDiv").mouseover(function(){
				$(this).css('cursor','pointer');
			});
			
			$("#vivranBackDiv").mouseout(function(){
				$(this).css('cursor','default');
			});
		});	
		
		
		function drillDownData(session, group, questionDate, value, reportType){
			if(value>0){
				var url = "question/report/generalreport?deviceTypeId="+$("#selectedQuestionType").val()+
							"&reportStatus="+reportType + "&report=QIS_VIVRAN_DRILL_DOWN_REPORT" + 
							"&houseType="+$("#selectedHouseType").val() +
							"&questionDate="+questionDate;
				//var devicetype = $("#deviceTypeMaster option[value='" + $("#selectedQuestionType").val() + "']").text();
				/* if(devicetype=='questions_starred'){
					url += "&report=QIS_STARRED_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_unstarred'){
					url += "&report=QIS_UNSTARRED_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_shortnotice'){
					url += "&report=QIS_SN_DEPARTMENT_SESSION_REPORT";
				}else if(devicetype=='questions_halfhourdiscussion_from_question' || devicetype=='questions_halfhourdiscussion_standalone'){
					url += "&report=QIS_HD_DEPARTMENT_SESSION_REPORT";
				} */
				
				var urlSession = "ref/sessionbyhousetype/"+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val();
				$.get(urlSession,function(data){
					if(data){
						url += '&sessionId='+data.id+'&locale='+$("#moduleLocale").val();
						if(reportType=='discussed'){
							url += '&reportout=vivrandiscussedreport';
						}else if(reportType=='timeshortage'){
							url += '&reportout=vivrantimeshortagereport';
						}else if(reportType=='kept'){
							url += '&reportout=vivrankeptreport';
						}else if(reportType=='memberabsence'){
							url += '&reportout=vivranmemberabsencereport';
						}else if(reportType=='ministerabsence'){
							url += '&reportout=vivranministerabsencereport';
						}
						showTabByIdAndUrl('details_tab', url);
					}
				});
			}
		}
	</script>
	<style type="text/css">
		th{
			text-align: center;
		}
		
		td{
			text-align: center;
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
<div style="float: right; width: 80px; text-align: center;" id="vivranBackDiv">
	<a href="javascript:void(0);" id="vivranBack" style="text-decoration: none; color: #000;">
		<img class="imgN" src="./resources/images/back2D.png" alt="Back" height="20px" title="<spring:message code='generic.back' text='Back' />" />
	</a>
</div>
<div id="reportDiv" >
	<div style="text-align: center; max-width: 800px; width: 800px; margin-left: 25px;">
		<h2 style="color: black; text-decoration: underline; font-family: 'Times New Roman';">
			${report[0][9]}
		</h2>
	</div>
	<br />
	<c:set var="columns" value="0" />
	<table class="strippedTable" border="1" style="margin-left: 25px; font-size: 15px;">
		<thead>
			<tr>
				<c:forEach items="${topHeader}" var="h" varStatus="headCols">
					<th>${h}</th>
					<c:set var="columns" value="${headCols.count}" />
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${report}" var="r" varStatus="counter">
				<tr>
					<td style="width: 60px;">${formater.formatNumberNoGrouping(counter.count, locale)}</td>
					<td style="width: 80px;"><a href="javascript:void(0);" onclick="editQ(${r[1]})" style="text-decoration: none; color: #000;">${r[2]}</a></td>
					<td style="width: 100px;">${r[3]}</td>
					<td style="width: 300px;">${formater.formatNumbersInGivenText(r[4], locale)}</td>
					<td style="width: 140px;">${r[5]}</td>	
					<td style="width: 60px;">${r[7]}</td>
					<td style="width: 60px;">${r[6]}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>