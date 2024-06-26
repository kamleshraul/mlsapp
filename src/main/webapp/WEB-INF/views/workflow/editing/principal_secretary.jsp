<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="editing" text="Editing"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css">
		.blue{
			color:blue;
		}
		.green{
			color:green;
		}
		.red{
			color:red;
		}
		
		input[type=button]{
			width:30px;
			margin: 5px;
			font-size: 13px; 
			padding: 5px;
			/*display: inline;*/ 		
		}	
		.highlight{
			font-weight: bold;
		}
		
		/******CSS FOR PAGE *******/
		textarea {
		height: 28px;
		width: 400px;
	}

	/* #textarea {
		-moz-appearance: textfield-multiline;
		-webkit-appearance: textarea;
		border: 1px solid gray;
		font: medium -moz-fixed;
		font: -webkit-small-control;
		height: 28px;
		overflow: auto;
		padding: 2px;
		resize: both;
		width: 400px;
	}

	input {
		margin-top: 5px;
		width: 400px;
	}

	#input {
		-moz-appearance: textfield;
		-webkit-appearance: textfield;
		background-color: white;
		background-color: -moz-field;
		border: 1px solid darkgray;
		box-shadow: 1px 1px 1px 0 lightgray inset;  
		font: -moz-field;
		font: -webkit-small-control;
		margin-top: 5px;
		padding: 2px 3px;
		width: 398px;    
	} */
	
		.replaceMe :hover{
			opacity:1.0;
			/*filter:alpha(opacity=100);*/ /* For IE8 and earlier */
		}
		
		.container{
			margin-left: 50px;
			width: 540px; 
			max-width: 800px; 
			opacity: 0.5; 
			box-shadow: 2px 2px 2px #000000; 
			border: 1px solid black;
		}
		
		div.wysiwyg{
			left: 10px;
			top: 10px;
			width: 600px !important;
		}
	
		.textDemo{
			margin-top: 20px; 
			display: none;
			position: fixed; 
			top: 120px; 
			background: scroll repeat-x #000000; 
			width: 620px;
			height: 262px;
			z-index: 10000; 
			box-shadow: 0px 2px 5px #888; 
			opacity:0.8;
		}
		
		#reportIconsDiv{
			
			border-radius: 5px; 
			padding: 5px 0px 0px 0px; 
			width: 814px; 
			margin-left: 50px; 
			border: 1px solid black; 
			/*background: #0E4269;*/
			
			/*background: -prefix-linear-gradient(top, #0E4269, #59A8E3);*/
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
			/*background: #0E4269;*/
			
			/*background: -prefix-linear-gradient(top, #0E4269, #59A8E3);*/
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
		
		#containerTable{
			width: 750px; 
			table-layout: fixed; 
			margin-left: 25px;
		}
		
		#container table ul{
			background: none;
		}
		
		.styleSelect select {
		   background: transparent;
		   width: 200px;
		   padding: 2px;
		   font-size: 12px;
		   line-height: 1;
		   border: 0;
		   border-radius: 10px;
		   height: 22px;
		   -webkit-appearance: none;
		  }
	
		.styleSelect{
		   width: 172px;
		   height: 20px;
		   overflow: hidden;
		   background: url(./resources/images/down_arrow_select_2.jpg) no-repeat right #ddd;
		   border: 1px solid #ccc;
		   box-shadow: 2px 2px 2px #000;
		   border_radius: 10px;
	   }
		
	</style>
	<script type="text/javascript">			
	
		var whichId;
		var showIt=0;
	
		function showEditor(e){
			//console.log("Text: " +window.getSelection().toString().trim());
			var pageWidth=$(window).width();
			var pageHeight=$(window).height();
			//console.log("******************************************");
			//console.log("Page: "+ pageWidth+":"+pageHeight);
			var clickX=e.clientX;
			var clickY=e.clientY;
			//console.log("Coordinates and height: "+clickX+":"+clickY+":"+$('#textDemo').height()+":"+(clickY+$('#textDemo').height()));
			//console.log((clickY+$('#textDemo').height())>pageHeight);

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
		
		function hideEditor(){
			$("#textDemo").fadeOut();
			return false;
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
		function replaceAndLoad(){
			var params="?houseType=" + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val();
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			$.post($("form[action='workflow/editing/replace']").attr('action') + params,
					$("form[action='workflow/editing/replace']").serialize(),function(data){
				if(data=='SUCCESS'){
					$("#"+whichId).empty();
					$("#"+whichId).html(newContent);
					$.unblockUI();
					//showEditProceeding();
				}
			}).fail(function(){
				$.unblockUI();
			});
		}
		function saveAndHide(){
			hideEditor();
			var content = $("#ttA").val().trim();
			var newContent=content.replace(/<span.*?>/g,"");
			/* $("#"+whichId).empty();
			//console.log($("#"+whichId)+"showIt:"+showIt);
			$("#"+whichId).html(newContent); */
			
			//console.log("Id: "+whichId+"Content: " + content + "\nNew Content: " + newContent);
			//alert(newContent);
			newContent=newContent.replace("</span>","");
			//alert(newContent);
			var params = "?userGroup="+$("#currentusergroup").val()+"&userGroupType="+$("#currentusergroupType").val();
            $("#data").val(newContent);
            if($("#prevcontent").val()!=newContent){
				$.post($("form[action='workflow/editing/savepart']").attr('action')+'/'+whichId.substring(2)+params,
						$("form[action='workflow/editing/savepart']").serialize(),function(data){
					if(data=='SUCCESS'){
						$("#"+whichId).empty();
						$("#"+whichId).html(newContent);
						//showEditProceeding();
					}
				}).fail(function(){
					
				});
            }
		}
		
		function showReport(reportOutputFormat, reedit, command, element){
			var params='houseType=' + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val()
			+ '&action=' + command
			+ '&reportType=' + $("#selectedReportType").val()
			+ '&reedit=' + reedit
			+ '&member='+ $("#selectedMember").val()
			+ '&memberReportType=' + $("#selectedMemberReport").val()
			+ '&pageheader=' + $("#selectedPageheader").val()
			+ '&outputFormat=' + reportOutputFormat;
			
			var reportURL = 'editing/editorreport?' + params;
			
			$('#' + element).attr('href', reportURL);
		}
		
		function replaceAll(command,reedit){
			var params='houseType=' + $('#selectedHouseType').val()
			+ '&sessionYear=' + $("#selectedSessionYear").val()
			+ '&sessionType=' + $("#selectedSessionType").val()
			+ '&language=' + $("#selectedLanguage").val()
			+ '&day=' + $('#selectedDay').val()
			+ '&action=' + command
			+ '&reportType=' + $("#selectedReportType").val()
			+ '&reedit=' + reedit
			+ '&member='+ $("#selectedMember").val()
			+ '&memberReportType=' + $("#selectedMemberReport").val()
			+ '&pageheader=' + $("#selectedPageheader").val();
			
			$("#undoCount").val((parseInt($("#undoCount").val()) + 1));				
			
			$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
			
			$.post($("form[action='editing/replace']").attr('action')+"?"+params,
					$("form[action='editing/replace']").serialize(),function(data){
				if(data.length>0){
					var i;
					for(i = 0; i < data.length; i++){
						if(data[i][7]=='include'){
							$("#pp"+data[i][0]).empty();
							$("#pp"+data[i][0]).html(data[i][4]);
							var undoData = $("#ppsp"+data[i][0]).html();
							if(undoData=='classes'){
								$("#ppsp"+data[i][0]).empty();
								$("#ppsp"+data[i][0]).html(data[i][5]);
							}else{
								$("#ppsp"+data[i][0]).html(undoData+";"+data[i][5]);
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
		
		function undoLastChange(){
			$(".ppsp").each(function(){
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var undoData=$(this).html();
				if(undoData!='classes'){
					var undoDataArray = undoData.split(";");
					var undoDataToWorkWith=undoDataArray[undoDataArray.length-1].split(":");
					$("#undoCount").val(parseInt($("#undoCount").val())-1);
					$("#uniqueIdentifierForUndo").val(undoDataToWorkWith[2]);
					var html=$(this).html().replace(";"+undoDataArray[undoDataArray.length-1],"");
					
					$(this).html(html);								
					
					$.post("workflow/editing/undolastchange",
							$("form[action='workflow/editing/replace']").serialize(),function(data){
						if(data){
							$("#"+$(this).attr('id').substring(4)).empty();
							$("#"+$(this).attr('id').substring(4)).html(data.value);
							$.unblockUI();
						}
					}).fail(function(){
						$.unblockUI();
					});
				}
				$.unblockUI();
			});
		}
		$(document).ready(function(){
			
			$('.wysiwyg').wysiwyg({controls: {
				       save: { 
				             visible: true, 
				             tags: ['tt'],  
				             exec: function(e){
				            	 	showIt=0;
				            	 	saveAndHide(e);
				             },
				             hotkey:{"ctrl":1,"shift":1,"key":88}			             
			        	},
			        	ReplaceByOrder: { 
				             visible: true, 
				             tags: ['bo'],  
				             exec: function(e){
				            	var wholeData = $("#ttA").val();
                               var data = getIframeSelectionText($("iframe")[0]);
                               var pattern = data.trim();
                               var re = new RegExp(pattern,"g");
                               var finalContent = wholeData.replace(re, "[***] " + $("#replaceByOrder").val());
                               $("#ttA").wysiwyg("setContent","");               
                               $("#ttA").wysiwyg("setContent",finalContent);
				             },
				             hotkey:{"ctrl":1,"alt":1,"key":79}			             
				        },
				        ReplaceByWithDrawal: { 
				             visible: true, 
				             tags: ['bw'],  
				             exec: function(e){
				            	var wholeData = $("#ttA").val();
                               var data = getIframeSelectionText($("iframe")[0]);
                               var pattern = data.trim();
                               var re = new RegExp(pattern,"g");
                               var finalContent = wholeData.replace(re, "[#] " + $("#replaceByWithdrawal").val());
                               $("#ttA").wysiwyg("setContent","");               
                               $("#ttA").wysiwyg("setContent",finalContent);
				             },
				             hotkey:{"ctrl":1,"alt":1,"key":87}			             
				        }
			    	},
					events:{keydown:function(e){
         				}
         			}
				});
			$("#data").click(function(){
				if ($(this).children('input').length == 0) { 
					
					var inputbox = "<input type='text' class='inputbox' value=\""+$(this).text()+"\">"; 
					 
					$(this).html(inputbox); 
					 
					$("input.inputbox").focus(); 
					 
					$("input.inputbox").blur(function() { 
						var value = $(this).val(); 
						$("#data").empty(); 
						$("#data").html(value);
					}); 
				} 	
			});
			
			$(".replaceMe").mouseup(function(e){
				
				if($("#action").val()=='edit'){
					var text=window.getSelection().toString().trim();
					var currentId=$(this).attr('id');
					
					if(showIt==0){
						whichId = $(this).attr('id');
					}else if(showIt==1 && currentId==whichId){
						whichId = $(this).attr('id');
					}
					
					
					if(text.length>0){
						if(showIt==0){
							showIt=1;
							showEditor(e);		
							
							var content = $(this).html().trim();
							$("#prevcontent").val(content);
							var newContent=content.replace(/<span.*?>/g,"");
							$(".wysiwyg").wysiwyg('setContent', newContent.replace(text,'<span style="background: yellow;">'+text+'</span>'));
							$("#ttA-wysiwyg-iframe").focus();
						}else if(showIt==1){
							showIt=0;
							saveAndHide(e);
							
							/* var content = $("#ttA").val().trim();
							var newContent=content.replace(/<span.*?>/g,"");
							$("#"+whichId).empty();
							$("#"+whichId).html(newContent); */
							
						}
					}else{
						if(showIt==1){
							showIt=0;
							saveAndHide();
						}
					}
				}
			});
			
			$(".revision").click(function(e){
				$.get("editing/revisions/" + $(this).attr('id').substring(2)+"?includeWfCopy=true",function(data){
				    $.fancybox.open(data);
			    });
			    return false;
			});
					
			/*$(".replaceMe").toggle(function(e){
				console.log("Text: " +window.getSelection().toString().trim());
				var pageWidth=$(window).width();
				var pageHeight=$(window).height();
				console.log("******************************************");
				console.log("Page: "+ pageWidth+":"+pageHeight);
				var clickX=e.clientX;
				var clickY=e.clientY;
				console.log("Coordinates and height: "+clickX+":"+clickY+":"+$('#textDemo').height()+":"+(clickY+$('#textDemo').height()));
				console.log((clickY+$('#textDemo').height())>pageHeight);
				if((clickY+$('#textDemo').height()) > pageHeight){
					var diff=$('#textDemo').height() - clickY;
					console.log("Diff: "+diff);
					if(diff < 0){
						console.log("clickY-half of height: "+(clickY - ($('#textDemo').height() / 2)));
						$('#textDemo').css('top', (pageHeight - clickY) + 'px');
					}else{
						$('#textDemo').css('top', diff + 'px');
					}
					
					$('#textDemo').css('left', clickX + 'px');
				}else{
				
					var diff=$('#textDemo').height() - clickY;
					console.log("Diff: "+diff);
					console.log("PageHeight-textDemo Height: " + (pageHeight - $('#textDemo').height()));
					$('#textDemo').css('top', clickY + 'px');
					$('#textDemo').css('left', 'auto');
				}
				$("#textDemo").fadeIn();
				
				//$('.wysiwyg').wysiwyg('focus');
				}, function(){
				//var op = window.open("http://google.com","_blank");
				$("#textDemo").fadeOut();
				return false;
			}); */
			$(".wysiwyg").keypress(function(e){
				//alert(e.keyCode);
			});		
			$('.wysiwyg').change(function(e){
				var html;
				$("#"+whichId ).empty();
				html = $(this).val();
				$("#" + whichId).html(html);
			});
			
			$("#re_edit").click(function(){
				showProceedingInGeneral('edit', 'true');
			});
			
			/* $(document).mousemove(function(e){
				document.title="x: " + e.clientX + "; y: " + e.clientY;
			}); */
			
			$("#editorreport_pdf").click(function(e){
				showReport("PDF", "false", "edit", $(this).attr('id'));
			});
			
			$("#editorreport_word").click(function(e){
				
				showReport("WORD", "false", "edit", $(this).attr('id'));
			});
			
			$(".memberImg").mouseover(function(){
				var srcURL = $(this).attr('src');
				$("#zoomImgDiv").css("display", "block");
				
				$("#zoomImg").attr("src", srcURL);
				$("#zoomImg").css("height", "64px");
			});
			
			$(".memberImg").mouseout(function(){
				/* var srcURL = $(this).attr('src');
				$("#zoomImgDiv").css("display", "block");
				$("#zoomImg").attr("src", srcURL); */
				$("#zoomImgDiv").css("display", "none");
			});
			
			$(".replaceMe").mouseover(function(){
				$("#zoomImgDiv").css("display", "block");
				var srcURL = $(".img"+$(this).attr('id').substring(2)).children("img").attr("src");
				
				$("#zoomImg").attr("src", srcURL);
				$("#zoomImg").css("height", "64px");
			});
			
			$(".replaceMe").mouseout(function(){
				$("#zoomImgDiv").css("display", "none");
			});
			
			$("#replaceAll").click(function(){
				replaceAll('edit','false');
			});
			
			$("#undo").click(function(){
				var undoCount = parseInt($("#undoCount").val());
				if(undoCount>0){
					undoLastChange();
				}
			});
			
			$("#submit").click(function(){
				var status=$("#selectedDecissiveStatus").val();
				if(status==undefined || status=='-'){
					
				}else{
					var params="?userGroup="+$("#currentusergroup").val()
					+ '&userGroupType='+$("#currentusergroupType").val()
					+ '&houseType=' + $("#selectedHouseType").val()
					+ '&sessionYear=' + $("#selectedSessionYear").val()
					+ '&sessionType=' + $("#selectedSessionType").val()
					+ '&workflowDetailsId=' + $("#workflowdetails").val()
					+ '&selectedSubWorkflow='+$("#selectedSubWorkflow").val()
					+ '&nextActor=' + $("#selectedDecissiveActor").val();
	
					$("#submitDiv").hide();
					
					$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
					$.post('workflow/editing'+params, 
							$("form[action='workflow/editing/savepart']").serialize(),function(data){
						if(data=='SUCCESS'){
						}
						$.unblockUI();
					}).fail(function(){
						$.unblockUI();
					});
				}
			});
			
			$("#wf_edit_copy").click(function(){
				if($("#key").val()==undefined || $("#key").val()==''){
				}else{
					showEditCopyOfMember("&str=str");
				}
			});
		});
	</script>
</head>

<body>
	
	<div id="editingLinkDiv" style="padding-top: 10px;">
		<a href="#" id="wf_unedited_copy" class="butSim">
			<spring:message code="editor.unedited" text="Unedited Copy" />
		</a> |			
		<a href="#" id="wf_edited_copy" class="butSim">
			<spring:message code="editor.edited" text="Edited Copy"/>
		</a> |			
		<c:if test="${workflowstatus!='COMPLETED'}">			
			<a href="#" id="wf_edit_copy" class="butSim">
				<spring:message code="editor.edit" text="Editing"/>
			</a> |		
		</c:if>		
		<hr />	
	</div>
<div>
	<div id="reportIconsDiv" style="display: none;">
		<a id="editorreport_pdf" class="exportLink" href="javascript:void(0);" style="text-decoration: none; margin-left: 40px;" target="_blank">
			<img class="imgN" src="./resources/images/pdf_icon.png" alt="Export to PDF" width="24px" height="32px" title="<spring:message code='editing.editorreport.pdf' text='Editor Report In PDF' />">
		</a>
		&nbsp;&nbsp;
		<a id="editorreport_word" class="exportLink" href="javascript:void(0);" style="text-decoration: none;" target="_blank">
			<img class="imgN" src="./resources/images/word_icon.png" alt="Export to WORD" width="33px" height="32px" title="<spring:message code='editing.editorreport.doc' text='Editor Report In Word' />">
		</a>
		<c:if test="${action=='edit'}">
			<div id="reeditDiv" style="display: none;/*inline-block*/ float: right; margin-right: 5px;"><a href="javascript:void(0);" id="re_edit"><img src="./resources/images/refresh.png" class="imgI" alt="Re-Edit" width="24px" title="<spring:message code='editing.reedit' text='Re-edit' />" /></a></div>
		</c:if>
	</div>
	<c:if test="${action=='edit'}">
		<div id="replaceToolDiv" style="display: none;">
			<form action="workflow/editing/replace" method="post">
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.searchTerm" text="Find" /></label><input type="text" id="searchTerm" name="searchTerm" value="${searchTerm}" style="border-radius: 3px; border: 1px solid #000080;" />
				<label style="margin: 0px 10px 0px 10px;"><spring:message code="editing.replace.replaceTerm" text="Replace With" /></label><input type="text" id="replaceTerm" name="replaceTerm" value="${replaceTerm}"  style="border-radius: 3px; border: 1px solid #000080;"/>
				<a href="javascript:void(0);" id="replaceAll" style="width: 70px; border: 1px solid #000080; text-decoration: none; text-align: center; color: #000080; padding: 1px; border-radius: 5px;"><spring:message code='editing.replace.replaceAll' text='Replace'></spring:message></a>
				<a href="javascript:void(0);" id="undo"><img src="./resources/images/undo.png" width="16px" /></a>&nbsp;&nbsp;<a href="javscript:void(0);" id="redo" style="display: none;"><img src=""/></a>
				<input type="hidden" name="undoCount" id="undoCount" value="${undoCount}" />
				<input type="hidden" name="uniqueIdentifierForUndo" id="uniqueIdentifierForUndo" value="" />
				<input type="hidden" name="redoCount" id="redoCount" value="${redoCount}" />
				<input type="hidden" name="uniqueIdentifierForRedo" id="uniqueIdentifierForRedo" value="" />
			</form> 
		</div> 
	</c:if>
</div>	
<div id="zoomImgDiv" style="display: none; position: fixed; right: 0; top: 160px; background: scroll repeat-x; height: 64px; z-index: 10000; box-shadow: 0px 2px 5px #888;">
	<img id="zoomImg" src="" height="64px" />
</div>

<div class="fields clearfix watermark">
<form action="workflow/editing/savepart" method="post">
	<c:if test="${workflowstatus=='PENDING'}">
		<div id="submitDiv" style="width: 750px; margin-left: 50px;">
		<c:if test="${not(statuses == null) and not(empty statuses) }">
				<a href="javascript:void(0);" id="selectStatus"><spring:message code="editing.selectdecissivestatus" text="Status" /></a> |
				<div class="styleSelect" style="display: inline-block; margin: 5px 5px 0px 5px;">
					<select id="selectedDecissiveStatus" name="decissiveStatus">
						<option value="-"><spring:message code='please.select' text='Please Select'></spring:message></option>
						<c:forEach items="${statuses}" var="s">
							<option value="${s.value}">${s.name}</option>
						</c:forEach>
					</select>|
				</div>
				<a href="javascript:void(0);" id="selectActor"><spring:message code="editing.selectdecissiveactor" text="Actor" /></a> |
				<div class="styleSelect" style="display: inline-block; margin: 5px 5px 0px 5px;">
					<select id="selectedDecissiveActor" name="decissiveActor">
					<option value="-"><spring:message code='please.select' text='Please Select'></spring:message></option>
					<c:forEach items="${actors}" var="a">
						<option value="${a.value}">${a.name}</option>
					</c:forEach>
					</select>|
				</div>
			</c:if>
			<a href="javascript:void(0);" id="submit">
				<img src="./resources/images/edisSubmit.png" alt="Submit Image" style="position;relative; top: 6px;" />
			</a> 			
		</div>
	</c:if>

	<div id="container" class="container">
		<table id="containerTable">
			<c:set var="ph" value="-" />
			<c:set var="mh" value="-" />
			<c:set var="idx" value="0" />
			<c:forEach items="${parts}" var="r">
				<c:if test="${idx!=r[0]}">
					<tr>
						<td>
							<div style="text-align: center; font-size: 16px;">
								<c:if test="${ph!=r[2] && mh!=r[3]}">
									<c:choose>
										<c:when test="${(fn:length(r[2])>0) && (fn:length(r[3])>0)}">
											<b><spring:message code="editing.pageheading" text="Page Heading" /></b>${r[1]}<br />
											<b><spring:message code="editing.mainheading" text="Main Heading" /></b>${r[2]}
										</c:when>
										<c:when test="${(fn:length(r[2])>0) || (fn:length(r[3])>0)}">
											<b><spring:message code="editing.pageheading" text="Page Heading" /></b> ${r[2]} / <b><spring:message code="editing.mainheading" text="Main Heading" /></b> ${r[3]}
										</c:when>
									</c:choose>
								</c:if>
							</div>
							<c:choose>						
								<c:when test="${action=='edited' or action=='edit'}">
									<c:choose>
										<c:when test="${not (empty r[15])}">
											<c:if test="${not(empty r[5]) and (not (r[5]==null))}">
												<b class="member" style="display: inline-block;">${r[6]}</b>
												<div id="memberImageDiv" style="display: inline;">
													<img src="editing/gememberimage/${r[5]}" height="16px;" class="memberImg" />	
												</div>
											</c:if>										
											<c:if test="${not(empty r[9]) and (not (r[9]==null))}">
												<b>/${r[10]}</b>
												<div id="memberImageDiv" style="display: inline;">
													<img src="editing/gememberimage/${r[9]}" height="16px;" class="memberImg"/>	
												</div>
											</c:if>:
											<div id="pp${r[0]}" style="width: 750px; max-width: 750px; word-wrap:break-word; display: inline;" class="replaceMe">
												${r[15]}
											</div>
											<div class="imgId img${r[0]}" id="imgId${r[5]}" style="display: none;">
												<img src="editing/gememberimage/${r[5]}" height="8px" class="imgIdDivImage"/>
											</div>
											<div id="ppsp${r[0]}" class="ppsp" style="display: none;">classes</div>
											<div class="revision" id="pd${r[0]}" style="position: relative; border-radius: 10px; margin-left: 750px; top: -20px; text-align: center; display: inline-block; min-width: 16px; min-height: 16px; border: 1px solid blue; background: #00ff00; cursor: pointer;">
												<spring:message code="editing.more" text="S" />
											</div>
										</c:when>
									</c:choose>
								</c:when>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
				</c:if>
				<c:set var="idx" value="${r[0]}" />
				<c:set var="ph" value="${r[1]}"/>
				<c:set var="mh" value="${r[2]}"/>
			</c:forEach>
		</table>
	</div>
		

	<div id="textDemo" class="textDemo">
		<textarea id="ttA" class="wysiwyg">
			Proceedings
		</textarea>
	</div>
	<input type="hidden" name="editedContent" id="data" value="demo" />
</form>
	<input type="hidden" id="workflowdetails" value="${workflowdetails}" />
	<input type="hidden" id="workflowstatus" value="${workflowstatus}" />
	<input id="reportType" type="hidden" value="${reportType}" />
	<input id="prevcontent" type="hidden" value="" />
	<input id="action" type="hidden" value="${action}" /> 
	<input id="selectItemFirstMessage" value="<spring:message code='ris.selectitem' text='Select an item first'/>" type="hidden">
	<input id="recreate_slots" value="<spring:message code='ris.recreate_slots' text='Recreate Slots'/>" type="hidden">
	<input id="turnoff_slots" value="<spring:message code='ris.turnoff_slots' text='Turn Off Slots'/>" type="hidden">
	<input id="delete_slots" value="<spring:message code='ris.delete_slots' text='Delete Slots'/>" type="hidden">
	<input id="create_new_slots" value="<spring:message code='ris.create_new_slots' text='Create New Slots'/>" type="hidden">
	<input id="recreate_slots_from_slot_duration_changed_time" value="<spring:message code='ris.recreate_slots_from_slot_duration_changed_time' text='Recreate Slots From Slot Duration Changed Time'/>" type="hidden">
	<input type="hidden" name="pleaseSelectMsg" id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'></spring:message>">	
</div>
</body>
</html>