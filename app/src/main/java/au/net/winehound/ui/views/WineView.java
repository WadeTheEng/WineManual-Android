package au.net.winehound.ui.views;

import android.content.Context;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.domain.Wine;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.views.edmondsans.EdmondSansUtils;

@EViewGroup(R.layout.view_wine)
public class WineView extends FrameLayout {

    @ViewById(R.id.wine_image)
    protected ImageView image;

    @ViewById(R.id.wine_name)
    protected TextView name;

    @ViewById(R.id.wine_description)
    protected TextView description;

    @ViewById(R.id.wine_favourite_button)
    protected ImageView favouriteButton;

    private WineHoundService service;
    private Wine wine;

    public WineView(Context context) {
        super(context);
    }

    public WineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWine(Wine wine, WineHoundService service, boolean showFavourite){
        this.wine = wine;
        this.service = service;

        if(!wine.getPhotographs().isEmpty()){
            Picasso.with(getContext()).load(wine.getPhotographs().iterator().next().getThumbUrl())
                    .fit().centerInside()
                    .placeholder(R.drawable.wine_bottle_dotted)
                    .into(image);
        }
        else{
            Picasso.with(getContext()).load(R.drawable.wine_default)
                    .fit().centerInside()
                    .into(image);
        }

        name.setText(wine.getName());

        Spanned formattedHtml =  Html.fromHtml(wine.getDescription());
        if(formattedHtml.length() > EdmondSansUtils.MAX_ABOUT_LENGTH){
            SpannableStringBuilder builder = new SpannableStringBuilder(formattedHtml, 0, EdmondSansUtils.MAX_ABOUT_LENGTH);
            builder.append(" ...");
            formattedHtml = builder;
        }

        // Setup the favourite button
        if(showFavourite){
            favouriteButton.setVisibility(View.VISIBLE);
            if(service.isWineFavourite(wine)){
                favouriteButton.setImageResource(R.drawable.favourite_icon_red_selected);
            }
            else {
                favouriteButton.setImageResource(R.drawable.favourite_icon_red_norm);
            }
        }
        else {
            favouriteButton.setVisibility(View.GONE);
        }

        description.setText(formattedHtml);
    }

    @Click(R.id.wine_favourite_button)
    protected void favourite(){
        // Toggle favourite status!
        if(service.isWineFavourite(wine)){
            service.setWineFavourite(wine, false);
            favouriteButton.setImageResource(R.drawable.favourite_icon_red_norm);
        }
        else {
            FavouriteDialog.startDialog(getContext(), wine.getWineryId(), new FavouriteDialog.FavouriteCompleted() {
                @Override
                public void onFavouriteCompleted() {
                    service.setWineFavourite(wine, true);
                    favouriteButton.setImageResource(R.drawable.favourite_icon_red_selected);
                }
            });
        }
    }
}
