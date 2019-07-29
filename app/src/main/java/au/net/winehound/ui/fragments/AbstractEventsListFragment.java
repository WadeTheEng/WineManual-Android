package au.net.winehound.ui.fragments;

import android.view.View;

import com.j256.ormlite.android.AndroidDatabaseResults;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.EventLightweight;
import au.net.winehound.ui.EventActivity;
import au.net.winehound.ui.FeaturedEventActivity;
import au.net.winehound.ui.views.EventListitemView;
import au.net.winehound.ui.views.EventListitemView_;

/**
 * Superclass for a fragment which shows a list of events
 */
@EFragment(R.layout.fragment_listview)
public abstract class AbstractEventsListFragment extends AbstractListFragment<EventLightweight> {
    @AfterViews
    protected void setupListView(){
        listView.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
    }

    @Override
    protected View createView() {
        return EventListitemView_.build(getActivity());
    }

    @Override
    protected void setViewItem(View view, EventLightweight item) {
        ((EventListitemView)view).setEvent(item);
    }

    @Override
    protected EventLightweight mapResults(AndroidDatabaseResults results) {
        return service.mapEventLightweightResults(results);
    }

    @ItemClick(R.id.listview)
    protected void eventClicked(int position){
        Event event = service.getEvent(getItem(position).getId());

        if(event.isFeatured()){
            startActivity(FeaturedEventActivity.start(getActivity(), service.getEvent(event.getId())));
        }
        else{
            startActivity(EventActivity.start(getActivity(), service.getEvent(event.getId())));
        }
    }

    @Override
    protected int getNoResultsText() {
        if(currentSearchString == null || currentSearchString.isEmpty()){
            return R.string.fetching_events;
        }
        else{
            return R.string.no_results;
        }
    }
}
