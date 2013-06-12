
var LoadingView = function() {};

LoadingView.prototype.spinnerOn = function (successCallback, failureCallback, title, msg) {
	return cordova.exec(successCallback, failureCallback, 'LoadingViewPlugin', 'spinnerOn', [title, msg]);
};

LoadingView.prototype.spinnerOff = function (successCallback, failureCallback) {
	return cordova.exec(successCallback, failureCallback, 'LoadingViewPlugin', 'spinnerOff', ['jsonFormat']);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("LoadingView", new LoadingView());
});