<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.statreport" text="Statistics Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
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
	        	width: 800px;
	        	padding: 10px;
	        }	        
	        
	        table{
	        	border: 1px solid black;
	        	width: 100%;
	        }
	        
	        @page {
			  size: auto;
			  margin: 20px 20px 20px 30px;
			}
			
			div#statsReportDiv{
				margin-left: 120px;
			}
			
			div#statHeaderDiv{
				margin-left: 110px;
			}
			
			div#typistDiv{
				margin: 50px 10px 10px 100px;
				width: 850px;				
			}
			
			div#typistDiv h2{
				width: 100%;
				text-align: center;
			}
			
			div#typistDiv table thead {display: table-header-group; margin-top: 50px;}
			
			div#rep8NoData{
				display: none;
			}
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        table#reportTable th{
        	text-align: center;
        	font-weight: bold;
        	font-size: 14px;
        }
        
        table#reportTable td{
        	text-align: center;
        	font-weight: bold;
        	font-size: 13px;
        	height: 50px;
        }
        
        table#reportTypistTable th{
        	text-align: center;
        }
        
        table#reportTypistTable td{
        	font-size: 13px;
        }
        
        table#reportTypistTable .center{
        	text-align: center;
        }
        
        table#reportTable .finalTotal{
        	height: 20px !important;
        }        
        
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="statHeaderDiv">
		<h2 align="center" style="color: black !important; margin-left: 50px;">
			${day}, <spring:message code="generic.date" text="date"/>&nbsp;${currDate}&nbsp;<spring:message code="qis.report.stat.tilldate" text="till date"/>
			<c:choose>
				<c:when test="${statsHouseType=='lowerhouse'}">&nbsp;<spring:message code="qis.report.stat.branch.lowerhouse" text="branch"/>&nbsp;</c:when>
		 		<c:when test="${statsHouseType=='upperhouse'}">&nbsp;<spring:message code="qis.report.stat.branch.upperhouse" text="branch"/>&nbsp;</c:when>
		 	</c:choose>
		 	<spring:message code="qis.report.stat.head1" text="stat header1"/>
		</h2>
		<h2 align="center" style="color: black !important; margin-left: 20px;">
			<c:choose>
				<c:when test="${statsHouseType=='upperhouse' and submissionBatch=='batch-1'}">&nbsp;<spring:message code="qis.report.stat.head2.batch1" text="stat header2"/>&nbsp;</c:when>
		 		<c:when test="${statsHouseType=='upperhouse' and submissionBatch=='batch-2'}">&nbsp;<spring:message code="qis.report.stat.head2.batch2" text="stat header2"/>&nbsp;</c:when>
		 		<c:otherwise>&nbsp;<spring:message code="qis.report.stat.head2" text="stat header2"/></c:otherwise>
		 	</c:choose>
		</h2>
		<br />	
	</div>
	<div id="statsReportDiv">
		<table border="1" id="reportTable" class="strippedTable">
			<thead>
				<tr>
					<th style="width: 82px; padding: 2px;"><spring:message code="qis.report.stat.col1" text="Column 1"/></th>
					<th style="width: 80px; padding: 2px;"><spring:message code="qis.report.stat.col2" text="Column 2"/></th>
					<th style="width: 80px; padding: 2px;"><spring:message code="qis.report.stat.col3" text="Column 3"/></th>
					<%-- <th style="width: 80px; padding: 2px;"><spring:message code="qis.report.stat.col4" text="Column 4"/></th> --%>
					<th style="width: 80px; padding: 2px;"><spring:message code="qis.report.stat.col5" text="Column 5"/></th>
					<th style="width: 90px; padding: 2px;"><spring:message code="qis.report.stat.col6" text="Column 6"/></th>
					<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col7" text="Column 7"/></th>
					<th style="width: 90px; padding: 2px;"><spring:message code="qis.report.stat.col8" text="Column 8"/></th>
					<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col9" text="Column 9"/></th>
					<c:choose>
						<c:when test="${statsHouseType=='lowerhouse'}">
							<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col10.lowerhouse" text="Column 10"/></th>
							<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col11.lowerhouse" text="Column 11"/></th>
						</c:when>
						<c:when test="${statsHouseType=='upperhouse'}">
							<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col10.upperhouse" text="Column 10"/></th>
							<th style="width: 95px; padding: 2px;"><spring:message code="qis.report.stat.col11.upperhouse" text="Column 11"/></th>
						</c:when>
					</c:choose>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${report}" var="r" varStatus="counter">
					<tr>
						<td>
							<c:choose>
								<c:when test="${empty r[1]}">
									-
								</c:when>
								<c:otherwise>
									${r[1]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[2]}">
									-
								</c:when>
								<c:otherwise>
									${r[2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[3]}">
									-
								</c:when>
								<c:otherwise>
									${r[3]}
								</c:otherwise>
							</c:choose>
						</td>
						<%-- <td>
							<c:choose>
								<c:when test="${empty r[4]}">
									-
								</c:when>
								<c:otherwise>
									${r[4]}
								</c:otherwise>
							</c:choose>
						</td> --%>
						<td>
							<c:choose>
								<c:when test="${empty r[5]}">
									-
								</c:when>
								<c:otherwise>
									${r[5]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[6]}">
									-
								</c:when>
								<c:otherwise>
									${r[6]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[7]}">
									-
								</c:when>
								<c:otherwise>
									${r[7]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[8]}">
									-
								</c:when>
								<c:otherwise>
									${r[8]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[9]}">
									-
								</c:when>
								<c:otherwise>
									${r[9]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[10]}">
									-
								</c:when>
								<c:otherwise>
									${r[10]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty r[11]}">
									-
								</c:when>
								<c:otherwise>
									${r[11]}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td class="finalTotal">
						<spring:message code="qis.report.stat.total" text="Total" />
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][12]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][12]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][13]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][13]}
							</c:otherwise>
						</c:choose>
					</td>
					<%-- <td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][14]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][14]}
							</c:otherwise>
						</c:choose>
					</td> --%>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][15]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][15]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][16]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][16]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][17]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][17]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][18]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][18]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][19]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][19]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][20]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][20]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report[0][21]}">
								-
							</c:when>
							<c:otherwise>
								${report[0][21]}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</tbody>
		</table>		
	</div>
	
	<c:choose>
		<c:when test="${typistReport==null or empty typistReport}">
			<div id="rep8NoData">
				<spring:message code="no.data.found" text="No data found"></spring:message>
			</div>
		</c:when>
		<c:otherwise>
			<span class="page-break-before-forced">&nbsp;</span>
			<div id="typistDiv">
				<h2 style="text-align: center; color: black !important; width: 100%;"><spring:message code="qis.report.stat.typist.header" text="Typed Questions"/></h2>
				<br />
				<table class="strippedTable" border="1" id="reportTypistTable">
					<thead>
						<tr>
							<th style="width: 20px;"><spring:message code="qis.report.stat.typist.col1" text="Sr. No." /></th>
							<th><spring:message code="qis.report.stat.typist.col2" text="Name" /></th>
							<th><spring:message code="qis.report.stat.typist.col3" text="Login Name" /></th>
							<th><spring:message code="qis.report.stat.typist.col4" text="Total By User" /></th>					
						</tr>
					</thead>
					<c:forEach items="${typistReport}" var="r8" varStatus="counterT">
						<tr>
							<td class="center">${counterT.count}</td>
							<td>${r8[1]}</td>
							<td>${r8[2]}</td>
							<td class="center">${r8[3]}</td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="3">&nbsp;</td>
						<td class="center"><spring:message code="qis.report.stat.total" text="total" />:
							<span style="font-weight: bold;"> 
							 	<c:choose>
									<c:when test="${empty typistReport[0][4]}">
										-
									</c:when>
									<c:otherwise>
										${typistReport[0][4]}
									</c:otherwise>
							 	</c:choose>
						</span>
					</tr>
				</table>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>