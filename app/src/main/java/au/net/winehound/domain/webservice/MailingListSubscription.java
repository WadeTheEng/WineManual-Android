package au.net.winehound.domain.webservice;

import com.google.gson.annotations.SerializedName;

public class MailingListSubscription {

    @SerializedName("mailing_list")
    private MailingListEmail mailingListEmail;

    @SerializedName("winery_id")
    private int wineryId;

    public MailingListSubscription(String email, int wineryId) {
        this.mailingListEmail = new MailingListEmail();
        this.mailingListEmail.email = email;
        this.wineryId = wineryId;
    }

    public MailingListEmail getMailingListEmail() {
        return mailingListEmail;
    }

    public int getWineryId() {
        return wineryId;
    }

    public static class MailingListEmail{
        private String email;


        public String getEmail() {
            return email;
        }
    }
}
