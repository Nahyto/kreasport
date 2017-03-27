package fr.univ_lille1.iut_info.caronic.mapsv3.maps.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by minfi on 24/03/2017.
 */

public class YourReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Toast.makeText(context, "entering area for balise", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "exiting area for balise", Toast.LENGTH_SHORT).show();
        }
    }
}
