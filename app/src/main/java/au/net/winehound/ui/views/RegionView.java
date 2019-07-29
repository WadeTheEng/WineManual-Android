package au.net.winehound.ui.views;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.RegionLightweight;

@EViewGroup(R.layout.view_region)
public class RegionView extends LinearLayout {

    @ViewById(R.id.region_name)
    protected TextView regionName;

    @ViewById(R.id.region_distance)
    protected TextView regionDistance;

    @ViewById(R.id.region_image)
    protected ImageView regionImage;

    public RegionView(Context context) {
        super(context);
    }

    public RegionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RegionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRegion(final RegionLightweight region, Location lastLocation){

        // Adjust the colour on the text
        regionName.setText(region.getName());
        regionName.setVisibility(View.VISIBLE);

        regionDistance.setText(region.getDistanceFrom(lastLocation));

        if(region.hasHeaderPhoto()){

            // Request the image
            Picasso.with(getContext()).load(region.getHeaderPhotoThumbUrl()).fit().centerCrop()
                    .placeholder(R.drawable.region_placeholder)
                    .into(regionImage);
        }

        else{
            // Request the image
            Picasso.with(getContext()).load(R.drawable.region_placeholder).into(regionImage);
        }
    }
}
