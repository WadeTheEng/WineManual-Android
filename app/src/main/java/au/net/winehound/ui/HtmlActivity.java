package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EActivity(R.layout.activity_html)
public class HtmlActivity extends AbstractWinehoundActivity {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_HTML = "EXTRA_HTML";

    @ViewById(R.id.html_webview)
    protected WebView webView;

    @Extra(EXTRA_HTML)
    protected String html;

    @Extra(EXTRA_TITLE)
    protected String title;

    @AfterViews
    protected void setupUi() {
        setTitle(new EdmondSansString(title, this));
        EdmondSansUtils.loadIntoWebview(webView, html, false);
    }

    public static Intent start(Context context, int title, String html) {
        Intent intent = new Intent(context, HtmlActivity_.class);
        intent.putExtra(EXTRA_TITLE, context.getResources().getString(title));
        intent.putExtra(EXTRA_HTML, html);

        return intent;
    }

}
