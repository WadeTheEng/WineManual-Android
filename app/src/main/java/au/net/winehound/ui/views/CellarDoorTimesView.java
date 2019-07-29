package au.net.winehound.ui.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.CellarDoorOpenTime;
import au.net.winehound.domain.CellarDoorOpenTimeSummary;

public class CellarDoorTimesView extends TableLayout {

    public CellarDoorTimesView(Context context) {
        super(context);
    }

    public CellarDoorTimesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCellarDoorTimes(List<CellarDoorOpenTime> openTimes, final String phoneNum){
        removeAllViews();

        if(openTimes.isEmpty()){
            // Quick exit - No opening times!
            return;
        }

        // Group together the times
        List<CellarDoorOpenTimeSummary> openTimeSummaries = CellarDoorOpenTime.groupTimes(openTimes);

        TableRow headerRow = new TableRow(getContext());
        headerRow.setPadding(0,
                getResources().getDimensionPixelSize(R.dimen.padding_medium),
                0,
                getResources().getDimensionPixelSize(R.dimen.padding_small));
        TextView headerText = new TextView(getContext());
        headerText.setText(R.string.opening_hours);
        headerRow.addView(headerText);
        addView(headerRow);


        for (CellarDoorOpenTimeSummary time : openTimeSummaries){
            // Construct our table row
            TableRow row = new TableRow(getContext());

            // Build the left part
            TextView left = new TextView(getContext());
            left.setPadding(0, 0, getResources().getDimensionPixelOffset(R.dimen.padding_small), 0);
            left.setText(time.getLeftPart());
            row.addView(left);

            // Build the right part
            TextView right = new TextView(getContext());
            if(time.getDay() == CellarDoorOpenTime.Day.ByAppointment){
                right.setText(phoneNum);
                right.setTextColor(getResources().getColor(R.color.winehound_red));
                right.setBackgroundResource(R.drawable.selectable_background_winehound);
                right.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phone(phoneNum);
                    }
                });
            }
            else{
                right.setText(time.getRightPart());
            }
            row.addView(right);

            addView(row);
        }

    }

    private void phone(String number){
        try{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            getContext().startActivity(intent);
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Exception dialing number " + number, e);
        }

    }
}
