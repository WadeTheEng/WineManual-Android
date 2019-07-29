package au.net.winehound;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.CellarDoorOpenTimeSummary;

public class CellarDoorOpenTimeTest extends WineHoundTestCase {

    public void testPaperCloud() throws Exception{

        List<CellarDoorOpenTime> times = service.downloadAndSaveWineryCellarDoorOpenTimes(4567);

        assertNotNull(times);
        assertEquals(8, times.size());

        times = service.getWineryCellarDoorOpenTimes(4567);

        assertNotNull(times);
        assertEquals(8, times.size());

        assertEquals(6, times.get(0).getId());
        assertSummary("Monday:", "9:00 AM - 4:00 PM", times.get(0));

        assertEquals(7, times.get(1).getId());
        assertSummary("Wednesday:", "9:00 AM - 4:00 PM", times.get(1));

        assertEquals(8, times.get(2).getId());
        assertSummary("Thursday:", "9:00 AM - 5:00 PM", times.get(2));

        assertEquals(9, times.get(3).getId());
        assertSummary("Friday:", "9:00 AM - 4:00 PM", times.get(3));

        assertEquals(10, times.get(4).getId());
        assertSummary("Call ahead for tasting hours:", "", times.get(4));

        assertEquals(11, times.get(5).getId());
        assertSummary("5 Jul:", "9:00 AM - 12:00 PM", times.get(5));

        assertEquals(13, times.get(6).getId());
        assertSummary("5 Aug:", "9:00 AM - 11:00 AM", times.get(6));

        assertEquals(14, times.get(7).getId());
        assertSummary("Public Holidays:", "1:00 AM - 12:00 AM", times.get(7));
    }

    public void testGroupingDaysOfWeek(){

        List<CellarDoorOpenTime> times = new ArrayList<CellarDoorOpenTime>();

        // We deliberately add the times out of order to make sure it sorts them correctly
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Wednesday, getTime(9), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Monday, getTime(9), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Friday, getTime(9), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Tuesday, getTime(9), getTime(17)));

        // This should result in Monday - Wednesday 9:00am - 5:00pm, Thursday 9:00am - 5:00pm
        List<CellarDoorOpenTimeSummary> summaries = CellarDoorOpenTime.groupTimes(times);

        assertEquals(2, summaries.size());
        assertSummary("Monday - Wednesday:", "9:00 am - 5:00 pm", summaries.get(0));
        assertSummary("Friday:", "9:00 am - 5:00 pm", summaries.get(1));
    }

    public void testGroupingMultipleTimesPerDay(){
        List<CellarDoorOpenTime> times = new ArrayList<CellarDoorOpenTime>();

        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Monday, getTime(13), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Monday, getTime(9), getTime(11)));

        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Tuesday, getTime(9), getTime(11)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Tuesday, getTime(13), getTime(17)));

        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Wednesday, getTime(13), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Wednesday, getTime(9), getTime(11)));

        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Thursday, getTime(13), getTime(17)));


        // This should result in Monday - Wednesday 9:00am - 11:00am, Monday - Thursday 1:00pm - 5:00pm
        List<CellarDoorOpenTimeSummary> summaries = CellarDoorOpenTime.groupTimes(times);

        assertEquals(2, summaries.size());
        assertSummary("Monday - Wednesday:", "9:00 am - 11:00 am", summaries.get(0));
        assertSummary("Monday - Thursday:", "1:00 pm - 5:00 pm", summaries.get(1));
    }

    public void testIncludeNotGrouping(){
        List<CellarDoorOpenTime> times = new ArrayList<CellarDoorOpenTime>();

        // We deliberately add the times out of order to make sure it sorts them correctly
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Wednesday, getTime(9), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Monday, getTime(11), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.PublicHoliday, getTime(9), getTime(17)));
        times.add(new CellarDoorOpenTime(CellarDoorOpenTime.Day.Tuesday, getTime(9), getTime(13)));

        // Nothing should be summarised, but it should at least be in order
        List<CellarDoorOpenTimeSummary> summaries = CellarDoorOpenTime.groupTimes(times);

        assertEquals(4, summaries.size());
        assertSummary("Monday:", "11:00 am - 5:00 pm", summaries.get(0));
        assertSummary("Tuesday:", "9:00 am - 1:00 pm", summaries.get(1));
        assertSummary("Wednesday:", "9:00 am - 5:00 pm", summaries.get(2));
        assertSummary("Public Holidays:", "9:00 am - 5:00 pm", summaries.get(3));
    }

    public void testGroupingPaperCloud() throws Exception {
        List<CellarDoorOpenTime> times = service.downloadAndSaveWineryCellarDoorOpenTimes(4567);

        // Actually, nothing in the papercloud winery can be summarised.
        List<CellarDoorOpenTimeSummary> summaries = CellarDoorOpenTime.groupTimes(times);

        assertEquals(8, summaries.size());

        assertSummary("Monday:", "9:00 AM - 4:00 PM", summaries.get(0));
        assertSummary("Wednesday:", "9:00 AM - 4:00 PM", summaries.get(1));
        assertSummary("Thursday:", "9:00 AM - 5:00 PM", summaries.get(2));
        assertSummary("Friday:", "9:00 AM - 4:00 PM", summaries.get(3));
        assertSummary("Public Holidays:", "1:00 AM - 12:00 AM", summaries.get(4));
        assertSummary("Call ahead for tasting hours:", "", summaries.get(5));
        assertSummary("5 Jul:", "9:00 AM - 12:00 PM", summaries.get(6));
        assertSummary("5 Aug:", "9:00 AM - 11:00 AM", summaries.get(7));
    }

    private Date getTime(int hourOfDay){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        return cal.getTime();
    }

    private void assertSummary(String expectedLeft, String expectedRight, CellarDoorOpenTimeSummary actual){
        assertEqualsIgnoreCase(expectedLeft, actual.getLeftPart());
        assertEqualsIgnoreCase(expectedRight, actual.getRightPart());
    }

    private void assertEqualsIgnoreCase(String expected, String actual){
        assertEquals(expected.toUpperCase(), actual.toUpperCase());
    }
}
