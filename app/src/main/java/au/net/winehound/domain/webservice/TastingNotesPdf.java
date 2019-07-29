package au.net.winehound.domain.webservice;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TastingNotesPdf implements Serializable{

    @SerializedName("tasting_notes_pdf")
    private TastingNotesPdfUrl tastingNotesPdfUrl;

    public TastingNotesPdfUrl getTastingNotesPdfUrl() {
        return tastingNotesPdfUrl;
    }
}
