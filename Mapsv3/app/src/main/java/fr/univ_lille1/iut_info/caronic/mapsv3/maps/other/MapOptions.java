package fr.univ_lille1.iut_info.caronic.mapsv3.maps.other;

import java.io.Serializable;

/**
 * Created by Master on 18/03/2017.
 */

public class MapOptions implements Serializable {

    private boolean enableLocationOverlay;
    private boolean enableCompass;
    private boolean enableMultiTouchControls;
    private boolean enableRotationGesture;
    private boolean enableScaleOverlay;

    public MapOptions() {
        this.enableLocationOverlay = false;
        this.enableCompass = false;
        this.enableMultiTouchControls = false;
        this.enableRotationGesture = false;
        this.enableScaleOverlay = false;
    }

    public boolean isEnableMultiTouchControls() {
        return enableMultiTouchControls;
    }

    public MapOptions setEnableMultiTouchControls(boolean enableMultiTouchControls) {
        this.enableMultiTouchControls = enableMultiTouchControls;
        return this;
    }

    public boolean isEnableLocationOverlay() {
        return enableLocationOverlay;
    }

    public MapOptions setEnableLocationOverlay(boolean enableLocationOverlay) {
        this.enableLocationOverlay = enableLocationOverlay;
        return this;
    }

    public boolean isEnableCompass() {
        return enableCompass;
    }

    public MapOptions setEnableCompass(boolean enableCompass) {
        this.enableCompass = enableCompass;
        return this;
    }

    public boolean isEnableRotationGesture() {
        return enableRotationGesture;
    }

    public MapOptions setEnableRotationGesture(boolean enableRotationGesture) {
        this.enableRotationGesture = enableRotationGesture;
        return this;
    }

    public boolean isEnableScaleOverlay() {
        return enableScaleOverlay;
    }

    public MapOptions setEnableScaleOverlay(boolean enableScaleOverlay) {
        this.enableScaleOverlay = enableScaleOverlay;
        return this;
    }


}
