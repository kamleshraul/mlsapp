<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.admission.report" text="Member Report"/>
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
			<c:set var="footerCols" value="-"></c:set>
			<div id="reportDiv">
				 <div style="width: 750px;">					
					<table style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							<tr>
								<th style="width: 30px;">${topHeader[0]}</th>
								<th style="width: 50px;">${topHeader[1]}</th>
								<th style="width: 120px;">${topHeader[2]}</th>
								<th style="width: 275px;">${topHeader[3]}</th>
								<th style="width: 125px;">${topHeader[4]}</th>
								<c:if test="${report[0][7]=='upperhouse'}">
									<th style="width: 50px;">${topHeader[6]}</th>
								</c:if>
								<c:choose>
									<c:when test="${report[0][7]=='upperhouse'}">
										<th style="width: 50px;">${topHeader[5]}</th>
									</c:when>
									<c:otherwise>
										<th style="width: 100px;">${topHeader[5]}</th>
									</c:otherwise>
								</c:choose>								
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								
								<c:if test="${footerCols=='-' and r[6]!=null}">
									<c:set var="footerCols" value="${fn:split(r[6],',')}"></c:set>
								</c:if>
								
								<tr class="page-break">
									<td style="width: 30px; text-align: center;">${formater.formatNumberNoGrouping(counter.count, locale)}</th>
									<td style="width: 50px;">${r[1]}</td>
									<td style="width: 120px;">${r[2]}</td>
									<td style="width: 275px;">${r[3]}</td>
									<td style="width: 125px;">${r[4]}</td>
									<c:if test="${r[7]=='upperhouse'}">
										<td style="width: 50px;">${r[8]}</td>
									</c:if>
									<c:choose>
										<c:when test="${r[7]=='upperhouse'}">
											<td style="width: 50px;">${r[5]}</td>
										</c:when>
										<c:otherwise>
											<td style="width: 100px;">${r[5]}</td>
										</c:otherwise>
									</c:choose>																		
								</tr>
							</c:forEach>
						</tbody>
					</table>	
					<br>
					
					<div style="color: black;">						
						<c:forEach var="fc" items="${footerCols}">
							<h3>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${fc}
							</h3>
						</c:forEach>
					</div>									
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>