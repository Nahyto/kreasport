package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import android.content.Context;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class CustomItemizedIconOverlay<Item extends OverlayItem> extends ItemizedOverlayWithFocus<Item> {

    private Context context;
    private List listItems;


    public CustomItemizedIconOverlay(Context pContext, List aList, OnItemGestureListener aOnItemTapListener) {
        super(pContext, aList, aOnItemTapListener);

        this.context = pContext;
        this.listItems = aList;
    }

    @Override
    public boolean addItem(final Item item) {
        final boolean result = mItemList.add(item);
        return result;
    }

    public static ItemizedIconOverlay.OnItemGestureListener getListener(final Context context) {
        return new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(
                        context,
                        "Item '" + item.getTitle() + "' (index=" + index
                                + ") got single tapped up", Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                Toast.makeText(
                        context,
                        "Item '" + item.getTitle() + "' (index=" + index
                                + ") got long pressed", Toast.LENGTH_LONG).show();
                return false;
            }
        };
    }
}
