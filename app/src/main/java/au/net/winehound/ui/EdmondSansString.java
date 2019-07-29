package au.net.winehound.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;


/**
 * A spannable string which wraps its text content in a EdmondSansSpan
 */
public class EdmondSansString extends SpannableString {

    public EdmondSansString(CharSequence source, Context context) {
        super(source);
        setSpan(new EdmondSansSpan(context), 0, length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
