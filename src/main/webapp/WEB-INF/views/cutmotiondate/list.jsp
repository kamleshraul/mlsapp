<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="session.cutmotiondate.list" text="List of CutMotionDate Settings"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			/**** initially list is hidden ****/
			$('#list_record').hide();
			$('#gridURLParams').val("houseType="+$("#selectedHouseType").val()+
			"&deviceType="+$("#selectedDeviceType").val()+"&sessionYear="+$("#selectedSessionYear").val()+
			"&sessionType="+$("#selectedSessionType").val()+
			"&usergroup="+$("#userGroup").val()+
			"&usergroupType="+$("#userGroupType").val()+
			"&role="+$("#role").val());
			
			//initially edit,delete and search links will be visible
			$('#editDeleteLinks').show();
			
			//new record		
			$('#new_record').click(function(){
				$('#editDeleteLinks').hide();
				$('#list_record').show();
				$("#selectionDiv1").hide();					
				newCutMotionDate();										
			});
			$('#edit_record').click(function(){
				$('#editDeleteLinks').hide();
				$('#list_record').show();	
				$("#selectionDiv1").hide();							
				editCutMotionDate();
			});
			$("#delete_record").click(function() {
				deleteCutMotionDate();
			});
			$("#list_record").click(function() {
				$('#editDeleteLinks').show();
				$('#list_record').hide();
				$("#selectionDiv1").show();						
				listCutMotionDate();
			});
			$("#search").click(function() {
				searchRecord();
			});
		});				
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
			<div class="commandbarContent">
			<a href="#" id="new_record" class="butSim">
				<spring:message code="cutmotiondate.new" text="New"/>
			</a>
			<span id="editDeleteLinks"> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="cutmotiondate.edit" text="Edit"/>
			</a> |
			<security:authorize access="hasAnyRole('SUPER_ADMIN')">
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="cutmotiondate.delete" text="Delete"/>
			</a> |
			</security:authorize>
			<a href="#" id="search" class="butSim">
				<spring:message code="cutmotiondate.search" text="Search"/>
			</a> 
			</span> | 
			<a href="#" id="list_record" class="butSim">
				<spring:message code="generic.list" text="List"/>
			</a>			
			<p>&nbsp;</p>
		</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">
	
</body>
</html>
