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
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="statsReportDiv">
		<%@ include file="/common/info.jsp" %>
			<h2 style="color: black !important; margin-left: 30px;">${head1}</h2>
			<h2 style="color: black !important;">${head2}</h2>
		<table border="1">
			<thead>
				<tr>
					<th>${col1}</th>
					<th>${col2}</th>
					<th>${col3}</th>
					<th>${col4}</th>
					<th>${col5}</th>
					<th>${col6}</th>
					<th>${col7}</th>
					<th>${col8}</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${report1}" var="r1" varStatus="counter">
					<tr>
						<td>${r1[1]}</td>
						<td>${r1[2]}</td>
						<td>${report2[counter.count-1][2]}</td>
						<td>${report3[counter.count-1][2]}</td>
						<td>${report4[counter.count-1][2]}</td>
						<td>${report5[counter.count-1][2]}</td>
						<td>${report6[counter.count-1][2]}</td>
						<td>${report7[counter.count-1][2]}</td>
					</tr>
				</c:forEach>
				<tr>
					<td>${total}</td>
					<td>${report1[0][3]}</td>
					<td>${report2[0][3]}</td>
					<td>${report3[0][3]}</td>
					<td>${report4[0][3]}</td>
					<td>${report5[0][3]}</td>
					<td>${report6[0][3]}</td>
					<td>${report7[0][3]}</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>