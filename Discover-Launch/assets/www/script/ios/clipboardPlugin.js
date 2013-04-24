/**
 * Clipboard plugin for PhoneGap
 * 
 * @constructor
 */
var clipboardPlugin = function() {};
/**
 * Set the clipboard text
 *
 * @param {String} text The new clipboard content
 */
clipboardPlugin.prototype.setText = function(successCallback, text) {
    cordova.exec(successCallback, null, "ClipboardPlugin", "setText", [text]);
}

/**
 * Get the clipboard text
 *
 * @param {String} text The new clipboard content
 */
clipboardPlugin.prototype.getText = function(callback) {
	cordova.exec(callback, null, "ClipboardPlugin", "getText", []);
}

/**
 * Register the plugin with PhoneGap
 */
PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("ClipboardPlugin", new clipboardPlugin());
});