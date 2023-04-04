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
				$("#unstarred_across_session_departmentwise_questions_report").click(function() {
					$(this).attr('href','#');
					unstarredAcrossSessionDepartmentwiseQuestionsReport();
				});
				/**** Starred & Unstarred Questions Report ****/
				$("#ahwal_shortnotice_stats_report").click(function(){				
					$(this).attr('href','#');
					ahwalShortNoticeStatsReport();
				});
				/**** Half Hour Discussion From Questions Condition Report ****/
				$("#ahwal_hdq_condition_report").click(function(){				
					$(this).attr('href','#');
					ahwalHDQConditionReport();
				});		
				
				/**** Rules Suspension Report ****/
				$("#ahwal_rules_suspension_report").click(function(){				
					$(this).attr('href','#');
					ahwalRulesSuspensionReport();
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
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>						
						<a href="#" id="unstarred_departmentwise_stats_report" class="butSim link">
							<spring:message code="question.unstarred_departmentwise_stats_report" text="Unstarred Questions Departmentwise Statistical Report"/>
						</a>
					</td>
				</tr>		
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>						
						<a href="#" id="unstarred_across_session_departmentwise_questions_report" class="butSim link">
							<spring:message code="question.unstarred_across_session_departmentwise_questions_report" text="Unstarred Questions Across Session Departmentwise Report"/>
						</a>
					</td>
				</tr>		
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
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<a href="#" id="ahwal_rules_suspension_report" class="butSim link">
							<spring:message code="question.ahwal_rules_suspension_report" text="Rules Suspension Motion"/>
						</a>
					</td>
				</tr>									
			</tbody>
		</table>			
	</body>
</html>