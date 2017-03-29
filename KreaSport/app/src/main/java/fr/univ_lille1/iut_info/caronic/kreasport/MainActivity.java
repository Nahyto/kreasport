package fr.univ_lille1.iut_info.caronic.kreasport;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.univ_lille1.iut_info.caronic.kreasport.fragments.BottomSheet;
import fr.univ_lille1.iut_info.caronic.kreasport.fragments.ExploreFragment;
import fr.univ_lille1.iut_info.caronic.kreasport.fragments.HomeFragment;
import fr.univ_lille1.iut_info.caronic.kreasport.fragments.OnFragmentInteractionListener;
import fr.univ_lille1.iut_info.caronic.kreasport.volley.VolleySingleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener {

    private static final String LOG = MainActivity.class.getSimpleName();

    public static final String DOWNLOAD_PUBLIC_RACES = "kreasport.frag_request_code.download_public_races";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private static final String BACKSTACK_REPLACE_WITH_FRAG_EXPLORE = "kreasport.backstack.replace_with_frag_explore";

    private static final String TAG_HOME = "kreasport.tag.home";
    private static final String TAG_EXPLORE = "kreasport.tag.explore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        selectDrawerItem(navigationView.getMenu().getItem(0));
    }

    @Override
    public void onBackPressed() {
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void selectDrawerItem(MenuItem menuItem) {

        hideKeyboard();

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = restoreFragment(R.id.nav_home);

                getSupportFragmentManager().popBackStack(BACKSTACK_REPLACE_WITH_FRAG_EXPLORE, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main_frame_layout, fragment, TAG_HOME)
                        .commit();
                break;
            case R.id.nav_explore:
                fragment = restoreFragment(R.id.nav_explore);
                BottomSheet bottomSheet = BottomSheet.newInstance("", "");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main_frame_layout, fragment, TAG_EXPLORE)
                        .add(R.id.fragment_explore_root_coordlayout, bottomSheet)
                        .addToBackStack(BACKSTACK_REPLACE_WITH_FRAG_EXPLORE)
                        .commit();
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
            default:
                break;
        }

        if (fragment != null) {
            completeDrawerAction(menuItem);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Fragment restoreFragment(int id) {
        Fragment fragment;
        switch (id) {
            case R.id.nav_home:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME);
                if (fragment != null) {
                    Log.d(LOG, "found home in manager");
                    return fragment;
                } else {
                    Log.d(LOG, "created new HomeFragment");
                    return HomeFragment.newInstance("", "");
                }
            case R.id.nav_explore:
                fragment = getSupportFragmentManager().findFragmentByTag(TAG_EXPLORE);
                if (fragment != null) {
                    Log.d(LOG, "found explore in manager");
                    return fragment;
                } else {
                    Log.d(LOG, "created new ExploreFragment");
                    return ExploreFragment.newInstance("", "");
                }
        }
        return null;
    }

    /**
     * Sets the menuItem to be checked and changes the title
     * @param menuItem
     */
    private void completeDrawerAction(MenuItem menuItem) {
        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());
    }

    @Override
    public void onFragmentInteraction(String request) {
        switch (request) {
            case DOWNLOAD_PUBLIC_RACES:
                downloadPublicRaces();
                break;
            default:
                break;
        }
    }

    private void downloadPublicRaces() {

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.download_message));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = getString(R.string.public_races_url);

        StringRequest stringRequest  = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                if (response != null && !response.equals("")) {
                    progressDialog.dismiss();

                    // TODO save json and transfer to ExploreFragment

                } else {
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.download_fail_title)
                        .setMessage(R.string.download_fail_description)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
                Log.d(LOG , "download error : " + error.toString());
            }
        });

        Log.d(LOG, "download requested");
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
