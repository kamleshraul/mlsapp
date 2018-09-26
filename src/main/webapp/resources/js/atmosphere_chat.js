/*Atmosphere related code for chat functionality */
$(function () {
    "use strict";

    var header = $('#header');
    var content = $('#content');
    var inputBox = $('#inputBox');
    var status = $('#status');
    var myName = false;
    var author = null;
    var logged = false;
    var socket = atmosphere;
    var subSocket;
    var transport = 'websocket';

    // We are now ready to cut the request
    var request = { url: /* document.location.toString() */ + 'http://localhost:8080/els/chat',
        contentType : "application/json",
        logLevel : 'debug',
        transport : transport ,
        shared : true, //for sharing connection across tabs/windows of single client browser
        trackMessageLength : true,
        reconnectInterval : 5000 };


    request.onOpen = function(response) {
        content.html($('<p class="input_box_text">', { text: 'Atmosphere connected using ' + response.transport }));
        alert("Atmosphere connected using " + response.transport);
        inputBox.removeAttr('disabled').focus();
        status.text('Choose name:');
        transport = response.transport;

        // Carry the UUID. This is required if you want to call subscribe(request) again.
        request.uuid = response.request.uuid;
    };

    request.onClientTimeout = function(r) {
        content.html($('<p class="input_box_text">', { text: 'Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval }));
        subSocket.push(atmosphere.util.stringifyJSON({ author: author, message: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval }));
        inputBox.attr('disabled', 'disabled');
        setTimeout(function (){
            subSocket = socket.subscribe(request);
        }, request.reconnectInterval);
    };

    request.onReopen = function(response) {
        inputBox.removeAttr('disabled').focus();
        content.html($('<p class="input_box_text">', { text: 'Atmosphere re-connected using ' + response.transport }));
    };

    // For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
    request.onTransportFailure = function(errorMsg, request) {
        atmosphere.util.info(errorMsg);
        request.fallbackTransport = "long-polling";
        header.html($('<h3>', { text: 'Atmosphere Default Transport Failed. Default transport was WebSocket, fallback is ' + request.fallbackTransport }));
    };

    request.onMessage = function (response) {

        var message = response.responseBody;
        try {
        	console.log("test: " + message);
            var json = atmosphere.util.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        inputBox.removeAttr('disabled').focus();
        if (!logged && myName) {
            logged = true;
            status.text(myName + ': ').css('color', 'blue');
        } else {
            var me = json.author == author;
            var date = typeof(json.time) == 'string' ? parseInt(json.time) : json.time;
            addMessage(json.author, json.message, me ? 'blue' : 'black', new Date(date));
        }
    };

    request.onClose = function(response) {
        content.html($('<p class="input_box_text">', { text: 'Server closed the connection after a timeout' }));
        if (subSocket) {
            subSocket.push(atmosphere.util.stringifyJSON({ author: author, message: 'disconnecting' }));
        }
        inputBox.attr('disabled', 'disabled');
    };

    request.onError = function(response) {
        content.html($('<p class="input_box_text">', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
        logged = false;
    };

    request.onReconnect = function(request, response) {
        content.html($('<p class="input_box_text">', { text: 'Connection lost, trying to reconnect. Trying to reconnect ' + request.reconnectInterval}));
        inputBox.attr('disabled', 'disabled');
    };

    subSocket = socket.subscribe(request);

    inputBox.keydown(function(e) {
        if (e.keyCode === 13) {
            var msg = $(this).val();

            // First message is always the author's name
            if (author == null) {
                author = msg;
            }

            subSocket.push(atmosphere.util.stringifyJSON({ author: author, message: msg }));
            $(this).val('');

            inputBox.attr('disabled', 'disabled');
            if (myName === false) {
                myName = msg;
            }
        }
    });

    function addMessage(author, message, color, datetime) {
        content.append('<p class="input_box_text"><span style="color:' + color + '">' + author + '</span> @ ' +
            + (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':'
            + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes())
            + ': ' + message + '</p>');
    }
});