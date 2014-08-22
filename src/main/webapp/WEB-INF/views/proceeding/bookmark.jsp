<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<title><spring:message code="proceeding.bookmark" text="Bookmark" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript">
	var contentKey="";
	var isPart=false;
	var divCount='';
	$(document).ready(function(){	
		var text="";
		var key="";
		var count=$('#count').val();
		
		if(count!=null && count!=""){
			$('#currentPart').val($('#partId'+count).val());
		}else{
			$('#currentPart').val($('#id').val());
		}
		
		
		$("#bookmark").click(function(){
			var slotArray=$('#slots').val();
			var language=$('#language').val().split("");
			var size=parseInt($('#bookmarkSize').val())+1;
			var currentSlotName=$('#currentSlotName').val();
			key=key+"0XINS-"+language[0].toUpperCase()+"~";
			var l="";
			for(var i=0;i<slotArray.length;i++){
				var j=slotArray[i];
				var k=j.split('##');
				l=l+k[1].toUpperCase();
				if(i+1<slotArray.length){
					l=l+"/";
				}
			}
			if(count!=null && count!=""){
				key=key+l+"_"+currentSlotName+$('#order'+count).val()+size;
				text=$('#content'+count).wysiwyg("getContent");
				$('#content'+count).wysiwyg("setContent",text+"<p>"+key+"</p>");
			}else{
				key=key+l+"_"+currentSlotName+$('#orderNo').val()+size;
				text=$('#proceedingContent').wysiwyg("getContent");
				$('#proceedingContent').wysiwyg("setContent",text+"<p>"+key+"</p>");
			}
			
			var param="&language="+$('#language').val()+
					  "&currentSlot="+$('#currentSlot').val()+
					  "&bookmarkKey="+key+
					  "&masterPart="+$('#currentPart').val();
					  
			$.post('proceeding/part/bookmark?'+param,function(data){
			});
			$.fancybox.close();
			
		});
		
		$('#language').change(function(){
			$.get('ref/slot?language='+$(this).val()+"&currentSlot="+$("#currentSlot").val(),function(data){
				$('#slots').empty();
				var slotText="<option value=''>"+$('#pleaseSelectMessage').val()+"</option>";
				var i;
				if(data.length>0){
				for(i=0;i<data.length;i++){
					slotText+="<option value='"+data[i].id+'##'+data[i].name+"'>"+data[i].name+"</option>";
				}
				$("#slots").html(slotText);			
				}else{
					$("#slots").empty();
							
				}
				
			}).done(function(){
				loadText();
			});
			
			
		});
		
		$('#slots').change(function(){
			loadText();
		});	
		
		
		
		
	});	
	
	function loadText(){
		var size=parseInt($('#bookmarkSize').val());
		var param="";
		$('#slots option').each(function(){
			if($(this).attr('selected')){
				var idval=$(this).val().split('##');
				param=param+idval[0];
				$('#masterSlot').val(param);
			}
		});
		$.get('ref/bookmarktext?slot='+param,function(data){
			var elementText="";
			var pCount = '';
			if(data.length>0){
				divCount=data.length;
				elementText=elementText+
				"<div id='addDeleteLinks0'>"+
				"<a href='javascript:void(0)' id='add0' class='addButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
				"</div>";
				for(var i=0;i<data.length;i++){
					elementText=elementText + "<div id='partCounter"+ (i+1)+"'>";
					console.log(data[i].value);
					if(data[i].value!=null && data[i].value!=''){
						elementText = elementText + data[i].value +" :<span id='span"+ data[i].id+"' class='bookmarkContent'> " + data[i].name +"</span>";
					}else{
						elementText = elementText + "<span id='span"+ data[i].id+"' class='bookmarkContent'> " + data[i].name + "</span>" ;
					}
					elementText = elementText + 
					"<div id='addDeleteLinks"+(i+1)+"'>"+
					"<a href='javascript:void(0)' id='add"+(i+1)+"' class='addButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
					"<a href='javascript:void(0)'  id='delete"+(i+1)+"' class='deleteButton'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' onclick='delete(0);' /></a>"+
					"</div>"+
					"</div>"+
					"<br>";
				}
				
				$('#replacingcontent').html(elementText);
				$('#replacingcontent').show();
				
				

				$('.bookmarkContent').click(function(e){
					 var divId = this.id;
					var spanId=divId.split("span");
					console.log(divId);
					$('#previousContent').val($(this).html());
					if ($(this).children('textarea').length == 0) { 
						var inputbox = "<textarea id='prContent' class='inputbox wysiwyg sTextarea'>"+$(this).text()+"</textarea>"; 
						$(this).html(inputbox); 
						$('#prContent').wysiwyg({
							 resizeOptions: {maxWidth: 600},
							 events: {
								blur: function() {
									console.log('blur');
									var value = $('#prContent').wysiwyg('getContent'); 
									console.log(value);
									$("#"+divId).empty(); 
									$("#"+divId).html(value);
									$('#replacedContent').val($("#"+divId).html());
									var param="?language="+$('#language').val()+
									  "&currentSlot="+$('#currentSlot').val()+
									  "&slavePart="+$('#currentPart').val()+
									  "&masterPart="+spanId[1]+
									  "&isPart="+isPart;
									 $.post($("form[action='proceeding/part/bookmark']").attr('action')+param,
												$("form[action='proceeding/part/bookmark']").serialize(),function(data){
												}); 	
								}
							 }
						});
						$("textarea.inputbox").focus(); 
					
					} 	
					
					$("div.wysiwyg").css('width','750px');
					//$.fancybox.close();
				}) ;
				
			}else{
				elementText=elementText+
				"<div id='addDeleteLinks0'>"+
				"<a href='javascript:void(0)' id='add0' class='addButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
				"</div>";
				$('#replacingcontent').html(elementText);
				$('#replacingcontent').show();
			}
			
			$('.addButton').click(function(){
				var linkId=this.id;
				var linkCount=linkId.split("add")[1];
				console.log(linkCount);
				add(parseInt(linkCount));
			});	
		});
			
		
			
			
	}
	
	function getIframeSelectionText(iframe) {
		  var win = iframe.contentWindow;
		 // var doc = iframe.contentDocument || win.document;

		  if (win.getSelection) {
		    return win.getSelection().toString();
		  } /* else if (doc.selection && doc.selection.createRange) {
		    return doc.selection.createRange().text;
		  } */
	}
	
	function insertHtmlAtCursor(html) {
	    var range, node;
	    if (window.getSelection && window.getSelection().getRangeAt) {
	        range = window.getSelection().getRangeAt(0);
	        node = range.createContextualFragment(html);
	        range.insertNode(node);
	    } else if (document.selection && document.selection.createRange) {
	        document.selection.createRange().pasteHTML(html);
	    }
	}
	
	
	function add(currentDivCount){
		divCount=divCount+1;
		isPart=true;
		console.log("currentCount="+currentDivCount);
		console.log('divCount='+divCount);
		var partIdArray='';
		if($('#partCounter'+i).children('span').length>0){
			for(var i=currentDivCount+1;i<divCount;i++){
				 var spanId = $('#partCounter'+i).children('span').attr("id");
				 partIdArray=partIdArray + spanId.split("span")[1]  + ",";
				console.log(partIdArray); 
			} 
		}
		 
		
		var param="?language="+$('#language').val()+
		  "&currentSlot="+$('#currentSlot').val()+
		  "&slavePart="+$('#currentPart').val()+
		  "&isPart="+isPart+
		  "&partIdArray="+partIdArray+
		  "&orderCount="+(currentDivCount+1)+
		  "&masterSlot="+$('#masterSlot').val();
		
		 $.post($("form[action='proceeding/part/bookmark']").attr('action')+param,
					$("form[action='proceeding/part/bookmark']").serialize(),function(data){
			 var text = "<div id='partCounter"+ divCount+"'>";
				if($('.member'+$('#count').val()).html()!=null && $('.member'+$('#count').val()).html()!=''){
					text = text + $('.member'+$('#count').val()).html()  +" :<span id='span"+ data.id+"' class='bookmarkContent'> " + $('#proceedingContent-2').html() +"</span>";
				}else{
					text = text + "<span id='span"+ data.id +"' class='bookmarkContent'> " + $('#proceedingContent-2').html() + "</span>" ;
				}
				text = text + 
				"<div id='addDeleteLinks"+divCount+"'>"+
				"<a href='javascript:void(0)' id='add"+divCount+"' class='addButton'><img src='./resources/images/add.jpg' title='Add Part' class='imageLink' /></a>"+
				"<a href='javascript:void(0)'  id='delete"+divCount+"' class='deleteButton'><img src='./resources/images/delete.jpg' title='Delete Part' class='imageLink' onclick='delete(0);' /></a>"+
				"</div>";
				"</div>"+
				"<br>";	
				if(currentDivCount==0){
					$('.addDeleteLinks'+currentDivCount).after(text);
				}else{
					$('#partCounter'+currentDivCount).after(text);
				}
		 	
		 }); 
		
	}
	</script>
