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
		<h2 style="color: black !important; margin-left: 50px;">${head1}</h2>
		<h2 style="color: black !important; margin-left: 20px;">${head2}</h2>
		<br />	
	</div>
	<div id="statsReportDiv">
		<table border="1" id="reportTable" class="strippedTable">
			<thead>
				<tr>
					<th style="width: 71px; padding: 2px;">${col1}</th>
					<th style="width: 80px; padding: 2px;">${col2}</th>
					<th style="width: 80px; padding: 2px;">${col3}</th>
					<th style="width: 80px; padding: 2px;">${col4}</th>
					<th style="width: 90px; padding: 2px;">${col5}</th>
					<th style="width: 95px; padding: 2px;">${col6}</th>
					<th style="width: 95px; padding: 2px;">${col7}</th>
					<th style="width: 95px; padding: 2px;">${col8}</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach begin="1" end="5" step="1" varStatus="counter">
					<tr>
						<td>
							<c:choose>
								<c:when test="${empty report1[counter.count-1][1]}">
									-
								</c:when>
								<c:otherwise>
									${report1[counter.count-1][1]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report1[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report1[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report2[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report2[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report3[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report3[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report4[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report4[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report5[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report5[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report6[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report6[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty report7[counter.count-1][2]}">
									-
								</c:when>
								<c:otherwise>
									${report7[counter.count-1][2]}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
				<tr>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty total}">
								-
							</c:when>
							<c:otherwise>
								${total}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report1[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report1[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report2[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report2[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report3[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report3[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report4[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report4[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report5[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report5[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report6[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report6[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="finalTotal">
						<c:choose>
							<c:when test="${empty report7[0][3]}">
								-
							</c:when>
							<c:otherwise>
								${report7[0][3]}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</tbody>
		</table>		
	</div>
	
	<c:choose>
		<c:when test="${report8==null or empty report8}">
			<div id="rep8NoData">
				<spring:message code="no.data.found" text="No data found"></spring:message>
			</div>
		</c:when>
		<c:otherwise>
			<span class="page-break-before-forced">&nbsp;</span>
			<div id="typistDiv">
				<h2 style="text-align: center; color: black !important; width: 100%;"><spring:message code="qis.stat.report.typist.header" text="Typped Questions"/></h2>
				<br />
				<table class="strippedTable" border="1" id="reportTypistTable">
					<thead>
						<tr>
							<th style="width: 20px;"><spring:message code="qis.stat.report.typist.col1" text="Sr. No." /></th>
							<th><spring:message code="qis.stat.report.typist.col2" text="Name" /></th>
							<th><spring:message code="qis.stat.report.typist.col3" text="Login Name" /></th>
							<th><spring:message code="qis.stat.report.typist.col4" text="Total By User" /></th>					
						</tr>
					</thead>
					<c:forEach items="${report8}" var="r8">
						<tr>
							<td class="center">${r8[0]}</td>
							<td>${r8[2]}</td>
							<td>${r8[3]}</td>
							<td class="center">${r8[4]}</td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="3">&nbsp;</td>
						<td class="center">${total} : 
							<c:choose>
								<c:when test="${empty report8[0][5]}">
									-
								</c:when>
								<c:otherwise>
									${report8[0][5]}
							</c:otherwise>
						</c:choose>
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