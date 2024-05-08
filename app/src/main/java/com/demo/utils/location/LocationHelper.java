package com.demo.utils.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.demo.utils.LogHelper;
import com.demo.utils.Util;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.demo.R;

/**
 * protected void onActivityResult(int requestCode, int resultCode, Intent data)
 * public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 * protected void onPause()
 * protected void onResume()`
 */
public class LocationHelper {
    private LocationHelperCallback helperCallback;
    private boolean showProgressDialog;
    private boolean isLocationCompulsory;
    private FusedLocationProviderClient mFusedLocationClient;
    private String TAG = this.getClass().getSimpleName();
    public static int REQUEST_CHECK_SETTINGS = 173;
    private final int PERMISSION_REQUEST_CODE = 175;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mLocation = null;
    private boolean isAllSettingsSatisfied;
    private ProgressDialog progressDialog;

    private Context mContext;
    private Activity activity;
    private String denyRequestDescription;
    private boolean requireContinuousLocationUpdate; //decide whether user want only one time location or frequently location.

    public LocationHelper(Activity activity, LocationHelperCallback helperCallback, boolean showProgressDialog) {
        this(activity, helperCallback, showProgressDialog, false); //by default set location update to false.
    }

    public LocationHelper(Activity activity, LocationHelperCallback helperCallback, boolean showProgressDialog, boolean requireContinuousLocationUpdate) {
        this(activity, helperCallback, showProgressDialog, requireContinuousLocationUpdate, false); //by default set location update to false.
    }

    public LocationHelper(Activity activity, LocationHelperCallback helperCallback, boolean showProgressDialog, boolean requireContinuousLocationUpdate, boolean isLocationCompulsory) {
        this.activity = activity;
        mContext = activity;
        this.helperCallback = helperCallback;
        this.showProgressDialog = showProgressDialog;
        this.isLocationCompulsory = isLocationCompulsory;
        progressDialog = Util.INSTANCE.getProgressDialog(mContext);
        denyRequestDescription = mContext.getString(R.string.without_location_you_cant_get_request);
        this.requireContinuousLocationUpdate = requireContinuousLocationUpdate;
        processForLocationFetching();
    }

    public void setDenyRequestDescription(String denyRequestDescription) {
        this.denyRequestDescription = denyRequestDescription;
    }

    private void processForLocationFetching() {
        if (checkPermission()) {
            createLocationRequest();
        } else {
            requestPermission();
        }
    }

    private void getCurrentLocation() {
        if (checkPermission()) {
            if (mFusedLocationClient == null) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            }

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            LogHelper.INSTANCE.e(TAG, "onSuccess==" + location);
                            mLocation = location;
                            if (mLocation == null || requireContinuousLocationUpdate) {
                                //if location is null or user want continuous location update then start for receiving updates.
                                if (mLocation != null && helperCallback != null) {
                                    //If we get last known location then give to activity.Don't wait for location update.
                                    helperCallback.onLocationFound(mLocation);
                                }
                                setUpCallback();
                                startLocationUpdates();
                            } else {
                                dismissDialog();
                                if (helperCallback != null) {
                                    helperCallback.onLocationFound(mLocation);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            LogHelper.INSTANCE.e(TAG, "onError=" + e.getMessage());
                            setUpCallback();
                            startLocationUpdates();
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void createLocationRequest() {
        if (mLocationRequest != null) {
            /*Log.e(TAG, "mLocationRequest is not_null simply return..");
            return;*/
            mLocationRequest = null;
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);   // 5 sec
        mLocationRequest.setFastestInterval(2000);  // 2 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                isAllSettingsSatisfied = true;
                getCurrentLocation();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                } else {
                    showAlertDialog(e.getMessage());
                }
            }
        });
    }

    private void setUpCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                int i = 0;
                for (Location location : locationResult.getLocations()) {
                    LogHelper.INSTANCE.e(TAG, i + " " + location);
                    i++;
                    mLocation = location;
                    if (mLocation != null) {
                        break;
                    }
                }
                if (mLocation != null) {
                    dismissDialog();
                    if (!requireContinuousLocationUpdate) {
                        //If user doesn't want only one time location update then remove this update listener. otherwise you will receiving location update.
                        stopLocationUpdates();
                    }
                    if (helperCallback != null) {
                        helperCallback.onLocationFound(mLocation);
                    }
                }
            }
        };
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.INSTANCE.e(TAG, "resultCode=" + resultCode + " requestCode=" + requestCode + " data=" + data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHECK_SETTINGS) {
            isAllSettingsSatisfied = true;
            getCurrentLocation();
        } else {
            isAllSettingsSatisfied = false;
           // showAlertDialog(null);
            helperCallback.onDeclineProcessForLocation();
        }
    }

    public void onResume() {
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (mLocationRequest != null && mLocationCallback != null && isAllSettingsSatisfied) {
            if (checkPermission()) {
                showDialog(mContext.getString(R.string.fetching_location));
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback,
                        null /* Looper */);
            }
        }
    }

    public void onPause() {
        if (mLocationCallback != null && mFusedLocationClient != null) {
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        if (!requireContinuousLocationUpdate) {
            mLocationCallback = null;
            mFusedLocationClient = null;
            mLocationRequest = null;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            displayToast("GPS permission allows us to access location data. Please allow in App Settings for additional functionality.");
//        } else
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @SuppressWarnings("unused")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                } else {
                    showAlertDialog(null);
                }
                break;
        }
    }

    private void dismissDialog() {
        if (showProgressDialog && progressDialog != null) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDialog(String message) {
        if (showProgressDialog && progressDialog != null && mLocation == null) {
/*

            show progress dialog only when user has set variable true as he/she want's to show it.
            before showing we check that progress dialog should not null
            And last condition is for if mLocation is null then show dialog,suppose we are already getting last location but due to
            user come to background and again come then form onResume() we are not showing dialog,because we already get the location,we are only getting updates.So do this without showing dialog.

*/
            try {
                progressDialog.setMessage(message);
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface LocationHelperCallback {
        void onLocationFound(Location location);

        void onDeclineProcessForLocation();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(message == null ? denyRequestDescription : message)
                .setPositiveButton(mContext.getString(R.string.retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processForLocationFetching();
                    }
                });

        if (!isLocationCompulsory) {
            builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismissDialog();
                    if (helperCallback != null) {
                        helperCallback.onDeclineProcessForLocation();
                    }
                }
            });
        }

        builder.setCancelable(false).show();
    }
}
