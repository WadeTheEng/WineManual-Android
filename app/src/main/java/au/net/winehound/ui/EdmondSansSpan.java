package au.net.winehound.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

/**
 * Styles its text in the edmond sans font - bold
 */
public class EdmondSansSpan extends MetricAffectingSpan {

    private Context context;

    public EdmondSansSpan(Context context) {
        this.context = context;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(EdmondSansUtils.getTypeface(context));

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(EdmondSansUtils.getTypeface(context));
        tp.setFakeBoldText(true);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}