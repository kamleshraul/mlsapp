<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		
		$(document).ready(function(){
			var outputFormat="";
			
				$('#viewReport').click(function(){
					var params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&day=' +$('#selectedDay').val()
					+ '&member='+$('#member').val()
					+ '&outputFormat=' +outputFormat;
					$(this).attr('href','proceeding/memberwiseproceeding?'+params);
					
				});
				
				$('#outputFormat').change(function(){
					outputFormat=$('#outputFormat').val();
				});
		});		
		
	</script>
	
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=4" />
	
	<style type="text/css" media="print">
		.doBreak{
			page-break-after: always !important;
		}
	</style>
	<style type="text/css" media="all">
		table{
		width:800px;
		font-size: 14px;
		}
		.content{
			text-align:justify;
		}
		.right{
			text-align: right;			
		}
		.left{
			text-left:left;
		}
		.center{
			text-align: center;
		}
		.reportLink{
			cursor: pointer;
			
		}
		.reportLink:hover{
			text-shadow: 1px 1px gray;
		}	
		
		#reportDiv table ul{
			background: none !important;
			background: white !important;
		}
	</style>
</head>

<body>
<a id="viewReport" target="_blank" class="reportLink">report</a>
<c:if test="${not empty outputFormats}">				
				<select id="outputFormat" name="outputFormat">
					<option value="" selected="selected">Please Select Output Format</option>
					<c:forEach items="${outputFormats}" var="i">
						<option value="${i.value}">${i.name}</option>
					</c:forEach>
				</select>				
</c:if>
<div class="fields clearfix watermark" style="margin-top: 30px">
	<%@ include file="/common/info.jsp" %>
	
	<div id="reportDiv" align="center">
		<c:set var="mheading" value="" />
		<c:set var="pheading" value="" />
		<c:set var="member1" value=""/>
		<c:set var="count" value="1" />
		<c:set var="chairPerson" value=""/>
		<%--${report[0][3]} --%>		
		<c:forEach items="${report}" var="r" varStatus="i">
					<c:choose>
						<c:when test="${(mheading!=r[2] and pheading!=r[1]) or count==1 }">
						</table>
						<table class="doBreak">
							<thead>
								<tr>
									<th class="left" width="200px">${r[14]}</th>
									<th class="center" width="400px">${generalNotice}</th>
									<th class="right" width="200px">${r[13]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r[16]}</th>
									<th class="center">${r[17]}</th>
									<th class="right">${r[15]}</th>
								</tr>
							</thead>
							<tr><td colspan="3" height="30px"></td></tr>
							<c:if test="${r[18]!=null and r[18]!= chairPerson}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[18]}  ${r[19]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
							</c:if>
							<c:choose>
								<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
									<tr>
										<td colspan="3" class="content" style="text-align: center;">
											<b>${mainHeading } :</b> ${r[2]}
											<br>
											<b>${pageHeading}: </b>${r[1]}
										</td>
									</tr>
									<tr><td colspan="3" height="30px"></td></tr>
								</c:when>
							</c:choose>
							<tr>
								<td colspan="3" style="text-align: justify;" >
								<c:choose>
									<c:when test="${r[9]!=null}">
										<c:choose>
											<c:when test="${r[6]!=null and r[6]!=''}">
												<b>${r[10]}</b>	
											</c:when>
											<c:otherwise>
												${r[10]}
											</c:otherwise>
										</c:choose>
										<c:if test="${r[20]!=null and r[20]!=''}">
											(${r[20]})
										</c:if>
										<c:choose>
											<c:when test="${r[21]!=null and r[21]!=''}">
												(<b>${r[21]}</b>
												<c:if test="${r[6]!=null and r[6]!='' }">
													<b>${r[6]}</b>)
												</c:if>
											</c:when>
											<c:otherwise>
												<c:if test="${r[6]!=null and r[6]!=''}">
													<b>${r[6]}</b>)
												</c:if>
											</c:otherwise>
										</c:choose>
										<c:if test="${r[11]!=null }">
											<c:choose>
												<c:when test="${r[7]!=null and r[7]!=''}">
													,<b>${r[12]} (${r[7]})</b>
												</c:when>
												<c:otherwise>
													,${r[12]}
												</c:otherwise>
											</c:choose>
										</c:if>
										:
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r[3]!=null or r[3]!=''}">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[3]}</b>(${r[4]}):
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
								${r[0]}
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:when>
					<c:otherwise>
						<c:if test="${r[18]!=null and r[18]!= chairPerson}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[18]}  ${r[19]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>
						<tr>
							<td colspan="3" class="content" >
								<c:choose>
									<c:when test="${r[9]!=null and r[9]!=member1}">
										<c:choose>
											<c:when test="${r[6]!=null and r[6]!=''}">
												<b>${r[10]}</b>	
											</c:when>
											<c:otherwise>
												${r[10]}
											</c:otherwise>
										</c:choose>
										<c:if test="${r[20]!=null and r[20]!=''}">
											(${r[20]})
										</c:if>
										<c:choose>
											<c:when test="${r[21]!=null and r[21]!=''}">
												(<b>${r[21]}</b>
												<c:if test="${r[6]!=null and r[6]!='' }">
													<b>${r[6]}</b>)
												</c:if>
											</c:when>
											<c:otherwise>
												<c:if test="${r[6]!=null and r[6]!=''}">
													<b>${r[6]}</b>)
												</c:if>
											</c:otherwise>
										</c:choose>
										<c:if test="${r[11]!=null }">
											<c:choose>
												<c:when test="${r[7]!=null and r[7]!=''}">
													,<b>${r[12]} (${r[7]})</b>
												</c:when>
												<c:otherwise>
													,${r[12]}
												</c:otherwise>
											</c:choose>
										</c:if>
										:
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r[3]!=null or r[3]!=''}">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[3]}</b>(${r[4]}):
											</c:when>
										</c:choose>
									</c:otherwise>
								</c:choose>
								${r[0]}
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:otherwise>
				</c:choose>
			
			<p id="page-number"></p>
			<c:set var="count" value="${count + 1}" />
			<c:set var="mheading" value="${r[2]}" />
			<c:set var="pheading" value="${r[1]}" />
			<c:set var="member1" value="${r[9]}"/>
			<c:set var="chairPerson" value="${r[18]}"/>
		</c:forEach>
	</div>	
</div>
<input type="hidden" name="member" id="member" value="${member}">
</body>
</html>