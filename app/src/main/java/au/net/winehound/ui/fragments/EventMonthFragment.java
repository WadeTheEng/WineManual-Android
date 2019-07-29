package au.net.winehound.ui.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonet.views.BtCalendarView;
import com.bonet.views.BtDate;
import com.bonet.views.BtMonth;
import com.bonet.views.DayGridAdapter;
import com.bonet.views.GridBtMonthViewProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.EventLightweight;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.EventActivity;
import au.net.winehound.ui.FeaturedEventActivity;
import au.net.winehound.ui.views.EventListitemView;
import au.net.winehound.ui.views.EventView;

@EFragment(R.layout.fragment_event_month)
public class EventMonthFragment extends Fragment {

    private static final String ARG_MONTH_START_DATE = "EXTRA_DATE";

    @ViewById(R.id.event_month_list)
    protected LinearLayout eventList;

    @ViewById(R.id.event_month_no_events)
    protected View noEvents;

    @ViewById(R.id.event_month_progress)
    protected View progress;

    @ViewById(R.id.event_month_calender)
    protected BtCalendarView calendarView;

    @FragmentArg(ARG_MONTH_START_DATE)
    protected Calendar monthStateDate;

    @FragmentArg
    protected boolean tradeEvents;

    @Bean
    protected WineHoundService service;

    @InstanceState
    protected Integer selectedDay;


    private DayGridAdapter calenderAdapter;
    private Map<Integer, List<EventLightweight>> eventsByDay = new HashMap<Integer, List<EventLightweight>>();
    private List<EventLightweight> allEvents = new ArrayList<EventLightweight>();

    @AfterViews
    protected void setupUI() {

        calendarView.initializeAsGrid();

        // Move to the end of the month
        Calendar monthEndDate = (Calendar) monthStateDate.clone();
        monthEndDate.add(Calendar.MONTH, 1);
        monthEndDate.add(Calendar.DAY_OF_MONTH, -1);

        BtMonth month = new BtMonth(monthStateDate.get(Calendar.YEAR), monthStateDate.get(Calendar.MONTH));
        BtDate minDate = new BtDate(monthStateDate.get(Calendar.YEAR), monthStateDate.get(Calendar.MONTH), monthStateDate.get(Calendar.DAY_OF_MONTH));
        BtDate maxDate = new BtDate(monthEndDate.get(Calendar.YEAR), monthEndDate.get(Calendar.MONTH), monthEndDate.get(Calendar.DAY_OF_MONTH));

        GridBtMonthViewProvider provider = (GridBtMonthViewProvider)calendarView.getMonthViewProvider();

        calenderAdapter = new DayGridAdapter(getActivity(), month, minDate, maxDate){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // Transform the 1D position two 2D
                int column = position %7;
                int row = position / 7;

                // Gets the date for the position
                final int day = mMonthDisplay.getDayAt(row, column);

                boolean isValid;

                TextView tv;
                if(null == convertView) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_calendar_day, null);
                }

                tv = (TextView)convertView.findViewById(R.id.calendar_day_text);

                // Sets the Text
                tv.setText(day +"");

                // Whether the current cell represents a valid date
                isValid = (mMonthDisplay.isWithinCurrentMonth(row, column)) &&
                        mMonth.getDate(day).isWithinBounds(mMinDate,mMaxDate) ;

                // Displays text with a lighter color in case the cell is no part of the month
                int textColor = isValid? Color.BLACK: Color.LTGRAY;

                // And disables the click
                convertView.setEnabled(isValid);

                // Sets the text color
                tv.setTextColor(textColor);

                // Hide / Show the event indicator
                View eventIndicator = convertView.findViewById(R.id.calendar_day_event);
                if(isValid && eventsByDay.containsKey(day) && !eventsByDay.get(day).isEmpty()){
                    eventIndicator.setVisibility(View.VISIBLE);
                }
                else{
                    eventIndicator.setVisibility(View.GONE);
                }

