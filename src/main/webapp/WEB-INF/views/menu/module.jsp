<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="menu.list" /></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		 $(document).ready(function(){
			 var urlPattern=$('#urlPattern').val();
			 	 showTabByIdAndUrl('menu_tab','menu/list');			
		 });
	
	</script>	
</head>
<body>
	<!-- .section -->
	<div class="clearfix tabbar" >
		<ul class="tabs">
			<li class="tab1">
				<a id="menu_tab" class="selected tab" href="#">
					Menus
				</a>
			</li>
			
		</ul>
	<input type="hidden" id="urlPattern" name="urlPattern" value="${urlPattern}">	
	</div> 
	<div class="tabContent clearfix">
	</div>
		
</body>
</html>