var PageSessionTimer = function() {};

// To keep the session alive
PageSessionTimer.prototype.keepSessionAlive = function() {
	cordova.exec(null, null, 'PageSessionTimerPlugin', 'keepSessionAlive', []);
};

// To upadte the lastRestCallTime variable
PageSessionTimer.prototype.updateLastRestCallTime = function() {
	cordova.exec(null, null, "PageSessionTimerPlugin", "updateLastRestCallTime", []);
};

// To start the initial page timer
PageSessionTimer.prototype.startPageTimer = function() {
	cordova.exec(null, null, 'PageSessionTimerPlugin', 'startPageTimer', []);
};