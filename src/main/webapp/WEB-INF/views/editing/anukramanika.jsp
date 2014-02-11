<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="editing.anukramanika" text="Anukramanika" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
		function produceAnukramanikaReport(element, repType){
			var params='houseType=' + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val()
			+ '&fromDay=' + $("#fromDay").val()
			+ '&toDay=' + $("#toDay").val()
			+ '&userGroup=' + $("#userGroup").val()
			+ '&userGroupType=' + $("#userGroupType").val()
			+ '&outputFormat=' + repType;
			
			var reportURL = 'editing/anukramanikareport?' + params;
			$(element).attr('href', reportURL);
		}
		
		$(document).ready(function() {	
			$("#anukramanika_pdf").click(function(){
				produceAnukramanikaReport(this, "PDF");
			});
			$("#anukramanika_word").click(function(){
				produceAnukramanikaReport(this, "WORD");
			});
		});
	</script>
	<style type="text/css">
		.dataBold{
			font-weight: bold;
			font-size: 12pt;
			margin-left: 12px;
		}
		
		.dataNorm{
			font-size: 12pt;
			margin-left: 30px;
		}
		
		.headerDate{
			border: 1px solid black;
			font-size: 12pt;
			text-align: center;
			margin: 10px;
			font-weight: bold; 
			padding: 5px;
		}
		
		.anukram{
			font-size: 12pt;
			text-align: center;
			margin: 10px;
			font-weight: bold;
		}
	</style>
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=48" media="print" /> 
</head>

<body>
<div>
	<a id="anukramanika_pdf" class="exportLink" href="javascript:void(0);" style="text-decoration: none; margin-left: 40px;" target="_blank">
		<img class="imgN" src="./resources/images/pdf_icon.png" alt="Export to PDF" width="24px" height="32px" title="<spring:message code='editing.anukramanika.pdf' text='Anukramanika In PDF' />">
	</a>
	&nbsp;&nbsp;
	<a id="anukramanika_word" class="exportLink" href="javascript:void(0);" style="text-decoration: none;" target="_blank">
		<img class="imgN" src="./resources/images/word_icon.png" alt="Export to WORD" width="33px" height="32px" title="<spring:message code='editing.anukramanika.doc' text='Anukramanika In Word' />">
	</a>
</div>

<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h5 style="color: #FF0000;">${error}</h5>
</c:if>
<c:choose>
<c:when test="${report == null}">
	<spring:message code="editing.anukramanika.notCreated" text="Anukramanika is not ready."/>
</c:when>

<c:when test="${empty report}">
	<spring:message code="editing.anukramanika.notCreated" text="Anukramanika is not ready."/>
</c:when>

<c:otherwise>
	<div id="reportDiv" style="width: 900px;">
		<c:set var="device" value="-" />
		<c:set var="rodate" value="-" />
		<c:forEach items="${report}" var="r" varStatus="counter">
			<c:if test="${fn:length(r[1])>0}">
				<div class="anuData">
					<c:if test="${rodate!=r[11]}">
						<div class="headerDate pagebreak">
							${r[11]}
						</div>
						<c:if test="${counter.count==1}">
							<div class="anukram">
								<spring:message code="editing.anukramanika" text="Index"></spring:message>
							</div>
						</c:if>
					</c:if>
					<c:choose>
						<c:when test="${r[9] != null}">
							<c:if test="${device!=r[9]}">
								<c:if test="${fn:startsWith(r[9],'questions_') and counter.count==1}">
									<span class="dataBold">
										<spring:message code="editing.questions" text="Question" />
									</span>
								</c:if>
							</c:if>
							<br />
							<span class="dataNorm">${r[1]}</span>
						</c:when>
						<c:otherwise>
							<br />
							<span class="dataBold">${r[1]}</span>
						</c:otherwise>						
					</c:choose>
				</div>
			</c:if>
			<c:set var="device" value="${r[9]}"/>
			<c:set var="rodate" value="${r[11]}" />
		</c:forEach>
	</div>
</c:otherwise>
</c:choose>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>

</html>