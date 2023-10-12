<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="message"
		text="Add Message Resource" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function() {
		initControls();
		$('#key').val('');
		$("#assigneeDiv").hide();
		$("#hiDiv").hide();
		loadStatusForDevice($("#selecteDeviceType").val())
		
		
		$("#selectedWorkflow").change(function(){
			var val = $(this).val();
			if(val == 'start_workflow_bulk') {
				$("#wokflowDiv").show();
			}else{
				$("#wokflowDiv").hide();
			}
		})
		
		
		
		$("#selectedUserGroupType").change(function(){
			var val = $(this).val();
			if(val == 'department_deskofficer') {
				$("#assigneeDiv").show();
			}else{
				$("#assigneeDiv").hide();
			}
		})
		
		$("#selecteDeviceType").change(function(){
			 var val = $(this).val()
			  
		     if(val != '')
		      {
		    	  loadStatusForDevice(val)
		    		 
		      }
			});
		
		
		$("#posterSubmit").click(function(){
			
			if($("#supportIssueNumber") == '' || $("#number") == '') {
				$.prompt($('#missingDetailMsg'.val()))
			} else {
				$.prompt($('#submissionMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
						$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
						$.post('admin/posterUtility?assignee='+$("#assignee").val(),
					        	{
								workflow:$("#selectedWorkflow").val(),
								selecteDeviceType:$("#selecteDeviceType").val(),
								number:$("#number").val(),
								selectedStatus:$("#selectedStatus").val(),
								selectedUserGroupType:$("#selectedUserGroupType").val(),
								level:$("#level").val(),
								assignee:$("#assignee").val(),
								issueNumber :$('#supportIssueNumber').val()
							 	},
			    	            function(data){
							 		$("#hiDiv").hide();	
			    					$.unblockUI();	
			    					$("#bulkResultDiv").empty();
			    					
			    					if(data == 'SUCCESS'){
			    						
			    							$("#hiDiv").show();
			    					
			    					}else{
			    						$("#error_p").html($("#ErrorMsg").val()).css({ 'color':'white','display':'block'});
			    					}
			    					//$("#bulkResultDiv").html(data);	
			    	            }
			    	            ).fail(function(){
			    					$.unblockUI();
			    					if($("#ErrorMsg").val()!=''){
			    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			    					}else{
			    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			    					}
			    					scrollTop();
			    				});
			        	}}});
			}
			
			
		})
		
		
	});
	
	
	function loadStatusForDevice(selectedDeviceType){
		 $.get('posterUtility/getStatusForParticularDeviceType/'+selectedDeviceType,function(data){
			 if(data != null) {
				 $("#selectedStatus").empty()
	 		   		var option = '';
	 		   		for (let i = 0; i < data.length; i++) {
	 		   		 option = option +'<option value="'+data[i].type+'"> '+data[i].name+' </option>  '
	 		   		}
	 		   		
	 		   		$("#selectedStatus").html(option)
	 		   		$("#successDiv").show()
	 			} 	
 			}).fail(function(){
 				if($("#ErrorMsg").val()!=''){
 					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
 				}else{
 					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
 				}
 				
 			});	 
	}
</script>
</head>
<body>
		 
<p id="error_p" class="toolTip tpRed clearfix" style="display: none; max-width: 355px !important; max-height: 30px !important;">
<img src="./resources/images/template/icons/light-bulb-off.png">
&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">

	<h4 style="color: #FF0000;">${error}</h4>
</c:if>


<div id="hiDiv">
	<div class="toolTip tpGreen clearfix" style=" max-width: 355px !important; max-height: 30px !important;">
	<p style="font-size: 14px;">
		<img src="./resources/images/template/icons/light-bulb-off.png">
		<spring:message code="update_success" text="Data saved successfully."/>
	</p>
	</div>
</div>
		
<div class="fields clearfix ">
<%@ include file="/common/info.jsp"%>	


		<%-- <h2>
			<spring:message code="generic.new.heading" text="Enter Details" />
			[
			<spring:message code="generic.id" text="Id"></spring:message>
			:&nbsp;
			<spring:message code="generic.new" text="New"></spring:message>
			]
		</h2> --%>
		
	
		<label style="width:150px;height: 25px;"  class="small"> Work flow Type : </label>
	
		<select id="selectedWorkflow">
		  <%-- <c:forEach items="${WORKFLOW_STATUS_TYPE}" var="i" varStatus="loop">
		    <option value="${i}">
		        ${i}
		    </option>
		  </c:forEach> --%>
			  <c:forEach items="${WORKFLOW_STATUS_TYPE}" var="entry">
			   <option value="${entry.key}">
			        ${entry.value}
			    </option>
			</c:forEach>
		</select>
		 
		<br>
		<label style="width:150px;height: 25px;"  class="small"> DeviceTypes : </label>
	
		<select id="selecteDeviceType">
		  <c:forEach items="${dt}" var="i" varStatus="loop">
		    <option value="${i.type}">
		        ${i.name}
		    </option>
		  </c:forEach>
		</select>
		
		<p>
				<label class="small"><spring:message code="generic.serialnumber"
						text="Code" />&nbsp;*</label>
				 <input type = "text" id="number"  name = "number" />
		</p>
		
		<p>
				<label class="small">Support Issue Number&nbsp;*</label>
				 <input type = "text" id="supportIssueNumber"  name = "number" />
		</p>
		
		<div id ="wokflowDiv">
			<label style="width:150px;height: 25px;"  class="small"><spring:message code="generic.status"
						text="Code" />&nbsp;* : </label>
	
			<select id="selectedStatus">
			  
			</select>
			<br>
			<label style="width:150px;height: 25px;"  class="small"> userGroup Types : </label>
		
			<select id="selectedUserGroupType">
			  <c:forEach items="${POSTER_USER_GROUP_TYPES}" var="i" varStatus="loop">
			    <option value="${i.type}">
			        ${i.displayName}
			    </option>
			  </c:forEach>
			</select>
			<br>
			<label style="width:150px;height: 25px;"  class="small"> Level : </label>
			<select id="level">
			  <c:forEach var = "i" begin = "1" end = "15">
			  <option value="${i}">
			        ${i}
			    </option>
			  </c:forEach>
			</select>
			
			<p id ="assigneeDiv">
				<label class="small"> assignee &nbsp;*</label>
				 <input type = "text" id="assignee"  name = "number" />
			</p>
			
		</div>
		
		<p style="margin-left: 10px; margin-top: 10px;">
			<input id="posterSubmit" type="button" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
		
		<div id="bulkResultDiv">
		
		</div>

	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
	<input type="hidden" id="submissionMsg" value=" Do you want to submit the poster ? "/>
	<input type="hidden" id="missingDetailMsg" value=" Please Fill All Details  "/>

</body>
</html>