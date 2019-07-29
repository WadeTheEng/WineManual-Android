package au.net.winehound;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.State;
import au.net.winehound.domain.webservice.Image;
import au.net.winehound.domain.webservice.RegionPage;

public class RegionsTest extends WineHoundTestCase {

    public void testSaveRegion() throws SQLException {
        // Construct a region
        Region region = new Region(1, "test region");
        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        region.getPhotographs().add(photo1);

        // Save it
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Check it comes out ok
        AndroidDatabaseResults it = service.getAllRegions();
        assertEquals(1, it.getCount());


        region = getRegion(it);

        assertEquals(1, region.getId());
        assertEquals("test region", region.getName());

        assertEquals(1, region.getPhotographs().size());
        Photograph photo = region.getPhotographs().iterator().next();
        assertEquals("http://test1", photo.getFullSizeUrl());

    }


    private Region getRegion(AndroidDatabaseResults results){
        return service.getRegion(service.mapRegionLightweightResults(results).getId());
    }

    public void testUpdateRegion() throws SQLException {
        // Construct and save a region
        Region region = new Region(1, "test region");
        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        region.getPhotographs().add(photo1);
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Construct an updated version
        region = new Region(1, "test region updated");
        photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1/changed"));
        region.getPhotographs().add(photo1);

        Photograph photo2 = new Photograph(2);
        photo2.setImage(new Image("http://test2"));
        region.getPhotographs().add(photo2);

        // Save it
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Check it all came out
        AndroidDatabaseResults it = service.getAllRegions();
        assertEquals(1, it.getCount());

        region = getRegion(it);

        assertEquals(1, region.getId());
        assertEquals("test region updated", region.getName());

        assertEquals(2, region.getPhotographs().size());
        Iterator<Photograph> photoIt = region.getPhotographs().iterator();
        assertEquals("http://test2", photoIt.next().getFullSizeUrl());
        assertEquals("http://test1/changed", photoIt.next().getFullSizeUrl());
    }

    public void testGetAllRegions() throws SQLException {
        // Save some regions
        Region region = new Region(1, "test region");
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        region = new Region(2, "test region 2");
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Check they both come out
        AndroidDatabaseResults it = service.getAllRegions();
        assertEquals(2, it.getCount());

        assertEquals("test region", getRegion(it).getName());

        it.moveRelative(1);
        assertEquals("test region 2", getRegion(it).getName());
    }

    public void testDownloadPageOfRegions() throws Exception{
        // Kick off a download of a page
        RegionPage resultsPage = service.downloadAndSaveRegions(1, "", new ArrayList<State>(), new HashSet<Integer>());

        // Assert that we have many pages to go
        assertNotNull(resultsPage);
        assertNotNull(resultsPage.getMeta());
        assertNotNull(resultsPage.getMeta().getPagination());
        assertTrue(resultsPage.getMeta().getPagination().getTotalPages() > 1);

        // Assert that it contains some regions
        assertNotNull(resultsPage.getRegions());
        assertEquals(25, resultsPage.getRegions().size());

        // Search the DB and check that they are in there as well
        AndroidDatabaseResults it = service.getAllRegions();
        assertEquals(25, it.getCount());
        it.moveRelative(4);
        Region region = getRegion(it);
        assertBarossaValley(region);

        // Get page 2
        service.downloadAndSaveRegions(2, "", new ArrayList<State>(), new HashSet<Integer>());

        // Check we have more regions
        it = service.getAllRegions();
        assertEquals(50,  it.getCount());
    }

    public void testUpdatePageOfRegions() throws Exception {
        // Download a page twice
        service.downloadAndSaveRegions(1, "", new ArrayList<State>(), new HashSet<Integer>());
        service.downloadAndSaveRegions(1, "", new ArrayList<State>(), new HashSet<Integer>());

        // Check its all good
        AndroidDatabaseResults it = service.getAllRegions();
        assertEquals(25, it.getCount());
        it.moveRelative(4);
        Region region = getRegion(it);
        assertBarossaValley(region);
    }

    public void testSearchRegionLocally() throws SQLException {
        // Construct and save a region
        Region region = new Region(1, "test region");
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Search for it
        AndroidDatabaseResults it = service.getRegions("test");
        assertEquals(1, it.getCount());
        assertEquals("test region", getRegion(it).getName());
    }

    public void testSearchRegionByStates() throws SQLException{
        // Insert some test regions
        Region region = new Region(1, "Melbourne");
        region.setStateID(1);
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        region = new Region(2, "Sydney");
        region.setStateID(3);
        region.initFromWebservice();
        service.createOrUpdateRegion(region);

        // Search for vic
        List<State> stateList = new ArrayList<State>();
        stateList.add(State.Victoria);
        AndroidDatabaseResults regionsIt = service.getRegions("", stateList);

        assertEquals(1, regionsIt.getCount());
        assertEquals("Melbourne", getRegion(regionsIt).getName());

        // Add in NSW
        stateList.add(State.New_South_Wales);

        // Check we get both melbourne and sydney
        regionsIt = service.getRegions("", stateList);

        assertEquals(2, regionsIt.getCount());
        assertEquals("Melbourne", getRegion(regionsIt).getName());

        regionsIt.moveRelative(1);
        assertEquals("Sydney", getRegion(regionsIt).getName());

    }

    public void testSearchRegionNetwork() throws Exception{
        // Search for region on network
        RegionPage page = service.downloadAndSaveRegions(1, "Barossa", new ArrayList<State>(), new HashSet<Integer>());
        assertEquals(1, page.getMeta().getPagination().getTotalObjects());
        assertEquals(1, page.getRegions().size());
        assertBarossaValley(page.getRegions().get(0));
    }

    private void assertBarossaValley(Region region) {
        assertEquals(1, region.getId());
        assertEquals("Barossa Valley", region.getName());
        assertEquals(1, region.getPhotographs().size());
        assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/photograph/image/28/Image.png",
                region.getPhotographs().iterator().next().getFullSizeUrl());
    }

}