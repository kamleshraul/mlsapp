/*Common javascript functions to be used across jsp files */
function initControls(){
	$("#comments").click(function(){
		var proc_id = $("#proc_id").val();
		if(proc_id=='')proc_id=0;
		$(".preview-pane .preview").animate({left:"-375px"},300,function(){
			$(this).animate({left:"-22px"},500).html('<img src="images/ajax-loader.gif" />').load('wf/'+proc_id+'/comments');
		});
	});

	$("#attachments").click(function(){
		var proc_id = $("#proc_id").val();
		if(proc_id=='')proc_id=0;
		$(".preview-pane .preview").animate({left:"-375px"},300,function(){
			$(this).animate({left:"-22px"},500).html('<img src="images/ajax-loader.gif" />').load('wf/'+proc_id+'/attachments');
		});
	});

	$(".file").each(function() {
		$('#' + this.id).uploadify( {
			'script' : 'file/upload?id=' + this.id,
			'fileDataName' : 'file',
			'folder' : '/Users/vishal/Documents/uploads',
			'auto' : true,
			'multi' : false,
			'onComplete' : onComplete,
			'onError' : onError
		});
	});
	$(".dynamicselect").fcbkcomplete({
        json_url:$(".dynamicselect").attr('url'),
        addontab: true,
        cache: false,
        height: 5,
        filter_case: false,
        filter_hide: false,
        newel: true,
        maxitems:50
       
    });
	$('.page_link').click(function(){
		$("#result_content").load($(this).attr('href')).dialog({title:'Leave Balance Details',modal:true});
		return false;
	});
	$('select[multiple="multiple"]').sexyselect({showTitle: false, selectionMode: 'multiple', styleize: true});
	$("input[class^='numeric']").autoNumeric();
	$("input[class^='date']").datepicker({changeMonth: true, changeYear: true, dateFormat: $('#dateformat').val(), yearRange: 'c-75:c+20'});
	$('.time').timepicker({ampm:true});
	//$('.datetime').datetimepicker({changeMonth: true, changeYear: true, dateFormat:$('#dateformat').val(), yearRange: 'c-75:c+0',ampm:true});

	function onComplete(event, ID, fileObj, response, data) {
		$('#' + event.target.id + 'Uploader').replaceWith(response);
	}

	function onError(event, ID, fileObj, errorObj) {
		console.log(errorObj);
		$.jGrowl("Error occured while uploading file:");
	}

	$('.autosuggest').each(function(){
		$(this).flexbox($(this).attr('url'), {  
			paging: false,  
			maxVisibleRows: 20,
			onSelect:autosuggest_onchange
		});
	});
	
	function autosuggest_onchange(){
		var target = $(this).parent().attr('target');
		if(target!=''){
			var url = $('#'+target).attr('url').replace('#',$(this).attr('hiddenvalue'));
			$('#'+target).empty();
			$('#'+target).flexbox(url,{  
				paging: false,  
				maxVisibleRows: 20,
				onSelect:autosuggest_onchange
			});
		}
	 }
};

function loadGrid(gridId, baseFilter) {
	var c_grid = null;
	$.ajax({async:false,url:'grid/' + gridId + '/meta.json', success:function(grid) {
		c_grid = $('#grid').jqGrid({
			scroll:1,
			altRows:true,
			autowidth:true,
			shrinkToFit:true,
			ajaxGridOptions:{async:false},
			url:'grid/'+ gridId +'.json',
			datatype: 'json',
			mtype: 'GET',
			colNames:eval(grid.colNames),
			colModel :eval(grid.colModel),
			pager: '#grid_pager',
			rowNum:grid.pageSize,
			sortname: 'id',
			sortorder:grid.sortOrder,
			viewrecords: true,
			jsonReader: { repeatitems : false},
			gridview:true,
			postData: {
				"baseFilters": baseFilter
			},
			loadComplete:function(data,obj){
				var top_rowid = $('#grid tbody:first-child tr:nth-child(2)').attr('id');
				if(!top_rowid){
					$('#contentPanel').load(grid.detailView +'/new');
				}
				else{
					$('#grid').setSelection(top_rowid);
				}
			},
			onSelectRow:function() {
				//showProcessing(true);
				/*if($("#detail-content").length==0){
					$('body').append('<div id="detail-content"><div>');
				}
				var row = $("#grid").jqGrid('getGridParam','selrow'); 
				var url = $('#controller').val()+'/'+row;
				if($('#controller').val()=='users'){
					url='users/'+row+'/profile';
					$.get(url, function(data){
						$(".main-content").empty()
						$(".main-content").html(data);
					});
				}
				else{
					$.get(url, function(data){
						$("#detail-content").html(data).dialog({height:grid.formHeight, width:grid.formWidth, title:'Edit Details',position:'center',close: function(ev, ui) { $(this).remove(); }});
						showProcessing(false);
					});
				}*/
				var row = $("#grid").jqGrid('getGridParam','selrow');
				$('#contentPanel').load(grid.detailView + '/' + row,function(data){
	                var title = $(data).filter('title').text();
					$('#content > .subHeader > div').html(title);
				});
			}
		});
		$("#grid").jqGrid('navGrid','#grid_pager',{edit:false,add:false,del:false, search:true},{},{},{},{multipleSearch:true});
		$("#grid").jqGrid('bindKeys');
		$("#new_record").click(function(){
			var url = $(this).attr('href');
			$('.contentPanel').load(url);
   			return false;
		});
		$("#delete_record").click(function() {
			var row = $("#grid").jqGrid('getGridParam','selrow'); 
			if(row==null){
				alert("Please select the desired row to delete");
				return;
			}
			else{
				var url = $(this).attr('href');
				$("#grid").jqGrid('delGridRow',row,{reloadAfterSubmit:true, mtype:'DELETE', url:url+'/'+row});
			}
			return false;
		});
	}});
	return c_grid;
};


function showProcessing(flag){
	if(flag)
		$("#activity").show();
	else
		$("#activity").hide();
};

function unUploadify(element){
	$(element).unbind("uploadifyComplete");
	$(element).next(element+"Uploader").remove();
	$(element).next(element+"Queue").remove();
	$(element).css("display","inline");
	$(element+"Remove").show();
};
	
function uploadify(element){
	$(element).uploadify( {
	'script' : 'doc/upload.json',
	'fileDataName' : 'file',
	'folder' : '/Users/vishal/Documents/uploads',
	'auto' : true,
	'multi' : false,
	 onComplete : uploadify_oncomplete
	});
	$(element+"Remove").hide();
};

function uploadify_oncomplete(event, ID, fileObj, response, data){
	var file = $.parseJSON(response);
	var element = '#'+event.target.id;
	$(element).val(file.file.tag);
	unUploadify(element);
	return false;
};