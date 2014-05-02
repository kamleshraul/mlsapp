<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="question.statusreport" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
			
			//load the remark report
			showRemarkReport();
			
			$("#loadMore").click(function(){
				
				if(counter == limit){
					limit += 10;
				}
				
				if(limit > dataSize){
					limit = dataSize;
				}				
				addRemarkReport();
			});
		});
		
		function showRemarkReport(){
			var paramVar = '?deviceType='+$("#selectedQuestionType").val()+
			'&sessionYear='+$("#selectedSessionYear").val()+
			'&sessionType='+$("#selectedSessionType").val()+
			'&houseType='+$("#selectedHouseType").val()+
			'&status='+$("#selectedStatus").val()+
			'&group='+$("#selectedGroup").val()+
			'&subdepartment='+$("#selectedSubDepartment").val()+
			'&device='+$("#device").val()+
			'&grid=device';

			$.get('ref/pendingtasksdevices'+paramVar,function(data){
			
				if(data){					
					ids = new Array();
					if(data.length > 0){
						dataSize = data.length;
						for(var i = 0; i < data.length; i++){
							ids.push(data[i].value);
						}
						counter = 0;
						limit = 10;
						if(ids.length > 0){
							addRemarkReport();
						}
					}else{
						$('#reportWindow1').html("<h3>No data found</h3>");
					}
				}else{
					$('#reportWindow1').html("<h3>No data found</h3>");
				}
			});	
		}
		
		function addRemarkReport(){
			if(ids.length > 0 && counter < ids.length){
				 $.get('question/report/'+ ids[counter] + '/currentstatusreportvm?device='+$("#device").val()+'&grid=device',function(data1){
						 								
							if($('#reportWindow1').text().trim()=='v'){
								$('#reportWindow1').empty();
								$('#reportWindow1').html(data1);
							}else{
								$('#reportWindow1').append(data1);
							}
				 }).fail(function(){				 
				 });
				 
				 counter++;
				 
				 if(counter < limit){
				 	setTimeout(addRemarkReport, 200);
				 }
			}
		}
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
	        	margin-top: 0px !important;
	        }
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 21cm 29.7cm;   /* auto is the initial value */
  				margin: 20mm;
	        }  
	        
	        hr{
	        	display: none !important;
	        }    
        }
        
        pre{
        	width: 100% !important;
        	background: #FFFFFF !important;
        	border: none !important;
        	background: none !important;
        	text-align: justify;
        }
        
        #loadMore{
        	background: #00FF00 scroll no-repeat;
			max-width: 100px;
			width: 50px;
			max-height: 15px;
			/*border-radius: 10px;*/
			text-align: center;
			border: 1px solid black;
			z-index: 5000;
			bottom: 5px;
			right: 50px;			
			position: fixed;
			cursor: pointer;
        }
    </style>
</head> 

<body>

<div id="reportDiv">
	<div id="statusReportDiv">				
		<div id="reportWindow" style="size: 600px;">
			<div id="reportWindow1" style="word-wrap: break-word;">
				v
			</div>
		</div>
	</div>
</div>
<div id="loadMore">
	<b>&#9660;</b>
</div>
<input type="hidden" id="device" value="${device}" /> 
</body>
</html>