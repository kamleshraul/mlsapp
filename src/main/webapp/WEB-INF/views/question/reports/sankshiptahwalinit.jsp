<%@ include file="/common/taglibs.jsp" %>
<html>
	<head>
		<title></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<script type="text/javascript">
			$(document).ready(function() {	
				/**** Questions Bulletein Report ****/
				$("#bulletein_report").click(function(){				
					$(this).attr('href','#');
					ahwalBulleteinReport();
				});	
				/**** Starred & Unstarred Questions Report ****/
				$("#ahwal_starredUnstarred_report").click(function(){				
					$(this).attr('href','#');
					ahwalStarredUnstarredReport();
				});
				/**** Starred Departmentwise Questions Report ****/
				$("#starred_departmentwise_stats_report").click(function(){				
					$(this).attr('href','#');
					starredDepartmentwiseStatsReport();
				});				
				/**** Unstarred Departmentwise Questions Report ****/
				$("#unstarred_departmentwise_stats_report").click(function(){				
					$(this).attr('href','#');
					unstarredDepartmentwiseStatsReport();
				});
				/**** Starred & Unstarred Questions Report ****/
				$("#ahwal_shortnotice_stats_report").click(function(){				
					$(this).attr('href','#');
					ahwalShortNoticeStatsReport();
				});
				/**** Half Hour Discussion From Questions Report ****/
				$("#ahwal_hdq_condition_report").click(function(){				
					$(this).attr('href','#');
					ahwalHDQConditionReport();
				});
				/**** Half Hour Discussion Standalone Report ****/
				$("#ahwal_hds_condition_report").click(function(){				
					$(this).attr('href','#');
					ahwalHDSConditionReport();
				});
			});
		</script>		 
	</head>	
	<body>		
		<p id="error_p" style="display: none;">&nbsp;</p>		
		<table>
			<thead>
				<tr>
					<th><h3><spring:message code="question.ahwal_report" text="Sankshipt Ahwal Reports"/></h3></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<a href="#" id="bulletein_report" class="butSim link">
							<spring:message code="question.ahwal_bulletein_report" text="Bulletein Report"/>
						</a>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<a href="#" id="ahwal_starredUnstarred_report" class="butSim link">
							<spring:message code="question.ahwal_starredUnstarred_report_${selectedHouseType}" text="Starred & Unstarred Questions Report"/>
						</a>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>						
						<a href="#" id="starred_departmentwise_stats_report" class="butSim link">
							<spring:message code="question.starred_departmentwise_stats_report" text="Starred Questions Departmentwise Statistical Report"/>
						</a>
					</td>
				</tr>
				<c:if test="${selectedHouseType=='lowerhouse'}">
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td>						
							<a href="#" id="unstarred_departmentwise_stats_report" class="butSim link">
								<spring:message code="question.unstarred_departmentwise_stats_report" text="Unstarred Questions Departmentwise Statistical Report"/>
							</a>
						</td>
					</tr>
				</c:if>		
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<a href="#" id="ahwal_shortnotice_stats_report" class="butSim link">
							<spring:message code="question.ahwal_shortnotice_stats_report_${selectedHouseType}" text="Short Notice Statistical Report"/>
						</a>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<a href="#" id="ahwal_hdq_condition_report" class="butSim link">
							<spring:message code="question.ahwal_hdq_condition_report" text="Half Hour Discussion From Question Report"/>
						</a>
					</td>
				</tr>
				<c:if test="${selectedHouseType=='upperhouse'}">
					<tr><td>&nbsp;</td></tr>
					<tr>
						<td>
							<a href="#" id="ahwal_hds_condition_report" class="butSim link">
								<spring:message code="question.ahwal_hds_condition_report" text="Half Hour Discussion Standalone Report"/>
							</a>
						</td>
					</tr>
				</c:if>						
			</tbody>
		</table>			
	</body>
</html>