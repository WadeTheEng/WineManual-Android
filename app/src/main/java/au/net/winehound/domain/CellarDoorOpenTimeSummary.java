package au.net.winehound.domain;

/**
 * A summary of one or more CellarDoorOpenTime objects.  This may just be one object such as
 * Monday 9:00am - 5:00pm or it may represent several grouped times, such as Monday to Friday
 * 9:00am to 5:00pm
 */
public interface CellarDoorOpenTimeSummary{

    /**
     * Gets the day of the summary - if this summary spans several days, then this will be the
     * last day it spans
     *
     * @return The last day this summary covers
     */
    public CellarDoorOpenTime.Day getDay();
    public String getLeftPart();
    public String getRightPart();
}
