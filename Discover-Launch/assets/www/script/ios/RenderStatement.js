var RenderStatement = function() {};

RenderStatement.prototype.show = function(failureCallback, jsonData, selectedIndex, baseUrl) {
    var jsonString = JSON.stringify(jsonData);
	return cordova.exec(null, failureCallback, 'DisplayStatementsPlugin', 'displayStatements', [jsonString, selectedIndex, baseUrl]);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("DisplayStatementsPlugin", new RenderStatement());
});
