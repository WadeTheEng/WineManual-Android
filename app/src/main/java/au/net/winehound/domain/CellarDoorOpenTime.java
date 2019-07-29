package au.net.winehound.domain;


import android.util.Log;
import android.util.TimeUtils;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import au.net.winehound.LogTags;

/**
 * Represents a single time period that a cellar door is opened.  This may be a day of the week, say
 * monday 9:00am - 5:00pm, it may be a date such as 1/1/2015 9:00am - 5:00pm, a public holidays
 * time or even just a message that the user should 'call head for tasting hours' with no time
 * associated.
 *
 * Typically the UI will not display CellarDoorOpenTimes directly, but instead will group them
 * together using the groupTimes method.
 *
 * Open hours defined at https://github.com/Papercloud/WineHoundiOS/blob/master/WineHound/Classes/Helpers/NSAttributedString%2BOpenHours.m
 */
@DatabaseTable
public class CellarDoorOpenTime implements CellarDoorOpenTimeSummary{

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm aa"){
        {
            setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    };

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMM:");

    public enum Day{
        EveryDay(0, "Everyday"),
        Sunday(1, "Sunday"),
        Monday(2, "Monday"),
        Tuesday(3, "Tuesday"),
        Wednesday(4, "Wednesday"),
        Thursday(5, "Thursday"),
        Friday(6, "Friday"),
        Saturday(7, "Saturday"),
        PublicHoliday(8, "Public Holidays"),
        ByAppointment(9, "Call ahead for tasting hours");

        private final int key;
        private final String label;

        private Day(int key, String label) {
            this.label = label;
            this.key = key;
        }

        public int getKey() {
            return this.key;
        }

        public String getLabel() {
            return label;
        }

        public String toString(){
            return label;
        }

        public boolean isDayOfWeek(){
            return this == Sunday || this == Monday || this == Tuesday || this == Wednesday ||
                    this == Thursday || this == Friday || this == Saturday;
        }

        private static Day fromKey(int key) {
            for(Day type : Day.values()) {
                if(type.getKey() == key) {
                    return type;
                }
            }
            return null;
        }
    }

    @DatabaseField(id = true)
    private int id;

    // A key for the Day enum
    @DatabaseField
    private Integer day;

    @DatabaseField
    private String date;

    @SerializedName("open_time")
    @DatabaseField
    private Date openTime;

    @SerializedName("close_time")
    @DatabaseField
    private Date closeTime;

    @SerializedName("winery_id")
    @DatabaseField
    private int wineryId;

    public CellarDoorOpenTime() {
    }

    public CellarDoorOpenTime(Day day, Date openTime, Date closeTime) {
        this.day = day.getKey();
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public Date getOpenTime() {
        return openTime;
    }

    @Override
    public Day getDay(){
        if(day == null){
            return null;
        }
        return Day.fromKey(day);
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public int getWineryId() {
        return wineryId;
    }

    @Override
    public String getLeftPart(){
        if(day == null && date != null){
            try {
                Date parsedDate = DATE_PARSER.parse(date);
                return DATE_FORMAT.format(parsedDate);
            } catch (ParseException e) {
                Log.e(LogTags.SERVICE, "Error parsing date " + date, e);
                return date;
            }

        }
        else{
            return getDay() + ":";
        }
    }

    @Override
    public String getRightPart(){
        if(openTime != null && closeTime != null){

            String date = TIME_FORMAT.format(openTime) + " - " + TIME_FORMAT.format(closeTime);
            return date;
        }
        else{
            return "";
        }
    }

    public static List<CellarDoorOpenTimeSummary> groupTimes(List<CellarDoorOpenTime> times){
        List<CellarDoorOpenTimeSummary> summaries =  new ArrayList<CellarDoorOpenTimeSummary>();

        // Sort our list by day
        Collections.sort(times, new Comparator<CellarDoorOpenTime>() {
            @Override
            public int compare(CellarDoorOpenTime cellarDoorOpenTime, CellarDoorOpenTime cellarDoorOpenTime2) {
                // Handle null days
                if(cellarDoorOpenTime.day == null){
                    return 1;
                }
                else if(cellarDoorOpenTime2.day == null){
                    return  -1;
                }

                if(cellarDoorOpenTime.day == cellarDoorOpenTime2.day && cellarDoorOpenTime.getOpenTime()
                        != null && cellarDoorOpenTime2.getOpenTime() != null){
                    // If they are the same day, sort by open time
                    return cellarDoorOpenTime.getOpenTime().compareTo(cellarDoorOpenTime2.getOpenTime());
                }

                // Otherwise just sort by day
                return cellarDoorOpenTime.day - cellarDoorOpenTime2.day;
            }
        });

        // Produce our summaries
        for(CellarDoorOpenTime toGroup : times){

            boolean hasBeenGrouped = false;

            // Loop through all existing summaries to see if we can group with one of them
            for(int i = 0 ; i < summaries.size() && !hasBeenGrouped ; i++){

                CellarDoorOpenTimeSummary summary = summaries.get(i);
                if(canGroupSummaries(summary, toGroup)) {

                    if (summary instanceof DayGroupingSummary) {
                        // Add our day into the grouping summary
                        DayGroupingSummary groupingSummary = (DayGroupingSummary) summary;
                        groupingSummary.setLastDay(toGroup.getDay());
                    } else {
                        // Create a grouping summary containing both days
                        DayGroupingSummary groupingSummary = new DayGroupingSummary(summary.getDay(), toGroup.getDay(),
                                toGroup.getRightPart());

                        // Replace the existing summary with the grouping one
                        summaries.set(i, groupingSummary);
                    }
                    hasBeenGrouped = true;
                }
            }

            if(!hasBeenGrouped){
                // We couldn't group to an exisitng summary - just add it in
                summaries.add(toGroup);
            }
        }

        return summaries;
    }

    private static boolean canGroupSummaries(CellarDoorOpenTimeSummary summary, CellarDoorOpenTime toGroup) {
        // We can group if both the summary and toGroup have a day of the wee
        // and the summary is the day before toGroup and they both have the same
        // open and close time
        return
                // Summary has a day of the week
                summary.getDay() != null && summary.getDay().isDayOfWeek()
                // To group has a day of the week
                && toGroup.getDay() != null && toGroup.getDay().isDayOfWeek()
                // Summary is one day before to group
                && summary.getDay().getKey() == toGroup.getDay().getKey() - 1
                // Start and end times are the same
                && summary.getRightPart().equals(toGroup.getRightPart());
    }

    private static class DayGroupingSummary implements CellarDoorOpenTimeSummary{

        private Day firstDay;
        private Day lastDay;
        private String rightPart;

        private DayGroupingSummary(Day firstDay, Day lastDay, String rightPart) {
            this.firstDay = firstDay;
            this.lastDay = lastDay;
            this.rightPart = rightPart;
        }

        @Override
        public Day getDay(){
            return lastDay;
        }

        public Day getFirstDay() {
            return firstDay;
        }

        public Day getLastDay() {
            return lastDay;
        }

        public void setLastDay(Day lastDay) {
            this.lastDay = lastDay;
        }

        @Override
        public String getLeftPart() {
            return firstDay.getLabel() + " - " + lastDay.getLabel() + ":";
        }

        @Override
        public String getRightPart() {
            return rightPart;
        }
    }

}
