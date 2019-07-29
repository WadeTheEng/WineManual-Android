package au.net.winehound.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
import au.net.winehound.domain.Wine;
import au.net.winehound.domain.WineRange;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.domain.WineryAmenity;
import au.net.winehound.domain.WineryRegionID;
import au.net.winehound.domain.WineryStateID;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WineHoundDatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_NAME = "wine_database.db";

    public static final List<Class<?>> persistedClasses = new ArrayList<Class<?>>() {
        {
            add(Wine.class);
            add(Winery.class);
            add(WineryLightweight.class);
            add(Photograph.class);
            add(WineryAmenity.class);
            add(WineryRegionID.class);
            add(WineryStateID.class);
            add(WineRange.class);
            add(Region.class);
            add(RegionLightweight.class);
            add(FavouriteWineID.class);
            add(FavouriteWineryID.class);
            add(CellarDoorOpenTime.class);
            add(Event.class);
            add(EventLightweight.class);
            add(EventRegionID.class);
            add(EventStateID.class);
            add(EventWineryID.class);
            add(FavouriteEventID.class);
        }
    };

    public WineHoundDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {

        try {
            // Create the tables
            for (Class createClass : persistedClasses) {
                if(!createClass.equals(WineryLightweight.class) && !createClass.equals(RegionLightweight.class)
                        && !createClass.equals(EventLightweight.class)){
                    TableUtils.createTable(connectionSource, createClass);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
