<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="role" text="User Role"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	$('document').ready(function(){	
		initControls();
		var recordId = ${domain.id};
		$('#key').val(recordId);
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
			});
		}else{
		$("#thirdLevels").empty();
		$("#thirdLevels").hide();	
		}			
	});
	if($("#firstLevels option").length>0){
		var selectedItems=$("#firstLevelsSelected").val().split(",");
		for(var i=0;i<selectedItems.length;i++){
			$("#firstLevels option[value='"+selectedItems[i]+"']").attr("selected","selected");
		}
	}
	if($("#secondLevels option").length>0){
		var selectedItems=$("#secondLevelsSelected").val().split(",");
		for(var i=0;i<selectedItems.length;i++){
			$("#secondLevels option[value='"+selectedItems[i]+"']").attr("selected","selected");
		}
		$("#secondLevels").show();		
	}else{
		$("#secondLevels").hide();
	}
	if($("#thirdLevels option").length>0){
		var selectedItems=$("#thirdLevelsSelected").val().split(",");
		for(var i=0;i<selectedItems.length;i++){
			$("#thirdLevels option[value='"+selectedItems[i]+"']").attr("selected","selected");
		}
		$("#thirdLevels").show();		
	}else{
		$("#thirdLevels").hide();
	}
});		
</script>
</head>
<body>

<div class="fields clearfix">
<form:form action="role" method="PUT"  modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="generic.edit.heading" text="Enter Details "/>
		 [<spring:message code="generic.id" text="Id"></spring:message>:${domain.id}]
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
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
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
		</p>
	</div>
	<input type="hidden" id="key" name="key">
	<form:hidden path="locale" />
	<form:hidden path="id"/>	
	<form:hidden path="version"/>
</form:form>
<input id="firstLevelsSelected" value="${firstLevelsSelected}" type="hidden">
<input id="secondLevelsSelected" value="${secondLevelsSelected}" type="hidden">
<input id="thirdLevelsSelected" value="${thirdLevelsSelected}" type="hidden">

</div>
</body>
</html>