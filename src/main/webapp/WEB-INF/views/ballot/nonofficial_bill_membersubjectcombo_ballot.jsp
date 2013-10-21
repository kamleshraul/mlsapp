<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			//====================================================
			//for bill balloting and approval workflow
			$("#discuss").click(function(){
				var postURL = "bill/discussbills";
				/* $("input[type=checkbox]:checked").each(function(){
					alert($(this).val());
				}); */
				$(this).attr('disabled','disabled');
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post(postURL,
					$('form').serialize(), function(data){
					$.unblockUI();
				});
				//viewBallot();
			});
		});
	</script>
</head>

<body>
<c:choose>
	<c:when test="${ballotVOs == null}">
		<spring:message code="bill.ballot.notCreated" text="Ballot is not Created"/>
	</c:when>

	<c:when test="${empty ballotVOs}">
		<spring:message code="bill.ballot.noEntries" text="There are no entries in the Ballot"/>
	</c:when>

	<c:otherwise>
		<label class="small"><spring:message code="bill.ballot.answeringDate" text="Discussion Date"/>: ${answeringDate}</label>
		<form action="bill/discussbills" method="post" >
			<div>
				<table class="strippedTable" border="1">
					<tr>
						<th><spring:message code="general.srnumber" text="Serial Number"/></th>
						<th><spring:message code="member.name" text="Member Name"/></th>
						<th><spring:message code="bill.number" text="Bill Number"/></th>
						<th><spring:message code="bill.title" text="Title"/></th>
						<th><spring:message code="bill.selectfordiscussion" text="To Be Discussed"/></th>
					</tr>
			
					<c:forEach items="${ballotVOs}" var="ballotVO" varStatus="counter">
						<tr>
							<td>${counter.count}</td>
							<td>${ballotVO.memberName}</td>
							<td>${ballotVO.billNumber}</td>
							<td>${ballotVO.billTitle}</td>
							<td align="center">
								<c:choose>
									<c:when test="${ballotVO.checked=='checked'}">
										<input type="checkbox" name="tobeDiscussed" value="${ballotVO.id}" checked="checked"/>
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="tobeDiscussed" value="${ballotVO.id}" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<br />
			<hr />
			<div id="submitDiv" style="width: 800px; display: none;">
				<%-- <c:if test="${workflowstatus!='COMPLETED' }">	
					<p>
						<label class="small"><spring:message code="bill.putupfor" text="Put up for"/></label>
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
						<label class="small"><spring:message code="bill.nextactor" text="Next Users"/></label>
						<select id="actor" name="actor" class="sSelect">	
						<c:forEach items="${actors}" var="i">
						<option value="${i.id}">${i.name}</option>
						</c:forEach>
						</select>
					</p>		
				</c:if> --%>
				<input type="hidden" name="allids" value="${ids}" />
				<div style="float: right;">
					<input type="button" id="discuss" value='<spring:message code="bill.discuss" text="Submit to Discuss"></spring:message>' class="butDef" />
				</div> 
			</div> 
		</form>
		<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</c:otherwise>
</c:choose>
</body>
</html>