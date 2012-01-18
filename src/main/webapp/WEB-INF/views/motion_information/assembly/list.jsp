<%@ include file="/common/taglibs.jsp" %>
<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
	<title><spring:message code="member_personal_details.list" text="प्रस्ताव सूची"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
   		$(document).ready(function() {

   			$('#print_record').click(function(){
   				window.open('motion_assembly/print');  
   			}); 
   		});
		</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<a href="motion_assembly/new" id="new_record">
				<spring:message code="generic.new" text="New"/>
			</a> |
			<a href="motion_assembly" id="delete_record">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |
			<a href="#" id="print_record"> Print
			</a> |
			<a href="#" id="report"> Report
			</a> |
			<a href="#" id="statistics"> Statistics
			</a>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	</div>
</body>
</html>
