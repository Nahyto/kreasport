package fr.univ_lille1.iut_info.caronic.mapsv3;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments.BlankFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments.PermissionsFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.activities.OfflineAreas;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.fragments.OSMFragment;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;

import static fr.univ_lille1.iut_info.caronic.mapsv3.R.id.nav_explore;
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

    private final static String TAG_FEED = "mapsv3.tag_feed";
    private final static String TAG_EXPLORE = "mapsv3.tag_explore";
    private final static String TAG_PERMISSIONS_FRAG = "mapsv3.tag_permissions";

    private final static String KEY_CURRENT_TAG = "mapsv3.key_current_tag";

    private Fragment storeFeedFragment;
    private Fragment storeExploreFragment;
    private Fragment storePermissionsFragment;

    /**
     * Variables to save which fragment is currently being displayed. NOT to use for switching activities.
     */
    private String currentTag;
    private int currentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (savedInstanceState != null) {
            String oldTag = savedInstanceState.getString(KEY_CURRENT_TAG);
            if (oldTag != null) {
                int newId = getFragmentIDByTag(oldTag);
                displaySelectedScreen(newId);
            }
        } else {
            displaySelectedScreen(R.id.nav_feed);
        }

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
                fragment = storeFeedFragment;
                if (fragment == null) {
                    Log.d(LOG, "creating new blank fragment");
                    fragment = BlankFragment.newInstance();
                    storeFeedFragment = fragment;
                }
                fab.show();
                break;
            case R.id.nav_explore:
                currentID = R.id.nav_explore;
                if (Build.VERSION.SDK_INT > 22) {
                    if (mWriteExternalStorageGranted) {
                        fragment = setFragToOSMFrag();
                    } else {
                        currentTag = TAG_PERMISSIONS_FRAG;
                        fragment = storePermissionsFragment;
                        if (fragment == null) {

                            List<Integer> permissionsToRequest = new ArrayList<>();
                            permissionsToRequest.add(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            permissionsToRequest.add(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            fragment = PermissionsFragment.newInstance(permissionsToRequest);

                            storePermissionsFragment = fragment;
                        }
                    }
                } else {
                    fragment = setFragToOSMFrag();
                }
                fab.hide();
                break;
            default:
                Log.w(LOG, "getting fragment but id was incorrect");
                break;
        }
        return fragment;
    }

    private Fragment setFragToOSMFrag() {
        Fragment fragment;
        currentTag = TAG_EXPLORE;
        fragment = storeExploreFragment;
        if (fragment == null) {
            Log.d(LOG, "creating new osm fragment");
            fragment = OSMFragment.newInstance(
                    new GeoPoint(50.633621, 3.0651845),
                    9,
                    new MapOptions()
                            .setEnableLocationOverlay(true)
                            .setEnableCompass(true)
                            .setEnableMultiTouchControls(true)
                            .setEnableScaleOverlay(true)
            );
            storeExploreFragment = fragment;
        }
        return fragment;
    }

    private int getFragmentIDByTag(String tag) {
        switch (tag) {
            case TAG_FEED:
                return R.id.nav_feed;
            case TAG_EXPLORE:
                return R.id.nav_explore;
            case TAG_PERMISSIONS_FRAG:
                return R.id.nav_explore;
            default:
                return -1;
        }
    }

    private void switchFragment(Fragment fragment, int itemId) {
        if (fragment != null) {

            removeOldFragment();
            addNewFragmentToContentFrame(fragment, itemId);
        }
    }

    private void removeOldFragment() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (frag != null) {
            Log.d(LOG, "removed from content_frame with tag = " + frag.getTag());
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    private void addNewFragmentToContentFrame(Fragment fragment, int itemId) {
        switch (itemId) {
            case nav_feed:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_frame, fragment, currentTag)
                        .commit();

                getSupportFragmentManager().executePendingTransactions();
                getSupportFragmentManager().popBackStack();

                Log.d(LOG, "added to content_frame with " + fragment.getTag());
                break;
            case nav_explore:
                // here we add to content_frame so that a back press will go backwards in the stack instead of exiting the app.
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.content_frame, fragment, currentTag)
                        .addToBackStack(currentTag)
                        .commit();

                Log.d(LOG, "added to content_frame with " + fragment.getTag());
                getSupportFragmentManager().executePendingTransactions();
                break;
            default:
                Log.w(LOG, "was expected to add fragment but id was incorrect");
                break;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG, "saving current tag with " + currentTag);
        outState.putString(KEY_CURRENT_TAG, currentTag);
        super.onSaveInstanceState(outState);
    }

    public static void askSpecificPermission(Activity activity, int permission) {
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
                int writeGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (writeGranted != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
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
                    mWriteExternalStorageGranted = true;
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

    public void downloadJson(View view) {

        JsonTask task = new JsonTask();
        task.execute("https://10.0.2.2:8080/v1/parcours");
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        TextView tv = (TextView) frag.getView().findViewById(R.id.json_text_view);

        String downloadedText = task.getJsonText();
        if (downloadedText != null && !downloadedText.equals("")) {
            tv.setText(task.getJsonText());
        } else {
            tv.setText("Could not download the run");
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        private String jsonText;

        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(MainActivity.this, "please wait", Toast.LENGTH_SHORT).show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                Log.i(LOG, "connection to " + params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivity.this, "end execution", Toast.LENGTH_SHORT).show();
            jsonText = result;
        }

        public String getJsonText() {
            return jsonText;
        }
    }


}
