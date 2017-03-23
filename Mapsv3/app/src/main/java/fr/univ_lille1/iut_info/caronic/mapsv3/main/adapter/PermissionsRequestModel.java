package fr.univ_lille1.iut_info.caronic.mapsv3.main.adapter;

/**
 * Created by Chris on 22-Mar-17.
 */

public class PermissionsRequestModel {

    private String title;
    private final int PERMISSION_REQUEST_CODE;
    private String description;

    public PermissionsRequestModel(String title, int PERMISSION_REQUEST_CODE) {
        this.title = title;
        this.PERMISSION_REQUEST_CODE = PERMISSION_REQUEST_CODE;
        this.description = null;
    }

    public PermissionsRequestModel(int PERMISSION_REQUEST_CODE, String title, String description) {
        this(title, PERMISSION_REQUEST_CODE);
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
