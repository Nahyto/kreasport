package fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OSMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OSMFragment extends Fragment {

    protected static final String KEY_FIRST_RUN = "first_run";

    protected static final String KEY_INITIAL_POINT = "initial_point";
    protected static final String KEY_ZOOM = "zoom";
    protected static final String KEY_MAP_OPTIONS = "map_options";

    private static GeoPoint initialPoint;
    protected int zoom;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // needs to be called before the MapView is created
        Configuration.getInstance().setMapViewHardwareAccelerated(true);

        restoreArguments();

        mMapView = new MapView(inflater.getContext());

        // set default location to the one specified on newInstance. Will still animate/move to last saved location after.
        // This is in case there is a delay in acquiring current/last position
        mMapView.getController().setCenter(initialPoint);

        basicMapSetup();

        goThroughOptions();

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

    /**
     * Enables options from {@link MapOptions}
     */
    private void goThroughOptions() {
        if (mMapOptions != null) {
            if (mMapOptions.isEnableLocationOverlay()) {
                MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mMapView);
                mLocationOverlay.enableMyLocation();
                mMapView.getOverlays().add(mLocationOverlay);
            }
            if (mMapOptions.isEnableCompass()) {
                CompassOverlay mCompassOverlay = new CompassOverlay(getContext(), new InternalCompassOrientationProvider(getContext()), mMapView);
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
    @SuppressWarnings({"ResourceType"}) // we can ignore because we only launch this fragment after checking permissions.
    private void restorePosition() {

        final IMapController mapController = mMapView.getController();
        mapController.setZoom(zoom);

        if (getArguments().getBoolean(KEY_FIRST_RUN)) {
            Toast.makeText(getContext(), R.string.osm_fragment_first_run_location_info, Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
        } else if (!getArguments().getBoolean(KEY_FIRST_RUN) && initialPoint != null) {
            mapController.animateTo(initialPoint);
        }
    }

}