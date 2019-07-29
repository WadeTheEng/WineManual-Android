package au.net.winehound.ui.fragments;

import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.Winery;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.WineListActivity;
import au.net.winehound.ui.WineListActivity_;
import au.net.winehound.ui.views.WineView;
import au.net.winehound.ui.views.WineView_;

@EFragment(R.layout.fragment_listview)
public class FavouriteWinesFragment extends AbstractListFragment<Wine> {

    @Override
    protected void downloadAndSavePage(int pageNum, String search, List<State> filterStates, WineHoundService.SearchOrder order, Set<Integer> visibleIds) {
    }

    @Override
    protected AndroidDatabaseResults getAllResults(String search, List<State> filterStates, WineHoundService.SearchOrder order) {
        return service.getFavouriteWines();
    }

    @Override
    protected View createView() {
        return WineView_.build(getActivity());
    }

    @Override
    protected void setViewItem(View view, Wine item) {
        ((WineView) view).setWine(item, service, false);
    }

    @Override
    protected Wine mapResults(AndroidDatabaseResults results) {
        return service.mapWineResults(results);
    }

    @Override
    protected int getNoResultsText() {
        return R.string.no_fav_wines;
    }


    @Override
    public void onResume(){
        super.onResume();
        resetView();
    }

    @ItemClick(R.id.listview)
    protected void wineClicked(int position) {

        // Find the wines winery
        Wine wine = getItem(position);

        // Display all of the wineries wines
        List<Wine> allWines = service.getWineryWines(wine.getWineryId());
        startActivity(WineListActivity.start(getActivity(), new ArrayList<Wine>(allWines), wine.getId()));
    }
}

