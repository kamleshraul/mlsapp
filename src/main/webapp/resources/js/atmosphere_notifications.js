/*Atmosphere related code for notification functionality */
$(function () {
    "use strict";
    
    Vue.component('notification-viewer', {
		data: function() {
			return {
				isWysiwygInitiated: false
			}
		},
		props: ['selectedNotification'],
		updated: function() {
			var self = this;			
			//console.log("test wysiwyg");
			if(self.isWysiwygInitiated) {
				//console.log("Wysiwyg Exists!");
				$("#selectedNotificationMessage").wysiwyg("setContent", self.selectedNotification.message).wysiwyg("destroy");
			}							
			$("#selectedNotificationMessage").wysiwyg({
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
					//rmFormat: {	rmMsWordMarkup: true }
				}
			});
			self.isWysiwygInitiated = true;
		}
	});
	
	//VueJs Instantiation (Global Instance of Vue)
	var vueVM = new Vue({
		el: '#vueApp',
		data: {
			vueMsg: 'Hello Vue!',
			notificationsCount: 0,
			notificationsList: [],
			selectedNotification: {}
		},
		created: function () {
			// `this` points to the vueVM instance
			var self = this;
			$.ajax({
				url: 'notification/active_count_for_user',
				type: 'GET',
		        async: false,
		        success: function(nCount) {
		        	self.notificationsCount = nCount;
		        	//console.log("self.notificationsCount: " + self.notificationsCount);
		        	$.ajax({
						url: 'notification/visible_list_for_user?active_count='+self.notificationsCount,
						type: 'GET',
						dataType: 'JSON',
						async: false,
				        success: function(nData) {
				        	if(nData!=undefined && nData.length > 0) {
				        		self.notificationsList = nData;
					        	for(var i=0; i<self.notificationsList.length; i++) {
					        		var formattedSentOnMoment = moment(self.notificationsList[i].sentOn);
					        		formattedSentOnMoment.locale('mr');
					        		self.notificationsList[i].formattedSentOn=formattedSentOnMoment.calendar();
					        	}					        	
				        	}							        	
				        }
					});
		        }
			});
		},
		mounted: function () {			
			// `this` points to the vueVM instance
			var self = this;
			if(self.notificationsList.length >= parseInt($('#notifications_visibleMaxCount').val())) {
        		$.ajax({
					url: 'notification/'+$('#authusername').val()+'/clearAllRead',
					type: 'POST',
					async: false,
			        success: function(cleared) {
			        	if(cleared) {
			        		for(var i=0; i<self.notificationsList.length; i++) {
			        			if(self.notificationsList[i].markedAsReadByReceiver) {
			        				self.notificationsList[i].clearedByReceiver = true;
			        			}       			
				        	}
			        		$.prompt("All the read messages are automatically cleared for you.. \nPlease click on view all to view them!");
			        		openNotificationsWindow();
			        	} else {
			        		alert("Some error occurred in clearing the notifications!");
			        		openNotificationsWindow();
			        	}
			        }
				});
        	} else if(self.notificationsList.length >= Math.round(parseInt($('#notifications_visibleMaxCount').val()) * 0.9)) {
        		$.prompt("Please consider clearing all the read messages!");
        		openNotificationsWindow();
        		
        	} else if(self.notificationsCount >= Math.round(parseInt($('#notifications_visibleMaxCount').val()) * 0.6)) {
        		$.prompt("You have new notifications pending since long.. Please read them!");
        		openNotificationsWindow();
        		
        	} else {        		
        		openNotificationsWindow();
        	}
		},
		methods: {
			readNotification: function(notificationIndex) {
				var self = this;
				$.ajax({
					url: 'notification/'+self.notificationsList[notificationIndex].id+'/markAsRead',
					type: 'POST',
					async: false,
			        success: function(marked) {
			        	if(marked) {
			        		self.notificationsList[notificationIndex].markedAsReadByReceiver = true;
			        		self.notificationsCount = self.notificationsCount - 1;
			        		self.selectedNotification = self.notificationsList[notificationIndex];
							$('.fbox').fancybox({
								autoSize: false,
								width: self.selectedNotification.title==self.selectedNotification.message? 580 : 720,
								height: self.selectedNotification.title==self.selectedNotification.message? 125 : 420,
								afterShow: function() {
									
							    }
							});										
							//$.fancybox.open($('#notificationViewerPopUp').html(),{autoSize:false,width:400,height:300});
			        	} else {
			        		alert("Some error occurred in reading the notification!");
			        	}
			        }
				});
			},
			clearNotification: function(notificationIndex) {
				var self = this;
				if(!self.notificationsList[notificationIndex].markedAsReadByReceiver) {
					$.prompt("You haven't opened this message yet.. Please read the message first");
					return false;
				} else {
					$.ajax({
						url: 'notification/'+self.notificationsList[notificationIndex].id+'/clear',
						type: 'POST',
				        success: function(isCleared) {
				        	if(isCleared) {
				        		self.notificationsList[notificationIndex].clearedByReceiver = true;
				        	} else {
				        		alert("Some error occurred in clearing the notification!");
				        	}
				        }
					});
				}				
			},
			markAllNotificationsAsRead: function() {
				var self = this;
				$.ajax({
					url: 'notification/'+$('#authusername').val()+'/markAllAsRead',
					type: 'POST',
			        success: function(marked) {
			        	if(marked) {
			        		for(var i=0; i<self.notificationsList.length; i++) {
			        			self.notificationsList[i].markedAsReadByReceiver = true;
				        	}
			        	} else {
			        		alert("Some error occurred in marking the notifications!");
			        	}
			        }
				});
			},
			clearAllReadNotifications: function() {
				var self = this;
				$.ajax({
					url: 'notification/'+$('#authusername').val()+'/clearAllRead',
					type: 'POST',
			        success: function(cleared) {
			        	if(cleared) {
			        		for(var i=0; i<self.notificationsList.length; i++) {
			        			if(self.notificationsList[i].markedAsReadByReceiver) {
			        				self.notificationsList[i].clearedByReceiver = true;
			        			}       			
				        	}
			        	} else {
			        		alert("Some error occurred in clearing the notifications!");
			        	}
			        }
				});
			},
			viewAllNotifications: function() {
				$('#notifications').fadeOut('fast');
				$.blockUI({ message: '<img src="./resources/images/waitAnimated.gif" />' });
				var notificationMasterURL = 'notification/module';
				if(notificationMasterURL.indexOf("home")==-1){
		           $("#alertmod").remove();//this is done as fix to jqgrid warning box that shows up at bottom of the page 
			       $('.content').load(notificationMasterURL, function(data){
			    	   var title = $(data).filter('title').text();
					   $('#module_title').html(title);
			       });
		        }
				$.unblockUI();
				return false;
			},
			loadMessageContentForWysiwyg: function() {
				var self = this;
				console.log("Child Notification Message: " + self.selectedNotification.message);
				$("#selectedNotificationMessage").wysiwyg("setContent", self.selectedNotification.message);
			}
		}
	});
	
	//vueVM.notificationsList.push({'id':50, 'title': 'Test Title 50', 'sentOn': '2017-09-18 15:05:28', 'markedAsReadByReceiver': true, 'clearedByReceiver': false});
	
	$('#notifyIcon').click(function () {
		openNotificationsWindow();
	});
	
	/* $('#notification_alert').val("");
	$('#notification_alert').change(function() {
		console.log("notification_alert: " + $(this).val());
		if($(this).val()!="") {
			alert($(this).val());
		}					
		$(this).val("");
	}); */
	
	/*$('.toast-item').click(function () {
	    
	});*/
	

    var client_username = $('#authusername').val();
    var socket = atmosphere;
    var subSocket;
    var transport = 'websocket';
    
    //console.log("client_username: " + client_username);

    // We are now ready to cut the request
    var request = { url: 'push_message?client_username='+client_username,
        contentType : "application/json",
        logLevel : 'debug',
        transport : transport ,
        shared : false, //for sharing connection across tabs/windows of single client browser
        trackMessageLength : true,
        reconnectInterval : 5000 
    };

    request.onOpen = function(response) {
        //console.log("Atmosphere connected using " + response.transport);
        transport = response.transport;
        // Carry the UUID. This is required if you want to call subscribe(request) again.
        request.uuid = response.request.uuid;
    };

    request.onClientTimeout = function(r) {
        //console.log("Client closed the connection after a timeout. Reconnecting in " + request.reconnectInterval);
        //subSocket.push(atmosphere.util.stringifyJSON({ sender: client_username, message: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval }));
        setTimeout(function (){
            subSocket = socket.subscribe(request);
        }, request.reconnectInterval);
    };

    request.onReopen = function(response) {
        //console.log("Atmosphere re-connected using " + response.transport);
    };

    // For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
    request.onTransportFailure = function(errorMsg, request) {
        atmosphere.util.info(errorMsg);
        request.fallbackTransport = "long-polling";
        //console.log("Atmosphere Default Transport Failed. Default transport was WebSocket, fallback is " + request.fallbackTransport);
    };

    request.onMessage = function (response) {

        var pushmessage = response.responseBody;
        try {
        	//console.log("test: " + pushmessage);
            var pushmessage_json = atmosphere.util.parseJSON(pushmessage);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', pushmessage);
            return;
        }
        
        if(pushmessage_json.isVolatile!=true) {
        	pushNotification(pushmessage_json);
        }        

        var date = typeof(pushmessage_json.time) == 'string' ? parseInt(pushmessage_json.time) : pushmessage_json.time;
        addMessage(pushmessage_json.senderName, pushmessage_json.title, new Date(date), pushmessage_json.isVolatile);    
        
    };

    request.onClose = function(response) {
        //console.log("Server closed the connection after a timeout");
        /*if (subSocket) {        	
            subSocket.push(atmosphere.util.stringifyJSON({ sender: sender, message: 'disconnecting' }));
        }*/
    };

    request.onError = function(response) {
        //console.log("Sorry, but there's some problem with your socket or the server is down");
    };

    request.onReconnect = function(request, response) {
        //console.log("Connection lost, trying to reconnect. Trying to reconnect " + request.reconnectInterval);
    };

    subSocket = socket.subscribe(request);

    function addMessage(senderName, message, datetime, isVolatile) {
    	var toastMessage = senderName + " @ " +
				+ (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
		        + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
		        + '<br/>' + message;
    	
        //$.prompt(toastMessage); 
    	//var notificationToast =  $().toastmessage('showNoticeToast', toastMessage);
    	var notificationToast =  $().toastmessage('showToast', {
    	    text     : toastMessage,
    	    sticky   : isVolatile,
    	    //position : 'left-bottom',
    	    //inEffectDuration : 800,
    	    stayTime : 5000,
    	    type     : 'notice', //isVolatile==true? 'warning':'notice',
    	    close    : function () {
    	    				/*console.log("toast is closed ...");
    	    				if(!isVolatile) {
    	    					$.ajax({
    								url: 'notification/'+vueVM.notificationsList[0].id+'/markAsRead',
    								type: 'POST',
    								async: false,
    						        success: function(marked) {
    						        	if(marked) {
    						        		vueVM.notificationsList[0].markedAsReadByReceiver = true;
    						        		vueVM.selectedNotification = vueVM.notificationsList[0];
    										$('.fbox').fancybox({
    											autoSize: false,
    											width: self.selectedNotification.title==self.selectedNotification.message? 580 : 720,
												height: self.selectedNotification.title==self.selectedNotification.message? 125 : 420,
    											afterShow: function() {
    												
    										    }
    										});										
    										//$.fancybox.open($('#notificationViewerPopUp').html(),{autoSize:false,width:400,height:300});
    						        	} else {
    						        		alert("Some error occurred in marking the notification as read!");
    						        	}
    						        }
    							});
    	    				}*/			    	    	
    	    		   }
    	});
    }
    
    function pushNotification(pushmessage_json) {
    	vueVM.notificationsCount = vueVM.notificationsCount + 1;
        $('#notification_counter').fadeIn('slow');		// SHOW THE COUNTER.
        
        var sentOnMoment = moment(new Date(pushmessage_json.sentOn)).format('YYYY-MM-DD HH:mm:ss');
        var formattedSentOnMoment = moment(sentOnMoment);
		formattedSentOnMoment.locale('mr');		
        
        var notification_identifiers = {
        	'pushmessage_id': pushmessage_json.id,
        	'receiver': $('#authusername').val()
        };
        $.get('notification/find_id_by_pushmessage_receiver', notification_identifiers)
        		.done(function(notificationId) {        		
        	vueVM.notificationsList.unshift({
        		'id': notificationId, 
        		'pushMessageId': pushmessage_json.id,
        		'title': pushmessage_json.title, 
        		'message': pushmessage_json.message,
        		'sender': pushmessage_json.sender,
        		'senderName': pushmessage_json.senderName,
        		'receiver': $('#authusername').val(),
        		'receivers': pushmessage_json.receivers,
        		'sentOn': sentOnMoment,
        		'formattedSentOn': formattedSentOnMoment.calendar(),
        		'receivedOn': '',
        		'formattedReceivedOn': '',
        		'markedAsReadByReceiver': false, 
        		'clearedByReceiver': false
        	});
        });        
    }
    
    function openNotificationsWindow() {
    	// TOGGLE (SHOW OR HIDE) NOTIFICATION WINDOW.
	    $('#notifications').fadeToggle('fast', 'linear', function () {
	        /* if ($('#notifications').is(':hidden')) {
	            $('#notifyIcon').css('background-color', '#2E467C');
	        }
	        else $('#notifyIcon').css('background-color', '#FFF');		// CHANGE BACKGROUND COLOR OF THE BUTTON. */
	    });
	    $('#notification_counter').fadeOut('slow');		// HIDE THE COUNTER.
	    return false;
    }
});