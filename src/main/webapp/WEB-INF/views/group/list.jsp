<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="group.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){			
			$("#selectionDiv1").show();	
			
			/**** grid params which is sent to load grid data being sent ****/					
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val() + "&year="+$("#selectedYear").val() + "&sessionType="+$("#selectedSessionType").val());
			
			$('#new_record').click(function(){				
				newRecord();
			});
			$('#edit_record').click(function(){				
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#copy").click(function() {
				newRecord("copy");
			});
		});		
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a>|
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |
			<a href="#" id="copy" class="butSim">
				<spring:message code="generic.copy" text="Copy Ministries"/>
			</a> 
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">	
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	
	</div>
</body>
</html>