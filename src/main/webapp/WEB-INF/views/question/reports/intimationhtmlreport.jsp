<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
		<spring:message code="question.statusreport" text="Intimation html Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
			
		 	if($("#qIDs").val().length==0){
				//load the remark report
				showRemarkReport();
			}else{
				
				if($("#reportType").val()=='multiple'){
					if($("#qIDs").val().length>0){
						counter = 0;
						ids = $("#qIDs").val().split(",");
						dataSize = ids.length;
						limit = ids.length;
						
						addRemarkReport();
					}
				}
			}
			
		 		$("#loadMore").click(function(){
				if(limit==dataSize){
					//$.prompt($("#noMorePages").val());
				}
				
				if($("#qIDs").val().length==0){
					if(counter == limit){
						limit += 10;
					}
					
					if(limit > dataSize){
						limit = dataSize;
					}				
					addRemarkReport();
				}
			}); 
		});		
		
		function showRemarkReport(){
			var strLocation = (($("#selectedDeviceType").val()==undefined)?"device":"workflow"); 
			var device = "";
			if(strLocation=='workflow'){
				device = $("#selectedDeviceType").val();
			}else if(strLocation=='device'){
				device = $("#selectedQuestionType").val();
			}
				
			var paramVar = '?deviceType='+device+
			'&sessionYear='+$("#selectedSessionYear").val()+
			'&sessionType='+$("#selectedSessionType").val()+
			'&houseType='+$("#selectedHouseType").val()+
			'&status='+$("#selectedStatus").val()+
			'&wfSubType='+$("#selectedSubWorkflow").val()+
			'&subdepartment='+$("#selectedSubDepartment").val()+ 
			'&grid='+ strLocation + 
			'&group='+(($("#selectedGroup").val()=='')?'0':$("#selectedGroup").val())+
			'&answeringDate='+(($("#selectedAnsweringDate").val()=='')?'0':$("#selectedAnsweringDate").val());

			/*  $.get('ref/pendingtasksdevices'+paramVar,function(data){
			
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
			});	  */
		}
		
		function addRemarkReport(){
			if(ids.length > 0 && counter < ids.length){
				
				 $.get('question/report/getIntimationLetterInHtmlFormat?questionId='+ids[counter]+'&intimationLetterFilter='+$("#intimationLetterFilter").val(),function(data1){
						 							
							if($('#reportWindow1').text().trim()=='v'){
								$('#reportWindow1').empty();
								$('#reportWindow1').html(data1);
							}else{
								$('#reportWindow1').append(data1);
							}
				 }).fail(function(){	
					 console.log("failed")
				 });
				 
				 counter++;
				 
				 if(counter < limit){
				 	setTimeout(addRemarkReport, 200);
				 }
			}
		}
	</script>
	<style>
	  #reportDiv{
	        	border: 1px solid;
	        	width: 850px;
	        	padding: 10px;
	        	font-family:Mangal !important;
	        	font-size: 16px !important;
	        	margin: auto;
	        	
	        }	
	        
	         @media print{
        	#reportWindow{
        		font-family:Mangal !important;
	        	font-size: 18px !important;
        	}
	        #reportDiv{
	        	width: 750px;
	        	padding: 5px;
	        	
	        	margin-top: 0px !important;
	        	font-family:Mangal !important;
	        	font-size: 10px ;
	        	border: 0px solid;
	         }
	          .page-break-before-forced{
	        	page-break-before: always;
	        } 
	        
	          
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 0px 0px 0px 60px !important;
	        } 
	        }
	</style>
	
</head>
<body>


<div id="reportDiv">
	<div id="statusReportDiv">				
		<div id="reportWindow" style="size: 750px;">
			<div id="reportWindow1" style="word-wrap: break-word;">
				v
			</div>
		</div>
	</div>
</div>








<input type="hidden" id="noMorePages" value="<spring:message code='client.message.no_more_pages' text='No more pages.' />"/>
<input type="hidden" id="device" value="${device}" /> 
<input type="hidden" id="reportType" value="${ReportType}" />
<input type="hidden" id="intimationLetterFilter" value="${intimationLetterFilter}" />
<input type="hidden" id="qIDs" value="${qId}" />
</body>
</html>