<%@ include file="/common/taglibs.jsp" %>

<html>
<head>
	<title>
		New Committee
	</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>	
	<script type="text/javascript">
	
	function split( val ) {
		return val.split( /,\s*/ );
	}	

	function extractLast( term ) {
		var temp = split( term ).pop();
		
		return temp;
	}	
	
	$('document').ready(function(){	
		
		var totalInvitedMember = $('#totalInvitedMembers').val();
		var totalRows = $('#totalRows').val();
		

		$( ".customDatePicker" ).datepicker(
				{
				      changeMonth: true,
				      changeYear: true,
				      dateFormat: 'yy-mm-dd'
				    }		
		);
		
		
		
		$( "#tbl_posts_body" ).sortable();
		
		$('#tbl_posts_body').bind('sortupdate',function(event, ui){
			e.preventDefault();
			var serialNo =1;
			var invitedMember = 0;
			//console.log($(this));
		    $(this).children().each(function(index) {
		      
		    	var el = $(this).find('td').eq(2).get(0);
		    	var firstChild = $(el.firstElementChild);
		    	var selectId = firstChild.attr('id');
		    	
		    	var val =$('#'+selectId).val();
		    	console.log(val + "")
		        //var el1  = el.firstElementChild.attr('id')
		       
		    	if(val != 52 ){
		    		$(this).find('td').first().html('<span class="sn">'+(serialNo)+'</span>.')
		    		serialNo++;
		    	}else{
		    		$(this).find('td').first().html('<span class="sn"> - </span>.')
		    		invitedMember++;
		    	}
		
					
		    });
		    totalInvitedMember = invitedMember;
		});
		
		$('#addRow').click(function(){
			var size = $('#tbl_posts >tbody >tr').length + 1 ;
			var numbering = size  - totalInvitedMember
			var content = $('#sample_tbl tr');
		
		    var element = null;  
			 element = content.clone();
			 element.attr('id', 'rec-'+size);
			 element.find('#committeeDesignation').attr('id','committeeDesignation'+size);
			 element.find('#selectedSupportingMembers').attr('id','selectedSupportingMembers'+size)
		     element.find('.delete-record').attr('data-id', size);
		     element.appendTo('#tbl_posts_body');
		     element.find('.sn').html(numbering); 
		     totalRows++;
		    // loadPriority(size)
			// console.log(element)
			 
			/*  $("#tbl_posts_body").append(
			'<tr id="rec-'+size+'"> <td><span class="sn">'+size+'</span>.</td> <td><textarea id="selectedSupportingMembers'+size+'"  class="autosuggestmultiple" rows="2" cols="25"></textarea></td> <td> <select name="country"> <option value="0">committee Invited Member</option> <option value="1">committee Member</option> <option value="1"> comittee Chairman </option> </select> </td>   <td> <a class="delete-record" data-id="'+size+'" > <img src="./resources/images/cancel.png" style="display:inline-block" title="Delete" width="30px" height="30px" align="justify"> <br>  Delete </a> </td> </tr>'		 
			 )
			 loadPriority(size) */
		})
		
		$('.delete-record').bind('click',function (e) {
			
			e.preventDefault(); // to Stop from Event Bubbling Up 
			//console.log("on Second");
			$.prompt("Are you sure You want to delete",{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	
		        	var id =  e.target.getAttribute("data-id")		        	
				    var targetDiv =e.target.getAttribute("targetDiv") ;
		        					    
		        	if(totalRows > 1){
		        		$('#rec-' + id).remove();
		        		totalRows--;
		        		//regnerate index number on table
					    $('#tbl_posts_body tr').each(function(index){
							$(this).find('span.sn').html(index+1);
					    });
					    return true;
		        		}
    	            }else{    	            
    	            	//console.log("Hi")
    	            	return false;
    	            }
			}});			
	          
		})
		
		/* $('body').delegate( '.delete-record','click', function (e) {
			
			e.preventDefault(); // to Stop from Event Bubbling Up 
			//console.log("on Second");
			$.prompt("Are you sure You want to delete",{
				buttons: {Ok:true, Cancel:false}, callback: function(v){
		        if(v){
		        	
		        	var id =  e.target.getAttribute("data-id")		        	
				    var targetDiv =e.target.getAttribute("targetDiv") ;
		        					    
		        	if(totalRows > 1){
		        		$('#rec-' + id).remove();
		        		totalRows--;
		        		//regnerate index number on table
					    $('#tbl_posts_body tr').each(function(index){
							$(this).find('span.sn').html(index+1);
					    });
					    return true;
		        		}
    	            }else{    	            
    	            	//console.log("Hi")
    	            	return false;
    	            }
			}});			
	          
		}) */
		
		$('body').delegate('.designation','change',function(e){
				
				//console.log("Working");			
				//var tableRows = document.querySelectorAll("#tbl_posts_body tr");
				$('#tbl_posts_body tr').each(function(index){
					//console.log(index)
					//console.log(  $(this).find('td').eq(2).get(0));
					var currEl = $(this).find('td').eq(2).get(0);
					var firstChild = $(currEl.firstElementChild);
			    	var selectId = firstChild.attr('id');		    	
			    	var val =$('#'+selectId).val();
			    	
					//var numbering = size  - totalInvitedMember
			    	//console.log(val);
			    	//console.log($(this).get(0));
			    	
			    	$('#tbl_posts_body').trigger('sortupdate');
			    	
				});						
			});
		
		

		$('body').delegate( '.autosuggestmultiple','click', function (e) {
			//console.log(this)
			$( this ).autocomplete({
				minLength:3,
				source: function( request, response ) {
					//var idName=$(".autosuggestmultiple").attr("id");
					//console.log($(this).get(0).term)
					$.getJSON( 'ref/membersbyterm?term='+$(this).get(0).term , {
						
						term: extractLast( request.term )
					}, response ).fail(function(){
						if($("#ErrorMsg").val()!=''){
							$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
						}else{
							$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
						}
						scrollTop();
					});
				},			
				search: function() {
					var term = extractLast( this.value );
					if ( term.length < 2 ) {
						return false;
					}
				},
				focus: function() {
					return false;
				},
				select: function( event, ui ) {
					//console.log("inside")
					//what happens when we are selecting a value from drop down
					var terms = $(this).val().split(",");
					//var controlName=$(".autosuggestmultiple").attr("id");
					  var select = $(this);
					    var controlName = select.attr('id');
					//if select box is already present i.e atleast one option is already added
					if($("select[name='"+controlName+"']").length>0){
						//console.log("inside")
						if($("select[name='"+controlName+"'] option[value='"+ui.item.id+"']").length>0){
						//if option being selected is already present then do nothing
						this.value = $(this).val();	
					
						$("select[name='"+controlName+"']").hide();						
						}else{
						//if option is not present then add it in select box and autocompletebox
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						var text="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";
						$("select[name='"+controlName+"']").append(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
						}							
						$("select[name='"+controlName+"']").hide();								
						}
					}else{
						if(ui.item.id!=undefined&&ui.item.value!=undefined){
						text="<select name='"+$(this).attr("id")+"'  multiple='multiple'>";
						textoption="<option value='"+ui.item.id+"' selected='selected' class='"+ui.item.value+"'></option>";				
						text=text+textoption+"</select>";
						$(this).after(text);
						terms.pop();
						terms.push( ui.item.value );
						terms.push( "" );
						this.value = terms.join( "," );
					
						}	
						$("select[name='"+controlName+"']").hide();									
					}		
					return false;
				}
			});
		})
		
		
		
		$('#submit').click(function(){
			
			// foundationDateInLocale ,dissolutionDateInLocale
			
			/* if($('#foundationDate').val() == ""){
				$.prompt(" Please Enter  "+$('#foundationDateInLocale').val());
			}else */ if($('#formationDate').val() == ""){
				$.prompt(" Please Enter  "+$('#formationDateInLocale').val());
			}else if($('#dissolutionDate').val() == ""){
				$.prompt(" Please Enter  "+$('#dissolutionDateInLocale').val());
			}else{
				var rows = document.querySelectorAll("#tbl_posts_body tr"),
			    data = {}, map , cells;
				
				
				var committeeDetails = new Array();
				var totalRows = rows.length;
				
				//console.log(rows);
				
				for (let r=0; r<rows.length; r++) {
					
					
					cells = rows[r].querySelectorAll("td");
									 
					  committeeDetails.push({'priority':cells[0].firstElementChild.innerHTML
					    	,'memberId':cells[1].lastElementChild.value
					    	,'designation':cells[2].firstElementChild.value
					    	
							})
								
				}
				//console.log(committeeDetails)	
				var totalDataFilledInTable = 0;
				$.each(committeeDetails, function( key, value ) {
				  //console.log('caste: ' + value.caste + ' | id: ' +value.id);
				  if(value.memberId == ""){
					  $.prompt("Please Enter Member in Row " + value.priority)
				  }else{
					  totalDataFilledInTable++
				  }
				})
				
				//console.log(committeeDetails);
				if(totalDataFilledInTable == committeeDetails.length ){
					$.prompt("Do you want create following Committee ?",{
						buttons: {Ok:true, Cancel:false}, callback: function(v){
				        if(v){
				        	
				        $.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				        	
				        	//console.log("inside")
							  $.post('committee/adminCommitteeCreation/edit',
						        	{
								 	committeeNameId:$('#committeeName').val(),
								 	statusId : $('#committeeStatus').val(),
								  	formationDate:$('#formationDate').val() ,
									foundationDate:$('#foundationDate').val() ,
									dissolutionDate:$('#dissolutionDate').val() ,
									committeeDetails:committeeDetails ,
									totalRows:totalRows,
									committeeId : $('#selectedCommitteeId').val()
								 	},
				    	            function(data){
								 		$('#mainDiv').empty()
								 		$("#mainDiv").html(data);	
				    					$.unblockUI();	
				    	            }
				    	            ).fail(function(){
				    					$.unblockUI();
				    					if($("#ErrorMsg").val()!=''){
				    						$("#error_p").html($("#ErrorMsg").val()).css({'color':'red', 'display':'block'});
				    					}else{
				    						$("#error_p").html("Error occured contact for support.").css({'color':'red', 'display':'block'});
				    					}
				    					scrollTop();
				    				}); 
						       
						     
		    	            }else{
		    	            	return false;
		    	            }
					}}); 
				}
				
				  
			}
			
		})
		
	});	
	
	
	</script>
