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
import au.net.winehound.domain.Event;
import au.net.winehound.ui.EventActivity;

@EViewGroup(R.layout.view_event)
public class EventView extends FrameLayout{

    @ViewById(R.id.event_view_image)
    protected ImageView image;

    @ViewById(R.id.event_view_name)
    protected TextView name;

    @ViewById(R.id.event_view_date)
    protected TextView date;

    public EventView(Context context) {
        super(context);
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEvent(final Event event){
        setForeground(getResources().getDrawable(R.drawable.selectable_background_winehound));

        if(!event.getPhotographs().isEmpty()){
            Picasso.with(getContext()).load(event.getPhotographs().iterator().next().getThumbUrl())
                    .resizeDimen(R.dimen.event_view_width, R.dimen.event_view_height).centerInside().into(image);
        }

        name.setText(event.getName());
        date.setText(event.getDates());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start event activity
                getContext().startActivity(EventActivity.start(getContext(), event));
            }
        });
    }
}
