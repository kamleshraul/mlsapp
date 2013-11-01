<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="proceeding.edit" text="Proceedings"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<style type="text/css">
		.imageLink{
			width: 16px;
			height: 16px;
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
	</style>
	<script type="text/javascript">
		
	/****Global Variable****/
		var test="";	
		var contentNo="";
		var partIndex=$("select option:selected").val();
		var partCount=parseInt($('#partCount').val());
		var totalPartCount=0;
		totalPartCount=partCount+totalPartCount;
		var orderCount=0;
		var chairPerson="";
		var role="";
		var elementCount=0;
		var keycode=new Array();
		var mainContent="";
		var finalContent="";
		var j=0;
		var currentContent="";
		var contentToBeReplaced="";
		var lineCount=1;
		var maxHeight = '';
		
		/****Dynamic Part Addition****/
		function addPart(currentCount){
			orderCount=parseInt(orderCount)+1;
			partCount=partCount+1;
			contentNo="content"+partCount;
			totalPartCount=totalPartCount+1;
			var text="<div id='part"+partCount+"'>"+
						  "<p align='right'>"+
						  "<a href='javascript:void(0)' id='mainHeadingLink"+partCount+"' class='mainHeadingLink'><img src='./resources/images/IcoMainHeading.jpg' title='Main Heading' class='imageLink' /></a>"+
						  "<a href='javascript:void(0)'  id='pageHeadingLink"+partCount+"' class='pageHeadingLink'><img src='./resources/images/IcoPageHeading.jpg' title='Page Heading' class='imageLink' /></a>"+
						  "<a href='javascript:void(0)' id='addBookmark"+partCount+"' class='addBookmark' ><img src='./resources/images/IcoBookMark.jpg' title='Bookmark' class='imageLink' /></a>"+
						  "<a href='javascript:void(0)' id='viewProceedingCitation"+partCount+"' class='viewProceedingCitation'><img src='./resources/images/IcoCitation.jpg' title='Citation' class='imageLink' /></a>"+
						  "<a href='javascript:void(0)' id='addDevice"+partCount+"' class='addDevice'><img src='./resources/images/IcoDeviceType.jpg' title='Device' class='imageLink' /></a>"+
						  "<a href='javascript:void(0)' id='privateLink"+partCount+"' class='privateLink' style='display:none;'><img src='./resources/images/IcoPrivateMember.jpg' title='Private Member' class='imageLink' /> </a>"+
			      		  "<a href='javascript:void(0)' id='ministerLink"+partCount+"' class='ministerLink'><img src='./resources/images/IcoMinister.jpg' title='Minister' class='imageLink' /></a>"+
			      		  "<a href='javascript:void(0)' id='publicLink"+partCount+"' class='publicLink'><img src='./resources/images/IcoPublicRepresentative.jpg' title='Public' class='imageLink' /> </a>"+
			      		  "<a href='javascript:void(0)' id='substituteLink"+partCount+"'><img src='./resources/images/IcoSubstitute.jpg' title='In place of ' class='imageLink' /> </a>"+
						  "</p>"+
						  "<p>"+
						  "<label class='small'>"+$('#roleMessage').val()+"</label>"+
			              "<select name='chairPersonRole"+partCount+"' id='chairPersonRole"+partCount+"' class='sSelect'>"+
					      $('#roleMaster').html()+
					      "</select>"+
					      "</p>"+
			              "<p class='member"+partCount+"'>"+
					 	  "<label class='small'>"+$('#primaryMemberNameMessage').val()+"</label>"+
			              "<input type='text' class='autosuggest sText formattedMember' name='formattedprimaryMember"+partCount+"' id='formattedprimaryMember"+partCount+"'/>"+
					      "<input name='primaryMember"+partCount+"' id='primaryMember"+partCount+ "' type='hidden'/>"+
			              "</p>"+
					      "<p class='order"+partCount+"'style='display:none;'>"+
					      "<label class='small'>"+$('#orderMessage').val()+"</label>"+
					      "<input type='text' class='sInteger' name='order"+partCount+"' id='order"+partCount+"'/>"+
					      "</p>"+
					      "<p class='minister"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#primaryMemberDesignationMessage').val()+"</label>"+
			              "<select name='primaryMemberDesignation"+partCount+"' id='primaryMemberDesignation"+partCount+"' style='width:100px;'>"+
					      $('#designationMaster').html()+
					      "</select>"+
					      "</p>"+
					      "<p class='minister"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#primaryMemberMinistryMessage').val()+"</label>"+
			              "<select name='primaryMemberMinistry"+partCount+"' id='primaryMemberMinistry"+partCount+"' style='width:100px;'>"+
					      $('#ministryMaster').html()+
					      "</select>"+
					      "</p>"+
					      "<p class='substitute"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#substituteMemberNameMessage').val()+"</label>"+
			              "<input type='text' class='autosuggest sText formattedSubstituteMember' name='formattedSubstituteMember"+partCount+"' id='formattedSubstituteMember"+partCount+"'/>"+
					      "<input name='substituteMember"+partCount+"' id='substituteMember"+partCount+ "' type='hidden'/>"+
			              "</p>"+
			              "<p class='substitute"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#substituteMemberDesignationMessage').val()+"</label>"+
			              "<select name='substituteMemberDesignation"+partCount+"' id='substituteMemberDesignation"+partCount+"' style='width:100px;'>"+
					      $('#designationMaster').html()+
					      "</select>"+
					      "</p>"+
					      "<p class='substitute"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#substituteMemberMinistryMessage').val()+"</label>"+
			              "<select name='substituteMemberMinistry"+partCount+"' id='substituteMemberMinistry"+partCount+"' style='width:100px;'>"+
					      $('#ministryMaster').html()+
					      "</select>"+
					      "</p>"+
					      "<p class='public"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#publicRepresentativeMessage').val()+"</label>"+
			              "<input type='text' class='sText' name='publicRepresentative"+partCount+"' id='publicRepresentative"+partCount+"'/>"+
					      "</p>"+
					      "<p class='public"+partCount+"' style='display:none;'>"+
					      "<label class='wysiwyglabel'>"+$('#publicRepresentativeDetailMessage').val()+"</label>"+
					      "<textarea class='wysiwyg' name='publicRepresentativeDetail"+partCount+"' id='publicRepresentativeDetail"+partCount+"'/>"+
					      "</p>"+
					      "<p class='deviceType"+partCount+"' style='display:none;'>"+
			              "<label class='small'>"+$('#deviceTypeMessage').val()+"</label>"+
			              "<select name='deviceType"+partCount+"' id='deviceType"+partCount+"'class='deviceTypes sSelect' style='width:100px;'>"+
					      $('#deviceTypeMaster').html()+
					      "</select>"+
					      "</p>"+
					      "<p class='deviceType"+partCount+"' style='display:none;'>"+
						  "<label class='small'>"+$('#deviceNoMessage').val()+"</label>"+
						  "<input type='text' name='deviceNo"+partCount+"' id='deviceNo"+partCount+"' class='deviceNo sInteger'/>"+
						  "</p>"+
						  "<p class='starredQuestion"+partCount+"' style='display:none;'>"+
						  "<label class='small'>"+$('#starredQuestionNoMessage').val()+"</label>"+
						  "<input type='text' name='starredQuestionNo"+partCount+"' id='starredQuestionNo"+partCount+"' class='sInteger starredQuestionNo'/>"+
						  "</p>"+
						  "<p class='halfHourDiscussionFromQuestion"+partCount+"' style='display:none;'>"+
						  "<label class='small'>"+$('#halfHourDiscussionFromQuestionMessage').val()+"</label>"+
						  "<select id='halfHourDiscussionFromQuestionNo"+partCount+"' name='halfHourDiscussionFromQuestionNo"+partCount+"' class='sSelect halfHourDiscussionFromQuestionNo'></select>"+
						  "</p>"+
						  "<p class='mainHeadingP"+partCount+"' style='display:none;'>"+
					      "<label class='small'>"+$('#mainHeadingMessage').val()+"</label>"+
					      "<textarea class='sTextArea' name='mainHeading"+partCount+"' id='mainHeading"+partCount+"'/>"+
					      "<a href='javascript:void(0)' id='clearMainHeadingLink"+partCount+"' class='clearMainHeading'>clear</a>"+
					      "</p>"+
					      "<p class='pageHeadingP"+partCount+"' style='display:none;'>"+
					      "<label class='small'>"+$('#pageHeadingMessage').val()+"</label>"+
					      "<textarea class='sTextArea' name='pageHeading"+partCount+"' id='pageHeading"+partCount+"'/>"+
					      "<a href='javascript:void(0)' id='clearPageHeadingLink"+partCount+"' class='clearPageHeading'>clear</a>"+
					      "</p>"+
						  "<p>"+ 
					      "<label class='wysiwyglabel'>"+$('#contentMessage').val()+"</label>"+
					      "<textarea class='wysiwyg' name='content"+partCount+"' id='content"+partCount+"'/>"+
					      "</p>"+
					      "<input type='button' class='button' id='addPart"+partCount+"' value='"+$('#addPartMessage').val()+"' onclick='addPart("+partCount+");'>"+
					      "<input type='button' class='button' id='"+partCount+"' value='"+$('#deletePartMessage').val()+"' onclick='deletePart("+partCount+");'>"+
					      "<input type='hidden' id='partId"+partCount+"' name='partId"+partCount+"'>"+
					      "<input type='hidden' id='deviceId"+partCount+"' name='deviceId"+partCount+"'>"+
					      "<input type='hidden' id='partLocale"+partCount+"' name='partLocale"+partCount+"' value='"+$('#locale').val()+"'>"+
						  "<input type='hidden' id='partVersion"+partCount+"' name='partVersion"+partCount+"'>"+
						  "<input type='hidden' id='partReporter"+partCount+"' name='partReporter"+partCount+"'>"+
						  "<input type='hidden' id='partEntryDate"+partCount+"' name='partEntryDate"+partCount+"'>"+
						  "<input type='hidden' id='partRevisedContent"+partCount+"' name='partRevisedContent"+partCount+"'>"+
						  "<input type='hidden' id='partProceeding"+partCount+"' name='partProceeding"+partCount+"' value='"+$('#id').val() +"''>"+
						  "</div>"; 
					     
					      if(totalPartCount==1){
						  	 $('#addPart'+currentCount).after(text);
						  	$('#order'+partCount).val(currentCount+1);
					      }else{
						  	$('#part'+currentCount).after(text);
					     	$('#order'+partCount).val(currentCount+1);
					     	if($('#mainHeading'+currentCount).val()!=''){
					     		$('#mainHeading'+partCount).val($('#mainHeading'+currentCount).val());
					     	}
					     	if($('#pageHeading'+currentCount).val()!=''){
					     		$('#pageHeading'+partCount).val($('#pageHeading'+currentCount).val());
					     	}
					      	for(var i=currentCount+1;i<partCount;i++){
					      		$('#order'+i).val(i+1);
					      	}
					      }
					      $('#partCount').val(partCount); 
					      $('#order'+partCount).focus();
					       	     
					     
					      	loadWysiwyg(contentNo);
					       	hideAndShowDivOnLinkClick();
					     	loadDeviceNoChangeEvent();
							loadDeviceTypeChangeEvent();
							loadStarredQuestionNoChangeEvent();
							loadHalfHourDiscussionFromQuestionNoChangeEvent();
							loadMainHeadingToggleEvent();
							loadPageHeadingToggleEvent();
							loadClearLinkClickEvent();
							loadAutoSuggest();
							loadViewCitationClick();
											      	
					      if($('#mainHeading'+partCount).val()!=''){
					      		$('.mainHeadingP'+partCount).show();
					      	}else{
					      		$('.mainHeadingP'+partCount).hide();	
					      	}
					      	if($('#pageHeading'+partCount).val()!=''){
					      		$('.pageHeadingP'+partCount).show();
					      	}else{
					      		$('.pageHeadingP'+partCount).hide();
					      	}
					      	 $("#deviceType"+partCount).prepend("<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>");										
					      return partCount;		
		}

		/****FUnction Delete Part****/
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
					}				
				}
			    }else{
				    $.prompt($("#deleteFailedMessage").val());
			    }
		    });	
			}else{
				$('#part'+id).remove();
				totalPartCount=totalPartCount-1;
				if(id==partCount){
					if(continous==null){
						partCount=partCount-1;
					}
				}
			}
			for(var i=id+1;i<totalPartCount;i++){
	      		$('#order'+i).val(i-1);
	      	}
		}
		
		
		$(document).ready(function(){
			
			/****AddPart****/
			$('.addPartButton').click(function(){
				var buttonId=this.id;
				var buttonCount=buttonId.split("addPart")[1];
				addPart(parseInt(buttonCount));
			});
			
			$('#memberMaster').hide();
			$('#roleMaster').hide();

			loadWysiwyg(contentNo);
			loadAutoSuggest();
			loadDeviceNoChangeEvent();
			loadDeviceTypeChangeEvent();
			loadStarredQuestionNoChangeEvent();
			loadHalfHourDiscussionFromQuestionNoChangeEvent();
			loadMainHeadingToggleEvent();
			loadPageHeadingToggleEvent();
			hideAndShowDivOnLinkClick();
			loadClearLinkClickEvent();
			loadViewCitationClick();
					
			/****Add Bookmark****/
			$('.addBookmark').click(function(){
				var id=this.id;
				var count=id.split("addBookmark");
				elementCount=count[count.length-1];
				$.get('proceeding/part/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val()+'&count='+elementCount+'&currentPart='+$('#partId'+elementCount).val(),function(data){
					    $.fancybox.open(data, {autoSize: false, width:800, height:500});
				    },'html');
				    return false;
			});
			
			/****BookmarkKey ****/
			$('.bookmarkKey').click(function(){
				var s=contentNo.split("");
				var id=this.id;
				var bookmarkId=id.split("##");
				var iframe= document.getElementById(contentNo+'-wysiwyg-iframe');
				contentToBeReplaced=getIframeSelectionText(iframe);
				$.prompt($('#addBookmarkMessage').val(),{
					buttons: {Ok:true, Cancel:false}, callback: function(v){
			        if(v){
			        	$.post('proceeding/part/updatetext?bookmark='+bookmarkId[0]+'&textToBeAdded='+contentToBeReplaced+"&part="+$('#partId'+s[s.length-1]).val()+"&currentSlot="+$('#slot').val(),function(data){
							if(data!=null){
								alert("The Text has Been added for the bookmark successfully");
							}
						});
			        }
					}});			
		        return false;  
		   	 }); 
	
			/****Autosuggest menu for Masters(Fort,Ghat etc)****/
			$("#autosuggest_menu").click(function(e){
				var content=$(this).val();
				content=replaceString(content);
				$("#"+contentNo).wysiwyg("setContent",finalContent+content);
				mainContent=$("#"+contentNo).val();
				mainContent=replaceString(mainContent);
				$(this).css("display","none");
				$("#"+contentNo+"-wysiwyg-iframe").focus();
			});
			
			
			 /**** Right Click Menu ****/
			$(".bookmarkKey").contextMenu({
		        menu: 'contextMenuItems'
		    	},
		        function(action, el, pos) {
				var id=$(el).attr("id");
				if(action=='viewBookmarkDetails'){
					viewBookmarkDetail(id);	
				}
			});	
			
			/****Hiding the Divs which has empty value****/
			$('.minister').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			
			$('.member').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			$('.wysiwyg').each(function(){
				var wysiwygVal=$(this).val();
				var wywsiwygId=this.id;
				if(wywsiwygId.contains('publicRepresentativeDetail')){
					if(wysiwygVal.trim()=='<p></p>'){
						var wywsiwygClass=$(this).parent().eq(0).attr('class').split(" ")[0];
						$("."+wywsiwygClass).hide();
					}
				}
			});
			$('.public').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			$('.substitute').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			$('.mainHeadingP').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			$('.pageHeadingP').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
			$('.deviceType').each(function(){
				if($(this).children().eq(1).attr('value')==''){
						$(this).hide();
				}
			}) ;
		});
		
		/****function to get the selectedText from iframe****/
		function getIframeSelectionText(iframe) {
			  var win = iframe.contentWindow;
			  var doc = iframe.contentDocument || win.document;

			  if (win.getSelection) {
			    return win.getSelection().toString();
			  } else if (doc.selection && doc.selection.createRange) {
			    return doc.selection.createRange().text;
			  }
		}
		
		/****function to view Bookmark detail****/
		function viewBookmarkDetail(id){
			var params="id="+id;
			$.get('proceeding/part/viewbookmark?'+params,function(data){
			    $.fancybox.open(data, {autoSize: false, width: 800, height:600});
		    },'html');					
		}
		
		/****Funtion to register the events and control of wysiwyg****/
		function loadWysiwyg(){
			
			 $('.wysiwyg').wysiwyg({
		    	  events:{
						keydown:function(e){
							
							if(j==0 && e.which==17){
								keycode[j]=e.which;
								j++;
							}else if(j==1 && e.which==16){
								keycode[j]=e.which;
								j++;
							}else if(j==2 && e.which!=0){
								mainContent=$('#'+contentNo).val();
								finalContent=mainContent;							
								mainContent=replaceString(mainContent);
								prevMainContentLength=mainContent.length;
								keycode[j]=e.which;
								j++;
							}else{
								if(e.which==13){
									lineCount=lineCount+1;
								}
								currentContent=$('#'+contentNo).wysiwyg('getContent');
								currentContent=replaceString(currentContent);
							}
							if(keycode[0]==17 && keycode[1]==16){
								var key="";
								if(keycode.length>2){
									e.preventDefault();
									key=keycode;
								}
								if(key!=""){
								if(currentContent.length>mainContent.length){
									currentContent=replaceString(currentContent);
									var lookupContent=currentContent.substring(mainContent.length);
									if(lookupContent!=""){
										$.get("ref/search?key="+key+"&term="+lookupContent,function(data){
											 $("#autosuggest_menu").empty();
											var menuText = "";
											if(data.length>0){
												for(var i=0;i<data.length;i++){
													menuText+="<option value='"+data[i].value+"'>"+data[i].name;
												}
												$("#autosuggest_menu").html(menuText);
												keycode.length=0;
												j=0;
												var offset = $("#"+contentNo+"-wysiwyg-iframe").offset();
												$("#shiftDiv").css("display","block");
												$("#autosuggest_menu").css("display","block");
												$("#autosuggest_menu").focus();
												$("#autosuggest_menu").get(0).selectedIndex = 0;;
												$("#shiftDiv").css("left",/* $('#'+contentNo).val().length  + */ offset.left);
												$("#shiftDiv").css("top",offset.top/* +(lineCount*30) */);
												
											}else{
												$("#shiftDiv").css("display","none");
												j=0;
												keycode.length=0;
											}
										}); 
									}
								}
								}
							}
						}
					},
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
							bookmark:{
								visible: true, 
								 icon: './resources/images/bookmark.png',
					             tags: ['bookmark'], 
					             exec: function(){
					            		 $.get('proceeding/bookmark?language='+$("#selectedLanguage").val()+'&currentSlot='+$('#slot').val(),function(data){
					 				   		 $.fancybox.open(data, {autoSize: false, width:800, height:500});
					 			   		 },'html');
					 			    	return false;
					             	}
					            }
							}	  
		      });
			 
			 $('.wysiwyg').change(function(e){
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
		     	 $('.wysiwyg').each(function(){
		  		var idval = this.id;
		  		if($('#'+idval).is('[readonly]')){
		  			$('<input>').attr({
		  			    type: 'hidden',
		  			    id: 'copyOf'+idval,
		  			    value: $('#'+idval).val()
		  			}).appendTo($('#'+idval));
		  		}
		  		});
		}
		
		/****function to import the content of devices by device No****/
		function loadDeviceNoChangeEvent(){
			$('.deviceNo').change(function(){
				var id=this.id;
				var mainId=id.split("deviceNo")[1];
				$.get('ref/device?number='+$(this).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType'+mainId).val(),function(data){
					if(data!=null){
						$('#deviceId'+mainId).val(data.id);
						$('#content'+mainId).wysiwyg('setContent',data.name);
					}
				});
			});
		}
		
		/****function to get the selectedText from iframe****/
		function loadDeviceTypeChangeEvent(){
			$('.deviceTypes').change(function(){
				var deviceType="";
				var id=this.id;
				var mainId=id.split("deviceType")[1];
				$.ajax({url: 'ref/getTypeOfSelectedDeviceType?deviceTypeId='+ $('#deviceType'+mainId).val(), async: false, success : function(data){	
					deviceType = data;
				}}).done(function(){
					if(deviceType=="questions_halfhourdiscussion_from_question"){
						$('.starredQuestion'+mainId).show();
					}else{
						$('.halfHourDiscussionFromQuestion'+mainId).hide();
						$('.starredQuestion'+mainId).hide();
					}
				});
				
			});
		}
		
		/****function to get the halfhourdiscussionfromquestion by starred question****/
		function loadStarredQuestionNoChangeEvent(){
			$('.starredQuestionNo').change(function(){
				var id=this.id;
				var mainId=id.split("starredQuestionNo")[1];
				$.get('ref/gethalfhourdiscussionfromquestion?starredQuestionNo='+$('#starredQuestionNo'+mainId).val()+'&session='+$('#session').val(),function(data){
					var text="<option value='' selected='selected'>----"+$("#pleaseSelectMessage").val()+"----</option>";
					$('#halfHourDiscussionFromQuestionNo'+mainId).empty();
					if(data.length>0){
						for(var i=0;i<data.length;i++){
							text=text+"<option value='"+data[i].name+"'>"+data[i].name+"</option>";
						}
						$('#halfHourDiscussionFromQuestionNo'+mainId).html(text);
						$('.halfHourDiscussionFromQuestion'+mainId).show();
					}
				});
			});
		}
		
		/****function to import halfhourdiscussionfromquestion from the number selected ****/
		function loadHalfHourDiscussionFromQuestionNoChangeEvent(){
			$('.halfHourDiscussionFromQuestionNo').change(function(){
				var id=this.id;
				var mainId=id.split("halfHourDiscussionFromQuestionNo")[1];
				$.get('ref/device?number='+$('#halfHourDiscussionFromQuestionNo'+mainId).val()+'&session='+$('#session').val()+'&deviceType='+$('#deviceType'+mainId).val(),function(data){
					if(data!=null){
						$('#deviceId'+mainId).val(data.id);
						$('#content'+mainId).wysiwyg('setContent',data.name);
					}
				});
			});
		}
		
		/****Toggling the mainHeading on clicking mainHeadingLink****/
		function loadMainHeadingToggleEvent(){
			$('.mainHeadingLink').click(function(){
				var currId=this.id;
				var mainHeadingCount=currId.split("mainHeadingLink")[1];
				$('.mainHeadingP'+mainHeadingCount).toggle();
				$('.mainHeadingP'+mainHeadingCount).focus();
			});
		}
		
		/****Toggling the pageHeading on clicking pageHeadingLink****/
		function loadPageHeadingToggleEvent(){
			$('.pageHeadingLink').click(function(){
				var currId=this.id;
				var pageHeadingCount=currId.split("pageHeadingLink")[1];
				$('.pageHeadingP'+pageHeadingCount).toggle();
			});
		}
		
		/****Function to hide and show Divs on Linkclick****/
		function hideAndShowDivOnLinkClick(){
			$('.ministerLink').click(function(){
				var id=this.id;
				var currCount=id.split("ministerLink")[1];
				$('.minister'+currCount).show();
				$('.member'+currCount).show();
				$('.substitute'+currCount).hide();
				$('.private'+currCount).hide();
				$('.public'+currCount).hide();
				$('#privateLink'+currCount).show();
				$('#ministerLink'+currCount).hide();
			});
			
			$('.substituteLink').click(function(){
				var id=this.id;
				var currCount=id.split("substituteLink")[1];
				$('.substitute'+currCount).toggle();
			});
			
			$('.privateLink').click(function(){
				var id=this.id;
				var currCount=id.split("privateLink")[1];
				$('.member'+currCount).show();
				$('.minister'+currCount).hide();
				$('.public'+currCount).hide();
				$('.substitute'+currCount).hide();
				$('#privateLink'+currCount).hide();
				$('#ministerLink'+currCount).show();
			});
			
			
			$('.publicLink').click(function(){
				var id=this.id;
				var currCount=id.split("publicLink")[1];
				$('.public'+currCount).show();
				$('.member'+currCount).hide();
				$('.substitute'+currCount).hide();
				$('.minister'+currCount).hide();
				
			});
			
			$('.addDevice').click(function(){
				var id=this.id;
				var mainId=id.split("addDevice")[1];
				$('.deviceType'+mainId).toggle();
			});
		}
		
		/****Reseting the MainHeading and PageHeading on Clicking Clear Link****/
		function loadClearLinkClickEvent(){
			
			$('.clearMainHeading').click(function(){
				var mId=this.id;
				var mhId=mId.split('clearMainHeadingLink')[1];
				$('#mainHeading'+mhId).val('');
			});
			$('.clearPageHeading').click(function(){
				var pId=this.id;
				var phId=pId.split('clearPageHeadingLink')[1];
				$('#pageHeading'+phId).val('');
			});
		}
		
		/****Function to register autosuggest for primaryMember and SubstituteMember****/
		function loadAutoSuggest(){
			 $( ".formattedMember").autocomplete({
					minLength:3,			
					source:'ref/member/supportingmembers?session='+$("#session").val(),
					select:function(event,ui){	
						id=this.id;
						var elementCount=id.split("formattedMember")[1];
						$("#primaryMember"+elementCount).val(ui.item.id);
				}	
			 });
		      
		      $( ".formattedSubstituteMember").autocomplete({
					minLength:3,			
					source:'ref/member/getmembers?session='+$("#session").val(),
					select:function(event,ui){	
						id=this.id;
						var elementCount=id.split("formattedSubstituteMember")[1];
						$("#substituteMember"+elementCount).val(ui.item.id);
				}	
			 });
		}
		
		/****Function to View Proceeding Citation****/
		function loadViewCitationClick(){
			$(".viewProceedingCitation").click(function(){
				var id=this.id;
				var idArray=id.split("");
				var counter=idArray[idArray.length-1];
				$.get('proceeding/part/citations?counter='+counter,function(data){
				    $.fancybox.open(data, {autoSize: false, width: 600, height:600});
			    },'html');
			    return false;
			});	
		}
		/****Function to replace a part of a string****/
		function replaceString(toBeReplacedContent){
			toBeReplacedContent=toBeReplacedContent.replace(/<br><p><\/p>/g,"");
			toBeReplacedContent=toBeReplacedContent.replace(/<br>/g,"");
			toBeReplacedContent=toBeReplacedContent.replace(/<p>/g,"");
			toBeReplacedContent=toBeReplacedContent.replace(/<\/p>/g,"");
			return toBeReplacedContent;
		}
	</script>
