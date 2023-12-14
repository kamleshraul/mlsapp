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
			
			/** Hide the div of selected user pending by status **/
				$("#CurrentPendingByStatusTextsDiv").hide();
				$("#hidePSDiv").hide();
			/** **/
			
			
		});	
		
		
		
		
		function motionUserCurrentPendingAsPerStatus(assignee) { //includes all submitted motions
			var parameters = "motionType=" + $("#selectedMotionType").val()
			+ "&locale="+$("#moduleLocale").val()
			+"&assignee="+assignee
			+ "&report=CALLING_ATTENTION_MOTION_GENERAL_STATISTICS_STATUS_REPORT"
			+ "&reportout=motionGeneralStatisticsByStatusReport";

			var urlSession = "ref/sessionbyhousetype/"
				+ $("#selectedHouseType").val() + "/" 
				+ $("#selectedSessionYear").val() + "/"
				+ $("#selectedSessionType").val();
			$.get(urlSession,function(data){
					if(data){
						parameters += '&sessionId=' + data.id;
						$("#CurrentPendingByStatusTextsDiv").hide();
						$("#hidePSDiv").hide();						
						//$("#CurrentPendingByStatusTextsDiv").empty();
						 $.get('motion/report/motion/genreport?'+parameters,function(data){
					
							$("#CurrentPendingByStatusTextsDiv").html(data);
							
						});	 

						$("#hidePSDiv").show();
						$("#CurrentPendingByStatusTextsDiv").show();
						
					}
					else{
						$("#CurrentPendingByStatusTextsDiv").hide();
						$("#hidePSDiv").hide();
					}
				});
		}
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
        
          #CurrentPendingByStatusTextsDiv, #clubbedRevisedQuestionTextDiv {
        	background: none repeat-x scroll 0 0 #FFF;
		    box-shadow: 0 2px 5px #888888;
		    max-height: 260px;
		    right: 32px;
		    position: fixed;
		    top: 200px;
		    width: 300px;
		    z-index: 10000;
		    overflow: auto;
		    border-radius: 10px;
	    }
                
    </style>
</head> 

<body>
	<center>
	<div id="reportDiv">
		<c:choose>
		
			<c:when test="${report != null and not (empty report)}">
				<div id="topHeader" style="text-align: center; width: 750px;">
					<h3 style="color: black;"><spring:message code="motion.generalStatisticsReportTitle" text="General Statistics Report"/> </h3>			
				</div>
				<br><br>
				<div style="width: 550px;">
					<table class="strippedTable" border="1" style="width: 550px;">
						<thead>
							<tr>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="room.name" text="General Statistics Report"/></th>
								<th style="text-align: center; font-size: 12px; width: 30px;"><spring:message code="qis.report.stat.total" text="General Statistics Report"/></th>
							</tr>
						
							
						</thead>
						<tbody>
							<c:forEach items="${report}" var="r" varStatus="counter">
								
										<tr class="page-break-after-forced">
													<td style="text-align: center; font-size: 12px; width: 30px;" id="${r[2] }">${r[0]}</td>
													<td style="text-align: center; font-size: 12px; width: 30px;">${r[1]}
													<a href="javascript:void(0);" id="viewLatestRevisedQuestionTextFromClubbedQuestionsDiv" onclick='motionUserCurrentPendingAsPerStatus("${r[2]}")'>
													<img src='./resources/images/instruction_icon.png' style='display:inline-block' title='Referenced' width='15px' height='15px' >
													</a>
													</td>
<%-- 													<td style="text-align: left; font-size: 12px; width: 60px;">${r[3]}</td>
													<td style="text-align: left; font-size: 12px; width: 90px;">${r[4]}</td>
													<td style="text-align: left; font-size: 12px; width: 60px;">${r[5]}</td> --%>
										</tr>
									
										<%-- <c:choose>
											<c:when test="${counter.count > 1}">
												<tr class="page-break-after-forced">
													<td style="text-align: center; font-size: 12px; width: 30px;">${r[1]}</td>
													<td style="text-align: justify; font-size: 12px; width: 300px;">${r[2]}</td>
													<td style="text-align: left; font-size: 12px; width: 60px;">${r[3]}</td>
													<td style="text-align: left; font-size: 12px; width: 90px;">${r[4]}</td>
													<td style="text-align: left; font-size: 12px; width: 60px;">${r[5]}</td>
												</tr>
											</c:when>
											<c:otherwise>
												<tr>
													<td style="text-align: center; font-size: 12px; width: 30px;">${r[1]}</td>
													<td style="text-align: justify; font-size: 12px; width: 300px;">${r[2]}</td>
													<td style="text-align: left; font-size: 12px; width: 60px;">${r[3]}</td>
													<td style="text-align: left; font-size: 12px; width: 90px;">${r[4]}</td>
													<td style="text-align: left; font-size: 12px; width: 60px;">${r[5]}</td>
												</tr>
											</c:otherwise>
										</c:choose>	
									</c:when>
									<c:otherwise>
										<tr>
											<td style="text-align: center; font-size: 12px; width: 30px;">${r[1]}</td>
											<td style="text-align: justify; font-size: 12px; width: 300px;">${r[2]}</td>
											<td style="text-align: left; font-size: 12px; width: 60px;">${r[3]}</td>
											<td style="text-align: left; font-size: 12px; width: 90px;">${r[4]}</td>
											<td style="text-align: left; font-size: 12px; width: 60px;">${r[5]}</td>
										</tr>
									</c:otherwise>
								</c:choose>	 --%>		
							</c:forEach>
						</tbody>
					</table>
					
					<div id="CurrentPendingByStatusTextsDiv" style="z-index: 9;" >
						
					</div>

				</div>
			</c:when>
			<c:otherwise>
				<spring:message code="member_questions_view.nodatafound" text="No Data Found"/>
			</c:otherwise>
		</c:choose>
	</div>
 </center>
</body>
</html>