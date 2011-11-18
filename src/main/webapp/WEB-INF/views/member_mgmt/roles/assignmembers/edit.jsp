<%@ include file="/common/taglibs.jsp" %>
<html>
<body>
<form class="wufoo" action="member_role/assignmembers/updateMemberRoles" method="post"">
<div class="info">
			<div style="background-color:#C1CDCD; ;padding: 3px"><spring:message code="generic.mandatory.label" text="Note: Fields marked * are mandatory"/></div>
</div>
<ul>
		<li>
		<label class="desc"><spring:message code="mms.assignmembers.roleid" text="Role Id"/></label>
			<div>
			<input type="text" value="${role.id}" name="roleId" id="roleId" readonly="readonly">
			</div>
		</li>
		<li>
		<label class="desc"><spring:message code="mms.assignmembers.rolename" text="Role Name"/></label>
			<div>
			<input type="text" value="${role.name}" name="roleName" id="roleName" readonly="readonly">
			</div>
		</li>
		
</ul>
<div id="grid_container">
		<table id="memberRoleGrid"></table> 
		<div id="memberrolegrid_pager"></div>
</div>
<ul>
<li class="buttons">
		<input type="hidden" name="memberRolesToUnassign" id="memberRolesToUnassign" >
		<input type="hidden" name="assignmentDate" value="${assignmentDate}" id="assignmentDate">
		<input id="saveForm" class="btTxt" type="button" value="<spring:message code='mms.assignmembers.unassign' text='Update Membership'/>" />
	</li>
</ul>
</form>
</body>
<head>
	<title><spring:message code="mms.assignroles.edit.title" text="Edit Assigned Members"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<link rel="stylesheet" media="screen" href="./resources/css/tables.css" />	
	<script type="text/javascript">
	function loadMemberGrid(gridId, baseFilter) {
		var c_grid = null;
		var unselectedRow=null;
		$.ajax({async:false,url:'grid/' + gridId + '/meta.json', success:function(grid) {
				c_grid = $('#memberRoleGrid').jqGrid({
				scroll:1,
				altRows:true,
				autowidth:true,
				shrinkToFit:true,
				ajaxGridOptions:{async:false},
				url:'member_role/assignmembers/assigned/'+$('#roleId').val()+'.json',
				datatype: 'json',
				mtype: 'GET',
				colNames:eval(grid.colNames),
				colModel :eval(grid.colModel),
				pager: '#memberrolegrid_pager',
				rowNum:grid.pageSize,
				sortname: 'id',
				sortorder:grid.sortOrder,
				viewrecords: true,
				jsonReader: { repeatitems : false},
				gridview:true,
				multiselect:eval(grid.multiSelect),
				postData: {
					"baseFilters": baseFilter
				},	
				loadComplete:function(data,obj){
					$('.cbox').attr("checked","checked");
					$('#cb_memberRoleGrid').removeAttr("checked");
														
				},		
				onSelectRow:function(rowId,status) {
						if(status){			
							$('input[type="checkbox"][id$="'+rowId+'"]').removeAttr("checked");	
						}else{
							$('input[type="checkbox"][id$="'+rowId+'"]').attr("checked","checked");	
						}										
				}
			});
			$("#memberRoleGrid").jqGrid('navGrid','#memberrolegrid_pager',{edit:false,add:false,del:false, search:true},{},{},{},{multipleSearch:true});
			$("#memberRoleGrid").jqGrid('bindKeys');			
		}});
		return c_grid;
	};
	$(document).ready(function(){
		loadMemberGrid(23);			
		
		$('#saveForm').click(function(){
			var rowsToUnassign=new Array();
			$('input[type="checkbox"][id*="memberRoleGrid"]').each(function(){
				if($(this).attr("checked")==undefined){
					rowsToUnassign.push($(this).attr("id").split("_")[2]);
				}	
			});
			$('#memberRolesToUnassign').val(rowsToUnassign);

			$.post($('form').attr('action'),  
		            $("form").serialize(),  
		            function(data){	
	   				$('.contentPanel').html(data);	
	   				$('#refresh').val($('#refreshSe').val());	   				      
		   				if($('#info_type').val()=='success'){			   				
			   	   	   		$("#grid").trigger("reloadGrid");		   				
						}		   					   						   					
		            }); 						
		});	
		
	});
	</script>
</head>
</html>