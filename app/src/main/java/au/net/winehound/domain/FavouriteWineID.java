package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class FavouriteWineID {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true)
    private Wine wine;

    public FavouriteWineID() {
    }

    public FavouriteWineID(Wine wine) {
        this.wine = wine;
    }

    public Wine getWine() {
        return wine;
    }
}
