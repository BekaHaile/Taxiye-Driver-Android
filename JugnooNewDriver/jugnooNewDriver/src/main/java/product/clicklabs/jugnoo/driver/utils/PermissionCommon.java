package product.clicklabs.jugnoo.driver.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import product.clicklabs.jugnoo.driver.R;

/**
 * Created by socomo-46 on 22/09/16.
 */


public final class PermissionCommon {
    public static final int REQUEST_CODE_READ_SMS = 2001;
    public static final int REQUEST_CODE_CAMERA = 2002;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2003;
    public static final int REQUEST_CODE_FINE_LOCATION = 2004;
    public static final int REQUEST_CODE_READ_PHONE_STATE = 2005;
    public static final int REQUEST_CODE_CALL_PHONE = 2006;
    public static final int REQUEST_CODE_CALL_LOGS = 2007;

    private static final int REQUEST_CODE = 0x8;
    private static final int REQUEST_CODE_RATIONAL = 0x9;
    private int requestCodeInitiated;
    private View view;
    private String[] permissionsInitiated;
    private Snackbar snackBarPermissionDenied, snackBarRational;
    private String appName;
    private Activity activity;
    private PermissionListener permissionListener;
    private HashMap<String, String> rationalMessageMap;
    private Fragment fragment;
    private boolean showRetryAlertOnFirstDenial;

    public static final int SHOW_RATIONAL_MESSAGE = 1;
    public static final int SKIP_RATIONAL_MESSAGE = 2;
    public static final int SKIP_RATIONAL_REQUEST = 3;

    @IntDef({SHOW_RATIONAL_MESSAGE, SKIP_RATIONAL_MESSAGE, SKIP_RATIONAL_REQUEST})
    @Retention(RetentionPolicy.SOURCE)
    private  @interface RationalMessageStatus {}

    @SuppressWarnings("unused")
    private PermissionCommon() {

    }


    /**
     * @param context context of activity or fragment
     */
    public <Instance extends Activity> PermissionCommon(Instance context) {
        activity = context;
        this.view = activity.findViewById(android.R.id.content);
    }

    public <Instance extends Fragment >PermissionCommon(Instance context){
        this.fragment = context;
        activity = context.getActivity();
        this.view = activity.findViewById(android.R.id.content);
    }


    /**
     *
     * @param rationalMessageMap An explanation to the user, on why you are using the features
     *                           This can be made like HashMap<String,String> hashMap = new HashMap<>();
     *                           hashMap.put("Manifest.permission.CAMERA","This app requires camera access to use face recognition");
     *                           hashMap.put("Manifest.permission.READ_EXTERNAL_STORAGE","This app requires storage access to display gallery images");
     *                           Alternatively, you can declare the permission reason in getRationalMessage() method which would be global for the application if not passed in method
     */
    public PermissionCommon setMessageMap( @NonNull HashMap<String, String> rationalMessageMap){
        this.rationalMessageMap = rationalMessageMap;
        return this;

    }

    public PermissionCommon setCallback(PermissionListener permissionListener){
        this.permissionListener = permissionListener;
        return this;

    }






