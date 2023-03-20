<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="motion" text="Motion Information System"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
	
	function viewDiscussionMotionDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&discussionMotionType="+$("#selectedDiscussionMotionType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='discussionmotion/'+id+'/edit?'+parameters;
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
	/**** load actors ****/
	function loadActors(value){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		if(value!='-'){
			var params="discussionmotion="+$("#id").val()+"&status="+value+
			"&usergroup="+$("#usergroup").val()+"&level="+$("#level").val();
			var resourceURL='ref/discussionmotion/actors?'+params;
		    var sendback=$("#internalStatusMaster option[value='discussionmotion_recommend_sendback']").text();			
		    var discuss=$("#internalStatusMaster option[value='discussionmotion_recommend_discuss']").text();		
			$.get(resourceURL,function(data){
				if(data!=undefined||data!=null||data!=''){
					$("#actor").empty();
					var text="";
					for(var i=0;i<data.length;i++){
					text+="<option value='"+data[i].id+"'>"+data[i].name+"</option>";
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
					 $('#actorName').val(temp[4]);
					 $('#actorName').css('display','inline');
				}else{
					$("#actor").empty();
					$("#actorDiv").hide();
					/**** in case of sendback and discuss only recommendation status is changed ****/
					if(value!=sendback&&value!=discuss){
					$("#internalStatus").val(value);
					}
				    $("#recommendationStatus").val(value);
				}
				$.unblockUI();	
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
			$.unblockUI();	
			$("#actor").empty();
			$("#actorDiv").hide();
			$("#internalStatus").val($("#oldInternalStatus").val());
		    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
		}
	}
	
	

	$(document).ready(function(){
		$("#actor").change(function(){
		    var actor=$(this).val();
		    var temp=actor.split("#");
		    $("#level").val(temp[2]);		    
		    $("#localizedActorName").val(temp[3]+"("+temp[4]+")");
		    $("#actorName").val(temp[4]);
		    $("#actorName").css('display','inline');
	    });
		
		/**** Ministry Changes ****/
		$("#ministries").change(function(){
		
			if($(this).val()!=''){
				loadSubDepartment($(this).val());
			}
		});
		
		/**** Citations ****/
		$("#viewCitation").click(function(){
			$.get('discussionmotion/citations/'+$("#type").val()+ "?status=" + $("#internalStatus").val(),function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html').fail(function(){
				if($("#ErrorMsg").val()!=''){
					$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				}else{
					$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				}
				scrollTop();
			});
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
		
		$("#reviseNoticeContent").click(function(){
			$(".revise2").toggle();		
			if($("#revisedNoticeContentDiv").css("display")=="none"){
				$("#revisedNoticeContent").wysiwyg("setContent","");
			}else{
				$("#revisedNoticeContent").wysiwyg("setContent",$("#noticeContent").val());				
			}				
			return false;			
		});	
		
		$("#reviseBriefExplanation").click(function(){
			$(".revise3").toggle();		
			if($("#revisedBriefExplanationDiv").css("display")=="none"){
				$("#revisedBriefExplanation").wysiwyg("setContent","");
			}else{
				$("#revisedBriefExplanation").wysiwyg("setContent",$("#briefExplanation").val());				
			}				
			return false;			
		});	
		
		/**** Revisions ****/
	    $("#viewRevision").click(function(){
		    $.get('discussionmotion/revisions/'+$("#id").val(),function(data){
			    $.fancybox.open(data,{autoSize: false, width: 670, height:700});
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
	    /**** Contact Details ****/
	    $("#viewContacts").click(function(){
		    var primaryMember=$("#primaryMember").val();
		    var supportingMembers=$("select[name='selectedSupportingMembers']").val();
		    var members=primaryMember;
		    if(supportingMembers!=null){
			    if(supportingMembers!=''){
				    members=members+","+supportingMembers;
			    }
		    }
		    $.get('discussionmotion/members/contacts?members='+members,function(data){
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
			    loadActors(value);	
			    $("#submit").attr("disabled","disabled");
			    $("#startworkflow").removeAttr("disabled");		    
		    }else{
			    $("#actor").empty();
			    $("#actorDiv").hide();
			    $("#internalStatus").val($("#oldInternalStatus").val());
			    $("#recommendationStatus").val($("#oldRecommendationStatus").val());
			    $("#startworkflow").attr("disabled","disabled");
			    $("#submit").removeAttr("disabled");
			}		    
	    });
	    /**** On page Load ****/
	    $("#startworkflow").attr("disabled","disabled");
		$("#submit").removeAttr("disabled");
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
			}});			
	        return false;  
	    });
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
		
		if($("#revisedSubject").val()!=''){
		    $("#revisedSubjectDiv").show();
	    }
	    
		if($("#revisedNoticeContent").val()!=''){
	    	$("#revisedNoticeContentDiv").show();
	    }

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
				$.getJSON( 'ref/member/supportingmembers?session='+$("#session").val()+'&primaryMemberId='+$('#primaryMember').val(), {
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
	  
	});
	
  	function split( val ) {
		return val.split( /,\s*/ );
	}	
	function extractLast( term ) {
		return split( term ).pop();
	}

	function loadSubDepartment(ministry){
		$.get('ref/getSubDeparmentsByMinistries?ministries='+ ministry +'&session='+$('#session').val(),
				function(data){
			if(data.length>0){
				var selectedSubDepartments = $('#subDepartments').val();
				var subDepartmentText='';
				for(var i=0;i<data.length;i++){
					var flag=false;
					if(selectedSubDepartments!=null && selectedSubDepartments!=''){
						for(var j=0;j<selectedSubDepartments.length;j++){
							if(selectedSubDepartments[j]==data[i].id){
								flag=true;
								break;
							}
						}
					}
					if(flag){
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}else{
						subDepartmentText = subDepartmentText+ "<option value='"+data[i].id+"' selected='selected'>"+data[i].name+"</option>";
					}
				}
				$('#subDepartments').html(subDepartmentText);
			}
		});
	}
	
	/**** Clubbing ****/
	function clubbingInt(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		var params="discussionMotionId="+id
					+"&usergroup="+$("#currentusergroup").val()
			        +"&usergroupType="+$("#currentusergroupType").val();	
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
	</script>
	 <style type="text/css">
        @media print {
            .tabs,#selectionDiv1,#selectionDiv2,title,#pannelDash,.menu{
            display:none;
            }
        }
        
     </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<c:if test="${error!=''}">
	<h3 style="color: #FF0000;">${error}</h3>
</c:if>
<div class="fields clearfix watermark">

<div id="assistantDiv">
<form:form action="discussionmotion" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2>${formattedDiscussionMotionType}: ${formattedNumber}</h2>
	<form:errors path="version" cssClass="validationError"/>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.houseType" text="House Type"/>*</label>
		<input id="formattedHouseType" name="formattedHouseType" value="${formattedHouseType}" class="sText" readonly="readonly">
		<input id="houseType" name="houseType" value="${houseType}" type="hidden">
		<form:errors path="houseType" cssClass="validationError"/>			
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.year" text="Year"/>*</label>
		<input id="formattedSessionYear" name="formattedSessionYear" value="${formattedSessionYear}" class="sText" readonly="readonly">
		<input id="sessionYear" name="sessionYear" value="${sessionYear}" type="hidden">
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.sessionType" text="Session Type"/>*</label>		
		<input id="formattedSessionType" name="formattedSessionType" value="${formattedSessionType}" class="sText" readonly="readonly">
		<input id="sessionType" name="sessionType" value="${sessionType}" type="hidden">		
		<input type="hidden" id="session" name="session" value="${session}"/>
		<form:errors path="session" cssClass="validationError"/>	
	</p>
	
	<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.type" text="Type"/>*</label>
		<input id="formattedDiscussionMotionType" name="formattedDiscussionMotionType" value="${formattedDiscussionMotionType}" class="sText" readonly="readonly">
		<input id="type" name="type" value="${discussionMotionType}" type="hidden">		
		<form:errors path="type" cssClass="validationError"/>		
	</p>	
	
	<p>
		<label class="small"><spring:message code="discussionmotion.number" text="Motion Number"/>*</label>
		<input id="formattedNumber" name="formattedNumber" value="${formattedNumber}" class="sText" readonly="readonly">		
		<input id="number" name="number" value="${domain.number}" type="hidden">
		<form:errors path="number" cssClass="validationError"/>

		<p style="display:none;">
		<label class="small"><spring:message code="discussionmotion.priority" text="Priority"/>*</label>
		<input name="formattedPriority" id="formattedPriority" class="sText" type="text" value="${formattedPriority }" readonly="readonly">
		<input name="priority" id="priority"  type="hidden" value="${priority }">	
		<form:errors path="priority" cssClass="validationError"/>
		</p>
	</p>
		
	<p>		
	<label class="small"><spring:message code="discussionmotion.submissionDate" text="Submitted On"/></label>
	<input id="formattedSubmissionDate" name="formattedSubmissionDate" value="${formattedSubmissionDate }" class="sText" readonly="readonly">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
		
	
		<c:if test="${(internalStatusType=='discussionmotion_final_admission')}">
		<label class="small"><spring:message code="discussionmotion.discussionDate" text="Discussion Date"/></label>
		
		<input id="formattedDiscussionDate" name="formattedDiscussionDate" value="${formattedDiscussionDate}" class="datemask sText" />
		<input id="setDiscussionDate" name="discussionDate" value="${discussionDate}" class="sText" type="hidden" />
		
	</c:if>
	</p>

	<br>	
	<p>
	<label class="centerlabel"><spring:message code="discussionmotion.members" text="Members"/></label>
	<textarea id="selectedSupportingMembers" class="autosuggestmultiple"  rows="2" cols="50" readonly="readonly">${memberNames}</textarea>
	<c:if test="${!(empty primaryMember)}">
		<input id="primaryMember" name="primaryMember" value="${primaryMember}" type="hidden">
	</c:if>
	<c:if test="${!(empty supportingMembers)}">
		<select  name="selectedSupportingMembers" multiple="multiple" id="selectedSupportingMembers">
		<c:forEach items="${supportingMembers}" var="i">
		<option value="${i.id}" class="${i.getFullname()}" selected="selected"></option>
		</c:forEach>		
		</select>
	</c:if>	
	</p>
		
	<p>
		<label class="small"><spring:message code="discussionmotion.primaryMemberConstituency" text="Constituency"/>*</label>
		<input type="text" readonly="readonly" value="${constituency}" class="sText">
		<a href="#" id="viewContacts" style="margin-left:20px;margin-right: 20px;"><img src="./resources/images/contactus.jpg" width="40" height="25"></a>		
	</p>
	<!-- 
	<p>
		<c:if test="${bulkedit!='yes'}">	
			<a href="#" id="clubbing" onclick="clubbingInt(${domain.id});" style="margin-left: 162px;margin-right: 20px;margin-bottom: 20px;margin-top: 20px;"><spring:message code="generic.clubbing" text="Clubbing"></spring:message></a>
			<a href="#" id="referencing" onclick="referencingInt(${domain.id});" style="margin: 20px;"><spring:message code="generic.referencing" text="Referencing"></spring:message></a>
			<a href="#" id="refresh" onclick="refreshEdit(${domain.id});" style="margin: 20px;"><spring:message code="generic.refresh" text="Refresh"></spring:message></a>
		</c:if>	
	</p>
	-->
	<c:if test="${!(empty parent)}">	
		<p>
			<label class="small"><spring:message code="cutmotion.parentmotion" text="Clubbed To"></spring:message></label>
			<a href="#" id="p${parent}" onclick="viewmotionDetail(${parent});"><c:out value="${formattedParentNumber}"></c:out></a>
			<input type="hidden" id="parent" name="parent" value="${parent}">
		</p>
	</c:if>	
	
	<p>
		<label class="small"><spring:message code="generic.clubbed" text="Clubbed Motions"></spring:message></label>
		<c:choose>
			<c:when test="${!(empty clubbedEntities) }">
				<c:choose>
					<c:when test="${!(empty clubbedEntities) }">
						<c:forEach items="${clubbedEntities }" var="i">
							<a href="#" id="cq${i.number}" class="clubbedRefMotions" onclick="viewDiscussionMotionDetail(${i.number});" style="font-size: 18px;"><c:out value="${i.name}"></c:out></a>
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
			</c:when>
			<c:otherwise>
				<c:out value="-"></c:out>
			</c:otherwise>
		</c:choose>
	</p>
	
	<c:if test="${!(empty referencedEntities) }">
		<select id="referencedEntities" name="referencedEntities" multiple="multiple" style="display:none;">
			<c:forEach items="${referencedEntities }" var="i">
				<option value="${i.id}" selected="selected"></option>
			</c:forEach>
		</select>
	</c:if>
	
	<p>
	<label class="labeltop"><spring:message code="discussionmotion.ministries" text="Ministries"/>*</label>
	<select name="ministries" id="ministries" multiple="multiple" size="5" style="width:200px;">
		<c:forEach items="${ministries}" var="i">
			<c:set var="selectedMinistry" value="no"></c:set>
			<c:forEach items="${selectedministries}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedMinistry" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedMinistry=='yes'}">
					<option selected="selected" value="${i.id}">${i.dropdownDisplayName}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.dropdownDisplayName}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select> 	
	<form:errors path="ministries" cssClass="validationError"/>
	
	<label class="labeltop"><spring:message code="discussionmotion.subdepartments" text="Sub Departments"/>*</label>
	<select name="subDepartments" id="subDepartments" multiple="multiple" size="5">
		<c:forEach items="${subDepartments}" var="i">
			<c:set var="selectedSubDepartment" value="no"></c:set>
			<c:forEach items="${selectedSubDepartments}" var="j">
				<c:if test="${j.id==i.id}">
					<c:set var="selectedSubDepartment" value="yes"></c:set>
				</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${selectedSubDepartment=='yes'}">
					<option selected="selected" value="${i.id}">${i.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${i.id}">${i.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>			
	<form:errors path="subDepartments" cssClass="validationError"/>	
	</p>	
		
	<p>	
		<label class="centerlabel"><spring:message code="discussionmotion.subject" text="Subject"/></label>
		<form:textarea path="subject" rows="2" cols="50" readonly="true"></form:textarea>
		<form:errors path="subject" cssClass="validationError"/>	
	</p>

	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.noticeContent" text="Notice Content"/></label>
		<form:textarea path="noticeContent" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="noticeContent" cssClass="validationError"/>	
	</p>
	<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
	<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.briefExplanation" text="Brief Explanation"/></label>
		<form:textarea path="briefExplanation" cssClass="wysiwyg" readonly="true"></form:textarea>
		<form:errors path="briefExplanation" cssClass="validationError"/>	
	</p>
	</c:if>

	<p>
		<a href="#" id="reviseSubject" style="margin-left: 162px;margin-right: 20px;"><spring:message code="discussionmotion.revisedSubject" text="Revise Subject"></spring:message></a>
		<a href="#" id="reviseNoticeContent" style="margin-right: 20px;"><spring:message code="discussionmotion.revisedNoticeContent" text="Revise Notice"></spring:message></a>
		<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
		<a href="#" id="reviseBriefExplanation" style="margin-right: 20px;"><spring:message code="discussionmotion.revisedBriefExplanation" text="Revise Brief Explanation"></spring:message></a>
		</c:if>
		<a href="#" id="viewRevision"><spring:message code="discussionmotion.viewrevisions" text="View Revisions"></spring:message></a>
		<br />
	</p>

	
	<p style="display:none;" class="revise1" id="revisedSubjectDiv">
	<label class="centerlabel"><spring:message code="discussionmotion.revisedSubject" text="Revised Subject"/></label>
	<form:textarea path="revisedSubject" rows="2" cols="50"></form:textarea>
	<form:errors path="revisedSubject" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	
	<p style="display:none;" class="revise2" id="revisedNoticeContentDiv">
	<label class="wysiwyglabel"><spring:message code="discussionmotion.revisedNoticeContent" text="Notice Content"/></label>
	<form:textarea path="revisedNoticeContent" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedNoticeContent" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	<c:if test="${selectedDiscussionMotionType=='motions_discussionmotion_shortduration'}">
	<p style="display:none;" class="revise3" id="revisedBriefExplanationDiv">
	<label class="wysiwyglabel"><spring:message code="discussionmotion.revisedBriefExplanation" text="Revised Brief Explanation"/></label>
	<form:textarea path="revisedBriefExplanation" cssClass="wysiwyg"></form:textarea>
	<form:errors path="revisedBriefExplanation" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
	</p>
	</c:if>
	
	<p id="internalStatusDiv">
	<label class="small"><spring:message code="discussionmotion.currentStatus" text="Current Status"/></label>
	<input id="formattedInternalStatus" name="formattedInternalStatus" value="${formattedInternalStatus }" type="text" readonly="readonly">
	</p>
	
	<c:if test="${(internalStatusType=='discussionmotion_system_assistantprocessed' )}">
		<security:authorize access="hasAnyRole('DMOIS_ASSISTANT')">		
		<p>
			<label class="small"><spring:message code="discussionmotion.putupfor" text="Put up for"/></label>
			<select id="changeInternalStatus" class="sSelect">
			<option value="-"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${internalStatuses}" var="i">
				<c:if test="${(i.type!='discussionmotion_recommend_sendback'&&i.type!='discussionmotion_recommend_discuss') }">
					<c:choose>
						<c:when test="${i.id==internalStatusSelected }">
							<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>	
						</c:when>
						<c:otherwise>
							<option value="${i.id}"><c:out value="${i.name}"></c:out></option>		
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			</select>
			
			<select id="internalStatusMaster" style="display:none;">
			<c:forEach items="${internalStatuses}" var="i">
			<option value="${i.type}"><c:out value="${i.id}"></c:out></option>
			</c:forEach>
			</select>	
			<form:errors path="internalStatus" cssClass="validationError"/>	
		</p>
		</security:authorize>

		<p id="actorDiv" style="display: none;">
			<label class="small"><spring:message code="motion.nextactor" text="Next Users"/></label>
			<form:select path="actor" cssClass="sSelect" itemLabel="name" itemValue="id" items="${actors }" />
			<input type="text" id="actorName" name="actorName" style="display: none;" class="sText" readonly="readonly"/>
		</p>		

	</c:if>		
		
	<input type="hidden" id="internalStatus"  name="internalStatus" value="${internalStatus }">
	<input type="hidden" id="recommendationStatus"  name="recommendationStatus" value="${recommendationStatus}">
	<c:if test="${fn:contains(internalStatusType, 'discussionmotion_final')}">
		<form:hidden path="actor"/>
	</c:if>
	<c:if test="${!(empty domain.clarification) || (internalStatusType=='discussionmotion_final_clarificationNeededFromDepartment')}">
		<p>
		<label class="wysiwyglabel"><spring:message code="discussionmotion.clarification" text="Clarification"/></label>
		<form:textarea path="clarification" cssClass="wysiwyg"></form:textarea>
		<form:errors path="clarification" cssClass="validationError" cssStyle="float:right;margin-top:-100px;margin-right:40px;"/>
		</p>
	</c:if>	
	
	<p>
	<a href="#" id="viewCitation" style="margin-left: 162px;margin-top: 30px;"><spring:message code="discussionmotion.viewcitation" text="View Citations"></spring:message></a>	
	</p>
	
	<p>
	<label class="wysiwyglabel"><spring:message code="discussionmotion.remarks" text="Remarks"/></label>
	<form:textarea path="remarks" cssClass="wysiwyg"></form:textarea>
	</p>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
		<c:choose>
			<c:when test="${bulkedit!='yes'}">
				<c:if test="${internalStatusType=='discussionmotion_submit'
							||internalStatusType=='discussionmotion_system_assistantprocessed'
							||(internalStatusType=='discussionmotion_system_putup')
							||(internalStatusType=='discussionmotion_final_admission')}">
					<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
					<c:if test="${(internalStatusType !='discussionmotion_final_admission')}">
					<security:authorize access="hasAnyRole('DMOIS_ASSISTANT')">
					<input id="startworkflow" type="button" value="<spring:message code='discussionmotion.putupdiscussionmotion' text='Put Up Motion'/>" class="butDef">
					</security:authorize>	
					</c:if>				
				</c:if>
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
	<input id="bulkedit" name="bulkedit" value="${bulkedit}" type="hidden">	
	<input type="hidden" name="status" id="status" value="${status }">
	<input type="hidden" name="createdBy" id="createdBy" value="${createdBy }">
	<input type="hidden" name="dataEnteredBy" id="dataEnteredBy" value="${dataEnteredBy }">
	<input type="hidden" name="setCreationDate" id="setCreationDate" value="${creationDate }">
	<input id="setSubmissionDate" name="setSubmissionDate" type="hidden"  value="${submissionDate}">
	<input type="hidden" name="workflowStartedOnDate" id="workflowStartedOnDate" value="${workflowStartedOnDate }">
	<input type="hidden" name="taskReceivedOnDate" id="taskReceivedOnDate" value="${taskReceivedOnDate }">	
	<input id="role" name="role" value="${role}" type="hidden">
	<input id="taskid" name="taskid" value="${taskid}" type="hidden">
	<input id="workflowdetails" name="workflowdetails" value="${workflowdetails}" type="hidden">
	<input id="usergroup" name="usergroup" value="${usergroup}" type="hidden">
	<input id="usergroupType" name="usergroupType" value="${usergroupType}" type="hidden">	
	<input type="hidden" name="originalType" id="originalType" value="${originalType}">
	<input type="hidden" id="houseTypeType" value="${houseTypeType}" />
	<c:if test="${domain.ballotStatus!=null}">
		<input type="hidden" name="ballotStatus" id="ballotStatusId" value="${domain.ballotStatus.id}"/>		
	</c:if>
</form:form>
<input id="confirmSupportingMembersMessage" value="<spring:message code='confirm.supportingmembers.message' text='A request for approval will be sent to the following members:'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
<input id="confirmMotionSubmission" value="<spring:message code='confirm.motionsubmission.message' text='Do you want to submit the Motion'></spring:message>" type="hidden">
<input id="startWorkflowMessage" name="startWorkflowMessage" value="<spring:message code='discussionmotion.startworkflowmessage' text='Do You Want To Put Up Motion?'></spring:message>" type="hidden">
<input id="oldInternalStatus" value="${internalStatus}" type="hidden">
<input id="oldRecommendationStatus" value="${ RecommendationStatus}" type="hidden">
<input id="ministryEmptyMsg" value='<spring:message code="client.error.ministriesempty" text="Ministry can not be empty."></spring:message>' type="hidden">
<input id="discussionMotionType" type="hidden" value="${selectedDiscussionMotionType}" />
</div>
</div>

<div id="clubbingResultDiv" style="display:none;">
</div>

<div id="referencingResultDiv" style="display:none;">
</div>

<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
</body>
</html>