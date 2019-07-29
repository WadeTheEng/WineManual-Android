package au.net.winehound.ui.fragments;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.EFragment;

import java.util.List;
import java.util.Set;

import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.service.WineHoundService;

@EFragment(R.layout.fragment_listview)
public class FavouriteWineiresFragment extends WineriesFragment {

    @Override
    public void onResume() {
        super.onResume();
        resetView();
    }

    @Override
    protected void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) {

    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order) {
        return service.getFavouriteWineries();
    }

    @Override
    protected int getNoResultsText() {
        return R.string.no_fav_wineries;
    }
}
