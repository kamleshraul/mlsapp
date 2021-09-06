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
			if($('#whichDevice').val()=='motions_cutmotion_') {
				$('.starredSpecificFilters').hide();
			}
			/**** Remove hr from embeddd table ****/
			$("#searchTable td > table hr:last").remove();
			/**** Reset Filters ****/
			$("#reset").click(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				$("#deviceTypeStarred").val("-");
				$("#deviceTypeStarred").css("color","");
				$("#sessionYearStarred").val("-");
				$("#sessionYearStarred").css("color","");				
				$("#sessionTypeStarred").val("-");
				$("#sessionTypeStarred").css("color","");								
				$("#groupStarred").empty();
				$("#groupStarred").html(text);	
				$("#groupStarred").css("color","");								
				$("#answeringDateStarred").empty();
				$("#answeringDateStarred").html(text);
				$("#answeringDateStarred").css("color","");				
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);
				$("#ministryStarred").css("color","");				
				$("#departmentStarred").empty();
				$("#departmentStarred").html(text);
				$("#departmentStarred").css("color","");				
				$("#subDepartmentStarred").empty();
				$("#subDepartmentStarred").html(text);
				$("#subDepartmentStarred").css("color","");				
				$("#statusStarred").val("-");	
				$("#statusStarred").css("color","");
				if($('#whichDevice').val()=='bills_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
				}		
				if($("#whichDevice").val().indexOf('motions_cutmotion_')==0){
					loadAllMinistries();
				}
			});
			/**** Filters ****/
			$(".unstarred").hide();
			/**** Session Year ****/
			$("#sessionYearStarred").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#sessionTypeStarred").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&type!='-'&&houseType!='-'){
					if($("#whichDevice").val().indexOf('questions_')==0){
						loadGrp(houseType,value,type);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupStarred").empty();
					$("#groupStarred").html(text);					
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);					
				}
			});
			/**** Session Type ****/
			$("#sessionTypeStarred").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#sessionYearStarred").val();
				var houseType=$("#houseTypeCommon").val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				if(value!='-'&&year!='-'&&houseType!='-'){
					if($("#whichDevice").val().indexOf('questions_')==0){
						loadGrp(houseType,year,value);
					}else{
						loadMinWithoutGroup();
					}
				}else{
					//$.prompt($("#houseTypeYearSessionTypeEmptyMsg").val());
					$("#groupStarred").empty();
					$("#groupStarred").html(text);					
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}
			});
			/**** Device Type ****/
			$("#deviceTypeStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");
				}else{
					$(this).css("color","");
				}
				if(value=='-'){
					$(".unstarred").hide();
				}else{
					var type=$("#deviceTypeStarredMaster option[value='"+value+"']").text();
					if(type=='questions_starred'){
						$(".unstarred").hide();
					}else{
						$(".unstarred").show();
					}
				}
			});
			/**** Group ****/
			$("#groupStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadAD(value);
				}
			});
			/**** Ministry ****/
			$("#ministryStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");	
					if($('#whichDevice').val()=='questions_'
							|| $('#whichDevice').val()=='motions_cutmotion_') {
						loadAllMinistries();
					}
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					//loadDep(value);
					loadSubdepartments(value);
				}
			});
			/**** Department ****/	
			$("#departmentStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadSubDep($("#ministryStarred").val(),value);
				}
			});		
			/**** Ansering Date ****/
			$("#answeringDateStarred").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Sub Department ****/
			$("#subDepartmentStarred").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Status ****/
			$("#statusStarred").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Language ****/
			$("#languageAllowed").change(function(){
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
			/**** Back To Search Page****/
			$("#backToSearch").click(function(){
					back();
			});			
			/**** Search Content Changes ****/
			$("#searchvalue").change(function(){
				start=0;				
				$("#clubbingResult").empty();	
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
			/**** Back To Device ****/
			/**** Back To Device ****/
			$("#backToDevice").click(function(){
				/* $("#reset").click();
				$('#searchvalue').val(""); */
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToDeviceDiv").hide();
				if($("#assistantDiv").length>0){
				$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}else{
					if($('#whichDevice').val()=='questions_') {
						showQuestionList();
					} else if($('#whichDevice').val()=='motions_cutmotion_') {
						showCutMotionList();
					}
				}
				/**** Hide update success/failure message on coming back to device ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			$("#formattedPrimaryMember").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers/fromsession?houseType1='+$("#houseTypeCommon").val()+'&houseType2='+$("#houseTypeCommon").val()+
						'&sessionYear1='+$("#sessionYearStarred").val()+'&sessionYear2='+$("#selectedSessionYear").val()+
						'&sessionType1='+$("#sessionTypeStarred").val()+'&sessionType2='+$("#selectedSessionType").val(),
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
			
			if($('#whichDevice').val()=='questions_'
					|| $('#whichDevice').val()=='motions_cutmotion_') {
				loadAllMinistries();
			}			
		});
		
		function loadAllMinistries(){
			$.get('ref/sessionbyhousetype/'+$("#selectedHouseType").val()+"/"+$("#selectedSessionYear").val()+"/"+$("#selectedSessionType").val(),function(data){
				if(data){
					if($('#whichDevice').val()=='questions_'
							|| $('#whichDevice').val()=='motions_cutmotion_') {
						var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
						
						$("#departmentStarred").empty();
						$("#departmentStarred").html(text);
						$("#subDepartmentStarred").empty();
						$("#subDepartmentStarred").html(text);
						
						$.get('ref/allministries?session='+data.id,function(data1){
								if(data1.length>0){
									for(var i=0;i<data1.length;i++){
										text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
									}
									$("#ministryStarred").empty();
									$("#ministryStarred").html(text);						
								}else{
									$("#ministryStarred").empty();
									$("#ministryStarred").html(text);		
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
			if($('#whichDevice').val()=='questions_'
					|| $('#whichDevice').val()=='motions_cutmotion_') {
				var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
				$("#subDepartmentStarred").empty();
				$("#subDepartmentStarred").html(text);
				
				$.get('ref/subdepartments/ministry?ministryId='+ministry,function(data1){
						if(data1.length>0){
							for(var i=0;i<data1.length;i++){
								text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
							}
							$("#subDepartmentStarred").empty();
							$("#subDepartmentStarred").html(text);						
						}else{
							$("#subDepartmentStarred").empty();
							$("#subDepartmentStarred").html(text);		
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
		
		/**** Group ****/
		function loadGrp(houseType,sessionYear,sessionType){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			param="houseType="+houseType+"&year="+sessionYear+"&sessionType="+sessionType;
			$("#answeringDateStarred").empty();
			$("#answeringDateStarred").html(text);
			$("#ministryStarred").empty();
			$("#ministryStarred").html(text);
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			$.get('ref/groups?'+param,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#groupStarred").empty();
				$("#groupStarred").html(text);
			}else{
				$("#groupStarred").empty();
				$("#groupStarred").html(text);					
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
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					loadMin(group);						
				}else{
					$("#answeringDateStarred").empty();
					$("#answeringDateStarred").html(text);
					$("#ministryStarred").empty();
					$("#ministryStarred").html(text);		
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
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			$.get('ref/group/'+group+'/ministries',function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);						
				}else{
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);		
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
			var year = $("#sessionYearStarred").val();
			var houseType = $("#houseTypeCommon").val();
			var sessionType = $("#sessionTypeStarred").val();
			
			var url = "ref/ministry/"+houseType+"/"+year+"/"+sessionType;
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#departmentStarred").empty();
			$("#departmentStarred").html(text);
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			$.get(url,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);						
				}else{
				$("#ministryStarred").empty();
				$("#ministryStarred").html(text);		
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
		/**** Department ****/
		function loadDep(ministry){
		var param = "houseType=" + $('#selectedHouseType').val() +
		"&sessionType=" + $('#selectedSessionType').val() +
		"&sessionYear=" +$('#selectedSessionYear').val();
		$.get('ref/departments/'+ministry+'?'+param,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
			if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
				$("#departmentStarred").empty();
				$("#departmentStarred").html(text);					
			}else{
				$("#departmentStarred").empty();
				$("#departmentStarred").html(text);
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
		/**** Sub Department ****/
		function loadSubDep(ministry,department){
		var param = "houseType=" + $('#selectedHouseType').val() +
		"&sessionType=" + $('#selectedSessionType').val() +
		"&sessionYear=" +$('#selectedSessionYear').val();
		$.get('ref/subdepartments/'+ministry+'/'+department +'?'+ param,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);					
			}else{
			$("#subDepartmentStarred").empty();
			$("#subDepartmentStarred").html(text);
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
					
					if($('#whichDevice').val()=='questions_'
							|| $('#whichDevice').val()=='motions_cutmotion_') {
						postData={param:$("#searchvalue").val(),session:data.id,record:record,start:start};
						
						var whichDevice = $('#whichDevice').val();
						postData['whichDevice']=whichDevice;
						
						var deviceType = $("#deviceTypeStarred").val();						
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
						if($("#sessionYearStarred").length>0){
							postData['sessionYear']=$("#sessionYearStarred").val();
						}
						if($("#sessionTypeStarred").length>0){
							postData['sessionType']=$("#sessionTypeStarred").val();
						}
						if($("#groupStarred").length>0){
								postData['group']=$("#groupStarred").val();
						}
						if($("#answeringDateStarred").length>0){
								postData['answeringDate']=$("#answeringDateStarred").val();
						}
						if($("#ministryStarred").length>0){
							postData['ministry']=$("#ministryStarred").val();
						}
						if($("#departmentStarred").length>0){
							postData['department']=$("#departmentStarred").val();
						}
						if($("#subDepartmentStarred").length>0){    
							postData['subDepartment']=$("#subDepartmentStarred").val();
						}
						if($("#statusStarred").length>0){
							postData['status']=$("#statusStarred").val();
						}
						resourceURL = "clubentity/searchfacility";
					} 
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
											"<td class='expand' style='width: 120px; max-width: 120px;'>"+
											"<span id='number"+data[i].id+"'>"+
											"<a onclick='viewDetail("+data[i].id+");' style='margin:10px; text-decoration: underline;'>"+									
											deviceNumber+"</a></span>"+
											data[i].onlineStatus
											+"<br>";
									textTemp+="<span id='operation"+data[i].id+"'></span></td>";
								
									if($('#whichDevice').val()=='questions_'
											|| $('#whichDevice').val()=='motions_cutmotion_') {
										if(data[i].revisions != null && data[i].revisions.length>0){
											$("#qRevision").show();
											textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'><table border='1'>";
											var revisions = data[i].revisions;
											for(var j=1;j<revisions.length;j++){
											textTemp+=	"<tr>"+
												" <td>"+data[i].revisions[j].name+"("+revisions[j].value+")" +
												 "<br><b>"+data[i].revisions[j].displayName+"</b>" ;
												 if(data[i].revisions[j].type != null){
													 textTemp+= "<br>"+data[i].revisions[j].type;
												 }
												 textTemp+=	"</td></tr>";
											}
											textTemp+="</table></td>";
										}else{
											$("#qRevision").css("display","none");
										}
										
										textTemp+="<td class='expand' style='width: 200px; max-width: 200px;'>"+data[i].subject+"</td>";
									} 
									
									if($('#whichDevice').val()=='questions_'
											|| $('#whichDevice').val()=='motions_cutmotion_') {
										textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].deviceContent
										+"<br/>"
										+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>";
										if($('#whichDevice').val()=='questions_') {
											textTemp+="<strong>"+data[i].formattedGroup+"</span>,"+data[i].ministry;
										} else {
											textTemp+=data[i].ministry;
										}										
										if(data[i].subDepartment==null||data[i].subdepartment==""){
											textTemp+=","+data[i].status+"<br>";
										   
									    }else{						     
									    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;
									    }
										if(data[i].chartAnsweringDate!=null && data[i].chartAnsweringDate!=''){
											textTemp+=" <br>"+$("#chartDateTitle").val() +" : "+ data[i].chartAnsweringDate;
										} 
										
										if(data[i].discussionDate != null && data[i].discussionDate!='' && data[i].discussionDate!="null"){
											textTemp+=" <br>"+$("#ballotDateTitle").val() +" : "+ data[i].discussionDate;
										}
										
										if(data[i].formattedParentNumber != null && data[i].formattedParentNumber != ''){
											textTemp+=" <br><a style='text-decoration:underline;' onclick=viewDetail('"+data[i].sessionId+"')>" +  data[i].formattedParentNumber +"</a>"+ $("#parentTitle").val();
										}
										
										if(data[i].formattedClubbedNumbers != null && data[i].formattedClubbedNumbers != ''){
											
											textTemp+=" <br>"+ $("#clubbingTitle").val() + " : ";
											var clubbedNumbers = data[i].formattedClubbedNumbers.split(",") ;
											for(var j=0;j<clubbedNumbers.length;j++){
												var cnos = clubbedNumbers[j].split("#");
												textTemp += "<a style='text-decoration:underline;' onclick=viewDetail('"+cnos[1]+"')>" + cnos[0] +"</a>, ";
											}
										}
										
										if($('#whichDevice').val()=='motions_cutmotion_') {
											if(data[i].isReplySentByDepartment==true) {
												textTemp+=" ,<br><b><spring:message code='cutmotion.replyReceivedStatus.replyReceived' text='Reply Received'/></b>";
											} else {
												if(data[i].statusType=='cutmotion_final_admission' && data[i].isReplySentByDepartment==false) {
													//textTemp+=" ,<br>Answer Not Received";
													textTemp+=" ,<br><b><spring:message code='cutmotion.replyReceivedStatus.replyNotReceived' text='Reply Not Received'/></b>";
												}
											}										
										}
										
										if(data[i].actor==null||data[i].actor==''){
											textTemp+="</td>";
										}else{
											textTemp+=" ,<br>"+data[i].actor+"</td>";
										}									
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
								$("#clubbingResult").empty();						
								$("#searchTable > #searchresultbody:last").append(text);	
								$("#searchresult").show();							
								$("#clubbingDiv").show();
								$.unblockUI();													
							}else{
								$("#clubbingResult").empty();
								$("#clubbingResult").html($("#noResultsMsg").val());
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
						/*}else{
							$("#clubbingResult").empty();
							$("#clubbingResult").html($("#finishedSearchingMsg").val());
							$("#clubbingDiv").show();
							if($("#searchTable > #searchresultbody tr").length>0){
							$("#searchresult").show();							
							}else{
							$("#searchresult").show();							
							}
							$.unblockUI();									
						}*/
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
		/**** On Clubbing ****/
		function clubbing(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
			var deviceId=$("#deviceId").val();	
			var deviceNumber=$("#deviceNumber").val();
			if($('#whichDevice').val()=="bills_" && (deviceNumber==undefined || deviceNumber=='' || deviceNumber==$('#billWithoutNumber').val())) {
				deviceNumber = "This Bill";
			}
			var clubbedNumber=$("#number"+clubId).text();	
			if($('#whichDevice').val()=="bills_" && (clubbedNumber==undefined || clubbedNumber=='' || clubbedNumber==$('#billWithoutNumber').val())) {
				clubbedNumber = "Searched Bill";
			}
			var whichDevice = $('#whichDevice').val();
			$.post('clubentity/clubbing?pId='+deviceId+"&cId="+clubId+'&whichDevice='+whichDevice,function(data){					
					if(data=='SEARCHED_CLUBBED_TO_PROCESSED'){
					var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" Clubbed To "+deviceNumber;
					$("#clubbingResult").empty();
					$("#clubbingResult").html(text);
					$("#operation"+clubId).empty();
					$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else if(data=='PROCESSED_CLUBBED_TO_SEARCHED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+deviceNumber+" Clubbed To "+clubbedNumber;
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else if(data=='BEINGSEARCHED_QUESTION_ALREADY_CLUBBED' || data=='BEINGSEARCHED_BILL_ALREADY_CLUBBED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" is already  clubbed.";
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html(data);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='clubbing("+clubId+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>");
					}
					$.unblockUI();
					scrollTop();								
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}
		/**** On unclubbing ****/
		function unclubbing(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var deviceId=$("#deviceId").val();
			var whichDevice = $('#whichDevice').val();
			$.post('clubentity/unclubbing?pId='+deviceId+"&cId="+clubId+'&whichDevice='+whichDevice,function(data){
				if(data=='SUCCESS'){
					$("#clubbingResult").empty();
					$("#clubbingResult").html(data);
					$("#operation"+clubId).empty();
					$("#operation"+clubId).html("<a onclick='clubbing("+clubId+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>");
					}else{
						$("#clubbingResult").empty();
						$("#clubbingResult").html(data);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					}				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			$.unblockUI();	
			$('html').animate({scrollTop:0}, 'slow');
			$('body').animate({scrollTop:0}, 'slow');	
			return false;
		}		
		/**** view device details in readonly mode ****/
		function viewDetail(clubId){
			var resourceURL="";
			var deviceTypeParameterName = "";
			if($('#whichDevice'=='questions_')) {
				deviceTypeParameterName = "questionType";
			} else if($('#whichDevice'=='bills_')) {
				deviceTypeParameterName = "deviceType";
			} else if($('#whichDevice'=='motions_')){
				
			}	
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&"+deviceTypeParameterName+"="+$("#refDeviceType").val()
			+"&ugparam="+$("#ugparam").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#currentusergroupType").val()
			+"&edit=false";
			if($('#whichDevice').val()=='questions_') {
				resourceURL='question/'+clubId+'/edit?'+parameters;
			}else if($('#whichDevice').val()=='bills_') {
				resourceURL='bill/'+clubId+'/edit?'+parameters;
			}else if($('#whichDevice').val()=='motions_') {
				resourceURL='motion/'+clubId+'/edit?'+parameters;
			}else if($('#whichDevice').val()=='motions_cutmotion_') {
				resourceURL='cutmotion/'+clubId+'/edit?'+parameters;
			}			
			$.get(resourceURL,function(data){
				$("#clubbingDiv").hide();
				$("#viewQuestion").html(data);
				$("#viewQuestionDiv").show();
				$.unblockUI();				
			},'html').fail(function(){
				$.unblockUI();
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			$.unblockUI();						
		}
		/**** on clicking back ****/
		function back(){
			$("#clubbingDiv").show();		
			$("#clubbingResult").empty();
			$("#viewQuestion").empty();
			$("#viewQuestionDiv").hide();
		}
	</script>

<style type="text/css">
/*#searchTable td > table{
width:400px;
height:400px;
}
#searchTable{
  border: 0px solid black;
  border-spacing: 0px;
}

#searchTable thead tr{
   font-size: 14px;
}

#searchTable thead tr th{
  border-bottom: 2px solid black;
  border-top: 1px solid black;
  margin: 0px;
  padding: 2px;
  background-color: #cccccc;
}

#searchTable tr {
  font-size:12px;  
}

#searchTable tr.odd {
  background-color: #AAAAAA;
}

#searchTable tr td, th{
  border-bottom: 1px solid black;
  padding: 2px; 
  text-align: center;  
}	

.expand{

}
#searchTable a{
text-decoration: underline;
color: green;
cursor: hand;
cursor:pointer;
}
*/
.filterSelected{
color:blue;
}
.highlightedSearchPattern{
font-weight: bold;
text-decoration: underline;
}
td>.strippedTable{
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
	<select name="deviceTypeStarred" id="deviceTypeStarred" style="width:100px;height: 25px;">			
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
		</c:forEach>
	</select> 
	<select id="deviceTypeStarredMaster" style="display:none;">
		<c:forEach items="${deviceTypes}" var="i">
			<option value="${i.id}"><c:out value="${i.type}"></c:out></option>			
		</c:forEach>
	</select> |
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.sessionyear" text="Year"/>
	</a>			
	<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;">				
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
	<select name="sessionTypeStarred" id="sessionTypeStarred" style="width:100px;height: 25px;">				
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
	<span class="starredSpecificFilters">
	<a href="#" class="butSim">
		<spring:message code="question.group" text="Group"/>
	</a>			
	<select name="groupStarred" id="groupStarred" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
		<c:forEach items="${groups}" var="i">			
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach> 
	</select> |			
	<a href="#" class="butSim">
		<spring:message code="question.answeringDate" text="Answering Date"/>
	</a>			
	<select name="answeringDateStarred" id="answeringDateStarred" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
	</select> |	
	</span>			
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.ministry" text="Ministry"/>
	</a>			
	<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
	</select> |			
	<%-- <a href="#" class="butSim">
		<spring:message code="advancedsearch.department" text="Department"/>
	</a>			
	<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
	</select> | --%>
	<a href="#" class="butSim">
		<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
	</a>	
	<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
		<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
	</select> |
	
	<hr>
	<a href="#" class="butSim">
		<spring:message code="question.status" text="Status"/>
	</a>			
	<select name="statusStarred" id="statusStarred" class="sSelect">			
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
				<a href="#" id="backToDevice" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.back" text="Back"></spring:message></a>
			</td>
		</tr>
	</table>
</div>

<p id="clubbingP">
<c:choose>
	<c:when test="${whichDevice=='questions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${subject}</c:when>
	<c:when test="${whichDevice=='bills_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${title}</c:when>
	<c:when test="${whichDevice=='motions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a>:${subject}</c:when>
</c:choose>
<input type="hidden" id="deviceId" value="${id }">
<input type="hidden" id="deviceNumber" value="${number}">
</p>

<div id="clubbingDiv">

<div id="clubbingResult" style="margin: 10px;">
</div>

<div id="searchresult" style="display:none; width: 910px; border: 2px solid; margin: 5px;">
<table  id="searchTable" style="width: 100%;" class="strippedTable">
<thead>
<tr>
<th class="expand"><spring:message code="clubbing.number" text="Number"></spring:message></th>
<c:choose>
<c:when test="${whichDevice=='questions_'}">
<th class="expand" id="qRevision" style="display:none;"><spring:message code="clubbing.revision" text="Revision"></spring:message></th>
<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='motions_'}">
	<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='bills_'}">
<th class="expand"><spring:message code="clubbing.title" text="Title"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='motions_cutmotion_'}">
<th class="expand" id="qRevision" style="display:none;"><spring:message code="clubbing.revision" text="Revision"></spring:message></th>
<th class="expand"><spring:message code="clubbing.cutmotion.main_title" text="Main Title"></spring:message></th>
</c:when>
</c:choose>
<c:choose>
<c:when test="${whichDevice=='questions_'}">
<th class="expand"><spring:message code="clubbing.question" text="Question"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='motions_'}">
<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='bills_'}">
<th class="expand"><spring:message code="clubbing.bill" text="Content Draft"></spring:message></th>
</c:when>
<c:when test="${whichDevice=='motions_cutmotion_'}">
<th class="expand"><spring:message code="clubbing.cutmotion.noticecontent" text="Notice Content"></spring:message></th>
</c:when>
</c:choose>
<c:if test="${whichDevice=='bills_'}">
<th class="expand"><spring:message code="clubbing.billDetails" text="Bill Details"></spring:message></th>
</c:if>
</tr>
</thead>
<tbody id="searchresultbody">
</tbody>
</table>
</div>

</div>

<div id="viewQuestionDiv" style="display:none;">
	<a id="backToSearch" href="#" style="display:block;"><spring:message code="clubbing.back" text="Back to search page"></spring:message></a>
<div id="viewQuestion">
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
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="subdepartmentValue" value="<spring:message code='question.department' text='subDepartment'/>" />
<input type="hidden" id="billWithoutNumber" value="<spring:message code='bill.referredBillWithoutNumber' text='Click To See'/>">
<input id="ballotDateTitle" value="<spring:message code='question.ballotDateTitle' text='Ballot Date'/>" type="hidden">
<input id="chartDateTitle" value="<spring:message code='question.chartDateTitle' text='Chart Date'/>" type="hidden">
<input id="parentTitle" value="<spring:message code='question.parentTitle' text='Parent'/>" type="hidden">
<input id="clubbingTitle" value="<spring:message code='question.clubbingTitle' text='Clubbed Entities'/>" type="hidden">
</body>
</html>