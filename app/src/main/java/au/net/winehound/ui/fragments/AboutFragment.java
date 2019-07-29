package au.net.winehound.ui.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.ui.HtmlActivity;

@EFragment(R.layout.fragment_about)
public class AboutFragment extends Fragment{

    @ViewById(R.id.about_privacy)
    protected TextView privacy;

    @ViewById(R.id.about_terms)
    protected TextView terms;

    @ViewById(R.id.about_version)
    protected TextView version;


    @AfterViews
    protected void setupUi(){

        // Setup the version
        String versionString = "";
        try
        {
            String appVersion = getActivity().getPackageManager().getPackageInfo("au.net.winehound", 0).versionName;
            versionString = "Version " + appVersion;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.v(LogTags.UI, e.getMessage());
        }
        version.setText(versionString);

        // Ugh ... why is it so hard to get underline text
        privacy.setPaintFlags(privacy.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        terms.setPaintFlags(terms.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
    }

    @Click(R.id.about_terms)
    protected void termsClicked(){
        showHtml("WineHound_Application_Terms_of_Us.html", R.string.terms_of_use);
    }

    @Click(R.id.about_privacy)
    protected void privacyClicked() {
        showHtml("WineHound_Application_Privacy_Policy.html", R.string.privacy_policy);
    }

    private void showHtml(String fileName, int title){
        // Setup info text
        try{
            InputStream in = getResources().getAssets().open(fileName);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder htmlText = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                htmlText.append(line);
            }
            in.close();

            startActivity(HtmlActivity.start(getActivity(), title, htmlText.toString()));
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error reading info.html",e);
        }
    }

    @Click(R.id.about_paperclound)
    protected void papercloundClicked(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.papercloud.com.au")));
    }

}
