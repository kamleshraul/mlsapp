<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	/**** detail of clubbed and refernced motions ****/		
	function viewMotionDetail(id,deviceType){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&motionType="+deviceType
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='motion/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
	}	
	/**** detail of clubbed and refernced questions ****/		
	function viewQuestionDetail(id,deviceType){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&questionType="+deviceType
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='question/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
	}
	
	/**** to view the referred resolution ****/
	function viewResolutionDetail(id, deviceType){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&deviceType="+deviceType
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='resolution/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:750,height:700});
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});	
	}	
	/**** Clubbing ****/
	function clubbingInt(id, initType){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="motionId="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val()
			        + ((initType==1)? "":"&useforfiling=yes");		
		$.get('clubentity/init?'+params,function(data){
			$.unblockUI();	
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#clubbingResultDiv").html(data);
			$("#clubbingResultDiv").show();
			$("#referencingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToMotionDiv").show();			
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	/**** Reverse Clubbing ****/
	function reverseClubbing(deviceId){
		console.log("deviceId: "+deviceId);
		$.prompt($('#reverseClubbingPromptMsg').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
	        	$.post('clubentity/reverse_clubbing?deviceId='+deviceId+"&whichDevice=motions_calling_",function(data){
					$.prompt(data,{callback: function(v){ 
									refreshEdit(deviceId);
									}
					});					
				},'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	        	
            }
		}});			
        return false;
	}
	/**** Referencing ****/
	function referencingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="id="+id
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val()
        +"&deviceType="+$("#motionType").val()
        +"&houseType="+$("#selectedHouseType").val();
		$.get('refentity/init?'+params,function(data){
			$.unblockUI();			
			//$.fancybox.open(data,{autoSize:false,width:750,height:700});
			$("#referencingResultDiv").html(data);
			$("#referencingResultDiv").show();
			$("#clubbingResultDiv").hide();
			$("#assistantDiv").hide();
			$("#backToMotionDiv").show();			
		},'html').fail(function(){
			$.unblockUI();
			if($("#ErrorMsg").val()!=''){
				$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
			}else{
				$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
			}
			scrollTop();
		});
	}
	/**** refresh clubbing and referencing ****/
	function refreshEdit(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&motionType="+$("#selectedMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
        +"&usergroupType="+$("#currentusergroupType").val();
		
		var resourceURL='motion/'+id+'/edit?'+parameters;
		$('a').removeClass('selected');
		//id refers to the tab name and it is used just to highlight the selected tab
		$('#'+ id).addClass('selected');
		//tabcontent is the content area where result of the url load will be displayed
		$('.tabContent').load(resourceURL,function(){$.unblockUI();});
		$("#referencingResultDiv").hide();
		$("#clubbingResultDiv").hide();
		$("#assistantDiv").show();
		scrollTop();
					
	}
	/**** load actors ****/
	function loadActors(value){
		if(value!='-'){
		var params="motion="+$("#id").val()+"&status="+value+
		"&usergroup="+$("#usergroup").val()+"&level=1";/*$("#olevel").val();*/
		var resourceURL='ref/motion/actors?'+params;
	    var sendback=$("#internalStatusMaster option[value='motion_recommend_sendback']").text();			
	    var discuss=$("#internalStatusMaster option[value='motion_recommend_discuss']").text();		
		$.post(resourceURL,function(data){
			if(data!=undefined||data!=null||data!=''){
				var length=data.length;
				$("#actor").empty();
				var text="<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>";
				for(var i=0;i<data.length;i++){
					if(i!=0){
						text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
					}else{
						text+="<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
						$("#actorName").val(data[i].id.split("#")[4]);
					}
				}				
				$("#actor").html(text);
				$("#actorDiv").show();				
				/**** in case of sendback and discuss only recommendation status is changed ****/
				if(value!=sendback&&value!=discuss){
					$("#internalStatus").val(value);
				}
				$("#recommendationStatus").val(value);	
				/**** setting level,localizedActorName ****/
				 var actor1=data[0].id;
				 var temp=actor1.split("#");
				 $("#level").val(temp[2]);		    
				 $("#localizedActorName").val(temp[3]+"("+temp[4]+")");					
			}else{
			$("#actor").empty();
			$("#actorDiv").hide();
			/**** in case of sendback and discuss only recommendation status is changed ****/
			if(value!=sendback&&value!=discuss){
			$("#internalStatus").val(value);
			}
		    $("#recommendationStatus").val(value);
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
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}	
	/**** Load Sub Departments ****/
	function loadSubDepartments(ministry){
		$.get('ref/ministry/subdepartments?ministry='+ministry+ '&session='+$('#session').val(),
				function(data){
			$("#subDepartment").empty();
			var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
			if(data.length>0){
			for(var i=0;i<data.length;i++){
				subDepartmentText+="<option value='"+data[i].id+"'>"+data[i].name;
			}
			$("#subDepartment").html(subDepartmentText);			
			}else{
				$("#subDepartment").empty();
				var subDepartmentText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
				$("#subDepartment").html(subDepartmentText);				
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
	/**** Load Clarifications ****/
	function loadClarifications(){
		$.get('ref/clarifications',function(data){
			if(data.length>0){
				var text="";
				for( var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
				}
				$("#clarificationNeededFrom").empty();
				$("#clarificationNeededFrom").html(text);
				$("#clarificationDiv").show();								
			}else{
				$("#clarificationNeededFrom").empty();
				$("#clarificationDiv").hide();
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
	
	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}
	
	$(document).ready(function(){
		
		
		/**** Back To motion ****/
		$("#backToMotion").click(function(){
			$("#clubbingResultDiv").hide();
			$("#referencingResultDiv").hide();
			//$("#backTomotionDiv").hide();
			$("#assistantDiv").show();
			/**** Hide update success/failure message on coming back to motion ****/
			$(".toolTip").hide();
		});
		/**** Ministry Changes ****/
		$("#ministry").change(function(){
			//console.log($("#subDepartment").val());
			if($(this).val()!=''){
				loadSubDepartments($(this).val());
			}else{
				$("#subDepartment").empty();				
				$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");				
			}
		});		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('motion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});								
		/**** Revise subject and text****/
		$("#reviseSubject").click(function(){
			$(".revise1").toggle();
			if($("#revisedSubjectDiv").css("display")=="none"){
				$("#revisedSubject").val("");	
			}else{
				$("#revisedSubject").val($("#subject").val());
			}						
			return false;			
		});	
		$("#reviseDetails").click(function(){
			$(".revise2").toggle();		
			if($("#revisedDetailsDiv").css("display")=="none"){
				$("#revisedDetails").wysiwyg("setContent","");
			}else{
				$("#revisedDetails").wysiwyg("setContent",$("#details").val());				
			}				
			return false;			
		});			
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('motion/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data);
		    });
		    return false;
	    });
	    /**** Contact Details ****/
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("#selectedSupportingMembers").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('motion/members/contacts?members='+members,function(data){
			    $.fancybox.open(data);
		    }).fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
		    return false;
	    });	    
	    /**** Internal Status Changes ****/   
	    $("#changeInternalStatus").change(function(){
		    var value=$(this).val();
		    if(value!='-'){
			    //var statusType=$("#internalStatusMaster option[value='"+value+"']").text();			    
			    loadActors(value);	
			  	$("#submit").attr("disabled","disabled");
			    //$("#startworkflow").removeAttr("disabled");		    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    //$("#startworkflow").attr("disabled","disabled");
			   	$("#submit").removeAttr("disabled");
			}		    
	    });
	    $("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#actorName").val(temp[3]+"("+temp[4]+")");
	    });
	    /**** On page Load ****/
	    //$("#startworkflow").attr("disabled","disabled");
		//$("#submit").removeAttr("disabled");
	    /**** Put Up ****/
		$("#startworkflow").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});			
			$.prompt($('#startWorkflowMessage').val(),{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' }); 			        
		        	$.post($('form').attr('action')+'?operation=startworkflow',  
		    	            $("form").serialize(),  
		    	            function(data){
		       					$('.tabContent').html(data);
		       					$('html').animate({scrollTop:0}, 'slow');
		       				 	$('body').animate({scrollTop:0}, 'slow');	
		    					$.unblockUI();	   				 	   				
		    	            });
    	            }
			}});			
	        return false;  
	    });
	    /**** Right Click Menu ****/
		$(".clubbedRefMotions").contextMenu({
	        menu: 'contextMenuItems'
	    },
	        function(action, el, pos) {
			var id=$(el).attr("id");
			//alert(id+"\n"+$(el).attr('class').split(' ').length);
			var cls = $(el).attr('class').split(' ')[1].substring("cbdevice".length);
			//var targetDevice = $("#allDevices option[value="+cls+"]").text().trim();
			
			if(action=='unclubbing'){
				if(id.indexOf("cq")!=-1){
				var motionId=$("#id").val();
				var clubId=id.split("cq")[1];				
				$.post('clubentity/unclubbing?pId='+motionId+"&cId="+clubId+"&whichDevice=motions_",function(data){
					$.prompt(data,{callback: function(v){ 
									refreshEdit(motionId);
									}
					});					
				},'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	
				}else{
					$.prompt("Unclubbing not allowed");
				}			
			}else if(action=='dereferencing'){
				if(id.indexOf("rq")!=-1){					
				var motionId=$("#id").val();
				var refId=id.split("rq")[1];
				var device = $("#allDevices option[value=" + $("#selectedMotionType").val() + "]").text().trim();
				
				$.post('refentity/dereferencing?pId='+motionId+"&rId="+refId+"&targetDevice="+cls+"&device="+device,function(data){
					if(data=='SUCCESS'){
						$.prompt("Dereferencing Successful",{
							buttons: {Ok:true}, callback: function(v){
						        if(v){
									refreshEdit(motionId);
						        }
							}});				
					}else{
						$.prompt("Dereferencing Failed");
					}							
				},'html').fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});	
				}else{
					$.prompt("Referencing not allowed");					
				}			
			}
	    });			    
	    /**** On Page Load ****/
		//$("#ministry").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		//$("#subDepartment").prepend("<option value=''>----"+$("#pleaseSelectMessage").val()+"----</option>");			
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
	    if($("#revisedDetails").val()!=''){
	    	$("#revisedDetailsDiv").show();
	    }
		/**** On Bulk Edit ****/
		$("#submitBulkEdit").click(function(e){
			//removing <p><br></p>  from wysiwyg editor
			$(".wysiwyg").each(function(){
				var wysiwygVal=$(this).val().trim();
				if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
					$(this).val("");
				}
			});								
			$.post($('form').attr('action'),  
	            $("form").serialize(),  
	            function(data){
   					$('.fancybox-inner').html(data);
   					$('html').animate({scrollTop:0}, 'slow');
   				 	$('body').animate({scrollTop:0}, 'slow');	
	            }).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
	        return false;  
	    });  
		
		
	/*****AutoSuggest Multiple for supporting members******/
		
		var controlName=$(".autosuggestmultiple").attr("id");
		$("select[name='"+controlName+"']").hide();	
		$( ".autosuggestmultiple" ).change(function(){
			//if we are removing a value from autocomplete box then that value needs to be removed from the attached select box also.
			//for this we iterate through the slect box selected value and check if that value is present in the 
			//current value of autocomplete.if a value is found which is there in autocomplete but not in select box
			//then that value will be removed from the select box.
			var value=$(this).val();
			$("select[name='"+controlName+"'] option:selected").each(function(){
				var optionClass=$(this).attr("class");
				if(value.indexOf(optionClass)==-1){
					$("select[name='"+controlName+"'] option[class='"+optionClass+"']").remove();
				}		
			});	
			$("select[name='"+controlName+"']").hide();				
		});
		//http://api.jqueryui.com/autocomplete/#event-select
		$( ".autosuggestmultiple" ).autocomplete({
			minLength:3,
			source: function( request, response ) {
				$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()
						+'&primaryMemberId='+$('#primaryMember').val(), {
					term: extractLast( request.term )
				}, response ).fail(function(){
					$.unblockUI();
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});
			},			
			search: function() {
				var term = extractLast( this.value );
				if ( term.length < 2 ) {
					return false;
				}
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				//what happens when we are selecting a value from drop down
				var terms = $(this).val().split(",");
				//if select box is already present i.e atleast one option is already added
				if($("select[name='"+controlName+"']").length>0){
					if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
					//if option being selected is already present then do nothing
					this.value = $(this).val();					
					$("select[name='"+controlName+"']").hide();						
					}else{
					//if option is not present then add it in select box and autocompletebox
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
					$("select[name='"+controlName+"']").append(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}							
					$("select[name='"+controlName+"']").hide();								
					}
				}else{
					if(ui.item.id!=undefined&&ui.item.value!=undefined){
					text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
					textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
					text=text+textoption+"</select>";
					$(this).after(text);
					terms.pop();
					terms.push( ui.item.value );
					terms.push( "" );
					this.value = terms.join( "," );
					}	
					$("select[name='"+controlName+"']").hide();									
				}		
				return false;
			}
			
			
		});
		
		function removeTags(str) {
		    if ((str===null) || (str===''))
		        return false;
		    else
		        str = str.toString();
		          
		    return str.replace( /(<([^>]+)>)/ig, '');		   
		}
		
		function checkMaxAllowedTextSize(str){
			if(str!==null && str.length!==undefined){
				 str=removeTags(str);
				 if(str!==null && str.length!==undefined && str.length>0){
				 var matches = str.match(/\S+/g);
				 var wordCountLbl=document.getElementById('wordCountLbl');
				 wordCountLbl.innerHTML= matches.length;
					 if(matches.length!==undefined && matches.length !==null ){
						wordCountLbl.style.backgroundColor='lavender';
					 }
				 }
			}
			return true;
		}
			
		$('.hddRevisedDetailsTxt').change(function(event){
			var str=event.target.value;
			checkMaxAllowedTextSize(str);
		});
		
		if($('.hddRevisedDetailsTxt')!==null && $('.hddRevisedDetailsTxt')!==undefined 
				&& $('.hddRevisedDetailsTxt')[0].value!==null){
			checkMaxAllowedTextSize($('.hddRevisedDetailsTxt')[0].value);
		}
		
	});
	</script>
	 <style type="text/css">
	 	#img_reverseClubbing{
			width: 23px;
			height: 23px;		
			/* box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; */ 
		}
		
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${(error!='') && (error!=null)}">
	<h4 style="color: #FF0000;">${error}</h4>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
