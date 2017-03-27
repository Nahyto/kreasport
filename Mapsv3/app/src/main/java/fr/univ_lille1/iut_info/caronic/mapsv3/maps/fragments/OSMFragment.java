package fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Balise;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomItemizedIconOverlay;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomMapView;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.CustomOverlayItem;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.Parcours;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects.ParcoursList;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.YourReceiver;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.ACTION_PROXIMITY_ALERT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.LOCATION_IUT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_DISTANCECHANGE_FOR_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_TIME_BETWEEN_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getDummyParcours;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getParcoursFromDownload;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getOverlayFromParours;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.restoreParcoursListFromOSM;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OSMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OSMFragment extends Fragment {

    private static final String LOG = OSMFragment.class.getSimpleName();

    private static final String KEY_ON_ITEM_CLICK = "mapsv3.osm_frag.on_item_click";
    private static final String KEY_ON_ITEM_CLICK_INDEX = "mapsv3.osm_frag.on_item_click_index";

    protected static final String KEY_ANIMATED_TO_SAVED_LOCATION = "mapsv3.osm_frag.animated_to_saved_location";
    protected static final String KEY_DEFAULT_POINT = "mapsv3.osm_frag.initial_point";
    protected static final String KEY_SAVED_LOCATION = "mapsv3.osm_frag.saved_location";
    protected static final String KEY_ZOOM = "mapsv3.osm_frag.zoom";
    protected static final String KEY_MAP_OPTIONS = "mapsv3.osm_frag.map_options";
    protected static final String KEY_CURRENT_PARCOURS = "mapsv3.osm_frag.current_parcours";

    /**
     * Used for getting the initial downloaded parcours transferred from {@link fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity}
     */
    public static final String KEY_PARCOURS = "mapsv3.osm_frag.parcours";
    /**
     * Used for saving/getting {@link ParcoursList} from inside this class. Useful for saving time elsapsed
     */
    public static final String KEY_PARCOURS_LIST = "mapsv3.osm_frag.parcours_list";

    private static GeoPoint defaultPoint;
    private static GeoPoint savedPoint;
    protected int zoom;


    private ParcoursList parcoursList;
    private CustomItemizedIconOverlay<CustomOverlayItem> mParcoursOverlay;


    private CustomMapView mMapView;
    private MapOptions mMapOptions;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private boolean animatedToSavedLocation;

    private LocationManager locationManager;

    private YourReceiver receiver;

    private View bottomSheet;
    private FloatingActionButton fab;
    private View bottomSheetMain;
    private View bottomSheetActive;
    private Chronometer chronometer;
    private RatingBar ratingBar;
    private TextView progression;


    private CustomOverlayItem focusedOverlayItem;
    private boolean parcoursStarted;
    private int currentParcoursId;
    private Location currentLocation;


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
     * Creates map and adds it to osm_frag_top_relative_layout in fragmentView
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

        intializeParcoursAndOverlay(true, -1, parcoursStarted);
        basicMapSetup();
        restoreSavedPosition();

        setLocationListener();

        RelativeLayout relativeLayout = (RelativeLayout) fragmentView.findViewById(R.id.osm_frag_top_relative_layout);
        relativeLayout.addView(mMapView);
    }

    /**
     * Sets up the bottom sheet and all views associated (fab...)
     *
     * @param parentView
     */
    private void setupBottomSheet(View parentView) {
        bottomSheet = parentView.findViewById(R.id.bottom_sheet);

        bottomSheetMain = parentView.findViewById(R.id.bottom_sheet_main);
        bottomSheetActive = parentView.findViewById(R.id.bottom_sheet_active);

        fab = (FloatingActionButton) parentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopParcours();
            }
        });

        Button cancel = (Button) parentView.findViewById(R.id.bottom_sheet_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopParcours();
            }
        });

        chronometer = (Chronometer) parentView.findViewById(R.id.bottom_sheet_chronometer);
        ratingBar = (RatingBar) parentView.findViewById(R.id.bottom_sheet_ratingBar);
        progression = (TextView) parentView.findViewById(R.id.bottom_sheet_active_progression);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        forceHideBottomSheet(true);
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

        int restoredParcoursId = getArguments().getInt(KEY_CURRENT_PARCOURS, -1);
        if (restoredParcoursId != -1) {
            currentParcoursId = restoredParcoursId;
        }


        mMapOptions = (MapOptions) getArguments().getSerializable(KEY_MAP_OPTIONS);
        zoom = getArguments().getInt(KEY_ZOOM);
        animatedToSavedLocation = getArguments().getBoolean(KEY_ANIMATED_TO_SAVED_LOCATION);
    }

    /**
     * Saves the current position the the MapView to the arguments of the fragment.
     */
    private void saveCurrentPosition() {
        if (mMapView != null) {
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

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(LOG, "location updated: " + location.toString());
                currentLocation = location;
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
    public void onSaveInstanceState(final Bundle outState) {
        saveCurrentPosition();
        saveParcoursListToPrefs();
        outState.putInt(KEY_CURRENT_PARCOURS, currentParcoursId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        saveCurrentPosition();
        saveParcoursListToPrefs();
        super.onStop();
    }

    /**
     * Call this every time we setup the map (so even at rotations). The data does not need to be saved directly as it is already present in SharedPrefrences.
     * Initializes the parcours list from SharedPreferences. If not empty, initializes the overlay for the parcours and the background overlay.
     */
    public void intializeParcoursAndOverlay(boolean includeDummy, int focusOnParcours, boolean parcoursStarted) {

        parcoursList = restoreParcoursListFromOSM(getActivity().getPreferences(Context.MODE_PRIVATE));

        if (parcoursList == null) {
            parcoursList = new ParcoursList();
        }

        parcoursList.addNew(getParcoursFromDownload(getActivity().getPreferences(Context.MODE_PRIVATE), focusOnParcours));
        if (includeDummy) {
            parcoursList.addNew(getDummyParcours());
        }

        if (parcoursList != null) {
            initParcoursOverlay(parcoursStarted);
            initBackgroundOverlay();
        } else {
            Log.d(LOG, "No parcours in sharedprefs");
        }
    }

    private void saveParcoursListToPrefs() {
        String parcoursListJson = new Gson().toJson(parcoursList, ParcoursList.class);
        getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_PARCOURS_LIST, parcoursListJson)
                .apply();
    }

    /**
     * Creates an overlay from the parcoursList and adds it to the MapView.
     */
    private void initParcoursOverlay(boolean parcoursStarted) {
        final List<CustomOverlayItem> firstBalisesInParcoursList = new ArrayList<>();

        firstBalisesInParcoursList.addAll(getOverlayFromParours(parcoursList, parcoursStarted));

        Log.d(LOG, "there are " + firstBalisesInParcoursList.size() + " primary balises");

        mParcoursOverlay = new CustomItemizedIconOverlay<>(getContext(), firstBalisesInParcoursList, new ItemizedIconOverlay.OnItemGestureListener<CustomOverlayItem>() {

            @Override
            public boolean onItemSingleTapUp(final int index, final CustomOverlayItem item) {
                onClick(item);
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final CustomOverlayItem item) {
                onClick(item);
                return false;
            }

            private void onClick(CustomOverlayItem item) {
                Log.d(LOG, "clicked on parcours: " + item.getParcoursId());
                setBottomSheetInfoToParcours(item);
                forceHideBottomSheet(false);
            }
        });

        mParcoursOverlay.setFocusItemsOnTap(true);
        mParcoursOverlay.setMarkerBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mParcoursOverlay.setMarkerTitleForegroundColor(Color.WHITE);
        mParcoursOverlay.setMarkerDescriptionForegroundColor(Color.WHITE);
        mParcoursOverlay.setDescriptionBoxPadding(20);
        mParcoursOverlay.setDescriptionBoxCornerWidth(20);

        mMapView.getOverlays().add(mParcoursOverlay);
    }

    /**
     * Creates an overlay in the background that will register all clicks to dismiss the overlay descriptions.
     */
    private void initBackgroundOverlay() {
        MapEventsOverlay mMapEventOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (!parcoursStarted) {
                    dismissParcoursInfo();
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });

        mMapView.getOverlays().add(0, mMapEventOverlay);
    }


    /**
     * Call to dismiss the focused overlayitem and bottomsheet with fab.
     */
    private void dismissParcoursInfo() {
        mParcoursOverlay.unSetFocusedItem();

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }

    /**
     * Call when an OverlayItem is clicked. Sets the bottom sheet's info to that item's and sets it to be visible.
     *
     * @param item
     */
    private void setBottomSheetInfoToParcours(CustomOverlayItem item) {
        TextView tvTitle = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_title);
        TextView tvDesc = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_description);

        tvTitle.setText(item.getTitle());
        tvDesc.setText(item.getSnippet());

        focusedOverlayItem = item;

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    /**
     * "Hides" or reveals the bottom sheet + fab by setting visibility.
     *
     * @param hide
     */
    private void forceHideBottomSheet(boolean hide) {
        if (hide) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    public void startStopParcours() {
        if (parcoursStarted) {
            new ParcoursConfirmation().show(false);
        } else {
            setParcoursAsTarget();
        }
    }

    /**
     * Called when the bottom sheet if alrady populated with info from clicking a primary balise.
     * Called on fab start click.
     * Sets the parcours as the target for the user.
     */
    public void setParcoursAsTarget() {
        if (focusedOverlayItem != null) {
            int parcoursId = focusedOverlayItem.getParcoursId();

            Balise primaryBalise = parcoursList.getParcoursById(parcoursId).getPrimaryBalise();

            if (verifyStartPossibility(primaryBalise)) {
                // Use the Builder class for convenient dialog construction
                new ParcoursConfirmation().show(true);
            }
        }
    }

    /**
     * @param primaryBalise the primary balise of the parcours the user is trying to start
     * @return whether the user is near the primary balise
     */
    @SuppressWarnings({"ResourceType"})
    private boolean verifyStartPossibility(Balise primaryBalise) {
        if (currentLocation == null) {
            Toast.makeText(getContext(), "Couldn't verify your present location", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(LOG, "calculating distance to  balise: " + primaryBalise.getId() + " from parcours: " + primaryBalise.getParcoursId());
        Log.d(LOG, "calculating distance to " + primaryBalise.toLocation().toString());
        Log.d(LOG, "calculating with current location: " + currentLocation.toString());


        Location destLocation = new Location("");
        destLocation.setLatitude(primaryBalise.getLatitude());
        destLocation.setLongitude(primaryBalise.getLongitude());

        final float distanceTo = currentLocation.distanceTo(destLocation);

        final int distanceRounded = Math.round(distanceTo);

        if (distanceRounded > MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS) {
            Toast.makeText(getContext(), "You are " + distanceRounded + "m too far from the parcours!", Toast.LENGTH_SHORT).show();
            Log.d(LOG, "Outside, distance from center: " + distanceRounded + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);
            return false;
        }

        Log.d(LOG, "Inside, distance from center: " + distanceRounded + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);

        return true;
    }

    private class ParcoursConfirmation {

        private void show(boolean start) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (start) {
                builder.setMessage("Do you really want to start the parcours now?")
                        .setPositiveButton("Yes please", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toggleBottomSheetParcoursState(true);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
            } else {
                builder.setMessage("Do you really want to stop the parcours?")
                        .setPositiveButton("Yes, cancel it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toggleBottomSheetParcoursState(false);
                            }
                        })
                        .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
            }
            builder.show();
        }
    }

    private void toggleBottomSheetParcoursState(boolean start) {
        if (start) {
            parcoursStarted = true;

            currentParcoursId = focusedOverlayItem.getParcoursId();
            mParcoursOverlay.unSetFocusedItem();
            focusOnParcours(false, currentParcoursId, parcoursStarted);

            Parcours currentParcours = parcoursList.getParcoursById(currentParcoursId);

            // offset the start by how much time has already passed last time the parcours was started
            chronometer.setBase(SystemClock.elapsedRealtime() - currentParcours.getElapsedTimeMillis());

            // TODO receiver for next balise location alert

            fab.setVisibility(GONE);
            bottomSheetMain.setVisibility(GONE);
            bottomSheetActive.setVisibility(VISIBLE);


            chronometer.start();
            Toast.makeText(getContext(), "Starting parcours!", Toast.LENGTH_SHORT).show();
        } else {
            parcoursStarted = false;

            chronometer.stop();
            saveTimeForParcours();
            saveParcoursListToPrefs();

            // TODO save progression

            mParcoursOverlay.unSetFocusedItem();
            focusOnParcours(true, -1, parcoursStarted);

            fab.setVisibility(VISIBLE);
            bottomSheetMain.setVisibility(View.VISIBLE);
            bottomSheetActive.setVisibility(GONE);
            Toast.makeText(getContext(), "Stopped parcours!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTimeForParcours() {
        Parcours currentParcours = parcoursList.getParcoursById(currentParcoursId);

        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

        currentParcours.setElapsedTimeMillis(elapsedMillis);
    }

    private void focusOnParcours(boolean includeDummy, int parcoursId, boolean parcoursStarted) {
        mMapView.getOverlays().remove(mParcoursOverlay);
        mMapView.invalidate();
        intializeParcoursAndOverlay(includeDummy, parcoursId, parcoursStarted);

        Parcours currentParcours = parcoursList.getParcoursById(currentParcoursId);
        // initial target is primary balise at index 0 which is already shown
        // therefore we need to show the next one once we start
        if (parcoursStarted && currentParcours.getBaliseToTarget() == 0) {
            revealNextBalise(parcoursId);
        }
    }

    // TODO show next balise, add location alert, broadcast receiver...

    /*
    private void revealNextBalise(int parcoursId) {
        Parcours parcours = parcoursList.getParcoursById(parcoursId);
        Balise nextBalise = parcours.getNextBalise();
        if (nextBalise != null) {

            mParcoursOverlay.addItem(nextBalise.toCustomOverlayItem());
            mMapView.invalidate();
            Log.d(LOG, "addded balise: " + nextBalise.getId() + " from parcours: "
                    + nextBalise.getParcoursId() + ", " + nextBalise.getTitle());
        } else {
            Log.d(LOG, "no next balise, reached last one already");
        }
    }
    */

    private void revealNextBalise(int parcoursId) {
        Parcours parcours = parcoursList.getParcoursById(parcoursId);
        parcours.incrementTargetBalise();
        saveParcoursListToPrefs();
        focusOnParcours(false, parcoursId, parcoursStarted);
    }
}
