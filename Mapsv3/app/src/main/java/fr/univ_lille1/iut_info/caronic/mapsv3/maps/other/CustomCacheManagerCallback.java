package fr.univ_lille1.iut_info.caronic.mapsv3.maps.other;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqliteArchiveTileWriter;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.activities.OfflineAreas;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Master on 19/03/2017.
 */

public class CustomCacheManagerCallback implements CacheManager.CacheManagerCallback {

    private final String LOG = CustomCacheManagerCallback.class.getSimpleName();
    private final int ERROR_DOWNLOAD = 1;
    private final int SUCCESS_DOWNLOAD = ERROR_DOWNLOAD + 1;
    private final int ONGOING_DOWNLOAD = SUCCESS_DOWNLOAD + 1;
    private final int START_DOWNLOAD = ONGOING_DOWNLOAD + 1;

    private Activity activity;
    private SqliteArchiveTileWriter writer;
    private int notificationID;
    private NotificationManager mNotifyMgr;

    private int possibleTiles;
    private String downloadingAreaName;

    public CustomCacheManagerCallback(Activity activity, SqliteArchiveTileWriter writer, String downloadingAreaName) {
        this.activity = activity;
        this.writer = writer;
        this.notificationID = 42;
        this.downloadingAreaName = downloadingAreaName;

        this.mNotifyMgr = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onTaskComplete() {
        Log.i(LOG, "Download complete!");
        Log.i(LOG, "Last tile size: " + possibleTiles);
        if (writer != null)
            writer.onDetach();

        NotificationCompat.Builder mBuilder = createNotification(SUCCESS_DOWNLOAD, downloadingAreaName, 0);
        mNotifyMgr.notify(notificationID, mBuilder.build());
    }

    @Override
    public void onTaskFailed(int errors) {
        Log.i(LOG, "Download complete with " + errors + " errors");
        if (writer != null)
            writer.onDetach();

        NotificationCompat.Builder mBuilder = createNotification(ERROR_DOWNLOAD, downloadingAreaName, 0);
        mNotifyMgr.notify(notificationID, mBuilder.build());

    }

    @Override
    public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
//            Log.i(LOG, "progress " + progress);

        NotificationCompat.Builder mBuilder = createNotification(ONGOING_DOWNLOAD, downloadingAreaName, progress);
        mNotifyMgr.notify(notificationID, mBuilder.build());
    }

    @Override
    public void downloadStarted() {
        Log.i(LOG, "started download");

        NotificationCompat.Builder mBuilder = createNotification(START_DOWNLOAD, downloadingAreaName, 0);
        mNotifyMgr.notify(notificationID, mBuilder.build());
    }

    @Override
    public void setPossibleTilesInArea(int total) {
        Log.i(LOG, "actual tile size: " + total);
        possibleTiles = total;
    }

    private NotificationCompat.Builder createNotification(int status, String downloadingAreaName, int progress) {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(activity)
                .setSmallIcon(R.drawable.ic_map_black_24px)
                .setContentTitle(downloadingAreaName);
        String content = "";
        switch (status) {
            case ERROR_DOWNLOAD:
                content = String.format(activity.getString(R.string.offline_area_selection_notification_error), downloadingAreaName);
                break;
            case ONGOING_DOWNLOAD:
                content = activity.getString(R.string.offline_area_selection_notification_ongoing, progress, possibleTiles);
                mBuilder.setProgress(possibleTiles, progress, false);
                break;
            case SUCCESS_DOWNLOAD:
                content = String.format(activity.getString(R.string.offline_area_selection_notification_success), downloadingAreaName);
                mBuilder.setProgress(0, 0, false);
                break;
            case START_DOWNLOAD:
                content = String.format(activity.getString(R.string.offline_area_selection_notification_start), downloadingAreaName);
                break;
            default:
                break;
        }

        mBuilder.setContentText(content);
        setPendingIntent(mBuilder);

        return mBuilder;
    }

    private NotificationCompat.Builder setPendingIntent(NotificationCompat.Builder mBuilder) {
        Intent resultIntent = new Intent(activity.getApplicationContext(), OfflineAreas.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity.getApplicationContext()); // to preserve navigation
        stackBuilder.addParentStack(OfflineAreas.class); // adds all parents of OfflineAreas to the stack
        stackBuilder.addNextIntent(resultIntent); // sets the the most recent intent to the intent we just created

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT); // get a pending intent from the stack, will get the one we just created

        mBuilder.setContentIntent(pendingIntent);
        return mBuilder;
    }
}
