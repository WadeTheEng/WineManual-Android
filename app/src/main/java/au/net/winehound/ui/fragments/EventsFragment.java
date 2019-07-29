package au.net.winehound.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import au.net.winehound.R;

@EFragment(R.layout.fragment_events)
public class EventsFragment extends Fragment {

    @ViewById(R.id.events_months)
    protected TitlePageIndicator titlePage;

    @ViewById(R.id.events_calender_pager)
    protected ViewPager calenderPager;

    @AfterViews
    protected void setupUi() {
        calenderPager.setAdapter(new MonthsPagerAdapter(getChildFragmentManager(), showTradeEvents()));
        titlePage.setViewPager(calenderPager);
    }

    private static class MonthsPagerAdapter extends ArrayPagerAdapter<Fragment>{

        private static final String TAG_NEARBY = "NEARBY";
        private static final String TAG_MONTH = "MONTH_";

        private boolean showTradeEvents;



        public MonthsPagerAdapter(FragmentManager fragmentManager, boolean showTradeEvents) {
            super(fragmentManager, new ArrayList<PageDescriptor>() {
                {
                    add(new NearbyPageDescriptor(TAG_NEARBY, "Nearby"));

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    DateFormat monthFormat = new SimpleDateFormat("MMMM");

                    for (int i = 0; i < 12; i++) {
                        add(new MonthPageDescriptor(TAG_MONTH + cal.get(Calendar.MONTH), monthFormat.format(cal.getTime()), (Calendar) cal.clone()));
                        cal.roll(Calendar.MONTH, 1);
                    }
                }
            });

            this.showTradeEvents = showTradeEvents;
        }

        @Override
        protected Fragment createFragment(PageDescriptor pageDescriptor) {
            if(pageDescriptor instanceof NearbyPageDescriptor){
                return NearbyEventsFragment.newInstance(showTradeEvents);
            }
            else{
                MonthPageDescriptor descriptor = (MonthPageDescriptor)pageDescriptor;
                return EventMonthFragment.newInstance(descriptor.getMonthStartDate(), showTradeEvents);
            }
        }
    }

    protected boolean showTradeEvents(){
        return false;
    }

    private static final class MonthPageDescriptor extends SimplePageDescriptor{

        private Calendar monthStartDate;

        private MonthPageDescriptor(String tag, String title, Calendar monthStartDate) {
            super(tag, title);
            this.monthStartDate = monthStartDate;
        }

        public Calendar getMonthStartDate() {
            return monthStartDate;
        }
    }

    private static final class NearbyPageDescriptor extends SimplePageDescriptor{
        private NearbyPageDescriptor(String tag, String title) {
            super(tag, title);
        }
    }

}
