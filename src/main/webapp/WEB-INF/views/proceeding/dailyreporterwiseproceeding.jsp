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
					+ '&outputFormat=' +outputFormat;
					$(this).attr('href','proceeding/reporterwiseproceeding?'+params);
					
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
		<c:set var="slot" value="" />	
		<c:set var="mheading" value="" />
		<c:set var="pheading" value="" />
		<c:set var="member" value=""/>
		<c:set var="count" value="1" />
		<c:set var="chairPerson" value=""/>
		<%--${report[0][3]} --%>		
		<c:forEach items="${report}" var="r" varStatus="i">
			<c:choose>
				<c:when test="${slot==r[6]}">	
					<c:choose>
						<c:when test="${mheading!=r[2] and pheading!=r[1] }">
						</table>
						<table class="doBreak">
							<thead>
								<tr>
									<th class="left" width="200px">${r[7]}</th>
									<th class="center" width="400px">${generalNotice}</th>
									<th class="right" width="200px">${r[6]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r[19]}</th>
									<th class="center"></th>
									<th class="right">${r[8]}</th>
								</tr>
							</thead>
							<tr><td colspan="3" height="30px"></td></tr>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
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
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[11]!=null and r[11]!='' }">
										<b>${r[15]}</b>	
									</c:when>
									<c:otherwise>
										${r[15]}
									</c:otherwise>
								</c:choose>
								<c:if  test="${r[20]!=null and r[20]!=''}">
									(${r[20]})
								</c:if>
								<c:choose>
									<c:when test="${r[21]!=null and r[21]!=''}">
										(<b>${r[21]}</b>
										<c:if test="${r[11]!=null and r[11]!='' }">
											<b>${r[11]}</b>)
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${r[11]!=null and r[11]!='' }">
											(<b>${r[11]}</b>)
										</c:if>
									</c:otherwise>
								</c:choose>
								<c:if test="${r[16]!=null }">
									<c:choose>
										<c:when test="${r[12]!=null and r[12]!='' }">
											,<b>${r[17]} (${r[12]})</b>
										</c:when>
										<c:otherwise>
											,${r[17]}
										</c:otherwise>
									</c:choose>
							</c:if>
								:
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null or r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>(${r[5]}):
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
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>
						<tr>
						<td colspan="3" class="content" >
						<c:choose>
								<c:when test="${r[15]!=null and r[14]!= member}">
									<c:choose>
										<c:when test="${r[11]!=null and r[11]!='' }">
											<b>${r[15]}</b>	
										</c:when>
										<c:otherwise>
											${r[15]}
										</c:otherwise>
									</c:choose>
									<c:if  test="${r[20]!=null and r[20]!=''}">
										(${r[20]})
									</c:if>
									<c:choose>
										<c:when test="${r[21]!=null and r[21]!=''}">
											(<b>${r[21]}</b>
											<c:if test="${r[11]!=null and r[11]!='' }">
												<b>${r[11]}</b>)
											</c:if>
										</c:when>
										<c:otherwise>
											<c:if test="${r[11]!=null and r[11]!='' }">
												(<b>${r[11]}</b>)
											</c:if>
										</c:otherwise>
									</c:choose>
									<c:if test="${r[16]!=null }">
											<c:choose>
												<c:when test="${r[12]!=null and r[12]!='' }">
													,<b>${r[17]} (${r[12]})</b>
												</c:when>
												<c:otherwise>
													,${r[17]}
												</c:otherwise>
											</c:choose>
										</c:if>
									:
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${r[4]!=null or r[4]!=''}">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>(${r[5]}):
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
				</c:when>
				<c:otherwise>
					</table>
					
					<table class="doBreak">
						<thead>
							<tr>
								<th class="left" width="200px">${r[7]}</th>
								<th class="center" width="400px">${generalNotice}</th>
								<th class="right" width="200px">${r[6]} - ${count}</th>
							</tr>
							<tr>
								<th class="left">${r[19]}</th>
								<th class="center"></th>
								<th class="right">${r[8]}</th>
							</tr>
						</thead>
						<tr><td colspan="3" height="20px"></td></tr>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										${mainHeading } : ${r[2]}
										<br>
										${pageHeading}: ${r[1]}
									</td>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[1]!='' and r[2]==null and r[2]==''}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										${mainHeading}/${pageHeading} : ${r[1]}
									</td>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[2]!=null and r[2]!='' and r[1]==null and r[1]==''}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										${mainHeading}/${pageHeading} : ${r[2]}
									</td>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[23]!=null and r[23]!=''}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										${r[23]}
									</td>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
						</c:choose>
						
					<tr>
					<td colspan="3" style="text-align: justify;" >
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[11]!=null and r[11]!=''}">
										<b>${r[15]}</b>	
									</c:when>
									<c:otherwise>
										${r[15]}
									</c:otherwise>
								</c:choose>
								<c:if test="${r[20]!=null and r[20]!=''}">
									(${r[20]})
								</c:if>
								<c:choose>
									<c:when test="${r[21]!=null and r[21]!=''}">
										(<b>${r[21]}</b>
										<c:if test="${r[11]!=null and r[11]!='' }">
											<b>${r[11]}</b>)
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${r[11]!=null and r[11]!=''}">
											(<b>${r[11]}</b>)
										</c:if>
									</c:otherwise>
								</c:choose>
								<c:if test="${r[16]!=null }">
									<c:choose>
										<c:when test="${r[12]!=null and r[12]!=''}">
											,<b>${r[17]} (${r[12]})</b>
										</c:when>
										<c:otherwise>
											,${r[17]}
										</c:otherwise>
									</c:choose>
								</c:if>
								
								:
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null or r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>(${r[5]}):
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
			<c:set var="slot" value="${r[6]}" />
			<c:set var="mheading" value="${r[2]}" />
			<c:set var="pheading" value="${r[1]}" />
			<c:set var="member" value="${r[14]}"/>
			<c:set var="chairPerson" value="${r[9]}"/>
		</c:forEach>
	</div>	
</div>
</body>
</html>