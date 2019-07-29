package au.net.winehound.domain.webservice;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.List;

import au.net.winehound.domain.Winery;
import au.net.winehound.domain.webservice.PageMetadata;

public class WineryPage extends AbstractPage {

    private List<Winery> wineries;

    public List<Winery> getWineries() {
        return wineries;
    }

}
