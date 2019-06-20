<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proprietypoint.member_proprietypoints_view" text="Member's Propriety Points View"/>
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
					<h3 style="color: black;">${topHeader[0]}</h3>			
				</div>
				<br><br>
				<div style="width: 750px;">
					<table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th class="serialCount" style="text-align: left; font-size: 12px; width: 8px; vertical-align: top;">${topHeader[4]}</th>
								<th style="text-align: center; font-size: 12px; width: 30px; vertical-align: top;">${topHeader[1]}</th>
								<th style="text-align: center; font-size: 12px; width: 300px; vertical-align: top;">${topHeader[2]}</th>
								<th style="text-align: center; font-size: 12px; width: 60px; vertical-align: top;">${topHeader[3]}</th>
							</tr>
						
							<tr>
								<td colspan="5">&nbsp;</td>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<c:choose>
									<c:when test="${(counter.count mod 17)==0}">
										<c:choose>
											<c:when test="${counter.count > 1}">
												<tr class="page-break-after-forced">
													<td class="serialCount" style="text-align: left; font-size: 12px; width: 8px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
													<td style="text-align: left; font-size: 12px; width: 30px; vertical-align: top; padding-left: 20px;">${r[1]}</td>
													<td style="text-align: center; font-size: 12px; width: 300px; vertical-align: top;">${r[2]}</td>
													<td style="text-align: justify; font-size: 12px; width: 60px; vertical-align: top;">${r[3]}</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td class="serialCount" style="text-align: left; font-size: 12px; width: 8px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
													<td style="text-align: left; font-size: 12px; width: 30px; vertical-align: top; padding-left: 20px;">${r[1]}</td>
													<td style="text-align: center; font-size: 12px; width: 300px; vertical-align: top;">${r[2]}</td>
													<td style="text-align: justify; font-size: 12px; width: 60px; vertical-align: top;">${r[3]}</td>
												</tr>
											</c:otherwise>
										</c:choose>	
									</c:when>
									<c:otherwise>
										<tr>
											<td class="serialCount" style="text-align: left; font-size: 12px; width: 8px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
											<td style="text-align: left; font-size: 12px; width: 30px; vertical-align: top; padding-left: 20px;">${r[1]}</td>
											<td style="text-align: center; font-size: 12px; width: 300px; vertical-align: top;">${r[2]}</td>
											<td style="text-align: justify; font-size: 12px; width: 60px; vertical-align: top;">${r[3]}</td>
										</tr>
									</c:otherwise>
								</c:choose>			
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<spring:message code="member_proprietypoints_view.nodatafound" text="No Data Found"/>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>