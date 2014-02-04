<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
	<title>
	<spring:message code="bill.citationReport" text="Citation Report"/>
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	
	<style type="text/css">
		div.fixed-width-wysiwyg {
			min-width: 675px !important;
			left: 60px !important;
			margin-top: 10px;
		}
		
		#citation_editable-wysiwyg-iframe {height: 292px;}
	</style>
	
	<script type="text/javascript">
	function viewBillDetail(id){
		$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });	
		var parameters="houseType="+$("#selectedHouseType").val()
		+"&sessionYear="+$("#selectedSessionYear").val()
		+"&sessionType="+$("#selectedSessionType").val()
		+"&deviceType="+$("#typeOfSelectedDeviceType").val()
		+"&ugparam="+$("#ugparam").val()
		+"&status="+$("#selectedStatus").val()
		+"&role="+$("#srole").val()
		+"&usergroup="+$("#currentusergroup").val()
		+"&usergroupType="+$("#currentusergroupType").val()
		+"&edit=false";
		var resourceURL='bill/'+id+'/edit?'+parameters;
		$.get(resourceURL,function(data){
			$.unblockUI();
			$.fancybox.open(data,{autoSize:false,width:800,height:700});
		},'html');	
	}
	
	$('document').ready(function(){		
		$('#citation_editable').wysiwyg({
			resizeOptions: {maxWidth: 600},
			controls:{	
				fullscreen: {
					visible: true,
					hotkey:{
						"ctrl":1|0,
						"key":122
					},
					exec: function () {
						if ($.wysiwyg.fullscreen) {
							$.wysiwyg.fullscreen.init(this);
						}
					},
					tooltip: "Fullscreen"
				},
				strikeThrough: { visible: true },
				underline: { visible: true },
				subscript: { visible: true },
				superscript: { visible: true },
				insertOrderedList  : { visible : true},
				increaseFontSize:{visible:true},
				decreaseFontSize:{visible:true},
				highlight: {visible:true}			
			},
			plugins: {
				autoload: true,
				i18n: { lang: "mr" }
			}
		});		
		$('#citation_editable_para').children().filter('div.wysiwyg').addClass('fixed-width-wysiwyg');
		
		var text="<option value='' selected='selected'>----"+$("#pleaseSelectMsg").val()+"----</option>";		
		
		if($('#status').val()==undefined || $('#status').val()=='') {			
			$('#status').html(text);			
		} else {
			$('#status').prepend(text);	
		}
		
		if($('#selectedBillNumber').val()!=undefined && $('#selectedBillNumber').val()!='') {
			$('#viewBillDetails').show();			
		} else {
			$('#viewBillDetails').hide();			
		}
		
		$('#viewBillDetails').click(function() {			
			if($('#selectedBillNumber').val()!='') {
				if($('#deviceId').val()!='' && $('#deviceId').val()!=undefined) {
					viewBillDetail($('#deviceId').val());
				} else {
					$.get('ref/findIdOfBillWithGivenNumberYearAndHouseType?billNumber='+$("#selectedBillNumber").val()
							+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val(), function(data) {
						if(data!='' && data!=undefined) {
							viewBillDetail(data);
						}
					});
				}			
			} else {
				alert($("#emptyBillNumberMsg").val());
				return false;
			}
		});		
		
		$('#selectedBillNumber').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/citation_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
					$("#status").empty();
					var statusText=text;
					if(data.length!=undefined) {
						for(var i=0;i<data.length;i++){
							statusText+="<option value='"+data[i].value+"'>"+data[i].name;
						}
						$("#status").html(statusText);
						$('#deviceId').val(data[0].id);
						$('#viewBillDetails').show();
					} else {
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#citationDiv').empty();
			$('#citationDiv').hide();	
			$('#citationPrintIcon_para').hide();			
			$('#citation_editable_para').hide();
		});
		
		$('#houseType').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/citation_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
					$("#status").empty();
					var statusText=text;
					if(data.length!=undefined) {
						for(var i=0;i<data.length;i++){
							statusText+="<option value='"+data[i].value+"'>"+data[i].name;
						}
						$("#status").html(statusText);
						$('#deviceId').val(data[0].id);
						$('#viewBillDetails').show();
					} else {
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#citationDiv').empty();
			$('#citationDiv').hide();	
			$('#citationPrintIcon_para').hide();			
			$('#citation_editable_para').hide();
		});
		
		$('#selectedYear').change(function() {				
			if($('#selectedBillNumber').val()!='') {
				$.get('ref/bill/citation_statuses?billNumber='+$("#selectedBillNumber").val()
						+'&billYear='+$("#selectedYear").val()+'&houseTypeId='+$("#houseType").val()
						+'&currentHouseTypeType='+$("#currentHouseTypeType").val(), function(data) {
					$("#status").empty();
					var statusText=text;
					if(data.length!=undefined) {
						for(var i=0;i<data.length;i++){
							statusText+="<option value='"+data[i].value+"'>"+data[i].name;
						}
						$("#status").html(statusText);
						$('#deviceId').val(data[0].id);
						$('#viewBillDetails').show();
					} else {
						alert($('#billNotFoundMsg').val());
						$('#status').html(text);
					}					
				});
			} else {
				$('#status').empty();				
				$('#status').html(text);
				$('#viewBillDetails').hide();
			}			
			$('#citationDiv').empty();
			$('#citationDiv').hide();	
			$('#citationPrintIcon_para').hide();			
			$('#citation_editable_para').hide();
		});
		
		$('#status').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
			}
		});
		
		$('#status').change(function() {
			$('#citationDiv').empty();
			$('#citationDiv').hide();	
			$('#citationPrintIcon_para').hide();
			$('#citation_editable_para').hide();
		});
		
		$('#generateCitationReportButton').click(function(){
			if($('#selectedBillNumber').val()=='') {
				alert($("#emptyBillNumberMsg").val());
				return false;
			} else if($('#status').val()=='') {
				alert($("#emptyStatusMsg").val());
				return false;
			}
			if($('#citationDiv').is(':hidden')) {
				$.get('bill/generateCitationReport?deviceId='+$("#deviceId").val()
						+'&status='+$("#status").val()+'&statusDate='+$("#statusDate").val(), function(data) {				
					$('#citationDiv').html(data);					
					$('#citationDiv').show();	
					$('#citationPrintIcon_para').show();
					$('#citation_editable').wysiwyg("setContent", $('#citationDiv').html());					
				},'html');
			} else {
				return false;
			}			
		});	
		
		$('#citationPrintIcon').click(function() {		
			if(!$('#citation_editable_para').is(':hidden')) {
				$('#citationDiv').html($('#citation_editable').val());
			}
			var printContents = new $("#citationDiv").clone();			
			var myWindow = window.open("", "popup", "width=650,height=300,scrollbars=yes,resizable=no,titlebar=no," +
			"toolbar=no,directories=no,location=no,menubar=no,status=no,left=0,top=0");
			var doc = myWindow.document;
			//doc.open();
			//done in case your print button is in the same div as your content to be printed
			//$(printContents).find("#PrintNews").remove(); 
			console.log($(printContents).html());
			doc.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			doc.write("<html>");
			doc.write("<head>");
			doc.write("<link href='./resources/css/printCitationReport.css' rel='stylesheet' type='text/css' media='all' />"); // your css file comes here.
			doc.write("</head>");
			doc.write("<body><div class='printDiv'>");
			doc.write($(printContents).html());
			doc.write("</div></body>");
			doc.write("</html>");			
			myWindow.focus();			
			myWindow.print();
			//if you want you can close document after print command itself 
			myWindow.close();
		});
		
		$('#editCitationReportIcon').click(function() {	
			if($('#citation_editable_para').is(':hidden')) {
				$('#citationDiv').hide();
				$('#citation_editable_para').show();
				$('#editCitationReportIcon').find("img").attr("title", "View Citation Report");				
			} else {					
				$('#citation_editable_para').hide();
				$('#citationDiv').html($('#citation_editable').val());
				$('#citationDiv').show();
				$('#editCitationReportIcon').find("img").attr("title", "Edit Citation Report");
			}		
		});
	});		
	</script>
