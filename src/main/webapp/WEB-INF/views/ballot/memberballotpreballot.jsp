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
			font-size: 16px !important;
			width: 700px !important;		
			border: 1px;
		}
		.uiTable td{
			padding: 14px;
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
		.clsDate{
			margin-top: -23px;
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
	<th colspan="3">
	<h1 style="text-align: center;font-weight: bold;font-size: 22px;color: black; margin: 0px;"><spring:message code="preballot.topheader.council" text="Maharashtra Legislature"></spring:message></h1>
	<h2 style="text-align: center;font-weight: bold;font-size: 18px; margin: 0px;"><spring:message code="preballot.topheader" text="Vidhan Parishad Question Ballot"></spring:message></h2>
	<h5 style="text-align: center;font-weight: normal;font-size: 16px;"><spring:message code="preballot.topheader.${round }" text="Round ${round } Preballot List"></spring:message></h5><br />
	<c:choose>
		<c:when test="${attendance=='true' }">
			<h3 style="text-align: center; font-weight: normal; font-size: 18px; margin: 0px; padding: 0px;"><spring:message code="preballot.presentmember.name" text="Present Members"/></h3>
		</c:when>
		<c:otherwise>
			<h3 style="text-align: center; font-weight: normal; font-size: 18px; margin: 0px; padding: 0px;"><spring:message code="preballot.absentmember.name" text="Absent Members"/></h3>	
		</c:otherwise>
	</c:choose>
	<p style="text-align: right;font-weight: normal;font-size: 14px; margin-top: -28px;margin-bottom:0px; float: right; margin-right: 15px;">
		<spring:message code="preballot.topheader.date" text="Date"></spring:message>-${currentdate}
	</p>
	</th>	
	</tr>
	<tr>
	<th class="centeralign" style="font-size: 16px; width: 100px; text-align: center;"><spring:message code="preballot.position" text="S.no"/></th>
	<th class="leftalign" style="font-size: 16px;">
	<c:choose>
		<c:when test="${attendance=='true' }">
			<spring:message code="preballot.presentmember" text="Present Members"/>
		</c:when>
		<c:otherwise>
			<spring:message code="preballot.absentmember" text="Absent Members"/>	
		</c:otherwise>
	</c:choose>
	</th>	
	<th>&nbsp;</th>
	</tr>
	</thead>
	<tbody>	
	<c:forEach items="${selectedItems}" var="i">
	<tr>
		<td class="round${round } centeralign" style="width: 100px; text-align: center;">${i.formatPosition()}</td>
		<td class="round${round }">${i.member.findFirstLastName()}</td>
		<td>&nbsp;</td>		
	</tr>
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<p class="footerAuthoritiesForSignature" style="text-align: left;padding-left: 15px;font-weight: normal;font-size: 14px; float: left; padding-right: 15px;">
					<br/><br/><br/><br/><br/>
					<spring:message code="memberballot.footer.section_officer_label" text="Section Officer"></spring:message>
					<br/><br/><br/>
					<spring:message code="memberballot.footer.under_secretary_committee_label" text="Under Secretary Committee"></spring:message>
					<br/><br/><br/>
					<spring:message code="memberballot.footer.under_secretary_label" text="Under Secretary"></spring:message>
					<br/><br/><br/>
					<spring:message code="memberballot.footer.deputy_secretary_label" text="Deputy Secretary"></spring:message>
					<br/><br/><br/>
					<spring:message code="memberballot.footer.secretary_label" text="Secretary"></spring:message>
				</p>
			</td>
			
			<td valign="top">
				<p class="footerRole" style="text-align: center;font-weight: normal;font-size: 16px; float: right; padding-right: 15px;"><br />${role }<br>
				<spring:message code="memberballot.footer.council" text="Maharashtra Legislature"></spring:message></p>
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