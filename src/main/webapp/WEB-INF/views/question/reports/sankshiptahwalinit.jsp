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
				
				/**** Departmentwise Questions Report ****/
				$("#starred_departmentwise_stats_report").click(function(){				
					$(this).attr('href','#');
					starredDepartmentwiseStatsReport();
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
						<a href="#" id="starred_departmentwise_stats_report" class="butSim link">
							<spring:message code="question.starred_departmentwise_stats_report" text="Starred Questions Departmentwise Statistical Report"/>
						</a>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>						
						<a href="#" id="memberwise_questions_report" class="butSim link">
						<spring:message code="question.memberwisereport" text="Member's Questions Report"/>
					</a>
					</td>
				</tr>
			</tbody>
		</table>			
	</body>
</html>