<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<style type="text/css">	
	.fg-button { outline: 0; margin:0 4px 0 0; padding: .4em 1em; text-decoration:none !important; cursor:pointer; position: relative; text-align: center; zoom: 1; }
	.fg-button .ui-icon { position: absolute; top: 50%; margin-top: -8px; left: 50%; margin-left: -8px; }
	
	a.fg-button { float:left; }
	
	/* remove extra button width in IE */
	button.fg-button { width:auto; overflow:visible; }
	
	.fg-button-icon-left { padding-left: 2.1em; }
	.fg-button-icon-right { padding-right: 2.1em; }
	.fg-button-icon-left .ui-icon { right: auto; left: .2em; margin-left: 0; }
	.fg-button-icon-right .ui-icon { left: auto; right: .2em; margin-left: 0; }
	
	.fg-button-icon-solo { display:block; width:8px; text-indent: -9999px; }	 /* solo icon buttons must have block properties for the text-indent to work */	
	
	.fg-buttonset { float:left; }
	.fg-buttonset .fg-button { float: left; }
	.fg-buttonset-single .fg-button, 
	.fg-buttonset-multi .fg-button { margin-right: -1px;}
	
	.fg-toolbar { padding: .5em; margin: 0;  }
	.fg-toolbar .fg-buttonset { margin-right:1.5em; padding-left: 1px; }
	.fg-toolbar .fg-button { font-size: 1em;  }

	/*demo page css*/
	h2 { clear: both; padding-top:1.5em; margin-top:0; } 
	.strike { text-decoration: line-through; }
</style>	
	<script type="text/javascript">
		$(document).ready(function () {
			//$('.center-center').html('<div class="fg-toolbar ui-widget-header ui-corner-all ui-helper-clearfix"><div class="fg-buttonset ui-helper-clearfix" ><a href="#" class="fg-button ui-state-default fg-button-icon-solo ui-corner-all" title="New"><span class="ui-icon ui-icon-folder-open"></span> Open</a><a href="#" class="fg-button ui-state-default fg-button-icon-solo  ui-corner-all" title="Save"><span class="ui-icon ui-icon-disk"></span> Save</a><a href="#" class="fg-button ui-state-default fg-button-icon-solo  ui-corner-all" title="Delete"><span class="ui-icon ui-icon-trash"></span> Delete</a></div>');
			$(".fg-button:not(.ui-state-disabled)")
			.hover(
				function(){ 
					$(this).addClass("ui-state-hover"); 
				},
				function(){ 
					$(this).removeClass("ui-state-hover"); 
				}
			)
			.mousedown(function(){
					$(this).parents('.fg-buttonset-single:first').find(".fg-button.ui-state-active").removeClass("ui-state-active");
					if( $(this).is('.ui-state-active.fg-button-toggleable, .fg-buttonset-multi .ui-state-active') ){ $(this).removeClass("ui-state-active"); }
					else { $(this).addClass("ui-state-active"); }	
			})
			.mouseup(function(){
				if(! $(this).is('.fg-button-toggleable, .fg-buttonset-single .fg-button,  .fg-buttonset-multi .fg-button') ){
					$(this).removeClass("ui-state-active");
				}
			});
			
		});
	</script>
</head>
<body>
	<div class="fg-toolbar ui-widget-header ui-corner-all ui-helper-clearfix">
		<div class="fg-buttonset ui-helper-clearfix" >
			<a href="#" class="fg-button ui-state-default fg-button-icon-solo ui-corner-all" title="New" id="new_record"><span class="ui-icon ui-icon-folder-open"></span> Open</a>
			<a href="#" class="fg-button ui-state-default fg-button-icon-solo  ui-corner-all" title="Delete" id="delete_record"><span class="ui-icon ui-icon-trash"></span> Delete</a>
		</div>
		<div class="fg-buttonset ui-helper-clearfix">
			<a href="#" class="fg-button ui-state-default fg-button-icon-solo  ui-corner-all" title="Print"><span class="ui-icon ui-icon-print"></span> Print</a>
			<a href="#" class="fg-button ui-state-default fg-button-icon-solo  ui-corner-all" title="Email"><span class="ui-icon ui-icon-mail-closed"></span> Email</a>
		</div>
	</div>
	<p></p>
	<%@ include file="/common/gridview.jsp" %>
	<input type="hidden" id="grid_id" value="${gridId}">
</body>
</html>
