var ClearHistory = function() {};

ClearHistory.prototype.finish = function () {
	return cordova.exec(null, null, 'ClearHistoryPlugin', 'finish', []);
};

ClearHistory.prototype.clearHistory = function () {
	return cordova.exec(null, function failure(){}, 'ClearHistoryPlugin', 'clearHistory', []);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("ClearHistory", new ClearHistory());
});