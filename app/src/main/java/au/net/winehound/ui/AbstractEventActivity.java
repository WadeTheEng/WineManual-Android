package au.net.winehound.ui;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;

import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.Photograph;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.views.FavouriteDialog;

@EActivity
@OptionsMenu(R.menu.menu_event)
public abstract class AbstractEventActivity extends AbstractTransparentAbActivity {
    protected static final String EXTRA_EVENT = "EXTRA_EVENT";

    @Extra(EXTRA_EVENT)
    protected Event event;

    @ViewById(R.id.header_name)
    protected TextView eventName;

    @Bean
    protected WineHoundService service;

    @AfterViews
    protected void setupHeaderPhotographs(){
        // Image
        if(!event.getPhotographs().isEmpty()){
            loadCroppedImage(event.getPhotographs().iterator().next().getThumbUrl(), R.drawable.winery_placeholder);
        }
        else{
            loadPlaceholder(R.drawable.winery_placeholder);
        }

        eventName.setText(event.getName());

        if(service.isEventFavourite(event)){
            favouriteButton.setImageResource(R.drawable.favourite_icon_white_selected);
        }
    }

    @OptionsItem(R.id.menu_event_share)
    protected void shareClicked(){
        String text = "Checkout this event :  http://app.winehound.net.au/share/event/"+event.getId() + " from the #winehoundapp";
        IntentUtils.shareText(this, text);
    }

    protected boolean createFavouriteButton() {
        return true;
    }

    @Override
    protected void onFavouriteClick(){
        // Toggle favourite status!
        if(service.isEventFavourite(event)){
            service.setEventFavourite(event, false);
            favouriteButton.setImageResource(R.drawable.favourite_icon_white_norm);
        }
        else {
            FavouriteDialog.startDialog(this, event.getId(), new FavouriteDialog.FavouriteCompleted() {
                @Override
                public void onFavouriteCompleted() {
                    service.setEventFavourite(event, true);
                    favouriteButton.setImageResource(R.drawable.favourite_icon_white_selected);
                }
            });
        }
    }

    @Override
    protected Collection<Photograph> getPhotographs() {
        return event.getPhotographs();
    }
}
