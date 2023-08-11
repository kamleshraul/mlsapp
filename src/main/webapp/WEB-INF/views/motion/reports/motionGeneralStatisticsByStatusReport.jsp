<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.member_questions_view" text="Member's Qustions View"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){			
			
			$("#hidePSDiv").click(function(){
				$(this).hide();
				$('#CurrentPendingByStatusTextsDiv').hide();
			});
			
			
		});	
		
		
		
		
	</script>
	 <style type="text/css">
        
    </style>
</head> 

<body>
	<center>
	
		<c:choose>
		
			<c:when test="${report != null and not (empty report)}">
				<div id="topHeader" style="text-align: center; width: 150px;">
				<div id="hidePSDiv" style="background: #FF0000; margin-left:190px; margin-top:12px; color: #FFF; width: 22px; border-radius: 13px; cursor: pointer;">
						&nbsp;X&nbsp;
				</div>	
					<h5 style="color: black;"> <spring:message code="motion.CurrentUserPendingByStatus" text="Current User Pending By Status"/> </h5>		
					
				</div>
				
				<div style="width: 250px; padding-bottom: 20px;">
					<table class="strippedTable" border="1" style="width: 250px;">
						<thead>
							<tr>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="motion.currentStatus" text="General Statistics Report"/></th>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="qis.report.stat.total" text="General Statistics Report"/></th>
							</tr>
						
							
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								
										<tr class="page-break-after-forced">
													<td style="text-align: center; font-size: 12px; width: 30px;" id="${r[2] }">${r[0]}</td>
													<td style="text-align: center; font-size: 12px; width: 30px;">${r[1]}</td>

							</c:forEach>
						</tbody>
					</table>		
				</div>
			</c:when>
			<c:otherwise>
				<spring:message code="member_questions_view.nodatafound" text="No Data Found"/>
			</c:otherwise>
		</c:choose>
	
 </center>
</body>
</html>