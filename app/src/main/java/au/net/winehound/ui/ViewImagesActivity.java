package au.net.winehound.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import au.net.winehound.R;
import au.net.winehound.ui.views.ProgressImageView;
import au.net.winehound.ui.views.ProgressImageView_;

@EActivity(R.layout.activity_view_images)
public class ViewImagesActivity extends Activity{

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_IMAGES = "EXTRA_IMAGES";

    @Extra(EXTRA_IMAGES)
    protected List<String> imageUrls;

    @Extra(EXTRA_TITLE)
    protected String title;

    @ViewById(R.id.view_images_viewpager)
    protected ViewPager pager;

    @AfterViews
    protected void setupUi(){
        getActionBar().hide();
        getWindow().setBackgroundDrawableResource(R.color.black);
        setTitle(title);

        pager.setAdapter(new ImagePagerAdapter());
    }

    @Click(R.id.view_images_close)
    protected void closeClicked(){
        finish();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            ProgressImageView imageView = ProgressImageView_.build(ViewImagesActivity.this);

            imageView.setImageUrl(imageUrls.get(position));

            container.addView(imageView,0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object){
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object){
            return view == object;
        }
    }

    public static Intent start(Context context, String title, ArrayList<String> imageUrls){
        Intent intent = new Intent(context, ViewImagesActivity_.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_IMAGES, imageUrls);

        return intent;
    }
}
