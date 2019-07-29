package au.net.winehound;

import android.test.AndroidTestCase;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.File;
import java.lang.reflect.Field;

import au.net.winehound.service.WineHoundDatabaseHelper;
import au.net.winehound.service.WineHoundTestService;
import au.net.winehound.service.WineHoundTestService_;

/**
 * Base class for wine hound unit tests.  Clears the DB before
 * each test and creates our WineHoundService
 */
public abstract class WineHoundTestCase extends AndroidTestCase {

    protected WineHoundTestService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // If the DB file exists, delete it
        File database=getContext().getDatabasePath(WineHoundDatabaseHelper.DB_NAME);

        if (database.exists()) {
            database.delete();
        }

        service = WineHoundTestService_.getInstance_(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        OpenHelperManager.releaseHelper();
    }
}
