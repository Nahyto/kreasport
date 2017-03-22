package fr.univ_lille1.iut_info.caronic.mapsv3.maps.other;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.RectangleView;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils;

/**
 * Created by Christopher Caroni on 17/03/2017.
 * Creates TextView info for the area selection
 */
public class MapLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private Activity activity;
    private MapView mMapView;
    private RelativeLayout relativeLayout;

    /**
     * Offsets of obscured rectangle on MapView
     */
    private double sideAndTopOffsetPercentage;
    private double bottomOffsetPercentage;

    private static final int boundingBoxStrokeWidth = 20;

    private TextView tvDownloadSize;
    private TextView tvSpaceAvailable;

    public MapLayoutListener(Activity activity, MapView mapView, RelativeLayout relativeLayout, double sideAndTopOffsetPercentage, double bottomOffsetPercentage) {
        this.activity = activity;
        this.mMapView = mapView;
        this.relativeLayout = relativeLayout;
        this.sideAndTopOffsetPercentage = sideAndTopOffsetPercentage;
        this.bottomOffsetPercentage = bottomOffsetPercentage;
    }

    /**
     * Redraw info on layout change.
     */
    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            mMapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        drawOverlay();

        setupDownloadSize();
        updateDownloadSize();

        relativeLayout.addView(tvDownloadSize);

        // Use touchlistener because user can change zoom without the map actually being rescaled
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateDownloadSize();
                return false;
            }
        });
    }

    /**
     * Creates the {@link TextView} displaying the download size.
     */
    private void setupDownloadSize() {
        tvDownloadSize = new TextView(activity.getApplicationContext());

        int padding = Utils.getPixelsFromDIP(activity, 15);
        tvDownloadSize.setTextColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));
        tvDownloadSize.setPadding(0, padding / 3, 0, padding);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lp.addRule(RelativeLayout.ABOVE, tvSpaceAvailable.getId());

        tvDownloadSize.setLayoutParams(lp);
    }

    /**
     * Draws rectangle overlay to mask part of the map.
     * Rectangles defined by {@link MapLayoutListener#bottomOffsetPercentage} and {@link MapLayoutListener#sideAndTopOffsetPercentage};
     */
    private void drawOverlay() {
        // outer rectangle offsets
        int leftAndTopOffset = (int) (mMapView.getWidth() * sideAndTopOffsetPercentage);
        int rightOffset = mMapView.getWidth() - leftAndTopOffset;
        int bottomOffset = (int) (mMapView.getHeight() - (mMapView.getHeight() * bottomOffsetPercentage));

        // INSIDE STROKED RECTANGLE
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(boundingBoxStrokeWidth);
        paint.setColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorPrimaryDark));

        int leftAndTop = leftAndTopOffset + (boundingBoxStrokeWidth / 2);
        int right = rightOffset - (boundingBoxStrokeWidth / 2);
        int bottom = bottomOffset - (boundingBoxStrokeWidth / 2);
        relativeLayout.addView(new RectangleView(activity, leftAndTop, leftAndTop, right, bottom, paint));

        // OUTSIDE FILLED RECTANGLES
        paint = new Paint(Color.GRAY);
        paint.setAlpha(150);
        paint.setStyle(Paint.Style.FILL);

        // top side
        relativeLayout.addView(new RectangleView(activity, 0, 0, mMapView.getWidth(), leftAndTopOffset, paint));
        // left side
        relativeLayout.addView(new RectangleView(activity, 0, leftAndTopOffset, leftAndTopOffset, mMapView.getHeight(), paint));
        // right side
        relativeLayout.addView(new RectangleView(activity, rightOffset, leftAndTopOffset, mMapView.getWidth(), mMapView.getHeight(), paint));
        // bottom
        relativeLayout.addView(new RectangleView(activity, leftAndTopOffset, bottomOffset, mMapView.getWidth() - leftAndTopOffset, mMapView.getHeight(), paint));
    }

    private void updateDownloadSize() {
        BoundingBox boundingBox = mMapView.getBoundingBox();
        BoundingBox realSize = Utils.getBoundingBoxWithOffset(boundingBox, sideAndTopOffsetPercentage, bottomOffsetPercentage);
        double downloadSize = Utils.getDownloadSizeForBoundingBox(realSize, mMapView);

        int rounded = (int) Math.round(downloadSize);
        String infoText = getDownloadSizeInfoText(rounded);
        tvDownloadSize.setText(infoText);
    }

    /**
     * @return a String formatted from {@link fr.univ_lille1.iut_info.caronic.mapsv3.R.string#offline_area_selection_download_size_info}
     */
    private String getDownloadSizeInfoText(int downloadMB) {
        return String.format(activity.getResources().getString(R.string.offline_area_selection_download_size_info), downloadMB);
    }
}
