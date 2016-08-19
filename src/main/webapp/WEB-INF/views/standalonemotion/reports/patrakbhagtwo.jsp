<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="hds" text="Half Hour Discussion - General"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
	</script>
	 <style type="text/css">
        @media all{
        
	        .patrakheader{
	        	text-align: center;
	        	text-decoration: underline;
	        	font-size: 14pt;
	        	font-family: fantasy;
	        	font-weight: bold;
	        	width: 100%;
	        }
	        .patrakbody{
	        	font-size: 12pt;
	        	font-family: fantasy;
	        	width: 100%;
	        }
	        .patrakbhagdontable{
	        	width: 750px;
	        }
	        
	        .patrakbodyhead{
	        	text-decoration: underline;
	        	font-size: 12pt;
	        	font-family: fantasy;
	        	width: 100%;
	        }
	        
	        #reportDiv{
	        	border: 1px solid;
	        	width: 750px;
	        	padding: 10px;
	        }
	        
	        .patrakmember{
	        	font-size: 13pt;
	        	font-family: fantasy;
	        	font-weight: bold;
	        	text-decoration: underline;
	        }
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	<div id="patrakbahg2Div">
		<%@ include file="/common/info.jsp" %>
				<c:choose>
					<c:when test="${ballotVOs==null}">
						<spring:message code="hds.noballot" text="Ballot not yet done."/>
					</c:when>
					<c:when test="${empty ballotVOs}">
						<spring:message code="hds.noballot" text="No discussion has come in ballot."/>
					</c:when>
					<c:otherwise>
						<table class="patrakbhagdontable">
							<thead>
								<tr>
									<th class="patrakheader"><spring:message code="generic.state" text="State"></spring:message>&nbsp; <spring:message code="generic.lowerhouse" text="Lower House"></spring:message></th>
								</tr>
								<tr>
									<th class="patrakheader"><spring:message code="hds.patrakbhag2" text="Post Ballot Report"></spring:message></th>
								</tr>
								<tr>
									<th class="patrakheader">${formattedCurrentDay}, <spring:message code="generic.date" text="Date" /> ${formattedCurrentDate}</th>
								</tr>
							</thead>
							<br />
							<tbody>
								<tr><td>&nbsp;</td></tr>
								<tr>
									<td class="patrakbody">
										&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="hds.patrakbhag2.headerp_one" text="Patrak Bhag 2 Content"></spring:message>
										${formattedDiscussionDay}, <spring:message code="generic.date" text="Date" /> ${formattedDiscussionDate}
										<spring:message code="hds.patrakbhag2.headerp_two" text="Patrak Bhag 2 Content"></spring:message>
									</td>
								</tr>
								<tr><td>&nbsp;</td></tr>
								<c:forEach items="${ballotVOs}" var="b" varStatus="row"> 
									<tr style="margin-top: 20px;">
										<td>
											<span class="patrakmember">(${counter[row.count].value}) ${b.memberName}, <spring:message code="hds.patrakbhag2.lowerhousemember" text="Number" /> ${b.number} </span><br />
											${b.questionText}
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
	</div>
</div>
</body>
</html>