package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class FavouriteWineryID {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Winery winery;

    public FavouriteWineryID() {
    }

    public FavouriteWineryID(Winery winery) {
        this.winery = winery;
    }

    public Winery getWinery() {
        return winery;
    }
}
