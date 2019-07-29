package au.net.winehound.ui;

import android.support.v4.app.FragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

@EActivity
public abstract class AbstractWinehoundActivity extends FragmentActivity {

    private boolean destroyed;

    @AfterViews
    protected void setTitleFont(){
        setTitle(new EdmondSansString(getTitle(), this));
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OptionsItem(android.R.id.home)
    protected void home() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    public boolean isActivityDestroyed() {
        return destroyed;
    }
}
