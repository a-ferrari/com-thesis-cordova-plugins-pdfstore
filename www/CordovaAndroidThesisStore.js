var exec = require('cordova/exec');

exports.store = function (byteString, fileName, success, error) {
    exec(success, error, 
        'CordovaAndroidThesisStore', 
        'store', 
        [byteString, 
             fileName]);
};
