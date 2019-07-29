package au.net.winehound.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import au.net.winehound.R;
import au.net.winehound.service.WineHoundService;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends Fragment{

    @ViewById(R.id.settings_email)
    protected EditText email;

    @Bean
    protected WineHoundService service;

    @AfterViews
    protected void setupUi(){
        email.setText(service.getFavouritesEmail(getActivity()));
    }

    @Click(R.id.settings_change)
    protected void changeSettingsClicked(){

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            // Popup a dialog saying they need a valid email address
            new AlertDialog.Builder(getActivity())
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
            // Save it
            service.setFavouritesEmail(getActivity(), email.getText().toString());
            Toast.makeText(getActivity(), R.string.email_updated, Toast.LENGTH_LONG).show();
        }
    }
}
