<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.hd.daywisereport" text="Daywise Report"/>
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
	        	margin-top: 0px !important;
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
        
        th{
        	text-align: center;
        }
    </style>
</head> 

<body>
	<c:choose>
		<c:when test="${report==null}">
			<b>No data.</b>
		</c:when>
		<c:when test="${empty(report)}">
			Empty data found.
		</c:when>
		<c:otherwise>
			<div id="reportDiv">
				<div style="width: 750px; text-align: center; font-size: 20px; font-weight: bold;">
					<spring:message code="question.hdq.report.status.state" text="Maharashtra " />&nbsp;${report[0][7]}<br>
					${report[0][6]}
				</div>
				<br>
				<div style="width: 750px; text-align: center; font-size: 20px; font-weight: bold;">
						${report[0][5]}
				</div>
				<br><br>
				<div style="width: 750px;">
					<table style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							<tr>
								<th style="width: 40px;"><spring:message code="question.hdq.report.status.srno" text="Sr. No." /></th>
								<th style="width: 80px;"><spring:message code="question.hdq.report.status.hdqNumber" text="HDQ Number" /></th>
								<th style="width: 80px;"><spring:message code="question.hdq.report.status.questionNumber" text="Question Number" /></th>
								<th style="width: 150px;"><spring:message code="question.hdq.report.status.memberName" text="Member" /></th>
								<th style="width: 250px;"><spring:message code="question.hdq.report.status.subject" text="Subject" /></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<tr>
									<td style="width: 40px; text-align: center;">${counter.count}</td>
									<td style="width: 80px; text-align: center;">${r[0]}</td>
									<td style="width: 80px; text-align: center;">${r[1]}</td>
									<td style="width: 80px; text-align: center;">${r[2]}</td>
									<td style="width: 320px; text-align: center;">${r[3]}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>