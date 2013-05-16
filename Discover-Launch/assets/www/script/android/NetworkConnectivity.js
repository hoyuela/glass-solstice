var NetworkConnectivity = function() {};

NetworkConnectivity.prototype.isConnectionAvailable = function (successCallback, failureCallback) {
	cordova.exec(successCallback, failureCallback, 'NetworkConnectivityPlugin', 'isConnectionAvailable', []);
};

PhoneGap.addConstructor(function() {
                        PhoneGap.addPlugin("NetworkConnectivity", new NetworkConnectivity());
                        });