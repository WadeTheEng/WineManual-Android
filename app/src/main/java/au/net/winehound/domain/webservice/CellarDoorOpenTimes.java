package au.net.winehound.domain.webservice;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.net.winehound.domain.CellarDoorOpenTime;

public class CellarDoorOpenTimes {

    @SerializedName("cellar_door_open_times")
    private List<CellarDoorOpenTime> cellarDoorOpenTimes;


    public List<CellarDoorOpenTime> getCellarDoorOpenTimes() {
        return cellarDoorOpenTimes;
    }
}
