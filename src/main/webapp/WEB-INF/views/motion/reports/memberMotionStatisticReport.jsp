<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.memberstatistics.report" text="Member Motion Statistical Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
		
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
			<c:set var="total" value="0" />
			<c:set var="labels" value="${fn:split(report[0][1],';')}" />
			<div id="reportDiv">
<%-- 				<div style="width: 750px; text-align: center; font-size: 20px; font-weight: bold;">
					${labels[0]}	
				</div>		 --%>		
				<br>
				 <div style="width: 750px;">
<%-- 					<div style="font-weight: bold; width: 700px; margin: 0px 0px 10px 25px;">
						${report[0][2]}
					</div> --%>
					<br>
					<table style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							<tr>
								<th style="width: 40px;">${topHeader[0]}</th>
								<th style="width: 120px;">${topHeader[1]}</th>
								<th style="width: 20;">${topHeader[2]}</th>
								<th style="width: 40px;">${topHeader[3]}</th>
								<th style="width: 40px;">${topHeader[4]}</th>
								<th style="width: 40px;">${topHeader[5]}</th>
								<th style="width: 40px;">${topHeader[6]}</th>
								<th style="width: 40px;">${topHeader[7]}</th>
								<th style="width: 40px;">${topHeader[8]}</th>
								<th style="width: 40px;">${topHeader[9]}</th>
							</tr>						
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<tr class="page-break">
									<td style="width: 40px; text-align: center;">${formater.formatNumberNoGrouping(counter.count, locale)}</th>
									<td style="width: 120px; text-align: center;">${r[1]}</th>
									<td style="width: 40px; text-align: center;">${r[2]}</th>
									<td style="width: 40px; text-align: center;">${r[3]}</th>
									<td style="width: 40px; text-align: center;">${r[4]}</th>
									<td style="width: 40px; text-align: center;">${r[5]}</th>
									<td style="width: 40px; text-align: center;">${r[6]}</th>
									<td style="width: 40px; text-align: center;">${r[7]}</th>
									<td style="width: 40px; text-align: center;">${r[8]}</th>
									<td style="width: 40px; text-align: center;">${r[9]}</th>
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