package au.net.winehound.domain;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import au.net.winehound.domain.webservice.TastingNotesPdf;

@DatabaseTable
public class Wine implements Serializable{

    @DatabaseField(id = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private String vintage;

    @ForeignCollectionField(eager = true, orderColumnName = "id", orderAscending = false)
    private Collection<Photograph> photographs = new ArrayList<Photograph>();

    @SerializedName("display_variety")
    @DatabaseField
    private String displayVariety;

    @SerializedName("wine_range_id")
    @DatabaseField
    private int wineRangeId;

    @SerializedName("winery_id")
    @DatabaseField
    private int wineryId;

    @DatabaseField
    private String cost;

    @SerializedName("date_bottled")
    @DatabaseField
    private String dateBottled;

    @DatabaseField
    private String colour;

    @DatabaseField
    private String aroma;

    @DatabaseField
    private String palate;

    @SerializedName("alcohol_content")
    @DatabaseField
    private String alcholContent;

    @DatabaseField
    private String vineyard;

    @DatabaseField
    private String winemakers;

    @DatabaseField
    private String tastingNotesUrl;

    @SerializedName("tasting_notes_pdf")
    private TastingNotesPdf tastingNotesPdf;

    @DatabaseField
    private String ph;

    @DatabaseField
    private String closure;

    @DatabaseField
    private String website;

    @SerializedName("updated_at")
    @DatabaseField
    private String updatedAt;

    public Wine() {
    }

    public Wine(int id, String name, int wineryId) {
        this.id = id;
        this.name = name;
        this.wineryId = wineryId;
    }

    public Wine(int id, String name, int wineryId, int wineRangeId) {
        this.id = id;
        this.name = name;
        this.wineryId = wineryId;
        this.wineRangeId = wineRangeId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVintage() {
        return vintage;
    }

    public Collection<Photograph> getPhotographs() {
        return photographs;
    }

    public String getDisplayVariety() {
        return displayVariety;
    }

    public int getWineRangeId() {
        return wineRangeId;
    }

    public int getWineryId() {
        return wineryId;
    }

    public String getCost() {
        return cost;
    }

    public String getDateBottled() {
        return dateBottled;
    }

    public String getColour() {
        return colour;
    }

    public String getAroma() {
        return aroma;
    }

    public String getPalate() {
        return palate;
    }

    public String getAlcholContent() {
        return alcholContent;
    }

    public String getVineyard() {
        return vineyard;
    }

    public String getWinemakers() {
        return winemakers;
    }

    public String getTastingNotesUrl() {
        return tastingNotesUrl;
    }

    public String getPh() {
        return ph;
    }

    public String getClosure() {
        return closure;
    }

    public String getWebsite() {
        return website;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }


    public boolean hasColour(){
        return !isEmptyNullSafe(colour);
    }

    public boolean hasAraoma(){
        return !isEmptyNullSafe(aroma);
    }

    public boolean hasPalate(){
        return !isEmptyNullSafe(palate);
    }

    public boolean hasTastingNotes(){
        return hasColour() || hasPalate() || hasAraoma();
    }

    public boolean hasVintage(){
        return !isEmptyNullSafe(vintage);
    }

    public boolean hasCost(){
        return !isEmptyNullSafe(cost);
    }

    public boolean hasDateBottled(){
        return !isEmptyNullSafe(dateBottled);
    }

    public boolean hasGrapeVariety(){
        return !isEmptyNullSafe(displayVariety);
    }

    public boolean hasAlcholContent(){
        return !isEmptyNullSafe(alcholContent);
    }

    public boolean hasVineyard(){
        return !isEmptyNullSafe(vineyard);
    }

    public boolean hasWinemakers(){
        return !isEmptyNullSafe(winemakers);
    }

    public boolean hasPh(){
        return !isEmptyNullSafe(ph);
    }

    public boolean hasClosure(){
        return !isEmptyNullSafe(closure);
    }

    public boolean hasWineDetails(){
        return hasVintage() || hasCost() || hasDateBottled() || hasGrapeVariety()
                || hasAlcholContent() || hasVineyard() || hasWinemakers() || hasPh()
                || hasClosure();
    }

    public boolean hasTastingNotesPdf(){
        return !isEmptyNullSafe(tastingNotesUrl);
    }

    private boolean isEmptyNullSafe(String toCheck){
        return toCheck == null || toCheck.isEmpty();
    }

    public void initFromWebservice(){
        if(tastingNotesPdf != null && tastingNotesPdf.getTastingNotesPdfUrl() != null){
            tastingNotesUrl = tastingNotesPdf.getTastingNotesPdfUrl().getUrl();
        }

        for(Photograph photograph: photographs){
            photograph.initFromWebserviceWine(this);
        }
    }
}
