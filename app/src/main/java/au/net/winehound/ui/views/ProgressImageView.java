package au.net.winehound.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import uk.co.senab.photoview.PhotoViewAttacher;

@EViewGroup(R.layout.view_image)
public class ProgressImageView extends FrameLayout {


    @ViewById(R.id.view_image_image)
    protected ImageView image;

    @ViewById(R.id.view_image_progress)
    protected View progress;

    public ProgressImageView(Context context) {
        super(context);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String imageUrl) {

        Picasso.with(getContext()).load(imageUrl).resize(2048,2048).centerInside().into(image, new Callback() {
            @Override
            public void onSuccess() {
                progress.setVisibility(View.GONE);
                new PhotoViewAttacher(image);
            }

            @Override
            public void onError() {
                progress.setVisibility(View.GONE);
            }
        });

    }
}
