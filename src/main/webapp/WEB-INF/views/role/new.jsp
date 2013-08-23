<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<title><spring:message code="role" text="User Access Control roles" />
</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		$('#key').val('');
		$("#firstLevels").change(function(){
				var value=$(this).val();
				if(value!=''||value!=undefined){
					$.get('ref/menusbyparents?parents='+value,function(data){
						var secondoptions="";
						var thirdoptions="";
						var newparents="";
						if(data.length>0){
							for(var i=0;i<data.length;i++){
								secondoptions=secondoptions+"<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
								newparents=data[i].id+","+newparents;
							}
							var parents=newparents.slice(0,newparents.length-1);
							$("#secondLevels").empty();
							$("#secondLevels").html(secondoptions);
							$("#secondLevels").show();
							$.get('ref/menusbyparents?parents='+parents,function(data1){
								if(data1.length>0){
									for(var i=0;i<data1.length;i++){
										thirdoptions=thirdoptions+"<option value='"+data1[i].id+"' selected='selected'>"+data1[i].name+"</option>";
									}
									$("#thirdLevels").empty();
									$("#thirdLevels").html(thirdoptions);									
									$("#thirdLevels").show();
								}else{
									$("#thirdLevels").empty();
									$("#thirdLevels").hide();
								}	
							});
						}else{
							$("#secondLevels").empty();
							$("#thirdLevels").empty();
							$("#secondLevels").hide();
							$("#thirdLevels").hide();
						}
						
					}).fail(function(){
    					if($("#ErrorMsg").val()!=''){
    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
    					}else{
    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
    					}
    					scrollTop();
    				});
				}else{
				$("#secondLevels").empty();
				$("#thirdLevels").empty();
				$("#secondLevels").hide();
				$("#thirdLevels").hide();	
				}			
		});

		$("#secondLevels").change(function(){
			var value=$(this).val();
			if(value!=''||value!=undefined){
				$.get('ref/menusbyparents?parents='+value,function(data){
					var thirdoptions="";
					if(data.length>0){
						for(var i=0;i<data.length;i++){
							thirdoptions=thirdoptions+"<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
						}
						$("#thirdLevels").empty();
						$("#thirdLevels").html(thirdoptions);									
						$("#thirdLevels").show();						
					}else{
						$("#thirdLevels").empty();
						$("#thirdLevels").hide();
					}					
				}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			}else{
			$("#thirdLevels").empty();
			$("#thirdLevels").hide();	
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

	<div class="fields clearfix">
		<form:form action="role" method="POST"
			modelAttribute="domain">
			<%@ include file="/common/info.jsp"%>
			<h2>
				<spring:message code="generic.new.heading" text="Enter Details" />
				[
				<spring:message code="generic.id" text="Id"></spring:message>
				:&nbsp;
				<spring:message code="generic.new" text="New"></spring:message>
				]
			</h2>
			<form:errors path="version" cssClass="validationError" />
			<p>
				<label class="small"><spring:message
						code="role.localizedName" text="Role" />&nbsp;*</label>
				<form:input cssClass="sText " path="localizedName" />
				<form:errors path="localizedName" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="role.name" text="Role(in English)" />&nbsp;*</label>
				<form:input cssClass="sText " path="name" />
				<form:errors path="name" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="role.type" text="Role(in English)" />&nbsp;*</label>
				<form:input cssClass="sText " path="type" />
				<form:errors path="type" cssClass="validationError" />
			</p>
			<p>
				<label class="small"><spring:message
						code="role.selectmenus" text="Menus Allowed" />&nbsp;*</label>
				<select id="firstLevels" name="firstLevels" multiple="multiple" style="width: 188px;" size="10">
				<c:forEach items="${firstLevels}" var="i">
				<option value="${i.id }">${i.text}</option>
				</c:forEach>
				</select>
				<select id="secondLevels" name="secondLevels" multiple="multiple" style="display:none;width: 188px;" size="10">
				<c:forEach items="${secondLevels}" var="i">
				<option value="${i.id }">${i.text}</option>
				</c:forEach>
				</select>
				<select id="thirdLevels" name="thirdLevels" multiple="multiple" style="display:none;width: 188px;" size="10">
				<c:forEach items="${thirdLevels}" var="i">
				<option value="${i.id }">${i.text}</option>
				</c:forEach>
				</select>
				<form:errors path="type" cssClass="validationError" />
			</p>			
			<div class="fields">
				<h2></h2>
				<p class="tright">
					<input id="submit" type="submit"
						value="<spring:message code='generic.submit' text='Submit'/>"
						class="butDef"> 
				</p>
			</div>
			<form:hidden path="locale" />
			<form:hidden path="id" />
			<form:hidden path="version" />
			<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
		</form:form>
	</div>
</body>
</html>