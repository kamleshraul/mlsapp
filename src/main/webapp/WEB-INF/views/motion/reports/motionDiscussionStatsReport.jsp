<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.report.party" text="Party Report"/>
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
	        	margin-top: 20px !important;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 20px 0px 0px 15px !important;
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
					${topHeader[0]}	
				</div>				
				<br>
				 <div style="width: 750px;">
					<div style="font-weight: bold; width: 700px; margin: 0px 0px 10px 25px;">
						${report[0][7]}, ${report[0][9]}
					</div>
					<br>
					<table style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							<tr>
								<th style="width: 20px;">${topHeader[2]}</th>
								<th style="width: 20px;">${topHeader[3]}</th>
								<th style="width: 50px;">${topHeader[4]}</th>
								<th style="width: 380px;">${topHeader[5]}</th>
								<th style="width: 50px;">${topHeader[6]}</th>
								<th style="width: 50px;">${topHeader[7]}</th>
								<th style="width: 20px;">${topHeader[8]}</th>
							</tr>						
						</thead>
						<tbody>
							<c:set var="discussionDate" value=""></c:set>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<c:choose>
									<c:when test="${discussionDate ne r[6]}">
										<tr>
											<td colspan="7">${topHeader[1]} - ${r[6]}</td>
										</tr>
										<c:set var="discussionDate" value="${r[6]}"></c:set>
									</c:when>
								</c:choose>								
								<tr class="page-break">
									<td style="width: 20px; text-align: center;">${formater.formatNumberNoGrouping(counter.count, locale)}</td>
									<td style="width: 20px; text-align: center;">${formater.formatNumberNoGrouping(r[1], locale)}</td>
									<td style="width: 50px; text-align: left;">${r[2]}</td>
									<td style="width: 380px; text-align: center;">${r[3]}</td>
									<td style="width: 50px; text-align: center;">${r[4]}</td>
									<td style="width: 50px; text-align: center;">${r[5]}</td>
									<td style="width: 50px; text-align: center;">${r[8]}</td>
								</tr>	
								<c:set var="total" value="${counter.count}" />
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>