<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="bill" text="Bill Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly?v=45" />
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
	</script>
	 <style type="text/css">
        @media print {
            #reportDiv{
            	width: 800px;
            	margin-left: 80px;
            }
            #reportDiv table{
            	width: 100%;
            }
        }
        @media screen{
        	.patrakbhagdontable{
        		box-shadow: 2px 2px 2px #000000; 
        	 	border: 1px solid black;
        	}
        }
        
        .patrakheader{
        	text-align: center;
        	text-decoration: underline;
        	font-size: 16px;
        	font-family: fantasy;
        	font-weight: bold;
        	width: 100%;
        }
        .patrakbody{
        	font-size: 12px;
        	font-family: fantasy;
        	width: 100%;
        }
        
        .patrakbhagdontable{
        	width: 100%;
        }
        
        .patrakbodyhead{
        	text-decoration: underline;
        	font-size: 14px;;
        	font-family: fantasy;
        	width: 100%;
        }
        
        .center{
        	text-align: center;
        }
    </style>
</head> 

<body>

<div class="fields clearfix watermark">
	<div id="reportDiv">
		<%@ include file="/common/info.jsp" %>
				<c:choose>
					<c:when test="${report==null}">
						<spring:message code="bill.nodata" text="Nothing found."/>
					</c:when>
					<c:when test="${empty report}">
						<spring:message code="bill.nodata" text="Nothing found."/>
					</c:when>
					<c:otherwise>
						<table class="patrakbhagdontable">
							<thead>
								<tr>
									<th class="patrakheader" colspan="3">
										<spring:message code="bill.patrakbhag2.state" text="State" />&nbsp;${house}
									</th>
								</tr>
								<tr>
									<th class="patrakheader" colspan="3"><spring:message code="bill.patrakbhag2.name" text="Patrak Bhag Don"></spring:message></th>
								</tr>
								<tr>
									<th class="patrakheader" colspan="3">${formattedCurrentDay}, <spring:message code="generic.date" text="Date" /> ${formattedCurrentDate}/${indianDateFormatCurrentDate} </th>
								</tr>
							</thead>
							<tbody>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3" style="text-align: justify;"><spring:message code="bill.patrakbhag2.commontext" text="Common Start" ></spring:message></td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<c:forEach items="${report}" var="b" varStatus="row">
									<tr style="margin-top: 20px;">
										<td style="font-weight: bold; font-size: 14px; text-align: justify; margin-left: 20px;" colspan="3">
											"${san} ${sessionYear} ${houseShort} <spring:message code="bill.patrakbhag2.srno" text="Number"></spring:message> ${b[1]} - ${b[2]}, ${sessionYear}"
										</td>
									</tr>
								</c:forEach>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr>
									<td colspan="3">
										&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="bill.patrakbhag2.lowercontent1" /> ${formattedDistributionDay}, <spring:message code="generic.date" text="Date" /> ${formattedDistributionDate} <spring:message code="bill.patrakbhag2.lowercontent2" />. 
									</td>
								</tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr>
									<td><spring:message code="bill.patrakbhag2.mls" text="M L S"></spring:message>,</td><td>&nbsp;</td><td class="center" style="font-weight: bold;">${who},</td>
								</tr>
								<tr>
									<td>${sessionPlace},</td><td>&nbsp;</td><td class="center">${whopost},</td>
								</tr>
								<tr>
									<td><spring:message code="generic.date" text="Date" />: ${formattedCurrentDate},</td><td>&nbsp;</td><td class="center"><spring:message code="bill.patrakbhag2.state" text="State" />&nbsp;${house}</td>
								</tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr><td colspan="3">&nbsp;</td></tr>
								<tr>
									<td colspan="3">
										${footer}
									</td>
								</tr>
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