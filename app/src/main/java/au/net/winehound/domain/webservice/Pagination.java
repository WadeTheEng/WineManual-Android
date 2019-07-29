package au.net.winehound.domain.webservice;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import au.net.winehound.domain.webservice.PageMetadata;

public class Pagination {
    private int pageNumber;

    @SerializedName("per_page")
    private int perPage;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_objects")
    private int totalObjects;

    private PageMetadata metadata;

    public int getPageNumber() {
        return pageNumber;
    }

    public void initFromWebservice(PageMetadata pageMetadata) {
        this.pageNumber = pageMetadata.getPageNumber();
        this.metadata = pageMetadata;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalObjects() {
        return totalObjects;
    }

}
