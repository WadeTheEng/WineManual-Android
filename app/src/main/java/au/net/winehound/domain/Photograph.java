package au.net.winehound.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import au.net.winehound.domain.webservice.Image;

@DatabaseTable
public class Photograph implements Serializable{

    @DatabaseField(id = true)
    private int id;

    // This comes from the JSON in the webserice, but we just pull it in to the url
    // field in initFromWebservice
    private Image image;

    @DatabaseField(foreign = true, canBeNull = true)
    private Wine wine;

    @DatabaseField(foreign = true, canBeNull = true)
    private Winery wineryPhoto;

    @DatabaseField(foreign = true, canBeNull = true)
    private Winery wineryCellarDoor;

    @DatabaseField(foreign = true, canBeNull = true)
    private Region region;

    @DatabaseField(foreign = true, canBeNull = true)
    private Event event;

    @DatabaseField
    private String thumbUrl;

    @DatabaseField
    private String fullSizeUrl;

    public Photograph() {
    }

    /**
     * Used for unit tests
     * @param id
     */
    public Photograph(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getFullSizeUrl() {
        return fullSizeUrl;
    }

    public void initFromWebserviceWinery(Winery winery){
        wineryPhoto = winery;
        fullSizeUrl = image.getUrl();
        thumbUrl = image.getThumb().getUrl();
    }

    public void initFromWebserviceCellarDoor(Winery winery){
        wineryCellarDoor = winery;
        fullSizeUrl = image.getUrl();
        thumbUrl = image.getThumb().getUrl();
    }

    public void initFromWebserviceRegion(Region region){
        this.region = region;
        fullSizeUrl = image.getUrl();
        thumbUrl = image.getThumb().getUrl();
    }

    public void initFromWebserviceWine(Wine wine){
        this.wine = wine;
        fullSizeUrl = image.getUrl();
        thumbUrl = image.getThumb().getUrl();
    }

    public void initFromWebserviceEvent(Event event){
        this.event = event;
        fullSizeUrl = image.getUrl();
        thumbUrl = image.getThumb().getUrl();
    }

}
