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
			 $('.editQuestionFromReport').click(function(){
				 var questionId = $(this).attr('id').split("_")[1];
				 var parameters = "houseType=" + $("#selectedHouseType").val()
				 + "&sessionYear=" + $("#selectedSessionYear").val()
				 + "&sessionType=" + $("#selectedSessionType").val()
				 + "&questionType=" + $("#selectedQuestionType").val()
				 + "&ugparam=" + $("#ugparam").val() 
				 + "&status=" + $("#selectedStatus").val() 
				 + "&role=" + $("#srole").val()
				 + "&usergroup=" + $("#currentusergroup").val()
				 + "&usergroupType=" + $("#currentusergroupType").val();
				 var resourceUrl="question/" + questionId + "/edit?"+parameters;
				 showTabByIdAndUrl('details_tab', 'question/'+questionId+'/edit?'+parameters);
			 });
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
	        
	        .serialCount{
	        	max-width: 8px !important;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	margin-top: 10px !important;
	        	text-align: center;
	        }
	        
	        .serialCount{
	        	max-width: 8px !important;
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
	
	<div id="reportDiv" align="center">
		<c:choose>
		
			<c:when test="${report != null and not (empty report)}">
				<div id="topHeader" style="text-align: center; width: 750px;">
					<h3 style="color: black;">${topHeader[0]}</h3>			
				</div>
				<br>
				<div style="text-align: right; width: 750px; font-size: 14px;"><b><spring:message code="generic.date" text="Date"/></b>:&nbsp;${report[0][15]}</div>
				<br>
				<div style="width: 750px;">
					<table class="strippedTable" border="1" style="width: 750px;">
						<thead>
							<tr>
								<th class="serialCount" style="text-align: left; font-size: 12px; min-width: 20px; max-width: 20px; ">${topHeader[5]}</th>
								<th style="text-align: center; font-size: 12px; width: 60px;">${topHeader[1]}</th>
								<c:choose>
									<c:when test="${report[0][11]=='status_visible'}">
										<th style="text-align: center; font-size: 12px; width: 250px;">${topHeader[2]}</th>
									</c:when>
									<c:otherwise>
										<th style="text-align: center; font-size: 12px; width: 550px;">${topHeader[2]}</th>
									</c:otherwise>
								</c:choose>
								<th style="text-align: center; font-size: 12px; min-width: 60px;">${topHeader[3]}</th>
								<c:if test="${report[0][11]=='status_visible'}">
									<th style="text-align: center; font-size: 12px; min-width: 90px;">${topHeader[4]}</th>
									<%-- <th style="text-align: center; font-size: 12px; width: 250px;">${topHeader[6]}</th> --%>
								</c:if>					
							</tr>
						
							<tr>
								<c:choose>
									<c:when test="${report[0][11]=='status_visible'}">
										<td colspan="5">&nbsp;</td>
									</c:when>
									<c:otherwise>
										<td colspan="4">&nbsp;</td>
									</c:otherwise>
								</c:choose>								
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								<c:choose>
									<c:when test="${(counter.count mod 11)==0}">
										<c:choose>
											<c:when test="${counter.count > 1}">
												<tr class="page-break-after-forced">
													<td class="serialCount" style="text-align: left; font-size: 12px; min-width: 20px; max-width: 20px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
													<td style="text-align: center; font-size: 12px; width: 60px;vertical-align: top;">${r[1]}</td>
													<c:choose>
														<c:when test="${r[11]=='status_visible'}">
															<td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[2]}</td>
														</c:when>
														<c:otherwise>
															<td style="text-align: justify; font-size: 12px; width: 550px;vertical-align: top;">${r[2]}</td>
														</c:otherwise>
													</c:choose>
													<td style="text-align: left; font-size: 12px; min-width: 60px;vertical-align: top;">${r[3]}</td>
													<c:if test="${r[11]=='status_visible'}">
														<td style="font-size: 12px;vertical-align: top;">
															<c:choose>
																<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (empty r[6])}">
																	<p align="center">
																		${r[4]}
																		<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[5]}
																	</p>
																</c:when>
																<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (not empty r[6])}">
																	<p align="center">
																		${r[4]}
																		<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[7]}
																	</p>
																</c:when>
																<c:when test="${r[9]=='question_final_rejection' and (not empty r[12])}">
																	<p align="center">${r[4]}</p>
																	<br/>
																	<p align="justify">${r[12]}</p>
																	<c:if test="${not empty r[13]}">
																		<p align="justify">${r[13]}</p>
																	</c:if>
																</c:when>
																<c:otherwise><p align="center">${r[4]}</p></c:otherwise>
															</c:choose>							
														</td>
														<%-- <td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[12]}</td> --%>
													</c:if>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td class="serialCount" style="text-align: left; font-size: 12px; min-width: 20px; max-width: 20px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
													<td style="text-align: center; font-size: 12px; width: 60px;vertical-align: top;">${r[1]}</td>
													<c:choose>
														<c:when test="${r[11]=='status_visible'}">
															<td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[2]}</td>
														</c:when>
														<c:otherwise>
															<td style="text-align: justify; font-size: 12px; width: 550px;vertical-align: top;">${r[2]}</td>
														</c:otherwise>
													</c:choose>
													<td style="text-align: left; font-size: 12px; min-width: 60px;vertical-align: top;">${r[3]}</td>
													<c:if test="${r[11]=='status_visible'}">
														<td style="font-size: 12px;vertical-align: top;">
															<c:choose>
																<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (empty r[6])}">
																	<p align="center">
																		${r[4]}
																		<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[5]}
																	</p>
																</c:when>
																<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (not empty r[6])}">
																	<p align="center">
																		${r[4]}
																		<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[7]}
																	</p>
																</c:when>
																<c:when test="${r[9]=='question_final_rejection' and (not empty r[12])}">
																	<p align="center">${r[4]}</p>
																	<br/>
																	<p align="justify">${r[12]}</p>
																	<c:if test="${not empty r[13]}">
																		<p align="justify">${r[13]}</p>
																	</c:if>
																</c:when>
																<c:otherwise><p align="center">${r[4]}</p></c:otherwise>
															</c:choose>							
														</td>
														<%-- <td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[12]}</td> --%>
													</c:if>													
												</tr>
											</c:otherwise>
										</c:choose>	
									</c:when>
									<c:otherwise>
										<tr>
											<td class="serialCount" style="text-align: left; font-size: 12px; min-width: 20px; max-width: 20px; vertical-align: top;">${serialNumbers[counter.count-1]}</td>
											<td style="text-align: center; font-size: 12px; width: 60px;vertical-align: top;">${r[1]}</td>
											<c:choose>
												<c:when test="${r[11]=='status_visible'}">
													<td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[2]}</td>
												</c:when>
												<c:otherwise>
													<td style="text-align: justify; font-size: 12px; width: 550px;vertical-align: top;">${r[2]}</td>
												</c:otherwise>
											</c:choose>													
											<td style="text-align: left; font-size: 12px; min-width: 60px;vertical-align: top;">${r[3]}</td>
											<c:if test="${r[11]=='status_visible'}">
												<td style="font-size: 12px;vertical-align: top;">
													<c:choose>
														<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (empty r[6])}">
															<p align="center">
																${r[4]}
																<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[5]}
															</p>
														</c:when>
														<c:when test="${r[8]=='questions_starred' and r[9]=='question_final_admission' and (not empty r[6])}">
															<p align="center">
																${r[4]}
																<br/><br/><spring:message code="generic.date" text="Date"/>:&nbsp;${r[7]}
															</p>
														</c:when>
														<c:when test="${r[9]=='question_final_rejection' and (not empty r[12])}">
															<p align="center">${r[4]}</p>															
															<p align="justify">${r[12]}</p>
															<c:if test="${not empty r[13]}">
																<p align="justify">${r[13]}</p>
															</c:if>
														</c:when>
														<c:otherwise><p align="center">${r[4]}</p></c:otherwise>
													</c:choose>							
												</td>
												<%-- <td style="text-align: justify; font-size: 12px; width: 250px;vertical-align: top;">${r[12]}</td> --%>
											</c:if>
										</tr>
									</c:otherwise>
								</c:choose>			
							</c:forEach>
						</tbody>
					</table>
					<c:if test="${not empty report[0][16]}">
						<br/>
						<div style="text-align: left; width: 750px; font-size: 18px;"><b>${report[0][16]}</b></div>
					</c:if>
				</div>
			</c:when>
			<c:otherwise>
				<spring:message code="member_questions_view.nodatafound" text="No Data Found"/>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>