<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.list" text="List Of Members"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
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
			showTabByIdAndUrl('list_tab','member/list');
			
		});
				
		function showList() {
			showTabByIdAndUrl('list_tab','member/list');								
		}	
		function newRecord() {
			showTabByIdAndUrl('personal_tab','member/personal/new');
				
		}
		function editRecord(row) {
			if(row==null||row==''){
				$.prompt($('#selectRowFirstMessage').val());
				return false;
			}
			showTabByIdAndUrl('personal_tab','member/personal/'+row+'/edit');
		}		

		function rowDblClickHandler(rowid, iRow, iCol, e) {
			showTabByIdAndUrl('personal_tab', 'member/personal/'+rowid+'/edit');
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
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('personal_tab','member/personal/'+row+'/edit');
				}
		}

		function editMemberContactDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('contact_tab','member/contact/'+row+'/edit');
				}
		}

		function editMemberOtherDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('other_tab','member/other/'+row+'/edit');
				}
		}

		function listMemberHouseDetails(row) {			
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('house_tab','member/house/list');
				}
		}
		function listMemberMinisterDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('minister_tab','member/minister/list');
				}
		}
		function listMemberPartyDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('party_tab','member/party/list');
				}
		}
		function listMemberElectionDetails(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());		
				return;
			}
			else{
				showTabByIdAndUrl('election_tab','member/election/list');
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
				<a id="personal_tab" href="#" class="tab">
				   <spring:message code="member.module.memberPersonalDetails" text="Personal Details">
				   </spring:message>
				</a>
			</li>
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
			<li>
				<a id="house_tab" href="#" class="tab">
					<c:choose>
					<c:when test="${housetype=='lowerhouse'}">
					<spring:message code="generic.lowerhouse" text="Assembly">
				   	</spring:message>
					</c:when>
					<c:when test="${housetype=='upperhouse'}">
					<spring:message code="generic.upperhouse" text="Council">
				   	</spring:message>
					</c:when>
					<c:when test="${housetype=='both'}">
					<spring:message code="generic.house" text="House">
				   	</spring:message>
					</c:when>
					<c:otherwise>
					<spring:message code="generic.defaulthouse" text="House">
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
		</ul>
		<div class="tabContent clearfix">
		</div>		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='generic.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
		</div> 
</body>
</html>