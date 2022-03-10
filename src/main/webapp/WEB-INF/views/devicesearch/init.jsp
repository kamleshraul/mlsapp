<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var primaryMemberControlName=$(".autosuggest").attr("id");
	var start=0;
	var record=10;
	var previousSearchTerm="";
	var previousSearchCount=record;
	
		$(document).ready(function() {
 			var strDeviceType = $("#strDeviceType").val();
 			console.log(strDeviceType);
 			//If questions then only display group and answering date filter
			if(strDeviceType.startsWith("questions_")){
				$("#questionCriteria").css("display","inline");
			}else{
				$("#questionCriteria").css("display","none");
			}
	 
				
			/**** Remove hr from embeddd table ****/
			$("#searchTable td > table hr:last").remove();
			/**** Reset Filters ****/
			$("#reset").click(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				$("#requiredDeviceType").val("-");
				$("#requiredDeviceType").css("color","");
				$("#requiredSessionYear").val("-");
				$("#requiredSessionYear").css("color","");				
				$("#requiredSessionType").val("-");
				$("#requiredSessionType").css("color","");								
				$("#requiredGroup").empty();
				$("#requiredGroup").html(text);	
				$("#requiredGroup").css("color","");								
				$("#requiredAnsweringDate").empty();
				$("#requiredAnsweringDate").html(text);
				$("#requiredAnsweringDate").css("color","");				
				$("#requiredMinistry").empty();
				$("#requiredMinistry").html(text);
				$("#requiredMinistry").css("color","");				
				$("#requiredSubdepartment").empty();
				$("#requiredSubdepartment").html(text);
				$("#requiredSubdepartment").css("color","");				
				$("#requiredStatus").val("-");	
				$("#requiredStatus").css("color","");
				if($('#strDeviceType').val()=='bills_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
				}						
			});
			/**** Filters ****/
			$(".unstarred").hide();
			/**** Session Year ****/
			$("#requiredSessionYear").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#requiredSessionType").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&type!='-'&&houseType!='-'){
					if($("#strDeviceType").val().indexOf('questions_')==0){
						loadGrp(houseType,value,type);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#requiredGroup").empty();
					$("#requiredGroup").html(text);					
					$("#requiredAnsweringDate").empty();
					$("#requiredAnsweringDate").html(text);
					$("#requiredMinistry").empty();
					$("#requiredMinistry").html(text);
					$("#requiredSubdepartment").empty();
					$("#requiredSubdepartment").html(text);					
				}
			});
			/**** Session Type ****/
			$("#requiredSessionType").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#requiredSessionYear").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&year!='-'&&houseType!='-'){
					if($("#strDeviceType").val().startsWith('questions_')){
						loadGrp(houseType,year,value);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#requiredGroup").empty();
					$("#requiredGroup").html(text);					
					$("#requiredAnsweringDate").empty();
					$("#requiredAnsweringDate").html(text);
					$("#requiredMinistry").empty();
					$("#requiredMinistry").html(text);
					$("#requiredSubdepartment").empty();
					$("#requiredSubdepartment").html(text);
				}
			});
			/**** Device Type ****/
			$("#requiredDeviceType").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value=='-'){
					$(".unstarred").hide();
				}else{
					var type=$("#deviceTypeMaster option[value='"+value+"']").text();
					$("#strDeviceType").val(type);
					if(type=='questions_starred'){
						$(".unstarred").hide();
					}else{
						$(".unstarred").show();
					}
					
					if(type.startsWith("questions_")){
						$("#questionCriteria").css("display","inline");
					}else{
						$("#questionCriteria").hide("display","none");
					}
				}
			});
			/**** Group ****/
			$("#requiredGroup").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#requiredAnsweringDate").empty();
					$("#requiredAnsweringDate").html(text);
					$("#requiredMinistry").empty();
					$("#requiredMinistry").html(text);
					$("#requiredSubdepartment").empty();
					$("#requiredSubdepartment").html(text);
				}else{
					loadAD(value);
				}
			});
			/**** Ministry ****/
			$("#requiredMinistry").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");	
					if($('#strDeviceType').val()=='questions_'){
						loadAllMinistries();
					}
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#requiredSubdepartment").empty();
					$("#requiredSubdepartment").html(text);
				}else{
					//loadDep(value);
					loadSubdepartments(value);
				}
			});
			
				
			/**** Ansering Date ****/
			$("#requiredAnsweringDate").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Sub Department ****/
			$("#requiredSubdepartment").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Status ****/
			$("#requiredStatus").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			
			/**** primary Question's Details ****/
			$("#primary").click(function(){
				viewDetail($("#deviceId").val());
			});
			
						
			/**** Search Content Changes ****/
			$("#searchvalue").change(function(){
				start=0;				
				$("#searchTable tbody").empty();
				$("#clubbingDiv").hide();
				previousSearchCount=record;										
			});	
			
			/**** On clicking search button ****/
			$("#search").click(function(){
				search("YES");
			});
			
			/**** On Page Load ****/
			$("#clubbingDiv").hide();
					
			
			$("#formattedPrimaryMember").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers/fromsession?houseType1='+$("#houseTypeCommon").val()+'&houseType2='+$("#houseTypeCommon").val()+
						'&sessionYear1='+$("#requiredSessionYear").val()+'&sessionYear2='+$("#selectedSessionYear").val()+
						'&sessionType1='+$("#requiredSessionType").val()+'&sessionType2='+$("#selectedSessionType").val(),
				select:function(event,ui){			
				$("#primaryMember").val(ui.item.id);
				}			
			});		
			
			$("#searchBy").change(function(){
				var value = $(this).val();
				if(value != '-'){
					if(value == 'searchByNumber'){						
						$("#searchByMemberDiv").css({'display': 'none'});
					}else if(value == 'searchByMember'){
						$("#formattedPrimaryMember").val('');
						$("#primaryMember").val('');
						$("#searchByMemberDiv").css({'display': 'inline-block'});					
					}
				}else{
					$("#searchByMemberDiv").css({'display': 'none'});
				}
			});
			
			if($('#strDeviceType').val()=='questions_'){
				loadAllMinistries();
			}			
		});
		
		function loadAllMinistries(){
			$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val(),function(data){
				if(data){
					if($('#strDeviceType').val()=='questions_') {						
						var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
										
						$("#requiredSubdepartment").empty();
						$("#requiredSubdepartment").html(text);
						
						$.get('ref/allministries?session='+data.id,function(data1){
								if(data1.length>0){
									for(var i=0;i<data1.length;i++){
										text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
									}
									$("#requiredMinistry").empty();
									$("#requiredMinistry").html(text);						
								}else{
									$("#requiredMinistry").empty();
									$("#requiredMinistry").html(text);		
								}
							}).fail(function(){
								if($("#ErrorMsg").val()!=''){
									$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
								}else{
									$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
								}
								scrollTop();
							});	
					}
				}
			});
		}
		
		function loadSubdepartments(ministry){
							
				var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
				$("#requiredSubdepartment").empty();
				$("#requiredSubdepartment").html(text);
				
				$.get('ref/subdepartments/ministry?ministryId='+ministry,function(data1){
						if(data1.length>0){
							for(var i=0;i<data1.length;i++){
								text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
							}
							$("#requiredSubdepartment").empty();
							$("#requiredSubdepartment").html(text);						
						}else{
							$("#requiredSubdepartment").empty();
							$("#requiredSubdepartment").html(text);		
						}
					}).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});	
			}
		
		
		/**** Group ****/
		function loadGrp(houseType,sessionYear,sessionType){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			param="houseType="+houseType+"&year="+sessionYear+"&sessionType="+sessionType;
			$("#requiredAnsweringDate").empty();
			$("#requiredAnsweringDate").html(text);
			$("#requiredMinistry").empty();
			$("#requiredMinistry").html(text);
			$("#requiredSubdepartment").empty();
			$("#requiredSubdepartment").html(text);
			$.get('ref/groups?'+param,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#requiredGroup").empty();
				$("#requiredGroup").html(text);
			}else{
				$("#requiredGroup").empty();
				$("#requiredGroup").html(text);					
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
		}
		/**** Answering Date ****/
		function loadAD(group){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$.get('ref/group/'+group+'/answeringdates',function(data){
				if(data.length>0){
					for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
					}
					$("#requiredAnsweringDate").empty();
					$("#requiredAnsweringDate").html(text);
					loadMin(group);						
				}else{
					$("#requiredAnsweringDate").empty();
					$("#requiredAnsweringDate").html(text);
					$("#requiredMinistry").empty();
					$("#requiredMinistry").html(text);		
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}
		/**** Minister by group****/
		function loadMin(group){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#requiredSubdepartment").empty();
			$("#requiredSubdepartment").html(text);
			$.get('ref/group/'+group+'/ministries',function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#requiredMinistry").empty();
				$("#requiredMinistry").html(text);						
				}else{
				$("#requiredMinistry").empty();
				$("#requiredMinistry").html(text);		
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}
		
		/**** Minister ****/
		function loadMinWithoutGroup(){
			var year = $("#requiredSessionYear").val();
			var houseType = $("#houseTypeCommon").val();
			var sessionType = $("#requiredSessionType").val();
			
			var url = "ref/ministry/"+houseType+"/"+year+"/"+sessionType;
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#requiredSubdepartment").empty();
			$("#requiredSubdepartment").html(text);
			$.get(url,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#requiredMinistry").empty();
				$("#requiredMinistry").html(text);						
				}else{
				$("#requiredMinistry").empty();
				$("#requiredMinistry").html(text);		
				}
			}).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}
	
		
		
		/**** On clicking search button ****/		
		function search(fresh){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			/**** Constructing data to be sent in post request ****/
			if(fresh=='YES'){
				start=0;
				$("#searchTable tbody").empty();				
			}
			var resourceURL = "";
			var postData = "";
			$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val(),function(data){
				if(data){				
					
					
						postData={param:$("#searchvalue").val(),session:data.id,record:record,start:start};
						
						var deviceType = $("#requiredDeviceType").val();						
						if(deviceType!='' && deviceType!='-'){
							postData['deviceType']=deviceType;
						} 
						
						var searchByy = $("#searchBy").val();
						if(searchByy=='searchByNumber'){
							if($("#searchvalue").val()!=''){
								postData['number']=$("#searchvalue").val();	
							}
						}
						
						if(searchByy=='searchByMember'){
							if($("#primaryMember").val()!=''){
								postData['primaryMember']=$("#primaryMember").val();	
							}
						}
						
						if($("#houseTypeCommon").length>0){
							postData['houseType']=$("#houseTypeCommon").val();
						}
						if($("#requiredSessionYear").length>0){
							postData['sessionYear']=$("#requiredSessionYear").val();
						}
						if($("#requiredSessionType").length>0){
							postData['sessionType']=$("#requiredSessionType").val();
						}
						
						if($("#strDeviceType").val().startsWith("questions_")){
							if($("#requiredGroup").length>0){
								postData['group']=$("#requiredGroup").val();
							}
							if($("#requiredAnsweringDate").length>0){
									postData['answeringDate']=$("#requiredAnsweringDate").val();
							}
						}
						
						if($("#requiredMinistry").length>0){
							postData['ministry']=$("#requiredMinistry").val();
						}
						
						if($("#requiredSubdepartment").length>0){    
							postData['subDepartment']=$("#requiredSubdepartment").val();
						}
						
						if($("#requiredStatus").length>0){
							postData['status']=$("#requiredStatus").val();
						}
						resourceURL = "devicesearch/searchfacility";
					 
					var toBeSearched=$("#searchvalue").val();			
					//previousSearchTerm=toBeSearched;
					/**** Search Input Box is not empty ****/
					if(toBeSearched!=''){
						/**** Search takes place if its a new search or while loading more data in current search ****/
						//if((previousSearchCount==record)){
						$.post(resourceURL,postData,function(data){
							/**** previousSearchCount controls if clicking search button next time with same content
							 will call search function.It will only if this time no. of entries returned is
							 equal to max no of records in each search call=record****/
							previousSearchCount=data.length;
							if(data.length>0){
							var text="";	
								for(var i=0;i<data.length;i++){
									var textTemp="";
									var deviceNumber = data[i].number;
									if(deviceNumber==undefined || deviceNumber=='') {
										deviceNumber = $('#billWithoutNumber').val();
									}
									textTemp=textTemp+"<tr>"+
											"<td class='expand' style='width: 150px; max-width: 150px;'>"+
											"<span id='number"+data[i].id+"'>"+
												deviceNumber+"</span>"
											+"<br>";
									textTemp+="<span id='operation"+data[i].id+"'></span></td>";
								
									textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
																		
									textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
										+"<br/>";
									if(data[i].revisedContent!=null && data[i].revisedContent!=''){
										textTemp+=" ,"+data[i].revisedContent
										+"<br/>";
									} 
										
										
									textTemp += data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
									if($("#strDeviceType").val().startsWith("questions_")){
										+"<strong>"+data[i].formattedGroup+"</span>,"	
									}
									+ data[i].ministry;
									if(data[i].subDepartment==null||data[i].subdepartment==""){
										textTemp+=","+data[i].status+"<br>";
									   
								    }else{						     
								    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;
								    }
									if(data[i].chartAnsweringDate!=null && data[i].chartAnsweringDate!=''){
										textTemp+=" ,"+data[i].chartAnsweringDate;
									} 
									
									if(data[i].actor==null||data[i].actor==''){
										textTemp+="</td>";
									}else{
										textTemp+=" ,<br>"+data[i].actor+"</td>";
									} 
									 
									
									textTemp+="</tr>";								
									text+=textTemp;
								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><span class='clearLoadMore'><a onclick='loadMore();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></span></td>"
										+"</tr>";
									start=start+10;							
								}
														
								$("#searchTable > #searchresultbody:last").append(text);	
								$("#searchresult").show();							
								$("#clubbingDiv").show();
								$.unblockUI();													
							}else{
								$("#searchTable tbody").empty();							
								$("#clubbingDiv").show();
								$("#searchresult").hide();						
								$.unblockUI();										
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
					}else{
						$.prompt($("#nothingToSearchMsg").val());
						$("#searchTable tbody").empty();
						$("#searchresult").hide();				
						$("#clubbingDiv").hide();
						$.unblockUI();				
					}
				}
			});
		}
		/**** Load More ****/
		function loadMore(){			
			search("NO");
			$(".clearLoadMore").empty();
		}		
				
		
</script>

<style type="text/css">

.filterSelected{
color:blue;
}
.highlightedSearchPattern{
font-weight: bold;
text-decoration: underline;
}
td>table{
	width: 350px;
}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent" id="advancedSearch">

	<a href="#" class="butSim">
		<spring:message code="advancedsearch.searchType" text="Search By"/>
	</a>			
	<select name="searchBy" id="searchBy" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>	
		<c:forEach items="${searchBy}" var="sb">
			<c:choose>
				<c:when test="${sb.value=='searchByNumber'}">
					<option value="${sb.value}" selected="selected">${sb.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${sb.value}">${sb.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select> 	
	<div id="searchByMemberDiv" style="display: none;">
		<input type="text" class="sText autosuggest" style="width: 150px;" id="formattedPrimaryMember"/>
		<input type="hidden" style="width: 60px;" id="primaryMember"/>
	</div>|
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.deviceType" text="Device Type"/>
	</a>		
	<select name="requiredDeviceType" id="requiredDeviceType" style="width:100px;height: 25px;">			
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach items="${dTypes}" var="i">
			<c:choose>
				<c:when test="${i.id == deviceType}">
					<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
				</c:otherwise>
			</c:choose>
						
		</c:forEach>
	</select> 
	<select id="deviceTypeMaster" style="display:none;">
		<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id}"><c:out value="${i.type}"></c:out></option>			
		</c:forEach>
	</select> |
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.sessionyear" text="Year"/>
	</a>			
	<select name="requiredSessionYear" id="requiredSessionYear" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach var="i" items="${years}">
			<c:choose>
				<c:when test="${sessionYear==i.id}">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
				</c:when>
				<c:otherwise>
					<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
				</c:otherwise>
			</c:choose>
		</c:forEach> 
	</select> |			
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.sessionType" text="Session Type"/>
	</a>			
	<select name="requiredSessionType" id="requiredSessionType" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach items="${sessionTypes}" var="i">
			<c:choose>
				<c:when test="${sessionType==i.id}">
					<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>	
				</c:otherwise>
			</c:choose>
		</c:forEach> 
	</select> |		
	<hr>
	<div id="questionCriteria">
		<a href="#" class="butSim">
			<spring:message code="question.group" text="Group"/>
		</a>			
		<select name="requiredGroup" id="requiredGroup" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach items="${groups}" var="i">			
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
			</c:forEach> 
		</select> |			
		<a href="#" class="butSim">
			<spring:message code="question.answeringDate" text="Answering Date"/>
		</a>			
		<select name="requiredAnsweringDate" id="requiredAnsweringDate" style="width:100px;height: 25px;">				
			<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		</select> |		
	</div>		
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.ministry" text="Ministry"/>
	</a>			
	<select name="requiredMinistry" id="requiredMinistry" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach items="${ministries}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach> 
	</select> |			
	
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
	</a>	
	<select name="requiredSubdepartment" id="requiredSubdepartment" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
	</select> |
	
	<hr>
	<a href="#" class="butSim">
		<spring:message code="question.status" text="Status"/>
	</a>			
	<select name="requiredStatus" id="requiredStatus" class="sSelect">			
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
		<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
		<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
	</select> |				
</div>	


<hr>


<div id="searchBoxDiv">
	<table style="padding: 0px; margin: 0px;"> 
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:2px;">
				<input type="text" name="zoom_query" id="searchvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="search" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.reset" text="Reset Filters"></spring:message></a>
			</td>
		</tr>
	</table>
</div>


<div id="clubbingDiv">

	
	<div id="searchresult" style="display:none; width: 910px; border: 2px solid; margin: 5px;">
		<table  id="searchTable" style="width: 100%;" class="strippedTable">
		<thead>
		<tr>
		<th class="expand"><spring:message code="clubbing.number" text="Number"></spring:message></th>
		<c:choose>
		<c:when test="${fn:contains(deviceTypeType, 'questions_')}">
		<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'motions_')}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'bills_')}">
		<th class="expand"><spring:message code="clubbing.title" text="Title"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'resolutions_')}">
		<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'notices_specialmention')}">
		<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'proprietypoint')}">
		<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		</c:choose>
		<c:choose>
		<c:when test="${fn:contains(deviceTypeType, 'questions_')}">
		<th class="expand"><spring:message code="clubbing.question" text="Question"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'motions_')}">
		<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'resolutions_')}">
		<th class="expand"><spring:message code="clubbing.resolution" text="Resolution"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'bills_')}">
		<th class="expand"><spring:message code="clubbing.bill" text="Content Draft"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'notices_specialmention')}">
		<th class="expand"><spring:message code="clubbing.notices_specialmention" text="Special Mention Notice Content"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'proprietypoint')}">
		<th class="expand"><spring:message code="clubbing.proprietypoint" text="Points of Propriety"></spring:message></th>
		</c:when>
		<c:when test="${fn:contains(deviceTypeType, 'bills_')}">
			<th class="expand"><spring:message code="clubbing.billDetails" text="Bill Details"></spring:message></th>
		</c:when>
		</c:choose>
		</tr>
		</thead>
		<tbody id="searchresultbody">
		</tbody>
		</table>
	</div>

