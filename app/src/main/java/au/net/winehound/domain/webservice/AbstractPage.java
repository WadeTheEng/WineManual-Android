package au.net.winehound.domain.webservice;

public abstract class AbstractPage {
    private int pageNumber;
    private PageMetadata meta;

    public PageMetadata getMeta() {
        return meta;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
