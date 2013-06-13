var EventManager = function() {};

EventManager.prototype.createEvent = function(successCallback, failCallback, startTime, endTime, title, description, location) {
	cordova.exec(successCallback, failCallback, "EventManagerPlugin", "createEvent", [startTime, endTime, title, description, location]);
}

EventManager.prototype.showCalendar = function(successCallback, failCallback) {
	cordova.exec(successCallback, failCallback, "EventManagerPlugin", "showCalendar", []);
}

EventManager.prototype.retrieveEvents = function(successCallback, failCallback) {
	cordova.exec(successCallback, failCallback, "EventManagerPlugin", "retrieveEvents", []);
}

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("EventManager", new EventManager());
});