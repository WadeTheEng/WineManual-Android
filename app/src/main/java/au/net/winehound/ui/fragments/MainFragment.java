package au.net.winehound.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.MainActivity_;
import au.net.winehound.ui.views.SearchControlsView;

@EFragment(R.layout.fragment_main)
@OptionsMenu(R.menu.menu_main)
public class MainFragment extends Fragment{

    private SectionsPagerAdapter sectionsPagerAdapter;

    @ViewById(R.id.main_pager)
    protected ViewPager viewPager;

    @ViewById(R.id.main_tabs)
    protected TabPageIndicator indicator;

    @ViewById(R.id.main_search_controls)
    protected SearchControlsView searchControls;

    @AnimationRes(R.anim.slide_in_top)
    protected Animation slideInTop;

    @AnimationRes(R.anim.slide_out_top)
    protected Animation slideOutTop;

    @InstanceState
    protected boolean controlsVisible = false;


    @AfterViews
    protected void setupUi(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        indicator.setViewPager(viewPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // The state management for the visibility of the search controls if fiendish.
                // This method actually gets called when the ui is re-initilised after the screen has
                // been rotated.  We are storing the controlsVisible as @InstanceState.  This means
                // that the call to hideSearchControls() will actually get executed and any visible
                // search controls (and the keyboard) will be hidden on rotate.
                hideSearchControls();
            }

            @Override
            public void onPageSelected(int position) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Hide our searchMenuItem controls
        getActivity().invalidateOptionsMenu();
        searchControls.setMainFragment(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        // Quick exit - we are not yet initilsed
        if(viewPager == null){
            return;
        }

        if(viewPager.getCurrentItem() == SectionsPagerAdapter.REGIONS_PAGE){
            menu.findItem(R.id.menu_main_search).setVisible(true);
            menu.findItem(R.id.menu_main_map).setVisible(false);
            menu.findItem(R.id.menu_main_list).setVisible(false);
        }
        else if(viewPager.getCurrentItem() == SectionsPagerAdapter.WINERIES_PAGE){

            WineriesFragment wineriesFrag = (WineriesFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
            if(wineriesFrag.isListMode()){
                menu.findItem(R.id.menu_main_search).setVisible(true);
                menu.findItem(R.id.menu_main_map).setVisible(true);
                menu.findItem(R.id.menu_main_list).setVisible(false);
            }
            else{ // We are in map mode
                menu.findItem(R.id.menu_main_search).setVisible(false);
                menu.findItem(R.id.menu_main_map).setVisible(false);
                menu.findItem(R.id.menu_main_list).setVisible(true);
            }
        }
        else{
            menu.findItem(R.id.menu_main_search).setVisible(false);
            menu.findItem(R.id.menu_main_map).setVisible(false);
            menu.findItem(R.id.menu_main_list).setVisible(false);
        }
    }

    @OptionsItem(R.id.menu_main_map)
    protected void mapMenuItemClicked(){
        // Quick exit, we are not on the winery page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE){
            return;
        }

        hideSearchControls();
        WineriesFragment wineriesFrag = (WineriesFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        wineriesFrag.mapMode();
        getActivity().invalidateOptionsMenu();
    }

    @OptionsItem(R.id.menu_main_list)
    protected void listMenuItemClicked(){
        // Quick exit, we are not on the winery page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE){
            return;
        }
        WineriesFragment wineriesFrag = (WineriesFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        wineriesFrag.listMode();
        getActivity().invalidateOptionsMenu();
    }

    public void search(String searchString) {
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
           && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return;
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        currentListFrag.search(searchString);
    }

    public void clearSearch() {
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
                && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return;
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        currentListFrag.clearSearch();
    }

    public void setOrder(WineHoundService.SearchOrder order) {
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
                && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return;
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        currentListFrag.setOrder(order);
    }

    public void setFilterStates(ArrayList<State> filterStates){
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
                && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return;
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        currentListFrag.setFilterStates(filterStates);
    }

    public List<State> getFilterStates(){
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
                && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return new ArrayList<State>();
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());
        return currentListFrag.getFilterStates();
    }

    private void showSearchControls(){
        // Quick exit, we are not on a searchable page!
        if(viewPager.getCurrentItem() != SectionsPagerAdapter.WINERIES_PAGE
                && viewPager.getCurrentItem() != SectionsPagerAdapter.REGIONS_PAGE){
            return;
        }

        // Quick exit - they are already showing!
        if(controlsVisible){
            return;
        }

        AbstractListFragment currentListFrag = (AbstractListFragment) sectionsPagerAdapter.getExistingFragment(viewPager.getCurrentItem());

        searchControls.setVisibility(View.VISIBLE);
        searchControls.startAnimation(slideInTop);

        // Setup search order
        searchControls.setSearchOrder(currentListFrag.getSearchOrder());
        controlsVisible = true;
    }

    public void hideSearchControls(){
        // Quick exit - they are already hidden!
        if(!controlsVisible){
            return;
        }

        slideOutTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchControls.setVisibility(View.GONE);
                controlsVisible = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        searchControls.startAnimation(slideOutTop);
        searchControls.hideSearchView();
    }

    public boolean isSearchControlsVisible(){
        return controlsVisible;
    }

    @OptionsItem(R.id.menu_main_search)
    protected void searchClicked(){
        if(controlsVisible){
            hideSearchControls();
        }
        else{
            showSearchControls();
        }
    }

    private static class SectionsPagerAdapter extends ArrayPagerAdapter<Fragment> implements IconPagerAdapter {

        private static final String REGIONS_FRAGMENT_TAG = "regionsFragment";
        private static final String WINERIES_FRAGMENT_TAG = "wineriesFragment";
        private static final String FAVOURITES_FRAGMENT_TAG = "favouritesFragment";
        private static final String EVENTS_FRAGMENT_TAG = "eventsFragment";

        private static final int REGIONS_PAGE = 0;
        private static final int WINERIES_PAGE = 1;
        private static final int EVENTS_PAGE = 2;
        private static final int FAVOURITES_PAGE = 3;


        private final int [] ICONS = {
                R.drawable.regions_icon,
                R.drawable.wineries_icon,
                R.drawable.events_icon,
                R.drawable.favourites_icon
        };

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, new ArrayList<PageDescriptor>(){
                {
                    add(new SimplePageDescriptor(REGIONS_FRAGMENT_TAG, "Regions"));
                    add(new SimplePageDescriptor(WINERIES_FRAGMENT_TAG, "Wineries"));
                    add(new SimplePageDescriptor(EVENTS_FRAGMENT_TAG, "Events"));
                    add(new SimplePageDescriptor(FAVOURITES_FRAGMENT_TAG, "Favourites"));
                }
            });
        }


        @Override
        public int getIconResId(int i) {
            return ICONS[i];
        }

        @Override
        protected Fragment createFragment(PageDescriptor pageDescriptor) {
            if(pageDescriptor.getFragmentTag().equals(REGIONS_FRAGMENT_TAG)){
                return RegionsFragment_.builder().build();
            }
            else if(pageDescriptor.getFragmentTag().equals(WINERIES_FRAGMENT_TAG)){
                return WineriesFragment_.builder().build();
            }
            else if(pageDescriptor.getFragmentTag().equals(FAVOURITES_FRAGMENT_TAG)){
                return FavouritesFragment_.builder().build();
            }
            else if(pageDescriptor.getFragmentTag().equals(EVENTS_FRAGMENT_TAG)){
                return EventsFragment_.builder().build();
            }
            else {
                throw new IllegalArgumentException("Cannot create fragment for " + pageDescriptor.getFragmentTag());
            }
        }
    }
}
