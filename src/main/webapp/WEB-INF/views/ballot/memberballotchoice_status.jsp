<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function() {	
			$('.viewChoicesRevision').click(function() {
				var memberId = $(this).attr('id').split("_")[1];
				$.get('ballot/memberballotchoice/revisions/'+memberId+'/session/'+$("#sessionId").val(), function(data) {
				    $.fancybox.open(data,{autoSize: false, width: 800, height:700});
			    }).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			    return false;
			});
		});
	</script>
	<style type="text/css" media="screen">
		#reportDiv{
        	width: 800px;
        	padding: 10px;
        	page: auto;
        }
	</style>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<br><br>
	<div id="reportDiv">
	<table class="strippedTable" border="1" style="width: 800px;">
		<thead>		
			<tr>
				<th class="serialCount" style="text-align: center; font-size: 12px; width: 8px;">${colHeaders[0]}</th>
				<th style="text-align: center; font-size: 12px; width: 300px;">${colHeaders[1]}</th>
				<th style="text-align: center; font-size: 12px; width: 250px;">${colHeaders[2]}</th>
				<th style="text-align: center; font-size: 12px; width: 200px;">${colHeaders[3]}</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${report}" var="r" varStatus="counter">	
				<tr>
					<td class="serialCount" style="text-align: left; font-size: 12px; width: 8px;">${serialNumbers[counter.count-1]}</td>
					<td style="text-align: left; font-size: 12px; width: 300px;">${r[2]}</td>
					<td style="text-align: left; font-size: 12px; width: 250px;">
						<c:choose>
							<c:when test="${r[3]==0}">${statusMessages[0]}</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]==0}">${statusMessages[2]}</c:when>
									<c:otherwise>
										<b>${statusMessages[1]}</b>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</td>
					<td style="text-align: center; font-size: 12px; width: 200px;">
						<a href="#" id="viewChoicesRevision_${r[1]}" class="viewChoicesRevision"><spring:message code="question.viewrevisions" text="View Revisions"/></a>						
					</td>
				</tr>
			</c:forEach>
		</tbody>	
	</table>
	</div>
	<input type="hidden" id="sessionId" name="sessionId" value="${session}">
	<input type="hidden" id="questionType" name="questionType" value="${questionType}">
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>
