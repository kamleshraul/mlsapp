<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>memberstatistics</title>

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">

      function drawChart() {
     	  
    	  var text = [];
    	  
    	  $("#deviceCountOfMember option").each(function(){
    		  text.push([$(this).text(), $(this).val()]);
    	  });
    	 	  
    	  alert(text);
    	  
          var data = google.visualization.arrayToDataTable(text);

          var options = {
            title: 'My Devices Count'
          };

          var chart = new google.visualization.PieChart($('#chart_div'));
          chart.draw(data, options);
        }
	
		$(document).ready(function(){
			alert($("#deviceCountOfMember option:selected").text());
			google.load("visualization", "1", {packages:["corechart"]});
			google.setOnLoadCallback(drawChart);
			$("#deviceCountOfMember option").each(function(){
				alert($(this).text());
			});
		});
 	</script>
 	
</head>
<body>
<div id="dataDiv">
	<select id="deviceCountOfMember">
		<c:forEach items="${deviceCount}" var="i">
			<option value="${i.value}">${i.name}</option>
		</c:forEach>
	</select>
	<br />
	<input type="button" id="show" value="Show" />
</div>
<div id="chart_div" style="width: 900px; height: 500px;"></div>
</body>
</html>