    public final void onRequestPermissionsResult(final int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:


                int permissionsGranted = 0;
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED)
                        permissionsGranted++;
                }


                if (grantResults.length == permissionsGranted) {
                    // this means all the permissions have been granted and we are ready to GO!
                    if(permissionListener!=null) permissionListener.permissionGranted(requestCodeInitiated);


                } else {

                    String permissionDenied = permissionsInitiated[0];
                    for (String permission : permissions) {

                        if (!isGranted(permission,activity)) {
                            permissionDenied = permission;

                            if(!shouldShowRationalPermission(permission)){



                                String messageToShow = getNeverAskMessage(permission);

                                //this means the user has blocked a permission and chosen "Never ask again" for the that permission.


                                if(permissionListener!=null){
                                    if(permissionListener.permissionDenied(requestCodeInitiated, true)){
                                        showPermissionDenied(messageToShow);
                                    }
                                }else{
                                    showPermissionDenied(messageToShow);
                                }

                                return;

                            }
                        }

                    }

                    //the user has denied permission normally.Wait for user's action to ask for permission again

                    if(showRetryAlertOnFirstDenial){
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(getRationalMessage(permissionDenied) +  activity.getString(R.string.are_you_sure))
                                .setNegativeButton(activity.getString(R.string.i_am_sure), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(permissionListener!=null) { permissionListener.permissionDenied(requestCodeInitiated, false); }

                                    }
                                }).setPositiveButton(activity.getString(R.string.retry), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getPermission(requestCodeInitiated,SKIP_RATIONAL_MESSAGE,false,permissionsInitiated);

                            }
                        });
                        builder.show();

                    }else if(permissionListener!=null) {
                        permissionListener.permissionDenied(requestCodeInitiated, false);
                    }


                }


                break;


            case REQUEST_CODE_RATIONAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getPermission(requestCodeInitiated,  permissionsInitiated); //continues to check again if all permissions have been granted or there is still a rational permission pending
                else {
                    if (!shouldShowRationalPermission(permissions[0])) {

                        String messageToShow = getNeverAskMessage(permissions[0]);
                        //activity means the user has blocked a permission and chosen "Never ask again" for the same
                        if(permissionListener!=null){
                            if(permissionListener.permissionDenied(requestCodeInitiated, true)){
                                showPermissionDenied(messageToShow);
                            }
                        }else{
                            showPermissionDenied(messageToShow);
                        }

                        return;

                    }

                    if(permissionListener!=null) permissionListener.permissionDenied(requestCodeInitiated, false);

                }

                break;
            default:
                break;
        }
    }

    public String getNeverAskMessage(String permissionName){
        return "You have disabled the permission to " + getPermissionLabel(permissionName) + ".Please go to app settings to allow permission";
    }

    /**
     * @param permission permission for which the rational message is needed
     * @return A message for the permission explaining why the feature is required
     * This is displayed as per the hashMap if not passed feel free to declare the reasons below that would be global for the app
     */
    private String getRationalMessage(String permission) {


        if (rationalMessageMap != null && rationalMessageMap.containsKey(permission))
            return rationalMessageMap.get(permission);


        if (appName == null)
            appName = activity.getString(R.string.app_name);


        switch (permission) {
            case Manifest.permission.CAMERA:
                return appName + " needs permission to access the camera to save images. ";
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return appName + " needs permission fetch current location address. ";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return appName + " needs permission fetch current location address. ";
            case Manifest.permission.RECEIVE_SMS:
                return appName + " needs this permission to auto-detect OTP. ";
            case Manifest.permission.READ_PHONE_STATE:
                return appName + " needs Phone State permission to uniquely identify device.";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return appName + " needs External storage permission to fetch selected image from gallery.";
            default:
                return appName + " needs this permission to proceed.";
        }

    }



    public void getPermission(int requestCode,final String... permissionArray) {
        getPermission(requestCode,SHOW_RATIONAL_MESSAGE,false,permissionArray);

    }

    public void getPermission(int requestCode,boolean showRetryAlertOnFirstDenial ,final String... permissionArray){
        getPermission(requestCode,SHOW_RATIONAL_MESSAGE,showRetryAlertOnFirstDenial,permissionArray);
    }

    public void getPermission(int requestCode, @RationalMessageStatus int rationalOperationRequired,final String... permissionArray){
        getPermission(requestCode,rationalOperationRequired,false,permissionArray);
    }
    /**
     * @param requestCode     request code corresponding to set of permissions that would be returned onGranted() or onDenied()
     * @param permissionArray set of permissions needed for the feature
     */



    public void getPermission(int requestCode, @RationalMessageStatus int rationalOperationRequired,boolean showRetryAlertOnFirstDenial,final String... permissionArray) {


        // Android code Build is lower than Marshmallow no need to check for permissions

        this.showRetryAlertOnFirstDenial = showRetryAlertOnFirstDenial;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if(permissionListener!=null)permissionListener.permissionGranted(requestCode);

            return;
        }


        //creates an arrayList of all the permissions that need to be  asked for
        ArrayList<String> permissionsToAsk = new ArrayList<>();
        for (String permission : permissionArray) {

            if (!isGranted(permission,activity)) {
                permissionsToAsk.add(permission);
            }

        }

        //if all permissions are granted already calls the permissionsGrantedMethod
        if (permissionsToAsk.size() < 1) {
            if(permissionListener!=null)permissionListener.permissionGranted(requestCode);

            return;
        }

        permissionsInitiated = permissionArray;
        requestCodeInitiated = requestCode;


        //this loop checks for any rationalPermission and stops the process un till the user reacts to the first rational permission if found
        //if granted the process continues again from onRequestPermissionResult() if not granted we wait for user's further input
        for (final String permission : permissionsToAsk) {



            if (shouldShowRationalPermission(permission)) {
                if(rationalOperationRequired==SKIP_RATIONAL_REQUEST){
                    if(permissionListener!=null)permissionListener.onRationalRequestIntercepted();
                    return;
                }

                if(rationalOperationRequired==SKIP_RATIONAL_MESSAGE){
                    requestPermissions(new String[]{permission}, REQUEST_CODE_RATIONAL);
                    return;

                }

                getRationalSnackBar(getRationalMessage(permission)).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermissions(new String[]{permission}, REQUEST_CODE_RATIONAL);

                    }
                }).show();

                return;
            }


        }
        if(permissionsToAsk.contains(Manifest.permission.READ_CONTACTS)){
        	DialogPopup.alertPopupTwoButtonsWithListeners(activity,
					activity.getString(R.string.app_requires_read_contacts_permission_title),
					activity.getString(R.string.app_requires_read_contacts_permission_message),
					activity.getString(R.string.ok),
					activity.getString(R.string.cancel),
					v->{requestPermissions(permissionsToAsk.toArray(new String[permissionsToAsk.size()]), REQUEST_CODE);},
					v->{}, false, true);
        }
       else if(permissionsToAsk.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)||permissionsToAsk.contains(Manifest.permission.ACCESS_COARSE_LOCATION)||permissionsToAsk.contains(Manifest.permission.ACCESS_FINE_LOCATION)){
            DialogPopup.alertPopupWithListener(activity, activity.getString(R.string.perm_location,activity.getString(R.string.app_name)), activity.getString(R.string.perm_location_rational), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions(permissionsToAsk.toArray(new String[permissionsToAsk.size()]), REQUEST_CODE);
                }
            });
        }

        //At activity point if shouldAsk is true there is no rational Permission that exists and  No explanation needed, we can request for the permissions.
        else {
            requestPermissions(permissionsToAsk.toArray(new String[permissionsToAsk.size()]), REQUEST_CODE);
        }

    }



    private void requestPermissions(String[] strings, int requestCode) {

        if (fragment!=null) {
            fragment.requestPermissions(strings, requestCode);
        } else {
            ActivityCompat.requestPermissions(activity, strings, requestCode);
        }
    }

    private boolean shouldShowRationalPermission(String permission) {
        if (fragment!=null) {
            return fragment.shouldShowRequestPermissionRationale(permission);
        } else {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

    }





    /**
     * Determine whether <em>you</em> have been granted a particular permission.
     *
     * @param permission The name of the permission being checked.
     *
     * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
     * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
     *
     * @see android.content.pm.PackageManager#checkPermission(String, String)
     */    public static boolean isGranted(String permission,Context activity) {

        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param permission permission to check
     * @return returns human readable format of the permission
     */

    private CharSequence getPermissionLabel(String permission) {
        try {

            PermissionInfo permissionInfo = activity.getPackageManager().getPermissionInfo(permission, 0);
            return permissionInfo.loadLabel(activity.getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Shows a snackBar to inform the user that he needs to go to settings to enable permission
     *
     * @param message The message that needs to be shown to the user for the corresponding permission
     */
    public void showPermissionDenied(String message) {

        if (snackBarPermissionDenied == null) {
            view.setVisibility(View.VISIBLE);
            snackBarPermissionDenied = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
            snackBarPermissionDenied.setActionTextColor(ContextCompat.getColor(activity, R.color.themeColor));
            ((TextView) snackBarPermissionDenied.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setMaxLines(5);

            snackBarPermissionDenied.setAction(activity.getString(R.string.grant), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSettingsScreen(activity);
                }
            });
        }


        snackBarPermissionDenied.setText(message);
        snackBarPermissionDenied.show();
    }

    /**
     * @param message Message that needs to be shown to user to inform why we are using the feature
     * @return returns SnackBar
     */
    private Snackbar getRationalSnackBar(String message) {

        if (snackBarRational == null) {
            snackBarRational = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
            snackBarRational.setActionTextColor(ContextCompat.getColor(activity, R.color.themeColor));
            ((TextView) snackBarRational.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setMaxLines(5);

        }

        snackBarRational.setText(message);
        return snackBarRational;


    }

    public interface PermissionListener {
        /**
         * @param requestCode requestCode used to ask for specific set of permissions
         *                    activity method is called if all the permissions have been granted
         */

        void permissionGranted(int requestCode);

        /**
         * @param requestCode requestCode used to ask for specific set of permissions
         *                    activity method is called if any of the permission has been denied
         * @param neverAsk
         */
        boolean permissionDenied(int requestCode, boolean neverAsk);

        void onRationalRequestIntercepted();



    }


    public static void openSettingsScreen(Activity activity){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public void dismissSnackbars(){
        if(snackBarPermissionDenied != null){
            snackBarPermissionDenied.dismiss();
        }
        if(snackBarRational != null){
            snackBarRational.dismiss();
        }
    }

    public Snackbar getDisplayedSnackbar(){
        if(snackBarPermissionDenied != null){
            return snackBarPermissionDenied;
        }
        return null;
    }
}
