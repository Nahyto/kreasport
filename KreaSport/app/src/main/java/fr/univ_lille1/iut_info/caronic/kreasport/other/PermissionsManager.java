package fr.univ_lille1.iut_info.caronic.kreasport.other;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Master on 29/03/2017.
 */

public class PermissionsManager {

    public static final int WRITE_EXTERNAL_STORAGE = 0;
    public static final int ACCESS_FINE_LOCATION = WRITE_EXTERNAL_STORAGE + 1;


    private Activity activity;

    public PermissionsManager(Activity activity) {
        this.activity = activity;
    }

    public boolean isPermissionGranted(int requestCode) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                int writeGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writeGranted != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            case ACCESS_FINE_LOCATION:
                int fineGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (fineGranted != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    return true;
                }
            default:
                return false;
        }
    }

    public void requestPermission(int requestCode) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE:
                int writeGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writeGranted != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE);
            case ACCESS_FINE_LOCATION:
                int fineGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (fineGranted != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ACCESS_FINE_LOCATION);
            default:
                break;
        }
    }
}
