package au.net.winehound.ui.fragments;

import org.androidannotations.annotations.EFragment;

import au.net.winehound.R;

@EFragment(R.layout.fragment_events)
public class TradeEventsFragment extends EventsFragment {

    @Override
    protected boolean showTradeEvents() {
        return true;
    }
}
