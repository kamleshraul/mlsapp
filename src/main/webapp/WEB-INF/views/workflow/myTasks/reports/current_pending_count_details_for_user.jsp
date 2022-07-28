<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="workflow.myTasks.current_pending_count_details_for_user" text="Current Pending Count Details for User"/>
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
	        	margin: 10px;
	        	page: auto;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	margin: 5px;
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
                
    </style>
</head> 

<body>
	
	<div id="reportDiv">
		<c:choose>
		
			<c:when test="${report != null and not (empty report)}">
				<div id="topHeader" style="text-align: center; width: 750px;">
					<h3 style="color: black;">${topHeader[0]}</h3>			
				</div>
				<br><br>
				<div style="width: 750px;">
					<table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th style="text-align: center; font-size: 12px; width: 50px;">${topHeader[1]}</th>
								<th style="text-align: left; font-size: 12px; width: 200px; padding-left: 50px !important;">${topHeader[2]}</th>
								<th style="text-align: center; font-size: 12px; width: 80px;">${topHeader[3]}</th>
							</tr>
						
							<tr>
								<td colspan="4">&nbsp;</td>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<c:choose>
									<c:when test="${(counter.count mod 17)==0}">
										<c:choose>
											<c:when test="${counter.count > 1}">
												<tr class="page-break-after-forced">
													<td style="text-align: center; font-size: 12px; width: 50px;">${r[1]}</td>
													<td style="text-align: left; font-size: 12px; width: 200px; padding-left: 50px !important;">${r[2]}</td>
													<td style="text-align: right; font-size: 12px; width: 80px; padding-right: 70px !important;">${r[3]}</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td style="text-align: center; font-size: 12px; width: 50px;">${r[1]}</td>
													<td style="text-align: left; font-size: 12px; width: 200px; padding-left: 50px !important;">${r[2]}</td>
													<td style="text-align: right; font-size: 12px; width: 80px; padding-right: 70px !important;">${r[3]}</td>
												</tr>
											</c:otherwise>
										</c:choose>	
									</c:when>
									<c:otherwise>
										<tr>
											<td style="text-align: center; font-size: 12px; width: 50px;">${r[1]}</td>
											<td style="text-align: left; font-size: 12px; width: 200px; padding-left: 50px !important;">${r[2]}</td>
											<td style="text-align: right; font-size: 12px; width: 80px; padding-right: 70px !important;">${r[3]}</td>
										</tr>
									</c:otherwise>
								</c:choose>			
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<spring:message code="workflow.mytasks.current_pending_count_empty" text="No Pending Tasks Found"/>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>