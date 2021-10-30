<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.departmentstatement" text="Department Statement Report"/>
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
					<h2 style="color: black;">${report[0][1]}</h2>
				</div>
				<br>
				<div style="text-align: right;margin-right:50px">
					${report[0][12]}
				</div>
				<br>
				<div style="width: 750px;">
					<c:set var="number" value="0" />
					<table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th style="text-align: center; font-size: 12px; width: 40px;">${topHeader[0]}</th>
								<th style="text-align: center; font-size: 12px; width: 60px;">${topHeader[1]}</th>
								<th style="text-align: center; font-size: 12px; width: 200px;">${topHeader[2]}</th>
								<th style="text-align: center; font-size: 12px; width: 340px;">${topHeader[3]}</th>
								<th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[4]}</th>
								<%-- <th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[5]}</th> --%>
								<th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[6]}</th>
								<th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[6]}</th>
								<th style="text-align: center; font-size: 12px; width: 90px;">${topHeader[7]}</th>					
							</tr>
							<tr>
								<td colspan="9">&nbsp;</td>
							</tr>
						</thead>
						<tbody>
							<c:set var="countingVar" value="0"/>
							<c:set var="deskOfficerDisplayed" value="no"/>
							<c:set var="questionDisplayed" value="no"/>
							<c:set var="indexNo" value="1"/>
							<c:forEach items="${report}" var="r" varStatus="counter" >
								<c:if test="${counter.index==countingVar}">
									<c:set var="deskOfficerDisplayed" value="no"/>
									<c:set var="countVar" value="${countingVar}"/>
									<tr>
										<td style="text-align: center; font-size: 12px; width: 30px;">${indexNo}</td>
										<td style="text-align: left; font-size: 12px; width: 60px;">${formater.formatNumberNoGrouping(r[2], locale)}</td>
										<td style="text-align: center; font-size: 12px; width: 200px;">${r[4]}</td>
										<td style="text-align: justify; font-size: 12px; width: 340px;">${r[5]}</td>
										<td style="text-align: justify; font-size: 12px; width: 90px;">${r[8]}</td>
										<%-- <td style="text-align: left; font-size: 12px; width: 90px;">${r[9]}</td> --%>
										<td style="text-align: justify; font-size: 12px; width: 50px;">
											<c:set var="department" value="${r[11]}"/>
											<c:forEach items = "${report}" begin="${countVar}" end ="${report.size()}" var="j" varStatus="innerCount">
												<c:if test="${r[2]==j[2]}">
													<c:if test="${j[6]=='department' and j[3] !=department }">
														<c:set var="department" value="${j[3]}"/>
													</c:if>
													<c:if test="${j[11]==department}">
														<div>
															${j[3]}
														</div>
													</c:if>													
													<c:set var="countingVar" value="${innerCount.index+1}"/>
												</c:if>
											</c:forEach>
										</td>
										<td>
											<c:choose>
												<c:when test="${r[10]!=null && r[10]!='' }">
													<c:set var="actor" value="${fn:split(r[10], '#')}" />
													<c:choose>
														<c:when test="${actor[1]=='department' or actor[1]=='department_deskofficer'}">
															${actor[4]} <br/> (${actor[0]})
														</c:when>
														<c:otherwise>
															-
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													
												</c:otherwise>
											</c:choose>
										</td>
										<td style="text-align: left; font-size: 12px; width: 90px;">${r[7]}</td>
										<c:set var="indexNo" value="${indexNo + 1}"/>
									</tr>
									<%-- <c:if test="${deskOfficerDisplayed=='no' }">
										<c:if test="${questionDisplayed=='no'}">
											<tr>
												<td style="text-align: center; font-size: 12px; width: 40px;">${formater.formatNumberNoGrouping(counter.count, locale)}</td>
												<td style="text-align: left; font-size: 12px; width: 60px;">${formater.formatNumberNoGrouping(r[2], locale)}</td>
												<td style="text-align: center; font-size: 12px; width: 200px;">${r[4]}</td>
												<td style="text-align: justify; font-size: 12px; width: 340px;">${r[5]}</td>
												<td style="text-align: justify; font-size: 12px; width: 90px;">${r[8]}</td>
												comment below line if uncommented this block of comments
												<td style="text-align: left; font-size: 12px; width: 90px;">${r[9]}</td>
												<td style="text-align: justify; font-size: 12px; width: 90px;">
													<c:choose>
														<c:when test="${r[10]!=null && r[10]!='' }">
															<c:set var="actor" value="${fn:split(r[10], '#')}" />
															<c:choose>
																<c:when test="${actor[1]=='department' or actor[1]=='department_deskofficer'}">
																	${actor[4]} <br/> (${actor[0]})
																</c:when>
																<c:otherwise>
																	-
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															
														</c:otherwise>
													</c:choose>
												</td>
												<td style="text-align: left; font-size: 12px; width: 90px;">${r[3]}</td>
												<td style="text-align: left; font-size: 12px; width: 90px;">${r[7]}</td>
											</tr>
											<c:set var="questionDisplayed" value="yes"/>
										</c:if>
									</c:if> --%> 
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				No Data Found
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html> 