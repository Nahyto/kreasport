package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomOverlayItem;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Parcours;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

import static android.R.id.primary;
import static fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment.KEY_PARCOURS;

/**
 * Created by Christopher Caroni on 17/03/2017.
 */

public class Utils {

    private static final String LOG = Utils.class.getSimpleName();

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

    /**
     * @return a list of {@link OverlayItem} if this fragment's arguments contain any
     */
    public static List<CustomOverlayItem> getOverlayFromPreferences(SharedPreferences preferences) {
        String parcoursStringArray = preferences.getString(KEY_PARCOURS, "");
        if (!parcoursStringArray.equals("")) {
            return jsonArrayToOverlayItemList(parcoursStringArray);
        }
        return null;
    }

    public static List<CustomOverlayItem> jsonArrayToOverlayItemList(String parcoursStringArray) {
        JsonParser parser = new JsonParser();
        List<CustomOverlayItem> overlayItemsFromArgsList = new ArrayList<>();

        JsonArray parcoursJsonArray = parser
                .parse(parcoursStringArray)
                .getAsJsonArray();


        for (JsonElement parcoursJson : parcoursJsonArray) {
            Log.d(LOG, "downloaded parcours: " + parcoursJson.toString());

            Parcours parcours = new Gson().fromJson(parcoursJson, Parcours.class);
            if (parcours != null && parcours.getBaliseList() != null && parcours.getBaliseList().size() > 0) {

                GeoPoint point = parcours
                        .getPrimaryBalise()
                        .toGeoPoint();
                CustomOverlayItem item = new CustomOverlayItem(parcours.getTitle(), parcours.getDescription(), point, parcours.getPrimaryBalise().getId(), parcours.getId());

                overlayItemsFromArgsList.add(item);
                Log.d(LOG, "for parcours " + parcours.getId() + " added primary balise: " + point);
            } else {
                Log.d(LOG, "no balises found in that parcours");
            }
        }
        return overlayItemsFromArgsList;
    }


    public static void addDummyBalisesToList(List<CustomOverlayItem> items) {
        items.add(new CustomOverlayItem("IUT A : Balise 1", "Début de l'aventure !", new GeoPoint(50.6137196, 3.1367387), 0, -1));
        items.add(new CustomOverlayItem("IUT A : Balise 2", "Bravo l'aventure continue", new GeoPoint(50.613014, 3.138510), 0, -1)); // Berlin
        items.add(new CustomOverlayItem(
                "Washington",
                "This SampleDescription is a pretty long one. Almost as long as a the great wall in china.", new GeoPoint(38895000, -77036667), 0, -1)); // Washington
        items.add(new CustomOverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300, -122419200), 0, -1)); // San Francisco
    }
}
