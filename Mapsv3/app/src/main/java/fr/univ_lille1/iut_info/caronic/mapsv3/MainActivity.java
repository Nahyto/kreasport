package fr.univ_lille1.iut_info.caronic.mapsv3;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments.BlankFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments.PermissionsFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.activities.OfflineAreas;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;

import static android.R.attr.tag;
import static fr.univ_lille1.iut_info.caronic.mapsv3.R.id.nav_feed;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG = MainActivity.class.getSimpleName();

    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    public static boolean mLocationAccessGranted;
    public static boolean mWriteExternalStorageGranted;

    private NavigationView navigationView;
    private FloatingActionButton fab;

    private final static String TAG_FEED = "FEED";
    private final static String TAG_EXPLORE = "EXPLORE";
    private final static String TAG_PERMISSIONS_FRAG = "permissions_frag";

    private Fragment feedFragment;
    private Fragment osmFragment;

    /**
     * Variables to save which fragment is currently being displayed. NOT to use for switching activities.
     */
    private String currentTag;
    private int currentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyAllPermissions();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_not_implemented) {
            Snackbar.make(findViewById(R.id.content_frame), "Replace with your own action", Snackbar.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return displaySelectedScreen(item.getItemId());
    }

    /**
     * If we start an activity, on back button press, the selected nav item must be the last item corresponding to a fragment that was displayed in the content_frame. Not the
     * activity that was just exited. Therefore, on activity start, we say that no item was selected and the nav drawer doesn't change the selected item.
     *
     * @param itemId the id of the selected item in the nav drawer as specified in activity_main_drawer.xml
     * @return if we need to switch the selected item in the navigation drawer.
     */
    private boolean displaySelectedScreen(int itemId) {

        if (currentID == itemId) {
            return false;
        }

        Fragment fragment = getNavFragment(itemId);
        if (fragment != null) {
            switchFragment(fragment, itemId);
            return true;
        } else {
            startNavActivity(itemId);
        }
        return false;
    }


    /**
     * @param itemId the selected item's ID as specified in activity_main_drawer.xml
     * @return the fragment associated to the ID in the navigation drawer. If none, return null.
     */
    private Fragment getNavFragment(int itemId) {

        Fragment fragment = null;

        switch (itemId) {
            case R.id.nav_feed:
                currentID = R.id.nav_feed;
                currentTag = TAG_FEED;
                fragment = feedFragment;
                if (fragment == null) {
                    fragment = new BlankFragment();
                    feedFragment = fragment;
                }
                fab.show();
                break;
            case R.id.nav_explore:
                currentID = R.id.nav_explore;
                Log.i(LOG, "trying to open explore fragment");
                Log.i(LOG, "write perm = " + mWriteExternalStorageGranted);
                if (mWriteExternalStorageGranted) {
                    currentTag = TAG_EXPLORE;
                    fragment = osmFragment;
                    if (fragment == null) {
                        fragment = OSMFragment.newInstance(
                                new GeoPoint(50.633621, 3.0651845),
                                9,
                                new MapOptions()
                                        .setEnableLocationOverlay(true)
                                        .setEnableCompass(true)
                                        .setEnableMultiTouchControls(true)
                                        .setEnableScaleOverlay(true)
                        );
                        osmFragment = fragment;
                    }
                } else {
                    currentTag = TAG_PERMISSIONS_FRAG;
                    // TODO add new fragment asking permissions instead of having to reset the nav drawer to the main fragment
//                    askSpecificPermission(this, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    List<Integer> permissionsToRequest = new ArrayList<>();
                    permissionsToRequest.add(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    permissionsToRequest.add(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    fragment = PermissionsFragment.newInstance(permissionsToRequest);
                }
                fab.hide();
                break;
        }
        return fragment;
    }

    private void switchFragment(Fragment fragment, int itemId) {
        if (fragment != null) {
            if (itemId == nav_feed) {
                // here we replace content_frame with the main fragment. We also clear the back stack so a back press from the home fragment
                // exits the app instead of switching fragments
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.content_frame, fragment, currentTag)
                        .commit();
                getSupportFragmentManager().executePendingTransactions();

                getSupportFragmentManager().popBackStack();
                Log.i(LOG, "replaced content_frame with " + tag);
            } else {
                // here we add to content_frame so that a back press will go backwards in the stack instead of exiting the app.
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.content_frame, fragment, currentTag)
                        .addToBackStack(currentTag)
                        .commit();

                getSupportFragmentManager().executePendingTransactions();
                Log.i(LOG, "add to content_frame with " + tag);
            }
        }
    }

    /**
     * @param itemId the item ID from activity_main_drawer which specified an activity to launch.
     * @return if we found an activity corresponding to itemId and switched activity
     */
    private boolean startNavActivity(int itemId) {
        switch (itemId) {
            case R.id.nav_offline_areas:
                Intent offlineIntent = new Intent(this, OfflineAreas.class);
                startActivity(offlineIntent);
                return true;
            case R.id.nav_settings:
                Snackbar.make(findViewById(R.id.content_frame), "Replace with your own action", Snackbar.LENGTH_LONG).show();
                return false;
            case R.id.nav_help:
                Snackbar.make(findViewById(R.id.content_frame), "Replace with your own action", Snackbar.LENGTH_LONG).show();
                return false;
            default:
                return false;
        }
    }

    /**
     * Asks for permission to all dangerous permissions declared in the manifest.
     */
    private void verifyAllPermissions() {
        askSpecificPermission(this, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        askSpecificPermission(this, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public static void askSpecificPermission(Activity activity, int permission) {
        Log.i(LOG, "asking for permission int = " + permission);
        switch (permission) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                int fineGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (fineGranted != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    mLocationAccessGranted = true;
                }
                break;
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                Log.i(LOG, "checking for write permissions");
                int writeGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (writeGranted != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showExplanation(activity, "External sdcard access permission request", "We need to save the maps to your device", Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    Log.i(LOG, "write permission already granted");
                    mWriteExternalStorageGranted = true;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                mLocationAccessGranted = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationAccessGranted = true;
                }
            }
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                mWriteExternalStorageGranted = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    mWriteExternalStorageGranted = true;
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    mWriteExternalStorageGranted = false;
                }
            }
        }
    }

    /**
     * Shows AlertDialog to explain permission before the actual permisison dialog
     *
     * @param title
     * @param message
     * @param permission
     * @param permissionRequestCode
     */
    public static void showExplanation(final Activity activity, String title,
                                       String message,
                                       final String permission,
                                       final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(activity, new String[]{permission}, permissionRequestCode);
                    }
                });
        builder.create().show();
    }
}
