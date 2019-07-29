package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.WineRange;
import au.net.winehound.domain.Winery;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.fragments.WineriesMapFragment;
import au.net.winehound.ui.views.CellarDoorTimesView;
import au.net.winehound.ui.views.EventView;
import au.net.winehound.ui.views.EventView_;
import au.net.winehound.ui.views.FavouriteDialog;
import au.net.winehound.ui.views.WineView;
import au.net.winehound.ui.views.WineView_;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EActivity
@OptionsMenu(R.menu.menu_winery)
public class WineryActivity extends AbstractTransparentAbActivity {

    private static final String EXTRA_WINERY = "EXTRA_WINERY";

    @Extra(EXTRA_WINERY)
    protected Winery winery;

    @ViewById(R.id.header_name)
    protected TextView wineryName;

    @ViewById(R.id.winery_about_text)
    protected WebView wineryAbout;

    @ViewById(R.id.winery_about)
    protected View aboutSection;

    @ViewById(R.id.winery_wines)
    protected View wineSection;

    @ViewById(R.id.winery_whats_on)
    protected View whatsOnSection;

    @ViewById(R.id.winery_cellar_door)
    protected View cellarDoorSection;

    @ViewById(R.id.winery_cellar_door_text)
    protected WebView cellarDoorText;

    @ViewById(R.id.winery_cellar_door_more)
    protected View cellarDoorMore;

    @ViewById(R.id.winery_whats_on_view)
    protected View whatsOnView;

    @ViewById(R.id.winery_contact_view)
    protected View contactView;

    @ViewById(R.id.winery_whats_on_indicator)
    protected View whatsOnIndicator;

    @ViewById(R.id.winery_wine_indicator)
    protected View wineIndicator;

    @ViewById(R.id.winery_about_indicator)
    protected View aboutIndicator;

    @ViewById(R.id.winery_cellar_door_indicator)
    protected View cellarDoorIndicator;

    @ViewById(R.id.winery_contact_indicator)
    protected View contactIndicator;

    @ViewById(R.id.winery_address)
    protected TextView address;

    @ViewById(R.id.winery_phone)
    protected View phoneView;

    @ViewById(R.id.winery_cellar_door_times)
    protected CellarDoorTimesView cellarDoorTimes;

    @ViewById(R.id.winery_phone_text)
    protected TextView phoneText;

    @ViewById(R.id.winery_email)
    protected View emailView;

    @ViewById(R.id.winery_email_text)
    protected TextView emailText;

    @ViewById(R.id.winery_website)
    protected View websiteView;

    @ViewById(R.id.winery_website_text)
    protected TextView websiteText;

    @ViewById(R.id.winery_about_more)
    protected View aboutMoreButton;

    @ViewById(R.id.winery_wines_view)
    protected View winesView;

    @ViewById(R.id.winery_wines_progress)
    protected ProgressBar winesProgress;

    @ViewById(R.id.winery_wines_list)
    protected LinearLayout winesList;

    @ViewById(R.id.winery_no_wines)
    protected TextView noWines;

    @ViewById(R.id.winery_whats_on_progress)
    protected ProgressBar eventsProgress;

    @ViewById(R.id.winery_whats_on_list)
    protected LinearLayout eventsList;

    @ViewById(R.id.winery_no_events)
    protected TextView noEvents;

    @FragmentById(R.id.winery_map)
    protected WineriesMapFragment mapFrag;

    @AnimationRes(R.anim.rotate_down)
    protected Animation rotateDown;

    @AnimationRes(R.anim.rotate_up)
    protected Animation rotateUp;

    private boolean aboutTruncated = false;
    private boolean cellarDoorTruncated = false;

    @Bean
    protected WineHoundService service;

