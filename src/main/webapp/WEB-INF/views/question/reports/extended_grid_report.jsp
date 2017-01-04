<%@ include file="/common/taglibs.jsp"%>
<c:choose>
	<c:when test="${empty reportData}">
		<h3><spring:message code="extended_grid_report.noentriesfound" text="No Entries Found."/></h3>
	</c:when>
	<c:otherwise>
		<div id="reportDiv" >
			<div align="center">${reportHeaders[0][0]}</div>
			
			<table class="strippedTable" border="1" style="margin-top: 20px; margin-left: 25px; font-size: 15px;">
				<thead>
					<tr>
						<c:forEach begin="1" end="${fn:length(reportHeaders[0]) - 1}" var="index">
							${reportHeaders[0][index]}
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${reportData}" var="r" varStatus="rowNumber">
						<tr>
							<%-- <td style="padding-left: 20px;vertical-align: top;">
								${r[0]}
							</td>
							<td style="padding-left: 15px;vertical-align: top;">
								${r[1]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[2]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[3]}
							</td> --%>
							<%-- <td style="padding-left: 30px;vertical-align: top;">
								${r[4]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[5]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[6]}
							</td> --%>	
							
							<c:forEach begin="0" end="${fn:length(r) - 1}" var="index">
								${r[index]}
							</c:forEach>											
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>