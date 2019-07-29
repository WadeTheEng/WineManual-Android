package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.util.List;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.ui.fragments.WineriesMapFragment;
import au.net.winehound.ui.views.EventView;
import au.net.winehound.ui.views.EventView_;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EActivity
public class FeaturedEventActivity extends AbstractEventActivity {

    private static final int ZOOM_LEVEL = 10;

    @ViewById(R.id.featured_event_about_text)
    protected WebView eventAbout;

    @ViewById(R.id.featured_event_whats_on_view)
    protected View whatsOnView;

    @ViewById(R.id.featured_event_contact_view)
    protected View contactView;

    @ViewById(R.id.featured_event_whats_on_indicator)
    protected View whatsOnIndicator;

    @ViewById(R.id.featured_event_about_indicator)
    protected View aboutIndicator;

    @ViewById(R.id.featured_event_contact_indicator)
    protected View contactIndicator;

    @ViewById(R.id.featured_event_website)
    protected View websiteView;

    @ViewById(R.id.featured_event_website_text)
    protected TextView websiteText;

    @ViewById(R.id.featured_event_about_more)
    protected View moreButton;

    @ViewById(R.id.featured_event_whats_on_progress)
    protected ProgressBar eventsProgress;

    @ViewById(R.id.featured_event_whats_on_list)
    protected LinearLayout eventsList;

    @ViewById(R.id.featured_event_no_events)
    protected TextView noEvents;

    @FragmentById(R.id.featured_event_map)
    protected WineriesMapFragment mapFrag;

    @AnimationRes(R.anim.rotate_down)
    protected Animation rotateDown;

    @AnimationRes(R.anim.rotate_up)
    protected Animation rotateUp;

    private boolean aboutTruncated = false;

    // Each of these sections is loaded when they are first expanded.  Otherwise loading them
    // when the activity is first displayed slows down launching the activity too much.
    private boolean whatsOnLoaded;
    private boolean aboutLoaded;
    private boolean contactLoaded;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_featured_event;
    }

    @AfterViews
    protected void setupUi() {
        if (event.getWebsite().isEmpty()) {
            websiteView.setVisibility(View.GONE);
        } else {
            websiteText.setText(event.getWebsite());
        }

        // Hide views
        eventAbout.setVisibility(View.GONE);
        moreButton.setVisibility(View.GONE);
        whatsOnView.setVisibility(View.GONE);
        contactView.setVisibility(View.GONE);
    }

    @Background
    protected void downloadEvents() {
        try{
            service.downloadAndSaveFeaturedEventEvents(event.getId());
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error getting region events", e);
        }
        displayEvents(true);
    }

    @UiThread
    protected void displayEvents(boolean hideProgress) {
        if(isActivityDestroyed()){
            // Quick exit - we are no longer visible
            return;
        }

        if (hideProgress) {
            eventsProgress.setVisibility(View.GONE);
        }

        // Quick exit - there are no events!
        final List<Event> events = service.getFeaturedEventEvents(event.getId());
        if (events.isEmpty() && hideProgress) {
            noEvents.setVisibility(View.VISIBLE);
            return;
        } else {
            noEvents.setVisibility(View.GONE);
        }

        eventsList.removeAllViews();
        for (final Event event : events) {
            EventView eventView = EventView_.build(this);
            eventView.setEvent(event);
            eventsList.addView(eventView);
        }
    }

    @Click(R.id.featured_event_whats_on)
    protected void whatsOnClicked() {

        if(!whatsOnLoaded){
            downloadEvents();
            displayEvents(false);
            whatsOnLoaded = true;
        }

        if (whatsOnView.getVisibility() == View.VISIBLE) {
            whatsOnView.setVisibility(View.GONE);
            whatsOnIndicator.startAnimation(rotateUp);
        } else {
            whatsOnView.setVisibility(View.VISIBLE);
            whatsOnIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.featured_event_about)
    protected void aboutClicked() {

        if(!aboutLoaded){
            aboutTruncated = EdmondSansUtils.loadIntoWebview(eventAbout, event.getDescription());
            aboutLoaded = true;
        }

        if (eventAbout.getVisibility() == View.VISIBLE) {
            eventAbout.setVisibility(View.GONE);
            moreButton.setVisibility(View.GONE);
            aboutIndicator.startAnimation(rotateUp);
        } else {
            eventAbout.setVisibility(View.VISIBLE);
            aboutIndicator.startAnimation(rotateDown);
            if (aboutTruncated) {
                moreButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Click(R.id.featured_event_driving_directions)
    protected void drivingDirectionsClicked() {
        IntentUtils.navigate(this, event.getLatitude(), event.getLongitude());
    }

    @Click(R.id.featured_event_website)
    protected void websiteClicked() {
        String website = event.getWebsite();
        IntentUtils.viewWebsite(this, website);
    }

    @Click(R.id.featured_event_about_more)
    protected void moreClicked() {
        startActivity(HtmlActivity.start(this, R.string.about, event.getDescription()));
    }

    @Click(R.id.featured_event_contact)
    protected void contactClicked() {

        if(!contactLoaded){
            mapFrag.setStaticMap(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                }
            });
            mapFrag.display(event.getLatitude(), event.getLongitude(), ZOOM_LEVEL);
            contactLoaded = true;
        }

        if (contactView.getVisibility() == View.VISIBLE) {
            contactView.setVisibility(View.GONE);
            contactIndicator.startAnimation(rotateUp);
        } else {
            contactView.setVisibility(View.VISIBLE);
            contactIndicator.startAnimation(rotateDown);
        }
    }

    public static Intent start(Context context, Event event) {
        Intent i = new Intent(context, FeaturedEventActivity_.class);
        i.putExtra(EXTRA_EVENT, event);
        return i;
    }
}
