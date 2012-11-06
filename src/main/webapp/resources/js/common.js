/*Common javascript functions to be used across jsp files */
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
	$('.datemask').focus(function(){		
		if($(this).val()==""){
			$(".datemask").mask("99/99/9999");
		}
	});
	$(':input:visible:not([readonly]):first').focus();
	
	$('.wysiwyg').each(function(){		
		var idval = this.id;		
		if($('#'+idval).is('[readonly]')){			
			$('#'+idval).css('color', '#999999');
			$('<input>').attr({
			    type: 'hidden',
			    id: 'copyOf'+idval,
			    value: $('#'+idval).val()
			}).appendTo($('#'+idval));
		}
	});	
	$('.wysiwyg').wysiwyg({		
		resizeOptions: true,		
		controls:{			
			strikeThrough: { visible: true },
			underline: { visible: true },
			subscript: { visible: true },
			superscript: { visible: true },
			insertOrderedList  : { visible : true},
			increaseFontSize:{visible:true},
			decreaseFontSize:{visible:true},
			highlight: {visible:true}			
		}
	});		
	$('.wysiwyg').change(function(e){
		var idval = this.id;			
		if($('#'+idval).is('[readonly]')){
			$('#'+idval+'-wysiwyg-iframe').contents().find('html').html($('#copyOf'+idval).val());			
		} else {
			if($('#'+idval).val()=="<p></p>"){						
				$('#'+idval+'-wysiwyg-iframe').focus();				
				$('#'+idval+'-wysiwyg-iframe').contents().find('html').html("<br><p></p>");				
			}
		}
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
		console.log(eval(grid.subGrid));
		console.log(grid.groupField);
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
			rowNum:grid.pageSize,
			sortname: grid.sortField,
			sortorder:grid.sortOrder,
			viewrecords: true,
			jsonReader: { repeatitems : false},
			gridview:true,
			multiselect:eval(grid.multiSelect),	
			grouping:eval(grid.group),
			groupingView : { groupField : [grid.groupField],
							 groupText : ['<b>{0} - {1} Items(s)</b>'],
							 groupColumnShow : [false],
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
				var curr_page = $(this).getGridParam('page');
				if(curr_page==1) {
					var top_rowid = $('#grid tbody:first-child tr:nth-child(1)').attr('id');
					$(this).setSelection(top_rowid, true);
				}//this is the case when we delete all the records in the grid and reload the list.If we click on 
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
	$('.tabContent').load(url);
	scrollTop();
};


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
	console.log("unUploadify Called. Line no: 231");
	$(element).unbind("uploadifyComplete");
	$(element).next(element+"Uploader").remove();
	$(element).next(element+"Queue").remove();
	$(element).css("display","inline");
	$(element+"Remove").show();
	alert("File Uploaded Successfully");
};
	
	function uploadify(element,ext,size,url){
		console.log("uploadify Called. Line no: 241");
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
	console.log("uploadify_oncomplete Called. Line no: 277");
	try{
		var file = $.parseJSON(response);
		console.log(file);
		var element = '#'+event.target.id;
		console.log(event.target.id);
		$(element).val(file.file.originalFileName);
		console.log(file.file.originalFileName);
		$(element).attr("readonly","true");
		$(element+'Field').val(file.file.tag);
		console.log(file.file.tag);
		unUploadify(element);
		if(event.target.id=='photo'){
			console.log("INSIDE PHOTO");
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
	console.log("removeUpload Called. Line no: 303");	
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