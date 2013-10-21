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
									<th class="center">${r[20]}</th>
									<th class="right">${r[8]}</th>
								</tr>
							</thead>
							<tr><td colspan="3" height="30px"></td></tr>
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
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																<c:when test="${r[13]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}):</b> ${r[0]}	
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																<c:when test="${r[13]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) :</b> ${r[0]}
													</c:otherwise>
												</c:choose>
												
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
										<c:when test="${r[16]!=null }">
													<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																<c:when test="${r[13]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  ${inplaceOf}:</b> ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} ${inplaceOf}:</b> ${r[0]}
																</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[17]} , ${r[15]}  ${inplaceOf}: ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} : ${r[0]}	
													</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): ${r[0]}
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[0]}	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:when>
					<c:otherwise>
						<tr>
						<td colspan="3" class="content" >
						<c:choose>
							<c:when test="${r[15]!=null and r[14]!=member}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																<c:when test="${r[13]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}):</b> ${r[0]}	
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																<c:when test="${r[13]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) :</b> ${r[0]}
													</c:otherwise>
												</c:choose>
												
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
									<c:choose>
										<c:when test="${r[16]!=null }">
													<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  ${inplaceOf}:</b> ${r[0]}
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} ${inplaceOf}:</b> ${r[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[17]} , ${r[15]}  ${inplaceOf}: ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} : ${r[0]}	
													</c:otherwise>
											</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): ${r[0]}
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[0]}	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
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
								<th class="center">${r[20]}</th>
								<th class="right">${r[8]}</th>
							</tr>
						</thead>
						<tr><td colspan="3" height="20px"></td></tr>
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
						</c:choose>
						
					<tr>
					<td colspan="3" style="text-align: justify;" >
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[11]}) (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}):</b> ${r[0]}	
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[16]!=null }">
														<c:choose>
															<c:when test="${r[12] !=null}">
																<c:choose>
																	<c:when test="${r[13]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} , ${r[15]} (${r[10]}) ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) :</b> ${r[0]}
													</c:otherwise>
												</c:choose>
												
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<c:choose>
										<c:when test="${r[16]!=null}">
												<c:choose>
													<c:when test="${r[12]!=null}">
														<c:choose>
															<c:when test="${r[13]!=null}">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}) (${r[13]}), ${r[15]}  ${inplaceOf}:</b> ${r[0]}
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[17]} (${r[12]}), ${r[15]} ${inplaceOf}:</b> ${r[0]}
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[17]} , ${r[15]}  ${inplaceOf}: ${r[0]}
													</c:otherwise>
												</c:choose>
										</c:when>
										<c:otherwise>
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} : ${r[0]}	
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]} (${r[5]}):</b> ${r[0]}
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[0]}	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
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
		</c:forEach>
	</div>	
</div>
</body>
</html>