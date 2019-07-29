package au.net.winehound.ui.views.edmondsans;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckedTextView;
import android.widget.EditText;

import au.net.winehound.LogTags;


public class EdmondSansCheckedTextView extends CheckedTextView {

    public EdmondSansCheckedTextView(Context context) {
        super(context);
        initFont();
    }

    public EdmondSansCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont();
    }

    public EdmondSansCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont();
    }

    private void initFont(){
        // Quick exit - we are in edit mode
        if(isInEditMode()){
            return;
        }

        setTypeface(EdmondSansUtils.getTypeface(getContext()));
    }

}
