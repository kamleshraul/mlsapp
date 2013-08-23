<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title><spring:message code="menu.list" text="Menu Hierarchy"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" media="screen" href="./resources/css/jquery.treeview.css" type="text/css" />
	<script type="text/javascript" src="./resources/js/jquery.treeview.min.js"></script>
	<script type="text/javascript">
	 	$(document).ready(function(){
	 	 $("#treeview").treeview(); 
		 
		 $('#expandAll').click(function() {        
		     $('#treeview a:eq(1)').click();
		     return false;    
		 });
		    
	     $('#collapseAll').click(function() {
	        $('#treeview a:eq(0)').click();
	        return false;        
	     });

	     $('.node_click').click(function(){
	    	 $('.selected').removeClass('selected');
	    	 $(this).addClass('selected');
			 $("#disp").load(this.href,function(data){
				 var title = $(data).filter('title').text();
				 $('#content > .subHeader > div').html(title);
			 });
			 return false;
		 });
	     $('#refresh').click(function(){
	    	 showTabByIdAndUrl('menu_tab','menu/list');//$("#menu_tab").Load('menus/list');
	    	 return false;
	 	 });
	     $('#new_record').click(function(){
		     var select_node = $('.selected').attr('id');
		   //  alert(select_node)
		     if(select_node){
		    	$("#disp").load('menu/new?parentId='+select_node,function(data){
	    	 	});
		     }
		     else{
			    $.prompt("Please select the node under which you would like to create a new menu item and then click New"); 
		     }
	    	 return false;
	 	 });
	     $('#delete_record').click(function(){
		     var select_node = $('.selected').attr('id');
		     if(select_node){
		    	 $.ajax({
		    		   url: 'menu/'+select_node+'/delete',
		    		   type: 'DELETE',
		    		   success: function( response ) {
			    			$('#refresh').trigger('click');
			    			$('#disp').empty();
			    	   }
		    	}).fail(function(){
					if($("#ErrorMsg").val()!=''){
						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
					}else{
						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
					}
					scrollTop();
				});

		     	}
	    		 return false;
	 		 });
	 	 
	 	});
	 	</script>
</head>
<body>
	<p id="error_p" style="display: none;">&nbsp;</p>
	<c:if test="${(error!='') && (error!=null)}">
		<h4 style="color: #FF0000;">${error}</h4>
	</c:if>

	 <div style="width: 935px;" id="treeView1" >
	 
		<div class="commandbar" >
			<div class="commandbarContent"  >
				<a href="#" id="new_record" class="butSim"><spring:message code="generic.new" text="New"/></a>  |
				<a href="#" id="delete_record" class="butSim"><spring:message code="generic.delete" text="Delete"/></a> |
				<a href="#" id="refresh" class="butSim"><spring:message code="generic.refresh" text="refresh"/></a> |
			</div>
			<p>&nbsp;</p>
		</div>
	<div id="treeviewdiv"style="width:220px;height: 554px;border:solid;border-width:1px;float:left;overflow:auto;">
		<c:set var="menu_xsl">
	 	 <?xml version="1.0"?>
	  		<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	   		 <xsl:output indent="yes"/>
	   		 <xsl:template match="/root">
	       		 <ul id="treeview" class="treeview-black" style="overflow:auto;">
	           		 <xsl:apply-templates select="menu[not(@parent)]"/>
	        	</ul>
	  		  </xsl:template>
	  	
	    	<xsl:template match="menu">
	        <li>
	            <span><a id="{@id}" href="menu/{@id}/edit" class="node_click"><xsl:value-of select="@text"/></a></span>
	            <xsl:if test="count(../menu[@parent=current()/@id])>0">
	                <ul style="overflow:auto;">
	                    <xsl:apply-templates select="../menu[@parent=current()/@id]"/>
	                </ul>
	            </xsl:if>
	         </li>
	    </xsl:template>
		</xsl:stylesheet>
		</c:set>
		
		<x:transform xml="${menu_xml}" xslt="${menu_xsl}">
    	</x:transform>
	</div> 
  	<div id="disp" style="width:700px;height: 554px;border:solid;border-width:1px;float:right;overflow:auto;">
 	</div>    
 </div>
<input type="hidden" id="ErrorMsg" value="<spring:message code='generic.error' text='Error Occured Contact For Support.'/>"/>
 </body>
</html>
