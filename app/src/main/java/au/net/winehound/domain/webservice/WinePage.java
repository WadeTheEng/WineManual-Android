package au.net.winehound.domain.webservice;


import java.util.List;

import au.net.winehound.domain.Wine;

public class WinePage extends AbstractPage {

    private List<Wine> wines;

    public List<Wine> getWines() {
        return wines;
    }
}
