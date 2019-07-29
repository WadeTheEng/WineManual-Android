package au.net.winehound.ui.views.edmondsans;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.webkit.WebView;

import au.net.winehound.LogTags;

public class EdmondSansUtils {

    public static final int MAX_ABOUT_LENGTH = 500;

    // Static instance of our typeface
    private static Typeface typeface;
    private static Typeface boldTypeface;

    public static Typeface getTypeface(Context context){
        if(typeface == null){
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), "Edmondsans-webfont.ttf");
            } catch (Exception e) {
                Log.v(LogTags.UI, "Error loading typeface", e);
            }
        }
        return typeface;
    }

    public static Typeface getBoldTypeface(Context context){
        if(boldTypeface == null){
            try {
                boldTypeface = Typeface.createFromAsset(context.getAssets(), "Edmondsans-Medium.ttf");
            } catch (Exception e) {
                Log.v(LogTags.UI, "Error loading typeface", e);
            }
        }
        return boldTypeface;
    }

    /**
     * Loads and displays html text into a webview.  The html text may be truncated if it is longer
     * than MAX_ABOUT_LENGTH characters.  A boolean will be returned indicating if the HTML was
     * truncated.
     *
     * @param webView The webview to add the HTML into
     * @param htmlSnippit The html to add in.
     * @return true if the HTML was trucated to MAX_ABOUT_LENGTH
     */
    public static boolean loadIntoWebview(WebView webView, String htmlSnippit) {
        return loadIntoWebview(webView, htmlSnippit, true);
    }

    /**
     * Loads and displays html text into a webview.  The html text may optinally be truncated if it
     * is longer than MAX_ABOUT_LENGTH characters.  A boolean will be returned indicating if the HTML was
     * truncated.
     *
     * @param webView The webview to add the HTML into
     * @param htmlSnippit The html to add in.
     * @param trim Should the HTML be truncated to MAX_ABOUT_LENGTH
     * @return true if the HTML was trucated to MAX_ABOUT_LENGTH
     */
    public static boolean loadIntoWebview(WebView webView, String htmlSnippit, boolean trim){
        boolean isHtmlTruncated = false;
        if(trim && htmlSnippit.length() > MAX_ABOUT_LENGTH){
            htmlSnippit = htmlSnippit.substring(0, MAX_ABOUT_LENGTH) + " ...";
            isHtmlTruncated = true;
        }

        String modifiedHtml = "<html>"+
                "<head>"+
                "<style type=\"text/css\">"+
                "@font-face {"+
                "font-family: EdmondSans;"+
                "src: url(\"edmondsans-webfont.ttf\")"+
                "}"+
                "body {"+
                "font-family: EdmondSans;"+
                "}"+
                "</style>"+
                "</head>"+
                "<body>"+  htmlSnippit + "</body></html>";

        webView.loadDataWithBaseURL("file:///android_asset/", modifiedHtml, "text/html", "utf-8", null);

        return isHtmlTruncated;
    }
}
