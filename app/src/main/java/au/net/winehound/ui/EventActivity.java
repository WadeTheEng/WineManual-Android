package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.Photograph;
import au.net.winehound.service.IntentUtils;
import au.net.winehound.ui.views.FavouriteDialog;

@EActivity
public class EventActivity extends AbstractEventActivity {

    @ViewById(R.id.event_date)
    protected TextView date;

    @ViewById(R.id.event_time)
    protected TextView time;

    @ViewById(R.id.event_where)
    protected TextView where;

    @ViewById(R.id.event_address)
    protected TextView address;

    @ViewById(R.id.event_descrption)
    protected TextView description;

    @ViewById(R.id.event_cost)
    protected TextView cost;

    @ViewById(R.id.event_telephone)
    protected TextView telephone;

    @ViewById(R.id.event_website)
    protected View viewWebsite;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event;
    }

    @AfterViews
    protected void setupUi(){
        if (event.getWebsite().isEmpty()) {
            viewWebsite.setVisibility(View.GONE);
        }

        date.setText(event.getDatesLong());
        time.setText(event.getTimes());
        where.setText(event.getLocationName());
        address.setText(event.getAddress());
        description.setText(Html.fromHtml(event.getDescription()));
        cost.setText(event.getPriceAndDescription());
        telephone.setText(event.getPhoneNumber());
    }

    @Click(R.id.event_website)
    protected void viewWebsiteClicked(){
        String website = event.getWebsite();
        IntentUtils.viewWebsite(this, website);
    }

    @Click(R.id.event_add_to_calendar)
    protected void addToCalenderClicked(){

        long startOffset = TimeZone.getDefault().getOffset(event.getStartDate().getTime());
        long endOffset = TimeZone.getDefault().getOffset(event.getFinishDate().getTime());

        IntentUtils.addToCalender(this, event.getName(),
                event.getStartDate().getTime() - startOffset,
                event.getFinishDate().getTime() - endOffset,
                false, event.getDescription());
    }

    @Click(R.id.event_telephone)
    protected void telephoneClicked(){
        IntentUtils.phone(this, event.getPhoneNumber());
    }


    public static Intent start(Context context, Event event) {
        Intent i = new Intent(context, EventActivity_.class);
        i.putExtra(EXTRA_EVENT, event);
        return i;
    }
}
