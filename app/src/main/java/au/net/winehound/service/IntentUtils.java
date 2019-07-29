package au.net.winehound.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.util.Log;

import com.squareup.picasso.Callback;

import au.net.winehound.LogTags;
import au.net.winehound.R;

public class IntentUtils {

    public static void email(Context context, String email){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        context.startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    public static void phone(Context context, String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static void navigate(Context context, double latitude, double longitude){
        String uri = "http://maps.google.com/maps?daddr="+latitude + "," + longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

    public static void addToCalender(Context context, String title, long startDateMillis, long endDateMillis,
                                     boolean allDay, String description){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startDateMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                endDateMillis);
        intent.putExtra(CalendarContract.Events.ALL_DAY, allDay);// periodicity
        intent.putExtra(CalendarContract.Events.DESCRIPTION,description);

        context.startActivity(Intent.createChooser(intent, "Add to calender"));
    }

    public static void viewWebsite(Context context, String website){
        if(!(website.startsWith("http") || website.startsWith("https"))){
            website = "http://"+website;
        }

        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
    }

    public static void shareText(Context context,  String toShare){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, toShare);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share)));
    }

    public static void downloadPdf(Context context, String url, String name, final Callback callback){
        Uri uri;
        try{
            uri = Uri.parse(url);
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error parsing pdf url " + url, e);
            callback.onError();
            return;
        }

        DownloadManager.Request r = new DownloadManager.Request(uri);

        // This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);

        // When downloading music and videos they will be listed in the player
        // (Seems to be available since Honeycomb only)
        r.allowScanningByMediaScanner();

        // Notify user when download is completed
        // (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Start download
        final DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = dm.enqueue(r);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    if(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) != downloadId){
                        // Quick exit - its not our download!
                        return;
                    }

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            Uri uri = Uri.parse(uriString);

                            intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/pdf");

                            callback.onSuccess();

                            context.startActivity(intent);

                        }
                        else{
                            callback.onError();
                        }
                    }
                    else{
                        callback.onError();
                    }
                    context.unregisterReceiver(this);
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}
