<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//====================================================
			//for resolution balloting and approval workflow
			$("#propriety_point_ballot").click(function(){
				$(this).attr('href', '#');
				console.log($("#deviceTypeId").val()+"--"+$("#answeringDate").val());
				var parameters = "sessionId="+$("#sessionId").val()
				+"&answeringDate="+$("#answeringDate").val()
				+"&deviceTypeId="+$("#deviceTypeId").val();	
				var resourceURL = 'proprietypoint/report/ppballotfop?'+ parameters;
				$(this).attr('href', resourceURL);
			});
			$("#discuss").click(function(){
				//var postURL = "resolution/discussresolutions";
				/* $("input[type=checkbox]:checked").each(function(){
					alert($(this).val());
				}); */
				/* $(this).attr('disabled','disabled');
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post(postURL,
					$('form').serialize(), function(data){
					$.unblockUI();
				}).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg")==undefined){
						
					}else{
						if($("#ErrorMsg").val()!=''){
							$.prompt($("#ErrorMsg").val());
						}else{
							$.prompt("Error");
						}
					}
				}); */
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
				<spring:message code="prois.ballot.notCreated" text="Ballot is not Created"/>
			</c:when>
		
			<c:when test="${empty ballotVOs}">
				<spring:message code="prois.ballot.noEntries" text="There are no entries in the Ballot"/>
			</c:when>
		
			<c:otherwise>
			    <div style="float:right;">
			       <a id="propriety_point_ballot" class="exportLink" href="#" style="text-decoration: none;">
				       <img src="./resources/images/word_new.png" alt="Export to WORD" width="32" height="32"/>
				   </a>
			    </div>
				<h2 style="text-align: center; color: #000000;"><spring:message code="prois.post.ballot.header" text="POST BALLOT LIST" /></h2>
				<label class="small"><spring:message code="generic.date" text="Discussion Date"/>: <span style="text-decoration: underline; font-weight: bold;">${answeringDate}</span>&nbsp;&nbsp;<spring:message code="prois.post.ballotdailymessage" text="Notice of Propriety Point" /></label>
				<form action="question/discusshdss" method="post" >
					<div >
					</div>
					<div>
						<table class="strippedTable" border="1">
							<thead>
								<tr>
									<th style="width:10%;"><spring:message code="general.srnumber" text="Serial Number"/></th>
									<th><spring:message code="member.name" text="Member Name"/></th>
									<th><spring:message code="proprietypoint.number" text="Notice Number"/></th>
									<th><spring:message code="question.subject" text="Subject"/></th>
									<%-- <th><spring:message code="question.selectfordiscussion" text="To Be Discussed"/></th> --%>
								</tr>
							</thead>
							<tbody>
					
								<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="counter">
									<tr>
										<td style="width: 10%;">
											${serialnumber[counter.count - 1].name}
										</td>
										<%-- <td>${ballotVO.memberName}</td> --%>
										<td>${ballotVO[1]}</td>
										<td>${ballotVO[3]}</td>
										<td>${ballotVO[4]}</td>
										<%-- <td align="center">
											<c:choose>
												<c:when test="${ballotVO.selected=='checked'}">
													<input type="checkbox" name="tobeDiscussed" value="${ballotVO.id}" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="tobeDiscussed" value="${ballotVO.id}" />
												</c:otherwise>
											</c:choose>
										</td> --%>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<br />
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
						<%-- <div style="float: right;">
							<input type="button" id="discuss" value='<spring:message code="question.discuss" text="Submit to Discuss"></spring:message>' class="butDef" />
						</div>  --%>
					</div> 
				</form>
				<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
			</c:otherwise>
		</c:choose>
	</div>
	<input id="sessionId" name="sessionId" value="${sessionId}" type="hidden">
	<input id="answeringDate" name="answeringDate" value="${answeringDate}" type="hidden"/>
	<input id="deviceTypeId" name="deviceTypeId" value="${deviceTypeId}" type="hidden"/>
	
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>