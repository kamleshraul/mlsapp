/*Common javascript functions to be used across jsp files */
var browserPrefixes = ['moz', 'ms', 'o', 'webkit'];
var isVisible = true; // internal flag, defaults to true	    
// get the correct attribute name for hidden
function getHiddenPropertyName(prefix) {
	return (prefix ? prefix + 'Hidden' : 'hidden');
}	    
// get the correct event name
function getVisibilityEvent(prefix) {
	return (prefix ? prefix : '') + 'visibilitychange';
}
// get current browser vendor prefix
function getBrowserPrefix() {
	for (var i = 0; i < browserPrefixes.length; i++) {
	  if(getHiddenPropertyName(browserPrefixes[i]) in document) {
	    // return vendor prefix
	    return browserPrefixes[i];
	  }
	}
	// no vendor prefix needed
	return null;
}
// bind and handle events
var browserPrefix = getBrowserPrefix();
var hiddenPropertyName = getHiddenPropertyName(browserPrefix);
var visibilityEventName = getVisibilityEvent(browserPrefix);
function onVisible() {
	// prevent double execution
	if(isVisible) {
	  return;
	}
	// change flag value
	isVisible = true;
	console.log('visible');
	location.reload(true);
}
function onHidden() {
	// prevent double execution
	if(!isVisible) {
	  return;
	}
	// change flag value
	isVisible = false;
	console.log('hidden');
}
function handleVisibilityChange(forcedFlag) {
	// forcedFlag is a boolean when this event handler is triggered by a
	// focus or blur event otherwise it's an Event object
	if(typeof forcedFlag === "boolean") {
	  if(forcedFlag) {
	    return onVisible();
	  }			
	  return onHidden();
	}			
	if(document[hiddenPropertyName]) {
	  return onHidden();
	}			
	return onVisible();
}

document.addEventListener(visibilityEventName, handleVisibilityChange, false);
/* document.addEventListener("visibilitychange", function() {
    if (document.hidden){
        console.log("Login tab is hidden")
    } else {
        console.log("Login tab is visible")
        location.reload(true);
    }
}); */
// extra event listeners for better behaviour
document.addEventListener('focus', function() {
  handleVisibilityChange(true);
}, false);
document.addEventListener('blur', function() {
  handleVisibilityChange(false);
}, false);
window.addEventListener('focus', function() {
    handleVisibilityChange(true);
}, false);
window.addEventListener('blur', function() {
  handleVisibilityChange(false);
}, false);