</head>
<body>
<div class="fields clearfix vidhanmandalImg">
	<div id="non-printable">
	<h2><spring:message code="bill.citationReport" text="Citation Report"/></h2>
	<p>
		<label class="small"><spring:message code="bill.citationReport.houseType" text="House Type"/></label>
		<select id="houseType" class="sSelect">
			<c:forEach items="${houseTypes}" var="houseType">
				<c:choose>
					<c:when test="${houseType.getId()==selectedHouseType.getId()}">
						<option value="${houseType.getId()}" selected="selected">${houseType.getName()}</option>
					</c:when>
					<c:otherwise>
						<option value="${houseType.getId()}">${houseType.getName()}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>		
		<input type="hidden" id="currentHouseTypeType" name="houseType" value="${currentHouseType.getType()}"/>		
	</p>
	<p>
		<label class="small"><spring:message code="bill.year" text="Year"/></label>
		<input id="selectedYear" name="selectedYear" value="${formattedSelectedYear}" class="sInteger"/>
		<%-- <input type="hidden" id="selectedYear" name="selectedYear" value="${selectedYear}"/> --%>	
	</p>
	<p>
		<label class="small"><spring:message code="bill.billNumber" text="Bill Number"/></label>
		<input id="selectedBillNumber" name="selectedBillNumber" value="${selectedBillNumber}" class="sText"/>
		<a href="#" id="viewBillDetails"><spring:message code="bill.viewBillDetails" text="View Bill"/></a>
		<input type="hidden" id="deviceId" name="deviceId" value="${selectedBillId}"/>
	</p>
	<p>
		<label class="small"><spring:message code="bill.status" text="Status"/></label>
		<select id="status" class="sSelect" name="status"> 
			<c:forEach var="i" items="${citationStatuses}">
				<option value="${i.type}">${i.name}</option>
			</c:forEach>
		</select>
	</p>
	<p>
		<label class="small"><spring:message code="bill.statusDate" text="Status Date"/></label>
		<input type="text" id="statusDate" name="statusDate" value="${currentDate}" class="datemask sText"/>
	</p>
	<p align="right">
		<input id="generateCitationReportButton" type="button" value="<spring:message code='bill.generateCitationReportButton' text='Generate Citation Report'/>" class="butDef">
	</p>
	<p id="citationPrintIcon_para" style="display: none;margin-left: 60px;">
		<a href="#" id="citationPrintIcon" style="text-decoration: none;margin-right: 20px;">
			<img src="./resources/images/print_icon.gif" title="<spring:message code='bill.printCitationReport' text='Print Citation Report'></spring:message>" style="width: 32px; height: 32px;" />
		</a>
		<a href="#" id="editCitationReportIcon" style="text-decoration: none;margin-right: 20px;">
			<img src="./resources/images/Revise.jpg" title="<spring:message code='bill.editCitationReport' text='Edit Citation Report'></spring:message>" style="width: 18px; height: 18px; margin-bottom: 4px;" />
		</a>
	</p>
	<p id="citation_editable_para" style="display: none;margin-top: 10px;">
		<textarea class="wysiwyg" id="citation_editable"></textarea>
	</p>
	</div>	
	<div id="citationDiv" style="display:none; width: 650px; height: 300px; margin: 0px 60px 0px 60px; padding: 10px; overflow: auto; border: 1px solid black; box-shadow: 2px 2px 2px grey;">
	</div>		
	<input type="hidden" id="operation" name="operation"/>
	<input type="hidden" id="usergroupForCitation" name="usergroup"/>	
	<input id="pleaseSelectMsg" value="<spring:message code='please.select' text='Please Select'/>" type="hidden">
	<input id="emptyBillNumberMsg" value="<spring:message code='bill.emptyBillNumberMsg' text='Please Enter Bill Number'/>" type="hidden">
	<input id="emptyStatusMsg" value="<spring:message code='bill.emptyStatusMsg' text='Please Enter Status'/>" type="hidden">
	<input id="billNotFoundMsg" value="<spring:message code='bill.billNotFoundMsg' text='Bill not found.'/>" type="hidden">
</div>	
</body>
</html>