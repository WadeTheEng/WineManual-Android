package au.net.winehound.ui.views;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.service.WineHoundService;

@EViewGroup(R.layout.view_winery)
public class WineryView extends FrameLayout {

    @ViewById(R.id.winery_name_no_image)
    protected TextView wineryNameNoImage;

    @ViewById(R.id.winery_distance_no_image)
    protected TextView wineryDistanceNoImage;

    @ViewById(R.id.winery_name_image)
    protected TextView wineryNameImage;

    @ViewById(R.id.winery_distance_image)
    protected TextView wineryDistanceImage;

    @ViewById(R.id.winery_name_container)
    protected View nameContainer;

    @ViewById(R.id.winery_image)
    protected ImageView image;

    @ViewById(R.id.winery_amenities)
    protected AmenitiesView amenities;

    public WineryView(Context context) {
        super(context);
    }

    public WineryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WineryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWinery(WineryLightweight winery, Location lastLocation, WineHoundService service){


        if(winery.getTier() == Winery.Tier.Basic){
            // Hide photo
            image.setVisibility(View.GONE);


            // Setup text
            wineryNameNoImage.setText(winery.getName());
            wineryNameNoImage.setVisibility(View.VISIBLE);

            wineryDistanceNoImage.setVisibility(View.VISIBLE);
            wineryDistanceNoImage.setText(winery.getDistanceFrom(lastLocation));

            nameContainer.setVisibility(View.GONE);
            setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    getContext().getResources().getDimensionPixelSize(R.dimen.winery_view_height_normal)));
        }
        else {
            // Show photo
            image.setVisibility(View.VISIBLE);

            if(!winery.hasHeaderPhoto()){
                Picasso.with(getContext()).load(R.drawable.winery_placeholder).into(image);
            }
            else{
                Picasso.with(getContext()).load(winery.getHeaderPhotoThumbUrl())
                        .fit().centerCrop()
                        .placeholder(R.drawable.winery_placeholder)
                        .into(image);
            }

            // Setup text
            wineryNameImage.setText(winery.getName());
            wineryDistanceImage.setText(winery.getDistanceFrom(lastLocation));

            wineryNameNoImage.setVisibility(View.GONE);
            wineryDistanceNoImage.setVisibility(View.GONE);

            nameContainer.setVisibility(View.VISIBLE);

            amenities.setAmenities(service.getWineryAmenities(winery.getId()));

            int sizeRes;
            if(winery.getTier() == Winery.Tier.Gold || winery.getTier() == Winery.Tier.GoldPlus){
                sizeRes = R.dimen.winery_view_height_large;
            }
            else{
                sizeRes = R.dimen.winery_view_height_medium;
            }
            setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    getContext().getResources().getDimensionPixelSize(sizeRes)));

        }
    }
}
