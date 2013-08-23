<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {		
			$('#memberpreballot_pdf').click(function() {
				var parameters = "session="+$("#session").val()
				 +"&questionType="+$("#questionType").val()
				 +"&round="+$("#selectedRound").val()
				 +"&attendance="+$("#selectedAttendance").val()
				 +"&noofrounds="+$("#noOfRounds").val()
				 + "&outputFormat=PDF";
				var resourceURL = 'ballot/memberballot/preballotreport?'+ parameters;			
				$(this).attr('href', resourceURL);
			});
			
			$('#memberpreballot_word').click(function() {
				var parameters = "session="+$("#session").val()
				 +"&questionType="+$("#questionType").val()
				 +"&round="+$("#selectedRound").val()
				 +"&attendance="+$("#selectedAttendance").val()
				 +"&noofrounds="+$("#noOfRounds").val()
				 + "&outputFormat=WORD";
				var resourceURL = 'ballot/memberballot/preballotreport?'+ parameters;			
				$(this).attr('href', resourceURL);
			});
		});
	</script>
	<style type="text/css" media="screen">
	.round1{
	color:green ;
	}
	.round2{
	color:blue ;
	}
	.round3{
	color: red;
	}
	.round4{
	color: black;
	}
	.round5{
	color: #F26522;
	}
	</style>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=1" />
	<style type="text/css" media="print" >
		.uiTable{
			color: black !important;
			font-family: serif;
			font-size: 14pt !important;
			width: 650px !important;		
			border: 1px;
		}
		.exportLink{			
			display: none !important;
		}		
		.centeralign{
		text-align: center;
		}
		.leftalign{
		text-align: left;
		}	
	</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="reportDiv">
<c:choose>
<c:when test="${!(empty selectedItems)}">
<a id="memberpreballot_pdf" class="exportLink" href="#" style="text-decoration: none;">
	<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
</a>
&nbsp;
<a id="memberpreballot_word" class="exportLink" href="#" style="text-decoration: none;">
	<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
</a>
<table class="uiTable" style="width:650px;">
	<thead>
	<tr>
	<th colspan="2">
	<h4 style="text-align: center;font-weight: bold;font-size: 18px;color: black;"><spring:message code="preballot.topheader.council" text="Maharashtra Legislature"></spring:message></h4>
	<h5 style="text-align: center;font-weight: bold;font-size: 16px;"><spring:message code="preballot.topheader" text="Vidhan Parishad Question Ballot"></spring:message></h5><br>
	<h6 style="text-align: center;font-weight: normal;font-size: 14px;"><spring:message code="preballot.topheader.${round }" text="Round ${round } Preballot List"></spring:message></h6><br>
	<p style="text-align: right;font-weight: normal;font-size: 14px;">
	<spring:message code="preballot.topheader.date" text="Date"></spring:message>-${currentdate}</p>
	</th>	
	</tr>
	<tr>
	<th class="centeralign"><spring:message code="preballot.position" text="S.no"/></th>
	<th class="leftalign">
	<c:choose>
	<c:when test="${attendance=='true' }">
	<spring:message code="preballot.presentmember" text="Present Members"/>
	</c:when>
	<c:otherwise>
	<spring:message code="preballot.absentmember" text="Absent Members"/>	
	</c:otherwise>
	</c:choose>
	</th>	
	</tr>
	</thead>
	<tbody>	
	<c:forEach items="${selectedItems}" var="i">
	<tr>
		<td class="round${round } centeralign">${i.formatPosition()}</td>
		<td class="round${round }">${i.member.findFirstLastName()}</td>		
	</tr>
	</c:forEach>
	</tbody>
	<tfoot>
	<tr>
	<td colspan="2">
	<p style="text-align: right;font-weight: normal;font-size: 14px;">${role }<br>
	<spring:message code="preballot.footer.council" text="Maharashtra Legislature"></spring:message></p>
	</td>
	</tr>
	</tfoot>
</table>
</c:when>
<c:otherwise>
<spring:message code="preballot.nomember" text="No list found"/>
</c:otherwise>
</c:choose>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>