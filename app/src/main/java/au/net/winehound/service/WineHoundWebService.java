package au.net.winehound.service;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.List;

import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.webservice.CellarDoorOpenTimes;
import au.net.winehound.domain.webservice.EventPage;
import au.net.winehound.domain.webservice.MailingListSubscription;
import au.net.winehound.domain.webservice.RegionPage;
import au.net.winehound.domain.webservice.WinePage;
import au.net.winehound.domain.webservice.WineryPage;

@Rest( converters = { GsonHttpMessageConverter.class })
public interface WineHoundWebService {

    void setRootUrl(String rootUrl);

    @Get("/api/wineries?name={name}&page={page}&states_string={stateFilter}&visible_ids_string={visibleIds}")
    WineryPage getWineries(String name, int page, String stateFilter, String visibleIds);

    @Get("/api/wineries?name={name}&page={page}&visible_ids_string={visibleIds}")
    WineryPage getWineries(String name, int page, String visibleIds);

    @Get("/api/wineries?page={page}&near[latitude]={latitude}&near[longitude]={longitude}&near[distance]=5000&states_string={stateFilter}&visible_ids_string={visibleIds}")
    WineryPage getWineriesNearby(int page, double latitude, double longitude, String stateFilter, String visibleIds);

    @Get("/api/wineries?page={page}&near[latitude]={latitude}&near[longitude]={longitude}&near[distance]=5000&visible_ids_string={visibleIds}")
    WineryPage getWineriesNearby(int page, double latitude, double longitude, String visibleIds);

    @Get("/api/wineries/{wineryID}/wines?per_page=100")
    WinePage getWineryWines(int wineryID);

    @Get("/api/wineries?bottom_right_lat={bottomRightLat}&bottom_right_lon={bottomRightLon}&top_left_lat={topLeftLat}&top_left_lon={topLeftLon}&from[name]={fromName}")
    WineryPage getWineries(double bottomRightLat, double bottomRightLon, double topLeftLat, double topLeftLon, String fromName);

    @Get("/api/wineries/{wineryID}/cellar_door_open_times")
    CellarDoorOpenTimes getWineryCellarDoorOpenTimes(int wineryID);

    @Get("/api/regions?name={name}&page={page}&states_string={stateFilter}")
    RegionPage getRegions(String name, int page, String stateFilter);

    @Get("/api/regions?name={name}&page={page}")
    RegionPage getRegions(String name, int page);

    @Get("/api/regions?page={page}&near[latitude]={latitude}&near[longitude]={longitude}&near[distance]=5000&states_string={stateFilter}")
    RegionPage getRegionsNearby(int page, double latitude, double longitude, String stateFilter);

    @Get("/api/regions?page={page}&near[latitude]={latitude}&near[longitude]={longitude}&near[distance]=5000")
    RegionPage getRegionsNearby(int page, double latitude, double longitude);

    @Get("/api/events?winery={wineryID}&per_page=100")
    EventPage getWineryEvents(int wineryID);

    @Get("/api/events?region={regionID}&per_page=100")
    EventPage getRegionEvents(int regionID);

    @Get("/api/wineries?region={regionID}&page={page}")
    WineryPage getRegionWineries(int page, int regionID);

    @Post("/api/mailing_lists")
    void postFavouriteWinery(MailingListSubscription details);

    @Get("/api/events?month={month}&year={year}&per_page=200")
    EventPage getEvents(int year, int month);

    @Get("/api/events?page={page}&visible_ids_string={visibleIds}")
    EventPage getEvents(int page, String visibleIds);

    @Get("/api/events?page={page}&visible_ids_string={visibleIds}&per_page=200")
    EventPage getTradeEvents(int page, String visibleIds);

    @Get("/api/events?page={page}&near[latitude]={latitude}&near[longitude]={longitude}&near[distance]=5000&visible_ids_string={visibleIds}")
    EventPage getEventsNearby(int page, double latitude, double longitude, String visibleIds);

    @Get("/api/events/{eventID}/events?per_page=100")
    EventPage getFeaturedEventEvents(int eventID);

}
