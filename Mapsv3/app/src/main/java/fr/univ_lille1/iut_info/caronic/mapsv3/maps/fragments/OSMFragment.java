package fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_DISTANCECHANGE_FOR_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_TIME_BETWEEN_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getDummyParcours;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getOverlayFromParours;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getParcoursFromDownload;
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

    /**
     * Used for getting the initial downloaded parcours transferred from {@link fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity}
     */
    public static final String KEY_PARCOURS = "mapsv3.osm_frag.parcours";
    /**
     * Used for saving/getting {@link ParcoursList} from inside this class. Useful for saving time elsapsed
     */
    public static final String KEY_PARCOURS_LIST = "mapsv3.osm_frag.parcours_list";
    private static final String KEY_PARCOURS_STARTED = "mapsv3.osm_frag.parcours_started";
    private static final String KEY_CURRENT_BALISE = "mapsv3.osm_frag.current_balise";
    private static final String KEY_PARCOURS_FINISHED = "mapsv3.osm_frag.parcours_finished";

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

    private View bottomSheet;
    private FloatingActionButton fab;
    private View bottomSheetMain;
    private View bottomSheetActive;
    private Chronometer chronometer;
    private RatingBar ratingBar;
    private TextView progression;
    private Button cancel;


    private boolean parcoursStarted;
    private Location currentLocation;
    private Balise currentBalise;


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

        setupMapView(fragmentView);

        setupBottomSheet(fragmentView);
        if (currentBalise != null) { // restore bottom sheet on rotate
            toggleBottomSheetParcoursState(parcoursStarted);
        }

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

        if (currentBalise != null) {
            intializeParcoursAndOverlay(true, currentBalise.getParcoursId(), parcoursStarted);
        } else {
            intializeParcoursAndOverlay(true, -1, parcoursStarted);
        }
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
                toggleParcoursState();
            }
        });

        cancel = (Button) parentView.findViewById(R.id.bottom_sheet_cancel_button);
        setCancelClickListener(true);

        chronometer = (Chronometer) parentView.findViewById(R.id.bottom_sheet_chronometer);
        ratingBar = (RatingBar) parentView.findViewById(R.id.bottom_sheet_ratingBar);
        progression = (TextView) parentView.findViewById(R.id.bottom_sheet_active_progression);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        if (currentBalise == null) {
            forceHideBottomSheet(true);
        } else {

            //toggleBottomSheetParcoursState(parcoursStarted);
            Log.d(LOG, "on restore bottom sheet, current balise: " + currentBalise.getId() + " and parcours started: " + parcoursStarted);

            setBottomSheetInfoToParcours(currentBalise);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (parcoursStarted) {
                bottomSheetActive.setVisibility(VISIBLE);
                bottomSheetMain.setVisibility(GONE);
                fab.setVisibility(GONE);
            } else {
                bottomSheetActive.setVisibility(GONE);
                bottomSheetMain.setVisibility(VISIBLE);
                fab.setVisibility(VISIBLE);
            }
        }
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
        Log.d(LOG, "restore arguments");

        defaultPoint = (GeoPoint) getArguments().getSerializable(KEY_DEFAULT_POINT);

        String initalPointJson = getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .getString(KEY_SAVED_LOCATION, "");
        if (!initalPointJson.equals("")) {
            savedPoint = new Gson().fromJson(initalPointJson, GeoPoint.class);
        }

        parcoursStarted = getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .getBoolean(KEY_PARCOURS_STARTED, false);

        String currentBaliseJson = getActivity()
                .getPreferences(Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_BALISE, "");
        if (!currentBaliseJson.equals("")) {
            currentBalise = new Gson().fromJson(currentBaliseJson, Balise.class);
            Log.d(LOG, "restored current balise");
        } else {
            Log.d(LOG, "couldnt restore current balise");
        }


        mMapOptions = (MapOptions) getArguments().getSerializable(KEY_MAP_OPTIONS);
        zoom = getArguments().getInt(KEY_ZOOM);
        animatedToSavedLocation = getArguments().getBoolean(KEY_ANIMATED_TO_SAVED_LOCATION);

        Log.d(LOG, "done restore args\n");
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
                    .commit();
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
                if (parcoursStarted) {
                    verifyNearTargetBalise();
                }
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

    }

    private void saveCurrentBalise() {
        if (currentBalise != null) {
            Log.d(LOG, "saving current balise to prefs: " + currentBalise.getId());

            String currenbaliseJson = new Gson().toJson(currentBalise, Balise.class);

            getActivity()
                    .getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_CURRENT_BALISE, currenbaliseJson)
                    .putBoolean(KEY_PARCOURS_STARTED, parcoursStarted)
                    .commit();
        } else {
            Log.d(LOG, "couldnt save current balise, was null");
            getActivity()
                    .getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_CURRENT_BALISE, "")
                    .putBoolean(KEY_PARCOURS_STARTED, false)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        Log.d(LOG, "onsaveinstance");

        saveTimeForParcours();
        saveCurrentPosition();
        saveParcoursListToPrefs();
        saveCurrentBalise();

        Log.d(LOG, "DONE onsaveinstace");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        // unregister receiver
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG, "onStop");

        saveTimeForParcours();
        saveCurrentPosition();
        saveParcoursListToPrefs();
        saveCurrentBalise();

        Log.d(LOG, "DONE onstop");
        super.onStop();
    }

    /**
     * Call this every time we setup the map (so even at rotations). The data does not need to be saved directly as it is already present in SharedPrefrences.
     * Initializes the parcours list from SharedPreferences. If not empty, initializes the overlay for the parcours and the background overlay.
     */
    public void intializeParcoursAndOverlay(boolean includeDummy, int focusOnParcours, boolean parcoursStarted) {

        if (parcoursStarted && (focusOnParcours <= -10 || focusOnParcours >= 0)) {
            Log.d(LOG, "focusing on parcours:" + focusOnParcours);
            ParcoursList allRestored = restoreParcoursListFromOSM(getActivity().getPreferences(Context.MODE_PRIVATE));
            parcoursList = new ParcoursList();
            parcoursList.addSpecificParcours(allRestored, focusOnParcours);
        } else {
            Log.d(LOG, "showing all parcours:");
            parcoursList = restoreParcoursListFromOSM(getActivity().getPreferences(Context.MODE_PRIVATE));

            if (parcoursList == null) {
                parcoursList = new ParcoursList();
            }

            parcoursList.addNew(getParcoursFromDownload(getActivity().getPreferences(Context.MODE_PRIVATE), focusOnParcours));
            if (includeDummy) {
                parcoursList.addNew(getDummyParcours(focusOnParcours));
            }
        }

        if (parcoursList != null) {
            initParcoursOverlay(parcoursStarted);
            initBackgroundOverlay();
        } else {
            Log.d(LOG, "No parcours in sharedprefs");
        }
    }

    private void saveParcoursListToPrefs() {
        Log.d(LOG, "saving parcours list to prefs");

        ParcoursList alreadySaved = restoreParcoursListFromOSM(getActivity().getPreferences(Context.MODE_PRIVATE));
        if (alreadySaved != null) {
            int savedProgression = alreadySaved.getParcoursById(currentBalise.getParcoursId()).getBaliseToTargetIndex();
            int currentProgression = parcoursList.getParcoursById(currentBalise.getParcoursId()).getBaliseToTargetIndex();
            if (currentProgression >= savedProgression) {
                Log.d(LOG, "SAVED parcoursList with focused parcours: " + currentBalise.getParcoursId() + " progression = " + parcoursList.getParcoursById(currentBalise
                        .getParcoursId()).getBaliseToTargetIndex());
                String parcoursListJson = new Gson().toJson(parcoursList, ParcoursList.class);
                getActivity()
                        .getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putString(KEY_PARCOURS_LIST, parcoursListJson)
                        .commit();
            }
        } else {
            Log.d(LOG, "SAVED parcoursList with focused parcours: " + currentBalise.getParcoursId() + " progression = " + parcoursList.getParcoursById(currentBalise
                    .getParcoursId()).getBaliseToTargetIndex());
            String parcoursListJson = new Gson().toJson(parcoursList, ParcoursList.class);
            getActivity()
                    .getPreferences(Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_PARCOURS_LIST, parcoursListJson)
                    .commit();
        }
    }

    /**
     * Creates an overlay from the parcoursList and adds it to the MapView.
     */
    private void initParcoursOverlay(boolean parcoursStarted) {
        final List<CustomOverlayItem> firstBalisesInParcoursList = new ArrayList<>();

        firstBalisesInParcoursList.addAll(getOverlayFromParours(parcoursList, parcoursStarted));

        Log.d(LOG, "there are " + firstBalisesInParcoursList.size() + " balises in the overlay");

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
                Log.d(LOG, "clicked on overlayitem: " + item.getId() + " from parcours:" + item.getParcoursId());
                currentBalise = parcoursList.getParcoursById(item.getParcoursId()).getBaliseById(item.getId());
                Log.d(LOG, "clicked on balise: " + currentBalise.getId() + "from parcours: " + item.getParcoursId());
                setBottomSheetInfoToParcours(currentBalise);
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
        bottomSheetActive.setVisibility(View.INVISIBLE);
        bottomSheetMain.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.GONE);
    }

    /**
     * Call when an OverlayItem is clicked. Sets the bottom sheet's info to that item's and sets it to be visible.
     *
     * @param balise
     */
    private void setBottomSheetInfoToParcours(Balise balise) {
        Log.d(LOG, "set bottom sheet info to parcours");

        TextView tvTitle = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_title);
        TextView tvDesc = (TextView) bottomSheet.findViewById(R.id.bottom_sheet_description);

        tvTitle.setText(balise.getTitle());
        tvDesc.setText(balise.getDescription());

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheet.setVisibility(View.VISIBLE);
        if (parcoursStarted) {
            bottomSheetMain.setVisibility(GONE);
            bottomSheetActive.setVisibility(VISIBLE);
        } else {
            bottomSheetMain.setVisibility(VISIBLE);
            bottomSheetActive.setVisibility(GONE);
        }

        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheet.getVisibility() == VISIBLE) {
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
            }
        });

        if (currentBalise != null) {
            Parcours parcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
            if (parcours != null && !parcoursStarted && parcours.isPrimarybalise(currentBalise.getId())) {
                fab.setVisibility(VISIBLE);
            } else {
                fab.setVisibility(GONE);
            }
        } else {
            fab.setVisibility(GONE);
        }
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
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.setVisibility(View.VISIBLE);
        }

        if (currentBalise != null) {
            Parcours parcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
            if (parcours != null && !parcoursStarted && parcours.isPrimarybalise(currentBalise.getId())) {
                fab.setVisibility(VISIBLE);
            } else {
                fab.setVisibility(GONE);
            }
        } else {
            fab.setVisibility(GONE);
        }
    }


    /**
     * Called when the bottom sheet if alrady populated with info from clicking a primary balise.
     * Called on fab start click.
     */
    private void toggleParcoursState() {
        if (currentBalise != null) {
            Parcours currentParcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
            if (parcoursStarted) {
                Log.d(LOG, "toggling parcours state, want to stop");
                // stop
                showParcoursConfirmation(false, null);
            } else {
                // start from fab

                Log.d(LOG, "want to start parcours: " + currentParcours.getId() + " current state: " + currentParcours.getStateName());

                if (verifyStartPossibility(currentBalise)) {
                    showParcoursConfirmation(true, currentParcours);
                }
            }
        } else {
            Log.d(LOG, "Error: expected currentBalise to be non null after a fab click");
        }
    }

    private void showDismissalOnParcoursFinished() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to return to the parcours view?")
                .setPositiveButton("Yes, please", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismissParcoursInfo();
                        focusOverlayOnParcours(true, -1, false);
                    }
                })
                .setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).show();
    }

    /**
     * @param primaryBalise the primary balise of the parcours the user is trying to start
     * @return whether the user is near the primary balise
     */
    @SuppressWarnings({"ResourceType"})
    private boolean verifyStartPossibility(Balise primaryBalise) {
        if (currentLocation == null) {
            Toast.makeText(getContext(), "Couldn't verify your present location", Toast.LENGTH_SHORT).show();
            Log.d(LOG, "couldnt verify current location to verify proximity to start");
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
            Toast.makeText(getContext(), "You are " + distanceRounded + "m away from the parcours!", Toast.LENGTH_SHORT).show();
            Log.d(LOG, "Outside, distance from center: " + distanceTo + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);
            return false;
        }

        Log.d(LOG, "Inside, distance from center: " + distanceTo + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);

        return true;
    }


    private void showParcoursConfirmation(boolean start, final Parcours parcours) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (start) {
            if (parcours.getState() == Parcours.STATE_NEW) {
                builder.setMessage("Do you really want to start the parcours now?\nThe timer will start right away");
            } else if (parcours.getState() == Parcours.STATE_NOT_FINISHED) {
                builder.setMessage("Do you want to resume this parcours? Your progression will be restored.");
            } else if (parcours.getState() == Parcours.STATE_IS_FINISHED) {
                builder.setMessage("Do you want to retry this parcours?\nWarning! Your progression will be erased.");
            }
            builder
                    .setPositiveButton("Yes, please", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (parcours.getState() == Parcours.STATE_NEW) {
                                parcours.setState(Parcours.STATE_NOT_FINISHED);
                                Log.d(LOG, "START parcours");

                                refreshBottomSheetProgression();
                                setCancelClickListener(true);
                                Log.d(LOG, "confirmed start, changed state to " + parcours.getStateName());
                            } else if (parcours.getState() == Parcours.STATE_IS_FINISHED) {
                                parcours.setState(Parcours.STATE_NEW);
                                parcours.resetProgression();
                                setCancelClickListener(false);
                                Log.d(LOG, "START parcours, reset progression");
                            } else {
                                Log.d(LOG, "RESUME parcours");
                            }
                            toggleBottomSheetParcoursState(true);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
        } else {
            builder.setMessage("Do you really want to pause the parcours?")
                    .setPositiveButton("Yes, pause it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(LOG, "PAUSE parcours");
                            toggleBottomSheetParcoursState(false);
                            saveParcoursListToPrefs();
                        }
                    })
                    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
        }
        builder.show();
    }


    /**
     * Toggles the bottom sheet main/active state
     *
     * @param start if should switch to active view
     */
    private void toggleBottomSheetParcoursState(boolean start) {
        Log.d(LOG, "toggling bottomsheet state");

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Parcours currentParcours = parcoursList.getParcoursById(currentBalise.getParcoursId());

        if (start && currentParcours.getBaliseToTargetIndex() == Parcours.STATE_IS_FINISHED) {
            currentParcours.resetProgression();
        }

        if (start) {
            parcoursStarted = true;

            int currentParcoursId = currentBalise.getParcoursId();
            mParcoursOverlay.unSetFocusedItem();
            focusOverlayOnParcours(true, currentParcoursId, true);

            // offset the start by how much time has already passed last time the parcours was started
            long base = SystemClock.elapsedRealtime() - currentParcours.getElapsedTimeMillis();
            chronometer.setBase(base);

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

            mParcoursOverlay.unSetFocusedItem();
            focusOverlayOnParcours(true, -1, parcoursStarted);

            Parcours parcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
            if (parcours.isPrimarybalise(currentBalise.getId())) {
                fab.setVisibility(VISIBLE);
            } else {
                fab.setVisibility(GONE);
            }

            bottomSheetMain.setVisibility(View.VISIBLE);
            bottomSheetActive.setVisibility(GONE);
            Toast.makeText(getContext(), "Stopped parcours!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTimeForParcours() {
        if (currentBalise != null) {
            Parcours currentParcours = parcoursList.getParcoursById(currentBalise.getParcoursId());

            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

            currentParcours.setElapsedTimeMillis(elapsedMillis);
            Log.d(LOG, "saving time for parcours: " + currentParcours.getId() + " -> " + elapsedMillis);
        } else {
            Log.d(LOG, "couldnt save time for currentParcours bc currentBalise null");
        }
    }

    /**
     * Reinstanciates the overlay with only the selected parcours, up to its current progression.
     *
     * @param includeDummy
     * @param parcoursId
     * @param parcoursStarted
     */
    private void focusOverlayOnParcours(boolean includeDummy, int parcoursId, boolean parcoursStarted) {
        if (currentBalise != null) {
            Log.d(LOG, "focusing on parcours: " + parcoursId);
            saveParcoursListToPrefs();

            mMapView.getOverlays().remove(mParcoursOverlay);
            mMapView.invalidate();
            intializeParcoursAndOverlay(includeDummy, parcoursId, parcoursStarted);

            Parcours currentParcours = parcoursList.getParcoursById(currentBalise.getParcoursId());

            // initial target is primary balise at index 0 which is already shown
            // therefore we need to show the next one once we start
            if (currentParcours != null && parcoursStarted && currentParcours.getBaliseToTargetIndex() == 0) {
                revealNextBalise(parcoursId);
            }
        } else {
            Log.d(LOG, "could not focus on parcours, currentBalise is null");
        }
    }

    // TODO show next balise, add location alert, broadcast receiver...


    @SuppressWarnings({"ResourceType"})
    private void revealNextBalise(int parcoursId) {
        Log.d(LOG, "increment target balise for parcours: " + parcoursId);
        Log.d(LOG, "will reload the overlay");
        Parcours parcours = parcoursList.getParcoursById(parcoursId);
        parcours.incrementTargetBalise();

        Log.d(LOG, "the target is now balise: " + parcours.getBaliseToTargetIndex());
        saveParcoursListToPrefs();
        focusOverlayOnParcours(true, parcoursId, parcoursStarted);
    }

    private void verifyNearTargetBalise() {
        if (currentBalise != null) {
            Balise baliseToTarget = parcoursList.getParcoursById(currentBalise.getParcoursId()).getBaliseToTarget();
            Log.d(LOG, "veryfying near target balise: " + baliseToTarget.getId());

            Location destLocation = new Location("");
            destLocation.setLatitude(baliseToTarget.getLatitude());
            destLocation.setLongitude(baliseToTarget.getLongitude());

            float distance = destLocation.distanceTo(currentLocation);
            int distanceRounded = Math.round(distance);

            if (distance > MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS) {
                Log.d(LOG, "Outside, distance from center: " + distance + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);
            } else {
                Log.d(LOG, "reached next balise: " + baliseToTarget.getId() + " for parcours: " + baliseToTarget.getParcoursId());
                doActionReachedBalise();
            }
        }

    }

    private void doActionReachedBalise() {
        Parcours parcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
        Balise currentTarget = parcours.getBaliseToTarget();

        refreshBottomSheetProgression();

        if (parcours.isBaliseLastOne(currentTarget.getId())) {
            validateEndParcours();
        } else {
            Toast.makeText(getContext(), "You have reached the next balise!", Toast.LENGTH_SHORT).show();
            revealNextBalise(currentBalise.getParcoursId());
        }
    }

    /**
     * Updates bottom_sheet_active_progression. Call before revealing the next balise because the progression relies upon the target balise index.
     */
    private void refreshBottomSheetProgression() {
        Parcours currentParcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
        int max = currentParcours.getNumberOfBalises();
        int currentIndex = currentParcours.getBaliseToTargetIndex();
        if (max > 0 && currentIndex >= 0) {
            String progressionStr = "" + (currentIndex + 1) + "/" + max;
            progression.setText(progressionStr);
            Log.d(LOG, "got index: " + currentIndex + " and max: " + max + " -> " + progressionStr);
        }
    }

    /**
     * Call to stop the parcours.
     */
    private void validateEndParcours() {
        Log.d(LOG, "validating end of parcours");
        parcoursStarted = false;

        chronometer.stop();
        saveTimeForParcours();

        Parcours parcours = parcoursList.getParcoursById(currentBalise.getParcoursId());
        parcours.setState(Parcours.STATE_IS_FINISHED);

        saveParcoursListToPrefs();

        setCancelClickListener(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Congratulations! You have reached the last balise!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).show();

    }

    /**
     * @param setCancel cancel or dismiss
     */
    private void setCancelClickListener(boolean setCancel) {
        if (setCancel) {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleParcoursState();
                }
            });
        } else {
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDismissalOnParcoursFinished();
                }
            });
        }
    }
}
