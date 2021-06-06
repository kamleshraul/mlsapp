/*Common javascript functions to be used across jsp files */
currentGridPage=1;
currentSelectedRow=1;
var highSecurityPasswordEntered = false;
var detectedBrowser='';

function _ajax_request(url, data, callback, type, method) {
    if (jQuery.isFunction(data)) {
        callback = data;
        data = {};
    }
    return jQuery.ajax({
        type: method,
        url: url,
        data: data,
        success: callback,
        dataType: type
        });
}

jQuery.extend({
    put: function(url, data, callback, type) {
        return _ajax_request(url, data, callback, type, 'PUT');
    },
    delete_: function(url, data, callback, type) {
        return _ajax_request(url, data, callback, type, 'DELETE');
    }
});

function initControls(){
	/*$('select[multiple="multiple"]').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true});
	$("input[class^='numeric']").autoNumeric();
	$("input[class^='integer']").autoNumeric({mDec: 0});
	$('.autosuggest').each(function(){
		$(this).flexbox($(this).attr('url'), {  
			paging: false,  
			maxVisibleRows: 20,
			onSelect:autosuggest_onchange
		});
	});	
	$('#dateformat').val();*/
	/*$('select[multiple="multiple"]').sexyselect({width:250,showTitle: false, selectionMode: 'multiple', styleize: true,allowDelete:false,background:'#fff',allowInput:false});*/
	$("input[class^='integer']").autoNumeric({mDec: 0});
	
	/* ENFORCE THE USER TO ENTER ONLY NUMBERS IN CASE OF NUMERIC FIELDS */
	$('.integer').autoNumeric({mDec: 0});
	
	$('.datemask').focus(function(){		
		if($(this).val()==""){
			$(".datemask").mask("99/99/9999");
		}
	});
	$('.datetimemask').mask("99/99/9999 99:99:99");
	$('.timemask').mask("99:99:99");
	$('.timemask').focus(function(){		
		if($(this).val()==""){
			$(".timemask").mask("99:99:99");
		}
	});
	$('.datetimenosecondmask').mask("99/99/9999 99:99");
	
	$('.timenosecondmask').mask("99:99");
	
	$(':input:visible:not([readonly]):first').focus();
	
	$('.wysiwyg').wysiwyg({
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
			strikeThrough: { visible: true },
			underline: { visible: true },
			subscript: { visible: true },
			superscript: { visible: true },
			insertOrderedList  : { visible : true},
			increaseFontSize:{visible:true},
			decreaseFontSize:{visible:true},
			highlight: {visible:true}			
		},
		plugins: {
			autoload: true,
			i18n: { lang: "mr" }
			//rmFormat: {	rmMsWordMarkup: true }
		}
	});
	
	//$.wysiwyg.rmFormat.enabled = false;
	
	$('.wysiwyg').change(function(e){
		var idval = this.id;			
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
						return false;
					
					} else if($('#'+idval).val().toLowerCase().indexOf("o:p") >= 0){
						$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
						isWordContentTyped = true;
						$.prompt($('#noMsOfficeContentPrompt').val());	
						return false;
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
							return false;
						
						} else if($('#'+idval).val().toLowerCase().indexOf("&lt;ol&gt;&lt;/ol&gt;") >= 0){	
							$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
							$.prompt($('#noInvalidFormattingPrompt').val());	
							return false;
						
						} else if($('#'+idval).val().toLowerCase().indexOf("<ol></ol>") >= 0){
							$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
							$.prompt($('#noInvalidFormattingPrompt').val());	
							return false;
						
						} else if($('#'+idval).val().toLowerCase().indexOf("br style=") >= 0){	
							$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
							$.prompt($('#noInvalidFormattingPrompt').val());
							return false;
						
						} else if($('#'+idval).val().toLowerCase().indexOf("-webkit-text-stroke-widt") >= 0){	
							$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());
							$.prompt($('#noInvalidFormattingPrompt').val());
							return false;
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
	});
	$('.wysiwyg').each(function(){
		var idval = this.id;
		//if($('#'+idval).is('[readonly]')){
			$('<input>').attr({
			    type: 'hidden',
			    id: 'copyOf'+idval,
			    value: $('#'+idval).val()
			}).appendTo($('#'+idval));
		//}
	});
	$(".multiselect").parents("p").css("position","relative");
	$('.multiselect').sexyselect({			
		showTitle: false,			
		allowFilter: true,			
		allowDelete: false,
		selectionMode: 'multiple',	
		defaultCheckAllText: $('#defaultCheckAllText').val(),
		defaultUnCheckAllText: $('#defaultUnCheckAllText').val()
	});	
	/**** Transliteration ****/
	/*$("input,textarea").keydown(function(e){
		var id=$(this).attr("id");
		if(e.which==32){
			$.get('ref/transliterate?input='+$(this).val(),function(data){
			$("#"+id).val(data.name);
			
			});		
		}
	});*/	
	
	/*$('.sSelectMultiple').each(function() {		
		console.log('changing css dynamically');
		$(this).parent('p').closest('.small').css('top', '-80px !important');
	});*/
};
function resize_grid(){
	$('#grid').fluidGrid({base:'#grid_container', offset:-0});
	$("#grid").jqGrid('setGridHeight', $('#navigation').innerHeight()-103);
}
function scrollRowsInGrid(e) {
	var gridArr = $('#grid').getDataIDs();
    var selrow = $('#grid').getGridParam("selrow");
    var curr_index = 0;
    for(var i = 0; i < gridArr.length; i++)
    {
        if(gridArr[i]==selrow)
            curr_index = i;
    }

    if(e.keyCode == 38) //up
    {
        if((curr_index-1)>=0)
            $('#grid').resetSelection().setSelection(gridArr[curr_index-1],true);
    }
    if(e.keyCode == 40) //down
    {
        if((curr_index+1)<gridArr.length)
            $('#grid').resetSelection().setSelection(gridArr[curr_index+1],true);
    }
}