<form:form action="motion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>
		${formattedMotionType}: ${formattedNumber}
		<c:if test="${not empty discussionDetailsText}">
			&nbsp;&nbsp;(${discussionDetailsText})
		</c:if>
	</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="motion.type" text="Type"/>*</label>
		<input id="formattedMotionType" name="formattedMotionType" value="${formattedMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${motionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
	<label class="small"><spring:message code="motion.number" text="Motion Nmber"/>*</label>
	<c:choose>
		<c:when test="${domain.postBallotNumber != null}">
			<input id="formattedNumber" name="formattedNumber" value="${formattedPostBallotNumber}" class="sText" readonly="readonly">
			<input id="number" name="number" value="${domain.number}" type="hidden">
		</c:when>
		<c:otherwise>
			<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">
			<input id="number" name="number" value="${domain.number}" type="hidden">
		</c:otherwise>
	</c:choose>
	<form:errors path="number" cssClass="validationError"/>	
		<c:if test="${internalStatusType =='motion_recommend_admission' || internalStatusType == 'motion_final_admission'}">
			<label class="small" style="margin-left: 150px;"><spring:message code="motion.sendAdvanceCopy" text="Send Advance Copy"/></label>
			<form:checkbox path="advanceCopySent" cssClass="sCheck"/>
		</c:if>	
	</p>
		
	<p>		
	<label class="small"><spring:message code="motion.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	</p>
	
	<p>
		<label class="small"><spring:message code="motion.ministry" text="Ministry"/>*</label>
		<select name="ministry" id="ministry" class="sSelect">
			<option value=""><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${ministries }" var="i">
				<c:choose>
					<c:when test="${i.id==ministrySelected }">
						<option value="${i.id }" selected="selected">${i.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${i.id }" >${i.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	<form:errors path="ministry" cssClass="validationError"/>
	</p>	
	
	<p>
	<label class="small"><spring:message code="motion.subdepartment" text="Sub Department"/></label>
	<select name="subDepartment" id="subDepartment" class="sSelect">
		<option value=""><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${subDepartments }" var="i">
			<c:choose>
				<c:when test="${i.id==subDepartmentSelected }">
					<option value="${i.id }" selected="selected">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id }" >${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
	<form:errors path="subDepartment" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="centerlabel"><spring:message code="question.members" text="Members"/></label>
		<textarea class="autosuggestmultiple" id="selectedSupportingMembers" rows="2" cols="50">${memberNames}</textarea>
		<c:if test="${!(empty primaryMember)}">
			<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
		</c:if>
		<c:if test="${!(empty supportingMembers)}">
			<select  name="selectedSupportingMembers" multiple="multiple" style="display: none;">
			<c:forEach items="${supportingMembers}" var="i">
			<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
			</c:forEach>		
			</select>
		</c:if>	
		</p>
	
	<p>
		<label class="small"><spring:message code="motion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="/els/resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>			
	
	<p>
		<c:if test="${bulkedit!='yes' and domain.internalStatus.type!='motion_system_clubbed'}">
	
		<a href="#" id="clubbing" onclick="clubbingInt(${domain.id},1);" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="motion.clubbing" text="Clubbing"></spring:message></a>
		<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="motion.referencing" text="Referencing"></spring:message></a>
		<a href="#" id="filing" onclick="clubbingInt(${domain.id}, 2);" style="margin: 20px;"><spring:message code="motion.filing" text="Filing"></spring:message></a>
		<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="motion.refresh" text="Refresh"></spring:message></a>
		</c:if>	
	</p>
	
	<c:if test="${!(empty parent)}">	
	<p>
		<label class="small"><spring:message code="motion.parentmotion" text="Clubbed To"></spring:message></label>
		<a href="#" id="p${parent}" onclick="viewMotionDetail(${parent});" style="font-size: 18px;"><c:out value="${formattedParentNumber}"></c:out></a>
		<a id="reverseClubbing" href="#" onclick="reverseClubbing(${domain.id});" style="text-decoration: none;position: relative;top: 5px;left: 10px;">
			<img id="img_reverseClubbing" src="./resources/images/ico_reverse_clubbing.jpg" title="Reverse Clubbing">
		</a>
		<input type="hidden" id="parent" name="parent" value="${parent}">
	</p>
	</c:if>
		
	<p>
		<label class="small"><spring:message code="motion.clubbedmotions" text="Clubbed Motions"></spring:message></label>
		<c:if test="${!(empty clubbedEntities) }">
			<c:choose>
				<c:when test="${!(empty clubbedEntities) }">
					<c:forEach items="${clubbedEntities }" var="i">
						<a href="#" id="cq${i.number}" class="clubbedRefMotions cbdevice${i.number}" onclick="viewMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>
			<select id="clubbedEntities" name="clubbedEntities" multiple="multiple" style="display:none;">
				<c:forEach items="${clubbedEntities }" var="i">
					<option value="${i.id}" selected="selected"></option>
				</c:forEach>
			</select>
		</c:if>
	</p>
		
			
	<p>
		<label class="small"><spring:message code="motion.referencedMotions" text="Referenced Motions"></spring:message></label>
		<c:if test="${!(empty referencedMotions) }">
			<c:choose>
				<c:when test="${!(empty referencedMotions) }">
					<c:forEach items="${referencedMotions }" var="i">
						<a href="#" id="rq${i.device}" class="clubbedRefMotions cbdevice${i.deviceTypeId}" onclick="viewMotionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>	
		</c:if>	
	</p>
					
	<p>
		<label class="small"><spring:message code="motion.referencedQuestions" text="Referenced Questions"></spring:message></label>
		<c:if test="${!(empty referencedQuestions) }">
			<c:choose>
				<c:when test="${!(empty referencedQuestions) }">
					<c:forEach items="${referencedQuestions }" var="i">
						<a href="#" id="rq${i.device}" class="clubbedRefMotions cbdevice${i.deviceTypeId}" onclick="viewQuestionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>		
		</c:if>	
	</p>
		
	
	<p>
		<label class="small"><spring:message code="motion.referencedResolutions" text="Referenced Resolutions"></spring:message></label>
		<c:if test="${!(empty referencedResolutions) }">
			<c:choose>
				<c:when test="${!(empty referencedResolutions) }">
					<c:forEach items="${referencedResolutions }" var="i">
						<a href="#" id="rq${i.device}" class="clubbedRefMotions cbdevice${i.deviceTypeId}" onclick="viewResolutionDetail(${i.device},${i.deviceTypeId});" style="font-size: 18px;"><c:out value="${i.formatNumber(i.number)}-(${i.sessionTypeName}, ${i.formatNumber(i.sessionYear)})"></c:out></a>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:out value="-"></c:out>
				</c:otherwise>
			</c:choose>
		</c:if>		
	</p>
		
	<c:if test="${!(empty referencedEntities) }">
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedEntities }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</c:if>
	
	<p>	
		<label class="centerlabel"><spring:message code="motion.subject" text="Subject"/></label>
		<form:textarea path="subject" readonly="true" rows="2" cols="50"></form:textarea>
		<form:errors path="subject" cssClass="validationError"/>	
	</p>	
	
	<p>
		<label class="wysiwyglabel"><spring:message code="motion.details" text="Details"/></label>
		<form:textarea path="details" readonly="true" cssClass="wysiwyg"></form:textarea>
		<form:errors path="details" cssClass="validationError"/>	
	</p>	
	
	<p>
		<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="motion.reviseSubject" text="Revise Subject"></spring:message></a>
		<a href="#" id="reviseDetails" style="margin-right: 20px;"><spring:message code="motion.reviseDetails" text="Revise Details"></spring:message></a>
		<a href="#" id="viewRevision"><spring:message code="motion.viewrevisions" text="View Revisions"></spring:message></a>
	</p>	
	
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
		<label class="centerlabel"><spring:message code="motion.revisedSubject" text="Revised Subject"/></label>
		<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
		<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedDetailsDiv">
		<c:if test="${usergroupType == 'assistant' || usergroupType == 'clerk'}">
			<p style="padding-left:17%" id="maxTextLengthPara">
				<span class="wordCountBlk" style="display: inline;font-weight: 600;font-size: 1.13em">
					<spring:message code="max.words.in.text" text="max words"/>
					<label id="wordCountLbl" style="padding:0.6em 1.5em;font-weight: 800;font-size: 1.13em;display:inline-block"> 0 </label>
				</span>
			</p>
		</c:if>
		<label class="wysiwyglabel"><spring:message code="motion.revisedDetails" text="Revised Details"/></label>
		<form:textarea path="revisedDetails" cssClass="wysiwyg hddRevisedDetailsTxt"></form:textarea>
		<form:errors path="revisedDetails" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p id="internalStatusDiv">
		<label class="small"><spring:message code="motion.currentStatus" text="Current Status"/></label>
		<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	<c:if test="${usergroupType == 'assistant' || usergroupType == 'clerk'}">
	
	</c:if>
	<c:if test="${empty parent}">
	<c:if test="${internalStatusType == 'motion_system_assistantprocessed' 
					|| internalStatusType == 'motion_system_putup'
					|| (internalStatusType == 'motion_final_admission' && (usergroupType == 'assistant' || usergroupType == 'clerk'))}">
		<p>	
			<label class="small"><spring:message code="motion.putupfor" text="Put up for"/></label>	
			<select id="changeInternalStatus" class="sSelect">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${internalStatuses}" var="i">
				<c:choose>
						<c:when test="${i.id==internalStatusSelected }">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
						</c:when>
						<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>	
						</c:otherwise>
				</c:choose>
			</c:forEach>
			</select>
			
			<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
			<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
			</select>	
			<form:errors path="internalStatus" cssClass="validationError"/>
		</p>
		
		<p id="actorDiv" style="display: none;">
			<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }"/>
			<input type="text" class="sText" readonly="readonly" value="" id="actorName"/>
		</p>
	</c:if>	
	</c:if>
	
	<p>
		<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
				<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
		</select>	
		<form:errors path="internalStatus" cssClass="validationError"/>	
	</p>
	
	<c:if test="${domain.reply!=null || internalStatusType == 'motion_final_admission'}">
		<p>
			<label class="wysiwyglabel"><spring:message code="motion.nivedan" text="Nivedan"/></label>
			<form:textarea path="reply" cssClass="wysiwyg"></form:textarea>
		</p>
		
		<%-- <p>
			<label class="small"><spring:message code="motion.replyReceivedDate" text="Nivedan Received Date"/></label>
			<form:input path="replyReceivedDate" cssClass="sText datemask"/>
		</p> --%>
	</c:if>
	<c:if test="${domain.factualPositionFromDepartment!=null || (internalStatusType == 'motion_final_clarificationNeededFromDepartment' || internalStatusType == 'motion_final_clarificationNeededFromMemberAndDepartment')}">
		<p>
			<label class="wysiwyglabel"><spring:message code="motion.factualPositionFromDepartment" text="Factual Position from Department"/></label>
			<form:textarea path="factualPositionFromDepartment" cssClass="wysiwyg"></form:textarea>
		</p>
	</c:if>
	<c:if test="${domain.factualPositionFromMember!=null || (internalStatusType == 'motion_final_clarificationNeededFromMember' || internalStatusType == 'motion_final_clarificationNeededFromMemberAndDepartment')}">
		<p>
			<label class="wysiwyglabel"><spring:message code="motion.factualPositionFromMember" text="Factual Position from Member"/></label>
			<form:textarea path="factualPositionFromMember" cssClass="wysiwyg"></form:textarea>
		</p>
	</c:if>
	
	<p>
		<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="motion.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="motion.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
		
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<c:choose>
				
				<c:when test="${internalStatusType=='motion_recommend_admission'
				|| internalStatusType=='motion_recommend_rejection'
				|| internalStatusType=='motion_recommend_clarificationNeededFromDepartment'
				|| internalStatusType=='motion_recommend_clarificationNeededFromMemberAndDepartment' 
				|| fn:contains(internalStatusType,'motion_final')}">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
				</c:when>
								
				<c:when test="${ internalStatusType=='motion_system_assistantprocessed'
								|| internalStatusType == 'motion_system_putup'
								|| internalStatusType == 'motion_submit'
								|| internalStatusType == 'motion_putup_nameclubbing'}">
					<c:choose>
						<c:when test="${bulkedit!='yes'}">
							<c:choose>
								<c:when test="${internalStatusType == 'motion_submit'}">
									<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
									<input id="startworkflow" type="button" value="<spring:message code='motion.putupmotion' text='Put Up Motion'/>" class="butDef" disabled="disabled">
								</c:when>
								<c:otherwise>
									<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
									<input id="startworkflow" type="button" value="<spring:message code='motion.putupmotion' text='Put Up Motion'/>" class="butDef">								
								</c:otherwise>
							</c:choose>							
						</c:when>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:if test="${bulkedit=='yes'}">
						<input id="submitBulkEdit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">	
					</c:if>
				</c:otherwise>
			</c:choose>
		</p>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="locale"/>
	<form:hidden path="version"/>
	<form:hidden path="workflowStarted"/>	
	<form:hidden path="endFlag"/>
	<form:hidden path="level"/>
	<form:hidden path="localizedActorName"/>
	<form:hidden path="workflowDetailsId"/>
	<form:hidden path="file"/>
	<form:hidden path="fileIndex"/>	
	<form:hidden path="fileSent"/>
	<form:hidden path="replyReceivedDate"/>
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">		
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="internalStatusType"  name="internalStatusType" value="${internalStatusType}">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
	<security:authorize access="hasAnyRole('MOIS_CLERK')">
		<input type="hidden" name="dataEntryType" id="dataEntryType" value="offline">
	</security:authorize>
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmMotionSubmission" value="<spring:message code='confirm.motionsubmission.message' text='Do you want to submit the motion.'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='motion.startworkflowmessage' text='Do You Want To Put Up motion'></spring:message>" type="hidden">
<input id="reverseClubbingPromptMsg" value="<spring:message code='motion.reverseClubbingPromptMsg' text='Do you really want to perform reverse clubbing?'/>" type="hidden">
<input id="ministrySelected" value="${ministrySelected }" type="hidden">
<input id="subDepartmentSelected" value="${subDepartmentSelected }" type="hidden">
<input id="oldInternalStatus" value="${ internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministryempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="motionType" type="hidden" value="${selectedMotionType}" />
<input id="olevel" type="hidden" value="${olevel}" />

<select id="allDevices" style="display: none;">
	<c:forEach items="${allDevices}" var="i">
		<option value="${i.id}">${i.type}</option>
	</c:forEach>
</select>

<ul id="contextMenuItems" style="width: 140px; height: 50px;">
	<li><a href="#unclubbing" class="edit"><spring:message code="motion.unclubbing" text="Unclubbing"></spring:message></a></li>
	<li><a href="#dereferencing" class="edit"><spring:message code="motion.dereferencing" text="Dereferencing"></spring:message></a></li>
</ul>
</div>

</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingResultDiv" style="display:none;">
</div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>