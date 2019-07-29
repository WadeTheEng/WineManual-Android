package au.net.winehound.ui.views;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.domain.Winery;

@EViewGroup(R.layout.view_winery_map_marker)
public class WineryMapMarker extends FrameLayout {


    @ViewById(R.id.winery_map_marker_title)
    protected TextView title;

    @ViewById(R.id.winery_map_marker_distance)
    protected TextView distance;

    @ViewById(R.id.winery_map_marker_amenities)
    protected AmenitiesView amenities;

    public WineryMapMarker(Context context) {
        super(context);
    }

    public WineryMapMarker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WineryMapMarker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setWinery(Winery winery, Location lastLocation){
        title.setText(winery.getName());
        distance.setText(winery.getDistanceFrom(lastLocation));

        if(winery.getTier() == Winery.Tier.Basic){
            amenities.setVisibility(View.GONE);
        }
        else{
            amenities.setVisibility(View.VISIBLE);
            amenities.setAmenities(winery.getAmenities());
        }
    }
}
