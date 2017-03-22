package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.app.Activity;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

/**
 * Created by Christopher Caroni on 17/03/2017.
 */

public class Utils {

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
