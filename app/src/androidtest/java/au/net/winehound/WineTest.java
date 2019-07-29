package au.net.winehound;

import com.j256.ormlite.dao.CloseableIterator;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import au.net.winehound.domain.Photograph;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.webservice.Image;
import au.net.winehound.domain.webservice.RegionPage;
import au.net.winehound.domain.webservice.WinePage;
import au.net.winehound.service.WineHoundService;


public class WineTest extends WineHoundTestCase {

    public void testSaveWine() throws SQLException {
        // Construct a wine
        Wine wine = new Wine(1, "test wine", 10);

        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        wine.getPhotographs().add(photo1);

        // Save it
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        // Check it comes out ok
        List<Wine> wines = service.getWineryWines(10);
        assertEquals(1, wines.size());
        wine = wines.get(0);

        assertEquals(1, wine.getId());
        assertEquals("test wine", wine.getName());

        assertEquals(1, wine.getPhotographs().size());
        Photograph photo = wine.getPhotographs().iterator().next();
        assertEquals("http://test1", photo.getFullSizeUrl());

    }

    public void testUpdateWine() throws SQLException {
        // Construct and save a wine
        Wine wine = new Wine(1, "test wine", 10);
        Photograph photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1"));
        wine.getPhotographs().add(photo1);
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        // Construct an updated version
        wine = new Wine(1, "test wine updated", 11);
        photo1 = new Photograph(1);
        photo1.setImage(new Image("http://test1/changed"));
        wine.getPhotographs().add(photo1);

        Photograph photo2 = new Photograph(2);
        photo2.setImage(new Image("http://test2"));
        wine.getPhotographs().add(photo2);

        // Save it
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        // Check it all came out
        Iterator<Wine> it = service.getWineryWines(11).iterator();
        assertTrue(it.hasNext());

        wine = it.next();
        assertFalse(it.hasNext());

        assertEquals(1, wine.getId());
        assertEquals("test wine updated", wine.getName());

        assertEquals(2, wine.getPhotographs().size());
        Iterator<Photograph> photoIt = wine.getPhotographs().iterator();
        assertEquals("http://test2", photoIt.next().getFullSizeUrl());
        assertEquals("http://test1/changed", photoIt.next().getFullSizeUrl());
    }

    public void testGetWineryWines() throws SQLException {
        //Save some wines
        Wine wine = new Wine(1, "test wine", 10);
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        wine = new Wine(2, "test wine 2", 10);
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        // Check they both come out
        Iterator<Wine> it = service.getWineryWines(10).iterator();
        assertTrue(it.hasNext());
        assertEquals("test wine", it.next().getName());
        assertEquals("test wine 2", it.next().getName());
    }

    public void testGetRangeWines() throws SQLException {
        //Save some wines
        Wine wine = new Wine(1, "test wine", 10, 1);
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        wine = new Wine(2, "test wine 2", 10, 1);
        wine.initFromWebservice();
        service.createOrUpdateWine(wine);

        // Check they both come out
        Iterator<Wine> it = service.getRangeWines(1).iterator();
        assertTrue(it.hasNext());
        assertEquals("test wine", it.next().getName());
        assertEquals("test wine 2", it.next().getName());
    }

    public void testDownloadWineryWines() throws Exception{
        // Kick off a download of a page
        WinePage resultsPage = service.downloadAndSaveWineryWines(6835);
        Iterator<Wine> it = service.getWineryWines(6835).iterator();

        // Check we got everything we were after
        assertPCWineResults(resultsPage, it);
    }

    private void assertPCWineResults(WinePage resultsPage, Iterator<Wine> it){
        // Assert that we have many pages to go
        assertNotNull(resultsPage);
        assertNotNull(resultsPage.getMeta());
        assertNotNull(resultsPage.getMeta().getPagination());
        assertEquals(1, resultsPage.getMeta().getPagination().getTotalPages());

        // Assert that it contains some wineries
        assertNotNull(resultsPage.getWines());
        assertEquals(1, resultsPage.getWines().size());
        assertPCTestWine(resultsPage.getWines().get(0));

        // Search the DB and check that they are in there as well
        assertTrue(it.hasNext());
        Wine wine = it.next();
        assertPCTestWine(wine);
        assertFalse(it.hasNext());
    }

    private void assertPCTestWine(Wine wine) {
        assertEquals(48, wine.getId());
        assertEquals("Papercloud Test Wine", wine.getName());
        assertEquals(1, wine.getPhotographs().size());
        assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/photograph/image/60/image_2311854_full.png", wine.getPhotographs().iterator().next().getFullSizeUrl());
        assertEquals("https://winehound-staging.s3.amazonaws.com/uploads/wine/tasting_notes_pdf/48/screen_shot_2014-03-21_at_10.51.45_am.png", wine.getTastingNotesUrl());
    }

}
