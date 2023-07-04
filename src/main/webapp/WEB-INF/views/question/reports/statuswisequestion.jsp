<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.statreport" text="Statistics Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=3" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		$(document).ready(function(){
			
		});
	</script>
	 <style type="text/css">
        @media screen{
	        #reportDiv{
	        	border: 1px solid;
	        	width: 800px;
	        	padding: 10px;
	        }	        
        }
        @media print{
	        #reportDiv{
	        	width: 800px;
	        	padding: 10px;
	        }	        
	        
	        table{
	        	border: 1px solid black;
	        	width: 100%;
	        }
	        
	        @page {
			  size: auto;
			  margin: 20px 20px 20px 30px;
			}
			
			div#statsReportDiv{
				margin-left: 120px;
			}
			
			div#statHeaderDiv{
				margin-left: 110px;
			}
			
			div#typistDiv{
				margin: 50px 10px 10px 100px;
				width: 850px;				
			}
			
			div#typistDiv h2{
				width: 100%;
				text-align: center;
			}
			
			div#typistDiv table thead {display: table-header-group; margin-top: 50px;}
			
			div#rep8NoData{
				display: none;
			}
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        table#reportTable th{
        	text-align: center;
        	font-weight: bold;
        	font-size: 14px;
        }
        
        table#reportTable td{
        	text-align: center;
        	font-weight: bold;
        	font-size: 13px;
        	height: 50px;
        }
        
        table#reportTypistTable th{
        	text-align: center;
        }
        
        table#reportTypistTable td{
        	font-size: 13px;
        }
        
        table#reportTypistTable .center{
        	text-align: center;
        }
        
        table#reportTable .finalTotal{
        	height: 20px !important;
        }        
        
    </style>
</head> 

<body>
<p id="error_p" style="display: none;">&nbsp;</p>
<div id="reportDiv">
	 <div id="statHeaderDiv">
		<h2 align="center" style="color: black !important; margin-left: 50px;">
		 <spring:message code="question.statuswiseQuestionCount" text="Status Wise Question Count Report"/>		
		</h2>
		
		<br />	
	</div> 
	<div id="statsReportDiv">
		<table border="1" id="reportTable" class="strippedTable">
			<thead>
				<tr>
					<th style="width: 82px; padding: 2px;"><spring:message code="question.currentStatus" text="Column 1"/></th>
					 <c:forEach var="number" items="${AllowedGroupsText}">
					  <th style="width: 82px; padding: 2px;"><spring:message code="group.number" text="group Number"/> :-  ${number}</th>  
					</c:forEach>
					
					 
					
				</tr>
			</thead>
			<tbody>
			
				 <c:forEach var="entry" items="${report}">
				    <tr>
				      <td>${entry.key}</td>
				   
				        <c:forEach var="value" items="${entry.value}">
				          <td>${value}</td>
				        </c:forEach>
				     
				    </tr>
				 </c:forEach>
			
			
				 
				
			</tbody>
		</table>		
	</div>
	
	
	
	
</div>

<input type="hidden" id="device" value="${device}" />
<input type="hidden" id="qId" value="${id}" /> 
</body>
</html>