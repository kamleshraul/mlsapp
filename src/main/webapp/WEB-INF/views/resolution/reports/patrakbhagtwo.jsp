<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="resolution" text="Resolution Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
	</script>
	 <style type="text/css" media="all">
        /* @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
           		display:none;
            }
        } */
        
        .patrakheader{
        	text-align: center;
        	text-decoration: underline;
        	font-size: 16px;;
        	font-family: fantasy;
        	font-weight: bold;
        	width: 100%;
        }
        .patrakbody{
        	font-size: 15px;
        	font-family: fantasy;
        	width: 100%;
        }
        .patrakbhagdontable{
        	width: 800px;
        }
        
        .patrakbodyhead{
        	text-decoration: underline;
        	font-size: 15px;
        	font-family: fantasy;
        	width: 100%;
        }
        
        .patrakbhag2footertd{
        	width: 100%;;
        }
        
        .patrakfootercontentright{
			width: 350px; 
			text-align: center;  
			font-size: 15px;      
        }
        
         .patrakfootercontentleft{
			width: 350px; 
			text-align: left;  
			font-size: 15px;      
        }
    </style>
    
    <style type="text/css" media="print">
    	@media print{
        	div#reportDiv{
        		width: 800px;
        	}
        } 
    </style>
    
    <link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
</head> 

<body>
<div class="fields clearfix watermark">
	<div id="reportDiv">
		<div id="patrakbahg2Div">
			<%@ include file="/common/info.jsp" %>
			<c:choose>
				<c:when test="${report==null}">
					<spring:message code="resolution.noballot" text="Ballot not yet done."/>
				</c:when>
				<c:when test="${empty report}">
					<spring:message code="resolution.noballot" text="No resolution have come in ballot."/>
				</c:when>
				<c:otherwise>
					<div>
						<table class="patrakbhagdontable">
							<thead>
								<tr><th style="text-align: right; text-decoration: underline; font-weight: bold;"><spring:message code="generic.serialnumber" text="Sr. No.">:&nbsp;</spring:message></th></tr>
								<tr>
									<th class="patrakheader">
										<c:choose>
											<c:when test="${houseType=='lowerhouse'}">
												<spring:message code="generic.lowerhouse" text="Lower House" />
											</c:when>
											<c:when test="${housetype=='upperhouse'}">
												<spring:message code="generic.upperhouse" text="Upper House" />
											</c:when>
										</c:choose>
									</th>
								</tr>
								<tr>
									<th class="patrakheader"><spring:message code="resolution.patrakbhag2" text="Post Ballot Report"></spring:message></th>
								</tr>
								<tr>
									<th class="patrakheader">${formattedCurrentDay}, <spring:message code="generic.date" text="Date" /> ${formattedCurrentDate} / ${patrakbhag2indianDateFormat}</th>
								</tr>
							</thead>								
							<tbody>
								<tr><td colspan="2">&nbsp;</td></tr>
								<tr>
									<td class="patrakbody">
										&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="resolution.patrakbhag2.headerp_one" text="Patrak Bhag 2 Content one"></spring:message>
										${formattedDiscussionDay}, <spring:message code="generic.date" text="Date" /> ${formattedDiscussionDate}
										<spring:message code="resolution.patrakbhag2.headerp_two" text="Patrak Bhag 2 Content Two"></spring:message>
									</td>
								</tr>
								<tr><td colspan="2">&nbsp;</td></tr>
								<c:forEach items="${report}" var="r" varStatus="row"> 
									<tr style="margin-top: 20px;">
										<td>
											<span class="patrakheader">(${counter[row.count].value}) ${r[0]}, 
											<c:choose>
												<c:when test="${houseType =='lowerhouse'}">
													<spring:message code="resolution.patrakbhag2.lowerhousemember" text="Number" />
												</c:when>
												<c:when test="${houseType =='upperhouse'}">
													<spring:message code="resolution.patrakbhag2.upperhousemember" text="Number" />
												</c:when>
											</c:choose> ${r[2]} </span><br />
											<span style="font-size: 14px; margin-top: 10px;text-align:justify;" >${r[4]}</span>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<div id="patrakbhag2footer">
						<table class="patrakbhagdontable">
							<tr>
								<td>&nbsp;</td>
								<td class="patrakfootercontentright"><b>${authorityName}</b></td>
							</tr>
							<tr>
								<td class="patrakfootercontentleft"><spring:message code="generic.vidhanbhavan" text="Vidhan Bhavan" />,</td>
								<td class="patrakfootercontentright">${userRole}</td>
							</tr>
							<tr>
								<td class="patrakfootercontentleft">${sessionPlace},</td>
								<td class="patrakfootercontentright">
									<c:choose>
										<c:when test="${houseType=='lowerhouse'}">
											<spring:message code="generic.lowerhouse" text="Lower House" />
										</c:when>
										<c:when test="${housetype=='upperhouse'}">
											<spring:message code="generic.upperhouse" text="Upper House" />
										</c:when>
									</c:choose>
								</td>
							</tr>
							<tr>
								<td class="patrakfootercontentleft"><spring:message code="generic.date" text="Date"/>: ${formattedCurrentDate}</td>
								<td class="patrakfootercontentright">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2" style="font-size: 14px;">${footer}</td>
							</tr>
						</table>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>	
</div>
</body>
</html>