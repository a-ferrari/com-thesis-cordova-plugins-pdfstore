package com.thesis.plugins;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 *
 */
public class CordovaAndroidThesisStore extends CordovaPlugin {


    private CallbackContext _tmpCallbackContext;
    private JSONArray _tmpArgs;

   // private ActivityResultLauncher<Intent> _launcher;


    @Override
    public void onStart() {

        /* TODO cordova,  startActivityForResult is deprecated....
        _launcher = cordova.getActivity().registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Here, no request code
                            //Intent data = result.getData();

                        }
                    }
                });
*/
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("store")) {
            //cordova.getActivity().runOnUiThread(()
            cordova.getThreadPool().execute(() -> {
                try {
                    store(args, callbackContext);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            return true;


        }
        return false;
    }

    private void store(JSONArray args, CallbackContext callbackContext) throws JSONException {


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            this.store(args.getString(0), args.getString(1), callbackContext);
        } else {
            if (cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                this.store(args.getString(0), args.getString(1), callbackContext);
            } else {
                this._tmpCallbackContext = callbackContext;
                this._tmpArgs = args;
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                 cordova.requestPermissions(this, 1, permissions); //attention onRequestPermissionResult (deprecated) is called

            }

        }
    }



    /**
     * Never called :(
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this._tmpCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "NO_PERMISSION"));
                return;
            }
        }
        if (requestCode == 1) {
            this.store(_tmpArgs.getString(0), _tmpArgs.getString(1), _tmpCallbackContext);
        }

    }


    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this._tmpCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "NO_PERMISSION"));
                return;
            }
        }
        if (requestCode == 1) {
            this.store(_tmpArgs.getString(0), _tmpArgs.getString(1), _tmpCallbackContext);
        }

    }


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);



    }


    protected void store(String byteString, String fileName, CallbackContext callbackContext) {

        byte[] byteArray = Base64.decode(byteString, Base64.DEFAULT);

        boolean result = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

            result = saveFileNew(fileName, byteArray, true);

        } else {
            result = saveFileOld(fileName, byteArray, true);
        }

        if (result) {
            callbackContext.success();
        } else {
            callbackContext.error("WRITE_ERROR");
        }
    }


    // apilevel 29 getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
    //returns always null, for now use  android:requestLegacyExternalStorage="true" and legacy method
    @RequiresApi(api = Build.VERSION_CODES.R)
    boolean saveFileNew(String name, byte[] data, boolean openFile) {
        ContentResolver resolver = cordova.getActivity().getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.SIZE, data.length);
        contentValues.put(MediaStore.Downloads.IS_PENDING, 1);
        //contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download");
        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

        Uri itemUri = cordova.getActivity().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);


        if (itemUri != null) {

            try {
                OutputStream outputStream = cordova.getActivity().getContentResolver().openOutputStream(itemUri);
                outputStream.write(data);
                outputStream.close();
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0);

                cordova.getActivity().getContentResolver().update(itemUri, contentValues, null, null);

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Downloads.EXTERNAL_CONTENT_URI);


               // _launcher.launch(intent);
                cordova.startActivityForResult(this,intent,1);

                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }


    boolean saveFileOld(String name, byte[] data, boolean openFile) {
        try {

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Uri contentUri = Uri.fromFile(path);
            File file = new File(path, name);
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);

            if (openFile) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);

               // _launcher.launch(intent);
                cordova.startActivityForResult(this,intent,1);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
