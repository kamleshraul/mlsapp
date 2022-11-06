<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	/**** Global Variables ****/
	var orderCount=0;
	var editWindowOpen= false;
	var newWindowOpen= false;
	var controlId='';
	var totalPartCount=0;
	var partCount=parseInt($('#partCount').val());
	totalPartCount=partCount+totalPartCount;

	$(document).ready(function(){
		
		
		/****Disbale browser back button****/
		 history.pushState(null, null, document.title);
		 window.addEventListener('popstate', function () {
		    history.pushState(null, null, 'home');
		}); 
		
		/****Disable F5 button****/
		document.onkeydown = function(e){
		    //keycode for F5 function
			if(e.keyCode===116){
				return false;
			}
		    
			if (e.ctrlKey || e.metaKey) {
		        switch (String.fromCharCode(e.which).toLowerCase()) {
		        case 's':
		            e.preventDefault();
		            break;
		        case 'r':
		        	e.preventDefault();
		        	break;
		        case 'w':
		        	e.preventDefault();
		        	break;
		        }
		        	
		    }
		}

		/**** Attributes set for line spacing and font size****/
		$("#proceedForm").find( "div" ).css( "line-height", "200%" );
		$("#proceedForm").find( "div" ).css( "font-size", "20px" ); 
		
		/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text starts****/
		$("#committeeMemberNamesDiv").hide();
		$("#hideCommitteeMemberNamesDiv").hide();
		$("#viewCommitteeMemberNamesDiv").click(function(){
			var committeeMeetingId = $("#committeeMeeting").val();
			if(committeeMeetingId!=undefined && committeeMeetingId!=''){			
				
				if($("#committeeMemberNamesDiv").css('display')=='none'){
					$("#committeeMemberNamesDiv").empty();
					$.get('ref/'+committeeMeetingId+'/committeeMemberNames',function(data){
						var text="";
						for(var i = 0; i < data.length; i++){
							text += "<p>"+data[i]+"</p><hr />";
						}						
						$("#committeeMemberNamesDiv").html(text);
						
					});	
					$("#hideCommitteeMemberNamesDiv").show();
					$("#committeeMemberNamesDiv").show();
				}else{
					$("#committeeMemberNamesDiv").hide();
					$("#hideCommitteeMemberNamesDiv").hide();
				}
			}
		});
		
		$("#hideCommitteeMemberNamesDiv").click(function(){
			$(this).hide();
			$('#committeeMemberNamesDiv').hide();
		});
		/**** To show/hide viewClubbedQuestionTextsDiv to view clubbed questions text end****/
		
		loadWysiwyg();
		
		/***Add Part***/
		loadAddPartClickEvent();
		
		/****Add Bookmark****/
		loadBookmarkClickEvent();
		
		/***Edit part***/
		//editPartContent();
		
		/***Replace All Functionality***/
		$("#replaceAll").click(function(){
			replaceAll('edit','false');
		});
		
		/***Undo***/
		$("#undo").click(function(){
			var undoCount = parseInt($("#undoCount").val());
			if(undoCount>0){
				undoLastChange();
			}
		});
		
		/***Redo***/
		$("#redo").click(function(){
			var redoCount = parseInt($("#redoCount").val());
			if(redoCount>0){
				redoLastChange();
			}
		});
		
		/* $(".editableContent").autoSave(function() {
		    var time = new Date().getTime();
		    $("#msg").text("Draft Autosaved " + time);
		}, 500); */
		
		/****Edit Proceeding Content****/
		 $('.editableContent').live('dblclick',function(e){
			  if (!editWindowOpen && !newWindowOpen) {			
					controlId=$(this).attr('id');
					var divId = this.id;
					$('#previousContent').val($(this).html());
					var text = window.getSelection().toString();
					var newContent = $(this).html();
					newContent = newContent.replace(/<span class='highlightedText'.*?>/g,"");
					newContent = newContent.replace(text,"<span class='highlightedText' style='background: yellow;'>"+text+"</span>"); 
					 
						var inputbox = "<textarea id='editContent' class='inputbox wysiwyg sTextarea'>"+newContent+"</textarea>"; 
						$(this).html(inputbox); 
						editWindowOpen=true;
						$('#editContent').wysiwyg({
							 resizeOptions: {maxWidth: 600},
							 controls:{
								 fullscreen: {
										visible: true,
										hotkey:{
											"ctrl":1|0,
											"key":122
										},
										exec: function () {
											if ($.wysiwyg.fullscreen) {
												$.wysiwyg.fullscreen.init(this);
											}
										},
										tooltip: "Fullscreen"
									},
								 },
							 events: {
								keypress: function(event) {
									if (event.ctrlKey  || event.metaKey) {
								        switch (String.fromCharCode(event.which).toLowerCase()) {
								        case 's':
								            event.preventDefault();
								            save();
								            break;
								        case 'w':
								        	event.preventDefault();
								        	saveAndHide();
								        	break;
								        }
								    }
									
									var keyCode = event.keyCode || event.which; 
										if (keyCode == 9) { 
										event.preventDefault(); 
									    insertAtCursor("editContent-wysiwyg-iframe",'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', 0);
									} 
																
								}
							 }
						});
						$("textarea.inputbox").focus(); 
						$("div.wysiwyg").css('width','700px');
						$('#editContent-wysiwyg-iframe').contents().find("body").css("line-height","200%");
						$('<input>').attr({
						    type: 'hidden',
						    id: 'copyOfeditContent',
						    value: $('#editContent').val()
						}).appendTo($('#editContent'));
					} 	
				
			});
		 
		 /****Edit Other Part Details****/
		 $('.editDetails').live('dblclick',function(){
			 	if(!editWindowOpen && !newWindowOpen){
			 		editWindowOpen = true;
					var divId = this.id;
					var tempId = divId.split('member');
					var divText= 
					 "<div id='editDetail' style='border:2px solid;height:170px;width: 900px;'>"+
					 "<form action='proceeding/part/updateMemberDetail'>"+
				 		"<p align='right' style='margin-top: 5px;margin-right:15px;'>"+
				    		"<a href='javascript:void(0)' id='editPublicLink'  class='imgLink publicLink' title='Public'>PU</a>"+
				 		"</p>"+
				 		"<p style='margin-top:5px;'>"+
						 "<label class='small'>"+$('#roleMessage').val()+"</label>"+
						 "<select name='editChairPersonRole' id='editChairPersonRole' class='sSelect'>"+
							 $('#roleMaster').html()+
					     "</select>"+
					     "<label class='small' style='margin-left: 110px;' >"+$('#chairPersonMessage').val()+"</label>"+
					 	 "<select name='editChairPerson' id='editChairPerson' class='sSelect' >"+
				    	 "</select>"+
				     	"</p>"+
				     	"<div id='member'>"+
					     "<p style='margin-top:5px;'>"+
					         "<label class='small'>"+$('#primaryMemberNameMessage').val()+"</label>"+
					         "<input type='text' class='autosuggest editFormattedMember sText' name='editFormattedPrimaryMember' id='editFormattedPrimaryMember' value='"+$('#primaryMemberName'+tempId[1]).val()+"' />"+
						     "<input name='editPrimaryMember' id='editPrimaryMember' type='hidden' value='"+$('#primaryMember'+tempId[1]).val()+"'/>"+
						 "</p>"+
						 "<p class='minister' >"+
				             "<label class='small'>"+$('#primaryMemberDesignationMessage').val()+"</label>"+
				             "<select name='editPrimaryMemberDesignation' id='editPrimaryMemberDesignation' class='sSelect'>"+
						      	$('#designationMaster').html()+
						      "</select>"+
						      "<label class='small' style='margin-left: 110px;'>"+$('#primaryMemberSubDepartmentMessage').val()+"</label>"+
						      "<select name='editPrimaryMemberSubDepartment' id='editPrimaryMemberSubDepartment'  class='sSelect'>"+
						     	 $('#subDepartmentMaster').html()+
						      "</select>"+
			             "</p>"+
			             "<p>"+
						     "<label  class='small' >"+$('#isConstituencyRequiredMessage').val()+"</label>"+
						     "<input type='checkbox' id='editIsConstituencyRequired' name='editIsConstituencyRequired' class='sCheck'  >"+
						     "<label  class='small' style='margin-left: 285px;'>"+$('#isSubstitutionRequiredMessage').val()+"</label>"+
						     "<input type='checkbox' id='editIsSubstitutionRequired' name='editIsSubstitutionRequired' class='sCheck'  >"+
					     "</p>"+
					     "</div>"+
					     "<div id='public' style='display:none;'>"+
					     	"<p>"+
					     	"<label class='small'>"+$('#publicRepresentativeMessage').val()+"</label>"+
				             "<input type='text' class='sText' name='editPublicRepresentative' id='editPublicRepresentative'/>"+
					     	"</p>"+
					     	"<p>"+
					     	"<label class='small'>"+$('#publicRepresentativeDetailMessage').val()+"</label>"+
						     "<textarea class='sTextArea' name='editPublicRepresentativeDetail' id='editPublicRepresentativeDetail'/>"+
					     	"</p>"+
					     "</div>"+
					     	"<h2></h2>"+
							"<p class='tright'>"+
								"<input id='submit' type='button' value='submit' class='butDef submit'>"+
							"</p>"+
							"<input type='hidden' id='editPartId' name='editPartId' value='"+$('#partId'+tempId[1]).val()+"'/>"+
						  "</form>"+
					"</div>";
					$('#'+divId).empty();
					$('#'+divId).html(divText);
					
					/****Populate the chairperson based on the role selected by the user.****/
					 $('#editChairPersonRole').change(function(){
							$.get('ref/getchairperson?chairPersonRole='+$(this).val()+'&proceeding='+$('#proceedingId').val(),function(data){
								$("#editChairPerson").empty();
								var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
								if(data.length>0){
									if(data.length==1){
										chairPersonText+="<option value='"+data[0]+"' selected='selected'>"+data[0];
									}else{
										for(var i=0;i<data.length;i++){
											chairPersonText+="<option value='"+data[i]+"'>"+data[i];
										}
									}
								$("#editChairPerson").html(chairPersonText);
							}else{
								$("#editChairPerson").empty();
								var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
								$("#editChairPerson").html(chairPersonText);	
							}
							});
						});
					
					/****Auto Setting of chaipersonrole,member,designation,department,isconstituency required of the current part****/
					 $('#editChairPersonRole option').each(function(){
						 if($(this).val()==$('#chairPersonRole'+tempId[1]).val()){
							 $(this).attr('selected',true);
							 $('#editChairPersonRole').change();
						 } 
				 	});
					 
							 
					 $('#editPrimaryMemberDesignation option').each(function(){
						 if($(this).val()==$('#primaryMemberDesignation'+tempId[1]).val()){
							 $(this).attr('selected',true);
						 } 
				 	});
					 
					 $('#editPrimaryMemberSubDepartment option').each(function(){
						 if($(this).val()==$('#primaryMemberSubDepartment'+tempId[1]).val()){
							 $(this).attr('selected',true);
						 } 
				 	});
					 
					 if($('#isConstituencyRequired'+tempId[1]).val()=='true'){
						 $('#editIsConstituencyRequired').attr('checked',true);
					 }
					 
					 if($('#isSubstitutionRequired'+tempId[1]).val()=='true'){
						 $('#editIsSubstitutionRequired').attr('checked',true);
					 }
					 
					 
					 $( ".editFormattedMember").autocomplete({
							minLength:3,			
							source:'ref/member/getmembers?session='+$("#session").val(),
							select:function(event,ui){	
								id=this.id;
								$("#editPrimaryMember").val(ui.item.id);
							}	
					  }); 
					 
					 /**** Submit****/
					 $('#submit').click(function(){
						 $.post($("form[action='proceeding/part/updateMemberDetail']").attr('action'),
									$("form[action='proceeding/part/updateMemberDetail']").serialize(),function(data){
						 		if(data!=null){
						 			var memberText='';
						 			if(data.primaryMemberName!=null && data.primaryMemberName!=''){
						 				$('#primayMember'+tempId[1]).val(data.primaryMember);
						 				$('#primayMemberName'+tempId[1]).val(data.primaryMemberName);
										if(data.constituency!=null && data.constituency!=''){
											$('#isConstituencyRequired'+tempId[1]).val(data.isConstituencyRequired);
											memberText= memberText + data.primaryMemberName + "("+ data.constituency +")";
										}else{
											memberText= memberText + data.primaryMemberName;
										}
										if(data.primaryMemberDesignation!=null && data.primaryMemberDesignation!=''){
											$('#primayMemberDesignation'+tempId[1]).val( data.primaryMemberDesignation);
											memberText= memberText + "("+data.primaryMemberDesignationName+")";
										}
										if(data.primaryMemberMinistry !=null && data.primaryMemberMinistry !=''){
											$('#primaryMemberMinistry'+tempId[1]).val( data.primaryMemberMinistry);
											memberText= memberText + "("+data.primaryMemberMinistryName+")";
										}
										if(data.primaryMemberSubDepartment !=null && data.primaryMemberSubDepartment !=''){
											$('#primaryMemberSubDepartment'+tempId[1]).val( data.primaryMemberSubDepartment);
											memberText= memberText + "("+data.primaryMemberSubDepartmentName+")";
										}
										if(data.substituteMember!=null && data.substituteMember!=''){
											$('#substituteMember'+tempId[1]).val(data.substituteMember);
											memberText= memberText + ","+data.substituteMemberName;
											if(data.substituteMemberDesignation!=null && data.substituteMemberDesignation!=''){
												$('#substituteMemberDesignation'+tempId[1]).val( data.substituteMemberDesignation);
												memberText= memberText + "("+data.substituteMemberDesignationName+")";
											}
											if(data.substituteMemberMinistry !=null && data.substituteMemberMinistry !=''){
												$('#substituteMemberMinistry'+tempId[1]).val( data.substituteMemberMinistry);
												memberText= memberText + "("+data.substituteMemberMinistryName+")";
											}
											if(data.substituteMemberSubDepartment !=null && data.substituteMemberSubDepartment !=''){
												$('#substituteMemberSubDepartment'+tempId[1]).val(data.substituteMemberSubDepartment);
												memberText= memberText + "("+data.substituteMemberSubDepartmentName+")";
											}
											memberText = memberText + $('#subsituteMemberText').val();
										}
									}else if(data.publicRepresentative != null && data.publicRepresentative !=''){
										$('#publicRepresentative'+tempId[1]).val(data.publicRepresentative);
										$('#publicRepresentativeDetail'+tempId[1]).val(data.publicRepresentativeDetail);
										memberText= memberText + data.publicRepresentative;
									}		
						 			$('#'+divId).empty;
						 			$('#'+divId).html(memberText);
						 			editWindowOpen = false;
						 		}else{
						 			$.unblockUI();
									if($("#ErrorMsg").val()!=''){
										$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
									}else{
										$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
									}
									scrollTop();
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
				 });
			 }
		});
	 
		 $('.wysiwyg').live('change', function(){
			var idval = this.id;	
			if($('#'+idval).val()!=$('#copyOf'+idval).val()) {
				if($('#'+idval).is('[readonly]')){
					if($('#'+idval).val()!=$('#copyOf'+idval).val()) {
						$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
					}
				} else {
					//added code to restrict MS word content 
					var isWordContentTyped = false;
					if(!$('#'+idval).hasClass( "wordContentAllowed" ))
					{
						if($('#'+idval).val()!=$('#copyOf'+idval).val()) {
							if($('#'+idval).val().toLowerCase().indexOf("mso") >= 0 || $('#'+idval).val().toLowerCase().indexOf("w:") >= 0){	
								$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
								isWordContentTyped = true;
								$.prompt($('#noWordContentPrompt').val());									
							} else if($('#'+idval).val().toLowerCase().indexOf("o:p") >= 0){
								$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
								isWordContentTyped = true;
								$.prompt($('#noMsOfficeContentPrompt').val());			
							}
						}				
					}
					if(!isWordContentTyped) {
						if(!$('#'+idval).hasClass( "invalidFormattingAllowed" ))
						{
							if($('#'+idval).val()!=$('#copyOf'+idval).val()) {
								if($('#'+idval).val().toLowerCase().indexOf("ol style=") >= 0){	
									$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
									$.prompt($('#noInvalidFormattingPrompt').val());			
								} else if($('#'+idval).val().toLowerCase().indexOf("br style=") >= 0){	
									$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
									$.prompt($('#noInvalidFormattingPrompt').val());			
								} else if($('#'+idval).val().toLowerCase().indexOf("&lt;ol&gt;&lt;/ol&gt;") >= 0){	
									$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
									$.prompt($('#noInvalidFormattingPrompt').val());			
								} else if($('#'+idval).val().toLowerCase().indexOf("-webkit-text-stroke-widt") >= 0){	
									$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
									$.prompt($('#noInvalidFormattingPrompt').val());		
								}
							}				
						}
					}
					if($('#'+idval).val()=="<p></p>"){						
						$('#'+idval+'-wysiwyg-iframe').focus();				
						$('#'+idval+'-wysiwyg-iframe').contents().find('html').html("<br><p></p>");				
					}
					$('#copyOf'+idval).val($('#'+idval).val());				
				}
				if($('#'+idval).hasClass('proceedingContentwysiwyg')) {
					maxHeight=$("#"+contentNo+"-wysiwyg-iframe").css("height");
				}				
			}			
		});
	});
	
	/***Add Part***/
	function addPart(currentCount){
		if(!editWindowOpen && !newWindowOpen){
			orderCount=parseInt(orderCount)+1;
			var flag=false;
			partCount=partCount+1;
			contentNo="content"+partCount;
			totalPartCount=totalPartCount+1;
			newWindowOpen = true;
			var partText= "<div id='part"+partCount+"'>"+
			"<a href='javascript:void(0)' id='showOtherContent' style='position:absolute;margin-top:12%;'><img src='./resources/images/toggle.jpg' title='Show Content' class='imageLink' /></a>"+
			"<div id='otherContent' style='width:320px;display: inline-block;margin-left: 25px;'>"+ 
				"<div id='div1' style='border:2px solid;height:120px;'>"+
					 "<p style='margin-top:5px;'>"+
						 "<label class='small' style='width:123px;'>"+$('#roleMessage').val()+"</label>"+
						 "<select name='chairPersonRole"+partCount+"' id='chairPersonRole"+partCount+"' class='sSelect' style='width:170px;'>"+
					    	 $('#roleMaster').html()+
					     "</select>"+
				     "</p>"+
				     "<p id='chairPersonP' style='display: none;'>"+
					 	 "<label class='small' style='width:123px;'>"+$('#chairPersonMessage').val()+"</label>"+
					 	 "<select name='chairPerson"+partCount+"' id='chairPerson"+partCount+"' class='sSelect' style='width:170px;'>"+
				    	 "</select>"+
					 "</p>"+
			         "<p class='deviceType"+partCount+"'>"+
			              "<label class='small' style='width:123px;'>"+$('#deviceTypeMessage').val()+"</label>"+
			              "<select name='deviceType"+partCount+"' id='deviceType"+partCount+"'class='sSelect' style='width:170px;'>"+
					     	 $('#deviceTypeMaster').html()+
					      "</select>"+
			         "</p>"+
			        
		             "<p>"+
		              "<label class='small' style='width:123px;'>"+$('#deviceNoMessage').val()+"</label>"+
					  "<input type='text' name='deviceNo"+partCount+"' id='deviceNo"+partCount+"' class='deviceNo sInteger' style='width:168px;'/>"+
		             "</p>"+
		            
				     "<p>"+
						"<label class='small' style='width:123px;'>"+$('#isInterruptedMessage').val()+"</label>"+
						"<input type='checkbox' id='isInterrupted"+partCount+"' name='isInterrupted"+partCount+"' class='sCheck'>"+
					 "</p>"+
				 "</div>"+
				 "<div id='div2' style='border:2px solid;height:320px;margin-top:2px;'>"+
				   "<p id='ip' align='right' style='margin-top: 5px;margin-right:15px;'>"+
				   		"<a href='javascript:void(0)' id='interruptedProceeding' class='imgLink' title='Interrupted Proceeding'>IP</a>"+			   
				   		"<a href='javascript:void(0)' id='specialHeading' class='imgLink' title='Special Heading'>SH</a>"+			   
				   "<p class='pageHeadingP'>"+
				   		"<textarea class='proceedingContentwysiwyg' name='pageHeading"+partCount+"' id='pageHeading"+partCount+"'/>"+
				   "</p>"+
				    "<p style='margin-top:20px;' class='mainHeadingP'>"+
				   	 	"<textarea class='proceedingContentwysiwyg' name='mainHeading"+partCount+"' id='mainHeading"+partCount+"'/>"+
				    "</p>"+ 		
				    "<p style='display:none;' class='specialHeadingP'>"+
			   	 		"<textarea class='proceedingContentwysiwyg' name='specialHeading"+partCount+"' id='specialHeading"+partCount+"'/>"+
			    	"</p>"+
				    "<p class='order"+partCount+"'style='display:none;'>"+
			     		"<label class='small'>"+$('#orderMessage').val()+"</label>"+
			     		"<input type='text' class='sInteger' name='partOrder"+partCount+"' id='partOrder"+partCount+"'/>"+
			     	"</p>"+
		     	 "</div>"+
			 "</div>"+
			 "<div id='mainContent' style='width:570px;display: inline-block;float:right;'>"+
			     "<div id='div3' style='border:2px solid;height:120px;'>"+
			 		"<p align='right' style='margin-top: 5px;margin-right:15px;'>"+
			    		"<a href='javascript:void(0)' id='publicLink"+partCount+"'  class='imgLink publicLink' title='Public'>PU</a>"+
			 		"</p>"+
			 		"<div id='member'>"+
				     "<p style='margin-top:5px;'>"+
				         "<label class='small' style='width:100px;'>"+$('#primaryMemberNameMessage').val()+"</label>"+
				         "<input type='text' class='autosuggest formattedMember sText' name='formattedPrimaryMember"+partCount+"' id='formattedPrimaryMember"+partCount+"' style='width:150px'/>"+
					     "<input name='primaryMember"+partCount+"' id='primaryMember"+partCount+ "' type='hidden'/>"+
					     "<label class='small' style='margin-left: 6px;width:100px;'>OR party</label>"+
						 "<select id='party' class='sSelect' style='width:150px;'>"+
						  	$('#partyMaster').html()+
						 "</select>"+
					 "</p>"+
					 "<p class='minister"+partCount+"' >"+
			             "<label class='small' style='width:100px;'>"+$('#primaryMemberDesignationMessage').val()+"</label>"+
			             "<select name='primaryMemberDesignation"+partCount+"' id='primaryMemberDesignation"+partCount+"' class='sSelect' style='width:160px;'>"+
					      	$('#designationMaster').html()+
					      "</select>"+
					      "<label class='small' style='margin-left: 6px;width:100px;'>"+$('#primaryMemberSubDepartmentMessage').val()+"</label>"+
					      "<select name='primaryMemberSubDepartment"+partCount+"' id='primaryMemberSubDepartment"+partCount+"' style='width:150px;' class='sSelect'>"+
					     	 $('#subDepartmentMaster').html()+
					      "</select>"+
		             "</p>"+
		             "<p>"+
					     "<label  class='small' style='width:160px;'>"+$('#isConstituencyRequiredMessage').val()+"</label>"+
					     "<input type='checkbox' id='isConstituencyRequired"+partCount+"' name='isConstituencyRequired"+partCount+"' class='sCheck'  style='margin-left:20px;'>"+
					     "<label  class='small' style='width:160px;margin-left: 75px;'>"+$('#isSubstitutionRequiredMessage').val()+"</label>"+
					     "<input type='checkbox' id='isSubstitutionRequired"+partCount+"' name='isSubstitutionRequired"+partCount+"' class='sCheck'  style='margin-left: 20px;'>"+
				     "</p>"+
				     "</div>"+
				     "<div id='public' style='display:none;'>"+
				     	"<p>"+
				     	"<label class='small'>"+$('#publicRepresentativeMessage').val()+"</label>"+
			             "<input type='text' class='sText' name='publicRepresentative"+partCount+"' id='publicRepresentative"+partCount+"'/>"+
				     	"</p>"+
				     	"<p>"+
				     	"<label class='small'>"+$('#publicRepresentativeDetailMessage').val()+"</label>"+
					      "<textarea class='sTextArea' name='publicRepresentativeDetail"+partCount+"' id='publicRepresentativeDetail"+partCount+"'/>"+
				     	"</p>"+
				     "</div>"+
				"</div>"+
			    "<div id='div4' style='border:2px solid;height:320px;margin-top:2px;'>"+
			    	"<p align='right' style='margin-top: 5px;margin-right:15px;'>"+
			    		
					  	"<a href='javascript:void(0)' id='viewProceedingCitation"+partCount+"' class='viewProceedingCitation imgLink'>CT</a>"+
					 "</p>"+
			     	"<p>"+
					 	"<textarea class='proceedingContentwysiwyg' name='partContent"+partCount+"' id='partContent"+partCount+"'/>"+
				     "</p>"+
			    "</div>"+
		     "</div>"+
		     "<input type='hidden' id='partId"+partCount+"' name='partId"+partCount+"'>"+
		      "<input type='hidden' id='deviceId"+partCount+"' name='deviceId"+partCount+"'>"+
		      "<input type='hidden' id='partLocale"+partCount+"' name='partLocale"+partCount+"' value='"+$("#locale").val()+"'>"+
			  "<input type='hidden' id='partVersion"+partCount+"' name='partVersion"+partCount+"'>"+
			  "<input type='hidden' id='partReporter"+partCount+"' name='partReporter"+partCount+"' value='"+$('#proceedingReporter').val()+"'>"+
			  "<input type='hidden' id='partEntryDate"+partCount+"' name='partEntryDate"+partCount+"'>"+
			  "<input type='hidden' id='partRevisedContent"+partCount+"' name='partRevisedContent"+partCount+"'>"+
			  "<input type='hidden' id='partProceeding"+partCount+"' name='partProceeding"+partCount+"' value='"+$('#proceedingId').val() +"'>"+
		      "<div id='addDeleteButtons"+partCount+"'>"+
		      //"<a href='javascript:void(0)' id='addPart"+partCount+"' class=' addNewPartButton' style='display:none;'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
			  "<a href='javascript:void(0)'  id='deletePart"+partCount+"' class=' deletePartButton' onclick='deletePart("+partCount+");' style='margin-left:30px;'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' /></a>"+
			  "<a href='javascript:void(0)' id='savePart"+partCount+"' class='saveButton' style='margin-left:840px;'><img src='./resources/images/save.jpg' title='Save Part' class='imageLink' /></a>"+
			  "</div>"+
		     "</div>";
		    
			var text="";
			/** if the part is not inserted in between two parts then only individual part will be saved
			*** else Entire Proceeding will be updated **/
			console.log("totalPartCount="+totalPartCount);
			if(currentCount==totalPartCount-1){
				 text="<form action='proceeding/part/save' id='partForm"+partCount+"'>"+
					partText+
				     "</form>"; 
				     flag=true;
			}else{
				text=partText;
			}
			
			/** if the user entering first part it should be appended to the add delete buttons
			*** else append the part to the previous part **/
			if(currentCount==0){
				$('#addDeleteButtons'+currentCount).after(text);
			  	$('#partOrder'+partCount).val(currentCount+1);
			  	loadWysiwyg();
			    loadImageCss();
			    loadDeviceNoChangeEvent();
			    /** The Main Heading, page Heading, DeviceType , Device of last part of previous slot is set **/
			  /* 	if($('#previousPartMainHeading').val()!=null && $('#previousPartMainHeading').val()!=''){
			  		$('#mainHeading'+partCount).wysiwyg('setContent',$('#previousPartMainHeading').val());
		  		}
		  		if($('#previousPartPageHeading').val()!=null && $('#previousPartPageHeading').val()!=''){
		  			$('#pageHeading'+partCount).wysiwyg('setContent',$('#previousPartPageHeading').val());
		  		} */
		  		/* if($('#previousPartSpecialHeading').val()!=null && $('#previousPartSpecialHeading').val()!=''){
		  			$('#specialHeading'+partCount).wysiwyg('setContent',$('#previousPartSpecialHeading').val());
		  			$('.mainHeadingP').hide();
		  			$('.pageHeadingP').hide();
		  			$('.specialHeadingP').css('display','inline-block');
		  		} */
		  		if($('#previousPartDeviceType').val()!=null && $('#previousPartDeviceType').val()!=''){
		  			 $('#deviceType'+partCount+' option').each(function(){
		  				if($(this).val()==$('#previousPartDeviceType').val()){
		  					 $(this).attr('selected',true);
		  				 } 
		  			  });
		  		}
		  		if($('#previousPartDeviceNumber').val()!=null && $('#previousPartDeviceNumber').val()!=''){
		  			 $('#deviceNo'+partCount).val($('#previousPartDeviceNumber').val());
		  		}
		  		if($('#previousPartDeviceId').val()!=null && $('#previousPartDeviceId').val()!=''){
		  			$('#deviceId'+partCount).val($('#previousPartDeviceId').val());
		  		}
		  		if($('#previousPartChairPersonRole').val()!=null && $('#previousPartChairPerson').val()!=''){
		  			 $('#chairPersonRole'+partCount+' option').each(function(){
						 if($('#previousPartChairPersonRole').val()==$(this).val()){
							 $(this).attr('selected',true);
						 } 
				 	});
		  		} 
		  		
		  		/** When the User insert a part in between two parts, the orderNo needs to be updated of the parts below the current inserted part**/
			    for(var i = currentCount+1;i<partCount;i++){
		      		$('#partOrder'+i).val(i+1);
		      	}
		    }else{
		    	$('#part'+currentCount).after(text);
		    	$('#partOrder'+partCount).val(currentCount+1);
		    	loadWysiwyg();
			    loadImageCss();
			    loadDeviceNoChangeEvent();
			    
			   
			    if($("#committeeMeeting").val()==null || $("#committeeMeeting").val()=='' || $("#committeeMeeting").val()==0){
			    	if($('#specialHeading-'+currentCount).html()!=null  && $('#specialHeading-'+currentCount).html()!=''){
				    	$('#specialHeading'+partCount).wysiwyg('setContent',$('#specialHeading-'+currentCount).html().trim());
				    	$('.specialHeadingP').css('display','inline-block');
				    	$('.mainHeadingP').hide();
				    	$('.pageHeadingP').hide();
				    }else{
				    	/* 
				    	if($('#mainHeading-'+currentCount).html()!=null && $('#mainHeading-'+currentCount).html()!=''){
				    		$('#mainHeading'+partCount).wysiwyg('setContent',$('#mainHeading-'+currentCount).html().trim());
				  		}
				  		if($('#pageHeading-'+currentCount).html()!=null && $('#pageHeading-'+currentCount).html()!=''){
				  			$('#pageHeading'+partCount).wysiwyg('setContent',$('#pageHeading-'+currentCount).html().trim());
				  		} */
				    } 
			    }
			    		    
			    
			    /** The Main Heading, page Heading, DeviceType , Device of previous part of current slot is set **/
			     $('#chairPersonRole'+partCount+' option').each(function(){
					 if($(this).val()==$('#chairPersonRole'+currentCount).val()){
						 $(this).attr('selected',true);
					 } 
			 	});
			    
			    $('#deviceType'+partCount+' option').each(function(){
					if($(this).val()==$('#partDeviceType'+currentCount).val()){
						 $(this).attr('selected',true);
					 } 
				 });
			    
			    if($('#deviceId'+currentCount).val()!='' && $('#deviceId'+currentCount).val()!=null){
					  $.get('ref/getDeviceNumber?deviceId='+$('#deviceId'+currentCount).val()+'&deviceType='+$('#partDeviceType'+currentCount).val(),function(data){
						  if(data!=null){
							  $('#deviceNo'+partCount).val(data.name);
						  }
					  });
					  $('#deviceId'+partCount).val($('#deviceId'+currentCount).val());
				}  
			    
			    
			    /** When the User insert a part in between two parts, the orderNo needs to be updated of the parts below the current inserted part**/
			    for(var i = currentCount+1;i<partCount;i++){
		      		$('#partOrder'+i).val(i+1);
		      	}
		     }
			
		      $('#partCount').val(partCount); 
		      $('#mainHeading'+partCount+'-wysiwyg-iframe').css('height','80px');
			  $('#pageHeading'+partCount+'-wysiwyg-iframe').css('height','80px');
			  $('#content'+partCount+'-wysiwyg-iframe').css('height','225px');
			  $('#content'+partCount+'-wysiwyg-iframe').contents().find("body").css("line-height","200%");
			  $($($("#div4").children()).children('div.wysiwyg')).css("width","530px"); 
			 
			  if(currentCount>0){
				  //$("#showOtherContent").css("display","inline");
				  $("#otherContent").css("display","none");
				  $("#mainContent").css("width","890px");
				  $($($("#div4").children()).children('div.wysiwyg')).css("width","850px");
			  }
			  
			  $("#showOtherContent").click(function(){
				 // $("#showOtherContent").css("display","none");
				 if($("#otherContent").css("display")=='none'){
					 $("#otherContent").css("display","inline-block");
					 $("#mainContent").css("width","570px");
					 $($($("#div4").children()).children('div.wysiwyg')).css("width","530px");
				 }else{
					 $("#otherContent").css("display","none");
					 $("#mainContent").css("width","890px");
					 $($($("#div4").children()).children('div.wysiwyg')).css("width","850px");
				 }
			  });
				 
			/***Selecting Member By Party***/
			  $('#party').change(function(){
				 	$.get('proceeding/part/getMemberByPartyPage?partyId='+$(this).val()+'&partCount='+partCount+'&housetype='+$("#selectedHouseType").val(),function(data){
			 			$.fancybox.open(data,{autoSize: false,width:800,height:500});
					},'html'); 
			     return false;
			  });
				  
			/***Member AutoSuggest***/
			  $( ".formattedMember").autocomplete({
					minLength:3,			
					source:'ref/member/getmembers?session='+$("#session").val(),
					select:function(event,ui){	
						id=this.id;
						var elementCount=id.split("formattedPrimaryMember")[1];
						$("#primaryMember"+elementCount).val(ui.item.id);
						/** Setting the party of the Selected member **/
						$.get('ref/findParty?memberId='+ui.item.id,function(data){
							if(data!=null){
								$('#party option').each(function(){
									if($(this).val()==data){
										 $(this).attr('selected',true);
									 } 
								});
							}
						});
					}	
			  });
			
	  		/**There are number of talika sabhadhyaksha.. to select the appropriate chariperson selection is provided **/	
			  $('#chairPersonRole'+partCount).change(function(){
					$.get('ref/getchairperson?chairPersonRole='+$(this).val()+'&proceeding='+$('#proceedingId').val(),function(data){
						$("#chairPerson"+partCount).empty();
						var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
						if(data.length>0){
							console.log(data);
							if(data.length==1){
								chairPersonText+="<option value='"+data[0]+"' selected='selected'>"+data[0];
							}else{
								for(var i=0;i<data.length;i++){
									chairPersonText+="<option value='"+data[i]+"'>"+data[i];
								}
							}
						$("#chairPerson"+partCount).html(chairPersonText);
						$("#chairPersonP").css('display','block');
						$('#div1').css('height','150px');
						$('#div3').css('height','150px');
						}else{
							$("#chairPerson"+partCount).empty();
							var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
							$("#chairPerson"+partCount).html(chairPersonText);	
							$("#chairPersonP").css('display','block');
						}
					});
				});
			  
			 
			  /***Save Part****/
			  $('.saveButton').click(function(){
				  var parameters="?partCount="+partCount;
				  var savetext='';
				  
				 $('.proceedingContentwysiwyg').each(function(){
					 var wysiwygVal=$(this).val().trim();
						if(wysiwygVal=="<p></p>"||wysiwygVal=="<p><br></p>"||wysiwygVal=="<br><p></p>"){
							$(this).val("");
						}
				 });
				 /** if Flag is True then single part will be saved using form[action='proceeding/part/save']
				   ** else the entire proceeding is updated using form[action='proceeding']**/
				   $.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				   if(flag){
					  
					   $.post($("form[action='proceeding/part/save']").attr('action')+parameters,
								$("form[action='proceeding/part/save']").serialize(),function(data){
								if(data!=null && data.id!=null){
									if($('#part'+partCount).parent().attr("id")=='partForm'+partCount){
										$('#partForm'+partCount).remove();
									}else{
										$('#part'+partCount).remove();
									} 
									var procContent = data.proceedingContent;
									procContent = procContent.replace(/"/g, '\\"');
							  		savetext="<div id='part"+partCount+"' class='abc'>"+
							  		"<div id='dummyScreen' >"+
									"<div class='myeditablePara'>"	+
									"<div align='center' id='headings' >";
									if(data.pageHeading!=null && data.pageHeading!=''){
										savetext=savetext+$('#pageHeadingMessage').val()+" :" +
												"<span id='pageHeading-"+partCount+"' class='editableContent'>"+
												data.pageHeading+
											    "</span>"+
											    "<br>";
									}
									if(data.mainHeading!=null && data.mainHeading!=''){
										savetext=savetext + $('#mainHeadingMessage').val()+" :"+
											"<span id='mainHeading-"+partCount+"' class='editableContent'>"+ 
												data.mainHeading+
											"</span>";
									}
									if(data.specialHeading!=null && data.specialHeading!=''){
										savetext=savetext + 
										"<span id='specialHeading-"+partCount+"' class='editableContent'>"+ 
											data.specialHeading+
										"</span>";
									}
									savetext=savetext+	
										"<br>"+
										"</div>";
									if(data.primaryMemberName!=null && data.primaryMemberName!=''){
										if(data.constituency!=null && data.constituency!=''){
											savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
											data.primaryMemberName+ 
											"("+ data.constituency +")";
										}else{
											savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
											data.primaryMemberName;
											
										}
										if(data.primaryMemberDesignation!=null && data.primaryMemberDesignation!=''){
											savetext= savetext + "("+data.primaryMemberDesignationName+")";
										}
										if(data.primaryMemberMinistry !=null && data.primaryMemberMinistry !=''){
											savetext= savetext + "("+data.primaryMemberMinistryName+")";
										}
										if(data.primaryMemberSubDepartment !=null && data.primaryMemberSubDepartment !=''){
											savetext= savetext + "("+data.primaryMemberSubDepartmentName+")";
										}
										if(data.substituteMember!=null && data.substituteMember!=''){
											savetext= savetext + ","+data.substituteMemberName;
											if(data.substituteMemberDesignation!=null && data.substituteMemberDesignation!=''){
												savetext= savetext + "("+data.substituteMemberDesignationName+")";
											}
											if(data.substituteMemberMinistry !=null && data.substituteMemberMinistry !=''){
												savetext= savetext + "("+data.substituteMemberMinistryName+")";
											}
											if(data.substituteMemberSubDepartment !=null && data.substituteMemberSubDepartment !=''){
												savetext= savetext + "("+data.substituteMemberSubDepartmentName+")";
											}
											savetext = savetext + $('#subsituteMemberText').val();
										}
										savetext = savetext + ":"+
										"</div>";
										
									}else if(data.publicRepresentative != null && data.publicRepresentative !=''){
										savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
										data.publicRepresentative+ 
									":"+
									"</div>";
									}					  			
									savetext=savetext +	"<div style='min-width:10px;width: 50px; display: inline-block;'>&nbsp;&nbsp;</div>"+
										"<div class='proceedingContent"+partCount+" editableContent proceedingContent-"+data.id+"' style='display:inline-block;' id='proceedingContent"+partCount+"'>"+
											data.proceedingContent+
										"</div>"+
										"<div id='addDeleteButtons"+partCount+"'>"+
											"<a href='javascript:void(0)' id='addPart"+partCount+"' class='addPartButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
											"<a href='javascript:void(0)'  id='deletePart"+partCount+"' class='deletePartButton'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' onclick='deletePart("+partCount+");' /></a>"+
											"<a href='javascript:void(0)'  id='bookmark"+partCount+"' class='addBookmark'><img src='./resources/images/star_full.jpg' title='Bookmark' class='imageLink'/></a>"+
										"</div>"+
										"<input type='hidden' id='partId"+partCount+"' name='partId"+partCount+"' value='"+data.id+"'>"+
										"<input type='hidden' id='partVersion"+partCount+"' name='partVersion"+partCount+"' value='"+data.version+"'>"+
										"<input type='hidden' id='partLocale"+partCount+"' name='partLocale"+partCount+"' value='"+$('#locale').val()+"'>"+
										"<input type='hidden' id='partOrder"+partCount+"' name='partOrder"+partCount+"' value='"+data.OrderNo+"'>"+
										"<input type='hidden' id='mainHeading"+partCount+"' name='mainHeading"+partCount+"' value='"+data.mainHeading+"'>"+
										"<input type='hidden' id='pageHeading"+partCount+"' name='pageHeading"+partCount+"' value='"+data.pageHeading+"'>";
										if(data.memberrole != null){
											savetext = savetext +"<input type='hidden' id='chairPersonRole"+partCount+"' name='chairPersonRole"+partCount+"' value='"+data.memberrole+"'>";
										}else{
											savetext   = savetext +"<input type='hidden' id='chairPersonRole"+partCount+"' name='chairPersonRole"+partCount+"' value=''>";
										}
										savetext   = savetext +
										"<input type='hidden' id='partContent"+partCount+"' name='partContent"+partCount+"' value='"+data.proceedingContent+"'>"+
										"<input type='hidden' id='primaryMember"+partCount+"' name='primaryMember"+partCount+"' value='"+data.primaryMember+"'>"+
										"<input type='hidden' id='primaryMemberName"+partCount+"' name='primaryMemberName"+partCount+"' value='"+data.primaryMemberName+"'>"+
										"<input type='hidden' id='primaryMemberMinistry"+partCount+"' name='primaryMemberMinistry"+partCount+"' value='"+data.primaryMemberMinistry+"'>"+
										"<input type='hidden' id='primaryMemberDesignation"+partCount+"' name='primaryMemberDesignation"+partCount+"' value='"+data.primaryMemberDesignation+"'>"+
										"<input type='hidden' id='primaryMemberSubDepartment"+partCount+"' name='primaryMemberSubDepartment"+partCount+"' value='"+data.primaryMemberSubDepartment+"'>"+
										"<input type='hidden' id='substituteMember"+partCount+"' name='substituteMember"+partCount+"' value='"+data.substituteMember+"'>"+
										"<input type='hidden' id='substituteMemberMinistry"+partCount+"' name='substituteMemberMinistry"+partCount+"' value='"+data.substituteMemberMinistry+"'>"+
										"<input type='hidden' id='substituteMemberDesignation"+partCount+"' name='substituteMemberDesignation"+partCount+"' value='"+data.substituteMemberDesignation+"'>"+
										"<input type='hidden' id='substituteMemberSubDepartment"+partCount+"' name='substituteMemberSubDepartment"+partCount+"' value='"+data.substituteMemberSubDepartment+"'>"+
										"<input type='hidden' id='publicRepresentative"+partCount+"' name='publicRepresentative"+partCount+"' value='"+data.publicRepresentative+"'>"+
										"<input type='hidden' id='publicRepresentativeDetail"+partCount+"' name='publicRepresentativeDetail"+partCount+"' value='"+data.publicRepresentativeDetail+"'>"+ 
										"<input type='hidden' id='partReporter"+partCount+"' name='partReporter"+partCount+"' value='"+data.reporter+"'>"+
										 "<input type='hidden' id='partDeviceType"+partCount+"' name='partDeviceType"+partCount+"' value='"+data.deviceType+"'>"+
										 "<input type='hidden' id='deviceId"+partCount+"' name='deviceId"+partCount+"' value='"+data.deviceId+"'>"+
										 "<input type='hidden' id='partEntryDate"+partCount+"' name='partEntryDate"+partCount+"' value='"+data.entryDate+"'>"+
										"<input type='hidden' id='isConstituencyRequired"+partCount+"' name='isConstituencyRequired"+partCount+"' value='"+data.isConstituencyRequired+"'>"+
										"<input type='hidden' id='isSubstitutionRequired"+partCount+"' name='isSubstitutionRequired"+partCount+"' value='"+data.isSubstitutionRequired+"'>"+
										"<input type='hidden' id='isInterrupted"+partCount+"' name='isInterrupted"+partCount+"' value='"+data.isInterrupted+"'>"+
										"<input type='hidden' id='partRevisedContent"+partCount+"' name='partRevisedContent"+partCount+"' value='"+ procContent +"'>"+ 
										"<input type='hidden' id='partProceeding"+partCount+"' name='partProceeding"+partCount+"' value='"+$('#id').val()+"'>"+
									  	"</div>"+
									  	"</div>"+
									  	"</div>";
								/** After the part is saved, the content and member name is displayed for editing purpose if needed
								 ** Here if the saved part is first part, the content is appended to the first buttons
								 ** else its appended to previous part **/	  	
							  	if(currentCount==0){
							  		$('#addDeleteButtons'+currentCount).after(savetext);
							  		
							  	}else{
							  		$('#part'+currentCount).after(savetext);
							  	}
							  	newWindowOpen = false;
							  	//addPart(partCount);
							   	$('#formattedPrimaryMember'+partCount).focus();
							    
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
					  $.post("proceeding",
								$("form[action='proceeding']").serialize(),function(data){
						 	 $('.tabContent').html(data);
		   					$('html').animate({scrollTop:0}, 'slow');
		   				 	$('body').animate({scrollTop:0}, 'slow');
		   				 	newWindowOpen = false;
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
				   $.unblockUI();	
				});
			  
			  /**Registering events for dynamic content**/

			 /***Add Part***/
			 	loadAddPartClickEvent();
			
			 /****Add Bookmark****/
			 	loadBookmarkClickEvent();
					  
		 	 /****Edit Content****/
		 	 	/****Edit Proceeding Content****/
			 $('.editableContent').live('dblclick',function(e){
				  if (!editWindowOpen && !newWindowOpen) {			
						controlId=$(this).attr('id');
						 var divId = this.id;
						$('#previousContent').val($(this).html());
						var text = window.getSelection().toString()
						var newContent = $(this).html();
						newContent = newContent.replace(/<span class='highlightedText'.*?>/g,"");
						newContent = newContent.replace(text,"<span class='highlightedText' style='background: yellow;'>"+text+"</span>"); 
						 
							var inputbox = "<textarea id='editContent' class='inputbox wysiwyg sTextarea'>"+newContent+"</textarea>"; 
							$(this).html(inputbox); 
							editWindowOpen=true;
							$('#editContent').wysiwyg({
								 resizeOptions: {maxWidth: 600},
								 controls:{
									 fullscreen: {
											visible: true,
											hotkey:{
												"ctrl":1|0,
												"key":122
											},
											exec: function () {
												if ($.wysiwyg.fullscreen) {
													$.wysiwyg.fullscreen.init(this);
												}
											},
											tooltip: "Fullscreen"
										},
									 },
								 events: {
									keypress: function(event) {
										if (event.ctrlKey || event.metaKey) {
									        switch (String.fromCharCode(event.which).toLowerCase()) {
									        case 's':
									            event.preventDefault();
									            save();	
									            break;
									        case 'w':
									            event.preventDefault();
									            saveAndHide();	
									            break;
									       }
										}
										
										var keyCode = event.keyCode || event.which; 
										if (keyCode == 9) { 
											event.preventDefault(); 
										    insertAtCursor("editContent-wysiwyg-iframe",'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', 0);
										} 
									}
								 }
							});
							$("textarea.inputbox").focus(); 
							$("div.wysiwyg").css('width','700px');
							$('#editContent-wysiwyg-iframe').contents().find("body").css("line-height","200%");
						} 	
					
				});
			 
			 /****Edit Other Part Details****/
			 $('.editDetails').live('dbclick',function(){
				 	if(!editWindowOpen && !newWindowOpen){
				 		editWindowOpen = true;
						var divId = this.id;
						var tempId = divId.split('member');
						var divText= 
						 "<div id='editDetail' style='border:2px solid;height:170px;width: 900px;'>"+
						 "<form action='proceeding/part/updateMemberDetail'>"+
					 		"<p align='right' style='margin-top: 5px;margin-right:15px;'>"+
					    		"<a href='javascript:void(0)' id='editPublicLink'  class='imgLink publicLink' title='Public'>PU</a>"+
					 		"</p>"+
					 		"<p style='margin-top:5px;'>"+
							 "<label class='small'>"+$('#roleMessage').val()+"</label>"+
							 "<select name='editChairPersonRole' id='editChairPersonRole' class='sSelect'>"+
								 $('#roleMaster').html()+
						     "</select>"+
						     "<label class='small' style='margin-left: 110px;' >"+$('#chairPersonMessage').val()+"</label>"+
						 	 "<select name='editChairPerson' id='editChairPerson' class='sSelect' >"+
					    	 "</select>"+
					     	"</p>"+
					     	"<div id='member'>"+
						     "<p style='margin-top:5px;'>"+
						         "<label class='small'>"+$('#primaryMemberNameMessage').val()+"</label>"+
						         "<input type='text' class='autosuggest editFormattedMember sText' name='editFormattedPrimaryMember' id='editFormattedPrimaryMember' value='"+$('#primaryMemberName'+tempId[1]).val()+"' />"+
							     "<input name='editPrimaryMember' id='editPrimaryMember' type='hidden' value='"+$('#primaryMember'+tempId[1]).val()+"'/>"+
							 "</p>"+
							 "<p class='minister' >"+
					             "<label class='small'>"+$('#primaryMemberDesignationMessage').val()+"</label>"+
					             "<select name='editPrimaryMemberDesignation' id='editPrimaryMemberDesignation' class='sSelect'>"+
							      	$('#designationMaster').html()+
							      "</select>"+
							      "<label class='small' style='margin-left: 110px;'>"+$('#primaryMemberSubDepartmentMessage').val()+"</label>"+
							      "<select name='editPrimaryMemberSubDepartment' id='editPrimaryMemberSubDepartment'  class='sSelect'>"+
							     	 $('#subDepartmentMaster').html()+
							      "</select>"+
				             "</p>"+
				             "<p>"+
							     "<label  class='small' >"+$('#isConstituencyRequiredMessage').val()+"</label>"+
							     "<input type='checkbox' id='editIsConstituencyRequired' name='editIsConstituencyRequired' class='sCheck'  >"+
							     "<label  class='small' style='margin-left: 285px;'>"+$('#isSubstitutionRequiredMessage').val()+"</label>"+
							     "<input type='checkbox' id='editIsSubstitutionRequired' name='editIsSubstitutionRequired' class='sCheck'  >"+
						     "</p>"+
						     "</div>"+
						     "<div id='public' style='display:none;'>"+
						     	"<p>"+
						     	"<label class='small'>"+$('#publicRepresentativeMessage').val()+"</label>"+
					             "<input type='text' class='sText' name='editPublicRepresentative' id='editPublicRepresentative'/>"+
						     	"</p>"+
						     	"<p>"+
						     	"<label class='small'>"+$('#publicRepresentativeDetailMessage').val()+"</label>"+
							     "<textarea class='sTextArea' name='editPublicRepresentativeDetail' id='editPublicRepresentativeDetail'/>"+
						     	"</p>"+
						     "</div>"+
						     	"<h2></h2>"+
								"<p class='tright'>"+
									"<input id='submit' type='button' value='submit' class='butDef submit'>"+
								"</p>"+
								"<input type='hidden' id='editPartId' name='editPartId' value='"+$('#partId'+tempId[1]).val()+"'/>"+
							  "</form>"+
						"</div>";
						$('#'+divId).empty();
						$('#'+divId).html(divText);
						
						/****Populate the chairperson based on the role selected by the user.****/
						 $('#editChairPersonRole').change(function(){
								$.get('ref/getchairperson?chairPersonRole='+$(this).val()+'&proceeding='+$('#proceedingId').val(),function(data){
									$("#editChairPerson").empty();
									var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
									if(data.length>0){
										if(data.length==1){
											chairPersonText+="<option value='"+data[0]+"' selected='selected'>"+data[0];
										}else{
											for(var i=0;i<data.length;i++){
												chairPersonText+="<option value='"+data[i]+"'>"+data[i];
											}
										}
									$("#editChairPerson").html(chairPersonText);
								}else{
									$("#editChairPerson").empty();
									var chairPersonText="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";				
									$("#editChairPerson").html(chairPersonText);	
								}
								});
							});
						
						/****Auto Setting of chaipersonrole,member,designation,department,isconstituency required of the current part****/
						 $('#editChairPersonRole option').each(function(){
							 if($(this).val()==$('#chairPersonRole'+tempId[1]).val()){
								 $(this).attr('selected',true);
								 $('#editChairPersonRole').change();
							 } 
					 	});
						 
								 
						 $('#editPrimaryMemberDesignation option').each(function(){
							 if($(this).val()==$('#primaryMemberDesignation'+tempId[1]).val()){
								 $(this).attr('selected',true);
							 } 
					 	});
						 
						 $('#editPrimaryMemberSubDepartment option').each(function(){
							 if($(this).val()==$('#primaryMemberSubDepartment'+tempId[1]).val()){
								 $(this).attr('selected',true);
							 } 
					 	});
						 
						 if($('#isConstituencyRequired'+tempId[1]).val()=='true'){
							 $('#editIsConstituencyRequired').attr('checked',true);
						 }
						 
						 if($('#isSubstitutionRequired'+tempId[1]).val()=='true'){
							 $('#editIsSubstitutionRequired').attr('checked',true);
						 }
						 
						 $( ".editFormattedMember").autocomplete({
								minLength:3,			
								source:'ref/member/getmembers?session='+$("#session").val(),
								select:function(event,ui){	
									id=this.id;
									$("#editPrimaryMember").val(ui.item.id);
								}	
						  });
						 
						 /**** Submit****/
						 $('#submit').click(function(){
							 $.post($("form[action='proceeding/part/updateMemberDetail']").attr('action'),
										$("form[action='proceeding/part/updateMemberDetail']").serialize(),function(data){
							 		if(data!=null &&(data.primaryMember!=null || data.publicRepresentative!=null)){
							 			var memberText='';
							 			if(data.primaryMemberName!=null && data.primaryMemberName!=''){
							 				$('#primayMember'+tempId[1]).val(data.primaryMember);
							 				$('#primayMemberName'+tempId[1]).val(data.primaryMemberName);
											if(data.constituency!=null && data.constituency!=''){
												$('#isConstituencyRequired'+tempId[1]).val(data.isConstituencyRequired);
												memberText= memberText + data.primaryMemberName + "("+ data.constituency +")";
											}else{
												memberText= memberText + data.primaryMemberName;
											}
											if(data.primaryMemberDesignation!=null && data.primaryMemberDesignation!=''){
												$('#primayMemberDesignation'+tempId[1]).val( data.primaryMemberDesignation);
												memberText= memberText + "("+data.primaryMemberDesignationName+")";
											}
											if(data.primaryMemberMinistry !=null && data.primaryMemberMinistry !=''){
												$('#primaryMemberMinistry'+tempId[1]).val( data.primaryMemberMinistry);
												memberText= memberText + "("+data.primaryMemberMinistryName+")";
											}
											if(data.primaryMemberSubDepartment !=null && data.primaryMemberSubDepartment !=''){
												$('#primaryMemberSubDepartment'+tempId[1]).val( data.primaryMemberSubDepartment);
												memberText= memberText + "("+data.primaryMemberSubDepartmentName+")";
											}
											if(data.substituteMember!=null && data.substituteMember!=''){
												$('#substituteMember'+tempId[1]).val(data.substituteMember);
												memberText= memberText + ","+data.substituteMemberName;
												if(data.substituteMemberDesignation!=null && data.substituteMemberDesignation!=''){
													$('#substituteMemberDesignation'+tempId[1]).val( data.substituteMemberDesignation);
													memberText= memberText + "("+data.substituteMemberDesignationName+")";
												}
												if(data.substituteMemberMinistry !=null && data.substituteMemberMinistry !=''){
													$('#substituteMemberMinistry'+tempId[1]).val( data.substituteMemberMinistry);
													memberText= memberText + "("+data.substituteMemberMinistryName+")";
												}
												if(data.substituteMemberSubDepartment !=null && data.substituteMemberSubDepartment !=''){
													$('#substituteMemberSubDepartment'+tempId[1]).val(data.substituteMemberSubDepartment);
													memberText= memberText + "("+data.substituteMemberSubDepartmentName+")";
												}
												memberText = memberText + $('#subsituteMemberText').val();
											}
										}else if(data.publicRepresentative != null && data.publicRepresentative !=''){
											$('#publicRepresentative'+tempId[1]).val(data.publicRepresentative);
											$('#publicRepresentativeDetail'+tempId[1]).val(data.publicRepresentativeDetail);
											memberText= memberText + data.publicRepresentative;
										}		
							 			$('#'+divId).empty;
							 			$('#'+divId).html(memberText);
							 			editWindowOpen = false;
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
					 });
				 }
			});
		 	 
			 /****Load Citation****/
		 	 	loadViewCitationClick(partCount);
			 /****To import the mainHeading and PageHeading of Interrupted Proceeding****/
				$('#interruptedProceeding').click(function(){
					var offset=$(this).offset();
					$("#interruptedProceedingDiv").css({'left':offset.left+10+'px','top':offset.top+10+'px','position':'absolute'});
					if($('#interruptedProceedingDiv').css('display')!='none'){
						$("#interruptedProceedingDiv").slideUp('slow');
					}else{
						$("#interruptedProceedingDiv").slideDown('slow');
					}
				});
			  
			  	$('#searchBy').change(function(){
					$.get('ref/getInterruptedProceedings?selectedDate='+$('#searchByDate').val()
							+"&searchBy="+$(this).val()
							+'&language='+$("#selectedLanguage").val()
							+'&session='+$("#session").val(),function(data){
						var text="";
						if(data.length>0){
							text=text+"<option value=' '>"+$('#pleaseSelectMsg').val()+"</option>"; 
						 for(var i=0;i<data.length;i++){
							 text=text+"<option value='"+data[i].value +"'>"+data[i].name+"</option>"; 
						 }
						 $('#iProceeding').html(text);
						 $('#iProceeding').css('display','inline');
					}
				});
			  });
				
				$('#iProceeding').change(function(){
					var strAction=$(this).val().split("#");
					$('#mainHeading'+partCount).wysiwyg('setContent',strAction[0]);
					$('#pageHeading'+partCount).wysiwyg('setContent',strAction[1]);
					$('#mainHeadingP').css('display','block');
					$('#pageHeadingP').css('display','block');
				});
				
				$('.publicLink').click(function(){
					$('#public').toggle();
					$('#member').toggle();
				});
				
				/****Special heading****/
				$('#specialHeading').click(function(){
					if($('.specialHeadingP').css('display')=='none'){
						$('.specialHeadingP').css('display','inline-block');
						$('#mainHeading'+partCount).wysiwyg('setContent',"");
						$('#pageHeading'+partCount).wysiwyg('setContent',"");
						$('.mainHeadingP').hide();
						$('.pageHeadingP').hide();
					}else{
						$('.specialHeadingP').css('display','none');
						$('#specialHeading'+partCount).wysiwyg('setContent',"");
						$('.mainHeadingP').show();
						$('.pageHeadingP').show();
					}
					
				});
				
				if($('#committeeMeeting').val()!=null && $('#committeeMeeting').val()!='' && $('#committeeMeeting').val()!='0'){
					$('#div1').css("display","none");
					$('#div2').css("display","none");
					$('#div3').css("display","none");
					$("#showOtherContent").css("display","none");
					$("#mainContent").css("width","910px");
					$($($("#div4").children()).children('div.wysiwyg')).css("width","880px");
				}
		      return partCount;	
		} 
	}
	
	/****Function Delete Part****/
	function deletePart(id,continous){	
		var partId=$('#partId'+id).val();
		$.prompt($('#confirmDeletePartMessage').val(),{
			buttons: {Ok:true, Cancel:false}, callback: function(v){
	        if(v){
	    		if(partId != ''){
	    		    $.delete_('proceeding/'+$("#id").val()+"/"+partId+'/delete', null, function(data, textStatus, XMLHttpRequest) {
	    			    if(data=='SUCCESS'){
	    			    $('#part'+id).remove();
	    		    	totalPartCount=totalPartCount-1;
	    				if(id==partCount){
	    					if(continous==null){
	    						partCount=partCount-1;
	    						console.log("PartCount="+partCount);
	    					}				
	    				}
	    			    }else{
	    				    $.prompt($("#deleteFailedMessage").val());
	    			    }
	    		    });	
	    		}else{
	    				console.log(id);
	    				if($('#part'+id).parent().attr("id")=='partForm'+id){
	    					$('#partForm'+id).remove();
	    				}else{
	    					$('#part'+id).remove();
	    				}
	    			/* 	console.log("previous count : " + totalPartCount);
	    				console.log("previous Part Count : " + partCount); */
	    				totalPartCount=totalPartCount-1;
	    				if(id==partCount){
	    					if(continous==null){
	    						partCount=partCount-1;
	    					}
	    				}
	    				newWindowOpen = false;
	    				/* console.log("After Part Count : " + partCount);
	    				console.log("After count : " + totalPartCount); */
	    				
	    		}
    			for(var i=id+1;i<totalPartCount;i++){
    	      		$('#partOrder'+i).val(i-1);
    	      	}
	        }
		}});
		return false;
	}
	/****Funtion to register the events and control of wysiwyg****/
	function loadWysiwyg(){
		$('.proceedingContentwysiwyg').wysiwyg({
		  resizeOptions: {maxWidth: 600},
		  controls:{
					fullscreen: {
						visible: true,
						hotkey:{
							"ctrl":1|0,
							"key":122
						},
						exec: function () {
							if ($.wysiwyg.fullscreen) {
								$.wysiwyg.fullscreen.init(this);
							}
						},
						tooltip: "Fullscreen"
					}
		    },
			events: {
				keypress: function(event) {
					var idval = document.getElementsByTagName('iframe')[3].id;
					var keyCode = event.keyCode || event.which; 
					if (keyCode == 9) { 
						event.preventDefault(); 
					    insertAtCursor(idval,'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;', 0);
					} 
					
					if (event.ctrlKey  || event.metaKey) {
				        switch (String.fromCharCode(event.which).toLowerCase()) {
					        case 's':
					            event.preventDefault();
					            $(".saveButton").click();
					            break;
				        }
				    }
				
				}
			}
				
		 });
		 
		 $('.proceedingContentwysiwyg').each(function(){
	  		var idval = this.id;
	  		$('#'+idval+'-wysiwyg-iframe').contents().find("body").css("line-height","200%");
	  		//$('#'+idval+'-wysiwyg-iframe').contents().find("body").css("font-s","200%");
	  		$('#'+idval).addClass("wysiwyg");
	  		$('<input>').attr({
			    type: 'hidden',
			    id: 'copyOf'+idval,
	  			value: $('#'+idval).val()
			}).appendTo($('#'+idval));
	  	});
	     
	    $("div.wysiwyg").css('left','10px');
     	$("div.wysiwyg").css('top','10px');
     	$("div.wysiwyg").css('min-width','290px');	     	
	}
	
	function loadImageCss(){
		$('.imageLink').css({'width':'14px','height':'14px','box-shadow':'2px 2px 5px #000000','border-radius':'5px','padding':'2px','border':'1px solid #000000'});
		$('.imgLink').css({'background-color':' #256498','border':'1px solid #FFFFFF','color':'#FFFFFF','font-size':' 10px','font-family':'verdana','text-decoration':'none','text-shadow':' 2px 1px 1px #000000','padding-left':'2px','box-shadow':'2px 1px 2px #000000','padding-right':'2px'});
		$('.searchBy').css({'width':'60px','font-size':'12px'});
		$('.searchByDate').css({'width':'70px','font-size':'12px'});
	}
	
	function getSelectionText() {
	    var text = "";
	    if (window.getSelection) {
	        text = window.getSelection().toString();
	    } else if (document.selection && document.selection.type != "Control") {
	        text = document.selection.createRange().text;
	    }
	    return text;
	}
	
	function getIframeSelectionText(iframe) {
	  var win = iframe.contentWindow;
	  var doc = iframe.contentDocument || win.document;

	  if (win.getSelection) {
		return win.getSelection().toString();
	  } else if (doc.selection && doc.selection.createRange) {
		return doc.selection.createRange().text;
	  }
	}
	
	function setCursor(nodeEx,pos){
	    var node = (typeof nodeEx == "string" ||nodeEx instanceof String) ? $("#"+nodeEx) : nodeEx;
	   
        if(!node){
            return false;
        }else if(node.createTextRange){
            var textRange = node.createTextRange();
            textRange.collapse(true);
            textRange.moveEnd(pos);
            textRange.moveStart(pos);
            textRange.select();
            return true;
        }else if(node.setSelectionRange){
            node.setSelectionRange(pos,pos);
            return true;
        }
        return false;
    }
	
	/**Save the Edited Part and Hide the Text Editor**/
	function saveAndHide(){
		var content = $("#editContent").val();
		var newContent=content.replace(/<span.*?>/g,"");
		newContent=newContent.replace(/<\/span>/g,"");
		//newContent=newContent.replace(/line-height: 200%.*?/g,"");
		var elementId=$('#'+controlId).attr('class').split(' ')[2];
		var tempId=elementId.split('-');
		$("#undoCount").val(parseInt($("#undoCount").val()) + 1);			
		if($("#undoCount").val()=='1'){
			$("#redoCount").val('0');
				$(".pprp").empty();
				$(".pprp").html('classes');
		}
		var params = "?editedBy="+$('#editingUser').val()+"&partField="+tempId[0]+"&partId="+tempId[1]+"&undoCount="+$("#undoCount").val();;
		$("#data").val(newContent);
		if($("#previousContent").val()!=newContent){
        	$.post($("form[action='proceeding/part/updatePart']").attr('action')+params,
					$("form[action='proceeding/part/updatePart']").serialize(),function(data){
				if(data!=null && data!=''){		
					$("#"+controlId).empty();
					var proceedingContentId = controlId.split("-");
					if(controlId.contains("proceedingContent")){
						$("#partRevisedContent"+proceedingContentId[1]).val(newContent);
					}
					$("#"+controlId).html(newContent); 
 					var undoData=$(".ppsp").html();
					var draftData=data.value;
					if(undoData=='classes' || undoData==''){
						$(".ppsp").empty();
						$(".ppsp").html(draftData);
					}else{
						$(".ppsp").html(undoData+";"+data.value); 
					}
				}else{
					$("#undoCount").val((parseInt($("#undoCount").val()) - 1));
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
		editWindowOpen=false;
		
	}
	
	function save(){
		var content = $("#editContent").val().trim();
		var newContent=content.replace(/<span.*?>/g,"");
		newContent=newContent.replace(/<\/span>/g,"");
		var elementId=$('#'+controlId).attr('class').split(' ')[2];
		var tempId=elementId.split('-');
		$("#undoCount").val(parseInt($("#undoCount").val()) + 1);			
		if($("#undoCount").val()=='1'){
			$("#redoCount").val('0');
				$(".pprp").empty();
				$(".pprp").html('classes');
		}
		var params = "?editedBy="+$('#editingUser').val()+"&partField="+tempId[0]+"&partId="+tempId[1]+"&undoCount="+$("#undoCount").val();;
		$("#data").val(newContent);
		if($("#previousContent").val()!=newContent){
        	$.post($("form[action='proceeding/part/updatePart']").attr('action')+params,
					$("form[action='proceeding/part/updatePart']").serialize(),function(data){
				if(data!=null && data!=''){		
					var undoData=$(".ppsp").html();
					var draftData=data.value;
					if(undoData=='classes' || undoData==''){
						$(".ppsp").empty();
						$(".ppsp").html(draftData);
					}else{
						$(".ppsp").html(undoData+";"+data.value); 
					}
				}else{
					$("#undoCount").val((parseInt($("#undoCount").val()) - 1));
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
		editWindowOpen=true;
	}
	
	/****function to import the content of devices by device No****/
	function loadDeviceNoChangeEvent(){
		$('.deviceNo').change(function(){
			var id=this.id;
			var mainId=id.split("deviceNo")[1];
			$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType'+mainId).val(),function(data){
				if(data!=null){
					$('#dContent').html(data);
					var pContent=$('#partContent'+mainId).val();
					$('#partContent'+mainId).wysiwyg('setContent',pContent+$('#deviceContent').html());
			   		$('#deviceId'+mainId).val(parseInt($('#dId').html()));
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
		});
	}
	
	/****Function to View Proceeding Citation****/
	function loadViewCitationClick(counter){
		$(".viewProceedingCitation").click(function(){
			$.get('proceeding/part/citations?counter='+counter,function(data){
			    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
		    },'html');
		    return false;
		});	
	}
	
	function loadAddPartClickEvent(){
		$('.addPartButton').live('click',function(event){
			var buttonId=this.id;
			var buttonCount=buttonId.split("addPart")[1];
			addPart(parseInt(buttonCount));
			return false;
		});
	}
	
	function loadBookmarkClickEvent(){
		$('.addBookmark').click(function(){
			var id=this.id;
			var count=id.split("bookmark");
			elementCount=count[count.length-1];
			$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count='+elementCount+'&currentPart='+$('#partId'+elementCount).val(),function(data){
				    $.fancybox.open(data, {autoSize: false, width:750, height:500});
			    },'html');
			    return false;
		});
	}
	
	
	
	/**Replace All Functionality**/
	function replaceAll(command,reedit){
		var params='proceedingId='+$('#proceedingId').val();
		$("#undoCount").val(parseInt($("#undoCount").val()) + 1);	
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
		$.post($("form[action='proceeding/part/replace']").attr('action')+"?"+params,
				$("form[action='proceeding/part/replace']").serialize(),function(data){
			if(data.length>0){
				var i;
				for(i = 0; i < data.length; i++){
					if(data[i][12]=='include'){
						$(".mainHeading-"+data[i][0]).empty();
						$(".mainHeading-"+data[i][0]).html(data[i][6]);
						$(".pageHeading-"+data[i][0]).empty();
						$(".pageHeading-"+data[i][0]).html(data[i][9]);
						$(".proceedingContent-"+data[i][0]).empty();
						$(".proceedingContent-"+data[i][0]).html(data[i][3]);
						var undoData=$("#ppsp"+data[i][0]).html();
						if(undoData=='classes' || undoData==''){
								$(".ppsp").empty();
								$(".ppsp").html(data[i][10]);
						}else{
							$(".ppsp").html(undoData+";"+data[i][10]);
						}
					}
				}
				$.unblockUI();
			}else{
				$.unblockUI();
				$("#undoCount").val((parseInt($("#undoCount").val()) - 1));	
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
	
	/**Undo Function**/
	function undoLastChange(){
		$(".ppsp").each(function(){
			var undoData=$(this).html();
			console.log(undoData);
			if(undoData!='classes'){
				var undoDataArray = undoData.split(";");
				var undoDataToWorkWith=undoDataArray[undoDataArray.length-1].split(":");
				$("#uniqueIdentifierForUndo").val(undoDataToWorkWith[2]);
				var ppId=undoDataToWorkWith[0];
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				$.post("proceeding/part/undolastchange/"+ppId,
						$("form[action='proceeding/part/replace']").serialize(),function(data){
					if(data && data.length > 0){
						var i;
						for(i = 0 ; i < data.length; i++){
							$(".mainHeading-"+data[i].id).empty();
							$(".mainHeading-"+data[i].id).html(data[i].mainHeading);
							$(".pageHeading-"+data[i].id).empty();
							$(".pageHeading-"+data[i].id).html(data[i].pageHeading);
							$(".proceedingContent-"+data[i].id).empty();
							$(".proceedingContent-"+data[i].id).html(data[i].content);
															
							var redoCountX=undoDataArray[undoDataArray.length-1].split(":")[0];
							if(redoCountX==''){
								redoCountX='0';
							}
							
							
							if(i==0){
								var pprpData=$(".pprp").html();
								if(pprpData=='classes' || pprpData==''){
									$(".pprp").empty();
									$(".pprp").html(undoDataArray[undoDataArray.length-1]);
									$("#redoCount").val(parseInt($("#redoCount").val())+1);
								}else{
									$(".pprp").html(pprpData+";"+undoDataArray[undoDataArray.length-1]);
									$("#redoCount").val(parseInt($("#redoCount").val())+1);
								}
								
								$("#undoCount").val(parseInt($("#undoCount").val())-1);
							}
							
							var html="";
							if(undoDataArray.length>1){
								html=$(".ppsp").html().replace(";"+undoDataArray[undoDataArray.length-1],"");
							}else{
								html=$(".ppsp").html().replace(undoDataArray[undoDataArray.length-1],"");
							}
							$(".ppsp").html(html);
						}
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
			}
		});
	}
	
	
	/**Redo Function**/
	
	function redoLastChange(){
		$(".pprp").each(function(){
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			var redoData=$(this).html();
			if(redoData!='classes'){
				var redoDataArray = redoData.split(";");
				var redoDataToWorkWith=redoDataArray[redoDataArray.length-1].split(":");
				$("#uniqueIdentifierForRedo").val(redoDataToWorkWith[2]);
				var html="";
				if(redoDataArray.length>1){
					html=$(this).html().replace(";"+redoDataArray[redoDataArray.length-1],"");
				}else{
					html=$(this).html().replace(redoDataArray[redoDataArray.length-1],"");
					
				}
				var redoData=$(".pprp").html();
				
				var ppspData=$(".ppsp").html();
				if(ppspData=='classes' || ppspData==''){
					$(".ppsp").empty();
					$(".ppsp").html(redoDataArray[redoDataArray.length-1]);
				}else{
					$(".ppsp").html(ppspData+";"+redoDataArray[redoDataArray.length-1]);
				}
				$("#urData").val(redoDataArray[redoDataArray.length-1]);
				var ppId = redoDataToWorkWith[0];
				$(this).html(html);			
				$.post("proceeding/part/redolastchange/"+ppId,
						$("form[action='proceeding/part/replace']").serialize(),function(data){
					if(data){
						var i;
						for(i = 0; i < data.length; i++){		
							$(".mainHeading-"+data[i].id).empty();
							$(".mainHeading-"+data[i].id).html(data[i].mainHeading);
							$(".pageHeading-"+data[i].id).empty();
							$(".pageHeading-"+data[i].id).html(data[i].pageHeading);
							$(".proceedingContent-"+data[i].id).empty();
							$(".proceedingContent-"+data[i].id).html(data[i].content);
							if(i==0){
								$("#redoCount").val(parseInt($("#redoCount").val())-1);
								$("#undoCount").val(parseInt($("#undoCount").val())+1);
							}
						}
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
			}
		});
	}
	
	function insertAtCursor(iframename, text, replaceContents) {
	      if(replaceContents==null){replaceContents=false;}
	      if(!replaceContents){//collapse selection:
	         var sel=document.getElementById(iframename).contentWindow.getSelection();
	      	 sel.collapseToStart();
	      }
	      document.getElementById(iframename).contentWindow.document.execCommand('insertHTML', false, text);
	};
	
	
	</script>
	<style type="text/css">
		.imageLink{
			width: 14px;
			height: 14px;
			box-shadow: 2px 2px 5px #000000;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #000000; 
		} 
		
		.imageLink:hover{
			box-shadow: 2px 2px 5px #888888;
			border-radius: 5px;
			padding: 2px;
			border: 1px solid #888888; 
		}
		
		.bookmarkKey{
			margin-left: 162px;
			margin-top: 30px;
		}
		
		.fields ul{
			background: none !important;
			background: white !important;
		}
		
		.searchBy{
			width:60px;
			font-size: 12px;
		}
		
		.searchByDate{
			width:70px;
			font-size:12px;
		}
		
		.textDemo{
			margin-top: 20px; 
			display: none;
			position: fixed; 
			top: 120px; 
			background: scroll repeat-x #000000; 
			width: 610px;
			height: 265px;
			z-index: 10000; 
			box-shadow: 0px 2px 5px #888; 
			/* opacity:0.8; */
		}
		
		#reportIconsDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin-left: 50px; 
			border: 1px solid black; 
			/* The new syntax needed by standard-compliant browsers (Opera 12.1, IE 10, Fx 16 onwards), without prefix */
			background: #feffff; /* Old browsers */
			background: -moz-linear-gradient(top,  #feffff 0%, #d2ebf9 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#feffff), color-stop(100%,#d2ebf9)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #feffff 0%,#d2ebf9 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#feffff', endColorstr='#d2ebf9',GradientType=0 ); /* IE6-9 */
		}
		
		#replaceToolDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin: 10px 0px 0px 50px; 
			border: 1px solid black; 
			min-height: 25px;
			position: relative;
			/* The new syntax needed by standard-compliant browsers (Opera 12.1, IE 10, Fx 16 onwards), without prefix */
			background: #feffff; /* Old browsers */
			background: -moz-linear-gradient(top,  #feffff 0%, #d2ebf9 100%); /* FF3.6+ */
			background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#feffff), color-stop(100%,#d2ebf9)); /* Chrome,Safari4+ */
			background: -webkit-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Chrome10+,Safari5.1+ */
			background: -o-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* Opera 11.10+ */
			background: -ms-linear-gradient(top,  #feffff 0%,#d2ebf9 100%); /* IE10+ */
			background: linear-gradient(to bottom,  #feffff 0%,#d2ebf9 100%); /* W3C */
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#feffff', endColorstr='#d2ebf9',GradientType=0 ); /* IE6-9 */
		}
		
		.imgI:hover{
			border-radius: 32px;
			box-shadow: 2px 2px 2px #0E4269;
		}
		.imgN:hover{
			/*border-radius: 32px;*/
			box-shadow: 2px 2px 2px #0E4269;
		}
		
		div.wysiwyg{
			left: 1px;
			top: 16px;
			width: 600px;
		}
		
		 #committeeMemberNamesDiv {
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
		}
	</style>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${error!=''}">
		<h3 style="color: #FF0000;">${error}</h3>
	</c:if>
	<div class="fields clearfix watermark">
	<div style="width:900px;">
		<div id="replaceToolDiv" style="display:inline-block">
			<form action="proceeding/part/replace" method="post">
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.searchTerm" text="Find" /></label><input type="text" id="searchTerm" name="searchTerm" value="${searchTerm}" style="border-radius: 3px; border: 1px solid #000080;" />
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.replaceTerm" text="Replace With" /></label><input type="text" id="replaceTerm" name="replaceTerm" value="${replaceTerm}"  style="border-radius: 3px; border: 1px solid #000080;"/>
				<a href="javascript:void(0);" id="replaceAll" style="width: 70px; border: 1px solid #000080; text-decoration: none; text-align: center; color: #000080; padding: 1px; border-radius: 5px;"><spring:message code='editing.replace.replaceAll' text='Replace'></spring:message></a>
				<a href="javascript:void(0);" id="undo"><img src="./resources/images/undo.png" width="16px" /></a>&nbsp;&nbsp;<a href="javascript:void(0);" id="redo"><img src="./resources/images/redo.png" alt="Redo" width="16px"/></a>
				<input type="hidden" name="undoCount" id="undoCount" value="${undoCount}" />
				<input type="hidden" name="uniqueIdentifierForUndo" id="uniqueIdentifierForUndo" value="" />
				<input type="hidden" name="redoCount" id="redoCount" value="${redoCount}" />
				<input type="hidden" name="uniqueIdentifierForRedo" id="uniqueIdentifierForRedo" value="" />
				<input type="hidden" name="urData" id="urData" value="" />
			</form>
		</div>
		<c:if test="${committeeMeeting != null and committeeMeeting != ''}">
			<a href="javascript:void(0);" id="viewCommitteeMemberNamesDiv" style="border: 1px solid #000000; background-color: #657A8F; border-radius: 5px; color: #FFFFFF; text-decoration: none;"><spring:message code="question.clubbed.texts" text="C"></spring:message></a>
		</c:if>
		
	</div>
	 
	<form:form action="proceeding" method="PUT" modelAttribute="domain" id="proceedForm">
		
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="proceeding.edit.heading" text="Slot : "/>${slotName}	</h2>
	<br>
	<c:if test="${committeeMeeting != null and committeeMeeting != ''}">
		<spring:message code="proceeding.edit.heading.committeeName" text="Committee : "/>${committeeName}
		<br>
	</c:if>
	<spring:message code="proceeding.edit.heading.timing" text="Slot Time : "/>${slotStartTime} - ${slotEndTime}	
	<c:choose>
		<c:when test='${!(empty parts)}'>
			<div id='addDeleteButtons0'>
				<a href='javascript:void(0)' id='addPart0' class=' addPartButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>
				<!-- <a href="javascript:void(0)"  id="deletePart0" class="deletePartButton"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" onclick='deletePart(0);' /></a> -->
				<a href='javascript:void(0)'  id='deletePart0' class='deletePartButton'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' /></a>
			</div>
			<c:set var='count' value='1'></c:set>
			<c:set var='oldmainHeading' value=''></c:set>
			<c:set var="oldpageHeading"  value=''></c:set>
			<c:set var="oldspecialHeading" value=''></c:set>		
										
			<c:forEach items='${parts}' var='outer'>
			<div id='part${count}' class='abc'>
				<div id='dummyScreen' >
					<div class='myeditablePara '>
						<div class='content${count} ' id='content${count}' style='background-color: white;'>
							<div align='center' id='headings' >
								<c:choose>
									<c:when test="${outer.pageHeading!=null and outer.pageHeading!=''}">
										<%-- <c:if test="${oldpageHeading !=  outer.pageHeading}"> --%>
										<spring:message code='part.pageHeading'/> : 
										<span id='pageHeading-${count}' class='pageHeading${count} editableContent pageHeading-${outer.id}'>
											${outer.pageHeading}
										</span>
										<%-- </c:if> --%>
										<br>
									</c:when>
									<c:otherwise>
										<%-- <c:if test="${committeeMeeting != '' and committeeMeeting !=null }">
											<span id='pageHeading-${count}' class='pageHeading${count} editableContent pageHeading-${outer.id}'>
												<spring:message code='part.pageHeading'/> 
											</span>
										</c:if> --%>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${outer.mainHeading!=null and outer.mainHeading!='' }">
										<%-- <c:if test="${oldmainHeading !=  outer.mainHeading}"> --%>
										<spring:message code='part.mainHeading'/> 
										<span id='mainHeading-${count}' class='mainHeading${count} editableContent mainHeading-${outer.id} '>
											${outer.mainHeading}
		
										</span>
										<%-- </c:if> --%>
									</c:when>
									<c:otherwise>
										<%-- <c:if test="${committeeMeeting != '' and committeeMeeting !=null }">
											 <span id='mainHeading-${count}' class='mainHeading${count} editableContent mainHeading-${outer.id} '>
												<spring:message code='part.mainHeading'/>
											</span>
										</c:if> --%>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${outer.specialHeading!=null and outer.specialHeading!='' }">
										<%-- <c:if test="${oldspecialHeading !=  outer.specialHeading}"> --%>
										<span id='specialHeading-${count}' class='specialHeading${count} editableContent specialHeading-${outer.id} '> 
											${outer.specialHeading}
										</span>
										<%-- </c:if> --%>
									</c:when>
									<c:otherwise>
										<%-- <c:if test="${committeeMeeting != '' and committeeMeeting !=null }">
											 <span id='specialHeading-${count}' class='specialHeading${count} editableContent specialHeading-${outer.id} '> 
												<spring:message code='part.specialHeading' text="SH"/>
											 </span>
										</c:if> --%>
									</c:otherwise>
								</c:choose>
								<br>
							</div>
							<c:choose>
								<c:when test="${outer.primaryMember!=null and outer.primaryMember!=''}">
									<div class='member${count} editDetails' id='member${count}' style='display: inline-block;'>
										${outer.primaryMember.getFullname()} 
										<c:if test='${outer.isConstituencyRequired==true }'>
											<c:if test='${outer.primaryMember.findConstituency() !=null }'>
												(${outer.primaryMember.findConstituency().name})
											</c:if>
										</c:if>
										<c:if test='${outer.primaryMemberDesignation!=null}'>
											(${outer.primaryMemberDesignation.name})
										</c:if>
										<c:if test='${outer.primaryMemberMinistry!=null}'>
											(${outer.primaryMemberMinistry.name})
										</c:if>
										<c:if test='${outer.primaryMemberSubDepartment!=null}'>
												(${outer.primaryMemberSubDepartment.name})
										</c:if>
										<c:if test='${outer.substituteMember!=null }'>
											${outer.substituteMember.getFullname()} 
										</c:if>
										<c:if test='${outer.substituteMemberDesignation!=null}'>
											(${outer.substituteMemberDesignation.name})
										</c:if>
										<c:if  test='${outer.substituteMemberMinistry!=null }'>
											(${outer.substituteMemberMinistry.name})
										</c:if>
										<c:if test='${outer.substituteMemberSubDepartment!=null}'>
												(${outer.substituteMemberSubDepartment.name})
										</c:if>
											:
									</div>
								</c:when>
								<c:when test="${outer.publicRepresentative!=null and outer.publicRepresentative!=''}">
									${outer.publicRepresentative}
								</c:when>
								<c:otherwise>
									<div class='member${count} editDetails' id='member${count}' style='display: inline-block;' > <spring:message code='part.primaryMemberName' text='Member Name'/>   :</div>
								</c:otherwise>
							</c:choose>
							<div style='min-width:10px;width: 50px; display: inline-block;'>&nbsp;&nbsp;</div>
							<div class='proceedingContent${count} editableContent proceedingContent-${outer.id}' style='display:inline-block;text-align:justify !important;' id='proceedingContent-${count}'>
								${outer.revisedContent}
							</div>
						</div>
						<br>
						<div id='addDeleteButtons${count}'>
							<a href='javascript:void(0)' id='addPart${count}' class=' addPartButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>
							<a href='javascript:void(0)'  id='deletePart${count}' class=' deletePartButton'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' onclick='deletePart(${count});' /></a>
							<a href='javascript:void(0)'  id='bookmark${count}' class=' addBookmark'><img src='./resources/images/star_full.jpg' title='Bookmark' class='imageLink'/></a>
						</div>
						<label id="msg"></label>
						<input type='hidden' id='partId${count}' name='partId${count}' value='${outer.id}'>
						<input type='hidden' id='partVersion${count}' name='partVersion${count}' value='${outer.version}'>
						<input type='hidden' id='partLocale${count}' name='partLocale${count}' value='${domain.locale}'>
						<input type='hidden' id='partOrder${count}' name='partOrder${count}' value='${outer.orderNo}'>
						<c:set var="mainHeadingEscapingDoubleQuote" value="${fn:replace(outer.mainHeading, '\"', '&#34;')}" />
						<c:set var='mainHeadingContentEscapingSingleQuote' value='${fn:replace(mainHeadingEscapingDoubleQuote, "\'", "&#39;")}'/>
						<input type='hidden' id='mainHeading${count}' name='mainHeading${count}' value='${mainHeadingContentEscapingSingleQuote }'>
						<c:set var="pageHeadingEscapingDoubleQuote" value="${fn:replace(outer.pageHeading, '\"', '&#34;')}" />
						<c:set var='pageHeadingContentEscapingSingleQuote' value='${fn:replace(pageHeadingEscapingDoubleQuote, "\'", "&#39;")}' />
						<input type='hidden' id='pageHeading${count}' name='pageHeading${count}' value='${pageHeadingContentEscapingSingleQuote }'>
						<c:set var="specialHeadingEscapingDoubleQuote" value="${fn:replace(outer.specialHeading, '\"', '&#34;')}" />
						<c:set var='specialHeadingContentEscapingSingleQuote' value='${fn:replace(specialHeadingEscapingDoubleQuote, "\'", "&#39;")}' />
						<input type='hidden' id='specialHeading${count}' name='specialHeading${count}' value='${specialHeadingContentEscapingSingleQuote }'>
						<input type='hidden' id='chairPersonRole${count}' name='chairPersonRole${count}' value='${outer.chairPersonRole.id}'>
						<input type='hidden' id='chairPerson${count}' name='chairPerson${count}' value='${outer.chairPerson}'>
						<c:set var="proceedingContentEscapingDoubleQuote" value="${fn:replace(outer.proceedingContent, '\"', '&#34;')}" />
						<c:set var='proceedingContentEscapingSingleQuote' value='${fn:replace(proceedingContentEscapingDoubleQuote, "\'", "&#39;")}' />
						<input type='hidden' id='partContent${count}' name='partContent${count}' value='${proceedingContentEscapingSingleQuote }'>
						<input type='hidden' id='primaryMember${count}' name='primaryMember${count}' value='${outer.primaryMember.id}'>
						<input type='hidden' id='primaryMemberName${count}' name='primaryMemberName${count}' value='${outer.primaryMember.getFullname()}'>
						<input type='hidden' id='primaryMemberMinistry${count}' name='primaryMemberMinistry${count}' value='${outer.primaryMemberMinistry.id}'>
						<input type='hidden' id='primaryMemberDesignation${count}' name='primaryMemberDesignation${count}' value='${outer.primaryMemberDesignation.id}'>
						<input type='hidden' id='primaryMemberSubDepartment${count}' name='primaryMemberSubDepartment${count}' value='${outer.primaryMemberSubDepartment.id}'>
						<input type='hidden' id='substituteMember${count}' name='substituteMember${count}' value='${outer.substituteMember.id}'>
						<input type='hidden' id='substituteMemberMinistry${count}' name='substituteMemberMinistry${count}' value='${outer.substituteMemberMinistry.id}'>
						<input type='hidden' id='substituteMemberDesignation${count}' name='substituteMemberDesignation${count}' value='${outer.substituteMemberDesignation.id}'>
						<input type='hidden' id='substituteMemberSubDepartment${count}' name='substituteMemberSubDepartment${count}' value='${outer.substituteMemberSubDepartment.id}'>
						<input type='hidden' id='publicRepresentative${count}' name='publicRepresentative${count}' value='${outer.publicRepresentative}'>
						<input type='hidden' id='publicRepresentativeDetail${count}' name='publicRepresentativeDetail${count}' value='${outer.publicRepresentativeDetail}'>
						<input type='hidden' id='partReporter${count}' name='partReporter${count}' value='${outer.reporter.id}'>
						<input type='hidden' id='partDeviceType${count}' name='partDeviceType${count}' value='${outer.deviceType.id}'>
						<input type='hidden' id='partEntryDate${count}' name='partEntryDate${count}'>
						<input type='hidden' id='isConstituencyRequired${count}' name='isConstituencyRequired${count}' value='${outer.isConstituencyRequired}'>
						<input type='hidden' id='isInterrupted${count}' name='isInterrupted${count}' value='${outer.isInterrupted}'>
						<input type='hidden' id='isSubstitutionRequired${count}' name='isSubstitutionRequired${count}' value='${outer.isSubstitutionRequired}'>
						<c:set var="RevisedContentEscapingDoubleQuote" value="${fn:replace(outer.revisedContent, '\"', '&#34;')}" />
						<c:set var='RevisedContentEscapingSingleQuote' value='${fn:replace(RevisedContentEscapingDoubleQuote, "\'", "&#39;")}' />
						<input type='hidden' id='partRevisedContent${count}' name='partRevisedContent${count}' value='${RevisedContentEscapingSingleQuote}'>
						<input type='hidden' id='deviceId${count}' name='deviceId${count}' value='${outer.deviceId}'>
						<input type='hidden' id='partProceeding${count}' name='partProceeding${count}' value='${id}'>
						<c:set var="count" value="${count+1}"></c:set>	
						<c:set var="oldmainHeading" value="${outer.mainHeading}" /> 
						<c:set var="oldpageHeading" value="${outer.pageHeading}" /> 
						<c:set var="oldspecialHeading" value="${outer.specialHeading}" /> 				
					</div>
				</div>
		 	</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div id='addDeleteButtons0'>
				<a href="javascript:void(0)" id="addPart0" class=" addPartButton"><img src="./resources/images/add.jpg" title="Add Part" class="imageLink" /></a>
				<!-- <a href="javascript:void(0)"  id="deletePart0" class="deletePartButton"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" onclick='deletePart(0);'  /></a> -->
				<a href="javascript:void(0)"  id="deletePart0" class="deletePartButton"  style="margin-right:1px;"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" /></a>
				
			</div>
		</c:otherwise>
	</c:choose>	
	
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<input type="hidden" id="slot" value="${slotId}" name="slot"/>
	<input type="hidden" id="partCount" name="partCount" value="${partCount}"/>
	</form:form>
	<div id="undoStack" class="ppsp" style="display: none;">classes</div>
	<div id="redoStack" class="pprp" style="display: none;">classes</div>
	<form action="proceeding/part/updatePart" method="post">
	 	<input type="hidden" name="editedContent" id="data" value="demo" />
	</form>
		
	<select name="roleMaster" id="roleMaster" style="display: none;">
		<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${roles}" var="i">
		<c:choose>
			<c:when test="${role==i.id}">
				<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
			</c:when>
			<c:otherwise>
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</select>
	
	<select name="designationMaster" id="designationMaster" style="display: none;">
	<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${designations}" var="i">
		<c:choose>
			<c:when test="${designation==i.id}">
				<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
			</c:when>
			<c:otherwise>
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</select>
	
	<select name="ministryMaster" id="ministryMaster" style="display: none;">
	<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${ministries}" var="i">
		<c:choose>
			<c:when test="${ministry==i.id}">
				<option value="${i.id}" selected="selected"><c:out value="${i.dropdownDisplayName}"></c:out></option>
			</c:when>
			<c:otherwise>
				<option value="${i.id}"><c:out value="${i.dropdownDisplayName}"></c:out></option>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</select>
	
	<select name="deviceTypeMaster" id="deviceTypeMaster" style="display: none;">
	<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${deviceTypes}" var="i">
		<c:choose>
			<c:when test="${ministry==i.id}">
				<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
			</c:when>
			<c:otherwise>
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
			</c:otherwise>
		</c:choose>
		</c:forEach>
	</select>
	
	<select name="subDepartmentMaster" id="subDepartmentMaster" style="display: none;">
	<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
		<c:forEach items="${subDepartments}" var="i">
			<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
		</c:forEach>
	</select>
	
	<select name="partyMaster" id="partyMaster" style="display:none;">
		 <c:forEach items="${parties}" var="i">
		 	<option value="${i.id}">${i.name}</option>
		 </c:forEach>
	</select>
	
	<div id="textDemo" class="textDemo">
		<textarea id="ttA" class="proceedingContentwysiwyg">
			Proceedings
		</textarea>
	</div>

	<div id='dContent' style="display: none">
		
	</div>
	
	<div id='interruptedProceedingDiv' style='display:none;background:#10466e;border-radius:10px;padding:5px;'>
		<input id='searchByDate' type='text' class=' sText datemask searchByDate' style='margin-left:2px;'/>
		<select id='searchBy' class='sSelect searchBy' style='margin-left:2px;vertical-align:top;min-width:230px;'>
			<option class='searchBy' selected='selected' value=''><spring:message code='client.prompt.select' text='Please Select'/></option>
			<option class='searchBy' value='pageHeading'><spring:message code='part.pageHeadingMessage' text='Page heading'></spring:message></option>
			<option class='searchBy' value='mainHeading'><spring:message code='part.mainHeadingMessage' text='Main heading'></spring:message></option>
		</select>
		<hr>
		<select id='iProceeding' style='width:332px; display:none;' >
		</select>	
	</div>
	
	<!--To show the questionTexts of the clubbed questions -->
<div id="committeeMemberNamesDiv">
	<h1>Assistant Questio texts of clubbed questions</h1>
</div>
<div id="hideCommitteeMemberNamesDiv" style="background: #FF0000; color: #FFF; position: fixed; bottom: 0; right: 10px; width: 15px; border-radius: 10px; cursor: pointer;">&nbsp;X&nbsp;</div>
	
	<input id="ErrorMsg" type="hidden" name ="ErrorMsg" value="" />
	<input id="prevcontent" type="hidden" name ="prevContent" value="" />
	<input type="hidden" id="proceedingId" name="proceedingId" value="${proceeding}">
	<input type="hidden" id="editingUser" name="editingUser" value="${userName}">
	<input type="hidden" id="proceedingReporter" name="proceedingReporter" value="${reporter}">
	<input type="hidden" id="isInterruptedMessage" name="isInterruptedMessage" value="<spring:message code='part.isInterrupted' text='Is Interrupted'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="isConstituencyRequiredMessage" name="isConstituencyRequiredMessage" value="<spring:message code='part.isConstituencyRequired' text='Is Constituency Required'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="isSubstitutionRequiredMessage" name="isSubstitutionRequiredMessage" value="<spring:message code='part.isSubstitutionRequired' text='Is Substitution Required'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="specialHeadingMessage" name="specialHeadingMessage" value="<spring:message code='part.specialHeading' text='Special Heading'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="deviceTypeMessage" name="deviceTypeMessage" value="<spring:message code='part.deviceType' text='Device Type'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="deviceNoMessage" name="deviceNoMessage" value="<spring:message code='part.deviceNo' text='Device No'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="primaryMemberDesignationMessage" name="primaryMemberDesignationMessage" value="<spring:message code='part.designation' text='Designation'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="primaryMemberMinistryMessage" name="primaryMemberMinistryMessage" value="<spring:message code='part.ministry' text='Ministry'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="primaryMemberSubDepartmentMessage" name="primaryMemberSubDepartmentMessage" value="<spring:message code='part.subdepartment' text='Department'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="primaryMemberNameMessage" name="primaryMemberNameMessage" value="<spring:message code='part.memberName' text='Member'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="substituteMemberNameMessage" name="substituteMemberNameMessage" value="<spring:message code='part.substituteMemberName' text='Substitute Member'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="orderMessage" name="orderMessage" value="<spring:message code='part.order' text='Order'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="mainHeadingMessage" name="mainHeadingMessage" value="<spring:message code='part.mainHeading' text='Main heading'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="pageHeadingMessage" name="pageHeadingMessage" value="<spring:message code='part.pageHeading' text='Page heading'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="chairPersonMessage" name="chairPersonMessage" value="<spring:message code='part.chairPersonMessage' text='Chair Person'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="roleMessage" name="roleMessage" value="<spring:message code='part.roleMessage' text='Role'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="contentMessage" name="contentMessage" value="<spring:message code='part.contentMessage' text='Content'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="session" name="session" value="${session}"/>
	<input type="hidden" id="committeeMeeting" name="committeeMeeting" value="${committeeMeeting}"/>
	
	<c:set var="previousPartMainHeadingContentEscapingDoubleQuote" value="${fn:replace(previousPartMainHeading, '\"', '&#34;')}" />
	<c:set var='previousPartMainHeadingEscapingSingleQuote' value='${fn:replace(previousPartMainHeadingContentEscapingDoubleQuote, "\'", "&#39;")}' />
	<input type="hidden" id="previousPartMainHeading" name="previousPartMainHeading" value="${previousPartMainHeadingEscapingSingleQuote }">
	
	<c:set var="previousPartPageHeadingContentEscapingDoubleQuote" value="${fn:replace(previousPartPageHeading, '\"', '&#34;')}" />
	<c:set var='previousPartPageHeadingEscapingSingleQuote' value='${fn:replace(previousPartPageHeadingContentEscapingDoubleQuote, "\'", "&#39;")}' />
	<input type="hidden" id="previousPartPageHeading" name="previousPartPageHeading" value="${previousPartPageHeadingEscapingSingleQuote }">
	
	<c:set var="previousPartSpecialHeadingContentEscapingDoubleQuote" value="${fn:replace(previousPartSpecialHeading, '\"', '&#34;')}" />
	<c:set var='previousPartSpecialHeadingEscapingSingleQuote' value='${fn:replace(previousPartSpecialHeadingContentEscapingDoubleQuote, "\'", "&#39;")}' />
	<input type="hidden" id="previousPartSpecialHeading" name="previousPartSpecialHeading" value="${previousPartSpecialHeadingEscapingSingleQuote }">
	
	<input type="hidden" id="previousPartDeviceType" name="previousPartDeviceType" value="${previousPartDeviceType }">
	<input type="hidden" id="previousPartDeviceId" name="previousPartDeviceId" value="${previousPartDeviceId }">
	<input type="hidden" id="previousPartDeviceNumber" name="previousPartDeviceNumber" value="${previousPartDeviceNumber}">
	<input type="hidden" id="previousPartChairPersonRole" name="previousPartChairPersonRole" value="${previousPartChairPersonRole }">
	<input type="hidden" id="subsituteMemberText" name="subsituteMemberText" value="<spring:message code='part.subsituteMemberText' text='yanchya Tarfe'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="publicRepresentativeMessage" name="publicRepresentativeMessage" value="<spring:message code='part.publicRepresentative' text='Public Representative'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="publicRepresentativeDetailMessage" name="publicRepresentativeDetailMessage" value="<spring:message code='part.publicRepresentativeDetail' text='Public Representative Detail'></spring:message>" disabled="disabled"/>
	<input id="pleaseSelectMessage" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input id="confirmDeletePartMessage" value="<spring:message code='part.deleteMessage' text='Do You Want to Delete Part?'/>" type="hidden">
</body>
</html>