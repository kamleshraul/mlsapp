<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="discussionmotion.statusreport" text="Status Report"/>
	</title>
	<link rel="stylesheet" type="text/css" media="print" href="./resources/css/printerfriendly.css?v=31" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script type="text/javascript">
		var ids, counter, limit, dataSize;
		$(document).ready(function(){
			
			if($("#dmIDs").val().length==0){
				//load the remark report
				showRemarkReport();
				
			}else{
				if($("#reportType").val()=='multiple'){
					if($("#dmIDs").val().length>0){
						counter = 0;
						ids = $("#dmIDs").val().split(",");
						dataSize = ids.length;
						limit = ids.length;
						console.log("line 24");
						addRemarkReport();
					}
				}
			}
			
			$("#loadMore").click(function(){
				if(limit==dataSize){
					$.prompt($("#noMorePages").val());
				}
				
				if($("#dmIDs").val().length==0){
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
				device = $("#selectedDiscussionMotionType").val();
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
			console.log("line 92")
			if(ids.length > 0 && counter < ids.length){
				var reportQuery = $("#device").val().toUpperCase()+ "_CURRENT_STATUS_REPORT";
				var houseType = $("#selectedHouseType").val();
				if(houseType=='lowerhouse'){
					reportQuery+="_LOWERHOUSE";
				}else if(houseType=='upperhouse'){
					reportQuery+="_UPPERHOUSE";
				} else { //case when in workflow page.. need to find type from name of housetype
					houseType = $("#houseTypeMaster option[value='"+$("#selectedHouseType").val()+"']").text();
					if(houseType=='lowerhouse'){
						reportQuery+="_LOWERHOUSE";
					}else{
						reportQuery+="_UPPERHOUSE";
					}
				}
				
				/****************************/
				console.log('discussionmotion/report/'+ ids[counter] + '/currentstatusreportvm?device='+$("#device").val()+'&reportOut='+reportQuery)
				/****************************/
				
				$.get('discussionmotion/report/'+ ids[counter] + '/currentstatusreportvm?device='+$("#device").val()+'&reportOut='+reportQuery,function(data1){
						 								
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
	        	margin-left:5%;
	        	font-family:Mangal !important;
	        	font-size: 16px !important;
	        }	 
	        
	        td,th{
				font-family:Mangal !important;
	        	font-size: 16px !important;
			}       
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
	        	font-size: 18px !important;
	         }
	        
	        td,th{
				font-family:Mangal !important;
	        	font-size: 18px !important;
			} 
	        
	        .page-break-before-forced{
	        	page-break-before: always;
	        }     
	        
	        @page{
	        	size: 210mm 297mm !important;   /* auto is the initial value */
  				margin: 0px 0px 0px 60px !important;
	        }  
	        
	        hr{
	        	display: none !important;
	        }   
	        
	        span{
	        	display: block !important;
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
		<div id="reportWindow" style="size: 750px;">
			<div id="reportWindow1" style="word-wrap: break-word;">
				v
			</div>
		</div>
	</div>
</div>
<div id="loadMore">
	<b>&#9660;</b>
</div>
<input type="hidden" id="noMorePages" value="<spring:message code='client.message.no_more_pages' text='No more pages.' />"/>
<input type="hidden" id="device" value="${device}" /> 
<input type="hidden" id="reportType" value="${reportType}" />
<input type="hidden" id="dmIDs" value="${dmId}" />
<%-- <input type="hidden" id="locationOfPrint" value="${location}" /> --%>
</body>
</html>