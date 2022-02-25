<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//====================================================
			//for resolution balloting and approval workflow
			$("#discuss").click(function(){
				var postURL = "resolution/discussresolutions";
				/* $("input[type=checkbox]:checked").each(function(){
					alert($(this).val());
				}); */
				$(this).attr('disabled','disabled');
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post(postURL,
					$('form').serialize(), function(data){
					$.unblockUI();
				}).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.");
					}
					scrollTop();
				});
				//viewBallot();
			});
		});
	</script>
	
	<link rel="stylesheet" type="text/css" href="./resources/css/printerfriendly.css?v=3" media="print" />
	
</head>

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="reportDiv">
<c:choose>
	<c:when test="${ballotVOs == null}">
		<spring:message code="resolution.ballot.notCreated" text="Ballot is not Created"/>
	</c:when>

	<c:when test="${empty ballotVOs}">
		<spring:message code="resolution.ballot.noEntries" text="There are no entries in the Ballot"/>
	</c:when>

	<c:otherwise>
		<label class="small"><spring:message code="generic.date" text="Discussion Date"/>: ${answeringDate}</label>
		<form action="resolution/discussresolutions" method="post" >
			<div>
				<table class="strippedTable" border="1">
					<tr>
						<th><spring:message code="general.srnumber" text="Serial Number"/></th>
						<th><spring:message code="member.name" text="Member Name"/></th>
						<th><spring:message code="resolution.number" text="Resolution Number"/></th>
						<%-- <th><spring:message code="resolution.subject" text="Subject"/></th> --%>
						<th><spring:message code="resolution.selectfordiscussion" text="To Be Discussed"/></th>
					</tr>
			
					<c:forEach items="${ballotVOs}" var="report" varStatus="counter">
						<tr>
							<td>${serialnumber[counter.count - 1].name}</td>
							<td>${report[2]}</td>
							<td>${report[3]}</td>
							<%-- <td>${report[4]}</td> --%>
							<td align="center">
								<c:choose>
									<c:when test="${report[6]=='checked'}">
										<input type="checkbox" name="tobeDiscussed" value="${report[0]}" checked="checked"/>
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="tobeDiscussed" value="${report[0]}" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<br />
				<spring:message code="generic.date" text="Answering Date"/> ${answeringDate}<spring:message code="resolution.ballot.starred.lowerhouse.footer1" text="Footer"/>
								${formattedCurrentDate}<spring:message code="resolution.ballot.starred.lowerhouse.footer2" text="Footer"/>
			<hr />
			<div id="submitDiv" style="width: 800px; display: none;">
				<%-- <c:if test="${workflowstatus!='COMPLETED' }">	
					<p>
						<label class="small"><spring:message code="resolution.putupfor" text="Put up for"/></label>
						<select id="changeInternalStatus" class="sSelect">
						<option value="-"><spring:message code='please.select' text='Please Select'/></option>
						<c:forEach items="${internalStatuses}" var="i">
							<c:choose>
								<c:when test="${i.id==internalStatus }">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
								</c:when>
								<c:otherwise>
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</select>
						
						<select id="internalStatusMaster" style="display:none;">
						<c:forEach items="${internalStatuses}" var="i">
						<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
						</c:forEach>
						</select>	
					</p>
						
					<p id="actorDiv" style="display:none;">
						<label class="small"><spring:message code="resolution.nextactor" text="Next Users"/></label>
						<select id="actor" name="actor" class="sSelect">	
						<c:forEach items="${actors}" var="i">
						<option value="${i.id}">${i.name}</option>
						</c:forEach>
						</select>
					</p>		
				</c:if> --%>
				<input type="hidden" name="allids" value="${ids}" />
				<div style="float: right;">
					<input type="button" id="discuss" value='<spring:message code="resolution.discuss" text="Submit to Discuss"></spring:message>' class="butDef" />
				</div> 
			</div> 
		</form>
		<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</c:otherwise>
</c:choose>
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>