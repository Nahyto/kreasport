package fr.univ_lille1.iut_info.caronic.mapsv3.maps.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity;
import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.adapter.DownloadedItemAdapter;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.adapter.DownloadedItemModel;

import static android.content.Context.MODE_PRIVATE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
import static fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity.mWriteExternalStorageGranted;

public class OfflineAreas extends AppCompatActivity {

    private static final String LOG = OfflineAreas.class.getSimpleName();

    public static final int REQUEST_DOWNLOAD_AREA = 1;

    private static final String KEY_DOWNLOADED_AREAS_LIST = "mapsv3.downloaded_areas_list";


    public static Type ListDownloadedItemsType = new TypeToken<ArrayList<DownloadedItemModel>>(){}.getType();

    private Gson gson;
    private static List<DownloadedItemModel> downloadedAreas;
    private DownloadedItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_areas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.offline_areas_action_bar_title);

        setDownloadedAreas();

        setButtonActions();

    }

    private void setDownloadedAreas() {

//        Clears preferences. Call this for debugging purposes
//        getPreferences(MODE_PRIVATE)
//                .edit()
//                .clear()
//                .commit();

        gson = new Gson();

        downloadedAreas = getDownloadedAreasFromPrefs();

        ListView listView = (ListView) findViewById(R.id.list_view_downloaded_areas);
        if (downloadedAreas == null) {
            downloadedAreas = new ArrayList<>();
            initNoDownloadedAreas();
        }
        adapter = new DownloadedItemAdapter(downloadedAreas, getApplicationContext());
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                DownloadedItemModel dataModel = downloadedAreas.get(position);
                Snackbar.make(view, "TODO Implement new activity", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setButtonActions() {
        Button customAreaButton = (Button) findViewById(R.id.custom_area_button);
        customAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWriteExternalStorageGranted) {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Log.i(LOG, "external storage isn't mounted");

                        AlertDialog.Builder builder = new AlertDialog.Builder(OfflineAreas.this);
                        builder.setTitle("Error");
                        builder.setMessage("Cannot access the external storage. This is needed to download the maps.")
                                .setPositiveButton("OK", null);
                        builder.show();
                        return;
                    }

                    Intent areaSelection = new Intent(OfflineAreas.this, OfflineAreaSelection.class);
                    startActivityForResult(areaSelection, REQUEST_DOWNLOAD_AREA);
                } else {
                    MainActivity.askSpecificPermission(OfflineAreas.this, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    private void initNoDownloadedAreas() {
        TextView tvNoAreas = (TextView) findViewById(R.id.offline_areas_no_downloaded_areas_text);
        tvNoAreas.setText(R.string.offline_areas_no_areas_info);
        tvNoAreas.setVisibility(View.VISIBLE);
    }

    private void removeNoDownloadedAreas() {
        TextView tvNoAreas = (TextView) findViewById(R.id.offline_areas_no_downloaded_areas_text);
        tvNoAreas.setVisibility(View.GONE);
    }

    /**
     * Uses Gson to restore downloaded areas.
     * @return
     */
    private ArrayList<DownloadedItemModel> getDownloadedAreasFromPrefs() {
        String jsonAreaList = getPreferences(MODE_PRIVATE).getString(KEY_DOWNLOADED_AREAS_LIST, "");

        return gson.fromJson(jsonAreaList, ListDownloadedItemsType);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveDownloadedAreasToPrefs();
        super.onSaveInstanceState(outState);
    }

    /**
     * Uses Gson to save downloaded areas
     */
    private void saveDownloadedAreasToPrefs() {
        String jsonAreaList = gson.toJson(downloadedAreas, ListDownloadedItemsType);
        Log.i("JSON SAVE", jsonAreaList);
        getPreferences(MODE_PRIVATE).edit()
                .putString(KEY_DOWNLOADED_AREAS_LIST, jsonAreaList)
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DOWNLOAD_AREA) {
            if (resultCode == RESULT_OK) {
                Log.i(LOG, "received download intent");

                // TODO change DownloadedItemModel to include progressbar and update download progress
                String name = data.getStringExtra(OfflineAreaSelection.KEY_AREA_NAME);
                String absolutePath = data.getStringExtra(OfflineAreaSelection.KEY_AREA_FILE_ABSOLUTE_PATH);
                int estimatedSize = data.getIntExtra(OfflineAreaSelection.KEY_AREA_SIZE, 0);

                removeNoDownloadedAreas();
                downloadedAreas.add(new DownloadedItemModel(name, absolutePath, estimatedSize));
                adapter.notifyDataSetChanged();
                saveDownloadedAreasToPrefs(); // save right away
            }
        }
    }

}
