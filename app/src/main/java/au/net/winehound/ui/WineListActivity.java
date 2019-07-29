package au.net.winehound.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;
import au.net.winehound.R;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.Winery;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.views.FavouriteDialog;
import au.net.winehound.ui.views.WineCoverflowView;
import au.net.winehound.ui.views.WineCoverflowView_;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EActivity(R.layout.activity_winelist)
@OptionsMenu(R.menu.menu_winelist)
public class WineListActivity extends AbstractWinehoundActivity {

    private static final String EXTRA_WINE_LIST = "EXTRA_WINE_LIST";
    private static final String EXTRA_WINE_ID = "EXTRA_WINE_ID";

    @Extra(EXTRA_WINE_LIST)
    protected List<Wine> wineList;

    // ID of a wine to scroll to
    @Extra(EXTRA_WINE_ID)
    protected Integer wineId;

    @Bean
    protected WineHoundService service;

    @ViewById(R.id.winelist_coverflow)
    protected FancyCoverFlow coverFlow;

    @ViewById(R.id.winelist_wine_name)
    protected TextView wineName;

    @ViewById(R.id.winelist_wine_description)
    protected WebView wineDescription;

    @ViewById(R.id.winelist_colour)
    protected TextView tastingColour;

    @ViewById(R.id.winelist_aroma)
    protected TextView tastingAroma;

    @ViewById(R.id.winelist_palate)
    protected TextView tastingPalate;

    @ViewById(R.id.winelist_vintage)
    protected TextView detailsVintage;

    @ViewById(R.id.winelist_cost)
    protected TextView detailsCost;

    @ViewById(R.id.winelist_date_bottled)
    protected TextView detailsDateBottled;

    @ViewById(R.id.winelist_variety)
    protected TextView detailsVariety;

    @ViewById(R.id.winelist_alcoholic_content)
    protected TextView detailsAlcoholicContent;

    @ViewById(R.id.winelist_winemakers)
    protected TextView detailsWinemakers;

    @ViewById(R.id.winelist_ph)
    protected TextView detailsPh;

    @ViewById(R.id.winelist_closed_under)
    protected TextView detailsClosedUnder;

    @ViewById(R.id.winelist_tasting_notes)
    protected View tastingNotes;

    @ViewById(R.id.winelist_details)
    protected View details;

    @ViewById(R.id.winelist_vintage_row)
    protected View vintageRow;

    @ViewById(R.id.winelist_cost_row)
    protected View costRow;

    @ViewById(R.id.winelist_date_row)
    protected View dateRow;

    @ViewById(R.id.winelist_variety_row)
    protected View varietyRow;

    @ViewById(R.id.winelist_alcoholic_content_row)
    protected View alcoholicContentRow;

    @ViewById(R.id.winelist_winemakers_row)
    protected View winemakersRow;

    @ViewById(R.id.winelist_ph_row)
    protected View phRow;

    @ViewById(R.id.winelist_closure_row)
    protected View closureRow;

    @ViewById(R.id.winelist_tasting_notes_button)
    protected Button downloadTastingNotesButton;

    @ViewById(R.id.winelist_favourite_button)
    protected ImageView favouriteButton;

    private WineAdapter adapter;
    private Wine currentWine;

    // A cache of images for the coverflow view.  For some reason we can't picassa because it causes
    // the coverflow to lag up - we have to do some of the caching ourselves.  See WineCoverflowView
    // for more info.
    private Map<Integer, Bitmap> imageCache = new HashMap<Integer, Bitmap>();

