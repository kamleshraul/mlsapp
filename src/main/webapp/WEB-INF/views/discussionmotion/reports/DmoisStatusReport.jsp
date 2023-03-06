<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="motion.admission.report" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
			var selectedStatus = $("#selectedStatus").val();
			
			if(selectedStatus == 0 )
			{
				$(".specificHeader").hide();
				$(".genericHeader").show();
			}
			else
				{
				$(".specificHeader").show();
				$(".genericHeader").hide();
				}
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
			<c:set var="total" value="0" />
			<div id="reportDiv">
				<div style="width: 750px; text-align: center; font-size: 20px; font-weight: bold;" class="specificHeader">
					${report[0][12]} ${report[0][13]} 		
				</div>		
				<div style="width: 750px; text-align: center; font-size: 20px; font-weight: bold;" class="genericHeader">
					 ${report[0][13]} 		
				</div>			
				<br><br>
				<div style="width: 750px;">
					<%-- <div style="font-weight: bold; width: 700px; margin: 0px 0px 10px 25px;">
						${report[0][1]}
					</div> --%>
					
					<table style="width: 700px; border: 1px solid black;" border="1" align="center" class="strippedTable">  
						<thead>
							
									<tr>
										<th>${topHeader[0]}</th>
										<th>${topHeader[1]}</th>
										<th>${topHeader[2]}</th>
										<th>${topHeader[3]}</th>
										<th>${topHeader[4]}</th>
										<%-- <th>${topHeader[5]}</th> --%>
										<th>${topHeader[6]}</th>
									</tr>
													
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								
								<tr>
								
							
											<td style="width: 40px; text-align: center;">${counter.count}</td>
											<td style="width: 80px; text-align: center;">${r[4]}</td>
											<td style="width: 80px; text-align: center;">${r[6]}</td>
											<td style="width: 320px; text-align: center;">${r[7]}</td>
											<td style="width: 160px; text-align: center;">${r[8]}</td>
											<td style="width: 120px; text-align: center;">${r[9]}</td>
										</tr>
									
								<c:set var="total" value="${counter.count}" />
							</c:forEach>
						</tbody>
					</table>
					<div style="font-weight: bold;  float: rigth; width: 715px; text-align: right; margin: 10px">
						${report[0][10]}&nbsp;${total}
					</div>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>