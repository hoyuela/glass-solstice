var Screenshot = function() {};

Screenshot.prototype.takeScreenshot = function (successCallback, failureCallback) {
	return cordova.exec(successCallback, failureCallback, 'ScreenshotPlugin', 'takeScreenshot', []);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("Screenshot", new Screenshot());
});