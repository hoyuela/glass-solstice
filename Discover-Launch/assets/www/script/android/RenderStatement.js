var RenderStatement = function() {};

RenderStatement.prototype.show = function(failureCallback, jsonData, selectedIndex, baseUrl) {
	return cordova.exec(null, failureCallback, 'StatementPlugin', 'method', [jsonData, selectedIndex, baseUrl]);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("StatementPlugin", new RenderStatement());
});
