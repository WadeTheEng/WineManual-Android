package au.net.winehound.domain.webservice;

import com.google.gson.annotations.SerializedName;

public class PageMetadata {

    private int pageNumber;
    private WineryPage wineryPage;
    private Pagination pagination;

    @SerializedName("deleted_ids")
    private Integer [] deletedIds;

    public int getPageNumber() {
        return pageNumber;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Integer[] getDeletedIds() {
        return deletedIds;
    }
}
