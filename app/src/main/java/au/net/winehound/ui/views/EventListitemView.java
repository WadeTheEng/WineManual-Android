package au.net.winehound.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.domain.EventLightweight;

@EViewGroup(R.layout.view_event_listitem)
public class EventListitemView extends FrameLayout {

    @ViewById(R.id.event_view_listitem_name)
    protected TextView name;

    @ViewById(R.id.event_view_listitem_time)
    protected TextView time;

    @ViewById(R.id.event_view_listitem_location)
    protected TextView location;

    @ViewById(R.id.event_view_listitem_image)
    protected ImageView image;

    @ViewById(R.id.event_view_listitem_textlayout)
    protected View textLayout;

    public EventListitemView(Context context) {
        super(context);
    }

    public EventListitemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventListitemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEvent(EventLightweight event){
        if(event.isFeatured()){
            image.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(event.getHeaderPhotoThumbUrl()).centerCrop().fit().into(image);
            textLayout.setBackgroundColor(getResources().getColor(R.color.white_transparent));
        }
        else{
            image.setVisibility(View.GONE);
            Picasso.with(getContext()).cancelRequest(image);
            textLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        name.setText(event.getName());
        time.setText(event.getDates());

        if(event.getLocationName().isEmpty()){
            location.setVisibility(View.GONE);
        }
        else{
            location.setText(event.getLocationName());
        }
    }
}
