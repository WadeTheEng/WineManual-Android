package au.net.winehound.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.State;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.fragments.MainFragment;

@EViewGroup(R.layout.view_searchcontrols)
public class SearchControlsView extends FrameLayout {

    @ViewById(R.id.search_order_nearby)
    protected ImageView searchNearby;

    @ViewById(R.id.search_order_alpha)
    protected ImageView searchAlpha;

    @ViewById(R.id.search_filter)
    protected ImageView searchFilter;

    @ViewById(R.id.search_close)
    protected View searchClose;

    @ViewById(R.id.search_text)
    protected EditText searchText;

    @ViewById(R.id.search_filter_view)
    protected View filterView;

    @ViewById(R.id.search_state_list)
    protected ListView stateList;

    private MainFragment mainFragment;

    public SearchControlsView(Context context) {
        super(context);
    }

    public SearchControlsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchControlsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    protected void setupUi(){

        // Add our focus changed listener
        searchText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    focusSearch();
                } else {
                    unfocusSearch();
                }
            }
        });

        // Populate the state list
        ArrayAdapter stateListAdapter = new ArrayAdapter(getContext(),R.layout.edmond_sans_simple_list_item_multiple_choice, State.getAll());
        stateList.setAdapter(stateListAdapter);
    }

    @Click(R.id.search_text)
    protected void searchTextClicked(){
        focusSearch();
    }

    @Click(R.id.search_search_icon)
    protected void searchIconClicked(){
        focusSearch();
    }

    @Click(R.id.search_close)
    protected void searchCloseClicked(){
        unfocusSearch();
    }

    @Click(R.id.search_filter)
    protected void filtersClicked(){
        if(filterView.getVisibility() == View.VISIBLE){
            filterView.setVisibility(View.GONE);
        }
        else{
            filterView.setVisibility(View.VISIBLE);
            unfocusSearch();

            // Setup the filter states
            mainFragment.getFilterStates();
            // TODO select the filtered states
        }
    }

    @ItemClick(R.id.search_state_list)
    public void stateSelected() {
        ArrayList<State> selectedStates = new ArrayList<State>();

        int len = stateList.getCount();
        SparseBooleanArray checked = stateList.getCheckedItemPositions();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                selectedStates.add((State) stateList.getAdapter().getItem(i));
            }
        }

        if(selectedStates.isEmpty()){
            searchFilter.setImageResource(R.drawable.filter_icon);
        }
        else{
            searchFilter.setImageResource(R.drawable.filter_icon_filled);
        }

        mainFragment.setFilterStates(selectedStates);
    }

    private  void focusSearch(){
        searchAlpha.setVisibility(View.GONE);
        searchNearby.setVisibility(View.GONE);
        searchFilter.setVisibility(View.GONE);
        searchClose.setVisibility(View.VISIBLE);
        filterView.setVisibility(View.GONE);

        searchText.requestFocus();
    }

    private void unfocusSearch(){
        searchAlpha.setVisibility(View.VISIBLE);
        searchNearby.setVisibility(View.VISIBLE);
        searchFilter.setVisibility(View.VISIBLE);
        searchClose.setVisibility(View.GONE);
        searchText.clearFocus();
        searchText.setText("");

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    public void hideSearchView(){
        unfocusSearch();
    }

    @Click(R.id.search_order_nearby)
    protected void orderNearbyClicked(){
        searchAlpha.setImageResource(R.drawable.btn_a_z_normal);
        searchNearby.setImageResource(R.drawable.btn_nearby_selected);

        mainFragment.setOrder(WineHoundService.SearchOrder.Distance);
    }

    @Click(R.id.search_order_alpha)
    protected void orderAlphaClicked(){
        searchAlpha.setImageResource(R.drawable.btn_a_z_selected);
        searchNearby.setImageResource(R.drawable.btn_nearby_normal);

        mainFragment.setOrder(WineHoundService.SearchOrder.Alphabetical);
    }

    @Click(R.id.search_apply_filter)
    protected void applyFilterClicked(){

        // Just toggle off the filter view, filters are already applied!
        filtersClicked();
    }

    @TextChange(R.id.search_text)
    protected void searchTextChanged(CharSequence text){
        // Quick exit, we don't have a fragment interested yet
        if(mainFragment == null){
            return;
        }

        if(text.length() == 0){
            mainFragment.clearSearch();
            return;
        }
        mainFragment.search(text.toString());
    }

    public void setMainFragment(MainFragment mainFragment){
        this.mainFragment = mainFragment;
    }

    public void setSearchOrder(WineHoundService.SearchOrder searchOrder) {
        if(searchOrder == WineHoundService.SearchOrder.Alphabetical){
            searchAlpha.setImageResource(R.drawable.btn_a_z_selected);
            searchNearby.setImageResource(R.drawable.btn_nearby_normal);
        }
        else{
            searchAlpha.setImageResource(R.drawable.btn_a_z_normal);
            searchNearby.setImageResource(R.drawable.btn_nearby_selected);
        }
    }
}
