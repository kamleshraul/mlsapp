<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="user.list"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var urlPattern=$('#urlPattern').val();
		$(document).ready(function(){
			$('#list_tab').click(function(){
				showList();
			});	
			$('#details_tab').click(function(){
				var row = $("#key").val();
				if(row==null||row==""){
					$.prompt($('#selectRowFirstMessage').val());		
					return false;
				}else{	
					if($('#currentPage').val()=="list") {
						editRecord();
					}					
				}
			});
			$('#groups_tab').click(function(){
				var row = $("#key").val();
				if(row=="undefined"||row==""){
					var row = $("#user").val();
					if(row==undefined||row==""){
					$.prompt($('#selectRowFirstMessage').val());		
					return false;
					}else{
						showTabByIdAndUrl('groups_tab','user/usergroup/list'+'?user='+row);
					}
				}else{
					showTabByIdAndUrl('groups_tab','user/usergroup/list'+'?user='+row);
				}
			});
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					newRecord();
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					editRecord($('#key').val());
				}
				if(e.which==8 && e.ctrlKey){
					deleteRecord($('#key').val());
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			showTabByIdAndUrl('list_tab','user/list');	
		});	
		function showList() {
			//here grid will be displayed and the key will be set automatically
			showTabByIdAndUrl('list_tab','user/list');
		}	
		function rowSelectHandler(rowid,status){
			//on row select key will be set
			if($('#key')){
				$('#key').val(rowid);					
			}		 
		}
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			//here when we are clicking a particular row then we will first set the key and then load the edit
			//page. 
			$("#key").val(rowid);
			showTabByIdAndUrl('details_tab', 'user/'+rowid+'/edit');
		}
		function newRecord() {
			//on clicking new record key set to empty as the user id has not been created till then
			$("#cancelFn").val("newRecord");
			showTabByIdAndUrl('details_tab','user/new');			
		}		
		function editRecord() {
			//this function will be called on double clicking row in grid or by cliking edit link on list page.
			//this function is also called when details tab is clicked
			var row = $("#key").val();
			if(row==null||row==""){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			}else{
				showTabByIdAndUrl('details_tab','user/'+row+'/edit');
			}
		}		
		function deleteRecord(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return false;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('user/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showList();
				        }).fail(function(){
							if($("#ErrorMsg").val()!=''){
								$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
							}else{
								$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
							}
							scrollTop();
						});
			        }
				}});
			}
		}	
		function resetPassword() {
			var row = $("#key").val();
			if(row==null||row==""){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			}else{
				var username = $('#grid').jqGrid('getCell', row, 'credential.username');
				showTabByIdAndUrl('details_tab','user/resetPassword?username='+username);
			}
		}
		function loginForSupport() {
			var row = $("#key").val();
			if(row==null||row==""){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			}else{
				var credentialId = $('#grid').jqGrid('getCell', row, 'credential.id');
				var parameters = "credentialId="+credentialId;
				$.ajax({url: 'ref/user/checkIfAllowedForMultiLogin', data: parameters,
					type: 'GET',
			        async: false,
					success: function(data) {
						var promptMsg = "";
						var successMsg = "";
						if(data==false) {
							promptMsg = "Do you want to enable the support now?";
							successMsg = "Login for support enabled now!"
						} else if(data==true) {
							promptMsg = "Login for support already enabled! Do you want to disable the support now?";
							successMsg = "Login for support disabled now!"
						} else {
							return false;
						} 						
						$.prompt(promptMsg,{
							buttons: {Ok:true, Cancel:false}, callback: function(v){
					        if(v){
					        	$.post('user/credential/updateAllowedForMultiLogin?'+parameters, function(data) {
									if(data==1) {
										$.prompt(successMsg);
									} else {
										alert("Some error occured.. contact support");
									}
								});
					        }
						}});
					}
				});
			}
		}
		function sendNotification() {
			var selectedUserIds = $("#grid").jqGrid ('getGridParam', 'selarrrow');
			if(selectedUserIds==null||selectedUserIds==""){								
				$.prompt($('#selectAllUsersPromptMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){				        	
				        	showTabByIdAndUrl('details_tab','user/sendNotification?usernames=');
				        } else {
				        	$.prompt($('#selectRowFirstMessage').val());
							return false;
				        }
					}
				});
			}else{
				var usernames = "";
				for(var i=0; i<selectedUserIds.length; i++) {
					usernames += $('#grid').jqGrid('getCell', selectedUserIds[i], 'credential.username');
					usernames += ',';
				}				
				showTabByIdAndUrl('details_tab','user/sendNotification?usernames='+usernames);
			}
		}
		function resetHighSecurityPassword() {
			var row = $("#key").val();
			if(row==null||row==""){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			}else{
				var username = $('#grid').jqGrid('getCell', row, 'credential.username');
				console.log("username: " + username);
				showTabByIdAndUrl('details_tab','user/resetHighSecurityPassword?username='+username);
			}
		}
	</script>
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>
			<li>
				<a id="details_tab" href="#" class="tab">
				   <spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>	
			<li>
				<a id="groups_tab" href="#" class="tab">
				   <spring:message code="user.groups" text="Groups"></spring:message>
				</a>
			</li>		
		</ul>
		<div class="tabContent clearfix">
			<c:if test="${(error!='') && (error!=null)}">
				<h4 style="color: #FF0000;">${error}</h4>
			</c:if>
		</div>
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired user first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		<input type="hidden" id="currentPage" value="list">
		<input type="hidden" id="selectAllUsersPromptMsg" name="selectAllUsersPromptMsg" value="<spring:message code='user.selectAllUsersPromptMsg' text='Do you want to send notification to all users?'></spring:message>" disabled="disabled">
	</div> 
</body>
</html>