    @AfterViews
    protected void setupUi(){
        adapter = new WineAdapter();
        coverFlow.setAdapter(adapter);
        coverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wineSelected(wineList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(wineId != null){
            // Try and find the ID of our favourite wine!
            int position = -1;
            for (int i = 0; i < wineList.size(); i++){
                if(wineList.get(i).getId() == wineId){
                    position = i;
                    break;
                }
            }

            if(position != -1){
                coverFlow.setSelection(position);
            }
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    protected void wineSelected(Wine wine){
        if(isActivityDestroyed()){
            // Quick exit - we are no longer visible
            return;
        }


        currentWine = wine;
        wineName.setText(wine.getName());

        EdmondSansUtils.loadIntoWebview(wineDescription, wine.getDescription(), false);

        // Tasting notes
        if(wine.hasTastingNotes()){
            tastingColour.setText(Html.fromHtml(wine.getColour()));
            tastingAroma.setText(Html.fromHtml(wine.getAroma()));
            tastingPalate.setText(Html.fromHtml(wine.getPalate()));
            tastingNotes.setVisibility(View.VISIBLE);
        }
        else{
            tastingNotes.setVisibility(View.GONE);
        }

        if(wine.hasWineDetails()){
            details.setVisibility(View.VISIBLE);

            if(wine.hasVintage()){
                vintageRow.setVisibility(View.VISIBLE);
                detailsVintage.setText(wine.getVintage());
            }
            else{
                vintageRow.setVisibility(View.GONE);
            }

            if(wine.hasCost()){
                costRow.setVisibility(View.VISIBLE);
                detailsCost.setText(wine.getCost());
            }
            else{
                costRow.setVisibility(View.GONE);
            }

            if(wine.hasDateBottled()){
                dateRow.setVisibility(View.VISIBLE);
                detailsDateBottled.setText(wine.getDateBottled());
            }
            else{
                dateRow.setVisibility(View.GONE);
            }

            if(wine.hasGrapeVariety()){
                varietyRow.setVisibility(View.VISIBLE);
                detailsVariety.setText(wine.getDisplayVariety());
            }
            else{
                varietyRow.setVisibility(View.GONE);
            }


            if(wine.hasAlcholContent()){
                alcoholicContentRow.setVisibility(View.VISIBLE);
                detailsAlcoholicContent.setText(wine.getAlcholContent());
            }
            else{
                alcoholicContentRow.setVisibility(View.GONE);
            }

            if(wine.hasWinemakers()){
                winemakersRow.setVisibility(View.VISIBLE);
                detailsWinemakers.setText(wine.getWinemakers());
            }
            else{
                winemakersRow.setVisibility(View.GONE);
            }

            if(wine.hasPh()){
                phRow.setVisibility(View.VISIBLE);
                detailsPh.setText(wine.getPh());
            }
            else{
                phRow.setVisibility(View.GONE);
            }

            if(wine.hasClosure()){
                closureRow.setVisibility(View.VISIBLE);
                detailsClosedUnder.setText(wine.getClosure());
            }
            else{
                closureRow.setVisibility(View.GONE);
            }

        }
        else{
            details.setVisibility(View.VISIBLE);
        }

        if(wine.hasTastingNotesPdf()){
            downloadTastingNotesButton.setVisibility(View.VISIBLE);
        }
        else {
            downloadTastingNotesButton.setVisibility(View.GONE);
        }

        if(service.isWineFavourite(wine)){
            favouriteButton.setImageResource(R.drawable.favourite_icon_red_selected);
        }
        else{
            favouriteButton.setImageResource(R.drawable.favourite_icon_red_norm);
        }
    }

    @OptionsItem(R.id.menu_winelist_share)
    protected void shareClicked(){
        Winery winery = service.getWinery(currentWine.getWineryId());

        String text = "Checkout this wine from " + winery.getName() + " : " + currentWine.getName()
                + " http://app.winehound.net.au/share/wine/"+currentWine.getId() + " from the #winehoundapp";
        IntentUtils.shareText(this, text);
    }

    @Click(R.id.winelist_tasting_notes_button)
    protected void tastingNotesPdfClicked(){
        String filename =  currentWine.getName() + " Notes.pdf";
        final ProgressDialog dialog = ProgressDialog.show(this,
                "Downloading PDF",
                "Donwloading " + filename, true, true);

        IntentUtils.downloadPdf(this,
                currentWine.getTastingNotesUrl(),
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

    @Click(R.id.winelist_buy_online)
    protected void buyOnlineClicked(){
        String website = currentWine.getWebsite();
        if(!website.startsWith("http://")){
            website = "http://"+website;
        }

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
    }

    @Click(R.id.winelist_favourite_button)
    protected void favouriteButtonClicked(){
        // Toggle favourite status!
        if(service.isWineFavourite(currentWine)){
            service.setWineFavourite(currentWine, false);
            favouriteButton.setImageResource(R.drawable.favourite_icon_red_norm);
        }
        else {
            FavouriteDialog.startDialog(this, currentWine.getWineryId(), new FavouriteDialog.FavouriteCompleted() {
                @Override
                public void onFavouriteCompleted() {
                    service.setWineFavourite(currentWine, true);
                    favouriteButton.setImageResource(R.drawable.favourite_icon_red_selected);
                }
            });
        }
    }

    /**
     * Causes the adapter to be invalidated the the views re-rendered.  This should be called
     * when a new image is placed in the cache.
     */
    private Callback notifyCallback = new Callback() {
        @Override
        public void onSuccess() {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onError() {
            adapter.notifyDataSetChanged();
        }
    };

    private class WineAdapter extends FancyCoverFlowAdapter{

        @Override
        public View getCoverFlowItem(int position, View reusableView, ViewGroup parent) {
            WineCoverflowView wineView;
            if(reusableView != null){
                wineView = (WineCoverflowView)reusableView;
            }
            else {
                wineView = WineCoverflowView_.build(WineListActivity.this);
            }

            wineView.setWine(wineList.get(position), notifyCallback, imageCache);

            return wineView;
        }

        @Override
        public int getCount() {
            return wineList.size();
        }

        @Override
        public Object getItem(int i) {
            return wineList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return wineList.get(i).getId();
        }
    }

    public static Intent start(Context context, ArrayList<Wine> wineList){
        Intent intent = new Intent(context, WineListActivity_.class);
        intent.putExtra(EXTRA_WINE_LIST, wineList);
        return intent;
    }

    public static Intent start(Context context, ArrayList<Wine> wineList, int favouriteWineId){
        Intent intent = new Intent(context, WineListActivity_.class);
        intent.putExtra(EXTRA_WINE_LIST, wineList);
        intent.putExtra(EXTRA_WINE_ID, favouriteWineId);
        return intent;
    }
}
