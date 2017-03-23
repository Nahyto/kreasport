package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Created by minfi on 23/03/2017.
 */

public class ItemizeIconOverlay implements ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
    Context context;
    public ItemizeIconOverlay(Context context){
        this.context = context;
    }
    @Override
    public boolean onItemSingleTapUp(int index, OverlayItem item) {
        Toast.makeText(
                context,
                "Item '" + item.getTitle() + "' (index=" + index
                        + ") got single tapped up", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onItemLongPress(final int index, final OverlayItem item) {
        Toast.makeText(context
                ,
                "Item '" + item.getTitle() + "' (index=" + index
                        + ") got long pressed", Toast.LENGTH_LONG).show();
        return false;
    }
}