</head>

<body>
<div class="fields clearfix watermark">
<form:form action="proceeding" method="PUT" modelAttribute="domain">
	<%@ include file="/common/info.jsp" %>
	<h2><spring:message code="proceeding.edit.heading" text="Proceeding:ID"/>(${domain.id })		
	</h2>
	<p>
		<c:forEach items="${bookmarks}" var="i">
			<a href="#" id="${i.id}##${i.bookmarkKey}" class="bookmarkKey" style="margin-left: 162px;margin-top: 30px;">${i.bookmarkKey}</a> 
		</c:forEach>
	</p>
	<p>
	<label class="small"><spring:message code="proceeding.slot" text="Slot"/></label>
	<input type="text" name="slotName" id="slotName" value="${slotName}"/>
	</p>
	<input type="button" class="button addPartButton" id="addPart0" value="<spring:message code='part.addPart' text='Add Part'></spring:message>">
	
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
	
	<c:if test="${!(empty parts)}">
	<c:set var="count" value="1"></c:set>
	<c:forEach items="${parts}" var="outer">
	<div id="part${count}" class="abc">
	<p align="right">
		<a href="javascript:void(0)" id="mainHeadingLink${count}" class="mainHeadingLink"><img src="./resources/images/IcoMainHeading.jpg" title="Main Heading" class="imageLink" /></a>
		<a href="javascript:void(0)"  id="pageHeadingLink${count}" class="pageHeadingLink"><img src="./resources/images/IcoPageHeading.jpg" title="Page Heading" class="imageLink" /></a>
		<a href="javascript:void(0)" id="addBookmark${count}" class="addBookmark" ><img src="./resources/images/IcoBookMark.jpg" title="Bookmark" class="imageLink" /></a>
		<a href="javascript:void(0)" id="viewProceedingCitation${count}" class="viewProceedingCitation"><img src="./resources/images/IcoCitation.jpg" title="Citation" class="imageLink" /></a>
		<a href="javascript:void(0)" id="addDevice${count}" class="addDevice"><img src="./resources/images/IcoDeviceType.jpg" title="Device" class="imageLink" /></a>
		<a href='javascript:void(0)' id="privateLink${count}" class="privateLink"><img src="./resources/images/IcoPrivateMember.jpg" title="Private" class="imageLink" /></a>
		<a href='javascript:void(0)' id="ministerLink${count}" class="ministerLink"><img src="./resources/images/IcoMinister.jpg" title="Minister" class="imageLink" /></a>
		<a href='javascript:void(0)' id="publicLink${count}" class="publicLink"><img src="./resources/images/IcoPublicRepresentative.jpg" title="Public" class="imageLink" /></a>
		<a href='javascript:void(0)' id="substituteLink${count}" class="substituteLink"><img src="./resources/images/IcoSubstitute.jpg" title="In place of" class="imageLink" /></a>
	</p>
	<p>
		<label class="small"><spring:message code="part.chairPerson" text="Chair Person Name"/></label>
		<input type="text" class="sText" name="chairPerson${count}" id="chairPerson${count}" value="${outer.chairPerson}"/>
		
	</p>
	<p>
		<label class="small"><spring:message code="part.chairPersonRole" text="Chair Person Role"/></label>
		<select name="chairPersonRole${count}" id="chairPersonRole${count}">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${roles}" var="i">
				<c:choose>
					<c:when test="${outer.chairPersonRole.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p class="member${count} member">
		<label class="small"><spring:message code="part.memberName" text="Member"/></label>
		<input type="text" name="formattedMember${count}" id="formattedMember${count}" class="autosuggest sText formattedMember" value="${outer.primaryMember.getFullname()}"/>
		<input name="primaryMember${count}" id="primaryMember${count}" type="hidden" value="${outer.primaryMember.id}">
	</p>
		
	<p>
		<label class="small"><spring:message code="part.order" text="Order"/></label>
		<input type="text" class="sInteger" name="order${count}" id="order${count}" value="${outer.orderNo}"/>
	</p>
	<p class="minister${count} minister">
		<label class="small"><spring:message code="part.primaryMemberDesignation" text="Designation"/></label>
		<select name="primaryMemberDesignation${count}" id="primaryMemberDesignation${count}">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${designations}" var="i">
				<c:choose>
					<c:when test="${outer.primaryMemberDesignation.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p class="minister${count} minister">
		<label class="small"><spring:message code="part.primaryMemberMinistry" text="Ministry"/></label>
		<select name="primaryMemberMinistry${count}" id="primaryMemberMinistry${count}">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${ministries}" var="i">
				<c:choose>
					<c:when test="${outer.primaryMemberMinistry.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p> 
	<p class="substitute${count} substitute">
		<label class="small"><spring:message code="part.substitutememberName" text=" Substitute Member"/></label>
		<input type="text" name="formattedSubstituteMember${count}" id="formattedSubstituteMember${count}" class="autosuggest sText formattedSubstituteMember" value="${outer.substituteMember.getFullname()}"/>
		<input name="substituteMember${count}" id="substituteMember${count}" type="hidden" value="${outer.substituteMember.id}">
	</p>
	
	<p class="substitute${count} substitute">
		<label class="small"><spring:message code="part.substituteMemberDesignation" text="Designation"/></label>
		<select name="substituteMemberDesignation${count}" id="substituteMemberDesignation${count}">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${designations}" var="i">
				<c:choose>
					<c:when test="${outer.substituteMemberDesignation.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p class="substitute${count} substitute">
		<label class="small"><spring:message code="part.substituteMemberMinistry" text="Ministry"/></label>
		<select name="substituteMemberMinistry${count}" id="substituteMemberMinistry${count}">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${ministries}" var="i">
				<c:choose>
					<c:when test="${outer.substituteMemberMinistry.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p class="public${count} public">
		<label class="small"><spring:message code="part.publicRepresentative" text="Public"/></label>
		<input type="text" class="sText" name="publicRepresentative${count}" id="publicRepresentative${count}" value="${outer.publicRepresentative}"/>
	</p>
	<p class="public${count} public">
		<label class="wysiwyglabel"><spring:message code="part.publicRepresentativeDetail" text="Detail"/></label>
		<textarea class="wysiwyg" name="publicRepresentativeDetail${count}" id="publicRepresentativeDetail${count}">${outer.publicRepresentativeDetail}</textarea>
	</p>
	<p class="deviceType${count} deviceType">
		<label class="small"><spring:message code="part.deviceType" text="DeviceType"/></label>
		<select name="deviceType${count}" id="deviceType${count}" class="deviceTypes">
			<option value="" selected="selected"><spring:message code='please.select' text='Please Select'/></option>
			<c:forEach items="${deviceTypes}" var="i">
				<c:choose>
					<c:when test="${outer.deviceType.id==i.id}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</p>
	<p class="deviceType${count} deviceType" style="display: none;">
		<label class="small"><spring:message code="part.deviceNo" text="Device No"/></label>
		<input type="text" name="deviceNo${count}" id="deviceNo${count}" class="sInteger deviceNo"/>
	</p>
	<p class="starredQuestion${count} starredQuestion" style="display: none;">
		<label class="small"><spring:message code="part.starredQuestionNo" text=" OR Starred Question No"/></label>
		<input type="text" name="starredQuestionNo${count}" id="starredQuestionNo${count}" class="sInteger starredQuestionNo"/>
	</p>
	<p class="halfHourDiscussionFromQuestion${count} halfHourDiscussionFromQuestion" style="display: none;">
		<label class="small"><spring:message code="part.DeviceNo" text="Device No"/></label>
		<select id="halfHourDiscussionFromQuestionNo${count}" name="halfHourDiscussionFromQuestionNo${count}" class="sSelect halfHourDiscussionFromQuestionNo"></select>
	</p>
	<p class="mainHeadingP${count} mainHeadingP">
		<label class="small"><spring:message code="part.mainHeading" text="Main Heading"/></label>
		<textarea class="sTextarea" name="mainHeading${count}" id="mainHeading${count}" >${outer.mainHeading}</textarea>
		<a href="javascript:void(0)" id="clearMainHeadingLink${count}" class="clearMainHeading">clear</a>
	</p>
	<p class="pageHeadingP${count} pageHeadingP">
		<label class="small"><spring:message code="part.pageHeading" text="Page Heading"/></label>
		<textarea class="sTextarea" name="pageHeading${count}" id="pageHeading${count}">${outer.pageHeading}</textarea>
		<a href="javascript:void(0)" id="clearPageHeadingLink${count}" class="clearPageHeading">clear</a>
	</p>
	<p>
		<label class="wysiwyglabel"><spring:message code="part.proceedingContent" text="Content"/></label>
		<textarea class="wysiwyg" name="content${count}" id="content${count}">${outer.proceedingContent}</textarea>
	</p>
	<input type='button' class='button addPartButton' id='addPart${count}' value='<spring:message code="proceeding.addPart" text="Add Part"></spring:message>' />
	<input type='button' class='button' id='${count}' value='<spring:message code="proceeding.deletePart" text="Delete Part"></spring:message>' onclick='deletePart(${count});'/>
	<input type='hidden' id='partId${count}' name='partId${count}' value="${outer.id}">
	<input type='hidden' id='partVersion${count}' name='partVersion${count}' value="${outer.version}">
	<input type='hidden' id='partLocale${count}' name='partLocale${count}' value="${domain.locale}">
	<input type='hidden' id='partReporter${count}' name='partReporter${count}'>
	<input type='hidden' id='partEntryDate${count}' name='partEntryDate${count}'>
	<input type='hidden' id='partRevisedContent${count}' name='partRevisedContent${count}'>
	<input type='hidden' id='deviceId${count}' name='deviceId${count}' value='${outer.deviceId}'>
	<input type='hidden' id='partProceeding${count}' name='partProceeding${count}' value='${id}'>
	<c:set var="count" value="${count+1}"></c:set>	
	</div>	
	</c:forEach>
	</c:if>	
	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef submit">
		</p>
	</div>		
	<div style="border: 2px solid black; position:absolute; z-index: 2000; display:none;" id="shiftDiv">
		<select size="5" id="autosuggest_menu">
		
		</select> 
	</div>
	<form:hidden path="version" />
	<form:hidden path="locale"/>
	<form:hidden path="id"/>
	<form:hidden path="slot" value="${slot}"/>
	<input type="hidden" id="partCount" name="partCount" value="${partCount}"/>
	<input type="hidden" id="currentSlotName" name="currentSlotName" value="${slotName}"/>
	<div style="border: 2px solid black; position:absolute; z-index: 2000; display:none;" id="shiftDiv">
		<select size="5" id="autosuggest_menu">
		
		</select> 
	</div>