                // Hide / Show the day selection
                View daySelection = convertView.findViewById(R.id.calendar_day_selection);
                if(selectedDay != null && day == selectedDay && isValid){
                    daySelection.setVisibility(View.VISIBLE);
                    tv.setTextColor(getResources().getColor(android.R.color.white));
                }
                else{
                    daySelection.setVisibility(View.GONE);
                    tv.setTextColor(getResources().getColor(R.color.winehound_grey));
                }


                if(isValid){
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectedDay = day;
                            calenderAdapter.notifyDataSetChanged();
                            showEventsInList();
                        }
                    });
                }

                return convertView;
            }

        };
        provider.setAdapter(calenderAdapter);

        calendarView.getLeftButton().setVisibility(View.GONE);
        calendarView.getTitleTextView().setVisibility(View.GONE);
        calendarView.getRightButton().setVisibility(View.GONE);

        downloadEvents();
    }


    @Background
    protected void downloadEvents(){
        showProgress(true);

        try{
            loadAndDisplayEventsByDay();

            service.downloadAndSaveEvents(monthStateDate.get(Calendar.YEAR), monthStateDate.get(Calendar.MONTH));

            loadAndDisplayEventsByDay();
        }
        catch (Exception e){
            Log.e(LogTags.UI, "Error downloading events", e);
        }

        showProgress(false);
    }

    @UiThread
    protected void showProgress(boolean showProgress){
        if(showProgress){
            progress.setVisibility(View.VISIBLE);
        }
        else{
            progress.setVisibility(View.GONE);
        }
    }

    private void loadAndDisplayEventsByDay(){
        // Load the events from the DB
        int year = monthStateDate.get(Calendar.YEAR);
        int month = monthStateDate.get(Calendar.MONTH);

        List<EventLightweight> allEvents = service.getEventsLightweight(year, month, tradeEvents);
        Map<Integer, List<EventLightweight>> eventsByDay = EventLightweight.organiseByDay(year, month, allEvents);

        showEventsInCalenderAndList(eventsByDay, allEvents);
    }

    @UiThread
    protected void showEventsInCalenderAndList(Map<Integer, List<EventLightweight>> newEventsByDay, List<EventLightweight> newAllEvents){
        if(!isVisible()){
            // Quick exit - we are no longer visible
            return;
        }

        eventsByDay = newEventsByDay;
        allEvents = newAllEvents;

        // Tell the adapter to referesh its self.
        calenderAdapter.notifyDataSetChanged();

        showEventsInList();
    }

    private void showEventsInList(){
        eventList.removeAllViews();

        List <EventLightweight> events;
        if(selectedDay == null){
            // Show all events
            events = allEvents;
        }
        else{
            // Only show events from the selected day
            events = eventsByDay.get(selectedDay);
        }

        if(events.isEmpty()){
            noEvents.setVisibility(View.VISIBLE);
            eventList.setVisibility(View.GONE);
        }
        else{
            noEvents.setVisibility(View.GONE);
            eventList.setVisibility(View.VISIBLE);

            for(EventLightweight event : events){
                View eventView = createEventView(event);
                eventList.addView(eventView);
            }
        }
    }

    private View createEventView(final EventLightweight event){
        EventListitemView eventView = EventListitemView_.build(getActivity());
        eventView.setEvent(event);

        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event.isFeatured()){
                    startActivity(FeaturedEventActivity.start(getActivity(), service.getEvent(event.getId())));
                }
                else{
                    startActivity(EventActivity.start(getActivity(), service.getEvent(event.getId())));
                }
            }
        });

        return eventView;
    }

    public static EventMonthFragment newInstance(Calendar monthStartDate, boolean tradeEvents) {
        EventMonthFragment frag = EventMonthFragment_.builder().monthStateDate(monthStartDate).tradeEvents(tradeEvents).build();

        return frag;
    }
}
