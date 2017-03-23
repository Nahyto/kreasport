package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.app.Activity;
import android.content.Context;
import android.content.res.*;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;

/**
 * Created by Christopher Caroni on 17/03/2017.
 */

public class Utils {

    public static void addBalisesToOverlay(ArrayList<OverlayItem> items,String titre, String description, double longitude, double latitude){
        items.add(new OverlayItem(titre, description, new GeoPoint(longitude, latitude)));
    }

    public static void addBalises(ArrayList<OverlayItem> items, ItemizedOverlayWithFocus mMyLocationOverlay, MapView mMapView, RotationGestureOverlay mRotationGestureOverlay){
			/* OnTapListener for the Markers, shows a simple Toast. */
        mMyLocationOverlay.setFocusItemsOnTap(true);
        mMyLocationOverlay.setFocusedItem(0);
        //https://github.com/osmdroid/osmdroid/issues/317
        //you can override the drawing characteristics with this
        mMyLocationOverlay.setMarkerBackgroundColor(Color.BLUE);
        mMyLocationOverlay.setMarkerTitleForegroundColor(Color.WHITE);
        mMyLocationOverlay.setMarkerDescriptionForegroundColor(Color.WHITE);
        mMyLocationOverlay.setDescriptionBoxPadding(15);

        mMapView.getOverlays().add(mMyLocationOverlay);

        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(false);
        mMapView.getOverlays().add(mRotationGestureOverlay);
    }

    @SuppressWarnings({"ResourceType"})
    public static void goThroughOptions(final Context context, MapView mMapView, MapOptions mMapOptions) {
        if (mMapOptions != null) {
            if (mMapOptions.isEnableLocationOverlay()) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
                MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
                mLocationOverlay.enableMyLocation();
                mMapView.getOverlays().add(mLocationOverlay);
            }
            if (mMapOptions.isEnableCompass()) {
                CompassOverlay mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mMapView);
                mCompassOverlay.enableCompass();
                mMapView.getOverlays().add(mCompassOverlay);
            }
            if (mMapOptions.isEnableMultiTouchControls()) {
                mMapView.setMultiTouchControls(true);
            }
            if (mMapOptions.isEnableRotationGesture()) {
                RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
                mRotationGestureOverlay.setEnabled(true);
                mMapView.getOverlays().add(mRotationGestureOverlay);
            }
            if (mMapOptions.isEnableScaleOverlay()) {
                ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mMapView);
                mScaleBarOverlay.setCentred(true);
                //play around with these values to get the location on screen in the right place for your application
                mScaleBarOverlay.setScaleBarOffset(100, 10);
                mMapView.getOverlays().add(mScaleBarOverlay);
            }
        }
    }
    public static int getPixelsFromDIP(Activity activity, int padding_in_dp) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }
    /**
     * @param boundingBox
     * @return a new BoundingBox reduced by offsets.
     */
    public static BoundingBox getBoundingBoxWithOffset(BoundingBox boundingBox, double sideAndTopOffsetPercentage, double bottomOffsetPercentage) {
        double newLngWest = boundingBox.getLonWest() + (boundingBox.getLongitudeSpan() * sideAndTopOffsetPercentage);
        double newLngEast = boundingBox.getLonEast() - (boundingBox.getLongitudeSpan() * sideAndTopOffsetPercentage);

        double newLatSouth = boundingBox.getLatSouth() - (boundingBox.getLatitudeSpan() * bottomOffsetPercentage);
        double newLatNorth = boundingBox.getLatNorth() + (boundingBox.getLatitudeSpan() * sideAndTopOffsetPercentage);

        return new BoundingBox(newLatNorth, newLngEast, newLatSouth, newLngWest);
    }


    public static double getDownloadSizeForBoundingBox(BoundingBox realSize, MapView mMapView) {
        CacheManager cacheManager = new CacheManager(mMapView, null);

        int nTiles = cacheManager.possibleTilesInArea(realSize, mMapView.getZoomLevel(), mMapView.getMaxZoomLevel());
        return 0.001 * (Constants.TILE_KB_SIZE * nTiles);
    }


}
