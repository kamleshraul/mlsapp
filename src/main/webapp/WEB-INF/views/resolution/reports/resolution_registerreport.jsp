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
	        	page: auto;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	margin-top: 10px !important;
	        	text-align: center;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }  
	        .page-break-after-forced{
	        	page-break-after: always;
	        }    
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 20px 0px 0px 30px !important;
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
		<c:choose>
		
			<c:when test="${report != null and not (empty report)}">
				<div id="topHeader" style="text-align: center; width: 750px;">
					<h2 style="color: black;">${report[0][1]}
					<br/>
					${report[0][2]}
					</h2>
				</div>
				<br><br>
				<div style="width: 750px;">
					<table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th style="text-align: center; font-size: 12px; width: 40px;">${topHeader[0]}</th>
								<th style="text-align: center; font-size: 12px; width: 100px;">${topHeader[1]}</th>
								<th style="text-align: center; font-size: 12px; width: 520px;">${topHeader[2]}</th>
								<th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[3]}</th>
							</tr>
						
							<tr>
								<th style="text-align: center; font-size: 12px; width: 40px;">(${topHeader[4]})</th>
								<th style="text-align: center; font-size: 12px; width: 100px;">(${topHeader[5]})</th>
								<th style="text-align: center; font-size: 12px; width: 520px;">(${topHeader[6]})</th>
								<th style="text-align: center; font-size: 12px; width: 90px;">(${topHeader[7]})</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<c:choose>
									<c:when test="${(counter.count mod 17)==0}">
										<c:choose>
											<c:when test="${counter.count > 1}">
												<tr class="page-break-after-forced">
													<td style="text-align: center; font-size: 12px; width: 40px;">${formater.formatNumberNoGrouping(r[3], locale)}</td>
													<td style="text-align: left; font-size: 12px; width: 100px;">${r[4]}</td>
													<td style="text-align: justify; font-size: 12px; width: 520px;">${r[5]}</td>
													<td style="text-align: center; font-size: 12px; width: 90px;">${r[6]}</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td style="text-align: center; font-size: 12px; width: 40px;">${formater.formatNumberNoGrouping(r[3], locale)}</td>
													<td style="text-align: left; font-size: 12px; width: 100px;">${r[4]}</td>
													<td style="text-align: justify; font-size: 12px; width: 520px;">${r[5]}</td>
													<td style="text-align: center; font-size: 12px; width: 90px;">${r[6]}</td>						
												</tr>
											</c:otherwise>
										</c:choose>	
									</c:when>
									<c:otherwise>
										<tr>
											<td style="text-align: center; font-size: 12px; width: 40px;">${formater.formatNumberNoGrouping(r[3], locale)}</td>
											<td style="text-align: left; font-size: 12px; width: 100px;">${r[4]}</td>
											<td style="text-align: justify; font-size: 12px; width: 520px;">${r[5]}</td>
											<td style="text-align: center; font-size: 12px; width: 90px;">${r[6]}</td>											
										</tr>
									</c:otherwise>
								</c:choose>			
							</c:forEach>
						</tbody>
					</table>
					<div style="font-weight: bold;font-size: 20px;margin-left:350px;">
						${topHeader[8]}  :- ${report[0][7]}
						<br>
						${topHeader[9]}  :- ${report[0][8]}
					</div>
				</div>
			</c:when>
			<c:otherwise>
				No Data Found
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>