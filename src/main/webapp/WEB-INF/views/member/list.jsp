<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="member.list" text="List Of Members"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">	
		$(document).ready(function(){
			var houseType=$("#houseType").val();
			if(houseType=="upperhouse"){
				$("#assemblies").hide();
				$("#selectedDate").show();
				$("#house").val($("#assemblies").val());
				var dbFormatDate=convertToDbFormat($("#selectedDate").val());
				$("#gridURLParams").val("house="+$("#assemblies").val()+"&selectedDate="+dbFormatDate);
			}else if(houseType=="lowerhouse"){
				$("#assemblies").show();
				$("#selectedDate").hide();	
				$("#house").val($("#assemblies").val());	
				$("#gridURLParams").val("house="+$("#assemblies").val());										
			}		
			$('#selectedDate').focus(function(){		
					$("#selectedDate").mask("99/99/9999");				
			});	
			$('#selectedDate').change(function(){										
					var dbFormatDate=convertToDbFormat($("#selectedDate").val());
					if(dbFormatDate!="Invalid Date"){
					var oldURL=$("#grid").getGridParam("url");
					var newURL=oldURL.split("&selectedDate=")[0]+"&selectedDate="+dbFormatDate;
					$("#gridURLParams").val("house="+$("#assemblies").val()+"&selectedDate="+dbFormatDate);					
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");
					}else{
						$("#selectedDate").val("");
						$("#selectedDate").mask("99/99/9999");
					}				
			});					
			$('#new_record').click(function(){
				newRecord();
			});
			$('#edit_record').click(function(){
				editRecord($('#key').val());
			});
			$("#delete_record").click(function() {
				deleteRecord($('#key').val());
			});
			$("#search").click(function() {
				searchRecord();
			});
			$("#view").click(function(){
				viewRecord();
			});
			$("#print").click(function(){
				printRecord();
			});
			$("#printCredential").click(function(){
				printCredential();
			});
			$("#assemblies").change(function(){
				$("#house").val($("#assemblies").val());
				var oldURL=$("#grid").getGridParam("url");
				var newURL=oldURL.split("?house=")[0]+"?house="+$("#house").val();
				$("#gridURLParams").val("house="+$("#house").val());					
				$("#grid").setGridParam({"url":newURL});				
				$("#grid").trigger("reloadGrid");								
			});
			$('#dateblock').toggle();
		});
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			var rowid=$('#key').val();
			$("#cancelFn").val("rowDblClickHandler");
			showTabByIdAndUrl('personal_tab', 'member/personal/'+rowid+'/edit?house='+$('#house').val()+'&houseType='+$("#houseType").val());
		}
		function rowSelectHandler(rowid,status){			
			if($('#key')){
				$('#key').val(rowid);
			}
		}
		function viewRecord(){
			var row=$("#key").val();
			$.get('member/view/'+row,function(data){
			$.fancybox(data);						
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		function printRecord(){
			var row=$("#key").val();
			$.get('member/print',function(data){
			$.fancybox(data);						
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		}
		
		function printCredential(){
			var row=$("#key").val();
		
			
			var url = "member/printCredentials?house="+$('#house').val()+'&houseType='+$("#houseType").val()+ '&member=' + row;
	
		
	showTabByIdAndUrl('details_tab', url);
		}
		
		function convertToDbFormat(date){
			var splitResult=date.split("/");
			if(splitResult.length==3){
				return splitResult[2]+"-"+splitResult[1]+"-"+splitResult[0];
			}else{
				return "Invalid Date";
			}
		}
		
		$('#memberData').click(function(){
			$('#dateblock').toggle();
			$('#txtFromDate,#txtEndDate').focus(function(){		
				if($(this).val()==""){
					$("#txtFromDate,#txtEndDate").mask("99-99-9999");
				}
			});
		});
		
		
		$('#lnkGenerateMemberData').click(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 	
			$.ajax({
				type:'POST',
				contentType : "application/json",
				url:'member/exportMemberList',
				//dataType: "json",
				data:JSON.stringify({
					'houseTypeId':$("#houseType").val(),
					'fromDate':$("#txtFromDate").val(),
					'toDate':$('#txtEndDate').val()
				}),
				success: function (result,status,xhr) {
			       try{
					var disposition = xhr.getResponseHeader('content-disposition');
			        var startIndex = disposition.indexOf("filename=")+9;
			        var endIndex = disposition.length ;
			        var filename = disposition.substring(startIndex, endIndex);
			              
			        
		            var a = document.createElement('a');
		            var blob = new Blob([result], { type: 'application/msword' });
		            var url = window.URL.createObjectURL(blob);
		            a.href = url;
		            a.download = filename;
		            document.body.append(a);
		            a.click();
		            a.remove();
		            window.URL.revokeObjectURL(url);
			       }catch(e){$.unblockUI();}
		        },
		        error:function(xhr,status,error){
		        	//console.log('Error',xhr,status,error);
		        	$.unblockUI();
		        },
		        complete:function(xhr,status){
		        	//console.log('Complelete');
		        	$.unblockUI();
		        }
			});
			
		});
		
	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>
	<div>
	<div class="commandbar">
		<div class="commandbarContent">	
			<a href="#" id="select_assembly" class="butSim">
				<c:choose>
				<c:when test="${houseType=='lowerhouse' }">
				<spring:message code="assembly.select" text="Select Assembly"/>
				</c:when>
				<c:otherwise>
				<spring:message code="council.select" text="Select Date"/>
				</c:otherwise>
				</c:choose>
			</a>
			<select name="assemblies" id="assemblies" style="width:100px;height: 25px;">			
			<c:forEach items="${assemblies}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:forEach>
			</select>			
			<input id="selectedDate" name="selectedDate" class=" sText datemask" type="text" value="${selectedDate}" style="width:75px;"> |
			<a href="#" id="new_record" class="butSim">
				<spring:message code="member.new" text="New"/>
			</a> |
			<a href="#" id="edit_record" class="butSim">
			<spring:message code="member.edit" text="Edit"/>
			</a> |
			<a href="#" id="delete_record" class="butSim">
				<spring:message code="member.delete" text="Delete"/>
			</a> |
			<a href="#" id="search" class="butSim">
				<spring:message code="member.search" text="Search"/>
			</a> 
			<hr/>
			<a href="#" id="view" class="butSim" style="display:none;">
				<spring:message code="member.view" text="View"/>
			</a> 
			<%--  <a href="#" id="print" class="butSim" style="">
				<spring:message code="member.print" text="Print"/>
			</a>   --%>
		<%-- 	<a href="#" id="previewProfile" class="butSim" >
				<spring:message code="member.previewProfile" text="Profile Preview"/>
			</a> | --%>
				<security:authorize access="hasAnyRole('SUPER_ADMIN')">
			<a href="#" id="printCredential" class="butSim">
				<spring:message code="member.print" text="printCredential"/>
			</a> 
			</security:authorize>|
			<security:authorize access="hasAnyRole('SUPER_ADMIN')">
				<a href="#" id="memberData" class="butSim">
					<spring:message code="member.list.export" text="printCredential"/>				
				</a>
				<span id="dateblock">
					<br/>
					<input type="text" id="txtFromDate" class="datemask"/>
					<input type="text" id="txtEndDate" class="datemask"/>
					<a href="javascript:void(0)" id="lnkGenerateMemberData">Generate</a>
				</span> 
			</security:authorize>	
						
			<p>&nbsp;</p>
		</div>
	</div>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
	<input type="hidden" id="gridURLParams" name="gridURLParams">	
	</div>
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>