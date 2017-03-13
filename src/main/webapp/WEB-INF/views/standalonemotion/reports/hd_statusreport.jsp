<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.hdq.statusreport" text="Statuswise Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
						
		});
		
		
	</script>
	 <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 800px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	margin-top: 10px !important;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 0px 0px 0px 15px !important;
	        } 
	        
	        hr{
	        	display: none !important;
	        }    
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        #loadMore{
        	background: #00FF00 scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			/*border-radius: 10px;*/
			text-align: center;
			border: 1px solid black;
			z-index: 5000;
			bottom: 5px;
			right: 50px;			
			position: fixed;
			cursor: pointer;
        }
                
    </style>
</head> 

<body>

	<div id="reportDiv">
			<div style="width: 750px; text-align: center; font-size: 16px;">
				<spring:message code="question.hdq.report.status.vivran" text="Vivran Number" />---<br>
				<spring:message code="question.hdq.report.status.state" text="Maharashtra " />&nbsp;${headerStats[0][0]}<br>
				<spring:message code="question.hdq.report.status.device" text="Halfhour Discussion" /><br>
				<c:choose>
					<c:when test="${headerStats[0][1]=='lowerhouse'}">
						<spring:message code="question.hdq.report.status.rule.lowerhouse" text="Halfhour Discussion Rule" />
					</c:when>
				</c:choose>
				<br>
				${headerStats[0][2]}
			</div>
			<br>
			<div style="width: 750px;">
				<table align="center" style="width: 550px; border: 2px solid black;" border="1" class="strippedTable">
					<tr>
						<td style="width: 400px">
							<spring:message code="question.hdq.report.status.totalreceived" text="Total Received Questions" />
						</td>
						<td style="width: 150px; text-align: center;">
							${headerStats[0][3]}
						</td>
					</tr>
					<tr>
						<td style="width: 400px">
							<spring:message code="question.hdq.report.status.totaladmission" text="Total Admitted Questions" />
						</td>
						<td style="width: 150px; text-align: center;">
							${headerStats[0][4]}
						</td>
					</tr>
					<tr>
						<td style="width: 400px">
							<spring:message code="question.hdq.report.status.totaldiscussed" text="Total Discussed Questions" />
						</td>
						<td style="width: 150px; text-align: center;">
							${headerStats[0][5]}
						</td>
					</tr>
					<tr>
						<td style="width: 400px">
							<spring:message code="question.hdq.report.status.totalrejection" text="Total Rejected Questions" />
						</td>
						<td style="width: 150px; text-align: center;">
							${headerStats[0][6]}
						</td>
					</tr>
					<tr>
						<td style="width: 400px">
							<spring:message code="question.hdq.report.status.totaltobediscussed" text="Total To Be Discussed Questions" />
						</td>
						<td style="width: 150px; text-align: center;">
							${headerStats[0][7]}
						</td>
					</tr>
				</table>
			</div>
			<br><br>
			<c:if test="${showStats=='1'}">
				<div style="width: 750px;">
					<table id="mainReportTab" style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							<tr>
								<th style="width: 30px; text-align: center;"><spring:message code="question.hdq.report.status.srno" text="Sr. No." /></th>
								<th style="width: 80px; text-align: center;"><spring:message code="question.hdq.report.status.hdqNumber" text="HDQ Number" /></th>
								<%-- <th style="width: 80px; text-align: center;"><spring:message code="question.hdq.report.status.questionNumber" text="Question Number" /></th> --%>
								<th style="width: 80px; text-align: center;"><spring:message code="question.hdq.report.status.memberName" text="Member" /></th>
								<th style="width: 300px; text-align: center;"><spring:message code="question.hdq.report.status.subject" text="Subject" /></th>
								<th style="width: 100px; text-align: center;"><spring:message code="question.hdq.report.status.status" text="Status" /></th>
								<th style="width: 80px; text-align: center;"><spring:message code="question.hdq.report.status.discussionDate" text="Discussion Date" /></th>
								<th style="width: 80px; text-align: center;"><spring:message code="question.hdq.report.status.ballotDate" text="Ballot Date" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<tr>
									<td style="width: 30px; text-align: center;">${formater.formatNumberNoGrouping(counter.count,locale)}</td>
									<td style="width: 80px; text-align: center;">${r[0]}</td>
									<%-- <td style="width: 80px; text-align: center;">${r[1]}</td> --%>
									<td style="width: 80px; text-align: center;">${r[2]}</td>
									<td style="width: 300px; text-align: center;">${formater.formatNumbersInGivenText(r[3], locale)}</td>
									<td style="font-weight: bold; width: 100px; text-align: center;">${r[4]}</td>
									<td style="width: 80px; text-align: center;">${r[5]}</td>
									<td style="width: 80px; text-align: center;">${r[6]}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:if>
	</div>
</body>
</html>