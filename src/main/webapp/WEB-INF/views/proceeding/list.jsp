<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="proceeding.list" text="List Of Proceeding"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css">
		#goBtn{
			padding: 2px;
			border: 1px solid #004D80; 
			background-color: #B4D6ED; 
			border-radius: 5px;
			height: 12px;
		}
		#goBtn:hover{
			padding: 2px;
			border: 1px solid #004D80; 
			background-color: #6BB5E8; 
			border-radius: 5px;
			height: 12px;
		}
	</style>
	<script type="text/javascript">	
		$(document).ready(function(){
			$(".toolTip").hide();
			loadRosterDayFromSessions();
			/**** grid params which is sent to load grid data being sent ****/		
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val()
					+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val()
					+'&language='+$("#selectedLanguage").val()	
					+'&day='+$("#selectedDay").val()	
					+'&ugparam='+$("#ugparam").val()	
					);
			/**** edit Proceeding ****/
			$('#edit_record').click(function(){
				currentSelectedRow=$('#key').val();
				editProceeding();
			});
		
			$.get("ref/session?houseType="+$('#selectedHouseType').val()+"&sessionYear="+$("#selectedSessionYear").val()
					+"&sessionType="+$("#selectedSessionType").val(),function(data){
				$('#sessionId').val(data.id);
			});
			/**** delete Proceeding ****/
			$("#delete_record").click(function() {
				deleteProceeding();
			});		
			/****Searching roster****/
			$("#search").click(function() {
				searchRecord();
			});	
			
			$("#rosterwise").click(function() {
				rosterWiseReport();
			});	
			
			$("#reporterwise").click(function() {
				reporterWiseReport();
			});
			
					
			$("#memberwise").click(function(){
				if($("#memberText").css('display')=='none'){
					$("#memberText").css('display', 'inline-block');
				}else{
					$("#memberText").css('display', 'none');
				}
				
				 $( ".autosuggest").autocomplete({
						minLength:3,			
						source:'ref/member/supportingmembers?session='+$('#sessionId').val(),
						select:function(event,ui){			
							$("#memberId").val(ui.item.id);
						}	
				});
							
			});
			
			 $( ".autosuggest").autocomplete({
					minLength:3,			
					source:'ref/member/supportingmembers?session='+$('#sessionId').val(),
					select:function(event,ui){			
						$("#memberId").val(ui.item.id);
					}	
			});
			 
			 $('#createMemberwiseReport').click(function(){
				 params="member="+$('#memberId').val()+
				 "&houseType="+$("#selectedHouseType").val()
				 +"&sessionYear="+$("#selectedSessionYear").val()
				 +"&sessionType="+$("#selectedSessionType").val()
				 +'&language='+$("#selectedLanguage").val()	
				 +'&day='+$("#selectedDay").val();
				 showTabByIdAndUrl('details_tab', 'proceeding/memberwisereport?'+params);
				
			 });
		});	
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();			
			$("#cancelFn").val("rowDblClickHandler");			
			$('#key').val(rowid);
			showTabByIdAndUrl('details_tab', 'proceeding/'+rowid+'/edit?'+$("#gridURLParams").val());
		}			
	</script>
</head>
<body>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">	
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="generic.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="generic.delete" text="Delete"/>
			</a> |			
			<a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> |	
			<a href="#" id="rosterwise" class="butSim">
				<spring:message code="proceeding.rosterwisereport" text="Rosterwise Report"/>
			</a>|	
			<a href="#" id="reporterwise" class="butSim">
				<spring:message code="proceeding.reporterwise" text="Reporterwise Report"/>
			</a>|	
			<a href="#" id="memberwise" class="butSim">
				<spring:message code="proceeding.memberwise" text="Memberwise Report"/>
			</a>
			<div style="display: none;" id="memberText">
				<input type="text" class="autosuggest sText" id="memberOption" style="width: 100px;" />
				 	<a href="#" id="createMemberwiseReport" style="text-decoration: none;"><span id="goBtn"><spring:message code="part.memberwiseReport" text="Go" ></spring:message></span></a>
				 </div> |					
			<p>&nbsp;</p>
		</div>
		
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">	
	<input type="hidden" id="memberId" name="memberId">	
	<input type="hidden" id="sessionId" name="sessionId" value="">
	
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	</div>
</body>
</html>