package au.net.winehound.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.util.ArrayList;
import java.util.List;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.MainActivity_;
import au.net.winehound.ui.views.SearchControlsView;

@EFragment(R.layout.fragment_favourites)
public class FavouritesFragment extends Fragment{

    private SectionsPagerAdapter sectionsPagerAdapter;

    @ViewById(R.id.favourites_pager)
    protected ViewPager viewPager;

    @ViewById(R.id.favourites_tabs)
    protected TabPageIndicator indicator;

    @AfterViews
    protected void setupUi(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        indicator.setViewPager(viewPager);
    }

    private static class SectionsPagerAdapter extends ArrayPagerAdapter<Fragment> implements IconPagerAdapter {

        public static final String FAVOURITE_WINERIES = "regionsFragment";
        public static final String FAVOURITE_WINES = "wineriesFragment";
        public static final String FAVOURITE_EVENTS = "eventsFragment";

        private final int [] ICONS = {
                R.drawable.wineries_icon,
                R.drawable.wines_icon_favourites,
                R.drawable.events_icon
        };

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, new ArrayList<PageDescriptor>(){
                {
                    add(new SimplePageDescriptor(FAVOURITE_WINERIES, "Wineries"));
                    add(new SimplePageDescriptor(FAVOURITE_WINES, "Wines"));
                    add(new SimplePageDescriptor(FAVOURITE_EVENTS, "Events"));
                }
            });
        }

        @Override
        public int getIconResId(int i) {
            return ICONS[i];
        }

        @Override
        protected Fragment createFragment(PageDescriptor pageDescriptor) {
            if(pageDescriptor.getFragmentTag().equals(FAVOURITE_WINERIES)){
                return FavouriteWineiresFragment_.builder().build();
            }
            else if(pageDescriptor.getFragmentTag().equals(FAVOURITE_WINES)){
                return FavouriteWinesFragment_.builder().build();
            }
            else if(pageDescriptor.getFragmentTag().equals(FAVOURITE_EVENTS)){
                return FavouriteEventsFragment_.builder().build();
            }
            else {
                throw new IllegalArgumentException("Cannot create fragment for " + pageDescriptor.getFragmentTag());
            }
        }
    }
}
