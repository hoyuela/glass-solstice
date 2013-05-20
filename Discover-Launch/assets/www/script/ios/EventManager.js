var EventManager = function() {};

EventManager.prototype.showCalendar = function(successCallback, failCallback) {
	cordova.exec(successCallback, failCallback, "EventManagerPlugin", "showCalendar", []);
}

EventManager.prototype.createEvent = function(startDateComponents, endDateComponents, title, description, location, hoursPrecedingEndDate) {
	cordova.exec(null, null, "EventManagerPlugin", "createEvent", [startDateComponents, endDateComponents, title, description, location, hoursPrecedingEndDate]);
} 

/*
   retrieve the events from oldest to latest
// startDate - An array in the format (mm, dd, yyyy)
// stopDate - An array in the format (mm, dd, yyyy)
*/
EventManager.prototype.retrieveEvents = function(successCallback, failCallback, startDate, stopDate) {
    cordova.exec(successCallback, failCallback, "EventManagerPlugin", "retrieveEvents", [startDate, stopDate]);
}

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("EventManager", new EventManager());
});