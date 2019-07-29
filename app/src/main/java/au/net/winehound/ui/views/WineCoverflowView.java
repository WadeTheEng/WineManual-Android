package au.net.winehound.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Wine;

/**
 * View for showing a wine bottle in the wine list coverflow view.
 */
@EViewGroup(R.layout.view_wine_coverflow)
public class WineCoverflowView extends FrameLayout{

    @ViewById(R.id.wine_coverflow_image)
    protected ImageView image;

    private Callback downloadedCallback;
    private Wine wine;
    private Map<Integer, Bitmap> imageCache;

    public WineCoverflowView(Context context) {
        super(context);
    }

    public WineCoverflowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WineCoverflowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the wine to be displayed in this view.  The way the image is loaded is somewhat strange.
     * It turns out that if we use picassa to load the image directly into the view, it causes
     * the coverflow widget to lag up and behave strangely.  Instead we have a cache of images
     * hashed by wine ID.  Picassa is still responsible for loading and resizing the image, but
     * the loaded image is then placed in the cache.  The images shown in the view come from the cache
     * they are not added to the view by picassa directly.
     *
     *
     * @param wine The wine to show
     * @param downloadedCallback A callback to run if the wine image has been downloaded by picassa
     * @param imageCache A cache of wine images from which the image views bitmap should be taken.
     *                   If the cache doesn't contain this wines bitmap, it will be loaded in the background
     *                   using picassa
     */
    public void setWine(Wine wine, Callback downloadedCallback, Map<Integer, Bitmap> imageCache){
        this.wine = wine;
        this.downloadedCallback = downloadedCallback;
        this.imageCache = imageCache;

        int width = getContext().getResources().getDimensionPixelSize(R.dimen.coverflow_wine_width);
        int height = getContext().getResources().getDimensionPixelSize(R.dimen.coverflow_wine_height);

        setLayoutParams(new FancyCoverFlow.LayoutParams(width, height));

        if(imageCache.containsKey(wine.getId())){
            image.setImageBitmap(imageCache.get(wine.getId()));
        }
        else{
            image.setImageResource(R.drawable.wine_bottle_dotted);

            if(!wine.getPhotographs().isEmpty()) {
                loadImage(wine.getPhotographs().iterator().next().getThumbUrl(), width, height);
            }
        }
    }

    /**
     * Uses picassa to download a wine image.  Once the download it complete it will be placed in
     * the cache and the callback notified
     *
     * @param url
     * @param width
     * @param height
     */
    @Background
    protected void loadImage(String url, int width, int height){
        try {
            Bitmap bm = Picasso.with(getContext())
                        .load(url)
                        .resize(width, height).centerInside()
                        .get();

            addImageToCache(bm);
        } catch (IOException e) {
            Log.e(LogTags.UI, "Error loading image from " + url, e);
        }
    }

    /**
     * Places the wine image in the cache and calls the callback.  The callback will invalidate
     * the coverflow views adapter causing it to re-draw the views, which will load the image out of
     * the cache
     *
     * @param bm
     */
    @UiThread
    protected void addImageToCache(Bitmap bm){
        imageCache.put(wine.getId(), bm);
        downloadedCallback.onSuccess();
    }
}
