package fr.univ_lille1.iut_info.caronic.mapsv3.maps.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.CustomCacheManagerCallback;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapLayoutListener;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Constants;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils;

public class OfflineAreaSelection extends AppCompatActivity {

    private final static String LOG = OfflineAreaSelection.class.getSimpleName();

    public final static String KEY_AREA_NAME = "mapsv3.key_area_name";
    public final static String KEY_AREA_SIZE = "mapsv3.key_area_description";
    public static final String KEY_AREA_FILE_ABSOLUTE_PATH = "mapsv3.key_area_file_absolute_path";


    private MapView mMapView;

    /**
     * Offsets of obscured rectangle on MapView
     */
    private double sideAndTopOffsetPercentage = 0.1;
    private double bottomOffsetPercentage = 0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_area_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.offline_area_selection_action_bar_title);

        setButtonActions();

        setupMap();
    }


    @SuppressWarnings({"ResourceType"}) // we can ignore because we only launch this fragment from a checked state
    private void setupMap() {
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_relative_layout);

        // needs to be called before the MapView is created
        Configuration.getInstance().setMapViewHardwareAccelerated(true);
        mMapView = new MapView(this);
        // set default location to Lille, will still animate/move to last saved location after
        // this is in case there is a delay in acquiring current/last position
        mMapView.getController().setCenter(new GeoPoint(50.633621, 3.0651845));

        mMapView.setMinZoomLevel(Constants.DOWNLOAD_MIN_ZOOM);
        mMapView.setMaxZoomLevel(Constants.DOWNLOAD_MAX_ZOOM);
        /*
            From levels 11 to 17, the max download size is approx. 150MB
            From levels 11 to max, the max download size is approx. 25000MB
            OSM policy prohibits mass downloading and downloading past zoom level 17 without special access.
            See https://operations.osmfoundation.org/policies/tiles/
         */

        mMapView.setMultiTouchControls(true);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getOverlays().add(new CopyrightOverlay(getApplicationContext()));

        mMapView.setTilesScaledToDpi(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint currentLocation = new GeoPoint(location);
                mMapView.getController().animateTo(currentLocation);
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

        relativeLayout.addView(mMapView);

        MapLayoutListener mapLayoutListener = new MapLayoutListener(this, mMapView, relativeLayout, sideAndTopOffsetPercentage, bottomOffsetPercentage);
        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(mapLayoutListener);

    }

    private void setButtonActions() {
        Button confirm = (Button) findViewById(R.id.confirm_area_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });


        Button cancel = (Button) findViewById(R.id.cancel_area_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
    }

    private void startDownload() {
        BoundingBox boundingBox = mMapView.getBoundingBox();
        BoundingBox realBox = Utils.getBoundingBoxWithOffset(boundingBox, sideAndTopOffsetPercentage, bottomOffsetPercentage);

        String locationName = null;
        GeoPoint center = (GeoPoint) mMapView.getMapCenter();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Address address = geocoder.getFromLocation(center.getLatitude(), center.getLongitude(), 1).get(0);

            locationName = address.getLocality();

        } catch (IOException e) {
            locationName = "New Area";
        }
        try {

            String absoluteFilePath = getUniqueFileName(locationName);
            Log.i(LOG, "Selected area \"" + locationName + "\"");
            Log.i(LOG, "Will download to \"" + absoluteFilePath + "\"");

            final SqliteArchiveTileWriter writer = new SqliteArchiveTileWriter(absoluteFilePath);
            CacheManager mgr = new CacheManager(mMapView, writer);

            CustomCacheManagerCallback customCacheManagerCallback = new CustomCacheManagerCallback(this, writer, locationName);

            mgr.downloadAreaAsyncNoUI(this, realBox, mMapView.getZoomLevel(), mMapView.getMaxZoomLevel(), customCacheManagerCallback);

            double estimatedSize = Utils.getDownloadSizeForBoundingBox(realBox, mMapView);
            int roundedSize = (int) Math.round(estimatedSize);

            Intent response = new Intent();
            response.putExtra(KEY_AREA_NAME, locationName);
            response.putExtra(KEY_AREA_FILE_ABSOLUTE_PATH, absoluteFilePath);
            response.putExtra(KEY_AREA_SIZE, roundedSize);

            setResult(RESULT_OK, response);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets a unique filename like sdcard/osmdroid/ouputName&lt;generated_numbers&gt;.sqlite.
     * Uses {@link File#createTempFile(String, String, File)}
     * @param outputName the original name of what we want to download
     * @return either a unique filename or sdcard/osmdroid/outputName.sqlite
     */
    private String getUniqueFileName(String outputName) {
        outputName = outputName.replaceAll("\\s", "_");
        String extension = ".sqlite";

        String baseDirectoryString = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "osmdroid" + File.separator;
        File baseDirectory = new File(baseDirectoryString);

        try {
            File file = File.createTempFile(outputName, extension, baseDirectory);
            String absolutePath = file.getAbsolutePath();
            file.delete();
            return absolutePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baseDirectoryString + outputName + extension;
    }

}
