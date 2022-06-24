<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	var start=0;
	var record=10;
	var previousSearchTerm="";
	var previousSearchCount=record;
		$(document).ready(function() {
			
			$(".wysiwyg").wysiwyg();
			$("#questionText1").css("display","none");
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
				if($('#whichDevice').val()=='bills_' || $('#whichDevice').val()=='motions_billamendment_') {
					$("#languageAllowed").val("-");	
					$("#languageAllowed").css("color","");
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
					if(($("#whichDevice").val().indexOf('questions_')==0)
							|| ($("#refDeviceType").val()=='motions_standalonemotion_halfhourdiscussion' && $("#houseTypeCommon").val()=='upperhouse')
							){
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
				}
				var text="<option value='-'>"+$("#pleaseSelect").val()+"</option>";		
				if(value=='-'){
					$("#departmentStarred").empty();
					$("#departmentStarred").html(text);
					$("#subDepartmentStarred").empty();
					$("#subDepartmentStarred").html(text);
				}else{
					loadDep(value);
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
					refreshEdit($("#id").val());
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			/**** Populate subject line for bill amendment motion ****/
			if($('#whichDevice').val()=='motions_billamendment_') {
				$.get('ref/billamendmentmotion/subjectline?amendedBillInfo='+$('#amendedBillInfo').val(), function(data) {
					$('#billAmendmentMotion_subjectline').empty();
					$('#billAmendmentMotion_subjectline').html(data);				
				}).fail(function() {
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					//resetControls();
					scrollTop();
				});
			}
			
			
			
			
			/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text starts****/
			$("#clubbedQuestionTextsDiv1").hide();
			$("#hideClubQTDiv1").hide();
			$("#viewClubbedQuestionTextsDiv1").click(function(){
				if($("#clubbedQuestionTextsDiv1").css('display')=='none'){
						$("#clubbedQuestionTextsDiv1").empty();
						var text = $("#questionText1").val();
						$("#clubbedQuestionTextsDiv1").html(text);
						$("#hideClubQTDiv1").show();
						$("#clubbedQuestionTextsDiv1").show();
					}else{
						$("#clubbedQuestionTextsDiv1").hide();
						$("#hideClubQTDiv1").hide();
					}
				
			});
			$("#hideClubQTDiv1").click(function(){
				$(this).hide();
				$('#clubbedQuestionTextsDiv1').hide();
			});
			/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text end****/
		});
		
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
		$.get('ref/subdepartments/'+ministry+'/'+department+'?'+param,function(data){
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
			if($('#whichDevice').val()=='questions_') {
				postData={param:$("#searchvalue").val(),question:$("#deviceId").val(),record:record,start:start};
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
				resourceURL = "clubentity/search?filing="+$("#useforfiling").val();
			} else if($('#whichDevice').val()=='bills_') {
				postData={param:$("#searchvalue").val(),billId:$("#deviceId").val(),record:record,start:start};
				if($("#languageAllowed").length>0){
					postData['language']=$("#languageAllowed").val();
				}
				if($("#statusStarred").length>0){
					postData['status']=$("#statusStarred").val();
				}
				resourceURL = "clubentity/searchbill?filing="+$("#useforfiling").val();
			} else if($('#whichDevice').val()=='motions_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_standalonemotion_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchstandalone?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_cutmotion_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchcutmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_discussion_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchdiscussionmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_eventmotion_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searcheventmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_adjournment_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchadjournmentmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='notices_specialmention_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchspecialmentionnotice?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_billamendment_') {
				postData={param:$("#searchvalue").val(),billAmendmentMotionId:$("#deviceId").val(),record:record,start:start};
				if($("#languageAllowed").length>0){
					postData['language']=$("#languageAllowed").val();
				}
				if($("#statusStarred").length>0){
					postData['status']=$("#statusStarred").val();
				}
				resourceURL = "clubentity/searchbillamendmentmotion?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='resolutions_') {
				postData={param:$("#searchvalue").val(),resolutionId:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
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
				resourceURL = "clubentity/searchresolution?filing="+$("#useforfiling").val();
			}else if($('#whichDevice').val()=='motions_rules_suspension_'){
				postData={param:$("#searchvalue").val(),motion:$("#deviceId").val(),record:record,start:start};
				if($("#houseTypeCommon").length>0){
					postData['houseType']=$("#houseTypeCommon").val();
				}
				if($("#sessionYearStarred").length>0){
					postData['sessionYear']=$("#sessionYearStarred").val();
				}
				if($("#sessionTypeStarred").length>0){
					postData['sessionType']=$("#sessionTypeStarred").val();
				}				
				if($("#statusStarred").length>0){
					postData['status']=$("#statusStarred").val();
				}
				resourceURL = "clubentity/searchrulessuspensionmotion?filing="+$("#useforfiling").val();
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
							textTemp=textTemp+"<tr>";
							if(data[i].classification=='Clubbing'){
								textTemp+="<td style='width:20px;'><input type='checkbox' id='chk"+data[i].id+"' class='sCheck action' value='true' style='margin-right: 10px;'>";
							} else {
								textTemp+="<td style='width:20px;'><input type='checkbox' id='chk"+data[i].id+"' class='sCheck action' disabled='disabled' value='false' style='margin-right: 10px;'>";
							}
							textTemp+="<td class='expand' style='width: 150px; max-width: 150px;'>"+
									"<span id='number"+data[i].id+"'>"+
									"<a onclick='viewDetail("+data[i].id+");' style='margin:10px; text-decoration: underline;'>"+									
									deviceNumber+"</a></span>"
									+"<br>";
							textTemp+="<span id='operation"+data[i].id+"'>";
							if($("#useforfiling").val()!='' && $("#useforfiling").val()=='yes'){
								textTemp+="<a onclick='addToFile("+data[i].id+");' style='margin:10px;cursor:pointer;'>"+$("#addToFileMsg").val()+"</a>";
							}else{
								if(data[i].classification=='Clubbing'){
									textTemp+="<a onclick='clubbing("+data[i].id+");' style='margin:10px;cursor:pointer;'>"+$("#clubMsg").val()+"</a>";
								}else if(data[i].classification=='Group Change'){
									textTemp+="<a style='margin:10px;' href='javascript:void(0);'>"+$("#groupChangeMsg").val()+"</a>";
								}else if(data[i].classification=='Ministry Change'){
									textTemp+="<a style='margin:10px;' href='javascript:void(0);'>"+$("#ministryChangeMsg").val()+"</a>";
								}else if(data[i].classification=='Department Change'){
									textTemp+="<a style='margin:10px;' href='javascript:void(0);'>"+$("#departmentChangeMsg").val()+"</a>";
								}else if(data[i].classification=='Sub Department Change'){
									textTemp+="<a style='margin:10px;' href='javascript:void(0);'>"+$("#subDepartmentChangeMsg").val()+"</a>";
								}else if(data[i].classification=='Referencing'){
									textTemp+="<a style='margin:10px;' href='javascript:void(0);'>"+$("#referencingMsg").val()+"</a>";
								}
							}
							textTemp+="</span>";					
							+"</td>";
						
							if($('#whichDevice').val()=='questions_') {
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							} else if($('#whichDevice').val()=='bills_') {
								var title = "";
								if(data[i].revisedTitle!='null' && data[i].revisedTitle!='' && data[i].revisedTitle!=undefined) {
									title = data[i].revisedTitle;									
								} else if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
									title = data[i].title;
								} 								
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+title+"</td>";
							}else if($('#whichDevice').val()=='motions_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";
							}else if($('#whichDevice').val()=='motions_cutmotion_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";
							}else if($('#whichDevice').val()=='motions_eventmotion_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";
							}else if($('#whichDevice').val()=='motions_discussion_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";
							}else if($('#whichDevice').val()=='motions_standalonemotion_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							}else if($('#whichDevice').val()=='motions_adjournment_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							}else if($('#whichDevice').val()=='notices_specialmention_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							}else if($('#whichDevice').val()=='motions_billamendment_') {
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+$('#billAmendmentMotion_subjectline').html()+"</td>";
							}else if($('#whichDevice').val()=='resolutions_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							}else if($('#whichDevice').val()=='motions_rules_suspension_'){
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].subject+"</td>";
							}
							
							if($('#whichDevice').val()=='questions_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : ";
								
								var questText = data[i].questionText;
								questText = questText.replace(/&nbsp;/g," ");
								questText = questText.replace(/ /g,"");
								
								var currentQuestionText = $("#questionText1").val();
								currentQuestionText = currentQuestionText.replace(/\s/g,"");
								currentQuestionText = currentQuestionText.replace(/ /g,"");
								
								if(questText == currentQuestionText){
									textTemp+= "<b>" + data[i].questionText + "</b>";
								}else{
									textTemp+=data[i].questionText;
								}
								textTemp += "<br/>"
								+ data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+"<strong>"+data[i].formattedGroup+"</span>,"+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;
								 
							     
							    }
								if(data[i].chartAnsweringDate==null||data[i].chartAnsweringDate==''){
									textTemp+="</td>";
								}else{
									textTemp+=" ,"+data[i].chartAnsweringDate+"</td>";
								} 
							} else if($('#whichDevice').val()=='bills_') {
								var content = "";
								if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
									content = data[i].revisedContent;									
								} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
									content = data[i].content;
								} 								
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+content+"</td>";
							}else if($('#whichDevice').val()=='motions_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_cutmotion_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_eventmotion_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_discussion_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_standalonemotion_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].questionText
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_adjournment_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/><strong>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status+"</strong>";							     
							    }
							}else if($('#whichDevice').val()=='notices_specialmention_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/><strong>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status+"</strong>";							     
							    }
							}else if($('#whichDevice').val()=='motions_billamendment_') {
								var content = "";
								if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
									content = data[i].revisedContent;									
								} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
									content = data[i].content;
								} 
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+content
								+"<br/><strong>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br></span>"+data[i].status+"</strong></td>";							
							}else if($('#whichDevice').val()=='resolutions_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].questionText
								+"<br/>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status+"<br>";
								   
							    }else{						     
							    	textTemp+=","+data[i].subDepartment+" "+$('#subdepartmentValue').val()+"<br>"+ data[i].status;							     
							    }
							}else if($('#whichDevice').val()=='motions_rules_suspension_') {
								textTemp+="<td class='expand' style='width: 420px; max-width: 420px;'>"+data[i].formattedPrimaryMember+" : "+data[i].noticeContent
								+"<br/><strong>"
								+data[i].sessionYear+","+data[i].sessionType+","+data[i].deviceType+"<br>"
								+data[i].status
							}
							
							
							if($('#whichDevice').val()=='bills_') {
								textTemp+="<td class='expand' style='width: 300px; max-width: 300px;'>"+data[i].sessionYear+","+
								data[i].sessionType+","+data[i].deviceType+",<br></span>"+data[i].ministry;
								if(data[i].subDepartment==null||data[i].subdepartment==""){
									textTemp+=","+data[i].status
								    +"</td>";
							    }else{						     
							    textTemp+=","+data[i].subDepartment+"<br>"
								 +data[i].status
							     +"</td>";
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
		/**** Load More ****/
		function loadMore(){			
			search("NO");
			$(".clearLoadMore").empty();
		}	
		/**** On Clubbing ****/
		function clubbing(clubId){
			if($('#chk'+clubId).is(":checked")) {
				bulkClubbing(clubId);
			} else {
				singleClubbing(clubId);
			}
		}
		/**** On Single Clubbing ****/
		function singleClubbing(clubId){
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
			$.post('clubentity/clubbing?pId='+deviceId+"&cId="+clubId
					+"&usergroupType="+$("#currentusergroupType").val()
					+'&whichDevice='+whichDevice,function(data){
					
					if(data=='CLUBBING_SUCCESS'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+$('#clubbingSuccessMsg').val();
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					} else if(data=='CLUBBING_FAILURE'){
						var text="<span style='color:red;font-weight:bold;font-size:16px;'>"+$('#clubbingFailureMsg').val();
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);						
					} else if(data=='SEARCHED_CLUBBED_TO_PROCESSED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" Clubbed To "+deviceNumber;
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					} else if(data=='PROCESSED_CLUBBED_TO_SEARCHED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+deviceNumber+" Clubbed To "+clubbedNumber;
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					} else if(data=='BEINGSEARCHED_QUESTION_ALREADY_CLUBBED' || data=='BEINGSEARCHED_BILL_ALREADY_CLUBBED'){
						var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+clubbedNumber+" is already  clubbed.";
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
						$("#operation"+clubId).empty();
						$("#operation"+clubId).html("<a onclick='unclubbing("+clubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
					} else{
						var text="<span style='color:red;font-weight:bold;font-size:16px;'>"+data;
						$("#clubbingResult").empty();
						$("#clubbingResult").html(text);
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
		/**** On Single Clubbing ****/
		function bulkClubbing(clubId){
			var clubbingIds = clubId;
			var bulkClubbingDevicesLimit = $('#devicesCountLimitAllowedForBulkClubbing').val();
			var devicesCount = 0;
			$(".action").each(function() {
				if($(this).is(":checked")) {
					var selectedDeviceId = $(this).attr('id').split("chk")[1];
					if(selectedDeviceId!=clubId) {
						clubbingIds += "," + selectedDeviceId;
					}		
					devicesCount++;
				}
			});
			if(devicesCount>parseInt(bulkClubbingDevicesLimit)) {
				$.prompt("You cannot club more than " + bulkClubbingDevicesLimit + " devices in bulk!");
			} else {
				$.prompt($('#bulkClubbingConfirmationMsg').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
						if(v){
							$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });		
							var deviceId=$("#deviceId").val();	
							var whichDevice = $('#whichDevice').val();
							$.post('clubentity/bulk_clubbing?pId='+deviceId+"&clubIds="+clubbingIds
									+"&usergroupType="+$("#currentusergroupType").val()
									+'&whichDevice='+whichDevice,function(data){
									
									console.log("data.result=:"+data.result);
									//console.log("data.result=:"+data[result]);						
								
									if(data.result==true){
										console.log("data.success_result=:"+data.result);
										var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+data.clubSuccessDetails;
										$("#clubbingResult").empty();
										$("#clubbingResult").html(text);
										
										$(".action").each(function() {
											if($(this).is(":checked")) {
												var selectedClubId = $(this).attr('id').split("chk")[1];
												$("#operation"+selectedClubId).empty();
												$("#operation"+selectedClubId).html("<a onclick='unclubbing("+selectedClubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
												$(this).removeAttr('checked');
											}
										});										
										
									} else if(data.result==false){										
										console.log("data.error_result=:"+data.result);
										var text="<span style='color:red;font-weight:bold;font-size:16px;'>"+data.clubFailureDetails;
										text += "<br>" + data.clubSuccessDetails;
										$("#clubbingResult").empty();
										$("#clubbingResult").html(text);	
										
										if(data.childDevices!=null && data.childDevices!=undefined
												&& data.childDevices.length>=1) {
											for(var i=0; i<data.childDevices.length; i++) {
												var selectedClubId = data.childDevices[i].id;
												$("#operation"+selectedClubId).empty();
												$("#operation"+selectedClubId).html("<a onclick='unclubbing("+selectedClubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
												$('#chk'+selectedClubId).removeAttr('checked');
											}
										}
									} else{ //error case
										console.log("data.error_result=:"+data.result);
										var text="<span style='color:red;font-weight:bold;font-size:16px;'>"+data.clubFailureDetails;
										text += "<br>" + data.clubSuccessDetails;
										$("#clubbingResult").empty();
										$("#clubbingResult").html(text);	
										
										if(data.childDevices!=null && data.childDevices!=undefined
												&& data.childDevices.length>=1) {
											for(var i=0; i<data.childDevices.length; i++) {
												var selectedClubId = data.childDevices[i].id;
												$("#operation"+selectedClubId).empty();
												$("#operation"+selectedClubId).html("<a onclick='unclubbing("+selectedClubId+");' style='margin:10px;'>"+$("#unclubMsg").val()+"</a>");
												$('#chk'+selectedClubId).removeAttr('checked');
											}
										}
									}
									$.unblockUI();
									scrollTop();								
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
					}
				});
			}			
			return false;
		}
		/**** On unclubbing ****/
		function unclubbing(clubId){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
			var deviceId=$("#deviceId").val();
			var whichDevice = $('#whichDevice').val();
			$.post('clubentity/unclubbing?pId='+deviceId+"&cId="+clubId+'&whichDevice='+whichDevice,function(data){
				if(data=='SUCCESS' || data=='UNCLUBBING_SUCCESS'){
					var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+$('#unclubbingSuccessMsg').val();
					$("#clubbingResult").empty();
					$("#clubbingResult").html(text);
					$("#operation"+clubId).empty();
					$("#operation"+clubId).html("<a onclick='clubbing("+clubId+");' style='margin:10px;'>"+$("#clubMsg").val()+"</a>");
				} else{
					var text="<span style='color:green;font-weight:bold;font-size:16px;'>"+$('#unclubbingFailureMsg').val();
					$("#clubbingResult").empty();
					$("#clubbingResult").html(text);
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
				
			} else if($('#whichDevice'=='motions_adjournment_')){
				deviceTypeParameterName = "deviceType";
			} else if($('#whichDevice'=='notices_specialmention_')){
				deviceTypeParameterName = "deviceType";
			} else if($('#whichDevice'=='motions_billamendment_')){
				deviceTypeParameterName = "deviceType";
			}else if($('#whichDevice'=='motions_rules_suspension')){
				deviceTypeParameterName = "deviceType";
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
			}else if($('#whichDevice').val()=='motions_eventmotion_') {
				resourceURL='eventmotion/'+clubId+'/edit?'+parameters;	
			}else if($('#whichDevice').val()=='motions_discussion_') {
				resourceURL='discussionmotion/'+clubId+'/edit?'+parameters;	
			}else if($('#whichDevice').val()=='motions_standalonemotion_') {
				resourceURL='standalonemotion/'+clubId+'/edit?'+parameters;				
			}else if($('#whichDevice').val()=='motions_adjournment_') {
				resourceURL='adjournmentmotion/'+clubId+'/edit?'+parameters;				
			}else if($('#whichDevice').val()=='notices_specialmention_') {
				resourceURL='specialmentionnotice/'+clubId+'/edit?'+parameters;				
			}else if($('#whichDevice').val()=='motions_billamendment_') {
				resourceURL='billamendmentmotion/'+clubId+'/edit?'+parameters;				
			}else if($('#whichDevice').val()=='resolutions_') {
				resourceURL='resolution/'+clubId+'/edit?'+parameters;				
			}else if($('#whichDevice').val()=='motions_rules_suspension_') {
				resourceURL='rulessuspensionmotion/'+clubId+'/edit?'+parameters;				
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
		
		/****To add the serached device to a file ****/
		function addToFile(id){
			var url = "";
			var device = $("#refDeviceType").val();
			
			if($("#fileNumber").val()!='-'){
				if(device.indexOf("question_")==0){
					
				}else if(device.indexOf("resolutions_")==0){
					url += 'resolution/filing/' + id + '/' + $("#fileNumber").val() + '/enter';						
				}else if(device.indexOf("motions_")==0){
					if(device.indexOf("motions_standalonemotion_")==0){
						url += 'standalonemotion/filing/' + id + '/' + $("#fileNumber").val() + '/enter';
					}else{
						url += 'motion/' + id + '/filing' + id + '/' + $("#fileNumber").val() + '/enter';
					}
				}
				
				if(url != ''){
					$("#operation"+id).hide();
				}
				
				$.get(url,function(data){
					if(data){
						$.prompt(data);	
					}
				});
			}else{
				$.prompt($("#fileNumberSelectionMsg").val());
			}
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
td>table{
	width: 350px;
}

 #clubbedQuestionTextsDiv1{
    background: none repeat-x scroll 0 0 #FFF;
    box-shadow: 0 2px 5px #888888;
    max-height: 260px;
    right: 0;
    position: fixed;
    top: 10px;
    width: 300px;
    z-index: 10000;
    overflow: auto;
    border-radius: 10px;
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="commandbarContent" id="advancedSearch">

			<c:choose>
				<c:when test="${deviceType=='questions_starred'}">
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
					<a href="#" class="butSim unstarred">
						<spring:message code="advancedsearch.sessionyear" text="Year"/>
					</a>			
					<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;" class="unstarred">				
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
					<a href="#" class="butSim unstarred">
						<spring:message code="advancedsearch.sessionType" text="Session Type"/>
					</a>			
					<select name="sessionTypeStarred" id="sessionTypeStarred" style="width:100px;height: 25px;" class="unstarred">				
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
					<hr>			
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.ministry" text="Ministry"/>
					</a>			
					<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.department" text="Department"/>
					</a>			
					<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
					</a>			
					<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			
					<a href="#" class="butSim">
						<spring:message code="question.status" text="Status"/>
					</a>			
					<select name="statusStarred" id="statusStarred" class="sSelect">			
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
						<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
						<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
					</select> |
				</c:when>
			
			
				<c:when test="${deviceType=='questions_unstarred'}">
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.sessionyear" text="Year"/>
					</a>			
					<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;">				
					<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach var="i" items="${years}">
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
						<spring:message code="advancedsearch.sessionType" text="Session Type"/>
					</a>			
					<select name="sessionTypeStarred" id="sessionTypeStarred" style="width:100px;height: 25px;">				
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
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.ministry" text="Ministry"/>
					</a>			
					<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.department" text="Department"/>
					</a>			
					<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |	
					<hr>		
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
					</a>			
					<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
					</select> |			
					<a href="#" class="butSim">
						<spring:message code="question.status" text="Status"/>
					</a>			
					<select name="statusStarred" id="statusStarred" class="sSelect">			
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
						<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
						<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
					</select> |
				</c:when>	
			
				<c:when test="${deviceType=='bills_nonofficial'}">
					<a href="#" class="butSim">
						<spring:message code="bill.language" text="Language"/>
					</a>		
					<select name="languageAllowed" id="languageAllowed" class="sSelect">			
						<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach var="i" items="${languagesAllowedForBill}">
							<option value="${i.type}">${i.name}</option>			
						</c:forEach>
					</select>&nbsp;&nbsp;&nbsp;| 
					<a href="#" class="butSim">
						<spring:message code="bill.status" text="Status"/>
					</a>			
					<select name="statusStarred" id="statusStarred" class="sSelect">			
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<option value="UNPROCESSED"><spring:message code='bill.unprocessed' text='Un Processed'/></option>
						<option value="PENDING"><spring:message code='bill.pending' text='Pending'/></option>
						<option value="APPROVED"><spring:message code='bill.approved' text='Approved'/></option>
					</select>			
				</c:when>
				<c:when test="${deviceType=='motions_billamendment'}">
					<a href="#" class="butSim">
						<spring:message code="billamendmentmotion.amendedBill.language" text="Language"/>
					</a>		
					<select name="languageAllowed" id="languageAllowed" class="sSelect">			
						<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<c:forEach var="i" items="${languagesAllowedForMotion}">
							<option value="${i.type}">${i.name}</option>			
						</c:forEach>
					</select>&nbsp;&nbsp;&nbsp;| 
					<a href="#" class="butSim">
						<spring:message code="billamendmentmotion.status" text="Status"/>
					</a>			
					<select name="statusStarred" id="statusStarred" class="sSelect">			
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<option value="UNPROCESSED"><spring:message code='bill.unprocessed' text='Un Processed'/></option>
						<option value="PENDING"><spring:message code='bill.pending' text='Pending'/></option>
						<option value="APPROVED"><spring:message code='bill.approved' text='Approved'/></option>
					</select>			
				</c:when>		
				<c:when test="${fn:startsWith(deviceType,'motions_') or fn:startsWith(deviceType,'resolutions_')}">
					<%-- <a href="#" class="butSim">
						<spring:message code="advancedsearch.deviceType" text="Device Type"/>*
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
					</select> |	 --%>
					<a href="#" class="butSim">
						<spring:message code="advancedsearch.sessionyear" text="Year"/>
					</a>			
					<select name="sessionYearStarred" id="sessionYearStarred" style="width:100px;height: 25px;">				
						<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
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
						<option value="-" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
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
					<c:if test="${deviceType=='motions_standalonemotion_halfhourdiscussion' and houseType=='upperhouse'}">
						<a href="#" class="butSim">
							<spring:message code="question.group" text="Group"/>
						</a>			
						<select name="groupStarred" id="groupStarred" style="width:100px;height: 25px;">				
							<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
							<c:forEach items="${groups}" var="i">			
								<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
							</c:forEach> 
						</select> |	
					</c:if>
					<c:if test="${whichDevice!='motions_eventmotion_'}">
						<a href="#" class="butSim">
							<spring:message code="advancedsearch.ministry" text="Ministry"/>
						</a>			
						<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
							<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						</select> |
						<a href="#" class="butSim">
							<spring:message code="advancedsearch.department" text="Department"/>
						</a>			
						<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
							<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						</select> |	
						<hr>
						<a href="#" class="butSim">
							<spring:message code="advancedsearch.subdepartment" text="Sub Department"/>
						</a>			
						<select name="subDepartmentStarred" id="subDepartmentStarred" style="width:100px;height: 25px;">				
							<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						</select> |
					</c:if>			
					<a href="#" class="butSim">
						<spring:message code="question.status" text="Status"/>
					</a>			
					<select name="statusStarred" id="statusStarred" class="sSelect">			
						<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
						<option value="UNPROCESSED"><spring:message code='question.unprocessed' text='Un Processed'/></option>
						<option value="PENDING"><spring:message code='question.pending' text='Pending'/></option>
						<option value="APPROVED"><spring:message code='question.approved' text='Approved'/></option>
					</select> |
					<c:if test="${useforfiling=='yes'}">
						<a href="#" class="butSim">
							<spring:message code="advancedsearch.filing" text="File"/>
						</a>
						<select name="fileNumber" id="fileNumber" class="sSelect">			
							<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
							<c:forEach begin="1" end="10" step="1" var="i">
								<option value="${i}">${i}</option>
							</c:forEach>
						</select>
					</c:if>
				</c:when>
			<c:otherwise>					
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
					<spring:message code="advancedsearch.ministry" text="Ministry"/>
				</a>			
				<select name="ministryStarred" id="ministryStarred" style="width:100px;height: 25px;">				
					<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
				</select> |			
				<a href="#" class="butSim">
					<spring:message code="advancedsearch.department" text="Department"/>
				</a>			
				<select name="departmentStarred" id="departmentStarred" style="width:100px;height: 25px;">				
					<option value="-"><spring:message code="please.select" text="Please Select"></spring:message></option>			
				</select> |			
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
			</c:otherwise>
			</c:choose>						
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
	<c:when test="${whichDevice=='questions_'}">
		<a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a> (${memberName}): ${subject}
		<a href="javascript:void(0);" id="viewClubbedQuestionTextsDiv1" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
		<%-- <input type="hidden" id="questionText1" value='${questionText}' /> --%>
		<c:set var="questionTextEscapingDoubleQuote" value="${fn:replace(questionText, '\"', '&#34;')}" />
		<c:set var='questionTextEscapingSingleQuote' value='${fn:replace(questionTextEscapingDoubleQuote, "\'", "&#39;")}' />
		<input type="hidden" id="questionText1" value='${questionTextEscapingSingleQuote}' />
	</c:when>
	<c:when test="${whichDevice=='bills_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a> (${memberName}): ${title}</c:when>
	<c:when test="${whichDevice=='motions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a> (${memberName}): ${subject}</c:when>
	<c:when test="${whichDevice=='resolutions_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a> (${memberName}): ${subject}</c:when>
	<c:when test="${whichDevice=='motions_billamendment_'}"><a style="color:blue;font-size:14px;" id="primary" href="#">
		${number}</a>:<span id="billAmendmentMotion_subjectline"></span>
	</c:when>
	<c:when test="${fn:startsWith(whichDevice,'motions_')}"><a style="color:blue;font-size:14px;" id="primary" href="#">${number}</a> (${memberName}): ${subject}</c:when>
</c:choose>
<input type="hidden" id="deviceId" value="${id }">
<input type="hidden" id="deviceNumber" value="${number}">
<input type="hidden" id="deviceSubject" value="${subject}">
</p>

<div id="clubbingDiv">

<div id="clubbingResult" style="margin: 10px;">
</div>

<div id="searchresult" style="display:none; width: 910px; border: 2px solid; margin: 5px;">
<table  id="searchTable" style="width: 100%;" class="strippedTable">
<thead>
<tr>
	<th style="width:20px;"><spring:message code="clubbing.select_devices" text="Select"></spring:message>
	<th class="expand"><spring:message code="clubbing.number" text="Number"></spring:message></th>
	<c:choose>
		<c:when test="${whichDevice=='questions_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_cutmotion_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Main Title"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_eventmotion_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Event Title"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_discussion_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_adjournment_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='notices_specialmention_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_billamendment_'}">
			<th class="expand"><spring:message code="clubbing.subjectline" text="Subject Line"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='bills_'}">
			<th class="expand"><spring:message code="clubbing.title" text="Title"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_standalonemotion_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='resolutions_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_rules_suspension_'}">
			<th class="expand"><spring:message code="clubbing.subject" text="Subject"></spring:message></th>
		</c:when>
	</c:choose>
	<c:choose>
		<c:when test="${whichDevice=='questions_'}">
			<th class="expand"><spring:message code="clubbing.question" text="Question"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_cutmotion_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_eventmotion_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_discussion_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_standalonemotion_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='resolutions_'}">
			<th class="expand"><spring:message code="clubbing.resolution" text="Resolution"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_adjournment_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Adjournment Motion"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='notices_specialmention_'}">
			<th class="expand"><spring:message code="clubbing.motion" text="Special Mention Notice"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_billamendment_'}">
			<th class="expand"><spring:message code="clubbing.amendingContent" text="Amending Content"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='bills_'}">
			<th class="expand"><spring:message code="clubbing.bill" text="Content Draft"></spring:message></th>
		</c:when>
		<c:when test="${whichDevice=='motions_rules_suspension_'}">
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
<div id="clubbedQuestionTextsDiv1">
	<h1>Assistant Questio texts of clubbed questions</h1>
</div>
<div id="hideClubQTDiv1" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
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
<input id="clubbingSuccessMsg" value="<spring:message code='clubbing.success' text='Clubbing Successful'></spring:message>" type="hidden">
<input id="clubbingFailureMsg" value="<spring:message code='clubbing.failure' text='Clubbing Failed'></spring:message>" type="hidden">
<input id="unclubbingSuccessMsg" value="<spring:message code='unclubbing.success' text='Unclubbing Successful'></spring:message>" type="hidden">
<input id="unclubbingFailureMsg" value="<spring:message code='unclubbing.failure' text='Unclubbing Failed'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="houseTypeCommon" value="${houseType}" type="hidden">
<input id="houseTypeYearSessionTypeEmptyMsg" value="<spring:message code='client.error.advancedsearch.yeartypeempty' text='Session Year and Session Type must be selected to continue'/>" type="hidden">
<input id="addToFileMsg" type="hidden" value="<spring:message code='standalone.addtofile' text='Add to file' />"/>
<input id="pleaseSelect" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="defaultTitleLanguage" value="${defaultTitleLanguage}" />
<input type="hidden" id="subdepartmentValue" value="<spring:message code='question.department' text='subDepartment'/>" />
<input type="hidden" id="billWithoutNumber" value="<spring:message code='bill.referredBillWithoutNumber' text='Click To See'/>">
<input id="amendedBillInfo" value="${amendedBillInfo}" type="hidden">
<input type="hidden" id="defaultAmendedBillLanguage" value="${defaultAmendedBillLanguage}" />
<input type="hidden" id="useforfiling" value="${useforfiling}" />
<input type="hidden" id="devicesCountLimitAllowedForBulkClubbing" value="${devicesCountLimitAllowedForBulkClubbing}" />
<input type="hidden" id="bulkClubbingConfirmationMsg" value="<spring:message code='clubbing.bulk_clubbing.confirmationMessage' text='Are you sure you want to club selected devices now?' />" />
</body>
</html>