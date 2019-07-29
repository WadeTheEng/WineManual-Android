package au.net.winehound.ui.fragments;

import android.location.Location;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

import java.util.List;
import java.util.Set;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.RegionLightweight;
import au.net.winehound.domain.State;
import au.net.winehound.domain.webservice.RegionPage;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.RegionActivity;
import au.net.winehound.ui.views.RegionView;
import au.net.winehound.ui.views.RegionView_;

@EFragment(R.layout.fragment_listview)
public class RegionsFragment extends AbstractListFragment<RegionLightweight> {

    @Override
    protected  void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) throws Exception{
        Location lastLocation = getLastLocation();

        // Download from the service
        Log.i(LogTags.UI, "Downloading regions page " + pageNum);
        RegionPage page = null;
        if(lastLocation == null || order == WineHoundService.SearchOrder.Alphabetical){
            page = service.downloadAndSaveRegions(pageNum, search, filterStates, visibleIds);
        }
        else{
            page = service.downloadAndSaveRegions(pageNum, lastLocation, filterStates, visibleIds);
        }
        Log.i(LogTags.UI, "Got  " + page.getRegions().size() + " regions for page " +  pageNum + " and " + page.getMeta().getDeletedIds().length + " deleted IDs");

    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order){
        Location lastLocation = getLastLocation();


        if(lastLocation == null || order == WineHoundService.SearchOrder.Alphabetical || (search != null && !search.isEmpty())){
            return service.getRegions(search, filterStates);
        }
        else{
            return service.getRegions(filterStates, lastLocation);
        }
    }

    @Override
    protected View createView() {
        return RegionView_.build(getActivity());
    }

    @Override
    protected void setViewItem(View view, RegionLightweight item) {
        ((RegionView)view).setRegion(item, getLastLocation());
    }

    @Override
    protected RegionLightweight mapResults(AndroidDatabaseResults results) {
        return service.mapRegionLightweightResults(results);
    }

    @ItemClick(R.id.listview)
    protected void regionClicked(int position){
        Region region = service.getRegion(getItem(position).getId());
        startActivity(RegionActivity.start(getActivity(), region));
    }

    @Override
    protected int getNoResultsText() {
        if(currentSearchString == null || currentSearchString.isEmpty()){
            return R.string.fetching_regions;
        }
        else{
            return R.string.no_results;
        }
    }
}
