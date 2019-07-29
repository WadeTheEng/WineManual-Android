package au.net.winehound.domain.webservice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import au.net.winehound.domain.webservice.Image;

public class Logo {

    private int id;

    private Image logo;

    public Logo() {
    }

    public int getId() {
        return id;
    }

    public Logo(Image logo) {
        this.logo = logo;
    }

    public Image getLogo() {
        return logo;
    }
}
