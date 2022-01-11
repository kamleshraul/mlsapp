<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="group.list" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var urlPattern=$('#urlPattern').val();
		$(document).ready(function(){	
			$('#errorDiv').hide();			
			showTabByIdAndUrl('list_tab','group/list?houseType='+$('#selectedHouseType').val()+'&year='+$("#selectedYear").val()+'&sessionType='+$("#selectedSessionType").val());
			
			$('#list_tab').click(function(){	
				$("#selectionDiv1").show();				
				if($('#errorDiv').is(':visible')) {					
					return false;
				} else {
					showGroupList();
				}				
			});
			
			/**** house type changes then reload grid****/			
			$("#selectedHouseType").change(function(){
				var value=$(this).val();
				var selectedHouseType=value;
				var resourceURL = "ref/findLatestSessionYear/" + selectedHouseType;
				$.get(resourceURL, function(data){
					var dataLength = data.length;
					if(dataLength > 0) {
						var text = "";
						for(var i = 0; i < dataLength; i++) {
							if(i==0)
								{
								text += "<option value='" + data[i].value + "' selected=selected>" + data[i].name + "</option>";
								}
							else
								{
								text += "<option value='" + data[i].value + "'>" + data[i].name + "</option>";
								}
							
							
						}
						$('#selectedYear').empty();
						$('#selectedYear').html(text);
					}
					else {
						$('#selectedYear').empty();
					}
				});
			
				if(value!=""){					
					reloadGroupGrid();									
				}	
			});
			
			/**** session year changes then reload grid****/			
			$("#selectedYear").change(function(){
				var value=$(this).val();
				if(value!=""){		
					reloadGroupGrid();								
				}			
			});
			
			/**** session type changes then reload grid****/
			$("#selectedSessionType").change(function(){
				var value=$(this).val();
				if(value!=""){			
					reloadGroupGrid();							
				}			
			});		
			
			$('#details_tab').click(function(){
				if($('#errorDiv').is(':visible')) {
		    		return false;
		    	} else {		    				    		
					var row = $("#grid").jqGrid('getGridParam','selrow');
					if(row == null){
						if($('#key').val()!=""){							
							editRecord();
						}else{		
							$.prompt($('#selectRowFirstMessage').val());
							return false;
						};					
					}
					else{						
						editRecord();
					}
		    	}				
			});			
			
			$('#rotationOrder_tab').click(function(){
				if($('#errorDiv').is(':visible')) {
		    		return false;
		    	} else {		    		
					assignRotationOrder($('#key').val());
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
					editRecord();
				}
				if(e.which==8 && e.ctrlKey){
					deleteRecord();
				}
				
				if(e.keyCode == 38 || e.keyCode == 40){
					scrollRowsInGrid(e);
		        }
			});			
		});	
			
		/**** displaying grid ****/					
		function showGroupList() {
			showTabByIdAndUrl('list_tab','group/list?houseType='+$('#selectedHouseType').val()+'&year='+$("#selectedYear").val()+'&sessionType='+$("#selectedSessionType").val());
		}
	
		function newRecord(copy) {
			$("#selectionDiv1").hide();	
			if(copy==null||copy==undefined){				
				showTabByIdAndUrl('details_tab','group/new?'+$("#gridURLParams").val());
			}else{
				showTabByIdAndUrl('details_tab','group/new?copy='+$("#key").val() +"&"+ $("#gridURLParams").val());
			}
		}
	
		function editRecord() {
			var row = $("#grid").jqGrid('getGridParam','selrow');
			if(this.id =='edit_record' && row==null){				
				$.prompt($('#selectRowFirstMessage').val());
				return false;							
			} 
			else{
				$("#selectionDiv1").hide();	
				if(row!=$('#key').val()) {
					row = $('#key').val();
				}								
				showTabByIdAndUrl('details_tab','group/'+row+'/edit?'+$("#gridURLParams").val());
			}
		}
	
		function deleteRecord() {
			var row = $("#grid").jqGrid('getGridParam','selrow'); 
			if(row==null){
				$.prompt("Please select the desired row to delete");		
			}
			else{
				$.prompt('Are you sure you want to delete the record with Id: '+ row,{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
				        $.delete_('group/'+row+'/delete', null, function(data, textStatus, XMLHttpRequest) {
				            $('#grid').trigger("reloadGrid");
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
	
		function rowDblClickHandler(rowid, iRow, iCol, e) {
			$("#selectionDiv1").hide();	
			showTabByIdAndUrl('details_tab', 'group/'+rowid+'/edit');
		}
		
		function assignRotationOrder(row) {
			if(row == null || row == ''){
				$.prompt($('#selectRowFirstMessage').val());	
				return;
			}
			else{
				$("#selectionDiv1").hide();	
				showTabByIdAndUrl('rotationOrder_tab','group/rotationorder/'+row+'/edit');
			}
		}
		
		/**** reload grid ****/
		function reloadGroupGrid(){
			$("#gridURLParams").val("houseType="+$("#selectedHouseType").val() + "&year="+$("#selectedYear").val() + "&sessionType="+$("#selectedSessionType").val());
			//check whether session exists			
			$.get('ref/sessionforgroups?' + $("#gridURLParams").val(), function(data){				
				if(data == "success") {					
					$('#errorDiv').hide();
					$('#listDiv').show();
					var oldURL=$("#grid").getGridParam("url");
					var baseURL=oldURL.split("?")[0];
					newURL=baseURL+"?"+$("#gridURLParams").val();
					$("#grid").setGridParam({"url":newURL});
					$("#grid").trigger("reloadGrid");
				}	
				else{
					$('#errorDiv').show();
					$('#listDiv').hide();	
				
					if(data == "error_nosessionfound"){		
						
						$('#error_nosessionfound').show();
						$('#error_duplicatesessionfound').hide();
						
					}else if(data == "error_duplicatesessionfound") {
						
						$('#error_duplicatesessionfound').show();
						$('#error_nosessionfound').hide();						
					}										
				}				
			}).fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			
			var oldURL=$("#grid").getGridParam("url");
			var baseURL=oldURL.split("?")[0];
			newURL=baseURL+"?"+$("#gridURLParams").val();
			$("#grid").setGridParam({"url":newURL});
			$("#grid").trigger("reloadGrid");							
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
			<li class="tab1">
				<a id="list_tab" class="selected tab" href="#">
					<spring:message code="generic.list" text="List"></spring:message>
				</a>
			</li>
			<li class="tab2">
				<a id="details_tab" href="#" class="tab">
					<spring:message code="generic.details" text="Details"></spring:message>
				</a>
			</li>
			<li class="tab3">
				<a id="rotationOrder_tab" href="#" class="tab">
					<spring:message code="group.module.rotationOrder" text="Rotation Order"></spring:message>
				</a>
			</li>
		</ul>
		
		<div class="commandbarContent" style="margin-top: 10px;" id="selectionDiv1">		
			<a href="#" id="select_houseType" class="butSim">
				<spring:message code="question.houseType" text="House Type"/>
			</a>
			<select name="selectedHouseType" id="selectedHouseType" style="width:100px;height: 25px;">	
				<c:if test="${empty selectedHouseType}">
					<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>
				</c:if>		
				<c:forEach items="${houseTypes}" var="i">					
					<c:choose>
						<c:when test="${i.type==selectedHouseType}">
							<option value="${i.type}" selected="selected"><c:out value="${i.name}"></c:out></option>			
						</c:when>
						<c:otherwise>
							<option value="${i.type}"><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select> |					
			<a href="#" id="select_session_year" class="butSim">
				<spring:message code="question.sessionyear" text="Year"/>
			</a>
			<select name="selectedYear" id="selectedYear" style="width:100px;height: 25px;">	
				<c:if test="${empty selectedYear}">
					<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>
				</c:if>			
				<c:forEach var="i" items="${years}">					
					<c:choose>
						<c:when test="${i.value==selectedYear }">
							<option value="${i.value}" selected="selected"><c:out value="${i.name}"></c:out></option>				
						</c:when>
						<c:otherwise>
							<option value="${i.value}" ><c:out value="${i.name}"></c:out></option>			
						</c:otherwise>
					</c:choose>
				</c:forEach> 
			</select> |						
			<a href="#" id="select_sessionType" class="butSim">
				<spring:message code="question.sessionType" text="Session Type"/>
			</a>
			<select name="selectedSessionType" id="selectedSessionType" style="width:100px;height: 25px;">		
				<c:if test="${empty selectedSessionType}">
					<option value=""><spring:message code='client.prompt.selectForDropdown' text='----Please Select----'/></option>
				</c:if>			
				<c:forEach items="${sessionTypes}" var="i">
					<c:choose>
						<c:when test="${i.type==selectedSessionType}">
							<option value="${i.type}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>				
						</c:when>
						<c:otherwise>
							<option value="${i.type}"><c:out value="${i.sessionType}"></c:out></option>			
						</c:otherwise>
					</c:choose>			
				</c:forEach> 
			</select>
			<hr>
		</div>		
		
		<div id="errorDiv" class="toolTip tpRed clearfix">			
			<p>
				<img src="./resources/images/template/icons/light-bulb-off.png">			
				
				<label id="error_nosessionfound"><spring:message code="question.errorcode.nosessionentryfound" text="No session found."/></label>
				
				<label id="error_duplicatesessionfound"><spring:message code="question.errorcode.nosessionentryfound" text="Duplicate sessions found."/></label>
			</p>
			<p></p>			
		</div>		
		
		<div id="listDiv" class="tabContent clearfix"></div>
		
		<input type="hidden" id="key" name="key">
		<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">
		<input type="hidden" id="selectRowFirstMessage" name="selectRowFirstMessage" value="<spring:message code='group.selectRowFirstMessage' text='Please select the desired row first'></spring:message>" disabled="disabled">
		<input type="hidden" id="confirmDeleteMessage" name="confirmDeleteMessage" value="<spring:message code='generic.confirmDeleteMessage' text='Do you want to delete the row with Id: '></spring:message>" disabled="disabled">
	</div>		
	<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>