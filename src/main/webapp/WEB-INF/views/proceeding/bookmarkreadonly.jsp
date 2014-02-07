<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="part.citation"	text=" Proceeding Citations" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
	#reportDiv{
			width: 750px;
			border: solid 2px black;
			 margin: 20px 2px 2px 2px; 
			
		}
</style>
<script type="text/javascript">
$(document).ready(function(){	
});
</script>
</head>
<body>
<div class="fields clearfix watermark">	
	
	<p>
	<label class="small"><spring:message code="bookmark.bookmarkkey" text="Bookmark key"/></label>
	<input type="text" id="bookmarkkey" name="bookmarkkey" value="${bookmarkKey}"/>
	
	<label class="small"><spring:message code="bookmark.reporterName" text="Reporter"/>*</label>
	<input type="text" id="reporterName" name="reporterName" value="${reporter}"/>
	
	</p>
	
	<%-- <p>
	<label class="small"><spring:message code="bookmark.memberName" text="Member Name"/>*</label>
	<input type="text" id="memberName" name="memberName" value="${memberName}"/>
	</p>
	<p>
	<label class="wysiwyglabel"><spring:message code="bookmark.previousContent" text="Previous Content"/>*</label>
	<textarea id="previousContent" name="previousContent" class="wysiwyg" >${previousText}</textarea>
	</p> --%>
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
							<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[18]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
							</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings-${r[18]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[18]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[18]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[18]}" style="display: inline;" class="editableContent">${r[2]}</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading-${r[18]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading${r[18]}" style="display: inline;" class="editableContent">${r[1]}</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading-${r[18]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading${r[18]}" style="display: inline;">${r[2]}</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
						
					<tr>
					<td id="pContent${r[18]}" colspan="3" style="text-align: justify;"  >
						<div id="procMember-${r[18]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[20]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>,: <%-- ${r[0]} --%>
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
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																			<c:when test="${r[21]!=null}">
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																	<c:when test="${r[21]!=null}">
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
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
													<c:when test="${r[19]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}) : <%-- ${r[0]} --%>
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
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[18]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:when>
					<c:otherwise>
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chairPerson-${r[18]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<tr>
						<td id="pContent${r[18]}" colspan="3" class="content"  >
						<div id="procMember-${r[18]}" style="display: inline-block;" >
						<c:choose>
							<c:when test="${r[15]!=null and r[14]!=member}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[20]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>,: <%-- ${r[0]} --%>
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
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																			<c:when test="${r[21]!=null}">
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																	<c:when test="${r[21]!=null}">
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
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
													<c:when test="${r[19]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}) : <%-- ${r[0]} --%>
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
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[18]}" style="display: inline;" class="editableContent">
							${r[0]}
						</div>
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
					</c:otherwise>
					</c:choose>
					
				</c:when>
				<c:otherwise>
					</table>
					
					<table class="doBreak">
						<c:if test="${r[9]!=null and r[9]!= chairPerson}">
								<tr>
									<td id="chaiPerson-${r[18]}" colspan="3" class="content editableContent" style="text-align: center;">
										<b>(<spring:message code="part.chairPersonMessage"/>  ${r[9]}  ${r[3]}) </b>
									</td>
								</tr>
								<tr><td colspan="3" height="30px"> </td></tr>
						</c:if>	
						<c:choose>
							<c:when test="${r[1]!=null and r[2]!=null and r[1]!='' and r[2]!=''}">
								<tr>
									<td id="Headings${r[18]}" colspan="3" style="text-align: center;">
										<div id="pTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}</b></div>: <div id="pageHeading-${r[18]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="mTitle${r[18]}" style="display: inline-block;"><b>${mainHeading }</b></div> :<div id="mainHeading-${r[18]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[18]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[18]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
								<tr>
									
								</tr>
								<tr><td colspan="3" height="30px"></td></tr>
							</c:when>
							<c:when test="${r[1]!=null and r[2]==null or r[1]!='' and r[2]=='' }">
								<tr>
									<td id="pageHeading${r[18]}" colspan="3" style="text-align: center;"  >
										<div id="pTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="pageHeading-${r[18]}" style="display: inline;" class="editableContent">${r[1]}</div>
										<div id="ppsp${r[18]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[18]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
							<c:when test="${r[2]!=null and r[1]==null or r[2]!='' and r[1]=='' }">
								<tr>
									<td id="mainHeading${r[18]}" colspan="3" style="text-align: center;">
										<div id="mTitle${r[18]}" style="display: inline-block;"><b>${pageHeading}/${mainHeading }</b></div>: <div id="mainHeading-${r[18]}" style="display: inline;" class="editableContent">${r[2]}</div>
										<div id="ppsp${r[18]}" class="ppsp" style="display: none;">classes</div>
										<div id="pprp${r[18]}" class="pprp" style="display: none;">classes</div>
									</td>
								</tr>
							</c:when>
						</c:choose>
					<tr>
					<td id="pContent${r[18]}" colspan="3" style="text-align: justify;">
						<div id="procMember-${r[18]}" style="display: inline-block;">
						<c:choose>
							<c:when test="${r[15]!=null}">
								<c:choose>
									<c:when test="${r[10]!=null}">
										<c:choose>
											<c:when test="${r[11]!=null}">
												<c:choose>
													<c:when test="${r[20]!=null}">
														<c:choose>
															<c:when test="${r[16]!=null }">
																<c:choose>
																	<c:when test="${r[12] !=null}">
																		<c:choose>
																			<c:when test="${r[13]!=null }">
																				<c:choose>
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>	
																					</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) (${r[20]}),</b> : <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>,: <%-- ${r[0]} --%>
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
																					<c:when test="${r[21]!=null}">
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:when>
																							<c:otherwise>
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																							</c:otherwise>
																						</c:choose>																				
																					</c:when>
																					<c:otherwise>
																						<c:choose>
																							<c:when test="${r[19]!=null }">
																								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[11]}) (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]})(${r[11]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																			<c:when test="${r[21]!=null}">
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:when>
																					<c:otherwise>
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}), ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																					</c:otherwise>
																				</c:choose>																				
																			</c:when>
																			<c:otherwise>
																				<c:choose>
																					<c:when test="${r[19]!=null }">
																						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]}), ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:otherwise>
																<c:choose>
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}),</b> ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:when>
																	<c:otherwise>
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]} (${r[10]}) (${r[20]})</b>, ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
																	</c:otherwise>
																</c:choose>
															</c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[15]}(${r[19]}) (${r[10]}) ,</b> : <%-- ${r[0]} --%>
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
																	<c:when test="${r[21]!=null}">
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:when>
																			<c:otherwise>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]},<b> ${r[17]} (${r[12]}) (${r[13]}) (${r[21]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
																			</c:otherwise>
																		</c:choose>																				
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${r[19]!=null }">
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), <b>${r[17]} (${r[12]}) (${r[13]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
																	<c:when test="${r[19]!=null }">
																		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}),<b> ${r[17]} (${r[12]})</b>  ${inplaceOf}: <%-- ${r[0]} --%>
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
															<c:when test="${r[19]!=null }">
																&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}), ${r[17]}  ${inplaceOf}: <%-- ${r[0]} --%>
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
													<c:when test="${r[19]!=null }">
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[15]}(${r[19]}) : <%-- ${r[0]} --%>
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
									<c:when test="${r[4]!=null}">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[4]} (${r[5]}): <%-- ${r[0]} --%>
									</c:when>
									<c:otherwise>
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%-- ${r[0]} --%>	
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						</div>
						<div id="procContent-${r[18]}" style="display: inline;" class="editableContent" >
							${r[0]}
						</div>
						<div id="ppsp${r[18]}" class="ppsp" style="display: none;">classes</div>
						<div id="pprp${r[18]}" class="pprp" style="display: none;">classes</div>
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
	</div>
</html>