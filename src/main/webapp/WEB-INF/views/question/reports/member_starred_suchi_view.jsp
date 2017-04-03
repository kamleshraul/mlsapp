<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>
	<spring:message code="question.member_starred_suchi_view" text="Member's Starred Questions Suchi View"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){			
			$('.viewQuestion').click(function(e) {
				e.preventDefault();
				var qNum = $(this).attr('id').split("_")[1];
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
				var parameters="session="+$("#session").val()
				+"&deviceType="+$("#deviceType").val()
				+"&number="+qNum
				+"&viewName=question/reports/question_view_in_yaadi_format";
				var resourceURL='ref/device?'+parameters;
				$.get(resourceURL,function(data){
					$.unblockUI();
					$.fancybox.open(data,{autoSize:false,width:650,height:600});
				},'html').fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
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
	        
	        a.viewQuestion{
		     	text-decoration: underline !important;
		    }	        
        }
        @media print{
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	margin-top: 10px !important;
	        	text-align: center;
	        }
	        
	        a.viewQuestion{
		     	text-decoration: none !important;
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
		<div id="contentDiv" style="text-align: center;font-size: 13pt;font-weight: bold;">
			<div style="font-size: 20pt;">
				${reportLocalizedDetails[0][0]}
			</div>
			<div style="margin-top: 10px;font-size: 16pt;">
				${reportLocalizedDetails[0][1]}
			</div>
			<div style="margin-top: 10px;font-size: 12pt;">
				--------------------------
			</div>
			<div style="margin-top: 10px;font-size: 14pt;">
				${answeringDate} / ${answeringDateInIndianCalendar} ${reportLocalizedDetails[0][2]}
			</div>
			<div style="margin-top: 15px;" align="center">
				<table border="0">
					<tbody>
						<c:set var="rowsCount" value="${ministryVOs.size()}"/>
						<c:forEach items="${ministryVOs}" var="ministryVO" varStatus="rowPosition">
							<c:choose>
								<c:when test="${rowPosition.count==1}">
									<tr>
										<td width="12%" style="font-size: 13pt;font-weight: bold;">
											(${ministryVO.number})
										</td>
										<td width="55%" style="font-size: 13pt;font-weight: bold;padding-left: 5px;text-align: left;">
											${ministryVO.name}
										</td>
										<td width="12%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;" rowspan="${rowsCount}">
											<img src="./resources/images/brace.JPG" height="100" width="70"/>
										</td>
										<td width="21%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;text-align: left;" rowspan="${rowsCount}">
											${reportLocalizedDetails[0][3]}
										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<tr>
										<td width="12%" style="font-size: 13pt;font-weight: bold;">
											(${ministryVO.number})
										</td>
										<td width="55%" style="font-size: 13pt;font-weight: bold;padding-left: 5px;text-align: left;">
											${ministryVO.name}
										</td>
									</tr>
								</c:otherwise>
							</c:choose>							
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div style="margin-top: 15px;font-size: 14pt;">
				${reportLocalizedDetails[0][4]} - ${totalNumberOfDevices}
			</div>
			<div style="margin-top: 10px;font-size: 14pt;">
				<c:forEach items="${roundVOs}" var="roundVO" varStatus="roundPosition">
					<c:choose>
						<c:when test="${roundPosition.count==1}">
							${fn:split(reportLocalizedDetails[0][5], '#')[0]}
						</c:when>
						<c:when test="${roundPosition.count==2}">
							${fn:split(reportLocalizedDetails[0][5], '#')[1]}
						</c:when>
						<c:when test="${roundPosition.count==3}">
							${fn:split(reportLocalizedDetails[0][5], '#')[2]}
						</c:when>
						<c:when test="${roundPosition.count==4}">
							${fn:split(reportLocalizedDetails[0][5], '#')[3]}
						</c:when>
						<c:when test="${roundPosition.count==5}">
							${fn:split(reportLocalizedDetails[0][5], '#')[4]}
						</c:when>
						<c:when test="${roundPosition.count==6}">
							${fn:split(reportLocalizedDetails[0][5], '#')[5]}
						</c:when>
						<c:when test="${roundPosition.count==7}">
							${fn:split(reportLocalizedDetails[0][5], '#')[6]}
						</c:when>
						<c:when test="${roundPosition.count==8}">
							${fn:split(reportLocalizedDetails[0][5], '#')[7]}
						</c:when>
						<c:when test="${roundPosition.count==9}">
							${fn:split(reportLocalizedDetails[0][5], '#')[8]}
						</c:when>
						<c:when test="${roundPosition.count==10}">
							${fn:split(reportLocalizedDetails[0][5], '#')[9]}
						</c:when>
						<c:otherwise>&#160;</c:otherwise>
					</c:choose>
					${reportLocalizedDetails[0][6]} - ${formattedNumberOfQuestionsInGivenRound} [
					<c:choose>
						<c:when test="${roundVO.firstElementInGivenRound==roundVO.lastElementInGivenRound}">
							${roundVO.firstElementInGivenRound}
						</c:when>
						<c:otherwise>
							${roundVO.firstElementInGivenRound} ${reportLocalizedDetails[0][7]} ${roundVO.lastElementInGivenRound}
						</c:otherwise>
					</c:choose>					
					]
					<div style="margin-top: 10px;font-size: 3pt;">&#160;</div>
				</c:forEach>
				<div style="margin-left: 120px;">
					<div style="font-size: 14pt;">
						${reportLocalizedDetails[0][8]} - ${totalNumberOfDevices}
					</div>
					<div style="margin-top: 10px;font-size: 12pt;">
						----------------
					</div>
				</div>				
			</div>
			<div style="margin-top: 10px;font-size: 14pt;">
				${reportLocalizedDetails[0][9]}
			</div>
			<div style="margin-top: 10px;font-size: 6pt;">&#160;</div>
			<c:forEach items="${roundVOs}" var="roundVO" varStatus="roundPosition">
				<div style="margin-top: 10px;font-size: 14pt;">
					<c:choose>
						<c:when test="${roundPosition.count==1}">
							${fn:split(reportLocalizedDetails[0][10], '#')[0]}
						</c:when>
						<c:when test="${roundPosition.count==2}">
							${fn:split(reportLocalizedDetails[0][10], '#')[1]}
						</c:when>
						<c:when test="${roundPosition.count==3}">
							${fn:split(reportLocalizedDetails[0][10], '#')[2]}
						</c:when>
						<c:when test="${roundPosition.count==4}">
							${fn:split(reportLocalizedDetails[0][10], '#')[3]}
						</c:when>
						<c:when test="${roundPosition.count==5}">
							${fn:split(reportLocalizedDetails[0][10], '#')[4]}
						</c:when>
						<c:when test="${roundPosition.count==6}">
							${fn:split(reportLocalizedDetails[0][10], '#')[5]}
						</c:when>
						<c:when test="${roundPosition.count==7}">
							${fn:split(reportLocalizedDetails[0][10], '#')[6]}
						</c:when>
						<c:when test="${roundPosition.count==8}">
							${fn:split(reportLocalizedDetails[0][10], '#')[7]}
						</c:when>
						<c:when test="${roundPosition.count==9}">
							${fn:split(reportLocalizedDetails[0][10], '#')[8]}
						</c:when>
						<c:when test="${roundPosition.count==10}">
							${fn:split(reportLocalizedDetails[0][10], '#')[9]}
						</c:when>
						<c:otherwise>${fn:split(reportLocalizedDetails[0][10], '#')[10]}</c:otherwise>
					</c:choose>
				</div>
				<div style="margin-top: 10px;" align="center">
					<table border="1" style="width: 640px;">
						<thead>
							<tr bgcolor="green">
								<td width="8%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;color: white;text-align: center;">
									${fn:split(reportLocalizedDetails[0][11], '#')[0]}
								</td>
								<td width="16%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;color: white;text-align: center;">
									${fn:split(reportLocalizedDetails[0][11], '#')[1]}
								</td>
								<td width="38%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;color: white;text-align: center;">
									${fn:split(reportLocalizedDetails[0][11], '#')[2]}
								</td>
								<td width="38%" style="font-size: 13pt;font-weight: bold;vertical-align: middle;color: white;text-align: center;">
									${fn:split(reportLocalizedDetails[0][11], '#')[3]}
								</td>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${roundVO.deviceVOs}" var="deviceVO" varStatus="roundPosition">
								<tr>
									<td width="8%" style="font-size: 13pt;font-weight: bold;vertical-align: top;text-align: center;">
										${deviceVO.serialNumber}
									</td>
									<td width="16%" style="font-size: 13pt;font-weight: bold;vertical-align: top;text-align: center;">
										<a href="#" id="viewQuestion_${deviceVO.number}" class="viewQuestion" style="">${deviceVO.formattedNumber}</a>						
									</td>
									<td width="38%" style="font-size: 13pt;font-weight: bold;vertical-align: top;text-align: left;padding-left: 5px;padding-right: 5px;">
										${deviceVO.memberNames}
									</td>
									<td width="38%" style="font-size: 13pt;font-weight: bold;vertical-align: top;text-align: left;padding-left: 5px;padding-right: 5px;">
										${deviceVO.subject}
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div style="margin-top: 10px;font-size: 6pt;">&#160;</div>
			</c:forEach>	
			<div style="margin-top: 25px;" align="center">
				<table border="0" style="width: 640px;">
					<tbody>
						<tr>
							<td width="30%" style="text-align: left;font-size: 13pt;font-weight: bold;">
								${fn:split(reportLocalizedDetails[0][12], '#')[0]} :
							</td>
							<td width="40%" style="font-size: 13pt;font-weight: bold;">
								&#160;
							</td>
							<td width="30%" style="text-align: center;font-size: 13pt;font-weight: bold;">
								${userName}
							</td>							
						</tr>
						<tr>
							<td width="30%" style="text-align: left;font-size: 13pt;font-weight: bold;">
								${sessionPlace}
							</td>
							<td width="40%" style="font-size: 13pt;font-weight: bold;">
								&#160;
							</td>
							<td width="30%" style="text-align: center;font-size: 13pt;font-weight: bold;">
								${userRole}
							</td>							
						</tr>
						<tr>
							<td width="30%" style="text-align: left;font-size: 13pt;font-weight: bold;">
								${fn:split(reportLocalizedDetails[0][12], '#')[1]} : ${publishingDate}
							</td>
							<td width="40%" style="font-size: 13pt;font-weight: bold;">
								&#160;
							</td>
							<td width="30%" style="text-align: center;font-size: 13pt;font-weight: bold;">
								${reportLocalizedDetails[0][0]}
							</td>							
						</tr>
					</tbody>
				</table>
			</div>	
			<div style="margin-top: 5px;">
				_______________________________________________________________________
			</div>
			<div style="margin-top: 5px;font-size: 12pt;font-weight: normal;" align="center">
				${fn:split(reportLocalizedDetails[0][12], '#')[2]}
			</div>
			<div style="margin-top: 5px;font-size: 12pt;font-weight: normal;" align="center">
				${fn:split(reportLocalizedDetails[0][12], '#')[3]}, ${sessionPlace}
			</div>
		</div>
	</div>
	<input type="hidden" id="session" value="${session}">
	<input type="hidden" id="deviceType" value="${deviceType}">
</body>
</html>