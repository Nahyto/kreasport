package fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Balise;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomOverlayWithFocus;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Parcours;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.YourReceiver;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OSMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OSMFragment extends Fragment {

    private static final String LOG = OSMFragment.class.getSimpleName();

    protected static final String KEY_FIRST_RUN = "mapsv3.osm_frag.first_run";

    protected static final String KEY_INITIAL_POINT = "mapsv3.osm_frag.initial_point";
    protected static final String KEY_ZOOM = "mapsv3.osm_frag.zoom";
    protected static final String KEY_MAP_OPTIONS = "mapsv3.osm_frag.map_options";

    public static final String KEY_PARCOURS = "mapsv3.osm_frag.parcours";

    private static GeoPoint initialPoint;
    private ItemizedOverlayWithFocus<OverlayItem> mParcoursOverlay;
    protected int zoom;
    private ArrayList<Parcours> listeParcours;

    private MapView mMapView;
    private MapOptions mMapOptions;


    public OSMFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param zoom Zoom.
     * @return A new instance of fragment osm_fragment.
     */
    public static OSMFragment newInstance(GeoPoint initialPoint, int zoom, MapOptions mMapOptions) {
        OSMFragment fragment = new OSMFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_ZOOM, zoom);
        args.putBoolean(KEY_FIRST_RUN, true);
        args.putSerializable(KEY_INITIAL_POINT, initialPoint);
        args.putSerializable(KEY_MAP_OPTIONS, mMapOptions);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    @SuppressWarnings({"ResourceType"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // needs to be called before the MapView is created
        Configuration.getInstance().setMapViewHardwareAccelerated(true);

        restoreArguments();

        mMapView = new MapView(inflater.getContext());

        // set default location to the one specified on newInstance. Will still animate/move to last saved location after.
        // This is in case there is a delay in acquiring current/last position
        mMapView.getController().setCenter(initialPoint);
        initParcoursOverlay();
        basicMapSetup();

        Utils.goThroughOptions(getActivity(), mMapView, mMapOptions);
        restorePosition();

        return mMapView;
    }

    /**
     * Basic map settings such as hw acceleration, copyright overlay...
     */
    private void basicMapSetup() {
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getOverlays().add(new CopyrightOverlay(getContext()));

        TilesOverlay x = mMapView.getOverlayManager().getTilesOverlay();
        x.setOvershootTileCache(x.getOvershootTileCache() * 2);

        mMapView.setTilesScaledToDpi(true);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        saveCurrentPosition();
        super.onSaveInstanceState(outState);
    }

    private void restoreArguments() {
        initialPoint = (GeoPoint) getArguments().getSerializable(KEY_INITIAL_POINT);
        mMapOptions = (MapOptions) getArguments().getSerializable(KEY_MAP_OPTIONS);
        zoom = getArguments().getInt(KEY_ZOOM);
    }

    /**
     * Saves the current position the the MapView to the arguments of the fragment.
     */
    private void saveCurrentPosition() {
        GeoPoint currentPoint = (GeoPoint) mMapView.getMapCenter();
        zoom = mMapView.getZoomLevel();

        getArguments().putSerializable(KEY_INITIAL_POINT, currentPoint);
        getArguments().putInt(KEY_ZOOM, zoom);
        getArguments().putBoolean(KEY_FIRST_RUN, false);
    }

    /**
     * Restores the old position in the MapView from the arguments of the fragment.
     */
    @SuppressWarnings({"ResourceType"})
    // we can ignore because we only launch this fragment after checking permissions.
    private void restorePosition() {

        final IMapController mapController = mMapView.getController();
        mapController.setZoom(zoom);

        if (getArguments().getBoolean(KEY_FIRST_RUN)) {
            Toast.makeText(getContext(), R.string.osm_fragment_first_run_location_info, Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Intent intent = new Intent(Constants.ACTION_PROXIMITY_ALERT);
            PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, intent, 0);
            locationManager.addProximityAlert(50.6137196,3.1367387,1000,-1,pendingIntent);
            YourReceiver received = new YourReceiver();
            received.onReceive(getContext(),intent);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    GeoPoint currentLocation = new GeoPoint(location);
                    mapController.animateTo(currentLocation);
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
            }, null);
            //for (Parcours p: listeParcours) {
           // }
        } else if (!getArguments().getBoolean(KEY_FIRST_RUN) && initialPoint != null) {
            mapController.animateTo(initialPoint);
        }
    }

    /**
     * ONLY CALL IF ONCREATEVIEW ALREADY CALLED, OTHERWISE USE FRAGMENT ARGUMENTS
     * @param parcoursJsonArray
     */
    public void addMulmtipleParcoursFromJsonArray(String parcoursJsonArray) {

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(parcoursJsonArray);
        JsonArray parcoursArray = element.getAsJsonArray();

        for (JsonElement parcoursJson : parcoursArray) {
            Log.d(LOG, "one parcours = " + parcoursJson.toString());


            Parcours parcours = new Gson().fromJson(parcoursJson, Parcours.class);
            if (parcours != null && parcours.getBaliseList() != null && parcours.getBaliseList().size() > 0) {
                Log.d(LOG, "creating new parcours");
                listeParcours.add(parcours);
                Balise premier = parcours.getBaliseList().get(0);
                GeoPoint point = new GeoPoint(premier.getLatitude(), premier.getLongitude());
                OverlayItem item = new OverlayItem(parcours.getTitle(), parcours.getDescription(), point);
                addBaliseToOverlay(item);

                mMapView.invalidate();
            } else {
                Log.d(LOG, "tried adding parcours but didn't contain any balises");
            }

        }
    }

    private List<OverlayItem> getOverlayFromArguments() {
        String parcoursJsonArray = getArguments().getString(KEY_PARCOURS);
        if (parcoursJsonArray != null) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(parcoursJsonArray);
            JsonArray parcoursArray = element.getAsJsonArray();

            List<OverlayItem> result = new ArrayList<>();

            for (JsonElement parcoursJson : parcoursArray) {
                Log.d(LOG, "one parcours = " + parcoursJson.toString());


                Parcours parcours = new Gson().fromJson(parcoursJson, Parcours.class);
                if (parcours != null && parcours.getBaliseList() != null && parcours.getBaliseList().size() > 0) {
                    Log.d(LOG, "creating new parcours");

                    Balise premier = parcours.getBaliseList().get(0);
                    GeoPoint point = new GeoPoint(premier.getLatitude(), premier.getLongitude());
                    OverlayItem item = new OverlayItem(parcours.getTitle(), parcours.getDescription(), point);

                    Log.d(LOG, "first balise for parcours = " + point.toString());

                    result.add(item);
                } else {
                    Log.d(LOG, "tried adding parcours but didn't contain any balises");
                }

            }
            return result;
        }
        return null;
    }

    private void addBaliseToOverlay(OverlayItem item) {
        mParcoursOverlay.addItem(item);
        mMapView.invalidate();
    }

    public void initParcoursOverlay() {
        final ArrayList<OverlayItem> parcoursMainBalisesList = new ArrayList<OverlayItem>();

        addDummyBalisesToList(parcoursMainBalisesList);

        List<OverlayItem> parcoursFromJson = getOverlayFromArguments();
        if (parcoursFromJson != null) {
            parcoursMainBalisesList.addAll(parcoursFromJson);
        }

        ItemizedOverlayWithFocus.OnItemGestureListener listener = CustomOverlayWithFocus.getListener(getContext());
        mParcoursOverlay = new CustomOverlayWithFocus(getContext(), parcoursMainBalisesList, listener);

        mParcoursOverlay.setFocusItemsOnTap(true);
        mParcoursOverlay.setFocusedItem(0);

        mParcoursOverlay.setMarkerBackgroundColor(Color.BLUE);
        mParcoursOverlay.setMarkerTitleForegroundColor(Color.WHITE);
        mParcoursOverlay.setMarkerDescriptionForegroundColor(Color.WHITE);
        mParcoursOverlay.setDescriptionBoxPadding(15);
        mMapView.getOverlays().add(mParcoursOverlay);
    }

    private void addDummyBalisesToList(List<OverlayItem> items) {
        items.add(new OverlayItem("IUT A : Balise 1", "Début de l'aventure !", new GeoPoint(50.6137196,3.1367387)));
        items.add(new OverlayItem("IUT A : Balise 2", "Bravo l'aventure continue", new GeoPoint(50.613014, 3.138510))); // Berlin
        items.add(new OverlayItem(
                "Washington",
                "This SampleDescription is a pretty long one. Almost as long as a the great wall in china.", new GeoPoint(38895000, -77036667))); // Washington
        items.add(new OverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300, -122419200))); // San Francisco

    }
}
