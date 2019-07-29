package au.net.winehound.ui.fragments;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.EFragment;

import java.util.List;
import java.util.Set;

import au.net.winehound.R;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.State;
import au.net.winehound.domain.webservice.WineryPage;
import au.net.winehound.service.WineHoundService;

@EFragment(R.layout.fragment_wineries)
public class RegionWineriesFragment extends WineriesFragment {

    private Integer regionID;
    private boolean noWineries = false;


    @Override
    protected void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) throws Exception{
        if (regionID != null) {
            WineryPage page = service.downloadAndSaveRegionWineries(pageNum, regionID);
            if(pageNum == 0 && page.getWineries().isEmpty()){
                noWineries = true;
            }
        }
    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order) {
        if (regionID != null) {
            if(getLastLocation() != null){
                return service.getRegionWineries(regionID, getLastLocation());
            }
            else{
                return service.getRegionWineries(regionID);
            }
        } else {
            return null;
        }
    }

    @Override
    protected int getNoResultsText() {
        if(noWineries){
            return R.string.no_wineries_for_region;
        }
        else{
            return R.string.fetching_wineries;
        }
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
        resetView();

        Region region = service.getRegion(regionID);
        mapView.display(region.getLatitude(), region.getLongitude(), region.getZoomLevel());
    }
}
