package au.net.winehound.ui.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.service.WineHoundService;

@EViewGroup(R.layout.view_favourite_dialog)
public class FavouriteDialog extends FrameLayout{

    @ViewById(R.id.favourite_dialog_text)
    protected TextView text;

    @ViewById(R.id.favourite_dialog_email)
    protected TextView email;

    @Bean
    WineHoundService service;

    private Dialog dialog;
    private int wineryId;
    private FavouriteCompleted completed;

    public FavouriteDialog(Context context) {
        super(context);
    }

    public FavouriteDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavouriteDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    protected void setupUi(){
        if(service.hasFavouritesEmail(getContext())){
            text.setText(R.string.do_you_wish);
            email.setVisibility(View.GONE);
        }
        else{
            text.setText(R.string.add_your_email);
            email.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.favourite_dialog_add_button)
    protected void addButtonClicked(){

        if(!service.hasFavouritesEmail(getContext()) && !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            // Popup a dialog saying they need a valid email address
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.invalid_email)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
        else{
            String emailString;
            if(service.hasFavouritesEmail(getContext())){
                // Get it from our saved preferences
                emailString = service.getFavouritesEmail(getContext());
            }
            else{
                // Get it from the ui
                emailString = email.getText().toString();

                // Save it
                service.setFavouritesEmail(getContext(), emailString);
            }

            // Submit data
            sendData(wineryId);

            dialog.dismiss();
            completed.onFavouriteCompleted();
        }
    }

    @Background
    protected void sendData(int wineryId){
        try{
            service.postFavouriteWinery(getContext(), wineryId);
            Log.i(LogTags.UI, "Favourite sent to winery " + wineryId);
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error submitting favourite", e);
        }
    }

    private void setDialog(AlertDialog dialog){
        this.dialog = dialog;
    }

    public void setWineryId(int wineryId) {
        this.wineryId = wineryId;
    }

    public void setCompleted(FavouriteCompleted completed) {
        this.completed = completed;
    }

    public interface FavouriteCompleted{
        public void onFavouriteCompleted();
    }

    public static void startDialog(Context context, int wineryId, FavouriteCompleted completed){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        FavouriteDialog view = FavouriteDialog_.build(context);
        builder.setView(view);
        AlertDialog dialog = builder.show();

        view.setDialog(dialog);
        view.setWineryId(wineryId);
        view.setCompleted(completed);
    }
}
