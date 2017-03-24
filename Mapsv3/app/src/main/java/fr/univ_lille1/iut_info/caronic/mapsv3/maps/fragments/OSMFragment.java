package fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomItemizedIconOverlay;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomMapView;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Parcours;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.YourReceiver;

import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.ACTION_PROXIMITY_ALERT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.LOCATION_IUT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_DISTANCECHANGE_FOR_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_TIME_BETWEEN_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.addDummyBalisesToList;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getOverlayFromArguments;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OSMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OSMFragment extends Fragment {

    private static final String LOG = OSMFragment.class.getSimpleName();


    protected static final String KEY_ANIMATED_TO_SAVED_LOCATION = "mapsv3.osm_frag.animated_to_saved_location";
    protected static final String KEY_DEFAULT_POINT = "mapsv3.osm_frag.initial_point";
    protected static final String KEY_SAVED_LOCATION = "mapsv3.osm_frag.saved_location";
    protected static final String KEY_ZOOM = "mapsv3.osm_frag.zoom";
    protected static final String KEY_MAP_OPTIONS = "mapsv3.osm_frag.map_options";

    public static final String KEY_PARCOURS = "mapsv3.osm_frag.parcours";

    private static GeoPoint defaultPoint;
    private static GeoPoint savedPoint;


    private ItemizedOverlayWithFocus<OverlayItem> mParcoursOverlay;
    protected int zoom;
    private ArrayList<Parcours> listeParcours;

    private CustomMapView mMapView;
    private MapOptions mMapOptions;
    private BottomSheetBehavior<View> mBottomSheetBehavior2;
    private Button mButton2;
    private boolean animatedToSavedLocation;

    private YourReceiver receiver;


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
    public static OSMFragment newInstance(GeoPoint defaultPoint, int zoom, MapOptions mMapOptions) {
        OSMFragment fragment = new OSMFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_ZOOM, zoom);
        args.putSerializable(KEY_DEFAULT_POINT, defaultPoint);
        args.putSerializable(KEY_MAP_OPTIONS, mMapOptions);
        args.putBoolean(KEY_ANIMATED_TO_SAVED_LOCATION, false);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    @SuppressWarnings({"ResourceType"})
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.osm_fragment, container, false);

        setupBottomSheet(fragmentView);

        setupMapView(fragmentView);

        return fragmentView;
    }

    /**
     * Creates map and adds it to relativelayout in fragmentView
     *
     * @param fragmentView
     */
    private void setupMapView(View fragmentView) {
        // needs to be called before the MapView is created
        Configuration.getInstance().setMapViewHardwareAccelerated(true);

        restoreArguments();

        mMapView = new CustomMapView(getActivity(), mMapOptions);

        // set default location to the one specified on newInstance. Will still animate/move to last saved location after.
        // This is in case there is a delay in acquiring current/last position
        GeoPoint point = new GeoPoint(defaultPoint);
        mMapView.getController().setCenter(point);

        initParcoursOverlay();
        basicMapSetup();
        restoreSavedPosition();

        setLocationListener();

        RelativeLayout relativeLayout = (RelativeLayout) fragmentView.findViewById(R.id.osm_frag_top_relative_layout);
        relativeLayout.addView(mMapView);
    }

    private void setupBottomSheet(View parentView) {

        final View bottomSheet2 = parentView.findViewById(R.id.layout_bottom_sheet);
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);

        mButton2 = (Button) parentView.findViewById(R.id.button_2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        mBottomSheetBehavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mButton2.setText("Peek");
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mButton2.setText("Hide");
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mButton2.setText("Show");
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });
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

        mMapView.setMinZoomLevel(2);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        saveCurrentPosition();
        super.onSaveInstanceState(outState);
    }

    /**
     * Restores intialPoint, mMapOptions and zoom from arguments
     */
    private void restoreArguments() {
        defaultPoint = (GeoPoint) getArguments().getSerializable(KEY_DEFAULT_POINT);

        String initalPointJson = getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .getString(KEY_SAVED_LOCATION, "");
        if (!initalPointJson.equals("")) {
            savedPoint = new Gson().fromJson(initalPointJson, GeoPoint.class);
        }


        mMapOptions = (MapOptions) getArguments().getSerializable(KEY_MAP_OPTIONS);
        zoom = getArguments().getInt(KEY_ZOOM);
        animatedToSavedLocation = getArguments().getBoolean(KEY_ANIMATED_TO_SAVED_LOCATION);
    }

    /**
     * Saves the current position the the MapView to the arguments of the fragment.
     */
    private void saveCurrentPosition() {
        GeoPoint currentPoint = (GeoPoint) mMapView.getMapCenter();
        zoom = mMapView.getZoomLevel();

        getArguments().putInt(KEY_ZOOM, zoom);
        getArguments().putBoolean(KEY_ANIMATED_TO_SAVED_LOCATION, animatedToSavedLocation);
        getArguments().putSerializable(KEY_DEFAULT_POINT, defaultPoint);

        String currentPointJson = new Gson().toJson(currentPoint, GeoPoint.class);
        getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SAVED_LOCATION, currentPointJson)
                .apply();
        Log.d(LOG, "saving current position: " + currentPointJson);
    }

    private void restoreSavedPosition() {
        final IMapController mapController = mMapView.getController();
        mapController.setZoom(zoom);

        if (!animatedToSavedLocation && savedPoint != null) {
            GeoPoint point = new GeoPoint(savedPoint);
            mapController.animateTo(point);
            animatedToSavedLocation = true;
            Log.d(LOG, "restoring saved point and animating to " + point.toString());
        } else {
            Log.d(LOG, "no saved point or already animated to it");
        }
    }

    /**
     * Restores the old position in the MapView from the arguments of the fragment.
     */
    @SuppressWarnings({"ResourceType"})
    // we can ignore because we only launch this fragment after checking permissions.
    private void setLocationListener() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOG, "location updated: " + location.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(LOG, "Provider " + provider + " enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(LOG, "Provider " + provider + " disabled");
            }
        };

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                locationListener
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATE,
                MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                locationListener
        );


        Intent intent = new Intent(ACTION_PROXIMITY_ALERT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        locationManager.addProximityAlert(LOCATION_IUT.getLatitude(), LOCATION_IUT.getLatitude(), 1000, -1, pendingIntent);

        receiver = new YourReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_PROXIMITY_ALERT);

        getActivity().registerReceiver(receiver, intentFilter);

    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        saveCurrentPosition();
        super.onStop();
    }

    public void initParcoursOverlay() {
        final ArrayList<OverlayItem> firstBalisesInParcoursList = new ArrayList<OverlayItem>();

        addDummyBalisesToList(firstBalisesInParcoursList);

        List<OverlayItem> parcoursFromJson = getOverlayFromArguments(getArguments());
        if (parcoursFromJson != null) {
            firstBalisesInParcoursList.addAll(parcoursFromJson);
        }

        ItemizedOverlayWithFocus.OnItemGestureListener listener = CustomItemizedIconOverlay.getListener(getContext());
        mParcoursOverlay = new CustomItemizedIconOverlay(getContext(), firstBalisesInParcoursList, listener);

        mParcoursOverlay.setFocusItemsOnTap(true);

        mParcoursOverlay.setMarkerBackgroundColor(Color.BLUE);
        mParcoursOverlay.setMarkerTitleForegroundColor(Color.WHITE);
        mParcoursOverlay.setMarkerDescriptionForegroundColor(Color.WHITE);
        mParcoursOverlay.setDescriptionBoxPadding(15);
        mMapView.getOverlays().add(mParcoursOverlay);
    }
}
