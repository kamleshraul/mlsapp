<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.list" text="List Of Members"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){	
			//here we are trying to add date mask in grid search when field names
			//ends with Date
			$(".sf .field").change(function(){
				var field=$(this).val();
				if(field.indexOf("Date")!=-1){
					$(".sf .data").mask("99/99/9999");
				}
			});			
			$('#list_tab').click(function(){
				showList();
			});	
			$('#personal_tab').click(function(){
				editMemberPersonalDetails($('#key').val());
			});
			$('#contact_tab').click(function(){
				editMemberContactDetails($('#key').val());
			});
			$('#other_tab').click(function(){
				editMemberOtherDetails($('#key').val());
			});
			$('#house_tab').click(function(){
				listMemberHouseDetails($('#key').val());
			});	
			 $('#minister_tab').click(function(){
				listMemberMinisterDetails($('#key').val()); 	
			}); 		
			$('#party_tab').click(function(){
				listMemberPartyDetails($('#key').val());
			});	
			$('#election_tab').click(function(){
				listMemberElectionDetails($('#key').val());
			});	
			$('#suspension_tab').click(function(){
				listMemberSuspensionDetails($('#key').val());
			});	
			$('#report_tab').click(function(){
				listMemberFullProfileView($('#key').val());
			});	
			$(document).keydown(function (e){
				if(e.which==78 && e.ctrlKey){
					if($('#srole').val()=='SUPER_ADMIN') { //also allow for MIS roles later
						newRecord();
					}					
				}
				if(e.which==83 && e.ctrlKey){
					$('#submit').trigger('click');
				}
				if(e.which==76 && e.ctrlKey){
					showList();
				}
				if(e.which==79 && e.ctrlKey){
					if($('#srole').val()=='SUPER_ADMIN') { //also allow for MIS roles later
						editRecord($('#key').val());
					}					
				}
				if(e.which==8 && e.ctrlKey){
					if($('#srole').val()=='SUPER_ADMIN') { //also allow for MIS roles later
						deleteRecord($('#key').val());
					}
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});
			//houseType is passed so as to appropriately populate select assembly/council select box
			showTabByIdAndUrl('list_tab','member/list?houseType='+$('#houseType').val());			
		});
				
		function showList() {
			//houseType is passed so as to appropriately populate select assembly/council select box			
			showTabByIdAndUrl('list_tab','member/list?houseType='+$('#houseType').val());								
		}	
		function newRecord() {
			//here house parameter will be used to add house member role association i.e default role and so need to be present in new.jsp/edit.jsp
			//also housetype is needed to load proper background image
			showTabByIdAndUrl('personal_tab','member/personal/new?house='+$('#house').val()+'&houseType='+$('#houseType').val()+'&usergroupType='+$('#currentusergroupType').val());
			$("#key").val("");			
			$("#cancelFn").val("newRecord");
		}
		function editRecord(row) {			
			var row=$('#key').val();
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			$("#cancelFn").val("editRecord");
			showTabByIdAndUrl('personal_tab','member/personal/'+row+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val()+'&usergroupType='+$('#currentusergroupType').val());		
		}	

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('personal_tab', 'member/personal/'+rowid+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val()+'&usergroupType='+$('#currentusergroupType').val());
		}			
		
		function deleteRecord(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$.prompt($('#confirmDeleteMessage').val()+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('member/personal/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
					    showList();
				        });
			        }
				}});
			}
		}	

		function editMemberPersonalDetails(row) {
			var row=$('#key').val();			
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$("#cancelFn").val("editMemberPersonalDetails");
				showTabByIdAndUrl('personal_tab','member/personal/'+row+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val()+'&usergroupType='+$('#currentusergroupType').val());
				}
		}

		function editMemberContactDetails(row) {
			var row=$('#key').val();		
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$("#cancelFn").val("editMemberContactDetails");
				showTabByIdAndUrl('contact_tab','member/contact/'+row+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val()+'&usergroupType='+$('#currentusergroupType').val());				
				}
		}

		function editMemberOtherDetails(row) {
			var row=$('#key').val();
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				$("#cancelFn").val("editMemberOtherDetails");
				showTabByIdAndUrl('other_tab','member/other/'+row+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val()+'&usergroupType='+$('#currentusergroupType').val());							
				}
		}

		function listMemberHouseDetails(row) {			
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{							
				var housetype=$('#houseType').val();
				var house=$('#house').val();
				//in case of lowerhouse pass member,houseType and house as parameter.
				//in case of upperhouse pass member and houseType as parameter.
				if(house!=""){				
				showTabByIdAndUrl('house_tab','member/house/list?member='+$('#key').val()+'&houseType='+housetype+'&house='+house);				
				}else{
					showTabByIdAndUrl('house_tab','member/house/list?member='+$('#key').val()+'houseType='+housetype);	
				}
				}
		}
		function listMemberMinisterDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('minister_tab','member/minister/list?house='+$('#house').val()+'&houseType='+$("#houseType").val());
				}
		}
		function listMemberPartyDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('party_tab','member/party/list?house='+$('#house').val()+'&houseType='+$("#houseType").val());
				}
		}
		function listMemberElectionDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				var housetype=$('#houseType').val();				
				showTabByIdAndUrl('election_tab','member/election/list?house='+$('#house').val()+'&houseType='+$("#houseType").val());
				}
		}	
		function listMemberSuspensionDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				var housetype=$('#houseType').val();				
				showTabByIdAndUrl('suspension_tab','member/suspension/list?house='+$('#house').val()+'&houseType='+$("#houseType").val());
				}
		}
		function listMemberFullProfileView(row){
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				var url = "member/view/"+row;
				showTabByIdAndUrl('report_tab',url);
				}
		}
	</script>
