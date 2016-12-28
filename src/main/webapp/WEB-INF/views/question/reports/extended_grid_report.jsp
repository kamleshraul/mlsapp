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
						<c:set var="columnHeaders" value="${fn:split(reportHeaders[0][1], ';')}"/>
						<th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[0]}</th>
						<th style="padding-left: 15px;font-size: 15px;vertical-align: top;">${columnHeaders[1]}</th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[2]}</th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[3]}</th>
						<%-- <th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[4]}</th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[5]}</th>
						<th style="text-align: center;font-size: 15px;vertical-align: top;">${columnHeaders[6]}</th> --%>		
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${reportData}" var="r" varStatus="rowNumber">
						<tr>
							<td style="padding-left: 20px;vertical-align: top;">
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
							</td>
							<%-- <td style="padding-left: 30px;vertical-align: top;">
								${r[4]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[5]}
							</td>
							<td style="padding-left: 30px;vertical-align: top;">
								${r[6]}
							</td> --%>												
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</c:otherwise>
</c:choose>