package au.net.winehound.ui.fragments;

import android.location.Location;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ItemClick;

import java.util.List;
import java.util.Set;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.domain.webservice.WineryPage;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.WineryActivity;
import au.net.winehound.ui.views.WineryView;

@EFragment(R.layout.fragment_wineries)
public class WineriesFragment extends AbstractListFragment<WineryLightweight> {

    @FragmentById(R.id.wineries_map_fragment)
    protected WineriesMapFragment mapView;

    private boolean listMode = true;

    @Override
    protected  void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) throws Exception{
        Location lastLocation = getLastLocation();
        Log.i(LogTags.UI, "Downloading winery page " + pageNum);

        WineryPage page;
        if(lastLocation == null || order == WineHoundService.SearchOrder.Alphabetical || (search != null && !search.isEmpty())){
            page = service.downloadAndSaveWineries(pageNum, search, filterStates, visibleIds);
        }
        else{
            page = service.downloadAndSaveWineries(pageNum, lastLocation, filterStates, visibleIds);
        }
        Log.i(LogTags.UI, "Got  " + page.getWineries().size() + " wineries for page " +  pageNum + " and " + page.getMeta().getDeletedIds().length + " deleted IDs");

    }

    @AfterViews
    protected void setupUi(){
        listMode();
    }

    public boolean isListMode(){
        return listMode;
    }

    public void mapMode(){

        getChildFragmentManager().beginTransaction().show(mapView).commit();
        listMode = false;
    }

    public void listMode(){
        listMode = true;
        getChildFragmentManager().beginTransaction().hide(mapView).commit();

    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order){
        Location lastLocation = getLastLocation();

        if(lastLocation == null || order == WineHoundService.SearchOrder.Alphabetical || (search != null && !search.isEmpty())){
            return service.getWineries(search, filterStates);
        }
        else{
            return service.getWineries(filterStates, lastLocation);
        }
    }

    @Override
    protected View createView() {
        return WineryView_.build(getActivity());
    }

    @Override
    protected void setViewItem(View view, WineryLightweight item) {
        ((WineryView)view).setWinery(item, getLastLocation(), service);
    }

    @Override
    protected WineryLightweight mapResults(AndroidDatabaseResults results) {
        return service.mapWineryLightweightResults(results);
    }

    @Override
    protected int getNoResultsText() {
        if(currentSearchString == null || currentSearchString.isEmpty()){
            return R.string.fetching_wineries;
        }
        else{
            return R.string.no_results;
        }
    }

    @ItemClick(R.id.listview)
    protected void wineryClicked(int position){
        Winery winery = service.getWinery(getItem(position).getId());
        startActivity(WineryActivity.start(getActivity(), winery));
    }
}
