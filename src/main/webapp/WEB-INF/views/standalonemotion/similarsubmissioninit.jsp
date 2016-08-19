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
			
			
			$("#searchrefvalue").change(function(){				
				start=0;				
				$("#referencingResult").empty();	
				$("#searchTable tbody").empty();		
				$("#referencingDiv").hide();
				previousSearchCount=record;
			});		
			
			$("#searchReference").click(function(){
				searchRef();
			});
			
			$("#backToQuestion").click(function(){
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToQuestionDiv").hide();
				if($("#assistantDiv").length>0){
					$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					$("#selectionDiv2").show();					
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			$("#backToMotion").click(function(){
				$("#clubbingResultDiv").hide();
				$("#referencingResultDiv").hide();
				//$("#backToQuestionDiv").hide();
				if($("#assistantDiv").length>0){
					$("#assistantDiv").show();
				}else if($("#chartResultDiv").length>0){
					$("#chartResultDiv").show();
					//$("#selectionDiv2").show();					
				}
				/**** Hide update success/failure message on coming back to question ****/
				if($("#.toolTipe").length>0){
				$(".toolTip").hide();
				}
			});
			
			$("#refSsessionYear").change(function(){
				if($(this).val()=='-'){
					$(this).css('color','black');
				}else{
					$(this).css('color','blue');
				}
			});
			$("#refSessionType").change(function(){
				if($(this).val()=='-'){
					$(this).css('color','black');
				}else{
					$(this).css('color','blue');
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
			$("#reset").click(function(){					
				$("#refSessionYear").prop('selectedIndex',0);
				$("#refSessionType").prop('selectedIndex',0);
				$("#refSessionYear").css('color','black');
				$("#refSessionType").css('color','black');
				
				$("#referencingResult").empty();
				$("#referencingResult").html("");
				$("#searchTable tbody").empty();
				$("#referencingDiv").hide();
				
				start=0;
				previousSearchTerm="";
				previousSearchCount=record;							
			});
				
					
			if($("#searchTable > tbody > tr").length==0) {
				$("#referencingDiv").hide();
			}			
			/* $("#refSessionYear").change(function(){
				if($(this).val()=='-'){
					$(this).css("color","black");
				}else{
					$(this).css("color","blue");
				}
			});
			
			$("#refSessionType").change(function(){
				if($(this).val()=='-'){
					$(this).css("color","black");
				}else{
					$(this).css("color","blue");
				}
			}); */
			
			
			
			/**** primary Question's Details ****/
			$("#primary").click(function(){
				viewDetail($("#questionId").val());
			});
		});
		
		function searchExactReference() {
			start=start+10;	
			if(previousSearchCount==record) {
				$.post('refentity/searchexactbill',{bill:$("#billId").val(),language:$("#languageAllowed").val(),billSessionYear:$("#refSessionYear").val(),billSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
					
					if(data.length>0){								
						var text="";
						for(var i=0;i<data.length;i++){									
							text+="<tr>";
							var billNumber = data[i].number;
							if(billNumber==undefined || billNumber=='') {
								billNumber = "";
							}
							if(data[i].deviceType=='act') {
								text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
								+"<br/><a onclick='viewActDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
								+"</td>";
							} else {
								text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
								+"<br/><a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
								+"</td>";
							}							
							if(data[i].revisedTitle!='null' && data[i].revisedTitle!='' && data[i].revisedTitle!=undefined) {
								text+="<td style='width: 300px; max-width: 300px;'>"+data[i].revisedTitle+"</td>";	
							} else if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
								text+="<td style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";	
							} else {
								text+="<td>&nbsp;</td>";
							}
							var content = "";
							if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
								content = data[i].revisedContent;									
							} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
								content = data[i].content;
							} 				
							text+="<td style='width: 420px; max-width: 420px;'>";
							text+="<div id='contentInShortDiv"+i+"' style='height: 200px; max-height: 200px; overflow: hidden;'>";
							text+=content;
							text+="</div>";
							text+="<div style='text-align: right;'>";
							text+="<a href='#' style='text-decoration: none;' onclick='showFullContent("+i+");'>";
							text+="<img src='./resources/images/ViewRevision.jpg' title='<spring:message code='referencing.viewContentInDetail' text='View Entire Details'></spring:message>' class='imageLink' />";
							text+="</a>";
							text+="</div>";
							text+="</td>";
							if(data[i].deviceType=='act') {
								text+="<td><spring:message code='bill.referredAct' text='Act'/></td>";
							} else {
								text+="<td>"+data[i].deviceType+"</td>";
							}								
							text+="<td>"+data[i].sessionYear+"</td>";			
							text+="<td>"+data[i].sessionType+"</td>";
							text+="<td>"+data[i].status+"</td>";
							text+="<td>"+data[i].dateOfBill+"</td>";
							text+="</tr>";						
						}	
						if(data.length==10){
							text+="<tr>"
								+"<td style='text-align:center;'><a onclick='search();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
								+"</tr>";
							start=start+10;							
						}
						$("#searchTable tbody").empty();
						$("#searchTable tbody").append(text);	
						$("#referencingDiv").show();										
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html($("#noResultsMsg").val());
						$("#referencingDiv").show();
						//$("#searchresult").hide();
					}				
				});
			} else {
				$("#referencingResult").empty();
				$("#referencingResult").html($("#finishedSearchingMsg").val());
				$("#referencingDiv").show();
			}
		}
		
		function searchRef(){
			var toBeSearched=$("#searchrefvalue").val();
			previousSearchTerm=toBeSearched;			
			
			var currentDeviceType = $("#refDeviceType").val();
			if(toBeSearched!=''){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				if((toBeSearched==previousSearchTerm)&&(previousSearchCount==record)){
					if(/*/^questions_/.test(currentDeviceType)*/currentDeviceType.indexOf('questions')==0){
						$.post('refentity/search',{param:$("#searchrefvalue").val(),question:$("#questionId").val(), houseType:$("#houseTypeType").val(), questionSessionYear:$("#refSessionYear").val(),questionSessionType:$("#refSessionType").val(),sessionCount:$("#refSessionCount").val(),record:record,start:start},function(data){
							if(data.length>0){
								var text="";	
								for(var i=0;i<data.length;i++){
									text+="<tr>";
									text+="<td>"+data[i].number+"<span id='operation"+data[i].id+"'><a onclick='";
									var session = ""+data[i].sessionId;
									
									if(session!=$("#currentSession").val().trim()){
										text+="referencing(" + data[i].id + ");' style='margin:10px; color: #00FF00;'>";
									}else{
										if(data[i].ballotStatus=="NOT"){
											text+="javascript:void(0);' style='margin:10px; color: #FF0000;' disabled='disabled'>";
										}else{
											text+="referencing(" + data[i].id + ");' style='margin:10px; color: #00FF00;'>";
										}
									}
									text+=$("#referMsg").val()+"</a></span>"
									+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
									+"</td>";
									text+="<td>"+data[i].subject+"</td>";			
									text+="<td>"+data[i].questionText+"</td>";
									text+="<td>"+data[i].deviceType+", " + data[i].sessionYear+", "+data[i].sessionType +
											"<br>"+ data[i].status + ", " + data[i].subDepartment + ", ";
											
									if(data[i].deviceTypeType=='questions_starred'){
										text+=data[i].chartAnsweringDate;
									}else if(data[i].deviceTypeType=='questions_unstarred'){
										text+=data[i].yaadiDate+", "+data[i].yaadiNumber;
									}
									text+="</td></tr>";		

								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><a onclick='searchRef();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
										+"</tr>";
									start=start+10;							
								}
								
								$("#referencingResult").empty();		
								if($("#searchTable > #searchresultbody:last > tr:last").text().trim()=="Show More"){
									$("#searchTable > #searchresultbody:last > tr:last").remove();
								}
								$("#searchTable > #searchresultbody:last").append(text);	
								$("#searchresult").show();	
								$("#referencingDiv").show();	
								
								$.unblockUI();
							}else{
								$("#referencingResult").empty();
								$("#referencingResult").html($("#noResultsMsg").val());
								$("#referencingDiv").show();
								$.unblockUI();
							}				
						}).fail(function(){
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					$.unblockUI();
	    					scrollTop();
	    				});	
					}else if(currentDeviceType.indexOf("motions_")==0){
						if(currentDeviceType.indexOf("motions_standalonemotion_")==0){
							$.post('refentity/searchhds',{param:$("#searchrefvalue").val(),motion:$("#motionId").val(), houseType:$("#houseTypeType").val(), motionSessionYear:$("#refSessionYear").val(),motionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
								if(data.length>0){
									var text="";	
									for(var i=0;i<data.length;i++){
										text+="<tr>";
										text+="<td>"+data[i].number+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
										text+="<td>"+data[i].subject+"</td>";			
										text+="<td>"+data[i].questionText+"</td>";			
										text+="<td>"+data[i].deviceType+"</td>";			
										text+="<td>"+data[i].sessionYear+"</td>";			
										text+="<td>"+data[i].sessionType+"</td>";			
										text+="<td>"+data[i].status+"</td>";
										text+="</tr>";						
									}	
									if(data.length==10){
										text+="<tr>"
											+"<td style='text-align:center;'><a onclick='searchRef();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
											+"</tr>";
										start=start+10;							
									}
									$("#searchTable tbody").empty();
									$("#searchTable tbody").append(text);	
									$("#referencingDiv").show();	
									$.unblockUI();
								}else{
									$("#referencingResult").empty();
									$("#referencingResult").html($("#noResultsMsg").val());
									$("#referencingDiv").show();
									$.unblockUI();
								}				
							}).fail(function(){
		    					if($("#ErrorMsg").val()!=''){
		    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    					}else{
		    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    					}
		    					$.unblockUI();
		    					scrollTop();
		    				});
						}else{
							$.post('refentity/searchmotion',{param:$("#searchrefvalue").val(),motion:$("#motionId").val(), houseType:$("#houseType").val(), motionSessionYear:$("#refSessionYear").val(),motionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
								if(data.length>0){
									var text="";	
									for(var i=0;i<data.length;i++){
										text+="<tr>";
										text+="<td>"+data[i].number+"<br><span id='operation"+data[i].id+"'><a onclick='referencingMotion("+data[i].id+","+data[i].device+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<br><a onclick='viewDetailsMotion("+data[i].id+"," +data[i].device + ");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
										text+="<td>"+data[i].title+"</td>";			
										text+="<td>"+data[i].noticeContent+"</td>";			
										text+="<td>"+data[i].deviceType+"</td>";			
										text+="<td>"+data[i].sessionYear+"</td>";			
										text+="<td>"+data[i].sessionType+"</td>";			
										text+="<td>"+data[i].status+"</td>";
										text+="</tr>";						
									}	
									if(data.length==10){
										text+="<tr>"
											+"<td style='text-align:center;'><a onclick='searchRef();' style='margin:10px;' class=''>"+$("#loadMoreMsg").val()+"</a></td>"
											+"</tr>";
										start=start+10;							
									}
									/* $("#searchTable tbody").empty();
									$("#searchTable tbody").append(text);	
									$("#referencingDiv").show(); */	
									
									$("#referencingResult").empty();		
									if($("#searchTable > #searchresultbody:last > tr:last").text().trim()=="Show More"){
										$("#searchTable > #searchresultbody:last > tr:last").remove();
									}
									$("#searchTable > #searchresultbody:last").append(text);	
									$("#searchresult").show();	
									$("#referencingDiv").show();
									
									$.unblockUI();
								}else{
									$("#referencingResult").empty();
									$("#referencingResult").html($("#noResultsMsg").val());
									$("#searchResult").hide();
									$("#referencingDiv").show();
									$.unblockUI();
								}				
							}).fail(function(){
		    					if($("#ErrorMsg").val()!=''){
		    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
		    					}else{
		    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
		    					}
		    					$.unblockUI();
		    					scrollTop();
		    				});
						}							
					}else if(/*/^resolutions_/.test(currentDeviceType)*/currentDeviceType.indexOf('resolutions')>-1){
						$.post('refentity/searchresolution',{param:$("#searchrefvalue").val(),resolution:$("#resolutionId").val(), resolutionSessionYear:$("#refSessionYear").val(),resolutionSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							
							if(data.length>0){
								var text="";
								for(var i=0;i<data.length;i++){
									text+="<tr>";
									text+="<td>"+data[i].number+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
									+"<a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
									+"</td>";
									text+="<td>"+data[i].subject+"</td>";			
									text+="<td>"+data[i].noticeContent+"</td>";			
									text+="<td>"+data[i].deviceType+"</td>";	
									text+="<td>"+data[i].sessionYear+"</td>";			
									text+="<td>"+data[i].sessionType+"</td>";
									text+="<td>"+data[i].status+"</td>";
									text+="</tr>";						
								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><a onclick='searchRef();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
										+"</tr>";
									start=start+10;							
								}
								$("#searchTable tbody").empty();
								$("#searchTable tbody").append(text);	
								$("#referencingDiv").show();	
								$.unblockUI();
							}else{
								$("#referencingResult").empty();
								$("#referencingResult").html($("#noResultsMsg").val());
								$("#referencingDiv").show();
								$("#searchresult").hide();
								$.unblockUI();
							}				
						}).fail(function(){
	    					if($("#ErrorMsg").val()!=''){
	    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
	    					}else{
	    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
	    					}
	    					$.unblockUI();
	    					scrollTop();
	    				});
						
					}else if(/^bills_/.test(currentDeviceType)){
						$.post('refentity/searchbill',{param:$("#searchrefvalue").val(),bill:$("#billId").val(),language:$("#languageAllowed").val(),billSessionYear:$("#refSessionYear").val(),billSessionType:$("#refSessionType").val(),record:record,start:start},function(data){
							
							if(data.length>0){								
								var text="";
								for(var i=0;i<data.length;i++){									
									text+="<tr>";
									var billNumber = data[i].number;
									if(billNumber==undefined || billNumber=='') {
										billNumber = "";
									}
									if(data[i].deviceType=='act') {
										text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<a onclick='viewActDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
									} else {
										text+="<td>"+billNumber+"<span id='operation"+data[i].id+"'><a onclick='referencing("+data[i].id+");' style='margin:10px;'>"+$("#referMsg").val()+"</a></span>"
										+"<br/><a onclick='viewDetail("+data[i].id+");' style='margin:10px;'>"+$("#viewDetailMsg").val()+"</a>"
										+"</td>";
									}										
									if(data[i].revisedTitle!='null' && data[i].revisedTitle!='' && data[i].revisedTitle!=undefined) {
										text+="<td style='width: 300px; max-width: 300px;'>"+data[i].revisedTitle+"</td>";	
									} else if(data[i].title!='null' && data[i].title!='' && data[i].title!=undefined) {
										text+="<td style='width: 300px; max-width: 300px;'>"+data[i].title+"</td>";	
									} else {
										text+="<td>&nbsp;</td>";
									}									
									var content = "";
									if(data[i].revisedContent!='null' && data[i].revisedContent!='' && data[i].revisedContent!=undefined) {
										content = data[i].revisedContent;									
									} else if(data[i].content!='null' && data[i].content!='' && data[i].content!=undefined) {
										content = data[i].content;
									} else {
										content = "&nbsp;";
									}				
									text+="<td style='width: 420px; max-width: 420px;'>";
									text+="<div id='contentInShortDiv"+i+"' style='height: 200px; max-height: 200px; overflow: hidden;'>";
									text+=content;
									text+="</div>";
									text+="<div style='text-align: right;'>";
									text+="<a href='#' style='text-decoration: none;' onclick='showFullContent("+i+");'>";
									text+="<img src='./resources/images/ViewRevision.jpg' title='<spring:message code='referencing.viewContentInDetail' text='View Entire Details'></spring:message>' class='imageLink' />";
									text+="</a>";
									text+="</div>";
									text+="</td>";
									if(data[i].deviceType=='act') {
										text+="<td><spring:message code='bill.referredAct' text='Act'/></td>";
									} else {
										text+="<td>"+data[i].deviceType+"</td>";
									}		
									if(data[i].sessionYear!=null) {
										text+="<td>"+data[i].sessionYear+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].sessionType!=null) {
										text+="<td>"+data[i].sessionType+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].status!=null) {
										text+="<td>"+data[i].status+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}
									if(data[i].dateOfBill!=null) {
										text+="<td>"+data[i].dateOfBill+"</td>";
									}else {
										text+="<td>&nbsp;</td>";
									}								
									text+="</tr>";						
								}	
								if(data.length==10){
									text+="<tr>"
										+"<td style='text-align:center;'><a onclick='searchRef();' style='margin:10px;'>"+$("#loadMoreMsg").val()+"</a></td>"
										+"</tr>";
									start=start+10;							
								}								
								$("#searchTable tbody").empty();
								$("#searchTable tbody").append(text);	
								$("#referencingDiv").show();	
								$.unblockUI();
							}else{
								$("#referencingResult").empty();
								$("#referencingResult").html($("#noResultsMsg").val());
								$("#referencingDiv").show();
								//$("#searchresult").hide();
								$.unblockUI();
							}				
						});
					}
				}else{
					$("#referencingResult").empty();
					$("#referencingResult").html($("#finishedSearchingMsg").val());
					$("#referencingDiv").show();
					$.unblockUI();
				}	
			}else{
				$.prompt($("#nothingToSearchMsg").val());
				$("#referencingDiv").hide();
			}
		}
		function referencing(referId,isAct){
			var whichDevice= $('#whichDevice').val();
			var device;
			if(isAct=='true') {
				device = "act";
			} else {
				device=$("#refDeviceType").val();
			}
			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
			}else if(whichDevice=='motions_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_standalonemotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_cutmotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_eventmotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_discussion_'){
				deviceId=$("#motionId").val();					
			}
			
			$.post('refentity/referencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
					if(data=='SUCCESS'){
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}
		function dereferencing(referId,isAct){
			var whichDevice= $('#whichDevice').val();
			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
			}else if(whichDevice=='motions_'){
				deviceId=$("#motionId").val();
			}
			var device;
			if(isAct=='true') {
				device = "act";
			} else {
				device=$("#refDeviceType").val();
			}
			$.post('refentity/dereferencing?pId='+deviceId+"&rId="+referId+"&device="+device,function(data){
				if(data=='SUCCESS'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).html("<a onclick='referencing("+referId+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencing("+referId+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}				
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}	
		
		function showFullContent(indexOfContent) {		
			$.fancybox.open($('#contentInShortDiv'+indexOfContent).html(),{autoSize:false,width:800,height:600});
		}

		function viewDetailsMotion(referId, targetDevice){
			
			/*^questions_/.test(device)){*/
				
			var resourceURL="";
			var device = $("#refAllDevices option[value='"+ targetDevice +"']").text().trim();
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+targetDevice
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#usergroupType").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			console.log(device+"\n"+parameters+"\n"+targetDevice);
			if(device.indexOf("questions_")==0){
				resourceURL='question/'+referId+'/edit?'+parameters+'&questionType='+device;
			}else if(device.indexOf("resolutions")==0){
				resourceURL='resolution/'+referId+'/edit?'+parameters;
			}else if(device.indexOf("bills_")==0){
				resourceURL='bill/'+referId+'/edit?'+parameters;
			}else if(device.indexOf("motions_")==0){				
				if(device.indexOf("motions_standalonemotion_")==0){
					resourceURL='standalonemotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_cutmotion_")==0){
					resourceURL='cutmotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_eventmotion_")==0){
					resourceURL='eventmotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_discussion_")==0){
					resourceURL='discussionmotion/'+referId+'/edit?'+parameters;
				}else{
					resourceURL='motion/'+referId+'/edit?'+parameters;
				}
			}
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				if($("#whichDevice").val()=='questions_'){
					$("#viewQuestion").html(data);
					$("#viewQuestionDiv").show();
				}else if($("#whichDevice").val()=='resolutions_'){
					$("#viewResolution").html(data);
					$("#viewResolutionDiv").show();
				}else if($("#whichDevice").val()=='bills_'){
					$("#viewBill").html(data);
					$("#viewBillDiv").show();
				}else if($("#whichDevice").val()=='motions_'
						|| $("#whichDevice").val()=='motions_standalonemotion_'
						|| $("#whichDevice").val()=='motions_cutmotion_'
						|| $("#whichDevice").val()=='motions_eventmotion_'
						|| $("#whichDevice").val()=='motions_discussion_'){
					$("#viewMotion").html(data);
					$("#viewMotionDiv").show();
				}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});	
		}
		
		function referencingMotion(referId,targetDevice){
			var whichDevice= $('#whichDevice').val();
			var device= $("#refDeviceType").val();			

			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
				parentDeviceModule = ""
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
			}else if(whichDevice=='motions_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_standalonemotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_cutmotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_eventmotion_'){
				deviceId=$("#motionId").val();					
			}else if(whichDevice=='motions_discussion_'){
				deviceId=$("#motionId").val();					
			}
			
			$.post('refentity/referencing?pId='+deviceId+"&rId="+referId+"&device="+device+"&targetDevice="+targetDevice,function(data){
					if(data=='SUCCESS'){
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencingMotion("+referId+","+ targetDevice+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='referencingMotion("+referId+","+ targetDevice+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}
		function dereferencingMotion(referId,targetDevice){
			var whichDevice= $('#whichDevice').val();
			var deviceId = "";
			if(whichDevice=='questions_'){
				deviceId=$("#questionId").val();
			}else if(whichDevice=='resolutions_'){
				deviceId=$("#resolutionId").val();
			}else if(whichDevice=='bills_'){
				deviceId=$("#billId").val();
			}else if(whichDevice=='motions_'){
				deviceId=$("#motionId").val();
			}
			
			var device = $("#refDeviceType"); 
			var targetDevice = $("#allDevices option[value=" + targetDevice + "]").text().trim();
			
			$.post('refentity/dereferencing?pId='+deviceId+"&rId="+referId+"&device="+device+"&targetDevice="+targetDevice,function(data){
				if(data=='SUCCESS'){
					$("#referencingResult").empty();
					$("#referencingResult").html(data);
					$("#operation"+referId).empty();
					$("#operation"+referId).html("<a onclick='referencingMotion("+referId+","+ targetDevice+");' style='margin:10px;'>"+$("#referMsg").val()+"</a>");
					}else{
						$("#referencingResult").empty();
						$("#referencingResult").html(data);
						$("#operation"+referId).empty();
						$("#operation"+referId).html("<a onclick='dereferencingMotion("+referId+","+ targetDevice+");' style='margin:10px;'>"+$("#dereferMsg").val()+"</a>");
					}				
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
			return false;
		}	
		
		function viewDetail(referId){
			var resourceURL="";
			var device=$("#refDeviceType").val();
			var parameters="houseType="+$("#selectedHouseType").val()
			+"&sessionYear="+$("#selectedSessionYear").val()
			+"&sessionType="+$("#selectedSessionType").val()
			+"&deviceType="+$("#refDeviceType").val()
			+"&usergroup="+$("#currentusergroup").val()
			+"&usergroupType="+$("#usergroupType").val()
			+"&status="+$("#selectedStatus").val()
			+"&role="+$("#srole").val()
			+"&edit=false";
			if(device.indexOf("questions_")==0){/*^questions_/.test(device)){*/
				resourceURL='question/'+referId+'/edit?'+parameters+'&questionType='+$("#refDeviceType").val();
			}else if(device.indexOf("resolutions")==0){
				resourceURL='resolution/'+referId+'/edit?'+parameters;
			}else if(device.indexOf("bills_")==0){
				resourceURL='bill/'+referId+'/edit?'+parameters;
			}else if(device.indexOf("motions_")==0){
				if(device.indexOf("motions_standalonemotion_")==0){
					resourceURL='standalonemotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_cutmotion_")==0){
					resourceURL='cutmotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_eventmotion_")==0){
					resourceURL='eventmotion/'+referId+'/edit?'+parameters;
				}else if(device.indexOf("motions_discussion_")==0){
					resourceURL='discussionmotion/'+referId+'/edit?'+parameters;
				}else{
					resourceURL='motion/'+referId+'/edit?'+parameters;
				}
			}
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				if($("#whichDevice").val()=='questions_'){
					$("#viewQuestion").html(data);
					$("#viewQuestionDiv").show();
				}else if($("#whichDevice").val()=='resolutions_'){
					$("#viewResolution").html(data);
					$("#viewResolutionDiv").show();
				}else if($("#whichDevice").val()=='bills_'){
					$("#viewBill").html(data);
					$("#viewBillDiv").show();
				}else if($("#whichDevice").val()=='motions_'
						|| $("#whichDevice").val()=='motions_standalonemotion_'
						|| $("#whichDevice").val()=='motions_cutmotion_'
						|| $("#whichDevice").val()=='motions_eventmotion_'
						|| $("#whichDevice").val()=='motions_discussion_'){
					$("#viewMotion").html(data);
					$("#viewMotionDiv").show();
				}
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});		
		}
		
		/****To view Act details****/
		function viewActDetail(referId){
			var resourceURL='act/'+referId+'/edit?edit=false;';			
			$.get(resourceURL,function(data){
				$("#referencingDiv").hide();		
				$("#viewBill").html(data);
				$("#viewBillDiv").show();
			},'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});			
		}

		function back(){
			$("#referencingDiv").show();		
			$("#referencingResult").empty();
			if($("#whichDevice").val()=='resolutions_'){
				$("#viewResolution").empty();
				$("#viewResolutionDiv").hide();
			}else if($("#whichDevice").val()=='questions_'){
				$("#viewQuestion").empty();
				$("#viewQuestionDiv").hide();
			}else if($("#whichDevice").val()=='bills_'){
				$("#viewBill").empty();
				$("#viewBillDiv").hide();
			}else if($("#whichDevice").val()=='motions_'
				|| $("#whichDevice").val()=='motions_standalonemotion_'
				|| $("#whichDevice").val()=='motions_cutmotion_'
				|| $("#whichDevice").val()=='motions_eventmotion_'
				|| $("#whichDevice").val()=='motions_discussion_'){
				$("#viewMotion").empty();
				$("#viewMotionDiv").hide();
			}
		}
		
		
		/**** load actors(Dynamically Change Actors-Actor Will be selected
		once and it will be set for all selected questions) ****/
		function loadActors(value){
			var devices = $("#similardevice").val();
			var motion="";
			if(devices !=''){
				motion = devices.split(",")[0];	
			}	
			
			if(question!=undefined&&question!=''){
				var params="question="+question+"&status="+value+
				"&usergroup="+$("#assiusergroup").val()+"&level=1";
				var resourceURL='ref/standalonemotion/actors?'+params;				
				$.post(resourceURL,function(data){
					if(data!=undefined||data!=null||data!=''){
						var length=data.length;
						$("#assiactor").empty();
						var text="";
						for(var i=0;i<data.length;i++){
							if(i!=0){
							text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
							}else{
								text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
							}
						}
						text+="<option value='-'>----"+$("#pleaseSelectMessage").val()+"----</option>";
						$("#assiactor").html(text);
						$("#actorDiv").show();								
					}else{
						$("#assiactor").empty();
						$("#actorDiv").hide();	
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
				$("#nextactor").empty();
				$("#actorDiv").hide();			
			}	
		}
		
		function bulkPutUpUpdate(){
			var next="";
			var level="";
			if($("#assiactor").val()!=null&&$("#assiactor").val()=='-'){
				$.prompt($("#selectActorMsg").val());
				return false;
			}else if($("#assiactor").val()!=null&&$("#assiactor").val()!="-"){
				var temp=$("#assiactor").val().split("#");
				next=temp[1];	
				level=temp[2];			
			}	
			var items=new Array();
			$(".action").each(function(){
				if($(this).is(":checked")){
				items.push($(this).attr("id").split("chk")[1]);
				}
			});		
			if(items.length<=0){
				$.prompt($("#selectItemsMsg").val());
				return false;	
			}	
			var status=$("#assiInternalStatus").val();
			$.prompt($('#submissionMsg').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post('standalonemotion/bulksubmission/assistant/update?actor='+next+"&level="+level,
				        	{items:items
				        	,assistatus:status
				        	,status:$("#internalStatus").val()
				        	,houseType:$("#assihouseType").val()
				   		 	,sessionYear:$("#assisessionYear").val()
						 	,sessionType:$("#assisessionType").val()
						 	,deviceType:$("#assiquestionType").val()
						 	,role:$("#assirole").val()
						 	,usergroup:$("#assiusergroup").val()
						 	,usergroupType:$("#assiusergroupType").val()
						 	,file:$("#assifile").val()
						 	,itemscount:+$("#assiitemscount").val()
						 	,group:+$("#assigroup").val()
						 	},
		    	            function(data){
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	
		    					$("#bulkResultDiv").empty();	
		    					$("#bulkResultDiv").html(data);	
		    	            }
		    	            ,'html').fail(function(){
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
	</script>
	<style type="text/css">
	#testingview:hover{
		cursor: pointer;
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
.bold{
font-weight: bold;
}
.expand{
}
#searchTable a{
text-decoration: underline;
color: green;
cursor: hand;
cursor:pointer;
}
</style>
</head>
<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div id="searchBoxDiv">	
	<p>
		<a href="javascript:void(0);">
			<spring:message code="advancedsearch.sessioncount" text="Session Count"/>
		</a>	
		<select name="sessionCount" id="refSessionCount" style="width:100px;height: 25px;">				
			<option value="0" selected="selected"><spring:message code="please.select" text="Please Select"></spring:message></option>			
			<c:forEach var="i" items="${sessionCount}">
				<option value="${i.id}" ><c:out value="${i.name}"></c:out></option>
			</c:forEach> 
		</select>|
		
		<a href="#">
			<spring:message code="advancedsearch.sessionyear" text="Session Year"/>
		</a>	
		<select name="sessionYear" id="refSessionYear" style="width:100px;height: 25px;">				
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
			
		<a href="#">
			<spring:message code="advancedsearch.sessionType" text="Session Type"/>
		</a>			
		<select name="sessionType" id="refSessionType" style="width:100px;height: 25px;">				
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
	</p>
	<table cellpadding="0px" cellspacing="0px">
		<tr> 
			<td style="border-style:solid none solid solid;border-color:#4B7B9F;border-width:1px;">
				<input type="text" name="zoom_query" id="searchrefvalue" style="width:660px; border:0px solid; height:17px; padding:0px 3px; position:relative;"> 
			</td>
			<td style="border-style:solid;border-color:#4B7B9F;border-width:1px;cursor: pointer;"> 
				<input type="button" id="searchReference" value="" style="border-style: none; background: url('/els/resources/images/searchbutton3.gif') no-repeat; width: 24px; height: 20px;">
			</td>
			<td>
				<a href="#" id="reset" style="margin-left: 10px;margin-right: 10px;"><spring:message code="referencing.reset" text="Reset Filters"></spring:message></a>				
			</td>
		</tr>
	</table>
</div>

<p id="referencingP">
	<label class="small"><spring:message code="motion.putupfor" text="Put up for"/></label>	
		<select id="internalStatus" class="sSelect">
		<option value="-"><spring:message code='please.select' text='Please Select'/></option>	
		<c:forEach items="${internalStatuses}" var="i">
					<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
		</c:forEach>
		</select>
		<span id="actorDiv" style="margin: 10px;display: none;">
			<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
			<select id="nextactor" class="sSelect"></select>
		</span>	
		<input type="button" id="bulksubmit" value="<spring:message code='generic.submit' text='Submit'/>"  style="width: 100px;margin: 10px;"/>
</p>

<div id="referencingDiv">
	<div id="refResult"></div>
	<div id="referencingResult" style="margin: 10px;">
	</div>
	
	<div id="searchresult">
		<table style="border: 2px;"  id="searchTable" class="strippedTable" width="100%">
		<thead>
			<tr>
				<th><spring:message code="referencing.number" text="Device Number"></spring:message></th>
				<th><spring:message code="referencing.motion" text="Detail"></spring:message></th>
				<th><spring:message code="referencing.devicetype" text="Device Type"></spring:message></th>
				<th><spring:message code="referencing.sessionyear" text="Session Year"></spring:message></th>
				<th><spring:message code="referencing.sessiontype" text="Session Type"></spring:message></th>
				<th><spring:message code="referencing.status" text="Status"></spring:message></th>
			</tr>
		</thead>
		<tbody id="searchresultbody">
		</tbody>
		</table>
	</div>
</div>

<c:choose>
	<c:when test="${whichDevice=='motions_standalonemotion_'}" >
		<div id="viewMotionDiv" style="display:none;">
			<a id="backToSearch" href="#" style="display:block;"><spring:message code="referencing.back" text="Back to search page"></spring:message></a>
			<div id="viewMotion">
			</div>
		</div>
	</c:when>
</c:choose>
<br />
<br />
<input id="nothingToSearchMsg" value="<spring:message code='referencing.nothingtosearch' text='Search Field Cannot Be Empty'></spring:message>" type="hidden">
<input id="noResultsMsg" value="<spring:message code='referencing.noresults' text='Search Returned No Results'></spring:message>" type="hidden">
<input id="viewDetailMsg" value="<spring:message code='referencing.viewdetail' text='View'></spring:message>" type="hidden">
<input id="referMsg" value="<spring:message code='referencing.referencing' text='Referencing'></spring:message>" type="hidden">
<input id="dereferMsg" value="<spring:message code='referencing.dereferencing' text='Dereferencing'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='clubbing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='clubbing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="loadMoreMsg" value="<spring:message code='referencing.loadmore' text='Show More'></spring:message>" type="hidden">
<input id="finishedSearchingMsg" value="<spring:message code='referencing.finishedsearching' text='Finished Searching'></spring:message>" type="hidden">
<input id="refUsergroupType" type="hidden" value="${usergroupType}" />
<input id="refUsergroup" type="hidden" value="${usergroup}" />
<input id="refDeviceType" type="hidden" value="${deviceType}" />
<input type="hidden" id="whichDevice" value="${whichDevice}" />
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
<input type="hidden" id="currentSession" value="${currSession}" />
<select id="refAllDevices" style="display: none;">
	<c:forEach items="${allDevices}" var="d">
		<option value="${d.id}">${d.type}</option>
	</c:forEach>	
</select>

<input type="hidden" id="similardevice" value="0" />
</body>
</html>