function loadGrid(gridId, gridurl, baseFilter) {
	var c_grid = null;
	//added by amitd and sandeeps.	
	//Allow use of controllers other than grid controllers i.e data in the grid can be fetched using custom startegy
	var baseURL=null;
	if(gridurl!=undefined){
		baseURL=gridurl;
	}else if(gridurl!=null){
		baseURL=gridurl;
	}else{
		baseURL='grid/data/';
	}
	var url=baseURL+ gridId +'.json';	
	//for parameters have a field with name gridURLParams in name=valye format separated by '&'
	if($('#gridURLParams').val()!=undefined){
		url=url+'?'+$('#gridURLParams').val();
	}
	
	$.ajax({async:false,url:'grid/' + gridId + '/meta.json', success:function(grid) {
		c_grid = $('#grid').jqGrid({
			scroll:1,
			altRows:true,
			autowidth:true,
			height:'400px',
			ajaxGridOptions:{async:false},
			url:url,
			datatype: 'json',
			mtype: 'GET',
			colNames:eval(grid.colNames),
			colModel :eval(grid.colModel),
			pager: '#grid_pager',
			page: currentGridPage,
			rowNum:grid.pageSize,
			sortname: grid.sortField,
			sortorder:grid.sortOrder,
			viewrecords: true,
			jsonReader: { repeatitems : false},
			gridview:true,
			multiselect:eval(grid.multiSelect),	
			grouping:eval(grid.group),
			groupingView : { groupField : [grid.groupField],
							 groupText :['<b>{0} - {1} Items(s)</b>'],
							 groupColumnShow : [true],
							 groupCollapse : false,
							 groupOrder: ['desc']},
			subGrid:eval(grid.subGrid),	
			subGridRowExpanded: function(subgrid_id, row_id) {			    
			       var subgrid_table_id;
			       subgrid_table_id = subgrid_id+"_t";
			       jQuery("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table>");
			       var subgridId=eval(grid.subGridId);			       
			       $.ajax({async:false,url:'grid/' + subgridId + '/meta.json', success:function(subgrid) {			    	   
			    	   	c1_grid = $("#"+subgrid_table_id).jqGrid({
						scroll:1,
						altRows:true,
						autowidth:true,
						height:'100%',
						ajaxGridOptions:{async:false},
						url:'grid/data/'+eval(grid.subGridId)+'.json'+'?parent='+row_id,
						datatype: 'json',
						mtype: 'GET',
						colNames:eval(subgrid.colNames),
						colModel :eval(subgrid.colModel),
						rowNum:subgrid.pageSize,
						sortname: subgrid.sortField,
						sortorder:subgrid.sortOrder,
						viewrecords: true,
						jsonReader: { repeatitems : false},
						gridview:true,
						multiselect:eval(subgrid.multiSelect)
						});	
					}});			       
			},			
			postData: {
				"baseFilters": baseFilter
			},
			loadComplete: function(data, obj) {
//				var curr_page = $(this).getGridParam('page');
//				if(curr_page==1) {
//					var top_rowid = $('#grid tbody:first-child tr:nth-child(1)').attr('id');
//					$(this).setSelection(top_rowid, true);
//				}
//				else{
					currentGridPage=1;
				//}
				if(currentSelectedRow==1){
					var top_rowid = $('#grid tbody:first-child tr:nth-child(1)').attr('id');
					$(this).setSelection(top_rowid, true);
					//$(this).setSelection(currentSelectedRow,true);
				}
				else{
					$(this).setSelection(currentSelectedRow,true);
					currentSelectedRow=1;
				}
				//this is the case when we delete all the records in the grid and reload the list.If we click on 
				//other tabs then key value has not been set and is still the previous value giving
				//exceptions
				//else{
					//$('#key').val("");
				//}
				},
			onSelectRow: function(rowid,status) {
				//added by sandeeps
				//first we must ckeck for presence of the handler so that custom logic rather than generic logic
				//is executed.And only if no handler is found we must set the value of key.This is particularly
				//useful in case of screens containing multiple tags with grids on one or more of them with one
				//grid serving as parent grid
				if(typeof window.rowSelectHandler == 'function'){
					rowSelectHandler(rowid,status);			    			
		    	}else if($('#key')){
					$('#key').val(rowid);					
				}
		    },
		    ondblClickRow: function(rowid,
		    		iRow,
		    		iCol,
		    		e) {		    	
		    	if(typeof window.rowDblClickHandler == 'function'){
		    		rowDblClickHandler(rowid, iRow, iCol, e);			    			
		    	}		    	
		    }
		});
		$("#grid").jqGrid('navGrid','#grid_pager',{search:false,edit:false,add:false,del:false});	
		$("#grid").jqGrid('bindKeys');
		$("#showhide_columns").click(function(){
			$("#grid").setColumns({caption:"Check/Uncheck columns to Show/Hide"});
			return false;
		});		
	}});
	return c_grid;
};