    // Each of these sections is loaded when they are first expanded.  Otherwise loading them
    // when the activity is first displayed slows down launching the activity too much.
    private boolean whatsOnLoaded;
    private boolean aboutLoaded;
    private boolean cellarDoorLoaded;
    private boolean contactLoaded;
    private boolean winesLoaded;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_winery;
    }

    @AfterViews
    public void setupUi(){

        // Image
        if(!winery.getPhotographs().isEmpty() && winery.getTier() != Winery.Tier.Basic){
            loadCroppedImage(winery.getPhotographs().iterator().next().getThumbUrl(), R.drawable.winery_placeholder);
        }
        else{
            loadPlaceholder(R.drawable.winery_placeholder);
        }

        wineryName.setText(winery.getName());

        // About section
        if(winery.getAbout() == null || winery.getAbout().isEmpty() || winery.getTier() == Winery.Tier.Basic){
            aboutSection.setVisibility(View.GONE);
        }

        // Wines section
        if(winery.getTier() == Winery.Tier.Basic){
            wineSection.setVisibility(View.GONE);
        }

        // Whats on section
        if(winery.getTier() == Winery.Tier.Basic){
            whatsOnSection.setVisibility(View.GONE);
        }

        // Cellar door section
        if(winery.getCellarDoorDescription() == null || winery.getCellarDoorPhotographs().isEmpty() || winery.getTier() == Winery.Tier.Basic){
            cellarDoorSection.setVisibility(View.GONE);
        }

        // Contact section
        if(winery.getWebsite() != null && winery.getWebsite().isEmpty()){
            websiteView.setVisibility(View.GONE);
        }
        else{
            websiteText.setText(winery.getWebsite());
        }

        if(winery.getEmail() != null && winery.getEmail().isEmpty()){
            emailView.setVisibility(View.GONE);
        }
        else{
            emailText.setText(winery.getEmail());
        }

        if(winery.getPhoneNumber() != null && winery.getPhoneNumber().isEmpty()){
            phoneView.setVisibility(View.GONE);
        }
        else{
            phoneText.setText(winery.getPhoneNumber());
        }

        address.setText(winery.getAddress());

        // Hide detail views
        wineryAbout.setVisibility(View.GONE);
        aboutMoreButton.setVisibility(View.GONE);
        cellarDoorText.setVisibility(View.GONE);
        cellarDoorMore.setVisibility(View.GONE);
        whatsOnView.setVisibility(View.GONE);
        contactView.setVisibility(View.GONE);
        winesView.setVisibility(View.GONE);
        cellarDoorTimes.setVisibility(View.GONE);

        // If only the contact section is visible, then we need to show that
        if(aboutSection.getVisibility() == View.GONE && wineSection.getVisibility() == View.GONE
                && whatsOnSection.getVisibility() == View.GONE && cellarDoorSection.getVisibility() == View.GONE){
            contactClicked();
        }

        if(service.isWineryFavourite(winery)){
            favouriteButton.setImageResource(R.drawable.favourite_icon_white_selected);
        }
    }

    @Background
    protected void downloadOpenTimes(){
        try{
            service.downloadAndSaveWineryCellarDoorOpenTimes(winery.getId());
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error downloading open times", e);
        }
        displayOpenTimes();
    }

    @Background
    protected void downloadWines(){
        // Get the wines
        try{
            service.downloadAndSaveWineryWines(winery.getId());
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error downloading open times", e);
        }
        displayWines(true);
    }

    @Background
    protected void downloadEvents(){
        try{
            service.downloadAndSaveWineryEvents(winery.getId());
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error downloading open times", e);
        }
        displayEvents(true);
    }

    @UiThread
    protected void displayOpenTimes(){
        if(isActivityDestroyed()){
            // Quick exit - we are no longer visible
            return;
        }

        cellarDoorTruncated = EdmondSansUtils.loadIntoWebview(cellarDoorText, winery.getCellarDoorDescription());

        // List our cellar door times
        List<CellarDoorOpenTime> openTimes = service.getWineryCellarDoorOpenTimes(winery.getId());
        cellarDoorTimes.setCellarDoorTimes(openTimes, winery.getPhoneNumber());
    }

    @UiThread
    protected void displayEvents(boolean hideProgress){
        if(isActivityDestroyed()){
            // Quick exit - we are no longer visible
            return;
        }


        if(hideProgress){
            eventsProgress.setVisibility(View.GONE);
        }

        // Quick exit - there are no wines!
        final List<Event> events = service.getWineryEvents(winery.getId());
        if(events.isEmpty()){
            noEvents.setVisibility(View.VISIBLE);
            return;
        }
        else{
            noEvents.setVisibility(View.GONE);
        }

        eventsList.removeAllViews();
        for(final Event event : events){
            EventView eventView = EventView_.build(this);
            eventView.setEvent(event);
            eventsList.addView(eventView);
        }
    }

    @UiThread
    protected void displayWines(boolean hideProgress){
        if(isActivityDestroyed()){
            // Quick exit - we are no longer visible
            return;
        }

        if(hideProgress){
            winesProgress.setVisibility(View.GONE);
        }


        // Quick exit - there are no wines!
        final List<Wine> wineList = service.getWineryWines(winery.getId());
        if(wineList.isEmpty() && hideProgress){
            noWines.setVisibility(View.VISIBLE);
            return;
        }
        else{
            noWines.setVisibility(View.GONE);
        }

        winesList.removeAllViews();
        if(winery.getTier() == Winery.Tier.Gold || winery.getTier() == Winery.Tier.GoldPlus){

            // Display the wine ranges
            for(final WineRange range : winery.getWineRanges()){

                TextView item = (TextView)getLayoutInflater().inflate(R.layout.view_wine_range, winesList, false);

                item.setText(range.getName());
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Start the activity
                        startActivity(WineListActivity_.start(WineryActivity.this,new ArrayList<Wine>(service.getRangeWines(range.getId()))));
                    }
                });

                // Add it
                winesList.addView(item, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
        else{
            // Display the wines
            for(Wine wine : wineList){

                // Make the view
                WineView wineView = WineView_.build(this);
                wineView.setWine(wine, service, true);

                // Add it
                winesList.addView(wineView, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Click(R.id.winery_whats_on)
    protected void whatsOnClicked(){

        if(!whatsOnLoaded){
            downloadEvents();
            displayEvents(false);
            whatsOnLoaded = true;
        }

        if(whatsOnView.getVisibility() == View.VISIBLE){
            whatsOnView.setVisibility(View.GONE);
            whatsOnIndicator.startAnimation(rotateUp);
        }
        else{
            whatsOnView.setVisibility(View.VISIBLE);
            whatsOnIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.winery_about)
    protected void aboutClicked(){

        if(!aboutLoaded){
            aboutTruncated = EdmondSansUtils.loadIntoWebview(wineryAbout, winery.getAbout());
            aboutLoaded = true;
        }

        if(wineryAbout.getVisibility() == View.VISIBLE){
            wineryAbout.setVisibility(View.GONE);
            aboutMoreButton.setVisibility(View.GONE);
            aboutIndicator.startAnimation(rotateUp);
        }
        else{
            wineryAbout.setVisibility(View.VISIBLE);
            if(aboutTruncated){
                aboutMoreButton.setVisibility(View.VISIBLE);
            }
            aboutIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.winery_cellar_door)
    protected void cellarDoorClicked(){

        if(!cellarDoorLoaded){
            downloadOpenTimes();
            displayOpenTimes();
            cellarDoorLoaded = true;
        }

        if(cellarDoorText.getVisibility() == View.VISIBLE){
            cellarDoorText.setVisibility(View.GONE);
            cellarDoorMore.setVisibility(View.GONE);
            cellarDoorTimes.setVisibility(View.GONE);
            cellarDoorIndicator.startAnimation(rotateUp);
        }
        else{
            cellarDoorText.setVisibility(View.VISIBLE);
            if(cellarDoorTruncated){
                cellarDoorMore.setVisibility(View.VISIBLE);
            }
            cellarDoorTimes.setVisibility(View.VISIBLE);
            cellarDoorIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.winery_driving_directions)
    protected void drivingDirectionsClicked(){
        String uri = "http://maps.google.com/maps?daddr="+winery.getLatitude() + "," + winery.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Click(R.id.winery_email)
    protected void emailClicked(){
        IntentUtils.email(this, winery.getEmail());
    }

    @Click(R.id.winery_website)
    protected void websiteClicked(){
        String website = winery.getWebsite();
        IntentUtils.viewWebsite(this, website);
    }

    @Click(R.id.winery_phone_text)
    protected void phoneClicked(){
        IntentUtils.phone(this, winery.getPhoneNumber());
    }

    @Click(R.id.winery_contact)
    protected void contactClicked(){
        if(!contactLoaded){
            mapFrag.setStaticMap(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                }
            });
            mapFrag.display(winery.getLatitude(), winery.getLongitude(), winery.getZoomLevel());

            contactLoaded = true;
        }

        if(contactView.getVisibility() == View.VISIBLE){
            contactView.setVisibility(View.GONE);
            contactIndicator.startAnimation(rotateUp);
        }
        else{
            contactView.setVisibility(View.VISIBLE);
            contactIndicator.startAnimation(rotateDown);
        }
    }

    @Click(R.id.winery_about_more)
    protected void aboutMoreClicked(){
        startActivity(HtmlActivity.start(this, R.string.about, winery.getAbout()));
    }

    @Click(R.id.winery_cellar_door_more)
    protected void cellarDoorMoreClicked(){
        startActivity(CellarDoorActivity.start(this, winery));
    }

    @Click(R.id.winery_wines)
    protected void winesClicked(){

        if(!winesLoaded){
            // Start loading our wines
            downloadWines();
            displayWines(false);
            winesLoaded = true;
        }


        if(winesView.getVisibility() == View.VISIBLE){
            winesView.setVisibility(View.GONE);
            wineIndicator.startAnimation(rotateUp);
        }
        else{
            winesView.setVisibility(View.VISIBLE);
            wineIndicator.startAnimation(rotateDown);
        }
    }


    @OptionsItem(R.id.menu_winery_share)
    protected void shareClicked(){
        String text = "Checkout this winery : " + winery.getName() + " http://app.winehound.net.au/share/winery/"+winery.getId() + " from the #winehoundapp";
        IntentUtils.shareText(this, text);
    }

    public static final Intent start(Context c, Winery winery){
        Intent i = new Intent(c, WineryActivity_.class);
        i.putExtra(EXTRA_WINERY, winery);

        return i;
    }

    @Override
    protected void onFavouriteClick(){
        // Toggle favourite status!
        if(service.isWineryFavourite(winery)){
            service.setWineryFavourite(winery, false);
            favouriteButton.setImageResource(R.drawable.favourite_icon_white_norm);
        }
        else {
            FavouriteDialog.startDialog(this, winery.getId(), new FavouriteDialog.FavouriteCompleted() {
                @Override
                public void onFavouriteCompleted() {
                    service.setWineryFavourite(winery, true);
                    favouriteButton.setImageResource(R.drawable.favourite_icon_white_selected);
                }
            });
        }
    }

    @Override
    protected Collection<Photograph> getPhotographs() {
        return winery.getPhotographs();
    }

    protected boolean createFavouriteButton(){
        return true;
    }
}
