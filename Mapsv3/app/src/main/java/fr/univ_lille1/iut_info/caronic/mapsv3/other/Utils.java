package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Balise;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomOverlayItem;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Parcours;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.ParcoursList;

import java.util.List;

import static fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment.KEY_PARCOURS;
import static fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment.KEY_PARCOURS_LIST;

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
     * Gets a {@link ParcoursList} from SharedPreferences
     * <br>Uses {@link fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment#KEY_PARCOURS_LIST}
     *
     * @param preferences
     * @return
     */
    public static ParcoursList restoreParcoursListFromOSM(SharedPreferences preferences, String KeyToUse) {
        String preSavedParcoursListJson = preferences.getString(KEY_PARCOURS_LIST, "");
        if (!preSavedParcoursListJson.equals("")) {
            ParcoursList list = new Gson().fromJson(preSavedParcoursListJson, ParcoursList.class);
            return list;
        }
        return null;
    }

    /**
     * Gets a list of {@link Parcours} from SharedPreferences.
     * <br>Uses {@link fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment#KEY_PARCOURS}
     *
     * @param preferences
     * @param parcoursIdToFocus
     * @return
     */
    public static List<Parcours> getParcoursFromDownload(SharedPreferences preferences, int parcoursIdToFocus) {
        String parcoursStringArray = preferences.getString(KEY_PARCOURS, "");
        if (!parcoursStringArray.equals("")) {
            return jsonArrayToParcoursList(parcoursStringArray, parcoursIdToFocus);
        }
        return null;
    }

    /**
     * Converts the json array to a list of {@link Parcours}
     *
     * @param parcoursStringArray
     * @param parcoursIdToFocus
     * @return
     */
    public static List<Parcours> jsonArrayToParcoursList(String parcoursStringArray, int parcoursIdToFocus) {
        JsonParser parser = new JsonParser();
        List<Parcours> parcoursList = new ArrayList<>();

        JsonArray parcoursJsonArray = parser
                .parse(parcoursStringArray)
                .getAsJsonArray();


        for (JsonElement parcoursJson : parcoursJsonArray) {
            Log.d(LOG, "downloaded parcours: " + parcoursJson.toString());

            Parcours parcours = new Gson().fromJson(parcoursJson, Parcours.class);

            if (parcours != null && parcours.getBaliseList() != null && parcours.getBaliseList().size() > 0) {
                if (parcoursIdToFocus > -10 && parcoursIdToFocus < 0) {
                    Log.d(LOG, "converted parcours: " + parcours.toString());
                    parcoursList.add(parcours);
                } else if ((parcoursIdToFocus >= 0 || parcoursIdToFocus <= -10) && parcours.getId() == parcoursIdToFocus) {
                    Log.d(LOG, "converted parcours: " + parcours.toString());
                    parcoursList.add(parcours);
                }
            }
        }
        return parcoursList;
    }

    /**
     * Converts the {@link Parcours} list to a list of {@link CustomOverlayItem}.
     *
     * @param parcoursList
     * @return
     */
    public static List<CustomOverlayItem> getOverlayFromParours(Activity activity, ParcoursList parcoursList, boolean parcoursStarted) {
        Log.d(LOG, "converting parcours to overlay items");

        List<CustomOverlayItem> overlayItemsList = new ArrayList<>();

        for (Parcours parcours : parcoursList.getParcoursList()) {
            GeoPoint point = parcours
                    .getPrimaryBalise()
                    .toGeoPoint();
            CustomOverlayItem item = new CustomOverlayItem(parcours.getTitle(), parcours.getDescription(), point, parcours.getId(), parcours.getPrimaryBalise().getId());
            if ( parcours.getState() == Parcours.STATE_IS_FINISHED) {
                item.setMarker(ContextCompat.getDrawable(activity, R.drawable.ic_beenhere_blue_500_36dp));
                Log.d(LOG, "for primary balise, parcours is not new, setting been here");
            } else if (parcours.getState() == Parcours.STATE_NOT_FINISHED) {
                item.setMarker(ContextCompat.getDrawable(activity, R.drawable.ic_directions_run_blue_500_36dp));
                Log.d(LOG, "for primary balise, parcours is not finished, setting not finished");
            } else {
                item.setMarker(ContextCompat.getDrawable(activity, R.drawable.ic_place_blue_500_36dp));
                Log.d(LOG, "for primary balisen parcours is not started, setting as parcours icons");
            }

            overlayItemsList.add(item);
            Log.d(LOG, "for parcours " + parcours.getId() + " added primary balise: " + point);

            if (parcoursStarted) {
                Log.d(LOG, "will now add up to targeted balise: " + parcours.getBaliseToTargetIndex());
                for (int i = 1; i <= parcours.getBaliseToTargetIndex(); i++) {
                    if (i < parcours.getBaliseList().size()) {
                        Balise balise = parcours.getBaliseList().get(i);
                        point = balise.toGeoPoint();
                        item = new CustomOverlayItem(balise.getTitle(), balise.getDescription(), point, parcours.getId(), balise.getId());

                        if (parcours.getState() == Parcours.STATE_IS_FINISHED) {
                            item.setMarker(ContextCompat.getDrawable(activity, R.drawable.ic_beenhere_blue_500_36dp));
                            Log.d(LOG, "for item at index: " + i + " setting done icon");
                        } else {
                            if (i != parcours.getBaliseToTargetIndex()) {
                                item.setMarker(ContextCompat.getDrawable(activity, R.drawable.ic_beenhere_blue_500_36dp));
                                Log.d(LOG, "for item at index: " + i + " setting done icon");
                            }
                        }

                        overlayItemsList.add(item);
                        Log.d(LOG, "for parcours " + parcours.getId() + " added target balise: " + balise.getId() + ", " + balise.getTitle());
                    }
                }
            }
        }
        return overlayItemsList;
    }


    public static List<Parcours> getDummyParcours(int focusOnParcours) {
        List<Parcours> parcoursList = new ArrayList<>();


        /* PREMIER */
        Parcours parcours = new Parcours("Parcours IUT", "Premier parcours", -10);

        Balise balise = new Balise("IUT A : Balise 1", "Début de l'aventure !", new GeoPoint(50.6137196, 3.1367387), -10, 0);
        parcours.addBalise(balise);

        balise = new Balise("IUT A : Balise 2", "Bravo l'aventure continue", new GeoPoint(50.612464, 3.139467), -10, 1);
        parcours.addBalise(balise);

        balise = new Balise("IUT A : Balise 3", "La fin!", new GeoPoint(50.612709, 3.141881), -10, 2);
        parcours.addBalise(balise);

        parcoursList.add(parcours);

        /* SECOND */
        parcours = new Parcours("Washington", "This SampleDescription is a pretty long one. Almost as long as a the great wall in china.", -11);

        balise = new Balise("Washington: Balise 1", "Début de l'aventure !", new GeoPoint(38895000, -77036667), -11, 0);
        parcours.addBalise(balise);

        parcoursList.add(parcours);

        /* TROISIEME */
        parcours = new Parcours("San Francisco", "SampleDescription", -12);

        balise = new Balise("San Francisco 1", "Début de l'aventure !", new GeoPoint(37779300, -122419200), -12, 0);
        parcours.addBalise(balise);

        parcoursList.add(parcours);

        if (focusOnParcours <= -10 || focusOnParcours >= 0) {
            for (Parcours parcoursIdCheck : parcoursList) {
                if (parcoursIdCheck.getId() == focusOnParcours) {
                    List<Parcours> oneList = new ArrayList<>(1);
                    oneList.add(parcoursIdCheck);
                    return oneList;
                }
            }
        }


        return parcoursList;
    }
}
