package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;
import java.util.List;

import au.net.winehound.R;
import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Winery;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.views.CellarDoorTimesView;

@EActivity
public class CellarDoorActivity extends AbstractTransparentAbActivity {

    private static final String EXTRA_WINERY = "EXTRA_WINERY";

    @Extra(EXTRA_WINERY)
    public Winery winery;

    @ViewById(R.id.cellar_door_textview)
    protected TextView htmlTextView;

    @ViewById(R.id.cellar_door_open_times)
    protected CellarDoorTimesView cellarDoor;

    @Bean
    protected WineHoundService service;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cellar_door;
    }

    @AfterViews
    public void setupUi() {

        if (!winery.getCellarDoorPhotographs().isEmpty()) {
            loadCroppedImage(winery.getCellarDoorPhotographs().iterator().next().getThumbUrl(), R.drawable.winery_placeholder);
        } else {
            loadPlaceholder(R.drawable.winery_placeholder);
        }

        htmlTextView.setText(Html.fromHtml(winery.getCellarDoorDescription()));

        List<CellarDoorOpenTime> openTimes = service.getWineryCellarDoorOpenTimes(winery.getId());
        cellarDoor.setCellarDoorTimes(openTimes, winery.getPhoneNumber());

    }

    @Override
    protected Collection<Photograph> getPhotographs() {
        return winery.getCellarDoorPhotographs();
    }

    public static Intent start(Context context, Winery winery){
        Intent intent = new Intent(context, CellarDoorActivity_.class);
        intent.putExtra(EXTRA_WINERY, winery);
        return intent;
    }

}
