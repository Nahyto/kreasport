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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.BuildConfig;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import static android.R.string.cancel;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.ACTION_PROXIMITY_ALERT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.LOCATION_IUT;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_DISTANCECHANGE_FOR_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants.MINIMUM_TIME_BETWEEN_UPDATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getDummyParcours;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.getParcoursFromPreferences;
import static fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils.mainParcoursBalisesToOverlayList;

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

    public static final String KEY_PARCOURS = "mapsv3.osm_frag.parcours";

    private static GeoPoint defaultPoint;
    private static GeoPoint savedPoint;
    protected int zoom;


    private ParcoursList parcoursList;
    private CustomItemizedIconOverlay mParcoursOverlay;


    private CustomMapView mMapView;
    private MapOptions mMapOptions;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private boolean animatedToSavedLocation;

    private LocationManager locationManager;

    private YourReceiver receiver;

    private View bottomSheet;
    private FloatingActionButton fab;

    private CustomOverlayItem focusedOverlayItem;
    private boolean confirmStartParcours;
    private boolean parcoursStarted;


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

        restoreParcoursList();
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

        fab = (FloatingActionButton) parentView.findViewById(R.id.fab);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        forceBottomSheetState(false);
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

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    /**
     * Call this every time we setup the map (so even at rotations). The data does not need to be saved directly as it is already present in SharedPrefrences.
     * Initializes the parcours list from SharedPreferences. If not empty, initializes the overlay for the parcours and the background overlay.
     */
    public void restoreParcoursList() {

        parcoursList = new ParcoursList();
        parcoursList.addAll(getParcoursFromPreferences(getActivity().getPreferences(Context.MODE_PRIVATE)));
        parcoursList.addAll(getDummyParcours());

        if (parcoursList != null) {
            initParcoursOverlay();
            initBackgroundOverlay();
        } else {
            Log.d(LOG, "No parcours in sharedprefs");
        }
    }

    /**
     * Creates an overlay from the parcoursList and adds it to the MapView.
     */
    private void initParcoursOverlay() {
        final List<CustomOverlayItem> firstBalisesInParcoursList = new ArrayList<>();

        firstBalisesInParcoursList.addAll(mainParcoursBalisesToOverlayList(parcoursList));

        Log.d(LOG, "there are " + firstBalisesInParcoursList.size() + " primary balises");

        mParcoursOverlay = new CustomItemizedIconOverlay(getContext(), firstBalisesInParcoursList, new ItemizedIconOverlay.OnItemGestureListener<CustomOverlayItem>() {

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
                forceBottomSheetState(false);
            }
        });

        mParcoursOverlay.setFocusItemsOnTap(true);

        mParcoursOverlay.setMarkerBackgroundColor(Color.BLUE);
        mParcoursOverlay.setMarkerTitleForegroundColor(Color.WHITE);
        mParcoursOverlay.setMarkerDescriptionForegroundColor(Color.WHITE);
        mParcoursOverlay.setDescriptionBoxPadding(15);
        mMapView.getOverlays().add(mParcoursOverlay);
    }

    /**
     * Creates an overlay in the background that will register all clicks to dismiss the overlay descriptions.
     */
    private void initBackgroundOverlay() {
        MapEventsOverlay mMapEventOverlay = new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                dismissParcoursInfo();
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
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Log.d(LOG, "calculating distance to  balise: " + primaryBalise.getId() + " from parcours: " + primaryBalise.getParcoursId());
        Log.d(LOG, "calculating distance to " + primaryBalise.toLocation().toString());
        Log.d(LOG, "calculating with current location: " + lastLocation.toString());


        Location destLocation = new Location("");
        destLocation.setLatitude(primaryBalise.getLatitude());
        destLocation.setLongitude(primaryBalise.getLongitude());

        final float distanceTo = lastLocation.distanceTo(destLocation);

        final int distanceRounded = Math.round(distanceTo);
        Toast.makeText(getContext(), "Distance: " + distanceRounded + "m", Toast.LENGTH_SHORT).show();

        if (distanceRounded > MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS) {
            Log.d(LOG, "Outside, distance from center: " + distanceRounded + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);
            return false;
        }

        Log.d(LOG, "Inside, distance from center: " + distanceRounded + " limit: " + MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS);

        return true;
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
    private void forceBottomSheetState(boolean hide) {
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

    public void toggleFabClick() {
        if (parcoursStarted) {
            new ParcoursConfirmation().show(false);
        } else {
            setParcoursAsTarget();
        }
    }

    private class ParcoursConfirmation {

        private void show(boolean start) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (start) {
                builder.setMessage("Do you really want to start the parcours now?")
                        .setPositiveButton("Yes please", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toggleActiveParcoursState(true);
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
                                toggleActiveParcoursState(false);
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

    private void toggleActiveParcoursState(boolean start) {
        if (start && fab.getVisibility() == VISIBLE) {
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_stop_white_24dp));
            Toast.makeText(getContext(), "Starting parcours!", Toast.LENGTH_SHORT).show();
            parcoursStarted = true;
        } else if (!start && fab.getVisibility() == VISIBLE) {
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play_arrow_white_24dp));
            Toast.makeText(getContext(), "Stopped parcours!", Toast.LENGTH_SHORT).show();
            parcoursStarted = false;
        }

    }
}
