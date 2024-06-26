<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
		
		$(document).ready(function(){
			
			$("#reportDiv").find( "*" ).css( "line-height", "200%" );
			$("#reportDiv").find( "*" ).css( "font-size", "16px" );
			
			var outputFormat="";
				$('#viewReport').click(function(){
					if($('#selectedModule').val()=="COMMITTEE"){
						var params="committeeMeeting=" + $("#selectedCommitteeMeeting").val()
						+ '&language=' + $("#selectedLanguage").val()
						+ '&day=' +$('#selectedDay').val()
						+ '&outputFormat=' +outputFormat;
						if($('#outputFormat').val()!=""){
							$(this).attr('href','proceeding/viewreport?'+params);
						}else{
							alert("select the file format");
							return false;
						}
					}else{
						var params="houseType=" + $('#selectedHouseType').val()
						+ '&sessionYear=' + $("#selectedSessionYear").val()
						+ '&sessionType=' + $("#selectedSessionType").val()
						+ '&language=' + $("#selectedLanguage").val()
						+ '&day=' +$('#selectedDay').val()
						+ '&outputFormat=' +outputFormat;
						if($('#outputFormat').val()!=""){
							$(this).attr('href','proceeding/viewreport?'+params);
						}else{
							alert("select the file format");
							return false;
						}
					
					}
				});
				
				$('#sessionWiseReport').click(function(){
					var params="houseType=" + $('#selectedHouseType').val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&language=' + $("#selectedLanguage").val()
					+ '&outputFormat=' +outputFormat;
					if($('#outputFormat').val()!=""){
						$(this).attr('href','proceeding/sessionwisereport?'+params);
					}else{
						alert("select the file format");
						return false;
					}
					
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
			/* background: white !important; */
		}
		
		#reportDiv table td{
			background: none !important;
			/* background: white !important; */
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
	<c:if test="${not empty outputFormats}">				
		<select id="outputFormat" name="outputFormat">
			<option value="" selected="selected">Please Select Output Format</option>
				<c:forEach items="${outputFormats}" var="i">
					<option value="${i.value}">${i.name}</option>
				</c:forEach>
		</select>				
	</c:if>
	<a id="viewReport" target="_blank" class="reportLink">rosterwisereport</a>
	<c:if test="${committeeMeeting==null && committeeMeeting==''}">
		<a id="sessionWiseReport" target="_blank" class="reportLink">sessionwisereport</a>
	</c:if>
<div class="fields clearfix watermark" style="margin-top: 30px;">
	<%@ include file="/common/info.jsp" %>
	
	
	<div id="reportDiv" align="center" >
		<c:set var="slot" value="" />	
		<c:set var="mheading" value="" />
		<c:set var="pheading" value="" />
		<c:set var="member" value=""/>
		<c:set var="count" value="1" />
		<c:set var="nextReporter" value=""/>
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
									<th class="center" width="400px"><spring:message code="part.generalNotice" text="Un edited Copy"/></th>
									<th class="right" width="200px">${r[6]} - ${count}</th>
								</tr>
								<tr>
									<th class="left">${r[19]}</th>
									<c:choose>
										<c:when test="${r[26]!= null and r[26]!=''}">
											<th class="center"><spring:message code="part.previousReporterMessage" text="Previous Reporter"/> ${r[26]}</th>
										</c:when>
										<c:otherwise>
											<th></th>
										</c:otherwise>
									</c:choose>
									<th class="right">${r[8]}</th>
								</tr>
								<c:if test="${member==r[14]}">
								<tr>
									<th class="left">
										${r[14]}....
									</th>
								</tr>	
								</c:if>
							</thead>
							<tr><td colspan="3" height="30px"> </td></tr>
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
										<td colspan="3" class="content" style="text-align: center;" >
											<b><spring:message code="part.pageHeading" text="Page Heading"/>:</b> ${r[1]}<br>
											<b><spring:message code="part.mainHeading" text="Main Heading"/> :</b> ${r[2]}
										</td>
									</tr>
									<tr><td colspan="3" height="30px"> </td></tr>
								</c:when>
								<c:when test="${r[25]!=null and r[25]!='' }">
									<tr>
										<td colspan="3" style="text-align:center;">
											<b>${r[25]}</b>
										</td>
									</tr>
									<tr><td colspan="3" height="30px"> </td></tr>
								</c:when>
							</c:choose>
							<tr>
							
							<td colspan="3" style="text-align: justify; line-height:200%;" >
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
										<c:if test="${r[22]!=null and r[22]!=''}">
											(${r[22]})
										</c:if>
										<c:choose>
											<c:when test="${r[23]!=null and r[23]!=''}">
												(<b>${r[23]}</b>
												<c:if test="${r[11]!=null and r[11]!='' }">
													<b>${r[11]}</b>)
												</c:if>
											</c:when>
											<c:otherwise>
												<c:if test="${r[11]!=null and r[11]!=''}">
													<b>${r[11]}</b>)
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
							<!-- <tr><td colspan="3" height="30px"> </td></tr> -->
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
						<td colspan="3" class="content" style="line-height:200%;">
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
									<c:if  test="${r[22]!=null and r[22]!=''}">
										(${r[22]})
									</c:if>
									<c:choose>
										<c:when test="${r[23]!=null and r[23]!=''}">
											(<b>${r[23]}</b>
											<c:if test="${r[11]!=null and r[11]!='' }">
												<b>${r[11]}</b>)
											</c:if>
										</c:when>
										<c:otherwise>
											<c:if test="${r[11]!=null and r[11]!='' }">
												<b>${r[11]}</b>)
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
										<c:when test="${r[4]!=null and  r[4]!=''}">
											<c:choose>
												<c:when test="${r[5]!= null and r[5]!='' }">
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>(${r[5]}):
												</c:when>
												<c:otherwise>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>:
												</c:otherwise>
											</c:choose>
											
										</c:when>
									</c:choose>
								</c:otherwise>
							</c:choose>
							${r[0]}
						</td>
					</tr>
					<tr><td colspan="3" height="30px"> </td></tr>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:set var="count" value="1" />
				<c:if test="${nextReporter!=null && nextReporter!='' }">
					<tr>
						<td colspan="3" align="right">
							<spring:message code="part.nextReporterMessage" text="next"/>  ${nextReporter}
						</td>
					</tr>
					<tr><td colspan="3" height="30px"></td></tr>
				</c:if>
				</table>
				<table class="doBreak">
					<thead>
						<tr>
							<th class="left" width="200px">${r[25]}/${r[7]}</th>
							<th class="center" width="400px"><spring:message code="part.generalNotice" text="Unedited Copy"/></th>
							<th class="right" width="200px">${r[6]} - ${count}</th>
						</tr>
						<tr>
							<th class="left">${r[19]}</th>
							<c:choose>
								<c:when test="${r[26]!= null and r[26]!=''}">
									<th class="center"><spring:message code="part.previousReporterMessage" text="Previous Reporter"/> ${r[26]}</th>
								</c:when>
								<c:otherwise>
									<th></th>
								</c:otherwise>
							</c:choose>
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
									<b><spring:message code="part.pageHeading" text="Page Heading"/>:</b> ${r[1]}<br>
									<b><spring:message code="part.mainHeading" text="Main Heading"/> :</b> ${r[2]}
								</td>
							</tr>
							<tr><td colspan="3" height="30px"> </td></tr>
						</c:when>
					</c:choose>
					<tr>
					<td colspan="3" style="text-align: justify;line-height:200%" >
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
								<c:if  test="${r[22]!=null and r[22]!=''}">
									(${r[22]})
								</c:if>
								<c:choose>
									<c:when test="${r[23]!=null and r[23]!=''}">
										(<b>${r[23]}</b>
										<c:if test="${r[11]!=null and r[11]!='' }">
											<b>${r[11]}</b>)
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test="${r[11]!=null and r[11]!='' }">
											<b>${r[11]}</b>)
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
									<c:when test="${r[4]!=null and  r[4]!=''}">
										<c:choose>
												<c:when test="${r[5]!= null and r[5]!='' }">
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>(${r[5]}):
												</c:when>
												<c:otherwise>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${r[4]}</b>:
												</c:otherwise>
											</c:choose>
									</c:when>
								</c:choose>
							</c:otherwise>
						</c:choose>
						${r[0]}
						
					</td>
				</tr>
				<tr><td colspan="3" height="30px"> </td></tr>
				</c:otherwise>						
			</c:choose>
				
			<p id="page-number"></p>
			<c:set var="count" value="${count + 1}" />
			<c:set var="slot" value="${r[6]}" />
			<c:set var="mheading" value="${r[2]}" />
			<c:set var="pheading" value="${r[1]}" />
			<c:set var="member" value="${r[14]}"/>
			<c:set var="chairPerson" value="${r[9]}"/>
			<c:set var="nextReporter" value="${r[25]}"/>
		</c:forEach>
	</div>	
</div>
</body>
</html>