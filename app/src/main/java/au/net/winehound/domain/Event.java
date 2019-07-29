package au.net.winehound.domain;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import au.net.winehound.LogTags;

@DatabaseTable
public class Event implements Serializable{

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");


    public static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("d MMM"){
        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };

    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("EEE d MMMM yyyy"){
        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };


    private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("haa"){
        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };

    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String website;

    @SerializedName("phone_number")
    @DatabaseField
    private String phoneNumber;

    @SerializedName("price_and_description")
    @DatabaseField
    private String priceAndDescription;

    @SerializedName("location_name")
    @DatabaseField
    private String locationName;

    @DatabaseField
    private String address;

    @DatabaseField
    private String description;

    @SerializedName("trade_event")
    @DatabaseField
    private boolean tradeEvent;

    @SerializedName("start_date")
    private String startDateString;

    @DatabaseField
    private Date startDate;

    @SerializedName("finish_date")
    private String finishDateString;

    @DatabaseField
    private Date finishDate;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @SerializedName("is_featured")
    @DatabaseField
    private boolean isFeatured;

    @DatabaseField(canBeNull = true)
    private String headerPhotoThumbUrl;

    @SerializedName("parent_event_id")
    @DatabaseField(canBeNull = true)
    private Integer parentEventId;

    @ForeignCollectionField(eager = true, orderColumnName = "id", orderAscending = false)
    private Collection<Photograph> photographs = new ArrayList<Photograph>();

    // Contains the integer array of region id's we get from the webservice.
    // This will be converted to WineryRegionID's to save in the DB
    @SerializedName("regions")
    private List<Integer> webserviceRegionIDs = new ArrayList<Integer>();

    // Contains the integer array of state id's we get from the webservice.
    // This will be converted to WinerySateID's to save in the DB
    @SerializedName("state_ids")
    private List<Integer> webserviceStateIDs = new ArrayList<Integer>();

    // Contains the integer array of state id's we get from the webservice.
    // This will be converted to WinerySateID's to save in the DB
    @SerializedName("wineries")
    private List<Integer> webserviceWineryIDs = new ArrayList<Integer>();

    @ForeignCollectionField(eager = true)
    private Collection<EventRegionID> regionIds = new ArrayList<EventRegionID>();

    @ForeignCollectionField(eager = true)
    private Collection<EventStateID> stateIds = new ArrayList<EventStateID>();

    @ForeignCollectionField(eager = true)
    private Collection<EventWineryID> wineryIds = new ArrayList<EventWineryID>();


    public Event() {
    }

    /**
     * Creates a new event with the specified name, start and end.  Used for unit testing
     *
     * @param id Event ID
     * @param name The name
     * @param startDate The start date in YYYY-MM-dd format
     * @param finishDate The start date in YYYY-MM-dd format
     */
    public Event(int id, String name, Date startDate, Date finishDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPriceAndDescription() {
        return priceAndDescription;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTradeEvent() {
        return tradeEvent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getFinishDate() {
        return  finishDate;
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

    public Collection<Photograph> getPhotographs() {
        return photographs;
    }

    public Collection<EventRegionID> getRegionIds() {
        return regionIds;
    }

    public Collection<EventWineryID> getWineryIds() {
        return wineryIds;
    }

    public Collection<EventStateID> getStateIds() {
        return stateIds;
    }

    public String getDates(){
        // Quick exit - the dates are null
        if(getStartDate() == null || getFinishDate() == null){
            return "";
        }

        if(isSameDay(getStartDate(), getFinishDate())){
            return SHORT_DATE_FORMAT.format(getStartDate());
        }
        else{
            return SHORT_DATE_FORMAT.format(getStartDate()) + " - " + SHORT_DATE_FORMAT.format(getFinishDate());
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

    public String getDatesLong(){
        // Quick exit - the dates are null
        if(getStartDate() == null || getFinishDate() == null){
            return "";
        }

        if(isSameDay(getStartDate(), getFinishDate())){
            return LONG_DATE_FORMAT.format(getStartDate());
        }
        else{
            return LONG_DATE_FORMAT.format(getStartDate()) + " - " + SHORT_DATE_FORMAT.format(getFinishDate());
        }
    }

    public String getTimes(){
        // Quick exit - the dates are null
        if(getStartDate() == null || getFinishDate() == null){
            return "";
        }
        return SHORT_TIME_FORMAT.format(getStartDate()) + " - " + SHORT_TIME_FORMAT.format(getFinishDate());
    }

    public void initFromWebservice(){

        if (photographs == null) photographs = new ArrayList<Photograph>();

        for(Photograph photograph: photographs){
            photograph.initFromWebserviceEvent(this);
        }

        if(!photographs.isEmpty()){
            headerPhotoThumbUrl = photographs.iterator().next().getThumbUrl();
        }

        // Setup our regions and states
        stateIds.clear();
        for(int stateId: webserviceStateIDs){
            stateIds.add(new EventStateID(stateId, this));
        }

        regionIds.clear();
        for(int regionId: webserviceRegionIDs){
            regionIds.add(new EventRegionID(regionId, this));
        }

        wineryIds.clear();
        for(int wineryId: webserviceWineryIDs){
            wineryIds.add(new EventWineryID(wineryId, this));
        }

        // Setup our start and end date
        try {
            startDate = DATE_PARSER.parse(startDateString);
            finishDate = DATE_PARSER.parse(finishDateString);
        } catch (ParseException e) {
            Log.e(LogTags.SERVICE, "Error parsing date " + startDate);
        }
    }


}
