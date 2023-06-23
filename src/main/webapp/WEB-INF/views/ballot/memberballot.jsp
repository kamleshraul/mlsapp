<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	$(document).ready(function(){
		$("#with").click(function(){
			$(".question").show();
		});
		$("#without").click(function(){
			$(".question").hide();
		});
		//$(".question").hide();
		
		$('#memberballot_pdf').click(function() {
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val()
			 +"&round="+$("#selectedRound").val()
			 +"&attendance="+$("#selectedAttendance").val()
			 +"&noofrounds="+$("#noOfRounds").val()
			 +"&group="+$("#mbGroup").val()
			 +"&answeringDate="+$("#mbAnsweringDate").val()
			 + "&outputFormat=PDF";
			var resourceURL = 'ballot/memberballot/report?'+ parameters;			
			$(this).attr('href', resourceURL);
		});
		
		$('#memberballot_word').click(function() {
			var parameters = "session="+$("#session").val()
			 +"&questionType="+$("#questionType").val()
			 +"&round="+$("#selectedRound").val()
			 +"&attendance="+$("#selectedAttendance").val()
			 +"&noofrounds="+$("#noOfRounds").val()
			 +"&group="+$("#mbGroup").val()
			 +"&answeringDate="+$("#mbAnsweringDate").val()
			 + "&outputFormat=WORD";
			var resourceURL = 'ballot/memberballot/report?'+ parameters;			
			$(this).attr('href', resourceURL);
		});
	});
	</script>
	<style type="text/css" media="screen">
	.true{
	font-size: 14px;
	}
	.false{
	font-size: 14px;
	}
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
	.withQuestion,.withoutQuestion{
	font-size: 14px;
	font-weight: bolder;
	margin-right: 20px;
	cursor: pointer;
	}
	.withQuestion:HOVER{
		color:blue;
		font-size: 16px;
	}
	.withoutQuestion:HOVER{
		color:blue;
		font-size: 16px;
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
			padding-top: 14px;
			padding-bottom: 14px;
		}
		#controls, .exportLink{			
			display: none !important;
		}		
		.centeralign{
		text-align: center;
		}
		.leftalign{
		text-align: left;
		}
		.clsDate{
			margin-top: -23px !important;
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
<c:when test="${!(empty memberBallots)}">
<a id="memberballot_pdf" class="exportLink" href="#" style="text-decoration: none;">
	<img src="./resources/images/pdf_icon.jpg" alt="Export to PDF" width="32" height="32">
</a>
&nbsp;&nbsp;
<a id="memberballot_word" class="exportLink" href="#" style="text-decoration: none;">
	<img src="./resources/images/word_icon.jpg" alt="Export to WORD" width="32" height="32">
</a>
<h4 id="controls"><span class="withQuestion"><a id="with">+</a></span><span class="withoutQuestion"><a id="without">-</a></span></h4>
<table class="uiTable" style="width:650px;">
	<thead>
		<tr>
			<th colspan="3">
			<h1 style="text-align: center;font-weight: bold;font-size: 22px;color: black; margin: 0px;color: black;"><spring:message code="memberballot.topheader.council" text="Maharashtra Legislature"></spring:message></h1>			
			<h2 style="text-align: center;font-weight: bold;font-size: 18px; margin: 0px;"><spring:message code="memberballot.topheader" text="Vidhan Parishad Question Ballot"></spring:message></h2>
			<h5 style="text-align: center;font-weight: normal;font-size: 16px;"><spring:message code="memberballot.topheader.${round }" text="Round ${round } Member Ballot List"></spring:message></h5><br/>
			<c:choose>
				<c:when test="${attendance=='true' }">
					<h3 style="text-align: center; font-weight: normal; font-size: 18px; margin: 0px; padding: 0px;"><spring:message code="memberballot.presentmember.name" text="Present Members"/></h3>
				</c:when>
				<c:otherwise>
					<h3 style="text-align: center; font-weight: normal; font-size: 18px; margin: 0px; padding: 0px;"><spring:message code="memberballot.absentmember.name" text="Absent Members"/></h3>	
				</c:otherwise>
			</c:choose>
			<p style="text-align: right;font-weight: normal;font-size: 14px; margin-top: -28px;margin-bottom:0px; float: right; margin-right: 15px;">
				<spring:message code="preballot.topheader.date" text="Date"></spring:message>-${currentdate}
			</p>
			</th>	
		</tr>		
		<tr>
			<th class="centeralign" style="font-size: 16px; width: 100px; text-align:"><spring:message code="memberballot.position" text="S.no"/>
			</th>
			<th class="leftalign" style="font-size: 16px;">
			<c:choose>
			<c:when test="${attendance=='true' }">
			<spring:message code="memberballot.presentmember" text="Present Members"/>
			</c:when>
			<c:otherwise>
			<spring:message code="memberballot.absentmember" text="Absent Members"/>	
			</c:otherwise>
			</c:choose>
			</th>	
			<th class="question centeralign" style="font-size: 16px;">	
			<spring:message code="memberballot.question" text="Question No."/>
			</th>		
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${memberBallots}" var="i">	
			<tr>
				<td class="round${i.round } centeralign" style="text-align: center; width: 100px;">${i.position}</td>
				<td class="round${i.round }">${i.member}</td>	
				<td class="question">
				<c:choose>
				<c:when test="${!(empty i.questions)}">
				<c:forEach items="${i.questions}" var="j">
				<c:choose>
				<c:when test="${!(empty j.parentNumber) }">
				<div style="margin-right:5px;" class="${i.attendance} round${i.round}">${j.number}&nbsp;&nbsp;(${j.answeringDate})&nbsp;&nbsp;(${j.parentNumber} <spring:message code="memberballot.clubbedto" text="is Parent"></spring:message>)</div>
				</c:when>
				<c:otherwise>
				<div style="margin-right:5px;" class="${i.attendance} round${i.round}">${j.number}&nbsp;&nbsp;(${j.answeringDate})</div>
				</c:otherwise>
				</c:choose>
				</c:forEach>
				</c:when>
				<c:otherwise>
				
				</c:otherwise>
				</c:choose>
				</td>		
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
					<br/><br/><br/>
					<spring:message code="memberballot.footer.principal_secretary_label" text="Principal Secretary"></spring:message>
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
	<spring:message code="memberballot.noballot" text="No Member Ballot Found"/>
</c:otherwise>
</c:choose>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