function searchRecord(){
	$("#grid").jqGrid('searchGrid', {multipleSearch:true,overlay:false});
}	

function scrollTop(){
	$('html').animate({scrollTop:0}, 'slow');
	$('body').animate({scrollTop:0}, 'slow');			 	   	
}

function showTabByIdAndUrl(id, url) {
	$('a').removeClass('selected');
	//id refers to the tab name and it is used just to highlight the selected tab
	$('#'+ id).addClass('selected');
	//tabcontent is the content area where result of the url load will be displayed
	$('.tabContent').load(url, function (response, status, xhr) {
		var code = parseInt(xhr.status);
		var msg = "";
		if(code > 399){
			msg = ""+xhr.status+" ";
			if(code==403){
				msg += "ACTION PERFORMED IS NOT ALLOWED CURRENTLY.<br>Please Try again later.";
			}else if(code==500){
				msg += "Contact administrator.";
			}
			
			$("#error_p").html(msg).css({'color':'red', 'display':'block'});
		}
  });
	scrollTop();
};

function showTabByIdAndUrl(id, url, restrictionErrorMessage) {
	$('a').removeClass('selected');
	//id refers to the tab name and it is used just to highlight the selected tab
	$('#'+ id).addClass('selected');
	//tabcontent is the content area where result of the url load will be displayed
	$('.tabContent').load(url, function (response, status, xhr) {
		var code = parseInt(xhr.status);
		var msg = "";
		if(code > 399){
			msg = ""+xhr.status+" ";
			if(code==403){
				if(restrictionErrorMessage!=null && restrictionErrorMessage!='' && restrictionErrorMessage!=undefined) {
					msg = restrictionErrorMessage;
				} else {
					msg += "ACTION PERFORMED IS NOT ALLOWED CURRENTLY.<br>Please Try again later.";	
				}
			}else if(code==500){
				msg += "Contact administrator.";
			}
			
			$("#error_p").html(msg).css({'color':'red', 'display':'block'});
		}
  });
	scrollTop();
};

