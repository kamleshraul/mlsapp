<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.part.proceedingwisereport" text="Proceeding Wise Report"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	 $(document).ready(function(){
		var outputFormat="";
		$('#viewReport').click(function(){
			var params="proceeding="+$('#proceedingId').val()+
			"&language="+$('#languageId').val()+
			"&outputFormat="+outputFormat;
			$(this).attr('href','proceeding/proceedingwisereport?'+params);
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
		#reportDiv{
			width: 800px;
			margin: 0px 0px 0px 75px;
			
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
			width: 100px;
			max-width: 100px;		
		}
		.left{
			text-left:left;
			width: 100px;
			max-width: 100px;
		}
		
		.center{
			text-align: center;
			width: 600px;
			max-width: 600px;
		}
		
		.reportLink{
			cursor: pointer;
			
		}
		.reportLink:hover{
			text-shadow: 1px 1px gray;
		}
		
		#reportDiv table ul{
			background: white !important;
		}
				
		#reportIconsDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin-left: 50px; 
			border: 1px solid black; 
			/*background: #0E4269;*/
			
			/*background: -prefix-linear-gradient(top, #0E4269, #59A8E3);*/
			/* The new syntax needed by standard-compliant browsers (Opera 12.1, IE 10, Fx 16 onwards), without prefix */
			background: #feffff; /* Old browsers */
			background: -moz-linear-gradient(top,  #feffff 0%, #d2ebf9 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#feffff), color-stop(100%,#d2ebf9)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #feffff 0%,#d2ebf9 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#feffff', endColorstr='#d2ebf9',GradientType=0 ); /* IE6-9 */
		}
		
		@media screen{
			#reportDiv{
				box-shadow: 2px 2px 2px 2px #000000; 
				border: 1px solid black;
				background: #F2F0F2; 
			}
		}
		
		@media print{
			#reportDiv{
				/*box-shadow: 2px 2px 2px 2px #000000; */
				/*border: 1px solid black;*/
				background: #F2F0F2; 
			}
		}	
				
	</style>
</head>

<body>
<a id="viewReport" target="_blank" class="reportLinkl">report</a>
<c:if test="${not empty outputFormats}">				
	<select id="outputFormat" name="outputFormat">
		<option value="" selected="selected">Please Select Output Format</option>
		<c:forEach items="${outputFormats}" var="i">
			<option value="${i.value}">${i.name}</option>
		</c:forEach>
	</select>				
</c:if>

<div class="fields clearfix watermark" style="margin-top: 30px">
		
<form action="proceeding/part/updatePart" method="post">
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
									<th class="left">${r[7]}</th>
									<th class="center">${generalNotice}</th>
									<th class="right">${r[6]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r[19]}</th>
									<th class="center">${r[18]}</th>
									<th class="right">${r[8]}</th>
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</thead>
							<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
							</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings-${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading-${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading-${r[20]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading${r[20]}" style="display: inline;">${r[2]}</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
						
					<tr>
					<td id="pContent${r[20]}" colspan="3" style="text-align: justify;"  >
						<div id="procMember-${r[20]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
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
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
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
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
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
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
					</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:when>
					<c:otherwise>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<tr>
						<td id="pContent${r[20]}" colspan="3" class="content"  >
						<div id="procMember-${r[20]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null and r[14]!=member}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
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
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
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
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
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
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
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
								<th class="left">${r[7]}</th>
								<th class="center">${generalNotice}</th>
								<th class="right">${r[6]} - ${count}</th>
							</tr>
							<tr>
								<th class="left">${r[19]}</th>
								<th class="center">${r[18]}</th>
								<th class="right">${r[8]}</th>
							</tr>
							<tr><td colspan="3" height="30px"></td></tr>
						</thead>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chaiPerson-${r[20]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings${r[20]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading${r[20]}" colspan="3" style="text-align: center;"  >
										<div id="pTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading-${r[20]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading${r[20]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[20]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading-${r[20]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
					<tr>
					<td id="pContent${r[20]}" colspan="3" style="text-align: justify;">
						<div id="procMember-${r[20]}" style="display: inline-block;">
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[22]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) (${r[22]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>,: <%-- ${r[0]} --%>
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
																				<c:choose>
																					<c:when test="${r[23]!=null}">
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[21]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]})</b>,: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
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
																		<c:choose>
																			<c:when test="${r[23]!=null}">
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[21]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>	
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[22]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[21]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]})</b>,: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
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
																<c:choose>
																	<c:when test="${r[23]!=null}">
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[23]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[21]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>	
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[21]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]} ,<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[21]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:when>
															<c:otherwise>
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
															</c:otherwise>
														</c:choose>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${r[21]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[21]}) : <%-- ${r[0]} --%>
													</c:when>
													<c:otherwise>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}: <%-- ${r[0]} --%>
													</c:otherwise>
												</c:choose>
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${r[4]!=null && r[4]!=''}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[20]}" style="display: inline;" class="editableContent" >
							${r[0]}
						</div>
						<div id="ppsp${r[20]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[20]}" class="pprp" style="display: none;">classes</div>
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
	
	<input type="hidden" id="proceedingId" name="proceedingId" value="${proceeding}">
	<input type="hidden" id="languageId" name="languageId" value="${language}">
	<input type="hidden" id="editingUser" name="editingUser" value="${userName}">
	<input id="prevcontent" type="hidden" name ="prevContent" value="" />
</div>
</form>
</body>
</html>