package au.net.winehound.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import au.net.winehound.R;
import au.net.winehound.ui.fragments.RegionWineriesFragment;
import au.net.winehound.ui.fragments.WineriesFragment;

@EActivity(R.layout.activity_region_wineries)
@OptionsMenu(R.menu.menu_region_wineries)
public class RegionWineriesActivity extends AbstractWinehoundActivity {

    private static final String EXTRA_REGION_ID = "EXTRA_REGION_ID";
    private static final String EXTRA_SHOW_MAP = "EXTRA_SHOW_MAP";


    @Extra(EXTRA_SHOW_MAP)
    protected boolean showMap;


    @Extra(EXTRA_REGION_ID)
    protected int regionID;

    @FragmentById(R.id.region_wineries_fragment)
    protected RegionWineriesFragment regionWineriesFragment;



    @AfterViews
    protected void setupUi() {
        regionWineriesFragment.setRegionID(regionID);

        if(showMap){
            mapMenuItemClicked();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Quick exit - we are not yet initilsed
        if (regionWineriesFragment == null) {
            return false;
        }

        if (regionWineriesFragment.isListMode()) {
            menu.findItem(R.id.menu_region_wineries_map).setVisible(true);
            menu.findItem(R.id.menu_region_wineries_list).setVisible(false);
        } else { // We are in map mode
            menu.findItem(R.id.menu_region_wineries_map).setVisible(false);
            menu.findItem(R.id.menu_region_wineries_list).setVisible(true);
        }

        return true;
    }

    @OptionsItem(R.id.menu_region_wineries_map)
    protected void mapMenuItemClicked() {
        regionWineriesFragment.mapMode();
        invalidateOptionsMenu();
    }

    @OptionsItem(R.id.menu_region_wineries_list)
    protected void listMenuItemClicked() {

        regionWineriesFragment.listMode();
        invalidateOptionsMenu();
    }


    public static Intent start(Context context, int regionID) {
        return start(context, regionID, false);
    }


    public static Intent start(Context context, int regionID, boolean showMap) {
        Intent i = new Intent(context, RegionWineriesActivity_.class);
        i.putExtra(EXTRA_REGION_ID, regionID);
        i.putExtra(EXTRA_SHOW_MAP, showMap);

        return i;
    }
}
