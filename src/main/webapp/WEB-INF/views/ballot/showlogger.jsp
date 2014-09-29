<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				
			});
		</script>
		<style type="text/css" media="screen">
			.memberName{
				min-width: 170px;
				width: 300px;
				text-align: left;
				padding: 5px;
				font-weight: bold;
			}
			.questionNumber{
				width: 50px;
				max-width: 105px;
				text-align: center;
				padding: 5px;
				font-weight: bold;
			}	
			.round{
				width: 50px;
				max-width: 105px;
				text-align: center;
				padding: 5px;
				font-weight: bold;
			}
			.td{
				text-align: center;
				padding: 5px;
			}			
		</style>
		<style type="text/css" media="print">
		table.strippedTable {width: 780px;}
			.memberName, .td, th, p#header, table#footer {font-size: 20px !important;}	
			.memberName {font-weight: bold;}		
			table#footer {width: 725px !important;}
		</style>
		<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
	</head>	
	<body>
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<c:choose>
			<c:when test="${data == null}">
				<spring:message code="question.log.notCreated" text="Log is not Created"/>
			</c:when>
			
			<c:when test="${empty data}">
				<spring:message code="question.log.noEntries" text="There are no entries in the log"/>
			</c:when>
			
			<c:otherwise>
				<div id="reportDiv" >
					<table class="strippedTable" style="width: 750px !important;">
						<thead>
							<tr>
								<th colspan="2" style="text-align: center; font-size: 14px; font-weight: bold;">
									<spring:message code="log.ballotview" text="Ballot View Log"/>
								</th>
							</tr>
							<tr>
								<th>
									<spring:message code="log.users" text="User"/>
								</th>
								<th>
									<spring:message code="log.tine" text="time"/>
								</th>
							</tr>
						</thead>
						<c:forEach items="${data}" var="d">
							<tr>
								<td>${d.name}</td>
								<td>${d.value}</td>			
							</tr>
						</c:forEach>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	</body>
</html>