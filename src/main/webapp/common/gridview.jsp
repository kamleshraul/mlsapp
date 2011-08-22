<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<link rel="stylesheet" media="screen" href="./resources/css/ui.jqgrid.css" type="text/css" />
	<link rel="stylesheet" media="screen" href="./resources/css/aristo/jquery-ui-1.8.7.custom.css" type="text/css" />
	<script type="text/javascript" src="./resources/js/i18n/grid.locale-en.js"></script>
	<script type="text/javascript" src="./resources/js/jquery.jqGrid.min.js"></script>
	<script type="text/javascript" src="./resources/js/common.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			var gridId = $('#grid_id').val();
		    loadGrid(gridId);
		    myLayout.resizeAll();
		});
	</script>
</head>
<body>
<div id="grid_container">
	<table id="grid"></table> 
	<div id="grid_pager"></div>
</div>
</body>
</html>
