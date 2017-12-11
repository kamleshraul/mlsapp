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
		.imageLink{
			width: 14px;
			height: 14px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		} 
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
	</style>
	<script type="text/javascript">	
	var flagVal=0;
		$(document).ready(function(){
			$(".toolTip").hide();
			//clearInterval(myVar); 
			//loadRosterDayFromSessions();
			/**** grid params which is sent to load grid data being sent ****/		
			if($('#selectedModule').val()=="COMMITTEE"){
				$("#sessionLinks").css("display","none");
				$("#gridURLParams").val("houseType=" + $("#selectedHouseType").val()
								+ "&sessionYear=0"
								+ "&sessionType=0"
								+ '&language=' + $("#selectedLanguage").val()
								+ '&day=' + $("#selectedDay").val() 
								+ "&committeeMeeting="+ $("#selectedCommitteeMeeting").val()
								+ '&ugparam='+ $("#ugparam").val()
								+ '&roleType='+$("#roleType").val());
			}else{
				$("#sessionLinks").css("display","inline");
				$("#gridURLParams").val("houseType=" + $("#selectedHouseType").val()
								+ "&sessionYear=" + $("#selectedSessionYear").val()
								+ "&sessionType=" + $("#selectedSessionType").val()
								+ '&language=' + $("#selectedLanguage").val()
								+ '&day=' + $("#selectedDay").val()
								+ '&ugparam=' + $("#ugparam").val()
								+ "&committeeMeeting=0"
								+ '&roleType='+$("#roleType").val());
			}
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
			
			/****Publish the roster proceeding for Committees & Editing****/
			$("#complete").click(function(){
				currentSelectedRow=$('#key').val();
				$.get("ref/rosterPublished?partId="+currentSelectedRow,function(data){
					if(data){
						$.prompt($('#chiefReporterPublishedMsg').val(),{
							buttons: {Ok:true, Cancel:false}, callback: function(v){
					        if(v){
					        	completeProceeding(currentSelectedRow);
					        }
						}});
					}
				});
				
			});
			
			$("#rosterwise").click(function() {
				rosterWiseReport();
			});	
			
			$("#reporterwise").click(function() {
				reporterWiseReport();
			});
			
			$("#proceedingwise").click(function(){
				proceedingwiseReport();
			});
					
			$("#memberwise").click(function(){
				flag=1;
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
			
			$('#members').click(function(){
				flag=2;
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
			
			$('#membersVal').click(function(){
				flag=3;
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
				 if(flag==1){
					showTabByIdAndUrl('details_tab', 'proceeding/memberwisereport?'+params);
				 }else if(flag==2){
					 showTabByIdAndUrl('details_tab', 'proceeding/memberwisereport1?'+params);
				 }else if(flag==3){
					 showTabByIdAndUrl('details_tab', 'proceeding/memberwisereport2?'+params);
				 }				
				
			 });
			 
			 $("#bookmark").click(function(){
				 var  row1 = $('#key').val();
					if (row1 == null || row1 == '') {
						$.prompt($('#selectRowFirstMessage').val());
						return false;
					} else {
						$("#selectionDiv1").hide();
						showTabByIdAndUrl('bookmarks_tab', 'proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentProceeding='+ row1 +'&count=1');
					}
			 });
			 
			
		});	
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}		
		/**** double clicking record in grid handler ****/		
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$.get("ref/isValidForNewRis?proceedingId="+rowid,function(data){
				if(data){
					$("#selectionDiv1").hide();			
					$("#cancelFn").val("rowDblClickHandler");			
					$('#key').val(rowid); 
					var params="proceeding="+$('#key').val()+
					'&language=' + $("#selectedLanguage").val();
					$.get('proceeding/getProceedingris?'+params, 
						    function(returnedData){
						 // var loc = window.location;
						 //   var pathName = loc.pathname.substring(0, loc.pathname.lastIndexOf('/') );
						 //   var MLSurl=loc.href.substring(0, loc.href.length - ((loc.pathname + loc.search + loc.hash).length - pathName.length))

					//	+""+returnedData.name+""+""+formattedNumber+""+formattedOrder+""+displayName);
						window.open('riscust://http://49.45.8.156/els/???1.0.0???word???'+returnedData.id+'???'+returnedData.displayName+'???'+returnedData.formattedOrder +'???'+returnedData.name +'???'+returnedData.formattedNumber +'???'+returnedData.value +'???'+returnedData.type +'???',"_self");
					}); 
				}else{
					$("#selectionDiv1").hide();			
					$("#cancelFn").val("rowDblClickHandler");			
					$('#key').val(rowid);
					//showTabByIdAndUrl('details_tab', 'proceeding/'+rowid+'/uploadproceeding?'+$("#gridURLParams").val());
					showTabByIdAndUrl('details_tab', 'proceeding/'+rowid+'/edit?'+$("#gridURLParams").val());
				}
				
			});
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
			<a href="#" id="complete" class="butSim">
				<spring:message code="roster.complete" text="Complete"/>
			</a> |		
			<%-- <a href="#" id="search" class="butSim">
				<spring:message code="generic.search" text="Search"/>
			</a> | --%>	
			<hr>
			<a href="#" id="rosterwise" class="butSim">
				<spring:message code="proceeding.rosterwisereport" text="Rosterwise Report"/>
			</a>|	
			<a href="#" id="reporterwise" class="butSim">
				<spring:message code="proceeding.reporterwise" text="Reporterwise Report"/>
			</a>|	
			<a href="#" id="proceedingwise" class="butSim">
				<spring:message code="proceeding.proceedingwiseReport" text="proceeding wise report"/>
			</a>|
			<div id="sessionLinks" style="display:inline;">
				<a href="#" id="memberwise" class="butSim">
					<spring:message code="proceeding.memberwise" text="Memberwise Report"/>
				</a>|	
				<a href="#" id="members" class="butSim">
					<spring:message code="proceeding.members" text="Member Report"/>
				</a>|	
				<a href="#" id="membersVal" class="butSim">
					<spring:message code="proceeding.members" text="Member Report"/>
				</a>
				<div style="display: none;" id="memberText">
					<input type="text" class="autosuggest sText" id="memberOption" style="width: 100px;" />
					 	<a href="#" id="createMemberwiseReport" style="text-decoration: none;"><span id="goBtn"><spring:message code="part.memberwiseReport" text="Go" ></spring:message></span></a>
				</div> |
			</div>
			<a href='javascript:void(0)'  id='bookmark' class='addBookmark'><img src='./resources/images/star_full.jpg' title='Bookmark' class='imageLink'/></a>|
			<p>&nbsp;</p>
		</div>
		
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">	
	<input type="hidden" id="memberId" name="memberId">	
	<input type="hidden" id="sessionId" name="sessionId" value="">
	
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="chiefReporterPublishedMsg" value="<spring:message code='proceeding.chiefReporterPublishedMsg' text='Chief Reporter has published the proceedings. Please inform Chief Reporter before making changes'/>" type="hidden">
	</div>
</body>
</html>