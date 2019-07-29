package au.net.winehound;

import au.net.winehound.domain.Region;
import au.net.winehound.domain.State;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.domain.WineryStateID;
import au.net.winehound.domain.webservice.Image;
import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.WineRange;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.webservice.Logo;
import au.net.winehound.domain.WineryAmenity;
import au.net.winehound.domain.webservice.WineryPage;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class WineriesTest extends WineHoundTestCase {


    /**
     * Tests creating and saving a winery with photos, amenities, a logo, etc
     */
    public void testSaveWinery() throws SQLException {
        // Create a new winery
        Winery newWinery = new Winery(1, "Test winery");

        // Add some photos
        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        newWinery.getPhotographs().add(photo1);

        // Add a logo
        newWinery.setLogo(new Logo(new Image("http://logo2")));

        // Put in a few amenities
        newWinery.getAmenities().add(new WineryAmenity("test amenity"));
        newWinery.getAmenities().add(new WineryAmenity("test amenity 2"));

        // Some wine ranges
        newWinery.getWineRanges().add(new WineRange(1, "test range", "test description"));

        // Init before saving!
        newWinery.initFromWebservice();

        // Save
        service.createOrUpdateWinery(newWinery);

        // Load it back up
        AndroidDatabaseResults it = service.getWineries("Test");

        // Check the winery exists
        assertEquals(1, it.getCount());
        WineryLightweight wineryLightweight = service.mapWineryLightweightResults(it);
        newWinery = service.getWinery(wineryLightweight.getId());

        // Check those photos came out good!
        assertNotNull(newWinery.getPhotographs());
        assertEquals(1, newWinery.getPhotographs().size());
        Photograph photoImage = newWinery.getPhotographs().iterator().next();
        assertEquals("http://test1", wineryLightweight.getHeaderPhotoThumbUrl());
        assertEquals("http://test1", photoImage.getFullSizeUrl());


        assertNotNull(newWinery.getCellarDoorPhotographs());
        assertEquals(0, newWinery.getCellarDoorPhotographs().size());

        // Check the logo
        assertEquals("http://logo2", newWinery.getLogoUrl());

        // Check the amenities
        assertEquals(2, newWinery.getAmenities().size());
        WineryAmenity amenity = newWinery.getAmenities().iterator().next();
        assertEquals("test amenity", amenity.getName());

        // Check the ranges
        assertEquals(1, newWinery.getWineRanges().size());
        WineRange range = newWinery.getWineRanges().iterator().next();
        assertEquals("test range", range.getName());
    }

    /**
     * Tests updating a saved winery, by adding, removing and editing its photos, logo, etc
     */
    public void testUpdateWinery() throws SQLException {
        // Create a new winery
        Winery newWinery = new Winery(1, "Test winery");
        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        newWinery.getPhotographs().add(photo1);
        Photograph photo2 = new Photograph(2);
        photo2.setImage(new Image("http://test2"));
        newWinery.getPhotographs().add(photo2);
        newWinery.setLogo(new Logo(new Image("http://logo2")));
        newWinery.getAmenities().add(new WineryAmenity("test amenity"));
        newWinery.getAmenities().add(new WineryAmenity("test amenity 2"));
        newWinery.getWineRanges().add(new WineRange(1, "test range", "test description"));
        newWinery.initFromWebservice();

        // Save
        service.createOrUpdateWinery(newWinery);

        // Create the same winery with updated photos, as if it just came in through the webservce
        newWinery = new Winery(1, "Test winery changed");
        photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1/changed"));
        newWinery.getPhotographs().add(photo1);

        Photograph photo3 = new Photograph(3);
        photo3.setImage(new Image("http://test3"));
        newWinery.getPhotographs().add(photo3);

        newWinery.setLogo(new Logo(new Image("http://logo2/changed")));
        newWinery.getAmenities().add(new WineryAmenity("test amenity 2"));
        newWinery.getAmenities().add(new WineryAmenity("test amenity 3"));
        newWinery.getWineRanges().add(new WineRange(1, "test range changed", "test description"));
        newWinery.getWineRanges().add(new WineRange(2, "test range 2", "test description"));
        newWinery.initFromWebservice();

        // Save
        service.createOrUpdateWinery(newWinery);

        // Pull the winery out and check that our updates went through
        AndroidDatabaseResults it = service.getWineries("Test");

        // Check the winery exists and we don't have anything extra
        assertEquals(1, it.getCount());

        WineryLightweight wineryLightweight = service.mapWineryLightweightResults(it);
        newWinery = getWinery(it);
        assertEquals("Test winery changed", newWinery.getName());
        assertEquals("Test winery changed", wineryLightweight.getName());


        // Check those photos came out good!
        assertNotNull(newWinery.getPhotographs());
        assertEquals(2, newWinery.getPhotographs().size());
        Iterator<Photograph> photoIt = newWinery.getPhotographs().iterator();
        Photograph photoImage = photoIt.next();
        assertEquals("http://test1/changed",wineryLightweight.getHeaderPhotoThumbUrl());
        assertEquals("http://test1/changed", photoImage.getFullSizeUrl());

        photoImage = photoIt.next();
        assertEquals("http://test3", photoImage.getFullSizeUrl());

        assertNotNull(newWinery.getCellarDoorPhotographs());
        assertEquals(0, newWinery.getCellarDoorPhotographs().size());

        // Check the logo
        assertEquals("http://logo2/changed", newWinery.getLogoUrl());

        // Check the amenities
        assertEquals(2, newWinery.getAmenities().size());
        Iterator<WineryAmenity> amenityIt = newWinery.getAmenities().iterator();
        assertEquals("test amenity 2", amenityIt.next().getName());
        assertEquals("test amenity 3", amenityIt.next().getName());

        // Check the ranges
        assertEquals(2, newWinery.getWineRanges().size());
        Iterator<WineRange> rangeIt = newWinery.getWineRanges().iterator();
        assertEquals("test range changed", rangeIt.next().getName());
        assertEquals("test range 2", rangeIt.next().getName());
    }

    private Winery getWinery(AndroidDatabaseResults results){
        WineryLightweight wineryLightweight = service.mapWineryLightweightResults(results);
        return service.getWinery(wineryLightweight.getId());
    }

    /**
     * Test just getting the full list of wineries direct from the DB
     */
    public void testGetAllWineries() throws SQLException {
        // Check search gives us an empty list
        AndroidDatabaseResults it = service.getAllWineries();
        assertEquals(0, it.getCount());

        // Manually insert a winery or two ...
        service.createOrUpdateWinery(new Winery(1, "Test one"));
        service.createOrUpdateWinery(new Winery(2, "Test two"));

        // Check search to see our wineries came out
        it = service.getAllWineries();
        assertEquals(2, it.getCount());

        WineryLightweight wineryLightweight = service.mapWineryLightweightResults(it);
        assertEquals(1, wineryLightweight.getId());
        assertEquals("Test one", wineryLightweight.getName());


        it.moveRelative(1);
        wineryLightweight = service.mapWineryLightweightResults(it);
        assertEquals(2, wineryLightweight.getId());
        assertEquals("Test two", wineryLightweight.getName());
    }


    /**
     * Tests downloading a new page of wineries we don't have inside our database
     */
    public void testDownloadPageOfWineries() throws Exception{

        // Kick off a download of a page
        WineryPage resultsPage = service.downloadAndSaveWineries(1);

        // Assert that we have many pages to go
        assertNotNull(resultsPage);
        assertNotNull(resultsPage.getMeta());
        assertNotNull(resultsPage.getMeta().getPagination());
        assertTrue(resultsPage.getMeta().getPagination().getTotalPages() > 1);

        // Assert that it contains some wineries
        assertNotNull(resultsPage.getWineries());
        assertEquals(25, resultsPage.getWineries().size());


        // Search the DB and check that they are in there as well
        AndroidDatabaseResults it = service.getAllWineries();
        assertEquals(25, it.getCount());

        WineryLightweight winery = service.mapWineryLightweightResults(it);
        assertEquals(4565, winery.getId());
        assertEquals("181 Wines", winery.getName());

        // Get page 2
        service.downloadAndSaveWineries(2);
        assertEquals(50,  service.getAllWineries().getCount());
    }

    /**
     * Test downloading a page of wineries we already have in our database
     */
    public void testUpdatePageOfWineries() throws Exception{
        // Download paper cloud twice
        service.downloadAndSaveWineries(0, "paper", new ArrayList<State>(), new HashSet<Integer>());
        service.downloadAndSaveWineries(0, "paper", new ArrayList<State>(), new HashSet<Integer>());

        // Assert it looks like we expect
        AndroidDatabaseResults it = service.getAllWineries();
        assertEquals(1, it.getCount());

        assertPapercloundWinery(getWinery(it));
    }

    /**
     * Tests searching our local DB for wineries that match a name
     */
    public void testSearchWineryLocally() throws SQLException {
        // Manually insert some wineries
        service.createOrUpdateWinery(new Winery(1, "Test one"));
        service.createOrUpdateWinery(new Winery(2, "Test two"));

        // Search and check they come out
        AndroidDatabaseResults it = service.getWineries("Test o");
        assertEquals(1, it.getCount());

        WineryLightweight winery = service.mapWineryLightweightResults(it);
        assertEquals(1, winery.getId());
        assertEquals("Test one", winery.getName());

        // Make sure an empty search also works
        it = service.getWineries("Empty");
        assertEquals(0, it.getCount());
    }

    /**
     * Test searching over the network for wineries, updating the local DB and then
     * refreshing our list wth new wineries
     */
    public void testSearchWineryNetwork() throws Exception{
        // Kick off a search over the network
        WineryPage resultsPage = service.downloadAndSaveWineries(0, "paper", new ArrayList<State>(), new HashSet<Integer>());

        // Check the search results contain the winery we are after
        assertNotNull(resultsPage);
        assertNotNull(resultsPage.getWineries());
        assertEquals(1, resultsPage.getWineries().size());
        assertPapercloundWinery(resultsPage.getWineries().iterator().next());

        // Run a local search and check the results come out
        AndroidDatabaseResults it = service.getWineries("paper", new ArrayList<State>());
        assertEquals(1, it.getCount());

        assertPapercloundWinery(getWinery(it));
    }


    public void testDownloadWineryPhotograpsLogoAndAmenities() throws Exception{
        // Search for the papercloud winery
        WineryPage resultsPage = service.downloadAndSaveWineries(0, "paper", new ArrayList<State>(), new HashSet<Integer>());

        assertEquals(1, resultsPage.getWineries().size());
        assertPapercloundWinery(resultsPage.getWineries().get(0));

        // Load it from the DB
        AndroidDatabaseResults it = service.getWineries("paper");
        assertEquals(1, it.getCount());

        assertPapercloundWinery(getWinery(it));

    }

    public void testSearchWinriesByStates() throws Exception{
        // Insert some test wineries
        Winery winery = new Winery(1, "Melbourne Winery");
        winery.getStateIds().add(new WineryStateID(1, winery));
        service.createOrUpdateWinery(winery);

        winery = new Winery(2, "Sydney Winery");
        winery.getStateIds().add(new WineryStateID(3, winery));
        service.createOrUpdateWinery(winery);


        winery = new Winery(3, "Albury–Wodonga winery");
        winery.getStateIds().add(new WineryStateID(1, winery));
        winery.getStateIds().add(new WineryStateID(3, winery));
        service.createOrUpdateWinery(winery);


        // Search for vic
        List<State> stateList = new ArrayList<State>();
        stateList.add(State.Victoria);
        AndroidDatabaseResults wineriesIt = service.getWineries(null, stateList);


        assertEquals(2, wineriesIt.getCount());
        WineryLightweight lightweight = service.mapWineryLightweightResults(wineriesIt);
        assertEquals("Albury–Wodonga winery", lightweight.getName());

        wineriesIt.moveRelative(1);
        lightweight = service.mapWineryLightweightResults(wineriesIt);
        assertEquals("Melbourne Winery",lightweight.getName());

        // Add in NSW
        stateList.add(State.New_South_Wales);

        // Check we get both melbourne and sydney
        wineriesIt = service.getWineries(null, stateList);

        assertEquals(3, wineriesIt.getCount());
        lightweight = service.mapWineryLightweightResults(wineriesIt);
        assertEquals("Albury–Wodonga winery", lightweight.getName());

        wineriesIt.moveRelative(1);
        lightweight = service.mapWineryLightweightResults(wineriesIt);
        assertEquals("Melbourne Winery", lightweight.getName());

        wineriesIt.moveRelative(1);
        lightweight = service.mapWineryLightweightResults(wineriesIt);
        assertEquals("Sydney Winery", lightweight.getName());
    }

    public void testFavouriteWinery() throws Exception{
        // Get ourselves the paperclound winery
        service.downloadAndSaveWineries(0, "paper", new ArrayList<State>(), new HashSet<Integer>());

        // Put another winery in which is not going to be favourited
        Winery unloved = new Winery(10, "This will not be favourited");
        service.createOrUpdateWinery(unloved);

        // Favourite and save
        AndroidDatabaseResults it =service.getWineries("paper");

        Winery papercloud = getWinery(it);
        assertFalse(service.isWineryFavourite(papercloud));
        service.setWineryFavourite(papercloud, true);
        assertTrue(service.isWineryFavourite(papercloud));

        // Get favourites
        it = service.getFavouriteWineries();

        // Check its in
        assertEquals(1, it.getCount());

        papercloud = getWinery(it);

        // Check its ok!
        assertPapercloundWinery(papercloud);
    }

    public void testDeletedIDs() throws Exception{

        // Insert a winery that definitely will not be in the webservice
        Winery winery = new Winery(424242, "Delete me winery!");
        service.createOrUpdateWinery(winery);

        // Make a webservice request with that ID in the viewed IDs
        HashSet<Integer> viewedIds = new HashSet<Integer>(1);
        viewedIds.add(424242);

        // Check it comes back as a deleted ID
        WineryPage page = service.downloadAndSaveWineries(0,"",new ArrayList<State>(), viewedIds);

        assertEquals(1, page.getMeta().getDeletedIds().length);
        assertEquals(new Integer(424242), page.getMeta().getDeletedIds()[0]);

        // Check its gone from the DB
        AndroidDatabaseResults it = service.getWineries("Delete me");
        assertEquals(0, it.getCount());
    }

    private void assertPapercloundWinery(Winery winery) {
        assertEquals(6835, winery.getId());

        // Check that all the photographs are working
        assertNotNull(winery.getPhotographs());
        assertEquals(2, winery.getPhotographs().size());

        Iterator<Photograph> photoIt = winery.getPhotographs().iterator();
        Photograph photo = photoIt.next();
        assertPapercloudPhoto(photo);
        assertTrue(photoIt.hasNext());
        photo = photoIt.next();
        assertPapercloudPhoto(photo);

        // Have a quick look at the cellar door photos
        assertEquals(1, winery.getCellarDoorPhotographs().size());
        Photograph cellearPhoto = winery.getCellarDoorPhotographs().iterator().next();
        assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/cellar_door_photograph/image/11/pc_logo.png", cellearPhoto.getFullSizeUrl());

        // Check the logo
        assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/winery/logo/6835/pc_logo.png", winery.getLogoUrl());

        // Check the amenties
        assertEquals(4, winery.getAmenities().size());
        WineryAmenity amenity = winery.getAmenities().iterator().next();
        assertEquals("Coffee/Light Meals", amenity.getName());

        // Check the state and regions
        assertEquals(1, winery.getRegionIds().size());
        assertEquals(64, winery.getRegionIds().iterator().next().getRegionID());

        assertEquals(1, winery.getStateIds().size());
        assertEquals(1, winery.getStateIds().iterator().next().getStateID());

        // Check the range
        assertEquals(1, winery.getWineRanges().size());
        assertEquals("A Wine Range", winery.getWineRanges().iterator().next().getName());
    }

    private void assertPapercloudPhoto(Photograph photo){
        if(photo.getId() == 57){
            assertEquals( "https://winehound-staging.s3.amazonaws.com/uploads/photograph/image/57/3552999261_70f2cd966a.jpg", photo.getFullSizeUrl());
        }
        else if(photo.getId() == 58){
            assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/photograph/image/58/shirts.jpg", photo.getFullSizeUrl());
        }
        else{
            fail("Photo has ID " + photo.getId() + " but we expect 57 or 58");
        }
    }
}
