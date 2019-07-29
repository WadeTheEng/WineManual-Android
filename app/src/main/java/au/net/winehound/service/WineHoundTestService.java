package au.net.winehound.service;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;

import java.sql.SQLException;

import au.net.winehound.domain.Event;
import au.net.winehound.domain.Region;
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.Winery;

/**
 * A subclass of WineHoundService which makes various methods used only for testing publicly visible.
 * The service also allows the tests to get at the various DAO's used by its superclass.
 *
 * DO NOT USE THIS CLASS FROM ANYWHERE IN THE APP - ONLY THE TESTS.
 */
@EBean()
public class WineHoundTestService extends WineHoundService{

    @Override
    public void createOrUpdateWinery(Winery toSave) throws SQLException {
        super.createOrUpdateWinery(toSave);
    }

    @Override
    public void createOrUpdateRegion(Region toSave) throws SQLException {
        super.createOrUpdateRegion(toSave);
    }

    @Override
    public void createOrUpdateWine(Wine toSave) throws SQLException {
        super.createOrUpdateWine(toSave);
    }

    @Override
    public void createOrUpdateEvent(Event toSave) throws SQLException {
        super.createOrUpdateEvent(toSave);
    }

    @AfterInject
    protected void setupRootUrl(){
        webService.setRootUrl("http://winehound-staging.herokuapp.com/");
    }

}
