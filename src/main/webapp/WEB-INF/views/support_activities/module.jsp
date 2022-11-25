<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>Support Utilities</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var primaryMemberControlName=$(".autosuggest").attr("id");
	var start=0;
	var record=10;
	var previousSearchTerm="";
	var previousSearchCount=record;
		$(document).ready(function() {
			initControls();
			/**** Remove hr from embeddd table ****/
			$("#searchTable td > table hr:last").remove();
			/**** Reset Filters ****/
			$("#reset").click(function(){
				$("#houseTypeForSearch").val($("#defaultSelectedHouseType").val());
				$("#houseTypeForSearch").css("color","");
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				$("#deviceTypeForSearch").val($("#defaultSelectedDeviceType").val());
				$("#deviceTypeForSearch").css("color","");
				$("#sessionYearForSearch").val("-");
				$("#sessionYearForSearch").css("color","");				
				$("#sessionTypeForSearch").val("-");
				$("#sessionTypeForSearch").css("color","");								
				$("#groupForSearch").empty();
				$("#groupForSearch").html(text);	
				$("#groupForSearch").css("color","");								
				$("#answeringDateForSearch").empty();
				$("#answeringDateForSearch").html(text);
				$("#answeringDateForSearch").css("color","");				
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);
				$("#ministryForSearch").css("color","");				
				$("#departmentForSearch").empty();
				$("#departmentForSearch").html(text);
				$("#departmentForSearch").css("color","");				
				$("#subDepartmentForSearch").empty();
				$("#subDepartmentForSearch").html(text);
				$("#subDepartmentForSearch").css("color","");				
				$("#statusForSearch").val("-");	
				$("#statusForSearch").css("color","");
				if($('#whichDevice').val()=='bills_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
				}						
			});
			/******** Filters *********/
			/**** Session Year ****/
			$("#sessionYearForSearch").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var type=$("#sessionTypeForSearch").val();
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
					$("#groupForSearch").empty();
					$("#groupForSearch").html(text);					
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);					
				}
			});
			/**** Session Type ****/
			$("#sessionTypeForSearch").change(function(){
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				var value=$(this).val();
				var year=$("#sessionYearForSearch").val();
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
					$("#groupForSearch").empty();
					$("#groupForSearch").html(text);					
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);
				}
			});
			/**** Device Type ****/
			$("#deviceTypeForSearch").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");
					$.get('ref/devicetype/'+value+'/statuses_for_support_activities', function(data) {
						console.log(data);
						var options="<option value='0'>"+$("#pleaseSelect").val()+"</option>";	
						$('#statusForSearch option').empty();
						for(var i=0;i<data.length;i++){
							options+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
						}
						$('#statusForSearch').html(options);
					});
				}else{
					$(this).css("color","");					
				}
				
			});
			/**** Group ****/
			$("#groupForSearch").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);
				}else{
					loadAD(value);
				}
			});
			/**** Ministry ****/
			$("#ministryForSearch").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");	
					if($('#whichDevice').val()=='questions_'){
						loadAllMinistries();
					}
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#departmentForSearch").empty();
					$("#departmentForSearch").html(text);
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);
				}else{
					//loadDep(value);
					loadSubdepartments(value);
				}
			});
			/**** Department ****/	
			$("#departmentForSearch").change(function(){
				var value=$(this).val();
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#subDepartmentForSearch").empty();
					$("#subDepartmentForSearch").html(text);
				}else{
					loadSubDep($("#ministryForSearch").val(),value);
				}
			});		
			/**** Ansering Date ****/
			$("#answeringDateForSearch").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Sub Department ****/
			$("#subDepartmentForSearch").change(function(){
				var value=$(this).val();				
				if(value!='-'){
					$(this).css("color","blue");				
				}else{
					$(this).css("color","");					
				}
			});
			/**** Status ****/
			$("#statusForSearch").change(function(){
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
			/**** Back To Question ****/
			/**** Back To Question ****/
			$("#backToQuestion").click(function(){
				/* $("#reset").click();
				$('#searchvalue').val(""); */
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToQuestionDiv").hide();
				if($("#assistantDiv").length>0){
				$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}else{
					if($('#whichDevice').val()=='questions_') {
						showQuestionList();
					}
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			$("#formattedPrimaryMember").autocomplete({
				minLength:3,			
				source:'ref/member/supportingmembers/fromsession?houseType1='+$("#houseTypeCommon").val()+'&houseType2='+$("#houseTypeCommon").val()+
						'&sessionYear1='+$("#sessionYearForSearch").val()+'&sessionYear2='+$("#sessionYearForSearch").val()+
						'&sessionType1='+$("#sessionTypeForSearch").val()+'&sessionType2='+$("#sessionTypeForSearch").val(),
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
			
			if($('#whichDevice').val()=='questions_'){
				loadAllMinistries();
			}			
		});
		
		function loadAllMinistries(){
			$.get('ref/sessionbyhousetype/'+$("#houseTypeForSearch").val()+"/"+$("#sessionYearForSearch").val()+"/"+$("#sessionTypeForSearch").val(),function(data){
				if(data){
					if($('#whichDevice').val()=='questions_') {						
						var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
						
						$("#departmentForSearch").empty();
						$("#departmentForSearch").html(text);
						$("#subDepartmentForSearch").empty();
						$("#subDepartmentForSearch").html(text);
						
						$.get('ref/allministries?session='+data.id,function(data1){
								if(data1.length>0){
									for(var i=0;i<data1.length;i++){
										text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
									}
									$("#ministryForSearch").empty();
									$("#ministryForSearch").html(text);						
								}else{
									$("#ministryForSearch").empty();
									$("#ministryForSearch").html(text);		
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
			if($('#whichDevice').val()=='questions_') {						
				var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
				$("#subDepartmentForSearch").empty();
				$("#subDepartmentForSearch").html(text);
				
				$.get('ref/subdepartments/ministry?ministryId='+ministry,function(data1){
						if(data1.length>0){
							for(var i=0;i<data1.length;i++){
								text+="<option value='"+data1[i].id+"'>"+data1[i].name+"</option>";
							}
							$("#subDepartmentForSearch").empty();
							$("#subDepartmentForSearch").html(text);						
						}else{
							$("#subDepartmentForSearch").empty();
							$("#subDepartmentForSearch").html(text);		
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
			$("#answeringDateForSearch").empty();
			$("#answeringDateForSearch").html(text);
			$("#ministryForSearch").empty();
			$("#ministryForSearch").html(text);
			$("#departmentForSearch").empty();
			$("#departmentForSearch").html(text);
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);
			$.get('ref/groups?'+param,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#groupForSearch").empty();
				$("#groupForSearch").html(text);
			}else{
				$("#groupForSearch").empty();
				$("#groupForSearch").html(text);					
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
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					loadMin(group);						
				}else{
					$("#answeringDateForSearch").empty();
					$("#answeringDateForSearch").html(text);
					$("#ministryForSearch").empty();
					$("#ministryForSearch").html(text);		
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
			$("#departmentForSearch").empty();
			$("#departmentForSearch").html(text);
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);
			$.get('ref/group/'+group+'/ministries',function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);						
				}else{
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);		
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
			var year = $("#sessionYearForSearch").val();
			var houseType = $("#houseTypeCommon").val();
			var sessionType = $("#sessionTypeForSearch").val();
			
			var url = "ref/ministry/"+houseType+"/"+year+"/"+sessionType;
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#departmentForSearch").empty();
			$("#departmentForSearch").html(text);
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);
			$.get(url,function(data){
				if(data.length>0){
				for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name;
				}
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);						
				}else{
				$("#ministryForSearch").empty();
				$("#ministryForSearch").html(text);		
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
		var param = "houseType=" + $('#houseTypeForSearch').val() +
		"&sessionType=" + $('#sessionTypeForSearch').val() +
		"&sessionYear=" +$('#sessionYearForSearch').val();
		$.get('ref/departments/'+ministry+'?'+param,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);
			if(data.length>0){
				for(var i=0;i<data.length;i++){
				text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
				$("#departmentForSearch").empty();
				$("#departmentForSearch").html(text);					
			}else{
				$("#departmentForSearch").empty();
				$("#departmentForSearch").html(text);
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
		var param = "houseType=" + $('#houseTypeForSearch').val() +
		"&sessionType=" + $('#sessionTypeForSearch').val() +
		"&sessionYear=" +$('#sessionYearForSearch').val();
		$.get('ref/subdepartments/'+ministry+'/'+department +'?'+ param,function(data){
			var text="<option value='-'>----"+$("#pleaseSelect").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
			text+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);					
			}else{
			$("#subDepartmentForSearch").empty();
			$("#subDepartmentForSearch").html(text);
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
			$.get('ref/sessionbyhousetype/'+$("#houseTypeForSearch").val()+"/"+$("#sessionYearForSearch").val()+"/"+$("#sessionTypeForSearch").val(),function(data){
				if(data){				
					
					if($('#whichDevice').val()=='questions_') {
						postData={param:$("#searchvalue").val(),session:data.id,record:record,start:start};
						
						var deviceType = $("#deviceTypeForSearch").val();						
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
						if($("#sessionYearForSearch").length>0){
							postData['sessionYear']=$("#sessionYearForSearch").val();
						}
						if($("#sessionTypeForSearch").length>0){
							postData['sessionType']=$("#sessionTypeForSearch").val();
						}
						if($("#groupForSearch").length>0){
								postData['group']=$("#groupForSearch").val();
						}
						if($("#answeringDateForSearch").length>0){
								postData['answeringDate']=$("#answeringDateForSearch").val();
						}
						if($("#ministryForSearch").length>0){
							postData['ministry']=$("#ministryForSearch").val();
						}
						if($("#departmentForSearch").length>0){
							postData['department']=$("#departmentForSearch").val();
						}
						if($("#subDepartmentForSearch").length>0){    
							postData['subDepartment']=$("#subDepartmentForSearch").val();
						}
						if($("#statusForSearch").length>0){
							postData['status']=$("#statusForSearch").val();
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
								
									if($('#whichDevice').val()=='questions_') {
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
									
									if($('#whichDevice').val()=='questions_') {
										textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].questionText
										+"<br/>"
										+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
										+"<strong>"+data[i].formattedGroup+"</span>,"+data[i].ministry;
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
			var parameters="houseType="+$("#houseTypeForSearch").val()
			+"&sessionYear="+$("#sessionYearForSearch").val()
			+"&sessionType="+$("#sessionTypeForSearch").val()
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
	<div class="clearfix content">
		<div class="clearfix tabbar">
			<ul class="tabs">
				<li>
					<a id="search_tab" href="#" class="tab selected">
					   <spring:message code="generic.search" text="Search"></spring:message>
					</a>
				</li>
				<li>
					<a id="details_tab" href="#" class="tab">
					   <spring:message code="generic.details" text="Details"></spring:message>
					</a>
				</li>			
			</ul>
			<div class="tabContent clearfix">
				<p id="error_p" style="display: none;">&nbsp;</p>
				<c:if test="${(error!='') && (error!=null)}">
					<h4 style="color: #FF0000;">${error}</h4>
				</c:if>
				<div id="supportSearch">
					<a href="#" class="butSim">
						<spring:message code="generic.houseType" text="House Type"/>
					</a>		
					<select name="houseTypeForSearch" id="houseTypeForSearch" style="width:100px;height: 25px;">			
						<%-- <option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option> --%>			
						<c:forEach items="${houseTypes}" var="i">
							<c:choose>
								<c:when test="${defaultSelectedHouseType==i.type}">
									<option value="${i.type}" selected="selected">
										<c:out value="${i.name}"></c:out>
									</option>
								</c:when>
								<c:otherwise>
									<option value="${i.type}">
										<c:out value="${i.name}"></c:out>
									</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					
					<a href="#" class="butSim">
						<spring:message code="generic.deviceType" text="Device Type"/>
					</a>		
					<select name="deviceTypeForSearch" id="deviceTypeForSearch" style="width:100px;height: 25px;">			
						<c:forEach items="${deviceTypes}" var="i">
							<c:choose>
								<c:when test="${defaultSelectedDeviceType==i.id}">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
								</c:when>
								<c:otherwise>
									<option value="${i.id}"><c:out value="${i.name}"></c:out></option>			
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select> 
					<select id="deviceTypeForSearchMaster" style="display:none;">
						<c:forEach items="${deviceTypes}" var="i">
							<option value="${i.id}"><c:out value="${i.type}"></c:out></option>			
						</c:forEach>
					</select> |
					
					<a href="#" class="butSim">
						<spring:message code="generic.sessionyear" text="Year"/>
					</a>			
					<select name="sessionYearForSearch" id="sessionYearForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach items="${years}" var="i">
							<c:choose>
								<c:when test="${sessionYear==i.id}">
									<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>			
								</c:when>
								<c:otherwise>
									<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>			
								</c:otherwise>
							</c:choose>
						</c:forEach> 
					</select> |	
							
					<a href="#" class="butSim">
						<spring:message code="generic.sessionType" text="Session Type"/>
					</a>			
					<select name="sessionTypeForSearch" id="sessionTypeForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach items="${sessionTypes}" var="i">
							<c:choose>
								<c:when test="${sessionType==i.id}">
									<option value="${i.id}" selected="selected"><c:out value="${i.sessionType}"></c:out></option>
								</c:when>
								<c:otherwise>
									<option value="${i.id}"><c:out value="${i.sessionType}"></c:out></option>	
								</c:otherwise>
							</c:choose>
						</c:forEach> 
					</select> |		
					<hr>
					<a href="#" class="butSim">
						<spring:message code="question.group" text="Group"/>
					</a>			
					<select name="groupForSearch" id="groupForSearch" style="width:100px;height: 25px;">
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
						<c:forEach items="${groups}" var="i">
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
						</c:forEach>
					</select> |			
					<%-- <a href="#" class="butSim">
						<spring:message code="question.answeringDate" text="Answering Date"/>
					</a>			
					<select name="answeringDateForSearch" id="answeringDateForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |				
					<a href="#" class="butSim">
						<spring:message code="generic.ministry" text="Ministry"/>
					</a>			
					<select name="ministryForSearch" id="ministryForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			 --%>
					<%-- <a href="#" class="butSim">
						<spring:message code="generic.department" text="Department"/>
					</a>			
					<select name="departmentForSearch" id="departmentForSearch" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> | --%>
					
					<a href="#" class="butSim">
						<spring:message code="generic.subdepartment" text="Sub Department"/>
					</a>	
					<select name="subDepartmentForSearch" id="subDepartmentForSearch" style="width:100px;height: 25px;">
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>
						<c:forEach items="${subdepartments}" var="i">
							<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>
						</c:forEach>
					</select> |	
					
					<a href="#" class="butSim"> 
						<spring:message	code="generic.status" text="Status" />
					</a>
					<select name="statusForSearch" id="statusForSearch"
						style="width: 150px; height: 25px;">
						<option value="0">
								<spring:message code='please.select' text='Please Select'/>
						</option>
						<c:forEach items="${statusesForDeviceType}" var="i">
							<option value="${i.id}">
								<c:out value="${i.name}"></c:out>
							</option>
						</c:forEach>
					</select> |
					
					<a href="#" class="butSim">
						<spring:message	code="generic.clubbingStatus" text="Clubbing Status"/>
					</a>
					<select name="clubbingStatusForSearch" id="clubbingStatusForSearch" style="height: 25px;">
						<option value="all" selected="selected"><spring:message code="generic.clubbingStatus.all" text="Please Select"/></option>
						<option value="parent"><spring:message code="generic.clubbingStatus.parent" text="Parent"/></option>
						<option value="child"><spring:message code="generic.clubbingStatus.child" text="Child"/></option>
					</select> |
									
					<hr>
					<a href="#" class="butSim">
						<spring:message code="generic.searchType" text="Search By"/>
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
					<%-- <a href="#" class="butSim">
						<spring:message code="question.status" text="Status"/>
					</a>			
					<select name="statusForSearch" id="statusForSearch" class="sSelect">			
					<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
					<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
					<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
					</select> |	 --%>			
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
								<a href="#" id="backToQuestion" style="margin-left: 10px;margin-right: 10px;"><spring:message code="clubbing.back" text="Back"></spring:message></a>
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
			</div>
		</div>
	</div>
	
	<input type="hidden" id="moduleLocale" value="${moduleLocale}" />
	<input type="hidden" id="defaultSelectedHouseType" value="${defaultSelectedHouseType}" />
	<input type="hidden" id="defaultSelectedDeviceType" value="${defaultSelectedDeviceType}" />
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
	<input id="houseTypeYearSessionTypeEmptyMsg" value="<spring:message code='client.error.generic.yeartypeempty' text='Session Year and Session Type must be selected to continue'/>" type="hidden">
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