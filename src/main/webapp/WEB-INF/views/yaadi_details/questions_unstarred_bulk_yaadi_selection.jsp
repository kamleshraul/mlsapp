<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<script type="text/javascript">
		/**** Check/Uncheck Submit All ****/		
		$("#chkall").change(function(){
			if($(this).is(":checked")){
				$(".action:not([disabled])").attr("checked","checked");
			}else{
				$(".action:not([disabled])").removeAttr("checked");
			}
		});
	</script>
	<style type="text/css">
		.uiTable {
			width: 680px !important;
		}
	</style>
</head>
<body>
	<div>
		<c:choose>
			<c:when test="${!(empty selectedYaadis) }">
				<h3>${success} ${failure}</h3>
				<table class="uiTable">
						<tr>
							<th><input type="checkbox" id="chkall" name="chkall" class="sCheck" value="true">
							<spring:message code="yaadidetails.bulkYaadiUpdate.submitall" text="Submit All"></spring:message></th>
							<th><spring:message code="yaadidetails.number" text="Yaadi Number"></spring:message></th>
							<th><spring:message code="yaadidetails.device_count" text="Questions Count"></spring:message></th>
							<th><spring:message code="yaadidetails.status" text="Yaadi Status"></spring:message></th>
							<th><spring:message code="yaadidetails.laying_date" text="Yaadi Laying Date"></spring:message></th>
						</tr>			
						<%-- <c:forEach start="0" end="${selectedYaadisCount-1}" var="i"> --%>
						<c:forEach items="${selectedYaadis}" var="selectedYaadi">
							<tr>	
								<td><input type="checkbox" id="chk${selectedYaadi[0]}" name="chk${selectedYaadi[0]}" class="sCheck action" value="true" ${selectedYaadi[6]=='yes'?'disabled="disabled"':''} style="margin-right: 10px;"></td>					
								<td>${selectedYaadi[1]}</td>
								<td>${selectedYaadi[5]}</td>
								<td>
									${selectedYaadi[3]}
									<input type="hidden" id="sta${selectedYaadi[0]}" name="sta${selectedYaadi[0]}" value="${selectedYaadi[2]}"/>
								</td>
								<td>${selectedYaadi[4]}</td>
								
							</tr>
						</c:forEach>
				</table>
			</c:when>
			<c:otherwise>
				<spring:message code="yaadidetails.bulkYaadiUpdate.no_yaadis" text="No Yaadis Found"></spring:message>
			</c:otherwise>
		</c:choose>	
	</div>
</body>