</head>
<body>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<!-- .section -->
	<div class="clearfix tabbar">
		<ul class="tabs">
			<li>
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>	
			<security:authorize	access="hasAnyRole('SUPER_ADMIN')">
			<li>
				<a id="personal_tab" href="#" class="tab">
				   <spring:message code="member.module.memberPersonalDetails" text="Personal Details">
				   </spring:message>
				</a>
			</li>
			</security:authorize>
			<li>
				<a id="contact_tab" href="#" class="tab">
				   <spring:message code="member.module.memberContactDetails" text="Contact Details">
				   </spring:message>
				</a>
			</li>
			<li>
				<a id="other_tab" href="#" class="tab">
				   <spring:message code="member.module.memberOtherDetails" text="Other Details">
				   </spring:message>
				</a>
			</li>
			<security:authorize	access="hasAnyRole('SUPER_ADMIN')">
			<li>
				<a id="house_tab" href="#" class="tab">
					<c:choose>
					<c:when test="${housetype=='lowerhouse'}">
					<spring:message code="member.house.lowerhouserole" text="Assembly">
				   	</spring:message>
					</c:when>
					<c:otherwise>
					<spring:message code="member.house.upperhouserole" text="Council">
				   	</spring:message>
					</c:otherwise>
					</c:choose>				   
				</a>
			</li>	
			<li>
				<a id="minister_tab" href="#" class="tab">
				   <spring:message code="member.module.memberMinister" text="Minister">
				   </spring:message>
				</a>
			</li>
			<li>
				<a id="party_tab" href="#" class="tab">
				   <spring:message code="member.module.memberPoliticalParty" text="Political Party">
				   </spring:message>
				</a>
			</li>
			<li>
				<a id="election_tab" href="#" class="tab">
				   <spring:message code="member.module.memberElectionDetails" text="Election Details">
				   </spring:message>
				</a>
			</li>		
			<li>
				<a id="suspension_tab" href="#" class="tab">
				   <spring:message code="member.module.memberSuspensionDetails" text="Suspension Details">
				   </spring:message>
				</a>
			</li>		
			<li>
				<a id="report_tab" href="#" class="tab">
				   <spring:message code="member.profileView.heading" text="Member Profile View">
				   </spring:message>
				</a>
			</li>
			</security:authorize>	
		</ul>
		<div class="tabContent clearfix">
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		<input type="hidden" name="houseType" id="houseType" value="${housetype}">
		<input type="hidden" name="srole" id="srole" value="${role}">
		<input type="hidden" name="house" id="house" value="">
		<input type="hidden" name="currentusergroup" id="currentusergroup" value="${usergroup}">
		<input type="hidden" name="currentusergroupType" id="currentusergroupType" value="${usergroupType}">
		</div> 
		<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>