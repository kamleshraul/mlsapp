<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<title>QIS Statistical Counts Report</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<style type="text/css">
			.strippedTable1{
				font-family: "Lucida Sans Unicode", "Lucida Grande", Sans-Serif;
				font-size: 12px;	
				width: 800px;
				text-align: left;
				border-collapse: collapse;
				border-left: 2px solid #000000;
				border-right: 2px solid #000000;
				border-bottom: 2px solid #000000;
			}
			.strippedTable1 tbody{
			}
			.strippedTable1 tr:nth-child(even) {background: #C6D3DD/*#CCC*/}
			.strippedTable1 tr:nth-child(odd) {background: #DAE4EC/*#FFF*/}
			.strippedTable1 th
			{
				font-size: 13px;
				font-weight: bold;
				padding: 8px;
				/* background: #FEFFD1; */	
				border-top: 2px solid #000000;
				border-bottom: 1px solid #000000;
				/* color: #039; */
				background-color: #A2C6E4;	
			}
			.strippedTable1 td
			{
				/* padding: 8px; */
				/* background: #e8edff; */
				border-bottom: 1px solid #000000;
				/* color: #669; */
				border-top: 1px solid transparent;
				max-width: 250px;
				vertical-align: top;
			}
			td {
				vertical-align: top;
			}
		</style>
	</head>
	<body>
		<p id="error_p" style="display: none;">&nbsp;</p>
		<c:if test="${(error!='') && (error!=null)}">
			<h4 style="color: #FF0000;">${error}</h4>
		</c:if>
		<div>
		<c:choose>
			<c:when test="${empty reportData}">
				<h3><spring:message code="qis.statistical_counts_report.noentriesfound" text="No Entries Found."/></h3>
			</c:when>
			<c:otherwise>
				<div id="reportDiv" >
					<div align="center">${reportHeaders[0][0]}</div>
					
					<table border="1" style="width: 90%;margin-top: 20px; margin-left: 25px; font-size: 15px;">
						<thead>
							<tr>
								<%-- <c:forEach begin="1" end="${fn:length(reportHeaders[0]) - 1}" var="index">
									<th style="text-align: center;font-size: 15px;vertical-align: top;">
										${reportHeaders[0][index]}
									</th>
								</c:forEach> --%>
								<th style="width: 10%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][1]}
								</th>
								<th style="width: 22.5%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][2]}
								</th>
								<th style="width: 22.5%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][3]}
								</th>
								<th style="width: 15%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][4]}
								</th>
								<th style="width: 15%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][5]}
								</th>
								<th style="width: 15%;text-align: center;font-size: 15px;vertical-align: top;">
									${reportHeaders[0][6]}
								</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${dataMap1}" var="map1" varStatus="map1Cnt">
								<tr>
									<td style="width: 10%;">${map1.key}</td>
									<td style="width: 75%;" colspan="${2*2}">							
										<table>
											<c:forEach items="${map1.value}" var="map2" varStatus="map2Cnt">
												<tr>
													<td style="width: 22.5%;">${map2}</td>
													<td style="width: 37.5%;">
														<table>
															<c:forEach items="${dataMap2[map2]}" var="map3" varStatus="map3Cnt">
																<tr>
																	<td style="width: 22.5%;">${map3}</td>
																	<c:set var="map3_count" value="dataMap3_${map3}"/>
																	<td style="width: 15%;">${requestScope[map3_count]}</td>
																</tr>
															</c:forEach>
														</table>
													</td>
													<c:set var="map2_count" value="dataMap2_${map2}"/>
													<td style="width: 15%;">${requestScope[map2_count]}</td>
												</tr>
											</c:forEach>
										</table>									
									</td>
									<c:set var="map1_count" value="dataMap1_${map1.key}"/>
									<td style="width: 15%;">${requestScope[map1_count]}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
		</div>
	</body>
</html>