# com-thesis-plugins-pdfstore
This simple Cordova plugin saves a bytestring (the byte image of a pdf) to a pdf file in MediaStore.Downloads.
The purpose is to allow the user to download a pdf file and open it using the device's default pdf viewer.
Starting from android 10 and especially from android 11 with the introducion of 'scope storage' (https://developer.android.com/about/versions/11/privacy/storage) we need to change the way to save files in shared storages (https://developer.android.com/training/data-storage/shared) and go beyound the old filesystem approach of 'cordova-plugin-file'

This is our production example that uses the MediaStore Api. 

Tested on Android APIs 28, 29(10 Q),30(11 R) and 31.

### NOTE
Api level 29: using MediaStore Api with MediaStore.Downloads triggers 'SQLiteConstraintException: UNIQUE constraint failed' excpetions, so we use the legacy method (filesystem), you need to add android:requestLegacyExternalStorage="true" to your <application> element in the manifest. 

Api leved <= 29:
add these permissions in your Manifest
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

## API
The plugin exports only one function:

```javascript
//CordovaAndroidThesisStore.js

/**
 * @param byteString the bytestring 
 * @param fileName 
 */
exports.store = function (byteString, fileName, success, error) {
    exec(success, error, 
        'CordovaAndroidThesisStore', 
        'store', 
        [byteString, fileDir, fileName]);
};
```

## example

```

var array = new Uint8Array([37, 80, 68, 70, 45, 49, 46, 49, 10, 37, -62, -91, -62, -79, -61,
                                            -85, 10, 10, 49, 32, 48, 32, 111, 98, 106, 10, 32, 32, 60, 60, 32, 47, 84, 121, 112, 101, 32, 47, 67, 97, 116,
                                            97, 108, 111, 103, 10, 32, 32, 32, 32, 32, 47, 80, 97, 103, 101, 115, 32, 50, 32, 48, 32, 82, 10, 32, 32, 62,
                                            62, 10, 101, 110, 100, 111, 98, 106, 10, 10, 50, 32, 48, 32, 111, 98, 106, 10, 32, 32, 60, 60, 32, 47, 84, 121,
                                            112, 101, 32, 47, 80, 97, 103, 101, 115, 10, 32, 32, 32, 32, 32, 47, 75, 105, 100, 115, 32, 91, 51, 32, 48, 32,
                                            82, 93, 10, 32, 32, 32, 32, 32, 47, 67, 111, 117, 110, 116, 32, 49, 10, 32, 32, 32, 32, 32, 47, 77, 101, 100,
                                            105, 97, 66, 111, 120, 32, 91, 48, 32, 48, 32, 51, 48, 48, 32, 49, 52, 52, 93, 10, 32, 32, 62, 62, 10, 101,
                                            110, 100, 111, 98, 106, 10, 10, 51, 32, 48, 32, 111, 98, 106, 10, 32, 32, 60, 60, 32, 32, 47, 84, 121, 112,
                                            101, 32, 47, 80, 97, 103, 101, 10, 32, 32, 32, 32, 32, 32, 47, 80, 97, 114, 101, 110, 116, 32, 50, 32, 48, 32,
                                            82, 10, 32, 32, 32, 32, 32, 32, 47, 82, 101, 115, 111, 117, 114, 99, 101, 115, 10, 32, 32, 32, 32, 32, 32, 32,
                                            60, 60, 32, 47, 70, 111, 110, 116, 10, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 60, 60, 32, 47, 70, 49, 10,
                                            32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 60, 60, 32, 47, 84, 121, 112, 101, 32, 47, 70, 111,
                                            110, 116, 10, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 47, 83, 117, 98, 116,
                                            121, 112, 101, 32, 47, 84, 121, 112, 101, 49, 10, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                                            32, 32, 32, 47, 66, 97, 115, 101, 70, 111, 110, 116, 32, 47, 84, 105, 109, 101, 115, 45, 82, 111, 109, 97, 110,
                                            10, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 62, 62, 10, 32, 32, 32, 32, 32, 32, 32, 32, 32,
                                            32, 32, 62, 62, 10, 32, 32, 32, 32, 32, 32, 32, 62, 62, 10, 32, 32, 32, 32, 32, 32, 47, 67, 111, 110, 116, 101,
                                            110, 116, 115, 32, 52, 32, 48, 32, 82, 10, 32, 32, 62, 62, 10, 101, 110, 100, 111, 98, 106, 10, 10, 52, 32, 48,
                                            32, 111, 98, 106, 10, 32, 32, 60, 60, 32, 47, 76, 101, 110, 103, 116, 104, 32, 53, 53, 32, 62, 62, 10, 115,
                                            116, 114, 101, 97, 109, 10, 32, 32, 66, 84, 10, 32, 32, 32, 32, 47, 70, 49, 32, 49, 56, 32, 84, 102, 10, 32,
                                            32, 32, 32, 48, 32, 48, 32, 84, 100, 10, 32, 32, 32, 32, 40, 72, 101, 108, 108, 111, 32, 87, 111, 114, 108,
                                            100, 41, 32, 84, 106, 10, 32, 32, 69, 84, 10, 101, 110, 100, 115, 116, 114, 101, 97, 109, 10, 101, 110, 100,
                                            111, 98, 106, 10, 10, 120, 114, 101, 102, 10, 48, 32, 53, 10, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 32, 54,
                                            53, 53, 51, 53, 32, 102, 32, 10, 48, 48, 48, 48, 48, 48, 48, 48, 49, 56, 32, 48, 48, 48, 48, 48, 32, 110, 32,
                                            10, 48, 48, 48, 48, 48, 48, 48, 48, 55, 55, 32, 48, 48, 48, 48, 48, 32, 110, 32, 10, 48, 48, 48, 48, 48, 48,
                                            48, 49, 55, 56, 32, 48, 48, 48, 48, 48, 32, 110, 32, 10, 48, 48, 48, 48, 48, 48, 48, 52, 53, 55, 32, 48, 48,
                                            48, 48, 48, 32, 110, 32, 10, 116, 114, 97, 105, 108, 101, 114, 10, 32, 32, 60, 60, 32, 32, 47, 82, 111, 111,
                                            116, 32, 49, 32, 48, 32, 82, 10, 32, 32, 32, 32, 32, 32, 47, 83, 105, 122, 101, 32, 53, 10, 32, 32, 62, 62, 10,
                                            115, 116, 97, 114, 116, 120, 114, 101, 102, 10, 53, 54, 53, 10, 37, 37, 69, 79, 70, 10]);
    var base64String = btoa(String.fromCharCode.apply(null, new Uint8Array(array)));
    window.cordova.plugins.CordovaAndroidThesisStore.store(base64String, 'test3.pdf', function() {
        console.log('Excelsior!');
      }, function(err) {
        console.log('Uh oh... ' + err);
      });
```
