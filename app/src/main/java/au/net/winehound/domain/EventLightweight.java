package au.net.winehound.domain;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A lightweight version of the event class, which is mapped to the same table and is
 * suitable for using in list views or other applications where you need to load a large
 * number of events.  The lightweight events loads none of the FK relationships used in the
 * proper event object
 */
@DatabaseTable(tableName = "Event")
public class EventLightweight {
    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @SerializedName("location_name")
    @DatabaseField
    private String locationName;

    @DatabaseField
    private Date startDate;

    @DatabaseField
    private Date finishDate;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private boolean isFeatured;

    @DatabaseField(canBeNull = true)
    private String headerPhotoThumbUrl;

    @DatabaseField
    private boolean tradeEvent;

    @DatabaseField(canBeNull = true)
    private Integer parentEventId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public String getHeaderPhotoThumbUrl() {
        return headerPhotoThumbUrl;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getFinishDate() {
        return  finishDate;
    }

    public boolean isTradeEvent() {
        return tradeEvent;
    }

    public String getDates(){
        // Quick exit - the dates are null
        if(getStartDate() == null || getFinishDate() == null){
            return "";
        }

        if(isSameDay(getStartDate(), getFinishDate())){
            return Event.SHORT_DATE_FORMAT.format(getStartDate());
        }
        else{
            return Event.SHORT_DATE_FORMAT.format(getStartDate()) + " - " + Event.SHORT_DATE_FORMAT.format(getFinishDate());
        }
    }

    private boolean isSameDay(Date start, Date end){
        Calendar startCal = Calendar.getInstance();
        Calendar finishCal = Calendar.getInstance();
        startCal.setTime(start);
        finishCal.setTime(end);

        return startCal.get(Calendar.YEAR) == finishCal.get(Calendar.YEAR) &&
                startCal.get(Calendar.DAY_OF_YEAR) == finishCal.get(Calendar.DAY_OF_YEAR);

    }

    public static Map<Integer,List<EventLightweight>> organiseByDay(int year, int month, List<EventLightweight> eventList) {
        Map<Integer, List<EventLightweight>> eventsByDay = new HashMap<Integer, List<EventLightweight>>(31);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, 1);

        while (cal.get(Calendar.MONTH) == month){
            Integer day = cal.get(Calendar.DAY_OF_MONTH);
            List<EventLightweight> eventsForDay = new ArrayList<EventLightweight>();
            eventsByDay.put(day, eventsForDay);

            // Get the start of that day
            Date dayStart = cal.getTime();

            // Roll to the first instant of the next day
            cal.add(Calendar.DAY_OF_MONTH, 1);

            // Get the end of that day
            Date dayEnd = cal.getTime();


            // Iterate through all our events checking if they occur on that day
            for (EventLightweight event : eventList){
                if(event.getFinishDate().after(dayStart) && event.getStartDate().before(dayEnd)){
                    eventsForDay.add(event);
                }
            }
        }

        return eventsByDay;
    }
}
