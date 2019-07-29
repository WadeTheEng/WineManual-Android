package au.net.winehound.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;

import au.net.winehound.R;
import au.net.winehound.ui.fragments.MainFragment;

@EActivity(R.layout.activity_main)
public class MainActivity extends AbstractDrawerActivity {

    @FragmentById(R.id.main_fragment)
    protected MainFragment mainFragment;

    @FragmentById(R.id.about_fragment)
    protected Fragment aboutFragment;

    @FragmentById(R.id.settings_fragment)
    protected Fragment settingsFragment;

    @FragmentById(R.id.trade_events_fragment)
    protected Fragment tradeEventsFragment;

    @InstanceState
    protected int selectedFragment;

    @AfterViews
    protected void setupActionBar() {
        // Setup winehound logo in action bar
        // setup action bar for tabs
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setLogo(R.drawable.winehound_logo);

        if (selectedFragment == R.id.about_fragment) {
            aboutClicked();
        } else if (selectedFragment == R.id.settings_fragment) {
            settingsClicked();
        } else {
            homeClicked();
        }
    }

    @Override
    public void onBackPressed() {
        if(selectedFragment == R.id.main_fragment && mainFragment.isSearchControlsVisible()){
            mainFragment.hideSearchControls();
        }
        else{
            super.onBackPressed();
        }
    }


    @Click(R.id.drawer_home)
    protected void homeClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.hide(aboutFragment);
        transaction.hide(settingsFragment);
        transaction.hide(tradeEventsFragment);
        transaction.show(mainFragment);

        transaction.commit();

        selectedFragment = R.id.main_fragment;
        drawerLayout.closeDrawers();
    }

    @Click(R.id.drawer_about)
    protected void aboutClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.show(aboutFragment);
        transaction.hide(settingsFragment);
        transaction.hide(tradeEventsFragment);
        transaction.hide(mainFragment);

        transaction.commit();

        selectedFragment = R.id.about_fragment;
        drawerLayout.closeDrawers();
    }

    @Click(R.id.drawer_settings)
    protected void settingsClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.hide(aboutFragment);
        transaction.show(settingsFragment);
        transaction.hide(tradeEventsFragment);
        transaction.hide(mainFragment);

        transaction.commit();

        selectedFragment = R.id.settings_fragment;
        drawerLayout.closeDrawers();
    }

    @Click(R.id.drawer_trade_events)
    protected void tradeEventsClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.hide(aboutFragment);
        transaction.hide(settingsFragment);
        transaction.show(tradeEventsFragment);
        transaction.hide(mainFragment);

        transaction.commit();

        selectedFragment = R.id.trade_events_fragment;
        drawerLayout.closeDrawers();
    }
}