</head>
<body>
<div class="fields clearfix">
<div id="mainDiv">
	<%@ include file="/common/info.jsp" %>

	<c:if test = "${success == 'Added' || success == 'Updated'}">
		<div class="toolTip tpGreen clearfix"  style="height:30px;">
				<p style="font-size: 12px;">
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="memberballotattendance.update_success" text="Data saved successfully."/>
				</p>
				<p></p>
		</div>
	</c:if>
	
	<c:if test = "${error == 'error'}">
		<div class="toolTip tpRed clearfix" id="failedDiv" style="display:none;height:30px;">
				<p style="font-size: 12px;">
					<img src="./resources/images/template/icons/light-bulb-off.png">
					<spring:message code="memberballotattendance.failed" text="Changes cannot be updated.Please refresh and try again."/>
				</p>
				<p></p>
		</div>
	</c:if> 
	
	<h2><spring:message code="generic.new.heading" text="Enter Details"/>
		[<spring:message code="generic.id" text="Id"></spring:message>:&nbsp;${selectedCommitteeId}]
	</h2>
	
	
	
	<p>
		<label class="small"><spring:message code="committee.committeeName" text="Committee Name" />*</label>
		<select id="committeeName" name="committeeName" class="sSelect">
			<c:forEach items="${allCommitteeNames}" var="i">
				<c:choose>
					<c:when test="${i.name eq selectedCommitteeName}">
						<option value="${i.id}" selected="selected"><c:out value="${i.displayName}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.displayName}"></c:out></option>
					</c:otherwise>
				</c:choose>				
			</c:forEach>	
		</select>							
	</p>
	

	<p>
		<label class="small"><spring:message code="generic.status" text="Committee Status" />*</label>
		<select id="committeeStatus" name="committeeStatus" class="sSelect">
			<c:forEach items="${allCommitteeStatus}" var="i">
				<c:choose>
					<c:when test="${i.type eq selectedCommitteStatus.type}">
						<option value="${i.id}" selected="selected"><c:out value="${i.name}"></c:out></option>
					</c:when>
					<c:otherwise>
						<option value="${i.id}"><c:out value="${i.name}"></c:out></option>
					</c:otherwise>
				</c:choose>				
			</c:forEach>	
		</select>							
	</p>
	
	
	<p>
		<label class="small"><spring:message code="committee.foundationDate" text="Foundation Date"/></label>
		<input type="text" id="foundationDate" name="foundationDate"  class=" sText customDatePicker" size="30" value="${selectedCommitteeCreationDate}" />
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.formationDate" text="Formation Date"/>*</label>
		<input type="text" id="formationDate" name="formationDate"  class=" sText customDatePicker"  size="30" value="${selectedCommitteFormationDate}"></p>
		
		<%-- <form:input path="formationDate" cssClass="datemask sText" />
		<form:errors path="formationDate" cssClass="validationError"/>	 --%>
	</p>
	
	<p>
		<label class="small"><spring:message code="committee.dissolutionDate" text="Dissolution Date"/>*</label>
		<input type="text" id="dissolutionDate" name="dissolutionDate"  class=" sText customDatePicker" size="30" value ="${selectedCommitteeDissolutionDate}"/>
		<%-- <form:input path="dissolutionDate" cssClass="datemask sText" />
		<form:errors path="dissolutionDate" cssClass="validationError"/> --%>	
	</p>

	
      <div style="margin:10px; width:150px; " >
      <input id="addRow" type="button" value="Add Member" class="butDef">
      </div>
      
        <table class="strippedTable" border="1" style="width: 750px;" id="tbl_posts">
        <thead>
          <tr>
            <th>No.</th>
            <th>Member Name</th>
            <th> Committee Designation </th>       
           
            <th>Action</th>
          </tr>
        </thead>
        <tbody id="tbl_posts_body">
       	  <c:forEach items="${allCommitteeMembers}" var="i" varStatus="loop">
	          <tr id="rec-${loop.index+1}">
	            <td><span class="sn">${i.position}</span>.</td>
	            <td>
	            <textarea readonly id="selectedSupportingMembers${loop.index+1}"  class="autosuggestmultiple" rows="2" cols="25">${i.member.getFullname() }</textarea>
	            <select style = "display: none;"  name="selectedSupportingMembers" multiple="multiple">
						<option value="${i.member.id}" class="${i.member.getFullname()}" selected="selected"></option>						
				</select>
	            </td>
	            <td>
	              <select  name="committeeDesignation" class="sSelect designation" id="committeeDesignation${loop.index+1}"  >
	              <c:forEach items="${allComitteeDesignation}" var="cd" >
	              	<c:choose>
						<c:when test="${cd.type eq i.designation.type}">
							<option value="${cd.id}" selected="selected"><c:out value="${cd.name}"></c:out></option>							
						</c:when>
						<c:otherwise>
							 <option value="${cd.id}" ><c:out value="${cd.name}"></c:out></option>
						</c:otherwise>
					</c:choose>	
				  </c:forEach>			  
				</select>  
				 	
				 
	            </td>
	           
	            <td  >
	            <!--  D:\dev environment\eclipse2022\workspace\els_workspace\src\main\webapp\resources\images\cancel.png -->
	             <img src="./resources/images/cancel.png" style="display:inline-block" title="Delete" width="30px" height="30px" align="justify"> 
	             <br>
	              <p>
	               <a class="delete-record" data-id="${loop.index+1}" style = "cursor: pointer;">
	            	Delete
	               </a> 
	              </p>
	           </td>
	          </tr>
	      </c:forEach>
        </tbody>
        </table> 
        
        
        <!-- sample  -->
        
         <div style="display:none;">
	         <table  id="sample_tbl">
		          <tr id="rec-1">
		            <td><span class="sn">1</span>.</td>
		            <td><textarea id="selectedSupportingMembers"  class="autosuggestmultiple" rows="2" cols="25"></textarea></td>
		            <td>
	              <select  name="committeeDesignation" class="sSelect designation" id="committeeDesignation${loop.index+1}"  >
	              <c:forEach items="${allComitteeDesignation}" var="cd" >
	              	<c:choose>
						<c:when test="${cd.type eq i.designation.type}">
							<option value="${cd.id}" selected="selected"><c:out value="${cd.name}"></c:out></option>							
						</c:when>
						<c:otherwise>
							 <option value="${cd.id}" ><c:out value="${cd.name}"></c:out></option>
						</c:otherwise>
					</c:choose>	
				  </c:forEach>			  
				</select>  
				 	
				 
	            </td>
		           
		            <td  >
		            
		            <!--  D:\dev environment\eclipse2022\workspace\els_workspace\src\main\webapp\resources\images\cancel.png -->
		             <img src="./resources/images/cancel.png" style="display:inline-block" title="Delete" width="30px" height="30px" align="justify"> 
		             <br>
		             <p>
		             <a class="delete-record" data-id="-1" style = "cursor: pointer;" >
		            	Delete
		              </a> 
		              </p>
		               </td>
		          </tr>     
	        </table>
         </div>
        
		
	<div class="fields expand">
		<h2></h2>
		<p class="tright">
			<input id="submit" type="submit" value="<spring:message code='generic.submit' text='Submit'/>" class="butDef">
			<input id="cancel" type="button" value="<spring:message code='generic.cancel' text='Cancel'/>" class="butDef">
		</p>
	</div>	

</div>
</div>
<!-- totalMembers -->
<input type="hidden" id="totalInvitedMembers" value="${totalInvitedMembers}"/>
<input type="hidden" id="totalRows" value="${totalMembers}"/>
<input type="hidden" id="selectedCommitteeId" value="${selectedCommitteeId}"/>
</body>
</html>