</form:form>
<ul id="contextMenuItems" >
	<li><a href="#viewBookmarkDetails" class="edit"><spring:message code="proceeding.viewBookmarkDetails" text="viewDetails"></spring:message></a></li>
</ul>
<input type="hidden" id="primaryMemberNameMessage" name="primaryMemberNameMessage" value="<spring:message code='part.memberName' text='Member'></spring:message>" disabled="disabled"/>
<input type="hidden" id="substituteMemberNameMessage" name="substituteMemberNameMessage" value="<spring:message code='part.substituteMemberName' text='Substitute Member'></spring:message>" disabled="disabled"/>
<input type="hidden" id="orderMessage" name="orderMessage" value="<spring:message code='part.order' text='Order'></spring:message>" disabled="disabled"/>
<input type="hidden" id="mainHeadingMessage" name="mainHeadingMessage" value="<spring:message code='part.mainHeadingMessage' text='Main heading'></spring:message>" disabled="disabled"/>
<input type="hidden" id="pageHeadingMessage" name="pageHeadingMessage" value="<spring:message code='part.pageHeadingMessage' text='Page heading'></spring:message>" disabled="disabled"/>
<input type="hidden" id="chairPersonMessage" name="chairPersonMessage" value="<spring:message code='part.chairPersonMessage' text='Chair Person'></spring:message>" disabled="disabled"/>
<input type="hidden" id="roleMessage" name="roleMessage" value="<spring:message code='part.roleMessage' text='Role'></spring:message>" disabled="disabled"/>
<input type="hidden" id="contentMessage" name="contentMessage" value="<spring:message code='part.contentMessage' text='Content'></spring:message>" disabled="disabled"/>
<input type="hidden" id="deletePartMessage" name="deletePartMessage" value="<spring:message code='part.deletePartMessage' text='Delete Part'></spring:message>" disabled="disabled"/>
<input type="hidden" id="addPartMessage" name="addPartMessage" value="<spring:message code='part.addPartMessage' text='add Part'></spring:message>" disabled="disabled"/>
<input type="hidden" id="primaryMemberDesignationMessage" name="primaryMemberDesignationMessage" value="<spring:message code='part.designation' text='Designation'></spring:message>" disabled="disabled"/>
<input type="hidden" id="primaryMemberMinistryMessage" name="primaryMemberMinistryMessage" value="<spring:message code='part.ministry' text='Ministry'></spring:message>" disabled="disabled"/>
<input type="hidden" id="substituteMemberDesignationMessage" name="substituteMemberDesignationMessage" value="<spring:message code='part.designation' text='Designation'></spring:message>" disabled="disabled"/>
<input type="hidden" id="substituteMemberMinistryMessage" name="substituteMemberMinistryMessage" value="<spring:message code='part.ministry' text='Ministry'></spring:message>" disabled="disabled"/>
<input type="hidden" id="publicRepresentativeMessage" name="publicRepresentativeMessage" value="<spring:message code='part.publicRepresentative' text='Public Representative'></spring:message>" disabled="disabled"/>
<input type="hidden" id="publicRepresentativeDetailMessage" name="publicRepresentativeDetailMessage" value="<spring:message code='part.publicRepresentativeDetail' text='Public Representative Detail'></spring:message>" disabled="disabled"/>
<input type="hidden" id="deviceTypeMessage" name="deviceTypeMessage" value="<spring:message code='part.deviceType' text='Device Type'></spring:message>" disabled="disabled"/>
<input type="hidden" id="deviceNoMessage" name="deviceNoMessage" value="<spring:message code='part.deviceNo' text='Device No'></spring:message>" disabled="disabled"/>
<input type="hidden" id="starredQuestionNoMessage" name="starredQuestionNoMessage" value="<spring:message code='part.starredQuestionNo' text='Starred Question No'></spring:message>" disabled="disabled"/>
<input type="hidden" id="HalfHourDiscussionFromQuestionMessage" name="HalfHourDiscussionFromQuestionMessage" value="<spring:message code='part.HalfHourDiscussionFromQuestionMessage' text='Half Hour Question No'></spring:message>" disabled="disabled"/>
<input type="hidden" id="session" value="${session}"/>
<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
<input id="addBookmarkMessage" value="<spring:message code='client.prompt.updateText' text='Do you want to add the Text for the Bookmark.'></spring:message>" type="hidden">
<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
</div>
</body>
</html>