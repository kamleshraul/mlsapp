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
				/**** Half Hour Discussion Standalone Condition Report ****/
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