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
<div class="fields clearfix watermark" style="margin-top: 30px;">
	
	<%@ include file="/common/info.jsp" %>
	
	<div id="reportDiv" align="center">
		<table id="doBreak">
			<tbody>
				<c:forEach items="${report}" var="r">
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
					<td>
					<c:choose>
						<c:when test="${r[9]!=null}">
							<c:choose>
								<c:when test="${r[5]!=null}">
									<c:choose>
										<c:when test="${r[6]!=null }">
											<c:choose>
												<c:when test="${r[11]!=null}">
													<c:choose>
														<c:when test="${r[7]!=null }">
															<c:choose>
																<c:when test="${r[8]!=null }">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]} (${r[6]} ${r[5]}) , ${r[12]} (${r[8]} ${r[7]}) ${inplaceOf} : ${r[0]}
																</c:when>
																<c:otherwise>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]} (${r[6]} ${r[5]}) , ${r[12]} (${r[7]}) ${inplaceOf} : ${r[0]}
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]} (${r[6]} ${r[5]}) , ${r[12]} ${inplaceOf} : ${r[0]}
														</c:otherwise>
													</c:choose>	
												</c:when>
												<c:otherwise>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]} (${r[6]} ${r[5]}) : ${r[0]}
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]} (${r[6]}) : ${r[0]}
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[10]}: ${r[0]}
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${r[11]!=null}">
									<c:choose>
										<c:when test="${r[7]!=null }">
											<c:choose>
												<c:when test="${r[8]!=null }">
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[12]} (${r[8]} ${r[7]} ) : ${r[0]}
												</c:when>
												<c:otherwise>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[12]} (${r[7]}) : ${r[0]}
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${r[12]}  : ${r[0]}
										</c:otherwise>
									</c:choose>
								</c:when>
							</c:choose>
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
				<tr><td colspan="3" height="30px"></td></tr>
				</c:forEach>
			</tbody>
		</table>
	</div>	
</div>
<input type="hidden" name="member" id="member" value="${member}">
</body>
</html>