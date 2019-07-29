package au.net.winehound.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collection;

import au.net.winehound.R;
import au.net.winehound.domain.Photograph;

/**
 * Abstract super class for activities which have a transparent action bar, showing an header image.
 *
 * Whats this you say?  There are no activities like that in winehound?!!  Well, your right.  It used
 * to work that way using the FadingActionbar library, but unfortunately the following bug stopped us:
 * https://github.com/ManuelPeinado/FadingActionBar/issues/35
 *
 * This class is kept in, so that when the issue is resolved it should be easy to add the fading
 * action bar back in.
 */
@EActivity
public abstract class AbstractTransparentAbActivity extends AbstractWinehoundActivity {

    @ViewById(R.id.header_image)
    protected ImageView headerImage;

    @ViewById(R.id.header_num_images)
    protected TextView numImagesText;

    @ViewById(R.id.header_num_images_gradient)
    protected View numImagesGradient;

    protected ImageView favouriteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(container);

        // Build the header
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.image_header, container, true);

        // Build the contents
        getLayoutInflater().inflate(getLayoutId(), container, true);

        favouriteButton = (ImageView) header.findViewById(R.id.header_favourite_button);
        if (!createFavouriteButton()) {
            favouriteButton.setVisibility(View.GONE);
        }

        setContentView(scrollView);
    }

    @Click(R.id.header_image)
    protected void headerClicked() {
        // Quick exit - no photographs
        if (getPhotographs().isEmpty()) {
            return;
        }


        ArrayList<String> urls = new ArrayList<String>();
        for (Photograph photograph : getPhotographs()) {
            urls.add(photograph.getFullSizeUrl());
        }

        startActivity(ViewImagesActivity.start(this, getTitle().toString(), urls));
    }

    protected abstract Collection<Photograph> getPhotographs();

    protected void loadCroppedImage(String url, int placeholder) {

        // Get screen width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Get header height
        int height = getResources().getDimensionPixelSize(R.dimen.header_image_height);

        Picasso.with(this).load(url).resize(width, height).centerCrop().placeholder(placeholder).into(headerImage);

        if(getPhotographs().size() > 1){
            numImagesText.setText("1 of " + getPhotographs().size());
            numImagesGradient.setVisibility(View.VISIBLE);
        }
    }

    protected void loadPlaceholder(int placeholder) {
        Picasso.with(this).load(R.drawable.winery_placeholder).into(headerImage);
    }

    protected abstract int getLayoutId();

    @Click(R.id.header_favourite_button)
    protected void onFavouriteClick() {
        // By default do nothing
    }

    /**
     * Orveridable method that indicates if we should show the favourite button.  By default returns
     * false.  Overide to return true if you want a fav button.
     *
     * @return a boolean indicating if the fav button should be shown
     */
    protected boolean createFavouriteButton() {
        return false;
    }
}
