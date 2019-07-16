package net.crowmaster.cardasmarto.widgets;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import net.crowmaster.cardasmarto.R;

/**
 * Created by root on 7/16/16.
 */
public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    //private FormattedStringCache.PrimFloat mFormattedStringCache = new FormattedStringCache.PrimFloat(new DecimalFormat("##0"));

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        //tvContent.setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"));
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float value = e.getY();
        tvContent.setText(String.format("%s", e.getY()));
    }

    @Override
    public MPPointF getOffset() {
        MPPointF offsetPoint = new MPPointF(-((float)getWidth() / 2), -getHeight()-10);
        return offsetPoint;
    }

    //    @Override
//    public int getXOffset(float xpos) {
//        // this will center the marker-view horizontally
//        return -(getWidth() / 2);
//    }
//
//    @Override
//    public int getYOffset(float ypos) {
//        // this will cause the marker-view to be above the selected value
//        return -getHeight()-10;
//    }
}