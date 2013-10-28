<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="roster.list" text="List Of Rosters"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			$("#selectedMember").val(0);
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val('houseType='+$("#selectedHouseType").val()
					+'&sessionYear='+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+'&language='+$("#selectedLanguage").val()
					+'&day='+$("#selectedDay").val()
					);
			
			
			/* $("#unedited_copy").click(function(){
				showUneditedProceeding();
			});
			$("#compiled_copy").click(function(){
				showCompiledProceeding();
			});
			$("#edited_copy").click(function(){
				showEditedProceeding();
			});
			
			$("#edit_copy").click(function(){
				showEditProceeding();
			}); */
			
		});	
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();
			//$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			//showTabByIdAndUrl('details_tab', 'roster/'+rowid+'/edit?'+$("#gridURLParams").val());
			showUneditedProceeding();
		}			
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">
			<security:authorize access="hasAnyRole('xyz')">
				<a href="#" id="unedited_copy" class="butSim">
					<spring:message code="editor.unedited" text="Unedited Copy"/>
				</a><div style="display: inline;" id="compileDiv">|
				<a href="#" id="compiled_copy" class="butSim">
					<spring:message code="editor.compiled" text="Compiled Copy"/>
				</a></div>|
				<a href="#" id="edited_copy" class="butSim">
					<spring:message code="editor.edited" text="Edited Copy"/>
				</a> |			
				<a href="#" id="edit_copy" class="butSim">
					<spring:message code="editor.edit" text="Editing"/>
				</a> |
			</security:authorize>		
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">		
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
</body>
</html>