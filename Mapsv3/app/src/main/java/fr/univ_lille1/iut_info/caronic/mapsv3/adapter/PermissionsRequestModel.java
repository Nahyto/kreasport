package fr.univ_lille1.iut_info.caronic.mapsv3.adapter;

/**
 * Created by Chris on 22-Mar-17.
 */

public class PermissionsRequestModel {

    private String title;
    private final int PERMISSION_REQUEST_CODE;


    public PermissionsRequestModel(String title, int permission_request_code) {
        this.title = title;
        PERMISSION_REQUEST_CODE = permission_request_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPERMISSION_REQUEST_CODE() {
        return PERMISSION_REQUEST_CODE;
    }
}
