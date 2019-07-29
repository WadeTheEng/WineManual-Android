package au.net.winehound.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.service.LocationHelper;
import au.net.winehound.service.WineHoundService;

@EFragment
public abstract class AbstractListFragment<T> extends Fragment {

    private static final int NUM_PER_PAGE = 25;

    @ViewById(R.id.listview)
    protected ListView listView;

    @ViewById(R.id.listview_progress)
    protected ProgressBar listProgress;

    @ViewById(R.id.listview_no_results)
    protected View noResultsView;

    @ViewById(R.id.listview_no_results_text)
    protected TextView noResultsText;

    @InstanceState
    protected int lastPageDownloaded = 0;

    @Bean
    protected WineHoundService service;

    protected String currentSearchString;

    @InstanceState
    protected ArrayList<State> filterStates = new ArrayList<State>();

    @InstanceState
    protected WineHoundService.SearchOrder searchOrder = WineHoundService.SearchOrder.Distance;


    // A set of ID's which we have checked with the server to see if they are already
    @InstanceState
    protected HashSet<Integer> checkedVisibleIds = new HashSet<Integer>();

    // A set of ID's we have seen, which we still need to check with the server
    @InstanceState
    protected HashSet<Integer> visibleIds = new HashSet<Integer>();

    private LocationHelper locationHelper = new LocationHelper();

    protected abstract void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) throws  Exception;
    protected abstract AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order);
    protected abstract View createView();
    protected abstract void setViewItem(View view, T item);
    protected abstract T mapResults(AndroidDatabaseResults results);
    protected abstract int getNoResultsText();

    protected T getItem(int position){

        if(listView.getAdapter() == null) {
            // Quick exit!
            return null;
        }

        NotifyViewDbResultsAdapter adapter = (NotifyViewDbResultsAdapter)listView.getAdapter();
        adapter.results.moveAbsolute(position);
        return mapResults(adapter.results);
    }

    @UiThread
    protected void showResults(){
        if(!isVisible()){
            // Quick exit - we are no longer visible
            return;
        }

        AndroidDatabaseResults it = getAllResults(currentSearchString, filterStates, searchOrder);

        if(it == null || it.getCount() == 0){
            noResultsText.setText(getNoResultsText());
            noResultsView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else {
            noResultsView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            if(listView.getAdapter() == null){
                NotifyViewDbResultsAdapter adapter = new NotifyViewDbResultsAdapter(getActivity(), it);
                listView.setAdapter(adapter);
            }
            else{
                NotifyViewDbResultsAdapter adapter = (NotifyViewDbResultsAdapter)listView.getAdapter();
                adapter.changeResults(it);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Background(id = "downloadPage", serial = "downloadPage", delay = 1000)
    protected void downloadPage(int pageNum, Set<Integer> visibleIdsToCheck) {
        setProgressVisibility(true);
        try {
            downloadAndSavePage(pageNum, currentSearchString, filterStates, searchOrder, visibleIdsToCheck);

            // Update the UI
            showResults();
        } catch (Exception e) {
            Log.e(LogTags.UI, "Error downloading page " + pageNum, e);
        }
        setProgressVisibility(false);
    }

    @UiThread
    protected void setProgressVisibility(boolean visible){
        if(visible){
            listProgress.setVisibility(View.VISIBLE);
        }
        else{
            listProgress.setVisibility(View.GONE);
        }
    }

    public void search(String searchString) {
        Log.i(LogTags.UI, "Wineries searching for " + searchString);

        // Quick exit - sometimes we get duplicate events!
        if(searchString.equals(currentSearchString)){
            return;
        }

        currentSearchString = searchString;
        resetView();
    }

    public void clearSearch() {
        Log.i(LogTags.UI, "Wineries clearing searchMenuItem");

        currentSearchString = null;
        resetView();
    }

    /**
     * Causes the list to be reset to page 0, the list re-rendered and downloading to kick off again
     */
    @AfterViews
    public void resetView(){
        setProgressVisibility(false);
        lastPageDownloaded = 0;

        // Sometimes this gets called before we have been initialised, so we only actually try
        // and display something if we are setup
        if(service != null){
            // Display list data
            showResults();

            // Kickoff network update for next page
            checkedVisibleIds.addAll(visibleIds);
            downloadPage(0, visibleIds);
            visibleIds = new HashSet<Integer>();
        }
    }

    public void setFilterStates(ArrayList<State> filterStates){
        this.filterStates = filterStates;

        resetView();
    }

    public List<State> getFilterStates(){
        return filterStates;
    }

    public void setOrder(WineHoundService.SearchOrder order) {
        searchOrder = order;

        resetView();
    }

    private void itemViewed(int position, int total){
        // Figure out what page we are on
        int pageNum = (int)Math.floor(position / NUM_PER_PAGE) + 1;

        if(pageNum > lastPageDownloaded){
            lastPageDownloaded = pageNum;

            // Then get this page
            checkedVisibleIds.addAll(visibleIds);
            downloadPage(pageNum, visibleIds);
            visibleIds = new HashSet<Integer>();

            // If we are the very last page of the searchMenuItem results
            if(pageNum == (int)Math.floor(total / NUM_PER_PAGE) + 1){
                // Get the next page as well
                downloadPage(pageNum + 1, new HashSet<Integer>());
                // This should be self terminating as the last+1 page has no results, thus never appears
                // in the list, to kick off downloading last+2
                lastPageDownloaded = pageNum + 1;
            }
        }
    }

    public WineHoundService.SearchOrder getSearchOrder() {
        return searchOrder;
    }

    private class NotifyViewDbResultsAdapter extends CursorAdapter {

        private AndroidDatabaseResults results;
        private

        NotifyViewDbResultsAdapter(Context context, AndroidDatabaseResults newResults){
            super(context, newResults.getRawCursor(), 0);
            results = newResults;
        }


        public void changeResults(AndroidDatabaseResults newResults){
            results = newResults;
            changeCursor(results.getRawCursor());
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Get the ID.  Because we are extending a CursorAdapter, we are guaranteed that the
            // ID will always be in a column called _id
            Integer rowID = cursor.getInt(cursor.getColumnIndex("_id"));

            if(!checkedVisibleIds.contains(rowID)){
                visibleIds.add(rowID);
            }

            setViewItem(view, mapResults(results));
            itemViewed(cursor.getPosition(), cursor.getCount());
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View view = createView();
            setViewItem(view, mapResults(results));
            return  view;
        }
    }


    @Override
    public void onCreate(Bundle instanceState){
        super.onCreate(instanceState);
        locationHelper.onCreate(getActivity());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        locationHelper.onDestroy();
    }

    public Location getLastLocation(){
        return locationHelper.getLastLocation();
    }

}
