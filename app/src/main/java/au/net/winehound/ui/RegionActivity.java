package au.net.winehound.ui;

import android.app.ProgressDialog;
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
import com.squareup.picasso.Callback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.util.Collection;
import java.util.List;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Region;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.fragments.WineriesMapFragment;
import au.net.winehound.ui.views.EventView;
import au.net.winehound.ui.views.EventView_;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EActivity
public class RegionActivity extends AbstractTransparentAbActivity {

    private static final String EXTRA_REGION = "EXTRA_REGION";


    @Extra(EXTRA_REGION)
    protected Region region;


    @ViewById(R.id.header_name)
    protected TextView regionName;

    @ViewById(R.id.region_about_text)
    protected WebView regionAbout;

    @ViewById(R.id.region_whats_on_view)
    protected View whatsOnView;

    @ViewById(R.id.region_contact_view)
    protected View contactView;

    @ViewById(R.id.region_wineries)
    protected View wineriesView;

    @ViewById(R.id.region_whats_on_indicator)
    protected View whatsOnIndicator;

    @ViewById(R.id.region_about_indicator)
    protected View aboutIndicator;

    @ViewById(R.id.region_contact_indicator)
    protected View contactIndicator;

    @ViewById(R.id.region_email)
    protected View emailView;

    @ViewById(R.id.region_email_text)
    protected TextView emailText;

    @ViewById(R.id.region_website)
    protected View websiteView;

    @ViewById(R.id.region_website_text)
    protected TextView websiteText;

    @ViewById(R.id.region_download_map)
    protected View downloadMapView;

    @ViewById(R.id.region_about_more)
    protected View moreButton;

    @ViewById(R.id.region_whats_on_progress)
    protected ProgressBar eventsProgress;

    @ViewById(R.id.region_whats_on_list)
    protected LinearLayout eventsList;

    @ViewById(R.id.region_no_events)
    protected TextView noEvents;

    @FragmentById(R.id.region_map)
    protected WineriesMapFragment mapFrag;

    @AnimationRes(R.anim.rotate_down)
    protected Animation rotateDown;

    @AnimationRes(R.anim.rotate_up)
    protected Animation rotateUp;

    @Bean
    protected WineHoundService service;

    private boolean aboutTruncated = false;

    // Each of these sections is loaded when they are first expanded.  Otherwise loading them
    // when the activity is first displayed slows down launching the activity too much.
    private boolean eventsLoaded;
    private boolean aboutLoaded;
    private boolean mapLoaded;

    public static final Intent start(Context c, Region region) {
        Intent i = new Intent(c, RegionActivity_.class);
        i.putExtra(EXTRA_REGION, region);

        return i;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_region;
    }

    @AfterViews
    public void setupUi() {
        if (!region.getPhotographs().isEmpty()) {
            loadCroppedImage(region.getPhotographs().iterator().next().getThumbUrl(), R.drawable.region_placeholder);
        } else {
            loadPlaceholder(R.drawable.region_placeholder);
        }

        regionName.setText(region.getName());

        if (region.getWebsite().isEmpty()) {
            websiteView.setVisibility(View.GONE);
        } else {
            websiteText.setText(region.getWebsite());
        }

        if (region.getEmail().isEmpty()) {
            emailView.setVisibility(View.GONE);
        } else {
            emailText.setText(region.getEmail());
        }

        if (region.getMapPdf() == null || region.getMapPdf().isEmpty()) {
            downloadMapView.setVisibility(View.GONE);
        }

        // Hide views
        regionAbout.setVisibility(View.GONE);
        moreButton.setVisibility(View.GONE);
        whatsOnView.setVisibility(View.GONE);
        contactView.setVisibility(View.GONE);

//        checkForWineries();
    }



//    @Background
//    protected void checkForWineries(){
//        // Quick exit - there are some in our local DB
//        if(service.getRegionWineries(region.getId()).getCount() != 0){
//            return;
//        }
//
//        // Check the network to see if there are any there
//        try{
//            if(service.downloadAndSaveRegionWineries(0, region.getId()).getWineries().isEmpty()){
//                // None on the network!  Hide the wineries section
//                hideWineries();
//            }
//        }
//        catch(Exception e){
//            Log.e(LogTags.UI, "Error getting region wineries", e);
//            hideWineries();
//        }
//    }

    @UiThread
    protected void hideWineries(){
        wineriesView.setVisibility(View.GONE);
    }

    protected void mapClicked(){
        startActivity(RegionWineriesActivity.start(this, region.getId(), true));
    }

    @Background
    protected void downloadEvents() {
        try{
            service.downloadAndSaveRegionEvents(region.getId());
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
        final List<Event> events = service.getRegionEvents(region.getId());
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

    @Click(R.id.region_whats_on)
    protected void whatsOnClicked() {

        if(!eventsLoaded){
            downloadEvents();
            displayEvents(false);
            eventsLoaded = true;
        }

        if (whatsOnView.getVisibility() == View.VISIBLE) {
            whatsOnView.setVisibility(View.GONE);
            whatsOnIndicator.startAnimation(rotateUp);
        } else {
            whatsOnView.setVisibility(View.VISIBLE);
            whatsOnIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.region_about)
    protected void aboutClicked() {

        if(!aboutLoaded){
            aboutTruncated = EdmondSansUtils.loadIntoWebview(regionAbout, region.getAbout());
            aboutLoaded = true;
        }

        if (regionAbout.getVisibility() == View.VISIBLE) {
            regionAbout.setVisibility(View.GONE);
            moreButton.setVisibility(View.GONE);
            aboutIndicator.startAnimation(rotateUp);
        } else {
            regionAbout.setVisibility(View.VISIBLE);
            aboutIndicator.startAnimation(rotateDown);
            if (aboutTruncated) {
                moreButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Click(R.id.region_wineries)
    protected void wineriesClicked() {
        startActivity(RegionWineriesActivity.start(this, region.getId()));
    }

    @Click(R.id.region_download_map)
    protected void downloadMapClicked() {


        String filename = region.getName() + " Map.pdf";
        final ProgressDialog dialog = ProgressDialog.show(this, "Downloading PDF", "Donwloading " + filename, true, true);

        IntentUtils.downloadPdf(this,
                region.getMapPdf(),
                filename,
                new Callback() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        dialog.dismiss();
                    }
                });
    }

    @Click(R.id.region_driving_directions)
    protected void drivingDirectionsClicked() {
        IntentUtils.navigate(this, region.getLatitude(), region.getLongitude());
    }

    @Click(R.id.region_email)
    protected void emailClicked() {
        IntentUtils.email(this, region.getEmail());
    }

    @Click(R.id.region_website)
    protected void websiteClicked() {
        String website = region.getWebsite();
        IntentUtils.viewWebsite(this, website);
    }

    @Click(R.id.region_about_more)
    protected void moreClicked() {
        startActivity(HtmlActivity.start(this, R.string.about, region.getAbout()));
    }

    @Click(R.id.region_contact)
    protected void contactClicked() {

        if(!mapLoaded){
            mapFrag.setStaticMap(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mapClicked();
                }
            });
            mapFrag.display(region.getLatitude(), region.getLongitude(), region.getZoomLevel());
            mapLoaded = true;
        }

        if (contactView.getVisibility() == View.VISIBLE) {
            contactView.setVisibility(View.GONE);
            contactIndicator.startAnimation(rotateUp);
        } else {
            contactView.setVisibility(View.VISIBLE);
            contactIndicator.startAnimation(rotateDown);
        }
    }

    @Override
    protected Collection<Photograph> getPhotographs() {
        return region.getPhotographs();
    }
}
