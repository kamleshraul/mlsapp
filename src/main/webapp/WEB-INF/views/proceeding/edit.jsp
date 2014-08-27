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
	var partCount=parseInt($('#partCount').val());
	var totalPartCount=0;
	var controlId='';
	totalPartCount=partCount+totalPartCount;
	
	$(document).ready(function(){
		
		loadWysiwyg();
		
		/***Add Part***/
		$('.addPartButton').click(function(){
			var buttonId=this.id;
			console.log("Button Id:"+buttonId);
			var buttonCount=buttonId.split("addPart")[1];
			console.log("buttonCount"+buttonCount);
			addPart(parseInt(buttonCount));
		});
		
		/***Edit part***/
		$('.editableContent').dblclick(function(e){
			 controlId=$(this).attr('id');
			 
			 showEditor(e);
			 console.log(controlId);
			 var text = window.getSelection().toString().trim();
			 var newContent = $('#'+controlId).html();
			 
			 console.log(text);
			 newContent = newContent.replace(/<span class='highlightedText'.*?>/g,"");
			 newContent = newContent.replace(text,"<span class='highlightedText' style='background: yellow;'>"+text+"</span>");
			$('#prevContent').val($('#'+controlId).html());
			$('#textDemo').children('div.wysiwyg').css('width','600px');
			$('#ttA').wysiwyg('setContent',newContent);
			
		});
		
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
		
		/****Add Bookmark****/
		$('.addBookmark').click(function(){
			var id=this.id;
			var count=id.split("bookmark");
			elementCount=count[count.length-1];
			$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count='+elementCount+'&currentPart='+$('#partId'+elementCount).val(),function(data){
				    $.fancybox.open(data, {autoSize: false, width:800, height:500});
			    },'html');
			    return false;
		});
		
		/***Registering the wysiwyg for a pop up texteditor***/
		$('#ttA').wysiwyg({
				controls:{
					hide: {
						visible: true,
						tags: ['hide'],
						exec: function () {
							hideEditor();
						},
						tooltip: "Hide"
				}
			}
		});
		
		
		 
	});
	
	/***Add Part***/
	function addPart(currentCount){
		orderCount=parseInt(orderCount)+1;
		var flag=false;
		partCount=partCount+1;
		contentNo="content"+partCount;
		totalPartCount=totalPartCount+1;
		var partText= "<div id='part"+partCount+"'>"+
		"<div id='otherContent' style='width:320px;display: inline-block;'>"+ 
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
			   		"<a href='javascript:void(0)' id='interruptedProceeding' class='imgLink' title='Interrupted Proceeding'>IP</a>"+			   "</p>"+
			   "<p>"+
			   		"<textarea class='proceedingContentwysiwyg' name='pageHeading"+partCount+"' id='pageHeading"+partCount+"'/>"+
			   "</p>"+
			    "<p style='margin-top:20px;'>"+
			   	 	"<textarea class='proceedingContentwysiwyg' name='mainHeading"+partCount+"' id='mainHeading"+partCount+"'/>"+
			    "</p>"+ 				     
			    "<p class='order"+partCount+"'style='display:none;'>"+
		     		"<label class='small'>"+$('#orderMessage').val()+"</label>"+
		     		"<input type='text' class='sInteger' name='partOrder"+partCount+"' id='partOrder"+partCount+"'/>"+
		     	"</p>"+
	     	 "</div>"+
		 "</div>"+
	     "<div id='mainContent' style='width:590px;display: inline-block;float:right;'>"+
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
	      "<a href='javascript:void(0)' id='addPart"+partCount+"' class=' addNewPartButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
		  "<a href='javascript:void(0)'  id='deletePart"+partCount+"' class=' deletePartButton' onclick='deletePart("+partCount+");'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' /></a>"+
		  "<a href='javascript:void(0)' id='savePart' class='saveButton'><img src='./resources/images/save.jpg' title='Save Part' class='imageLink' /></a>"+
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
		  	loadWysiwyg(contentNo);
		    loadImageCss();
		    loadDeviceNoChangeEvent();
		    /** The Main Heading, page Heading, DeviceType , Device of last part of previous slot is set **/
		  	if($('#previousPartMainHeading').val()!=null && $('#previousPartMainHeading').val()!=''){
		  		$('#mainHeading'+partCount).wysiwyg('setContent',$('#previousPartMainHeading').val());
	  		}
	  		if($('#previousPartPageHeading').val()!=null && $('#previousPartPageHeading').val()!=''){
	  			$('#pageHeading'+partCount).wysiwyg('setContent',$('#previousPartPageHeading').val());
	  		}
	  		if($('#previousPartDeviceType').val()!=null && $('#previousPartDeviceType').val()!=''){
	  			 $('#deviceType'+partCount+' option').each(function(){
	  				if($(this).val()==$('#previousPartDeviceType').val()){
	  					 $(this).attr('selected',true);
	  				 } 
	  			  });
	  		}
	  		if($('#previousPartDeviceNumber').val()!=null || $('#previousPartDeviceNumber').val()!=''){
	  			 $('#deviceNo'+partCount).val($('#previousPartDeviceNumber').val());
	  		}
	  		if($('#previousPartDeviceId').val()!=null || $('#previousPartDeviceId').val()!=''){
	  			$('#deviceId'+partCount).val($('#previousPartDeviceId').val());
	  		}
	  		
	  		 /** When the User insert a part in between two parts, the orderNo needs to be updated of the parts below the current inserted part**/
		    for(var i = currentCount+1;i<partCount;i++){
	      		$('#partOrder'+i).val(i+1);
	      	}
	    }else{
	    	$('#part'+currentCount).after(text);
	    	$('#partOrder'+partCount).val(currentCount+1);
	    	loadWysiwyg(contentNo);
		    loadImageCss();
		    loadDeviceNoChangeEvent();
		    $('#mainHeading'+partCount).wysiwyg('setContent',$('#mainHeading-'+currentCount).html());
		    $('#pageHeading'+partCount).wysiwyg('setContent',$('#pageHeading-'+currentCount).html());
		    
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
		  $($($("#div4").children()).children('div.wysiwyg')).css("width","560px");
		  
		  
		  /***Selecting Member By Party***/
		  $('#party').change(function(){
			 	$.get('proceeding/part/getMemberByPartyPage?partyId='+$(this).val()+'&partCount='+partCount,function(data){
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
		  $('#savePart').click(function(){
			  var parameters="?partCount="+partCount;
			  var savetext='';
			  /** if Flag is True then single part will be saved using form[action='proceeding/part/save']
			   ** else the entire proceeding is updated using form[action='proceeding']**/
			  if(flag){
				  $.post($("form[action='proceeding/part/save']").attr('action')+parameters,
							$("form[action='proceeding/part/save']").serialize(),function(data){
						if(data!=null){		
							if(data!=null){
								if($('#part'+partCount).parent().attr("id")=='partForm'+partCount){
									$('#partForm'+partCount).remove();
								}else{
									$('#part'+partCount).remove();
								}
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
								savetext=savetext+	
									"<br>"+
									"</div>";
								if(data.primaryMemberName!=null && data.primaryMemberName!=''){
									if(data.constituency!=null && data.constituency!=''){
										savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
										data.primaryMemberName+ 
										"("+ data.constituency +")"+
										":"+
										"</div>";
									}else{
										savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
										data.primaryMemberName+ 
										":"+
										"</div>";
									}
									
								}else if(data.publicRepresentative != null && data.publicRepresentative !=''){
									savetext= savetext + "<div class='member"+partCount+"' style='display: inline-block;'>"+
									data.publicRepresentative+ 
								":"+
								"</div>";
								}					  			
								savetext=savetext +	"<div style='min-width:10px;width: 50px; display: inline-block;'>&nbsp;&nbsp;</div>"+
									"<div class='proceedingContent"+partCount+" editableContent' style='display:inline-block;' id='proceedingContent"+partCount+"'>"+
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
									"<input type='hidden' id='pageHeading"+partCount+"' name='pageHeading"+partCount+"' value='"+data.pageHeading+"'>"+
									"<input type='hidden' id='chairPersonRole"+partCount+"' name='chairPersonRole"+partCount+"' value='"+data.memberrole+"'>"+
									"<input type='hidden' id='partContent"+partCount+"' name='partContent"+partCount+"' value='"+data.proceedingContent+"'>"+
									"<input type='hidden' id='primaryMember"+partCount+"' name='primaryMember"+partCount+"' value='"+data.primaryMember+"'>"+
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
									"<input type='hidden' id='isInterrupted"+partCount+"' name='isInterrupted"+partCount+"' value='"+data.isInterrupted+"'>"+
									"<input type='hidden' id='partRevisedContent"+partCount+"' name='partRevisedContent"+partCount+"' value='"+data.proceedingContent+"'>"+ 
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
						  	
						   	addPart(partCount);
						   	$('#formattedPrimaryMember'+partCount).focus();
						    
							}
						} else{
							alert("unsuccessful");
						} 
					}).fail(function(){
					});
			  }else{
				  $.post("proceeding",
							$("form[action='proceeding']").serialize(),function(data){
					 	 $('.tabContent').html(data);
	   					$('html').animate({scrollTop:0}, 'slow');
	   				 	$('body').animate({scrollTop:0}, 'slow');
				  });
			  }
			});
		  
		  /**Registering events for dynamic content**/
		   
		  /**Add part**/
		  $('#addPart'+partCount).click(function(){
			 	var buttonId=this.id;
				var buttonCount=buttonId.split("addPart")[1];
				addPart(parseInt(buttonCount));
		  }); 
		  
		  /**Add Bookmark**/
		  $('.addBookmark').click(function(){
				var id=this.id;
				var count=id.split("bookmark");
				elementCount=count[count.length-1];
				$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count='+elementCount+'&currentPart='+$('#partId'+elementCount).val(),function(data){
					    $.fancybox.open(data, {autoSize: false, width:800, height:500});
				    },'html');
				    return false;
			});
		  
		  /**Edit Content**/
		  $('.editableContent').dblclick(function(e){
				controlId=$(this).attr('id');
				showEditor(e);
				var text=window.getSelection().toString().trim();
				var newContent = $('#'+controlId).html();
				console.log(text);
				newContent=content.replace(/<span class='highlightedText'.*?>/g,"");
				newContent = newContent.replace(text,"<span class='highlightedText' style='background: yellow;'>"+text+"</span>");
				$('#prevContent').val($('#'+controlId).html());
				$('#textDemo').children('div.wysiwyg').css('width','600px');
				$('#ttA').wysiwyg('setContent',newContent);
				
			});
		 
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
				$.get('ref/getInterruptedProceedings?selectedDate='+$('#searchByDate').val()+"&searchBy="+$(this).val()+'&language='+$("#selectedLanguage").val(),function(data){
					var text="";
					if(data.length>0){
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
				$('#mainHeading'+partCount).wysiwyg('setContent',strAction[1]);
				$('#pageHeading'+partCount).wysiwyg('setContent',strAction[0]);
				$('#mainHeadingP').css('display','block');
				$('#pageHeadingP').css('display','block');
			});
			
			loadViewCitationClick(partCount);
			
			
			$('.publicLink').click(function(){
				$('#public').toggle();
				$('#member').toggle();
			});
			
			
			
	      return partCount;	
	}
	
	/****Function Delete Part****/
	function deletePart(id,continous){	
		var partId=$('#partId'+id).val();	
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
			if($('#part'+id).parent().attr("id")=='partForm'+id){
				$('#partForm'+id).remove();
			}else{
				$('#part'+id).remove();
			}
			totalPartCount=totalPartCount-1;
			if(id==partCount){
				if(continous==null){
					partCount=partCount-1;
				}
			}
		}
		for(var i=id+1;i<totalPartCount;i++){
      		$('#partOrder'+i).val(i-1);
      	}
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
						},
						Save: { 
				             visible: true, 
				             tags: ['tt'],  
				             exec: function(e){
				            	 	showIt=0;
				            	 	saveAndHide(e);
				             },
				             hotkey:{"ctrl":1,"shift":1,"key":88}			             
				        },
				        close:{
				        	visible:true,
				        	tags:['close'],
				        	exec:function(e){
				        		hideEditor();
				        	},
				        	hotkey:{"ctrl":1,"shift":1,"key":72}
				        }
				},
		 });
		 
		 $('.proceedingContentwysiwyg').change(function(e){
				contentNo=this.id;
				var idval = this.id;			
				if($('#'+idval).is('[readonly]')){
					if($('#'+idval).val()!=$('#copyOf'+idval).val()) {
						$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
					}
				} else {
					if($('#'+idval).val()=="<p></p>"){						
						$('#'+idval+'-wysiwyg-iframe').focus();				
						$('#'+idval+'-wysiwyg-iframe').contents().find('html').html("<br><p></p>");				
					}
				}
				maxHeight=$("#"+contentNo+"-wysiwyg-iframe").css("height");
			});
		 
	     $('.proceedingContentwysiwyg').each(function(){
	  		var idval = this.id;
	  		if($('#'+idval).is('[readonly]')){
	  			$('<input>').attr({
	  			    type: 'hidden',
	  			    id: 'copyOf'+idval,
	  			    value: $('#'+idval).val()
	  			}).appendTo($('#'+idval));
	  		}
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
	
	
	function hideEditor(){
		$("#textDemo").fadeOut();
		return false;
	}
	
	function showEditor(e){
		var pageWidth=$(window).width();
		var pageHeight=$(window).height();
		
		var clickX=e.clientX;
		var clickY=e.clientY;
		
		/****To Adjust the location of the editor when shown to the user by height****/ 
		if((clickY+$('#textDemo').height()) > pageHeight){
			var diffH=$('#textDemo').height() - clickY;
			if(diffH < 0){
				$('#textDemo').css('top', (pageHeight - clickY) + 'px');
			}else{
				$('#textDemo').css('top', diffH + 'px');
			}
		}else{
		
			var diffH=$('#textDemo').height() - clickY;
			$('#textDemo').css('top', clickY + 'px');
		}
		
		/****To Adjust the location of the editor when shown to the user by width****/
		if((clickX+$('#textDemo').width()) > pageWidth){
			var diffW=$('#textDemo').width() - clickX;
			
			if(diffW < 0){
				$('#textDemo').css('left', (pageWidth - clickX) + 'px');
			}else{
				$('#textDemo').css('left', diffW + 'px');
			}
			
		}else{
		
			var diffW=$('#textDemo').width() - clickX;
			$('#textDemo').css('left', clickX + 'px');
		}
		$("#textDemo").fadeIn();
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
		hideEditor();
		var content = $("#ttA").val().trim();
		var newContent=content.replace(/<span.*?>/g,"");
		newContent=newContent.replace("</span>","");
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
		if($("#prevcontent").val()!=newContent){
        	$.post($("form[action='proceeding/part/updatePart']").attr('action')+params,
					$("form[action='proceeding/part/updatePart']").serialize(),function(data){
				if(data!=null){		
					$("#"+controlId).empty();
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
			});
        }
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
				});
			}
		});
	}
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
	</style>
