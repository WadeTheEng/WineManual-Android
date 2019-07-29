package au.net.winehound.domain.webservice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

public class Image {

    private int id;

    private String url;

    private ThumbUrl thumb;

    public Image() {
    }

    /**
     * Used for unit tests
     *
     * @param url
     */
    public Image(String url) {
        this.url = url;
        this.thumb = new ThumbUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ThumbUrl getThumb() {
        return thumb;
    }
}
