package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import static android.R.attr.x;
import static android.R.attr.y;

/**
 * Created by Master on 16/03/2017.
 */

public class RectangleView extends View {

    private Rect rectangle;
    private Paint paint;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public RectangleView(Context context, int left, int top, int right, int bottom, Paint paint) {
        super(context);

        // create a rectangle that we'll draw later
        rectangle = new Rect(left, top, right, bottom);

        this.paint = paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rectangle, paint);
    }

}