</head>
<body>
	<div class="fields clearfix watermark">
	<div id="replaceToolDiv">
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
	<form:form action="proceeding" method="PUT" modelAttribute="domain" id="proceedForm">
		
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="proceeding.edit.heading" text="Proceeding:ID"/>(${domain.id })		
	</h2>
	<c:choose>
		<c:when test='${!(empty parts)}'>
			<div id='addDeleteButtons0'>
				<a href="javascript:void(0)" id="addPart0" class=" addPartButton"><img src="./resources/images/add.jpg" title="Add Part" class="imageLink" /></a>
				<a href="javascript:void(0)"  id="deletePart0" class="deletePartButton"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" onclick='deletePart(0);' /></a>
			</div>
			<c:set var="count" value="1"></c:set>
			<c:forEach items="${parts}" var="outer">
			<div id="part${count}" class="abc">
				<div id="dummyScreen" >
					<div class="myeditablePara ">
						<div class="content${count} " id="content${count}" style="background-color: white;">
							<div align="center" id='headings' >
								<c:if test="${outer.pageHeading!=null and outer.pageHeading!=''}">
									<spring:message code='part.pageHeading'/> : 
									<span id='pageHeading-${count}' class='pageHeading${count} editableContent pageHeading-${outer.id}'>
										${outer.pageHeading}
									</span>
									<br>
								</c:if>
								<c:if test="${outer.mainHeading!=null and outer.mainHeading!='' }">
									<spring:message code='part.mainHeading'/> :
									<span id='mainHeading-${count}' class='mainHeading${count} editableContent mainHeading-${outer.id} '> 
										${outer.mainHeading}
									</span>
								</c:if>
								<br>
							</div>
							<c:if test="${outer.primaryMember!=null and outer.primaryMember!=''}"> 
								<div class="member${count}" style="display: inline-block;">
									${outer.primaryMember.getFullname()} 
									<c:if test="${outer.isConstituencyRequired==true }">
										<c:if test="${outer.primaryMember.findConstituency() !=null }">
											(${outer.primaryMember.findConstituency().name})
										</c:if>
									</c:if>
									<c:if test="${outer.primaryMemberMinistry!=null}">
										(${outer.primaryMemberMinistry.name})
										<c:if test="${outer.primaryMemberSubDepartment!=null}">
											(${outer.primaryMemberSubDepartment.name})
										</c:if>
									</c:if>
									<c:if test="${outer.substituteMember!=null }">
										${outer.substituteMember.getFullname()} 
									</c:if>
									<c:if  test="${outer.substituteMemberMinistry!=null }">
										(${outer.substituteMemberMinistry.name})
										<c:if test="${outer.substituteMemberSubDepartment!=null}">
											(${outer.substituteMemberSubDepartment.name})
										</c:if>
									</c:if>
										:
								</div>
							</c:if>
							<div style="min-width:10px;width: 50px; display: inline-block;">&nbsp;&nbsp;</div>
							<div class='proceedingContent${count} editableContent proceedingContent-${outer.id}' style='display:inline-block;text-align:justify !important;' id='proceedingContent-${count}'>
									${outer.revisedContent}
							</div>
						</div>
						<br>
						<div id='addDeleteButtons${count}'>
							<a href="javascript:void(0)" id="addPart${count}" class=" addPartButton"><img src="./resources/images/add.jpg" title="Add Part" class="imageLink" /></a>
							<a href="javascript:void(0)"  id="deletePart${count}" class=" deletePartButton"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" onclick='deletePart(${count});' /></a>
							<a href="javascript:void(0)"  id="bookmark${count}" class=" addBookmark"><img src="./resources/images/star_full.jpg" title="Bookmark" class="imageLink"/></a>
						</div>
						<input type='hidden' id='partId${count}' name='partId${count}' value="${outer.id}">
						<input type='hidden' id='partVersion${count}' name='partVersion${count}' value="${outer.version}">
						<input type='hidden' id='partLocale${count}' name='partLocale${count}' value="${domain.locale}">
						<input type='hidden' id='partOrder${count}' name='partOrder${count}' value="${outer.orderNo}">
						<input type='hidden' id='mainHeading${count}' name='mainHeading${count}' value="${outer.mainHeading}">
						<input type='hidden' id='pageHeading${count}' name='pageHeading${count}' value="${outer.pageHeading}">
						<input type='hidden' id='chairPersonRole${count}' name='chairPersonRole${count}' value="${outer.chairPersonRole.id}">
						<input type='hidden' id='partContent${count}' name='partContent${count}' value='${outer.proceedingContent}'>
						<input type='hidden' id='primaryMember${count}' name='primaryMember${count}' value="${outer.primaryMember.id}">
						<input type='hidden' id='primaryMemberMinistry${count}' name='primaryMemberMinistry${count}' value="${outer.primaryMemberMinistry.id}">
						<input type='hidden' id='primaryMemberDesignation${count}' name='primaryMemberDesignation${count}' value="${outer.primaryMemberDesignation.id}">
						<input type='hidden' id='primaryMemberSubDepartment${count}' name='primaryMemberSubDepartment${count}' value="${outer.primaryMemberSubDepartment.id}">
						<input type='hidden' id='substituteMember${count}' name='substituteMember${count}' value="${outer.substituteMember.id}">
						<input type='hidden' id='substituteMemberMinistry${count}' name='substituteMemberMinistry${count}' value="${outer.substituteMemberMinistry.id}">
						<input type='hidden' id='substituteMemberDesignation${count}' name='substituteMemberDesignation${count}' value="${outer.substituteMemberDesignation.id}">
						<input type='hidden' id='substituteMemberSubDepartment${count}' name='substituteMemberSubDepartment${count}' value="${outer.substituteMemberSubDepartment.id}">
						<input type='hidden' id='publicRepresentative${count}' name='publicRepresentative${count}' value="${outer.publicRepresentative}">
						<input type='hidden' id='publicRepresentativeDetail${count}' name='publicRepresentativeDetail${count}' value="${outer.publicRepresentativeDetail}">
						<input type='hidden' id='partReporter${count}' name='partReporter${count}' value='${outer.reporter.id}'>
						<input type='hidden' id='partDeviceType${count}' name='partDeviceType${count}' value='${outer.deviceType.id}'>
						<input type='hidden' id='partEntryDate${count}' name='partEntryDate${count}'>
						<input type='hidden' id='isConstituencyRequired${count}' name='isConstituencyRequired${count}' value='${outer.isConstituencyRequired}'>
						<input type='hidden' id='isInterrupted${count}' name='isInterrupted${count}' value='${outer.isInterrupted}'>
						<input type='hidden' id='partRevisedContent${count}' name='partRevisedContent${count}'>
						<input type='hidden' id='deviceId${count}' name='deviceId${count}' value='${outer.deviceId}'>
						<input type='hidden' id='partProceeding${count}' name='partProceeding${count}' value='${id}'>
						<c:set var="count" value="${count+1}"></c:set>	
					</div>
				</div>
		 	</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div id='addDeleteButtons0'>
				<a href="javascript:void(0)" id="addPart0" class=" addPartButton"><img src="./resources/images/add.jpg" title="Add Part" class="imageLink" /></a>
				<a href="javascript:void(0)"  id="deletePart0" class="deletePartButton"><img src="./resources/images/delete.jpg" title="Delete Part" class="imageLink" onclick='deletePart(0);' /></a>
			</div>
		</c:otherwise>
	</c:choose>	
	
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<form:hidden path="slot" value="${slot}"/>
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
				<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
			</c:when>
			<c:otherwise>
				<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
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
			<option class='searchBy' selected='selected' value='pageHeading'><spring:message code='part.pageHeadingMessage' text='Page heading'></spring:message></option>
			<option class='searchBy' value='mainHeading'><spring:message code='part.mainHeadingMessage' text='Main heading'></spring:message></option>
		</select>
		<hr>
		<select id='iProceeding' style='width:332px; display:none;' >
		</select>	
	</div>
	
	<input id="prevcontent" type="hidden" name ="prevContent" value="" />
	<input type="hidden" id="proceedingId" name="proceedingId" value="${proceeding}">
	<input type="hidden" id="editingUser" name="editingUser" value="${userName}">
	<input type="hidden" id="proceedingReporter" name="proceedingReporter" value="${reporter}">
	<input type="hidden" id="isInterruptedMessage" name="isInterruptedMessage" value="<spring:message code='part.isInterrupted' text='Is Interrupted'></spring:message>" disabled="disabled"/>
	<input type="hidden" id="isConstituencyRequiredMessage" name="isConstituencyRequiredMessage" value="<spring:message code='part.isConstituencyRequired' text='Is Constituency Required'></spring:message>" disabled="disabled"/>
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
	<input type="hidden" id="session" value="${session}"/>
	<input type="hidden" id="previousPartMainHeading" name="previousPartMainHeading" value="${previousPartMainHeading }">
	<input type="hidden" id="previousPartPageHeading" name="previousPartPageHeading" value="${previousPartPageHeading }">
	<input type="hidden" id="previousPartDeviceType" name="previousPartDeviceType" value="${previousPartDeviceType }">
	<input type="hidden" id="previousPartDeviceId" name="previousPartDeviceId" value="${previousPartDeviceId }">
	<input type="hidden" id="previousPartDeviceNumber" name="previousPartDeviceNumber" value="${previousPartDeviceNumber}">

	<input type="hidden" id="publicRepresentativeMessage" name="publicRepresentativeMessage" value="<spring:message code='part.publicRepresentative' text='Public Representative'></spring:message>" disabled="disabled"/>
<input type="hidden" id="publicRepresentativeDetailMessage" name="publicRepresentativeDetailMessage" value="<spring:message code='part.publicRepresentativeDetail' text='Public Representative Detail'></spring:message>" disabled="disabled"/>
</body>
</html>