</div>

</div>
</div>

<input id="nothingToSearchMsg" value="<spring:message code='clubbing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='clubbing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='clubbing.viewdetail' text='Detail'></spring:message>" type="hidden">
<input id="clubMsg" value="<spring:message code='clubbing.club' text='Club'></spring:message>" type="hidden">
<input id="unclubMsg" value="<spring:message code='clubbing.unclub' text='Unclub'></spring:message>" type="hidden">
<input id="groupChangeMsg" value="<spring:message code='clubbing.groupchange' text='Change Group'></spring:message>" type="hidden">
<input id="ministryChangeMsg" value="<spring:message code='clubbing.ministrychange' text='Change Ministry'></spring:message>" type="hidden">
<input id="departmentChangeMsg" value="<spring:message code='clubbing.departmentchange' text='Change Department'></spring:message>" type="hidden">
<input id="subDepartmentChangeMsg" value="<spring:message code='clubbing.subDepartmentchange' text='Change Sub Department'></spring:message>" type="hidden">
<input id="referencingMsg" value="<spring:message code='clubbing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="houseTypeCommon" value="${houseType}" type="hidden">
<input id="houseTypeYearSessionTypeEmptyMsg" value="<spring:message code='client.error.advancedsearch.yeartypeempty' text='Session Year and Session Type must be selected to continue'/>" type="hidden">
<input id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id="strDeviceType" value="${deviceTypeType}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="subdepartmentValue" value="<spring:message code='question.department' text='subDepartment'/>" />
<input type="hidden" id="billWithoutNumber" value="<spring:message code='bill.referredBillWithoutNumber' text='Click To See'/>">
</body>
</html>