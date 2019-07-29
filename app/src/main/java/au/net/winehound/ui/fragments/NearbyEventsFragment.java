package au.net.winehound.ui.fragments;

import android.location.Location;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;

import java.util.List;
import java.util.Set;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.EventLightweight;
import au.net.winehound.domain.State;
import au.net.winehound.domain.webservice.EventPage;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.EventActivity;
import au.net.winehound.ui.FeaturedEventActivity;
import au.net.winehound.ui.views.EventListitemView;
import au.net.winehound.ui.views.EventListitemView_;

@EFragment(R.layout.fragment_listview)
public class NearbyEventsFragment extends AbstractEventsListFragment {

    @FragmentArg
    protected boolean tradeEvents;

    @Override
    protected  void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) throws Exception{
        Location lastLocation = getLastLocation();

        // Download from the service
        Log.i(LogTags.UI, "Downloading events page " + pageNum);
        EventPage page = null;

        if(tradeEvents){
            page = service.downloadAndSaveTradeEvents(pageNum, visibleIds);
        }
        else if(lastLocation == null){
            page = service.downloadAndSaveEvents(pageNum, visibleIds);
        }
        else{
            page = service.downloadAndSaveEvents(pageNum, lastLocation, visibleIds);
        }
        Log.i(LogTags.UI, "Got  " + page.getEvents().size() + " events for page " +  pageNum + " and " + page.getMeta().getDeletedIds().length + " deleted IDs");
    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order){
        Location lastLocation = getLastLocation();

        if(lastLocation == null){
            return service.getEventsLightweight(tradeEvents);
        }
        else{
            return service.getEventsLightweight(lastLocation, tradeEvents);
        }
    }

    public static NearbyEventsFragment newInstance(boolean tradeEvents) {
        NearbyEventsFragment frag = NearbyEventsFragment_.builder().tradeEvents(tradeEvents).build();

    return frag;
}

}
