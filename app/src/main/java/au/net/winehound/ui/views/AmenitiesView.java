package au.net.winehound.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Collection;
import java.util.List;

import au.net.winehound.R;
import au.net.winehound.domain.WineryAmenity;

public class AmenitiesView extends LinearLayout {

    public AmenitiesView(Context context) {
        super(context);
    }

    public AmenitiesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AmenitiesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAmenities(Collection<WineryAmenity> amenities){
        removeAllViews();

        for(WineryAmenity amenity: amenities){
            ImageView image = new ImageView(getContext());

            int paddingSize = getResources().getDimensionPixelSize(R.dimen.padding_xxsmall);
            image.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);

            if(amenity.getName().equals("Concerts/Festivals")){
                image.setImageResource(R.drawable.amenity_concert);
            }
            else if(amenity.getName().equals("Conferences/Functions")){
                image.setImageResource(R.drawable.amenity_conferences);
            }
            else if(amenity.getName().equals("Crafts/Local Produce")){
                image.setImageResource(R.drawable.amenity_local_produce);
            }
            else if(amenity.getName().equals("Gallery")){
                image.setImageResource(R.drawable.amenity_gallery);
            }
            else if(amenity.getName().equals("Cafe/Restaurant")){
                image.setImageResource(R.drawable.amenity_cafe);
            }
            else if(amenity.getName().equals("Picnic facilities")){
                image.setImageResource(R.drawable.amenity_picnic);
            }
            else if(amenity.getName().equals("Historical buildings")){
                image.setImageResource(R.drawable.amenity_historical_building);
            }
            else if(amenity.getName().equals("Playground")){
                image.setImageResource(R.drawable.amenity_playground);
            }
            else if(amenity.getName().equals("Accommodation")){
                image.setImageResource(R.drawable.amenity_accommodation);
            }
            else if(amenity.getName().equals("Private tastings")){
                image.setImageResource(R.drawable.amenity_tastings);
            }
            else if(amenity.getName().equals("Disabled Access")){
                image.setImageResource(R.drawable.amenity_disabled);
            }
            else if(amenity.getName().equals("Bikes for Hire")){
                image.setImageResource(R.drawable.amenity_bike);
            }
            else if(amenity.getName().equals("Brewery or Wine Bar")){
                image.setImageResource(R.drawable.amenity_brewery);
            }
            else if(amenity.getName().equals("Antipasto Platter / Cheese Plate")){
                image.setImageResource(R.drawable.amenity_antipasto);
            }
            else if(amenity.getName().equals("Coffee / Light Meals")){
                image.setImageResource(R.drawable.amenity_cafe);
            }
            else if(amenity.getName().equals("Winery & Vineyard Tours")){
                image.setImageResource(R.drawable.amenity_tour);
            }
            addView(image);
        }
    }
}
