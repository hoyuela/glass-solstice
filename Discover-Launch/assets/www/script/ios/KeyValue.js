var KeyValue = function() {};

KeyValue.prototype.setKeyValue = function (successCallback, failureCallback, key, value) {
	cordova.exec(successCallback, failureCallback, 'KeyValuePlugin', 'setKeyValue', [key, value]);
};

KeyValue.prototype.getValue = function (successCallback, failureCallback, key) {
	cordova.exec(successCallback, failureCallback, 'KeyValuePlugin', 'getValue', [key]);
};

KeyValue.prototype.setEncryptedKeyValue = function (successCallback, failureCallback, key, value) {
	cordova.exec(successCallback, failureCallback, 'KeyValuePlugin', 'setEncryptedKeyValue', [key, value]);
};

KeyValue.prototype.getEncryptedValue = function (successCallback, failureCallback, key) {
	cordova.exec(successCallback, failureCallback, 'KeyValuePlugin', 'getEncryptedValue', [key]);
};

PhoneGap.addConstructor(function() {
                        PhoneGap.addPlugin("KeyValue", new KeyValue());
                        });