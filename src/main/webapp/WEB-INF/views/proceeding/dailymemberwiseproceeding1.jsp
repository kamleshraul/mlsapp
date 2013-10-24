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
					$(this).attr('href','proceeding/memberwiseproceeding1?'+params);
					
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
		<c:set var="count" value="1"/>
		<c:set var="mHeading" value=""/>
		<c:set var="pHeading" value=""/>
		<c:set var="slot" value=""/>
		<c:set var="cnt" value="0"/>
		<c:forEach items="${report}" var="r" step="${count}">
			<c:if test="${r[9]==member}">
				<c:forEach items="${report}" var ="r1" begin="${cnt}">
						<c:choose>
						<c:when test="${(mHeading==r1[2] && pHeading==r1[1] && slot==r1[13])}">
							<tr>
								<td colspan="3" style="text-align: justify;" >
								<c:choose>
									<c:when test="${r1[9]!=null}">
										<c:choose>
											<c:when test="${r1[5]!=null}">
												<c:choose>
													<c:when test="${r1[6]!=null}">
														<c:choose>
															<c:when test="${r1[11]!=null }">
																<c:choose>
																	<c:when test="${r1[7] !=null}">
																		<c:choose>
																			<c:when test="${r1[8]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}):</b> ${r1[0]}	
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r1[11]!=null }">
																<c:choose>
																	<c:when test="${r1[7] !=null}">
																		<c:choose>
																			<c:when test="${r1[8]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}), ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}) , ${r1[12]} (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}), ${r1[12]}  ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[10]} (${r[5]}) :</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r1[11]!=null }">
														<c:choose>
															<c:when test="${r1[7] !=null}">
																<c:choose>
																	<c:when test="${r1[8]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} , ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} , ${r1[12]}  (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[10]} , ${r1[12]}  ${inplaceOf}: ${r1[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[10]} : ${r1[0]}	
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r1[4]!=null}">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[3]} (${r1[4]}): ${r1[0]}
											</c:when>
											<c:otherwise>
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[0]}	
											</c:otherwise>
										</c:choose>
									</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					<c:set var="mHeading" value="${r1[2]}"/>
					<c:set var="pHeading" value="${r1[1] }"/>
					<c:set var="slot" value="${r1[13]}"/>
					<c:set var="count" value="${count+1}"/>
				</c:when>
				<c:otherwise>
					<c:if test="${r1[9]==member}">
						</table>
						<table class="doBreak">
							<thead>
								<tr>
									<th class="left" width="200px">${r1[14]}</th>
									<th class="center" width="400px">${generalNotice}</th>
									<th class="right" width="200px">${r1[13]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r1[16]}</th>
									<th class="center">${r1[17]}</th>
									<th class="right">${r1[15]}</th>
								</tr>
							</thead>
						<tr><td colspan="3" height="30px"></td></tr>
						<c:choose>
							<c:when test="${r1[1]!=null and r1[2]!=null and r1[1]!='' and r1[2]!=''}">
								<tr>
									<td colspan="3" class="content" style="text-align: center;">
										<b>${mainHeading} :</b> ${r1[2]}
										<br>
										<b>${pageHeading}: </b>${r1[1]}
									</td>
								</tr>
							<tr><td colspan="3" height="30px"></td></tr>
						</c:when>
						</c:choose>
							<tr>
								<td colspan="3" style="text-align: justify;" >
								<c:choose>
									<c:when test="${r1[9]!=null}">
										<c:choose>
											<c:when test="${r1[5]!=null}">
												<c:choose>
													<c:when test="${r1[6]!=null}">
														<c:choose>
															<c:when test="${r1[11]!=null }">
																<c:choose>
																	<c:when test="${r1[7] !=null}">
																		<c:choose>
																			<c:when test="${r1[8]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}), ${r1[12]} ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[6]}) (${r1[5]}):</b> ${r1[0]}	
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r1[11]!=null }">
																<c:choose>
																	<c:when test="${r1[7] !=null}">
																		<c:choose>
																			<c:when test="${r1[8]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}), ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}) , ${r1[12]} (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} (${r1[5]}), ${r1[12]}  ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[10]} (${r[5]}) :</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r1[11]!=null }">
														<c:choose>
															<c:when test="${r1[7] !=null}">
																<c:choose>
																	<c:when test="${r1[8]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} , ${r1[12]} (${r1[8]}) (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r1[10]} , ${r1[12]}  (${r1[7]}) ${inplaceOf}:</b> ${r1[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[10]} , ${r1[12]}  ${inplaceOf}: ${r1[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[10]} : ${r1[0]}	
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${r1[4]!=null}">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[3]} (${r1[4]}): ${r1[0]}
											</c:when>
											<c:otherwise>
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r1[0]}	
											</c:otherwise>
										</c:choose>
									</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					<c:set var="mHeading" value="${r1[2]}"/>
					<c:set var="pHeading" value="${r1[1] }"/>
					<c:set var="slot" value="${r1[13]}"/>
					<c:set var="count" value="${count+1}"/>
				</c:if>
				
				</c:otherwise>
				</c:choose>		
				<c:set var="cnt" value="${cnt+1}"/>		
			</c:forEach>
		</c:if>
	  </c:forEach>
	</div>	
</div>
<input type="hidden" name="member" id="member" value="${member}">
</body>
</html>