function form_submit(path, params, method) {
	form_submit_with_target(path, params, method, '_self');
}

function form_submit_with_target(path, params, method, target) {
	method = method || "post"; // Set method to post by default if not specified.
	target = target || "_self";

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);
    form.setAttribute("target", target);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}

function validateHighSecurityPassword(isHighSecurityValidationRequired, securedItemId, eventName) {
	var parameters="securedItemId="+securedItemId+"&eventName="+eventName;
	var resourceURL='high_security_validation_check/init?'+parameters;
	$.get(resourceURL,function(data){
		$.fancybox.open(data,{
			autoSize:false,
			width:280,
			height:210,
			closeBtn: false,
			helpers: {
				overlay: { closeClick: false } //Disable click outside event
		    },
		    afterLoad: function() {
		    	setTimeout(function(){
		    		$("#highSecurityPassword").focus();
				},200);
		    },
			onClose: function() {
				return false;
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

function loadDistrictsByStateId(id,type){
	$.ajax({
		url:'ref/'+id+'/districts',
		datatype:'json',
		success:function(data){
			$('#'+type+'Districts option').remove();
			if(data.length>=1){
				for(var i=0;i<data.length;i++){
					$('#'+type+'Districts').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
				}
				loadTehsilsByDistrictId(data[0].id,type);							
			}else{
				$('#'+type+'tehsils option').remove();
			}					
	}							
	});
}

function loadTehsilsByDistrictId(id,type){
	$.ajax({
		url:'ref/'+id+'/tehsils',
		datatype:'json',
		success:function(data){
		$('#'+type+'Tehsils option').remove();
			for(var i=0;i<data.length;i++){
			$('#'+type+'Tehsils').append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
		}
	}							
	});
}

/*Code for uploading files*/
function unUploadify(element){
	$(element).unbind("uploadifyComplete");
	$(element).next(element+"Uploader").remove();
	$(element).next(element+"Queue").remove();
	$(element).css("display","inline");
	$(element+"Remove").show();
	alert("File Uploaded Successfully");
};
	
	function uploadify(element,ext,size,url){
		var sizeInMB=size/(1024*1024);	
		var extTokens=ext.split(",");
		var extType="";
		for(var i=0;i<extTokens.length;i++){
			var temp=extTokens[i].replace('.','*.');
			extType=extType+temp+";";
		}	
	$(element).uploadify( {
	'script' : 'file/upload.json?ext='+ext+"#"+size,	
	'fileDataName' : 'file',
	'auto' : true,
	'multi' : false,
	'fileExt' : extType,
	'fileDesc'    : 'Files ('+ext+')',
	'onComplete' : uploadify_oncomplete,
	'uploader':'./resources/js/uploadify.swf',
	'cancelImg':'./resources/images/cancel.png',
	'sizeLimit':size,
	'onError': function (event,ID,fileObj,errorObj) {
		if(errorObj.type=="File Size"){
			alert("It seems you are trying to upload "+ Math.round((fileObj.size/(1024*1024)))+"MB file which exceeds permissible limit of "+sizeInMB+"MB");
		}
		else if(errorObj.type=="HTTP"){
			alert("File is not uploaded...please try uploading again");
		}
		else{
			alert(errorObj.type + ' Error: ' + errorObj.info);
		}
	      $('#'+event.target.id).uploadifyCancel(ID);

	    }
	});
	$(element+"Remove").hide();
};
function uploadify_oncomplete(event, ID, fileObj, response, data){
	try{
		var file = $.parseJSON(response);
		var element = '#'+event.target.id;
		$(element).val(file.file.originalFileName);
		$(element).attr("readonly","true");
		$(element+'Field').val(file.file.tag);
		unUploadify(element);
		if(event.target.id=='photo'){
			$('#photoDisplay').attr('src','/els/file/photo/'+$('#'+event.target.id+'Field').val());
			$('#photoDiv').removeClass('hideDiv').addClass('showDiv');
		}
	}
	catch(ex){
		alert("File is not uploaded...please try uploading again");
		$('#'+event.target.id).uploadifyCancel(ID);
	}		
	return false;
};
function removeUpload(element,ext)
{
$.ajax({
   				    type: "DELETE",
   				    url: "file/remove/"+$(element).val(),
   				    contentType: "application/json; charset=utf-8",
   				    dataType: "json",
   				    success: function(json) {
   				        if(json==true){
   				        	$(element).val('');
   				        	uploadify(element,ext);
   	   				        alert('File successfully deleted');
   				        }
   				    },
   				    error: function (xhr, textStatus, errorThrown) {
   				    	alert(xhr.responseText);
   				    }
   				});

};
/***************/

jQuery.fn.sortElements = (function(){
	 
    var sort = [].sort;
 
    return function(comparator, getSortable) {
 
        getSortable = getSortable || function(){return this;};
 
        var placements = this.map(function(){
 
            var sortElement = getSortable.call(this),
                parentNode = sortElement.parentNode,
 
                // Since the element itself will change position, we have
                // to have some way of storing its original position in
                // the DOM. The easiest way is to have a 'flag' node:
                nextSibling = parentNode.insertBefore(
                    document.createTextNode(''),
                    sortElement.nextSibling
                );
 
            return function() {
 
                if (parentNode === this) {
                   // throw new Error(
                       // "You can't sort elements if any one is a descendant of another."
                   // );
                }
 
                // Insert before flag:
                parentNode.insertBefore(this, nextSibling);
                // Remove flag:
                parentNode.removeChild(nextSibling);
 
            };
 
        });
 
        return sort.call(this, comparator).each(function(i){
            placements[i].call(getSortable.call(this));
        });
 
    };
 
})();

//custom multiselect having checkboxes, select all & unselect all
//added by dhananjayb
jQuery.fn.multiSelect = function() {
	
	//$(this).hide();		    	
		
	var selectBoxId = $(this).attr('id');
	//var selectBoxName = $(this).attr('name');
	//console.log("selectBoxId: " + selectBoxId);
	
	var content = "";			    	
	
	content += "<a id='selectAll_" + selectBoxId + "' href='#'>" + $('#selectAll').val() + "</a> | <a id='unselectAll_" + selectBoxId + "' href='#'>" + $('#selectNone').val() + "</a>";
	
	$('#' + selectBoxId + ' option').each(function(){		
		if($(this).attr('selected') == 'selected') {			
			if($(this).attr('hidden') == 'hidden' || $(this).css('display') == 'none') {
				content += "<label><input type='checkbox' hidden='true' class='checkbox_" + selectBoxId + "' value='" + $(this).val() + "' checked='checked'/>" + $(this).text() + "</label>";
			}
			else {
				content += "<label><input type='checkbox' class='checkbox_" + selectBoxId + "' value='" + $(this).val() + "' checked='checked'/>" + $(this).text() + "</label>";
			}			
		} else {
			if($(this).attr('hidden') == 'hidden' || $(this).css('display') == 'none') {
				content += "<label><input type='checkbox' hidden='true' class='checkbox_" + selectBoxId + "' value='" + $(this).val() + "'/>" + $(this).text() + "</label>";
			}
			else {
				content += "<label><input type='checkbox' class='checkbox_" + selectBoxId + "' value='" + $(this).val() + "'/>" + $(this).text() + "</label>";
			}			
		}			    		
	}); 	
	
	if($('#span_'+selectBoxId).get(0)){
		$('#span_'+selectBoxId).remove();
	}
	$("<span id='span_" + selectBoxId + "' class='multiSelectSpan'>&nbsp;</span>").appendTo($('#' + selectBoxId).parent());
	$('#span_'+selectBoxId).html(content);		
	
	if($('#' + selectBoxId).attr('disabled') == 'disabled') {
		$('#' + selectBoxId).show();
		$('#span_'+selectBoxId).remove();//css('display','none !important');	
	} else {
		$('#' + selectBoxId).hide();
		$('#span_'+selectBoxId).show();
	}
	
	var labels = $('#span_'+selectBoxId).find("label");
	labels.each(function(){
		var label = $(this);	
		label.click(function(e) {
        	if(e.ctrlKey) {
        		//Ctrl+Click should be handled as handled in select box       		       		
        		var checkboxInLabel = $(this).find('input[type=checkbox]');
        		
        		//change state of checkbox
        		if (checkboxInLabel.attr("checked")=="checked") {
        			checkboxInLabel.removeAttr("checked");        			
        		} else {        			
        			checkboxInLabel.attr("checked", "checked");
        		}
        		
        		//handle like checkbox click event
        		if (checkboxInLabel.attr("checked")) {        			
        			$(this).addClass("multiSelectSpan-on");			                	
                	$('#'+selectBoxId +' option').each(function(){						    		
                		if($(this).val() == checkboxInLabel.attr('value')) {			                			
                			$(this).attr('selected', 'selected');						    			
    		    		}			                		
    		    	});				                    
                }
                else {
                	$(this).removeClass("multiSelectSpan-on");
                    $('#'+selectBoxId +' option').each(function(){						    		
                		if($(this).val() == checkboxInLabel.attr('value')) {			                			
                			$(this).removeAttr('selected');						    			
    		    		}			                		
    		    	});	
                }
                $('#'+selectBoxId).change();        		
        	}
		});
	});
	
    var checkboxes = $('#span_'+selectBoxId).find("input:checkbox");
    checkboxes.each(function() {
        var checkbox = $(this);        
        
        // Highlight pre-selected checkboxes
        if (checkbox.attr("checked"))
            checkbox.parent().addClass("multiSelectSpan-on");
        
        if(checkbox.attr("hidden")) {        	
        	checkbox.parent().addClass("hiddenNow");
        }else {        	
        	checkbox.parent().removeClass("hiddenNow");        	
        }

        // Highlight checkboxes that the user selects
        checkbox.click(function(e) {
        	if(e.ctrlKey) {
        	    //no change in case of Ctrl+Click as this is checkbox        		
        		return false;
        	}
        	
            if (checkbox.attr("checked")) {
            	checkbox.parent().addClass("multiSelectSpan-on");			                	
            	$('#'+selectBoxId +' option').each(function(){						    		
            		if($(this).val() == checkbox.attr('value')) {			                			
            			$(this).attr('selected', 'selected');						    			
		    		}			                		
		    	});				                    
            }
            else {
                checkbox.parent().removeClass("multiSelectSpan-on");
                $('#'+selectBoxId +' option').each(function(){						    		
            		if($(this).val() == checkbox.attr('value')) {			                			
            			$(this).removeAttr('selected');						    			
		    		}			                		
		    	});	
            }
            $('#'+selectBoxId).change();
        });
    });
    //to select all
    $('a#selectAll_'+selectBoxId).click(function() {	
    	scrollTop(false);
		var checkboxes = $(this).parent().find("input:checkbox");
    	checkboxes.each(function() {
    		var checkbox = $(this);
    		checkbox.attr("checked", "checked");		
    		checkbox.parent().addClass("multiSelectSpan-on");
    	});	
    	var changed = false;
    	$('#'+selectBoxId +' option').each(function(){
    		if(!$(this).is(':selected')) {
    			changed = true;
    		}
    		$(this).attr('selected', 'selected');
    	});	
    	if(changed == true) {
    		$('#'+selectBoxId).change();
    	}    	
    });
    //to unselect all
    $('a#unselectAll_'+selectBoxId).click(function() {	
    	scrollTop(false);
		var checkboxes = $(this).parent().find("input:checkbox");
    	checkboxes.each(function() {
    		var checkbox = $(this);
    		checkbox.removeAttr('checked');	
    		checkbox.parent().removeClass("multiSelectSpan-on");
    	});
    	var changed = false;
    	$('#'+selectBoxId +' option').each(function(){
    		if($(this).is(':selected')) {
    			changed = true;
    			$(this).removeAttr('selected');
    		}    		
    	});				    		
    	if(changed == true) {    		
    		$('#'+selectBoxId).change();
    	}   	
    });	 		    
};

var BrowserDetect = {
		init : function() {
			this.browser = this
					.searchString(this.dataBrowser)
					|| "An unknown browser";
			this.version = this
					.searchVersion(navigator.userAgent)
					|| this
							.searchVersion(navigator.appVersion)
					|| "an unknown version";
			this.OS = this.searchString(this.dataOS)
					|| "an unknown OS";
		},
		searchString : function(data) {
			for ( var i = 0; i < data.length; i++) {
				var dataString = data[i].string;
				var dataProp = data[i].prop;
				this.versionSearchString = data[i].versionSearch
						|| data[i].identity;
				if (dataString) {
					if (dataString
							.indexOf(data[i].subString) != -1)
						return data[i].identity;
				} else if (dataProp)
					return data[i].identity;
			}
		},
		searchVersion : function(dataString) {
			var index = dataString
					.indexOf(this.versionSearchString);
			if (index == -1)
				return;
			return parseFloat(dataString
					.substring(index
							+ this.versionSearchString.length
							+ 1));
		},
		dataBrowser : [ {
			string : navigator.userAgent,
			subString : "Chrome",
			identity : "Chrome"
		}, {
			string : navigator.userAgent,
			subString : "OmniWeb",
			versionSearch : "OmniWeb/",
			identity : "OmniWeb"
		}, {
			string : navigator.vendor,
			subString : "Apple",
			identity : "Safari",
			versionSearch : "Version"
		}, {
			prop : window.opera,
			identity : "Opera",
			versionSearch : "Version"
		}, {
			string : navigator.vendor,
			subString : "iCab",
			identity : "iCab"
		}, {
			string : navigator.vendor,
			subString : "KDE",
			identity : "Konqueror"
		}, {
			string : navigator.userAgent,
			subString : "Firefox",
			identity : "Firefox"
		}, {
			string : navigator.vendor,
			subString : "Camino",
			identity : "Camino"
		}, { // for newer Netscapes (6+)
			string : navigator.userAgent,
			subString : "Netscape",
			identity : "Netscape"
		}, {
			string : navigator.userAgent,
			subString : "MSIE",
			identity : "Explorer",
			versionSearch : "MSIE"
		}, {
			string : navigator.userAgent,
			subString : "Gecko",
			identity : "Mozilla",
			versionSearch : "rv"
		}, { // for older Netscapes (4-)
			string : navigator.userAgent,
			subString : "Mozilla",
			identity : "Netscape",
			versionSearch : "Mozilla"
		} ],
		dataOS : [ {
			string : navigator.platform,
			subString : "Win",
			identity : "Windows"
		}, {
			string : navigator.platform,
			subString : "Mac",
			identity : "Mac"
		}, {
			string : navigator.userAgent,
			subString : "iPhone",
			identity : "iPhone/iPod"
		}, {
			string : navigator.platform,
			subString : "Linux",
			identity : "Linux"
		} ]

};
BrowserDetect.init();	
detectedBrowser=BrowserDetect.browser;