package au.net.winehound.ui.views.edmondsans;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import au.net.winehound.LogTags;


public class EdmondSansButton extends Button {

    public EdmondSansButton(Context context) {
        super(context);
        initFont();
    }

    public EdmondSansButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public EdmondSansButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont();
    }

    private void initFont(){
        // Quick exit - we are in edit mode
        if(isInEditMode()){
            return;
        }

        setTypeface(EdmondSansUtils.getBoldTypeface(getContext()));
    }

}
