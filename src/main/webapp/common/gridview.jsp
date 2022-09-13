<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$(document).ready(function(){
			var gridId = $('#grid_id').val();
			var grid=null;
			//Here if we want to load data in the grid using controller
			//other than grid controller then we need to provide a hidden parameter having id as 
			//gridURL in list.jsp
			if($('#gridURL')!=undefined){
				grid = loadGrid(gridId,$('#gridURL').val());				
			}else{
				//console.log(gridId);
			    grid = loadGrid(gridId);				
			}
		});
	</script>
</head>
<body>
<input type="hidden" id="refresh" value="<%=session.getAttribute("refresh") %>">
<div id="grid_container">
	<table id="grid"></table> 
	<div id="grid_pager"></div>
</div>
</body>
</html>
