package au.net.winehound.service;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.squareup.picasso.Callback;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.Event;
import au.net.winehound.domain.EventLightweight;
import au.net.winehound.domain.EventRegionID;
import au.net.winehound.domain.EventStateID;
import au.net.winehound.domain.EventWineryID;
import au.net.winehound.domain.FavouriteEventID;
import au.net.winehound.domain.FavouriteWineID;
import au.net.winehound.domain.FavouriteWineryID;
import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.RegionLightweight;
import au.net.winehound.domain.State;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.WineRange;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.WineryAmenity;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.domain.WineryRegionID;
import au.net.winehound.domain.WineryStateID;
import au.net.winehound.domain.webservice.CellarDoorOpenTimes;
import au.net.winehound.domain.webservice.EventPage;
import au.net.winehound.domain.webservice.MailingListSubscription;
import au.net.winehound.domain.webservice.RegionPage;
import au.net.winehound.domain.webservice.WinePage;
import au.net.winehound.domain.webservice.WineryPage;

/**
 * Provides the main entry point for fetching WineHound data from
 * both the database and over the network from the WineHound webservice.
 */
@EBean()
public class WineHoundService {

    public static final long MAX_WINERY_MARKERS = 150l;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = Wine.class)
    protected RuntimeExceptionDao<Wine, Integer> wineDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = Winery.class)
    protected RuntimeExceptionDao<Winery, Integer> wineryDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = Photograph.class)
    protected RuntimeExceptionDao<Photograph, Integer> photographDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = WineryAmenity.class)
    protected RuntimeExceptionDao<WineryAmenity, Integer> amenityDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = WineryStateID.class)
    protected RuntimeExceptionDao<WineryStateID, Integer> wineryStateIdDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = WineryRegionID.class)
    protected RuntimeExceptionDao<WineryRegionID, Integer> wineryRegionIdDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = WineRange.class)
    protected RuntimeExceptionDao<WineRange, Integer> wineRangeDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = Region.class)
    protected RuntimeExceptionDao<Region, Integer> regionDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = FavouriteWineryID.class)
    protected RuntimeExceptionDao<FavouriteWineryID, Integer> favouriteWineryIdDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = FavouriteWineID.class)
    protected RuntimeExceptionDao<FavouriteWineID, Integer> favouriteWineIdDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = CellarDoorOpenTime.class)
    protected RuntimeExceptionDao<CellarDoorOpenTime, Integer> cellarDoorOpenTimesDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = WineryLightweight.class)
    protected RuntimeExceptionDao<WineryLightweight, Integer> wineryLightweightDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = RegionLightweight.class)
    protected RuntimeExceptionDao<RegionLightweight, Integer> regionLightweightDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = Event.class)
    protected RuntimeExceptionDao<Event, Integer> eventDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = EventLightweight.class)
    protected RuntimeExceptionDao<EventLightweight, Integer> eventLightweightDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = EventRegionID.class)
    protected RuntimeExceptionDao<EventRegionID, Integer> eventRegionDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = EventStateID.class)
    protected RuntimeExceptionDao<EventStateID, Integer> eventStateDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = EventWineryID.class)
    protected RuntimeExceptionDao<EventWineryID, Integer> eventWineryDao;

    @OrmLiteDao(helper = WineHoundDatabaseHelper.class, model = FavouriteEventID.class)
    protected RuntimeExceptionDao<FavouriteEventID, Integer> favouriteEventIdDao;

    @RestService
    protected WineHoundWebService webService;

    @AfterInject
    protected void setupRootUrl(){
        webService.setRootUrl("http://app.winehound.net.au/");
    }

    /**
     * Saves a winery and all its children (such as photos, logos, etc) into the database.  This
     * method will take care of adding, removing and updating photos as appropriate.
     *
     * @param toSave The winery to save
     */
    protected void createOrUpdateWinery(final Winery toSave) throws SQLException {

        // The main event, insert the winery!
        wineryDao.createOrUpdate(toSave);

        // TODO - We can use DatabaseTableConfig to get all these values dynamically instead of haivng them passed in as params

        // Photos
        createOrUpdateList(toSave.getCellarDoorPhotographs(), photographDao, "wineryCellarDoor_id", toSave.getId(), "id");
        createOrUpdateList(toSave.getPhotographs(), photographDao, "wineryPhoto_id", toSave.getId(), "id");

        // Amenities
        createOrUpdateList(toSave.getAmenities(), amenityDao, "winery_id", toSave.getId(), "dbId");

        // States and regions
        createOrUpdateList(toSave.getRegionIds(), wineryRegionIdDao, "winery_id", toSave.getId(), "id");
        createOrUpdateList(toSave.getStateIds(), wineryStateIdDao, "winery_id", toSave.getId(), "id");

        // Ranges
        createOrUpdateList(toSave.getWineRanges(), wineRangeDao, "winery_id", toSave.getId(), "id");
    }

    /**
     * Saves a event and all its children (such as photos, logos, etc) into the database.  This
     * method will take care of adding, removing and updating photos as appropriate.
     *
     * @param toSave The winery to save
     */
    protected void createOrUpdateEvent(final Event toSave) throws SQLException {

        // The main event, insert the event!
        eventDao.createOrUpdate(toSave);

        // Photos
        createOrUpdateList(toSave.getPhotographs(), photographDao, "event_id", toSave.getId(), "id");

        // States, wineries and regions
        createOrUpdateList(toSave.getRegionIds(), eventRegionDao, "event_id", toSave.getId(), "id");
        createOrUpdateList(toSave.getStateIds(), eventStateDao, "event_id", toSave.getId(), "id");
        createOrUpdateList(toSave.getWineryIds(), eventWineryDao, "event_id", toSave.getId(), "id");
    }



    public boolean isWineryFavourite(Winery toCheck){
        try {
            return favouriteWineryIdDao.queryBuilder().where().eq("winery_id", toCheck).iterator().hasNext();
        }
        catch (SQLException e) {
            Log.e(LogTags.SERVICE, "Error favouriting winery", e);
            return false;
        }
    }

    public void setWineryFavourite(Winery toUpdate, boolean favourite){
        if(favourite){
            favouriteWineryIdDao.createOrUpdate(new FavouriteWineryID(toUpdate));
        }
        else{
            try {
                DeleteBuilder<FavouriteWineryID, Integer> deleteQuery = favouriteWineryIdDao.deleteBuilder();
                deleteQuery.where().eq("winery_id", toUpdate);
                deleteQuery.delete();
            } catch (SQLException e) {
                Log.e(LogTags.SERVICE, "Error favouriting winery", e);
            }
        }
    }

    public boolean isWineFavourite(Wine toCheck){
        try {
            return favouriteWineIdDao.queryBuilder().where().eq("wine_id", toCheck).iterator().hasNext();
        }
        catch (SQLException e) {
            Log.e(LogTags.SERVICE, "Error favouriting wine", e);
            return false;
        }
    }

    public void setWineFavourite(Wine toUpdate, boolean favourite){
        if(favourite){
            favouriteWineIdDao.createOrUpdate(new FavouriteWineID(toUpdate));
        }
        else{
            try {
                DeleteBuilder<FavouriteWineID, Integer> deleteQuery = favouriteWineIdDao.deleteBuilder();
                deleteQuery.where().eq("wine_id", toUpdate);
                deleteQuery.delete();
            } catch (SQLException e) {
                Log.e(LogTags.SERVICE, "Error favouriting wine", e);
            }
        }
    }

    public boolean isEventFavourite(Event toCheck){
        try {
            return favouriteEventIdDao.queryBuilder().where().eq("event_id", toCheck).iterator().hasNext();
        }
        catch (SQLException e) {
            Log.e(LogTags.SERVICE, "Error favouriting event", e);
            return false;
        }
    }

    public void setEventFavourite(Event toUpdate, boolean favourite){
        if(favourite){
            favouriteEventIdDao.createOrUpdate(new FavouriteEventID(toUpdate));
        }
        else{
            try {
                DeleteBuilder<FavouriteEventID, Integer> deleteQuery = favouriteEventIdDao.deleteBuilder();
                deleteQuery.where().eq("event_id", toUpdate);
                deleteQuery.delete();
            } catch (SQLException e) {
                Log.e(LogTags.SERVICE, "Error favouriting event", e);
            }
        }
    }

    /**
     * Saves a region and all its children (such as photos, logos, etc) into the database.  This
     * method will take care of adding, removing and updating photos as appropriate.
     *
     * @param toSave The winery to save
     */
    protected void createOrUpdateRegion(final Region toSave) throws SQLException {
        regionDao.createOrUpdate(toSave);

        // Photos
        createOrUpdateList(toSave.getPhotographs(), photographDao, "region_id", toSave.getId(), "id");
    }

    /**
     * Saves a wine and all its children (such as photos, etc) into the database.  This
     * method will take care of adding, removing and updating photos as appropriate.
     *
     * @param toSave The winery to save
     */
    protected void createOrUpdateWine(final Wine toSave) throws SQLException {
        wineDao.createOrUpdate(toSave);

        // Photos
        createOrUpdateList(toSave.getPhotographs(), photographDao, "wine_id", toSave.getId(), "id");
    }

    /**
     * Persists a list of objects which are linked to a parent object, then deletes any objects in
     * the DB who are not in that list.
     *
     * @param saveList The list of objects to save
     * @param dao A DAO to save the objects with
     * @param parentBacklinkCol A column in the objects which links back to their parent
     * @param parentId The ID of the parent, which these objects belong to
     * @param idCol The ID column of these objects
     * @param <T> The type of object to save
     * @param <ID> The type of ID they have
     * @throws SQLException
     */
    private <T, ID> void createOrUpdateList(Collection<T> saveList, RuntimeExceptionDao<T, ID> dao,
                String parentBacklinkCol, Object parentId, String idCol) throws SQLException {

        // Save eveything in our list
        List<ID> listIDs = new ArrayList<ID>();

        for(T saveObject : saveList){
            dao.createOrUpdate(saveObject);

            listIDs.add(dao.extractId(saveObject));
        }

        // TODO - We can use DatabaseTableConfig to get all these values dynamically instead of haivng them passed in as params

        // Delete everything attached to our parent, but not in our list
        DeleteBuilder<T, ID> deleteOld = dao.deleteBuilder();
        deleteOld.where().eq(parentBacklinkCol, parentId).and().notIn(idCol, listIDs);
        deleteOld.delete();
    }

    /**
     * Delete multiple objects based of an array of ID numbers to remove
     *
     * @param dao The dao for the objects we are removing
     * @param idCol The ID column of these objects
     * @param deleteIds An array of IDS to delete
     * @param <T> The type of object to delete
     * @param <ID> The type of ID it has
     * @throws SQLException
     */
    private <T, ID> void deleteIDList(RuntimeExceptionDao<T, ID> dao, String idCol, ID [] deleteIds) throws SQLException {
        DeleteBuilder<T, ID> deleteOld = dao.deleteBuilder();
        deleteOld.where().in(idCol, Arrays.asList(deleteIds));
        deleteOld.delete();
    }

    /**
     * Gets all known wineries from the database.
     *
     * @return A ClosableIteration with access to all wineries
     */
    public AndroidDatabaseResults getAllWineries(){
        return getWineries(null, new ArrayList<State>());
    }

    public AndroidDatabaseResults getWineries(String name){
        return getWineries(name, null);
    }

    /**
     * Gets a list of wineries ordered alphabetically belonging to a region, ordered alphabetically.
     *
     * @return
     */
    public AndroidDatabaseResults getRegionWineries(int regionID) {
        try {
            QueryBuilder<Winery, Integer> query = wineryDao.queryBuilder();
            query.distinct();

            QueryBuilder<WineryRegionID, Integer> wineryRegionQuery = wineryRegionIdDao.queryBuilder();
            wineryRegionQuery.where().eq("regionID", regionID);

            query.join(wineryRegionQuery);

            query.orderBy("name", true);

            return (AndroidDatabaseResults)query.iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    /**
     * Gets a list of wineries ordered alphabetically belonging to a region, ordered by distance
     * to a location.
     *
     * @return
     */
    public AndroidDatabaseResults getRegionWineries(int regionID, Location nearby) {
        try {
            QueryBuilder<Winery, Integer> query = wineryDao.queryBuilder();
            double latitude = nearby.getLatitude();
            double longitude = nearby.getLongitude();
            double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

            query.selectRaw(
                    "*",
                    String.format("((%f - latitude) * (%f - latitude) + (%f - longitude) * (%f - longitude) * %f) as theDistance", latitude, latitude, longitude, longitude, fudge)
            );


            query.distinct();

            QueryBuilder<WineryRegionID, Integer> wineryRegionQuery = wineryRegionIdDao.queryBuilder();
            wineryRegionQuery.where().eq("regionID", regionID);

            query.join(wineryRegionQuery);

            query.orderByRaw("theDistance ASC");

            return (AndroidDatabaseResults)query.queryRaw().closeableIterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    public AndroidDatabaseResults getWineries(String searchName, List<State> filterStates) {
        try {
            QueryBuilder<Winery, Integer> query = wineryDao.queryBuilder();
            query.distinct();
            Where<Winery, Integer> where;
            if(searchName != null && !searchName.isEmpty()){
                where = query.where();
                where.like("name", searchName + "%");
            }

            if(filterStates != null && !filterStates.isEmpty()){
                // Build a list of the states ids
                List<Integer> stateIds = getStateIDs(filterStates);

                QueryBuilder<WineryStateID, Integer> wineryStateQuery = wineryStateIdDao.queryBuilder();
                wineryStateQuery.where().in("stateID", stateIds);

                query.join(wineryStateQuery);
            }
            query.orderBy("name", true);

            return (AndroidDatabaseResults)query.iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    /**
     * Gets a list of wineries ordred by distance to a location
     *
     * @param filterStates
     * @param nearby
     * @return
     */
    public AndroidDatabaseResults getWineries(List<State> filterStates, Location nearby) {
        try {

            QueryBuilder<Winery, Integer> query = wineryDao.queryBuilder();

            double latitude = nearby.getLatitude();
            double longitude = nearby.getLongitude();
            double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

            query.selectRaw(
                    "*",
                    String.format("((%f - latitude) * (%f - latitude) + (%f - longitude) * (%f - longitude) * %f) as theDistance", latitude, latitude, longitude, longitude, fudge)
            );

            query.distinct();

            if(filterStates != null && !filterStates.isEmpty()){
                // Build a list of the states ids
                List<Integer> stateIds = getStateIDs(filterStates);

                QueryBuilder<WineryStateID, Integer> wineryStateQuery = wineryStateIdDao.queryBuilder();
                wineryStateQuery.where().in("stateID", stateIds);

                query.join(wineryStateQuery);
            }

            query.orderByRaw("theDistance ASC");

            return (AndroidDatabaseResults)query.queryRaw().closeableIterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }


    public CloseableIterator<WineryLightweight> getWineryPlaceMarkers(LatLngBounds bounds){

        try {
            QueryBuilder<WineryLightweight, Integer> query = wineryLightweightDao.queryBuilder();
            query.where().lt("latitude", bounds.northeast.latitude).and().gt("latitude", bounds.southwest.latitude)
                    .and().gt("longitude", bounds.southwest.longitude).and().lt("longitude", bounds.northeast.longitude);
            query.orderBy("tier", true);
            query.limit(MAX_WINERY_MARKERS);

            return query.iterator();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }


    public Region getRegion(int id){
        return regionDao.queryForId(id);
    }


    public Winery getWinery(int id){
        return wineryDao.queryForId(id);
    }

    public AndroidDatabaseResults getFavouriteWineries() {
        try {
                QueryBuilder<FavouriteWineryID, Integer> favouriteWinery = favouriteWineryIdDao.queryBuilder();

                QueryBuilder<Winery, Integer> wineryQuery = wineryDao.queryBuilder();
                return (AndroidDatabaseResults)wineryQuery.join(favouriteWinery).iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    public AndroidDatabaseResults getFavouriteWines() {
        try {
            QueryBuilder<FavouriteWineID, Integer> favouriteWines = favouriteWineIdDao.queryBuilder();

            QueryBuilder<Wine, Integer> wineQuery = wineDao.queryBuilder();
            return (AndroidDatabaseResults)wineQuery.join(favouriteWines).iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    public AndroidDatabaseResults getFavouriteEvents() {
        try {
            QueryBuilder<FavouriteEventID, Integer> favouriteEvents = favouriteEventIdDao.queryBuilder();

            QueryBuilder<Event, Integer> eventQuery = eventDao.queryBuilder();
            return (AndroidDatabaseResults)eventQuery.join(favouriteEvents).iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    /**
     * Notifies the webservice that the user has favourited a winery
     *
     * @param wineryId The winery which was favourited
     */
    public void postFavouriteWinery(Context c, int wineryId){
        MailingListSubscription details = new MailingListSubscription(getFavouritesEmail(c), wineryId);

        webService.postFavouriteWinery(details);
    }


    /**
     * Starts downloading a specific page of wineries from the webservice.  The result
     * will be cached in the database
     *
     * @param pageNumber
     */
    public WineryPage downloadAndSaveWineries(final int pageNumber) throws Exception{

        return downloadAndSaveWineries(pageNumber, "", new ArrayList<State>(), new HashSet<Integer>());
    }

    public WineryPage downloadAndSaveWineries(int pageNumber, String name, List<State> filterStates, Set<Integer> visibleIds) throws Exception{
        // Get the page from the webservice
        WineryPage page;
        if(filterStates.isEmpty()){
            page = webService.getWineries(name, pageNumber, getVisibleIdsParam(visibleIds));
        }
        else{
            page = webService.getWineries(name, pageNumber, getStatesParam(filterStates), getVisibleIdsParam(visibleIds));
        }

        saveWineryPage(page);
        deleteIDList(wineryDao, "_id", page.getMeta().getDeletedIds());
        return page;
    }

    public WineryPage downloadAndSaveWineries(int pageNumber, Location location, List<State> filterStates, Set<Integer> visibleIds) throws Exception{
        // Get the page from the webservice
        WineryPage page;
        if(filterStates.isEmpty()){
            page = webService.getWineriesNearby(pageNumber, location.getLatitude(), location.getLongitude(), getVisibleIdsParam(visibleIds));
        }
        else {
            page = webService.getWineriesNearby(pageNumber, location.getLatitude(), location.getLongitude(), getStatesParam(filterStates), getVisibleIdsParam(visibleIds));
        }
        saveWineryPage(page);
        deleteIDList(wineryDao, "_id", page.getMeta().getDeletedIds());
        return page;
    }


    private String getVisibleIdsParam(Set<Integer> visibleIds){
        if(visibleIds.isEmpty()){
            return "";
        }
        else {
            StringBuilder param = new StringBuilder();
            for(Integer id : visibleIds){
                param.append(id);
                param.append(',');
            }

            return param.toString();
        }
    }

    private String getStatesParam(List<State> filterStates){
        if(filterStates.isEmpty()){
            return "";
        }
        else {
            StringBuilder param = new StringBuilder();
            for(State state : filterStates){
                param.append(state.getKey());
                param.append(',');
            }

            return param.toString();
        }
    }

    public WineryPage downloadAndSaveWineries(LatLngBounds bounds, String fromName) throws Exception{
        // Get the page from the webservice
        WineryPage page = webService.getWineries(bounds.southwest.latitude, bounds.northeast.longitude,
                bounds.northeast.latitude, bounds.southwest.longitude, fromName);

        Log.i(LogTags.SERVICE, "Got " + page.getWineries().size() + " for " + bounds);
        saveWineryPage(page);

        return page;
    }

    public WineryPage downloadAndSaveRegionWineries(int pageNumber, int regionID) throws Exception{
        // Get the page from the webservice
        WineryPage page = webService.getRegionWineries(pageNumber, regionID);

        Log.i(LogTags.SERVICE, "Got " + page.getWineries().size() + " for region " + regionID + " page " + pageNumber);
        saveWineryPage(page);

        return page;
    }

    private void saveWineryPage(final WineryPage page){
        // Init and save
        try{
            TransactionManager.callInTransaction(wineDao.getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    for (Winery winery : page.getWineries()){
                        winery.initFromWebservice();
                        createOrUpdateWinery(winery);
                    }
                    return null;
                }
            });
        }
        catch (SQLException e){
            Log.e(LogTags.SERVICE, "Error saving winery page " + e.getMessage(), e);
        }
    }

    public EventPage downloadAndSaveWineryEvents(int wineryID) throws Exception{
        EventPage page = webService.getWineryEvents(wineryID);

        Log.i(LogTags.SERVICE, "Got " + page.getEvents().size() + " events for winery " + wineryID);
        saveEventPage(page);

        return page;
    }

    public EventPage downloadAndSaveEvents(int year, int month) throws Exception{
        EventPage page = webService.getEvents(year, month + 1);

        Log.i(LogTags.SERVICE, "Got " + page.getEvents().size() + " events for month " + (month + 1) + " year " + year);
        saveEventPage(page);

        return page;
    }

    public EventPage downloadAndSaveRegionEvents(int regionID) throws Exception{
        EventPage page = webService.getRegionEvents(regionID);

        Log.i(LogTags.SERVICE, "Got " + page.getEvents().size() + " events for region " + regionID);
        saveEventPage(page);

        return page;
    }

    public EventPage downloadAndSaveFeaturedEventEvents(int eventID) throws Exception{
        EventPage page = webService.getFeaturedEventEvents(eventID);

        Log.i(LogTags.SERVICE, "Got " + page.getEvents().size() + " events for region " + eventID);
        saveEventPage(page);

        return page;
    }

    private void saveEventPage(final EventPage page){
        // Init and save
        try{
            TransactionManager.callInTransaction(wineDao.getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    for (Event event : page.getEvents()){
                        event.initFromWebservice();
                        createOrUpdateEvent(event);
                    }
                    return null;
                }
            });
        }
        catch (SQLException e){
            Log.e(LogTags.SERVICE, "Error saving event page " + e.getMessage(), e);
        }
    }

    /**
     * Gets all of the events for a given month and year.  Note that month is offset from 0 not
     * 1, just as in the java Date classes.  So month 0 is January.
     *
     * @param year The year to get events for
     * @param month Note that month is offset from 0 not
     * 1, just as in the java Date classes.  So month 0 is January.
     * @return A list of events
     */
    public List<EventLightweight> getEventsLightweight(int year, int month, boolean trade){
        try {
            QueryBuilder<EventLightweight, Integer> query = eventLightweightDao.queryBuilder();

            // Figure out our start date
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);

            Where<EventLightweight, Integer> where = query.where();
            where.gt("finishDate", cal.getTime());


            // Figure out our end date
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);

            where.and().lt("startDate", cal.getTime());
            where.and().isNull("parentEventId");
            where.and().eq("tradeEvent", trade);

            return query.query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting events for month " + month + " and year " + year, e);
        }
    }

    public Event getEvent(int eventID){
        return eventDao.queryForId(eventID);
    }

    public AndroidDatabaseResults getEventsLightweight(boolean trade){
        try {
            QueryBuilder<EventLightweight, Integer> query = eventLightweightDao.queryBuilder();
            query.where().eq("tradeEvent", trade).and().isNull("parentEventId");

            query.orderBy("name", true);

            return (AndroidDatabaseResults)query.iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all events", e);
        }
    }

    public AndroidDatabaseResults getEventsLightweight(Location nearby, boolean trade){
        try {
            QueryBuilder<EventLightweight, Integer> query = eventLightweightDao.queryBuilder();

            double latitude = nearby.getLatitude();
            double longitude = nearby.getLongitude();
            double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

            query.selectRaw(
                    "*",
                    String.format("((%f - latitude) * (%f - latitude) + (%f - longitude) * (%f - longitude) * %f) as theDistance", latitude, latitude, longitude, longitude, fudge)
            );
            query.where().eq("tradeEvent", trade).and().isNull("parentEventId");
            query.orderByRaw("theDistance ASC");
            return (AndroidDatabaseResults)query.queryRaw().closeableIterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    public List<Event> getWineryEvents(int wineryID){
        try {
            QueryBuilder<Event, Integer> query = eventDao.queryBuilder();
            query.distinct();
            query.where().eq("tradeEvent", false);

            QueryBuilder<EventWineryID, Integer> eventWineryQuery = eventWineryDao.queryBuilder();
            eventWineryQuery.where().eq("wineryID", wineryID);


            query.join(eventWineryQuery);

            return query.query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting winery events for winery " + wineryID, e);
        }
    }

    public List<Event> getRegionEvents(int regionID){
        try {
            QueryBuilder<Event, Integer> query = eventDao.queryBuilder();
            query.distinct();
            query.where().eq("tradeEvent", false);

            QueryBuilder<EventRegionID, Integer> eventRegionQuery = eventRegionDao.queryBuilder();
            eventRegionQuery.where().eq("regionID", regionID);

            query.join(eventRegionQuery);

            return query.query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting winery events for region " + regionID, e);
        }
    }

    public List<Event> getFeaturedEventEvents(int eventId){
        try {
            QueryBuilder<Event, Integer> query = eventDao.queryBuilder();
            query.where().eq("parentEventId", eventId);

            return query.query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting events for featured event " + eventId, e);
        }
    }


    /**
     * Gets all known regions from the database.
     *
     * @return A ClosableIteration with access to all wineries
     */
    public AndroidDatabaseResults getAllRegions(){
        return getRegions(null, new ArrayList<State>());
    }

    public AndroidDatabaseResults getRegions(String name){
        return getRegions(name, null);
    }

    /**
     * Gets a list of regions sorted alphabetically
     *
     * @param searchName
     * @param filterStates
     * @return
     */
    public AndroidDatabaseResults getRegions(String searchName, List<State> filterStates) {
        try {
            QueryBuilder<Region, Integer> query = regionDao.queryBuilder();

            Where<Region, Integer> where = null;
            if(searchName != null && !searchName.isEmpty()){
                where = query.where();
                where.like("name", searchName + "%");
            }

            Log.i(LogTags.UI, "filter states is " + filterStates);

            if(filterStates != null && !filterStates.isEmpty()){
                Log.i(LogTags.UI, "There are " + filterStates.size() + " filter states");

                if(where == null){
                    where = query.where();
                }
                List<Integer> stateIds = getStateIDs(filterStates);


                where.in("stateID", stateIds);
            }
            query.orderBy("name", true);

            return (AndroidDatabaseResults)query.iterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }

    /**
     * Gets a list of regions ordered by their distance to a point.
     *
     * @param filterStates
     * @param nearby The point the regions should be near
     * @return
     */
    public AndroidDatabaseResults getRegions(List<State> filterStates, Location nearby) {
        try {
            QueryBuilder<Region, Integer> query = regionDao.queryBuilder();

            double latitude = nearby.getLatitude();
            double longitude = nearby.getLongitude();
            double fudge = Math.pow(Math.cos(Math.toRadians(latitude)),2);

            query.selectRaw(
                    "*",
                    String.format("((%f - latitude) * (%f - latitude) + (%f - longitude) * (%f - longitude) * %f) as theDistance", latitude, latitude, longitude, longitude, fudge)
            );

            Where<Region, Integer> where = null;
            Log.i(LogTags.UI, "filter states is " + filterStates);

            if(filterStates != null && !filterStates.isEmpty()){
                Log.i(LogTags.UI, "There are " + filterStates.size() + " filter states");

                if(where == null){
                    where = query.where();
                }
                List<Integer> stateIds = getStateIDs(filterStates);


                where.in("stateID", stateIds);
            }

            query.orderByRaw("theDistance ASC");
            return (AndroidDatabaseResults)query.queryRaw().closeableIterator().getRawResults();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting all wineries", e);
        }
    }


    private List<Integer> getStateIDs(List<State> filterStates) {
        // Build a list of the states ids
        List<Integer> stateIds = new ArrayList<Integer>();
        for(State toAdd : filterStates){
            stateIds.add(toAdd.getKey());
        }
        return stateIds;
    }

    public RegionPage downloadAndSaveRegions(int pageNumber, String name, List<State> filterStates, Set<Integer> visibleIds) throws Exception{
        // Get the page from the webservice

        RegionPage page;
        if(filterStates.isEmpty()){
            page = webService.getRegions(name, pageNumber);
        }
        else{
            page = webService.getRegions(name, pageNumber, getStatesParam(filterStates));
        }
        saveRegionPage(page);
        deleteIDList(regionDao, "_id", page.getMeta().getDeletedIds());

        return page;
    }

    public RegionPage downloadAndSaveRegions(int pageNumber, Location location, List<State> filterStates, Set<Integer> visibleIds) throws Exception{
        // Get the page from the webservice
        RegionPage page;
        if(filterStates.isEmpty()){
            page = webService.getRegionsNearby(pageNumber, location.getLatitude(), location.getLongitude());
        }
        else {
            page = webService.getRegionsNearby(pageNumber, location.getLatitude(), location.getLongitude(), getStatesParam(filterStates));
        }
        saveRegionPage(page);
        deleteIDList(regionDao, "_id", page.getMeta().getDeletedIds());

        return page;
    }

    public EventPage downloadAndSaveEvents(int pageNumber, Set<Integer> visibleIds) throws Exception{
        String visibleIdsString = getVisibleIdsParam(visibleIds);

        // Get the page from the webservice
        EventPage page = webService.getEvents(pageNumber, visibleIdsString);

        saveEventPage(page);
        deleteIDList(eventDao, "_id", page.getMeta().getDeletedIds());

        return page;
    }

    public EventPage downloadAndSaveTradeEvents(int pageNumber, Set<Integer> visibleIds) throws Exception{
        String visibleIdsString = getVisibleIdsParam(visibleIds);

        // Get the page from the webservice
        // What is this??? If you look at the actual webservice params we are sending in here, we
        // are not filtering to trade events at all!  It turns out there is no way to filter to
        // trade events on the server.  However if we call the normal endpoint to get events
        // most of them are not trade events.  This means nothing displays in the trade event list
        // ui.  If nothing is shown in the ui, then there is no event to kick off downloading page
        // 2, and so on, so nothing will ever show in the list.
        // To work around that we get the events with a per_page of 200.  That shuld be enough
        // to pull down any trade events.
        EventPage page = webService.getTradeEvents(pageNumber, visibleIdsString);

        saveEventPage(page);
        deleteIDList(eventDao, "_id", page.getMeta().getDeletedIds());

        return page;
    }

    public EventPage downloadAndSaveEvents(int pageNumber, Location location, Set<Integer> visibleIds) throws Exception{
        String visibleIdsString = getVisibleIdsParam(visibleIds);

        // Get the page from the webservice
        EventPage page = webService.getEventsNearby(pageNumber, location.getLatitude(), location.getLongitude(), visibleIdsString);

        saveEventPage(page);
        deleteIDList(eventDao, "_id", page.getMeta().getDeletedIds());

        return page;
    }


    private void saveRegionPage(final RegionPage page){
        // Init and save
        try{
            TransactionManager.callInTransaction(wineDao.getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    for (Region region : page.getRegions()){
                        region.initFromWebservice();
                        createOrUpdateRegion(region);
                    }
                    return null;
                }
            });
        }
        catch (SQLException e){
            Log.e(LogTags.SERVICE, "Error saving region page", e);
        }
    }

    /**
     * Gets all known regions from the database.
     *
     * @return A ClosableIteration with access to all wineries
     */
    public List<Wine> getWineryWines(int wineryId){
        try {
            return wineDao.queryBuilder().where().eq("wineryId", wineryId).query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting wines for winery " + wineryId, e);
        }
    }

    public List<CellarDoorOpenTime> getWineryCellarDoorOpenTimes(int wineryId){
        try {
            return cellarDoorOpenTimesDao.queryBuilder().where().eq("wineryId", wineryId).query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting wines for winery " + wineryId, e);
        }
    }

    public List<Wine> getRangeWines(int rangeId){
        try {
            return wineDao.queryBuilder().where().eq("wineRangeId", rangeId).query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting wines for range " + rangeId, e);
        }
    }

    /**
     * Starts downloading a specific page of wineries from the webservice.  The result
     * will be cached in the database
     *
     * @param wineryId
     */
    public WinePage downloadAndSaveWineryWines(int wineryId) throws Exception{

        Log.i(LogTags.SERVICE, "Getting wines for " + wineryId);
        // Get the page from the webservice
        final WinePage page = webService.getWineryWines(wineryId);

        Log.i(LogTags.SERVICE, "Got " + page.getWines().size() + " wines");


        // Init and save
        try{
            TransactionManager.callInTransaction(wineDao.getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    for (Wine wine : page.getWines()){
                        wine.initFromWebservice();
                        createOrUpdateWine(wine);
                    }
                    return null;
                }
            });
        }
        catch (SQLException e){
            Log.e(LogTags.SERVICE, "Error saving wine page", e);
        }

        return page;
    }

    public List<CellarDoorOpenTime> downloadAndSaveWineryCellarDoorOpenTimes(final int wineryId) throws Exception{

        Log.i(LogTags.SERVICE, "Getting cellar door open times for " + wineryId);
        final CellarDoorOpenTimes times= webService.getWineryCellarDoorOpenTimes(wineryId);

        Log.i(LogTags.SERVICE, "Got " + times.getCellarDoorOpenTimes().size() + " open times");

        // Init and save
        try{
            TransactionManager.callInTransaction(wineDao.getConnectionSource(), new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                   createOrUpdateList(times.getCellarDoorOpenTimes(), cellarDoorOpenTimesDao, "wineryId", wineryId, "id");
                    return null;
                }
            });
        }
        catch (SQLException e){
            Log.e(LogTags.SERVICE, "Error saving wine page", e);
        }

        return times.getCellarDoorOpenTimes();
    }

    public List<WineryAmenity> getWineryAmenities(int wineryId){
        try {
            return amenityDao.queryBuilder().where().eq("winery_id", wineryId).query();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error getting amnities for winery " + wineryId, e);
        }
    }

    public Region mapRegionResults(AndroidDatabaseResults r){
        return regionDao.mapSelectStarRow(r);
    }

    public WineryLightweight mapWineryLightweightResults(AndroidDatabaseResults r){
        return wineryLightweightDao.mapSelectStarRow(r);
    }


    public RegionLightweight mapRegionLightweightResults(AndroidDatabaseResults r){
        return regionLightweightDao.mapSelectStarRow(r);
    }

    public EventLightweight mapEventLightweightResults(AndroidDatabaseResults r){
        return eventLightweightDao.mapSelectStarRow(r);
    }


    public Wine mapWineResults(AndroidDatabaseResults r){
        return wineDao.mapSelectStarRow(r);
    }

    private static final String PREF_FAVOURITE_EMAIL = "PREF_FAVOURITE_EMAIL";

    public boolean hasFavouritesEmail(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).contains(PREF_FAVOURITE_EMAIL);
    }

    public void setFavouritesEmail(Context context, String email){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_FAVOURITE_EMAIL, email).commit();
    }

    public String getFavouritesEmail(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FAVOURITE_EMAIL, "");
    }

    public static enum SearchOrder implements Serializable{
        Distance, Alphabetical
    }
}