</head>
<body>
	<div class="fields clearfix watermark">	
	<form:form action="proceeding/part/bookmark" method="POST">
	<%@ include file="/common/info.jsp" %>
	<p>
	<label class="small"><spring:message code="proceeding.language" text="Language"/>*</label>
	<select name="language" id="language" class="sSelect" >
		<c:forEach items="${languages}" var="i">
			<option value="${i.type}">${i.name}</option>
		</c:forEach>
	</select>
	</p>
	
	<p>
	<label class="small"><spring:message code="proceeding.slot" text="Slot"/>*</label>
	<select name="slots" id="slots"  class="sSelect" >
	</select>
	</p>
	<div id="replacingcontent" style="display: none;text-align:justify;"></div>	
	<div class="fields">
		<h2></h2>
		<p class="tright">
			<input type="button" id="bookmark" value="bookmark" class="butDef"/>
			<input type="button" id="addtext" value="add text" class="butDef" style="display:none;"/>
		</p>
	</div>
	<input type="hidden" name="previousContent" id="previousContent"/>
	<input type="hidden" name="replacedContent" id="replacedContent"/>
	<input type="hidden" name="currentSlot" id="currentSlot" value="${currentSlot}"/>
	<input type="hidden" name="masterSlot" id="masterSlot"/>
	<input type="hidden" name="proceeding" id="proceeding" value="${proceedingId}"/>
	<input type="hidden" name="currentPart" id="currentPart" />
	<input type="hidden" name="count" id="count" value="${count}"/>
	<input type="hidden" name="bookmarkSize" id="bookmarkSize" value="${bookmarkSize}"/>
	</form:form>
	<input id="submissionMsg" value="<spring:message code='client.prompt.submit' text='Do you want to submit the motions.'></spring:message>" type="hidden">
	<input id="pleaseSelectMsg" value="<spring:message code='client.prompt.select' text='Please Select'/>" type="hidden">
	<input type="hidden" id="textToBeReplacedMessage" name="textToBeReplacedMessage" value="<spring:message code='part.textToBeReplacedMessage' text='Text to Be replaced'></spring:message>" disabled="disabled"/>
	</div>
	<input id="pleaseSelectMessage" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<div id='bookmarkKey' style='display:none;'></div>
</body>
</html>