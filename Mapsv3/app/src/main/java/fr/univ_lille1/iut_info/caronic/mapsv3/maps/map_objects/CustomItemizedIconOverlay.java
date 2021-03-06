package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class CustomItemizedIconOverlay<Item extends OverlayItem> extends ItemizedOverlayWithFocus<Item> {

    private Context context;
    private List listItems;


    public CustomItemizedIconOverlay(Context pContext, List<Item> aList, OnItemGestureListener aOnItemTapListener) {
        super(pContext, aList, aOnItemTapListener);

        this.context = pContext;
        this.listItems = aList;
    }


}
